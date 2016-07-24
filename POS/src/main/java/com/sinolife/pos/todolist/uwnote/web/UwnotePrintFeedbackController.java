package com.sinolife.pos.todolist.uwnote.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sinolife.pos.acceptance.branch.service.BranchAcceptService;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.todolist.uwnote.service.UwnotePrintFeedbackService;
import com.sinolife.sf.platform.runtime.PlatformContext;

/**
 * 核保函打印、回销
 *
 */
@SuppressWarnings({ "rawtypes" })
@Controller
public class UwnotePrintFeedbackController {

	@Autowired
	UwnotePrintFeedbackService uwnoteService;
	
	@Autowired
	BranchAcceptService acceptService;
	
	/**
	 * 批查询出来逐单打印的入口
	 */
	@RequestMapping("/todolist/uwnote/print")
	public String printEntry(){
		return "/todolist/uwnote/print";
	}
	
	/**
	 * 回销入口
	 */
	@RequestMapping("/todolist/uwnote/feedback")
	public String feedbackEntry(){
		return "/todolist/uwnote/feedback";
	}
	
	/**
	 * 更新detail表里的核保函打印次数
	 * @param posNo
	 * @return
	 */
	@RequestMapping("/todolist/uwnotePrint/updateTimes")
	@ResponseBody
	public String updateDetailPrintTimes(String posNo){
		
		uwnoteService.updateDetailPrintTimes(posNo);
		
		return "Y";
	}
	
	/**
	 * 查询可以打印核保函的受理列表
	 * @param model
	 * @return
	 */
	@RequestMapping("/todolist/uwnotePrint/query")
	public String queryForPrintList(@RequestParam String startDate,@RequestParam String endDate, Model model){
		List list = uwnoteService.queryForPrintList(startDate, endDate, PlatformContext.getCurrentUser());
		if(list!=null && list.size()>0){
			model.addAttribute("forUwnoteList", list);
			model.addAttribute("UW_URL", PosUtils.getPosProperty("uwUrl"));
		}else{
			model.addAttribute("RETURN_MSG", "没有符合条件的数据");
		}
		
		return "/todolist/uwnote/print";
	}
	
	/**
	 * 核保函回销 根据保全号查询保全信息，能回销才能查出记录
	 */
	@RequestMapping("/todolist/uwnoteFeedback/query")
	public String feedbackQueryPosInfo(@RequestParam String posNo, Model model){
		List list = uwnoteService.feedbackQueryPosInfo(posNo, PlatformContext.getCurrentUser());
		if(list!=null && list.size()>0){
			model.addAttribute("posInfo", list);
		}else{
			model.addAttribute("RETURN_MSG", "没有符合条件的数据。");
		}
		
		return "/todolist/uwnote/feedback";
	}
	
	/**
	 * 核保函回销提交
	 */
	@RequestMapping("/todolist/uwnoteFeedback/submit")
	@Transactional(propagation = Propagation.REQUIRED)
	public String feedbackSubmit(String feedbackPosNo, String taskType, String feedbackResult, Model model){

		uwnoteService.insertAcceptDetail(feedbackPosNo, feedbackResult);
		uwnoteService.undwrtNoteWriteOff(feedbackPosNo, taskType, feedbackResult);
		
		acceptService.workflowControl("13", feedbackPosNo, false);
		
		model.addAttribute("RETURN_MSG", "回销意见提交成功！");
		return "/todolist/uwnote/feedback";
	}
	
}
