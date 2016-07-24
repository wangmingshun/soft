package com.sinolife.report.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;
import com.sinolife.pos.report.dao.ListDAO;
@Repository("AsyncListDao")
public class AsyncListDao extends AbstractPosDAO{
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getReport(String sql, Map pMap) {
		Logger.getLogger(getClass()).info("\n____report pMap=" + pMap);

		if (sql.startsWith("select")) {// 直接是select查询sql的情况下
			pMap.put("queryResult", queryForList(sql, pMap));

		} else {// 专门的清单pkg查询
			queryForObject(sql, pMap);
			if ("1".equals(pMap.get("flag"))) {
				throw new RuntimeException("" + pMap.get("message"));
			}
		}

		return pMap;
	}
	
	/**
	 * 异步清单
	 * @param map
	 * @return
	 * @author Luoyonggang
	 */
	public List queryAsyncTask(Map map) {
		return queryForList("queryAsyncTask", map);
	}
	
	/**
	 * 生成异步清单提取任务
	 * 
	 * @param map
	 * @return void
	 * @author Luoyonggang
	 */
	public void insertReportTask(Map map) {
		getSqlMapClientTemplate().insert(sqlName("insertReportTask"), map);
	}
	
	/**
	 * 清单作废
	 * 
	 * @param void
	 * @author Luoyonggang
	 */
	public void updatePosListControlIsValid(Map paraMap) {
		getSqlMapClientTemplate().update(sqlName("updatePosListControlIsValid"), paraMap);
	}
	/**
	 * 更新报表任务状态和开始时间
	 * 
	 * @return List<Map<String,Object>>
	 * @author Luoyonggang
	 */
	@SuppressWarnings("unchecked")
	public void updateReportTaskStart(Map<String, Object> paraMap) {
		getSqlMapClientTemplate().update(sqlName("updateReportTaskStart"), paraMap);
	}
	
	/**
	 * 更新报表任务结束数据
	 * 
	 * @return List<Map<String,Object>>
	 * @author Luoyonggang
	 */
	@SuppressWarnings("unchecked")
	public void updateReportTaskEnd(Map<String, Object> paraMap) {
		getSqlMapClientTemplate().update(sqlName("updateReportTaskEnd"), paraMap);
	}
	
	/**
	 * 查询清单报表表头
	 * @author Luoyonggang
	 */
	public List<Map<String, String>> getListHeader(String listCode) {

		return getSqlMapClientTemplate().queryForList(
				AsyncListDao.class.getName() + ".asyncGetListCode", listCode);
	}
	
	/**
	 * 查询临时表数据
	 * @author Luoyonggang
	 */
	public int getListCount(String queryIndex) {

		return ((Integer) getSqlMapClientTemplate().queryForObject(
				AsyncListDao.class.getName() + ".getListCount", queryIndex))
				.intValue();

	}
	
	/**
	 * 查询分页临时表数据
	 * @author Luoyonggang
	 */
	public List getListResult(String queryIndex, String headerSql,
			int startRow, int endRow) {
		String sql = "select "
			+ headerSql
			+ " from listintf.async_list_data a where a.query_index =? order by a.data_seq";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(this.getDataSource());
		return jdbcTemplate.queryForList(sql, new Object[] { queryIndex});
	}
	
	/**
	 * 异步清单提取任务查询
	 * 
	 * @param map
	 * @return List
	 * @author Luoyonggang
	 */
	public List queryReportTask(Map map) {
		return queryForList("queryReportTask", map);
	}
	
	/**
	 * 下载获取对应文件名
	 * @param paraMap
	 * @return String
	 * @author Luoyonggang
	 */
	public String getDownLoadFileName(Map paraMap) {
		return (String) queryForObject("getDownLoadFileName", paraMap);
	}
	
	/**
	 * 校验同一用户同一清单是否重复提交
	 * 
	 * @param paraMap
	 * @return String
	 * @author Luoyonggang
	 */
	public String checkReportTaskStatus(Map paraMap) {
		return (String) queryForObject("checkReportTaskStatus", paraMap);
	}
	
	/**
	 * 查询清单，并把结果写入临时表
	 */
	public void queryList(Map pMap) {
		String ibatisSqlId = (String) pMap.get("ibatisSqlId");
		getSqlMapClientTemplate().queryForList(
				ListDAO.class.getName() + ibatisSqlId, pMap);
	}
	
	/**
	 * oracle分页数据查询
	 * @return List
	 * @author WangMingShun
	 * @date 2016-3-14
	 */
	public List getOracleListResult(String queryIndex, String headerSql,
			int startRow, int endRow) {
		String sql = " select " + headerSql
			+ " from (select t.*,rownum rowno from (select * from listintf.async_list_data p where p.query_index = ? order by p.data_seq) t where rownum <= " + endRow +") tt"
	     + " where tt.rowno >= " + startRow;
		JdbcTemplate jdbcTemplate = new JdbcTemplate(this.getDataSource());
		return jdbcTemplate.queryForList(sql, new Object[] { queryIndex});
	}
}
