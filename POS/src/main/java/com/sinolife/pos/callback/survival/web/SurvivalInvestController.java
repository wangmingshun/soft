package com.sinolife.pos.callback.survival.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sinolife.pos.callback.survival.service.SurvivalInvestService;
import com.sinolife.sf.platform.runtime.PlatformContext;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
public class SurvivalInvestController {

	@Autowired
	SurvivalInvestService survivalService;
	
	@RequestMapping("/callback/survival")
	public String entry(){
		return "/callback/survival/input";
	}
	
	/**
	 * 从生存调查表
	 * 根据保单号查询被保人基本信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/callback/survival/query")
	public Map queryInsured(@RequestParam String policyNo){
		return survivalService.queryInsured(policyNo);
	}
	
	/**
	 * 提交录入的生存调查信息
	 * @param request
	 * @return
	 */
	@RequestMapping("/callback/survival/inputsubmit")
	public String inputSubmit(HttpServletRequest request,Model model){
		Map pMap = new HashMap();
		pMap.put("policyNo", request.getParameter("policyNo"));
		pMap.put("deathDate", request.getParameter("deathDate"));
		pMap.put("remark", request.getParameter("remark"));
		pMap.put("submitType", request.getParameter("submitType"));
		pMap.put("clientNo", request.getParameter("clientNo"));
		pMap.put("investgator", PlatformContext.getCurrentUser());
		
		survivalService.inputSubmit(pMap);
		
		model.addAttribute("RETURN_MSG", "数据录入成功！");
		return "/callback/survival/input";
	}
	
}
