package com.sinolife.pos.riskcontrol.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.sinolife.pos.common.dto.SampleDTO;
import com.sinolife.pos.riskcontrol.service.BatchSampleService;
import com.sinolife.sf.platform.runtime.PlatformContext;

/**
 * 批处理抽样
 * 设计和实现有两种，生存金和红利
 * 后来干掉红利，只剩生存金的了
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
public class BatchSampleController {
	
	protected Logger logger = Logger.getLogger(getClass());

	@Autowired
	BatchSampleService sampleService;
	
	/**
	 * 抽样入口
	 * @return
	 */
	@RequestMapping("/riskcontrol/batch/sample")
	public String sampleEntry(){
		return "/riskcontrol/batch/sample";
	}
	
	/**
	 * 复核入口
	 * @return
	 */
	@RequestMapping("/riskcontrol/batch/verify")
	public String verifyEntry(){
		return "/riskcontrol/batch/verify";
	}
	
	/**
	 * 复核确认入口
	 * @return
	 */
	@RequestMapping("/riskcontrol/batch/confirm")
	public String confirmEntry(){
		return "/riskcontrol/batch/confirm";
	}
	
	/**
	 * 抽样 提交
	 * @return
	 */
	@RequestMapping("/riskcontrol/batch/sampleSubmit")
	public String sampleSubmit(@RequestParam String sampleType,@RequestParam String branchCode,@RequestParam String sampleMonth,Model model){
		Map pMap = new HashMap();
		pMap.put("sampleType", sampleType);
		pMap.put("branchCode", branchCode);
		pMap.put("sampleMonth", sampleMonth);
		
		sampleService.sampleSubmit(pMap);
		
		model.addAttribute("RETURN_MSG", "抽样提交成功");
		return "/riskcontrol/batch/sample";
	}
	
	/**
	 * 复核取下一单
	 * @return
	 */
	@RequestMapping("/riskcontrol/batch/verifyNext")
	public String verifyNext(@RequestParam String sampleTypeSel, Model model){
		
		Map map = sampleService.verifyNext(sampleTypeSel);
		if(map!=null && map.size()>1){
			model.addAttribute("verifyData", map);	
		}else{
			model.addAttribute("RETURN_MSG", "没有待复核的数据");
		}
		return "/riskcontrol/batch/verify";
	}
	
	/**
	 * 复核提交
	 * @return
	 */
	@RequestMapping("/riskcontrol/batch/verifySubmit")
	public String verifySubmit(HttpServletRequest request,Model model){
		Map pMap = new HashMap();
		pMap.put("sampleSeq", request.getParameter("sampleSeq"));
		pMap.put("sampleType", request.getParameter("sampleType"));
		pMap.put("getDueDate", request.getParameter("getDueDate"));
		pMap.put("getDueSum", request.getParameter("getDueSum"));
		pMap.put("reviewGetDueDate", request.getParameter("reviewGetDueDate"));
		pMap.put("reviewGetDueSum", request.getParameter("reviewGetDueSum"));
		pMap.put("reviewer", PlatformContext.getCurrentUser());
		pMap.put("resultDesc", PlatformContext.getCurrentUser()+"："+request.getParameter("resultDesc")+" ");

		sampleService.verifySubmit(pMap);
		
		model.addAttribute("RETURN_MSG", "复核数据提交完成");
		return "/riskcontrol/batch/verify";
	}
	
	/**
	 * 复核确认 查询
	 * @return
	 */
	@RequestMapping("/riskcontrol/batch/confirmQuery")
	public String confirmQuery(@RequestParam String sampleType, Model model){
		
		List list = sampleService.confirmQuery(sampleType);
		if(list!=null && list.size()>0){
			model.addAttribute("confirmList", list);
		}else{
			model.addAttribute("RETURN_MSG", "没有待确认的数据");
		}
		
		return "/riskcontrol/batch/confirm";
	}
	
	/**
	 * 复核确认 提交
	 * @return
	 */
	@RequestMapping(value = "/riskcontrol/batch/confirmSubmit",method = RequestMethod.POST)
	public String confirmSubmit(SampleDTO sampleDto, Model model){	
		logger.info("riskcontrolConfirmSubmit"+PlatformContext.getCurrentUser());
		sampleDto.setConfirmer(PlatformContext.getCurrentUser());
		sampleDto.setResultDesc(PlatformContext.getCurrentUser()+"："+sampleDto.getResultDesc()+" ");
		sampleService.confirmSubmit(sampleDto);	
		logger.info(" riskcontrolConfirmSubmit"+PlatformContext.getCurrentUser()+"end.");
		model.addAttribute("RETURN_MSG", "确认结果提交成功");
		return "/riskcontrol/batch/confirm";
	}
	
}
