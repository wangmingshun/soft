package com.sinolife.esbpos.product;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.sinolife.pos.acceptance.branch.service.BranchAcceptService;
import com.sinolife.pos.common.dao.CommonAcceptDAO;
import com.sinolife.pos.others.sms.dao.SmsDAO;
import com.sinolife.pos.schedule.dao.ScheduleDAO;
import com.sinolife.sf.config.RuntimeConfig;
import com.sinolife.sf.esb.EsbMethod;
import com.sinolife.sf.platform.runtime.PlatformContext;

@Service("smsReplyCallBackService")
public class SmsReplyCallBackServiceImpl implements SmsReplyCallBackService {

	@Autowired
	CommonAcceptDAO commonAcceptDAO;

	@Autowired
	private ScheduleDAO scheduleDAO;

	@Autowired
	SmsDAO smsDAO;

	@Autowired
	private BranchAcceptService branchAcceptService;

	private Logger logger = Logger.getLogger(getClass());

	/**
	 * 短信回复进行保全状态修改
	 * 
	 * @param businessNo
	 *            业务号
	 * @param id
	 *            短信信息序列号
	 * @param mobile
	 *            手机号
	 * @param replyMsg
	 *            回复内容
	 * 
	 * @return 成功与失败标志 Y-成功；N-失败
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.sms.sendCallSms")
	public String smsReplyCallBack(String businessNo, String id, String mobile,
			String replyMsg) {
		String ret = "N";
		logger.info("正调回调方法smsReplyCallBack");
		logger.info("businessNo" + businessNo);
		logger.info("id" + id);
		logger.info("mobile" + mobile);
		logger.info("replyMsg" + replyMsg);

		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("procType", "14");
		paraMap.put("posNo", businessNo);
		paraMap.put("result", replyMsg);
		commonAcceptDAO.workflowControl(paraMap);

		if ("01".endsWith((String) paraMap.get("flag"))) {
			Map<String, Object> smsMap = new HashMap<String, Object>();

			smsMap = smsDAO.sendMposSMS(businessNo, "07");

			Map<String, Object> map = new HashMap<String, Object>();

			map.put("BUSINESS_NO", businessNo);// 业务号
			String modle_id = RuntimeConfig.get("sf.system.number",
					String.class);
			map.put("MODULE_ID", modle_id);// 模块ID

			String envname = RuntimeConfig.get("com.sinolife.pos.envname",
					String.class);// 环境名称
			map.put("ENV_NAME", envname);
			String serviceUrl = RuntimeConfig.get(
					"com.sinolife.pos.serviceurl", String.class);// 回调服务url
			map.put("SERVICE_URL", serviceUrl);

			map.put("SERVICE_ID", "com.sinolife.pos.sms.sendCallSms");// 回调服务id
			map.put("MOBILE", smsMap.get("phone"));// 手机号

			map.put("SEND_MSG", (String) paraMap.get("message"));// 发送内容

			ProductService cc = PlatformContext.getEsbContext().getEsbService(
					ProductService.class);
			cc.sendNeedReplySmsRealTime(map);
		} else {

			boolean locked = scheduleDAO.lockPosInfoRecord(businessNo);
			if (locked && scheduleDAO.verifyAcceptStatus(businessNo, "A10")) {

				Map<String, Object> retMap = branchAcceptService
						.workflowControl("06", businessNo, false);

				logger.info((String) retMap.get("flag"));

				ret = "Y";
			}
		}

		logger.info("方法smsReplyCallBack结束");
		return ret;
	}

}
