package com.sinolife.pos.setting.holiday.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@SuppressWarnings("rawtypes")
@Repository("holidaySetDAO")
public class HolidaySetDAO extends AbstractPosDAO{

	public List yearDataQuery(String year){
		return queryForList("QUERY_YEAR_LONG_VACATION", year);
	}

	/**
	 * 某日期是否已存在了
	 * @param vacation
	 * @return
	 */
	public boolean vacationExist(String vacation){
		List list = queryForList("QUERY_VACATION_EXIST", vacation);
		if(list==null || list.size()<1){
			return false;
		}
		return true;
	}
	
	/**
	 * 更新描述
	 * @param pMap
	 */
	public void updateVacationDesc(Map pMap){
		getSqlMapClientTemplate().update(sqlName("UPDATE_VACATION_DESCRIPTION"), pMap);
	}
	
	/**
	 * 插入数据
	 * @param pMap
	 */
	public void insertVacation(Map pMap){
		getSqlMapClientTemplate().update(sqlName("INSERT_LONG_VACATION"), pMap);
	}
	
}
