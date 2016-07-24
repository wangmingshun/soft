package com.sinolife.pos.callback.survival.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@SuppressWarnings("rawtypes")
@Repository("survivalInvestDAO")
public class SurvivalInvestDAO extends AbstractPosDAO{

	public Map queryInsured(String policyNo){
		return (Map)queryForObject("QUERY_INSURED_INFO", policyNo);
	}
	
	/**
	 * 修改生存调查结果
	 * @param pMap
	 */
	public void modifySubmit(Map pMap){
		getSqlMapClientTemplate().update(sqlName("MODIFY_SURVIVAL_INVEST"), pMap);
	}
	
	/**
	 * 录入生存调查结果
	 * @param pMap
	 */
	public void inputSubmit(Map pMap){
		getSqlMapClientTemplate().update(sqlName("UPDATE_SURVIVAL_INVEST"), pMap);
	}
	
	/**
	 * 插入生存调查表	
	 * @param pMap
	 */
	public void insertInvest(Map pMap){
		getSqlMapClientTemplate().insert(sqlName("INSERT_SURVIVAL_INVEST"), pMap);
	}
	
	/**
	 * 修改client_information表的客户死亡日期
	 * @param pMap
	 */
	public Map updateClientInfoDeathDate(Map pMap){
		queryForObject("UPDATE_CLIENT_INFO_DEATH_DATE", pMap);
		return pMap;
	}
}
