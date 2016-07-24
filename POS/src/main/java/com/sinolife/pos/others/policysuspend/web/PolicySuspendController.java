package com.sinolife.pos.others.policysuspend.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sinolife.pos.others.policysuspend.service.PolicySuspendService;

@Controller
public class PolicySuspendController {

	@Autowired
	PolicySuspendService suspendService;
	
	@RequestMapping("/others/policysuspend")
	public String entry(){
		return "/others/policysuspend/manual";
	}
	
	/**
	 * 查询保单号相关信息
	 * @param policyNoInput
	 * @return
	 */
	@RequestMapping("/others/policysuspend/query")
	public String policyQuery(String policyNoInput, Model model){
		
		model.addAttribute("policyInfo", suspendService.policyQuery(policyNoInput));
		
		return "/others/policysuspend/manual";
	}
	
	/**
	 * 设置或取消暂停
	 * @param policyNo
	 * @param clientNo
	 * @param suspendFlag
	 * @param suspendRemark
	 * @param posNo
	 * @param model
	 * @return
	 */
	@RequestMapping("/others/policysuspend/suspend")
	public String updateSuspend(String policyNo, String clientNo, String suspendFlag, String suspendRemark, String posNo, Model model){
		
		if("Y".equals(suspendFlag)){
			suspendService.cancelSuspend(policyNo, clientNo, suspendRemark, posNo);
			model.addAttribute("RETURN_MSG", policyNo+"保单人工暂停取消成功！");
			
		}else if("N".equals(suspendFlag)){
			suspendService.setSuspend(policyNo, clientNo, suspendRemark);
			model.addAttribute("RETURN_MSG", policyNo+"保单置人工暂停成功！");
		}
		
		return "/others/policysuspend/manual";
	}
	
}
