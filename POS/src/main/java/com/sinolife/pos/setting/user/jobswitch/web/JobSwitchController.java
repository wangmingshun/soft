package com.sinolife.pos.setting.user.jobswitch.web;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.rpc.um.PosUMService;
import com.sinolife.pos.setting.user.dutychange.service.DutyChangeService;
import com.sinolife.pos.setting.user.jobswitch.service.JobSwitchService;
import com.sinolife.sf.framework.email.Mail;
import com.sinolife.sf.framework.email.MailService;
import com.sinolife.sf.framework.email.MailType;
import com.sinolife.sf.platform.runtime.PlatformContext;

@SuppressWarnings({"rawtypes","unchecked"})
@Controller
public class JobSwitchController {

	@Autowired
	JobSwitchService jobsService;

	@Autowired
	DutyChangeService changeService;
	
	@Autowired
	PosUMService umService;
	
	@Autowired @Qualifier("mailService01")
	MailService mailService;
	
	Logger logger = Logger.getLogger(getClass());
	
	/**
	 * 设置页面入口.
	 * @return
	 */
	@RequestMapping("/setting/user/jobswitch/set")
	public String setEntry() {
		return "/setting/user/jobswitch/set";
	}
	/**
	 * 确认交接任务页面入口
	 * 查询出待确认任务和工作类型列表
	 * @return
	 */
	@RequestMapping("/setting/user/jobswitch/confirm")
	public String confirmEntry(Model model){
		String userName = PlatformContext.getCurrentUser();
		
		Map rstMap = jobsService.jobSwitchForConfirm(userName);

		model.addAttribute("USER", userName);
		model.mergeAttributes(rstMap);
		
		return "/setting/user/jobswitch/confirm";
	}

	/***********************************
	 * 查询某用户下的工作交接相关信息
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/jobswitch/queryjobsinfo")
	public String queryJobSwitchInfo(@RequestParam("userIdInput")String userId, Model model){
		userId = PosUtils.parseInputUser(userId);
		String curUser = PlatformContext.getCurrentUser();
		
		if(umService.isPrivsManager(curUser) || changeService.selfOrUnderling(curUser, userId)){

			model.addAttribute("taskList",jobsService.queryTaskListForSwitch(userId));
			model.addAttribute("confirmList",jobsService.queryTaskListConfirming(userId));
			model.addAttribute("workStatus",jobsService.queryWorkStatusForSwitch(userId));
			model.addAttribute("USER_ID", userId);
			
		}else{
			model.addAttribute("RETURN_MSG", userId+"是您的上级或与您不存在上下级关系，您不是权限管理员不能进行交接操作");
		}
		return "/setting/user/jobswitch/set";
	}
	
	/**
	 * 取消用户所交接的工作类型
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/jobswitch/workcancel")
	public String cancelWorkSwitch(@RequestParam("userIdInput")String userId, Model model){
		userId = PosUtils.parseInputUser(userId);
		
		jobsService.cancelWorkSwitch(userId);
		
		model.addAttribute("RETURN_MSG", "取消成功！");
		return "/setting/user/jobswitch/set"; 
	}
	
	/***********************************
	 * 将选中的工作任务交接给指定用户
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/jobswitch/tasktosomeguy")
	public String switchTaskToUser(HttpServletRequest request, Model model){
		String undertaker = PosUtils.parseInputUser(request.getParameter("undertaker"));
		String[] pos = request.getParameterValues("pkPosNo");
		String[] approve = request.getParameterValues("pkApprove");
		String[] problem = request.getParameterValues("pkProblem");
		Map pMap = new HashMap();
		pMap.put("handover", request.getParameter("handover"));//这个是隐藏域不用检查输入用户了
		pMap.put("undertaker", undertaker);
		
		jobsService.switchTaskToUser(pos, approve, problem, pMap);
		
		email2Confirm(undertaker);

		model.addAttribute("RETURN_MSG", "任务交接成功！");
		return queryJobSwitchInfo(request.getParameter("handover"), model);//返回页面时重新查询一次
	}
	
	/***********************************
	 * 将选中的工作类型交接给指定用户
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/jobswitch/worktosomeguy")
	public String switchWorkToUser(HttpServletRequest request, Model model){
		String undertaker = PosUtils.parseInputUser(request.getParameter("undertaker"));
		Map pMap = new HashMap();
		pMap.put("handover", request.getParameter("handover"));
		pMap.put("undertaker", undertaker);
		pMap.put("cause", request.getParameter("cause"));
		pMap.put("endDate", request.getParameter("endDate"));
		pMap.put("works", request.getParameterValues("workType"));
		
		jobsService.switchWorkToUser(pMap);
		
		email2Confirm(undertaker);
		
		model.addAttribute("RETURN_MSG", "工作交接成功！");
		return "/setting/user/jobswitch/set";
	}
	
	/***************************************
	 * 接受or拒绝页面选中的任务和工作交接给我
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/jobswitch/confirm")
	public String confirm(HttpServletRequest request,Model model){
		String status = request.getParameter("handoverStatus");
		String[] tasks = request.getParameterValues("taskPK");
		String[] works = request.getParameterValues("workPK");
		
		jobsService.confirm(tasks, works, status);
		
		confirmedEmail(jobsService.queryHandoversByPK(tasks, works),status);
		
		if("3".equals(status)){
			model.addAttribute("RETURN_MESSAGE", "工作交接已拒绝");			
		}else{
			model.addAttribute("RETURN_MESSAGE", "工作交接确认成功");	
		}
		
		return "/setting/user/jobswitch/confirm";
	}
	
	/**
	 * 比较交接人与承接人审批等级，只能交接给平级或更高级
	 * @param undertaker
	 * @return
	 */
	@RequestMapping(value = "/jobswitch/amountDaysGradeCheck")
	@ResponseBody
	public String amountDaysGradeCheck(String handover, String undertaker){
		if(umService.isPrivsManager(PlatformContext.getCurrentUser())){
			return "Y";
		}
		return jobsService.amountDaysGradeCheck(PosUtils.parseInputUser(handover), PosUtils.parseInputUser(undertaker));
	}

