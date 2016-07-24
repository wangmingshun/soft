package com.sinolife.pos.approve.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@SuppressWarnings("rawtypes")
@Repository("ApproveManageDAO")
public class ApproveManageDAO extends AbstractPosDAO {

	/**
	 * 不良记录查询
	 * 
	 * @param map
	 * @return List
	 * @author GaoJiaMing
	 * @time 2014-8-19
	 */
	public List queryBadBehavior(Map map) {
		if ("1".equals(map.get("objectType"))) {
			return queryForList("queryBadBehavior1", map);
		} else {
			return queryForList("queryBadBehavior2", map);
		}

	}

	/**
	 * 不良记录新增
	 * 
	 * @param map
	 * @return void
	 * @author GaoJiaMing
	 * @time 2014-8-21
	 */
	public void insertBadBehavior(Map map) {
		getSqlMapClientTemplate().insert(sqlName("insertBadBehavior"), map);
	}

	/**
	 * 获取网点名称
	 * 
	 * @param map
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-8-21
	 */
	public String getObjectNameByObjectNo1(Map map) {
		return (String) queryForObject("getObjectNameByObjectNo1", map);
	}

	/**
	 * 获取银代名称
	 * 
	 * @param map
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-8-21
	 */
	public String getObjectNameByObjectNo2(Map map) {
		return (String) queryForObject("getObjectNameByObjectNo2", map);
	}

	/**
	 * 获取不良记录次数
	 * 
	 * @param map
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-8-21
	 */
	public String getBadBehaviorCountByObjectNo(Map map) {
		return (String) queryForObject("getBadBehaviorCountByObjectNo", map);
	}

	/**
	 * 判断是否需要送审
	 * 
	 * @param map
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-8-22
	 */
	public String needExamPrivsApproved(Map map) {
		return (String) queryForObject("needExamPrivsApproved", map);
	}

	/**
	 * 新增代审审批表
	 * 
	 * @param map
	 * @return void
	 * @author GaoJiaMing
	 * @time 2014-8-22
	 */
	public void insertPosExamPrivsApprove(Map map) {
		getSqlMapClientTemplate().insert(sqlName("insertPosExamPrivsApprove"), map);
	}

	/**
	 * 代审审批查询
	 * 
	 * @param map
	 * @return List
	 * @author GaoJiaMing
	 * @time 2014-8-19
	 */
	public List queryExamPrivsApprovePage(Map map) {
		return queryForList("queryExamPrivsApprovePage", map);
	}

	/**
	 * 代审审核
	 * 
	 * @param paraMap
	 * @return void
	 * @author GaoJiaMing
	 * @time 2014-8-25
	 */
	public void examPrivsApprove(Map paraMap) {
		getSqlMapClientTemplate().update(sqlName("examPrivsApprove"), paraMap);
	}

	/**
	 * 撤销黑名单
	 * 
	 * @param paraMap
	 * @return void
	 * @author GaoJiaMing
	 * @time 2014-8-25
	 */
	public void examPrivsCancel(Map paraMap) {
		getSqlMapClientTemplate().update(sqlName("examPrivsCancel"), paraMap);
	}

	/**
	 * 代审权限判断
	 * 
	 * @param map
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-8-21
	 */
	public Map<String, Object> checkApprovePrivs(Map<String, Object> map) {
		return (Map<String, Object>) queryForObject("checkApprovePrivs", map);
	}
	
	/**
	 * 根据保全号获取代审信息
	 * 
	 * @param posNo
	 * @return Map<String,Object>
	 * @author GaoJiaMing
	 * @time 2014-9-4
	 */
	public Map<String, Object> getBadBehaviorInfoByPosNo(String posNo) {
		return (Map<String, Object>) queryForObject("getBadBehaviorInfoByPosNo", posNo);
	}
	
	/**
	 * 结合保单号校验代审权限
	 * 
	 * @param map
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-9-18
	 */
	public String checkPrivsByPolicyNo(Map<String, Object> map) {
		return (String) queryForObject("checkPrivsByPolicyNo", map);
	}
	/**
	 *  检查是否是代办代审的试点机构
	 * @param map
	 * @return String
	 * @author WangMingShun
	 * @time 2015-4-7
	 */
	public String checkExamBranch(Map<String, Object> map) {
		return (String) queryForObject("checkExamBranch", map);
	}
}
