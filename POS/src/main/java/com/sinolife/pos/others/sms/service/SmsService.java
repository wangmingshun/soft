package com.sinolife.pos.others.sms.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.others.sms.dao.SmsDAO;

@Service("smsService")
public class SmsService {
	@Autowired
	SmsDAO smsDao;

	@Transactional(propagation = Propagation.REQUIRED)
	public int sendSMS(String branchCode, Date startDate, Date endDate,
			String channelType, String productCode, String vip,
			String forceSend, Date arrangeTime,String mobileType, String sendText) {
		return smsDao.sendSMS(branchCode, startDate, endDate, channelType,
				productCode, vip, forceSend, arrangeTime, mobileType,sendText);
	}
}
