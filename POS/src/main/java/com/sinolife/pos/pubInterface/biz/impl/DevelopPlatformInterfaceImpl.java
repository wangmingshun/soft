package com.sinolife.pos.pubInterface.biz.impl;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeUtility;

import net.unihub.framework.util.common.DateUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.indigopacific.idg.IDGClientException;
import com.indigopacific.idg.IDGClientImpl;
import com.indigopacific.idg.IIDGResult;
import com.sinolife.efs.rpc.domain.UserInfo;
import com.sinolife.esbpos.web.WebPosService;
import com.sinolife.pos.acceptance.branch.service.BranchAcceptService;
import com.sinolife.pos.acceptance.branch.service.BranchQueryService;
import com.sinolife.pos.acceptance.rollback.dao.BranchRollbackDAO;
import com.sinolife.pos.common.dao.ClientInfoDAO;
import com.sinolife.pos.common.dao.CommonAcceptDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dao.PosCodeTableDAO;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_1;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_19;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_2;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_21;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_23;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_24;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_26;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_29;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_33;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_35;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_37;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_38;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_39;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_45;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_5;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_6;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_9;
import com.sinolife.pos.common.dto.ClientAddressDTO;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.ClientPhoneDTO;
import com.sinolife.pos.common.dto.ClientTouchHistoryDTO;
import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.FinancialProductsDTO;
import com.sinolife.pos.common.dto.PolicyDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.common.dto.PosApplyFilesDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.PosPolicy;
import com.sinolife.pos.common.dto.PosProblemItemsDTO;
import com.sinolife.pos.common.dto.PosStatusChangeHistoryDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_1;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_19;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_2;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_21;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_23;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_24;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_26;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_28;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_29;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_33;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_35;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_37;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_38;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_39;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_45;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_5;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_6;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_9;
import com.sinolife.pos.common.file.dao.PosFileDAO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.print.dao.PrintDAO;
import com.sinolife.pos.print.notice.NoticePrintHandler;
import com.sinolife.pos.print.notice.NoticePrintHandlerFactory;
import com.sinolife.pos.print.service.PrintService;
import com.sinolife.pos.pubInterface.biz.service.DevelopPlatformInterface;
import com.sinolife.pos.receipt.dao.ReceiptManageDAO;
import com.sinolife.pos.rpc.ilogjrules.posrules.dao.PosRulesDAO;
import com.sinolife.pos.setting.user.privilege.service.UserPrivilegeService;
import com.sinolife.pos.todolist.acceptentry.dao.AcceptEntryDAO;
import com.sinolife.pos.todolist.acceptentry.service.AcceptEntryService;
import com.sinolife.pos.todolist.problem.dao.ProblemDAO;
import com.sinolife.pos.todolist.problem.service.ProblemService;
import com.sinolife.pos.vip.dao.VipManageDAO;
import com.sinolife.sf.config.RuntimeConfig;
import com.sinolife.sf.framework.email.Mail;
import com.sinolife.sf.framework.email.MailService;
import com.sinolife.sf.framework.email.MailType;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMRuleInfoDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSVerifyResultDto;
import com.sinolife.sf.store.MimeType;
import com.sinolife.sf.store.SFFile;
import com.sinolife.sf.store.SFFilePath;
import com.sinolife.sf.store.StogeType;
import com.sinolife.sf.util.Util;

@Service("posForMspPlatformInterface")
public class DevelopPlatformInterfaceImpl implements DevelopPlatformInterface {

	@Autowired
	private BranchAcceptService branchAcceptService;
	@Autowired
	private AcceptEntryService acceptEntryService;

	@Autowired
	private BranchQueryService branchQueryService;

	@Autowired
	private ClientInfoDAO clientInfoDAO;

	@Autowired
	private CommonQueryDAO commonQueryDAO;

	@Autowired
	TransactionTemplate txTmpl;

	@Autowired
	private CommonAcceptDAO acceptDAO;
	@Autowired
	private AcceptEntryDAO acceptEntryDAO;

	@Autowired
	private ProblemDAO problemDAO;

	@Autowired
	ProblemService problemService;
	@Autowired
	private ServiceItemsDAO_38 serviceItemsDAO_38;
	@Autowired
	private ServiceItemsDAO_39 serviceItemsDAO_39;
	@Autowired
	private ServiceItemsDAO_24 serviceItemsDAO_24;
	@Autowired
	private ServiceItemsDAO_21 serviceItemsDAO_21;
	@Autowired
	private ServiceItemsDAO_23 serviceItemsDAO_23;
	@Autowired
	private ServiceItemsDAO_26 serviceItemsDAO_26;
	@Autowired
	private ServiceItemsDAO_29 serviceItemsDAO_29;
	@Autowired
	private ServiceItemsDAO_37 serviceItemsDAO_37;
	@Autowired
	private ServiceItemsDAO_9 serviceItemsDAO_9;
	@Autowired
	private ServiceItemsDAO_19 serviceItemsDAO_19;
	@Autowired
	private ServiceItemsDAO_1 serviceItemsDAO_1;
	@Autowired
	private ServiceItemsDAO_2 serviceItemsDAO_2;

	@Autowired
	private ServiceItemsDAO_33 serviceItemsDAO_33;

	@Autowired
	private ServiceItemsDAO_35 serviceItemsDAO_35;
	@Autowired
	private ServiceItemsDAO_6 serviceItemsDAO_6;
	@Autowired
	private ServiceItemsDAO_5 serviceItemsDAO_5;
	@Autowired
	private ServiceItemsDAO_45 serviceItemsDAO_45;
	@Autowired
	private PosCodeTableDAO posCodeTableDAO;
	@Autowired
	private UserPrivilegeService userPrivilegeService;

	@Autowired
	private PrintService printService;

	@Autowired
	private PrintDAO printDAO;

	@Autowired
	private PosFileDAO posFileDAO;

	@Autowired
	private VipManageDAO vipManageDAO;

	@Autowired
	private BranchRollbackDAO rollbackDAO;

	@Autowired
	@Qualifier("mailService03")
	private MailService mailService;

	@Autowired
	protected PosRulesDAO posRulesDAO;

	@Autowired
	ReceiptManageDAO receiptDAO;
	
	@Autowired
	private TransactionTemplate transactionTemplate;

	/** MPOS虚拟柜面 */
	public static final String MPOS_VIRTUAL_COUNTER = "903";

	Logger logger = Logger.getLogger(DevelopPlatformInterfaceImpl.class
			.getName());

	public DevelopPlatformInterfaceImpl() {
		String ips = RuntimeConfig.get("indogoIp", String.class);
		sigIp = ips.split(",");
	}

	synchronized String nextSigIP() {
		sigIpIndex++;
		sigIpIndex = sigIpIndex % sigIp.length;
		logger.info("========indogoIp:" + sigIp[sigIpIndex]);
		return sigIp[sigIpIndex];
	}

	// 签章服务IP
	private static String[] sigIp;
	private static int sigIpIndex = 0;

	/**
	 * 查询保全员
	 * 
	 * @param user
	 * @return
	 */
	public Map queryUserPrivsInfo(String user) {
		return userPrivilegeService.queryUserPrivsInfo(user);
	}

	/**
	 * 查询用户受理权限
	 * 
	 * @param user
	 * @return Y/N
	 */
	public String queryUserPosAcceptPrivs(String user) {
		return userPrivilegeService.queryUserPosAcceptPrivs(user);
	}

	/**
	 * 根据证件号码查询
	 * 
	 * @param idTypeCode
	 *            id类型
	 * @param idNo
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getClientinfoProById(String idTypeCode,
			String idNo) {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<ClientInformationDTO> retList = null;
		retList = clientInfoDAO.selClientinfoPro(null, null, null, idTypeCode,
				idNo);

		for (ClientInformationDTO clintInfo : retList) {

			list.addAll(getClientInfo(clintInfo));
		}

		return list;

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
	@Override
	public List<Map<String, Object>> getClientinfoProbyName(String clientName,
			String sex, Date birthday) {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<ClientInformationDTO> retList = null;
		retList = clientInfoDAO.selClientinfoPro(clientName, sex, birthday,
				null, null);

		for (ClientInformationDTO clintInfo : retList) {

			list.addAll(getClientInfo(clintInfo));
		}
		return list;

	}

	/**
	 * 根据客户号查询
	 * 
	 * @param clientNo
	 *            客户号
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getClientinfoProbyClientNo(String clientNo) {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<ClientInformationDTO> retList = null;
		retList = clientInfoDAO.selClientinfoForClientno(clientNo);

		for (ClientInformationDTO clintInfo : retList) {

			list.addAll(getClientInfo(clintInfo));
		}
		return list;

	}

	/**
	 * 根据保单号查询
	 * 
	 * @param policyNO
	 * @param applicantOrInsured
	 *            投，被保险人
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getClientinfoProbyPolicyNo(
			String policyNO, String applicantOrInsured)

	{

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<ClientInformationDTO> retList = new ArrayList<ClientInformationDTO>();
		if (StringUtils.isBlank(applicantOrInsured)
				|| "A".equals(applicantOrInsured)) {
			String clientNo = commonQueryDAO.getApplicantByPolicyNo(policyNO);
			retList.addAll(clientInfoDAO.selClientinfoForClientno(clientNo));
		}
		if (StringUtils.isBlank(applicantOrInsured)
				|| "I".equals(applicantOrInsured)) {
			List<String> clientNoList = commonQueryDAO
					.getInsuredByPolicyNo(policyNO);
			if (clientNoList != null && !clientNoList.isEmpty()) {
				for (String clientNo : clientNoList) {
					retList.addAll(clientInfoDAO
							.selClientinfoForClientno(clientNo));
				}
			}
		}

		if (retList != null && !retList.isEmpty()) {
			// 除重
			Iterator<ClientInformationDTO> it = retList.iterator();
			while (it.hasNext()) {
				ClientInformationDTO clientInfo = it.next();

				list.addAll(getClientInfo(clientInfo));
			}
		}

		return list;
	}

	/**
	 * 根据客户号查询详细信息
	 * 
	 * @param clientInfo
	 * @return
	 */
	private List<Map<String, Object>> getClientInfo(
			ClientInformationDTO clientInfo) {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		List<ClientAddressDTO> addressList = null;
		List<ClientPhoneDTO> phoneList = null;
		List<String> clientProductList = null;
		List<ClientTouchHistoryDTO> clientTouchHistoryList = null;
		StringBuffer address = new StringBuffer("");
		StringBuffer phone = new StringBuffer("");
		StringBuffer ponos = new StringBuffer("");
		StringBuffer touchs = new StringBuffer("");

		map = new HashMap<String, Object>();

		map.put("clientName", clientInfo.getClientName());
		map.put("birthday", DateUtil.dateToString(clientInfo.getBirthday()));
		map.put("sexDesc", clientInfo.getSexDesc());
		map.put("idNo", clientInfo.getIdNo());
		map.put("idNoValidyDate", clientInfo.getIdnoValidityDate());
		map.put("idNoType", clientInfo.getIdTypeDesc());
		map.put("idNoTypeCode", clientInfo.getIdTypeCode());
		map.put("clientNo", clientInfo.getClientNo());
		addressList = clientInfoDAO.queryAddressByClientNo(clientInfo
				.getClientNo());
		clientInfo.setClientAddressList(addressList);
		for (ClientAddressDTO addressDto : addressList) {
			address.append(addressDto.getAddressTypeDesc() + ": "
					+ addressDto.getFullAddress() + " \n");

		}
		map.put("address", address);
		// 查询电话
		phoneList = clientInfoDAO
				.queryPhoneByClientNo(clientInfo.getClientNo());
		for (ClientPhoneDTO phoneDto : phoneList) {
			phone.append(phoneDto.getPhoneTypeDesc()
					+ ": "
					+ (phoneDto.getAreaNo() == null ? " " : (phoneDto
							.getAreaNo() + "-")) + phoneDto.getPhoneNo() + "\n");

		}
		map.put("phone", phone);

		// 查询客户产品列表
		clientProductList = commonQueryDAO
				.queryPolicyNoListByClientNo(clientInfo.getClientNo());
		clientInfo.setClientProductList(clientProductList);
		for (String pono : clientProductList) {
			ponos.append(pono + "\n");

		}
		map.put("policyNo", ponos);

		// 查询接触历史
		clientTouchHistoryList = clientInfoDAO
				.selClientTouchHistoryPro(clientInfo.getClientNo());

		for (ClientTouchHistoryDTO touchDto : clientTouchHistoryList) {
			touchs.append(touchDto.getTouchContent() + "\n");

		}
		map.put("touch", touchs);

		list.add(map);

		return list;
	}

