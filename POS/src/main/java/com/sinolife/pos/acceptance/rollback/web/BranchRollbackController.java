package com.sinolife.pos.acceptance.rollback.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sinolife.pos.acceptance.branch.service.BranchAcceptService;
import com.sinolife.pos.acceptance.rollback.dto.RollBackInputDTO;
import com.sinolife.pos.acceptance.rollback.service.BranchRollbackService;

/**
 * 机构端受理撤销
 *
 */
@Controller
public class BranchRollbackController {

	@Autowired
	BranchRollbackService rollbackService;
	
	@Autowired
	BranchAcceptService  acptService;
	
	/**
	 * 入口
	 * 或者是
	 * 撤销或回退结果展示页面点击取消
	 * @return
	 */
	@RequestMapping("/acceptance/rollback/entry")
	public String entry(){
		return "/acceptance/rollback/posInfoQuery";
	}
	
	/*********************************
	 * 根据posNo查询可供撤销回退的批单
	 * 若不能回退，返回不能回退的原因
	 * @return
	 */
	@RequestMapping("/acceptance/rollback/queryByPosNo")
	public String queryByPosNo(@RequestParam("posNo")String posNo,Model model){
		
		model.addAttribute("posInfo",rollbackService.queryByPosNo(posNo));
		
		return "/acceptance/rollback/posInfoQuery";
	}
	
	/*********************
	 * 确认要撤销或回退
	 * @return
	 */
	@RequestMapping("/acceptance/rollback/process")
	public String process(@RequestParam("posNoHid")String posNo,Model model){
		
		model.addAttribute("posInfo", rollbackService.process(posNo));

		return "/acceptance/rollback/resultDisplay";
	}
	
	/**********************************
	 * 撤销或回退结果展示页面点击确定提交
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/acceptance/rollback/submit")
	@Transactional(propagation = Propagation.REQUIRED)
	public String submit(RollBackInputDTO rollbackDTO, Model model){
		//提交数据
		String posNo = rollbackService.submit(rollbackDTO);
		//流转状态
		Map rstMap = acptService.workflowControl("03", posNo, true);
		if("A19".equals(rstMap.get("flag"))){
			rstMap = acptService.workflowControl("05", posNo, true);
		}
		model.addAttribute("RESULT", rstMap);
		
		return "/acceptance/rollback/resultDisplay";
	}

}
