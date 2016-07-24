package com.sinolife.pos.todolist.problem.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.sinolife.pos.common.consts.SessionKeys;
import com.sinolife.pos.common.consts.ViewNames;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.PosProblemItemsDTO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.common.web.PosAbstractController;
import com.sinolife.pos.print.notice.NoticePrintHandler;
import com.sinolife.pos.print.notice.NoticePrintHandlerFactory;
import com.sinolife.pos.todolist.problem.dto.ProblemLetterInfoDTO;
import com.sinolife.pos.todolist.problem.service.ProblemService;

/**
 * 问题件处理控制器
 */
@RequestMapping("/todolist/problem")
@SessionAttributes({ SessionKeys.TODOLIST_PROBLEM_INFO })
@Controller
public class ProblemController extends PosAbstractController {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private ProblemService problemService;

	@Autowired
	private CommonQueryDAO commonQueryDAO;

	/**
	 * 问题件分派
	 * 
	 * @param problemItemNo
	 * @return
	 */
	@RequestMapping(value = "/{problemItemNo}")
	public String problemItemDispatch(
			@PathVariable("problemItemNo") String problemItemNo) {
		PosProblemItemsDTO problemItems = problemService
				.queryProblemItemsByID(problemItemNo);
		if (problemItems == null)
			throw new RuntimeException("找不到问题件：" + problemItemNo);

		String problemStatus = problemItems.getProblemStatus();
		if (!"4".equals(problemStatus)) {
			return "redirect:/todolist/problem/proc/" + problemItemNo;
		} else {
			throw new RuntimeException("无效的问题件状态：问题件已处理完成");
		}
	}

	/**
	 * 进入问题件处理
	 * 
	 * @return
	 */
	@RequestMapping(value = "/proc/{problemItemNo}", method = RequestMethod.GET)
	public ModelAndView problemItemProcEntry(
			@PathVariable("problemItemNo") String problemItemNo) {
		ModelAndView mav = new ModelAndView(ViewNames.TODOLIST_PROBLEM_PROCESS);

		PosProblemItemsDTO problemItemsDTO = problemService
				.queryProblemItemsByID(problemItemNo);
		String posAcceptStatusCode = problemItemsDTO.getPosInfo()
				.getAcceptStatusCode();
		String problemStatus = problemItemsDTO.getProblemStatus();

		problemItemsDTO.setProblemDealResultList(getProblemDealResultList(
				problemStatus, posAcceptStatusCode));
		if ("4".equals(problemStatus)) {
			mav.addObject("readOnly", "true");
		}
		mav.addObject(SessionKeys.TODOLIST_PROBLEM_INFO, problemItemsDTO);

		mav.addObject("imageBarcodeNo", problemItemsDTO.getPosInfo()
				.getBarcodeNo());
		mav.addObject("imageRelateBarcodeNo",
				commonQueryDAO.getRelateBarcodeNo(problemItemsDTO.getPosInfo()
						.getBarcodeNo()));
		return mav;
	}

