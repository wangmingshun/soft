package com.sinolife.pos.print.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.sinolife.pos.acceptance.branch.service.BranchQueryService;
import com.sinolife.pos.common.consts.SessionKeys;
import com.sinolife.pos.common.consts.ViewNames;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.LoginUserInfoDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.util.PaginationDataWrapper;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.common.web.PosAbstractController;
import com.sinolife.pos.print.dao.PrintDAO;
import com.sinolife.pos.print.dto.EndorsementPrintCriteriaDTO;
import com.sinolife.pos.print.dto.NoticePrintCriteriaDTO;
import com.sinolife.pos.print.dto.PosNoteDTO;
import com.sinolife.pos.print.notice.NoticePrintHandler;
import com.sinolife.pos.print.notice.NoticePrintHandlerFactory;
import com.sinolife.pos.print.service.PrintService;
import com.sinolife.pos.schedule.HandlePdfMail;
import com.sinolife.pos.schedule.dao.ScheduleDAO;
import com.sinolife.pos.schedule.dto.NoticePrintBatchTask;
import com.sinolife.sf.attatch.UrlSignature;
import com.sinolife.sf.framework.controller.BrowserNoCache;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.store.SFFile;
import com.sinolife.sf.store.TempFileFactory;

/**
 * 打印控制器
 */
@RequestMapping("/print")
@SessionAttributes({SessionKeys.PRINT_ENDORSEMENT_PRINT_CRITERIA, SessionKeys.PRINT_NOTICE_PRINT_CRITERIA})
@Controller
public class PrintController extends PosAbstractController {
    
    @Autowired
    private BranchQueryService queryService;    

	@Autowired
	private PrintService printService;
	
	@Autowired
	private BranchQueryService branchQueryService;
	
	@Autowired
	private CommonQueryDAO commonQueryDAO;
	
	@Autowired
	private PrintDAO printDAO;
	
	@Autowired
	private ScheduleDAO scheduleDAO;
	
	@Autowired
	private NoticePrintCriteriaValidator validator;
	
	@Autowired
	private TransactionTemplate txTmpl;
	@Autowired
	private HandlePdfMail handlePdfMail;
	
	private TempFileFactory tempFileFactory = TempFileFactory.getInstance(getClass());
	
	@ModelAttribute(SessionKeys.PRINT_ENDORSEMENT_PRINT_CRITERIA)
	public EndorsementPrintCriteriaDTO getEndorsementBatchPrintCriteriaDTO() {
		return new EndorsementPrintCriteriaDTO();
	}
	
	@ModelAttribute(SessionKeys.PRINT_NOTICE_PRINT_CRITERIA)
	public NoticePrintCriteriaDTO getNoticePrintCriteriaDTO() {
		return new NoticePrintCriteriaDTO();
	}
	
	
	@RequestMapping("/entry")
	public String entry(SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		return ViewNames.PRINT_ENTRY;
	}
	
	/**
	 * 批单逐单打印入口
	 */
	@RequestMapping(value="/endorsement_single")
	public String endorsementSingleEntry(SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		return ViewNames.PRINT_ENDORSEMENT_SINGLE;
	}
	
	/**
	 * 批单逐单打印预览
	 */
	@RequestMapping(value="/endorsement_single_preview")
	public ModelAndView endorsementSinglePreview(@RequestParam String posNo,@RequestParam String againPrint) {
		ModelAndView mav = new ModelAndView();
		Map<String, Object> posInfo = printService.queryEndorsementInfoByPosNo(posNo);
		if(posInfo != null && !posInfo.isEmpty()) {
			
			if(posInfo.get("ENTPRINTUSER")!=null&&!"Y".equals(againPrint))
			{
				
				mav.setViewName(ViewNames.PRINT_ENDORSEMENT_SINGLE);
				mav.addObject("alterMessage", "该批单已经打印过，是否确定再次打印？");
				return mav;
			}
			mav.setViewName(ViewNames.PRINT_ENDORSEMENT_SINGLE_PREVIEW);
			mav.addObject("posInfo", posInfo);
			//查询是否需要打印保证价值表或条款
			boolean isEndorsementHasNoteOrPlanProvision = branchQueryService.isEndorsementHasNoteOrPlanProvision(posNo);
			mav.addObject("isEndorsementHasNoteOrPlanProvision", isEndorsementHasNoteOrPlanProvision);
		} else {
			mav.setViewName(ViewNames.PRINT_ENDORSEMENT_SINGLE);
			mav.addObject("message", "找不到该批单，或者该保全申请还未生效");
		}
		return mav;
	}
	
