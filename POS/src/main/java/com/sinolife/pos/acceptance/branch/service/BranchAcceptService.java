package com.sinolife.pos.acceptance.branch.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.unihub.framework.util.common.DateUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.consts.CodeTableNames;
import com.sinolife.pos.common.dao.ClientInfoDAO;
import com.sinolife.pos.common.dao.CommonAcceptDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.LoginUserInfoDTO;
import com.sinolife.pos.common.dto.PersonalNoticeDTO;
import com.sinolife.pos.common.dto.PersonalNoticeDTO.InsProductInfo;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.common.dto.PosApplyFileMaterialsDTO;
import com.sinolife.pos.common.dto.PosApplyFilesDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.PosServiceItemSequenceSetDTO;
import com.sinolife.pos.common.dto.PosStatusChangeHistoryDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.rpc.ilogjrules.posrules.POSJrulesCheck;
import com.sinolife.pos.rpc.ilogjrules.posrules.UWJrulesCheck;
import com.sinolife.pos.rpc.ilogjrules.posrules.dao.PosRulesDAO;
import com.sinolife.pos.rpc.underwriting.PosUnderwritingService;
import com.sinolife.sf.http.util.log.Log;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMRuleInfoDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSVerifyResultDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.ServiceItemDomainDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPolicyDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.UWBOMRuleInfoDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.UWBOMVerifyResultDto;

/**
 * 注意：该类所自动注入的对象，必须在BUILD.XML中打进给LINTF的JAR包中，否则导出的JAR包，LINTF引用将会报异常
 */
@Component
@Transactional(propagation = Propagation.REQUIRED)
public class BranchAcceptService {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private CommonAcceptDAO acceptDAO;

	@Autowired
	private ClientInfoDAO clientInfoDAO;

	@Autowired
	private PosRulesDAO posRulesDAO;

	@Autowired
	private CommonQueryDAO commonQueryDAO;

	@Autowired
	private POSJrulesCheck posJrulesCheck;

	@Autowired
	private UWJrulesCheck uwJrulesCheck;

	@Autowired
	private PosUnderwritingService posUnderwritingService;

	/* 核保类保全项目 */
	private static Set<String> UNDWRT_SERVICE_ITEMS;

	/* 需投保规则检查的项目 */
	private static Set<String> APP_SERVICE_ITEMS;

	static {
		Set<String> set = Collections.synchronizedSet(new HashSet<String>());
		set.add("3"); // 3加保
		set.add("5"); // 5新增附加险
		set.add("6"); // 6复效
		set.add("12"); // 12年龄性别错误更正
		set.add("14"); // 14交费年期变更
		set.add("15"); // 15职业等级变更
		set.add("16"); // 16补充告知
		set.add("20"); // 20投保人变更
		set.add("22"); // 22受益人变更
		set.add("35"); // 35追加保费
		UNDWRT_SERVICE_ITEMS = set;

		set = Collections.synchronizedSet(new HashSet<String>());
		set.add("3"); // 3加保
		set.add("4"); // 4减保
		set.add("5"); // 5新增附加险
		set.add("6"); // 6复效
		set.add("12"); // 12年龄性别错误更正
		set.add("13"); // 13缴费频次变更
		set.add("14"); // 14交费年期变更
		set.add("15"); // 15职业变更
		set.add("20"); // 20投保人变更
		set.add("24"); // 24年金给付方式选择
		set.add("34"); // 34保险金转换年金
		set.add("36"); // 36基本保费变更
		APP_SERVICE_ITEMS = set;
	}

