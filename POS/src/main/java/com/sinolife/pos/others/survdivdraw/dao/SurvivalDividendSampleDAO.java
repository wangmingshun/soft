package com.sinolife.pos.others.survdivdraw.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@SuppressWarnings("rawtypes")
@Repository("survivalDividendSampleDAO")
public class SurvivalDividendSampleDAO extends AbstractPosDAO{

	/**
	 * 查询险种列表
	 * @param policyNo
	 * @return
	 */
	public List queryProductInfo(String policyNo, String type){
		return queryForList("QUERY_PRODUCT_"+type, policyNo);
	}
	
	public void submitSample(Map pMap){
		queryForObject("PROC_SAMPLE_IDVL_NOTICE_"+pMap.get("sampleType"), pMap);
		if("1".equals(pMap.get("flag"))){
			throw new RuntimeException(""+pMap.get("message"));
		}
	}
	
	/**
	 * 生存金的抽档还多调一个过程
	 * @param pMap
	 */
	public void prepareToPaySingle(Map pMap){
		queryForObject("PREPARE_TO_PAY_SINGLE", pMap);
		if("1".equals(pMap.get("flag"))){
			throw new RuntimeException(""+pMap.get("message"));
		}
	}
	
}