	/**
	 * 批单逐单打印提交，生成批单内容
	 */
	@RequestMapping(value="/endorsement_single_submit_for_ent")
	@BrowserNoCache(enableNoCache=false)
	public ModelAndView endorsementSingleSubmitForEnt(@RequestParam String posNo, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView(ViewNames.PRINT_ENDORSEMENT_SINGLE_PREVIEW);
		Map<String, Object> posInfo = printService.queryEndorsementInfoByPosNo(posNo);
		if(posInfo == null || posInfo.isEmpty()) {
			mav.addObject("message", "找不到批单，或者保全申请还未生效");
			mav.addObject("posInfo", posInfo);
		}
		//更新批单打印人员信息
		String submitUserId=PosUtils.getLoginUserInfo().getLoginUserID();
		printDAO.updatePosInfoEntData(posNo, submitUserId);		
		
		InputStream inputStream = null;
		OutputStream outputStream = null;
		NoticePrintHandler handler = NoticePrintHandlerFactory.getNoticePrintHandler("ent");
		File tmpFile = null;
		try {
			tmpFile = handler.handleSingleNoticePrint(posNo);
			// 电子印章
			SFFile sigFile = handlePdfMail.signature((SFFile) tmpFile, posNo,"toprsement");
			inputStream = new FileInputStream(sigFile);
			outputStream = response.getOutputStream();
			handler.processPdfFileNameHeader(getHttpServletRequest(), getHttpServletResponse(), posNo);
			IOUtils.copy(inputStream, outputStream);
			return null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if(outputStream == null) {
				mav.addObject("message", "打印错误：" + e.getMessage());
				mav.addObject("posInfo", posInfo);
				return mav;
			} else {
				throw new RuntimeException(e);
			}
		} finally {
			PosUtils.safeCloseInputStream(inputStream);
			PosUtils.safeCloseOuputStream(outputStream);
			PosUtils.deleteFile(tmpFile);
		}
	}
	
	/**
	 * 批单逐单打印提交，生成保证价值表与产品条款表
	 */
	@RequestMapping(value="/endorsement_single_submit_for_note")
	@BrowserNoCache(enableNoCache=false)
	public ModelAndView endorsementSingleSubmitForNote(@RequestParam String posNo, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView(ViewNames.PRINT_ENDORSEMENT_SINGLE_PREVIEW);
		Map<String, Object> posInfo = printService.queryEndorsementInfoByPosNo(posNo);
		if(posInfo == null || posInfo.isEmpty()) {
			mav.addObject("message", "找不到批单，或者保全申请还未生效");
			mav.addObject("posInfo", posInfo);
			return mav;
		}
		
		Date applyDate = (Date) posInfo.get("APPLY_DATE");
		
		//产品条款
		Set<String> productSet = printDAO.getAddProduct(posNo);
		
		//保证价值表
		List<PosNoteDTO> posNoteList = printService.queryPosNoteMainByPosNo(posNo);
		
		InputStream inputStream = null;
		OutputStream outputStream = null;
		NoticePrintHandler handler = NoticePrintHandlerFactory.getNoticePrintHandler("ent");
		File tmpFile = null;
		List<File> resultFileList = new ArrayList<File>();
		try {
			if(posNoteList != null && !posNoteList.isEmpty()) {
				for(PosNoteDTO posNote : posNoteList) {
					handler = NoticePrintHandlerFactory.getNoticePrintHandler(posNote.getNoteType());
					tmpFile = handler.handleSingleNoticePrint(posNote.getDetailSequenceNo());
					resultFileList.add(tmpFile);
				}
			}
			
			//添加险种条款信息
			for(String productCode : productSet) {
				byte[] planProvisionContent = printDAO.queryPlanProvisionByProductCode(productCode, applyDate);
				if(planProvisionContent != null && planProvisionContent.length > 0) {
					tmpFile = tempFileFactory.createTempFile();
					outputStream = new FileOutputStream(tmpFile);
					outputStream.write(planProvisionContent);
					outputStream.flush();
					outputStream.close();
					resultFileList.add(tmpFile);
				}
			}
			outputStream = response.getOutputStream();
			handler.processPdfFileNameHeader(getHttpServletRequest(), getHttpServletResponse(), ":" + posNo + "(note).pdf");
			handler.mergePdfFiles(resultFileList, outputStream);
			return null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if(outputStream == null) {
				mav.addObject("message", "打印错误：" + e.getMessage());
				mav.addObject("posInfo", posInfo);
				return mav;
			} else {
				throw new RuntimeException(e);
			}
		} finally {
			PosUtils.safeCloseInputStream(inputStream);
			PosUtils.safeCloseOuputStream(outputStream);
			PosUtils.deleteFile(tmpFile);
			PosUtils.deleteFiles(resultFileList);
		}
	}
	
