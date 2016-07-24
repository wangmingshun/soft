package com.sinolife.pos.others.drawtoprepay.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sinolife.pos.others.drawtoprepay.service.DrawToPrepayService;

@SuppressWarnings({ "rawtypes" })
@Controller
public class DrawToPrepayController {

	@Autowired
	private DrawToPrepayService prepayService;
	
	/**
	 * 入口
	 * @return
	 */
	@RequestMapping("/others/drawtoprepay")
	public String entry(){
		return "/others/drawtoprepay/operate";
	}
	
	/**
	 * 根据输入保单查询保单信息
	 * @param policyNo
	 * @param model
	 * @return
	 */
	@RequestMapping("/others/drawtoprepay/query")
	public String query(@RequestParam("policyNoValue")String policyNo,Model model){
		Map map = prepayService.query(policyNo);
		
		if(map==null||map.size()<1){
			model.addAttribute("RETURN_MSG", "无该保单的信息，请核实后重新查询！");	
		}else{
			model.addAttribute("policyInfo", map);
		}
		return "/others/drawtoprepay/operate";
	}
	
	/**
	 * 操作转出上一期保费
	 * @param policyNo
	 * @param model
	 * @return
	 */
	@RequestMapping("/others/drawtoprepay/operate")
	@Transactional(propagation = Propagation.REQUIRED)
	public String operate(String policyNo,String premSum, Model model){

		prepayService.operate(policyNo, premSum);
		prepayService.costRollback(policyNo);
		
		model.addAttribute("RETURN_MSG", "操作成功！");
		return "/others/drawtoprepay/operate";
	}
}
