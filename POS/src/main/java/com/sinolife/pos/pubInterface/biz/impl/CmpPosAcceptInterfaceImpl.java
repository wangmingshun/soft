package com.sinolife.pos.pubInterface.biz.impl;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.sinolife.pos.acceptance.branch.service.BranchAcceptService;
import com.sinolife.pos.acceptance.branch.service.BranchQueryService;
import com.sinolife.pos.common.dao.ClientInfoDAO;
import com.sinolife.pos.common.dao.CommonAcceptDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dao.PosCodeTableDAO;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_1;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_13;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_17;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_2;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_21;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_23;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_9;
import com.sinolife.pos.common.dto.BankInfoDTO;
import com.sinolife.pos.common.dto.ClientAccountDTO;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.common.dto.PosApplyFilesDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.PosStatusChangeHistoryDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_1;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_13;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_17;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_2;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_21;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_23;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_9;
import com.sinolife.pos.common.include.dao.IncludeDAO;
import com.sinolife.pos.pubInterface.biz.dto.PosApplyInfoDTO;
import com.sinolife.pos.pubInterface.biz.service.CmpPosAcceptInterface;
import com.sinolife.pos.rpc.ilogjrules.posrules.POSJrulesCheck;
import com.sinolife.pos.todolist.acceptentry.dao.AcceptEntryDAO;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMRuleInfoDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSVerifyResultDto;

