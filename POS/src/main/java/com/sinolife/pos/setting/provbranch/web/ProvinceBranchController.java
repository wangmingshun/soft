package com.sinolife.pos.setting.provbranch.web;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sinolife.pos.setting.provbranch.service.ProvinceBranchService;

/**
 * 此功能由渠道系统实现，job将渠道的数据导过来写在表pos_branch_in_province就是了
 * 代码摆在这里没删而已
 */
@SuppressWarnings("rawtypes")
@Controller
public class ProvinceBranchController {

	@Autowired
	ProvinceBranchService provService;
	
	/**
	 * 入口.
	 * @return
	 */
	@RequestMapping("/setting/provbranch")
	public String entry(){
		return "/setting/provbranch/maintain";
	}
	
	/**
	 * 根据省代码查询所拥有的机构
	 * @param province
	 * @param model
	 * @return
	 */
	@RequestMapping("/setting/provbranch/provincequery")
	public String query(@RequestParam("queryProvince")String province, Model model){
		
		List list = provService.query(province);
		if(list!=null&&list.size()>0){
			model.addAttribute("branchList", list);
			model.addAttribute("queryProvince", province);
		}else{
			model.addAttribute("RETURN_MSG", "该省下暂无对应机构信息");
		}
		
		return "/setting/provbranch/maintain";
	}

	/**
	 * 保存新增的信息
	 * @param jsonStr
	 * @return
	 */
	@RequestMapping("/setting/provincebranch/save")
	@ResponseBody
	public String saveAdd(String addInfo){
		
		List addList = (List)JSONArray.toCollection(JSONArray.fromObject(addInfo), HashMap.class);
		
		provService.saveAdd(addList);
		
		return "Y";
	}
	
	/**
	 * 删除页面上选中的已有信息
	 * 根据主键置有效标志为N
	 * @param pks
	 * @return
	 */
	@RequestMapping("/setting/provincebranch/delete")
	@ResponseBody
	public String delete(String pks){
		
		provService.delete(pks);
		
		return "Y";
	}
	
	/**
	 * 修改已有的信息
	 * @param jsonStr
	 * @return
	 */
	@RequestMapping("/setting/provincebranch/update")
	@ResponseBody
	public String update(String updateInfo){
		
		List updateList = (List)JSONArray.toCollection(JSONArray.fromObject(updateInfo), HashMap.class);
		
		provService.update(updateList);
		
		return "Y";
	}

	/**
	 * 导出某省的查询结果
	 * 需求也没说，没实现
	 * @return
	 */
	@RequestMapping("/setting/provincebranch/excel")
	public void excel(@RequestParam("queryProvince")String province, HttpServletResponse response){
		response.resetBuffer();
		response.setContentType("application/vnd.ms-excel");
		response.addHeader("Content-Disposition", "attachment;filename=\"" + "data.xls" + "\"");
	}
	
}
