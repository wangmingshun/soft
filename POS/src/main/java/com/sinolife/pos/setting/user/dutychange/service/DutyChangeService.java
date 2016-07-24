package com.sinolife.pos.setting.user.dutychange.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinolife.pos.setting.user.dutychange.dao.DutyChangeDAO;
import com.sinolife.pos.setting.user.privilege.dao.UserPrivilegeDAO;

@SuppressWarnings("rawtypes")
@Service("dutyChangeService")
public class DutyChangeService {

	@Autowired
	DutyChangeDAO dutyDAO;
	
	@Autowired
	UserPrivilegeDAO privsDAO;

	/**
	 * 是否存在上下级关系
	 * inputUser是否是operator的下级或自己，若不是，不能进行操作
	 * @param operator
	 * @param inputUser
	 * @return
	 */
	public boolean selfOrUnderling(String operator, String inputUser){
		if(operator.equals(inputUser) || "Y".equals(privsDAO.upperInputCheck(inputUser, operator))){
			return true;
		}
		return false;
	}
	
	public Map queryUserDuty(String userId){
		return dutyDAO.queryUserDuty(userId);
	}
	
	public List queryJuniorDetail(String userId){
		return dutyDAO.queryJuniorDetail(userId);
	}
	
	public void setDirector(String userId, String directorId){
		dutyDAO.setDirector(userId, directorId);
	}
	
	/**
	 * 查询directorId是否是userId的间接下级
	 * @param userId
	 * @param directorId
	 * @return
	 */
	public boolean upperRelationCheck(String userId, String directorId){
		List juniorList = dutyDAO.queryJuniorDetail(userId);
		for (int i = 0; juniorList!=null && i < juniorList.size(); i++) {
			Map map = (Map)juniorList.get(i);
			if("Y".equals(privsDAO.upperInputCheck(directorId, (String)map.get("USER_ID")))){
				return true;
			}
		}
		return false;
	}
	
}