	/**
	 * @param clientNo
	 *            客户号
	 * @param clientWill
	 *            是否考虑客户愿意接收短信：Y/N
	 * @return
	 */
	public String getClientMobile(String clientNo, String clientWill) {

		return clientInfoDAO.getClientMobile(clientNo, clientWill);

	}

	/**
	 * @param policyNo
	 *            保单号
	 * @return 是否有未完成保全 Y/N
	 */
	public String isposacceptflag(String policyNo) {
		String isaccept = "N";
		if (commonQueryDAO.isposacceptflag(policyNo) > 0) {
			isaccept = "Y";
		}
		return isaccept;

	}

	/**
	 * 查询未结案保全信息
	 * 
	 * @param clientNo
	 *            客户号
	 * @return List<PosInfoDTO>
	 */
	public List<PosInfoDTO> queryNotCompletePosInfoRecord(String clientNo) {
		return commonQueryDAO.queryNotCompletePosInfoRecord(clientNo);
	}

	/**
	 * 根据客户号，客户类型 查询所以有效的保单号
	 * 
	 * @param clientNo
	 * @return
	 */
	public List<Map<String, String>> PolicyNoListByClientno(String clientNo,
			String clientType) {
		List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
		if (clientType.equals("I")) {
			retList = commonQueryDAO.PolicyNoListByinsNo(clientNo);
		}
		if (clientType.equals("A")) {
			retList = commonQueryDAO.PolicyNoListByappNo(clientNo);
		}
		return retList;
	}

	/**
	 * 查询 客户名下的保单至少有一张是由该服务人员服务
	 * 
	 * @param client_no
	 *            客户号
	 * @param agent_no
	 *            代理人编号
	 * @return
	 */
	public String isagenthavepolicy(String client_no, String agent_no) {
		return commonQueryDAO.isagenthavepolicy(client_no, agent_no);
	}

	/**
	 * 查询有无其他客户手机号和此客户相同
	 * 
	 * @param client_no
	 *            客户号
	 * @param agent_no
	 *            手机号
	 * @return
	 */
	public String isotherclientphone(String client_no, String phone_no) {
		return commonQueryDAO.isotherclientphone(client_no, phone_no);
	}

	/**
	 * 生成移动保全保全用的条码
	 * 
	 * @param serviceItems
	 * @return
	 */
	public String generateMPBarcode() {
		// 条形码统一改为sequence生成方
		return "MPOS" + commonQueryDAO.queryBarcodeNoSequence();
	}

	/**
	 * 生成CMP保全用的条码
	 * 
	 * @param serviceItems
	 * @return
	 */
	public String generateCMPBarcode() {
		// 条形码统一改为sequence生成方
		return "CMPN" + commonQueryDAO.queryBarcodeNoSequence();
	}

	/**
	 * 移动保全受理信息处理
	 * 
	 * @param applyBatchInfo
	 * @return
	 */
	public PosApplyBatchDTO createBatchHeader(PosApplyBatchDTO applyBatchInfo) {

		// 实施属性值依赖关系
		branchAcceptService.applyDependency(applyBatchInfo);

		List<ClientInformationDTO> clientInfoList = clientInfoDAO
				.selClientinfoForClientno(applyBatchInfo.getClientNo());
		if (clientInfoList == null || clientInfoList.isEmpty()
				|| clientInfoList.size() != 1)
			throw new RuntimeException("无效的客户号：" + applyBatchInfo.getClientNo());

		List<String> validPolicyNoList = branchQueryService
				.queryPolicyNoListByClientNo(applyBatchInfo.getClientNo());
		if (validPolicyNoList == null || validPolicyNoList.isEmpty()) {
			throw new RuntimeException("找不到客户作为投保人或被保人的保单");
		}

		String applyType = applyBatchInfo.getApplyTypeCode();

		applyBatchInfo.setClientInfo(clientInfoList.get(0));

		// 默认申请类型为亲办
		if (applyType == null || "".equals(applyType)
				|| StringUtils.isBlank(applyType)) {
			applyBatchInfo.setApplyTypeCode("1");
		}

		for (PosApplyFilesDTO posapplyFilesDto : applyBatchInfo.getApplyFiles()) {

			if (!validPolicyNoList.contains(posapplyFilesDto.getPolicyNo())) {
				throw new RuntimeException("申请保单不为客户作为投保人或被保人的保单");
			}

		}

		List<PosApplyFilesDTO> applyFiles = new ArrayList<PosApplyFilesDTO>();
		for (PosApplyFilesDTO applyFile : applyBatchInfo.getApplyFiles()) {
			PosApplyFilesDTO file = new PosApplyFilesDTO();
			file.setBarcodeNo(applyFile.getBarcodeNo());
			file.setPolicyNo(applyFile.getPolicyNo());
			file.setApplyDate(applyFile.getApplyDate());
			file.setForeignExchangeType("1");
			file.setServiceItems(applyFile.getServiceItems());
			file.setPaymentType(applyFile.getPaymentType());
			file.setApprovalServiceType(applyFile.getApprovalServiceType());
			if (file.getApplyDate() == null) {
				file.setApplyDate(commonQueryDAO.getSystemDate());
			}
			if (StringUtils.isBlank(applyFile.getApprovalServiceType())) {
				file.setApprovalServiceType("1");// 自领
			}
			if ("2".equals(applyFile.getApprovalServiceType())) {
				file.setAddress(applyFile.getAddress());
				file.setZip(applyFile.getZip());
			}
			String paymentType = applyFile.getPaymentType();
			if ("2".equals(paymentType)) {
				file.setAccountNoType(applyFile.getAccountNoType());
				file.setBankCode(applyFile.getBankCode());
				file.setTransferAccountno(applyFile.getTransferAccountno());

			}

			file.setTransferAccountOwner(applyFile.getTransferAccountOwner());
			file.setTransferAccountOwnerIdNo(applyFile
					.getTransferAccountOwnerIdNo());
			file.setTransferAccountOwnerIdType(applyFile
					.getTransferAccountOwnerIdType());

			applyFiles.add(file);
		}

		applyBatchInfo.getApplyFiles().clear();
		applyBatchInfo.getApplyFiles().addAll(applyFiles);

		// 生成受理
		branchAcceptService.generatePosInfoList(applyBatchInfo, false);

		// 生成捆绑顺序
		branchAcceptService.generateBindingOrder(applyBatchInfo);

		return applyBatchInfo;

	}

	/**
	 * 批量受理信息录入
	 * 
	 * @param applyBatchInfo
	 * @param mposMobile
	 * @return
	 */
	public PosApplyBatchDTO insertBatchAccept(PosApplyBatchDTO applyBatchInfo,
			String mposMobile) {

		// 写受理
		applyBatchInfo = branchAcceptService.batchAccept(applyBatchInfo);

		if (mposMobile != null && !"".equals(mposMobile)) {
			// 写受理人工号到pos_accept_detail
			PosAcceptDetailDTO detail = null;

			for (PosInfoDTO posInfoDto : applyBatchInfo.getPosInfoList()) {

				detail = new PosAcceptDetailDTO();
				detail.setPosNo(posInfoDto.getPosNo());
				detail.setPosObject("5");
				detail.setProcessType("1");
				detail.setItemNo("016");
				detail.setObjectValue(posInfoDto.getPosNo());
				detail.setNewValue(applyBatchInfo.getAcceptor());// 移动保全受理人员
				detail.setOldValue(applyBatchInfo.getAcceptor());
				acceptDAO.insertPosAcceptDetail(detail);

				detail = new PosAcceptDetailDTO();
				detail.setPosNo(posInfoDto.getPosNo());
				detail.setPosObject("5");
				detail.setProcessType("1");
				detail.setItemNo("017");
				detail.setObjectValue(mposMobile);
				detail.setNewValue(mposMobile);// 移动保全通知电话
				detail.setOldValue(mposMobile);
				acceptDAO.insertPosAcceptDetail(detail);

			}
		}
		return applyBatchInfo;

	}

