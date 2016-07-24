package com.sinolife.esbpos.product;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinolife.pos.schedule.dao.ScheduleDAO;

@Service("SmsSatisfactionCallBackService")
public class SmsSatisfactionCallBackServiceImpl implements SmsSatisfactionCallBackService {

	@Autowired
	private ScheduleDAO scheduleDAO;

	private Logger logger = Logger.getLogger(getClass());

	/**
	 * 短信回复进行客户满意度写入
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
	public String smsReplyCallBack(String businessNo, String id, String mobile,
			String replyMsg) {
		String flag = "N";
		logger.info("回调方法smsReplyCallBack开始......");
		logger.info("businessNo:" + businessNo + ",MOBILE:" + mobile 
				+ ",短信信息序列号ID:" + id + ",replyMsg:" + replyMsg);
		   
		try {
			replyMsg = replyMsg.trim();
			
			if (replyMsg.startsWith("1")) {
				replyMsg = "1";
			} else if (replyMsg.startsWith("2")) {
				replyMsg = "2";
			} else if (replyMsg.startsWith("3")) {
				replyMsg = "3";
			}
			
			if("1".equals(replyMsg) || "2".equals(replyMsg) || "3".equals(replyMsg)) {
				scheduleDAO.updatePosApplyBatch(businessNo, replyMsg);
				logger.error("客户回复的消息满足要求,将其写入表中！replyMsg:" + replyMsg);
			} else {
				logger.error("客户回复的消息不满足要求，将其过滤，不写入表中！replyMsg:" + replyMsg);
			}
			flag = "Y";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		logger.info("方法smsReplyCallBack结束......");
		
		return flag;
	}
}
