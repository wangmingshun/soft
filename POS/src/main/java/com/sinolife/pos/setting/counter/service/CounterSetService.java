package com.sinolife.pos.setting.counter.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinolife.pos.setting.counter.dao.CounterSetDAO;

@SuppressWarnings("rawtypes")
@Service("counterSetService")
public class CounterSetService {

	@Autowired
	CounterSetDAO counterDAO;
	
	/**
	 * @param branchNo  机构
	 * @param sonBranch 是否包含下级
	 * @return
	 */
	public List queryCounterInfoList(String branchNo, String sonBranch){
		if("Y".equals(sonBranch)){
			return counterDAO.queryCounterInfoListSon(branchNo);
		}else{
			return counterDAO.queryCounterInfoList(branchNo);			
		}
	}
	
	public void deleteCounterInfo(String[] counterNos){
		counterDAO.deleteCounterInfo(counterNos);
	}
	
	public void updateCounterInfo(List list){
		counterDAO.updateCounterInfo(list);
	}
	
	public void saveAddCounterInfo(List list){
		counterDAO.saveAddCounterInfo(list);
	}

}