	/**
	 * 保全流转接口
	 * 
	 * @param procType
	 *            处理类型：
	 *            <ul>
	 *            <li>03处理规则检查结果导入</li>
	 *            <li>05审批规则检查</li>
	 *            <li>11受理取消</li>
	 *            <li>12保单补发打印任务完成</li>
	 *            </ul>
	 * @param posNo
	 *            保全号码
	 * @param result
	 *            规则检查结果
	 * @param omitRuleCheck
	 *            是否省略规则检查（省略则默认为规则检查通过），用于试算
	 * @return Map&lt;String, Object&gt; Key说明如下：
	 *         <ul>
	 *         <li>p_flag：
	 *         <ul>
	 *         <li>C01:规则不通过 procType:03</li>
	 *         <li>C02:人工取消 procType:11</li>
	 *         <li>A19:待审批判断处理 procType:03</li>
	 *         <li>A08:已送审 procType:05</li>
	 *         <li>A12:已送人工核保 procType:05</li>
	 *         <li>A15:待收费 procType:05</li>
	 *         <li>A17:待保单打印 procType:05</li>
	 *         <li>E01:生效完成 procType:05</li>
	 *         </ul>
	 *         </li>
	 *         <li>p_message:结果标志相关消息</li>
	 *         </ul>
	 */
	public Map<String, Object> workflowControl(String procType, String posNo,
			String result, String resultDesc, boolean omitRuleCheck) {
		String channelCode = commonQueryDAO.getacceptChannelByPosNo(posNo);
		PosInfoDTO posInfo = (PosInfoDTO) commonQueryDAO
				.queryPosInfoRecord(posNo);
		if ("03".equals(procType)) {
			resultDesc = "";
			if (omitRuleCheck) {
				result = "Y";
				resultDesc = "通过未检查";
			} else {
				String serviceItems = commonQueryDAO
						.queryServiceItemsByPosNo(posNo);
				boolean ruleCheckOK = true;
				// 仅检查部分保全项目
				if (APP_SERVICE_ITEMS.contains(serviceItems)) {
					// 投保规则检查
					Log.info(posNo+"startUwJrulesRules starting...");
					SUWBOMPolicyDto uwBom = uwJrulesCheck.createBOM(posNo);
					//增加bom属性信息到临时表中以便快速核实规则
					try{
						String bomContent =uwBom.toString();
			    
						posRulesDAO.insertRulesBom(posNo, "投保规则",bomContent);
					}catch(Exception e){
						logger.info("将调用规则引擎的的入参出参toString后放到数据库   出现异常");
						e.printStackTrace();
					}
					
					UWBOMVerifyResultDto uwVerifyResult = uwJrulesCheck
							.excuteRules(uwBom);
					Log.info(posNo+"endUwJrulesRules ending...");
					if (uwVerifyResult != null
							&& uwVerifyResult.getRuleInfos() != null
							&& uwVerifyResult.getRuleInfos().length > 0) {
						ruleCheckOK = false;
						result = "N";
						StringBuilder resultDescSB = new StringBuilder(
								"投保规则检查不通过:<br/>");
						for (UWBOMRuleInfoDto ruleInfo : uwVerifyResult
								.getRuleInfos()) {
							PosAcceptDetailDTO pad = new PosAcceptDetailDTO();
							pad.setPosNo(posNo);
							pad.setPosObject("9");
							pad.setItemNo("000");
							pad.setProcessType("3");
							pad.setObjectValue("#"); // 投保规则没有特殊规则，设置为"#"
							pad.setNewValue(ruleInfo.getDescription());
							pad.setOldValue(ruleInfo.getRuleID());
							acceptDAO
									.insertOrReplaceAcceptDetailForRuleMsg(pad);
							resultDescSB.append(ruleInfo.getDescription()
									+ "<br/>");
						}
						resultDesc = resultDescSB.toString();
					} else {
						resultDesc += "投保规则";
					}
				}
				if (ruleCheckOK) {
					resultDesc = "投保规则检查通过";
					// 投保规则检查通过，继续进行处理规则检查
					POSBOMPolicyDto posBom = (POSBOMPolicyDto) posJrulesCheck
							.createBOMForProcessRuleCheck(posNo);
					
					Log.info(posNo+"startProcessRules starting...");
					//增加bom属性信息到临时表中以便快速核实规则
					try{
						String bomContent =posBom.toString();
			    
						posRulesDAO.insertRulesBom(posNo,"处理规则",bomContent);
					}catch(Exception e){
						logger.info("将调用规则引擎的的入参出参toString后放到数据库   出现异常");
						e.printStackTrace();
					}
					
					
					POSVerifyResultDto posVerifyResult = posJrulesCheck
							.excuteRules(posBom);
					Log.info(posNo+"endProcessRules end...");
					if (posVerifyResult != null
							&& posVerifyResult.getRuleInfos() != null
							&& !posVerifyResult.getRuleInfos().isEmpty()) {
						ruleCheckOK = false;
						result = "N";
						StringBuilder resultDescSB = new StringBuilder(
								"处理规则检查不通过:<br/>");
						for (POSBOMRuleInfoDto ruleInfo : posVerifyResult
								.getRuleInfos()) {
							PosAcceptDetailDTO pad = new PosAcceptDetailDTO();
							pad.setPosNo(posNo);
							pad.setPosObject("9");
							pad.setItemNo("000");
							pad.setProcessType("3");
							pad.setObjectValue(ruleInfo.getSpecialRule());
							pad.setNewValue(ruleInfo.getDescription());
							pad.setOldValue(ruleInfo.getRuleName());
							// 非特殊规则ObjectValue为空，设置默认值为"@"
							if (StringUtils.isBlank(pad.getObjectValue())) {
								pad.setObjectValue("@");
							}
							acceptDAO
									.insertOrReplaceAcceptDetailForRuleMsg(pad);
							resultDescSB.append(ruleInfo.getDescription()
									+ "<br/>");
						}
						resultDesc = resultDescSB.toString();
					} else {
						resultDesc += "投保规则";
					}
				}
				if (ruleCheckOK) {
					result = "Y";
					resultDesc += "检查通过";
				}
			}
		} else if ("06".equals(procType)) {
			String serviceItems = commonQueryDAO
					.queryServiceItemsByPosNo(posNo);
			// String policy_channel_type = commonQueryDAO
			// .getChannelTypeByPolicyNo(posInfo.getPolicyNo());
			// 组合险种的保单直接送人工核保
			boolean combinationCode = posRulesDAO
					.isPolicyCombinationCode(posInfo.getPolicyNo());
			if (combinationCode
					&& ArrayUtils.contains(new String[] { "22", "15", "3", "4",
							"42", "12", "16" }, serviceItems)) {

				String acceptBranchCode = commonQueryDAO
						.getAcceptBranchCodeByPosNo(posNo);

				Map<String, Object> retMap = posRulesDAO.applyGimUndwrt(
						posInfo.getPolicyNo(), posNo, acceptBranchCode);

				String flag = (String) retMap.get("p_sign");
				String msg = (String) retMap.get("p_message");
				if (!"Y".equalsIgnoreCase(flag)) {

					throw new RuntimeException("送人工核保失败：" + msg);
				} else {
					result = "N";
					resultDesc = "送人工核保";
				}

			} else {
				// 增加官网和电话中心渠道受理的保全不需要送核保
				if (UNDWRT_SERVICE_ITEMS.contains(serviceItems)
						&& (!"7".equals(channelCode)&&!"4".equals(channelCode)) && !omitRuleCheck
						&& isNeedSendToUnderwriting(posNo)) {
					// 送核系统用户，当取不到登陆用户时，会认为是后台JOB，默认取这个用户
					String userId = "v_system_uw@virtual.com";
					if (PosUtils.getLoginUserInfo() != null
				  			&& StringUtils.isNotBlank(PosUtils
									.getLoginUserInfo().getLoginUserID())) {
						userId = PosUtils.getLoginUserInfo().getLoginUserID();
					}
					Map<String, Object> retMap = posUnderwritingService
							.ruleCheckAndSendToManualWork(posNo, userId);
					String flag = (String) retMap.get("FLAG");
					String msg = (String) retMap.get("MESSAGE");
					String taskCodeApp = (String) retMap.get("TASKCODE_APP");
					String taskCodeIns = (String) retMap.get("TASKCODE_INS");
					String rulesResult = (String) retMap.get("RULESRESULT");
					String underwriteNo = (String) retMap.get("UNDERWRITERNO");
					if ("3".equals(rulesResult)) {
						throw new RuntimeException("调用核保接口异常：" + msg);
					} else if ("N".equals(rulesResult)) {
						if ("Y".equals(flag)) {
							// 送人工核保成功
							logger.info(posNo + " -> 送人工核保成功");
							result = "N";
							resultDesc = "送人工核保成功，投保人任务号：" + taskCodeApp
									+ "，被保人任务号：" + taskCodeIns + "，核保员："
									+ underwriteNo;
						} else {
							throw new RuntimeException("送人工核保失败：" + msg);
						}
					} else if ("Y".equals(rulesResult)) {
						logger.info(posNo + " -> 自核通过");
						result = "Y";
						resultDesc = "自核通过";
					}
				} else {
					// 非核保类保全项目，或者指定了忽略规则检查，则直接通过
					result = "Y";
					resultDesc = "通过未送核";
				}
			}
		}
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("procType", procType);
		paraMap.put("posNo", posNo);
		paraMap.put("result", result);
		paraMap.put("resultDesc", resultDesc);
		paraMap.put("ruleInfo", resultDesc);
		acceptDAO.workflowControl(paraMap);

		logger.info("workflowControl:" + paraMap);

		String flag = (String) paraMap.get("flag");
		String message = (String) paraMap.get("message");

		if ("00".equals(flag)) {
			throw new RuntimeException(message);

		} else if ("A05".equals(flag)) {
			// A05 调用流转接口的p_proc_type='04'进行预处理
			return this.workflowControl("04", posNo, omitRuleCheck);

		} else if ("A10".equals(flag)) {
			// A10 待投保、核保规则检查 调用流转接口的p_proc_type='06'投保、核保规则检查结果导入
			return this.workflowControl("06", posNo, omitRuleCheck);

		} else if ("A20".equals(flag)) {
			// A20 待收付费处理 则调用流转接口p_proc_type='07'收付费处理
			// 银保通，银行自助终端，招行网银渠道受理的退保，契撤，满期给付的停在A20
			if ("12".equals(channelCode)||"18".equals(channelCode)||"28".equals(channelCode)) {
				if (!("1".equals(posInfo.getServiceItems())
						|| "2".equals(posInfo.getServiceItems()) || "10"
						.equals(posInfo.getServiceItems()))) {
					return this.workflowControl("07", posNo, omitRuleCheck);
				}
			} else {
				return this.workflowControl("07", posNo, omitRuleCheck);
			}

		} else if ("A16".equals(flag)) {
			// A16 待生效 调用流转接口p_proc_type='09'生效处理(银保通的后台判断推动)
			return this.workflowControl("09", posNo, omitRuleCheck);
		}

		return paraMap;
	}

	/**
	 * 判断保全项目是否需要送核
	 * 
	 * @param posNo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean isNeedSendToUnderwriting(String posNo) {
		Map<String, Object> retMap = posRulesDAO
				.getToundwrtProductDatail(posNo);

		Map<String, Object> clientInfoApp = (Map<String, Object>) retMap
				.get("clientInfoApp");
		Map<String, Object> clientInfoIns = (Map<String, Object>) retMap
				.get("clientInfoIns");

		if (clientInfoApp != null && !clientInfoApp.isEmpty()) {
			List<Map<String, Object>> undwrtInfo = (List<Map<String, Object>>) clientInfoApp
					.get("UNWRTINFO");
			if (undwrtInfo != null && !undwrtInfo.isEmpty()) {
				for (Map<String, Object> item : undwrtInfo) {
					String prodSeq = (String) item.get("PRODSEQ");
					if (StringUtils.isNotBlank(prodSeq)) {
						return true;
					}
				}
			}
		}

		if (clientInfoIns != null && !clientInfoIns.isEmpty()) {
			List<Map<String, Object>> undwrtInfo = (List<Map<String, Object>>) clientInfoIns
					.get("UNWRTINFO");
			if (undwrtInfo != null && !undwrtInfo.isEmpty()) {
				for (Map<String, Object> item : undwrtInfo) {
					String prodSeq = (String) item.get("PRODSEQ");
					if (StringUtils.isNotBlank(prodSeq)) {
						return true;
					}
				}
			}
		}

		logger.info(posNo + " -> 不需要送核");

		return false;
	}

	/**
	 * workflowControl重载
	 */
	public Map<String, Object> workflowControl(String procType, String posNo,
			boolean omitRuleCheck) {
		return workflowControl(procType, posNo, null, null, omitRuleCheck);
	}

