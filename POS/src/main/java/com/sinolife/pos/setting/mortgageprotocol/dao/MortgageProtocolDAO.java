package com.sinolife.pos.setting.mortgageprotocol.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@SuppressWarnings("rawtypes")
@Repository("mortgageProtocolDAO")
public class MortgageProtocolDAO extends AbstractPosDAO{

	public List query(String branch){
		return queryForList("QUERY_BRANCH_MORTGAGE_PROTOCOL", branch);
	}
	
	/**
	 * 插入协议表
	 * @param pMap
	 */
	public void insertProtocol(Map pMap){
		getSqlMapClientTemplate().insert(getClass().getName()+".INSERT_MORTGAGE_PROTOCOL", pMap);
	}
	
	/**
	 * 插入协议险种表
	 * @param pMap
	 */
	public void insertProduct(Map pMap){
		getSqlMapClientTemplate().insert(getClass().getName()+".INSERT_MORTGAGE_PRODUCT", pMap);
	}
	
	/**
	 * 根据主键置为无效
	 * @param protocolCode
	 */
	public void updateProtocolFlag(String protocolCode){
		getSqlMapClientTemplate().update(getClass().getName()+".UPDATE_PROTOCOL_VAILID_FLAG", protocolCode);
	}
	
	/**
	 * 根据主键更新协议表
	 * @param pMap
	 */
	public void updateProtocol(Map pMap){
		getSqlMapClientTemplate().update(getClass().getName()+".UPDATE_MORTGAGE_PROTOCOL", pMap);
	}
	
	public List queryBranchBanks(String branchNo){
		return queryForList("QUERY_BRANCH_BANK_LIST", branchNo);
	}
	
}
