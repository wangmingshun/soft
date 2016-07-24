package com.sinolife.pos.setting.user.acceptlevel.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sinolife.pos.setting.user.acceptlevel.service.AcceptLevelSetService;

@SuppressWarnings({"unchecked","rawtypes"})
@Controller
public class AcceptLevelSetController {

	
	@Autowired
	AcceptLevelSetService acptLevelService;
	/**
	 * 入口.
	 * @return
	 */
	@RequestMapping("/setting/user/acceptlevel/set")
	public String entry() {
		return "/setting/user/acceptlevel/set";
	}
	
	/*******************************************
	 * 查询所选等级的信息，即该等级能录入的保全项目
	 * @return
	 */
	@RequestMapping("/queryAcceptLevelInfo")
	public String queryAcceptLevelInfo(@RequestParam("gradeSelect") String level, Model model){
		List list = acptLevelService.queryAcceptLevelInfo(level);
		
		model.addAttribute("SETED_PRIVS", list);
		model.addAttribute("LEVEL", level);
		
		return "/setting/user/acceptlevel/set";
	}
	
	/*******************************************
	 * 设置录入等级信息
	 * @return
	 */
	@RequestMapping("/setAcceptLevel")
	@ResponseBody
	public String setAcceptLevel(String serviceItems, String gradeSelect){
		Map pMap = new HashMap();
		pMap.put("items", serviceItems);
		pMap.put("grade", gradeSelect);
		acptLevelService.setAcceptLevel(pMap);
		return "Y";
	}
	
}
