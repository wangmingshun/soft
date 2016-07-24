package com.sinolife.pos.setting.holiday.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.setting.holiday.dao.HolidaySetDAO;

@SuppressWarnings("rawtypes")
@Service("holidaySetService")
public class HolidaySetService {

	@Autowired
	HolidaySetDAO holidayDAO;
	
	public List yearDataQuery(String year){
		return holidayDAO.yearDataQuery(year);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void save(List list){
		for (int i = 0; list!=null && i < list.size(); i++) {
			Map pMap = (Map)list.get(i);
			if(holidayDAO.vacationExist(""+pMap.get("vacationDate"))){
				holidayDAO.updateVacationDesc(pMap);//更新，其实只能更新描述，没有多大意义
				
			}else{
				holidayDAO.insertVacation(pMap);
			}
		}
	}
}
