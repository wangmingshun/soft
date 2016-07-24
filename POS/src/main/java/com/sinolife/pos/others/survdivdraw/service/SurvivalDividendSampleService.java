package com.sinolife.pos.others.survdivdraw.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.others.survdivdraw.dao.SurvivalDividendSampleDAO;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Service("survivalDividendSampleService")
public class SurvivalDividendSampleService {

	@Autowired
	SurvivalDividendSampleDAO sampleDAO;
	
	@Autowired
	CommonQueryDAO queryDAO;
	
	public List queryProductInfo(String policyNo, String type){
		return sampleDAO.queryProductInfo(policyNo, type);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void submitSample(Map pMap){
		String flag = "Y";
		if("1".equals(pMap.get("sampleType"))){//生存金的抽档接口，日期参数变态
			Date sysdate = queryDAO.getSystemDate();
			String month = (String)pMap.get("dueMonth");
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.YEAR, Integer.parseInt(month.substring(0,month.indexOf("-"))));
			c.set(Calendar.MONTH, Integer.parseInt(month.substring(month.indexOf("-")+1))-1);
			c.set(Calendar.DAY_OF_MONTH, 1);
			pMap.put("startDate", c.getTime());
			
			c.add(Calendar.MONTH, 1);
			c.add(Calendar.DAY_OF_MONTH, -1);
			pMap.put("endDate", c.getTime());
			
			if(sysdate.compareTo((Date)pMap.get("startDate"))<0){
				flag = "N";
				
			}else if(sysdate.compareTo((Date)pMap.get("endDate"))<0){
				pMap.put("tailDate", sysdate);	
			}else if(sysdate.compareTo((Date)pMap.get("endDate"))>=0){
				pMap.put("tailDate", pMap.get("endDate"));
			}
		}else{
			pMap.put("dueMonth",new Date());//应领月份
		}
		
		sampleDAO.submitSample(pMap);
		
		if("1".equals(pMap.get("sampleType")) && "Y".equals(flag)){
//			sampleDAO.prepareToPaySingle(pMap);
		}
	}
}
