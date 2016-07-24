package com.sinolife.pos.others.poszj.web;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sinolife.pos.common.consts.SessionKeys;
import com.sinolife.pos.common.include.service.IncludeService;
import com.sinolife.pos.common.util.PaginationDataWrapper;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.others.poszj.service.PosDividendSampleService;
import com.sinolife.pos.schedule.dto.NoticePrintBatchTask;
import com.sinolife.sf.platform.runtime.PlatformContext;

/**
 * 保全质检
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
public class PosDividendSampleController {

	@Autowired
	PosDividendSampleService sampleService;
	
	@Autowired
	IncludeService includeService;

	
	/**
	 * 入口
	 * @return
	 */
	@RequestMapping("/others/poszj/{path}")
	public String entry(@PathVariable("path") String path,Model model) {

		model.addAttribute("userBranchCode",
				includeService.userBranchCode(PlatformContext.getCurrentUser()));
		model.addAttribute("userID",
				PlatformContext.getCurrentUserInfo().getUserId());
		model.addAttribute("GQ_URL", PosUtils.getPosProperty("gqUrl"));
		return "/others/poszj/"+path;
	}
	
	/**
	 * 查询抽样保全
	 * @param policyNo
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/others/poszj/querypos")
	public String queryProductInfo(HttpServletRequest request,Model model) throws ParseException{
		Map pMap = new HashMap();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		pMap.put("branchCode", request.getParameter("branchCode"));//机构
//		pMap.put("serviceItems", request.getParameter("serviceItems").split(","));//保全项目
		pMap.put("serviceItems", request.getParameter("serviceItems"));//保全项目
		pMap.put("acceptor", request.getParameter("accepteUserId"));//保全受理人
		pMap.put("startDate",df.parse(request.getParameter("startDate")));//保全完成时间
		pMap.put("endDate",df.parse(request.getParameter("endDate")));//保全完成时间
		pMap.put("posNum", Integer.parseInt(request.getParameter("posNum")));//保全件数量
		pMap.put("user", PlatformContext.getCurrentUserInfo().getUserId());	//当前用户的id
		sampleService.submitSample(pMap);
		if("0".equals(pMap.get("recordNumber").toString())){
			model.addAttribute("RETURN_MSG", "抽样失败，没有可抽样的数据！");
		}else {
			model.addAttribute("RETURN_MSG", "提交成功！");
		}
		
		model.addAttribute("userBranchCode",
				includeService.userBranchCode(PlatformContext.getCurrentUser()));
		return "/others/poszj/sample";
	}
	

	/**
	 * 查询待质检列表
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("others/poszj/waitlist")
	@ResponseBody
	public Map<String, Object> getPosList(@RequestParam int page, @RequestParam int rows) {
		Map pMap = new HashMap();
		pMap.put("LoginUserName", PlatformContext.getCurrentUserInfo().getUserId());
		List<?> taskList = sampleService.queryAllPos(pMap);
		PaginationDataWrapper wrapper = new PaginationDataWrapper(taskList, rows);
		wrapper.gotoPage(page);
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("total", wrapper.getTotalDataSize());
		retMap.put("rows", wrapper.getCurrentPageDataList());
		return retMap;
	}
	
	
	/**
	 * 查询指定的保全
	 * @param policyNo
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/others/poszj/queryInfor")
	public String queryPosInfo(HttpServletRequest request,Model model) throws ParseException{
		Map pMap = new HashMap();
		
		String posNo = request.getParameter("posNo");
		String take = request.getParameter("take");
		pMap.put("posNo",posNo );//保全号
		pMap.put("take", take); //新增或者更新标识
		if(take.equals("N")){
			model.addAttribute("posInfo", pMap);
			return "/others/poszj/entryZJ";
		}
		
		List list = sampleService.queryPosInfo(posNo);
		if(list.size()==0){
			model.addAttribute("RETURN_MSG", "查询不到合适的数据，请核实");
			return "/others/poszj/modify";
		}else{
			pMap.putAll((Map)list.get(0));
			model.addAttribute("posInfo", pMap);
		}
		
		return "/others/poszj/entryZJ";
	}
	
	/**
	 * 增加或者修改质检
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("others/poszj/addZJ")
	public String addOrUpdate(HttpServletRequest request,Model model){
		Map pMap = new HashMap();
		pMap.put("posNo", request.getParameter("posNo"));//保全号
		pMap.put("resultSet", request.getParameter("resultSet"));//质检结果
		pMap.put("payType", request.getParameter("payType"));//问题件类型
		pMap.put("problem",request.getParameter("problem"));//问题件描述栏
		pMap.put("score", request.getParameter("score"));//分数
		pMap.put("take", request.getParameter("take"));//新增或者更新标识
		pMap.put("user", PlatformContext.getCurrentUserInfo().getUserId());//当前用户的id
		
		sampleService.addOrUpdate(pMap);
		if("N".equals(pMap.get("take"))){
			model.addAttribute("RETURN_MSG", "结果录入成功");
			model.addAttribute("GQ_URL", PosUtils.getPosProperty("gqUrl"));
			return "/others/poszj/entryList";
		}
		model.addAttribute("RETURN_MSG", "结果更新成功");
		return "/others/poszj/modify";
	}
	
}
