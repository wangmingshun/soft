package com.sinolife.pos.callback.survival.service;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.callback.survival.dao.SurvivalInvestDAO;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Service("survivalInvestService")
public class SurvivalInvestService {

	@Autowired
	SurvivalInvestDAO survivalDAO;
	
	public Map queryInsured(String policyNo){
		return survivalDAO.queryInsured(policyNo);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void inputSubmit(Map pMap){
		if(("1").equals(pMap.get("submitType"))){//回销
			survivalDAO.inputSubmit(pMap);
			
		}else if(("2").equals(pMap.get("submitType"))){//修改
			survivalDAO.modifySubmit(pMap);
			
		}else if(("3").equals(pMap.get("submitType"))){//新插入（无待回销的抽档情况下）
			survivalDAO.insertInvest(pMap);
		}
		
		if(StringUtils.isBlank((String)pMap.get("deathDate"))){
			pMap.put("nullKey", "death_date");
		}
		survivalDAO.updateClientInfoDeathDate(pMap);
		if(!"Y".equals(pMap.get("flag"))){
			throw new RuntimeException((String)pMap.get("message"));
		}
	}
}
