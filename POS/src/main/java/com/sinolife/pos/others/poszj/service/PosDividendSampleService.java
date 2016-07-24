package com.sinolife.pos.others.poszj.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.others.poszj.dao.PosDividendSampleDAO;

@Transactional(propagation = Propagation.REQUIRED)
@SuppressWarnings({ "rawtypes", "unchecked" })
@Service("posDividendSampleService")
public class PosDividendSampleService {

	@Autowired
	PosDividendSampleDAO sampleDAO;
	
	
	public void submitSample(Map pMap){
		sampleDAO.submitSample(pMap);
	}
	
	public List<?> queryPosInfo(String posNo){
		return sampleDAO.queryPosInfo(posNo);
	}
	
	public List<?> queryAllPos(Map pMap){
		return sampleDAO.queryAllPos(pMap);
	}
	
	public void addOrUpdate(Map pMap){
		sampleDAO.addOrUpdate(pMap);;
	}
	
}
