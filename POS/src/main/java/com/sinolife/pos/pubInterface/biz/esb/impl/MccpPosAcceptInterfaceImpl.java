package com.sinolife.pos.pubInterface.biz.esb.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sinolife.esbpos.web.WebPosService;
import com.sinolife.pos.acceptance.branch.service.BranchAcceptService;
import com.sinolife.pos.acceptance.branch.service.BranchQueryService;
import com.sinolife.pos.common.dao.ClientInfoDAO;
import com.sinolife.pos.common.dao.CommonAcceptDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dao.PosCodeTableDAO;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_1;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_13;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_17;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_19;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_2;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_21;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_23;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_27;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_28;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_33;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_6;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_9;
import com.sinolife.pos.common.dto.BankInfoDTO;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.ClientPhoneDTO;
import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.PolicyContactInfoDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PolicyProductPremForMccDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.common.dto.PosApplyFilesDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.PosStatusChangeHistoryDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_1;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_13;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_17;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_19;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_2;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_21;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_23;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_27;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_28;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_33;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_6;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_9;
import com.sinolife.pos.common.include.dao.IncludeDAO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.others.unsuspendbankaccount.dao.UnsuspendBankAccountDAO;
import com.sinolife.pos.others.unsuspendbankaccount.service.UnsuspendBankAccountService;
import com.sinolife.pos.pubInterface.biz.dto.EsbClientPhoneInfoDTO;
import com.sinolife.pos.pubInterface.biz.dto.EsbPolicyContactInfoDTO;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO_1;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO_13;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO_17;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO_19;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO_2;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO_21;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO_23;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO_27;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO_28;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO_33;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO_6;
import com.sinolife.pos.pubInterface.biz.dto.PosApplyInfoDTO;
import com.sinolife.pos.pubInterface.biz.esb.service.MccpPosAcceptInterfaceEsb;
import com.sinolife.pos.pubInterface.biz.impl.DevelopPlatformInterfaceImpl;
import com.sinolife.pos.pubInterface.biz.service.WebPosAcceptInterface;
import com.sinolife.pos.vip.service.VipManageService;
import com.sinolife.sf.esb.EsbMethod;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMRuleInfoDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSVerifyResultDto;

