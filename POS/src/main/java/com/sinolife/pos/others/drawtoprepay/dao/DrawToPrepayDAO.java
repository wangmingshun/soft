package com.sinolife.pos.others.drawtoprepay.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Repository("drawToPrepayDAO")
public class DrawToPrepayDAO extends AbstractPosDAO{

	/**
	 * 查询保单的基本信息
	 * @param policyNo
	 * @return
	 */
	public Map queryPolicy(String policyNo){
		return (Map)queryForObject("QUERY_POLICY_INFO", policyNo);
	}
	
	/**
	 * 检查保单是否在主险交至日后做过补退费保全项目
	 * @param policyNo
	 * @return
	 */
	public Map posInfoCheck(String policyNo){
		return (Map)queryForObject("POLICY_POS_CHECK", policyNo);
	}
	
	/**
	 * 查询续期接口保单可以转出金额
	 * @param policyNo
	 * @return
	 */
	public Map policyPremTurnOutCheck(String policyNo){
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		queryForObject("POLICY_PREM_TURN_OUT_CHECK", pMap);
		return pMap;
	}
	
	public void operate(Map pMap){
		queryForObject("PROC_RN_PREM_TRANSFER_OUT", pMap);
		if("N".equals(pMap.get("flag"))){
			throw new RuntimeException(""+pMap.get("message"));
		}
	}
	
	public void dividendCostRollback(Map pMap){
		queryForObject("DIVIDEND_COST_ROLLBACK", pMap);
		if("1".equals(pMap.get("flag"))){
			throw new RuntimeException("dividend:"+pMap.get("message"));
		}
	}
	
	public void survivalCostRollback(Map pMap){
		queryForObject("SURVIVAL_PREM_ROLLBACK", pMap);
		if("1".equals(pMap.get("flag"))){
			throw new RuntimeException("survival:"+pMap.get("message"));
		}
	}
	
	/**
	 * 查询保单主险pay_to_date
	 * @param policyNo
	 * @return
	 */
	public Date primaryPayToDate(String policyNo){
		return (Date)queryForObject("POLICY_PRIMARY_PAY_TO_DATE", policyNo);
	}
}
