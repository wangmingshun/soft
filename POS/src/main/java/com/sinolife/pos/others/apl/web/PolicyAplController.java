package com.sinolife.pos.others.apl.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sinolife.pos.others.apl.service.PolicyAplService;

@Controller
public class PolicyAplController {

	@Autowired
	PolicyAplService aplService;
	
	/**
	 * 入口
	 * @return
	 */
	@RequestMapping("/others/apl/single")
	public String entry(){
		return "/others/apl/single";
	}
	
	/**
	 * 录入保单号提交
	 * @param policyNo
	 * @return
	 */
	@RequestMapping("/others/apl/policySubmit")
	public String policySubmit(String policyNo, Model model){
		
		model.addAttribute("RETURN_MSG", aplService.policySubmit(policyNo));
		
		return "/others/apl/single";
	}
}
