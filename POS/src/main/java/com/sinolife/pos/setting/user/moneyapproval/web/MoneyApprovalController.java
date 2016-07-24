package com.sinolife.pos.setting.user.moneyapproval.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sinolife.pos.setting.user.moneyapproval.dto.PosAmountDaysPrivsDTO;
import com.sinolife.pos.setting.user.moneyapproval.service.MoneyApprovalService;

@Controller
public class MoneyApprovalController {

	@Autowired
	MoneyApprovalService moneyApprovalService;
	
	/**
	 * 入口.
	 * @return
	 */
	@RequestMapping("/setting/user/moneyapproval")
	public String entry(Model model){
		
		model.addAttribute("posAmountDaysPrivs", new PosAmountDaysPrivsDTO()); 
		return "/setting/user/moneyapproval/set";
	}
	
	/************
	 * 提交设置
	 * @return
	 */
	@RequestMapping("/setting/user/moneyApprovalSet")
	public String moneyApprovalSet(PosAmountDaysPrivsDTO amountDaysDTO, Model model){
		
		moneyApprovalService.moneyApprovalSet(amountDaysDTO);
		
		model.addAttribute("RETURN_MSG", "设置成功！");
		return "/setting/user/moneyapproval/set";
	}

	/**
	 * 查询已有设置
	 * @param amountDaysGrade
	 * @param serviceItem
	 * @param acceptChannel
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/setting/user/moneyapproval/query")
	@ResponseBody
	public Map querySet(String amountDaysGrade,String serviceItem, String acceptChannel){
		return moneyApprovalService.querySet(amountDaysGrade, serviceItem, acceptChannel);
	}
	
}
