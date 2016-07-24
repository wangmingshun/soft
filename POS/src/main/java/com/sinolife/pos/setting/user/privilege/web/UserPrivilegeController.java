package com.sinolife.pos.setting.user.privilege.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.read.biff.BiffException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.rpc.um.PosUMService;
import com.sinolife.pos.setting.user.privilege.dto.UserPrivilegeInfoDTO;
import com.sinolife.pos.setting.user.privilege.service.UserPrivilegeService;
import com.sinolife.sf.platform.runtime.PlatformContext;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class UserPrivilegeController {

	@Autowired
	UserPrivilegeService privService;
	
	@Autowired
	PosUMService umService;
	
	
	
	
	/**
	 * 入口.
	 * @return
	 */
	@RequestMapping(value = "/setting/user/privilege/interestSet", method = RequestMethod.GET)
	public String rateSetEntry(Model model){
		
		model.addAttribute("interestUnits", privService.getInterestUnit());
		
		return "/setting/interest/rateSet";
	}
	

	/**利率设置
	 * @param model
	 * @param productCode
	 * @param interestTypeRate
	 * @param interestUnit
	 * @param interestRate
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value ="/setting/user/privilege/interestSet", method = RequestMethod.POST)
	public String interestSet(Model model,
			@RequestParam("productCode") String productCode,
			@RequestParam("interestTypeRate") String interestTypeRate,
			@RequestParam("interestUnit") String interestUnit,
			@RequestParam("interestRate") String interestRate,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate
			
	){
		

		
		model.addAttribute("interestUnits", privService.getInterestUnit());
		Map pMap=new HashMap();
		pMap.put("productCode", productCode);
		pMap.put("interestType", interestTypeRate);
		pMap.put("interestUnit", interestUnit);
		pMap.put("interestRate", interestRate);
		pMap.put("startDate", startDate);
		pMap.put("endDate", endDate);
		
		privService.addInterestRate(pMap);
		model.addAttribute("RETURN_MSG","设置成功！");
		
		return "/setting/interest/rateSet";
	}
	
	
	
	/**
	 * 入口.
	 * @return
	 */
	@RequestMapping("/setting/user/privilege")
	public String entry(Model model){
		
		model.addAttribute("isPM", umService.isPrivsManager(PlatformContext.getCurrentUser()));
		
		return "/setting/user/privilege/set";
	}
	
	/***************************
	 * 根据用户名查询用户权限信息
	 * @return
	 */
	@RequestMapping("/setting/user/privilege/query")
	public String queryUserPrivsInfo(@RequestParam("user")String user, Model model){

		queryUserPrivsInfoAid(user,model);
		
		return "/setting/user/privilege/set";
	}
	
	/**
	 * 用户录入前的检查校验
	 * 将这一段程序做方法供批处理和逐单共同使用
	 * @param user
	 * @param model
	 */
	private void  queryUserPrivsInfoAid(String user, Model model){
		boolean isPM = umService.isPrivsManager(PlatformContext.getCurrentUser());
		model.addAttribute("isPM", isPM);
		
		if(!isPM && privService.isCurrentUserUpper(PlatformContext.getCurrentUser(), PosUtils.parseInputUser(user))){
			model.addAttribute("RETURN_MSG", user+"是您的上级或不属于您的机构，您不是权限管理员不能设置其权限");
			
		}else{
			model.addAttribute("USER_ID", user);
			user = PosUtils.parseInputUser(user);
			
			if(umService.hasPosUM(user)){
				model.mergeAttributes(privService.queryUserPrivsInfo(user));
				model.addAttribute("UM_CHECK_FLAG", "Y");
			}else{
				model.addAttribute("RETURN_MSG", user+"尚无POS系统的UM权限，请先在UM系统中设置");
			}			
		}
	}
	
	/*************************************************
	 * 根据选择的用户审批等级和渠道查询审批金额的保全项目
	 * @return
	 */
	@RequestMapping("/setting/user/privilege/queryAmount")
	@ResponseBody
	public Map queryUserAmount(String amountDaysGrade, String channel){
		return privService.queryUserAmount(amountDaysGrade, channel);
	}
	
	/*********************************************
	 * 根据录入等级查询可操作的保全项目和特殊件
	 * @return
	 */
	@RequestMapping("/setting/user/privilege/inputGradeItems")
	@ResponseBody
	public List queryRankItems(String inputGrade){
		return privService.queryInputGradeItems(inputGrade);
	}
	
	/***************************
	 * 提交设置
	 * @return
	 */
	@RequestMapping("/setting/user/privilege/submit")
	public String submit(UserPrivilegeInfoDTO userPrivsDTO, Model model){
		
		privService.submit(userPrivsDTO);
		
		model.addAttribute("isPM", umService.isPrivsManager(PlatformContext.getCurrentUser()));
		model.addAttribute("RETURN_MSG", "设置成功");
		
		return "/setting/user/privilege/set";
	}
	
	/***************************
	 * 取消用户上级
	 * @return
	 */
	@RequestMapping("/setting/user/privilege/removeUpper")
	@ResponseBody
	public String removeUpper(@RequestParam("userId")String userId){
		privService.removeUpper(PosUtils.parseInputUser(userId));
		return "Y";
	}
	
	/**
	 * 录入上级用户的检查
	 * @param upperId
	 * @param userId
	 * @return
	 */
	@RequestMapping("/setting/user/privilege/upperCheck")
	@ResponseBody
	public String upperInputCheck(String upperId, String userId){
		return privService.upperInputCheck(PosUtils.parseInputUser(upperId), PosUtils.parseInputUser(userId));
	}
	
	/**
	 * 入口.
	 * @return
	 */
	@RequestMapping("/setting/user/privImport")
	public String batchBntry(){
		return "/setting/user/privilege/import";
	}
	
	/**
	 * 提交权限文件,用于用户权限批量导入，只支持新增的用户
	 * @return
	 * @throws IOException 
	 * @throws BiffException 
	 */
	@RequestMapping("/setting/user/importPrivFile")
	public String importPrivFile(@RequestParam MultipartFile file, Model model) throws IOException, BiffException{
		List rstList = new ArrayList();
		
		List list = privService.parsePrivsFile(file);
		
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map)list.get(i);
			queryUserPrivsInfoAid((String)map.get("userId"),model);
			Map rstMap = new HashMap();;
			rstMap.put("userId", map.get("userId"));
			if(model.containsAttribute("RETURN_MSG")){
				rstMap.put("resultMsg", model.asMap().get("RETURN_MSG"));
				if("用户代码".equals(map.get("userId"))){
					rstMap.put("resultMsg", "异常信息，若有疑问，请联系技术人员支持，或逐单设置");	
				}
				rstList.add(rstMap);
				model.asMap().clear();
				continue;
			}
			try {
				privService.insertSinglePrivsData(map);
			} catch (Exception e) {
				String es = e.getMessage();
				es = es.indexOf("Exception:")>0?es.substring(es.indexOf("Exception:")+10):es;
				rstMap.put("resultMsg", es.substring(0, es.length()<100?es.length():100));
				rstList.add(rstMap);
			}
		}
		
		model.addAttribute("importFlag", "Y");
		model.addAttribute("rstList", rstList);
		return "/setting/user/privilege/import";
	}
	
}
