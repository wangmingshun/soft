package com.sinolife.pos.schedule.job;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sinolife.efs.util.pub.domain.CallResult;
import com.sinolife.esbpos.product.ProductService;
import com.sinolife.esbpos.web.WebPosService;
import com.sinolife.pos.acceptance.branch.service.BranchAcceptService;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_20;
import com.sinolife.pos.common.dto.PolicyContactInfoDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.print.service.PrintService;
import com.sinolife.pos.schedule.HandlePdfMail;
import com.sinolife.pos.schedule.dao.ScheduleDAO;
import com.sinolife.sf.config.RuntimeConfig;
import com.sinolife.sf.platform.runtime.PlatformContext;

@Component("sendMailJob")
// @Service("sendMailJob")
public class SendMailJob {
	@Autowired
	private ScheduleDAO scheduleDAO;
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private CommonQueryDAO commonQueryDAO;
	@Autowired
	private HandlePdfMail handlePdfMail;
	@Autowired
	private ServiceItemsDAO_20 serviceItemsDAO_20;
	@Autowired
	private PrintService printService;
	@Autowired
	private TransactionTemplate txTmpl;
	@Autowired
	private BranchAcceptService branchAcceptService;

	public void execute() {
		logger.info("SendMailJob "
				+ Thread.currentThread().getId()
				+ " started at "
				+ PosUtils.formatDate(commonQueryDAO.getSystemDate(),
						"yyyy-MM-dd HH:mm:ss:SSS"));
		// 官网，微信，E终端渠道操作的保全项目发送电子批单;订阅了电子函件的官网加挂保单不管什么渠道都发送电子批单
		List<Map<String, Object>> notHandPdfEmailList = scheduleDAO
				.queryNothandPdfEmail();
		if (notHandPdfEmailList != null && !notHandPdfEmailList.isEmpty()) {
			for (final Map<String, Object> map : notHandPdfEmailList) {

				txTmpl.execute(new TransactionCallback<Boolean>() {
					@Override
					public Boolean doInTransaction(
							TransactionStatus transactionstatus) {
						// 验证存在需要发送批单的保全记录，将该记录锁定，并返回保全号码
						String posNo = scheduleDAO
								.lockPosInfoForSendEntMail((String) map
										.get("POS_NO"));
						if ("".equals(posNo)) {
							return false;
						}
						String policyNo = (String) map.get("POLICY_NO");
						String acceptChannelCode = (String) map
								.get("ACCEPT_CHANNEL_CODE");
						String appName = commonQueryDAO
								.getAppNameByPolicyNo(policyNo);
						// 邮箱置空
						String email = null;
						// 电子信函取邮箱方式
						if ("Y".equals((String) map.get("CAN_SEND_E_LETTER"))) {
								try {
									email = printService
									.getEmailAddressForELetter(policyNo);
								} catch (Exception e) {
									logger.error("保单号：" + policyNo + "；---保全号："
											+ posNo  + "---"
											+ e.getMessage());
								}
						}
						// 保全明细表取邮箱方式
						else if ("7".equals(acceptChannelCode)
								|| "15".equals(acceptChannelCode)
								|| "16".equals(acceptChannelCode)
								|| "24".equals(acceptChannelCode)) {
							try {
								email = printService
										.getEmailAddressByPosNo((String) map
												.get("POS_NO"));
							} catch (Exception e) {
								logger.error("保单号：" + policyNo + "；---保全号："
										+ posNo  + "---"
										+ e.getMessage());
							}
						}
						else if ("19".equals(acceptChannelCode)) {
							try {
							     handlePdfMail.handPDFEntIm(posNo);
							} catch (Exception e) {
								e.printStackTrace();
								logger.error("保单号：" + policyNo + "；---保全号："
										+ posNo  + "---"
										+ e.getMessage());
								transactionstatus.setRollbackOnly();
								return false;
							}
						}
						// 其它取保单层(上面情况没取到或者其它情况)
						if (email == null || "".equals(email)) {
							PolicyContactInfoDTO policyContactInfoDTO = serviceItemsDAO_20
									.queryPolicyContactInfo(policyNo);
							email = policyContactInfoDTO.getEmailDesc();
						}
						if (!StringUtils.isBlank(email)) {
							try {
								handlePdfMail.handPDFEnt(posNo, email, appName);
							} catch (Exception e) {
								e.printStackTrace();
								logger.error("保单号：" + policyNo + "；---保全号："
										+ posNo + ";---emai:" + email + "---"
										+ e.getMessage());
								transactionstatus.setRollbackOnly();
								return false;
							}
						}
						return true;
					}
				});
			}
		}
		// 成长红包在柜面加保后通知微信端
		List<Map<String, Object>> addPremList = scheduleDAO.queryAddPremList();

		if (addPremList != null && !addPremList.isEmpty()) {

			for (final Map<String, Object> map : addPremList) {

				txTmpl.execute(new TransactionCallback<Boolean>() {
					@Override
					public Boolean doInTransaction(
							TransactionStatus transactionstatus) {

						String policyNo = "";
						String posNo = "";

						try {
							policyNo = (String) map.get("POLICYNO");

							Date effectiveDate = (Date) map
									.get("EFFECTIVEDATE");

							String applyBarCode = commonQueryDAO
									.queryPolicyApplyBarcode(policyNo);

							posNo = (String) map.get("POSNO");
							
							//锁住本条记录
							boolean locked =scheduleDAO.lockNoticeAddPrem(posNo);
							if (!locked)
							{
								return false;
							}

							List<Map<String, Object>> lm = commonQueryDAO
									.getPosPolChangeList(policyNo, posNo);
							Map<String, Object> map = new HashMap<String, Object>();
							String changeType;
							BigDecimal changeValue;
							// 加保保费
							BigDecimal preSum = new BigDecimal(0);
							// 加保保额
							BigDecimal calSum = new BigDecimal(0);
							;
							if (lm != null && lm.size() > 0) {
								for (int i = 0; i < lm.size(); i++) {

									map = (Map<String, Object>) (lm.get(i));

									changeType = (String) map.get("CHANGETYPE");
									changeValue = (BigDecimal) map
											.get("CHANGEVALUE");

									if ("3".equals(changeType)) {

										preSum = changeValue;

									} else if ("1".equals(changeType)) {

										calSum = changeValue;

									}

								}
							}

							// * key-policyNo： 保单号 类型String
							// * key-applyBarCode: 投保单号 类型String
							// * key-preSum:本次保费 类型BigDecimal
							// * key-calSum:本次保额 类型BigDecimal
							// * key-posNo:保全号 类型String
							// * key-effectDate：生效日期 类型：string，格式:yyyy-MM-dd
							// hh:mi:ss

							Map<String, Object> reqMap = new HashMap<String, Object>();
							// reqMap.put("effectDate", DateUtils.toDate(
							// effectiveDate, "yyyy-MM-dd HH:mm:ss"));
							//
							//根据保全号查询是否为成长红包小天使理赔加保
							if (commonQueryDAO.getIsEixstAcceptDetail(posNo) > 0) {
								reqMap.put("transCode", "2");
							}
							reqMap.put("effectDate", effectiveDate);
							reqMap.put("policyNo", policyNo);
							reqMap.put("posNo", posNo);
							reqMap.put("applyBarCode", applyBarCode);
							reqMap.put("preSum", preSum);
							reqMap.put("calSum", calSum);
							WebPosService webPosService = PlatformContext
									.getEsbContext().getEsbService(
											WebPosService.class);
							CallResult<Map<String, String>> rtMap = webPosService
									.envetNoticeWithPolicyAdd(reqMap);

							String resultCode = rtMap.getResultCode();
							String resultMessage = rtMap.getResultMessage();

							logger.debug("addPremList" + posNo + " resultCode"
									+ resultCode + " resultMessage"
									+ resultMessage);

							if ("Y".equals(resultCode)) {

								scheduleDAO.updateNoticeAddPrem(posNo);
							}

						} catch (Exception e) {
							e.printStackTrace();
							logger.error("保单号：" + policyNo + "；---保全号：" + posNo
									+ ";---envetNoticeWithPolicyAdd---"
									+ e.getMessage());
							transactionstatus.setRollbackOnly();
							return false;
						}
						return false;
					}
				});

			}
		}
		
		//抓取满足发送短信的批次，自动发送“满意度调查”短信。
		List<Map<String, Object>> sendSMSList = scheduleDAO.querySatisfactionList();
		
		if(PosUtils.isNotNullOrEmpty(sendSMSList)) {
			for(final Map<String, Object> sendMap : sendSMSList) {
				txTmpl.execute(new TransactionCallback<Boolean>() {
					@Override
					public Boolean doInTransaction(
							TransactionStatus transactionstatus) {
						
						String posBatchNo = "";
						
						try {
							Map<String, Object> map = new HashMap<String, Object>();
							
							posBatchNo = (String) sendMap.get("POSBATCHNO");
							
							boolean locked = scheduleDAO.lockSatisfaction(posBatchNo);
							if(!locked) {
								return false;
							}
							
							map.put("BUSINESS_NO", posBatchNo);// 业务号
							
							String modle_id = RuntimeConfig.get("sf.system.number",
									String.class);
							map.put("MODULE_ID", modle_id);// 模块ID

							String envname = RuntimeConfig.get(
									"com.sinolife.pos.envname", String.class);// 环境名称
							map.put("ENV_NAME", envname);
							
							String serviceUrl = RuntimeConfig.get(
									"com.sinolife.pos.serviceurl", String.class);// 回调服务url
							map.put("SERVICE_URL", serviceUrl);

							String serviceId = 
								"com.sinolife.pos.sms.SmsSatisfactionCallBackService";
							map.put("SERVICE_ID", serviceId);// 回调服务id
							
							String phoneNo = scheduleDAO.queryPhoneNoByPosBatchNo(posBatchNo);
//							phoneNo = "PRD".equals(envname) ? phoneNo : "18682002271";
//							phoneNo = "PRD".equals(envname) ? phoneNo : "15602965566";
//							phoneNo = "PRD".equals(envname) ? phoneNo : "18681442617";
//							phoneNo = "PRD".equals(envname) ? phoneNo : "13530827707";
//							phoneNo = "PRD".equals(envname) ? phoneNo : "13377119641";
							phoneNo = "PRD".equals(envname) ? phoneNo : "";
							map.put("MOBILE", phoneNo);// 手机号

							String sendMsg = "请您对本次为您服务的柜面人员做满意度评价："
								+ "满意，回复1；基本满意，回复2；不满意，回复3，感谢支持！";
							map.put("SEND_MSG", sendMsg);// 发送内容
							
							ProductService cc = PlatformContext.getEsbContext()
								.getEsbService(ProductService.class);
							Map resultMap = cc.sendNeedReplySmsRealTime(map);
							
							String flag = (String) resultMap.get("FLAG");
							if("Y".equals(flag)) {
								scheduleDAO.updatePosApplyBatch(posBatchNo, "0");
								logger.info("客户满意度调查短信发送成功！"
										+ "businessNo:" + posBatchNo
										+ ",MOBILE:" + phoneNo 
										+ ",短信信息序列号ID:" + resultMap.get("ID")
										+ ",MODULE_ID:" + modle_id 
										+ ",ENV_NAME:" + envname
										+ ",SERVICE_URL:" + serviceUrl 
										+ ",SERVICE_ID:" + serviceId);
							} else {
								logger.error("发送短信失败！posBatchNo:" + posBatchNo);
							}
						} catch (Exception e) {
							e.printStackTrace();
							transactionstatus.setRollbackOnly();
							return false;
						}
						return true;
					}
				});
			}
		}
		
		//获取官网非资金类保全操作(联系方式变更,客户资料变更, 保单挂失)一小时后仍未完成的保全,将其撤销
		List<String> queryCanclePosInfoList = scheduleDAO.queryCanclePosInfoList();
		
		if(PosUtils.isNotNullOrEmpty(queryCanclePosInfoList)) {
			for(final String posNo : queryCanclePosInfoList) {
				txTmpl.execute(new TransactionCallback<Boolean>() {
					@Override
					public Boolean doInTransaction(
							TransactionStatus transactionstatus) {
						
						try {
							
							boolean locked = scheduleDAO.lockCanclePosInfo(posNo);
							if(!locked) {
								return false;
							}
							
							//撤销保全
							PosInfoDTO posInfo = commonQueryDAO.queryPosInfoRecord(posNo);
							// 取消受理
							branchAcceptService.cancelAccept(posInfo, PlatformContext.getCurrentUser());
							
							logger.info("保全受理取消成功");
						} catch (Exception e) {
							e.printStackTrace();
							transactionstatus.setRollbackOnly();
							logger.error("保全受理取消失败,posNo:" + posNo + "。" + e.getMessage());
							return false;
						}
						return true;
					}
				});
			}
		}

		logger.info("SendMailJob "
				+ Thread.currentThread().getId()
				+ " ended at "
				+ PosUtils.formatDate(commonQueryDAO.getSystemDate(),
						"yyyy-MM-dd HH:mm:ss:SSS"));
	}
}
