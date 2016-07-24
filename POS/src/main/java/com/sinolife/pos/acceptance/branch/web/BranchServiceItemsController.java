package com.sinolife.pos.acceptance.branch.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.sinolife.esbpos.irs.IrsCompareService;
import com.sinolife.esbpos.web.WebPosService;
import com.sinolife.pos.acceptance.branch.service.BranchAcceptService;
import com.sinolife.pos.acceptance.branch.service.BranchQueryService;
import com.sinolife.pos.common.consts.SessionKeys;
import com.sinolife.pos.common.consts.ViewNames;
import com.sinolife.pos.common.dao.ClientInfoDAO;
import com.sinolife.pos.common.dao.CommonAcceptDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.InsuredClientDTO;
import com.sinolife.pos.common.dto.PersonalNoticeDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.PersonalNoticeDTO.InsProductInfo;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.PosStatusChangeHistoryDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_20;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.common.web.PosAbstractController;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.util.Util;

/**
 * 保全项目信息录入controller
 * 
 */
@Controller
@RequestMapping("/acceptance/branch/")
@SessionAttributes(value = { SessionKeys.ACCEPTANCE_ITEM_INPUT,
		SessionKeys.ACCEPTANCE_PROCESS_RESULT_LIST })
public class BranchServiceItemsController extends PosAbstractController {

	/* 服务 */
	@Autowired
	private BranchQueryService queryService;

	@Autowired
	private BranchAcceptService acceptService;

	@Autowired
	private CommonQueryDAO commonQueryDAO;

	@Autowired
	private ClientInfoDAO clientInfoDAO;

	@Autowired
	private TransactionTemplate txTmpl;
	
	@Autowired
	private CommonAcceptDAO acceptDAO;

	/**
	 * 展示或返回受理详细信息录入页面
	 */
	@RequestMapping(value = "/acceptDetailInput/{posBatchNo}", method = RequestMethod.GET)
	public String acceptDetailInputBatchEntry(
			@PathVariable("posBatchNo") String posBatchNo,
			SessionStatus sessionStatus) {
		sessionStatus.setComplete();

		// 调用服务，获取批次中第一个未完成受理
		String posNo = acceptService.getNextPosNoInBatch(posBatchNo);
		if (posNo == null)
			throw new RuntimeException("找不到未完成受理");

		return "redirect:/acceptance/branch/acceptDetailInput/" + posBatchNo
				+ "/" + posNo;
	}

	/**
	 * 展示或返回受理详细信息录入页面 (保全项目页面)
	 */
	@RequestMapping(value = "/acceptDetailInput/{posBatchNo}/{posNo}", method = RequestMethod.GET)
	public String acceptDetailInputEntry(
			@PathVariable("posBatchNo") String posBatchNo,
			@PathVariable("posNo") String posNo, HttpSession session,
			ModelMap model) {

		// 这里使用Session.getAttribute不使用参数注入，是为了防止框架根据类型声明自动实例化为父类对象
		ServiceItemsInputDTO itemsInputDTO = (ServiceItemsInputDTO) session
				.getAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT);

