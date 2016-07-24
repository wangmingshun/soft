package com.sinolife.pos.todolist.acceptentry.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Repository("acceptEntryDAO")
public class AcceptEntryDAO extends AbstractPosDAO{

	/**
	 * 查询待受理录入保全件列表
	 * @param acceptor 受理人
	 * @return List&lt;Map&lt;String, Object&gt;&gt;
	 */
	public List<Map<String, Object>> queryBatchNotCompleteList(String acceptor) {
		return queryForList("queryBatchNotCompleteList", acceptor);
	}
	
	public String queryPosNoBatchNo(String posNo, String acceptor){
		Map pMap = new HashMap();
		pMap.put("posNo", posNo);
		pMap.put("acceptor", acceptor);
		return (String)queryForObject("queryPosNoBatchNo", pMap);
	}
	
	/**
	 * 查询保全员相关的保全信息列表
	 * @param acceptor
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<Map<String, Object>>  queryBatchAcceptList(String acceptor, Date startDate,Date endDate){
		Map pMap = new HashMap();
		pMap.put("acceptor", acceptor);
		pMap.put("startDate", startDate);
		pMap.put("endDate", endDate);
		return (List<Map<String,Object>>)queryForList("queryBatchAcceptList", pMap);
	}
	/**
	 * 代理人的电子回执回销记录查询
	 * @param startDate
	 * @param endDate
	 * @param flag
	 * @param policyNo
	 * @param agentNo
	 * @return List<Map<String,Object>>
	 * @author Zhangyi
	 * @time 2015-3-25
	 */
	public List<Map<String, Object>>  queryReceivePolicyMessageList(Map param){
		
		return (List<Map<String,Object>>)queryForList("queryreceivePolicyMessageList", param);
		
	}
	
}
