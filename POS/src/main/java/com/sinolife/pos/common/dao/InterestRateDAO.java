/*
 * Powered By sino-life
 * Web Site: http://www.sino-life.com
 * Since 2010 - 2010
 */

package com.sinolife.pos.common.dao;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@SuppressWarnings("rawtypes")
@Repository("InterestRateDAO")
public class InterestRateDAO extends AbstractPosDAO { 
	
//	
//	/**
//	 * 获取利率列表
//	 */
//	@SuppressWarnings("unchecked")
//	public List<InterestRate> findAllBy(String propertyName, Object value) {		 
//		HashMap pMap=new HashMap();
//		pMap.put(propertyName, value);
//		return getSqlMapClientTemplate().queryForList(InterestRateDao.class.getName()+".getInterestRateList", pMap);
//	}
//	
//	
//	/**
//	 * 新增利率信息
//	 */
//	public void save(InterestRate entity)
//	{
//		getSqlMapClientTemplate().insert(InterestRateDao.class.getName()+".addInterestRate", entity);
//	}
//	
//	public void save(List<InterestRate> resultList)
//	{
//		super.batchInsert(InterestRateDao.class.getName()+".addInterestRate", resultList);
//	}
//	
//	/**
//	 * 修改利率信息
//	 */
//	public void update(InterestRate entity) 
//	{
//		getSqlMapClientTemplate().update(InterestRateDao.class.getName()+".updateInterestRate", entity);
//	}
//	/**
//	 * 修改利率信息
//	 */
//	public void update(List<InterestRate> resultList) 
//	{
//		super.batchUpdate(InterestRateDao.class.getName()+".updateInterestRate", resultList);
//	}
	/**
	 * 获取 利率类型
	 * @return
	 */
	public List  getInterestType(){
	   
		return getSqlMapClientTemplate().queryForList(InterestRateDAO.class.getName()+".getInterestType");
	}
	/**
	 * 获取  利率单位
	 * @return
	 */
	public List  getInterestUnit(){
		return getSqlMapClientTemplate().queryForList(InterestRateDAO.class.getName()+".getInterestUnit");
	}
	
	/**
	 * 插入利率
	 * @param pMap
	 */
	public void addInterestRate(Map pMap){
		getSqlMapClientTemplate().insert(sqlName("addInterestRate"), pMap);
	}
	
	
	/**
	 * 根据主键判断是否已经存在 
	 * @param pk
	 * @return
	 */
	public int existInterestUnit(String pk){
		int count = (Integer)getSqlMapClientTemplate().queryForObject(InterestRateDAO.class.getName()+".existInterestUnit",pk);
		return count;
	}
	
	
	/**
	 * 把List 转换成Map
	 * @param list
	 * @param key
	 * @param value
	 * @return
	 */
	public Map<String,String> convertListToMap(List<Map> list,String key,String value){
		Map<String,String> map = new LinkedHashMap<String,String>();
		for(Map rMap:list){
			map.put((String)rMap.get(key), (String)rMap.get(value));			
		}
		return map;
	}
	
//	public List<InterestRate> existInterest(Map map){	
//		return  ((List<InterestRate>)getSqlMapClientTemplate().queryForList(InterestRateDao.class.getName()+".existInterest", map));		
//
//	}
	
	
	public String getInterestUnit(String interestUnit){
		return (String) getSqlMapClientTemplate().queryForObject(InterestRateDAO.class.getName()+".getInterestUnit",interestUnit);	
	}
	public String getInterestType(String interestType){
		return (String) getSqlMapClientTemplate().queryForObject(InterestRateDAO.class.getName()+".getInterestType",interestType);	
	}

}
