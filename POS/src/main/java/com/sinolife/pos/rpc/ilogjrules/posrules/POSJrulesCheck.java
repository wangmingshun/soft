/**
 * 保全规则引擎规则检查 
 * 
 * @author wangyunzhao
 */
package com.sinolife.pos.rpc.ilogjrules.posrules;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.life.foundation.common.lang.DateUtils;
import com.sinolife.pos.acceptance.branch.service.BranchQueryService;
import com.sinolife.pos.common.dao.ClientInfoDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.FinancialProductsDTO;
import com.sinolife.pos.common.dto.PolicyBeneficiaryDTO;
import com.sinolife.pos.common.dto.PolicyDTO;
import com.sinolife.pos.common.dto.PolicyPremInfoDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PolicyProductPremDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.rpc.ilogjrules.posrules.dao.PosRulesDAO;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMBeneficiaryDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMFundDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMInsuredDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMOwnerDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMServiceItemDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSUWBOMPlanDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSVerifyResultDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.ServiceItemDomainDto;
import com.sinolife.sf.ruleengine.service.RuleEngineClient;

@Component
public class POSJrulesCheck {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private PosRulesDAO posRulesDAO;

	@Autowired
	private CommonQueryDAO commonQueryDAO;

	@Autowired
	private ClientInfoDAO clientInfoDAO;

	@Autowired
	private BranchQueryService branchQueryService;

	@Autowired
	@Qualifier("ilogRulesPOS")
	private RuleEngineClient ruleEngineClient;

	protected static Map<String, String> SERVICE_ITEMS_TYPE_MAPPING;
	protected static Set<String> POLICY_OVER_LAPSE_REASON_SET;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		// 收退费类
		map.put("1", "1");
		map.put("2", "1");
		map.put("4", "1");
		map.put("9", "1");
		map.put("10", "1");
		map.put("11", "1");
		map.put("13", "1");
		map.put("17", "1");
		map.put("27", "1");
		// 核保类
		map.put("3", "2");
		map.put("5", "2");
		map.put("6", "2");
		map.put("12", "2");
		map.put("14", "2");
		map.put("15", "2");
		map.put("16", "2");
		map.put("20", "2");
		map.put("22", "2");
		map.put("35", "2");
		// 非收退费类
		map.put("7", "2");
		map.put("18", "2");
		map.put("19", "2");
		map.put("21", "2");
		map.put("23", "2");
		map.put("24", "2");
		map.put("25", "2");
		map.put("26", "2");
		map.put("28", "2");
		map.put("29", "2");
		map.put("30", "2");
		map.put("31", "2");
		map.put("32", "2");
		map.put("33", "2");
		map.put("34", "2");
		// 账户变更（非传统险类）
		map.put("36", "3");
		map.put("37", "3");
		map.put("38", "3");
		map.put("39", "3");
		SERVICE_ITEMS_TYPE_MAPPING = map;

