package com.sinolife.pos.others.policysuspend.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@SuppressWarnings("rawtypes")
@Repository("policySuspendDAO")
public class PolicySuspendDAO extends AbstractPosDAO{
	
	public Map policyQuery(String policyNo){
		return (Map)queryForObject("QUERY_POLICY_INFO", policyNo);
	}
	
	/**
	 * 将用户对置暂停时的描述更新到备注字段
	 * @param pMap
	 */
	public void updatePosInfoRemark(Map pMap){
		getSqlMapClientTemplate().update(sqlName("UPDATE_POS_INFO_REMARK"), pMap);
	}
	
	/**
	 * 取消暂停时，更新状态和备注
	 * @param pMap
	 */
	public void updatePosInfoStatus(Map pMap){
		getSqlMapClientTemplate().update(sqlName("UPDATE_POS_INFO_STATUS_REMARK"), pMap);
	}

}
