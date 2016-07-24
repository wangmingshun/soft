package com.sinolife.pos.rpc.um;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sinolife.um.auth.AuthService;
import com.sinolife.um.res.domain.SystemDefination;
import com.sinolife.um.res.spi.ResourceService;
import com.sinolife.um.user.domain.UserGroup;
import com.sinolife.um.user.spi.UserService;

@Component
public class PosUMService {

	@Autowired
	UserService userService;

	@Autowired
	AuthService authService;
	
	@Autowired
	ResourceService resService;
	
	/**
	 * 获取某个用户下所有角色，多个角色用"，"号分隔
	 * @param userid
	 * @return
	 */
	public String getUserRoles(String userId) {
		List<UserGroup> ugs = userService.getUGList(userId);
		StringBuffer userRule = new StringBuffer();
		if (null != ugs && ugs.size() != 0) {
			for (int i = 0; i < ugs.size(); i++) {
				UserGroup ug = ugs.get(i);
				userRule.append(ug.getUgName()).append(",");
			}
		}
		return userRule.toString();
	}

	/**
	 * 是否已有UM中pos系统的权限
	 * @param userId
	 * @return
	 */
	public boolean hasPosUM(String userId){
		return authService.hasSystemPermission(userId, "00006");//pos在um中终身代号00006
	}
	
	/**
	 * 用户是否是保全系统权限管理员
	 * 即是否有POS_PERMISSION_MANAGE角色
	 * @param userId
	 * @return
	 */
	public boolean isPrivsManager(String userId){
		return hasRole(userId, "POS_PERMISSION_MANAGE");
	}
	
	/**
	 * 是否有某种角色
	 * 注意角色的写法和um的设置保持一致
	 * @param userId
	 * @param role
	 * @return
	 */
	public boolean hasRole(String userId, String role){
		List<UserGroup> ugs = userService.getUGList(userId);
		for (int i = 0; ugs!=null && i<ugs.size(); i++) {
			if(ugs.get(i).getUgNumber().equalsIgnoreCase(role)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 是否总公司用户
	 * @param userId
	 * @return
	 */
	public boolean hasCenterRole(String userId){
		return hasRole(userId, "POS_PERMISSION_MANAGE")||
			   hasRole(userId, "POS_MANAGER")||
			   hasRole(userId, "POS_LIST_MANAGER")||
			   hasRole(userId, "POS_SURVIVAL")||
			   hasRole(userId, "POS_ADDITIONAL")||
			   hasRole(userId, "POS_SENIOR_BIZ")||
			   hasRole(userId, "POS_SENIOR");
	}
	
	/**
	 * 根据系统唯一编号获取系统的地址
	 * @param sysId
	 * @return
	 */
	public String systemUrl(String sysId){
		SystemDefination sys = resService.getSystemDefination(sysId);
		return sys.getUrl();
	}
}
