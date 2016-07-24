package com.sinolife.pos.report.service;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.report.dao.ListDAO;

@Service("listService")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ListService {

	@Autowired
	ListDAO dao;

	@SuppressWarnings("rawtypes")
	public Map getReport(Map pMap) {
		String sql = (String) pMap.get("sql");

		dao.getReport(sql, pMap);

		try {
			Method m = HeadKeyDefine.class.getMethod(sql, Map.class);// sql与方法同名,必须的
			m.invoke(new HeadKeyDefine(), pMap);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return pMap;
	}

	/**
	 * 查询清单，并把结果写入临时表
	 */
	public void queryList(Map pMap) {
		dao.queryList(pMap);
	}

	/**
	 * 查询清单报表表头
	 */
	public List<Map<String, String>> getListHeader(String listCode) {
		return dao.getListHeader(listCode);
	}

	/**
	 * 查询临时表数据
	 */
	public int getListCount(String queryIndex) {

		return dao.getListCount(queryIndex);

	}

	/**
	 * 分批插入表数据
	 */
	public List getListResult(String queryIndex, String headerSql,
			int startRow, int endRow) {
		return dao.getListResult(queryIndex, headerSql, startRow, endRow);
	}

	/**查询随机码是否存在*/
	public int queryRandomCode(String code){

		return dao.queryRandomCode(code);
	}

	/**查询满期提取清单控制表的sequence*/

	public int querySequence(){

		return  dao.querySequence();
	}
	/**插入随机码  发送短信*/
	@Transactional(propagation=Propagation.REQUIRED)
	public void insertCode(Map map){

		dao.insertRandomCode(map);
		dao.sendMessageCode(map);
	}
	/**查询清单，并插入临时表*/ 
	 @Transactional(propagation=Propagation.REQUIRED)
	 public Map  queryPayList(Map map){


			//根据提取码查询信息	
			Map newMap=dao.queryByMessageCode(map);
			if ("25".equals(newMap.get("LIST_CODE")))
			{ 
				//查询清单，并插入临时表 满期金提取
				dao.queryPayList(newMap);
			}
			else if("30".equals(newMap.get("LIST_CODE"))){
				//查询清单，并插入临时表 退保满期提取
				dao.querySurrPayList(newMap);
			}

			dao.updateMessageFlag(map);

		 return newMap;
	}

	 
	/**
	 * 生成异步清单提取任务
	 * 
	 * @param paraMap
	 * @return void
	 * @author GaoJiaMing
	 * @time 2014-8-8
	 */
	public void insertReportTask(Map paraMap) {
		dao.insertReportTask(paraMap);
	}

	/**
	 * 异步清单提取任务查询
	 * 
	 * @param paraMap
	 * @return List
	 * @author GaoJiaMing
	 * @time 2014-8-10
	 */
	public List queryReportTask(Map paraMap) {
		return dao.queryReportTask(paraMap);
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
		return dao.checkReportTaskStatus(paraMap);
	}
	
	/**
	 * 下载获取对应文件名
	 * @param paraMap
	 * @return String
	 * @author GaoJiaMing 
	 * @time 2014-8-14
	 */
	public String getDownLoadFileName(Map paraMap) {
		return dao.getDownLoadFileName(paraMap);
	}
	
	/**
	 * 清单作废
	 * 
	 * @param paraMap
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-9-17
	 */
	public String updatePosListControlIsValid(Map paraMap) {
		try {
			dao.updatePosListControlIsValid(paraMap);
		} catch (Exception e) {
			return "N";
		}
		return "Y";
	}
}