	/**
	 * 批单批次打印入口
	 */
	@RequestMapping(value="/endorsement_batch", method=RequestMethod.GET)
	public String endorsementBatchEntry(SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		return ViewNames.PRINT_ENDORSEMENT_BATCH;
	}
	
	/**
	 * 批单批次打印提交
	 */
	@RequestMapping(value="/endorsement_batch", method=RequestMethod.POST)
	@BrowserNoCache(enableNoCache=false)
	@Transactional(propagation=Propagation.REQUIRED)
	public ModelAndView endorsementBatchSubmit(@ModelAttribute(SessionKeys.PRINT_ENDORSEMENT_PRINT_CRITERIA) EndorsementPrintCriteriaDTO  criteria, BindingResult bindingResult, HttpServletResponse response, String printType, String entOrApplication) {
		ModelAndView mav = new ModelAndView();
		validator.validateEndorsementBatch(criteria, bindingResult);
		if(bindingResult.hasErrors()) {
			return mav;
		}
		int count = printService.countEndorsementByCriteria(criteria);
		if(count <= 0) {
			mav.setViewName(ViewNames.PRINT_ENDORSEMENT_BATCH);
			mav.addObject("message", "找不到批单，或者保全申请还未生效");
			return mav;
		}
		NoticePrintBatchTask task = new NoticePrintBatchTask();
		LoginUserInfoDTO userInfo = PosUtils.getLoginUserInfo();
		task.setEmail(userInfo.getLoginUserID());
		task.setSubmitUserId(userInfo.getLoginUserID());
		task.setSubmitUserName(userInfo.getLoginUserName());
		task.setQueryBranchCode(criteria.getAcceptBranchCode());
		if(StringUtils.isNotBlank(task.getQueryBranchCode())) {
			task.setQueryBranchName(commonQueryDAO.getBranchNameByBranchCode(task.getQueryBranchCode()));
		}
		task.setQueryDateStart(criteria.getEffectDateStart());
		task.setQueryDateEnd(criteria.getEffectDateEnd());
		task.setQueryChannelCode(criteria.getAcceptChannelCode());
		task.setQueryPolicyChannelCode(criteria.getPolicyChannelCode());
		task.setQueryServiceItems(criteria.getServiceItems());
		task.setQueryUserId(criteria.getAcceptor());
		task.setQueryApprovalServiceType(criteria.getApprovalServiceType());
		if ("logo".equals(entOrApplication)){
			task.setQueryNoticeType("ent1");
		}else{
			task.setQueryNoticeType("ent");
		}

		task.setUMBranchCode(userInfo.getLoginUserUMBranchCode());
		task.setNeedSendEmail("Y");
		task.setPrintStatus(criteria.getPrintOptions());
		scheduleDAO.insertNoticePrintBatchTask(task);
		mav.addObject("message", "您的查询请求已提交，请留意邮件通知！");
		return mav;
	}
	
	/**
	 * 通知书批次打印入口
	 */
	@RequestMapping(value="/notice_batch", method=RequestMethod.GET)
	public ModelAndView noticeBatchEntry(SessionStatus sessionStatus) {
		ModelAndView mav = new ModelAndView(ViewNames.PRINT_NOTICE_BATCH);
		sessionStatus.setComplete();
		String loginUserUMBranchCode = PosUtils.getLoginUserInfo().getLoginUserUMBranchCode();
		boolean isShenzhenUser = printDAO.isShenzhenUser(loginUserUMBranchCode);
		mav.addObject("isShenzhenUser", isShenzhenUser);
		return mav;
	}
	
