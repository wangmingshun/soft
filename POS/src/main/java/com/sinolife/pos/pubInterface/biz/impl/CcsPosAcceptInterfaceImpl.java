package com.sinolife.pos.pubInterface.biz.impl;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.unihub.framework.util.common.DateUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.sinolife.pos.acceptance.branch.service.BranchAcceptService;
import com.sinolife.pos.acceptance.branch.service.BranchQueryService;
import com.sinolife.pos.acceptance.trial.dto.TrialInfoDTO;
import com.sinolife.pos.acceptance.trial.service.TrialService;
import com.sinolife.pos.common.dao.CommonAcceptDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.PosStatusChangeHistoryDTO;
import com.sinolife.pos.common.dto.PosSurvivalDueDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_10;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_2;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_41;
import com.sinolife.pos.others.status.dao.AcceptStatusModifyDAO;
import com.sinolife.pos.pubInterface.biz.service.CcsPosAcceptInterface;
import com.sinolife.pos.todolist.approval.dao.ApprovalDAO;
import com.sinolife.pos.todolist.approval.service.ApprovalService;
import com.sinolife.sf.http.util.log.Log;
import com.sinolife.sf.platform.runtime.PlatformContext;

@Service("CcsPosAcceptInterface")
public class CcsPosAcceptInterfaceImpl extends DevelopPlatformInterfaceImpl
		implements CcsPosAcceptInterface {

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	private TrialService trialService;

	@Autowired
	private BranchAcceptService acceptService;

	@Autowired
	private BranchQueryService queryService;

	@Autowired
	private ApprovalService approvalService;

	@Autowired
	ApprovalDAO approvalDAO;

	@Autowired
	CommonAcceptDAO acceptDAO;

	@Autowired
	CommonQueryDAO queryDAO;

	@Autowired
	AcceptStatusModifyDAO modifyDAO;

	/**
	 * 选定受理客户，保单，项目等提交
	 */
	public ServiceItemsInputDTO clientSelectSubmit(final TrialInfoDTO trialInfo) {

		ServiceItemsInputDTO itemsInputDTO = null;

		try {
			itemsInputDTO = transactionTemplate
					.execute(new TransactionCallback<ServiceItemsInputDTO>() {
						@Override
						public ServiceItemsInputDTO doInTransaction(
								TransactionStatus transactionStatus) {
							try {
								return trialService
										.ccsTrialBatchAccept(trialInfo);
							} finally {
								transactionStatus.setRollbackOnly();
							}
						}
					});

			return itemsInputDTO;

		} catch (Exception e) {
			throw new RuntimeException("生成试算信息出错：" + e.getMessage());

		}

	}

	/**
	 * 受理明细录入提交，并试算出批文等信息
	 */
	public Map<String, Object> acceptDetailInputSubmit(
			final ServiceItemsInputDTO itemsInputDTO,
			final TrialInfoDTO trialInfo) {

		final BindingResult bindingResult = new BindingResult() {

			@Override
			public void setNestedPath(String arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void rejectValue(String arg0, String arg1, Object[] arg2,
					String arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void rejectValue(String arg0, String arg1, String arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void rejectValue(String arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void reject(String arg0, Object[] arg1, String arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void reject(String arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void reject(String arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void pushNestedPath(String arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void popNestedPath() throws IllegalStateException {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean hasGlobalErrors() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean hasFieldErrors(String arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean hasFieldErrors() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean hasErrors() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public String getObjectName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getNestedPath() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<ObjectError> getGlobalErrors() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getGlobalErrorCount() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public ObjectError getGlobalError() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object getFieldValue(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Class getFieldType(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<FieldError> getFieldErrors(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<FieldError> getFieldErrors() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getFieldErrorCount(String arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getFieldErrorCount() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public FieldError getFieldError(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public FieldError getFieldError() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getErrorCount() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public List<ObjectError> getAllErrors() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void addAllErrors(Errors arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public String[] resolveMessageCodes(String arg0, String arg1) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void recordSuppressedField(String arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public Object getTarget() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String[] getSuppressedFields() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object getRawFieldValue(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public PropertyEditorRegistry getPropertyEditorRegistry() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Map<String, Object> getModel() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public PropertyEditor findEditor(String arg0, Class arg1) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void addError(ObjectError arg0) {
				// TODO Auto-generated method stub

			}
		};
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			returnMap = transactionTemplate
					.execute(new TransactionCallback<Map<String, Object>>() {
						@Override
						public Map<String, Object> doInTransaction(
								TransactionStatus transactionStatus) {
							try {

								ServiceItemsInputDTO iiDTO = trialService
										.ccsTrialBatchAccept(trialInfo);

								String serviceItems = trialInfo
										.getServiceItems();
								if ("2".equals(serviceItems)) {

									ServiceItemsInputDTO_2 id2 = (ServiceItemsInputDTO_2) iiDTO;
									ServiceItemsInputDTO_2 d2 = (ServiceItemsInputDTO_2) itemsInputDTO;

									for (int i = 0; i < d2.getProductList()
											.size(); i++) {
										PolicyProductDTO pd = d2
												.getProductList().get(i);

										for (PolicyProductDTO pdo : id2
												.getProductList()) {

											if (pd.getProdSeq().intValue() == pdo
													.getProdSeq().intValue()) {

												pdo.setSelected(pd.isSelected());
												break;
											}
										}

									}
									id2.setAllSurrender(d2.isAllSurrender());
									return trialService.ccsTrialDetailSubmit(
											trialInfo, id2, bindingResult);

								} else if ("10".equals(serviceItems)) {

									ServiceItemsInputDTO_10 id10 = (ServiceItemsInputDTO_10) iiDTO;

									// 把页面输入的录入信息进行赋值
									ServiceItemsInputDTO_10 d10 = (ServiceItemsInputDTO_10) itemsInputDTO;

									// 功能特殊件类型
									id10.setSpecialFunc(d10.getSpecialFunc());

									// 生存金领取方式-1现金领取
									id10.setPayType("1");

									return trialService.ccsTrialDetailSubmit(
											trialInfo, id10, bindingResult);

								} else {
									return new HashMap<String, Object>();
								}

							}

							finally {
								transactionStatus.setRollbackOnly();
							}
						}
					});

		}

		catch (Exception e) {

			returnMap.put("errorMessage", "生成试算结果出错：" + e.getMessage());

			// throw new RuntimeException("生成试算结果出错：" + e.getMessage());

		}

		return returnMap;

	}

	/**
	 * 生成CCS保全用的条码
	 * 
	 * @param serviceItems
	 * @return
	 */
	public String generateCCSBarcode() {
		return "CCSN" + queryDAO.queryBarcodeNoSequence();

	}

	/**
	 * 实际保全退保操作受理入口
	 * 
	 * @param applyBatchInfo
	 * @return
	 */
	@Transactional
	public Map<String, Object> createApplyInfo(PosApplyBatchDTO applyInfo,
			List<String> lsSelectedProdSeq, String agreePremSum,
			String branchPercent, String specialFunc,String surrenderToNewApplyBarCode,
			String newApplyBarCodePrem) {

		Log.info("createApplyInfo start.....");
		String startDateStr = DateUtil
				.getCurrentDateString("yyyy-MM-dd hh:mm:ss");
		Date startDate = DateUtil.getCurrentDateTime();

		Map<String, Object> returnMap = new HashMap<String, Object>();
		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		applyInfo = createBatchHeader(applyInfo);
		PosApplyBatchDTO pab = acceptService.batchAccept(applyInfo);
		String posNo = pab.getPosInfoList().get(0).getPosNo();

		String policyNo = pab.getPosInfoList().get(0).getPolicyNo();
		Log.info("createApplyInfoPolicyNo=" + policyNo + " posNo=" + posNo);

		ServiceItemsInputDTO itemsInputDTO = queryService
				.queryAcceptDetailInputByPosNo(posNo);

		ServiceItemsInputDTO_2 serviceItemsInputDTO_2 = (ServiceItemsInputDTO_2) itemsInputDTO;

		// 功能特殊件
		serviceItemsInputDTO_2.setSpecialFunc(specialFunc);
		serviceItemsInputDTO_2.setSurrenderToNewApplyBarCode(surrenderToNewApplyBarCode);
		serviceItemsInputDTO_2.setNewApplyBarCodePrem(newApplyBarCodePrem);
		double agreeSum = Double.parseDouble(agreePremSum);
		String selectProdSeq;
		PolicyProductDTO ppd = null;
		String minSelectedProdSeq = null;
		for (int j = 0; j < lsSelectedProdSeq.size(); j++) {

			selectProdSeq = lsSelectedProdSeq.get(j);
			if (j == 0) {
				minSelectedProdSeq = selectProdSeq;

			}
			for (int i = 0; i < serviceItemsInputDTO_2.getProductList().size(); i++) {

				ppd = (serviceItemsInputDTO_2.getProductList().get(i));

				if (ppd.getProdSeq().toString().equals(selectProdSeq)) {
					ppd.setSelected(true);
					ppd.setAgreePremSum(ppd.getCashValue().toString());

					agreeSum = agreeSum - ppd.getCashValue().doubleValue();
					// 主险被选中，表示要整单退保
					if (ppd.getProdSeq().toString().equals("1")) {
						serviceItemsInputDTO_2.setAllSurrender(true);

					}

				}

			}
		}

		for (int i = 0; i < serviceItemsInputDTO_2.getProductList().size(); i++) {

			ppd = (serviceItemsInputDTO_2.getProductList().get(i));

			if (ppd.getProdSeq().toString().equals(minSelectedProdSeq)) {
				// 协议金额挂在最小序号产品上

				ppd.setAgreePremSum(String.valueOf(agreeSum
						+ ppd.getCashValue().doubleValue()));
				ppd.setBranchPercent(branchPercent);
			}
		}

		// 写受理明细
		Log.info("createApplyInfoAcceptDetailInputPolicyNo=" + policyNo
				+ " posNo=" + posNo);
		acceptService.acceptDetailInput(serviceItemsInputDTO_2);
		String resultMsg = null;
		List<String> posNoList = serviceItemsInputDTO_2.getPosNoList();
		for (int i = 0; i < posNoList.size(); i++) {
			posNo = posNoList.get(i);

			// 写状态变迁记录
			PosStatusChangeHistoryDTO statusChange = new PosStatusChangeHistoryDTO();
			statusChange.setPosNo(posNo);
			statusChange.setOldStatusCode("A01");
			statusChange.setNewStatusCode("A03");
			statusChange.setChangeUser(PlatformContext.getCurrentUser());
			acceptService.insertPosStatusChangeHistory(statusChange);

			acceptService.updatePosInfo(posNo, "accept_status_code", "A03");

			Log.info(posNo + " process rule engine start time -->");

			// 调用流转接口
			Map<String, Object> retMap = acceptService.workflowControl("03",
					posNo, false);

			String flag = (String) retMap.get("flag");
			String msg = (String) retMap.get("message");

			Log.info(PlatformContext.getCurrentUser() + posNo
					+ "workflow return posStatus-->" + flag);

			Log.info(posNo + " process rule engine end time -->");
			retMap = queryService.queryProcessResult(posNo);

			if ("C01".equals(flag)) {
				// 规则检查不通过
				retMap.put("RULE_CHECK_MSG", msg);

				Log.info("processRulesNotPassInfos-->" + msg);

				resultMsg = skipRuleService(posNo);

				Log.info("skip rules...");

			} else if ("A19".equals(flag)) {

				Log.info("UnderwritingRulesCheck.....");

				retMap = acceptService.workflowControl("05", posNo, false);

				flag = (String) retMap.get("flag");
				msg = (String) retMap.get("message");

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
				} else if ("A17".equals(flag)) {
					// 待保单打印
					resultMsg = "待保单打印";
				} else {
					resultMsg = msg;
				}
			}
			retList.add(retMap);
		}

		returnMap.put("retList", retList);
		returnMap.put("posNo", posNo);
		returnMap.put("acceptResult", resultMsg);

		Log.info(posNo + " call createApplyInfo  end  acceptResult-->"
				+ resultMsg);

		String endDateStr = DateUtil
				.getCurrentDateString("yyyy-MM-dd hh:mm:ss");
		org.eclipse.jetty.util.log.Log.info(posNo
				+ " createApplyInfo run startTime is=>" + startDateStr
				+ " endTime is=>" + endDateStr);
		Date endDate = DateUtil.getCurrentDateTime();
		long runSecond = (endDate.getTime() - startDate.getTime()) / 1000;
		// 由于rpc接口运行时间不能超过5分钟，对超过时间的情况进行撤销
		if (runSecond > 300) {

			Log.info(posNo + "createApplyInfo overtime start cancel ... ");
			cancelAccept(posNo);

			Log.info("because runtimes bigger 5 minutes " + posNo
					+ "  do cancel ;");
		}

		return returnMap;

	}

	/**
	 * 实际保全退保操作受理入口(咨诉系统41保全项目)
	 * 
	 * @param applyBatchInfo
	 * @return
	 */
	@Transactional
	public Map<String, Object> createApplyInfoFor41(Map<String, Object> map) {

		Map<String, Object> returnMap = new HashMap<String, Object>();
		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();

		PosApplyBatchDTO applyInfo = (PosApplyBatchDTO) map.get("applyInfo");
		String treatyPrem = (String) map.get("treatyPrem");
		String branchPercent = (String) map.get("branchPercent");

		applyInfo = createBatchHeader(applyInfo);
		PosApplyBatchDTO pab = acceptService.batchAccept(applyInfo);
		String posNo = pab.getPosInfoList().get(0).getPosNo();
		ServiceItemsInputDTO itemsInputDTO = queryService
				.queryAcceptDetailInputByPosNo(posNo);

		ServiceItemsInputDTO_41 serviceItemsInputDTO_41 = (ServiceItemsInputDTO_41) itemsInputDTO;

		// 功能特殊件为‘特殊件—协议退保(投诉业务)’
		// serviceItemsInputDTO_41.setSpecialFunc("9");
		PolicyProductDTO ppd = null;

		if (serviceItemsInputDTO_41.getProductList() != null
				&& serviceItemsInputDTO_41.getProductList().size() > 0) {

			ppd = (serviceItemsInputDTO_41.getProductList().get(0));

			ppd.setAgreePremSum(new Double(treatyPrem).toString());
			ppd.setBranchPercent(branchPercent);

		}

		// 写受理明细
		acceptService.acceptDetailInput(serviceItemsInputDTO_41);
		String resultMsg = null;
		List<String> posNoList = serviceItemsInputDTO_41.getPosNoList();
		for (int i = 0; i < posNoList.size(); i++) {
			posNo = posNoList.get(i);

			acceptService.updatePosInfo(posNo, "accept_status_code", "A03");

			Log.info(PlatformContext.getCurrentUser()
					+ " call process rules ....");
			Log.info(posNo + " process rule engine start time -->"
					+ queryDAO.getSystemDate());
			// 调用流转接口
			Map<String, Object> retMap = acceptService.workflowControl("03",
					posNo, false);

			String flag = (String) retMap.get("flag");
			String msg = (String) retMap.get("message");

			Log.info(posNo + " process rule engine end time -->"
					+ queryDAO.getSystemDate());
			retMap = queryService.queryProcessResult(posNo);

			retMap = queryService.queryProcessResult(posNo);
			if ("C01".equals(flag)) {
				// 规则检查不通过
				retMap.put("RULE_CHECK_MSG", msg);

				Log.info("processRulesNotPassInfos-->" + msg);

				resultMsg = skipRuleService(posNo);

				Log.info("skip rules...");

			} else if ("A19".equals(flag)) {

				Log.info("UnderwritingRulesCheck.....");
				retMap = acceptService.workflowControl("05", posNo, false);

				flag = (String) retMap.get("flag");
				msg = (String) retMap.get("message");

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
				} else if ("A17".equals(flag)) {
					// 待保单打印
					resultMsg = "待保单打印";
				} else {
					resultMsg = msg;
				}
			}
			retList.add(retMap);
		}

		returnMap.put("retList", retList);
		returnMap.put("posNo", posNo);
		returnMap.put("acceptResult", resultMsg);

		Log.info(PlatformContext.getCurrentUser()
				+ " call createApplyInfoFor41  end  acceptResult-->"
				+ resultMsg);

		return returnMap;

	}

	/**
	 * 协议满期实际保全操作受理入口
	 * 
	 * @param applyInfo
	 * @param specialFunc
	 *            功能特殊件类型
	 * @param agreeMaturitySum满期协议金额
	 * @param branchPercent分公司承担比例
	 * @param backBenefit
	 *            既得利益
	 * @return
	 */
	@Transactional
	public Map<String, Object> createApplyInfoForMaturity(
			PosApplyBatchDTO applyInfo, String specialFunc,
			String agreeMaturitySum, String branchPercent, String backBenefit
			,String surrenderToNewApplyBarCode,String newApplyBarCodePrem	
	) {

		String startDateStr = DateUtil
				.getCurrentDateString("yyyy-MM-dd hh:mm:ss");
		Date startDate = DateUtil.getCurrentDateTime();

		Map<String, Object> returnMap = new HashMap<String, Object>();
		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		applyInfo = createBatchHeader(applyInfo);
		PosApplyBatchDTO pab = acceptService.batchAccept(applyInfo);
		String posNo = pab.getPosInfoList().get(0).getPosNo();
		ServiceItemsInputDTO itemsInputDTO = queryService
				.queryAcceptDetailInputByPosNo(posNo);

		ServiceItemsInputDTO_10 serviceItemsInputDTO_10 = (ServiceItemsInputDTO_10) itemsInputDTO;

		List<PosSurvivalDueDTO> survivalDueList = serviceItemsInputDTO_10
				.getSurvivalDueList();

		for (int i = 0; survivalDueList != null && i < survivalDueList.size(); i++) {
			PosSurvivalDueDTO posSurvivalDueDTO = survivalDueList.get(i);
			// 协议满期金领取，需要录入分公司承担比较及既得利益
			if ("1".equals(posSurvivalDueDTO.getSurvivalType())) {

				posSurvivalDueDTO.setAgreeMaturitySum(agreeMaturitySum);
				posSurvivalDueDTO.setBranchPercent(branchPercent);
				posSurvivalDueDTO.setBackBenefit(backBenefit);

			}

		}
		// 协议满期功能特殊件类型
		serviceItemsInputDTO_10.setSpecialFunc(specialFunc);
		serviceItemsInputDTO_10.setPayType("1");// 现金领取
		
		serviceItemsInputDTO_10.setSurrenderToNewApplyBarCode(surrenderToNewApplyBarCode);
		serviceItemsInputDTO_10.setNewApplyBarCodePrem(newApplyBarCodePrem);
		// 写受理明细
		acceptService.acceptDetailInput(serviceItemsInputDTO_10);
		String resultMsg = null;
		List<String> posNoList = serviceItemsInputDTO_10.getPosNoList();
		for (int i = 0; i < posNoList.size(); i++) {
			posNo = posNoList.get(i);

			// 写状态变迁记录
			PosStatusChangeHistoryDTO statusChange = new PosStatusChangeHistoryDTO();
			statusChange.setPosNo(posNo);
			statusChange.setOldStatusCode("A01");
			statusChange.setNewStatusCode("A03");
			statusChange.setChangeUser(PlatformContext.getCurrentUser());
			acceptService.insertPosStatusChangeHistory(statusChange);

			Log.info(PlatformContext.getCurrentUser() + posNo
					+ "update posinfo A03 ...." + queryDAO.getSystemDate());

			acceptService.updatePosInfo(posNo, "accept_status_code", "A03");

			Log.info(PlatformContext.getCurrentUser()
					+ " call process rules ....");
			Log.info(posNo + " process rule engine start time -->"
					+ queryDAO.getSystemDate());

			// 调用流转接口
			Map<String, Object> retMap = acceptService.workflowControl("03",
					posNo, false);

			String flag = (String) retMap.get("flag");
			String msg = (String) retMap.get("message");

			Log.info(PlatformContext.getCurrentUser() + posNo
					+ "workflow return posStatus-->" + flag);

			Log.info(posNo + " process rule engine end time -->"
					+ queryDAO.getSystemDate());
			retMap = queryService.queryProcessResult(posNo);

			if ("C01".equals(flag)) {
				// 规则检查不通过
				retMap.put("RULE_CHECK_MSG", msg);

				Log.info("processRulesNotPassInfos-->" + msg);

				resultMsg = skipRuleService(posNo);

				Log.info("skip rules...");

			} else if ("A19".equals(flag)) {

				Log.info("UnderwritingRulesCheck.....");

				retMap = acceptService.workflowControl("05", posNo, false);

				flag = (String) retMap.get("flag");
				msg = (String) retMap.get("message");

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
				} else if ("A17".equals(flag)) {
					// 待保单打印
					resultMsg = "待保单打印";
				} else {
					resultMsg = msg;
				}
			}
			retList.add(retMap);
		}

		returnMap.put("retList", retList);
		returnMap.put("posNo", posNo);
		returnMap.put("acceptResult", resultMsg);

		Log.info(PlatformContext.getCurrentUser()
				+ " call createApplyInfoForMaturity  end  acceptResult-->"
				+ resultMsg);

		String endDateStr = DateUtil
				.getCurrentDateString("yyyy-MM-dd hh:mm:ss");
		org.eclipse.jetty.util.log.Log.info(posNo
				+ " createApplyInfoForMaturity run startTime is=>"
				+ startDateStr + " endTime is=>" + endDateStr);
		Date endDate = DateUtil.getCurrentDateTime();
		long runSecond = (endDate.getTime() - startDate.getTime()) / 1000;
		// 由于rpc接口运行时间不能超过5分钟，对超过时间的情况进行撤销
		if (runSecond > 300) {

			cancelAccept(posNo);

			Log.info("because runtimes bigger 5 minutes " + posNo
					+ "  do cancel ;");
		}

		return returnMap;

	}

	// 审批一个案件（包含多个保全）
	public void ccsApproveList(List<Map<String, Object>> ls) {

		for (int i = 0; i < ls.size(); i++) {

			Map<String, Object> pMap = (Map<String, Object>) ls.get(i);

			ccsApprove(pMap);

		}

	}

	/**
	 * 受理撤销，对于已经受理的保全，强制改掉受理状态为C02
	 * 
	 * @param posInfo
	 * @param acceptUser
	 */
	public void cancelAccept(String posNo) {

		PosInfoDTO posInfoDTO = queryDAO.queryPosInfoRecord(posNo);
		String acceptUser = posInfoDTO.getAcceptor();
		cancelAccept(posInfoDTO, acceptUser);

	}

	// 审批
	public void ccsApprove(Map<String, Object> pMap) {

		String posNo = (String) pMap.get("posNo");
		approvalService.lockPosApprove(posNo, "A08", "A");

		approvalDAO.updateApproveDecision(pMap);
		PosStatusChangeHistoryDTO change = new PosStatusChangeHistoryDTO();
		change.setPosNo(posNo);
		change.setChangeDate(queryDAO.getSystemDate());
		change.setChangeUser(PlatformContext.getCurrentUser());

		if ("1".equals(pMap.get("decision")) && approvalDAO.allApproved(pMap)) {// 通过，且全审批完了

			pMap.put("acceptStatus", "A10");// 状态走两步，到待投保、核保规则检查状态
			approvalDAO.updatePosApproved(pMap);

			change.setOldStatusCode("A08");
			change.setNewStatusCode("A09");
			acceptDAO.insertPosStatusChangeHistory(change);

			change.setOldStatusCode("A09");
			change.setNewStatusCode("A10");
			acceptDAO.insertPosStatusChangeHistory(change);

		} else if ("2".equals(pMap.get("decision"))) {// 不通过

			pMap.put("acceptStatus", "C03");
			approvalDAO.updatePosApproved(pMap);

			change.setOldStatusCode("A08");
			change.setNewStatusCode("C03");
			acceptDAO.insertPosStatusChangeHistory(change);

			acceptDAO.resumePolicySuspend((String) pMap.get("policyNo"), posNo,
					(String) pMap.get("serviceItems"));

		}
	}

	// 跳规则处理
	public String skipRuleService(String posNo) {

		PosInfoDTO posInfoDTO = queryDAO.queryPosInfoRecord(posNo);

		String returnFlag = "";
		if (posInfoDTO != null) {

			String statusCode = posInfoDTO.getAcceptStatusCode();
			if (StringUtils.isNotBlank(statusCode)) {
				String resultMsg = null;
				Date sysdate = queryDAO.getSystemDate();
				PosStatusChangeHistoryDTO change = new PosStatusChangeHistoryDTO();
				change.setPosNo(posNo);
				change.setChangeDate(sysdate);
				change.setChangeUser(PlatformContext.getCurrentUser());
				Log.info("posNo-->" + posNo + " accept_status_code--> "
						+ statusCode);
				if ("C01".equals(statusCode)) {

					acceptDAO.updatePosInfo(posNo, "accept_status_code", "A05");

					change.setOldStatusCode("C01");
					change.setNewStatusCode("A05");
					acceptDAO.insertPosStatusChangeHistory(change);
					modifyDAO.insertDataUpdateLog("04", "3",
							posInfoDTO.getPolicyNo(), "5", posNo, "", "");

					// 对保单置暂停
					acceptDAO.doPolicySuspend(posInfoDTO.getPolicyNo(), "5",
							posNo, posInfoDTO.getServiceItems(), sysdate);

					// 调用流转接口
					Map<String, Object> retMap = acceptService.workflowControl(
							"04", posNo, false);

					returnFlag = (String) retMap.get("flag");
					String msg = (String) retMap.get("message");

					retMap = queryService.queryProcessResult(posNo);
					if ("A19".equals(returnFlag)) {

						retMap = acceptService.workflowControl("05", posNo,
								false);

						returnFlag = (String) retMap.get("flag");
						msg = (String) retMap.get("message");

						if ("A08".equals(returnFlag)) {
							// 已送审
							resultMsg = "已送审";
						} else if ("A12".equals(returnFlag)) {
							// 已送人工核保
							resultMsg = "已送人工核保";
						} else if ("A15".equals(returnFlag)) {
							// 待收费
							resultMsg = "待收费";
						} else if ("E01".equals(returnFlag)) {
							// 生效完成
							resultMsg = "已生效";
						} else if ("A17".equals(returnFlag)) {
							// 待保单打印
							resultMsg = "待保单打印";
						} else {
							resultMsg = msg;
						}
					}
					// retList.add(retMap);

				} else if ("C05".equals(statusCode)) {

					acceptDAO.updatePosInfo(posNo, "accept_status_code", "A01");

					change.setOldStatusCode("C05");
					change.setNewStatusCode("A01");
					modifyDAO.insertDataUpdateLog("04", "2",
							posInfoDTO.getPolicyNo(), "5", posNo, "", "");

					// 对保单置暂停
					acceptDAO.insertPosStatusChangeHistory(change);
					acceptDAO.doPolicySuspend(posInfoDTO.getPolicyNo(), "5",
							posNo, posInfoDTO.getServiceItems(), sysdate);

					acceptService.updatePosInfo(posNo, "accept_status_code",
							"A03");

					// 调用流转接口
					Map<String, Object> retMap = acceptService.workflowControl(
							"03", posNo, false);

					returnFlag = (String) retMap.get("flag");
					String msg = (String) retMap.get("message");

					retMap = queryService.queryProcessResult(posNo);
					if ("C01".equals(returnFlag)) {
						// 规则检查不通过
						retMap.put("RULE_CHECK_MSG", msg);

						acceptDAO.updatePosInfo(posNo, "accept_status_code",
								"A05");

						change.setOldStatusCode("C01");
						change.setNewStatusCode("A05");
						acceptDAO.insertPosStatusChangeHistory(change);
						modifyDAO.insertDataUpdateLog("04", "3",
								posInfoDTO.getPolicyNo(), "5", posNo, "", "");

						// 调用流转接口
						retMap = acceptService.workflowControl("04", posNo,
								false);

						returnFlag = (String) retMap.get("flag");
						msg = (String) retMap.get("message");

						retMap = queryService.queryProcessResult(posNo);
						if ("A19".equals(returnFlag)) {

							retMap = acceptService.workflowControl("05", posNo,
									false);

							returnFlag = (String) retMap.get("flag");
							msg = (String) retMap.get("message");

							if ("A08".equals(returnFlag)) {
								// 已送审
								resultMsg = "已送审";
							} else if ("A12".equals(returnFlag)) {
								// 已送人工核保
								resultMsg = "已送人工核保";
							} else if ("A15".equals(returnFlag)) {
								// 待收费
								resultMsg = "待收费";
							} else if ("E01".equals(returnFlag)) {
								// 生效完成
								resultMsg = "已生效";
							} else if ("A17".equals(returnFlag)) {
								// 待保单打印
								resultMsg = "待保单打印";
							} else {
								resultMsg = msg;
							}
						}

					}

				}

			}

		}

		return returnFlag;

	}
}
