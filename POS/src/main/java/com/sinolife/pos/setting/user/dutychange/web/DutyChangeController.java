package com.sinolife.pos.setting.user.dutychange.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.rpc.um.PosUMService;
import com.sinolife.pos.setting.user.dutychange.service.DutyChangeService;
import com.sinolife.sf.platform.runtime.PlatformContext;

@SuppressWarnings({"unchecked","rawtypes"})
@Controller
public class DutyChangeController {

	@Autowired
	DutyChangeService changeService;
	
	@Autowired
	PosUMService umService;
	/**
	 * 入口.
	 * @return
	 */
	@RequestMapping("/setting/user/dutychange/queryAndSet")
	public String entry() {
		return "/setting/user/dutychange/queryAndSet";
	}
	
	/********************
	 * 查询用户信息
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping("/setting/user/dutychange/query")
	public String queryUserDuty(@RequestParam("userIdTxt") String userId, Model model){
		userId = PosUtils.parseInputUser(userId);
		String curUser = PlatformContext.getCurrentUser();
		
		if(umService.isPrivsManager(curUser) || changeService.selfOrUnderling(curUser, userId)){
			Map userMap = changeService.queryUserDuty(userId);
			userMap.put("ROLE_NAME", umService.getUserRoles(userId));
			
			model.addAttribute("userMap", userMap);
			
		}else{
			model.addAttribute("RETURN_MSG", userId+"是您的上级或与您不存在上下级关系，您不是权限管理员不能进行变更操作");
		}
		return "/setting/user/dutychange/queryAndSet";
	}
	
	/********************
	 * 查询下属明细信息
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping("/setting/user/dutychange/queryJuniorDetail")
	@ResponseBody
	public List queryJuniorDetail(String userId){
		return changeService.queryJuniorDetail(userId);
	}
	
	/********************
	 * 设置新的上级
	 * 将当前用户的所有下属的上级设置为新录入的用户
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping("/setting/user/dutychange/setDirector")
	@ResponseBody
	public String setDirector(String userId, String directorId) throws RuntimeException{
		directorId = PosUtils.parseInputUser(directorId);

		if(changeService.upperRelationCheck(userId, directorId)){
			return "X";
		}
		changeService.setDirector(userId, directorId);
		
		return "Y";
	}
}
