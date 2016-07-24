package com.sinolife.pos.schedule.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sinolife.esbpos.web.WebPosService;
import com.sinolife.pos.acceptance.branch.service.BranchAcceptService;
import com.sinolife.pos.common.dao.ClientInfoDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.schedule.dao.ScheduleDAO;
import com.sinolife.sf.platform.runtime.PlatformContext;

/**
 * 待核保规则检查件批处理
 */
@Component("undwrtRuleCheckJob")
public class UndwrtRuleCheckJob {

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

	public void execute() {
		logger.info("UndwrtRuleCheckJob " + Thread.currentThread().getId()
				+ " started .... ");

		// 查询待核保规则检查保全件
		List<Map<String, Object>> undwrtRuleCheckList = scheduleDAO
				.queryUndwrtRuleCheckList();
		if (undwrtRuleCheckList != null && !undwrtRuleCheckList.isEmpty()) {
			for (Map<String, Object> map : undwrtRuleCheckList) {
				final String posNo = (String) map.get("POS_NO");
				Map<String, Object> rMap = txTmpl
						.execute(new TransactionCallback<Map<String, Object>>() {
							@Override
							public Map<String, Object> doInTransaction(
									TransactionStatus transactionstatus) {
								boolean success = false;
								boolean locked = false;
								Map<String, Object> returnMap = new HashMap<String, Object>();
								try {
									logger.info("UndwrtRule..." + posNo);
									locked = scheduleDAO
											.lockPosInfoRecord(posNo);
									if (locked
											&& scheduleDAO.verifyAcceptStatus(
													posNo, "A10")) {
										logger.info("lock pos_info->" + posNo
												+ " success.");

										Map<String, Object> retMap = branchAcceptService
												.workflowControl("06", posNo,
														false);
										logger.info("workflowControl return:"
												+ retMap);
										String flag = (String) retMap
												.get("flag");
										if ("A12".equals(flag)) {
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
									} else {
										logger.info("lock -> " + posNo
												+ " failed");
									}
									returnMap.put("posNo", posNo);
								} catch (RuntimeException re) {
									logger.info("UndwrtRuleCheckJob->" + posNo
											+ " caught an RuntimeException"
											+ re.getMessage(), re);
									returnMap.put("wrongMes", re.getMessage());

								} catch (Exception e) {
									returnMap.put("wrongMes", e.getMessage());

									logger.info(
											"UndwrtRuleCheckJob->" + posNo
													+ " caught an exception"
													+ e.getMessage(), e);
								}
								// 处理失败回滚
								if (!success) {
									if (!locked) {// 获取锁失败
										returnMap.put("success", "L");

										logger.info("lock -> " + posNo
												+ " failed");
									} else {
										returnMap.put("success", "N");
										logger.info("process -> " + posNo
												+ " failed");
										transactionstatus.setRollbackOnly();
									}

								} else {
									returnMap.put("success", "Y");
								}
								return returnMap;
							}
						});
				// 工作流无法推动的保全进行暂停处理
				if ("N".equals(rMap.get("success"))) {

					boolean locked = scheduleDAO.lockPosInfoRecord(posNo);
					if (locked) {
						logger.info("workflowNextFailure -> " + posNo
								+ " needCreatProblemItem");

						final String wrongMes = (String) rMap.get("wrongMes");
						logger.info("workflowNextFailure -> " + posNo
								+ wrongMes);

						txTmpl.execute(new TransactionCallback<String>() {
							@Override
							public String doInTransaction(
									TransactionStatus transactionstatus) {

								Map<String, Object> map = scheduleDAO.workflowFailure(
										posNo, wrongMes.substring(0, wrongMes
												.length() > 3500 ? 3500
												: wrongMes.length() - 1));

								if ("1".equals(map.get("p_flag"))) {
									transactionstatus.setRollbackOnly();
									logger.info("UndwrtRuleCreateProblemItems->"
											+ posNo
											+ " caught an exception"
											+ map.get("p_message"));
								} else {
									logger.info("UndwrtRuleCreateProblemItems->"
											+ posNo + " sucess ");

								}

								return "Y";
							}
						});

					}

				}

			}
		}

		logger.info("UndwrtRuleCheckJob " + Thread.currentThread().getId()
				+ " ended ");
	}
}
