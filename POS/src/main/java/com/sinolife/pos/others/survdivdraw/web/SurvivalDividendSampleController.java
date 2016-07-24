package com.sinolife.pos.others.survdivdraw.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sinolife.pos.others.survdivdraw.service.SurvivalDividendSampleService;

/**
 * 生存金/红利逐单抽档
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
public class SurvivalDividendSampleController {

	@Autowired
	SurvivalDividendSampleService sampleService;
	
	/**
	 * 入口
	 * @return
	 */
	@RequestMapping("/others/survdiv")
	public String entry(){
		return "/others/survdiv/sample";
	}
	
	/**
	 * 查询保单对应的生存/红利险种列表
	 * @param policyNo
	 * @return
	 */
	@RequestMapping("/others/survdiv/queryproduct")
	@ResponseBody
	public List queryProductInfo(String policyNo, String type){
		return sampleService.queryProductInfo(policyNo, type);
	}
	
	/**
	 * 提交抽档
	 * @return
	 */
	@RequestMapping("others/survdiv/sample")
	public String submitSample(HttpServletRequest request,Model model){
		Map pMap = new HashMap();
		pMap.put("sampleType", request.getParameter("sampleType"));//抽档类型
		pMap.put("policyNo", request.getParameter("policyNo"));//保单号
//		pMap.put("prodSeq", request.getParameter("prodSeq"));//险种序号
		pMap.put("dueMonth", request.getParameter("dueMonth"));//应领月份
		
		sampleService.submitSample(pMap);
		if("N".equals(pMap.get("flag"))){
			model.addAttribute("RETURN_MSG", pMap.get("message"));
		}else
			model.addAttribute("RETURN_MSG", "提交成功！");
		return "/others/survdiv/sample";
	}
	
}
