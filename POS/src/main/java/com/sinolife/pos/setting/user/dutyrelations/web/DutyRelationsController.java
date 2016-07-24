package com.sinolife.pos.setting.user.dutyrelations.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sinolife.pos.setting.user.dutyrelations.service.DutyRelationsService;

/**
 * 页面已去掉了职级等级对照关系的菜单功能
 * 代码摆在这里没删而已
 *
 */
@SuppressWarnings({"rawtypes","unchecked"})
@Controller
public class DutyRelationsController {

	@Autowired
	DutyRelationsService drService;
	
	/**
	 * 入口.
	 * @return
	 */
	@RequestMapping("/setting/user/dutyrelations/set")
	public String entry(){
		return "/setting/user/dutyrelations/set";
	}
	
	/***
	 * 根据职级带出对应的6种信息
	 * @return
	 */
	@RequestMapping("/setting/dutyrelations/queryInfoByRank")
	public String queryInfoByRank(@RequestParam("rankSel") String rank, Model model){
		Map map = drService.queryInfoByRank(rank);
		map.put("RANK", rank);
		model.mergeAttributes(map);
		return "/setting/user/dutyrelations/set";
	}
	
	/***
	 * 设置
	 * @return
	 */
	@RequestMapping("/setting/user/setDutyRelations")
	@ResponseBody
	public String setDutyRelations(HttpServletRequest request){
		Map pMap = new HashMap();
		pMap.put("rank", request.getParameter("rank"));
		pMap.put("inputGrade", request.getParameter("inputGrade"));
		pMap.put("amountGrade", request.getParameter("amountGrade"));
		pMap.put("difPlace", request.getParameter("difPlace"));
		pMap.put("ruleSpec", request.getParameter("ruleSpec"));
		pMap.put("rollbackAprov", request.getParameter("rollbackAprov"));
		pMap.put("inputSpecials", request.getParameter("inputSpecials").split(";"));
		pMap.put("aprovSpecials", request.getParameter("aprovSpecials").split(";"));
		
		drService.setDutyRelations(pMap);
		
		return "Y";
	}
	
}
