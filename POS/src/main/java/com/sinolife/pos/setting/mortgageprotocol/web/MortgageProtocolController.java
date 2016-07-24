package com.sinolife.pos.setting.mortgageprotocol.web;

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
import com.sinolife.pos.setting.mortgageprotocol.service.MortgageProtocolService;

/**
 * 质押贷款协议维护
 * 此功能由外系统操作页面，pos没有对应的菜单链接
 */
@SuppressWarnings("rawtypes")
@Controller
public class MortgageProtocolController {

	@Autowired
	MortgageProtocolService protocolService;
	
	@Autowired
	IncludeService includeService;
	
	@RequestMapping("/setting/mortgageprotocol")
	public String entry(Model model){
		
		model.addAttribute("products", JSONArray.fromObject(includeService.productsList()).toString());
		
		return "/setting/mortgageprotocol/maintain";
	}
	
	/**
	 * 按机构查询已有协议信息
	 * @param queryBranch
	 * @return
	 */
	@RequestMapping("/setting/mortgageprotocol/query")
	public String query(@RequestParam String queryBranch,Model model){
		
		List list = protocolService.query(queryBranch);
		
		if(list!=null&&list.size()>0){
			model.addAttribute("protocolList", list);
			model.addAttribute("queryBranch", queryBranch);
		}else{
			model.addAttribute("RETURN_MSG", "该机构下无相关协议信息");
		}
		
		model.addAttribute("products", JSONArray.fromObject(includeService.productsList()).toString());
		
		return "/setting/mortgageprotocol/maintain";
	}
	
	/**
	 * 保存新增的信息
	 * @param jsonStr
	 * @return
	 */
	@RequestMapping("/setting/mortgageprotocol/save")
	@ResponseBody
	public String saveAdd(String addInfo){
		
		List addList = (List)JSONArray.toCollection(JSONArray.fromObject(addInfo), HashMap.class);
		
		protocolService.saveAdd(addList);
		
		return "Y";
	}
	
	/**
	 * 删除页面上选中的已有信息
	 * 根据主键置有效标志为N
	 * @param protocolCodes
	 * @return
	 */
	@RequestMapping("/setting/mortgageprotocol/delete")
	@ResponseBody
	public String delete(String protocolCodes){
		
		protocolService.delete(protocolCodes);
		
		return "Y";
	}
	
	/**
	 * 修改已有的信息
	 * 根据业务逻辑，只能修改协议截止日期
	 * @param jsonStr
	 * @return
	 */
	@RequestMapping("/setting/mortgageprotocol/update")
	@ResponseBody
	public String update(String updateInfo){
		
		List updateList = (List)JSONArray.toCollection(JSONArray.fromObject(updateInfo), HashMap.class);
		
		protocolService.update(updateList);
		
		return "Y";
	}
	
	/**
	 * 导出某机构的查询结果
	 * @param branch
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/setting/mortgageprotocol/excel")
	public void excel(@RequestParam("queryBranch")String branch, HttpServletResponse response) throws IOException{
		List<String> head = new ArrayList<String>();
		head.add("协议机构代码");
		head.add("协议机构名称");
		head.add("协议银行代码");
		head.add("协议银行名称");
		head.add("协议签订日期");
		head.add("协议截止日期");
		head.add("协议险种");
		List<String> key = new ArrayList<String>();
		key.add("BRANCH_CODE");
		key.add("BRANCH_NAME");
		key.add("DEPT_NO");
		key.add("DEPT_FULL_NAME");
		key.add("START_DATE");
		key.add("END_DATE");
		key.add("PRODUCT_NAME");
		
		List data = protocolService.query(branch);
		
		File file = new File(Excel.listToExcel(head, key, data));
		int i;
		response.setContentType("application/vnd.ms-excel");
		response.addHeader("Content-Disposition", "attachment;filename=\""+ URLEncoder.encode("机构"+branch+"质押贷款协议信息.xls", "UTF-8")+"\"");
		FileInputStream fis = new FileInputStream(file);
		ServletOutputStream out = response.getOutputStream();
		while ((i = fis.read()) != -1) {
			out.write(i);
		}
		fis.close();
		out.close();
		file.delete();
	}
	
	/**
	 * 查询机构下对应的银保通银行
	 * @param branchNo
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/setting/mortgageprotocol/branchbank")
	@ResponseBody
	public List queryBranchBanks(String branchNo, HttpServletResponse response) throws IOException{
		return protocolService.queryBranchBanks(branchNo);
	}
	
}
