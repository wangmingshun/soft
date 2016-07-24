package com.sinolife.pos.common.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

/**
 * DAO的父类，提供一些简化的SqlMapClientTemplate类的重载方法
 */
@SuppressWarnings("rawtypes")
public class AbstractPosDAO extends SqlMapClientDaoSupport {
	
	protected Logger logger = Logger.getLogger(getClass());
	
	/**
	 * 取得默认的SQL名称前缀，为类名称加"."再加上传入参数
	 * @param sqlName sql名称
	 * @return getClass().getName() + "." + sqlName
	 */
	protected String sqlName(String name ) {
		return getClass().getName() + "." + name;
	}
	
	/**
	 * 简化调用queryForObject方法，相当于调用了：
	 * getSqlMapClientTemplate().queryForObject(sqlName(statementName), parameterObject);
	 * @param statementName
	 * @param parameterObject
	 * @return
	 */
	protected Object queryForObject(String statementName, Object parameterObject) {
		return getSqlMapClientTemplate().queryForObject(sqlName(statementName), parameterObject);
	}
	
	/**
	 * 简化调用queryForList方法，相当于调用了：
	 * getSqlMapClientTemplate().queryForList(sqlName(statementName), parameterObject);
	 * @param statementName
	 * @param parameterObject
	 * @return
	 */
	protected List queryForList(String statementName, Object parameterObject) {
		return getSqlMapClientTemplate().queryForList(sqlName(statementName), parameterObject);
	}
	
	/**
	 * 简化调用queryForObject方法，相当于调用了：
	 * getSqlMapClientTemplate().queryForObject(sqlName(statementName), parameterObject, resultObject)
	 * @param statementName
	 * @param parameterObject
	 * @param resultObject
	 * @return
	 */
	protected Object queryForObject(String statementName, Object parameterObject, Object resultObject) {
		return getSqlMapClientTemplate().queryForObject(sqlName(statementName), parameterObject, resultObject);
	}
}
