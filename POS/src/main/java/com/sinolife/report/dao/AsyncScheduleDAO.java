package com.sinolife.report.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;
@Repository
public class AsyncScheduleDAO extends AbstractPosDAO{
	/**
	 * 查询报表任务
	 * 
	 * @return List<Map<String,Object>>
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryReportTask() {
		return queryForList("queryReportTask", null);
	}

	/**
	 * 锁定报表任务
	 * 
	 * @param paraMap
	 * @return boolean
	 */
	public boolean lockReportTask(Map<String, Object> paraMap) {
		try {
			return "Y".equals(queryForObject("lockReportTask", paraMap));
		} catch (Exception e) {
			return false;
		}
	}
}
