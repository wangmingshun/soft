package com.sinolife.pos.acceptance.rollback.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Repository("branchRollbackDAO")
public class BranchRollbackDAO extends AbstractPosDAO{

	/**
	 * 根据posNo查询受理是否可以撤销
	 * @param posNo
	 * @return
	 */
	public Map rollBackAble(String posNo){
		Map pMap = new HashMap();
		pMap.put("posNo", posNo);
		queryForObject("RULECHECK_OF_CANCLE_ROLLBACK", pMap);
		return pMap;
	}
	/**
	 * 根据posNo撤销保全
	 * @param posNo
	 * @return
	 */
	public Map rollBack(Date calcdate,String posNo){
		Map pMap = new HashMap();
		pMap.put("calcdate", calcdate);
		pMap.put("posNo", posNo);
		queryForObject("POS_CANCLE_ROLLBACK", pMap);
		return pMap;
	}
	
	/**
	 * 根据posNo查询保全信息
	 * @param posNo
	 * @return
	 */
	public Map queryOriginalPosInfo(String posNo){
		return (Map)queryForObject("QUERY_ORIGINAL_POS_INFO", posNo);
	}

	/**
	 * 撤销回退的简单试算接口,返回批文
	 * @param pMap{posNo, serviceItems}
	 * @return
	 */
	public Map process(String posNo){
		Map pMap = new HashMap();
		pMap.put("posNo", posNo);
		queryForObject("POS_ROLLBACK_PROCESS", pMap);
		return pMap;
	}
	
	/**
	 * 查询当前用户的柜面信息
	 * @return
	 */
	public String userCounterNo(String user){
		return (String)queryForObject("QUERY_USER_COUNTER_NO", user);
	}
	
}
