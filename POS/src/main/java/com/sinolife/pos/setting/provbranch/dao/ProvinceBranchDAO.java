package com.sinolife.pos.setting.provbranch.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@SuppressWarnings("rawtypes")
@Repository("provinceBranchDAO")
public class ProvinceBranchDAO extends AbstractPosDAO{
	
	public List query(String province){
		return queryForList("QUERY_PROVINCE_BRANCH", province);
	}
	
	/**
	 * 新增数据插入表POS_BRANCH_IN_PROVINCE
	 * @param pMap
	 */
	public void insertProvinceBranch(Map pMap){
		getSqlMapClientTemplate().insert(sqlName("INSERT_POS_BRANCH_IN_PROVINCE"), pMap);
	}
	
	/**
	 * 根据主键将数据置为无效
	 * @param pk
	 */
	public void setProvinceBranchFlag(String pk){
		getSqlMapClientTemplate().update(sqlName("SET_BRANCH_IN_PROVINCE_VAILID_FLAG"), pk);
	}
	
	/**
	 * 根据主键修改省市对应的机构码
	 * @param pMap
	 */
	public void updateProvinceBranch(Map pMap){
		getSqlMapClientTemplate().update(sqlName("UPDATE_BRANCH_IN_PROVINCE"), pMap);
	}
	
}