	/**
	 * 投保，处理规则检查及流转结果
	 * 
	 * @param ServiceItemsInputDTO
	 *            itemsInputDTO
	 * @param PosApplyBatchDTO
	 *            applyBatchInfo
	 * @return
	 */
	public Map<String, Object> acceptInternal(
			ServiceItemsInputDTO itemsInputDTO, PosApplyBatchDTO applyBatchInfo) {

		Map<String, Object> rMap = new HashMap<String, Object>();

		String posNo = itemsInputDTO.getPosNo();

		//
		ServiceItemsInputDTO oriItemsInputDTO = branchQueryService
				.queryAcceptDetailInputByPosNo(posNo);
		itemsInputDTO.setOriginServiceItemsInputDTO(oriItemsInputDTO
				.getOriginServiceItemsInputDTO());

		itemsInputDTO.setPosNo(posNo);
		itemsInputDTO.setPosNoList(null);

		// 写受理明细
		branchAcceptService.acceptDetailInput(itemsInputDTO);

		// 更新备注字段
		branchAcceptService.updatePosInfo(posNo, "remark",
				itemsInputDTO.getRemark());

		// 查询前一单是否已经受理完成
		boolean prePosIsEnd = branchAcceptService.prePosnoIsEnd(posNo);

		List<String> posNoList = itemsInputDTO.getPosNoList();
		List<Map<String, Object>> processResultList = new ArrayList<Map<String, Object>>();

		// 延时规则生效标志
		for (int i = 0; i < posNoList.size(); i++) {
			posNo = posNoList.get(i);

			// 更新备注字段
			branchAcceptService.updatePosInfo(posNo, "REMARK",
					itemsInputDTO.getRemark());

			// 写状态变迁记录
			PosStatusChangeHistoryDTO statusChange = new PosStatusChangeHistoryDTO();
			statusChange.setPosNo(posNo);
			statusChange.setOldStatusCode("A01");
			statusChange.setNewStatusCode(prePosIsEnd ? "A03" : "A02");
			statusChange.setChangeUser(applyBatchInfo.getAcceptor());
			branchAcceptService.insertPosStatusChangeHistory(statusChange);
			if (!"0".equals(statusChange.getFlag()))
				throw new RuntimeException("写保全状态变更记录失败："
						+ statusChange.getMessage());
			if (prePosIsEnd) {
				// 前一单受理完成，则更新状态为 A03待处理规则检查
				branchAcceptService.updatePosInfo(posNo, "ACCEPT_STATUS_CODE",
						"A03");

				// 调用流转接口
				Map<String, Object> retMap = branchAcceptService
						.workflowControl("03", posNo, false);
				logger.info("acceptDetailInputSubmit workflowControl return:"
						+ retMap);

				String flag = (String) retMap.get("flag");
				String msg = (String) retMap.get("message");
				String ruleInfo = (String) retMap.get("ruleInfo");

				retMap = branchQueryService.queryProcessResult(posNo);
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
				PosInfoDTO posInfo = commonQueryDAO.queryPosInfoRecord(posNo);
				retMap.put("PREM_FLAG", posInfo.getPremFlag());
				retMap.put("PREM_SUM", posInfo.getPremSum());
				if ("6".equals(posInfo.getServiceItems())){
					String isCheck = commonQueryDAO.checkPolicyClientRisk(posInfo.getPolicyNo());
					if ("N".equals(isCheck)){
						retMap.put("RULE_CHECK_MSG", "不符合官网复效的二核规则！"); // 批文预览界面使用
						retMap.put("IS_UW_CHECK", "Y");
					}
				}
				processResultList.add(retMap);

			} else {
				// 前一单受理未完成，则更新状态为 A02 已录入受理明细
				branchAcceptService.updatePosInfo(posNo, "accept_status_code",
						"A02");

				Map<String, Object> resultMap = branchQueryService
						.queryProcessResult(posNo);
				resultMap.put("RESULT_MSG", "已录入受理明细");
				processResultList.add(resultMap);
			}

		}
		rMap.put("prePosIsEnd", prePosIsEnd);
		rMap.put("processResultList", processResultList);
		if (!prePosIsEnd) {
			// 前一单受理完成

			rMap.put("hasNext", applyBatchInfo.getPosBatchNo());
			rMap.put("posBatchNo", applyBatchInfo.getPosBatchNo());

		}

		return rMap;

	}