	/**
	 * 批次受理接口.
	 * 
	 * @param batch
	 * @return
	 */
	public PosApplyBatchDTO batchAccept(PosApplyBatchDTO batch) {
		PosApplyBatchDTO pab = null;
		try {
			/* 为保证本处理逻辑不对原始值进行 更改，先对传入参数进行clone */
			pab = (PosApplyBatchDTO) PosUtils.deepCopy(batch);
			logger.info("batchAccept:" + PosUtils.describBeanAsJSON(pab));
			
			// 实施属性值依赖关系
			applyDependency(pab);

			LoginUserInfoDTO userInfo = null;
			logger.info("---***录入的受理人ID是***---"+pab.getAcceptor());
			if (StringUtils.isBlank(pab.getAcceptor())) {
				userInfo = PosUtils.getLoginUserInfo();
			}
			logger.info("---***最终获取到的受理人信息***---"+userInfo);
			String clientNo = pab.getClientNo(); // 当前受理客户

			// 按照受理顺序再排序一次
			Collections.sort(pab.getPosInfoList(),
					new Comparator<PosInfoDTO>() {
						@Override
						public int compare(PosInfoDTO o1, PosInfoDTO o2) {
							int seq1 = o1.getAcceptSeq();
							int seq2 = o2.getAcceptSeq();
							if (seq1 > seq2) {
								return 1;
							} else if (seq1 < seq2) {
								return -1;
							} else {
								return 0;
							}
						}
					});

			/* 1. 写保全批次表 */
			pab.setBatchStatus("0");
			if (userInfo != null && StringUtils.isBlank(pab.getAcceptor())) {
				pab.setAcceptor(userInfo.getLoginUserID());
				pab.setCounterNo(userInfo.getLoginUserCounterNo());
			}
			
			acceptDAO.insertPosApplyBatch(pab);

			logger.info("insertPosApplyBatch:" + PosUtils.deepCopy(pab));

			String posBatchNo = pab.getPosBatchNo(); // 批次号
			// String suspendItems = ""; // 暂停原因

			/* 2. 写申请书表 */
			List<PosApplyFilesDTO> applyFiles = pab.getApplyFiles();
			if (applyFiles != null && !applyFiles.isEmpty()) {
				/*
				 * 对申请书进行排序: 由于存在关联条码，因此必须保证无关联条码的申请书先insert，否则会报外键约束异常
				 */
				List<PosApplyFilesDTO> sortedApplyFiles = new ArrayList<PosApplyFilesDTO>();

				// 首先将无关联条码的放在最前面
				for (Iterator<PosApplyFilesDTO> it = applyFiles.iterator(); it
						.hasNext();) {
					PosApplyFilesDTO p1 = it.next();
					if (StringUtils.isBlank(p1.getRelateBarcode())) {
						sortedApplyFiles.add(p1);
						it.remove();
					}
				}
				// 在此基础上顺藤摸瓜，加入与现有条码有关联的条码
				while (applyFiles.size() > 0) {
					boolean found = false;
					for (Iterator<PosApplyFilesDTO> it = applyFiles.iterator(); it
							.hasNext();) {
						PosApplyFilesDTO p2 = it.next();
						for (PosApplyFilesDTO p3 : sortedApplyFiles) {
							if (p3.getBarcodeNo().equals(p2.getRelateBarcode())) {
								sortedApplyFiles.add(p2);
								it.remove();
								found = true;
								break;
							}
						}
					}
					// 每轮都应该至少找到一个，否则便存在无效的关联条码
					if (!found) {
						StringBuffer sb = new StringBuffer("存在无效的关联条码:");
						for (PosApplyFilesDTO p4 : applyFiles) {
							sb.append(" ").append(p4.getRelateBarcode());
						}
						throw new RuntimeException(sb.toString());
					}
				}
				applyFiles.addAll(sortedApplyFiles);

				// 排序结束，再依次处理每个申请书
				for (PosApplyFilesDTO applyFile : applyFiles) {
					/* 写申请书表 */
					applyFile.setPosBatchNo(posBatchNo);
					applyFile.setForeignExchangeType("1");// 钞 暂不支持外币
					if (StringUtils.isBlank(applyFile.getSignAccordFlag())) {
						applyFile.setSignAccordFlag("N");
					}
					List<PosApplyFileMaterialsDTO> materials = applyFile
							.getApplyFileMaterialList();
					if (materials != null) {
						applyFile.setFilePages((long) materials.size());
					} else {
						applyFile.setFilePages(0L);
					}
					applyFile.setApplyDate(DateUtil.stringToDate(DateUtil
							.dateToString(applyFile.getApplyDate(),
									"yyyy-MM-dd"), "yyyy-MM-dd"));

					acceptDAO.insertPosApplyFiles(applyFile);

					/* 写申请材料表 */
					String barcodeNo = applyFile.getBarcodeNo();
					if (materials != null && !materials.isEmpty()) {
						for (PosApplyFileMaterialsDTO material : materials) {
							material.setBarcodeNo(barcodeNo);

							acceptDAO.insertPosApplyFileMaterials(material);
						}
					}
				}

				/* 写保全信息表 */
				for (PosInfoDTO posInfo : pab.getPosInfoList()) {
					/* edit by gaojiaming start */
					int applyFileCount = 0;
					/* edit by gaojiaming end */
					for (PosApplyFilesDTO applyFile : pab.getApplyFiles()) {
						if (applyFile.getBarcodeNo().equals(
								posInfo.getBarcodeNo())) {
							// if (StringUtils.isNotBlank(applyFile
							// .getTransferAccountno())
							// && StringUtils.isBlank(applyFile
							// .getAccountNoType())) {
							//
							// throw new RuntimeException(
							// applyFile.getPolicyNo()
							// + " 银行账号类型为空，请重新触发选择!");
							// }

							posInfo.setAcceptor(pab.getAcceptor());
							boolean acceptRuleCheckOK = posInfo
									.getVerifyResult() == null
									|| posInfo.getVerifyResult().getRuleInfos() == null
									|| posInfo.getVerifyResult().getRuleInfos()
											.size() == 0;
							if (acceptRuleCheckOK) {
								posInfo.setAcceptStatusCode("A01"); // 受理状态：批次受理
							} else {
								posInfo.setAcceptStatusCode("C05"); // 受理状态：受理规则检查不通过
							}
							posInfo.setAccountNo(applyFile
									.getTransferAccountno());
							posInfo.setAccountNoType(applyFile
									.getAccountNoType());
							posInfo.setBankCode(applyFile.getBankCode());
							posInfo.setClientNo(clientNo);
							posInfo.setPaymentType(applyFile.getPaymentType());
							posInfo.setForeignExchangeType(applyFile
									.getForeignExchangeType());
							posInfo.setIdType(applyFile
									.getTransferAccountOwnerIdType());
							posInfo.setPremName(applyFile
									.getTransferAccountOwner());
							posInfo.setPremIdno(applyFile
									.getTransferAccountOwnerIdNo());
							posInfo.setAcceptDate(commonQueryDAO
									.getSystemDate());
							posInfo.setPolicyNo(applyFile.getPolicyNo());
							acceptDAO.insertPosInfo(posInfo);

							if (acceptRuleCheckOK) {
								// 受理规则检查通过的，对保单置暂停
								acceptDAO.doPolicySuspend(
										posInfo.getPolicyNo(), "5",
										posInfo.getPosNo(),
										posInfo.getServiceItems(),
										commonQueryDAO.getSystemDate());

							} else {
								// 受理规则检查不通过的，由于受理已经是结束状态，不用对保单置暂停，只需写入规则检查不通过的明细信息
								for (POSBOMRuleInfoDto ruleInfo : posInfo
										.getVerifyResult().getRuleInfos()) {
									PosAcceptDetailDTO pad = new PosAcceptDetailDTO();
									pad.setPosNo(posInfo.getPosNo());
									pad.setPosObject("9");
									pad.setItemNo("000");
									pad.setProcessType("3");
									pad.setObjectValue(ruleInfo
											.getSpecialRule());
									pad.setNewValue(ruleInfo.getDescription());
									pad.setOldValue(ruleInfo.getRuleName());
									// 非特殊规则ObjectValue为空，设置默认值为"@"
									if (StringUtils.isBlank(pad
											.getObjectValue())) {
										pad.setObjectValue("@");
									}
									acceptDAO.insertPosAcceptDetail(pad);
								}

								// 写入受理变迁记录 A01 -> C05
								PosStatusChangeHistoryDTO statusChange = new PosStatusChangeHistoryDTO();
								statusChange.setPosNo(posInfo.getPosNo());
								statusChange.setOldStatusCode("A01");
								statusChange.setNewStatusCode("C05");
								statusChange.setChangeUser(posInfo
										.getAcceptor());
								statusChange.setChangeDate(commonQueryDAO
										.getSystemDate());
								acceptDAO
										.insertPosStatusChangeHistory(statusChange);
							}

							/* 免填单写入保全明细表 edit by gaojiaming start */
							if (pab != null
									&& pab.getFillFormList() != null
									&& pab.getFillFormList().size() > applyFileCount) {
								String fillForm = pab.getFillFormList()
										.get(applyFileCount).getFillForm();
								if ("Y".equals(fillForm)) {
									PosAcceptDetailDTO padForFillForm = new PosAcceptDetailDTO();
									padForFillForm.setPosNo(posInfo.getPosNo());
									padForFillForm.setPosObject("5");
									padForFillForm.setItemNo("022");
									padForFillForm.setProcessType("3");
									padForFillForm.setObjectValue(posInfo
											.getPolicyNo());
									padForFillForm.setNewValue("Y");
									acceptDAO
											.insertPosAcceptDetail(padForFillForm);
								}
							}
							/* edit by gaojiaming end */
							
							/* 代审信息写入受理明细 edit by gaojiaming start */
							if ("12".equals(pab.getApplyTypeCode())) {
								// 代审类型
		/*						PosAcceptDetailDTO padForExamType = new PosAcceptDetailDTO();
								padForExamType.setPosNo(posInfo.getPosNo());
								padForExamType.setPosObject("5");
								padForExamType.setItemNo("028");
								padForExamType.setProcessType("3");
								padForExamType.setObjectValue(posInfo.getPolicyNo());
								padForExamType.setNewValue(pab.getExamType());
								acceptDAO.insertPosAcceptDetail(padForExamType);*/
								// 代审人
								if (("02".equals(pab.getExamType()) && !StringUtils.isBlank(pab.getApproveEmpNo()))
										|| ("01".equals(pab.getExamType()) && !StringUtils.isBlank(pab
												.getApproveDeptNo()))) {
									PosAcceptDetailDTO padForApprove = new PosAcceptDetailDTO();
									padForApprove.setPosNo(posInfo.getPosNo());
									padForApprove.setPosObject("5");
									padForApprove.setItemNo("026");
									padForApprove.setProcessType("3");
									padForApprove.setObjectValue(posInfo.getPolicyNo());
									if ("20".equals(pab.getAcceptChannelCode())) {
										padForApprove.setNewValue(pab.getApproveEmpNo());
									}
									if ("3".equals(pab.getAcceptChannelCode())) {
										padForApprove.setNewValue(pab.getApproveDeptNo());
									}
									acceptDAO.insertPosAcceptDetail(padForApprove);
								}
							}
							/* 代审信息写入受理明细 edit by gaojiaming end */
							
							/* 可疑交易写入受理明细表 edit by wangmingshun start */
							//勾选可疑交易才进行处理 
							if(pab.getIsDoubtTransaction()) {
								PosAcceptDetailDTO padForDoubt = new PosAcceptDetailDTO();
								padForDoubt.setPosNo(posInfo.getPosNo());
								padForDoubt.setPosObject("5");
								padForDoubt.setItemNo("032");
								padForDoubt.setProcessType("3");
								padForDoubt.setObjectValue(posInfo.getPolicyNo());
								padForDoubt.setNewValue(pab.getDoubtTransactionRemark());
								//可疑交易备注
								acceptDAO.insertPosAcceptDetail(padForDoubt);
								//可疑交易code
								padForDoubt.setItemNo("031");
								padForDoubt.setNewValue("1321");
								acceptDAO.insertPosAcceptDetail(padForDoubt);
							}
							/* 可疑交易写入受理明细表 edit by wangmingshun end */
							
							break;
						}
						/* edit by gaojiaming start */
						applyFileCount++;
						/* edit by gaojiaming end */
					}
				}
			}
		} catch (RuntimeException e) {
			logger.error(e.getMessage() + PosUtils.describBeanAsJSON(pab), e);
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage() + PosUtils.describBeanAsJSON(pab), e);
			throw new RuntimeException(e);
		}
		return pab;
	}

	public void applyDependency(PosApplyBatchDTO pb) {
		if (pb != null) {
			// 不为代办时，清空代办人信息
			String applyType = pb.getApplyTypeCode();
			if (!"11".equals(applyType) ) {
				pb.setRepresentor(null);
				pb.setRepresentIdno(null);
				pb.setIdType(null);
			}

			List<PosApplyFilesDTO> applyFiles = pb.getApplyFiles();
			if (applyFiles != null && applyFiles.size() > 0) {
				for (PosApplyFilesDTO paf : applyFiles) {
					// 批单送达方式不为邮寄，清空批单送达地址信息
					String approvalServiceType = paf.getApprovalServiceType();
					if (!"2".equals(approvalServiceType)) {
						paf.setAddress(null);
						paf.setZip(null);
					}
					// 收付方式不为银行转账时，清空银行信息
					String paymentType = paf.getPaymentType();
					if (!"2".equals(paymentType)) {
						paf.setBankCode(null);
						paf.setTransferAccountno(null);
					}

					String docType = PosUtils.getDocTypeFromBarcodeNo(paf
							.getBarcodeNo());
					if ("1211".equals(docType) || "1234".equals(docType)
							|| "1212".equals(docType)
							&& "22".equals(paf.getServiceItems())) {
						// 非补退费类申请书，或者核保类仅选择了受益人变更时，清空银行信息及收款人信息
						paf.setBankCode(null);
						paf.setTransferAccountno(null);
						paf.setTransferAccountOwner(null);
						paf.setTransferAccountOwnerIdType(null);
						paf.setTransferAccountOwnerIdNo(null);
						paf.setPaymentType(null);
					}
				}
			}
		}
	}

	/**
	 * 查询批次中第一 个状态为A01的受理
	 * 
	 * @param posBatchNo
	 *            批次号
	 * @return
	 */
	public String getNextPosNoInBatch(String posBatchNo) {
		return acceptDAO.getNextPosNoInBatch(posBatchNo);
	}

	/**
	 * 生成保全项目列表
	 * 
	 * @param applyInfo
	 * @param policyServiceItems
	 * @param omitRuleCheck
	 *            忽略规则检查
	 * @return
	 */
	public void generatePosInfoList(PosApplyBatchDTO applyInfo,
			boolean omitRuleCheck) {
		List<PosInfoDTO> posInfoList = applyInfo.getPosInfoList();
		posInfoList.clear();

		// 客户信息缓存
		Map<String, ClientInformationDTO> clientInfoCache = new HashMap<String, ClientInformationDTO>();

		// 汇总本次申请的保全项目
		Set<String> currentServiceItemsSet = new HashSet<String>();
		for (PosApplyFilesDTO applyFile : applyInfo.getApplyFiles()) {
			String serviceItemsStr = applyFile.getServiceItems();
			currentServiceItemsSet.addAll(Arrays.asList(serviceItemsStr
					.split(",")));
		}
		// 干掉可能的空字符串
		CollectionUtils.filter(currentServiceItemsSet, new Predicate() {
			@Override
			public boolean evaluate(Object obj) {
				return StringUtils.isNotBlank((String) obj);
			}
		});
		// 本次申请的保全项目
		ServiceItemDomainDto[] currentServiceItemsList = new ServiceItemDomainDto[currentServiceItemsSet
				.size()];
		int idx = 0;
		for (String serviceItems : currentServiceItemsSet) {
			currentServiceItemsList[idx++] = new ServiceItemDomainDto(
					serviceItems);
		}

//		PolicyDTO policyDTO = null;
		for (PosApplyFilesDTO applyFile : applyInfo.getApplyFiles()) {
			String policyNo = applyFile.getPolicyNo();

//			policyDTO = commonQueryDAO.queryPolicyInfoByPolicyNo(policyNo);
			String serviceItemsStr = applyFile.getServiceItems();
			Iterator<String> it = Arrays.asList(serviceItemsStr.split(","))
					.iterator();
			while (it.hasNext()) {
				String serviceItems = it.next();
				if (StringUtils.isBlank(serviceItems))
					continue;
				// 招财宝平台操作过变现不可操作退保、犹豫期退保、部分领取、追加保费、生存保险金领取、保单贷款、年龄性别错误更正dmp-5025
//				if ("08".equals(policyDTO.getChannelType())
//						&& "F".equals(policyDTO.getBusinessSource())
//						&& ArrayUtils.contains(new String[] { "1", "2", "35",
//								"37", "10", "8", "12" }, serviceItems)) {
//					Map<String, Object> map = new HashMap<String, Object>();
//					String bkAcctTime = DateUtils.formatDate(new Date(),
//							"yyyy-MM-dd hh:mm:ss");
//					map.put("TRAN_CODE", "SurrAllRealVerify");
//					map.put("PARTNER_ID", "web_alipay");
//					map.put("BK_ACCT_TIME", bkAcctTime);
//					map.put("POLICYNO", policyNo);
//					map.put("OPERATION_TYPE", "BEFOREHAND_REDEEM"); // BEFOREHAND_REDEEM:提前退保
//																	// CANCEL_REDEEM:犹豫期退保
//					Map<String, Object> resultMap = null;
//					try {
//
//						BiabPosService biabPosService = PlatformContext
//								.getEsbContext().getEsbService(
//										BiabPosService.class);
//						resultMap = biabPosService.handleBiagESBRequestMap(map);
//
//					} catch (Exception e) {
//						logger.error("发送支付宝验证操作异常！", e);
//						throw new RuntimeException("发送支付宝验证操作异常！：" + policyNo);
//
//					}
//					String resultCode = (String) resultMap.get("SL_RSLT_CODE");
//					logger.info(new Object[] { policyNo, serviceItems,
//							resultCode, (String) resultMap.get("SL_RSLT_MESG") });
//					if (!"999999".equals(resultCode)) {
//						if ("0101".equals(resultCode)) {
//							// TODO 包装错误信息
//							throw new RuntimeException("对不起，您的资产在支付宝不存在！："
//									+ policyNo);
//
//						} else if ("0102".equals(resultCode)) {
//							throw new RuntimeException("对不起，您的资产在支付宝已冻结！："
//									+ policyNo);
//						}
//
//						else if ("0103".equals(resultCode)) {
//							throw new RuntimeException("对不起，您的资产已到期！："
//									+ policyNo);
//
//						} else if ("0104".equals(resultCode)) {
//							throw new RuntimeException("对不起，您的资产已在支付宝申请退保！："
//									+ policyNo);
//
//						} else if ("9998".equals(resultCode)) {
//							throw new RuntimeException("退保申请正在处理中。：" + policyNo);
//
//						} else if ("9999".equals(resultCode)) {
//							throw new RuntimeException("其他错误！：" + policyNo);
//
//						} else if ("800001".equals(resultCode)) {
//							throw new RuntimeException("支付宝没有数据返回！：" + policyNo);
//
//						} else {
//							throw new RuntimeException("发送支付宝验证操作失败！"
//									+ policyNo);
//
//						}
//
//					}
//				}

				PosInfoDTO posInfo = new PosInfoDTO();
				posInfo.setPolicyNo(policyNo);
				posInfo.setBarcodeNo(applyFile.getBarcodeNo());
				posInfo.setRemark(applyFile.getRemark());
				posInfo.setServiceItems(serviceItems);
				posInfo.setServiceItemsDesc(PosUtils
						.getDescByCodeFromCodeTable(
								CodeTableNames.PRODUCT_SERVICE_ITEMS,
								serviceItems));

				// 查投保人信息
				String applicant = commonQueryDAO
						.getApplicantByPolicyNo(policyNo);
				if (StringUtils.isNotBlank(applicant)
						&& !clientInfoCache.containsKey(applicant)) {
					List<ClientInformationDTO> clientInfoList = clientInfoDAO
							.selClientinfoForClientno(applicant);
					if (clientInfoList.size() == 0)
						throw new RuntimeException("无效的客户号：" + applicant);
					clientInfoCache.put(applicant, clientInfoList.get(0));
				}
				posInfo.setApplicantInfo(clientInfoCache.get(applicant));

				// 查被保人信息
				String insured = commonQueryDAO
						.getInsuredOfPrimaryPlanByPolicyNo(policyNo);
				if (StringUtils.isNotBlank(insured)
						&& !clientInfoCache.containsKey(insured)) {
					List<ClientInformationDTO> clientInfoList = clientInfoDAO
							.selClientinfoForClientno(insured);
					if (clientInfoList.size() == 0)
						throw new RuntimeException("无效的客户号：" + insured);
					clientInfoCache.put(insured, clientInfoList.get(0));
				}
				posInfo.setInsuredInfo(clientInfoCache.get(insured));

				if (omitRuleCheck) {
					posInfo.setVerifyResult(null);
				} else {
					POSBOMPolicyDto bom = (POSBOMPolicyDto) posJrulesCheck
							.createBOMForAcceptRuleCheck(posInfo.getPolicyNo(),
									applyInfo.getClientNo(),
									posInfo.getServiceItems(),
									applyFile.getApplyDate(),
									commonQueryDAO.getSystemDate(),
									applyInfo.getAcceptor(),
									applyInfo.getApplyTypeCode(),
									applyInfo.getAcceptChannelCode(),
									applyInfo.getIdnoValidityDate(),
									currentServiceItemsList);
					//增加bom属性信息到临时表中以便快速核实规则
					try{
						String bomContent = bom.toString();
			    
						posRulesDAO.insertRulesBom(posInfo.getPolicyNo(),"受理规则",bomContent);
					}catch(Exception e){
						logger.info("将调用受理规则规则引擎的的入参出参toString后放到数据库   出现异常");
						e.printStackTrace();
					}
					POSVerifyResultDto verifyResult = posJrulesCheck
							.excuteRules(bom);
					posInfo.setVerifyResult(verifyResult);
				}

				// 查询受理提醒信息
				String acceptCaution = commonQueryDAO.getRiskRemind("1",
						serviceItems);
				//根据保单号查询是否是电销(0不代表电销，1代表是电销)
				int count = this.getPolicyNoCount(policyNo);
				//需求DMP-8133 所有电销保单增加受理提醒信息内容
				if(count==1){
					acceptCaution = "电销渠道保单除犹豫期退保、退保、复效保全项目，其他收付费类保全项目均不可以用信用卡进行收付费；"+acceptCaution;
				}
				if (StringUtils.isNotBlank(acceptCaution)) {
					posInfo.setHasAcceptCaution(true);
					posInfo.setAcceptCautionDesc(acceptCaution);
				} else {
					posInfo.setHasAcceptCaution(false);
					posInfo.setAcceptCautionDesc(null);
				}

				// 查询风险提示信息
				String riskCaution = commonQueryDAO.getRiskRemind("2",
						serviceItems);
				if (StringUtils.isNotBlank(riskCaution)) {
					posInfo.setHasRiskCaution(true);
					posInfo.setRiskCautionDesc(riskCaution);
				} else {
					posInfo.setHasRiskCaution(false);
					posInfo.setRiskCautionDesc(null);
				}

				posInfoList.add(posInfo);
			}
		}
	}

	/**
	 * 生成捆绑顺序
	 * 
	 * @param applyInfo
	 */
	public void generateBindingOrder(final PosApplyBatchDTO applyInfo) {
		List<PosInfoDTO> posInfoList = applyInfo.getPosInfoList();
		// 读取数据库绑定顺序配置
		List<PosServiceItemSequenceSetDTO> settings = commonQueryDAO
				.queryPosServiceItemSequenceSet();

		// 先将所有保全项目保存到一个Map中，key为保全项目，value为排在它之后的保全项目的集合
		Map<String, Set<String>> subElementMapping = new HashMap<String, Set<String>>();
		final Map<String, Integer> elementValueMapping = new HashMap<String, Integer>();
		for (PosServiceItemSequenceSetDTO setting : settings) {
			String proServiceItems = setting.getProServiceItems();// 优先的
			String serviceItems = setting.getServiceItems();
			Set<String> subElementProSet = null;
			if (subElementMapping.containsKey(proServiceItems)) {
				subElementProSet = subElementMapping.get(proServiceItems);
			} else {
				subElementProSet = new HashSet<String>();
				subElementMapping.put(proServiceItems, subElementProSet);
				elementValueMapping.put(proServiceItems, 0);
			}
			if (!subElementMapping.containsKey(serviceItems)) {
				subElementMapping.put(serviceItems, new HashSet<String>());
				elementValueMapping.put(serviceItems, 0);
			}
			subElementProSet.add(serviceItems);
		}
		int loopLimit = 1000;
		// 通过迭代得出每个保全项目存在下属的个数
		while (!subElementMapping.isEmpty() && --loopLimit > 0) {
			Set<String> keySet = new HashSet<String>();
			keySet.addAll(subElementMapping.keySet());
			for (String serviceItems : keySet) {
				Set<String> subElementSet = subElementMapping.get(serviceItems);
				if (subElementSet.isEmpty()) {
					// 迭代的终点为某集合下为元素为空，说明他是一个底层元素或迭代完成，将它现有的值加1作为该保全项目的值
					subElementMapping.remove(serviceItems);
					elementValueMapping.put(serviceItems,
							elementValueMapping.get(serviceItems) + 1);
					// 同时检查所有子元素中包含该元素的保全项目，干掉，并加上该元素的保全项目值
					Set<String> keySetInternal = new HashSet<String>();
					keySetInternal.addAll(subElementMapping.keySet());
					for (String serviceItemsInternal : keySetInternal) {
						Set<String> subElementSetInternal = subElementMapping
								.get(serviceItemsInternal);
						if (subElementSetInternal.contains(serviceItems)) {
							subElementSetInternal.remove(serviceItems);
							elementValueMapping.put(
									serviceItemsInternal,
									elementValueMapping
											.get(serviceItemsInternal)
											+ elementValueMapping
													.get(serviceItems));
						}
					}
				}
			}
		}
		if (loopLimit == 0) {
			throw new RuntimeException("保全项目排序设置存在循环!");
		}
		final List<String> serviceItemsOrder = new ArrayList<String>();
		serviceItemsOrder.addAll(elementValueMapping.keySet());
		// 再对List进行排序，以取得每个保全项目的排序序号值
		Collections.sort(serviceItemsOrder, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				int v1 = elementValueMapping.get(o1);
				int v2 = elementValueMapping.get(o2);
				if (v1 == v2) {
					return 0;
				}
				if (v1 > v2) {
					return -1;
				} else {
					return 1;
				}
			}
		});

		// 将受理规则检查不通过的放到最前面
		List<PosInfoDTO> newPosInfoList = new ArrayList<PosInfoDTO>();
		for (Iterator<PosInfoDTO> it = posInfoList.iterator(); it.hasNext();) {
			PosInfoDTO posInfo = it.next();
			if (posInfo.getVerifyResult() != null
					&& posInfo.getVerifyResult().getRuleInfos() != null
					&& posInfo.getVerifyResult().getRuleInfos().size() > 0) {
				newPosInfoList.add(posInfo);
				it.remove();
			}
		}

		// 剩下的先按照申请日期排一次序
		final Map<String, Date> barcodeApplyDateMapping = new HashMap<String, Date>();
		Collections.sort(posInfoList, new Comparator<PosInfoDTO>() {
			@Override
			public int compare(PosInfoDTO o1, PosInfoDTO o2) {
				String barcode1 = o1.getBarcodeNo();
				String barcode2 = o2.getBarcodeNo();
				Date date1 = null;
				Date date2 = null;
				if (barcodeApplyDateMapping.containsKey(barcode1)) {
					date1 = barcodeApplyDateMapping.get(barcode1);
				} else {
					date1 = findApplyDateByBarcodeNo(applyInfo, barcode1);
					barcodeApplyDateMapping.put(barcode1, date1);
				}
				if (barcodeApplyDateMapping.containsKey(barcode2)) {
					date2 = barcodeApplyDateMapping.get(barcode2);
				} else {
					date2 = findApplyDateByBarcodeNo(applyInfo, barcode2);
					barcodeApplyDateMapping.put(barcode1, date2);
				}
				return date1.compareTo(date2);
			}

		});
		// 然后在排序数组中查找对应的保全项目，取得序号值作为排序的权值进行排序，取不到的默认为-1，排在最前面
		Collections.sort(posInfoList, new Comparator<PosInfoDTO>() {
			@Override
			public int compare(PosInfoDTO o1, PosInfoDTO o2) {
				String serviceItems1 = o1.getServiceItems();
				String serviceItems2 = o2.getServiceItems();
				int idxServiceItems1 = serviceItemsOrder.indexOf(serviceItems1);
				int idxServiceItems2 = serviceItemsOrder.indexOf(serviceItems2);
				if (idxServiceItems1 == idxServiceItems2) {
					return 0;
				} else {
					return (idxServiceItems1 - idxServiceItems2)
							/ Math.abs(idxServiceItems1 - idxServiceItems2);
				}
			}
		});

		newPosInfoList.addAll(posInfoList);
		posInfoList.clear();
		posInfoList.addAll(newPosInfoList);

		// 将排序后的序号值作为绑定顺序
		for (int i = 0; i < posInfoList.size(); i++) {
			PosInfoDTO posInfo = posInfoList.get(i);
			posInfo.setAcceptSeq(i + 1);
		}
	}

	private Date findApplyDateByBarcodeNo(PosApplyBatchDTO batch,
			String barcodeNo) {
		for (PosApplyFilesDTO file : batch.getApplyFiles()) {
			if (barcodeNo.equals(file.getBarcodeNo())) {
				return file.getApplyDate();
			}
		}
		return null;
	}

	/**
	 * 对保全项目录入域进行校验
	 * 
	 * @param serviceItemsInputDTO
	 * @param err
	 */
	public void validateServiceItemsInputDTO(
			ServiceItemsInputDTO serviceItemsInputDTO, Errors err) {
		// 执行简单校验
		serviceItemsInputDTO.validate(err);

		// 执行数据库校验
		String serviceItems = serviceItemsInputDTO.getServiceItems();
		ServiceItemsDAO serviceItemsDAO = (ServiceItemsDAO) PlatformContext
				.getApplicationContext().getBean(
						"serviceItemsDAO_" + serviceItems);
		serviceItemsDAO.validate(serviceItemsInputDTO, err);
	}

	/**
	 * 将页面录入的信息写入保全受理明细 传入参数请使用子类实例，才能包含全部的detail信息
	 * 
	 * @param itemsInputDTO
	 */
	public void acceptDetailInput(ServiceItemsInputDTO itemsInputDTO) {
		ServiceItemsDAO itemsDAO = (ServiceItemsDAO) PlatformContext
				.getApplicationContext().getBean(
						"serviceItemsDAO_" + itemsInputDTO.getServiceItems());

		List<PosAcceptDetailDTO> detailDTOlist = itemsDAO
				.generateAcceptDetailDTOList(itemsInputDTO);
		logger.info("generated acceptDetailDTO list:"
				+ PosUtils.describBeanAsJSON(detailDTOlist));
		for (int i = 0; detailDTOlist != null && i < detailDTOlist.size(); i++) {
			PosAcceptDetailDTO detail = detailDTOlist.get(i);
			if (StringUtils.isBlank(detail.getPosNo())) {
				detail.setPosNo(itemsInputDTO.getPosNo());
			}
			/*
			 * 同一个保全号的免填单和代审信息因为在批次受理的时候已经写入明细表，所以这里不执行insert,
			 * 此改动为解决保全明细勾选多张保单生成的其它保全号不能把免填单和代审信息的明细项写入的问题
			 * edit by gaojiaming
			 */
			if (("026".equals(detail.getItemNo()) || "022".equals(detail.getItemNo()) || "028".equals(detail
					.getItemNo()))
					&& "3".equals(detail.getProcessType())
					&& "5".equals(detail.getPosObject())
					&& itemsInputDTO.getPosNo().equals(detail.getPosNo())) {
			} else {
				acceptDAO.insertPosAcceptDetail(detail);
			}
		}
	}

	/**
	 * 更新PosInfo字段值
	 * 
	 * @param posNo
	 * @param updateKey
	 * @param updateValue
	 * @return 出错消息，更新成功执行则返回null
	 */
	public void updatePosInfo(String posNo, String updateKey, String updateValue) {
		acceptDAO.updatePosInfo(posNo, updateKey, updateValue);
		;
	}

	/**
	 * 判断同一受理批次下，前一受理单是否已经生效
	 * 
	 * @param posNo
	 * @return
	 */
	public boolean prePosnoIsEnd(String posNo) {
		return acceptDAO.prePosnoIsEnd(posNo);
	}

	/**
	 * 写状态变迁记录
	 * 
	 * @param changeHistoryDTO
	 */
	public void insertPosStatusChangeHistory(
			PosStatusChangeHistoryDTO changeHistoryDTO) {
		changeHistoryDTO.setChangeDate(commonQueryDAO.getSystemDate());
		acceptDAO.insertPosStatusChangeHistory(changeHistoryDTO);
	}

	/**
	 * 保存个人资料告知书信息
	 * 
	 * @param personalNoticeDTO
	 * @param posNo
	 * @param policyNo
	 */
	public void savePersonalNotice(PersonalNoticeDTO personalNoticeDTO,
			String posNo, String policyNo) {
		String clientNo = null;
		
		// 取得客户号
		if (personalNoticeDTO.getClientInfo() != null) {
			clientNo = personalNoticeDTO.getClientInfo().getClientNo();
		} else {
			String clientOption = personalNoticeDTO.getClientOption();
			if ("A".equals(clientOption)) {
				clientNo = commonQueryDAO.getApplicantByPolicyNo(policyNo);
			} else if ("I".equals(clientOption)) {
				//家庭单多被保险人 增加被保人序列 add by zhangyi.wb
				String insuredSeq = personalNoticeDTO.getInsuredSeq();
				clientNo = commonQueryDAO
						.getInsuredOfPrimaryPlanByPolicyNoNew(policyNo,insuredSeq);
			}
		}

		Set<String> itemOption = personalNoticeDTO.getItemOption();
		if (itemOption == null) {
			throw new RuntimeException("个人资料告知书的告知项不能为空");
		}
		String noticeType = null;
		String noticeCheck = null;

		final String YES = "Y";
		final BigDecimal DECIMAL_ZERO = BigDecimal.valueOf(0);
		final BigInteger INTEGER_ZERO = BigInteger.valueOf(0);

		if (itemOption.contains("1")) {
			noticeType = "1";
			noticeCheck = personalNoticeDTO.getItemAnswer_1();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				List<InsProductInfo> insProductList = personalNoticeDTO
						.getInsProductList();
				if (insProductList != null && insProductList.size() > 0) {
					StringBuffer company = new StringBuffer();
					StringBuffer insName = new StringBuffer();
					StringBuffer insSum = new StringBuffer();
					StringBuffer hospitalizationPremPerDay = new StringBuffer();
					StringBuffer effDate = new StringBuffer();
					for (int i = 0; i < insProductList.size(); i++) {
						InsProductInfo ip = insProductList.get(i);
						String splitor = "";
						if (i > 0) {
							splitor = "##";
						}
						company.append(splitor).append(ip.getCompany());
						insName.append(splitor).append(ip.getInsName());
						insSum.append(splitor).append(
								String.valueOf(ip.getInsSum()));
						hospitalizationPremPerDay.append(splitor).append(
								String.valueOf(ip
										.getHospitalizationPremPerDay()));
						effDate.append(splitor).append(
								PosUtils.formatDate(ip.getEffDate(),
										"yyyy-MM-dd"));
					}
					acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
							noticeType, "2", company.toString());
					acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
							noticeType, "3", insName.toString());
					acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
							noticeType, "4", insSum.toString());
					acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
							noticeType, "5",
							hospitalizationPremPerDay.toString());
					acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
							noticeType, "6", effDate.toString());
				}
			}
		}

		if (itemOption.contains("2")) {
			noticeType = "2";
			noticeCheck = personalNoticeDTO.getItemAnswer_2();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "1", personalNoticeDTO.getItemRemark_2());
			}

			BigDecimal height = personalNoticeDTO.getHeight();
			BigDecimal weight = personalNoticeDTO.getWeight();
			if (height != null && height.compareTo(DECIMAL_ZERO) > 0) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "7", String.valueOf(height));
			}
			if (weight != null && height.compareTo(DECIMAL_ZERO) > 0) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "8", String.valueOf(weight));
			}
		}

		if (itemOption.contains("3")) {
			noticeType = "3";
			noticeCheck = personalNoticeDTO.getItemAnswer_3();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "1", personalNoticeDTO.getItemRemark_3());
			}
		}

		if (itemOption.contains("4")) {
			noticeType = "4";
			noticeCheck = personalNoticeDTO.getItemAnswer_4();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "1", personalNoticeDTO.getItemRemark_4());
			}
		}

		if (itemOption.contains("5")) {
			noticeType = "5";
			noticeCheck = personalNoticeDTO.getItemAnswer_5();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "1", personalNoticeDTO.getItemRemark_5());
			}
		}

		if (itemOption.contains("6")) {
			noticeType = "6";
			noticeCheck = personalNoticeDTO.getItemAnswer_6_1();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "1", personalNoticeDTO.getItemRemark_6_1());
			}

			BigInteger smokeYear = personalNoticeDTO.getSmokeYear();
			BigInteger smokePerDay = personalNoticeDTO.getSmokePerDay();
			String smokeQuitReason = personalNoticeDTO.getSmokeQuitReason();

			if (smokeYear != null && smokeYear.compareTo(INTEGER_ZERO) > 0) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "9", String.valueOf(smokeYear));
			}
			if (smokePerDay != null && smokePerDay.compareTo(INTEGER_ZERO) > 0) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "10", String.valueOf(smokePerDay));
			}
			if (StringUtils.isNotBlank(smokeQuitReason)) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "11", smokeQuitReason);
			}

			noticeType = "7";
			noticeCheck = personalNoticeDTO.getItemAnswer_6_2();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "1", personalNoticeDTO.getItemRemark_6_2());
			}

			BigInteger drinkYear = personalNoticeDTO.getDrinkYear();
			String drinkType = personalNoticeDTO.getDrinkType();
			BigDecimal drinkWeightPerWeek = personalNoticeDTO
					.getDrinkWeightPerWeek();
			String drinkQuitReason = personalNoticeDTO.getDrinkQuitReason();
			if (drinkYear != null && drinkYear.compareTo(INTEGER_ZERO) > 0) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "12", String.valueOf(drinkYear));
			}
			if (drinkWeightPerWeek != null
					&& drinkWeightPerWeek.compareTo(DECIMAL_ZERO) > 0) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "13", String.valueOf(drinkWeightPerWeek));
			}
			if (StringUtils.isNotBlank(drinkType)) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "14", drinkType);
			}
			if (StringUtils.isNotBlank(drinkQuitReason)) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "15", drinkQuitReason);
			}
		}

		if (itemOption.contains("7")) {
			noticeType = "8";
			noticeCheck = personalNoticeDTO.getItemAnswer_7();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "1", personalNoticeDTO.getItemRemark_7());
			}
		}

		if (itemOption.contains("8")) {
			noticeType = "9";
			noticeCheck = personalNoticeDTO.getItemAnswer_8();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "1", personalNoticeDTO.getItemRemark_8());
			}
		}

		if (itemOption.contains("9")) {
			noticeType = "10";
			noticeCheck = personalNoticeDTO.getItemAnswer_9();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "1", personalNoticeDTO.getItemRemark_9());
			}
		}

		if (itemOption.contains("10")) {
			noticeType = "11";
			noticeCheck = personalNoticeDTO.getItemAnswer_10_1();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "1",
								personalNoticeDTO.getItemRemark_10_1());
			}

			noticeType = "12";
			noticeCheck = personalNoticeDTO.getItemAnswer_10_2();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "1",
								personalNoticeDTO.getItemRemark_10_2());
			}

			noticeType = "13";
			noticeCheck = personalNoticeDTO.getItemAnswer_10_3();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "1",
								personalNoticeDTO.getItemRemark_10_3());
			}

			noticeType = "14";
			noticeCheck = personalNoticeDTO.getItemAnswer_10_4();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "1",
								personalNoticeDTO.getItemRemark_10_4());
			}
			noticeType = "35";
			noticeCheck = personalNoticeDTO.getItemAnswer_10_5();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "1",
								personalNoticeDTO.getItemAnswer_10_5());
			}
			noticeType = "36";
			noticeCheck = personalNoticeDTO.getItemAnswer_10_6();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "1",
								personalNoticeDTO.getItemAnswer_10_6());
			}
			noticeType = "37";
			noticeCheck = personalNoticeDTO.getItemAnswer_10_7();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "1",
								personalNoticeDTO.getItemAnswer_10_7());
			}
		}

		if (itemOption.contains("11")) {
			noticeType = "15";
			noticeCheck = personalNoticeDTO.getItemAnswer_11_1();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "1",
								personalNoticeDTO.getItemRemark_11_1());
			}

			noticeType = "16";
			noticeCheck = personalNoticeDTO.getItemAnswer_11_2();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "1",
								personalNoticeDTO.getItemRemark_11_2());
			}
		}

		if (itemOption.contains("12")) {
			noticeType = "17";
			noticeCheck = personalNoticeDTO.getItemAnswer_12_1();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "1",
								personalNoticeDTO.getItemRemark_12_1());
			}

			BigInteger fetationWeeks = personalNoticeDTO.getFetationWeeks();
			if (fetationWeeks != null
					&& fetationWeeks.compareTo(INTEGER_ZERO) > 0) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "16", String.valueOf(fetationWeeks));
			}

			noticeType = "18";
			noticeCheck = personalNoticeDTO.getItemAnswer_12_2();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "1",
								personalNoticeDTO.getItemRemark_12_2());
			}

			noticeType = "19";
			noticeCheck = personalNoticeDTO.getItemAnswer_12_3();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "1",
								personalNoticeDTO.getItemRemark_12_3());
			}

			noticeType = "20";
			noticeCheck = personalNoticeDTO.getItemAnswer_12_4();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "1",
								personalNoticeDTO.getItemRemark_12_4());
			}
		}

		if (itemOption.contains("13")) {
			noticeType = "21";
			noticeCheck = personalNoticeDTO.getItemAnswer_13_1();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "1",
								personalNoticeDTO.getItemRemark_13_1());
			}

			noticeType = "22";
			noticeCheck = personalNoticeDTO.getItemAnswer_13_2();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "1",
								personalNoticeDTO.getItemRemark_13_2());
			}

			noticeType = "23";
			noticeCheck = personalNoticeDTO.getItemAnswer_13_3();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "1",
								personalNoticeDTO.getItemRemark_13_3());
			}

			noticeType = "24";
			noticeCheck = personalNoticeDTO.getItemAnswer_13_4();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "1",
								personalNoticeDTO.getItemRemark_13_4());
			}

			noticeType = "25";
			noticeCheck = personalNoticeDTO.getItemAnswer_13_5();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "1",
								personalNoticeDTO.getItemRemark_13_5());
			}

			noticeType = "26";
			noticeCheck = personalNoticeDTO.getItemAnswer_13_6();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "1",
								personalNoticeDTO.getItemRemark_13_6());
			}

			noticeType = "27";
			noticeCheck = personalNoticeDTO.getItemAnswer_13_7();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "1",
								personalNoticeDTO.getItemRemark_13_7());
			}

			noticeType = "28";
			noticeCheck = personalNoticeDTO.getItemAnswer_13_8();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "1",
								personalNoticeDTO.getItemRemark_13_8());
			}

			noticeType = "29";
			noticeCheck = personalNoticeDTO.getItemAnswer_13_9();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "1",
								personalNoticeDTO.getItemRemark_13_9());
			}

			noticeType = "30";
			noticeCheck = personalNoticeDTO.getItemAnswer_13_10();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "1",
						personalNoticeDTO.getItemRemark_13_10());
			}

			noticeType = "31";
			noticeCheck = personalNoticeDTO.getItemAnswer_13_11();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "1",
						personalNoticeDTO.getItemRemark_13_11());
			}

			noticeType = "38";
			noticeCheck = personalNoticeDTO.getItemAnswer_13_12();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "1",
						personalNoticeDTO.getItemRemark_13_12());
			}
			
			noticeType = "32";
			noticeCheck = personalNoticeDTO.getItemAnswer_13_13();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "1",
						personalNoticeDTO.getItemRemark_13_13());
			}

		}

		if (itemOption.contains("14")) {
			noticeType = "33";
			noticeCheck = personalNoticeDTO.getItemAnswer_14();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "1", personalNoticeDTO.getItemRemark_14());
			}
		}

		if (itemOption.contains("15")) {
			noticeType = "34";
			noticeCheck = personalNoticeDTO.getItemAnswer_15();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "1", personalNoticeDTO.getItemRemark_15());
			}

			BigDecimal birthWeight = personalNoticeDTO.getBirthWeight();
			BigInteger birthStayHispitalDays = personalNoticeDTO
					.getBirthStayHispitalDays();
			BigDecimal birthHeight = personalNoticeDTO.getBirthHeight();
			String birthHospital = personalNoticeDTO.getBirthHospital();
			if (birthWeight != null && birthWeight.compareTo(DECIMAL_ZERO) > 0) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "17", String.valueOf(birthWeight));
			}
			if (birthStayHispitalDays != null
					&& birthStayHispitalDays.compareTo(INTEGER_ZERO) > 0) {
				acceptDAO
						.insertPosPersonalNoticeDetail(posNo, clientNo,
								noticeType, "18",
								String.valueOf(birthStayHispitalDays));
			}
			if (birthHeight != null && birthHeight.compareTo(DECIMAL_ZERO) > 0) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "19", String.valueOf(birthHeight));
			}
			if (StringUtils.isNotBlank(birthHospital)) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "20", birthHospital);
			}
		}
		if (itemOption.contains("16")) {
			noticeType = "39";
			noticeCheck = personalNoticeDTO.getItemAnswer_16();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "1", personalNoticeDTO.getItemRemark_16());
			}
		}
		if (itemOption.contains("17")) {
			noticeType = "40";
			noticeCheck = personalNoticeDTO.getItemAnswer_17();
			acceptDAO.insertPosPersonalNotice(posNo, clientNo, noticeType,
					noticeCheck);

			if (YES.equals(noticeCheck)) {
				acceptDAO.insertPosPersonalNoticeDetail(posNo, clientNo,
						noticeType, "1", personalNoticeDTO.getItemRemark_17());
			}
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
			this.updatePosInfo(tmpPosInfo.getPosNo(), "ACCEPT_SEQ",
					String.valueOf(tmpPosInfo.getAcceptSeq().intValue() + 1));
		}

		// 插入新受理
		PosInfoDTO newPosInfo = new PosInfoDTO();
		Date sysdate = commonQueryDAO.getSystemDate();
		newPosInfo.setAcceptor(foundPosInfo.getAcceptor());
		newPosInfo.setAcceptStatusCode("A01"); // 受理状态：批次受理
		newPosInfo.setAccountNo(foundPosInfo.getAccountNo());
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
			this.updatePosInfo(tmpPosInfo.getPosNo(), "remark", remark);

			// 更新取消原因
			this.updatePosInfo(posNo, "ROLLBACK_CAUSE", rollbackCause);

			// 更新取消详细信息
			this.updatePosInfo(posNo, "ABANDONED_DETAIL", rollbackCauseDetail
					+ "，新保全号为：" + newPosInfo.getPosNo());
		}
		/* 代审信息或免填单标志写入受理明细 edit by gaojiaming start */
		List<PosAcceptDetailDTO> posAcceptDetailDTOList = acceptDAO.getPosAcceptDetailDTOList(posNo);
		if (posAcceptDetailDTOList != null && posAcceptDetailDTOList.size() > 0) {
			for (PosAcceptDetailDTO posAcceptDetailDTO : posAcceptDetailDTOList) {
				PosAcceptDetailDTO posAcceptDetailDTOInsert = new PosAcceptDetailDTO();
				posAcceptDetailDTOInsert.setPosNo(newPosInfo.getPosNo());
				posAcceptDetailDTOInsert.setPosObject(posAcceptDetailDTO.getPosObject());
				posAcceptDetailDTOInsert.setItemNo(posAcceptDetailDTO.getItemNo());
				posAcceptDetailDTOInsert.setProcessType(posAcceptDetailDTO.getProcessType());
				posAcceptDetailDTOInsert.setObjectValue(posAcceptDetailDTO.getObjectValue());
				posAcceptDetailDTOInsert.setNewValue(posAcceptDetailDTO.getNewValue());
				acceptDAO.insertPosAcceptDetail(posAcceptDetailDTOInsert);
			}
		}
		/* 代审信息或免填单标志写入受理明细 edit by gaojiaming end */
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("posBatchNo", posBatchNo);
		retMap.put("newPosNo", newPosInfo.getPosNo());
		return retMap;
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
				this.updatePosInfo(posNo, "ACCEPT_STATUS_CODE", "C02");
			}
		} else if (!currentAcceptStatus.startsWith("E")) {
			Map<String, Object> ret = this.workflowControl("11", posNo, false);
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
	 * 检验抵交续期保费保单号的有效性
	 * 
	 * @param  map
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-9-22
	 */
	public String checkPaypolicyValid(Map<String, Object> map) {	
		return acceptDAO.checkPaypolicyValid(map);
	}
	
	/**
	 * 根据保单号查询是否是电销(0不代表电销，1代表是电销)
	 */
	public int getPolicyNoCount (String policyNo){
		return acceptDAO.getPolicyNoCount(policyNo);
	}
	
	/**
	 * @Description: 更新关联条码
	 * @methodName: updatePosApplyFiles
	 * @param barcodeNo 条码
	 * @param columnKey 字段名称
	 * @param columnValue 字段value
	 * @return void
	 * @author WangMingShun
	 * @date 2015-12-28
	 * @throws
	 */
	public void updatePosApplyFiles(String barcodeNo, 
			String columnKey, String columnValue) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("barcodeNo", barcodeNo);
		map.put("columnKey", columnKey);
		map.put("columnValue", columnValue);
		acceptDAO.updatePosApplyFiles(map);
	}
}
