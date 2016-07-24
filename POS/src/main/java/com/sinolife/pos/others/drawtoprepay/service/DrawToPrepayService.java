package com.sinolife.pos.others.drawtoprepay.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.others.drawtoprepay.dao.DrawToPrepayDAO;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Service("drawToPrepayService")
public class DrawToPrepayService {

	@Autowired
	DrawToPrepayDAO dao;
	
	@Autowired
	CommonQueryDAO commonQueryDAO;
	
	public Map query(String policyNo){
		Map map = dao.queryPolicy(policyNo);
		if(map!=null){
			Map tmp = dao.posInfoCheck(policyNo);
			if(tmp!=null){
				map.putAll(tmp);
				
			}
			tmp = dao.policyPremTurnOutCheck(policyNo);
			map.putAll(tmp);
		}
		return map;
	}
	
	public void operate(String policyNo, String premSum){
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("premSum", premSum);
		pMap.put("date", commonQueryDAO.getSystemDate());
		dao.operate(pMap);
	}
	
	/**
	 * 生存金或红利 反冲
	 * @param policyNo
	 * @param calcDate
	 */
	public void costRollback(String policyNo){
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("calcDate", dao.primaryPayToDate(policyNo));
		
		dao.dividendCostRollback(pMap);
		dao.survivalCostRollback(pMap);
	}
}