		if (itemsInputDTO == null
				|| StringUtils.isBlank(itemsInputDTO.getPosNo())
				|| !posNo.equals(itemsInputDTO.getPosNo())) {
			// 根据保全项目查询保全信息，这里得到的是具体的子类DTO对象
			try {
				itemsInputDTO = queryService
						.queryAcceptDetailInputByPosNo(posNo);

				itemsInputDTO.setAcceptClientNo(commonQueryDAO
						.getacceptClientNoByPosNo(posNo));

				logger.info("acceptDetailInputEntry ServiceItemsInputDTO:"
						+ PosUtils.describBeanAsJSON(itemsInputDTO));
			} catch (RuntimeException re) {
				logger.error(re.getMessage(), re);
				model.addAttribute("errorMessage", re.getMessage());
				model.addAttribute("posNo", posNo);
				return ViewNames.ACCEPTANCE_BRANCH_ACCEPT_ERROR;
			}
		}
		model.addAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT, itemsInputDTO);
		return ViewNames.ACCEPTANCE_BRANCH_ACCEPT_DETAIL_INPUT
				+ itemsInputDTO.getServiceItems();
	}

	/**
	 * 更新客户号
	 */
	@RequestMapping(value = "/acceptDetailInput/{posBatchNo}/{posNo}", method = RequestMethod.POST, params = "action_updateClientNo")
	public ModelAndView updateClientNo(
			@RequestParam String clientSelection,
			@ModelAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT) ServiceItemsInputDTO itemsInputDTO,
			SessionStatus sessionStatus) {
		ModelAndView mav = new ModelAndView(
				ViewNames.ACCEPTANCE_BRANCH_ACCEPT_DETAIL_INPUT
						+ itemsInputDTO.getServiceItems());
		//家庭单多被保险人 传过来的clientSelection是"I,b被保人客户号"  update by  zhangyi.wb
		String [] aa = clientSelection.split(",");
		clientSelection = aa[0];
		if ("A".equals(clientSelection) || "I".equals(clientSelection)) {
			PosInfoDTO posInfo = commonQueryDAO
					.queryPosInfoRecord(itemsInputDTO.getPosNo());
			String policyNo = posInfo.getPolicyNo();
			String clientNo = null;
			if ("A".equals(clientSelection)) {
				clientNo = commonQueryDAO.getApplicantByPolicyNo(policyNo);
			} else if ("I".equals(clientSelection)) {
//				clientNo = commonQueryDAO
//						.getInsuredOfPrimaryPlanByPolicyNo(policyNo);
				//家庭单多被保险人更改 add by zhangyi.wb
				clientNo = aa[1];
			}
			if (StringUtils.isNotBlank(clientNo)
					&& !clientNo.equals(posInfo.getClientNo())) {
				// 当前posInfo的客户号与预期不一致才需要变更
				acceptService.updatePosInfo(posInfo.getPosNo(), "client_no",
						clientNo);
				sessionStatus.setComplete();
				mav.setViewName("redirect:/acceptance/branch/acceptDetailInput/"
						+ itemsInputDTO.getBatchNo()
						+ "/"
						+ itemsInputDTO.getPosNo());
			}
		} else {
			mav.addObject("errorMessage", "无效的客户选项：" + clientSelection);
		}
		return mav;
	}

	/**
	 * 受理详细信息录入提交。 调用流转接口 处理类型为受理 保全项目页面录入后提交
	 */
	@RequestMapping(value = "/acceptDetailInput/{posBatchNo}/{posNo}", method = RequestMethod.POST)
	@Transactional(propagation = Propagation.REQUIRED)
	public ModelAndView acceptDetailInputSubmit(
			@PathVariable("posBatchNo") String posBatchNo,
			@PathVariable("posNo") String posNo,
			@ModelAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT) ServiceItemsInputDTO itemsInputDTO,
			BindingResult bindingResult) {
		
		if ("N".equals(commonQueryDAO.posStatusCheck(posNo, "A01"))) {

			throw new RuntimeException(
					"保全受理详细信息录入异常，这可能是由于客户端重复提交产生的问题，请核实保全数据对应的状态");
		}

		if (!posNo.equals(itemsInputDTO.getPosNo())) {

			throw new RuntimeException(
					"保全受理详细信息录入异常，存在不同保全共用一个session数据的情况，请重新打开浏览器操作！");
		}

		ModelAndView mav = new ModelAndView();
		logger.info("acceptDetailInputSubmit ServiceItemsInputDTO:"
				+ itemsInputDTO);

		// 校验
		acceptService
				.validateServiceItemsInputDTO(itemsInputDTO, bindingResult);

		if (bindingResult.hasErrors()) {
			mav.setViewName(ViewNames.ACCEPTANCE_BRANCH_ACCEPT_DETAIL_INPUT
					+ itemsInputDTO.getServiceItems());
			mav.addObject(SessionKeys.ACCEPTANCE_ITEM_INPUT, itemsInputDTO);

			return mav;
		}

		// 写受理明细
		acceptService.acceptDetailInput(itemsInputDTO);

		// 查询前一单是否已经受理完成
		boolean prePosIsEnd = acceptService.prePosnoIsEnd(posNo);

		List<String> posNoList = itemsInputDTO.getPosNoList();
		List<Map<String, Object>> processResultList = new ArrayList<Map<String, Object>>();

		// 延时规则生效标志
		for (int i = 0; i < posNoList.size(); i++) {
			posNo = posNoList.get(i);

			// 更新备注字段
			acceptService.updatePosInfo(posNo, "REMARK",
					itemsInputDTO.getRemark());

			// 保存个人资料告知书
			if (itemsInputDTO.getAppPersonalNotice() != null) {
				PersonalNoticeDTO personalNoticeApp = itemsInputDTO
						.getAppPersonalNotice();
				if (itemsInputDTO instanceof ServiceItemsInputDTO_20) {
					// 投保人变更时，录入的告知对象为新投保人的信息
					ServiceItemsInputDTO_20 item = (ServiceItemsInputDTO_20) itemsInputDTO;
					String newApplicantNo = item.getApplicantNo();
					ClientInformationDTO newApplicantInfo = clientInfoDAO
							.selClientinfoForClientno(newApplicantNo).get(0);
					personalNoticeApp.setClientInfo(newApplicantInfo);
				}
				acceptService.savePersonalNotice(personalNoticeApp, posNo,
						itemsInputDTO.getPolicyNo());
			}
			if (itemsInputDTO.getInsPersonalNoticeList() != null) {
				List<PersonalNoticeDTO> insPersonalNoticeList = itemsInputDTO.getInsPersonalNoticeList();
				//家庭单 多被保险人对应多个告知 add by zhangyi.wb
				List<InsuredClientDTO> insuredInfoList = commonQueryDAO.getInsuredClientList(itemsInputDTO.getPolicyNo());
				//检查个人资料告知是否填写全（一个被保人对应一个告知）
				if(insuredInfoList.size()==insPersonalNoticeList.size()){
					//个人资料告知填写全
					for(PersonalNoticeDTO insPersonalNotice : insPersonalNoticeList){
						//投保人变更特殊处理
						if (itemsInputDTO instanceof ServiceItemsInputDTO_20) {
							// 投保人变更时，录入的告知对象为新投保人的信息
							ServiceItemsInputDTO_20 item = (ServiceItemsInputDTO_20) itemsInputDTO;
							String newApplicantNo = item.getApplicantNo();
							for(InsuredClientDTO insuredClientDTO:insuredInfoList){
								if(insuredClientDTO.getInsuredSeq().equals(insPersonalNotice.getInsuredSeq())){
									//录入的新投保人与被保险人不相同时保存个人资料告知
									if(!newApplicantNo.equals(insuredClientDTO.getInsuredNo())){
										acceptService.savePersonalNotice(
												insPersonalNotice, posNo,
												itemsInputDTO.getPolicyNo());
									}
								}
							}
							
						}else{
							acceptService.savePersonalNotice(
									insPersonalNotice, posNo,
									itemsInputDTO.getPolicyNo());
						}
					}
				}else{
					//个人资料告知没有填写全 
					throw new RuntimeException(
					"保全受理详细信息录入异常，部分被保人“最新个人资料告知”没有填写，请检查");
				}
			}

			// 写状态变迁记录
			PosStatusChangeHistoryDTO statusChange = new PosStatusChangeHistoryDTO();
			statusChange.setPosNo(posNo);
			statusChange.setOldStatusCode("A01");
			statusChange.setNewStatusCode(prePosIsEnd ? "A03" : "A02");
			statusChange.setChangeUser(PosUtils.getLoginUserInfo()
					.getLoginUserID());
			acceptService.insertPosStatusChangeHistory(statusChange);
			if (!"0".equals(statusChange.getFlag()))
				throw new RuntimeException("写保全状态变更记录失败："
						+ statusChange.getMessage());

			if (prePosIsEnd) {
				// 前一单受理完成，则更新状态为 A03待处理规则检查
				acceptService.updatePosInfo(posNo, "ACCEPT_STATUS_CODE", "A03");

				// 调用流转接口
				Map<String, Object> retMap = acceptService.workflowControl(
						"03", posNo, false);
				logger.info("acceptDetailInputSubmit workflowControl return:"
						+ retMap);

				String flag = (String) retMap.get("flag");
				String msg = (String) retMap.get("message");
				String ruleInfo = (String) retMap.get("ruleInfo");

				retMap = queryService.queryProcessResult(posNo);
				if ("C01".equals(flag)) {
					// 规则检查不通过
					retMap.put("RULE_CHECK_MSG", msg); // 批文预览界面使用
					retMap.put("RESULT_MSG", msg); // 最终结果界面使用
				} else if ("A03".equals(flag)) {
					// 延时规则
					retMap.put("RULE_CHECK_MSG", "由于延时规则不通过，待处理规则检查：<br/>"
							+ ruleInfo + "<br/>等待以上原因解除后，系统将自动完成该保全受理。");
					retMap.put("RESULT_MSG", "由于延时规则不通过，待处理规则检查：<br/>"
							+ ruleInfo + "<br/>等待以上原因解除后，系统将自动完成该保全受理。"); // 为发生延时规则时预备显示
					retMap.put("DELAY_FLAG", "Y");
				} else if (!"A19".equals(flag)) {
					throw new RuntimeException("未知的状态：" + flag);
				}
				processResultList.add(retMap);
			} else {
				// 前一单受理未完成，则更新状态为 A02 已录入受理明细
				acceptService.updatePosInfo(posNo, "accept_status_code", "A02");

				Map<String, Object> resultMap = queryService
						.queryProcessResult(posNo);
				resultMap.put("RESULT_MSG", "已录入受理明细");
				processResultList.add(resultMap);
			}

		}
		if (prePosIsEnd) {
			// 前一单受理完成
			//调用irs系统人脸识别  获取控件参数接口  add by zhangyi.wb
			IrsCompareService irsCompareService = PlatformContext.getEsbContext().getEsbService(IrsCompareService.class);
			Map<String, Object> tMap= new HashMap<String, Object>();
			tMap.put("terminalType", "1");//设备类型（1：pc 2:ios 3:安卓 4：flex）
			tMap.put("compareModel", "301");//比对模式（ 301:保全柜面  303:生命云服务 ，305:E动生命）
			try{
				String controlParameter = irsCompareService.getControlParameters(tMap);
				if(StringUtils.isNotBlank(controlParameter)){
					itemsInputDTO.setFaceControlParameter(controlParameter);
					itemsInputDTO.setFaceControlParameterFlag("Y");
				}else{
					logger.error("保全号："+posNo+",获取人脸识别控件参数错误！");
					itemsInputDTO.setFaceControlParameter("");
					itemsInputDTO.setFaceControlParameterFlag("N");
				}
				
			}catch (Exception e) {
				logger.error(e.getMessage());
				itemsInputDTO.setFaceControlParameter("");
				itemsInputDTO.setFaceControlParameterFlag("E");
			}
			itemsInputDTO.setIsBranchFlag(commonQueryDAO.isBranchFlag(posNo));
			//获取控件
			String irsControlCAB =  PlatformContext.getSystemDefination().getAttribute("irsControlCAB");
			itemsInputDTO.setIrsControlCAB(irsControlCAB);
			//String irsControlEXE =  PlatformContext.getSystemDefination().getAttribute("irsControlEXE");
			mav.setViewName("redirect:/acceptance/branch/processResult.do");
			mav.addObject(SessionKeys.ACCEPTANCE_ITEM_INPUT, itemsInputDTO);
			mav.addObject(SessionKeys.ACCEPTANCE_PROCESS_RESULT_LIST,
					processResultList);
		} else {
			// 前一单受理未完成
			mav.addObject("hasNext", StringUtils.isNotBlank(acceptService
					.getNextPosNoInBatch(posBatchNo)));
			mav.addObject("posBatchNo", posBatchNo);
			mav.addObject(SessionKeys.ACCEPTANCE_PROCESS_RESULT_LIST,
					processResultList);
			mav.setViewName(ViewNames.ACCEPTANCE_BRANCH_FINISH);
		}
		return mav;
	}

	/**
	 * 展示或返回经办结果展示页面
	 */
	@RequestMapping(value = "/processResult")
	public String processResultEntry() {
		return ViewNames.ACCEPTANCE_BRANCH_PROCESS_RESULT;
	}

	/**
	 * 经办结果确认
	 */
	@RequestMapping(value = "/processResultSubmit")
	@Transactional(propagation = Propagation.REQUIRED)
	public ModelAndView processResultSubmit(
			@ModelAttribute(SessionKeys.ACCEPTANCE_PROCESS_RESULT_LIST) List<Map<String, Object>> processResultList,
			@ModelAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT) ServiceItemsInputDTO itemsInputDTO,
			SessionStatus status) {

		for (int i = 0; processResultList != null
				&& i < processResultList.size(); i++) {
			Map<String, Object> processResultMap = processResultList.get(i);
			if (!processResultMap.containsKey("RULE_CHECK_MSG")) {
				// 流转接口
				String posNo = (String) processResultList.get(i).get("POS_NO");
				Map<String, Object> retMap = acceptService.workflowControl(
						"05", posNo, false);
				logger.info("processResultSubmit workflowControl return:"
						+ retMap);

				String flag = (String) retMap.get("flag");
				String msg = (String) retMap.get("message");

				String resultMsg = null;
				if ("A08".equals(flag)) {
					// 已送审
					resultMsg = "已送审";
				} else if ("A12".equals(flag)) {
					// 已送人工核保
					resultMsg = "已送人工核保";
				} else if ("A15".equals(flag)) {
					// 待收费
					resultMsg = "待收费";
				} else if ("E01".equals(flag)) {
					// 生效完成
					resultMsg = "已生效";
					// 成长红包操作了退保、契撤后，调用下面的接口通知微信端
					if ("Y".equals(clientInfoDAO.queryWebNeedNotify(posNo))) {
						String applyBarCode = commonQueryDAO
								.queryPolicyApplyBarcode(itemsInputDTO
										.getPolicyNo());
						Map<String, String> reqMap = new HashMap<String, String>();
						reqMap.put("businessNo", "1002");
						reqMap.put("policyNo", itemsInputDTO.getPolicyNo());
						reqMap.put("applyNo", applyBarCode);

						WebPosService wps = PlatformContext.getEsbContext()
								.getEsbService(WebPosService.class);

						wps.surrenderNotify(reqMap);
					}
				} else if ("A17".equals(flag)) {
					// 待保单打印
					resultMsg = "待保单打印";
				} else {
					resultMsg = msg;
				}
				processResultMap.put("RESULT_MSG", resultMsg);
				// 查询是否需要打印保证价值表或条款
				boolean isEndorsementHasNoteOrPlanProvision = queryService
						.isEndorsementHasNoteOrPlanProvision(posNo);
				processResultMap.put("isEndorsementHasNoteOrPlanProvision",
						isEndorsementHasNoteOrPlanProvision);

				queryService.noImageEmailAcceptor(flag, posNo);
			}
		}

		ModelAndView mav = new ModelAndView();

		mav.addObject("hasNext", StringUtils.isNotBlank(acceptService
				.getNextPosNoInBatch(itemsInputDTO.getBatchNo())));
		mav.addObject("posBatchNo", itemsInputDTO.getBatchNo());

		mav.setViewName(ViewNames.ACCEPTANCE_BRANCH_FINISH);

		// 清空Session缓存
		status.setComplete();

		return mav;
	}

	/**
	 * 保全开放趸交可贷产品保单利率上浮最高至保单现价的90%
	 * 
	 * @param policyNo
	 *            ,posNo
	 * @return
	 */
	@RequestMapping(value = "/queryMaxLoanCash")
	@ResponseBody
	public double queryMaxLoanCash(
			@ModelAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT) ServiceItemsInputDTO itemsInputDTO,
			String policyNo, String posNo, String specialFunc) {

		double maxLoanCash = queryService.queryMaxLoanCash(policyNo, posNo,
				specialFunc);
		itemsInputDTO.setLoanMaxSum(new BigDecimal(maxLoanCash));

		return maxLoanCash;
	}

	/**
	 * 协议退费原因查询
	 * 
	 * @param specialFunc
	 * @return
	 */
	@RequestMapping(value = "/querySpecialRetreatReason")
	@ResponseBody
	public List<String> querySpecialRetreatReason(String specialFunc) {
		return queryService.querySpecialRetreatReason(specialFunc);
	}

	/**
	 * 经办结果撤销
	 */
	@RequestMapping(value = "/processResultCancel")
	@Transactional(propagation = Propagation.REQUIRED)
	public ModelAndView processResultCancel(
			@RequestParam String cancelCause,
			@RequestParam String cancelRemark,
			@ModelAttribute(SessionKeys.ACCEPTANCE_PROCESS_RESULT_LIST) List<Map<String, Object>> processResultList,
			@ModelAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT) ServiceItemsInputDTO itemsInputDTO,
			SessionStatus status) {

		for (int i = 0; processResultList != null
				&& i < processResultList.size(); i++) {
			Map<String, Object> processResultMap = processResultList.get(i);
			String posNo = (String) processResultMap.get("POS_NO");
			// 更新取消原因
			acceptService.updatePosInfo(posNo, "ROLLBACK_CAUSE", cancelCause);
			// 更新取消详细信息
			acceptService
					.updatePosInfo(posNo, "ABANDONED_DETAIL", cancelRemark);
			PosInfoDTO posInfo = commonQueryDAO.queryPosInfoRecord(posNo);
			// 取消受理
			acceptService.cancelAccept(posInfo, PosUtils.getLoginUserInfo()
					.getLoginUserID());
			processResultMap.put("RESULT_MSG", "已取消");
		}
		ModelAndView mav = new ModelAndView(ViewNames.ACCEPTANCE_BRANCH_FINISH);

		mav.addObject("hasNext", StringUtils.isNotBlank(acceptService
				.getNextPosNoInBatch(itemsInputDTO.getBatchNo())));
		mav.addObject("posBatchNo", itemsInputDTO.getBatchNo());
		// 清空Session缓存
		status.setComplete();
		return mav;
	}

	/**
	 * 捆绑顺序查询
	 */
	@RequestMapping(value = "/bindingOrderQuery")
	@ResponseBody
	public Object bindingOrderQuery(String posBatchNo) {
		List<Map<String, String>> batchPosInfo = queryService
				.queryBatchPosInfo(posBatchNo);
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("rows", batchPosInfo);
		retMap.put("total", batchPosInfo.size());
		return retMap;
	}

	/**
	 * 个人资料告知入口
	 */
	@RequestMapping(value = "/personalNotice", method = RequestMethod.GET)
	public ModelAndView personalNoticeEntry(
			@RequestParam String target,
			@RequestParam String insuredSeq,
			@ModelAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT) ServiceItemsInputDTO itemsInputDTO) {
		ModelAndView mav = new ModelAndView(
				ViewNames.ACCEPTANCE_BRANCH_PERSONAL_NOTICE);
		PersonalNoticeDTO personalNoticeDTO = null;
		if ("app".equalsIgnoreCase(target)) {
			personalNoticeDTO = itemsInputDTO.getAppPersonalNotice();
		} else if ("ins".equalsIgnoreCase(target)) {
			//personalNoticeDTO = itemsInputDTO.getInsPersonalNotice();
			//家庭单多被保险人多个资料告知
			List<PersonalNoticeDTO> insPersonalNoticeList= itemsInputDTO.getInsPersonalNoticeList();
			if(null!=insPersonalNoticeList){
				for(PersonalNoticeDTO personalNotice:insPersonalNoticeList){
					if(insuredSeq.equals(personalNotice.getInsuredSeq())){
						personalNoticeDTO = personalNotice;
					}
				}
			}

		} else {
			throw new RuntimeException("无效的参数target=" + target);
		}
		if (personalNoticeDTO == null) {
			personalNoticeDTO = new PersonalNoticeDTO();
			personalNoticeDTO
					.setInsProductList(new ArrayList<InsProductInfo>());
			personalNoticeDTO.getInsProductList().add(new InsProductInfo());
			personalNoticeDTO
					.setClientOption("app".equalsIgnoreCase(target) ? "A" : "I");
			personalNoticeDTO.setItemOption(new HashSet<String>());
			//家庭单多被保险人（每个被保险人对应一个资料告知） add by zhangyi.wb
			personalNoticeDTO.setInsuredSeq(insuredSeq);
		}
		mav.addObject("personalNoticeDTO", personalNoticeDTO);
		return mav;
	}

	/**
	 * 清除个人资料告知信息
	 */
	@RequestMapping(value = "/personalNotice", method = RequestMethod.GET, params = { "clear" })
	@ResponseBody
	public Map<String, String> personalNoticeClear(
			@RequestParam String target,
			@RequestParam String insuredSeq,
			@ModelAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT) ServiceItemsInputDTO itemsInputDTO) {
		if ("app".equalsIgnoreCase(target)) {
			itemsInputDTO.setAppPersonalNotice(null);
		} else if ("ins".equalsIgnoreCase(target)) {
			//itemsInputDTO.setInsPersonalNotice(null);
			//家庭单多被保险人多个资料告知
//			List<PersonalNoticeDTO> insPersonalNoticeList= itemsInputDTO.getInsPersonalNoticeList();
//			for(PersonalNoticeDTO personalNotice:insPersonalNoticeList){
//				if(insuredSeq.equals(personalNotice.getInsuredSeq())){
//					insPersonalNoticeList.remove(personalNotice);
//					break;
//				}
//			}
			//删除清空所有告知
			itemsInputDTO.setInsPersonalNoticeList(null);
		} else {
			throw new RuntimeException("无效的参数target=" + target);
		}
		Map<String, String> retMap = new HashMap<String, String>();
		retMap.put("flag", "Y");
		return retMap;
	}

	/**
	 * 个人资料告知提交
	 */
	@RequestMapping(value = "/personalNotice", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> personalNoticeSubmit(
			PersonalNoticeDTO pn,
			BindingResult bindingResult,
			@RequestParam String insuredSeq,
			@ModelAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT) ServiceItemsInputDTO itemsInputDTO) {
		logger.info("personalNoticeSubmit:" + PosUtils.describBeanAsJSON(pn));
		Map<String, String> retMap = new HashMap<String, String>();
		if (bindingResult.hasErrors()) {
			retMap.put("flag", "N");
			retMap.put("msg", "校验错误，请检查录入数据。");
			return retMap;
		}
		if (pn.getItemOption() == null || pn.getItemOption().isEmpty()) {
			retMap.put("flag", "N");
			retMap.put("msg", "校验错误，告知项不能为空。");
			return retMap;
		}
		if (pn.getInsProductList() == null) {
			pn.setInsProductList(new ArrayList<InsProductInfo>());
		}
		if (pn.getInsProductList().isEmpty()) {
			pn.getInsProductList().add(new InsProductInfo());
		}
		if ("A".equals(pn.getClientOption())) {
			itemsInputDTO.setAppPersonalNotice(pn);
		} else if ("I".equals(pn.getClientOption())) {
			//itemsInputDTO.setInsPersonalNotice(pn);
			//家庭单多被保人（每个被保人对应一个资料告知）add by zhangyi.wb
			List<PersonalNoticeDTO> insPersonalNoticeList = new ArrayList<PersonalNoticeDTO>();
			if(null!=itemsInputDTO.getInsPersonalNoticeList()){
				insPersonalNoticeList= itemsInputDTO.getInsPersonalNoticeList();
				for(PersonalNoticeDTO personalNotice:insPersonalNoticeList){
					if(insuredSeq.equals(personalNotice.getInsuredSeq())){
						insPersonalNoticeList.remove(personalNotice);
						break;
					}
				}
			}
			insPersonalNoticeList.add(pn);
			itemsInputDTO.setInsPersonalNoticeList(insPersonalNoticeList);
		}
		retMap.put("flag", "Y");
		return retMap;
	}

	/**
	 * 录受理明细时的撤销
	 */
	@RequestMapping(value = "/cancelService")
	@ResponseBody
	public Map<String, Object> cancelService(@RequestParam final String posNo,
			@RequestParam final String cancelCause,
			@RequestParam final String cancelRemark, final SessionStatus status) {
		return txTmpl.execute(new TransactionCallback<Map<String, Object>>() {
			@Override
			public Map<String, Object> doInTransaction(
					TransactionStatus transactionStatus) {
				Map<String, Object> returnMap = new HashMap<String, Object>();
				try {
					// 更新取消原因
					acceptService.updatePosInfo(posNo, "ROLLBACK_CAUSE",
							cancelCause);
					// 更新取消详细信息
					acceptService.updatePosInfo(posNo, "ABANDONED_DETAIL",
							cancelRemark);
					// 调用流转接口撤销
					Map<String, Object> ret = acceptService.workflowControl(
							"11", posNo, false);
					logger.info("processResultCancel workflowControl return:"
							+ ret);

					String flag = (String) ret.get("flag");
					String msg = (String) ret.get("message");

					if ("C02".equals(flag)) {
						returnMap.put("flag", "Y");
						returnMap.put("message", "受理撤销成功");
					} else {
						returnMap.put("flag", "N");
						returnMap.put("message", "受理撤销失败：" + flag + ", " + msg);
						transactionStatus.setRollbackOnly();
					}
					String posBatchNo = commonQueryDAO
							.queryPosBatchNoByPosNo(posNo);
					returnMap.put("hasNext", StringUtils
							.isNotBlank(acceptService
									.getNextPosNoInBatch(posBatchNo)));
					returnMap.put("posBatchNo", posBatchNo);
				} catch (Exception e) {
					logger.error(e);
					returnMap.put("flag", "E");
					returnMap.put("message", "系统异常：" + e.getMessage());
					transactionStatus.setRollbackOnly();
				}
				return returnMap;
			}
		});
	}

	/**
	 * 断点恢复，用于代办任务时，重新恢复到最后的处理点
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/continueFromBreak")
	public String continueFromBreak(@RequestParam String posBatchNo,
			ModelMap model, SessionStatus sessionStatus) {
		Map<String, Object> retMap = queryService
				.queryBreakInfoByPosBatchNo(posBatchNo);
		String breakStatusCode = null;
		if (retMap != null)
			breakStatusCode = (String) retMap.get("breakStatusCode");

		if ("A01".equals(breakStatusCode)) {
			String posNo = (String) retMap.get("posNo");
			sessionStatus.setComplete();
			return "redirect:/acceptance/branch/acceptDetailInput/"
					+ posBatchNo + "/" + posNo;
		} else if ("A19".equals(breakStatusCode)
				|| "C01".equals(breakStatusCode)) {
			List<Map<String, Object>> processResultList = (List<Map<String, Object>>) retMap
					.get("processResultList");
			String posNo = (String) processResultList.get(0).get("POS_NO");
			ServiceItemsInputDTO itemsInputDTO = queryService
					.queryAcceptDetailInputByPosNo(posNo);
			//调用irs系统人脸识别  获取控件参数接口  add by zhangyi.wb
			IrsCompareService irsCompareService = PlatformContext.getEsbContext().getEsbService(IrsCompareService.class);
			Map<String, Object> tMap= new HashMap<String, Object>();
			tMap.put("terminalType", "1");//设备类型（1：pc 2:ios 3:安卓 4：flex）
			tMap.put("compareModel", "301");//比对模式（ 301:保全柜面  303:生命云服务 ，305:E动生命）
			try{
				String controlParameter = irsCompareService.getControlParameters(tMap);
				if(StringUtils.isNotBlank(controlParameter)){
					itemsInputDTO.setFaceControlParameter(controlParameter);
					itemsInputDTO.setFaceControlParameterFlag("Y");
				}else{
					logger.error("保全号："+posNo+",获取人脸识别控件参数错误！");
					itemsInputDTO.setFaceControlParameter("");
					itemsInputDTO.setFaceControlParameterFlag("N");
				}
				
			}catch (Exception e) {
				logger.error(e.getMessage());
				itemsInputDTO.setFaceControlParameter("");
				itemsInputDTO.setFaceControlParameterFlag("E");
			}
			// edit to end;
			//通过保全号查询受理人是否属于山东 江西单
			itemsInputDTO.setIsBranchFlag(commonQueryDAO.isBranchFlag(posNo));
			//获取控件
			String irsControlCAB =  PlatformContext.getSystemDefination().getAttribute("irsControlCAB");
			itemsInputDTO.setIrsControlCAB(irsControlCAB);
			model.addAttribute(SessionKeys.ACCEPTANCE_PROCESS_RESULT_LIST,
					processResultList);
			model.addAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT, itemsInputDTO);
			return "redirect:/acceptance/branch/processResult.do";
		} else {
			throw new RuntimeException("该批次已经完成！");
		}
	}

	/**
	 * 受理撤销并重新生成受理，用于在预处理完成后对批文不满意或出现处理规则检查不通过的情况需要修改受理内容，重新进行受理
	 * 
	 * @param posNo
	 * @return
	 */
	@RequestMapping(value = "/cancelAndReaccept")
	public ModelAndView cancelAndReaccept(@RequestParam final String posNo) {
		return txTmpl.execute(new TransactionCallback<ModelAndView>() {
			@Override
			public ModelAndView doInTransaction(
					TransactionStatus transactionStatus) {
				ModelAndView mav = new ModelAndView(
						ViewNames.ACCEPTANCE_BRANCH_PROCESS_RESULT);
				try {
					String acceptUser = PosUtils.getLoginUserInfo()
							.getLoginUserID();
					Map<String, Object> retMap = acceptService
							.cancelAndReaccept(posNo, "23", "批文预览撤销并重新生成受理",
									acceptUser);
					String posBatchNo = (String) retMap.get("posBatchNo");
					String newPosNo = (String) retMap.get("newPosNo");
					mav.setViewName("redirect:/acceptance/branch/acceptDetailInput/"
							+ posBatchNo + "/" + newPosNo);
				} catch (RuntimeException re) {
					logger.error(re);
					mav.addObject("RETURN_MSG", re.getMessage());
					transactionStatus.setRollbackOnly();
				} catch (Exception e) {
					logger.error(e);
					mav.addObject("RETURN_MSG", "系统异常：" + e.getMessage());
					transactionStatus.setRollbackOnly();
				}
				return mav;
			}
		});
	}

	@RequestMapping(value = "/clearAttechment")
	@ResponseBody
	public Map<String, Object> clearAttechment(
			@ModelAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT) ServiceItemsInputDTO itemsInputDTO) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		itemsInputDTO.setAttechmentFileId(null);
		itemsInputDTO.setAttechmentFileName(null);
		retMap.put("flag", "Y");
		return retMap;
	}

	/**
	 * 检验抵交续期保费保单号的有效性
	 * 
	 * @param payPolicy
	 * @param applyPolicy
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-9-22
	 */
	@RequestMapping(value = "/acceptDetailInput/checkPaypolicyValid", method = RequestMethod.GET)
	@ResponseBody
	public String checkPaypolicyValid(String payPolicy, String applyPolicy) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("payPolicy", payPolicy);
		map.put("applyPolicy", applyPolicy);
		acceptService.checkPaypolicyValid(map);
		return acceptService.checkPaypolicyValid(map);
	}
	
	/**
	 * @Description: 查看保单是否能做类信托
	 * @methodName: getPolicyCanDoPwm
	 * @param policyNo
	 * @return
	 * @return String
	 * @author WangMingShun
	 * @date 2015-8-12
	 * @throws
	 */
	@RequestMapping(value = "/serviceItem_22/policyCanDoPwm", method = RequestMethod.GET)
	@ResponseBody
	public String getPolicyCanDoPwm(String policyNo) {
		return commonQueryDAO.getPolicyCanDoPwm(policyNo);
	}
	
	/**
	 * @Description: 根据保单号查询最近一期续期保费退费信息
	 * @methodName: getFinPosPayInfo
	 * @param policyNo
	 * @return
	 * @return Map<String,Object>
	 * @author WangMingShun
	 * @date 2015-9-2
	 * @throws
	 */
	@RequestMapping(value = "/getFinPosPayInfo", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getFinPosPayInfo(String policyNo) {
		return commonQueryDAO.getFinPosPayInfo(policyNo);
	}
	

	/**
	 * 人脸识别
	 * @param request
	 * @param response
	 * @throws Exception void
	 * @author zhangyi.wb
	 * @time 2016-7-11
	 */
	@RequestMapping(value = "/faceRecognition")
	public void faceRecognition(HttpServletRequest request, HttpServletResponse response) throws Exception{
		request.setCharacterEncoding("UTF-8");
		String clientNo=request.getParameter("clientNoApp");
		ClientInformationDTO clientInfo = clientInfoDAO.queryClientInfoByClientNo(clientNo);
		String clientName=clientInfo.getClientName();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String birthday=format.format(clientInfo.getBirthday());
		String sexCode=clientInfo.getSexCode();
		String idType=clientInfo.getIdTypeCode();
		String idNo=clientInfo.getIdNo();       
		String serviceItems =request.getParameter("serviceItems");
		String posNo =request.getParameter("posNo");
		String imgBase64 =request.getParameter("imgBase64");
		String controlVersion =request.getParameter("controlVersion");
		Map<String, Object> qMap = new HashMap<String, Object>();
		qMap.put("clientName", clientName);
		qMap.put("birthday", birthday);
		qMap.put("sexCode", sexCode);
		qMap.put("idType", idType);
		qMap.put("idNo", idNo);
		qMap.put("imgBase64", imgBase64);
		//业务类型：  保全项
		qMap.put("businessType", serviceItems);
		
		qMap.put("businessNo", posNo);
		//客户来源 ： 05 保全柜面
		qMap.put("clientSource", "05");
		qMap.put("comScene", "0001");
		
		qMap.put("sourceVersion", "pos_v_0.01");
		//比对类型 01柜面 
		qMap.put("compareType", "1");
		String loginUserID = PosUtils.getLoginUserInfo().getLoginUserID();
		qMap.put("operatorUser", loginUserID);
		//终端类型 1：pc
		qMap.put("terminalType", "1");
		//获取用户电脑的ip
    	String ip=request.getRemoteAddr();
    	//String macAddr = PosUtils.getLocalMac(ip);
		qMap.put("equipSeq", ip);
		qMap.put("controlVersion", controlVersion);
		qMap.put("cryptType", "0");
		qMap.put("glass", "0");
		qMap.put("compareModel", "301");
		qMap.put("compareSystem", "");
		PosInfoDTO posInfop = commonQueryDAO.queryPosInfoRecord(posNo);
		//查询保单号
		String policy_no = commonQueryDAO.getPolicyNoByPosNo(posNo);
		qMap.put("branchCode", posInfop.getBranchCode());
		Map<String, Object> resultMap = new HashMap<String, Object>();
		//调用irs系统人脸识别 比对服务
		IrsCompareService irsCompareService = PlatformContext.getEsbContext().getEsbService(IrsCompareService.class);
		resultMap = irsCompareService.checkClientInfo(qMap);
		logger.info("保全号："+posNo+"人脸比对结果："+resultMap);
		String compareFlag = (String)resultMap.get("flag");
		// 写受理人工号到pos_accept_detail
		//查询受理明细表是否存在比对记录  ItemNo=33 old_value记录比对总次数，new_value记录最后一次比对标示
									 //ItemNo=34 old_value记录比对成功次数，new_value记录最后一次比对标示
		int totalCount = acceptDAO.getFaceRecognitionCount(posNo, "33");
		int sucCount = acceptDAO.getFaceRecognitionCount(posNo, "34");
		PosAcceptDetailDTO detailTotal = new PosAcceptDetailDTO();
		detailTotal.setPosNo(posNo);
		detailTotal.setPosObject("5");
		detailTotal.setProcessType("1");
		detailTotal.setItemNo("033");
		detailTotal.setObjectValue(policy_no);
		detailTotal.setNewValue(compareFlag);
		totalCount = totalCount+1;
		detailTotal.setOldValue(String.valueOf(totalCount));
		if(totalCount==1){
			acceptDAO.insertPosAcceptDetail(detailTotal);
		}else{
			acceptDAO.updatePosAcceptDetail(detailTotal);
		}
		if("Y".equals(compareFlag)){
			sucCount = sucCount+1;
			PosAcceptDetailDTO detailSuc = new PosAcceptDetailDTO();
			detailSuc.setPosNo(posNo);
			detailSuc.setPosObject("5");
			detailSuc.setProcessType("1");
			detailSuc.setItemNo("034");
			detailSuc.setObjectValue(policy_no);
			detailSuc.setNewValue(compareFlag);
			detailSuc.setOldValue(String.valueOf(sucCount));
			if(sucCount==1){
				acceptDAO.insertPosAcceptDetail(detailSuc);
			}else{
				acceptDAO.updatePosAcceptDetail(detailSuc);
			}
		}
		
		JSONObject o = JSONObject.fromObject(resultMap);
    	PrintWriter write = response.getWriter();
		write.write(o.toString());
		write.flush();
		write.close();
		o.clear();
	}
	/**
	 * ie插件下载
    */
	@RequestMapping(value = "/downloadTFace")
	public static void download(HttpServletResponse response) {
		//response.reset();
		try{
			OutputStream out = response.getOutputStream();
			//从um获取文件TFaceMS_SDK_Setup.exe下载地址
			String irsControlEXE =  PlatformContext.getSystemDefination().getAttribute("irsControlEXE");
			//InputStream in=PlatformContext.getServletContext().getResourceAsStream(irsControlEXE);
			URL url = new URL(irsControlEXE);
            // 打开连接
			URLConnection con = url.openConnection();
			// 输入流
			InputStream in = con.getInputStream();
			String fileName = java.net.URLEncoder.encode("TFaceMS_SDK_Setup.exe", "UTF-8");
			fileName = new String(fileName.getBytes("UTF-8"), "GBK");
			response.setHeader("Content-disposition", "attachment;filename=" + fileName);
			//response.setContentType("application/x-msdownload");
			response.setContentType("application/octet-stream");
			
			Util.copyStream(in, out);
			out.flush();
			out.close();
			in.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	/**
	 * 获取客户信息
	 * @param request
	 * @param response
	 * @throws Exception void
	 * @author zhangyi.wb
	 * @time 2016-7-11
	 */
	@RequestMapping(value = "/getClientInfoByClientNo")
	public void getClientInfoByClientNo(HttpServletRequest request, HttpServletResponse response) throws Exception{
		request.setCharacterEncoding("UTF-8");
		String clientNo=request.getParameter("clientNo");
		ClientInformationDTO clientInfo = clientInfoDAO.selClientinfoForClientno(clientNo).get(0);
		String clientName=clientInfo.getClientName();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String birthday=format.format(clientInfo.getBirthday());
		String sexDesc=clientInfo.getSexDesc();
		String idTypeDesc=clientInfo.getIdTypeDesc();
		String idNo=clientInfo.getIdNo();       
		Map<String, Object> qMap = new HashMap<String, Object>();
		qMap.put("clientName", clientName);
		qMap.put("birthday", birthday);
		qMap.put("sexDesc", sexDesc);
		qMap.put("idTypeDesc", idTypeDesc);
		qMap.put("idNo", idNo);	
		JSONObject o = JSONObject.fromObject(qMap);
    	PrintWriter write = response.getWriter();
		write.write(o.toString());
		write.flush();
		write.close();
		o.clear();
	}
	
	/**
	 * 人脸识别入口
	 */
	@RequestMapping(value = "/openFaceRecognition", method = RequestMethod.GET)
	public ModelAndView openFaceRecognition(@ModelAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT) ServiceItemsInputDTO itemsInputDTO) {
		ModelAndView mav = new ModelAndView(ViewNames.ACCEPTANCE_BRANCH_FACE_RECOGNITION);
		mav.addObject(SessionKeys.ACCEPTANCE_ITEM_INPUT, itemsInputDTO);
		return mav;
	}
}
