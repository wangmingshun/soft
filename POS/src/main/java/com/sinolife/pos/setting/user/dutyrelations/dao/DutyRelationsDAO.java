package com.sinolife.pos.setting.user.dutyrelations.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@SuppressWarnings({"rawtypes","unchecked"})
@Repository("dutyRelationsDAO")
public class DutyRelationsDAO extends AbstractPosDAO{

	public List queryInputSpecialByRank(String rank){
		return queryForList("QUERY_GRADE_SPECIAL_FUNC", rank);
	}
	
	public List queryAprovSpecialByRank(String rank){
		return queryForList("QUERY_RANK_APROV_FUNC_SPECIALS", rank);
	}
	
	public Map queryPrivsByRank(String rank){
		Map rstMap = (Map)queryForObject("QUERY_GRADE_PRIVS_SET", rank);
		if(rstMap==null){
			rstMap = new HashMap();
		}
		
		rstMap.put("RULE_SPEC_PRIVS", queryRankAprovRuleRollback(rank, "1"));
		
		rstMap.put("ROLLBACK_APROV_PRIVS", queryRankAprovRuleRollback(rank, "3"));
		
		return rstMap;
	}
	
	/**
	 * 查询职级能审批规则特殊件或撤销回退不
	 * @param rank
	 * @param type 1-规则特殊件，3-撤销回退
	 * @return
	 */
	public String queryRankAprovRuleRollback(String rank, String type){
		Map pMap = new HashMap();
		pMap.put("rank", rank);
		pMap.put("type", type);
		return (String)queryForObject("QUERY_RANK_SPECIAL_PRIVS", pMap);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void setDutyRelations(Map pMap){
		SqlMapClientTemplate sqlClient = getSqlMapClientTemplate();
		int c;//更新的行数
		
		//作废已有的数据，3张表，5种信息
		sqlClient.update(sqlName("UPDATE_GRADE_PRIVS_SET_END_DATE"), (String)pMap.get("rank"));
		sqlClient.update(sqlName("UPDATE_GRADE_SPECIAL_FUNC_PRIVS_END_DATE"), (String)pMap.get("rank"));
		sqlClient.update(sqlName("UPDATE_RANK_APROV_SPECIAL_PRIVS_END_DATE"), (String)pMap.get("rank"));

		//再写入
		c = sqlClient.update(sqlName("UPDATE_GRADE_PRIVS_SET"), pMap);
		if(c<1){
			sqlClient.insert(sqlName("INSERT_GRADE_PRIVS_SET"), pMap);
		}
		
		String[] specials = (String[])pMap.get("inputSpecials");
		for (int i = 0; i < specials.length && specials[i].length()>0; i++) {
			pMap.put("special", specials[i]);
			c = sqlClient.update(sqlName("UPDATE_GRADE_SPECIAL_FUNC_PRIVS"), pMap);
			if(c<1){
				sqlClient.insert(sqlName("INSERT_GRADE_SPECIAL_FUNC_PRIVS"), pMap);
			}
		}

		pMap.put("special", "1");
		pMap.put("specialType", "2");
		specials = (String[])pMap.get("aprovSpecials");
		//功能特殊件
		for (int i = 0; i < specials.length && specials[i].length()>0; i++) {
			pMap.put("special", specials[i]);
			c = sqlClient.update(sqlName("UPDATE_RANK_APROV_SPECIAL_PRIVS"), pMap);
			if(c<1){
				sqlClient.insert(sqlName("INSERT_RANK_SPECIAL_PRIVS_SET"), pMap);
			}
		}
		
		//规则特殊件审批权限
		if("Y".equals(pMap.get("ruleSpec"))){
			pMap.put("specialType", "1");
			c = sqlClient.update(sqlName("UPDATE_RANK_APROV_SPECIAL_PRIVS"), pMap);
			if(c<1){
				sqlClient.insert(sqlName("INSERT_RANK_SPECIAL_PRIVS_SET"), pMap);
			}
		}
		
		//撤销回退审批权限
		if("Y".equals(pMap.get("rollbackAprov"))){
			pMap.put("specialType", "3");
			c = sqlClient.update(sqlName("UPDATE_RANK_APROV_SPECIAL_PRIVS"), pMap);
			if(c<1){
				sqlClient.insert(sqlName("INSERT_RANK_SPECIAL_PRIVS_SET"), pMap);
			}
		}
	}
	
}