	/**
	 * 交接给你，给你发一封邮件
	 * @param undertaker
	 */
	private void email2Confirm(String undertaker){
		try {
			Mail mail = new Mail();		
			mail.setSubject("[SL_POS]保全工作交接待确认通知");	//邮件标题
			mail.setTo(new String[]{undertaker});				//收件人
			mail.setForm("sl_pos_mail_admin@sino-life.com");	//发件人
			mail.setMailType(MailType.HTML_CONTENT);			//邮件类型
			mail.setContent(new StringBuilder("<html><body style=\"font-size:14px;text-align:left;\">")
				.append("您好，您的工作交接任务中有待确认的工作交接任务，请及时确认处理。 ")
				.append("<br/><br/><br/><br/><br/><br/><br/><br/>")
				.append("<div style=\"color:gray;font-size:12px;\">本邮件由系统生成，请勿回复</div></body></html>")
				.toString());									//邮件内容
			mailService.send(mail);
		} catch (Exception e) {
			logger.error("给"+undertaker+"发工作交接待确认邮件失败",e);
		}
	}
	
	/**
	 * 你交接给我，我接受or拒绝时，给你发一封邮件
	 */
	private void confirmedEmail(String[] handovers, String status){
		try {
			if(handovers==null || handovers.length<1){
				logger.info("confirmedEmailNotSend"+new Date());
				return;
			}
			Mail mail = new Mail();		
			mail.setSubject("[SL_POS]保全工作交接确认结果通知");	//邮件标题
			mail.setTo(handovers);				                //收件人
			mail.setForm("sl_pos_mail_admin@sino-life.com");	//发件人
			mail.setMailType(MailType.HTML_CONTENT);			//邮件类型
			mail.setContent(new StringBuilder("<html><body style=\"font-size:14px;text-align:left;\">")
				.append("您好，您交接给")
				.append(PlatformContext.getCurrentUser())
				.append("的工作任务，已被")
				.append("2".equals(status)?"接受。":"拒绝。")
				.append("<br/><br/><br/><br/><br/><br/><br/><br/>")
				.append("<div style=\"color:gray;font-size:12px;\">本邮件由系统生成，请勿回复</div></body></html>")
				.toString());									//邮件内容
			mailService.send(mail);
		} catch (Exception e) {
			logger.error("给"+handovers[0]+"等人发工作交接确认结果邮件失败",e);
		}
	}
	
}
