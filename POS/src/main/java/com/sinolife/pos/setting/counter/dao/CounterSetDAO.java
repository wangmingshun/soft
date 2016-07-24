package com.sinolife.pos.setting.counter.dao;

import java.util.List;

import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("rawtypes")
@Repository("counterSetDAO")
public class CounterSetDAO extends SqlMapClientDaoSupport{

	public List queryCounterInfoList(String branchNo){
		return getSqlMapClientTemplate().queryForList(CounterSetDAO.class.getName()+".QUERY_COUNTER_INFO_LIST", branchNo);
	}
	
	public List queryCounterInfoListSon(String branchNo){
		return getSqlMapClientTemplate().queryForList(CounterSetDAO.class.getName()+".QUERY_COUNTER_INFO_LIST_SON", branchNo);
	}
	
	/***********************
	 * 根据主键删除某些柜面信息
	 * 实际操作是置上关闭日期update
	 * @param counterNos
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteCounterInfo(String[] counterNos){
		SqlMapClientTemplate sqlClient = getSqlMapClientTemplate();
		for (int i = 0; i < counterNos.length; i++) {
			sqlClient.update(CounterSetDAO.class.getName()+".DELETE_COUNTER_INFO", counterNos[i]);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateCounterInfo(List list){
		SqlMapClientTemplate sqlClient = getSqlMapClientTemplate();
		for (int i = 0; i < list.size(); i++) {
			sqlClient.insert(CounterSetDAO.class.getName()+".UPDATE_COUNTER_INFO", list.get(i));			
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveAddCounterInfo(List list){
		SqlMapClientTemplate sqlClient = getSqlMapClientTemplate();
		for (int i = 0; i < list.size(); i++) {
			sqlClient.insert(CounterSetDAO.class.getName()+".INSERT_COUNTER_INFO", list.get(i));			
		}
	}
}
