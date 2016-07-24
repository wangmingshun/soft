package com.sinolife.pos.others.apl.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;

@Repository("policyAplDAO")
public class PolicyAplDAO extends AbstractPosDAO{

	@Autowired
	private CommonQueryDAO commonQueryDAO;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String policySubmit(String policyNo){
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("batchDate", commonQueryDAO.getSystemDate());
		
		queryForObject("PROC_APL_BATCH_PROCESS_SINGLE", pMap);
		
		if("0".equals(pMap.get("flag"))){
			return "执行成功";
		}else{
			return "异常："+pMap.get("message");
		}
	}
	
}