	/**
	 * 通知书批次打印提交
	 */
	@RequestMapping(value="/notice_batch", method=RequestMethod.POST)
	@BrowserNoCache(enableNoCache=false)
	@Transactional(propagation=Propagation.REQUIRED)
	public ModelAndView noticeBatchSubmit(@ModelAttribute(SessionKeys.PRINT_NOTICE_PRINT_CRITERIA) NoticePrintCriteriaDTO criteria, BindingResult bindingResult, HttpServletResponse response) {
		LoginUserInfoDTO userInfo = PosUtils.getLoginUserInfo();
		ModelAndView mav = new ModelAndView(ViewNames.PRINT_NOTICE_BATCH);
		mav.addObject(SessionKeys.PRINT_NOTICE_PRINT_CRITERIA, criteria);
		
		//查询登录用户是否为深圳用户
		String loginUserUMBranchCode = userInfo.getLoginUserUMBranchCode();
		boolean isShenzhenUser = printDAO.isShenzhenUser(loginUserUMBranchCode);
		mav.addObject("isShenzhenUser", isShenzhenUser);
				
		//校验参数
		validator.validateNoticeBatch(criteria, bindingResult);
		if(bindingResult.hasErrors()) {
			return mav;
		}
		
		//String serviceNo = criteria.getServiceNo();
		//String policyChannel = criteria.getPolicyChannel();
		
		//提交任务前校验下该任务是否能查到记录
		/* 去除电子信函 edit by gaojiaming start */
		criteria.setIsELetterFlag("N");
		/* 去除电子信函 edit by gaojiaming end */		
		int count = printService.countPosNoteByCriteria(criteria);
		if(count <= 0) {
			mav.addObject("message", "找不到通知书记录！");
			return mav;
		}
		
		
		
		//插入批次打印任务
		NoticePrintBatchTask task = new NoticePrintBatchTask();
		String userId = PosUtils.getLoginUserInfo().getLoginUserID();
		task.setEmail(userId);
		task.setSubmitUserId(userId);
		task.setSubmitUserName(userInfo.getLoginUserName());
		task.setQueryBranchCode(criteria.getBranchCode());
		if(StringUtils.isNotBlank(task.getQueryBranchCode())) {
			task.setQueryBranchName(commonQueryDAO.getBranchNameByBranchCode(task.getQueryBranchCode()));
		}
		task.setQueryDateStart(criteria.getBusinessDateStart());
		task.setQueryDateEnd(criteria.getBusinessDateEnd());
		task.setQueryNoticeType(criteria.getNoticeType());
		task.setUMBranchCode(loginUserUMBranchCode);
		task.setNeedSendEmail("Y");
		task.setQueryUserId(criteria.getServiceNo());
		task.setQueryPolicyChannelCode(criteria.getPolicyChannel());
		scheduleDAO.insertNoticePrintBatchTask(task);
		
		mav.addObject("message", "您的查询请求已提交，请留意邮件通知！");
		return mav;
	}
	
	/**
	 * 通知书逐单打印入口
	 */
	@RequestMapping(value="/notice_single", method=RequestMethod.GET)
	public String noticeSingleEntry(SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		return ViewNames.PRINT_NOTICE_SINGLE;
	}
	
