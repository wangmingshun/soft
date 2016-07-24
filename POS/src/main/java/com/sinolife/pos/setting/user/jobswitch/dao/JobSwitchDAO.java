package com.sinolife.pos.setting.user.jobswitch.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@SuppressWarnings("rawtypes")
@Repository("jobSwitchDAO")
public class JobSwitchDAO extends AbstractPosDAO{

	
	public List queryTaskListForSwitch(String userId){
		return queryForList("QUERY_TASK_LIST_FOR_SWITCH", userId);
	}
	
	public List queryTaskListConfirming(String userId){
		return queryForList("QUERY_TASK_LIST_CONFIRMING", userId);
	}
	
	public String queryWorkStatusForSwitch(Map pMap){
		return (String)queryForObject("QUERY_WORK_STATUS_FOR_SWITCH", pMap);
	}
	
	public void cancelWorkSwitch(String userId){
		getSqlMapClientTemplate().update(sqlName("UPDATE_WORK_HANDOVER_END_DATE"),userId);
	}
	
	/**
	 * 先查询该posNo是否交接过了，由于主键限制，交接过的只能更新不能再插入了
	 * 交接后被拒绝会有二次交接
	 * @param pMap
	 */
	public boolean queryTaskHanded(Map pMap){
		List list = queryForList("QUERY_TASK_HANDED_POSNO", pMap);
		return (list!=null && list.size()>0);
	}
	
	/**
	 * 写入任务交接表一条记录
	 * @param pMap
	 */
	public void insertTaskHandover(Map pMap){
		getSqlMapClientTemplate().insert(sqlName("INSERT_TASK_HANDOVER"), pMap);
	}
	
	/**
	 * 更新任务交接表一条记录
	 * @param pMap
	 */
	public void updateTaskHandover(Map pMap){
		getSqlMapClientTemplate().update(sqlName("UPDATE_TASK_HANDOVER"), pMap);
	}
	
	/**
	 * 已存在工作类型交接记录
	 * @param pMap
	 */
	public String workSwitched(Map pMap){
		return (String)queryForObject("QUERY_WORK_SWITCHED", pMap);
	}
	
	/**
	 * 更新工作类型交接表记录
	 * @param pMap
	 */
	public void updateWorkHandover(Map pMap){
		getSqlMapClientTemplate().insert(sqlName("UPDATE_WORK_HANDOVER"), pMap);
	}
	
	/**
	 * 写入工作类型交接表一条记录
	 * @param pMap
	 */
	public void insertWorkHandover(Map pMap){
		getSqlMapClientTemplate().insert(sqlName("INSERT_WORK_HANDOVER"), pMap);
	}
	
	public List taskListForConfirm(String userName){
		return queryForList("QUERY_TASK_LIST_CONFIRM", userName);
	}
	public List workListForConfirm(String userName){
		return queryForList("QUERY_WORK_LIST_CONFIRM", userName);
	}
	
	/**
	 * 任务交接
	 * @param pMap
	 */
	public void taskConfirm(Map pMap){
		//修改工作交接表
		getSqlMapClientTemplate().update(sqlName("UPDATE_TASK_HANDOVER_STATUS"), pMap);

		//如果是接受交接，修改对应业务数据表的处理人
		if("2".equals(pMap.get("status"))){
			getSqlMapClientTemplate().update(sqlName("UPDATE_PROCESSOR_"+pMap.get("taskType")), pMap.get("pkSerial"));
			
			//如果是问题件确认接受交接的话，还要调用问题件系统的接口
			if("3".equals(pMap.get("taskType"))){
				pMap = (Map)queryForObject("QUERY_PROBLEM_CHANGE_ACCESSOR", pMap.get("pkSerial"));//先查询到问题件号
				queryForObject("PROC_LN_CHANGE_ACCESSOR", pMap);//调用问题件系统接口
				if("N".equals(pMap.get("FLAG"))){
					throw new RuntimeException(""+pMap.get("MESSAGE"));//执行失败
				}
			}
		}
	}

	/**
	 * 类型交接
	 * @param pMap
	 */
	public void workConfirm(Map pMap){
		getSqlMapClientTemplate().update(sqlName("UPDATE_WORK_HANDOVER_STATUS"), pMap);
		if("2".equals(pMap.get("status"))){
			getSqlMapClientTemplate().update(sqlName("UPDATE_WORK_UNDERTAKER_TAKED"), pMap.get("pkSerial"));
		}
	}
	
	public String amountDaysGradeCheck(Map pMap){
		return (String)queryForObject("UNDERTAKER_AMOUNT_DAYS_GRADE_CHECK", pMap);
	}
	
	public String queryHandover(String pk,String table){
		return (String)queryForObject("QUERY_HANDOVER_PK_"+table, pk);
	}
	
}
