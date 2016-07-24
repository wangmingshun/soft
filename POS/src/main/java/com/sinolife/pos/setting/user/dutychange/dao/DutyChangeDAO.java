package com.sinolife.pos.setting.user.dutychange.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.util.PosUtils;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Repository("dutyChangeDAO")
public class DutyChangeDAO extends SqlMapClientDaoSupport{

	public Map queryUserDuty(String userId){
		return (Map)getSqlMapClientTemplate().queryForObject(DutyChangeDAO.class.getName()+".QUERY_DUTY_INFO_BY_USER", userId);
	}
	
	public List queryJuniorDetail(String userId){
		return getSqlMapClientTemplate().queryForList(DutyChangeDAO.class.getName()+".QUERY_JUNIOR_DETAIL_BY_USER", userId);
	}
	
	public void setDirector(String userId, String directorId){
		Map pMap = new HashMap();
		pMap.put("userId", userId);
		pMap.put("directorId", PosUtils.parseInputUser(directorId));
		getSqlMapClientTemplate().update(DutyChangeDAO.class.getName()+".UPDATE_DUTY_DIRECTOR", pMap);
	}
}
