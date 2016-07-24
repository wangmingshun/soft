package com.sinolife.pos.others.sms.web;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import net.unihub.framework.util.common.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sinolife.pos.common.include.service.IncludeService;
import com.sinolife.pos.others.sms.service.SmsService;
import com.sinolife.sf.platform.runtime.PlatformContext;

@Controller("smsController")
public class SmsController {
	@Autowired
	IncludeService includeService;

	@Autowired
	SmsService smsService;

	@RequestMapping("/others/sms")
	public String entry(Model model) {

		model.addAttribute("userBranchCode",
				includeService.userBranchCode(PlatformContext.getCurrentUser()));

		return "/others/sms/smsSend";
	}

	@RequestMapping("/others/sendSms")
	public String sendSMS(Model model, HttpServletRequest request) {

		String branchCode = (String) request.getParameter("branchCode");
		Date startDate = DateUtil.stringToDate(request
				.getParameter("startDate"));

		Date endDate = DateUtil.stringToDate(request.getParameter("endDate"));
		String channelType = (String) request.getParameter("channelType");
		String productName = (String) request.getParameter("productName");
		String productCode=null;
		if(productName!=null&& productName.length()>0){
		int st=productName.lastIndexOf("(");
		int sd=productName.lastIndexOf(")");		
		 productCode=productName.substring(st+1, sd);
		}		
		String vip = (String) request.getParameter("vip");
		String forceSend = (String) request.getParameter("forceSend");
		String sendDay = (String) request.getParameter("sendDay");
		String mobileType=(String) request.getParameter("mobileType");
		
		Date arrangeTime = DateUtil.stringToDate(sendDay+":00","yyyy-MM-dd HH:mm:ss");
		String sendText = (String) request.getParameter("sendText");

		int count=smsService.sendSMS(branchCode, startDate, endDate, channelType,
				productCode, vip, forceSend, arrangeTime,mobileType, sendText);
		
		model.addAttribute("count", count);
         
		return "forward:/others/sms";

	}
}