@Service("MccpPosAcceptInterface")
public class MccpPosAcceptInterfaceImpl extends DevelopPlatformInterfaceImpl
		implements MccpPosAcceptInterfaceEsb {
	@Autowired
	private PosCodeTableDAO posCodeTableDAO;
	@Autowired
	private IncludeDAO includeDAO;
	@Autowired
	private ClientInfoDAO clientInfoDAO;
	@Autowired
	private CommonQueryDAO commonQueryDAO;
	@Autowired
	private BranchQueryService branchQueryService;
	@Autowired
	private BranchAcceptService branchAcceptService;
	@Autowired
	private CommonAcceptDAO acceptDAO;
	@Autowired
	private TransactionTemplate transactionTemplate;
	@Autowired
	private ServiceItemsDAO_1 serviceItemsDAO_1;
	@Autowired
	private ServiceItemsDAO_2 serviceItemsDAO_2;
	@Autowired
	private ServiceItemsDAO_9 serviceItemsDAO_9;
	@Autowired
	private ServiceItemsDAO_33 serviceItemsDAO_33;
	@Autowired
	private ServiceItemsDAO_21 serviceItemsDAO_21;
	@Autowired
	private ServiceItemsDAO_23 serviceItemsDAO_23;
	@Autowired
	private ServiceItemsDAO_17 serviceItemsDAO_17;
	@Autowired
	private ServiceItemsDAO_19 serviceItemsDAO_19;
	@Autowired
	private ServiceItemsDAO_28 serviceItemsDAO_28;
	@Autowired
	private ServiceItemsDAO_6 serviceItemsDAO_6;
	@Autowired
	private ServiceItemsDAO_13 serviceItemsDAO_13;
	@Autowired
	private ServiceItemsDAO_27 serviceItemsDAO_27;
	@Autowired
	private UnsuspendBankAccountDAO unsuspendBankAccountDAO;
	@Autowired
	private UnsuspendBankAccountService unsuspendBankAccountService;
	@Autowired
	private VipManageService vipManageService;
	@Autowired
	private WebPosAcceptInterface webPosAcceptInterface;

	/** CMP虚拟用户 */
	public static final String CMP_VIRTUAL_USER = "cmpuser";

	/** CMP虚拟柜面 */
	public static final String CMP_VIRTUAL_COUNTER = "901";

	/**
	 * 查询基表
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.queryCodeTableList")
	public List<CodeTableItemDTO> queryCodeTableList(String codeTableName) {
		List<CodeTableItemDTO> codeTableList = (List<CodeTableItemDTO>) posCodeTableDAO
				.queryCodeTable(codeTableName);
		return codeTableList;
	}

	@EsbMethod(esbServiceId = "com.sinolife.pos.queryBranchTreeRoot")
	public List queryBranchTreeRoot(Map pMap) {
		return includeDAO.queryBranchTreeRoot(pMap);
	}

	@EsbMethod(esbServiceId = "com.sinolife.pos.queryBranchTree")
	public List queryBranchTree(Map pMap) {
		return includeDAO.queryBranchTree(pMap);
	}

	@EsbMethod(esbServiceId = "com.sinolife.pos.queryCityByProvince")
	@SuppressWarnings("unchecked")
	public List<CodeTableItemDTO> queryCityByProvince(String province) {
		List citylist = includeDAO.queryCityByProvince(province);
		List retList = new ArrayList();
		for (int i = 0; i < citylist.size(); i++) {
			CodeTableItemDTO cityDto = new CodeTableItemDTO();
			Map<String, String> map = new HashMap<String, String>();
			map = (Map<String, String>) citylist.get(i);
			cityDto.setCode(map.get("CITY_CODE"));
			cityDto.setDescription(map.get("CITY_NAME"));
			retList.add(cityDto);
		}
		return retList;
	}

	@EsbMethod(esbServiceId = "com.sinolife.pos.queryAreaByCity")
	@SuppressWarnings("unchecked")
	public List<CodeTableItemDTO> queryAreaByCity(String city) {
		List arealist = includeDAO.queryAreaByCity(city);
		List retList = new ArrayList();
		for (int i = 0; i < arealist.size(); i++) {
			CodeTableItemDTO areaDto = new CodeTableItemDTO();
			Map<String, String> map = new HashMap<String, String>();
			map = (Map<String, String>) arealist.get(i);
			areaDto.setCode(map.get("AREA_CODE"));
			areaDto.setDescription(map.get("AREA_NAME"));
			retList.add(areaDto);
		}
		return retList;
	}

	@EsbMethod(esbServiceId = "com.sinolife.pos.queryBranchInProvinceCity")
	public List queryBranchInProvinceCity(String province, String city,
			String channel) {
		return includeDAO.queryBranchInProvinceCity(province, city, channel);
	}

	@EsbMethod(esbServiceId = "com.sinolife.pos.queryCounter")
	public List queryCounter(String branchCode) {
		return includeDAO.queryCounter(branchCode);
	}

	/**
	 * 财务接口10：查询银行代码列表接口
	 * 
	 * @param countryCode
	 *            国家代码（非空）
	 * @param provinceCode
	 *            省/直辖市代码（非空）
	 * @param cityCode
	 *            城市代码（非空）
	 * @param bankCategory
	 *            银行大类编码（非空）
	 * @return
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.queryBankList")
	@SuppressWarnings({ "unchecked" })
	public List<BankInfoDTO> queryBankList(String countryCode,
			String provinceCode, String cityCode, String bankCategory) {
		List<BankInfoDTO> bankList = (List<BankInfoDTO>) includeDAO
				.queryBankList(countryCode, provinceCode, cityCode,
						bankCategory).get("p_array_bank_info_items");
		return bankList;
	}

	/***
	 * 根据保单号查询保单犹豫期截止日 update:20140522 DMP-3766 根据保单号查询保单犹豫期截止日的ESB接口迁移到GQS系统
	 * 
	 * @param policy_no
	 * @return
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.querypCruplePeriodPolicy")
	public Map<String, Object> querypCruplePeriodPolicy(String p_policy_no) {
		return commonQueryDAO.querypCruplePeriodPolicy(p_policy_no);
	}

	/**
	 * 查询保单挂失状态
	 * 
	 * @param policyNo
	 * @return {lossFlag[Y-N],lossDate}
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.policyLossStatus")
	public Map policyLossStatus(String policyNo) {
		return commonQueryDAO.policyLossStatus(policyNo);
	}

	/**
	 * 生成Mccp用的条码
	 * 
	 * @param serviceItems
	 * @return
	 */
	public String generateMccpBarcode(String serviceItems) {
		return "MCCP" + commonQueryDAO.queryBarcodeNoSequence();
	}

	/**
	 * 写入批次记录、申请书记录，及保全记录
	 * 
	 * @param applyInfo
	 * @return
	 */
	@SuppressWarnings("unused")
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
		// 如果受理日传空，则取系统时间
		if (applyInfo.getApplyDate() == null) {
			applyInfo.setApplyDate(commonQueryDAO.getSystemDate());
		}
		String barcode = applyInfo.getBarCode();
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
		// 如果传入的条形码为空则重新生成
		if (StringUtils.isBlank(barcode)) {
			barcode = generateMccpBarcode(applyInfo.getServiceItems());
		} else {
			posApplyFilesDto = commonQueryDAO.queryPosApplyfilesRecord(barcode);
			if (posApplyFilesDto != null) {
				throw new RuntimeException("条形码：" + barcode + "已经使用");
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
		batch.setAcceptor(CMP_VIRTUAL_USER);
		batch.setCounterNo(CMP_VIRTUAL_COUNTER);
		batch.setApplyTypeCode(applyType);
		/* 加入证件有效期 edit by wangmingshun start */
		if (applyInfo.getIdnoValidityDate() != null) {
			batch.setIdnoValidityDate(applyInfo.getIdnoValidityDate());
		}
		/* 加入证件有效期 edit by wangmingshun end */

		// 默认申请类型为亲办
		if (StringUtils.isBlank(applyType)) {
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
		file.setTransferAccountOwnerIdType(idtype);// 证件类型
		file.setTransferAccountOwnerIdNo(idno);// 证件号码
		file.setTransferAccountOwner(clientName);// 收款人姓名
		file.setApprovalServiceType(approvalServiceType);
		if (file.getApplyDate() == null) {
			file.setApplyDate(commonQueryDAO.getSystemDate());
		}
		if (StringUtils.isBlank(approvalServiceType)) {
			file.setApprovalServiceType("1");// 自领
		}
		if ("2".equals(approvalServiceType)) {
			file.setAddress(applyInfo.getAddress());
			file.setZip(applyInfo.getZip());
		}
		if (StringUtils.isBlank(paymentType)) {
			file.setPaymentType("1");// 现金
		}
		if ("2".equals(paymentType)) {
			// 复效需要传录入账户
			if ("6".equals(applyInfo.getServiceItems())
					|| "9".equals(applyInfo.getServiceItems())
					|| "33".equals(applyInfo.getServiceItems())) {
				file.setBankCode(applyInfo.getBankCode());
				file.setTransferAccountno(applyInfo.getTransferAccountno());
				file.setTransferAccountOwner(applyInfo
						.getTransferAccountOwner());
				file.setAccountNoType(applyInfo.getAccountNoType());
			}
			// 其它传原账户
			else {
				List<Map<String, Object>> accountList = commonQueryDAO
						.queryAccountByPolicyNo(policyNo);
				if (accountList.size() == 0) {
					throw new RuntimeException("收付方式为转账，但找不到原缴费账号！");
				} else {
					Map<String, Object> accountMap = accountList.get(0);
					file.setBankCode((String) accountMap.get("BANK_CODE"));
					file.setTransferAccountno((String) accountMap
							.get("ACCOUNT_NO"));
					file.setTransferAccountOwner((String) accountMap
							.get("CLIENT_ACCOUNT"));
					file.setAccountNoType((String) accountMap
							.get("ACCOUNT_NO_TYPE"));
				}
			}

		}

		files.add(file);
		batch.setApplyFiles(files);

		// 生成受理
		branchAcceptService.generatePosInfoList(batch, false);

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
		detail.setObjectValue(applyInfo.getClientNo());
		detail.setNewValue(applyInfo.getAcceptor());
		detail.setOldValue(applyInfo.getAcceptor());
		acceptDAO.insertPosAcceptDetail(detail);
		return batch;
	}

	/**
	 * 保全试算
	 * 
	 * @param sii
	 * @param applyInfo
	 * @return
	 */
	public List<Map<String, Object>> queryInputservice(
			EsbServiceItemsInputDTO esii, PosApplyInfoDTO applyInfo) {
		List<Map<String, Object>> processResultList = new ArrayList<Map<String, Object>>();
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> parMap = new HashMap<String, Object>();
		try {
			PosApplyBatchDTO batch = createBatch(applyInfo);
			String serviceItems = esii.getServiceItems();
			ServiceItemsInputDTO sii = new ServiceItemsInputDTO();
			PosInfoDTO posInfo = new PosInfoDTO();
			String posNo = batch.getPosInfoList().get(0).getPosNo();
			ServiceItemsInputDTO oriItemsInputDTO = branchQueryService
					.queryAcceptDetailInputByPosNo(posNo);
			// 经办信息录入
			if ("1".equals(serviceItems)) {
				// 犹豫期退保
				ServiceItemsInputDTO_1 sii1 = new ServiceItemsInputDTO_1();
				sii1.setPosNo(posNo);
				sii1.setPosNoList(null);
				sii1.setBarcodeNo(batch.getApplyFiles().get(0).getBarcodeNo());
				sii1.setBatchNo(batch.getPosBatchNo());
				sii1.setOriginServiceItemsInputDTO(oriItemsInputDTO
						.getOriginServiceItemsInputDTO());
				sii1.setClientNo(applyInfo.getClientNo());
				sii1.setAcceptDate(applyInfo.getApplyDate().toString());
				sii1.setApplyDate(esii.getApplyDate());
				sii1.setPolicyNo(applyInfo.getPolicyNo());
				sii1.setServiceItems(serviceItems);
				EsbServiceItemsInputDTO_1 esii1 = (EsbServiceItemsInputDTO_1) esii;
				sii1.setSurrenderCauseCode(esii1.getSurrenderCauseCode());
				sii = queryServiceItemsInfoExtraToMccp_1(sii1);
			} else if ("2".equals(serviceItems)) {
				// 退保
				ServiceItemsInputDTO_2 sii2 = new ServiceItemsInputDTO_2();
				sii2.setPosNo(posNo);
				sii2.setPosNoList(null);
				sii2.setBarcodeNo(batch.getApplyFiles().get(0).getBarcodeNo());
				sii2.setBatchNo(batch.getPosBatchNo());
				sii2.setOriginServiceItemsInputDTO(oriItemsInputDTO
						.getOriginServiceItemsInputDTO());
				sii2.setClientNo(applyInfo.getClientNo());
				sii2.setAcceptDate(applyInfo.getApplyDate().toString());
				sii2.setApplyDate(esii.getApplyDate());
				sii2.setPolicyNo(applyInfo.getPolicyNo());
				sii2.setServiceItems(serviceItems);
				EsbServiceItemsInputDTO_2 esii2 = (EsbServiceItemsInputDTO_2) esii;
				sii2.setSurrenderCause(esii2.getSurrenderCauseCode());

				String oldTimeString = esii.getApplyDate();

				sii = queryServiceItemsInfoExtraToMccp_2(sii2);
				// 与当前时间相差15天以上，需处理规则跳过规则检查
				Date nowTime = new Date();

				Calendar calendarOldTime = Calendar.getInstance();
				SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
				Date paraTime = formate.parse(oldTimeString);
				calendarOldTime.setTime(paraTime);
				// calendarOldTime.add(Calendar.DAY_OF_MONTH, 15);
				Date applyTime = commonQueryDAO.getAfterDatesOfTime(
						calendarOldTime.getTime(), 15);

				if (applyTime.before(nowTime)) {
					esii.setSpecialRuleType("06");
					esii.setSpecialRuleReason("03");
				}
			} else if ("9".equals(serviceItems)) {
				// 保单还款
				ServiceItemsInputDTO_9 sii9 = new ServiceItemsInputDTO_9();
				sii9.setPosNo(posNo);
				sii9.setPosNoList(null);
				sii9.setBarcodeNo(batch.getApplyFiles().get(0).getBarcodeNo());
				sii9.setBatchNo(batch.getPosBatchNo());
				sii9.setOriginServiceItemsInputDTO(oriItemsInputDTO
						.getOriginServiceItemsInputDTO());
				sii9.setClientNo(applyInfo.getClientNo());
				sii9.setAcceptDate(applyInfo.getApplyDate().toString());
				sii9.setApplyDate(esii.getApplyDate());
				sii9.setPolicyNo(applyInfo.getPolicyNo());
				sii9.setServiceItems(serviceItems);
				// EsbServiceItemsInputDTO_9 esii9 = (EsbServiceItemsInputDTO_9)
				// esii;
				// String returnPayType = esii9.getReturnPayType();
				sii = queryServiceItemsInfoExtraToMccp_9(sii9);
				// returnPayType : all -- 全部还款, part -- 部分还款
				/*
				 * if("ALL".equalsIgnoreCase(returnPayType)){
				 * 
				 * }else{
				 * 
				 * }
				 */

			} else if ("23".equals(serviceItems)) {
				// 续期缴费方式变更
				ServiceItemsInputDTO_23 sii23 = new ServiceItemsInputDTO_23();
				sii23.setPosNo(posNo);
				sii23.setPosNoList(null);
				sii23.setBarcodeNo(batch.getApplyFiles().get(0).getBarcodeNo());
				sii23.setBatchNo(batch.getPosBatchNo());
				sii23.setOriginServiceItemsInputDTO(oriItemsInputDTO
						.getOriginServiceItemsInputDTO());
				sii23.setClientNo(applyInfo.getClientNo());
				sii23.setAcceptDate(applyInfo.getApplyDate().toString());
				sii23.setApplyDate(esii.getApplyDate());
				sii23.setPolicyNo(applyInfo.getPolicyNo());
				sii23.setServiceItems(serviceItems);
				EsbServiceItemsInputDTO_23 esii23 = (EsbServiceItemsInputDTO_23) esii;
				sii23.setChargingMethod(esii23.getChargingMethod());
				sii23.setAccount(esii23.getAccount());
				sii23.setApplicantName(esii23.getApplicantName());
				sii = queryServiceItemsInfoExtraToMccp_23(sii23);
			} else if ("21".equals(serviceItems)) {
				// 客户资料变更
				ServiceItemsInputDTO_21 sii21 = new ServiceItemsInputDTO_21();
				sii21.setPosNo(posNo);
				sii21.setPosNoList(null);
				sii21.setBarcodeNo(batch.getApplyFiles().get(0).getBarcodeNo());
				sii21.setBatchNo(batch.getPosBatchNo());
				sii21.setOriginServiceItemsInputDTO(oriItemsInputDTO
						.getOriginServiceItemsInputDTO());
				sii21.setAcceptDate(applyInfo.getApplyDate().toString());
				sii21.setApplyDate(esii.getApplyDate());
				sii21.setPolicyNo(applyInfo.getPolicyNo());
				sii21.setServiceItems(serviceItems);
				EsbServiceItemsInputDTO_21 esii21 = (EsbServiceItemsInputDTO_21) esii;
				String clientNo;
				if ("I".equals(esii21.getClientType())) {
					// 被保人变更
					clientNo = commonQueryDAO.getInsuredByPolicyNo(
							esii21.getPolicyNo()).get(0);
				} else if ("A".equals(esii21.getClientType())) {
					// 投保人变更
					clientNo = applyInfo.getClientNo();
				} else {
					retMap.put("RESULT_MSG", "只能做投被保人变更");
					retMap.put("RESULT_FLAG", "N");
					processResultList.add(retMap);
					return processResultList;
				}
				sii21.setClientNo(clientNo);
				sii21 = (ServiceItemsInputDTO_21) queryServiceItemsInfoExtraToMccp_21(sii21);
				ClientInformationDTO clientInfoDto = sii21
						.getClientInformationDTO();
				clientInfoDto = sii21.getClientInformationDTO();
				clientInfoDto.setCountryCode(esii21.getCountryCode());
				clientInfoDto.setIdNo(esii21.getIdNo());
				clientInfoDto.setMarriageCode(esii21.getMarriageCode());
				clientInfoDto.setIdnoValidityDate(esii21.getIdnoValidityDate());
				sii21.setClientInformationDTO(clientInfoDto);
				sii = sii21;
			} else if ("28".equals(serviceItems)) {
				// 保单挂失
				ServiceItemsInputDTO_28 sii28 = new ServiceItemsInputDTO_28();
				sii28.setPosNo(posNo);
				sii28.setPosNoList(null);
				sii28.setBarcodeNo(batch.getApplyFiles().get(0).getBarcodeNo());
				sii28.setBatchNo(batch.getPosBatchNo());
				sii28.setOriginServiceItemsInputDTO(oriItemsInputDTO
						.getOriginServiceItemsInputDTO());
				sii28.setClientNo(applyInfo.getClientNo());
				sii28.setAcceptDate(applyInfo.getApplyDate().toString());
				sii28.setApplyDate(esii.getApplyDate());
				sii28.setPolicyNo(applyInfo.getPolicyNo());
				sii28.setServiceItems(serviceItems);
				EsbServiceItemsInputDTO_28 esii28 = (EsbServiceItemsInputDTO_28) esii;
				Map map = commonQueryDAO.policyLossStatus(sii28.getPolicyNo());
				String isLoss = (String) map.get("lossFlag");
				if ("Y".equals(isLoss)) {
					retMap.put("RESULT_MSG", "保单已经挂失，不能继续操作挂失");
					retMap.put("RESULT_FLAG", "N");
					processResultList.add(retMap);
					return processResultList;
				}
				sii28.setIsLoss(esii28.getIsLoss());
				sii = (ServiceItemsInputDTO_28) queryServiceItemsInfoToMccp_28(sii28);
			} else if ("19".equals(serviceItems)) {
				// 联系方式变更
				ServiceItemsInputDTO_19 sii19 = new ServiceItemsInputDTO_19();
				sii19.setPosNo(posNo);
				sii19.setPosNoList(null);
				sii19.setBarcodeNo(batch.getApplyFiles().get(0).getBarcodeNo());
				sii19.setBatchNo(batch.getPosBatchNo());
				sii19.setOriginServiceItemsInputDTO(oriItemsInputDTO
						.getOriginServiceItemsInputDTO());
				sii19.setClientNo(applyInfo.getClientNo());
				sii19.setAcceptDate(applyInfo.getApplyDate().toString());
				sii19.setApplyDate(esii.getApplyDate());
				sii19.setPolicyNo(applyInfo.getPolicyNo());
				sii19.setServiceItems(serviceItems);
				EsbServiceItemsInputDTO_19 esii19 = (EsbServiceItemsInputDTO_19) esii;
				sii19.setClientNo(esii19.getClientNo());
				sii19.setClientName(esii19.getClientName());
				sii19 = queryServiceItemsInfoToMccp_19(sii19);
				sii19.setAddress(esii19.getAddress());
				sii19.setPhoneList(esii19.getPhoneList());
				sii19.setEmail(esii19.getEmail());
				List<EsbPolicyContactInfoDTO> ContactList = esii19
						.getPolicyContactList();
				List<PolicyContactInfoDTO> policyContactList = new ArrayList<PolicyContactInfoDTO>();
				for (int i = 0; i < ContactList.size(); i++) {
					PolicyContactInfoDTO policyContactInfoDTO = new PolicyContactInfoDTO();
					EsbPolicyContactInfoDTO esbPolicyContactInfoDTO = new EsbPolicyContactInfoDTO();
					esbPolicyContactInfoDTO = ContactList.get(i);
					policyContactInfoDTO.setAddressDesc(esbPolicyContactInfoDTO
							.getAddressDesc());
					policyContactInfoDTO.setAddressSeq(esbPolicyContactInfoDTO
							.getAddressSeq());
					policyContactInfoDTO.setBranchCode(esbPolicyContactInfoDTO
							.getBranchCode());
					policyContactInfoDTO.setBranchName(esbPolicyContactInfoDTO
							.getBranchName());
					policyContactInfoDTO.setChecked(esbPolicyContactInfoDTO
							.getChecked());
					policyContactInfoDTO.setClient(esbPolicyContactInfoDTO
							.getClient());
					policyContactInfoDTO.setEmailDesc(esbPolicyContactInfoDTO
							.getEmailDesc());
					policyContactInfoDTO.setEmailSeq(esbPolicyContactInfoDTO
							.getEmailSeq());
					policyContactInfoDTO
							.setHomePhoneDesc(esbPolicyContactInfoDTO
									.getHomePhoneDesc());
					if (esbPolicyContactInfoDTO.getHomePhoneSeq() != null
							&& !"".endsWith(esbPolicyContactInfoDTO
									.getHomePhoneSeq())
							&& !"FAKE_".equals(esbPolicyContactInfoDTO
									.getHomePhoneSeq().substring(0, 5))) {
						policyContactInfoDTO.setHomePhoneSeq("FAKE_1--");
					} else {
						policyContactInfoDTO
								.setHomePhoneSeq(esbPolicyContactInfoDTO
										.getHomePhoneSeq());
					}
					policyContactInfoDTO
							.setMobilePhoneDesc(esbPolicyContactInfoDTO
									.getMobilePhoneDesc());
					if (esbPolicyContactInfoDTO.getMobilePhoneSeq() != null
							&& !"".endsWith(esbPolicyContactInfoDTO
									.getMobilePhoneSeq())
							&& !"FAKE_".equals(esbPolicyContactInfoDTO
									.getMobilePhoneSeq().substring(0, 5))) {
						policyContactInfoDTO.setMobilePhoneSeq("FAKE_4--");
					} else {
						policyContactInfoDTO
								.setMobilePhoneSeq(esbPolicyContactInfoDTO
										.getMobilePhoneSeq());
					}
					policyContactInfoDTO
							.setOfficePhoneDesc(esbPolicyContactInfoDTO
									.getOfficePhoneDesc());
					if (esbPolicyContactInfoDTO.getOfficePhoneSeq() != null
							&& !"".endsWith(esbPolicyContactInfoDTO
									.getOfficePhoneSeq())
							&& !"FAKE_".equals(esbPolicyContactInfoDTO
									.getOfficePhoneSeq().substring(0, 5))) {
						policyContactInfoDTO.setOfficePhoneSeq("FAKE_3--");
					} else {
						policyContactInfoDTO
								.setOfficePhoneSeq(esbPolicyContactInfoDTO
										.getOfficePhoneSeq());
					}
					policyContactInfoDTO.setPhoneDesc(esbPolicyContactInfoDTO
							.getPhoneDesc());
					if (esbPolicyContactInfoDTO.getPhoneSeq() != null
							&& !"".endsWith(esbPolicyContactInfoDTO
									.getPhoneSeq())
							&& !"FAKE_".equals(esbPolicyContactInfoDTO
									.getPhoneSeq().substring(0, 5))) {
						policyContactInfoDTO.setPhoneSeq("FAKE_2--");
					} else {
						policyContactInfoDTO
								.setPhoneSeq(esbPolicyContactInfoDTO
										.getPhoneSeq());
					}
					policyContactInfoDTO.setPolicyNo(esbPolicyContactInfoDTO
							.getPolicyNo());
					policyContactInfoDTO.setSmsService(esbPolicyContactInfoDTO
							.getSmsService());
					policyContactList.add(policyContactInfoDTO);
				}
				sii19.setPolicyContactList(policyContactList);
				sii = sii19;
			} else if ("17".equals(serviceItems)) {
				// 续期保费退费
				ServiceItemsInputDTO_17 sii17 = new ServiceItemsInputDTO_17();
				sii17.setPosNo(posNo);
				sii17.setPosNoList(null);
				sii17.setBarcodeNo(batch.getApplyFiles().get(0).getBarcodeNo());
				sii17.setBatchNo(batch.getPosBatchNo());
				sii17.setOriginServiceItemsInputDTO(oriItemsInputDTO
						.getOriginServiceItemsInputDTO());
				sii17.setClientNo(applyInfo.getClientNo());
				sii17.setAcceptDate(applyInfo.getApplyDate().toString());
				sii17.setApplyDate(esii.getApplyDate());
				sii17.setPolicyNo(applyInfo.getPolicyNo());
				sii17.setServiceItems(serviceItems);
				EsbServiceItemsInputDTO_17 esii17 = (EsbServiceItemsInputDTO_17) esii;
				sii17.setRefundAmount(esii17.getRefundAmount());
				sii17.setRefundCauseCode(esii17.getRefundCauseCode());
				sii = queryServiceItemsInfoToMccp_17(sii17);
			} else if ("6".equals(serviceItems)) {
				// 复效
				ServiceItemsInputDTO_6 sii6 = new ServiceItemsInputDTO_6();
				sii6.setPosNo(posNo);
				sii6.setPosNoList(null);
				sii6.setBarcodeNo(batch.getApplyFiles().get(0).getBarcodeNo());
				sii6.setBatchNo(batch.getPosBatchNo());
				sii6.setOriginServiceItemsInputDTO(oriItemsInputDTO
						.getOriginServiceItemsInputDTO());
				sii6.setClientNo(applyInfo.getClientNo());
				sii6.setAcceptDate(applyInfo.getApplyDate().toString());
				sii6.setApplyDate(esii.getApplyDate());
				sii6.setPolicyNo(applyInfo.getPolicyNo());
				sii6.setServiceItems(serviceItems);
				EsbServiceItemsInputDTO_6 esii6 = (EsbServiceItemsInputDTO_6) esii;
				sii6.setRenewType(esii6.getRenewType());
				sii6.setAppPersonalNotice(esii6.getAppPersonalNotice());
				sii6.setInsPersonalNotice(esii6.getInsPersonalNotice());
				sii = queryServiceItemsInfoToMccp_6(sii6);
			} else if ("33".equals(serviceItems)) {
				// 保全收付款方式调整
				ServiceItemsInputDTO_33 sii33 = new ServiceItemsInputDTO_33();
				EsbServiceItemsInputDTO_33 esii33 = (EsbServiceItemsInputDTO_33) esii;
				sii33.setPosNo(posNo);
				sii33.setPosNoList(null);
				sii33.setBarcodeNo(batch.getApplyFiles().get(0).getBarcodeNo());
				sii33.setBatchNo(batch.getPosBatchNo());
				sii33.setOriginServiceItemsInputDTO(oriItemsInputDTO
						.getOriginServiceItemsInputDTO());
				sii33.setClientNo(applyInfo.getClientNo());
				sii33.setAcceptDate(applyInfo.getApplyDate().toString());
				sii33.setApplyDate(esii33.getApplyDate());
				sii33.setChangePosNo(esii33.getChangePosNo());
				sii33.setPolicyNo(applyInfo.getPolicyNo());
				sii33.setServiceItems(serviceItems);
				sii = queryServiceItemsInfoExtraToMccp_33(sii33);
			} else if ("13".equals(serviceItems)) {
				// 交费频次变更
				ServiceItemsInputDTO_13 sii13 = new ServiceItemsInputDTO_13();
				EsbServiceItemsInputDTO_13 esii13 = (EsbServiceItemsInputDTO_13) esii;
				sii13.setPosNo(posNo);
				sii13.setPosNoList(null);
				sii13.setBarcodeNo(batch.getApplyFiles().get(0).getBarcodeNo());
				sii13.setBatchNo(batch.getPosBatchNo());
				sii13.setOriginServiceItemsInputDTO(oriItemsInputDTO
						.getOriginServiceItemsInputDTO());
				sii13.setClientNo(applyInfo.getClientNo());
				sii13.setAcceptDate(applyInfo.getApplyDate().toString());
				sii13.setApplyDate(esii13.getApplyDate());
				sii13.setPeriodPrem(esii13.getPeriodPrem());
				sii13.setPolicyNo(applyInfo.getPolicyNo());
				sii13.setServiceItems(serviceItems);
				sii = queryServiceItemsInfoExtraToMccp_13(sii13);
				sii13.setFrequency(esii13.getFrequency());
			} else if ("27".equals(serviceItems)) {
				// 保单补发
				ServiceItemsInputDTO_27 sii27 = new ServiceItemsInputDTO_27();
				EsbServiceItemsInputDTO_27 esii27 = (EsbServiceItemsInputDTO_27) esii;
				sii27.setPosNo(posNo);
				sii27.setPosNoList(null);
				sii27.setBarcodeNo(batch.getApplyFiles().get(0).getBarcodeNo());
				sii27.setBatchNo(batch.getPosBatchNo());
				sii27.setOriginServiceItemsInputDTO(oriItemsInputDTO
						.getOriginServiceItemsInputDTO());
				sii27.setClientNo(applyInfo.getClientNo());
				sii27.setAcceptDate(applyInfo.getApplyDate().toString());
				sii27.setApplyDate(esii27.getApplyDate());
				sii27.setReProvideCause(esii27.getReProvideCause());
				sii27.setChargeOption(esii27.getChargeOption());
				sii27.setDeliveryType(esii27.getDeliveryType());
				sii27.setReProvideCauseDesc(esii27.getReProvideCauseDesc());
				sii27.setPolicyNo(applyInfo.getPolicyNo());
				sii27.setServiceItems(serviceItems);
				sii = queryServiceItemsInfoExtraToMccp_27(sii27);

			} else {
				retMap.put("RESULT_MSG", "电话渠道不支持该保全项目");
				retMap.put("RESULT_FLAG", "N");
				processResultList.add(retMap);
				return processResultList;
			}
			// 规则特殊件内容
			// sii.setSpecialFunc(esii.getSpecialFunc());
			if (esii.getSpecialRuleType() != null
					&& !esii.getSpecialRuleType().isEmpty()) {
				sii.setSpecialRuleType(esii.getSpecialRuleType());
				sii.setSpecialRuleReason(esii.getSpecialRuleReason());
				sii.setSpecialRule("Y");
			}
			System.out.println("serviceItems:" + serviceItems
					+ "===特殊规则参数===SpecialRuleType："
					+ esii.getSpecialRuleType() + "===SpecialRuleReason:"
					+ esii.getSpecialRuleReason());

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
					branchAcceptService.cancelAccept(posInfo,
							batch.getAcceptor());
					// throw new RuntimeException("写保全状态变更记录失败：" +
					// statusChange.getMessage());
				}

				// 这些渠道都是逐单受理，因此前一单受理完成，更新状态为 A03待处理规则检查
				branchAcceptService.updatePosInfo(posNo, "accept_status_code",
						"A03");

				// 调用流转接口
				parMap = branchAcceptService
						.workflowControl("03", posNo, false);
				retMap = branchQueryService.queryProcessResult(posNo);
				PosInfoDTO nposInfo=commonQueryDAO.queryPosInfoRecord(posNo);
				if (!("27".equals(nposInfo.getServiceItems())&&"A03".equals(nposInfo.getAcceptStatusCode()))){
					retMap.put("RULE_INFO", parMap.get("ruleInfo"));
					retMap.put("RESULT_MSG", "试算完成");
					retMap.put("RESULT_FLAG", "Y");
				}else{
					retMap.put("APPROVE_TEXT", "保单补发，延迟生效");
					retMap.put("RESULT_MSG", "试算完成");
					retMap.put("RESULT_FLAG", "Y");
				}
				
				processResultList.add(retMap);
			}
		} catch (Exception e) {
			retMap.put("RESULT_MSG", e.getMessage());
			System.out.println(e.getMessage());
			retMap.put("RESULT_FLAG", "N");
			processResultList.add(retMap);
		}
		return processResultList;
	}

	/**
	 * 经办结果确认
	 * 
	 */
	public List<Map<String, Object>> processSubmit(
			EsbServiceItemsInputDTO esii, PosApplyInfoDTO applyInfo) {
		List<Map<String, Object>> processSubmitList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> processResultList = queryInputservice(esii,
				applyInfo);
		for (int i = 0; i < processResultList.size(); i++) {
			Map<String, Object> processResultMap = processResultList.get(i);
			// 如果规则通过则才可以确认
			if ("Y".equals(processResultMap.get("RESULT_FLAG"))) {
				String posNo = (String) processResultMap.get("POS_NO");
				String flag;
				String msg ;
				PosInfoDTO nposInfo=commonQueryDAO.queryPosInfoRecord(posNo);
				if (!("27".equals(nposInfo.getServiceItems())&&"A03".equals(nposInfo.getAcceptStatusCode()))){
				    Map<String, Object> retMap = branchAcceptService
						    .workflowControl("05", posNo, false);
				    flag = (String) retMap.get("flag");
					msg = (String) retMap.get("message");
				}else{
				    flag = nposInfo.getAcceptStatusCode();
				    msg = "保单补发延迟生效";
				}

				String resultMsg = null;
				if ("A08".equals(flag)) {
					// 已送审
					flag = "Y";
					resultMsg = "已送审";
				} else if ("A12".equals(flag)) {
					// 已送人工核保
					flag = "Y";
					resultMsg = "已送人工核保";
				} else if ("A15".equals(flag)) {
					// 待收费
					flag = "Y";
					resultMsg = "待收费";
				} else if ("E01".equals(flag)) {
					// 生效完成
					flag = "Y";
					resultMsg = "已生效";
				} else if ("A17".equals(flag)) {
					// 待保单打印
					flag = "Y";
					resultMsg = "待保单打印";
				} else if ("27".equals(nposInfo.getServiceItems())&&"A03".equals(nposInfo.getAcceptStatusCode())) {
					// 延迟生效
					flag = "Y";
					resultMsg = "保单补发延迟生效";
				} else {
					flag = "N";
					resultMsg = msg;
				}
				processResultMap.put("RESULT_MSG", resultMsg);
				processResultMap.put("RESULT_FLAG", flag);
			}
			processSubmitList.add(processResultMap);
		}
		return processSubmitList;
	}

	/**
	 * 保全试算外部接口
	 * 
	 * @param posInfo
	 * @param acceptUser
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.acceptDetailInputSubmit")
	public List<Map<String, Object>> acceptDetailInputSubmit(
			final EsbServiceItemsInputDTO esii, final PosApplyInfoDTO applyInfo) {
		try {
			List<Map<String, Object>> processResultList = transactionTemplate
					.execute(new TransactionCallback<List<Map<String, Object>>>() {
						@Override
						public List<Map<String, Object>> doInTransaction(
								TransactionStatus transactionStatus) {
							try {
								return queryInputservice(esii, applyInfo);
							} finally {
								transactionStatus.setRollbackOnly();
							}
						}
					});
			return processResultList;
		} catch (Exception e) {
			throw new RuntimeException("保全试算失败" + e.getMessage());
		}
	}

	/**
	 * 经办结果确认提交
	 * 
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.processResultSubmit")
	public List<Map<String, Object>> processResultSubmit(
			final EsbServiceItemsInputDTO esii, final PosApplyInfoDTO applyInfo) {
		try {
			List<Map<String, Object>> processResultList = transactionTemplate
					.execute(new TransactionCallback<List<Map<String, Object>>>() {
						@Override
						public List<Map<String, Object>> doInTransaction(
								TransactionStatus transactionStatus) {
							try {
								return processSubmit(esii, applyInfo);
							} catch (Exception e) {
								transactionStatus.setRollbackOnly();
								throw new RuntimeException(e.getMessage(), e);
							}
						}
					});
			return processResultList;
		} catch (Exception e) {
			throw new RuntimeException("保全试算失败" + e.getMessage());
		}
	}

	/**
	 * 犹豫期退保经办信息录入
	 */
	public ServiceItemsInputDTO_1 queryServiceItemsInfoExtraToMccp_1(
			ServiceItemsInputDTO sii) {
		ServiceItemsInputDTO_1 items1 = new ServiceItemsInputDTO_1();
		items1 = (ServiceItemsInputDTO_1) serviceItemsDAO_1
				.queryServiceItemsInfoExtraToBia_1(sii);
		// 是否可疑交易
		items1.setDouBt(false);
		boolean ishaveprd = false;
		List<PolicyProductDTO> productList = items1.getPolicyProductList();
		for (int i = 0; productList != null && i < productList.size(); i++) {
			PolicyProductDTO product = productList.get(i);
			if ("1".equals(product.getDutyStatus())) {
				product.setSelected(true);
				ishaveprd = true;
			}
		}
		if (!ishaveprd) {
			throw new RuntimeException("该保单没可操作契撤的险种");
		}
		items1.setPolicyProductList(productList);
		return items1;
	}

	/**
	 * 退保经办信息录入
	 */
	public ServiceItemsInputDTO_2 queryServiceItemsInfoExtraToMccp_2(
			ServiceItemsInputDTO sii) {
		ServiceItemsInputDTO_2 items2 = new ServiceItemsInputDTO_2();
		boolean ishaveprd = false;
		items2 = (ServiceItemsInputDTO_2) serviceItemsDAO_2
				.queryServiceItemsInfoExtraToBia_2(sii);
		// 是否整单退保
		items2.setAllSurrender(true);
		// 是否可疑交易
		items2.setDouBt(false);
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
			throw new RuntimeException("该保单没可操作退保的险种");
		}
		items2.setProductList(productList);
		return items2;
	}

	/**
	 * 续期交费方式变更经办信息录入
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtraToMccp_23(
			ServiceItemsInputDTO sii) {
		return serviceItemsDAO_23.queryServiceItemsInfoExtraToBia_23(sii);
	}

	/**
	 * 客户资料变更
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtraToMccp_21(
			ServiceItemsInputDTO sii) {
		return serviceItemsDAO_21.queryServiceItemsInfoExtra(sii);

	}

	/**
	 * 续期保费退费经办信息录入页面
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoToMccp_17(
			ServiceItemsInputDTO sii) {
		return serviceItemsDAO_17.queryServiceItemsInfoMcc_17(sii);
	};

	/**
	 * 联系方式变更
	 */
	public ServiceItemsInputDTO_19 queryServiceItemsInfoToMccp_19(
			ServiceItemsInputDTO sii) {
		return serviceItemsDAO_19.queryServiceItemsInfo(sii);
	};

	/**
	 * 挂失及解挂
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoToMccp_28(
			ServiceItemsInputDTO sii) {
		return serviceItemsDAO_28.queryServiceItemsInfoExtra_28(sii);
	};

	/**
	 * 复效
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoToMccp_6(
			ServiceItemsInputDTO sii) {
		ServiceItemsInputDTO_6 items6 = new ServiceItemsInputDTO_6();
		items6 = serviceItemsDAO_6.queryServiceItemsInfoExtra_6(sii);
		List<PolicyProductDTO> productList = items6.getPolicyProductList();
		for (int i = 0; productList != null && i < productList.size(); i++) {
			PolicyProductDTO product = productList.get(i);
			if ("Y".equals(product.getCanBeSelectedFlag()))
				product.setSelected(true);
		}
		items6.setPolicyProductList(productList);
		return items6;
	};

	/**
	 * 查询保单联系信息
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.queryEsbPolicyContactInfoList")
	public List<EsbPolicyContactInfoDTO> queryEsbPolicyContactInfoList(
			String clientNo, String policyNo) {
		List<EsbPolicyContactInfoDTO> esbPolicyContactInfoList = new ArrayList<EsbPolicyContactInfoDTO>();
		List<PolicyContactInfoDTO> policyContactInfoList = serviceItemsDAO_19
				.queryAppContactInfo(clientNo, policyNo);
		;
		for (int i = 0; i < policyContactInfoList.size(); i++) {
			EsbPolicyContactInfoDTO esbPolicyContactInfo = new EsbPolicyContactInfoDTO();
			PolicyContactInfoDTO policyContactInfo = policyContactInfoList
					.get(i);
			esbPolicyContactInfo.setAddressDesc(policyContactInfo
					.getAddressDesc());
			esbPolicyContactInfo.setAddressSeq(policyContactInfo
					.getAddressSeq());
			esbPolicyContactInfo.setBranchCode(policyContactInfo
					.getBranchCode());
			esbPolicyContactInfo.setBranchName(policyContactInfo
					.getBranchName());
			esbPolicyContactInfo.setEmailDesc(policyContactInfo.getEmailDesc());
			esbPolicyContactInfo.setEmailSeq(policyContactInfo.getEmailSeq());
			esbPolicyContactInfo.setHomePhoneDesc(policyContactInfo
					.getHomePhoneDesc());
			esbPolicyContactInfo.setHomePhoneSeq(policyContactInfo
					.getHomePhoneSeq());
			esbPolicyContactInfo.setMobilePhoneDesc(policyContactInfo
					.getMobilePhoneDesc());
			esbPolicyContactInfo.setMobilePhoneSeq(policyContactInfo
					.getMobilePhoneSeq());
			esbPolicyContactInfo.setOfficePhoneDesc(policyContactInfo
					.getOfficePhoneDesc());
			esbPolicyContactInfo.setOfficePhoneSeq(policyContactInfo
					.getOfficePhoneSeq());
			esbPolicyContactInfo.setPhoneDesc(policyContactInfo.getPhoneDesc());
			esbPolicyContactInfo.setPhoneSeq(policyContactInfo.getPhoneSeq());
			esbPolicyContactInfo.setSmsService(policyContactInfo
					.getSmsService());
			esbPolicyContactInfo.setPolicyNo(policyContactInfo.getPolicyNo());
			esbPolicyContactInfo.setIssueDate(policyContactInfo.getIssueDate());
			esbPolicyContactInfo.setDutyStatus(policyContactInfo
					.getDutyStatus());
			/* edit by wangmingshun start 增加邮政编码 */
			esbPolicyContactInfo.setPostalCode(policyContactInfo
					.getPostalCode());
			/* edit by wangmingshun end 增加邮政编码 */
			esbPolicyContactInfoList.add(esbPolicyContactInfo);
		}
		return esbPolicyContactInfoList;
	};

	/**
	 * 根据客户号查询已经加挂的电话
	 * 
	 * @param clientNo
	 * @return
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.getUserMobileByClientNo")
	public String getUserMobileByClientNo(String clientNo) {
		// 查询已经加挂手机号
		WebPosService wps = PlatformContext.getEsbContext().getEsbService(
				WebPosService.class);
		String webMobilePhoneNo = wps.getUserMobileByClientNo(clientNo);
		return webMobilePhoneNo;
	}

	/**
	 * 根据客户号判断手机号是否允许加挂保单
	 * 
	 * @param clientNo
	 * @param mobile
	 * @return map (key:flag (Y/N),message)
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.checkClientMobile")
	public Map<String, Object> checkClientMobile(String clientNo, String mobile) {
		WebPosService wps = PlatformContext.getEsbContext().getEsbService(
				WebPosService.class);
		Map checkMap = wps.checkClientMobile(clientNo, mobile);
		return checkMap;
	}

	/**
	 * 获取与当前客户使用相同手机号的其它客户，排除家庭关系，自保件
	 * 
	 * @param clientNo
	 * @param phoneNo
	 * @return ArrayList<EsbClientPhoneInfoDTO>
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.getDifClientPhoneInfoByMobile")
	public ArrayList<EsbClientPhoneInfoDTO> getDifClientPhoneInfoByPhoneNo(
			String clientNo, String phoneNo) {
		System.out.println("=======================start================");
		System.out.println("时间==="
				+ PosUtils.formatDate(new Date(), "yyyy-MM-dd hh:mm:ss"));
		System.out
				.println("开始调ESB接口method=getDifClientPhoneInfoByPhoneNo,clientNo="
						+ clientNo + ",phoneNo=" + phoneNo);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// Map<String, Object> retMap = new HashMap<String, Object>();
		// WebPosService wps = PlatformContext.getEsbContext().getEsbService(
		// WebPosService.class);
		// retMap = wps.getOtherClientsUsingSamePhone(clientNo, phoneNo);
		ArrayList<EsbClientPhoneInfoDTO> difClientInfoList = new ArrayList<EsbClientPhoneInfoDTO>();
		// List<String> clientList = (ArrayList<String>) retMap
		// .get("bizClients");
		try {
			List<String> clientList = this.commonQueryDAO.getClientNoByPhoneNo(
					phoneNo, clientNo);
			if (clientList != null) {
				for (int i = 0; i < clientList.size(); i++) {
					EsbClientPhoneInfoDTO clientPhoneInfoDto = new EsbClientPhoneInfoDTO();
					String otherclientNo = clientList.get(i);

					String isFamily = this.commonQueryDAO.isFamilyPhoneNo(
							otherclientNo, clientNo);
					if ("Y".equalsIgnoreCase(isFamily)) {
						continue;
					}

					Map<String, Object> clientMap = commonQueryDAO
							.getClientPhoneInfo(otherclientNo, phoneNo);
					if (clientMap == null || clientMap.isEmpty()) {
						continue;
					}
					clientPhoneInfoDto.setIsCheck("N");
					clientPhoneInfoDto.setClientNo((String) clientMap
							.get("CLIENTNO"));
					clientPhoneInfoDto.setClientName((String) clientMap
							.get("CLIENTNAME"));
					clientPhoneInfoDto.setIdType((String) clientMap
							.get("IDTYPE"));
					clientPhoneInfoDto.setIdNo((String) clientMap.get("IDNO"));
					clientPhoneInfoDto
							.setSex((String) clientMap.get("SEXCODE"));
					clientPhoneInfoDto.setBirthday(sdf.format((Date) clientMap
							.get("BIRTHDAY")));
					clientPhoneInfoDto
							.setIsVip((String) clientMap.get("ISVIP"));
					clientPhoneInfoDto.setPhoneNo(phoneNo);
					difClientInfoList.add(clientPhoneInfoDto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

		System.out
				.println("调ESB接口method=getDifClientPhoneInfoByPhoneNo,clientNo="
						+ clientNo
						+ ",phoneNo="
						+ phoneNo
						+ ",返回结果：result="
						+ difClientInfoList.toString());
		System.out.println("======================end======================");
		return difClientInfoList;
	}

	/**
	 * 根据MCC返回的客户电话列表停用电话
	 * 
	 * @param ArrayList
	 *            <EsbClientPhoneInfoDTO>
	 * @return Map<String, Object>
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.stopClientPhoneInfo")
	public Map<String, Object> stopClientPhoneInfo(
			ArrayList<EsbClientPhoneInfoDTO> clientPhoneList) {
		Map<String, String> pkMap = new HashMap<String, String>();
		Map<String, Object> retMap = new HashMap<String, Object>();
		ArrayList<EsbClientPhoneInfoDTO> newclientPhonelist = new ArrayList<EsbClientPhoneInfoDTO>();
		pkMap.put("table", "POS_INVALID_PHONE");
		pkMap.put("column", "BATCH_NO");
		pkMap.put("owner", "POSDATA");
		String v_batch_no = commonQueryDAO.getSeq(pkMap);
		for (int i = 0; i < clientPhoneList.size(); i++) {
			EsbClientPhoneInfoDTO clientPhone = clientPhoneList.get(i);
			if ("Y".equals(clientPhone.getIsCheck())) {
				commonQueryDAO.stopClientPhone(v_batch_no,
						clientPhone.getClientNo(), clientPhone.getPhoneNo(),
						clientPhone.getProcessor(),
						clientPhone.getProcessed_date());
			}
			clientPhone.setBatch_no(v_batch_no);
			newclientPhonelist.add(clientPhone);
		}
		retMap.put("clientList", newclientPhonelist);
		return retMap;
	}

	/**
	 * 根据批次号返回客户保单列表
	 * 
	 * @param batch_no
	 * @return Map<String, String>
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.getStopClientPolicy")
	public ArrayList<Map<String, String>> getStopClientPolicy(String batch_no) {
		ArrayList<Map<String, String>> retList = new ArrayList<Map<String, String>>();
		retList = (ArrayList<Map<String, String>>) commonQueryDAO
				.getStopClientPolicy(batch_no);
		return retList;
	}

	/**
	 * 根据批次号，电话号码 查询需要恢复的客户电话列表
	 * 
	 * @param BATCH_NO
	 * @param phoneNo
	 * @return ArrayList<EsbClientPhoneInfoDTO>
	 */

	@EsbMethod(esbServiceId = "com.sinolife.pos.getClientPhoneInfoByBachtNo")
	public ArrayList<EsbClientPhoneInfoDTO> getClientPhoneInfoByBachtNo(
			String batchNo, String phoneNo) {
		List<Map<String, Object>> clientList = commonQueryDAO
				.getClientPhoneInfoByBachtNo(batchNo, phoneNo);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ArrayList<EsbClientPhoneInfoDTO> clientInfoList = new ArrayList<EsbClientPhoneInfoDTO>();
		for (int i = 0; i < clientList.size(); i++) {
			EsbClientPhoneInfoDTO clientPhoneInfoDto = new EsbClientPhoneInfoDTO();
			Map<String, Object> clientMap = clientList.get(i);
			clientPhoneInfoDto.setIsCheck("N");
			clientPhoneInfoDto.setClientNo((String) clientMap.get("CLIENTNO"));
			clientPhoneInfoDto.setClientName((String) clientMap
					.get("CLIENTNAME"));
			clientPhoneInfoDto.setIdType((String) clientMap.get("IDTYPE"));
			clientPhoneInfoDto.setIdNo((String) clientMap.get("IDNO"));
			clientPhoneInfoDto.setSex((String) clientMap.get("SEXCODE"));
			clientPhoneInfoDto.setBirthday(sdf.format((Date) clientMap
					.get("BIRTHDAY")));
			clientPhoneInfoDto.setIsVip((String) clientMap.get("ISVIP"));
			clientPhoneInfoDto.setBatch_no(batchNo);
			clientPhoneInfoDto.setPhoneNo(phoneNo);
			clientInfoList.add(clientPhoneInfoDto);
		}
		return clientInfoList;
	}

	/**
	 * 根据MCC返回的需要恢复的客户电话列表 恢复停用电话
	 * 
	 * @param ArrayList
	 *            <EsbClientPhoneInfoDTO>
	 * @return Y/N
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.rollbackClientPhoneInfo")
	public String rollbackClientPhoneInfo(
			ArrayList<EsbClientPhoneInfoDTO> clientPhoneList) {
		Map<String, Object> clientPhoneMap = new HashMap<String, Object>();
		for (int i = 0; i < clientPhoneList.size(); i++) {
			EsbClientPhoneInfoDTO clientPhone = clientPhoneList.get(i);
			if ("Y".equals(clientPhone.getIsCheck())) {
				clientPhoneMap = commonQueryDAO.rollbackClientPhone(
						clientPhone.getBatch_no(), clientPhone.getClientNo(),
						clientPhone.getPhoneNo(),
						clientPhone.getRollbacked_user(),
						clientPhone.getRollbacked_date());
				String flag = (String) clientPhoneMap.get("p_flag");
				if (!"0".equals(flag)) {
					return "N";
				}
			}
		}
		return "Y";
	}

	/**
	 * 检查电话号码的格式是否正确
	 * 
	 * @param
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.checkPhone")
	public List<ClientPhoneDTO> checkPhone(List<ClientPhoneDTO> phoneList) {

		Map<String, Object> pMap = new HashMap<String, Object>();
		Map<String, Object> phoneMap = new HashMap<String, Object>();
		for (int i = 0; phoneList != null && i < phoneList.size(); i++) {
			ClientPhoneDTO phone = phoneList.get(i);
			// 境外电话不需要进行验证 DMP-2501
			if ("T".equals(phone.getForeignType())) {
				continue;
			}
			pMap = new HashMap<String, Object>();
			if ("4".equals(phone.getPhoneType())) {
				pMap.put("checkType", "33");
				pMap.put("checkChar", phone.getPhoneNo());
			} else {

				phoneMap.put("checkChar", phone.getPhoneNo());

				serviceItemsDAO_19.pubCheckClientMobile(phoneMap);

				// 是手机号码
				if ("Y".equals(phoneMap.get("flag"))) {

					pMap.put("checkType", "33");
					pMap.put("checkChar", phone.getPhoneNo());
				} else {
					pMap.put("checkType", "3");
					pMap.put("checkChar",
							phone.getAreaNo() + "-" + phone.getPhoneNo());
				}
			}

			serviceItemsDAO_19.pubCheckClientContactInfo(pMap);

			if ("N".equals(pMap.get("flag"))) {

				phone.setInvalidReason(phone.getAreaNo() == null ? phone
						.getPhoneNo() + "此电话号码格式不正确" : phone.getAreaNo() + "-"
						+ phone.getPhoneNo() + "此电话号码格式不正确");
			}

		}
		return phoneList;
	}

	/**
	 * 函件历史查询接口
	 * 
	 * @param paraMap
	 *            key-clientNo：客户号；key-startTime(Date)：开始时间；key-endTime(Date)：
	 *            结束时间；key-noteType：通知书类型；
	 * @return List<Map<String,Object>> Key:CLIENT_NO --客户号 KEY:POLICY_NO --保单号
	 *         KEY:PRODUCT_CODE --险种代码 KEY:FULL_NAME --险种名称
	 *         KEY:DETAIL_SEQUENCE_NO --通知书序号 KEY:BASE_SUM_INS --保额
	 *         KEY:EFFECT_DATE --生效日期 KEY:NOTE_TYPE --通知书类型代码 KEY:DESCRIPTION
	 *         --通知书类型名称 KEY:IM_FILE_ID --附件IM服务器ID KEY:NOTE_CREATED_DATE --生成时间
	 *         KEY:PDF_UPLOAD_DATE --最后发送时间
	 * @author GaoJiaMing
	 * @time 2014-7-11
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.queryPosNoteHistory")
	public List<Map<String, Object>> queryPosNoteHistory(
			Map<String, Object> paraMap) {
		return webPosAcceptInterface.queryPosNoteHistory(paraMap);
	}

	/**
	 * 获取电子函件ID(电子函件下载接口)
	 * 
	 * @param policyNo
	 * @param detailSequenceNo
	 * @return String 失败：null 成功：fileId
	 * @author GaoJiaMing
	 * @time 2014-7-8
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.getLoadFileId")
	public String getLoadFileId(String policyNo, String detailSequenceNo) {
		return webPosAcceptInterface.getLoadFileId(policyNo, detailSequenceNo);
	}

	/**
	 * 电子函件重新发送
	 * 
	 * @param clientNo
	 * @param policyNo
	 * @param detailSequenceNo
	 * @return String 成功：Y 失败：错误信息
	 * @author GaoJiaMing
	 * @time 2014-7-8
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.resendELetter")
	public String resendELetter(String clientNo, String policyNo,
			String detailSequenceNo) {
		return webPosAcceptInterface.resendELetter(clientNo, policyNo,
				detailSequenceNo);
	}

	/**
	 * 函件发送记录查询接口
	 * 
	 * @param detailSequenceNo
	 * @return List<Map<String, Object>> KEY:EMAIL --邮箱地址 KEY:SEND_DATE --发送时间
	 *         KEY:BUSINESS_NO --业务号
	 * @author GaoJiaMing
	 * @time 2014-11-19
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.getMailLogByDetailSequenceNo")
	public List<Map<String, Object>> getMailLogByDetailSequenceNo(
			String detailSequenceNo) {
		return webPosAcceptInterface
				.getMailLogByDetailSequenceNo(detailSequenceNo);
	}

	/**
	 * 取消保单自动退保
	 * 
	 * @param policyNo
	 * @return Map<String, Object>
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.cancelAutoSurrender")
	public Map<String, Object> cancelAutoSurrender(String policyNo) {
		Map<String, Object> returnMap = new HashMap<String, Object>();

		returnMap = commonQueryDAO.cancelAutoSurrender(policyNo);
		return returnMap;
	}

	/**
	 * 保单还款经办信息录入
	 */
	public ServiceItemsInputDTO_9 queryServiceItemsInfoExtraToMccp_9(
			ServiceItemsInputDTO sii) {

		return serviceItemsDAO_9.queryServiceItemsInfo(sii);

	}

	public ServiceItemsInputDTO_33 queryServiceItemsInfoExtraToMccp_33(
			ServiceItemsInputDTO sii) {

		return (ServiceItemsInputDTO_33) serviceItemsDAO_33
				.queryServiceItemsInfo(sii);

	}

	public ServiceItemsInputDTO_13 queryServiceItemsInfoExtraToMccp_13(
			ServiceItemsInputDTO sii) {

		return (ServiceItemsInputDTO_13) serviceItemsDAO_13
				.queryServiceItemsInfoExtra_13(sii);

	}

	public ServiceItemsInputDTO_27 queryServiceItemsInfoExtraToMccp_27(
			ServiceItemsInputDTO sii) {

		return (ServiceItemsInputDTO_27) serviceItemsDAO_27
				.queryServiceItemsInfoExtra_27(sii);

	}

	/**
	 * 根据保单号查保单自垫及借款信息
	 * 
	 * @param policyNo
	 * @return Map<String, Object>queryServiceItemsInfoExtraToMccp_9
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.getAplLoanAndAplExtraFeeInfo")
	public Map<String, Object> getAplLoanAndAplExtraFeeInfo(String policyNo) {
		System.out.println("====================start=======================");
		System.out.println("时间==="
				+ PosUtils.formatDate(new Date(), "yyyy-MM-dd hh:mm:ss"));
		System.out
				.println("开始调ESB接口method=getAplLoanAndAplExtraFeeInfo,policyNo="
						+ policyNo);

		ServiceItemsInputDTO_9 itemsInputDTO_9 = serviceItemsDAO_9
				.getAplLoanAndAplExtraFeeInfo(policyNo);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("policyNo", policyNo);
		if (itemsInputDTO_9.getAplExtraFee() == null) {
			map.put("aplExtraFee", 0);
		} else {
			map.put("aplExtraFee", itemsInputDTO_9.getAplExtraFee());
		}
		map.put("aplInterestSum", itemsInputDTO_9.getAplInterestSum());
		map.put("aplLoanAllSum", itemsInputDTO_9.getAplLoanAllSum());
		map.put("interestSum", itemsInputDTO_9.getInterestSum());
		map.put("loanPayBackSum", itemsInputDTO_9.getLoanPayBackSum());
		map.put("loanAllSum", itemsInputDTO_9.getLoanAllSum());

		System.out.println("调ESB接口method=getAplLoanAndAplExtraFeeInfo,posNo="
				+ policyNo + ",返回结果：result=" + map.toString());
		System.out.println("======================end======================");
		return map;
	}

	/**
	 * 根据保单号查询该保单下可以做收付款方式调整失败的所有保全项目
	 * 
	 * @param policyNo
	 * @return Map<String, Object>
	 */
	@SuppressWarnings("unchecked")
	@EsbMethod(esbServiceId = "com.sinolife.pos.getPayServiceItemsByPolicyNo")
	public List<Map<String, Object>> getPayServiceItemsByPolicyNo(
			String policyNo) {
		System.out.println("=======================start================");
		System.out.println("时间==="
				+ PosUtils.formatDate(new Date(), "yyyy-MM-dd hh:mm:ss"));
		System.out
				.println("开始调ESB接口method=getPayServiceItemsByPolicyNo,policyNo="
						+ policyNo);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		list = serviceItemsDAO_33.queryPremInfosByPolicyNo(policyNo);

		System.out
				.println("调ESB接口method=getOriginalPaymentInfoByPosNo,policyNo="
						+ policyNo + ",返回结果：result=" + list.toString());
		System.out.println("======================end====================");
		return list;
	}

	/**
	 * 根据保全号查询原收付款信息
	 * 
	 * @param policyNo
	 * @return Map<String, Object>
	 */
	@SuppressWarnings("unchecked")
	@EsbMethod(esbServiceId = "com.sinolife.pos.getOriginalPaymentInfoByPosNo")
	public Map<String, Object> getOriginalPaymentInfoByPosNo(String posNo) {
		System.out.println("=======================start================");
		System.out.println("时间==="
				+ PosUtils.formatDate(new Date(), "yyyy-MM-dd hh:mm:ss"));
		System.out
				.println("开始调ESB接口method=getOriginalPaymentInfoByPosNo,posNo="
						+ posNo);
		Map<String, Object> map = serviceItemsDAO_33
				.queryPremInfosByPosNo(posNo);
		String stringData = "null";
		if (map != null) {
			stringData.toString();
		}
		System.out.println("调ESB接口method=getOriginalPaymentInfoByPosNo,posNo="
				+ posNo + ",返回结果：result=" + stringData);
		System.out.println("======================end====================");
		return map;
	}

	/**
	 * 根据条件查询可取消转账暂停数据
	 * 
	 * @param policyNo
	 * @return Map<String, Object>
	 */
	@SuppressWarnings("unchecked")
	@EsbMethod(esbServiceId = "com.sinolife.pos.getCancelTransferStopData")
	public List<Map<String, Object>> getCancelTransferStopData(String clientNo,
			String posNo, String policyNo) {
		System.out.println("=======================start===================");
		System.out.println("时间==="
				+ PosUtils.formatDate(new Date(), "yyyy-MM-dd hh:mm:ss"));
		System.out.println("开始调ESB接口method=getCancelTransferStopData,clientNo="
				+ clientNo + ",posNo=" + posNo + ",policyNo=" + policyNo);

		List<Map<String, Object>> posInfos = new ArrayList<Map<String, Object>>();
		posInfos = unsuspendBankAccountDAO
				.getPosInfoByPosNoForUnsuspendBankAccountData(clientNo, posNo,
						policyNo);

		System.out.println("调ESB接口method=getCancelTransferStopData,clientNo="
				+ clientNo + ",posNo=" + posNo + ",policyNo=" + policyNo
				+ ",返回结果：result=" + posInfos.toString());
		System.out.println("======================end=======================");
		return posInfos;
	}

	/**
	 * 取消转账暂停操作
	 * 
	 * @param policyNo
	 * @return Map<String, Object>
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.cancelTransferStopOperation")
	public Map<String, Object> cancelTransferStopOperation(String posNo) {
		Map<String, Object> map = new HashMap<String, Object>();
		boolean succssFailFlag = false;
		try {
			System.out.println("=======================start================");
			System.out.println("时间==="
					+ PosUtils.formatDate(new Date(), "yyyy-MM-dd hh:mm:ss"));
			System.out
					.println("开始调ESB接口method=cancelTransferStopOperation,posNo="
							+ posNo);
			unsuspendBankAccountService.unsuspendBankAccount(posNo);
			map.put("result", true);
            map.put("message", "");
		} catch (Exception e) {
			map.put("result", false);
			map.put("message", e.getMessage());
		}
		System.out.println("调ESB接口method=cancelTransferStopOperation,posNo="
				+ posNo + ",返回结果：result=" + map.toString());
		System.out.println("======================end====================");
		return map;
	}

	/**
	 * VIP赠险回访 查询客户VIP信息
	 * 
	 * @param clientNo
	 *            客户号 key vipEffectDate VIP评定日 key vipGrade VIP等级 key
	 *            vipGradeName VIP等级描述 key vipPlan VIP方案
	 * @return List<Map<String, Object>>
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.getClientVIPInfoByClientNo")
	public Map<String, Object> getClientVIPInfoByClientNo(String clientNo) {
		System.out.println("===================start========================");
		System.out.println("时间==="
				+ PosUtils.formatDate(new Date(), "yyyy-MM-dd hh:mm:ss"));
		System.out
				.println("开始调ESB接口method=getClientVIPInfoByClientNo,clientNo="
						+ clientNo);

		Map<String, Object> map = vipManageService
				.queryPayFailPolicyInfoByClientNo(clientNo);
		String stringData = "null";
		if (map != null) {
			stringData.toString();
		}
		System.out.println("调ESB接口method=getClientVIPInfoByClientNo,param="
				+ clientNo + ",返回结果：result=" + stringData);
		System.out.println("======================end=======================");
		return map;
	}

	/**
	 * 收付款失败回访 查询保单保全列表
	 * 
	 * @param clientNo
	 *            客户号 key posNo 保单号 key policyNo 保全号 key projectName 保全项目 key
	 *            acceptDate 受理日期 key acceptStatusCode 受理状态 key
	 *            acceptStatusDescription 受理状态描述
	 * @return List<Map<String, Object>>
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.getPayFailPolicyInfoByClientNo")
	public List<Map<String, Object>> getPayFailPolicyInfoByClientNo(
			String clientNo) {
		System.out.println("=======================start====================");
		System.out.println("时间==="
				+ PosUtils.formatDate(new Date(), "yyyy-MM-dd hh:mm:ss"));
		System.out
				.println("开始调ESB接口method=getPayFailPolicyInfoByClientNo,clientNo="
						+ clientNo);

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		data = serviceItemsDAO_33.queryPayFailPremPosInfoByClientNo(clientNo);

		System.out.println("调ESB接口methos=getPayFailPolicyInfoByClientNo,param="
				+ clientNo + ",返回结果：result=" + data.toString() + ",返回结果数量："
				+ data.size());
		System.out.println("=======================end======================");
		return data;
	}

	@EsbMethod(esbServiceId = "com.sinolife.pos.getFrequencyByPolicyNo")
	public List<CodeTableItemDTO> getFrequencyByPolicyNo(String policyNo) {
		System.out.println("=======================start====================");
		System.out.println("时间==="
				+ PosUtils.formatDate(new Date(), "yyyy-MM-dd hh:mm:ss"));
		System.out.println("开始调ESB接口method=getFrequencyByPolicyNo,policyNo="
				+ policyNo);
		List<CodeTableItemDTO> list = new ArrayList<CodeTableItemDTO>();
		try {
			String frequencys = this.serviceItemsDAO_13
					.getFrequencyByPolicyNo(policyNo);
			System.out.println("=========frequencys=" + frequencys);
			if (frequencys != null && !frequencys.isEmpty()) {
				String[] frequencyArray = frequencys.split(";");
				List<CodeTableItemDTO> options = this
						.queryCodeTableList("FREQUENCY");

				for (CodeTableItemDTO info : options) {
					for (int i = 0; i < frequencyArray.length; i++) {
						String frequency = frequencyArray[i];
						if (info.getCode().equals(frequency)) {
							CodeTableItemDTO codeInfo = new CodeTableItemDTO();
							codeInfo.setCode(frequency);
							codeInfo.setDescription(info.getDescription());
							list.add(codeInfo);
						}
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("查询缴费频次异常:" + e.getMessage());
		}

		System.out.println("调ESB接口methos=getFrequencyByPolicyNo,param="
				+ policyNo + ",返回结果：result=" + list.toString());
		System.out.println("=======================end======================");
		return list;
	}

	@EsbMethod(esbServiceId = "com.sinolife.pos.getProductPremListByPolicyNo")
	public List<PolicyProductPremForMccDTO> getProductPremListByPolicyNo(
			String policyNo) {
		System.out.println("=======================start====================");
		System.out.println("时间==="
				+ PosUtils.formatDate(new Date(), "yyyy-MM-dd hh:mm:ss"));
		System.out
				.println("开始调ESB接口method=getProductPremListByPolicyNo,policyNo="
						+ policyNo);

		List<PolicyProductPremForMccDTO> data = new ArrayList<PolicyProductPremForMccDTO>();
		data = serviceItemsDAO_13.getProductPremListByPolicyNo(policyNo);

		System.out.println("调ESB接口methos=getProductPremListByPolicyNo,param="
				+ policyNo + ",返回结果：result=" + data.toString() + ",返回结果数量："
				+ data.size());
		System.out.println("=======================end======================");
		return data;
	}

	@EsbMethod(esbServiceId = "com.sinolife.pos.getPrimaryPlanYearRateByPolicyNo")
	public BigDecimal getPrimaryPlanYearRateByPolicyNo(String policyNo) {
		return commonQueryDAO.getPrimaryPlanYearRateByPolicyNo(policyNo);
	}

}