@Service("cmpPosAcceptInterface")
public class CmpPosAcceptInterfaceImpl implements CmpPosAcceptInterface,
		InitializingBean {
	@Autowired
	private BranchAcceptService branchAcceptService;
	@Autowired
	private IncludeDAO includeDAO;
	@Autowired
	private PosCodeTableDAO posCodeTableDAO;
	@Autowired
	private BranchQueryService branchQueryService;

	@Autowired
	private ClientInfoDAO clientInfoDAO;

	@Autowired
	private CommonQueryDAO commonQueryDAO;

	@Autowired
	private POSJrulesCheck posJrulesCheck;

	@Autowired
	private TransactionTemplate txTmpl;

	@Autowired
	private CommonAcceptDAO acceptDAO;

	private DevelopPlatformInterfaceImpl developPlatformInterfaceImpl;
	@Autowired
	private AcceptEntryDAO acceptEntryDAO;
	@Autowired
	private ServiceItemsDAO_23 serviceItemsDAO_23;
	@Autowired
	private ServiceItemsDAO_9 serviceItemsDAO_9;
	@Autowired
	private ServiceItemsDAO_2 serviceItemsDAO_2;
	@Autowired
	private ServiceItemsDAO_1 serviceItemsDAO_1;
	@Autowired
	private ServiceItemsDAO_13 serviceItemsDAO_13;
	@Autowired
	private ServiceItemsDAO_17 serviceItemsDAO_17;
	@Autowired
	private ServiceItemsDAO_21 serviceItemsDAO_21;
	/** CMP虚拟用户 */
	public static final String CMP_VIRTUAL_USER = "cmpuser";

	/** CMP虚拟柜面 */
	public static final String CMP_VIRTUAL_COUNTER = "901";

	Logger logger = Logger.getLogger(CmpPosAcceptInterfaceImpl.class.getName());

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
	 * 查询基表
	 */
	public List queryCodeTableList(String codeTableName) {
		List listMap = new ArrayList();
		List codeTableList = (List<CodeTableItemDTO>) posCodeTableDAO
				.queryCodeTable(codeTableName);
		CodeTableItemDTO codeTableDto = new CodeTableItemDTO();
		for (int i = 0; i < codeTableList.size(); i++) {
			Map<String, String> map = new HashMap<String, String>();
			codeTableDto = (CodeTableItemDTO) codeTableList.get(i);
			map.put("CODE", codeTableDto.getCode());
			map.put("DESCRIPTION", codeTableDto.getDescription());
			listMap.add(map);
		}
		return listMap;
	}

	public List queryBranchTreeRoot(Map pMap) {
		return includeDAO.queryBranchTreeRoot(pMap);
	}

	public List queryBranchTree(Map pMap) {
		return includeDAO.queryBranchTree(pMap);
	}

	public List queryCityByProvince(String province) {
		return includeDAO.queryCityByProvince(province);
	}

	public List queryAreaByCity(String city) {
		return includeDAO.queryAreaByCity(city);
	}

	public List queryBranchInProvinceCity(String province, String city,
			String channel) {
		return includeDAO.queryBranchInProvinceCity(province, city, channel);
	}

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
	public List queryBankList(String countryCode, String provinceCode,
			String cityCode, String bankCategory) {
		List listMap = new ArrayList();
		BankInfoDTO bankInfoDTO = new BankInfoDTO();
		List bankList = (List<BankInfoDTO>) includeDAO.queryBankList(
				countryCode, provinceCode, cityCode, bankCategory).get(
				"p_array_bank_info_items");
		for (int i = 0; i < bankList.size(); i++) {
			Map<String, String> map = new HashMap<String, String>();
			bankInfoDTO = (BankInfoDTO) bankList.get(i);
			map.put("BANKCODE", bankInfoDTO.getBankCode());// 银行代码
			map.put("DESCRIPTION", bankInfoDTO.getDescription());// 描述
			map.put("BANKABBR", bankInfoDTO.getBankAbbr());// 银行简称
			map.put("BANKCATEGORY", bankInfoDTO.getBankCategory());// 银行大类编码
			map.put("COUNTRYCODE", bankInfoDTO.getCountryCode());// 国家代码
			map.put("PROVICECODE", bankInfoDTO.getProvinceCode());// 省代码
			map.put("CITYCODE", bankInfoDTO.getCityCode());// 市代码
			listMap.add(map);
		}
		return listMap;
	}

	/**
	 * 生成CMP用的条码
	 * 
	 * @param serviceItems
	 * @return
	 */
	public String generateCMParcode(String serviceItems) {
		// 条形码统一改为sequence生成方
		return "CMPN" + commonQueryDAO.queryBarcodeNoSequence();
	}

	/**
	 * 根据证件号码查询
	 * 
	 * @param idTypeCode
	 *            id类型
	 * @param idNo
	 * @return
	 */
	public List<Map<String, Object>> getClientinfoProById(String idTypeCode,
			String idNo) {
		return developPlatformInterfaceImpl.getClientinfoProById(idTypeCode,
				idNo);
	}

	/**
	 * 根据客户姓名查询
	 * 
	 * @param clientName
	 *            客户性别
	 * @param sex
	 *            性别
	 * @param birthday
	 *            生日
	 * @return
	 */
	public List<Map<String, Object>> getClientinfoProbyName(String clientName,
			String sex, Date birthday) {
		return developPlatformInterfaceImpl.getClientinfoProbyName(clientName,
				sex, birthday);
	}

	/**
	 * 根据客户号查询
	 * 
	 * @param clientNo
	 *            客户号
	 * @return
	 */
	public List<Map<String, Object>> getClientinfoProbyClientNo(String clientNo) {
		return developPlatformInterfaceImpl
				.getClientinfoProbyClientNo(clientNo);
	}

	/**
	 * 根据保单号查询
	 * 
	 * @param policyNO
	 * @param applicantOrInsured
	 *            投，被保险人
	 * @return
	 */
	public List<Map<String, Object>> getClientinfoProbyPolicyNo(
			String policyNO, String applicantOrInsured) {
		return developPlatformInterfaceImpl.getClientinfoProbyPolicyNo(
				policyNO, applicantOrInsured);
	}

	/**
	 * 根据保单号查询客户信息
	 * 
	 * @param policyNO
	 * @param applicantOrInsured
	 *            投，被保险人
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getClientinfobyPolicyNo(String policyNO,
			String applicantOrInsured) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		ClientInformationDTO clientInfo = new ClientInformationDTO();
		List<ClientInformationDTO> retList = new ArrayList<ClientInformationDTO>();
		if (StringUtils.isBlank(applicantOrInsured)
				|| "A".equals(applicantOrInsured)) {
			String clientNo = commonQueryDAO.getApplicantByPolicyNo(policyNO);
			clientInfo = clientInfoDAO.queryClientInfoByClientNo(clientNo);
			Map<String, Object> clientMap = new HashMap<String, Object>();
			clientMap.put("CLIENT_NO", clientInfo.getClientNo());
			clientMap.put("CLIENT_NAME", clientInfo.getClientName());
			clientMap.put("BIRTHDAY", clientInfo.getBirthday());
			clientMap.put("SEX_CODE", clientInfo.getSexCode());
			clientMap.put("ID_TYPE", clientInfo.getIdTypeCode());
			clientMap.put("IDNO", clientInfo.getIdNo());
			clientMap.put("IDNO_VALIDITY_DATE",
					clientInfo.getIdnoValidityDate());
			clientMap.put("COUNTRY_CODE", clientInfo.getCountryCode());
			clientMap.put("EDUCATION_CODE", clientInfo.getEducationCode());
			clientMap.put("MARRIAGE_CODE", clientInfo.getMarriageCode());
			clientMap.put("WORK_UNIT", clientInfo.getWorkUnit());
			clientMap.put("POSITION", clientInfo.getPosition());
			clientMap.put("DEATH_DATE", clientInfo.getDeathDate());
			clientMap.put("REGISTER_PLACE", clientInfo.getRegisterPlace());
			clientMap.put("PHONETICIZE_FIRSTNAME",
					clientInfo.getPhoneticizeFirstName());
			clientMap.put("PHONETICIZE_LASTNAME",
					clientInfo.getPhoneticizeLastName());
			list.add(clientMap);
		}
		if (StringUtils.isBlank(applicantOrInsured)
				|| "I".equals(applicantOrInsured)) {
			List<String> clientNoList = commonQueryDAO
					.getInsuredByPolicyNo(policyNO);
			if (clientNoList != null && !clientNoList.isEmpty()) {
				for (String clientNo : clientNoList) {
					clientInfo = clientInfoDAO
							.queryClientInfoByClientNo(clientNo);
					Map<String, Object> clientMap = new HashMap<String, Object>();
					clientMap.put("CLIENT_NO", clientInfo.getClientNo());// 客户号
					clientMap.put("CLIENT_NAME", clientInfo.getClientName());// 姓名
					clientMap.put("BIRTHDAY", clientInfo.getBirthday());// 生日
					clientMap.put("SEX_CODE", clientInfo.getSexCode());// 性别代码
					clientMap.put("ID_TYPE", clientInfo.getIdTypeCode());// 证件类型代码
					clientMap.put("IDNO", clientInfo.getIdNo());// 证件号
					clientMap.put("IDNO_VALIDITY_DATE",
							clientInfo.getIdnoValidityDate());// 证件有效期
					clientMap.put("COUNTRY_CODE", clientInfo.getCountryCode());// 国家代码
					clientMap.put("EDUCATION_CODE",
							clientInfo.getEducationCode());// 学历代码
					clientMap
							.put("MARRIAGE_CODE", clientInfo.getMarriageCode());// 婚姻状况代码
					clientMap.put("WORK_UNIT", clientInfo.getWorkUnit());// 工作单位
					clientMap.put("POSITION", clientInfo.getPosition());// 工作职务
					clientMap.put("DEATH_DATE", clientInfo.getDeathDate());// 死亡时间
					clientMap.put("REGISTER_PLACE",
							clientInfo.getRegisterPlace());// 户籍
					clientMap.put("PHONETICIZE_FIRSTNAME",
							clientInfo.getPhoneticizeFirstName());// 拼音
					clientMap.put("PHONETICIZE_LASTNAME",
							clientInfo.getPhoneticizeLastName());// 姓拼音
					list.add(clientMap);
				}
			}
		}
		return list;
	}

	/***
	 * 根据保单号查险种信息
	 * 
	 * @param posNo
	 * @return
	 */
	public List<Map<String, String>> ProductInfoListByPolicyNO(String policy_no) {
		List<Map<String, String>> prodcutlist = new ArrayList<Map<String, String>>();
		prodcutlist = commonQueryDAO.ProductInfoListByPolicyNo(policy_no);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		int prdSize = prodcutlist.size();
		for (int i = 0; i < prdSize; i++) {
			Map<String, String> retMap = prodcutlist.get(i);
			String prod_seq = String.valueOf(retMap.get("PROD_SEQ"));
			Map<String, Object> premMap = commonQueryDAO.queryProdctPremInfo(
					policy_no, prod_seq);
			Date prem_mdate = (Date) premMap.get("p_prem_mdate");
			Number due_prem_sum = (java.math.BigDecimal) premMap
					.get("p_due_prem_sum");
			Number due_prem_term = (java.math.BigDecimal) premMap
					.get("p_due_prem_term");
			String project_name = (String) premMap.get("p_project_name");
			retMap.put("PREM_MDATE", format.format(prem_mdate));// 交费止期
			retMap.put("DUE_PREM_SUM", String.valueOf(due_prem_sum));// 已交保费
			retMap.put("DUE_PREM_TERM", String.valueOf(due_prem_term));// 已交保费期数
			retMap.put("PROJECT_NAME", project_name);// 项目
			// prodcutlist.add(retMap);
		}
		return prodcutlist;
	}

	/***
	 * 根据保单号查询保单账号信息
	 * 
	 * @param posNo
	 * @return
	 */
	public Map<String, Object> queryAccountInfoByPolicyNo(String policy_no) {

		return commonQueryDAO.queryAccountInfo(policy_no);
	}

	/**
	 * 写入批次记录、申请书记录，及保全记录
	 * 
	 * @param applyInfo
	 * @return
	 */
	public Map<String, Object> createBatch(Map<String, Object> applyInfoMap) {
		PosApplyInfoDTO applyInfo = new PosApplyInfoDTO();
		MapToBean(applyInfo, (Map) applyInfoMap.get("POSAPPLYINFO"));
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
		String clientNo = applyInfo.getClientNo();
		String policyNo = applyInfo.getPolicyNo();
		String barcode = applyInfo.getBarCode();
		Map clientMap = new HashMap();
		clientMap = commonQueryDAO.QueryClientInfoByclientNo(clientNo);
		String clientName = (String) clientMap.get("CLIENT_NAME");// 客户姓名
		String idtype = (String) clientMap.get("ID_TYPE");// 证件类型
		String idno = (String) clientMap.get("IDNO");// 证件号码
		// 如果传入的条形码为空则重新生成
		if (StringUtils.isBlank(barcode)) {
			barcode = generateCMParcode(applyInfo.getServiceItems());
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
		/*
		 * if(StringUtils.isBlank(paymentType)) { file.setPaymentType("1");//现金
		 * }
		 */
		if ("2".equals(paymentType)) {
			List<Map<String, Object>> accountList = commonQueryDAO
					.queryAccountByPolicyNo(policyNo);
			if (accountList.size() == 0) {
				throw new RuntimeException("收付方式为转账，但找不到原缴费账号！");
			} else {
				Map accountMap = accountList.get(0);
				file.setBankCode((String) accountMap.get("BANK_CODE"));
				file.setTransferAccountno((String) accountMap.get("ACCOUNT_NO"));
				file.setTransferAccountOwner((String) accountMap
						.get("CLIENT_ACCOUNT"));
				file.setAccountNoType((String) accountMap
						.get("ACCOUNT_NO_TYPE"));
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
				applyInfoMap.put("FLAG", "N");
				applyInfoMap.put("RULEMSG", sb.toString());
				return applyInfoMap;
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
		detail.setObjectValue(applyInfo.getAcceptor());
		detail.setNewValue(applyInfo.getAcceptor());
		detail.setOldValue(applyInfo.getAcceptor());
		acceptDAO.insertPosAcceptDetail(detail);
		applyInfoMap.put("FLAG", "Y");
		applyInfoMap.put("POSNO", batch.getPosInfoList().get(0).getPosNo());
		return applyInfoMap;
	}

	/**
	 * 保全受理，从申请书录入一直到生效的全部过程都在这里（传入Bean）
	 * 
	 * @param sii
	 * @param applyInfo
	 * @return
	 */
	public Map<String, Object> acceptInternal(ServiceItemsInputDTO sii,
			String posNo, String barcodeNo) {

		Map<String, Object> retMap = new HashMap<String, Object>();
		String SL_RSLT_MESG = null;
		String SL_RSLT_FLAG = null;
		String APPROVE_TEXT = null;
		try {
			// PosApplyBatchDTO batch = createBatchHeader(applyInfo);
			PosInfoDTO posInfo = new PosInfoDTO();
			String batchNo = commonQueryDAO.queryPosBatchNoByPosNo(posNo);
			sii.setPosNo(posNo);
			sii.setPosNoList(null);
			sii.setBarcodeNo(barcodeNo);
			sii.setBatchNo(batchNo);
			ServiceItemsInputDTO oriItemsInputDTO = branchQueryService
					.queryAcceptDetailInputByPosNo(posNo);
			sii.setOriginServiceItemsInputDTO(oriItemsInputDTO
					.getOriginServiceItemsInputDTO());
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
				statusChange.setChangeUser(CMP_VIRTUAL_USER);
				branchAcceptService.insertPosStatusChangeHistory(statusChange);
				if (!"0".equals(statusChange.getFlag())) {
					SL_RSLT_MESG = "写保全状态变更记录失败：" + statusChange.getMessage();
					branchAcceptService.cancelAccept(posInfo, CMP_VIRTUAL_USER);
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
					SL_RSLT_FLAG = "N";
					SL_RSLT_MESG = msg;
					// throw new RuntimeException(msg);
				} else if ("A19".equals(flag)) {
					SL_RSLT_FLAG = "Y";
					posInfo = commonQueryDAO.queryPosInfoRecord(posNo);
					APPROVE_TEXT = posInfo.getApproveText();// 批文
				} else if ("A03".equals(flag)) {
					throw new RuntimeException(resultDesc);
				}
			}
		} catch (Exception e) {
			SL_RSLT_FLAG = "N";
			SL_RSLT_MESG = e.getMessage();
		}
		retMap.put("SL_RSLT_MESG", SL_RSLT_MESG);
		retMap.put("SL_RSLT_FLAG", SL_RSLT_FLAG);
		retMap.put("APPROVE_TEXT", APPROVE_TEXT);
		return retMap;
	}

	/**
	 * 经办结果确认
	 * 
	 * @param posNoList
	 * @param
	 * @return
	 */
	public Map<String, Object> processResultSubmit(
			List<Map<String, Object>> posNoList) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		for (int i = 0; posNoList != null && i < posNoList.size(); i++) {
			Map<String, Object> processResultMap = posNoList.get(i);
			// 流转接口
			String posNo = (String) posNoList.get(i).get("POS_NO");
			Map<String, Object> retMap = branchAcceptService.workflowControl(
					"05", posNo, false);
			logger.info("processResultSubmit workflowControl return:" + retMap);

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
			} else if ("A17".equals(flag)) {
				// 待保单打印
				resultMsg = "待保单打印";
			} else {
				resultMsg = msg;
			}
			resultMap.put("RESULT_MSG", resultMsg);
		}
		return resultMap;

	}

	/**
	 * 保全受理，从申请书录入一直到生效的全部过程都在这里（传入Map）
	 * 
	 * @param sii
	 * @param applyInfo
	 * @return
	 */
	public Map<String, Object> acceptInterl(Map<String, Object> accMap) {
		ServiceItemsInputDTO sii = new ServiceItemsInputDTO();
		String posNo = (String) accMap.get("POSNO");
		String barCode = (String) accMap.get("BARCODE");
		sii.setBarcodeNo(barCode);
		sii.setPosNo(posNo);
		sii.setApplyDate((String) accMap.get("APPLYDATE"));
		sii.setAcceptDate((String) accMap.get("APPLYDATE"));
		String serviceItems = (String) accMap.get("SERVICEITEMS");

		if ("23".equals(serviceItems)) {
			// 续期缴费方式变更
			sii = queryServiceItemsInfoExtraToBia_23(accMap);
		} else if ("2".equals(serviceItems)) {
			// 退保
			sii = queryServiceItemsInfoExtraToBia_2(accMap);
		} else if ("1".equals(serviceItems)) {
			// 犹豫期退保
			sii = queryServiceItemsInfoExtraToBia_1(accMap);
			sii.setServiceItems(serviceItems);
		} else if ("9".equals(serviceItems)) {
			// 保单还款
			sii = queryServiceItemsInfo_9(accMap);
			sii.setServiceItems(serviceItems);
		} else if ("13".equals(serviceItems)) {
			// 交费频次变更
			sii = queryServiceItemsInfo_13(accMap);
			sii.setServiceItems(serviceItems);
			sii.setClientNo((String) accMap.get("CLIENTNO"));
			sii.setApplyDate((String) accMap.get("APPLYDATE"));
		} else if ("17".equals(serviceItems)) {
			// 续期保费退费
			sii = queryServiceItemsInfo_17(accMap);
			sii.setServiceItems(serviceItems);
			sii.setClientNo((String) accMap.get("CLIENTNO"));
			sii.setApplyDate((String) accMap.get("APPLYDATE"));
		} else if ("21".equals(serviceItems)) {
			// 客户资料变更
			sii = queryServiceItemsInfoExtra_21(accMap);
			sii.setServiceItems(serviceItems);
			sii.setClientNo((String) accMap.get("CLIENTNO"));
			sii.setApplyDate((String) accMap.get("APPLYDATE"));
		}

		return accMap = acceptInternal(sii, posNo, barCode);
	}

	/**
	 * 保全受理规则处理接口
	 * 
	 * @param policyNo
	 *            保单号
	 * @param applyDate
	 *            申请时间
	 * @param acceptor
	 *            受理人
	 * @param applyTypeCode
	 *            受理类型
	 * @param acceptChannelCode
	 *            受理渠道
	 * @param currentServiceItems
	 *            批次一个申请的保全项目 格式如: "18,19"
	 */
	// public Map<String, String> acceptRulesCheck(String policyNo,
	// String clientNo, Date applyDate, String acceptor,
	// String applyTypeCode, String acceptChannelCode,
	// String currentServiceItems){
	// return developPlatformInterfaceImpl.acceptRulesCheck(policyNo, clientNo,
	// applyDate,
	// acceptor, applyTypeCode, acceptChannelCode, currentServiceItems);
	// }

	/**
	 * 经办结果撤销
	 * 
	 * @param cancelCause
	 * @param cancelRemark
	 * @param processResultList
	 * @param itemsInputDTO
	 * @return
	 */
	public Map<String, Object> processResultCancel(String cancelCause,
			String cancelRemark, List<Map<String, Object>> processResultList) {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		for (int i = 0; processResultList != null
				&& i < processResultList.size(); i++) {
			Map<String, Object> processResultMap = processResultList.get(i);
			String posNo = (String) processResultMap.get("POS_NO");
			// 更新取消原因
			branchAcceptService.updatePosInfo(posNo, "ROLLBACK_CAUSE",
					cancelCause);
			// 更新取消详细信息
			branchAcceptService.updatePosInfo(posNo, "ABANDONED_DETAIL",
					cancelRemark);
			PosInfoDTO posInfo = commonQueryDAO.queryPosInfoRecord(posNo);
			// 取消受理
			branchAcceptService.cancelAccept(posInfo, CMP_VIRTUAL_USER);
			processResultMap.put("RESULT_MSG", "已取消");
		}
		return resultMap;
	}

	/**
	 * 受理撤销，对于已经是结束状态的受理，强制改掉受理状态为C02
	 * 
	 * @param posInfo
	 * @param acceptUser
	 */
	public void cancelAccept(PosInfoDTO posInfo, String acceptUser) {
		developPlatformInterfaceImpl.cancelAccept(posInfo, acceptUser);
	}

	/**
	 * 受理撤销并重新生成受理
	 * 
	 * @param posNo
	 * @param rollbackCause
	 *            撤销原因
	 * @param rollbackCauseDetail
	 *            撤销原因描述
	 * @param acceptUser
	 *            受理用户
	 * @return
	 */
	public Map<String, Object> cancelAndReaccept(String posNo,
			String rollbackCause, String rollbackCauseDetail) {
		// 先将该批次的所有记录均锁定
		String posBatchNo = commonQueryDAO.queryPosBatchNoByPosNo(posNo);
		List<PosInfoDTO> posInfoList = acceptDAO
				.queryAndLockPosInfoByPosBatchNo(posBatchNo);

		// 将该受理之后的不同条码，不同保全项目的受理序号均加1，以腾出当前的受理序号
		PosInfoDTO foundPosInfo = null; // 批次中找到当前受理
		List<PosInfoDTO> posInfoListNeedAdjustSequence = new ArrayList<PosInfoDTO>(); // 需要调整顺序的受理
		List<PosInfoDTO> posInfoListNeedCancel = new ArrayList<PosInfoDTO>(); // 需要撤销的受理
		int lastSequence = 0; // 同条码同保全项目的最后一个受理
		if (posInfoList != null && !posInfoList.isEmpty()) {
			for (int i = 0; i < posInfoList.size(); i++) {
				PosInfoDTO tmpPosInfo = posInfoList.get(i);
				String tmpPosNo = tmpPosInfo.getPosNo();
				if (tmpPosNo.equals(posNo)) {
					foundPosInfo = tmpPosInfo;
					lastSequence = foundPosInfo.getAcceptSeq();
					posInfoListNeedCancel.add(tmpPosInfo);
				} else if (foundPosInfo != null) {
					// 从当前受理之后的受理，需要判断是否存在同保全项目，同条码的的受理，这些受理一般是受理时勾选其他保单产生的，也需要一并撤销掉
					if (tmpPosInfo.getServiceItems().equals(
							foundPosInfo.getServiceItems())
							&& tmpPosInfo.getBarcodeNo().equals(
									foundPosInfo.getBarcodeNo())
							&& posInfoListNeedAdjustSequence.isEmpty()) {
						lastSequence = tmpPosInfo.getAcceptSeq();
						posInfoListNeedCancel.add(tmpPosInfo);
					} else {
						posInfoListNeedAdjustSequence.add(tmpPosInfo);
					}
				}
			}
		}
		if (foundPosInfo == null)
			throw new RuntimeException("找不到受理记录，受理号：" + posNo);

		// 对所有需要调整顺序的保全受理进行序号加1的操作
		for (PosInfoDTO tmpPosInfo : posInfoListNeedAdjustSequence) {
			acceptDAO.updatePosInfo(tmpPosInfo.getPosNo(), "ACCEPT_SEQ",
					String.valueOf(tmpPosInfo.getAcceptSeq().intValue() + 1));
		}
		PosInfoDTO newPosInfo = new PosInfoDTO();
		Date sysdate = commonQueryDAO.getSystemDate();
		newPosInfo.setAcceptor(foundPosInfo.getAcceptor());
		newPosInfo.setAcceptStatusCode("A01"); // 受理状态：批次受理
		newPosInfo.setAccountNo(foundPosInfo.getAccountNo());
		// 如果转账查询卡类型
		if ("2".equals(foundPosInfo.getPaymentType())) {
			List<Map<String, Object>> accountList = commonQueryDAO
					.queryAccountByPolicyNo(foundPosInfo.getPolicyNo());
			Map accountMap = accountList.get(0);
			foundPosInfo.setAccountNoType((String) accountMap
					.get("ACCOUNT_NO_TYPE"));
		}
		;
		// 插入卡类型
		newPosInfo.setAccountNoType(foundPosInfo.getAccountNoType());
		newPosInfo.setBankCode(foundPosInfo.getBankCode());
		newPosInfo.setClientNo(foundPosInfo.getClientNo());
		newPosInfo.setPaymentType(foundPosInfo.getPaymentType());
		newPosInfo
				.setForeignExchangeType(foundPosInfo.getForeignExchangeType());
		newPosInfo.setIdType(foundPosInfo.getIdType());
		newPosInfo.setPremName(foundPosInfo.getPremName());
		newPosInfo.setPremIdno(foundPosInfo.getPremIdno());
		newPosInfo.setAcceptDate(foundPosInfo.getAcceptDate());
		newPosInfo.setPolicyNo(foundPosInfo.getPolicyNo());
		newPosInfo.setServiceItems(foundPosInfo.getServiceItems());
		newPosInfo.setAcceptSeq(lastSequence + 1);
		newPosInfo.setBarcodeNo(foundPosInfo.getBarcodeNo());
		if (StringUtils.isBlank(foundPosInfo.getRemark())) {
			newPosInfo.setRemark(rollbackCauseDetail);
		} else {
			newPosInfo.setRemark(foundPosInfo.getRemark() + ";"
					+ rollbackCauseDetail);
		}
		acceptDAO.insertPosInfo(newPosInfo);

		PosAcceptDetailDTO detail;
		detail = new PosAcceptDetailDTO();
		detail.setPosNo(newPosInfo.getPosNo());
		detail.setPosObject("5");
		detail.setProcessType("1");
		detail.setItemNo("016");
		detail.setObjectValue(CMP_VIRTUAL_USER);
		detail.setNewValue(CMP_VIRTUAL_USER);// 受理人员
		detail.setOldValue(CMP_VIRTUAL_USER);
		acceptDAO.insertPosAcceptDetail(detail);

		// 对保单置暂停
		acceptDAO.doPolicySuspend(newPosInfo.getPolicyNo(), "5",
				newPosInfo.getPosNo(), newPosInfo.getServiceItems(), sysdate);

		for (PosInfoDTO tmpPosInfo : posInfoListNeedCancel) {
			// 撤销受理
			this.cancelAccept(tmpPosInfo, CMP_VIRTUAL_USER);
			// 更新受理备注
			String remark = "该保全申请由于" + rollbackCauseDetail + "，新保全号为:"
					+ newPosInfo.getPosNo();
			if (StringUtils.isNotBlank(tmpPosInfo.getRemark())) {
				remark = tmpPosInfo.getRemark() + ";" + remark;
			}
			acceptDAO.updatePosInfo(tmpPosInfo.getPosNo(), "remark", remark);

			// 更新取消原因
			acceptDAO.updatePosInfo(posNo, "ROLLBACK_CAUSE", rollbackCause);

			// 更新取消详细信息
			acceptDAO.updatePosInfo(posNo, "ABANDONED_DETAIL",
					rollbackCauseDetail + "，新保全号为：" + newPosInfo.getPosNo());
		}
		// 将就保全号在pos_accept_detail记录的手机号，写到新的保全明细
		//

		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("posBatchNo", posBatchNo);
		retMap.put("newPosNo", newPosInfo.getPosNo());
		return retMap;
	}

	/**
	 * 犹豫期退保经办信息录入
	 */
	public ServiceItemsInputDTO_1 queryServiceItemsInfoExtraToBia_1(Map map) {
		String policyNo = (String) map.get("POLICYNO");
		String serviceItems = (String) map.get("SERVICEITEMS");
		String clientNo = (String) map.get("CLIENTNO");
		String applyDate = (String) map.get("APPLYDATE");
		ServiceItemsInputDTO_1 items1 = new ServiceItemsInputDTO_1();
		items1.setPolicyNo(policyNo);
		items1.setServiceItems(serviceItems);
		items1.setClientNo(clientNo);
		items1.setApplyDate(applyDate);
		items1 = (ServiceItemsInputDTO_1) serviceItemsDAO_1
				.queryServiceItemsInfoExtraToBia_1(items1);
		// 是否可疑交易
		items1.setDouBt(false);
		// 退保原因
		items1.setSurrenderCauseCode((String) map.get("SURRENDERCAUSECODE"));

		List<PolicyProductDTO> productList = items1.getPolicyProductList();
		for (int i = 0; productList != null && i < productList.size(); i++) {
			PolicyProductDTO product = productList.get(i);
			product.setSelected(true);
		}
		items1.setPolicyProductList(productList);
		return items1;
	}

	/**
	 * 退保经办信息录入
	 */
	public ServiceItemsInputDTO_2 queryServiceItemsInfoExtraToBia_2(Map map) {
		String policyNo = (String) map.get("POLICYNO");
		String isAllSurrender = (String) map.get("ISALLSURRENDER");
		String serviceItems = (String) map.get("SERVICEITEMS");
		String clientNo = (String) map.get("CLIENTNO");
		ServiceItemsInputDTO_2 items2 = new ServiceItemsInputDTO_2();
		items2.setPolicyNo(policyNo);
		items2.setClientNo(clientNo);
		items2.setServiceItems(serviceItems);
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
			product.setSelected(true);
		}
		items2.setProductList(productList);
		return items2;
	}

	/**
	 * 保单还款经办信息录入页面
	 */
	public ServiceItemsInputDTO_9 queryServiceItemsInfo_9(Map map) {
		ServiceItemsInputDTO_9 item9 = new ServiceItemsInputDTO_9();
		String policyNo = (String) map.get("POLICYNO");
		item9.setPolicyNo(policyNo);
		return serviceItemsDAO_9.queryServiceItemsInfo(item9);
	}

	/**
	 * 交费频次变更经办信息录入页面
	 */
	public ServiceItemsInputDTO_13 queryServiceItemsInfo_13(Map map) {
		ServiceItemsInputDTO_13 item13 = new ServiceItemsInputDTO_13();
		String policyNo = (String) map.get("POLICYNO");
		String frequency = (String) map.get("FREQUENCY");
		item13.setPolicyNo(policyNo);
		item13 = serviceItemsDAO_13.queryServiceItemsInfoExtra_13(item13);
		item13.setFrequency(frequency);
		return item13;

	};

	/**
	 * 续期保费退费经办信息录入页面
	 */
	public ServiceItemsInputDTO_17 queryServiceItemsInfo_17(Map map) {
		ServiceItemsInputDTO_17 item17 = new ServiceItemsInputDTO_17();
		String policyNo = (String) map.get("POLICYNO");
		item17.setPolicyNo(policyNo);
		item17.setRefundCauseCode((String) map.get("REFUNDCAUSECODE"));
		return serviceItemsDAO_17.queryServiceItemsInfoExtra_17(item17);
	};

	/**
	 * 续期交费方式变更经办信息录入
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtraToBia_23(Map map) {
		String client_no = commonQueryDAO.getApplicantByPolicyNo((String) map
				.get("POLICYNO"));
		ServiceItemsInputDTO_23 items23 = new ServiceItemsInputDTO_23();
		String serviceItems = (String) map.get("SERVICEITEMS");
		ClientAccountDTO clientAcount = new ClientAccountDTO();
		MapToBean(clientAcount, (Map) map.get("CLIENTACOUNT"));
		items23.setAccount(clientAcount);
		items23.setClientNo(client_no);
		items23.setPolicyNo((String) map.get("POLICYNO"));
		items23.setChargingMethod((String) map.get("CHARGINGMETHOD"));
		items23.setServiceItems(serviceItems);
		items23.getAccount().getAccountSeq();
		return serviceItemsDAO_23.queryServiceItemsInfoExtraToBia_23(items23);
	}

	/**
	 * 客户资料变更
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtra_21(Map map) {
		ServiceItemsInputDTO items = new ServiceItemsInputDTO();
		ServiceItemsInputDTO_21 items21 = new ServiceItemsInputDTO_21();
		ClientInformationDTO clientInfo = new ClientInformationDTO();
		String serviceItems = (String) map.get("SERVICEITEMS");
		// 要变更的客户号
		String clientNo = (String) map.get("CLIENTNO");
		// 国籍 countryCode 婚姻状况 marriageCode 证件号码idNo
		String countryCode = (String) map.get("COUNTRYCODE");
		String marriageCode = (String) map.get("MARRIAGECODE");
		String idNo = (String) map.get("IDNO");
		items21.setServiceItems(serviceItems);
		items21.setClientNo(clientNo);
		items21 = (ServiceItemsInputDTO_21) serviceItemsDAO_21
				.queryServiceItemsInfoExtra(items21);
		clientInfo = items21.getClientInformationDTO();
		clientInfo.setCountryCode(countryCode);
		clientInfo.setMarriageCode(marriageCode);
		clientInfo.setIdNo(idNo);
		items21.setClientInformationDTO(clientInfo);
		return items21;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		developPlatformInterfaceImpl = (DevelopPlatformInterfaceImpl) PlatformContext
				.getApplicationContext().getBean("posForMspPlatformInterface");
	}

}