	/**
	 * 删除附件
	 */
	@RequestMapping(value = "/proc/clearAttechment")
	@ResponseBody
	public Map<String, Object> clearAttechment(
			@ModelAttribute(SessionKeys.TODOLIST_PROBLEM_INFO) PosProblemItemsDTO problemItemsDTO) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		problemItemsDTO.setAttechmentFileId(null);
		problemItemsDTO.setAttechmentFileName(null);
		retMap.put("flag", "Y");
		return retMap;
	}

	/**
	 * 问题件处理提交
	 * 
	 * @return
	 */
	@RequestMapping(value = "/proc/{problemItemNo}", method = RequestMethod.POST)
	// @Transactional(propagation=Propagation.REQUIRED)
	public ModelAndView problemItemProcSubmit(
			@PathVariable("problemItemNo") String problemItemNo,
			@ModelAttribute(SessionKeys.TODOLIST_PROBLEM_INFO) PosProblemItemsDTO problemItemsDTO) {
		ModelAndView mav = new ModelAndView(ViewNames.TODOLIST_PROBLEM_PROCESS);
		mav.addObject("imageBarcodeNo", problemItemsDTO.getPosInfo()
				.getBarcodeNo());
		mav.addObject("imageRelateBarcodeNo",
				commonQueryDAO.getRelateBarcodeNo(problemItemsDTO.getPosInfo()
						.getBarcodeNo()));

		PosProblemItemsDTO ppi = problemService
				.queryProblemItemsByID(problemItemNo);
		String problemStatus = ppi.getProblemStatus();
		if (!"1".equals(problemStatus) && !"2".equals(problemStatus)
				&& !"3".equals(problemStatus)) {
			mav.addObject("message", "无效的问题件状态：问题件不在可处理的状态");
			return mav;
		}

		String dealResult = problemItemsDTO.getDealResult();
		if ("01".equals(dealResult)) {
			mav.setViewName("redirect:/todolist/problem/letterSend/"
					+ problemItemNo);
			return mav;
		} else if ("02".equals(dealResult) || "05".equals(dealResult)) {
			try {
				problemService.replyProblem(problemItemsDTO);
				mav.addObject("flag", "success");
				mav.addObject("message", "问题件回复成功");
			} catch (Exception e) {
				logger.error(e.getMessage(), e);

				mav.addObject("VALIDATE_MSG", "问题件回复失败：" + e.getMessage());
				return mav;
			}
		} else if ("03".equals(dealResult)) {
			try {
				problemService.unsuspendBankAccount(problemItemsDTO);
				mav.addObject("flag", "success");
				mav.addObject("message", "取消转账暂停成功");
			} catch (Exception e) {
				logger.error(e.getMessage(), e);

				mav.addObject("VALIDATE_MSG", "取消转账暂停失败：" + e.getMessage());
				return mav;
			}
		} else if ("04".equals(dealResult)) {
			try {
				problemService.cancelPosApply(problemItemsDTO);
				mav.addObject("flag", "success");
				mav.addObject("message", "受理撤销成功");
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				mav.addObject("VALIDATE_MSG", "受理撤销失败：" + e.getMessage());
				return mav;
			}
		} else if ("06".equals(dealResult)) {
			try {
				// 定时工作流继续推动
				problemService.workflowRunAgain(problemItemsDTO);
				mav.addObject("flag", "success");
				mav.addObject("message", "定时工作流继续推动成功");
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				mav.addObject("VALIDATE_MSG", "定时工作流继续推动失败：" + e.getMessage());
				return mav;
			}
		}

		if (StringUtils.isNotBlank(problemItemsDTO.getAttechmentFileId())) {
			problemService.saveAttechment(problemItemsDTO.getPosNo(),
					problemItemsDTO.getAttechmentFileId(),
					problemItemsDTO.getAttechmentFileName());
		}
		return mav;
	}

	/**
	 * 进入函件下发
	 * 
	 * @return
	 */
	@RequestMapping(value = "/letterSend/{problemItemNo}", method = RequestMethod.GET)
	public ModelAndView problemLetterSendEntry(
			@PathVariable("problemItemNo") String problemItemNo,
			@ModelAttribute(SessionKeys.TODOLIST_PROBLEM_INFO) PosProblemItemsDTO problemItemsDTO) {
		ModelAndView mav = new ModelAndView(
				ViewNames.TODOLIST_PROBLEM_LETTER_SEND);
		problemService.prepareLetterInfo(problemItemsDTO);
		problemItemsDTO.getLetterInfo().setProblemContent(
				problemItemsDTO.getProblemContent());
		mav.addObject(SessionKeys.TODOLIST_PROBLEM_INFO, problemItemsDTO);
		return mav;
	}

	/**
	 * 函件下发提交
	 * 
	 * @return
	 */
	@RequestMapping(value = "/letterSend/{problemItemNo}", method = RequestMethod.POST)
	public ModelAndView problemLetterSendSubmit(
			@PathVariable("problemItemNo") String problemItemNo,
			@ModelAttribute(SessionKeys.TODOLIST_PROBLEM_INFO) PosProblemItemsDTO problemItemsDTO,
			HttpServletResponse response) {
		ModelAndView mav = new ModelAndView(
				ViewNames.TODOLIST_PROBLEM_LETTER_SEND);
		NoticePrintHandler handler = NoticePrintHandlerFactory
				.getNoticePrintHandler("problem");
		InputStream inputStream = null;
		OutputStream outputStream = null;
		File tmpFile = null;
		try {
			// 更新问题件状态及问题件内容
			problemService.createProblemLetter(problemItemsDTO);

			// 生成问题函内容
			ProblemLetterInfoDTO letter = problemItemsDTO.getLetterInfo();
			handler.clearThreadParameters();
			handler.setThreadParameter("EMP_NAME", letter.getEmpName());
			handler.setThreadParameter("EMP_DEPT", letter.getEmpDeptName());
			handler.setThreadParameter("EMP_TEL", letter.getEmpPhone());
			handler.setThreadParameter("EMP_NO", letter.getEmpNo());
			handler.setThreadParameter("CLIENT_NAME", letter.getClientName());
			handler.setThreadParameter("CLIENT_TEL", letter.getClientPhone());
			handler.setThreadParameter("CLIENT_ADDRESS",
					letter.getClientAddress());
			handler.setThreadParameter("CLIENT_ZIP", letter.getClientZip());
			handler.setThreadParameter("PRINT_BARCODE", problemItemNo);
			handler.setThreadParameter("PROBLEM_CONTENT",
					letter.getProblemContent());
			handler.setThreadParameter("PROBLEM_ITEM_NO", problemItemNo);
			tmpFile = handler.handleSingleNoticePrint(problemItemsDTO
					.getPosNo());

			// 输出到客户端
			inputStream = new FileInputStream(tmpFile);
			outputStream = response.getOutputStream();
			handler.processPdfFileNameHeader(getHttpServletRequest(),
					getHttpServletResponse(), problemItemsDTO.getPosNo());
			IOUtils.copy(inputStream, outputStream);
			return null;
		} catch (Exception e) {
			mav.addObject("message", "问题函创建失败:" + e.getMessage());
		} finally {
			handler.clearThreadParameters();
			PosUtils.safeCloseInputStream(inputStream);
			PosUtils.safeCloseOuputStream(outputStream);
			PosUtils.deleteFile(tmpFile);
		}
		return mav;
	}

	/**
	 * 根据问题件状态取得可以选择的处理结果集
	 * 
	 * @param problemStatus
	 * @return
	 */
	private List<CodeTableItemDTO> getProblemDealResultList(
			String problemStatus, String posAcceptStatusCode) {
		List<CodeTableItemDTO> problemDealResultList = new ArrayList<CodeTableItemDTO>();
		if ("1".equals(problemStatus)) {

			// 定时工作流继续推动
			if ("A23".equals(posAcceptStatusCode)) {
				problemDealResultList.add(new CodeTableItemDTO("06",
						"定时工作流继续推动"));
			} else {

				// 已生成状态
				problemDealResultList.add(new CodeTableItemDTO("01", "下发函件"));
				problemDealResultList.add(new CodeTableItemDTO("02", "回复问题件"));

				problemDealResultList.add(new CodeTableItemDTO("03", "取消转账暂停"));
				if (!"E01".equals(posAcceptStatusCode)) {
					problemDealResultList
							.add(new CodeTableItemDTO("04", "受理撤销"));
				}
				problemDealResultList
						.add(new CodeTableItemDTO("05", "调整保全收付方式"));
			}

		} else if ("2".equals(problemStatus)) {
			// 待函件回销
			problemDealResultList.add(new CodeTableItemDTO("01", "下发函件"));
			problemDealResultList.add(new CodeTableItemDTO("02", "回复问题件"));
		} else if ("3".equals(problemStatus)) {
			// 已函件回销
			problemDealResultList.add(new CodeTableItemDTO("02", "回复问题件"));
		}
		return problemDealResultList;
	}
}
