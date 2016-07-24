package com.sinolife.pos.pubInterface.biz.impl;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.unihub.framework.util.common.DateUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.BindingResult;

import com.sinolife.efs.rpc.domain.UserInfo;
import com.sinolife.pos.acceptance.branch.service.BranchAcceptService;
import com.sinolife.pos.acceptance.branch.service.BranchQueryService;
import com.sinolife.pos.acceptance.rollback.dao.BranchRollbackDAO;
import com.sinolife.pos.acceptance.trial.dto.TrialInfoDTO;
import com.sinolife.pos.common.dao.ClientInfoDAO;
import com.sinolife.pos.common.dao.CommonAcceptDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_1;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_10;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_2;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_20;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_23;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_3;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_35;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_37;
import com.sinolife.pos.common.dto.ClientAccountDTO;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.FinancialProductsDTO;
import com.sinolife.pos.common.dto.PolicyContactInfoDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.common.dto.PosApplyFilesDTO;
import com.sinolife.pos.common.dto.PosBiaCheckDto;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.PosStatusChangeHistoryDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_1;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_10;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_2;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_23;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_3;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_35;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_37;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.pubInterface.biz.dto.PosApplyInfoDTO;
import com.sinolife.pos.pubInterface.biz.service.BiaPosAcceptInterface;
import com.sinolife.pos.rpc.ilogjrules.posrules.dao.PosRulesDAO;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMRuleInfoDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSVerifyResultDto;
import com.sinolife.sf.http.util.log.Log;
@Service("BiaPosAcceptInterface")
public class BiaPosAcceptInterfaceImpl extends DevelopPlatformInterfaceImpl
		implements BiaPosAcceptInterface {
	@Autowired
	private BranchAcceptService branchAcceptService;
	@Autowired
	private BranchRollbackDAO branchRollbackDAO;

	@Autowired
	private BranchQueryService branchQueryService;
  
	@Autowired
	private ClientInfoDAO clientInfoDAO;

	@Autowired
	private CommonQueryDAO commonQueryDAO;
	@Autowired
	private CommonAcceptDAO acceptDAO;
	@Autowired
	private ServiceItemsDAO_23 serviceItemsDAO_23;
	@Autowired
	private ServiceItemsDAO_2 serviceItemsDAO_2;
	@Autowired
	private ServiceItemsDAO_1 serviceItemsDAO_1;
	@Autowired
	private ServiceItemsDAO_3 serviceItemsDAO_3;
	@Autowired
	private ServiceItemsDAO_35 serviceItemsDAO_35;
	@Autowired
	private ServiceItemsDAO_10 serviceItemsDAO_10;
	@Autowired
	private ServiceItemsDAO_37 serviceItemsDAO_37;

	@Autowired
	private ServiceItemsDAO_20 serviceItemsDAO_20;

	@Autowired
	private PosRulesDAO posRulesDAO;

	@Autowired
	private TransactionTemplate transactionTemplate;
	Logger logger = Logger.getLogger(BiaPosAcceptInterfaceImpl.class.getName());

	/**
	 * 生成BIA用的条码
	 * 
	 * @param serviceItems
	 * @return
	 */
	private String generateBiaarcode(String serviceItems) {
		// 条形码统一改为sequence生成方式
		return "BIAN" + commonQueryDAO.queryBarcodeNoSequence();
	}
	/**
	 * 生成银代前置的条码
	 * 
	 * @param serviceItems
	 * @return
	 */
	private String generateBankbarcode(String serviceItems) {
		// 条形码统一改为sequence生成方式
        String barcodeNoTemp = "1236" + "01" + commonQueryDAO.queryEpointBarcodeNoSequence();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("barCode", barcodeNoTemp);
        // 获取条形码后两位
        commonQueryDAO.getBarCodeSum(map);
        String barcodeNo = barcodeNoTemp + map.get("barCodeSum");
        return barcodeNo;	
	}
	/**
	 * 受理撤销，对于已经是结束状态的受理，强制改掉受理状态为C02
	 * 
	 * @param posInfo
	 * @param acceptUser
	 */
	public void cancelAccept(PosInfoDTO posInfo, String acceptUser) {
		String currentAcceptStatus = posInfo.getAcceptStatusCode();
		String posNo = posInfo.getPosNo();
		if (currentAcceptStatus.startsWith("C")) {
			if (!"C02".equals(currentAcceptStatus)) {
				// 写入受理变迁记录
				PosStatusChangeHistoryDTO statusChange = new PosStatusChangeHistoryDTO();
				statusChange.setPosNo(posNo);
				statusChange.setOldStatusCode(currentAcceptStatus);
				statusChange.setNewStatusCode("C02");
				statusChange.setChangeUser(acceptUser);
				statusChange.setChangeDate(commonQueryDAO.getSystemDate());
				acceptDAO.insertPosStatusChangeHistory(statusChange);
				// 更新受理状态
				branchAcceptService.updatePosInfo(posNo, "ACCEPT_STATUS_CODE",
						"C02");
			}
		} else if (!currentAcceptStatus.startsWith("E")) {
			Map<String, Object> ret = branchAcceptService.workflowControl("11",
					posNo, false);
			String flag = (String) ret.get("flag");
			String msg = (String) ret.get("message");
			if (!"C02".equals(flag)) {
				throw new RuntimeException("受理撤销失败：" + msg);
			}
		} else {
			throw new RuntimeException("受理撤销失败：保全" + posNo + "已经生效完成!");
		}
	}

	/**
	 * 受理撤销（银保通冲正）C02
	 * 
	 * @param posInfo
	 * @param acceptUser
	 */
	public Map<String, Object> rollbackAccept(Map map) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		String SL_RSLT_MESG = null;
		String SL_RSLT_CODE = null;
		try {
			// String barcode=(String) map.get("BARCODE_NO");
			// String service_items=(String) map.get("SERVICEITEMS");
			// List
			// posNolist=commonQueryDAO.queryPosNoByBarcodeAndServiceItems(barcode,service_items);
			String posNo = (String) map.get("POS_NO");
			Date calcdate = new Date();
			Map rolMap = branchRollbackDAO.rollBack(calcdate, posNo);
			String flag = (String) rolMap.get("flag");
			String message = (String) rolMap.get("message");
			if ("0".equals(flag)) {
				SL_RSLT_MESG = "冲正成功！";
				SL_RSLT_CODE = "999999";
			} else {
				SL_RSLT_MESG = message;
				SL_RSLT_CODE = "999002";
			}

		} catch (Exception e) {
			SL_RSLT_CODE = "999002";
			SL_RSLT_MESG = "冲正失败：" + e.getMessage();
		}
		retMap.put("SL_RSLT_MESG", SL_RSLT_MESG);
		retMap.put("SL_RSLT_CODE", SL_RSLT_CODE);
		return retMap;
	}

	/**
	 * 保全试算
	 * 
	 * @param sii
	 * @param applyInfo
	 * @return
	 */
	public List<Map<String, Object>> queryInputservice(
			ServiceItemsInputDTO sii, final PosApplyInfoDTO applyInfo) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		String SL_RSLT_MESG = null;
		String SL_RSLT_CODE = null;
		String APPROVE_TEXT = null;
		try {
			PosApplyBatchDTO batch = createBatch(applyInfo);
			PosInfoDTO posInfo = new PosInfoDTO();
			String posNo = batch.getPosInfoList().get(0).getPosNo();

			sii.setPosNo(posNo);
			sii.setPosNoList(null);
			sii.setBarcodeNo(batch.getApplyFiles().get(0).getBarcodeNo());
			sii.setBatchNo(batch.getPosBatchNo());
			ServiceItemsInputDTO oriItemsInputDTO = branchQueryService
					.queryAcceptDetailInputByPosNo(posNo);
			sii.setOriginServiceItemsInputDTO(oriItemsInputDTO
					.getOriginServiceItemsInputDTO());
			sii.setClientNo(applyInfo.getClientNo());
			// 写受理明细
			branchAcceptService.acceptDetailInput(sii);
			List<String> posNoList = sii.getPosNoList();
			for (int i = 0; i < posNoList.size(); i++) {
				posNo = posNoList.get(i);

				// 更新备注字段
				branchAcceptService.updatePosInfo(posNo, "remark",
						sii.getRemark());

				// 写状态变迁记录
				PosStatusChangeHistoryDTO statusChange = new PosStatusChangeHistoryDTO();
				statusChange.setPosNo(posNo);
				statusChange.setOldStatusCode("A01");
				statusChange.setNewStatusCode("A03");
				statusChange.setChangeDate(new Date());
				statusChange.setChangeUser(batch.getAcceptor());
				branchAcceptService.insertPosStatusChangeHistory(statusChange);
				if (!"0".equals(statusChange.getFlag())) {
					SL_RSLT_CODE = "999002";
					SL_RSLT_MESG = "写保全状态变更记录失败：" + statusChange.getMessage();
					branchAcceptService.cancelAccept(posInfo,
							batch.getAcceptor());
					// throw new RuntimeException("写保全状态变更记录失败：" +
					// statusChange.getMessage());
				}

				// 这些渠道都是逐单受理，因此前一单受理完成，更新状态为 A03待处理规则检查
				branchAcceptService.updatePosInfo(posNo, "accept_status_code",
						"A03");

				// 调用流转接口
				retMap = branchAcceptService
						.workflowControl("03", posNo, false);
				logger.info("acceptDetailInputSubmit workflowControl return:"
						+ retMap);
				String flag = (String) retMap.get("flag");
				String msg = (String) retMap.get("message");
				String resultDesc = (String) retMap.get("resultDesc");
				retMap = branchQueryService.queryProcessResult(posNo);
				if ("C01".equals(flag)) {
					// 规则检查不通过
					SL_RSLT_CODE = "999002";
					SL_RSLT_MESG = msg;
				} else if ("A19".equals(flag)) {
					posInfo = commonQueryDAO.queryPosInfoRecord(posNo);
					APPROVE_TEXT = posInfo.getApproveText();// 批文
					SL_RSLT_CODE = "999999";
					SL_RSLT_MESG = "查询成功！";

				} else if ("A03".equals(flag)) {
					SL_RSLT_CODE = "999002";
					SL_RSLT_MESG = resultDesc;
				}
			}
		} catch (Exception e) {
			SL_RSLT_CODE = "999002";
			SL_RSLT_MESG = e.getMessage();
		}
		retMap.put("SL_RSLT_MESG", SL_RSLT_MESG);
		retMap.put("SL_RSLT_CODE", SL_RSLT_CODE);
		retMap.put("APPROVE_TEXT", APPROVE_TEXT);
		listMap.add(retMap);
		return listMap;

	}

	/**
	 * 银保通查询（试算）公共部分
	 * 
	 * @param posInfo
	 * @param acceptUser
	 */
	public List<Map<String, Object>> acceptDetailInputSubmit(
			final ServiceItemsInputDTO sii, final PosApplyInfoDTO applyInfo) {
		List<Map<String, Object>> processResultList = new ArrayList<Map<String, Object>>();
		try {
			processResultList = transactionTemplate
					.execute(new TransactionCallback<List<Map<String, Object>>>() {
						@Override
						public List<Map<String, Object>> doInTransaction(
								TransactionStatus transactionStatus) {
							try {
								return queryInputservice(sii, applyInfo);
							} finally {
								transactionStatus.setRollbackOnly();
							}
						}
					});
			// if(!bindingResult.hasErrors()) {
			// mav.addObject(SessionKeys.ACCEPTANCE_PROCESS_RESULT_LIST,
			// processResultList);
			// mav.setViewName(ViewNames.ACCEPTANCE_BRANCH_PROCESS_RESULT);
			// }
		} catch (Exception e) {
			throw new RuntimeException("保全试算(查询)失败" + e.getMessage());
		}
		return processResultList;
	}

	/**
	 * 银保通查询（试算）
	 * 
	 * 
	 */
	public Map<String, Object> queryInputserviceItems(Map map) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		String SL_RSLT_MESG = null;
		String SL_RSLT_CODE = null;
		try {
			ServiceItemsInputDTO itemsInputDTO = new ServiceItemsInputDTO();
			BindingResult bindingResult = null;
			TrialInfoDTO trialInfo = new TrialInfoDTO();
			String serviceItems = (String) map.get("SERVICEITEMS");
			String applyDate = (String) map.get("APPLYDATE");
			String policyNo = (String) map.get("POLICYNO");
			PosApplyInfoDTO applyInfo = new PosApplyInfoDTO();
			MapToBean(applyInfo, (Map) map.get("POSAPPLYINFO"));
			applyInfo.setServiceItems(serviceItems);
			String trancode = (String) map.get("TRAN_CODE");
			String product_code = (String) map.get("PRODUCT_CODE");
			applyInfo.setApplyDate(DateUtil.stringToDate((String) map
					.get("APPLYDATE")));
			applyInfo.setClientNo(commonQueryDAO
					.getApplicantByPolicyNo(applyInfo.getPolicyNo()));
			String clientNo = commonQueryDAO.getApplicantByPolicyNo(policyNo);
			if ("35".equals(serviceItems)) {
				String contract_status = "1";
				retMap = commonQueryDAO.queryFinancialProductsInfo(policyNo,
						product_code, DateUtil.stringToDate(applyDate),
						contract_status);
				String p_total_amount = ((java.math.BigDecimal) retMap
						.get("p_total_amount")).toString();
				String p_settle_day_rate = ((java.math.BigDecimal) retMap
						.get("p_settle_day_rate")).toString();
				String p_settle_year_rate = ((java.math.BigDecimal) retMap
						.get("p_settle_year_rate")).toString();
				retMap.put("p_total_amount", p_total_amount);
				retMap.put("p_settle_day_rate", p_settle_day_rate);
				retMap.put("p_settle_year_rate", p_settle_year_rate);
				SL_RSLT_MESG = "查询成功！";
				SL_RSLT_CODE = "999999";
			} else {
				// 契撤
				if ("1".equals(serviceItems)) {
					itemsInputDTO = queryServiceItemsInfoExtraToBia_1(map);
					// 退保
				} else if ("2".equals(serviceItems)) {
					itemsInputDTO = queryServiceItemsInfoExtraToBia_2(map);
					// 满期给付
				} else if ("10".equals(serviceItems)) {
					itemsInputDTO = queryServiceItemsInfoExtraToBia_10(map);
				} else if ("37".equals(serviceItems)) {
					itemsInputDTO = queryServiceItemsInfoExtraToBia_37(map);
				} else {
					SL_RSLT_CODE = "999002";
					SL_RSLT_MESG = "该保全项目不能查询";
				}
				trialInfo.setServiceItems(serviceItems);
				trialInfo.setApplyDate(DateUtil.stringToDate(applyDate));
				trialInfo.setPolicyNo(policyNo);
				trialInfo.setClientNo(clientNo);
				retMap = acceptDetailInputSubmit(itemsInputDTO, applyInfo).get(
						0);
				// 如果是淘宝的部分领取，退保则需要传账户价值，百度投连退保
				if ("PartWithdrawQuery".endsWith(trancode)
						|| "SurrAllRealQuery".endsWith(trancode)
						|| "PartWithdrawQuery".endsWith(trancode)
						|| "SurrAllRealQueryII".endsWith(trancode)
						|| "SurrAllRealQuery".endsWith(trancode)) {
					Map<String, Object> accountMap = new HashMap<String, Object>();
					//百度退保查询，需要查询手续费
					if ("SurrAllRealQueryII".equals(trancode)&&"24".equals(applyInfo.getAcceptChannelCode())){		
					    accountMap = commonQueryDAO.getProductValueDetail(policyNo, product_code, DateUtil.stringToDate(applyDate));
					    String p_flag =(String) accountMap.get("p_flag");
						if ("0".equals(p_flag)){
						    	String p_total_amount = ((java.math.BigDecimal) accountMap
										.get("p_total_amount")).toString();
						    	String p_amt_amount="0";
						    //退保才有手续费
						    if ("2".equals(serviceItems)){
						        p_amt_amount = ((java.math.BigDecimal) accountMap
										.get("p_amt_amount")).toString();
						    }
						    retMap.put("p_total_amount", p_total_amount);
						    retMap.put("p_amt_amount", p_amt_amount);
						}else{
							
						}
					}else{
						accountMap = commonQueryDAO.queryFinancialProductsInfo(
								policyNo, product_code,
								DateUtil.stringToDate(applyDate), "2");
						String p_total_amount = ((java.math.BigDecimal) accountMap
								.get("p_total_amount")).toString();
						retMap.put("p_total_amount", p_total_amount);
					}
				}
				SL_RSLT_CODE = (String) retMap.get("SL_RSLT_CODE");
				SL_RSLT_MESG = (String) retMap.get("SL_RSLT_MESG");
			}
		} catch (Exception e) {
			SL_RSLT_CODE = "999002";
			SL_RSLT_MESG = e.getMessage();
		}
		retMap.put("SL_RSLT_MESG", SL_RSLT_MESG);
		retMap.put("SL_RSLT_CODE", SL_RSLT_CODE);
		return retMap;
	}

	/**
	 * 写入批次记录、申请书记录，及保全记录
	 * 
	 * @param applyInfo
	 * @return
	 */
	private PosApplyBatchDTO createBatch(final PosApplyInfoDTO applyInfo) {
		List<ClientInformationDTO> clientInfoList = clientInfoDAO
				.selClientinfoForClientno(applyInfo.getClientNo());
		if (clientInfoList == null || clientInfoList.isEmpty()
				|| clientInfoList.size() != 1)
			throw new RuntimeException("无效的保单号：" + applyInfo.getClientNo());
		PosApplyFilesDTO posApplyFilesDto = new PosApplyFilesDTO();
		String applyType = applyInfo.getApplyType();
		String acceptChannelCode = applyInfo.getAcceptChannelCode();
		String approvalServiceType = applyInfo.getApprovalServiceType();
		String paymentType = applyInfo.getPaymentType();
		String clientNo = applyInfo.getClientNo();// 投保人
		String policyNo = applyInfo.getPolicyNo();
		String barcode = applyInfo.getBarCode();
		String accountNoType = applyInfo.getAccountNoType();
		String serviceItems = applyInfo.getServiceItems();
		String insNo = commonQueryDAO.getInsuredByPolicyNo(policyNo).get(0);
		Map clientMap = new HashMap();
		// 满期金写被保人
		if ("10".equals(applyInfo.getServiceItems())) {
			clientMap = commonQueryDAO.QueryClientInfoByclientNo(insNo);
		} else {
			clientMap = commonQueryDAO.QueryClientInfoByclientNo(clientNo);
		}
		String clientName = (String) clientMap.get("CLIENT_NAME");// 客户姓名
		String idtype = (String) clientMap.get("ID_TYPE");// 证件类型
		String idno = (String) clientMap.get("IDNO");// 证件号码
		if (StringUtils.isBlank(barcode)) {
			if ("12".equals(acceptChannelCode)) {
				//银保通受理渠道需要传入部门代码
				if(StringUtils.isBlank(applyInfo.getDeptNo())){
					throw new RuntimeException("传入的部门代码为空");
				}else{
					Map needBankBarcodeMap = new HashMap();
					//根据部门代码查询对应银行是否需要传条码
					needBankBarcodeMap=commonQueryDAO.isNeedBankBarcode(applyInfo.getDeptNo());
					String v_flag=(String) needBankBarcodeMap.get("p_flag");
					String v_message=(String) needBankBarcodeMap.get("p_message");
					if("1".equals(v_flag)){
						throw new RuntimeException("根据部门代码查询该银行是否需要传保全申请条码异常"+v_message);
					}else{
						String v_is_need=(String) needBankBarcodeMap.get("p_is_need");
						if("Y".equals(v_is_need)){
							// 需要条码的银行，银保通受理的保全条形码不能为空
							throw new RuntimeException("条形码为空");
						}else{
							//不需要条码的银行，银保通受理的需要生成银行条码
							barcode=generateBankbarcode(applyInfo.getServiceItems());
						}
					}
				}
				

			} else {
				// 非银保通受理的保全如果传入的条形码为空则重新生成
				barcode = generateBiaarcode(applyInfo.getServiceItems());
			}
		} else {
			posApplyFilesDto = commonQueryDAO.queryPosApplyfilesRecord(barcode);
			if (posApplyFilesDto != null) {
				throw new RuntimeException("条形码：" + barcode + "已经使用");
			}
			if ("12".equals(acceptChannelCode)) {
				// 如果是银保通受理的保全申请的条码类型要与保全申请类型
				if ("N".equals(commonQueryDAO.isMatchBarcodeNo(serviceItems,
						barcode))) {
					throw new RuntimeException("申请书条形码：" + barcode
							+ "与保全申请类型不匹配");
				}
			}
		}

		List<String> validPolicyNoList = branchQueryService
				.queryPolicyNoListByClientNo(clientNo);
		if (validPolicyNoList == null || validPolicyNoList.isEmpty()) {
			throw new RuntimeException("找不到该保单的客户");
		}
		if (!validPolicyNoList.contains(policyNo)) {
			throw new RuntimeException("申请保单不为客户作为投保人或被保人的保单");
		}

		PosApplyBatchDTO batch = new PosApplyBatchDTO();
		batch.setClientNo(clientNo);
		batch.setClientInfo(clientInfoList.get(0));
		batch.setAcceptChannelCode(acceptChannelCode);
		batch.setAcceptor(applyInfo.getAcceptor());
		batch.setCounterNo("905");
		batch.setApplyTypeCode(applyType);

		// 默认申请类型为亲办
		if (applyType==null||"".equals(applyType)||StringUtils.isBlank(applyType)){
			batch.setApplyTypeCode("1");
		}
		if ("2".equals(applyType) || "3".equals(applyType)
				|| "4".equals(applyType)) {
			batch.setRepresentor(applyInfo.getRepresentor());
			batch.setRepresentIdno(applyInfo.getRepresentIdno());
			batch.setIdType(applyInfo.getIdType());
		}

		List<PosApplyFilesDTO> files = new ArrayList<PosApplyFilesDTO>();
		PosApplyFilesDTO file = new PosApplyFilesDTO();
		file.setBarcodeNo(barcode);// 条形码
		file.setPolicyNo(policyNo);
		file.setApplyDate(applyInfo.getApplyDate());
		file.setForeignExchangeType("1");
		file.setServiceItems(applyInfo.getServiceItems());
		file.setPaymentType(paymentType);
		file.setAccountNoType(accountNoType);
		if (accountNoType == null || "".equals(accountNoType)) {

			file.setAccountNoType("1");
		}

		file.setTransferAccountOwnerIdType(idtype);// 证件类型
		file.setTransferAccountOwnerIdNo(idno);// 证件号码
		file.setTransferAccountOwner(clientName);// 收款人姓名
		file.setApprovalServiceType(approvalServiceType);
		if (file.getApplyDate() == null) {
			file.setApplyDate(commonQueryDAO.getSystemDate());
		}
		if (approvalServiceType==null||"".equals(approvalServiceType)||StringUtils.isBlank(approvalServiceType)) {
			file.setApprovalServiceType("1");// 自领
		}
		if ("2".equals(approvalServiceType)) {
			file.setAddress(applyInfo.getAddress());
			file.setZip(applyInfo.getZip());
		}
		if (StringUtils.isBlank(paymentType)) {
			file.setPaymentType("1");// 现金
		}
		//非银基通
		if ("2".equals(paymentType)&& !"19".equals(acceptChannelCode)) {
			String bankcode = commonQueryDAO
					.queryBankCodeByBanDdeptno(applyInfo.getBankCode());// 银行代码
			if ("".equals(bankcode) || bankcode==null){
				bankcode =applyInfo.getBankCode();
			}
			file.setBankCode(bankcode);
			file.setTransferAccountno(applyInfo.getTransferAccountno());
		}
		//银基通
		if ("2".equals(paymentType)&& "19".equals(acceptChannelCode)) {
			file.setBankCode(applyInfo.getBankCode());	// 银行代码
			file.setTransferAccountno(applyInfo.getTransferAccountno());
		}
		// 淘宝收付类型
		if ("12".equals(paymentType)) {
			file.setPaymentType("2");
			file.setBankCode(applyInfo.getBankCode());
			file.setTransferAccountno(applyInfo.getTransferAccountno());
			file.setAccountNoType("2");
		}
		//百度受理
		if ("2".equals(paymentType)&& "24".equals(acceptChannelCode)){
			Map bankMap=commonQueryDAO.getPolicyBankacctno(policyNo);
			String flag=(String) bankMap.get("flag");
			if("".equals(flag)||"1".equals(flag)||"2".equals(flag)){
				throw new RuntimeException("百度受理的保全取不到原缴费账户不能受理");
			}else{
				//银行是数据交互平台传过来的，账号和户名是取原缴费账号
				file.setPaymentType("2");
				file.setTransferAccountOwner((String)bankMap.get("acctName"));
				file.setBankCode(applyInfo.getBankCode());
				file.setTransferAccountno((String)bankMap.get("acctNo"));
			}
		}

		files.add(file);
		batch.setApplyFiles(files);

		// 生成受理
		branchAcceptService.generatePosInfoList(batch, false);
		logger.info("BiaPosAcceptInterfaceImpl->保单号"
				+ applyInfo.getPolicyNo() + "规则检查开始： started at "
				+ PosUtils.formatDate(commonQueryDAO.getSystemDate(),
						"yyyy-MM-dd HH:mm:ss:SSS"));
		// 校验是否有规则检查不通过的
		for (PosInfoDTO posInfo : batch.getPosInfoList()) {
			POSVerifyResultDto verifyResult = posInfo.getVerifyResult();
			if (verifyResult != null && verifyResult.getRuleInfos().size() != 0) {
				StringBuilder sb = new StringBuilder("保单号[");
				sb.append(posInfo.getPolicyNo());
				sb.append("]申请");
				sb.append(posInfo.getServiceItemsDesc());
				sb.append("项目受理规则检查不通过:");
				for (POSBOMRuleInfoDto ruleInfo : verifyResult.getRuleInfos()) {
					sb.append(ruleInfo.getDescription() + "；");
				}
				throw new RuntimeException(sb.toString());
			}
		}
		logger.info("BiaPosAcceptInterfaceImpl->保单号"
				+ applyInfo.getPolicyNo() + "规则检查结束： ended at "
				+ PosUtils.formatDate(commonQueryDAO.getSystemDate(),
						"yyyy-MM-dd HH:mm:ss:SSS"));
		// 生成捆绑顺序
		branchAcceptService.generateBindingOrder(batch);

		// 写受理
		batch = branchAcceptService.batchAccept(batch);

		// 写受理人工号到pos_accept_detail
		PosAcceptDetailDTO detail = new PosAcceptDetailDTO();
		detail.setPosNo(batch.getPosInfoList().get(0).getPosNo());
		detail.setPosObject("5");
		detail.setProcessType("1");
		detail.setItemNo("011");
		detail.setObjectValue(applyInfo.getAcceptor());
		detail.setNewValue(applyInfo.getAcceptor());
		detail.setOldValue(applyInfo.getAcceptor());
		acceptDAO.insertPosAcceptDetail(detail);
		// 如果是微信加保受理的写订单号到pos_accept_detail
		if ("16".equals(acceptChannelCode)&&applyInfo.getOrder_id()!=null&&"3".equals(applyInfo.getServiceItems())){
			detail.setPosNo(batch.getPosInfoList().get(0).getPosNo());
			detail.setPosObject("5");
			detail.setProcessType("1");
			detail.setItemNo("023");
			detail.setObjectValue(applyInfo.getPolicyNo());
			detail.setNewValue(applyInfo.getOrder_id());
			detail.setOldValue(applyInfo.getOrder_id());
			acceptDAO.insertPosAcceptDetail(detail);		
		}
		//如果是微信定投则写入未同步记录pos_accept_detail，待数据同步
		if ("16".equals(acceptChannelCode)&&"3".equals(applyInfo.getServiceItems())&&"Y".equals(applyInfo.getIs_weixin_auto_add_prem())){
			detail.setPosNo(batch.getPosInfoList().get(0).getPosNo());
			detail.setPosObject("2");
			detail.setProcessType("1");
			detail.setItemNo("269");
			detail.setObjectValue("1");
			detail.setNewValue("N");
			detail.setOldValue("");
			acceptDAO.insertPosAcceptDetail(detail);
		}
		// 如果是微信受理的传了是否关注微信号则写入pos_accept_detail
		if ("16".equals(acceptChannelCode)&&applyInfo.getIs_wechat_attention()!=null){
			detail.setPosNo(batch.getPosInfoList().get(0).getPosNo());
			detail.setPosObject("5");
			detail.setProcessType("1");
			detail.setItemNo("027");
			detail.setObjectValue(applyInfo.getPolicyNo());
			detail.setNewValue(applyInfo.getIs_wechat_attention());
			detail.setOldValue(applyInfo.getIs_wechat_attention());
			acceptDAO.insertPosAcceptDetail(detail);
		}
		//记录受理渠道的机构代码
		if(StringUtils.isNotBlank(applyInfo.getDeptNo()))
		{			
			detail.setPosNo(batch.getPosInfoList().get(0).getPosNo());
			detail.setPosObject("5");
			detail.setProcessType("1");
			detail.setItemNo("029");
			detail.setObjectValue(applyInfo.getPolicyNo());
			detail.setNewValue(applyInfo.getDeptNo());
			detail.setOldValue(applyInfo.getDeptNo());
			acceptDAO.insertPosAcceptDetail(detail);
			
		}
		return batch;
	}

	/**
	 * 保全受理，从申请书录入一直到生效的全部过程都在这里（传入Bean）
	 * 
	 * @param sii
	 * @param applyInfo
	 * @return
	 */
	public Map<String, Object> acceptInternal(ServiceItemsInputDTO sii1,
			final PosApplyInfoDTO applyInfo) {
		final ServiceItemsInputDTO sii = sii1;
		return transactionTemplate
				.execute(new TransactionCallback<Map<String, Object>>() {
					@Override
					public Map<String, Object> doInTransaction(
							TransactionStatus transactionstatus) {
						Map<String, Object> retMap = new HashMap<String, Object>();
						String SL_RSLT_MESG = null;
						String SL_RSLT_CODE = null;
						String APPROVE_TEXT = null;
						String BARCODE_NO= null;
						try {
							String policyNo = applyInfo.getPolicyNo();
							logger.info("BiaPosAcceptInterfaceImpl->保单号"
									+ policyNo + "受理开始： started at "
									+ PosUtils.formatDate(commonQueryDAO.getSystemDate(),
											"yyyy-MM-dd HH:mm:ss:SSS"));
							PosApplyBatchDTO batch = createBatch(applyInfo);
							logger.info("BiaPosAcceptInterfaceImpl->保单号"
									+ policyNo + "受理结束： ended at "
									+ PosUtils.formatDate(commonQueryDAO.getSystemDate(),
											"yyyy-MM-dd HH:mm:ss:SSS"));
							PosInfoDTO posInfo = new PosInfoDTO();
							String posNo = batch.getPosInfoList().get(0)
									.getPosNo();

							sii.setPosNo(posNo);
							sii.setPosNoList(null);
							sii.setBarcodeNo(batch.getApplyFiles().get(0)
									.getBarcodeNo());
							sii.setBatchNo(batch.getPosBatchNo());
							ServiceItemsInputDTO oriItemsInputDTO = branchQueryService
									.queryAcceptDetailInputByPosNo(posNo);
							sii.setOriginServiceItemsInputDTO(oriItemsInputDTO
									.getOriginServiceItemsInputDTO());
							sii.setClientNo(applyInfo.getClientNo());

							// 写受理明细
							branchAcceptService.acceptDetailInput(sii);
							List<String> posNoList = sii.getPosNoList();
							for (int i = 0; i < posNoList.size(); i++) {
								posNo = posNoList.get(i);

								// 更新备注字段
								branchAcceptService.updatePosInfo(posNo,
										"remark", sii.getRemark());

								// 写状态变迁记录
								PosStatusChangeHistoryDTO statusChange = new PosStatusChangeHistoryDTO();
								statusChange.setPosNo(posNo);
								statusChange.setOldStatusCode("A01");
								statusChange.setNewStatusCode("A03");
								statusChange.setChangeDate(new Date());
								statusChange.setChangeUser(batch.getAcceptor());
								branchAcceptService
										.insertPosStatusChangeHistory(statusChange);
								if (!"0".equals(statusChange.getFlag())) {
									SL_RSLT_CODE = "999002";
									SL_RSLT_MESG = "写保全状态变更记录失败："
											+ statusChange.getMessage();
									branchAcceptService.cancelAccept(posInfo,
											batch.getAcceptor());

								}

								// 这些渠道都是逐单受理，因此前一单受理完成，更新状态为 A03待处理规则检查
								branchAcceptService.updatePosInfo(posNo,
										"accept_status_code", "A03");

								// 调用流转接口
								logger.info("BiaPosAcceptInterfaceImpl->保全号"
										+ posNo + "保单号：" + policyNo + "流转接口调用开始： started at "
										+ PosUtils.formatDate(commonQueryDAO.getSystemDate(),
												"yyyy-MM-dd HH:mm:ss:SSS"));
								retMap = branchAcceptService.workflowControl(
										"03", posNo, false);
								logger.info("BiaPosAcceptInterfaceImpl->保全号"
										+ posNo  + "保单号：" + policyNo + "流转接口调用结束： ended at "
										+ PosUtils.formatDate(commonQueryDAO.getSystemDate(),
												"yyyy-MM-dd HH:mm:ss:SSS"));
								logger.info("acceptDetailInputSubmit workflowControl return:"
										+ retMap);
								String flag = (String) retMap.get("flag");
								String msg = (String) retMap.get("message");
								String resultDesc = (String) retMap
										.get("resultDesc");
								retMap = branchQueryService
										.queryProcessResult(posNo);
								if ("C01".equals(flag)) {
									// 规则检查不通过
									SL_RSLT_CODE = "999C01";
									SL_RSLT_MESG = msg;
									// throw new RuntimeException(msg);
								} else if ("A19".equals(flag)) {
									// 待审批判断处理
									retMap = branchAcceptService
											.workflowControl("05", posNo, false);
									logger.info("acceptDetailInputSubmit workflowControl return:"
											+ retMap);
									flag = (String) retMap.get("flag");
									if ("A15".equals(flag)) {
										// 待收费
										posInfo = commonQueryDAO
												.queryPosInfoRecord(posNo);
										APPROVE_TEXT = posInfo.getApproveText();// 批文
										BARCODE_NO =posInfo.getBarcodeNo();//条码
										SL_RSLT_CODE = "999999";
										SL_RSLT_MESG = "待收费";
									} else if ("A20".equals(flag)) {
										// 生效完成
										posInfo = commonQueryDAO
												.queryPosInfoRecord(posNo);
										APPROVE_TEXT = posInfo.getApproveText();// 批文
										BARCODE_NO =posInfo.getBarcodeNo();//条码
										SL_RSLT_CODE = "999999";
										SL_RSLT_MESG = "待对账处理（待收付费处理）";
									} else if ("A16".equals(flag)) {
										// 生效完成
										posInfo = commonQueryDAO
												.queryPosInfoRecord(posNo);
										APPROVE_TEXT = posInfo.getApproveText();// 批文
										BARCODE_NO =posInfo.getBarcodeNo();//条码
										SL_RSLT_CODE = "999999";
										SL_RSLT_MESG = "待生效确认";
									} else if ("E01".equals(flag)) {
										// 生效完成
										posInfo = commonQueryDAO
												.queryPosInfoRecord(posNo);
										APPROVE_TEXT = posInfo.getApproveText();// 批文
										BARCODE_NO =posInfo.getBarcodeNo();//条码
										// 淘宝渠道受理的保全生效完成后给客户发送电子批单，如果发送失败也正常生效保全
										try{
											boolean isTaobaoPolicy = posRulesDAO
												.isTaobaoPolicy(policyNo);
										    if (isTaobaoPolicy) {
											     PolicyContactInfoDTO policyContactInfoDTO = serviceItemsDAO_20
												 .queryPolicyContactInfo(policyNo);
											     UserInfo UserInfo = new UserInfo();
											     String email = policyContactInfoDTO.getEmailDesc();
											     if (!StringUtils.isBlank(email)) {
												     UserInfo.setEmail(email);
												     handPDFEnt(posNo, UserInfo);
											        }
										       }
										    }catch (Exception e){
											   logger.error(e);
										    }
										SL_RSLT_CODE = "999999";
										SL_RSLT_MESG = "已生效";

									} else if ("A12".equals(flag)) {
										SL_RSLT_CODE = "999002";
										SL_RSLT_MESG = "该单需要人工核保，不能再银行受理";
										branchAcceptService.cancelAccept(
												posInfo, batch.getAcceptor());
									} else {
										SL_RSLT_CODE = "999002";
										SL_RSLT_MESG = flag;
										branchAcceptService.cancelAccept(
												posInfo, batch.getAcceptor());
									}
								} else if ("A03".equals(flag)) {
									SL_RSLT_CODE = "999002";
									SL_RSLT_MESG = resultDesc;
									branchAcceptService.cancelAccept(posInfo,
											batch.getAcceptor());
								}
							}

						} catch (Exception e) {

							logger.error(e);

							SL_RSLT_CODE = "999002";
							SL_RSLT_MESG = e.getMessage();
							transactionstatus.setRollbackOnly();
						}
						retMap.put("SL_RSLT_MESG", SL_RSLT_MESG);
						retMap.put("SL_RSLT_CODE", SL_RSLT_CODE);
						retMap.put("APPROVE_TEXT", APPROVE_TEXT);
						retMap.put("BARCODE_NO", BARCODE_NO);
						return retMap;
					}
				});
	}

	/**
	 * 保全受理，从申请书录入一直到生效的全部过程都在这里(传入Map形式)
	 * 
	 * @param sii
	 * @param applyInfo
	 * @return
	 */
	public Map<String, Object> acceptInternalToBia(Map siiMap,
			final Map applyInfoMap) throws Exception {
		ServiceItemsInputDTO sii = new ServiceItemsInputDTO();
		PosApplyInfoDTO applyInfo = new PosApplyInfoDTO();
		MapToBean(sii, siiMap);
		MapToBean(applyInfo, applyInfoMap);
		return acceptInternal(sii, applyInfo);
	}

	/**
	 * 将javaBean转换成Map
	 * 
	 * @param javaBean
	 *            javaBean
	 * @return Map对象
	 */
	public Map BeanToMap(Object javaBean) {
		Map result = new HashMap();
		Method[] methods = javaBean.getClass().getDeclaredMethods();

		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			try {
				if (method.getName().startsWith("get")) {
					String field = method.getName();
					field = field.substring(field.indexOf("get") + 3);
					field = field.toLowerCase().charAt(0) + field.substring(1);

					Object value = method.invoke(javaBean, (Object[]) null);
					if (value instanceof List) {
						List list = new ArrayList();
						List list1 = (List) value;
						for (int j = 0; j < list1.size(); j++) {
							Object obj = list1.get(j);
							String keyName = obj.getClass().getName();
							// keyName=keyName.substring(keyName.lastIndexOf(".")+1);
							Map map = BeanToMap(obj);
							Map map1 = new HashMap();
							map1.put(keyName, map);
							list.add(map1);
						}
						result.put(field.toUpperCase(),
								null == list ? new ArrayList() : list);
					} else {
						result.put(field.toUpperCase(), null == value ? ""
								: value.toString());
					}
				}
			} catch (Exception e) {
			}
		}

		return result;
	}

	/**
	 * 将map转换成Javabean
	 * 
	 * @param javabean
	 *            javaBean
	 * @param data
	 *            map数据
	 */
	public Object MapToBean(Object javabean, Map data) {
		Method[] methods = javabean.getClass().getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			try {
				if (method.getName().startsWith("set")) {
					String field = method.getName();
					field = field.substring(field.indexOf("set") + 3);
					field = field.toLowerCase().charAt(0) + field.substring(1);
					Object value = data.get(field.toUpperCase());
					if (value instanceof List) {
						List list = new ArrayList();
						List list1 = (List) value;
						for (int j = 0; j < list1.size(); j++) {
							Map map = (Map) list1.get(j);
							Set s = map.keySet();
							for (Iterator iter = s.iterator(); iter.hasNext();) {
								String beanName = (String) iter.next();
								Object obj = Class.forName(beanName)
										.newInstance();
								Map dat = (Map) map.get(beanName);
								MapToBean(obj, dat);
								list.add(obj);
							}
						}
						method.invoke(javabean, new Object[] { list });
					} else {
						method.invoke(javabean,
								new Object[] { data.get(field.toUpperCase()) });
					}
				}
			} catch (Exception e) {
			}
		}
		return javabean;
	}

	/**
	 * 续期交费方式变更经办信息录入
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtraToBia_23(Map map) {
		String client_no = commonQueryDAO.getApplicantByPolicyNo((String) map
				.get("POLICYNO"));
		ServiceItemsInputDTO_23 items23 = new ServiceItemsInputDTO_23();
		ClientAccountDTO clientAcount = new ClientAccountDTO();
		String branch_code = (String) map.get("BRANCH_CODE");// 网点代码
		String bankcode = commonQueryDAO.queryBankCodeByBanDdeptno(branch_code);// 银行代码
		clientAcount.setBankCode(bankcode);
		clientAcount.setAccountNo((String) map.get("CLIENTACCOUNT"));
		clientAcount.setClientAccount((String) map.get("CLIENTACCOUNT"));
		clientAcount.setAccountStatus((String) map.get("ACCOUNTSTATUS"));
		clientAcount.setClientNo(client_no);
		items23.setAccount(clientAcount);
		items23.setClientNo(client_no);
		items23.setPolicyNo((String) map.get("POLICYNO"));
		items23.setChargingMethod((String) map.get("CHARGINGMETHOD"));
		items23.setServiceItems("23");
		System.out.println(clientAcount.getAccountNo());
		System.out.println(items23.getPolicyNo());
		return serviceItemsDAO_23.queryServiceItemsInfoExtraToBia_23(items23);
	}

	/**
	 * 退保经办信息录入
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtraToBia_2(Map map) {
		boolean ishaveprd = false;
		String policyNo = (String) map.get("POLICYNO");
		String isAllSurrender = (String) map.get("ISALLSURRENDER");
		ServiceItemsInputDTO_2 items2 = new ServiceItemsInputDTO_2();
		items2.setPolicyNo(policyNo);
		items2.setServiceItems("2");
		items2.setApplyDate((String) map.get("APPLYDATE"));
		items2 = (ServiceItemsInputDTO_2) serviceItemsDAO_2
				.queryServiceItemsInfoExtraToBia_2(items2);
		// 是否整单退保
		if ("Y".equals(isAllSurrender)) {
			items2.setAllSurrender(true);
		} else {
			items2.setAllSurrender(false);
		}
		// 是否可疑交易
		items2.setDouBt(false);
		// 退保原因
		items2.setSurrenderCause((String) map.get("SURRENDERCAUSECODE"));
		List<PolicyProductDTO> productList = items2.getProductList();
		for (int i = 0; productList != null && i < productList.size(); i++) {
			PolicyProductDTO product = productList.get(i);
			// 险种为有效 或者 失效原因为[10, 7, 9, 8, 13, 11, 12] 才能退保
			if ("1".equals(product.getDutyStatus())
					|| ("2".equals(product.getDutyStatus())
							&& ("7".equals(product.getLapseReason()))
							|| "8".equals(product.getLapseReason())
							|| "9".equals(product.getLapseReason())
							|| "10".equals(product.getLapseReason())
							|| "11".equals(product.getLapseReason())
							|| "12".equals(product.getLapseReason()) || "13"
							.equals(product.getLapseReason()))) {
				product.setSelected(true);
				ishaveprd = true;
			}

		}
		if (!ishaveprd) {
			throw new RuntimeException("该保单没可操作该保全项目的险种");
		}
		items2.setProductList(productList);
		return items2;
	}

	/**
	 * 犹豫期退保经办信息录入
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtraToBia_1(Map map) {
		String policyNo = (String) map.get("POLICYNO");
		String isAllSurrender = (String) map.get("ISALLSURRENDER");
		ServiceItemsInputDTO_1 items1 = new ServiceItemsInputDTO_1();
		boolean ishaveprd = false;
		items1.setPolicyNo(policyNo);
		items1.setServiceItems("1");
		items1.setApplyDate((String) map.get("APPLYDATE"));
		items1 = (ServiceItemsInputDTO_1) serviceItemsDAO_1
				.queryServiceItemsInfoExtraToBia_1(items1);
		// 是否可疑交易
		items1.setDouBt(false);
		// 退保原因
		items1.setSurrenderCauseCode((String) map.get("SURRENDERCAUSECODE"));
		List<PolicyProductDTO> productList = items1.getPolicyProductList();
		for (int i = 0; productList != null && i < productList.size(); i++) {
			PolicyProductDTO product = productList.get(i);
			if ("1".equals(product.getDutyStatus())) {
				product.setSelected(true);
				ishaveprd = true;
			}
		}
		if (!ishaveprd) {
			throw new RuntimeException("该保单没可操作该保全项目的险种");
		}
		items1.setPolicyProductList(productList);
		return items1;
	}

	/**
	 * 追加保费经办信息录入
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtraToBia_35(Map map) {
		ServiceItemsInputDTO_35 items35 = new ServiceItemsInputDTO_35();
		String policyNo = (String) map.get("POLICYNO");
		// 追加保费金额
		String addPremAmount = (String) map.get("ADDPREMAMOUNT");
		items35.setPolicyNo(policyNo);
		items35.setServiceItems("35");
		items35 = (ServiceItemsInputDTO_35) serviceItemsDAO_35
				.queryServiceItemsInfoExtraToBia_35(items35);
		items35.setAddPremAmount(new BigDecimal(addPremAmount));// 追加保费金额
		return items35;
	}
	/**
	 * 加保经办信息录入
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtraToBia_3(Map map) {
		ServiceItemsInputDTO_3 items3 = new ServiceItemsInputDTO_3();
		String policyNo = (String) map.get("POLICYNO");
		String prod_seq = (String) map.get("PROD_SEQ");
		// 增加总金额	PREMSUM、
		String addPremSum=(String) map.get("PREMSUM");
		// 红包金额	BONUS_PREM
		String addBonusPrem=(String) map.get("BONUS_PREM");
		// 自主缴费金额	 PAY_PREM
		String addPayPrem = (String) map.get("PAY_PREM");
		// 用户红包金额	 USER_BONUS_PREM
		String userBounsPrem = (String) map.get("USER_BONUS_PREM");
		items3.setPolicyNo(policyNo);
		items3.setServiceItems("3");
		items3 = (ServiceItemsInputDTO_3) serviceItemsDAO_3
				.queryServiceItemsInfoExtraToBia_3(items3);	
		List<PolicyProductDTO> productList = items3.getPolicyProductList();
		for (int i = 0; productList != null && i < productList.size(); i++) {
			PolicyProductDTO product = productList.get(i);
			//生命成长红包年金保险
			if ("CEAN_AN1".equals(product.getProductCode())&& prod_seq.equals(product.getProdSeq()))
				product.setSelected(true);
			    product.setAddPremSum(new BigDecimal(addPremSum));
			    product.setAddBonusPrem(new BigDecimal(addBonusPrem));
			    product.setAddPayPrem(new BigDecimal(addPayPrem));
			    product.setUserBounsPrem(new BigDecimal(userBounsPrem));
			}
	    items3.setPolicyProductList(productList);
		return items3;
	}
	/**
	 * 满期金给付经办信息录入
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtraToBia_10(Map map) {
		ServiceItemsInputDTO_10 items10 = new ServiceItemsInputDTO_10();
		String policyNo = (String) map.get("POLICYNO");
		items10.setPolicyNo(policyNo);
		items10.setServiceItems("10");
		items10.setApplyDate((String) map.get("APPLYDATE"));
		items10 = (ServiceItemsInputDTO_10) serviceItemsDAO_10
				.queryServiceItemsInfoExtraToBia_10(items10);
		if (items10.getSurvivalDueList().size() == 0) {
			throw new RuntimeException("该单无满期金");
		}
		return items10;
	}

	/**
	 * 部分领取经办信息录入
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtraToBia_37(Map map) {
		ServiceItemsInputDTO_37 items37 = new ServiceItemsInputDTO_37();
		String policyNo = (String) map.get("POLICYNO");
		items37.setPolicyNo(policyNo);
		items37.setServiceItems("37");
		items37.setApplyDate((String) map.get("APPLYDATE"));
		items37 = (ServiceItemsInputDTO_37) serviceItemsDAO_37
				.queryServiceItemsInfoExtra_37(items37);
		// 部分领取金额
		if(!"Y".equals(items37.getUnitLinkedFlag())){
			String withdrawAmount = (String) map.get("WITHDRAWAMOUNT");
			items37.setWithdrawAmount(new BigDecimal(withdrawAmount));
		}else{
			List witList=(ArrayList)map.get("FINANCIAL_WITHDRAW_LIST");
			List<FinancialProductsDTO> financialProductsList =items37.getFinancialProductsList();
			for (int i = 0; financialProductsList != null && i < financialProductsList.size(); i++){
				FinancialProductsDTO finDto=financialProductsList.get(i);
				for (int j = 0; witList != null && j < witList.size(); j++){
					Map listMap=(Map)witList.get(j);
					Map withMap=(Map)listMap.get("FINANCIAL_WITHDRAW");
					String financialProduct=(String)withMap.get("FINANCIALPRODUCTS");
					String withUnits=(String)withMap.get("WITHDRAWUNITS");
					BigDecimal withdrawUnits=new BigDecimal(withUnits);
					if(financialProduct.equals(finDto.getFinancialProducts())&& withdrawUnits.doubleValue() > 0){
						finDto.setWithdrawUnits(withdrawUnits);
					}
				}
			}
			items37.setFinancialProductsList(financialProductsList);
		}

		return items37;
	}

	/*********************
	 * 写保银保通对账表
	 * 
	 * @param materialsDTO
	 */
	public Map insertPosBiaCheck(Map map) {
		String SL_RSLT_MESG = null;
		String SL_RSLT_CODE = null;
		PosBiaCheckDto biaCheckDto = new PosBiaCheckDto();
		try {
			String policy_no = (String) map.get("POLICY_NO"); // 保单号
			String pos_no = (String) map.get("POS_NO"); // 保全号
			String service_items = (String) map.get("SERVICE_IREMS"); // 保全项目
			Date accept_date = DateUtil.stringToDate((String) map
					.get("ACCEPT_DATE"));// 受理时间
			String barcode_no = (String) map.get("BARCODE_NO"); // 条形码
			Double totel_amount = Double
					.valueOf(map.get("PREM_SUM") == null ? "0" : (String) map
							.get("PREM_SUM"));// 收付总额
			String prem_flag = (String) map.get("PREM_FLAG"); // 收付标志
			biaCheckDto.setPolicy_no(policy_no);
			biaCheckDto.setPos_no(pos_no);
			biaCheckDto.setService_items(service_items);
			biaCheckDto.setAccept_date(accept_date);
			biaCheckDto.setBarcode_no(barcode_no);
			biaCheckDto.setTotel_amount(totel_amount);
			biaCheckDto.setPrem_flag(prem_flag);
			acceptDAO.insertPosBiaCheck(biaCheckDto);
			SL_RSLT_CODE = "999999";
			SL_RSLT_MESG = "写银保通对账表成功";
		} catch (Exception e) {
			SL_RSLT_CODE = "999002";
			SL_RSLT_MESG = "写银保通对账表失败" + e.getMessage();
		}
		map.put("SL_RSLT_MESG", SL_RSLT_MESG);
		map.put("SL_RSLT_CODE", SL_RSLT_CODE);
		return map;
	}

	/**
	 * 保全受理，从申请书录入一直到生效的全部过程都在这里(传入Map形式)
	 * 
	 * @param sii
	 * @param applyInfo
	 * @return
	 */
	public Map handleBiaRequest(Map map) {
		String SL_RSLT_MESG = null;
		String SL_RSLT_CODE = null;
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map posMap = new HashMap();
		retMap.putAll(map);
		try {
			String trancode = (String) map.get("TRAN_CODE");
			PosApplyInfoDTO applyInfo = new PosApplyInfoDTO();
			ServiceItemsInputDTO sii = new ServiceItemsInputDTO();
			MapToBean(applyInfo, (Map) map.get("POSAPPLYINFO"));
			applyInfo.setApplyDate(DateUtil.stringToDate((String) map
					.get("APPLYDATE")));
			applyInfo.setClientNo(commonQueryDAO
					.getApplicantByPolicyNo(applyInfo.getPolicyNo()));
			String productCode = commonQueryDAO
					.queryProductCodeOfPrimaryPlanByPolicyNo(applyInfo
							.getPolicyNo());
			if ("".equals(productCode) || productCode == null) {
				SL_RSLT_CODE = "999002";
				SL_RSLT_MESG = "传入的保单号无效";
				retMap.put("SL_RSLT_MESG", SL_RSLT_MESG);
				retMap.put("SL_RSLT_CODE", SL_RSLT_CODE);
				return retMap;
			}
			if ("SurrAllReal".equals(trancode)
					|| "PartWithdraw".equals(trancode)) {
				String finPosNo = commonQueryDAO.queryPosNoByAccountNo(
						applyInfo.getTransferAccountno(),
						applyInfo.getPolicyNo());
				if (finPosNo != null) {
					retMap.put("SL_RSLT_MESG", "该订单号已经完成");
					retMap.put("SL_RSLT_CODE", "999999");
					return retMap;
				}
			}
			// 查询犹豫期截止日
			Map periodMap = commonQueryDAO.querypCruplePeriodPolicy(applyInfo
					.getPolicyNo());
			String periodflag = (String) periodMap.get("p_flag");
			if ("1".equals(periodflag)) {
				SL_RSLT_CODE = "999002";
				SL_RSLT_MESG = (String) periodMap.get("p_message");
				retMap.put("SL_RSLT_MESG", SL_RSLT_MESG);
				retMap.put("SL_RSLT_CODE", SL_RSLT_CODE);
				return retMap;
			}
			Date scruple_date = (Date) periodMap.get("p_scruple_date");
			if (scruple_date == null) {
				SL_RSLT_CODE = "999002";
				SL_RSLT_MESG = "计算犹豫期出错：犹豫期为空";
				retMap.put("SL_RSLT_MESG", SL_RSLT_MESG);
				retMap.put("SL_RSLT_CODE", SL_RSLT_CODE);
				return retMap;
			}
			// 交费方式变更(建行签约)
			if ("pos_accept_23".equals(trancode)) {
				sii = queryServiceItemsInfoExtraToBia_23(map);
				sii.setApplyDate((String) map.get("APPLYDATE"));
				sii.setAcceptDate((String) map.get("BK_ACCT_DATE"));
				posMap = acceptInternal(sii, applyInfo);
			}
			// 交费方式变更(建行解约)
			else if ("pos_accept_23_t".equals(trancode)) {
				sii = queryServiceItemsInfoExtraToBia_23(map);
				sii.setApplyDate((String) map.get("APPLYDATE"));
				sii.setAcceptDate((String) map.get("BK_ACCT_DATE"));
				posMap = acceptInternal(sii, applyInfo);
			}
			// 退保
			else if ("SurrReal".equals(trancode)
					|| ("SurrAllReal".equals(trancode) && !scruple_date.after(applyInfo.getApplyDate()))
					|| ("SurrAllRealII".equals(trancode) && !scruple_date.after(applyInfo.getApplyDate()))
					|| ("YjtSurrAllReal".equals(trancode) && !scruple_date.after(applyInfo.getApplyDate()))) {
				sii = queryServiceItemsInfoExtraToBia_2(map);
				//查询是否需要进行金额校验
				String checkMoney=(String) map.get("CHECK_MONEY");
				//如果是退保并且checkMoney为Y（需要校验金额）则需要校验金额是否一致
				//如果是银行自助终端受理的需要判断金额是否查过20W
				  if (("SurrAllReal".equals(trancode) && !scruple_date.after(applyInfo.getApplyDate())&&"Y".equals(checkMoney))
					  ||"18".equals(applyInfo.getAcceptChannelCode())){
					  //校验金额
					  ServiceItemsInputDTO_2 sii2=(ServiceItemsInputDTO_2)sii;
					  List<PolicyProductDTO> productList =sii2.getProductList();
					  double cashvalueSum=0;
					for (int i = 0; productList != null && i < productList.size(); i++) {
							PolicyProductDTO product = productList.get(i);
							if (product.isSelected()){
							cashvalueSum=cashvalueSum + commonQueryDAO.getProductPremSum(product.getPolicyNo(),product.getProdSeq().toString(), applyInfo.getApplyDate()).doubleValue();
						}							
					}
					if("18".equals(applyInfo.getAcceptChannelCode())){
						//如果是银行自助终端受理的需要判断金额是否查过20W
                        if(cashvalueSum>200000){
                        	  SL_RSLT_CODE = "999004";
                        	  SL_RSLT_MESG = "退保金额:"+cashvalueSum+"超过200000，不能操作退保";
                        }
					}
				 if("SurrAllReal".equals(trancode) && !scruple_date.after(applyInfo.getApplyDate())&&"Y".equals(checkMoney)){
						//如果是退保并且checkMoney为Y（需要校验金额）则需要校验金额是否一致
						String vailableMoney=(String) map.get("AvailableMoney");
						if(cashvalueSum !=Double.parseDouble(vailableMoney)){
						    SL_RSLT_CODE = "999004";
							SL_RSLT_MESG = "传入金额:"+vailableMoney+"和实际退保金额:"+cashvalueSum+"不一致，不能操作退保";
							retMap.put("SL_RSLT_MESG", SL_RSLT_MESG);
							retMap.put("SL_RSLT_CODE", SL_RSLT_CODE);
							return retMap;
						} 
				 }
				}
				//百度退保，传入邮箱则记录
				if("SurrAllRealII".equals(trancode) && !scruple_date.after(applyInfo.getApplyDate())&&"24".equals(applyInfo.getAcceptChannelCode())){
					  //记录传入邮箱
					  if(map.get("EMAIL")!=null){
					    sii.setWebEmail((String) map.get("EMAIL")); 
					 }
				}
				sii.setApplyDate((String) map.get("APPLYDATE"));
				sii.setAcceptDate((String) map.get("BK_ACCT_DATE"));
				applyInfo.setServiceItems("2");
				posMap = acceptInternal(sii, applyInfo);
			}
			// 犹豫期退保
			else if ("HesiSurrReal".equals(trancode)
					|| ("SurrAllReal".equals(trancode) && scruple_date.after(applyInfo.getApplyDate()))
					|| ("SurrAllRealII".equals(trancode) && scruple_date.after(applyInfo.getApplyDate()))
					|| ("YjtSurrAllReal".equals(trancode) && scruple_date.after(applyInfo.getApplyDate()))) {
				sii = queryServiceItemsInfoExtraToBia_1(map);
				sii.setApplyDate((String) map.get("APPLYDATE"));
				sii.setAcceptDate((String) map.get("BK_ACCT_DATE"));
				//百度契撤，传入邮箱则记录
				if("SurrAllRealII".equals(trancode) && scruple_date.after(applyInfo.getApplyDate())&&"24".equals(applyInfo.getAcceptChannelCode())){
					  //记录传入邮箱
					  if(map.get("EMAIL")!=null){
					    sii.setWebEmail((String) map.get("EMAIL")); 
					 }
				}
				applyInfo.setServiceItems("1");
				posMap = acceptInternal(sii, applyInfo);
			}
			// 追加保费
			else if ("AddPolicyFee".equals(trancode)) {
				sii = queryServiceItemsInfoExtraToBia_35(map);
				sii.setApplyDate((String) map.get("APPLYDATE"));
				sii.setAcceptDate((String) map.get("BK_ACCT_DATE"));
				posMap = acceptInternal(sii, applyInfo);
			}
			// 满期给付
			else if ("MaturePay".equals(trancode)) {
				sii = queryServiceItemsInfoExtraToBia_10(map);
				sii.setApplyDate((String) map.get("APPLYDATE"));
				sii.setAcceptDate((String) map.get("BK_ACCT_DATE"));
				posMap = acceptInternal(sii, applyInfo);
			}
			// 部分领取
			else if ("PartWithdraw".equals(trancode)) {
				sii = queryServiceItemsInfoExtraToBia_37(map);
				sii.setApplyDate((String) map.get("APPLYDATE"));
				sii.setAcceptDate((String) map.get("BK_ACCT_DATE"));
				posMap = acceptInternal(sii, applyInfo);
			}
			// 加保AddPolicyAmount 正常加保，AutoPolicyAmountIn定投加保
			else if ("AddPolicyAmount".equals(trancode)
					|| "AutoPolicyAmountIn".equals(trancode)) {
				//根据保单号和订单号查询该保单的订单是否已经加保交易完成
				String isDeal=commonQueryDAO.checkOrderNoIsDeal(applyInfo.getPolicyNo(), applyInfo.getOrder_id());
				if("Y".equals(isDeal)){
					SL_RSLT_CODE = "999003";
					SL_RSLT_MESG = "该保单的加保订单已经交易完成，请核实！";
					retMap.put("SL_RSLT_MESG", SL_RSLT_MESG);
					retMap.put("SL_RSLT_CODE", SL_RSLT_CODE);
				}else{
				// 根据保单号和订单号查询该保单的订单是否处于待收费状态
			      String isFinstuas=commonQueryDAO.checkOrderNoIsFinStaus(applyInfo.getPolicyNo(), applyInfo.getOrder_id());
				  if ("N".equals(isFinstuas)){
					  sii = queryServiceItemsInfoExtraToBia_3(map);
					  sii.setApplyDate((String) map.get("APPLYDATE"));
					  sii.setAcceptDate((String) map.get("BK_ACCT_DATE"));
					  //记录传入邮箱
					  if(map.get("EMAIL")!=null){
					    sii.setWebEmail((String) map.get("EMAIL")); 
					  }
					  posMap = acceptInternal(sii, applyInfo);
				  }else{
					// 根据保单号和订单号查询保全号
					  String posNo=commonQueryDAO.queryFinStausPosNo(applyInfo.getPolicyNo(), applyInfo.getOrder_id());
					  SL_RSLT_CODE = "999999";
					  SL_RSLT_MESG = "微信加保待收费";
					  retMap.put("SL_RSLT_MESG", SL_RSLT_MESG);
					  retMap.put("SL_RSLT_CODE", SL_RSLT_CODE);
					  retMap.put("posNo", posNo);
				  }
					  
			  }
			}
			// 冲正：HesiSurrRealCancel犹豫期退保冲正；SurrRealCancel退保冲正；MaturePayCancel满期给付冲正；AddPolicyFeeCancel追加保费冲正
			else if ("HesiSurrRealCancel".equals(trancode)
					|| "SurrRealCancel".equals(trancode)
					|| "MaturePayCancel".equals(trancode)
					|| "AddPolicyFeeCancel".equals(trancode)) {
				posMap = rollbackAccept(map);
			}
			// 查询:MaturePayQuery犹豫期退保查询；SurrRealQuery退保查询；MaturePayQuery满期给付查询；
			// AddPolicyFeeQuery追加查询;PartWithdrawQuery部分领取查询;SurrAllRealQuery
			// 淘宝犹豫期退保和退保
			else if ("HesiSurrRealQuery".equals(trancode)
					|| "SurrRealQuery".equals(trancode)
					|| "MaturePayQuery".equals(trancode)
					|| "AddPolicyFeeQuery".equals(trancode)
					|| "PartWithdrawQuery".endsWith(trancode)
					|| "SurrAllRealQueryII".endsWith(trancode)
					|| "SurrAllRealQuery".endsWith(trancode)) {
				if ("SurrAllRealQuery".equals(trancode)|| "SurrAllRealQueryII".endsWith(trancode)) {
					if (!scruple_date.after(applyInfo.getApplyDate())) {
						// 淘宝退保
						map.put("SERVICEITEMS", "2");
					} else {
						// 淘宝犹豫期退保
						map.put("SERVICEITEMS", "1"); 
					}
				}
				posMap = queryInputserviceItems(map);
			} else if ("EndOfDay".equals(trancode)) {
				posMap = insertPosBiaCheck(map);

			} else if ("BatchRecv".equals(trancode)) { 
				posMap = insertJbzSinolife(map);

			} else {
				SL_RSLT_CODE = "999002";
				SL_RSLT_MESG = "该交易申请不能支持，请核实！";
				retMap.put("SL_RSLT_MESG", SL_RSLT_MESG);
				retMap.put("SL_RSLT_CODE", SL_RSLT_CODE);
			}
			retMap.putAll(posMap);
		} catch (Exception e) {
			SL_RSLT_CODE = "999002";
			SL_RSLT_MESG = e.getMessage();
			retMap.put("SL_RSLT_MESG", SL_RSLT_MESG);
			retMap.put("SL_RSLT_CODE", SL_RSLT_CODE);
		}

		return retMap;
	}

	/**
	 * 功能说明: 银保通写入待金标准发回数据
	 * 
	 * @param map
	 */
	public Map insertJbzSinolife(Map mapList) {

		final Map map = mapList;
		return transactionTemplate
				.execute(new TransactionCallback<Map<String, Object>>() {
					@Override
					public Map<String, Object> doInTransaction(
							TransactionStatus transactionStatus) {
						Map<String, Object> retMap = new HashMap<String, Object>();
						try {
							Map<String, Object> rowmap = new HashMap<String, Object>();

							List ls = (List) map.get("ROW_LIST");
							for (int i = 0; i < ls.size(); i++) {
								rowmap = (Map) ((Map) ls.get(i)).get("ROW");
								Date data_send_date = DateUtil
										.stringToDate((String) rowmap
												.get("sendJtoS"));// 数据发送日期
								String policy_no = (String) rowmap
										.get("policyNo");// 保单号
								Date last_examination_date = DateUtil
										.stringToDate((String) rowmap
												.get("lastPe"));// 末次体检日期
								Double examination_time = Double
										.valueOf((String) rowmap
												.get("alreadyPe"));// 已体检次数

								String booking_status;// 预约状态
								if ("已预约".equals((String) rowmap
										.get("appointmentStatus"))
										|| "变更预约".equals((String) rowmap
												.get("appointmentStatus"))) {
									booking_status = "Y";

								} else {
									booking_status = "N";
								}

								Date booking_date = DateUtil
										.stringToDate((String) rowmap
												.get("callAppDate"));// 预约或取消预约日期
								acceptDAO.insertJbzSinolife(data_send_date,
										policy_no, last_examination_date,
										examination_time, booking_status,
										booking_date);

							}
							retMap.put("SL_RSLT_MESG", "银保通写入待金标准发回数据成功");
							// retMap.put("SL_RSLT_CODE", SL_RSLT_CODE);

							return retMap;
						}

						catch (RuntimeException re) {

							transactionStatus.setRollbackOnly();
							retMap.put("SL_RSLT_MESG", "银保通写入待金标准发回数据失败");

							return retMap;
						} catch (Exception e) {

							transactionStatus.setRollbackOnly();
							retMap.put("SL_RSLT_MESG", "银保通写入待金标准发回数据失败");
							return retMap;
						}
					}

				});
	}
}
