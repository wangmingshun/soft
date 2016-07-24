package com.sinolife.pos.setting.user.jobswitch.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.setting.user.jobswitch.dao.JobSwitchDAO;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Service("jobSwitchService")
public class JobSwitchService {

	@Autowired
	JobSwitchDAO jobDAO;

	/**
	 * 查询目前有哪些任务可以拿来进行交接
	 * @param userId
	 * @return
	 */
	public List queryTaskListForSwitch(String userId) {
		return jobDAO.queryTaskListForSwitch(userId);
	}
	
	/**
	 * 已交接待确认任务列表
	 * @param userId
	 * @return
	 */
	public List queryTaskListConfirming(String userId) {
		return jobDAO.queryTaskListConfirming(userId);
	}
	
	/**
	 * 查询有当前工作类型交接情况
	 * @param userId
	 * @return
	 */
	public Map queryWorkStatusForSwitch(String userId){
		Map rstMap = new HashMap();
		
		Map pMap = new HashMap();
		pMap.put("handover", userId);

		pMap.put("taskType", "1");
		rstMap.put("POS_UNDERTAKER", jobDAO.queryWorkStatusForSwitch(pMap));
		
		pMap.put("taskType", "2");
		rstMap.put("APPROVE_UNDERTAKER", jobDAO.queryWorkStatusForSwitch(pMap));
		
		pMap.put("taskType", "3");
		rstMap.put("PROBLEM_UNDERTAKER", jobDAO.queryWorkStatusForSwitch(pMap));
		
		pMap.put("taskType", "4");
		rstMap.put("UWPRINT_UNDERTAKER", jobDAO.queryWorkStatusForSwitch(pMap));
		
		pMap.put("taskType", "5");
		rstMap.put("UWFEEDBACK_UNDERTAKER", jobDAO.queryWorkStatusForSwitch(pMap));
		
		return rstMap;
	}
	
	/**
	 * 取消工作类型的交接
	 * @param userId
	 */
	public void cancelWorkSwitch(String userId){
		jobDAO.cancelWorkSwitch(userId);
	}
	
	/**
	 * 提交任务细项的交接
	 * @param pos
	 * @param approve
	 * @param problem
	 * @param handover
	 * @param undertaker
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void switchTaskToUser(String[] pos,String[] approve,String[] problem, Map pMap){
		for (int i = 0; pos!=null && i < pos.length; i++) {
			pMap.put("taskType", "1");
			switchTaskAide(pMap,pos[i]);
		}
		for (int i = 0; approve!=null && i < approve.length; i++) {
			pMap.put("taskType", "2");
			switchTaskAide(pMap,approve[i]);
		}
		for (int i = 0; problem!=null && i < problem.length; i++) {
			pMap.put("taskType", "3");
			switchTaskAide(pMap,problem[i]);
		}
	}
	private void switchTaskAide(Map pMap,String pk){
		pMap.put("posNo", pk.substring(0, pk.indexOf("+")));
		pMap.put("correspondingPK", pk.substring(pk.indexOf("+")+1));
		if(jobDAO.queryTaskHanded(pMap)){
			jobDAO.updateTaskHandover(pMap);
		}else{
			jobDAO.insertTaskHandover(pMap);			
		}
	}
	
	/**
	 * 提交工作类型的交接
	 * @param works
	 * @param pMap
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void switchWorkToUser(Map pMap){
		String[] works = (String[])pMap.get("works");
		for (int i = 0; works!=null && i < works.length; i++) {
			pMap.put("type", works[i]);
			String workNo = jobDAO.workSwitched(pMap);
			if(StringUtils.isNotBlank(workNo)){//已存在则更新
				pMap.put("workHandoverNo", workNo);
				jobDAO.updateWorkHandover(pMap);
				
			}else{
				jobDAO.insertWorkHandover(pMap);
			}
		}
	}
	
	/*****************************
	 * 查询当前用户待确认的工作交接
	 * 
	 * @param userName
	 * @return
	 */
	public Map jobSwitchForConfirm(String userName) {
		Map rstMap = new HashMap();
		rstMap.put("taskList", jobDAO.taskListForConfirm(userName));
		rstMap.put("workList", jobDAO.workListForConfirm(userName));
		return rstMap;
	}

	/**
	 * 提交确认结果
	 * 
	 * @param tasks
	 * @param works
	 * @param status
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void confirm(String[] tasks, String[] works, String status) {
		Map pMap = new HashMap();
		pMap.put("status", status);
		for (int i = 0; tasks != null && i < tasks.length; i++) {
			pMap.put("taskType", tasks[i].substring(0, tasks[i].indexOf("+")));
			pMap.put("pkSerial", tasks[i].substring(tasks[i].indexOf("+")+1));
			jobDAO.taskConfirm(pMap);
		}
		for (int i = 0; works != null && i < works.length; i++) {
			pMap.put("pkSerial", works[i]);
			jobDAO.workConfirm(pMap);
		}
	}
	
	public String amountDaysGradeCheck(String handover, String undertaker){
		Map pMap = new HashMap();
		pMap.put("handover", handover);
		pMap.put("undertaker", undertaker);
		return jobDAO.amountDaysGradeCheck(pMap);
	}
	
	/**
	 * 此次确认的交接中，涉及哪些承接人
	 * @param tasks
	 * @param works
	 * @return
	 */
	public String[] queryHandoversByPK(String[] tasks, String[] works){
		Set<String> set = new HashSet<String>();
		
		for (int i = 0; tasks != null && i < tasks.length; i++) {
			set.add(jobDAO.queryHandover(tasks[i].substring(tasks[i].indexOf("+")+1), "TASK"));
		}
		for (int i = 0; works != null && i < works.length; i++) {
			set.add(jobDAO.queryHandover(works[i], "WORK"));
		}
		
		String[] users = new String[set.size()];
		return set.toArray(users);
	}
	
}