		Set<String> set = Collections.synchronizedSet(new HashSet<String>());
		set.add("1"); // 保全：犹豫期内退保
		set.add("2"); // 保全：保全解约
		set.add("3"); // 理赔：理赔解约
		set.add("4"); // 保全：险种转换
		set.add("5"); // 理赔：责任给付完毕（客户死亡）
		set.add("6"); // 理赔：责任给付完毕（非客户死亡）
		set.add("7"); // 保全：险种保障期限到期
		set.add("9"); // 理赔：只对附加险，因主险终止随之无效
		set.add("10"); // 保全：拒保
		set.add("13"); // 理赔：理赔责任未给付（客户死亡）
		POLICY_OVER_LAPSE_REASON_SET = set;
	}

	/**
	 * 执行规则检查
	 * 
	 * @param bom
	 * @return
	 */
	public POSVerifyResultDto excuteRules(POSBOMPolicyDto bom) {
		String appliedRuleType = bom.getAppliedRuleType();
		String ruleName = "1".equals(appliedRuleType) ? "受理规则检查" : ("2"
				.equals(appliedRuleType) ? "处理规则检查" : "受理规则和处理规则检查");
		logger.info(ruleName + "POSBOMPolicyDto:" + bom);
		POSVerifyResultDto result = (POSVerifyResultDto) ruleEngineClient
				.excuteRules(bom);
		logger.info(ruleName + "POSVerifyResultDto:" + result);
		return result;
	}

	/**
	 * 创建用于受理规则检查的BOM
	 * 
	 * @param policyNo
	 * @param clientNo
	 * @param serviceItems
	 * @param applyDate
	 * @param acceptor
	 * @param applyTypeCode
	 * @param acceptChannelCode
	 * @param currentServiceItemsList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public POSBOMPolicyDto createBOMForAcceptRuleCheck(String policyNo,
			String clientNo, String serviceItems, Date applyDate,
			Date acceptDate, String acceptor, String applyTypeCode,
			String acceptChannelCode,Date idValidyDate,
			ServiceItemDomainDto[] currentServiceItemsList) {
		ServiceItemsExists.init(serviceItems);
		try {
			POSBOMPolicyDto bom = createBOMInternal(policyNo, clientNo,
					serviceItems, applyDate, acceptDate, acceptor, "1", null,
					null);
			// 保单介质
			bom.setPosMediaType(posRulesDAO.policyMediaType(policyNo));
			// 保单打印次数
			bom.setProvideTimes(posRulesDAO.policyPrivideTime(policyNo));
			// 申请类型
			bom.setApplyTypeCode(applyTypeCode);

			bom.setAcceptSource(acceptChannelCode);
			
			// 受理人是否有电销续期保费退费权限
			bom.setReturnPremPrivs(posRulesDAO.isReturnPremPrivs(acceptor));
			
			//申请客户证件有效期 
			bom.setIdValidDate(idValidyDate);
			
			// 投连首期保费是否未进入理财账户
			bom.setUwUntransFund(posRulesDAO.isUwUntransFund(policyNo));

			// 本次申请的保全项目
			for (POSUWBOMPlanDto plan : bom.getPlansList()) {
				plan.setCurrentServiceItemsList(currentServiceItemsList);
			}

			// 历史保全项目列表
			POSBOMServiceItemDto[] historyServiceItemsList = posRulesDAO
					.historyServiceItemsList(policyNo, acceptDate);
			bom.setHistoryServiceItemsList(historyServiceItemsList);

			// 是否暂停及暂停原因
			Map<String, Object> retMap = posRulesDAO.checkPolicySuspend(
					policyNo, serviceItems, null);
			if ("Y".equals(retMap.get("p_sign"))) {
				boolean isSuspended = "Y"
						.equals(retMap.get("p_suffer_suspend"));
				StringBuilder suspendedReason = new StringBuilder();
				List<CodeTableItemDTO> suspendItemList = (List<CodeTableItemDTO>) retMap
						.get("p_suspend_item_arr");
				// 受理规则检查
				isSuspended = false;
				if (suspendItemList != null && !suspendItemList.isEmpty()) {

					if ("7".equals(acceptChannelCode)) {
						isSuspended = true;

					} else {
						for (CodeTableItemDTO item : suspendItemList) {
							String code = item.getCode();
							if ("50".equals(code) || "51".equals(code)) {
								isSuspended = true;
								break;
							}
						}
					}
				}
				// 已暂停
				bom.setSuspended(isSuspended);
				if (isSuspended) {
					if (suspendItemList != null && !suspendItemList.isEmpty()) {
						for (CodeTableItemDTO item : suspendItemList) {
							suspendedReason.append(item.getCode()).append(":")
									.append(item.getDescription()).append(";");
						}
					}
					// 暂停原因
					bom.setSuspendedReason(suspendedReason.toString());
				}
			} else {
				throw new RuntimeException("调用接口查询暂停出错：flag:"
						+ retMap.get("p_sign") + ", msg:"
						+ retMap.get("p_message"));
			}

			ServiceItemsDAO serviceItemsDAO = getServiceItemsDAOByServiceItems(serviceItems);
			serviceItemsDAO.fillBomPostCreated(bom);

			return bom;
		} finally {
			ServiceItemsExists.destroy();
		}
	}

	/**
	 * 创建用于处理规则检查的BOM
	 * 
	 * @param posNo
	 * @return
	 */
	public POSBOMPolicyDto createBOMForProcessRuleCheck(String posNo) {
		PosInfoDTO posInfo = commonQueryDAO.queryPosInfoRecord(posNo);
		Map<String, Object> posExtraInfo = posRulesDAO
				.queryPosExtraInfoByPosNo(posNo);
		Date applyDate = (Date) posExtraInfo.get("APPLY_DATE");
		Date idValidyDate = (Date) posExtraInfo.get("IDNO_VALIDITY_DATE");
		
		String policyNo = posInfo.getPolicyNo();
		Date acceptDate = posInfo.getAcceptDate();
		String acceptor = posInfo.getAcceptor();
		String serviceItems = posInfo.getServiceItems();
		String clientNo = posInfo.getClientNo();
		String paymentType = posInfo.getPaymentType();

		ServiceItemsExists.init(serviceItems);
		try {
			POSBOMPolicyDto bom = createBOMInternal(policyNo, clientNo,
					serviceItems, applyDate, acceptDate, acceptor, "2", posNo,
					paymentType);
			// 保全号
			bom.setPosNo(posNo);
			bom.setAcceptSource((String) posExtraInfo
					.get("ACCEPT_CHANNEL_CODE"));

			// 申请类型
			bom.setApplyTypeCode((String) posExtraInfo.get("APPLY_TYPE_CODE"));

			if (ServiceItemsExists.in("1,2")) {
				// 当前缴费银行账户号码和原账户号码相同
				boolean currentOriginalAccountSame = posRulesDAO
						.currentOriginalAccountSame(posNo);
				bom.setCurrentOriginalAccountSame(currentOriginalAccountSame);
			}

			// 本次申请的保全项目
			ServiceItemDomainDto[] currentServiceItemsList = posRulesDAO
					.currentServiceItemsList(posNo);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			for (POSUWBOMPlanDto plan : bom.getPlansList()) {
				plan.setCurrentServiceItemsList(currentServiceItemsList);
				resultMap = commonQueryDAO.getProductPremCoverage(posNo,
						plan.getProductCode(), policyNo,
						new Integer(plan.getProdSeq()).toString(), null);
				plan.setPremMaturityDate((Date) resultMap.get("paidEndDate"));
			}

			// 历史保全项目列表
			POSBOMServiceItemDto[] historyServiceItemsList = posRulesDAO
					.historyServiceItemsList(policyNo, acceptDate);
			bom.setHistoryServiceItemsList(historyServiceItemsList);

			// 是否暂停及暂停原因
			Map<String, Object> retMap = posRulesDAO.judgePosSuspend(posNo);
			String suspendFlag = (String) retMap.get("p_suspend_flag");
			String suspendCause = (String) retMap.get("p_suspend_cause");
			bom.setSuspended("Y".equals(suspendFlag));
			if (bom.isSuspended()) {
				// TODO: 替换暂停原因代码
				bom.setSuspendedReason(suspendCause);
			}

			// 已录入健康告知
			bom.setHasHealthNote(posRulesDAO.isHasHealthNote(posNo));

			// 退费银行账户户主姓名
			bom.setInAccountant(posInfo.getPremName());
			ServiceItemsDAO serviceItemsDAO = getServiceItemsDAOByServiceItems(serviceItems);
			serviceItemsDAO.fillBOMForProcessRuleCheck(bom);
			serviceItemsDAO.fillBomPostCreated(bom);

			// 是否免填单标志 edit by gaojiaming start
			String isApplicationFree = branchQueryService
					.checkIsApplicationFree(posNo);
			if ("Y".equals(isApplicationFree)) {
				bom.setApplicationFree(true);
			} else {
				bom.setApplicationFree(false);
			}
			// 是否免填单标志 edit by gaojiaming end
			// 变更的电话号码是否与业务员一致
			bom.setPhoneNoTheAgent(posRulesDAO.isPhoneNoTheAgent(posNo));
			// 变更的电话号码是否3个及以上客户共用
			bom.setPhoneNoForThreeClient(posRulesDAO
					.isPhoneNoForThreeClient(posNo));
			// 是否原交费账户
			bom.setFirstAccountNo(posRulesDAO.isFirstAccountNo(posNo));
			// 销售网点是否有与公司签订协议
			bom.setDepartmentHasProtocol(posRulesDAO
					.isDepartmentHasProtocol(posNo));
			// 保单当前收付交易方式
			bom.setOncePaymentType(paymentType);
			// 代审类别
			bom.setExamType(branchQueryService.getExamType(posNo));
			// 检查银行前置保全相关规则
			if ("12".equals(bom.getAcceptSource())) {
				bom.setResultBankPos(posRulesDAO.checkBankPosRules(posNo));
			}
			// 设置未清偿贷款次数
			bom.setNumberNonRefundLoan(posRulesDAO.getUnPaidLoanTimes(policyNo));
			// 设置申请人证件有效期
			bom.setIdValidDate(idValidyDate);
			return bom;
		} finally {
			ServiceItemsExists.destroy();

		}
	}

	private ServiceItemsDAO getServiceItemsDAOByServiceItems(String serviceItems) {
		return (ServiceItemsDAO) PlatformContext.getApplicationContext()
				.getBean("serviceItemsDAO_" + serviceItems);
	}

	@SuppressWarnings("unchecked")
	private POSBOMPolicyDto createBOMInternal(String policyNo, String clientNo,
			String serviceItems, Date applyDate, Date acceptDate,
			String acceptor, String appliedRuleType, String posNo,
			String paymentType) {
		POSBOMPolicyDto bom = POSBOMPolicyDto.create();

		// 是否支持自垫
		String primaryProductCode = commonQueryDAO
				.queryProductCodeOfPrimaryPlanByPolicyNo(policyNo);// 主险代码
		boolean isAplable = "1".equals(commonQueryDAO
				.pdSelPrdParameterValuePro(primaryProductCode, "502"));
		bom.setAplable(isAplable);

		// 保单号
		bom.setPolicyNo(policyNo);

		// 申请日期
		bom.setApplyDate(applyDate);

		// 保全项目
		ServiceItemDomainDto sidd = new ServiceItemDomainDto();
		sidd.setServiceItem(serviceItems);
		bom.setServiceItems(sidd);

		// 受理日期
		bom.setAcceptDate(acceptDate);

		// 申请日期前调天数
		int forwardAcceptDays = posRulesDAO.forwardAcceptDays(acceptDate,
				applyDate);
		bom.setForwardAcceptDays(forwardAcceptDays);

		// 保险合同周年日
		Date policyYearDate = posRulesDAO.policyYearDate(policyNo);
		bom.setPolicyYearDate(policyYearDate);

		// 受理人员可操作的保全项目列表
		String[] serviceItemsArr = posRulesDAO
				.acceptorAllowedServiceItems(acceptor);
		if (serviceItemsArr != null && serviceItemsArr.length > 0) {
			ServiceItemDomainDto[] serviceItemDomains = new ServiceItemDomainDto[serviceItemsArr.length];
			for (int i = 0; i < serviceItemsArr.length; i++) {
				serviceItemDomains[i] = new ServiceItemDomainDto();
				serviceItemDomains[i].setServiceItem(serviceItemsArr[i]);
			}
			bom.setAcceptorAllowedServiceItems(serviceItemDomains);
		}

		// 是否有异地受理限制
		boolean isOffSideLimit = posRulesDAO.isOffSideLimit(policyNo, acceptor);
		bom.setOffSideLimit(isOffSideLimit);

		PolicyDTO policyDTO = commonQueryDAO
				.queryPolicyInfoByPolicyNo(policyNo);
		PolicyPremInfoDTO policyPrem = policyDTO.getPremInfo();

		// 机构代码
		bom.setBranchCode(policyDTO.getBranchCode());

		// 销售渠道
		bom.setBussinessSource(policyDTO.getBusinessSource());

		// 业务渠道
		bom.setChannelType(policyDTO.getChannelType());

		// 状态
		bom.setPolicyDutyStatus(policyDTO.getDutyStatus());

		// 生效日期
		bom.setEffectedDate(policyDTO.getEffectDate());

		// 查询失效日期与失效时间
		Map<String, Object> retMap = posRulesDAO
				.queryLapseDateAndReasonByPolicyNo(policyNo);
		if (retMap != null) {
			String lapseType = (String) retMap.get("LAPSE_TYPE");
			Date lapseDate = (Date) retMap.get("LAPSE_DATE");
			// 失效类型
			bom.setLapseReason(lapseType);
			// 失效日期
			bom.setLapseDate(lapseDate);
			if (POLICY_OVER_LAPSE_REASON_SET.contains(lapseType)) {
				// 终止日期(失效原因对应的是终止，终止日期)
				bom.setPolicyOverDate(lapseDate);
			}
		}

		// 查询保单挂失状态
		retMap = commonQueryDAO.policyLossStatus(policyNo);
		if (retMap != null) {
			String lossFlag = (String) retMap.get("lossFlag");
			Date lossDate = (Date) retMap.get("lossDate");
			// 已挂失
			bom.setLossReported("Y".equals(lossFlag));
			// 挂失日期
			bom.setLossReportedDate(lossDate);
		}

		// 查询保单是否处于挂失期
		retMap = commonQueryDAO.policyLossPeriodStatus(policyNo,
				commonQueryDAO.getSystemDate());
		if (retMap != null) {
			String lossPeriodFlag = (String) retMap.get("loss_period_flag");

			// 挂失期
			bom.setLossPeriod("Y".equals(lossPeriodFlag));

		}

		if (ServiceItemsExists.in("5,8,19")) {
			// 在宽限期内
			boolean isExtendedTerm = posRulesDAO.isExtendedTerm(policyNo,
					applyDate);
			bom.setExtendedTerm(isExtendedTerm);
		}

		// 是否有犹豫期及犹豫结束时间
		retMap = posRulesDAO.scruplePeriodPolicy(policyNo);
		if (retMap != null) {
			// 是否存在犹豫期
			bom.setHasHesitate("Y".equals((String) retMap.get("p_is_scruple")));

			if (bom.isHasHesitate()) {
				Date policySignBackDate = (Date) retMap.get("p_scruple_date");
				int receiptSignDate = DateUtils.getYear(policySignBackDate);
				// 保单回执客户是否签收
				bom.setReceiptSigned(receiptSignDate != 9999);
			}
		}

		if (ServiceItemsExists.in("12")) {
			// 是自保件
			boolean isSelfInsured = posRulesDAO.isSelfInsured(policyNo);
			bom.setSelfInsured(isSelfInsured);
		}

		if (ServiceItemsExists.in("8")) {
			// 选择自垫
			boolean isAutoPolicyLoan = posRulesDAO.isAutoPolicyLoan(policyNo);
			bom.setAutoPolicyLoan(isAutoPolicyLoan);
		}

		// 有未清偿自垫
		boolean hasNonRefundAPL = posRulesDAO.hasNonRefundAPL(policyNo);
		bom.setHasNonRefundAPL(hasNonRefundAPL);

		// 有未清偿贷款
		boolean hasNonRefundLoan = posRulesDAO.hasNonRefundLoan(policyNo);
		bom.setHasNonRefundLoan(hasNonRefundLoan);

		// 保单未清偿贷款中存在一个是PAS
		boolean hasNonRefundLoanPas = posRulesDAO.hasNonRefundLoanPas(policyNo);
		bom.setHasNonRefundLoanPas(hasNonRefundLoanPas);

		// //判断保单新生效日是否有效
		// boolean effectIsCorrect=posRulesDAO.effectIsCorrect(posNo,policyNo);
		// bom.setEffectDateIsCorrect(effectIsCorrect) ;

		// 判断保单新生效日是否有效
		boolean effectIsCorrect = posRulesDAO.effectIsCorrect(posNo, policyNo);
		bom.setEffectDateIsCorrect(effectIsCorrect);

		// 判断卡单是否激活
		boolean activaTionCard = posRulesDAO.activaTionCard(policyNo);

		bom.setActivaTionCard(activaTionCard);

		// 续期转账在途
		boolean isRenewTransfering = posRulesDAO.isRenewTransfering(policyNo);
		bom.setRenewTransfering(isRenewTransfering);

		// 处于保费豁免状态
		boolean isPolicyFeeFree = posRulesDAO.isPolicyFeeFree(policyNo);
		bom.setPolicyFeeFree(isPolicyFeeFree);

		// 处于已预约状态
		boolean reServation = posRulesDAO.isReservation(policyNo);
		bom.setReservation(reServation);

		// 客户体检次数
		int examNum = posRulesDAO.examinaTionNum(policyNo);
		bom.setExaminaTionNum(examNum);

		// 是否是淘宝渠道保单
		boolean isTaobaoPolicy = posRulesDAO.isTaobaoPolicy(policyNo);

		bom.setTaobaoPolicy(isTaobaoPolicy);

		// 电商渠道保单的来源
		bom.setePolicySource(posRulesDAO.getEPolicySource(policyNo));

		if (policyPrem != null) {
			// 暂收余额
			if (policyPrem.getPolicyBalance() != null) {
				bom.setPolicyBalance(policyPrem.getPolicyBalance()
						.doubleValue());
			}
			// 保单缴费状态
			bom.setPolicyPremStatus(policyPrem.getPremStatus());
		}

		if (ServiceItemsExists.in("37")) {
			// 已提出个人账户价值分期领取的申请
			boolean hasApplyTermWithdraw = posRulesDAO.hasApplyTermWithdraw(
					policyNo, applyDate);
			bom.setHasApplyTermWithdraw(hasApplyTermWithdraw);
		}

		if (ServiceItemsExists.in("37,39,2,35")) {
			// 投资账户存在未成交的有效交易记录
			boolean hasFundInTrade = posRulesDAO.hasFundInTrade(policyNo,
					applyDate);
			bom.setHasFundInTrade(hasFundInTrade);
		}

		if (ServiceItemsExists.in("30")) {
			// 系统回执回销日
			Date sysPolicySignBackDate = posRulesDAO
					.sysPolicySignBackDate(policyNo);
			bom.setSysPolicySignBackDate(sysPolicySignBackDate);

			// 投保人回执签署日
			Date applicantPolicySignBackDate = posRulesDAO
					.applicantPolicySignBackDate(policyNo);
			bom.setApplicantPolicySignBackDate(applicantPolicySignBackDate);
		}

		// 系统日期
		Date systemDate = commonQueryDAO.getSystemDate();
		bom.setSystemDate(systemDate);

		// 非转账失败件(保留暂无用)
		// boolean isNotTransferFailure =
		// posRulesDAO.isNotTransferFailure(policyNo);
		// bom.setNotTransferFailure(isNotTransferFailure);

		if (ServiceItemsExists.in("12")) {
			// 服务人员电话
			String servicerPhone = posRulesDAO.servicerPhone(policyNo);
			if (servicerPhone != null) {
				servicerPhone = servicerPhone.replaceAll("\\D", "");// 去掉非数字
			}
			bom.setServicerPhone(servicerPhone);
		}

		// 保单当前现金价值(保留，暂无用)
		// retMap = commonQueryDAO.calcPolicyCashValue(policyNo, applyDate);
		// if("0".equals(retMap.get("p_flag"))) {
		// BigDecimal cashValue = (BigDecimal)
		// retMap.get("p_policy_cash_value");
		// if(cashValue != null)
		// bom.setPolicyCashValue(cashValue.doubleValue());
		// }

		if (ServiceItemsExists.in("1")) {
			// 首期保费财务是否未确认
			String applyBarcode = posRulesDAO.getApplyBarcode(policyDTO
					.getApplyNo());
			boolean financeNoConfirmPrem = posRulesDAO.financeNoConfirmPrem(
					applyBarcode, "1");
			bom.setFirstFinanceNoConfirmPrem(financeNoConfirmPrem);
		}

		if (ServiceItemsExists.in("2,4,10,17,41")) {
			// 保全退费进行资金确认校验保全项目增退保、减保、续期保费退费、生存保险金领取保全项目
			boolean financeConfirmed = posRulesDAO.finCapitalConfirm(
					policyDTO.getPolicyNo(), null);
			bom.setFinanceConfrimed(financeConfirmed);
		}

		if (ServiceItemsExists.in("1,2")) {
			// 关联保单号
			retMap = posRulesDAO.getRelatePolicyInfo(policyNo);
			if ("Y".equals(retMap.get("hasRelatedPolicy"))) {
				bom.setRelatedPolicyNo((String) retMap.get("relatedPolicyNo"));
			}
			// 关联保单是否该退的都退了
			boolean hasRelatedShortTermPlanCancel = "Y".equals(retMap
					.get("relatedShortTermCanceled"));
			bom.setHasRelatedShortTermPlanCancel(hasRelatedShortTermPlanCancel);
		}

		// 核保结果是次标准体
		Integer prodSeqOfPrimaryPlan = commonQueryDAO
				.queryProdSeqOfPrimaryPlanByPolicyNo(policyNo);
		retMap = posRulesDAO.uwGetLastDecisionDetail(policyNo,
				prodSeqOfPrimaryPlan);
		if (retMap != null && "1".equals(retMap.get("p_flag"))) {
			bom.setUnderWriteNonStandard("Y".equals(retMap.get("p_cbzt_flag")));
		}

		// 退费金额（已经放到审批规则中，这里置0）
		bom.setOutMoney(0D);

		// 系统记录的补发次数
		int sysReIssueTimes = commonQueryDAO
				.getPolicyProvideTimeByPolicyNo(policyNo);
		bom.setSysReIssueTimes(sysReIssueTimes);

		// 录入的保单补发次数，目前在页面校验这里直接写系统补发次数
		bom.setInReIssueTimes(sysReIssueTimes);

		// 投保人信息
		String applicantNo = commonQueryDAO.getApplicantByPolicyNo(policyNo);
		if (StringUtils.isNotBlank(applicantNo)) {
			ClientInformationDTO applicantInfo = clientInfoDAO
					.selClientinfoForClientno(applicantNo).get(0);
			POSBOMOwnerDto owner = POSBOMOwnerDto.create();
			if (applicantInfo != null) {
				// ID
				owner.setId(applicantInfo.getClientNo());
				// 姓名
				owner.setName(applicantInfo.getClientName());
				// 性别
				owner.setSex(applicantInfo.getSexCode());
				// 出生日期
				owner.setBirthDate(applicantInfo.getBirthday());
				// 证件类型
				owner.setIdType(applicantInfo.getIdTypeCode());
				// 证件号码
				
				owner.setIdNo(applicantInfo.getIdNo());
				// 已死亡
				owner.setDeath(applicantInfo.getDeathDate() != null);
				// 年龄（周岁）
				owner.setAgeInYear(PosUtils.calcAgeYearsFromBirthday(
						applicantInfo.getBirthday(), applyDate));
				// 年龄（月数）
				owner.setAgeInMonth(PosUtils.calcAgeMonthsFromBirthday(
						applicantInfo.getBirthday(), applyDate));
				// 投保人是否在黑名单中
				owner.setApplicantInBlacklist(posRulesDAO
						.isApplicantInBlacklist(applicantInfo.getClientName(),applicantInfo.getIdNo()));
			}
			// 是当前保全项目变更的客户对象
			owner.setCurrentPosObj(applicantNo.equals(clientNo));
			bom.setApplicant(owner);
		}

		// 被保人信息
		List<String> insuredNoList = commonQueryDAO
				.getInsuredByPolicyNo(policyNo);
		String primaryInsuredNo = commonQueryDAO
				.getInsuredOfPrimaryPlanByPolicyNo(policyNo);
		if (insuredNoList != null && !insuredNoList.isEmpty()) {
			POSBOMInsuredDto[] insuredArr = new POSBOMInsuredDto[insuredNoList
					.size()];
			for (int i = 0; i < insuredNoList.size(); i++) {
				String insuredNo = insuredNoList.get(i);
				ClientInformationDTO insuredInfo = clientInfoDAO
						.selClientinfoForClientno(insuredNo).get(0);
				POSBOMInsuredDto insured = POSBOMInsuredDto.create();
				if (insuredInfo != null) {
					// ID
					insured.setId(insuredInfo.getClientNo());
					// 姓名
					insured.setName(insuredInfo.getClientName());
					// 性别
					insured.setSex(insuredInfo.getSexCode());
					// 出生日期
					insured.setBirthDate(insuredInfo.getBirthday());
					// 证件类型
					insured.setIdType(insuredInfo.getIdTypeCode());
					// 证件号码
					insured.setIdNo(insuredInfo.getIdNo());
					// 已死亡
					insured.setDeath(insuredInfo.getDeathDate() != null);
					// 年龄（周岁）
					insured.setAgeInYear(PosUtils.calcAgeYearsFromBirthday(
							insuredInfo.getBirthday(), applyDate));
					// 年龄（月数）
					insured.setAgeInMonth(PosUtils.calcAgeMonthsFromBirthday(
							insuredInfo.getBirthday(), applyDate));
					// 被保人是否在黑名单中
					insured.setInsuredInBlacklist(posRulesDAO
							.isInsuredInBlacklist(insuredInfo.getClientName(),insuredInfo.getIdNo()));
				}
				// 是当前保全项目变更的客户对象
				insured.setCurrentPosObj(insuredNo.equals(clientNo));
				if (ServiceItemsExists.in("10")) {
					// 生存调查未结束
					boolean isInLifeInvestigation = posRulesDAO
							.isInLifeInvestigation(policyNo);
					insured.setInLifeInvestigation(isInLifeInvestigation);
				}
				// 变更前的累计身故保额(该属性涉及的规则目前忽略，强制设定为150000)
				insured.setCurrentDeathInsSum(150000D);
				// 变更后的累计身故保额(该属性涉及的规则目前忽略，强制设定为10000)
				insured.setPosedDeathInsSum(10000D);
				// 是主被保险人
				insured.setPrimaryInsured(primaryInsuredNo.equals(insuredNo));
				insuredArr[i] = insured;
			}
			bom.setInsurantList(insuredArr);
		}

		if (ServiceItemsExists.in("10,22,34")) {
			// 受益人信息
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("policyNo", policyNo);
			paraMap.put("benefitStatus", "Y");
			List<PolicyBeneficiaryDTO> benefList = commonQueryDAO
					.queryBeneficiaryByCriteria(paraMap);
			if (benefList != null && !benefList.isEmpty()) {
				POSBOMBeneficiaryDto[] benefArr = new POSBOMBeneficiaryDto[benefList
						.size()];
				for (int i = 0; i < benefList.size(); i++) {
					PolicyBeneficiaryDTO pb = benefList.get(i);
					POSBOMBeneficiaryDto benef = POSBOMBeneficiaryDto.create();
					// ID
					benef.setId(pb.getBeneficiaryNo());
					// 姓名
					benef.setName(pb.getBenefInfo().getBeneficiaryName());
					// 性别
					benef.setSex(pb.getBenefInfo().getSexCode());
					// 出生日期
					benef.setBirthDate(pb.getBenefInfo().getBirthdate());
					// 证件类型
					benef.setIdType(pb.getBenefInfo().getIdType());
					// 证件号码
					benef.setIdNo(pb.getBenefInfo().getIdno());
					// 年龄（周岁）
					benef.setAgeInYear(PosUtils.calcAgeYearsFromBirthday(pb
							.getBenefInfo().getBirthdate(), applyDate));
					// 年龄（月数）
					benef.setAgeInMonth(PosUtils.calcAgeMonthsFromBirthday(pb
							.getBenefInfo().getBirthdate(), applyDate));
					if (ServiceItemsExists.in("22")) {
						// 与被保险人关系
						benef.setRelationship(pb.getRelationship());
						// 受益人客户号
						benef.setBenefciaryClientNo(pb.getBenefInfo()
								.getClientNo());
					}
					// 类型
					benef.setBenefitType(pb.getBenefitType());
					// 受益次序
					benef.setBenefitSeq(String.valueOf(pb.getBenefitSeq()));
					// 受益比例
					benef.setBenefitRate(String.valueOf(pb
							.getBenefitRatePercent()));
					// 受益状态
					benef.setBenefitStatus(pb.getBenefitStatus());
					// 已授权转帐
					benef.setHasAuthoriyAccount("Y".equals(pb
							.getTransferGrantFlag()));
					benefArr[i] = benef;
					// 受益人是否在黑名单中
					benef.setBenefciaryInBlacklist(posRulesDAO
							.isBenefciaryInBlacklist(pb.getBenefInfo()
									.getBeneficiaryName(),pb.getBenefInfo().getIdno()));
				}
				bom.setBenefitantList(benefArr);
			}
		}

		// 查理财产品信息
		retMap = commonQueryDAO.getFinancialPolicyProductByPolicyNo(policyNo);
		String flag = (String) retMap.get("p_flag");
		String ulinkProdSeq = null;
		if ("0".equals(flag)) {
			ulinkProdSeq = (String) retMap.get("p_prod_seq"); // 理财产品的险种序号
			String financialType = (String) retMap.get("p_financial_type"); // 理财产品类型
																			// 1
																			// 投连
																			// 2
																			// 万能
			if ("1".equals(financialType)) {
				if (ServiceItemsExists.in("35,36,37,38,39")) {
					// 查询资金账户信息
					List<FinancialProductsDTO> financialProductsList = commonQueryDAO
							.getFinancialProductsList(policyNo, ulinkProdSeq);
					if (financialProductsList != null
							&& !financialProductsList.isEmpty()) {
						POSBOMFundDto[] fundArr = new POSBOMFundDto[financialProductsList
								.size()];
						for (int i = 0; i < financialProductsList.size(); i++) {
							FinancialProductsDTO fp = financialProductsList
									.get(i);
							POSBOMFundDto fund = POSBOMFundDto.create();
							// 基本帐户比例
							fund.setBaseFundRate(fp.getRate().doubleValue());
							fund.setBaseAccountUnits(fp.getUnits()
									.doubleValue());
							// 缺省true，保留，暂无用
							fund.setValidFlag(true);
							fund.setFinancialProducts(fp.getFinancialProducts());
							fundArr[i] = fund;
						}
						bom.setFundList(fundArr);
					}
				}
			}
			if ("1".equals(financialType) || "2".equals(financialType)) {
				if (ServiceItemsExists.in("37,24")) {
					// 查询个人账户价值
					retMap = commonQueryDAO.queryFinancialProductsValue(
							policyNo, ulinkProdSeq, applyDate);
					flag = (String) retMap.get("p_flag");
					if ("0".equals(flag)) {
						BigDecimal personalAccountValue = (BigDecimal) retMap
								.get("p_total_amount");
						// 个人账户价值
						if (personalAccountValue != null) {
							bom.setPersonalAccountValue(personalAccountValue
									.doubleValue());
						}
					}
				}
			} else {
				ulinkProdSeq = null;
			}
		}

		// 险种信息
		List<PolicyProductDTO> productList = commonQueryDAO
				.queryPolicyProductListByPolicyNo(policyNo);
		double maturedPaymentMoney = 0;
		double loanableMoney = 0;
		if (productList != null && !productList.isEmpty()) {
			POSUWBOMPlanDto[] planArr = new POSUWBOMPlanDto[productList.size()];
			for (int i = 0; i < productList.size(); i++) {
				PolicyProductDTO product = productList.get(i);
				String productCode = product.getProductCode();
				PolicyProductPremDTO premInfo = product.getPremInfo();
				Integer prodSeq = product.getProdSeq();
				POSUWBOMPlanDto plan = POSUWBOMPlanDto.create();
				// 险种代码
				plan.setProductCode(productCode);
				// 险种是否可追加保费
				String pdParaValue = commonQueryDAO.pdSelPrdParameterValuePro(productCode, "599");
				if("1".equals(pdParaValue) || "3".equals(pdParaValue)) {
					plan.setCanAdditionalPremium(true);
				}
				// 险种是否可部分领取
				pdParaValue = commonQueryDAO.pdSelPrdParameterValuePro(productCode, "110");
				if("1".equals(pdParaValue)) {
					plan.setCanAccountSection(true);
				}
				// 险种序号
				plan.setProdSeq(product.getProdSeq());
				// 简称
				plan.setAbbrName(product.getProductAbbrName());
				// 是主险
				plan.setPrimaryPlan("Y".equals(product.getIsPrimaryPlan()));
				if (plan.isPrimaryPlan() && bom.getApplicant() != null) {
					// 投保人与被保人的关系
					bom.getApplicant().setInsuredRelationship(
							product.getRelationship());

					// 投保人变更和补充告知可能会变化这个值
					if (ServiceItemsExists.in("16,20")) {
						String newRelation = posRulesDAO.latestRelationAppIns(
								posNo, serviceItems);
						if (StringUtils.isNotBlank(newRelation)) {
							bom.getApplicant().setInsuredRelationship(
									newRelation);
						}
					}
				}
				// 保费来源
				plan.setPremSource(premInfo.getPremSource());
				// 缴费状态
				plan.setPremStatus(premInfo.getPremStatus());
				// 责任状态
				plan.setDutyStatus(product.getDutyStatus());
				// 失效原因
				plan.setLapseReason(product.getLapseReason());
				// 被保险人ID
				plan.setInsurantNo(product.getInsuredNo());
				// 生效日期
				plan.setEffectedDate(product.getEffectDate());
				// 交至日
				plan.setPayToDay(premInfo.getPayToDate());
				// 是分红险种
				plan.setDivPlan(posRulesDAO.isDivPlan(productCode));
				// 是现金分红险种
				plan.setBonusDivPlan(posRulesDAO.isBonusDivPlan(productCode));
				// 是养老金险种
				plan.setEndowmentPlan(posRulesDAO.isEndowmentPlan(productCode));
				// 是投连险
				plan.setULink(posRulesDAO.isULink(productCode));
				// 是万能险
				plan.setUniversal(posRulesDAO.isUniversal(productCode));
				// 是长期险
				plan.setLongTerm(posRulesDAO.isLongTerm(productCode));
				// 是短期险
				plan.setShortTerm(posRulesDAO.isShortTerm(productCode));
				// 是豁免险
				plan.setExempt(posRulesDAO.isExemptPlan(productCode));
				// 是可贷险种
				plan.setLoanable(posRulesDAO.isLoanablePlan(productCode));
				// 交费频次
				plan.setFrequency(premInfo.getFrequency());
				// 基本保费
				plan.setBasicPolicyFee(product.getBaseSumIns().doubleValue());
				// 原期交保费
				plan.setOriginalPeriodTermSum(premInfo.getPeriodPremSum()
						.doubleValue());
				// 每期保费均已缴纳（包含当期）
				plan.setHasPaidDuePolicyFee(posRulesDAO.hasPaidDuePolicyFee(
						policyNo, applyDate));
				// 红利选择方式
				plan.setBonusOption(posRulesDAO.bonusOption(policyNo));
				// 年金领取开始领取日
				plan.setAnnualPaymentStartDate(posRulesDAO
						.annualPaymentStartDate(policyNo, prodSeq));
				// 已生效的年数
				plan.setEffectedYears(posRulesDAO.effectedYears(policyNo,
						prodSeq, applyDate));
				String calcType = commonQueryDAO
						.getProductPremCalTypeByProductCode(productCode);
				// 是否有医疗责任
				plan.setHasMedicalLiability(posRulesDAO.hasMedicalLiability(
						policyNo, prodSeq.toString()));
				// 是否发生过理赔
				plan.setProdClaimed(posRulesDAO.isPolicyProdClaimed(policyNo,
						prodSeq.toString()));

				// 按份数卖
				plan.setSaleInUnits("2".equals(calcType)
						|| "3".equals(calcType));
				// 按保额卖
				plan.setSaleInInsLimit("1".equals(calcType));

				if (ServiceItemsExists.in("35")) {
					// 最小追加保费限额
					double minAddupPolicyFee = posRulesDAO.minAddupPolicyFee(
							policyNo, String.valueOf(prodSeq), applyDate);
					plan.setMinAddupPolicyFee(minAddupPolicyFee);
					// 最大追加保费限额
					retMap = posRulesDAO.maxAddupPolicyFee(policyNo, prodSeq,
							applyDate);
					if (!"0".equals(retMap.get("p_flag"))) {
						throw new RuntimeException("查询最大追加保费限额出错："
								+ retMap.get("p_message"));
					}
					double maxAddupPolicyFee = ((BigDecimal) retMap
							.get("p_addto_max_amount")).doubleValue();
					plan.setMaxAddupPolicyFee(maxAddupPolicyFee);

				}

				if (ServiceItemsExists.in("37")) {
					// 账户价值领取-账户最低余额
					double minAccountBalance = posRulesDAO
							.minAccountBalance(productCode);
					plan.setMinAccountBalance(minAccountBalance);
				}

				if (ServiceItemsExists.in("39")) {
					if (StringUtils.isNotBlank(ulinkProdSeq)) {
						// 投连账户之间存在冲突
						plan.setAccountNoConflict(posRulesDAO
								.isAccountNoConflict(policyNo,
										String.valueOf(prodSeq), productCode,
										ulinkProdSeq));
					}
				}

				// 险种的isboundPlan属性先缺省设为false，目前业务在收集，可能以后要在规则引擎中直接配置
				plan.setBoundPlan(false);

				retMap = posRulesDAO.getPolProdPremCoverage(posNo, productCode,
						policyNo, prodSeq, applyDate);
				if (retMap != null) {
					BigDecimal coveragePeriod = (BigDecimal) retMap
							.get("p_coverage_period");
					if (coveragePeriod != null) {
						// 保险年期
						plan.setCoveragePeriod(coveragePeriod.intValue());
					}
					BigDecimal leftCoveragePeriod = (BigDecimal) retMap
							.get("p_left_coverage_period");
					if (leftCoveragePeriod != null) {
						// 剩余的保险年期
						plan.setLeftCoveragePeriod(leftCoveragePeriod
								.intValue());
					}
					BigDecimal premTerm = (BigDecimal) retMap
							.get("p_prem_term");
					if (premTerm != null) {
						// 缴费年期
						plan.setPremTerm(premTerm.intValue());
					}
					BigDecimal leftPremTerm = (BigDecimal) retMap
							.get("p_left_prem_term");
					if (leftPremTerm != null) {
						// 剩余缴费年期
						plan.setLeftPremTerm(leftPremTerm.intValue());
					}
				}

				// 是否在售
				retMap = commonQueryDAO.checkProductIsOnSale(productCode,
						policyDTO.getChannelType(), policyDTO.getBranchCode(),
						premInfo.getPremPeriodType(), premInfo.getPremTerm(),
						product.getCoverPeriodType(),
						new BigDecimal(product.getCoveragePeriod()), applyDate);
				if ("Y".equals(retMap.get("p_sign"))) {
					plan.setInSale("Y".equals(retMap.get("p_sale_flag")));
				}

				if (ServiceItemsExists.in("34,10")) {
					// 满期金
					double maturedMoney = posRulesDAO.getMaturityPay(policyNo,
							prodSeq, applyDate);
					maturedPaymentMoney += maturedMoney;
					plan.setMaturedMoney(maturedMoney);
				}

				// 满期日期
				plan.setMaturityDate(product.getMaturityDate());

				if (ServiceItemsExists.in("8")) {
					// 查最大可借金额
					String specialFunc = null;
					List<String> lsspecialFunc = commonQueryDAO
							.queryPosAcceptDetailInfo(posNo, "5", "003");
					if (lsspecialFunc != null && lsspecialFunc.size() > 0) {
						specialFunc = lsspecialFunc.get(0);
					}

					Map<String, Object> resultMap = null;
					boolean isSurvivalDutyProduct = false;
					Date nextAnniversary = null;
					BigDecimal nextAnniversaryLoanMax = new BigDecimal(0);
					BigDecimal curLoanMax = new BigDecimal(0);
					resultMap = commonQueryDAO
							.isSurvivalDutyProduct(productCode);
					if ("0".equals(resultMap.get("p_flag")))// 含有生存金产品

					{
						isSurvivalDutyProduct = true;
						resultMap = commonQueryDAO.calcProductAnniversary(
								policyNo, prodSeq, applyDate);

						nextAnniversary = (Date) resultMap
								.get("p_next_anniversary");
						// 保单年度末
						Calendar lastDay = GregorianCalendar.getInstance();
						lastDay.setTime(nextAnniversary);
						lastDay.add(Calendar.DATE, -1);
						// 查询最大可贷金额及现金价值

						resultMap = commonQueryDAO
								.calcLoanMaxAndCashValueByProduct(policyNo,
										productCode, prodSeq,
										lastDay.getTime(), specialFunc);
						if (!"0".equals(resultMap.get("p_flag"))) {
							throw new RuntimeException("查询最大可贷金额及现金价值出错:"
									+ resultMap.get("p_message"));
						}

						nextAnniversaryLoanMax = (BigDecimal) resultMap
								.get("p_loan_max");
					}
					retMap = commonQueryDAO.calcLoanMaxAndCashValueByProduct(
							policyNo, productCode, prodSeq, applyDate,
							specialFunc);
					if (retMap.get("p_loan_max") != null) {

						curLoanMax = (BigDecimal) retMap.get("p_loan_max");
					}
					if (isSurvivalDutyProduct) {

						if (nextAnniversaryLoanMax.doubleValue() > curLoanMax
								.doubleValue()) {

							loanableMoney += curLoanMax == null ? 0
									: curLoanMax.doubleValue();
						} else {
							loanableMoney += nextAnniversaryLoanMax == null ? 0
									: nextAnniversaryLoanMax.doubleValue();

						}
					} else {
						loanableMoney += retMap.get("p_loan_max") == null ? 0
								: ((BigDecimal) retMap.get("p_loan_max"))
										.doubleValue();
					}
				}

				// 对于保单层变更以主险为险种层变更对象的的保全项目，设置其主险的变更对象标识为true
				if (plan.isPrimaryPlan()) {
					String configStr = ServiceItemsDAO.STATUS_RESTRICT_CONFIG_MAP
							.get(serviceItems);
					if (StringUtils.isNotBlank(configStr)) {
						String[] configArr = configStr.toUpperCase().split(",");
						if (configArr.length == 7) {
							String altPrimaryProduct = configArr[6];// 保单层变更以主险为险种层变更对象
							if ("Y".equals(altPrimaryProduct)) {
								plan.setCurrentPosPlan(true);
							}
						}
					}
				}

				// 保险合同年度内申请加保次数
				plan.setPolicyYearAddupTimes(posRulesDAO.policyYearAddupTimes(
						policyNo, prodSeq, applyDate));
				// 保险合同年度内申请减保次数
				plan.setPolicyYearDeductTimes(posRulesDAO
						.policyYearDeductTimes(policyNo, prodSeq, applyDate));
				// 保险合同年度内申请追加保费次数
				plan.setFeePolicyYearAddupTimes(posRulesDAO
						.getYearlyServiceItemTimes(policyNo, prodSeq, "35",
								applyDate));
				// 保险合同年度内申请部分领取次数
				plan.setWithDrawTimesInPolicyYear(posRulesDAO
						.getYearlyServiceItemTimes(policyNo, prodSeq, "37",
								applyDate));

				// 险种已发生保险金给付
				plan.setProductPayByClaim(posRulesDAO.isProductPayByClaim(
						policyNo, prodSeq));

				// 查询险种生存金领取次数
				plan.setPaidSurivial((Integer) commonQueryDAO
						.getProductPaidSurvivalCount(policyNo, prodSeq)
						.intValue() > 0);
				planArr[i] = plan;
			}
			bom.setPlansList(planArr);
		}

		if (ServiceItemsExists.in("34,10")) {
			// 应领满期金金额
			bom.setMaturedPaymentMoney(maturedPaymentMoney);
		}

		if (ServiceItemsExists.in("8")) {
			// 最大可贷金额
			bom.setLoanableMoney(loanableMoney);

			// 最低可贷款金额
			double policyLoanMinMoney = posRulesDAO
					.policyLoanMinMoney(policyNo);
			bom.setPolicyLoanMinMoney(policyLoanMinMoney);
		}

		// 当前系统时间受理未完成的保全项目列表
		bom.setAcceptingServiceItems(posRulesDAO
				.acceptingServiceItems(policyNo));

		// 保全规则类型 1：受理规则；2：处理规则；3：受理规则和处理规则 wyz
		bom.setAppliedRuleType(appliedRuleType);

		Integer survivalPayCount = commonQueryDAO.getSurvivalPayCount(policyNo);

		// 保单是否有过保险金给付
		Map<String, Object> chailHistoryMap = commonQueryDAO
				.checkPolicyClaimHistory(policyNo, policyDTO.getEffectDate(),
						acceptDate);

		if (!"Y".equals(chailHistoryMap.get("p_flag"))) {
			throw new RuntimeException("检查保单理赔历史:"
					+ chailHistoryMap.get("p_message"));
		}

		if (survivalPayCount.intValue() > 0
				|| "Y".equals(chailHistoryMap.get("p_is_policy_claimed"))) {

			bom.setPaidSurvival(true);
		} else {
			bom.setPaidSurvival(false);

		}
		Map<String, Object> premTimesMap = commonQueryDAO
				.queryPolicyPremTimes(policyNo);
		BigDecimal premTimes = new BigDecimal(0);
		if (premTimesMap.get("p_prem_times") != null) {

			premTimes = (BigDecimal) premTimesMap.get("p_prem_times");

			bom.setPremTimes(premTimes.intValue());
		}
		// 客户是否已报案未受理
		bom.setClientReportedByClaim(posRulesDAO
				.isClientReportedByClaim(applicantNo));
		// 保单是否有组合代码
		bom.setPolicyCombinationCode(posRulesDAO
				.isPolicyCombinationCode(policyNo));
		// 客户e服务权限等级
		bom.getApplicant().setePrivs(
				commonQueryDAO.queryClientInputPrivs(clientNo));
		return bom;
	}

	/**
	 * 判断当前保全项目是否存在于指定的保全项目列表中的工具类
	 */
	protected static class ServiceItemsExists {
		private ThreadLocal<String> serviceItemsThreadLocal = new ThreadLocal<String>();
		private static ServiceItemsExists instance = new ServiceItemsExists();

		public static void init(String serviceItems) {
			instance.serviceItemsThreadLocal.set(serviceItems);
		}

		public static void destroy() {
			instance.serviceItemsThreadLocal.remove();
		}

		public static boolean in(String serviceItemsStr) {
			String serviceItems = instance.serviceItemsThreadLocal.get();
			if (StringUtils.isNotBlank(serviceItemsStr)
					&& StringUtils.isNotBlank(serviceItems)) {
				String[] serviceItemsArr = serviceItemsStr.split(",");
				for (String serviceItemsTmp : serviceItemsArr) {
					if (StringUtils.isNotBlank(serviceItemsTmp)
							&& serviceItemsTmp.trim().equals(serviceItems)) {
						return true;
					}
				}
			}
			return false;
		}
	}

}
