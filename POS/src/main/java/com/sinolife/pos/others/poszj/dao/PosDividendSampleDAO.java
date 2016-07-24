package com.sinolife.pos.others.poszj.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@SuppressWarnings("rawtypes")
@Repository("posDividendSampleDAO")
public class PosDividendSampleDAO extends AbstractPosDAO{


	
	public void submitSample(Map pMap){
	  queryForList("QUERY_ZJ_SAMPLE", pMap);
	  if("1".equals(pMap.get("flag"))){
			throw new RuntimeException(""+pMap.get("message"));
	  }
//	  getSqlMapClientTemplate().insert(sqlName("QUERY_ZJ_SAMPLE2"), pMap);
	}
	
	public List<?> queryPosInfo(String posNo){
		return queryForList("QUERY_ZJ_INFO", posNo);

	}
	
	public List<?> queryAllPos(Map pMap){
		return queryForList("QUERY_ALL_ZJ_INFO", pMap);
	}

	public void addOrUpdate(Map pMap){
		getSqlMapClientTemplate().update(sqlName("UPDATE_ZJ_INFO"), pMap);
	}
	
}
