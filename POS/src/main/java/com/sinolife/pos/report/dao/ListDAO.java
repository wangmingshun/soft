package com.sinolife.pos.report.dao;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@Repository("listDAO")
public class ListDAO extends AbstractPosDAO {

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
	 * 查询清单，并把结果写入临时表
	 */
	public void queryList(Map pMap) {
		String ibatisSqlId = (String) pMap.get("ibatisSqlId");
		getSqlMapClientTemplate().queryForList(
				ListDAO.class.getName() + ibatisSqlId, pMap);
	}

	/**
	 * 查询清单报表表头
	 */
	public List<Map<String, String>> getListHeader(String listCode) {

		return getSqlMapClientTemplate().queryForList(
				ListDAO.class.getName() + ".getListCode", listCode);
	}
	
//	
//	public void exportDb2File()
//	{
//		NamedParameterJdbcTemplate namedParameterJdbcTemplate=(NamedParameterJdbcTemplate) PlatformContext.getApplicationContext().getBean("POSJdbcTemplate");
//		HashMap p=new HashMap();
//		p.put("id", "dd");
//		File rt=namedParameterJdbcTemplate.query("select * ddd where d=:id", p, new  ResultSetExtractor(){
//
//			 
//			public Object extractData(ResultSet rs) throws SQLException,
//					DataAccessException {
//				
//				File file=new File();
//				
//				 while(rs.next())
//				 {
//					 //
//				 }
//				return file;
//			}
//			
//		});
//}

	/**
	 * 查询临时表数据
	 */
	public int getListCount(String queryIndex) {

		return ((Integer) getSqlMapClientTemplate().queryForObject(
				ListDAO.class.getName() + ".getListCount", queryIndex))
				.intValue();

	}

	/**
	 * 查询分页临时表数据
	 */
	public List getListResult(String queryIndex, String headerSql,
			int startRow, int endRow) {
		String sql = "select "
				+ headerSql
				+ " from pos_list_temp a where a.query_index =? and a.number0 >= ? and a.number0 <= ? order by a.number0";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(this.getDataSource());
		return jdbcTemplate.queryForList(sql, new Object[] { queryIndex,
				startRow, endRow });
	}

	/**查询随机码是否存在*/
	public int queryRandomCode(String code){

	
		return (Integer) queryForObject("queryCode", code);

	}
	/**查询满期提取清单控制表的sequence*/
	public int querySequence(){

		return (Integer) queryForObject("querySequence", null);
	}
	/**插入随机码  */
	public void insertRandomCode(Map map){

		getSqlMapClientTemplate().insert(sqlName("insertCode"), map);
	}
	/**发送短信*/
	public void sendMessageCode(Map map){
		queryForObject("sms_send_ver_code", map);
		if (!"0".equals(map.get("FLAG"))) {
			throw new RuntimeException("" + map.get("message"));
		}
	}
	/**根据随机码查询信息*/
	public Map queryByMessageCode(Map map){

		return  (Map) queryForObject("queryByMessageCode", map);

	}
	/**更新提取标志*/
	public void updateMessageFlag(Map map){

		getSqlMapClientTemplate().update(sqlName("updateMessageFlag"), map);

	}
	/**查询往清单临时表写数据的参数*/

	public Map queryParamMap(Map map ){

		return  (Map) queryForObject("queryParamMap", map);

	}
	/**查询清单，并插入临时表 满期金提取*/ 

	public void queryPayList(Map map){

		queryForObject("queryPayList", map);
		if (!"0".equals(map.get("FLAG"))) {
			throw new RuntimeException("" + map.get("message"));
		}


	}
	
	/**查询清单，并插入临时表 退保满期金提取*/ 

	public void querySurrPayList(Map map){

		queryForObject("querySurrPayList", map);
		if (!"0".equals(map.get("FLAG"))) {
			throw new RuntimeException("" + map.get("message"));
		}


	}

	/**
	 * 生成异步清单提取任务
	 * 
	 * @param map
	 * @return void
	 * @author GaoJiaMing
	 * @time 2014-8-8
	 */
	public void insertReportTask(Map map) {
		getSqlMapClientTemplate().insert(sqlName("insertReportTask"), map);
	}

	/**
	 * 异步清单提取任务查询
	 * 
	 * @param map
	 * @return List
	 * @author GaoJiaMing
	 * @time 2014-8-10
	 */
	public List queryReportTask(Map map) {
		return queryForList("queryReportTask", map);
	}

	/**
	 * 更新报表任务状态和开始时间
	 * 
	 * @return List<Map<String,Object>>
	 * @author GaoJiaMing
	 * @time 2014-8-11
	 */
	@SuppressWarnings("unchecked")
	public void updateReportTaskStart(Map<String, Object> paraMap) {
		getSqlMapClientTemplate().update(sqlName("updateReportTaskStart"), paraMap);
	}

	/**
	 * 更新报表任务结束数据
	 * 
	 * @return List<Map<String,Object>>
	 * @author GaoJiaMing
	 * @time 2014-8-11
	 */
	@SuppressWarnings("unchecked")
	public void updateReportTaskEnd(Map<String, Object> paraMap) {
		getSqlMapClientTemplate().update(sqlName("updateReportTaskEnd"), paraMap);
	}

	/**
	 * 校验同一用户同一清单是否重复提交
	 * 
	 * @param paraMap
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-8-13
	 */
	public String checkReportTaskStatus(Map paraMap) {
		return (String) queryForObject("checkReportTaskStatus", paraMap);
	}
	
	/**
	 * 下载获取对应文件名
	 * @param paraMap
	 * @return String
	 * @author GaoJiaMing 
	 * @time 2014-8-14
	 */
	public String getDownLoadFileName(Map paraMap) {
		return (String) queryForObject("getDownLoadFileName", paraMap);
	}
	
	/**
	 * 清单作废
	 * 
	 * @param void
	 * @author GaoJiaMing
	 * @time 2014-9-17
	 */
	public void updatePosListControlIsValid(Map paraMap) {
		getSqlMapClientTemplate().update(sqlName("updatePosListControlIsValid"), paraMap);
	}
}
