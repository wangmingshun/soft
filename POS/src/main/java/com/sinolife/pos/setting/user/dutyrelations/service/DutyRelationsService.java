package com.sinolife.pos.setting.user.dutyrelations.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinolife.pos.setting.user.dutyrelations.dao.DutyRelationsDAO;

@SuppressWarnings({"rawtypes","unchecked"})
@Service("dutyRelationsService")
public class DutyRelationsService {

	@Autowired
	DutyRelationsDAO drDAO;
	
	public Map queryInfoByRank(String rank){
		Map rstMap = new HashMap();
		rstMap.put("PRIVS", drDAO.queryPrivsByRank(rank));
		rstMap.put("INPUT_SPECIAL", drDAO.queryInputSpecialByRank(rank));
		rstMap.put("APROV_SPECIAL", drDAO.queryAprovSpecialByRank(rank));
		return rstMap;
	}
	
	public void setDutyRelations(Map pMap){
		drDAO.setDutyRelations(pMap);
	}
}
