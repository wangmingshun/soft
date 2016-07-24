package com.sinolife.pos.riskcontrol.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;

@SuppressWarnings("rawtypes")
@Repository("batchSampleDAO")
public class BatchSampleDAO extends AbstractPosDAO{

	@Autowired
	CommonQueryDAO queryDAO;
	
	public void sampleSubmit(Map pMap){
		queryForObject("PROC_MONTH_SAMPLE_"+pMap.get("sampleType"), pMap);
		if("1".equals(pMap.get("flag"))){
			throw new RuntimeException(""+pMap.get("message"));
		}
	}

	public Map verifyNext(String sampleType, String user){
		Map map = (Map)queryForObject("QUERY_SAMPLE_VERIFY_NEXT_"+sampleType, user);
		if("1".equals(sampleType) && map != null){
			Map pMap = new HashMap();
			pMap.put("policyno", (String)map.get("POLICY_NO"));
			if(map.get("PROD_SEQ")!=null){
				pMap.put("prodSeq",Integer.valueOf(map.get("PROD_SEQ").toString()));
				pMap.put("payDueDate", queryDAO.getSystemDate());
				queryForObject("PROC_PRODUCT_INS", pMap);
				if("1".equals(pMap.get("flag"))){
					throw new RuntimeException(""+pMap.get("message"));
				}else{
					map.put("SUM_INS", pMap.get("SUMINS"));
				}
			}else {
				map.put("SUM_INS", 0);
			}
		}
		return map;
//		return (Map)queryForObject("QUERY_SAMPLE_VERIFY_NEXT_"+sampleType, user);
	}
	
	public void verifySubmit(Map pMap){
		getSqlMapClientTemplate().update(sqlName("UPDATE_SAMPLE_VERIFY_RESULT"), pMap);
	}
	
	public List confirmQuery(String sampleType, String user){
		return queryForList("QUERY_SAMPLE_CONFIRM_"+sampleType, user);
	}
	
	public void confirmSubmit(Map pMap){
		getSqlMapClientTemplate().update(sqlName("UPDATE_SAMPLE_CONFIRM"), pMap);
	}

}
