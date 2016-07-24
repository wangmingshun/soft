package com.sinolife.pos.setting.holiday.web;

import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sinolife.pos.setting.holiday.service.HolidaySetService;

@SuppressWarnings("rawtypes")
@Controller
public class HolidaySetController {
	
	@Autowired
	HolidaySetService holidayService;
	/**
	 * 入口.
	 * @return
	 */
	@RequestMapping("/setting/holiday")
	public String entry(){
		return "/setting/holiday/set";
	}

	/**
	 * 按年查询数据
	 */
	@RequestMapping("/setting/holiday/query")
	public String yearDataQuery(@RequestParam("year")String year,Model model){
		
		model.addAttribute("vacationList", holidayService.yearDataQuery(year));
		
		return "/setting/holiday/set";
	}
	
	/**
	 * 保存设置
	 * @return
	 */
	@RequestMapping("/setting/holiday/save")
	public String save(@RequestParam("setResult")String setResult,Model model){
		List pList = (List)JSONArray.toCollection(JSONArray.fromObject(setResult), HashMap.class);
		holidayService.save(pList);
		
		model.addAttribute("RETURN_MSG", "设置成功！");
		return "/setting/holiday/set";
	}
}
