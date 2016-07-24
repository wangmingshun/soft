package com.sinolife.pos.schedule.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sinolife.efs.util.pub.domain.CallResult;
import com.sinolife.esbpos.web.WebPosService;
import com.sinolife.pos.acceptance.branch.service.BranchAcceptService;
import com.sinolife.pos.common.dao.ClientInfoDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.print.dao.PrintDAO;
import com.sinolife.pos.schedule.dao.ScheduleDAO;
import com.sinolife.sf.platform.runtime.PlatformContext;

/**
 * 待处理规则检查件批处理
 */
@Component("processRuleCheckJob")
public class ProcessRuleCheckJob {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private ScheduleDAO scheduleDAO;

	@Autowired
	private CommonQueryDAO commonQueryDAO;
	
	@Autowired
	private ClientInfoDAO clientInfoDAO;

	@Autowired
	private TransactionTemplate txTmpl;

	@Autowired
	private BranchAcceptService branchAcceptService;

	@Autowired
	private PrintDAO printDAO;

	public void execute() {
		logger.info("ProcessRuleCheckJob "
				+ Thread.currentThread().getId()
				+ " started at "
				+ PosUtils.formatDate(commonQueryDAO.getSystemDate(),
						"yyyy-MM-dd HH:mm:ss:SSS"));

		// 查询待处理规则检查保全件
		List<Map<String, Object>> processRuleCheckList = scheduleDAO
				.queryProcessRuleCheckList();
		if (processRuleCheckList != null && !processRuleCheckList.isEmpty()) {
			for (Map<String, Object> map : processRuleCheckList) {
				final String posNo = (String) map.get("POS_NO");
				final String serviceItems = (String) map.get("SERVICE_ITEMS");
				txTmpl.execute(new TransactionCallback<Boolean>() {
					@Override
					public Boolean doInTransaction(
							TransactionStatus transactionstatus) {
						boolean success = false;
						try {
							logger.info("processing ... " + posNo);
							boolean locked = scheduleDAO
									.lockPosInfoRecord(posNo);
							// 确保得到保全记录的数据库锁，并且再次验证受理状态，避免重复经办生效
							if (locked
									&& scheduleDAO.verifyAcceptStatus(posNo,
											"A03")) {
								Map<String, Object> retMap = branchAcceptService
										.workflowControl("03", posNo, false);
								String flag = (String) retMap.get("flag");
								if ("C01".equals(flag)) {
									// 规则检查不通过
									success = true;
								} else if ("A19".equals(flag)) {

									// 如果是免签单受理并且是非退费类保全项目需要打印免签单后才能继续推动
									String IsApplicationFree = printDAO
											.checkIsApplicationFree(posNo);

									if ("Y".endsWith(IsApplicationFree)
											&& !ArrayUtils.contains(
													new String[] { "19", "23",
															"21", "18", "28",
															"7", "26", "25",
															"24", "30", "31",
															"32", "29", "43" },
													serviceItems)) {
										// 需要打印申请书
										success = true;

									} else {

										// 待审批判断处理
										retMap = branchAcceptService
												.workflowControl("05", posNo,
														false);
										flag = (String) retMap.get("flag");
										if ("A08".equals(flag)) {
											// 已送审
											success = true;
										} else if ("A12".equals(flag)) {
											// 已送人工核保
											success = true;
										} else if ("A15".equals(flag)) {
											// 待收费
											success = true;
										} else if ("E01".equals(flag)
												|| "A17".equals(flag)) {
											// 生效完成 或 待保单打印
											success = true;
											
											// 成长红包操作了退保、契撤后，调用下面的接口通知微信端
											if ("Y".equals(clientInfoDAO
													.queryWebNeedNotify(posNo))) {
												PosInfoDTO posInfo = commonQueryDAO
														.queryPosInfoRecord(posNo);
												if (posInfo != null) {
													String policyNo = posInfo
															.getPolicyNo();

													String applyBarCode = commonQueryDAO
															.queryPolicyApplyBarcode(policyNo);
													Map<String, String> reqMap = new HashMap<String, String>();
													reqMap.put("businessNo",
															"1002");
													reqMap.put("policyNo",
															policyNo);
													reqMap.put("applyNo",
															applyBarCode);

													WebPosService wps = PlatformContext
															.getEsbContext()
															.getEsbService(
																	WebPosService.class);

													wps.surrenderNotify(reqMap);
												}
											}
											
											
										}
									}
								} else if ("A03".equals(flag)) {
									logger.info(posNo + " 被延时处理");
									success = true;
								}
								logger.info("process -> " + posNo + " success:"
										+ retMap);
							} else {
								logger.info("lock -> " + posNo + " failed");
							}
						} catch (Exception e) {
							logger.info("process -> " + posNo
									+ " caught an exception", e);
						}

						// 处理失败回滚
						if (!success) {
							logger.info("process -> " + posNo + " failed");
							transactionstatus.setRollbackOnly();
						}

						return success;
					}
				});
			}
		}
		// 增加一个定时的job对做完联系方式变更中对官网客户的联系电话的一个修改
		List<Map<String, Object>> webMobileChangedList = scheduleDAO
				.queryWebMobileChangedList();
		if (webMobileChangedList != null && !webMobileChangedList.isEmpty()) {
			for (Map<String, Object> map : webMobileChangedList) {

				String posNo = (String) map.get("POS_NO");
				String clientNo = (String) map.get("OBJECT_VALUE");
				String mobile = (String) map.get("NEW_VALUE");
				logger.info("clientNo-> " + clientNo + " mobile" + mobile);

				try {
					WebPosService webPosService = PlatformContext
							.getEsbContext().getEsbService(WebPosService.class);
					webPosService.modifyUserMobile(clientNo, mobile);
					scheduleDAO.updateDetailChangeWebPhone(posNo);
				} catch (Exception e) {
					logger.info(" modifyUserMobile  error posNo: " + posNo
							+ " clientNo:" + clientNo + " mobile:" + mobile, e);

				}

			}

		}
		// 增加一个定时的job对做投保人变更的保单通知官网解除加挂电话
/*		List<Map<String, Object>> appChangedList = scheduleDAO.queryAppChangeList();
    if (appChangedList != null && !appChangedList.isEmpty()) {
	   for (Map<String, Object> map : appChangedList) {
         String posNo = (String) map.get("POS_NO");
		 String clientNo = (String) map.get("OBJECT_VALUE");
		 String policy_no = (String) map.get("POLICY_NO");
		 String channelType= (String) map.get("CHANNEL_TYPE");
		 logger.info("clientNo-> " + clientNo + " policy_no->" + policy_no+ " posNo->" + posNo);
		 try {
			WebPosService webPosService = PlatformContext
					.getEsbContext().getEsbService(WebPosService.class);
			//解除加挂
			CallResult reMap=webPosService.relieveAddPolicy(policy_no, clientNo, channelType, "10");
			String resultCode = reMap.getResultCode();
			String resultMessage = reMap.getResultMessage();
			//更新状态
           if ("Y".equals(resultCode)){
   			  scheduleDAO.updateNoticeAppChange(posNo);
           }else{
        	   logger.info(" modifyUserMobile  error posNo: " + posNo
   					+ " clientNo:" + clientNo + " policy_no:" + policy_no + " resultMessage:" + resultMessage);
           }

		 } catch (Exception e) {
			logger.info(" modifyUserMobile  error posNo: " + posNo
					+ " clientNo:" + clientNo + " policy_no:" + policy_no, e);

		}
       }
     }	*/	
		logger.info("ProcessRuleCheckJob "
				+ Thread.currentThread().getId()
				+ " ended at "
				+ PosUtils.formatDate(commonQueryDAO.getSystemDate(),
						"yyyy-MM-dd HH:mm:ss:SSS"));

	}

}
