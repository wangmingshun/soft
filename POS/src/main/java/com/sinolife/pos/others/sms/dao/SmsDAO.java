package com.sinolife.pos.others.sms.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@Repository("smsDAO")
@Transactional(propagation = Propagation.REQUIRED)
public class SmsDAO extends AbstractPosDAO {

	public int sendSMS(String branchCode, Date startDate, Date endDate,
			String channelType, String productCode, String vip,
			String forceSend, Date arrangeTime,String mobileType, String sendText) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("branchCode", branchCode);
		paraMap.put("productCode", productCode);
		paraMap.put("startDate", startDate);
		paraMap.put("endDate", endDate);
		paraMap.put("channelType", channelType);
		paraMap.put("forceSend", forceSend);
		paraMap.put("vip", vip);
		paraMap.put("arrangeTime", arrangeTime);
		paraMap.put("mobileType", mobileType);
		paraMap.put("sendText", sendText);

		queryForObject("sendBatchSms", paraMap);

		if ("1".equals(paraMap.get("flag"))) {
			throw new RuntimeException("根据条件发批量送短信失败："
					+ paraMap.get("message"));
		} else {

			return ((java.math.BigDecimal) paraMap.get("count")).intValue();
		}
	}
	
	
	/**
	 * 移动平台客户告知短信
	 * @param posNo
	 * @param indicator
	 * @return
	 */
	public Map<String,Object> sendMposSMS(String posNo, String indicator) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("posNo", posNo);
		paraMap.put("indicator", indicator);
		
		queryForObject("sendMposSms", paraMap);

		if ("1".equals(paraMap.get("flag"))) {
			throw new RuntimeException("移动平台客户告知短信："
					+ paraMap.get("message"));
		} else {

			return paraMap;
		}
	}
	
	
	/**
	 * 移动平台审批不通过通知代理人短信
	 * @param posNo
	 * @param indicator
	 * @param  question 不通过原因
	 * @return
	 */
	public Map<String,Object> sendAgentMposSms(String posNo, String indicator,String question) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("posNo", posNo);
		paraMap.put("indicator", indicator);
		paraMap.put("question", question);
		queryForObject("sendAgentMposSms", paraMap);

		if ("1".equals(paraMap.get("flag"))) {
			throw new RuntimeException("移动平台客户告知短信："
					+ paraMap.get("message"));
		} else {

			return paraMap;
		}
	}
}