	/**
	 * 经办结果确认
	 * 
	 * @param processResultList
	 * @param itemsInputDTO
	 * @return
	 */
	public Map<String, Object> processResultSubmit(
			final List<Map<String, Object>> processResultList,
			final ServiceItemsInputDTO itemsInputDTO) {
		return txTmpl.execute(new TransactionCallback<Map<String, Object>>() {
			@Override
			public Map<String, Object> doInTransaction(
					TransactionStatus transactionStatus) {
				Map<String, Object> resultMap = new HashMap<String, Object>();
				String posNo =null;
				try {
				for (int i = 0; processResultList != null
						&& i < processResultList.size(); i++) {
					Map<String, Object> processResultMap = processResultList
							.get(i);
					if (!processResultMap.containsKey("RULE_CHECK_MSG")) {
						// 流转接口
						 posNo = (String) processResultList.get(i).get(
								"POS_NO");
						Map<String, Object> retMap = branchAcceptService
								.workflowControl("05", posNo, false);
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
							if ("Y".equals(clientInfoDAO
									.queryWebNeedNotify(posNo))) {
								String applyBarCode = commonQueryDAO
										.queryPolicyApplyBarcode(itemsInputDTO
												.getPolicyNo());
								Map<String, String> reqMap = new HashMap<String, String>();
								reqMap.put("businessNo", "1002");
								reqMap.put("policyNo",
										itemsInputDTO.getPolicyNo());
								reqMap.put("applyNo", applyBarCode);

								WebPosService wps = PlatformContext
										.getEsbContext().getEsbService(
												WebPosService.class);

								wps.surrenderNotify(reqMap);
							}
						} else if ("A17".equals(flag)) {
							// 待保单打印
							resultMsg = "待保单打印";
						} else {
							resultMsg = msg;
						}
						processResultMap.put("RESULT_MSG", resultMsg);

					}
				}
				resultMap.put("processResultList", processResultList);

				resultMap.put(
						"hasNext",
						StringUtils.isNotBlank(branchAcceptService
								.getNextPosNoInBatch(itemsInputDTO.getBatchNo())));
				resultMap.put("posBatchNo", itemsInputDTO.getBatchNo());
			} catch (Exception e) {
				logger.info("processResultSubmit exception"+posNo+e);
				
					transactionStatus.setRollbackOnly();
				throw new RuntimeException(
				e);
			}
			return resultMap;
		}
	});
}


	/**
	 * 断点恢复，用于代办任务时，重新恢复到最后的处理点
	 * 
	 * @param posBatchNo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> continueFromBreak(String posBatchNo) {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> retMap = branchQueryService
				.queryBreakInfoByPosBatchNo(posBatchNo);
		String breakStatusCode = null;
		if (retMap != null)
			breakStatusCode = (String) retMap.get("breakStatusCode");

		if ("A01".equals(breakStatusCode)) {
			String posNo = (String) retMap.get("posNo");
			resultMap.put("posNo", posNo);

		} else if ("A19".equals(breakStatusCode)
				|| "C01".equals(breakStatusCode)) {
			List<Map<String, Object>> processResultList = (List<Map<String, Object>>) retMap
					.get("processResultList");
			String posNo = (String) processResultList.get(0).get("POS_NO");
			ServiceItemsInputDTO itemsInputDTO = branchQueryService
					.queryAcceptDetailInputByPosNo(posNo);

			resultMap.put("acceptance__process_result_list", processResultList);
			resultMap.put("acceptance__item_input", itemsInputDTO);

		} else {
			throw new RuntimeException("该批次已经完成！");
		}
		return resultMap;
	}

	/**
	 * 查询客户下所有有效手机号码
	 * 
	 * @param client_no
	 * @return
	 */
	public List<String> getClientMobileAll(String client_no) {
		List<String> moblieList = commonQueryDAO.getClientMobileAll(client_no);
		return moblieList;
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
				acceptDAO.updatePosInfo(posNo, "ACCEPT_STATUS_CODE", "C02");
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
			String rollbackCause, String rollbackCauseDetail, String acceptUser) {

		String mposMobile = "";
		List<String> phoneList = commonQueryDAO.queryPosAcceptDetailInfo(posNo,
				"5", "017");
		if (phoneList != null && phoneList.size() > 0) {
			mposMobile = phoneList.get(0);
		}

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

		// 插入新受理
		PosInfoDTO newPosInfo = new PosInfoDTO();
		Date sysdate = commonQueryDAO.getSystemDate();
		newPosInfo.setAcceptor(foundPosInfo.getAcceptor());
		newPosInfo.setAcceptStatusCode("A01"); // 受理状态：批次受理
		newPosInfo.setAccountNo(foundPosInfo.getAccountNo());
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
		detail.setObjectValue(newPosInfo.getPosNo());
		detail.setNewValue(acceptUser);// 移动保全受理人员
		detail.setOldValue(acceptUser);
		acceptDAO.insertPosAcceptDetail(detail);

		detail = new PosAcceptDetailDTO();
		detail.setPosNo(newPosInfo.getPosNo());
		detail.setPosObject("5");
		detail.setProcessType("1");
		detail.setItemNo("017");
		detail.setObjectValue(mposMobile);
		detail.setNewValue(mposMobile);// 移动保全通知电话
		detail.setOldValue(mposMobile);
		acceptDAO.insertPosAcceptDetail(detail);

		// 对保单置暂停
		acceptDAO.doPolicySuspend(newPosInfo.getPolicyNo(), "5",
				newPosInfo.getPosNo(), newPosInfo.getServiceItems(), sysdate);

		for (PosInfoDTO tmpPosInfo : posInfoListNeedCancel) {
			// 撤销受理
			this.cancelAccept(tmpPosInfo, acceptUser);
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
	 * 录受理明细时的撤销
	 */
	public Map<String, Object> cancelService(final String posNo,
			final String cancelCause, final String cancelRemark) {
		return txTmpl.execute(new TransactionCallback<Map<String, Object>>() {
			@Override
			public Map<String, Object> doInTransaction(
					TransactionStatus transactionStatus) {
				Map<String, Object> returnMap = new HashMap<String, Object>();
				try {
					// 更新取消原因
					branchAcceptService.updatePosInfo(posNo, "ROLLBACK_CAUSE",
							cancelCause);
					// 更新取消详细信息
					branchAcceptService.updatePosInfo(posNo,
							"ABANDONED_DETAIL", cancelRemark);
					// 调用流转接口撤销
					Map<String, Object> ret = branchAcceptService
							.workflowControl("11", posNo, false);
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
							.isNotBlank(branchAcceptService
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
	 * 经办结果撤销
	 * 
	 * @param cancelCause
	 * @param cancelRemark
	 * @param processResultList
	 * @param itemsInputDTO
	 * @return
	 */
	public Map<String, Object> processResultCancel(String cancelCause,
			String cancelRemark, List<Map<String, Object>> processResultList,
			ServiceItemsInputDTO itemsInputDTO) {

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
			branchAcceptService.cancelAccept(posInfo, PosUtils
					.getLoginUserInfo().getLoginUserID());
			processResultMap.put("RESULT_MSG", "已取消");
		}
		resultMap.put("hasNext", StringUtils.isNotBlank(branchAcceptService
				.getNextPosNoInBatch(itemsInputDTO.getBatchNo())));
		resultMap.put("posBatchNo", itemsInputDTO.getBatchNo());

		return resultMap;
	}

	/**
	 * 查询待受理录入保全件列表
	 * 
	 * @param acceptor
	 *            受理人
	 * @return List&lt;Map&lt;String, Object&gt;&gt;
	 */
	public List<Map<String, Object>> queryAcceptEntryTodolist(String acceptor) {
		return acceptEntryService.queryAcceptEntryTodolist(acceptor);
	}

	/**
	 * 红利方式选择经办信息录入页面
	 */
	public ServiceItemsInputDTO_26 queryServiceItemsInfo_26(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return serviceItemsDAO_26.queryServiceItemsInfo(serviceItemsInputDTO);
	}

	/**
	 * 挂失及解挂经办信息录入页面
	 */
	@SuppressWarnings("unchecked")
	public ServiceItemsInputDTO_28 queryServiceItemsInfo_28(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_28 item = (ServiceItemsInputDTO_28) serviceItemsInputDTO;
		Map<String, Object> map = new HashMap<String, Object>();
		map = commonQueryDAO.policyLossStatus(item.getPolicyNo());
		item.setIsLoss((String) map.get("lossFlag"));

		return item;
	}

	/**
	 * 预约终止附加险经办信息录入页面
	 */
	public ServiceItemsInputDTO_29 queryServiceItemsInfo_29(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return serviceItemsDAO_29.queryServiceItemsInfo(serviceItemsInputDTO);
	}

	/**
	 * 保单还款经办信息录入页面
	 */
	public ServiceItemsInputDTO_9 queryServiceItemsInfo_9(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return serviceItemsDAO_9.queryServiceItemsInfo(serviceItemsInputDTO);
	}

	/**
	 * 续期交费方式变更经办信息录入页面
	 */
	public ServiceItemsInputDTO_23 queryServiceItemsInfo_23(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return serviceItemsDAO_23.queryServiceItemsInfo(serviceItemsInputDTO);
	};

	/**
	 * 联系方式变更经办信息录入页面
	 */
	public ServiceItemsInputDTO_19 queryServiceItemsInfo_19(
			ServiceItemsInputDTO serviceItemsInputDTO) {

		return (ServiceItemsInputDTO_19) serviceItemsDAO_19
				.queryServiceItemsInfo(serviceItemsInputDTO);
	}

	/**
	 * 保全收付费方式调整经办信息录入页面
	 */
	public ServiceItemsInputDTO_33 queryServiceItemsInfo_33(
			ServiceItemsInputDTO serviceItemsInputDTO) {

		return (ServiceItemsInputDTO_33) serviceItemsDAO_33
				.queryServiceItemsInfo(serviceItemsInputDTO);
	}

	/**
	 * 追加保费
	 */
	public ServiceItemsInputDTO_35 queryServiceItemsInfo_35(
			ServiceItemsInputDTO serviceItemsInputDTO) {

		return (ServiceItemsInputDTO_35) serviceItemsDAO_35
				.queryServiceItemsInfoExtraToBia_35(serviceItemsInputDTO);
	}

	/**
	 * 犹豫期退保经办信息录入页面
	 */
	public ServiceItemsInputDTO_1 queryServiceItemsInfo_1(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_1 items1 = serviceItemsDAO_1
				.queryServiceItemsInfoExtra_1(serviceItemsInputDTO);
		boolean ishaveprd = false;
		// 是否可疑交易
		items1.setDouBt(false);
		// 退保原因
		items1.setSurrenderCauseCode("46");
		List<PolicyProductDTO> productList = items1.getPolicyProductList();
		for (int i = 0; productList != null && i < productList.size(); i++) {
			PolicyProductDTO product = productList.get(i);
			if ("1".equals(product.getDutyStatus())) {
				product.setSelected(true);
				ishaveprd = true;
			}
		}
		if (!ishaveprd) {
			throw new RuntimeException("该保单没可操作改保全项目险种");
		}
		items1.setPolicyProductList(productList);
		return items1;

	}

	/**
	 * 退保经办信息录入页面
	 */
	public ServiceItemsInputDTO_2 queryServiceItemsInfo_2(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		boolean ishaveprd = false;
		ServiceItemsInputDTO_2 items2 = serviceItemsDAO_2
				.queryServiceItemsInfoExtra_2(serviceItemsInputDTO);
		items2.setAllSurrender(true);
		items2.setDouBt(false);
		// 退保原因
		items2.setSurrenderCause("46");
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
			throw new RuntimeException("该保单没可操作改保全项目险种");
		}
		items2.setProductList(productList);
		return items2;
	}

	/**
	 * 年金给付方式选择 经办信息录入页面
	 */
	public ServiceItemsInputDTO_24 queryServiceItemsInfo_24(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return (ServiceItemsInputDTO_24) serviceItemsDAO_24
				.queryServiceItemsInfoExtra_24(serviceItemsInputDTO);
	}

	/**
	 * 账户分配比例变更 经办信息录入页面
	 */
	public ServiceItemsInputDTO_38 queryServiceItemsInfo_38(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return (ServiceItemsInputDTO_38) serviceItemsDAO_38
				.queryServiceItemsInfoExtra_38(serviceItemsInputDTO);
	}

	/**
	 * 投资账户转换变更 经办信息录入页面
	 */
	public ServiceItemsInputDTO_39 queryServiceItemsInfo_39(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return (ServiceItemsInputDTO_39) serviceItemsDAO_39
				.queryServiceItemsInfoExtra_39(serviceItemsInputDTO);
	}

	/**
	 * 客户资料变更变更 经办信息录入页面
	 */
	public ServiceItemsInputDTO_21 queryServiceItemsInfo_21(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return (ServiceItemsInputDTO_21) serviceItemsDAO_21
				.queryServiceItemsInfoExtra(serviceItemsInputDTO);
	}
	
	/**
	 * 查询新增附加险保单初始信息
	 */
	public ServiceItemsInputDTO_5 queryServiceItemsInfo_5(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return (ServiceItemsInputDTO_5) serviceItemsDAO_5
		.queryServiceItemsInfoExtra_5(serviceItemsInputDTO);
	}
	
	/**
	 *部分领取经办信息录入页面
	 */
	public ServiceItemsInputDTO_37 queryServiceItemsInfo_37(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return (ServiceItemsInputDTO_37)serviceItemsDAO_37.queryServiceItemsInfoExtra_37(serviceItemsInputDTO);
	}
	/**
	 *复效经办信息录入页面
	 */
	public ServiceItemsInputDTO_6 queryServiceItemsInfo_6(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		boolean ishaveprd = false;
		ServiceItemsInputDTO_6 items6= (ServiceItemsInputDTO_6)serviceItemsDAO_6.queryServiceItemsInfoExtra_6(serviceItemsInputDTO);
		List<PolicyProductDTO> productList = items6.getPolicyProductList();
		for (int i = 0; productList != null && i < productList.size(); i++) {
			PolicyProductDTO product = productList.get(i);
			if (!"N".equals(product.getCanBeSelectedFlag())) {
				product.setSelected(true);
				ishaveprd = true;
			}

		}
		if (!ishaveprd) {
			throw new RuntimeException("该保单没可操作复效的险种");
		}
		items6.setPolicyProductList(productList);
		return items6;
	}
	/**
	 * 根据posNo查询受理录入页面的详细信息
	 * 
	 * @param posNo
	 * @return
	 */
	public ServiceItemsInputDTO queryAcceptDetailInputByPosNo(String posNo) {
		return branchQueryService.queryAcceptDetailInputByPosNo(posNo);
	}

	/**
	 * 查询批次中第一 个状态为A01的受理
	 * 
	 * @param posBatchNo
	 *            批次号
	 * @return
	 */
	public String getNextPosNoInBatch(String posBatchNo) {

		return branchAcceptService.getNextPosNoInBatch(posBatchNo);
	}

	/**
	 * 获取字典表信息
	 * 
	 * @param codeTableName
	 *            常用参数'POS_ROLLBACK_CAUSE_CODE'取消/回退原因 'DIVIDEND_SELECTION'红利选择
	 *            参考 CodeTableNames.java 描述类型
	 * @return
	 */

	public List<CodeTableItemDTO> queryCodeTable(String codeTableName) {

		return posCodeTableDAO.queryCodeTable(codeTableName);
	}

	/**
	 * 查询批次受理断点位置
	 * 
	 * @param posBatchNo
	 *            批次号
	 * @return Map&lt;String, Object&gt;包含Key:<br/>
	 *         breakStatusCode:断点的保全状态<br/>
	 *         posNo:批次受理时需跳转的保全号<br/>
	 *         processResultList:预处理完成时跳转时需要的经办结果
	 */
	public Map<String, Object> queryBreakInfoByPosBatchNo(String posBatchNo) {

		return branchQueryService.queryBreakInfoByPosBatchNo(posBatchNo);
	}

	/**
	 * 查询保全申请批次信息
	 * 
	 * @param posNo
	 * @return
	 */
	public PosApplyBatchDTO queryPosApplyBatchRecord(String posBatchNo) {
		return commonQueryDAO.queryPosApplyBatchRecord(posBatchNo);
	}

	/**
	 * 查询保全申请书信息
	 * 
	 * @param posNo
	 * @return
	 */
	public PosApplyFilesDTO queryPosApplyfilesRecord(String barcodeNo) {
		return commonQueryDAO.queryPosApplyfilesRecord(barcodeNo);
	}

	/**
	 * 查询保全信息
	 * 
	 * @param posNo
	 * @return
	 */
	public PosInfoDTO queryPosInfoRecord(String posNo) {
		return commonQueryDAO.queryPosInfoRecord(posNo);
	}

	/**
	 * 查询简要批次受理信息
	 * 
	 * @param posBatchNo
	 * @return
	 */
	public List<Map<String, String>> queryBatchPosInfo(String posBatchNo) {
		return commonQueryDAO.queryBatchPosInfo(posBatchNo);
	}

	/**
	 * 查询保全受理信息
	 * 
	 * @param posNo
	 * @return
	 */
	public List<String> queryPosAcceptDetailInfo(String posNo,
			String posObject, String itemNo) {

		return commonQueryDAO
				.queryPosAcceptDetailInfo(posNo, posObject, itemNo);

	}

	/**
	 * 根据posNo查询detail表信息
	 * 
	 * @param posNo
	 * @return
	 */
	public List<Map<String, Object>> getPosAcceptDetailByPosNo(String posNo) {
		return commonQueryDAO.getPosAcceptDetailByPosNo(posNo);
	}

	/**
	 * 查询保全员相关的保全信息列表
	 * 
	 * @param acceptor
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<Map<String, Object>> queryBatchAcceptList(String acceptor,
			Date startDate, Date endDate) {

		return acceptEntryDAO
				.queryBatchAcceptList(acceptor, startDate, endDate);
	}

	// 查询前一单是否已经受理完成
	public boolean prePosnoIsEnd(String posNo) {
		return branchAcceptService.prePosnoIsEnd(posNo);
	}

	/**
	 * 查询保单状态
	 * 
	 * @param policyNo
	 * @return
	 */
	public String getPolicyDutyStatus(String policyNo) {

		return commonQueryDAO.getPolicyDutyStatus(policyNo);
	}

	/**
	 * 查询移动保全问题件代办列表
	 * 
	 * @param accessor
	 *            处理人ID
	 * @return List&lt;PosProblemItemsDTO&gt;
	 */
	public List<PosProblemItemsDTO> queryProblemTodoListForMPos(String accessor) {

		return problemDAO.queryProblemTodoListForMPos(accessor);
	}

	/**
	 * 问题件处理，回复处理
	 * 
	 * @param problemItemsDTO
	 */
	public void replyProblem(PosProblemItemsDTO problemItemsDTO) {
		problemService.replyProblemForRpc(problemItemsDTO);

	}

	/**
	 * 根据问题件号查询问题件
	 * 
	 * @param problemItemNo
	 * @return
	 */
	public PosProblemItemsDTO queryProblemItemsByID(String problemItemNo) {
		return problemDAO.queryProblemItemsByID(problemItemNo);
	}

	/**
	 * 生成批单文件流
	 * 
	 * @param posNo
	 * @param submiter
	 * @return
	 */
	public SFFile createOutputStreamForEnt(String posNo, String submiter) {

		Map<String, Object> posInfo = printService
				.queryEndorsementInfoByPosNo(posNo);
		if (posInfo == null || posInfo.isEmpty()) {
			throw new RuntimeException("找不到批单，或者保全申请还未生效");

		}
		// 更新批单打印人员信息
		printDAO.updatePosInfoEntData(posNo, submiter);
		NoticePrintHandler handler = NoticePrintHandlerFactory
				.getNoticePrintHandler("ent");
		SFFile tmpFile = new SFFile();
		try {
			handler.handleSingleNoticePrint(posNo).renameTo(tmpFile);
			return tmpFile;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);

		} finally {

			tmpFile.getRpcAttach().setDeleteAfterReturn(true);
		}
	}

	/**
	 * 处理电子批单 1.根据保全号获取电子批单 2.电子批单加盖电子印章 3.电子批单上传服务器 4.发送邮件
	 */
	public List<Map<String, String>> handPDFEnt(String posNo, UserInfo userInfo)
			throws IOException, IDGClientException {
		List<Map<String, String>> entUrlList = new ArrayList<Map<String, String>>();
		List<Map<String, Object>> sffileList = new ArrayList<Map<String, Object>>();

		// 从POS获得电子批单
		SFFile file = createOutputStreamForEnt(posNo, userInfo.getEmail());
		// 电子印章
		SFFile sigFile = signature(file, posNo);
		// 上传服务器
		logger.info("######## handPDFEnt before saveFile 上传服务器");
		String url = saveFile(posNo, userInfo.getEmail(), sigFile);
		logger.info("######## handPDFEnt after saveFile 上传服务器");

		// 返回页面链接名称和地址
		Map<String, String> entUrlMap = new HashMap<String, String>();
		entUrlMap.put("posNo", posNo);
		entUrlMap.put("url", url);
		entUrlList.add(entUrlMap);
		// 添加电子批单为邮件附件
		Map<String, Object> fileMap = new HashMap<String, Object>();
		fileMap.put("fileName", posNo + "保险批单");
		fileMap.put("sigfile", sigFile);
		sffileList.add(fileMap);
		file.getRpcAttach().setDeleteAfterReturn(true);
		sigFile.getRpcAttach().setDeleteAfterReturn(true);

		logger.info("######## handPDFEnt before sendPosEntMail 发送邮件");
		sendPosEntMail(userInfo.getEmail(), posNo, userInfo.getName(),
				sffileList);
		logger.info("######## handPDFEnt after sendPosEntMail 发送邮件");

		return entUrlList;
	}

	/**
	 * 电子批单加盖电子印章
	 */
	public SFFile signature(SFFile file, String po) throws IOException,
			IDGClientException {
		IDGClientImpl idg = new IDGClientImpl(nextSigIP(), RuntimeConfig.get(
				"indogoPort", String.class));
		logger.info("======================indogoPort"
				+ RuntimeConfig.get("indogoPort", String.class));
		int timeout = 10000000;
		idg.setTimeOut(timeout);
		IIDGResult result = null;
		InputStream[] is = null;

		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<root>"
				+ "<rule>" + "<fileid>" + po + "</fileid>" + "<pdfname>" + po
				+ ".pdf" + "</pdfname>" + "<pdftype>" + "toprsement"
				+ "</pdftype>" + "</rule>" + "</root>";

		is = new InputStream[2];
		InputStream returnIn = null;
		FileOutputStream fos = null;
		SFFile sffile = new SFFile();
		try {
			is[0] = new ByteArrayInputStream(xml.getBytes("utf-8"));
			is[1] = new FileInputStream(file);
			result = idg.submitJob(is, new String[] { "RULEXML", "PDF" }, "",
					"", "", new String[] { "", "" });
			InputStream[] streams = result.getStreams();
			if (streams != null && streams.length > 0) {
				for (int j = 0; j < streams.length; j++) {
					returnIn = streams[j];
					break;
				}
			}

			fos = new FileOutputStream(sffile);
			Util.copyStream(returnIn, fos);
		} finally {
			try {
				is[1].close();
			} catch (Throwable e) {

			}
			try {
				is[0].close();
			} catch (Throwable e) {

			}
			try {
				returnIn.close();
			} catch (Throwable e) {

			}

			try {
				fos.close();
			} catch (Throwable e) {

			}
		}
		return sffile;
	}

	/**
	 * 上传电子保单到IM服务器
	 */
	public String saveFile(String posNo, String saveBy, SFFile sigFile) {
		SFFilePath destPath = new SFFilePath();
		destPath.setModule("pos");
		destPath.setModuleSubPath(new String[] { "ent" });
		destPath.setStogeType(StogeType.DAY);
		logger.info("######## PlatformContext.getIMFileService().putFile(sigFile, destPath) begin");
		String fileId = PlatformContext.getIMFileService().putFile(sigFile,
				destPath);
		logger.info("######## PlatformContext.getIMFileService().putFile(sigFile, destPath) end. filedId:"
				+ fileId);
		// 记录电子批单到文件表
		posFileDAO.insertPosFileInfo(fileId, posNo, null, posNo, "7", saveBy);

		logger.info("######## insertPosFileInfo");
		return fileId;
	}

	/**
	 * 发送电子批单邮件
	 */
	public void sendPosEntMail(String email, String posNo, String userName,
			List<Map<String, Object>> sffileList) {
		Mail mail = new Mail();
		mail.setSubject("富德生命人寿电子批单");// 邮件标题
		mail.setTo(new String[] { email });// 收件人
		mail.setForm("epos@sino-life.com");// 发件人
		mail.setMailType(MailType.HTML_CONTENT);// 邮件类型
		Map rstMap = rollbackDAO.queryOriginalPosInfo(posNo);
		String content = "尊敬的客户:<br/>&nbsp;&nbsp;&nbsp;&nbsp;您对保单"
				+ (String) rstMap.get("POLICY_NO")
				+ "的"
				+ (String) rstMap.get("SERVICE_ITEMS_DESC")
				+ "操作成功,现将保全号为"
				+ posNo
				+ "的保险批单电子版发送给您。<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;富德生命人寿保险股份有限公司";
		mail.setContent(content);// 邮件内容
		for (Map<String, Object> map : sffileList) {
			try {
				mail.addAttachment(
						MimeUtility.encodeText(map.get("fileName") + ".pdf"),
						(SFFile) map.get("sigfile"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		mailService.send(mail);
	}

	/**
	 * 查询是否淘宝通过网销操作保全（退保，契撤）的保单
	 * 
	 * @param policy_no
	 * @param prodSeq
	 * @return Y/N
	 */
	public String isTbToWxPolicy(String policyNo, String prodSeq) {
		return commonQueryDAO.isTbToWxPolicy(policyNo, prodSeq);
	}

	/**
	 * 根据保单号查询支付宝账号
	 * 
	 * @param policy_no
	 * @param
	 * @return
	 */
	public String getZhiFuBaoAccount(String policyNo) {
		Map<String, Object> paraMap = commonQueryDAO.getPolicySpinfoDetail(
				policyNo, "ALIPAYBUYERID");
		return (String) paraMap.get("p_def_info");
	}

	/**
	 * 查询保单原缴费账户信息
	 * 
	 * @param policyNo
	 * @return
	 */
	public Map getPolicyBankacctno(String policyNo) {

		Map map = new HashMap();
		Map<String, Object> mapC = new HashMap<String, Object>();

		map = commonQueryDAO.getPolicyBankacctno(policyNo);
		if (map.get("acctBankCode") != null) {
			String bankCode = (String) map.get("acctBankCode");
			List<Map<String, Object>> lst = commonQueryDAO
					.queryBankCategory(bankCode);
			if (lst.size() > 0) {

				mapC = (Map<String, Object>) lst.get(0);
				map.put("bankCategory", mapC.get("BANKCATEGORY"));
				map.put("bankCategoryDesc", mapC.get("BANKCATEGORYDESC"));
			}

		}
		return map;
	}

	/**
	 * 根据保单号查询保单详细信息
	 * 
	 * @param policyNo
	 * @return
	 */
	public PolicyDTO queryPolicyInfoByPolicyNo(String policyNo) {

		return commonQueryDAO.queryPolicyInfoByPolicyNo(policyNo);
	}

	/**
	 * 判断该保单是否包含万能产品
	 * 
	 * @param policyNo
	 * @return boolean
	 */
	public boolean isIncludeFinanceProduct(String policyNo) {

		boolean isIncludeFinanceProduct = false;

		// 查询产品信息
		List<PolicyProductDTO> policyProductList = commonQueryDAO
				.queryPolicyProductListByPolicyNo(policyNo);
		if (policyProductList != null && !policyProductList.isEmpty()) {
			for (PolicyProductDTO pp : policyProductList) {

				// 判断主险是否为万能险产品
				if (pp.getProdSeq().intValue() == 1) {

					isIncludeFinanceProduct = posRulesDAO.isUniversal(pp
							.getProductCode());

				}
			}

		}
		return isIncludeFinanceProduct;
	}

	/**
	 * --功能说明： 取理财险的账户价值明细 注意:对于投连险种,如不能实时交易,则以最近交易价格进行估算 参数说明： 输入参数：
	 * p_policy_no ：保单号 p_calc_date : 计算日期(受理申请日期) 输出参数： p_fin_type 理财类型 N 非理财
	 * ，U 万能，F投连 p_first_prem 首期保费, p_first_add_prem 首期追加, p_add_prem 保全追加,
	 * p_withdraw_prem 保全部分领取, p_total_amount 账户价值, p_benefit_amount 累计收益,
	 * p_flag 返回标志 '0' 成功 '1' 失败0 p_message 报错信息
	 * 
	 */
	public Map<String, Object> getFinValueInfo(String policyNo, Date requestDate) {
		Map<String, Object> finMap = new HashMap<String, Object>();
		finMap = commonQueryDAO.getFinValueInfo(policyNo, requestDate);
		return finMap;
	}

	/**
	 * 查询投连账户信息
	 * 
	 * @param policyNo
	 * @return
	 */
	public List<FinancialProductsDTO> getFinancialProductsList(String policyNo,
			String prodSeq) {
		List<FinancialProductsDTO> financialProductsList = (List<FinancialProductsDTO>) commonQueryDAO
				.getFinancialProductsList(policyNo, prodSeq);
		return financialProductsList;
	}

	/*
	 * 保单回执签收完成接口
	 * 
	 * @see com.sinolife.pos.pubInterface.biz.service.DevelopPlatformInterface#
	 * receivePolicyMessage(java.lang.String)
	 */
	public Map<String, Object> receivePolicyMessage(String policyNo) {
		Map<String, Object> pMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(policyNo)) {
			pMap.put("flag", "N");
			pMap.put("message", "请输入保单号");
			return pMap;
		}
		String exist = commonQueryDAO.existByPolicyNo(policyNo);
		if ("N".equals(exist)) {
			pMap.put("flag", "N");
			pMap.put("message", "无此保单记录");
			return pMap;
		}
		String signFlag = posRulesDAO.policySignFlag(policyNo);
		// 对状态为待回销的进行操作
		if ("D".equals(signFlag)) {

			pMap.put("policyNo", policyNo);
			pMap.put("signFlag", "E");
			Date systemDate = commonQueryDAO.getSystemDate();
			SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
			Date signDate = DateUtil.stringToDate(
					formatDate.format(systemDate), "yyyy-MM-dd");
			pMap.put("signDate", signDate);
			receiptDAO.pubProcUpPolContract(pMap);
			receiptDAO.updateReceiptSource(policyNo,"1");
			return pMap;
		}
		pMap.put("policyNo", policyNo);
		pMap.put("signFlag", signFlag);
		pMap.put("flag", "N");
		pMap.put("message", "回执状态不为待签收状态");
		return pMap;
	}

	/*
	 * 回写电子签名回执的file_id
	 * 
	 * @see com.sinolife.pos.pubInterface.biz.service.DevelopPlatformInterface#
	 * updateESignaturePolicyFileId(java.lang.String, java.lang.String)
	 */
	public Map<String, Object> updateESignaturePolicyFileId(String policyNo,
			String eSignatureFileId) {
		Map<String, Object> pMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(policyNo)) {
			pMap.put("flag", "N");
			pMap.put("message", "请输入保单号");
			return pMap;
		}
		String exist = commonQueryDAO.existByPolicyNo(policyNo);
		if ("N".equals(exist)) {
			pMap.put("flag", "N");
			pMap.put("message", "无此保单记录");
			return pMap;
		}
		return receiptDAO.updateESignaturePolicyFileId(policyNo,
				eSignatureFileId);

	}

	/*
	 * 代理人的电子回执回销记录查询
	 * 
	 * @see com.sinolife.pos.pubInterface.biz.service.DevelopPlatformInterface#
	 * queryreceivePolicyMessageList(java.util.Date, java.util.Date,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public List<Map<String, Object>> queryReceivePolicyMessageList(Map param) {
		if (param.get("agentNo") != null) {
			return acceptEntryDAO.queryReceivePolicyMessageList(param);
		}
		return null;
	}

	/*
	 * 获取当前保单保签收信息
	 * 
	 * @see com.sinolife.pos.pubInterface.biz.service.DevelopPlatformInterface#
	 * queryReceivePolicyInfo(java.lang.String, java.lang.String)
	 */
	public Map<String, Object> queryReceivePolicyInfo(String policyNo,
			String agentNo) {
		Map<String, Object> pMap = new HashMap<String, Object>();
		if (StringUtils.isBlank(policyNo)) {
			pMap.put("flag", "N");
			pMap.put("message", "请输入保单号");
			return pMap;
		}
		String exist = commonQueryDAO.existByPolicyNo(policyNo);
		if ("N".equals(exist)) {
			pMap.put("flag", "N");
			pMap.put("message", "无此保单记录");
			return pMap;
		}
		String printFlag=commonQueryDAO.queryPrintFlagByPolicyNo(policyNo);
		if ("N".equals(printFlag)) {
			pMap.put("flag", "N");
			pMap.put("message", "此保单尚未打印");
			return pMap;
		}
		String signFlag = commonQueryDAO.querySignFlagByPolicyNo(policyNo);
		if ("Y".equals(signFlag)) {
			pMap.put("flag", "N");
			pMap.put("message", "此保单已签收");
			return pMap;
		}
		if ("N".equals(signFlag)) {
			pMap.put("flag", "N");
			pMap.put("message", "此保单无需签收");
			return pMap;
		}
		if ("E".equals(signFlag)) {
			pMap.put("flag", "N");
			pMap.put("message", "此保单待复核");
			return pMap;
		}
		return commonQueryDAO.queryReceivePolicyInfo(policyNo, agentNo);
	}

	/**
	 * 查询VIP等级,卡号
	 * 
	 * @param clientNo
	 *            客户号
	 * @return Map&lt;String, Object&gt;包含如下key:<br/>
	 *         p_client_no: 客户号<br/>
	 *         p_vip_grade:VIP等级代码<br/>
	 *         p_vip_desc:VIP等级描述<br/>
	 *         p_card_no：vip卡号 p_flag:成功标志 0:成功 1:失败<br/>
	 *         p_message:返回信息
	 */
	public Map<String, Object> getVipInfo(String clientNo) {
		return vipManageDAO.getVipInfo(clientNo);
	}

	/**
	 * 查询VIP等级,卡号
	 * 
	 * @param p_client_no
	 *            :vip客户号(非空) p_card_no :vip卡号(非空) p_modified_date ：变更时间
	 *            p_modified_reason ：变更原因 p_provided_user ：发放人 p_start_date
	 *            :开始时间
	 * @return p_flag:成功标志 0:成功 1:失败<br/>
	 *         p_message:返回信息
	 */
	public Map<String, Object> updateVipCard(String p_client_no,
			String p_card_no, Date p_modified_date, String p_modified_reason,
			String p_provided_user, Date p_start_date) {
		return vipManageDAO.updateVipCard(p_client_no, p_card_no,
				p_modified_date, p_modified_reason, p_provided_user,
				p_start_date);
	}

	/**
	 * 取理财险某个时间段的账户价值明细
	 * 
	 * @methodName: getFinValueInfoList
	 * @Description:
	 * @param policyNo
	 *            :保单号
	 * @param startDate
	 *            :开始日期
	 * @param endDate
	 *            :结束日期
	 * @return List<Map<String,Object>>
	 * @author WangMingShun
	 * @date 2015-6-30
	 * @throws
	 */
	public List<Map<String, Object>> getFinValueInfoList(String policyNo,
			Date startDate, Date endDate) {
		return commonQueryDAO.getFinValueInfoList(policyNo, startDate, endDate);
	}
	
	/**
	 * 查询保单是否处于犹豫期内
	 * @param policyNo
	 * 			保单号
	 * @return 
	 * 		p_item_type : 保单是否过犹豫期  1:犹豫期内；2:犹豫期外
	 * 		p_flag:成功标志 0:成功 1:失败<br/>
	 * 		p_message:返回信息
	 * @throws Exception 
	 */
	public Map<String, Object> getPolicyScrupleDate(String policyNo) {
		// 获取当前时间
		Date sysdate = commonQueryDAO.getSystemDate();
		Map<String, Object> retMap = new HashMap<String, Object>();
		// 根据保单号，查询保单犹豫期截止日期
		Map periodMap = commonQueryDAO.querypCruplePeriodPolicy(policyNo);
		if(periodMap.get("p_scruple_date")== null){
			retMap.put("p_flag", 1);
			retMap.put("p_message", "犹豫期为空");
		}else{
			//日期装换
			Date scruple_date = (Date) periodMap.get("p_scruple_date");
			if (scruple_date == null) {
				retMap.put("p_flag", 1);
				retMap.put("p_message", "犹豫期为空");
			}else{
				if(scruple_date.after(sysdate)){
					//1:犹豫期内
					retMap.put("p_item_type", 1);
					retMap.put("p_scruple_date", scruple_date);
					retMap.put("p_flag", 0);
					retMap.put("p_message", "犹豫期内");
				}else{
					//2:犹豫期外
					retMap.put("p_item_type", 2);
					retMap.put("p_scruple_date", scruple_date);
					retMap.put("p_flag", 0);
					retMap.put("p_message", "犹豫期外");
				}
			}
		}
		return retMap;
	}
	
	
	/**
	 * 根据险种和到期时间查询保单客户信息
	 * 
	 * @methodName: getPolicyClientListByProductCode
	 * @Description:
	 * @param productCode
	 *            :险种代码 
	 * @param endDate
	 *            :结束日期
	 * @return List<Map<String,Object>>
	 * policy_no 保单号, full_name 险种名称, applicant_no 投保人客户号, client_name 客户姓名, birthday 生日, sex_code 性别, id_type 证件类型, idno 证件号码
	 * @date 2015-7-21
	 * @throws
	 */
	public List<Map<String, Object>> getPolicyClientListByproductCode(String productCode,
			Date endDate) {
		return commonQueryDAO.getPolicyClientListByproductCode(productCode, endDate);
	}
	/**
	 * 根据保单号、计算日期,部分领取金额计算手续费
	 * 
	 * @param policyNo
	 *            保单号
	 * @param    p_withdraw_amount 领取金额
	 * @param calcDate
	 *            计算日期
	 * @return Map<String, Object> 包含如下Key：<br/>
              p_withdraw_charge_amount     :领取手续费金额
              p_calculate_formula          :计算公式
              p_calculate_parameter        :计算参数值
              p_flag                       :返回标志   '0'  成功   ,'1'  失败，调用者需要进行异常处理                                   
              p_message                    :返回信息
	 */
	public Map<String, Object> calcWithdrawCharge(String policyNo,String p_withdraw_amount,
			Date calcDate) {
		return commonQueryDAO.calcWithdrawCharge(policyNo, p_withdraw_amount, calcDate);
	}
	

	/**
	 *  保单号查询保全信息 (参数Map里面只能传policyNo   保单号)
	 */
	public List<PosPolicy> getPosPolicyList(Map map){
		List<PosPolicy> posPolicyList = commonQueryDAO.getPosPolicyList(map);
		return posPolicyList;
	}

	/**
	 * @Description: 根据客户号和产品代码获取对应的保单号
	 * @methodName: getPosPolicyList
	 * @param map
	 * @return
	 * @return List<PosPolicy>
	 * @author WangMingShun
	 * @date 2015-11-17
	 * @throws
	 */
	public List<String> getPolicyListByClientNoAndProductCode(
			String clientNo, String productCode) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("clientNo", clientNo);
		paraMap.put("productCode", productCode);
		return commonQueryDAO.getPolicyListByClientNoAndProductCode(paraMap);
	}

	/**
	 * 根据客户号获取客户对应的资产证明通知书流
	 * @return Map<String, Object> 包含如下key:
	 * 		flag(String)				:0表示成功获取数据，1表示没有获取到数据
	 * 		msg(String)					:失败的时候才会得到失败信息
	 * 		file(File)					:成功的时候才会得到该文件
	 * @author WangMingShun
	 * @date 2016-2-22
	 */
	@Override
	public Map<String, Object> getClientWeathNoticeStream(String clientNo) {
		String flag = "1";//0表示成功，1表示失败
		Map<String, Object> resultMap = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date serverDate = null;
    	//获取数据库时间并格式化去掉时分秒
    	String dateStr = PosUtils.formatDate(commonQueryDAO.getSystemDate(), "yyyy-MM-dd");
    	try {
    		serverDate = sdf.parse(dateStr);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		//取得通知书处理器
    	NoticePrintHandler handler = NoticePrintHandlerFactory.getNoticePrintHandler("14");
    	SFFile tmpFile = null;
    	String fileID = null;
		try {
			String detailSequenceNo = null;
			//生成资产证明书
			Map<String, Object> retMap = printDAO.procClientWealthNotice(clientNo, serverDate);
			if(!"0".equals(retMap.get("p_flag"))) {
				resultMap.put("msg", "逐单抽档投保人名下保单资产证明书失败：" + retMap.get("p_message"));
			} else {
				detailSequenceNo = (String) retMap.get("p_detail_sequence_no");
				if(detailSequenceNo == null || StringUtils.isBlank(detailSequenceNo)) {
					resultMap.put("msg", "没有符合查询条件的资产证明书记录");
				} else {
					//生成PDF
					tmpFile = (SFFile) handler.handleSingleNoticePrint(detailSequenceNo);
					//上传到IM服务器
					fileID = uploadFileToIM(tmpFile);
					flag = "0";
				}
			}
			resultMap.put("flag", flag);
			resultMap.put("fileID", fileID);
			logger.info("上传客户资产证明书成功：--->fileID:" + fileID + ",clientNo:" + clientNo);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			resultMap.put("flag", flag);
			resultMap.put("msg", e.getMessage());
		}
		return resultMap;
	}
	
	/**
	 * 上传pdf到IM服务器
	 * 
	 * @param tmpFile
	 * @return String
	 * @author 
	 * @time 2014-6-27
	 */
	public String uploadFileToIM(SFFile tmpFile) {
		try {
			SFFilePath fileDestinationPath = new SFFilePath();
			fileDestinationPath.setModule("pos");
			fileDestinationPath.setModuleSubPath(new String[] { "report_list" });
			fileDestinationPath.setStogeType(StogeType.WEEK);
			fileDestinationPath.setMimeType(MimeType.application_pdf);
			String fileId = PlatformContext.getIMFileService().putFile(tmpFile, fileDestinationPath);
			return fileId;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 官网复效是否需要职业告知
	 * @param policyNo
	 * @return String
	 * @author TangJing
	 * @date 2016-5-3
	 */
	public String websiteAnswerEffect(String policyNo) {
		return commonQueryDAO.websiteAnswerEffect(policyNo);
	}
	
	/**
	 * 官网复效是否需要投保人告知
	 * @param policyNo
	 * @return String
	 * @author TangJing
	 * @date 2016-5-3
	 */
	public String websiteAnswerEffect2(String policyNo) {
		return commonQueryDAO.websiteAnswerEffect2(policyNo);
	}

	/**
	 * 核心系统预约退保
	 */
	public ServiceItemsInputDTO_45 queryServiceItemsInfo_45(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return serviceItemsDAO_45
				.queryServiceItemsInfoExtra_45(serviceItemsInputDTO);
	}

	/**
	 * 官网复效是否满足二核规则
	 */
	public String checkPolicyClientRisk(String p_policy_no) {
		return commonQueryDAO.checkPolicyClientRisk(p_policy_no);
	}
	
	/**
	 * 保全试算外部接口
	 * 
	 * @param posInfo
	 * @param acceptUser
	 */
	public Map<String, Object> acceptDetailInputSubmit(
			final ServiceItemsInputDTO sii, final PosApplyBatchDTO applyInfo,
			final String mposMobile) {
		try {
			Map<String, Object> processResult = txTmpl
					.execute(new TransactionCallback<Map<String, Object>>() {
						@Override
						public Map<String, Object> doInTransaction(
								TransactionStatus transactionStatus) {
							try {
								return queryInputservice(sii, applyInfo,
										mposMobile);
							}finally {
								transactionStatus.setRollbackOnly();
							}
						}
					});
			return processResult;
		} catch (Exception e) {
			throw new RuntimeException("保全试算失败" + e.getMessage());
		}
	}

	/**
	 * 保全试算
	 * 
	 * @param sii
	 * @param applyInfo
	 * @return
	 */
	public Map<String, Object> queryInputservice(ServiceItemsInputDTO sii,
			PosApplyBatchDTO applyInfo, String mposMobile) {

		Map<String, Object> retMap = new HashMap<String, Object>();
		String SL_RSLT_MESG = null;
		String SL_RSLT_CODE = null;
		String APPROVE_TEXT = null;
		try {
			String policyNo = sii.getPolicyNo();
			logger.info("移动保全->保单号"
					+ policyNo
					+ "受理开始： started at "
					+ PosUtils.formatDate(commonQueryDAO.getSystemDate(),
							"yyyy-MM-dd HH:mm:ss:SSS"));
			// 批量受理信息录入
			PosApplyBatchDTO batch = createBatch(applyInfo, mposMobile);
			logger.info("移动保全->保单号"
					+ policyNo
					+ "受理结束： ended at "
					+ PosUtils.formatDate(commonQueryDAO.getSystemDate(),
							"yyyy-MM-dd HH:mm:ss:SSS"));
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

				}

				// 这些渠道都是逐单受理，因此前一单受理完成，更新状态为 A03待处理规则检查
				branchAcceptService.updatePosInfo(posNo, "accept_status_code",
						"A03");

				// 调用流转接口
				logger.info("移动保全->保全号"
						+ posNo
						+ "保单号："
						+ policyNo
						+ "流转接口调用开始： started at "
						+ PosUtils.formatDate(commonQueryDAO.getSystemDate(),
								"yyyy-MM-dd HH:mm:ss:SSS"));
				retMap = branchAcceptService
						.workflowControl("03", posNo, false);
				logger.info("移动保全->保全号"
						+ posNo
						+ "保单号："
						+ policyNo
						+ "流转接口调用结束： ended at "
						+ PosUtils.formatDate(commonQueryDAO.getSystemDate(),
								"yyyy-MM-dd HH:mm:ss:SSS"));
				logger.info("acceptDetailInputSubmit workflowControl return:"
						+ retMap);
				String flag = (String) retMap.get("flag");
				String msg = (String) retMap.get("message");
				String resultDesc = (String) retMap.get("resultDesc");
				retMap = branchQueryService.queryProcessResult(posNo);
				logger.info("移动保全试算接口保单："+policyNo+"规则处理结果："+flag+"--------msg-------"+msg+"--------resultDesc-------"+resultDesc);
				if ("C01".equals(flag)) {
					// 规则检查不通过
					SL_RSLT_CODE = "999C01";
					SL_RSLT_MESG = msg;
				} else if ("A19".equals(flag)) {
					posInfo = commonQueryDAO.queryPosInfoRecord(posNo);
					APPROVE_TEXT = posInfo.getApproveText();// 批文
					SL_RSLT_CODE = "999999";
					SL_RSLT_MESG = "试算成功！";

				} else if ("A03".equals(flag)) {
					SL_RSLT_CODE = "999002";
					SL_RSLT_MESG = resultDesc;
				}
			}
		} catch (Exception e) {
		    logger.info("移动保全 -> " + sii.getPolicyNo() + " caught an exception", e);
			SL_RSLT_CODE = "999003";
			SL_RSLT_MESG = e.getMessage();
		}
		retMap.put("SL_RSLT_MESG", SL_RSLT_MESG);
		retMap.put("SL_RSLT_CODE", SL_RSLT_CODE);
		retMap.put("APPROVE_TEXT", APPROVE_TEXT);
		return retMap;

	}

	/**
	 * 批单结果确认生效
	 * 
	 */
	public Map<String, Object> processSubmit(final ServiceItemsInputDTO sii,
			final PosApplyBatchDTO applyInfo, final String mposMobile) {
		return txTmpl.execute(new TransactionCallback<Map<String, Object>>() {
			@Override
			public Map<String, Object> doInTransaction(
					TransactionStatus transactionStatus) {
				Map<String, Object> processResult = new HashMap<String, Object>();
				String posNo = null;
				try {
					String SL_RSLT_MESG = null;
					String SL_RSLT_CODE = null;
					String APPROVE_TEXT = null;
					String BARCODE_NO = null;

					String policyNo = sii.getPolicyNo();
					logger.info("移动保全->保单号"
							+ policyNo
							+ "受理开始： started at "
							+ PosUtils.formatDate(
									commonQueryDAO.getSystemDate(),
									"yyyy-MM-dd HH:mm:ss:SSS"));
					// 批量受理信息录入
					PosApplyBatchDTO batch = createBatch(applyInfo, mposMobile);
					logger.info("移动保全->保单号"
							+ policyNo
							+ "受理结束： ended at "
							+ PosUtils.formatDate(
									commonQueryDAO.getSystemDate(),
									"yyyy-MM-dd HH:mm:ss:SSS"));
					PosInfoDTO posInfo = new PosInfoDTO();
					posNo = batch.getPosInfoList().get(0).getPosNo();

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
						branchAcceptService.updatePosInfo(posNo, "remark",
								sii.getRemark());

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
						logger.info("移动保全->保全号"
								+ posNo
								+ "保单号："
								+ policyNo
								+ "流转接口调用开始： started at "
								+ PosUtils.formatDate(
										commonQueryDAO.getSystemDate(),
										"yyyy-MM-dd HH:mm:ss:SSS"));
						processResult = branchAcceptService.workflowControl(
								"03", posNo, false);
						logger.info("移动保全->保全号"
								+ posNo
								+ "保单号："
								+ policyNo
								+ "流转接口调用结束： ended at "
								+ PosUtils.formatDate(
										commonQueryDAO.getSystemDate(),
										"yyyy-MM-dd HH:mm:ss:SSS"));
						logger.info("acceptDetailInputSubmit workflowControl'03' return:"
								+ processResult);
						String flag = (String) processResult.get("flag");
						String msg = (String) processResult.get("message");
						String resultDesc = (String) processResult
								.get("resultDesc");
						processResult = branchQueryService
								.queryProcessResult(posNo);
						logger.info("移动保全受理生效接口保单号："+policyNo+"规则处理结果："+flag+"--------msg-------"+msg+"--------resultDesc-------"+resultDesc);
						if ("C01".equals(flag)) {
							// 规则检查不通过
							SL_RSLT_CODE = "999C01";
							SL_RSLT_MESG = msg;
							// throw new RuntimeException(msg);
						} else if ("A19".equals(flag)) {
							// 待审批判断处理
							processResult = branchAcceptService
									.workflowControl("05", posNo, false);
							logger.info("acceptDetailInputSubmit workflowControl'05' return:"
									+ processResult);
							flag = (String) processResult.get("flag");
							if ("A15".equals(flag)) {
								// 待收费
								posInfo = commonQueryDAO
										.queryPosInfoRecord(posNo);
								APPROVE_TEXT = posInfo.getApproveText();// 批文
								BARCODE_NO = posInfo.getBarcodeNo();// 条码
								SL_RSLT_CODE = "999999";
								SL_RSLT_MESG = "待收费";
							} else if ("A20".equals(flag)) {
								// 生效完成
								posInfo = commonQueryDAO
										.queryPosInfoRecord(posNo);
								APPROVE_TEXT = posInfo.getApproveText();// 批文
								BARCODE_NO = posInfo.getBarcodeNo();// 条码
								SL_RSLT_CODE = "999999";
								SL_RSLT_MESG = "待对账处理（待收付费处理）";
							} else if ("A16".equals(flag)) {
								// 生效完成
								posInfo = commonQueryDAO
										.queryPosInfoRecord(posNo);
								APPROVE_TEXT = posInfo.getApproveText();// 批文
								BARCODE_NO = posInfo.getBarcodeNo();// 条码
								SL_RSLT_CODE = "999999";
								SL_RSLT_MESG = "待生效确认";
							} else if ("E01".equals(flag)) {
								// 生效完成
								posInfo = commonQueryDAO
										.queryPosInfoRecord(posNo);
								APPROVE_TEXT = posInfo.getApproveText();// 批文
								BARCODE_NO = posInfo.getBarcodeNo();// 条码
								// 淘宝渠道受理的保全生效完成后给客户发送电子批单，如果发送失败也正常生效保全
								SL_RSLT_CODE = "999999";
								SL_RSLT_MESG = "已生效";

							} else if ("A12".equals(flag)) {
								SL_RSLT_CODE = "999002";
								SL_RSLT_MESG = "该单需要人工核保，不能再银行受理";
								branchAcceptService.cancelAccept(posInfo,
										batch.getAcceptor());
							} else {
								SL_RSLT_CODE = "999002";
								SL_RSLT_MESG = flag;
								branchAcceptService.cancelAccept(posInfo,
										batch.getAcceptor());
							}
						} else if ("A03".equals(flag)) {
							SL_RSLT_CODE = "999002";
							SL_RSLT_MESG = resultDesc;
							branchAcceptService.cancelAccept(posInfo,
									batch.getAcceptor());
						}
					}
					processResult.put("SL_RSLT_MESG", SL_RSLT_MESG);
					processResult.put("SL_RSLT_CODE", SL_RSLT_CODE);
					processResult.put("APPROVE_TEXT", APPROVE_TEXT);
					processResult.put("BARCODE_NO", BARCODE_NO);
				} catch (Exception e) {
					logger.info("processResultSubmit exception" + posNo + e);
					processResult.put("SL_RSLT_CODE", "999003");
					processResult.put("SL_RSLT_MESG", e.getMessage());
					transactionStatus.setRollbackOnly();
				}
				return processResult;
			}
		});
	}

	/**
	 * 移动保全受理信息处理
	 * 
	 * @param sii
	 * @param applyInfo
	 * @param mposMobile
	 * @return PosApplyBatchDTO
	 * @author zhangyi.wb
	 * @time 2016-7-13
	 */
	public PosApplyBatchDTO createBatch(final PosApplyBatchDTO applyInfo,
			final String mposMobile) {

		List<ClientInformationDTO> clientInfoList = clientInfoDAO
				.selClientinfoForClientno(applyInfo.getClientNo());
		if (clientInfoList == null || clientInfoList.isEmpty()
				|| clientInfoList.size() != 1)
			throw new RuntimeException("无效的保单号：" + applyInfo.getClientNo());
		PosApplyFilesDTO posApplyFilesDto = new PosApplyFilesDTO();
		String applyType = applyInfo.getApplyTypeCode();
		String acceptChannelCode = applyInfo.getAcceptChannelCode();
		String clientNo = applyInfo.getClientNo();// 投保人
		String policyNo = applyInfo.getApplyFiles().get(0).getPolicyNo();
		String barcode = applyInfo.getApplyFiles().get(0).getBarcodeNo();
		String serviceItems = applyInfo.getApplyFiles().get(0)
				.getServiceItems();
		String insNo = commonQueryDAO.getInsuredByPolicyNo(policyNo).get(0);
		Map clientMap = new HashMap();
		// 满期金写被保人
		if ("10".equals(serviceItems)) {
			clientMap = commonQueryDAO.QueryClientInfoByclientNo(insNo);
		} else {
			clientMap = commonQueryDAO.QueryClientInfoByclientNo(clientNo);
		}
		String clientName = (String) clientMap.get("CLIENT_NAME");// 客户姓名
		String idtype = (String) clientMap.get("ID_TYPE");// 证件类型
		String idno = (String) clientMap.get("IDNO");// 证件号码
		if (StringUtils.isBlank(barcode)) {
			barcode = generateMPBarcode();
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
		batch.setAcceptor(applyInfo.getAcceptor());
		batch.setCounterNo("904");
		batch.setApplyTypeCode(applyType);

		// 默认申请类型为亲办
		if (applyType == null || "".equals(applyType)
				|| StringUtils.isBlank(applyType)) {
			batch.setApplyTypeCode("1");
		}
		// if ("2".equals(applyType) || "3".equals(applyType)
		// || "4".equals(applyType)) {
		// batch.setRepresentor(applyInfo.getRepresentor());
		// batch.setRepresentIdno(applyInfo.getRepresentIdno());
		// batch.setIdType(applyInfo.getIdType());
		// }

		List<PosApplyFilesDTO> applyFiles = new ArrayList<PosApplyFilesDTO>();
		for (PosApplyFilesDTO applyFile : applyInfo.getApplyFiles()) {
			PosApplyFilesDTO file = new PosApplyFilesDTO();
			file.setBarcodeNo(applyFile.getBarcodeNo());
			file.setPolicyNo(applyFile.getPolicyNo());
			file.setApplyDate(applyFile.getApplyDate());
			file.setForeignExchangeType("1");
			file.setServiceItems(applyFile.getServiceItems());
			file.setPaymentType(applyFile.getPaymentType());
			file.setApprovalServiceType(applyFile.getApprovalServiceType());
			if (file.getApplyDate() == null) {
				file.setApplyDate(commonQueryDAO.getSystemDate());
			}
			if (StringUtils.isBlank(applyFile.getApprovalServiceType())) {
				file.setApprovalServiceType("1");// 自领
			}
			if ("2".equals(applyFile.getApprovalServiceType())) {
				file.setAddress(applyFile.getAddress());
				file.setZip(applyFile.getZip());
			}
			String paymentType = applyFile.getPaymentType();
			if ("2".equals(paymentType)) {
				file.setAccountNoType(applyFile.getAccountNoType());
				file.setBankCode(applyFile.getBankCode());
				file.setTransferAccountno(applyFile.getTransferAccountno());

			}

			file.setTransferAccountOwner(applyFile.getTransferAccountOwner());
			file.setTransferAccountOwnerIdNo(applyFile
					.getTransferAccountOwnerIdNo());
			file.setTransferAccountOwnerIdType(applyFile
					.getTransferAccountOwnerIdType());

			applyFiles.add(file);
		}
		batch.setApplyFiles(applyFiles);

		// 生成受理
		branchAcceptService.generatePosInfoList(batch, false);
		logger.info("移动保全->保单号"
				+ policyNo
				+ "规则检查开始： started at "
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
		logger.info("移动保全->保单号"
				+ policyNo
				+ "规则检查结束： ended at "
				+ PosUtils.formatDate(commonQueryDAO.getSystemDate(),
						"yyyy-MM-dd HH:mm:ss:SSS"));
		// 生成捆绑顺序
		branchAcceptService.generateBindingOrder(batch);

		// 写受理
		batch = branchAcceptService.batchAccept(batch);

		if (mposMobile != null && !"".equals(mposMobile)) {
			// 写受理人工号到pos_accept_detail
			PosAcceptDetailDTO detail = null;

			for (PosInfoDTO posInfoDto : batch.getPosInfoList()) {

				detail = new PosAcceptDetailDTO();
				detail.setPosNo(posInfoDto.getPosNo());
				detail.setPosObject("5");
				detail.setProcessType("1");
				detail.setItemNo("016");
				detail.setObjectValue(posInfoDto.getPosNo());
				detail.setNewValue(batch.getAcceptor());// 移动保全受理人员
				detail.setOldValue(batch.getAcceptor());
				acceptDAO.insertPosAcceptDetail(detail);

				detail = new PosAcceptDetailDTO();
				detail.setPosNo(posInfoDto.getPosNo());
				detail.setPosObject("5");
				detail.setProcessType("1");
				detail.setItemNo("017");
				detail.setObjectValue(mposMobile);
				detail.setNewValue(mposMobile);// 移动保全通知电话
				detail.setOldValue(mposMobile);
				acceptDAO.insertPosAcceptDetail(detail);
				
				detail = new PosAcceptDetailDTO();
                detail.setPosNo(posInfoDto.getPosNo());
                detail.setPosObject("5");
                detail.setProcessType("1");
                detail.setItemNo("033");
                detail.setObjectValue(batch.getAcceptor());
                detail.setNewValue("Y");// 人脸识别标示
                detail.setOldValue(posInfoDto.getPosNo());
                acceptDAO.insertPosAcceptDetail(detail);

			}
		}
		return batch;

	}
}
