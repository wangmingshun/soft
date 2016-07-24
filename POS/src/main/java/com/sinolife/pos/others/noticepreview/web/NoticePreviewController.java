package com.sinolife.pos.others.noticepreview.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.sinolife.pos.common.consts.SessionKeys;
import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.common.web.PosAbstractController;
import com.sinolife.pos.print.dao.PrintDAO;
import com.sinolife.pos.print.dto.NoticePrintCriteriaDTO;
import com.sinolife.pos.print.notice.NoticePrintHandler;
import com.sinolife.pos.print.notice.NoticePrintHandlerFactory;
import com.sinolife.pos.print.service.PrintService;
import com.sinolife.pos.print.web.NoticePrintCriteriaValidator;
import com.sinolife.sf.framework.controller.BrowserNoCache;

@Controller
@SessionAttributes({ SessionKeys.PRINT_NOTICE_PRINT_CRITERIA })
public class NoticePreviewController extends PosAbstractController {

	@Autowired
	private PrintService printService;

	@Autowired
	private NoticePrintCriteriaValidator validator;
	
	@Autowired
	private PrintDAO printDAO;

	
	@ModelAttribute(SessionKeys.PRINT_NOTICE_PRINT_CRITERIA)
	public NoticePrintCriteriaDTO getNoticePrintCriteriaDTO() {
		return new NoticePrintCriteriaDTO();
	}
	/**
	 * 通知书逐单打印入口
	 */
	@RequestMapping(value = "/others/notice_preview/entry")
	public String entry() {

		return "/others/noticepreview/noticePreview";
	}
	
	
	/**
	 * 查询通知书可选年度
	 */
	@RequestMapping(value="/others/queryNoteYearOptions")
	@ResponseBody
	public Map<String, Object> queryNoteYearOptions(final @RequestParam String policyNo, @RequestParam String noticeType) {
	
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			List<CodeTableItemDTO> optionList = printDAO.queryNoteYearOptions(policyNo, noticeType);
			retMap.put("flag", "Y");
			retMap.put("options", optionList);
		} catch(Exception e) {
			logger.error(e);
			retMap.put("flag", "N");
			retMap.put("message", "查询通知书可选年度失败：" + e.getMessage());
		}
		return retMap;
	}
	
	
	
	

	/**
	 * 通知书逐单打印提交
	 */
	@RequestMapping(value = "/others/notice_preview", method = RequestMethod.POST)
	@BrowserNoCache(enableNoCache = false)
	@Transactional(propagation = Propagation.REQUIRED)
	public ModelAndView noticeSingleSubmit(
			@ModelAttribute(SessionKeys.PRINT_NOTICE_PRINT_CRITERIA) NoticePrintCriteriaDTO criteria,
			BindingResult bindingResult, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("/others/noticePreview");
		mav.addObject(SessionKeys.PRINT_NOTICE_PRINT_CRITERIA, criteria);

		// 校验参数
		validator.validateNoticeSingle(criteria, bindingResult);
		if (bindingResult.hasErrors()) {
			return mav;
		}

		// 输出通知书
		criteria.setSinglePrintFlag("Y");
		NoticePrintHandler handler = NoticePrintHandlerFactory
				.getNoticePrintHandler(criteria.getNoticeType());
		OutputStream os = null;
		InputStream is = null;
		File file = null;
		try {
			// String noteType = criteria.getNoticeType();
			String detailSequenceNo = null;

			List<String> noticeIdList = printService
					.queryPosNoteByCriteria(criteria);
			if (noticeIdList == null || noticeIdList.isEmpty()) {
				mav.addObject("message", "没有符合查询条件的通知书记录");
				return mav;
			}
			detailSequenceNo = noticeIdList.get(0);

			// 生成PDF
			file = handler.handleSingleNoticePrint(detailSequenceNo);
			is = new FileInputStream(file);
			os = response.getOutputStream();
			handler.processPdfFileNameHeader(getHttpServletRequest(),
					getHttpServletResponse(), criteria.getPolicyNo());
			IOUtils.copy(is, os);
			return null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("打印通知书出错：" + e.getMessage());
		} finally {
			PosUtils.safeCloseOuputStream(os);
			PosUtils.safeCloseInputStream(is);
			if (file != null && file.exists()) {
				file.delete();
			}
		}
	}

}