	/**
	 * 查询通知书可选年度
	 */
	@RequestMapping(value="/queryNoteYearOptions")
	@ResponseBody
	public Map<String, Object> queryNoteYearOptions(final @RequestParam String policyNo, @RequestParam String noticeType) {
		if(StringUtils.isNotBlank(policyNo) && ("1".equals(noticeType) || "2".equals(noticeType))) {
			txTmpl.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus txStatus) {
					Map<String, Object> retMap = null;
					try {
						retMap = printDAO.divNoticeRedraw(policyNo);
						logger.info("divNoticeRedraw:" + retMap);
						String flag = (String) retMap.get("p_flag");
						if(!"0".equals(flag)) {
							txStatus.setRollbackOnly();
						}
					} catch(Exception e) {
						//抽取出现异常可能为保单号错误，这里忽略掉
						logger.error("divNoticeRedraw:" + retMap + ", excpetionMsg:" + e.getMessage(), e);
						txStatus.setRollbackOnly();
					}
				}
			});
		}
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
	@RequestMapping(value="/notice_single", method=RequestMethod.POST)
	@BrowserNoCache(enableNoCache=false)
	@Transactional(propagation=Propagation.REQUIRED)
	public ModelAndView noticeSingleSubmit(@ModelAttribute(SessionKeys.PRINT_NOTICE_PRINT_CRITERIA) NoticePrintCriteriaDTO criteria, BindingResult bindingResult, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView(ViewNames.PRINT_NOTICE_SINGLE);
		mav.addObject(SessionKeys.PRINT_NOTICE_PRINT_CRITERIA, criteria);
		
		//校验参数
		validator.validateNoticeSingle(criteria, bindingResult);
		if(bindingResult.hasErrors()) {
			return mav;
		}
		
		//输出通知书
		criteria.setSinglePrintFlag("Y");
		NoticePrintHandler handler = NoticePrintHandlerFactory.getNoticePrintHandler(criteria.getNoticeType());
		OutputStream os = null;
		InputStream is = null;
		File file = null;
		try {
			String noteType = criteria.getNoticeType();
			String detailSequenceNo = null;
			if("10".equals(noteType) || "11".equals(noteType)) {
				//投连万能年报逐单抽档单独调用接口生成
				if(criteria.getQueryDateEnd() == null) {
					//截止日期为空则取系统日期
					criteria.setQueryDateEnd(commonQueryDAO.getSystemDate());
				}
				Map<String, Object> retMap = printDAO.investAnnalsPrintSingle(criteria.getPolicyNo(), criteria.getQueryDateStart(), criteria.getQueryDateEnd(), noteType);
				if(!"0".equals(retMap.get("p_flag"))) {
					mav.addObject("message", "投连、万能年报通知书逐单抽档失败：" + retMap.get("p_message"));
					return mav;
				}
				detailSequenceNo = (String) retMap.get("p_detail_sequence_no");
				if(detailSequenceNo == null || StringUtils.isBlank(detailSequenceNo)) {
					mav.addObject("message", "没有符合查询条件的通知书记录");
					return mav;
				}
				//更新打印日期及邮储PDF文件名称以防止被邮储打印的抽取出来发送
				printService.updatePosNoteMainPrintDateAndPdfName(detailSequenceNo, "x");
			} else {
				List<String> noticeIdList = printService.queryPosNoteByCriteria(criteria);
				if(noticeIdList == null || noticeIdList.isEmpty()) {
					mav.addObject("message", "没有符合查询条件的通知书记录");
					return mav;
				}
				detailSequenceNo = noticeIdList.get(0);
				Map<String, Object> retMap = printService.updateNoteAddress(detailSequenceNo);
				if(!"0".equals(retMap.get("p_flag"))) {
					mav.addObject("message", "更新通知书寄送地址失败：" + retMap.get("p_message"));
					return mav;
				}
				printService.updatePosNoteMainPrintDate(detailSequenceNo);
			}
			
			//生成PDF
			file = handler.handleSingleNoticePrint(detailSequenceNo);
			is = new FileInputStream(file);
			os = response.getOutputStream();
			handler.processPdfFileNameHeader(getHttpServletRequest(), getHttpServletResponse(), criteria.getPolicyNo());
			IOUtils.copy(is, os);
			return null;
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("打印通知书出错：" + e.getMessage());
		} finally {
			PosUtils.safeCloseOuputStream(os);
			PosUtils.safeCloseInputStream(is);
			if(file != null && file.exists()) {
				file.delete();
			}
		}
	}
	
	/**
	 * 保单打印入口
	 */
	@RequestMapping(value="/policy_single")
	public String policySingleEntry() {
		return ViewNames.PRINT_POLICY_SINGLE;
	}
	
	/**
	 * 保单打印提交
	 */
	@RequestMapping(value="/policy_single_submit")
	public ModelAndView policySingleSubmit(@RequestParam String policyNo, @RequestParam String userId) {
		ModelAndView mav = new ModelAndView(ViewNames.PRINT_POLICY_SINGLE);
		
		String requestIp = PlatformContext.getHttpServletRequest().getRemoteAddr();
		String printUserId = userId;
		String errMsg = null;
		try {
			if(StringUtils.isBlank(printUserId)) {
				printUserId = PlatformContext.getCurrentUser();
			}
			errMsg = printService.printPolicyByPolicyNo(policyNo, printUserId, requestIp);
		} catch(Exception e) {
			logger.error(e);
			errMsg = e.getMessage();
		}
		if(StringUtils.isBlank(errMsg)) {
			mav.addObject("message", "打印成功！");
		} else {
			mav.addObject("message", "打印失败：" + errMsg);
		}
		return mav;
	}

	/**
	 * 查询批次任务页面数据
	 */
	@RequestMapping("/batchPrintTaskList")
	@ResponseBody
	public Map<String, Object> getBatchPrintTaskList(@RequestParam int page, @RequestParam int rows) {
		List<NoticePrintBatchTask> taskList = scheduleDAO.queryNoticePrintBatchTaskByUserId(PosUtils.getLoginUserInfo().getLoginUserID());
		PaginationDataWrapper<NoticePrintBatchTask> wrapper = new PaginationDataWrapper<NoticePrintBatchTask>(taskList, rows);
		wrapper.gotoPage(page);
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("total", wrapper.getTotalDataSize());
		retMap.put("rows", wrapper.getCurrentPageDataList());
		return retMap;
	}
	
	/**
	 * 删除批次任务
	 */
	@RequestMapping("/deleteBatchPrintTaskById")
	@ResponseBody
	public Map<String, Object> deleteBatchPrintTaskById(final @RequestParam String taskId, ModelMap model) {
		String errMsg = txTmpl.execute(new TransactionCallback<String>() {
			@Override
			public String doInTransaction(TransactionStatus transactionstatus) {
				try {
					scheduleDAO.updateNoticePrintBatchTask(taskId, "valid_flag", "N");
					return null;
				} catch(Exception e) {
					logger.error(e);
					transactionstatus.setRollbackOnly();
					return "删除任务失败：" + e.getMessage();
				}
			}
		});
		Map<String, Object> retMap = new HashMap<String, Object>();
		if(StringUtils.isBlank(errMsg)) {
			retMap.put("flag", "Y");
		} else {
			retMap.put("flag", "N");
			retMap.put("message", errMsg);
		}
		return retMap;
	}
	
	/**
	 * 获取任务结果的下载地址
	 */
	@RequestMapping("/getDownloadURL")
	@ResponseBody
	public Map<String, Object> getDownloadURL(@RequestParam String taskId) {
		NoticePrintBatchTask task = scheduleDAO.queryNoticePrintBatchTaskById(taskId);
		Map<String, Object> retMap = new HashMap<String, Object>();
		String downloadURL = null;
		if(task != null) {
			downloadURL = PlatformContext.getIMFileService().getFileURL(task.getIMFileId(), new UrlSignature(), true, task.getNoticeFileName(), null, null);
		}
		if(StringUtils.isNotBlank(downloadURL)) {
			retMap.put("flag", "Y");
			retMap.put("url", downloadURL);
		} else {
			retMap.put("flag", "N");
			retMap.put("message", "找不到下载地址");
		}
		return retMap;
	}
	
	/**
	 * 进入业务退费申请单打印页面
	 */
	@RequestMapping(value="/big_refund_application_form", method=RequestMethod.GET)
	public String bigRefundApplicationFormPrintEntry() {
		return ViewNames.PRINT_BIG_REFUND_APPLICATION_FORM;
	}
	
	/**
	 * 业务退费申请单打印提交
	 */
	@RequestMapping(value="/big_refund_application_form", method=RequestMethod.POST)
	public ModelAndView bigRefundApplicationFormPrintSubmit(@RequestParam String posNo, @RequestParam String memo, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView(ViewNames.PRINT_BIG_REFUND_APPLICATION_FORM);
		PosInfoDTO posInfo = commonQueryDAO.queryPosInfoRecord(posNo);
		if(posInfo == null) {
			mav.addObject("message", "找不到批单，请检查录入的批单号是否正确");
			return mav;
		}
		
		InputStream inputStream = null;
		OutputStream outputStream = null;
		NoticePrintHandler handler = NoticePrintHandlerFactory.getNoticePrintHandler("refundApp");
		File tmpFile = null;
		try {
			handler.setThreadParameter("memo", memo);
			tmpFile = handler.handleSingleNoticePrint(posNo);
			// 电子印章
			//SFFile sigFile = handlePdfMail.signature((SFFile) tmpFile, posNo,"toprsement");
			inputStream = new FileInputStream(tmpFile);
			
			outputStream = response.getOutputStream();
			handler.processPdfFileNameHeader(getHttpServletRequest(), getHttpServletResponse(), posNo);
			IOUtils.copy(inputStream, outputStream);
			return null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if(outputStream == null) {
				mav.addObject("message", "打印错误：" + e.getMessage());
				mav.addObject("posInfo", posInfo);
				return mav;
			} else {
				throw new RuntimeException(e);
			}
		} finally {
			PosUtils.safeCloseInputStream(inputStream);
			PosUtils.safeCloseOuputStream(outputStream);
			PosUtils.deleteFile(tmpFile);
			handler.clearThreadParameters();
		}
	}
	
	/**
	 * 查询保全备注
	 */
	@RequestMapping(value="/getPosInfoRemark")
	@ResponseBody
	public Map<String, Object> getPosInfoRemark(@RequestParam String posNo) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("flag", "N");
		PosInfoDTO posInfo = commonQueryDAO.queryPosInfoRecord(posNo);
		if(posInfo != null && StringUtils.isNotBlank(posInfo.getRemark())) {
			resultMap.put("flag", "Y");
			resultMap.put("remark", posInfo.getRemark());
		}
		return resultMap;
	}
	
    /**
     * 申请书打印
     * 
     * @param posNo
     * @param printType
     * @param entOrApplication
     * @param response
     * @return ModelAndView
     * @author GaoJiaMing
     * @time 2014-6-4
     */
    @RequestMapping(value = "/print_application_for_fillform")
	@BrowserNoCache(enableNoCache = false)
	public ModelAndView printApplicationForFillform(@RequestParam String printFlag, @RequestParam String posNo, @RequestParam String printType,
			@RequestParam String entOrApplication, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView(ViewNames.PRINT_APPLICATION_PREVIEW);
        // Map<String, Object> posInfo =
        // printService.queryFillFormApplicationInfoByPosNo(posNo);
        // if (posInfo == null || posInfo.isEmpty()) {
        // mav.addObject("message", "找不到申请书信息");
        // mav.addObject("posInfo", posInfo);
        // return mav;
        // }
        // if (posInfo.get("MESSAGE") != null) {
        // mav.addObject("message", "获取批文失败：" + posInfo.get("MESSAGE"));
        // mav.addObject("posInfo", posInfo);
        // return mav;
        // }
        InputStream inputStream = null;
        OutputStream outputStream = null;
        NoticePrintHandler handler = NoticePrintHandlerFactory.getNoticePrintHandler(entOrApplication);
        File tmpFile = null;
        
        try {
            tmpFile = handler.handleApplicationFillFormPrint(posNo,printType,entOrApplication);
			if("Y".equals(printFlag)){
				// 电子印章
				tmpFile = handlePdfMail.signature((SFFile) tmpFile, posNo,"toprsement");
            }
            
            inputStream = new FileInputStream(tmpFile);
            outputStream = response.getOutputStream();
            
            handler.processPdfFileNameHeader(getHttpServletRequest(), getHttpServletResponse(), queryService.getBarCodeNoByPosNo(posNo));
            IOUtils.copy(inputStream, outputStream);
            return null;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (outputStream == null) {
                mav.addObject("message", "打印错误：" + e.getMessage());
                // mav.addObject("posInfo", posInfo);
                return mav;
            } else {
                throw new RuntimeException(e);
            }
        } finally {
            PosUtils.safeCloseInputStream(inputStream);
            PosUtils.safeCloseOuputStream(outputStream);
            PosUtils.deleteFile(tmpFile);
        }
    }
    /**
     * 根据保全号查询申请书信息（没有跳转页面，用于校验）
     * @param posNo
     * @return String
     * @author GaoJiaMing 
     * @time 2014-6-12
     */
    @RequestMapping(value = "/queryFillFormApplicationInfoByPosNo", method = RequestMethod.GET)
    @ResponseBody
    public String queryFillFormApplicationInfoByPosNo(String posNo) {
        return printService.checkFillFormApplicationInfoByPosNo(posNo);
    }
    
    /**
     * 免填单申请书打印入口
     * @param sessionStatus
     * @return String
     * @author GaoJiaMing 
     * @time 2014-6-23
     */
    @RequestMapping(value = "/application")
    public String applicationEntry(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return ViewNames.PRINT_APPLICATION;
    }

    /**
     * 免填单申请书打印预览
     * @param posNo
     * @return ModelAndView
     * @author GaoJiaMing 
     * @time 2014-6-23
     */
    @RequestMapping(value = "/applicationPrintPreview")
    public ModelAndView applicationPrintPreview(@RequestParam String posNo) {
        ModelAndView mav = new ModelAndView();
        // 查询是否是免填单业务
        String isApplicationFree = queryService.checkIsApplicationFree(posNo);
        String barCodeNo = queryService.getBarCodeNoByPosNo(posNo);
        if ("Y".equals(isApplicationFree) && !barCodeNo.startsWith("1234")) {
            // 查询关联保全号
            List<PosInfoDTO> posInfoDTOList = printService.queryPosNosByPosNo(posNo);
            for (PosInfoDTO posInfoDTO : posInfoDTOList) {
                Map<String, Object> map = printService.queryFillFormApplicationInfoByPosNo(posInfoDTO.getPosNo());
                if (map != null && !map.isEmpty()) {
                    posInfoDTO.setAcceptor((String) map.get("ACCEPTOR_NAME"));
                    posInfoDTO.setRemark((String) map.get("ACCEPT_DATE"));
                    posInfoDTO
                            .setApproveText((String) map.get("APPROVE_TEXT")
                                    + ((String) map.get("REMINDING_TEXT") != null ? ("\n" + (String) map
                                            .get("REMINDING_TEXT")) : "")
                                    + ((String) map.get("TIPS") != null ? ("\n" + (String) map.get("TIPS")) : ""));
                } else {
                    mav.setViewName(ViewNames.PRINT_APPLICATION);
                    mav.addObject("message", "找不到申请书信息！");
                    mav.addObject("POS_NO", posNo);
                    return mav;
                }
            }
            if (posInfoDTOList != null && !posInfoDTOList.isEmpty()) {
                mav.setViewName(ViewNames.PRINT_APPLICATION_PREVIEW);
                mav.addObject("posInfoDTOList", posInfoDTOList);
            } else {
                mav.setViewName(ViewNames.PRINT_APPLICATION);
                mav.addObject("message", "找不到申请书信息！");
            }
        } else {
            mav.setViewName(ViewNames.PRINT_APPLICATION);
            if ("N".equals(isApplicationFree)) {
                mav.addObject("message", "不是免填单业务，不能打印申请书！");
            }else if (barCodeNo.startsWith("1234")) {
                mav.addObject("message", "非补退费类保全项目不需要打印申请书！");
            }
        }
        mav.addObject("POS_NO", posNo);
        return mav;
    }
    
    /**
     * 投保人名下保单资产证明书
     * @methodName: clientWealthNotice
     * @Description: 
     * @param sessionStatus
     * @return String 
     * @author WangMingShun
     * @date 2015-7-22
     * @throws
     */
    @RequestMapping(value = "/clientWealthNotice")
    public String clientWealthNotice(SessionStatus sessionStatus) {
    	sessionStatus.setComplete();
    	return ViewNames.PRINT_CLIENT_WEALTH_NOTICE;
    }
    
    /**
     * 投保人名下保单资产证明书打印预览
     * @methodName: clientWeathNoticePreview
     * @Description: 
     * @param clientNo
     * @param applyDate
     * @return ModelAndView 
     * @author WangMingShun
     * @date 2015-7-22
     * @throws
     */
    @RequestMapping(value = "/clientWealthNoticePreview", method=RequestMethod.POST)
    @BrowserNoCache(enableNoCache=false)
	@Transactional(propagation=Propagation.REQUIRED)
    public ModelAndView clientWeathNoticePreview(@RequestParam String clientNo, 
    		@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd")Date applyDate,
    		HttpServletResponse response) {
    	ModelAndView mav = new ModelAndView(ViewNames.PRINT_CLIENT_WEALTH_NOTICE);
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	Date serverDate = null;
    	//获取数据库时间并格式化去掉时分秒
    	String dateStr = PosUtils.formatDate(commonQueryDAO.getSystemDate(), "yyyy-MM-dd");
    	try {
    		serverDate = sdf.parse(dateStr);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		//传递进来的时间和数据库时间不同，则取数据库时间
		if(applyDate.getTime() != serverDate.getTime()) {
			applyDate = serverDate;
		}
    	//取得通知书处理器
    	NoticePrintHandler handler = NoticePrintHandlerFactory.getNoticePrintHandler("14");
    	OutputStream os = null;
		InputStream is = null;
		File file = null;
		try {
			String detailSequenceNo = null;
			//生成资产证明书
			Map<String, Object> retMap = printDAO.procClientWealthNotice(clientNo, applyDate);
			if(!"0".equals(retMap.get("p_flag"))) {
				mav.addObject("message", "逐单抽档投保人名下保单资产证明书失败：" + retMap.get("p_message"));
				return mav;
			}
			detailSequenceNo = (String) retMap.get("p_detail_sequence_no");
			if(detailSequenceNo == null || StringUtils.isBlank(detailSequenceNo)) {
				mav.addObject("message", "没有符合查询条件的资产证明书记录");
				return mav;
			}
    	
			//生成PDF
			file = handler.handleSingleNoticePrint(detailSequenceNo);
			SFFile sigFile = handlePdfMail.signature((SFFile) file,detailSequenceNo,"toprsement");
			is = new FileInputStream(sigFile);
			os = response.getOutputStream();
			handler.processPdfFileNameHeader(getHttpServletRequest(), getHttpServletResponse(), clientNo);
			IOUtils.copy(is, os);
			return null;
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("打印资产证明书出错：" + e.getMessage());
		} finally {
			PosUtils.safeCloseOuputStream(os);
			PosUtils.safeCloseInputStream(is);
			if(file != null && file.exists()) {
				file.delete();
			}
		}
    }
}
