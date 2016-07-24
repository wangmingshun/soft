package com.sinolife.pos.setting.counter.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sinolife.pos.common.include.service.IncludeService;
import com.sinolife.pos.common.util.Excel;
import com.sinolife.pos.setting.counter.service.CounterSetService;
import com.sinolife.sf.platform.runtime.PlatformContext;

@SuppressWarnings("rawtypes")
@Controller
public class CounterSetController {

	@Autowired
	CounterSetService counterService;
	
	@Autowired
	IncludeService includeService;
	
	/**
	 * 入口.
	 * @return
	 */
	@RequestMapping("/setting/counter")
	public String entry(Model model) {
		
		model.addAttribute("userBranchCode", includeService.userBranchCode(PlatformContext.getCurrentUser()));
		
		return "/setting/counter/set";
	}
	
	/***********************************
	 * 查询某机构(branchNo)下的柜面信息
	 */
	@RequestMapping(value = "/setting/counter/query")
	@ResponseBody
	public List queryCounterInfoList(String branchNo, String sonBranch){
		return counterService.queryCounterInfoList(branchNo, sonBranch);
	}
	
	/***********************************
	 * 根据主键删除某些柜面信息
	 * 实际操作是置上关闭日期update
	 */
	@RequestMapping(value = "/setting/counter/delete")
	@ResponseBody
	public String deleteCounterInfo(String counterNos){
		counterService.deleteCounterInfo(counterNos.split(";"));
		return "Y";
	}
	
	/***********************************
	 * 根据主键更新某些柜面信息
	 * @return
	 */
	@RequestMapping(value = "/setting/counter/update")
	@ResponseBody
	public String updateCounterInfo(String counterNewInfo){
		List pList = (List)JSONArray.toCollection(JSONArray.fromObject(counterNewInfo), HashMap.class);
		counterService.updateCounterInfo(pList);
		return "Y";
	}
	
	/***********************************
	 * 添加柜面信息
	 * @return
	 */
	@RequestMapping(value = "/setting/counter/add")
	@ResponseBody
	public String saveAddCounterInfo(String newCounterInfo){
		List pList = (List)JSONArray.toCollection(JSONArray.fromObject(newCounterInfo), HashMap.class);
		counterService.saveAddCounterInfo(pList);
		return "Y";
	}
	
	/***********************************
	 * 导出柜面信息
	 * @param model
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "/setting/counter/export")
	public void exportCounterInfo(@RequestParam("branchTxt0")String branchNo, @RequestParam("sonBranch")String sonBranch, HttpServletResponse response) throws IOException{
		
		List<String> head = new ArrayList<String>();
		head.add("柜面所属机构");
		head.add("柜面名称");
		head.add("柜面类型");
		head.add("柜面负责人");
		head.add("柜面电话");
		head.add("柜面传真");
		head.add("柜面地址");
		head.add("柜面邮编");
		head.add("柜面营业时间");
		List<String> key = new ArrayList<String>();
		key.add("COUNTER_BRANCH");
		key.add("COUNTER_NAME");
		key.add("COUNTER_TYPE");
		key.add("COUNTER_DIRECTOR");
		key.add("COUNTER_PHONE");
		key.add("COUNTER_FAX");
		key.add("COUNTER_ADDR");
		key.add("COUNTER_POST");
		key.add("OPEN_TIME");
		
		List data = counterService.queryCounterInfoList(branchNo, sonBranch);

		File file = new File(Excel.listToExcel(head, key, data));
		int i;
		response.setContentType("application/vnd.ms-excel");
		response.addHeader("Content-Disposition", "attachment;filename=\""+ URLEncoder.encode("柜面信息清单.xls", "UTF-8")+"\"");
		FileInputStream fis = new FileInputStream(file);
		ServletOutputStream out = response.getOutputStream();
		while ((i = fis.read()) != -1) {
			out.write(i);
		}
		fis.close();
		out.close();
		file.delete();
	}
	
}
