package com.sinolife.pos.schedule.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.tags.Param;

import com.sinolife.pos.common.dao.AbstractPosDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.print.dto.PosNoteDTO;
import com.sinolife.pos.schedule.dto.NoticePrintBatchTask;

@Repository
public class ScheduleDAO extends AbstractPosDAO {

	/**
	 * 查询待处理规则检查保全件列表
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryProcessRuleCheckList() {
		return queryForList("queryProcessRuleCheckList", null);
	}

	/**
	 * 查询待核保规则检查保全件列表
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryUndwrtRuleCheckList() {
		return queryForList("queryUndwrtRuleCheckList", null);
	}

	/**
	 * 做完联系方式变更中对官网客户的联系电话的一个修改
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryWebMobileChangedList() {
		return queryForList("queryWebMobileChangedList", null);
	}

	/**
	 * 锁定数据库中的保全记录
	 * 
	 * @param posNo
	 * @return
	 */
	public boolean lockPosInfoRecord(String posNo) {
		try {
			return "Y".equals(getSqlMapClientTemplate().queryForObject(
					ScheduleDAO.class.getName() + ".lockPosInfoRecord", posNo));
		} catch (Exception e) {
			logger.debug(e);
			return false;
		}
	}

	/**
	 * 校验数据当前的状态
	 * 
	 * @param posNo
	 * @param acceptStatusCode
	 * @return
	 */
	public boolean verifyAcceptStatus(String posNo, String acceptStatusCode) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("posNo", posNo);
		paraMap.put("acceptStatusCode", acceptStatusCode);
		return "Y".equals(getSqlMapClientTemplate().queryForObject(
				ScheduleDAO.class.getName() + ".verifyAcceptStatus", paraMap));
	}

	/**
	 * 查询需要生成PDF并同步至前置机的通知书信息
	 * 
	 * @param localZip
	 * @param batchSize
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PosNoteDTO> queryPosNoteNeedProcess(String localZip,
			Integer batchSize) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("localZip", localZip);
		paraMap.put("batchSize", batchSize);
		return queryForList("queryPosNoteNeedProcess", paraMap);
	}

	/**
	 * 定时自动流转
	 * 
	 * @param sysdate
	 */
	public void autoWorkflow(Date sysdate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_sysdate", sysdate);
		queryForObject("autoWorkflow", paraMap);
	}

	/**
	 * 取得PDF文件的索引
	 * 
	 * @param sysdate
	 * @return
	 */
	public int getPrintSeq(Date sysdate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_clac_date", sysdate);
		queryForObject("getPrintSeq", paraMap);
		if (!"0".equals(paraMap.get("p_flag"))) {
			throw new RuntimeException("取文件序号出错：" + paraMap.get("p_message"));
		}
		return ((BigDecimal) paraMap.get("p_print_seq")).intValue();
	}

	/**
	 * 查询通知书批次打印处理任务列表
	 * 
	 * @param limitTotal
	 *            限制返回条数
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> queryPosNoteBatchTask(int limitTotal) {
		return queryForList("queryPosNoteBatchTask", limitTotal);
	}

	/**
	 * 锁定通知书批次打印任务表数据库记录
	 * 
	 * @param taskId
	 * @return
	 */
	public boolean lockNoticePrintBatchTask(String taskId) {
		try {
			return "Y"
					.equals(queryForObject("lockNoticePrintBatchTask", taskId));
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 更新通知书批次打印任务表
	 * 
	 * @param taskId
	 * @param colName
	 * @param colValue
	 */
	public void updateNoticePrintBatchTask(String taskId, String colName,
			Object colValue) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("task_id", taskId);
		paraMap.put(colName.trim().toLowerCase(), colValue);
		getSqlMapClientTemplate().update(sqlName("updateNoticePrintBatchTask"),
				paraMap, 1);
	}

	/**
	 * 更新任务的最新活动时间，保持当前处理线程对任务独占有效性
	 * 
	 * @param taskId
	 */
	public void touchNoticePrintBatchTask(String taskId) {
		getSqlMapClientTemplate().update(sqlName("touchNoticePrintBatchTask"),
				taskId, 1);
	}

	/**
	 * 根据任务ID查询通知书批次打印任务
	 * 
	 * @param taskId
	 * @return
	 */
	public NoticePrintBatchTask queryNoticePrintBatchTaskById(String taskId) {
		return (NoticePrintBatchTask) queryForObject(
				"queryNoticePrintBatchTaskById", taskId);
	}

	/**
	 * 插入保全批次打印任务记录
	 * 
	 * @param task
	 */
	public void insertNoticePrintBatchTask(NoticePrintBatchTask task) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("table", "pos_notice_print_batch_task");
		paraMap.put("column", "task_id");
		paraMap.put("owner", "poslogtmp");
		getSqlMapClientTemplate().queryForObject(
				CommonQueryDAO.class.getName() + ".PROC_PUB_GET_SEQ", paraMap);
		if (!"1".equals(paraMap.get("flag"))) {
			throw new RuntimeException("获取sequence失败：" + paraMap.get("message"));
		}
		String sequence = (String) paraMap.get("sequence");
		task.setTaskId(sequence);
		getSqlMapClientTemplate().insert(sqlName("insertNoticePrintBatchTask"),
				task);
	}

	/**
	 * 查询用户批次打印任务
	 * 
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<NoticePrintBatchTask> queryNoticePrintBatchTaskByUserId(
			String userId) {
		return queryForList("queryNoticePrintBatchTaskByUserId", userId);
	}

	/**
	 * 更新任务的最新活动时间，保持当前处理线程对任务独占有效性
	 * 
	 * @param taskId
	 */
	public void updateDetailChangeWebPhone(String posNo) {
		getSqlMapClientTemplate().update(sqlName("updateDetailChangeWebPhone"),
				posNo, 1);
	}

	/**
	 * 操作完加微信保后未发送电子批单的保全
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryNothandPdfEmail() {
		return queryForList("queryNothandPdfEmail", null);
	}

	/**
	 * 更新任务更新电子批单是否已经发送
	 * 
	 * @param taskId
	 */
	public void updatePosInfoEfileID(String posNo, String flie_id) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_pos_no", posNo);
		paraMap.put("p_e_ent_flie_id", flie_id);
		queryForObject("updatePosInfoEfileID", paraMap);
	}

	/**
	 * 验证存在需要发送批单的保全记录，将该记录锁定，并返回保全号码
	 * 
	 * @param policyNo
	 * @return
	 */
	public String lockPosInfoForSendEntMail(String posNo) {
		try {
			return (String) queryForObject("lockPosInfoForSendEntMail", posNo);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 需要发送电子信函的数据
	 * 
	 * @param paraMap
	 * @return List<PosNoteDTO>
	 * @author GaoJiaMing
	 * @time 2014-6-26
	 */
	@SuppressWarnings("unchecked")
	public List<PosNoteDTO> queryPosNoteNeedELetter(Map<String, Object> paraMap) {
		return queryForList("queryPosNoteNeedELetter", paraMap);
	}

	/**
	 * 查询报表任务
	 * 
	 * @return List<Map<String,Object>>
	 * @author GaoJiaMing
	 * @time 2014-8-11
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
	 * @author GaoJiaMing
	 * @time 2014-9-4
	 */
	public boolean lockReportTask(Map<String, Object> paraMap) {
		try {
			return "Y".equals(queryForObject("lockReportTask", paraMap));
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 保全定时工作流推动失败处理
	 * 
	 * @param posNo
	 * @param wrongMessage
	 */
	public Map<String, Object> workflowFailure(String posNo, String wrongMessage) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_pos_no", posNo);
		paraMap.put("p_wrong_message", wrongMessage);
		queryForObject("workflowFailure", paraMap);
		return paraMap;
	}

	/**
	 * 保全定时工作流推动失败后继续推动
	 * 
	 * @param posNo
	 */
	public Map<String, Object> workfloRunAgain(String posNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_pos_no", posNo);
		queryForObject("workfloRunAgain", paraMap);
		return paraMap;

	}

	/**
	 * 成长红包在柜面加保后通知微信端
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryAddPremList() {
		return queryForList("queryAddPremList", null);
	}

	/**
	 * 线下加保锁定记录
	 * 
	 * @param posNo
	 * @return
	 */
	public boolean lockNoticeAddPrem(String posNo) {
		try {
			return "Y".equals((String) queryForObject("lockNoticeAddPrem",
					posNo));
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 线下加保通知后更新状态
	 * 
	 * @param taskId
	 */
	public void updateNoticeAddPrem(String posNo) {
		getSqlMapClientTemplate().update(sqlName("updateNoticeAddPrem"), posNo,
				1);
	}
	/**
	 * 投保人变更后通知官网解除加挂
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryAppChangeList() {
		return queryForList("queryAppChangeList", null);
	}

	
	/**
	 * 通知官网解除加挂后更新状态
	 * 
	 * @param taskId
	 */
	public void updateNoticeAppChange(String posNo) {
		getSqlMapClientTemplate().update(sqlName("updateNoticeAppChange"),
				posNo, 1);
	}
	
	/**
	 * 获取待发送短信数据  "客户满意度调查"
	 * @methodName: querySatisfactionList
	 * @return List
	 * @author WangMingShun
	 * @date 2016-2-3
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> querySatisfactionList() {
		return queryForList("querySatisfactionList", null);
	}
	
	/**
	 * 锁住 待发送短信数据的单条记录
	 * @methodName: lockSatisfaction
	 * @return boolean
	 * @author WangMingShun
	 * @date 2016-2-3
	 * @throws
	 */
	public boolean lockSatisfaction(String posBatchNo) {
		try {
			return "Y".equals((String) queryForObject("lockSatisfaction", posBatchNo));
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 通过批次号查询发送号码
	 * @methodName: queryPhoneNoByPosBatchNo
	 * @return String
	 * @author WangMingShun
	 * @date 2016-2-3
	 * @throws
	 */
	public String queryPhoneNoByPosBatchNo(String posBatchNo) {
		return (String) queryForObject("queryPhoneNoByPosBatchNo", posBatchNo);
	}
	
	/**
	 * 发送短信后调整pos_apply_batch,
	 * 		1、当定时任务给客户成功发送短信后，将状态改为'0',这时表示发送成功，定时任务就不会再取到该条信息了
	 * 		2、当客户回复短信的时候，将状态改为客户回复的满意度即可，这时表示收到回复
	 * @methodName: updatePosApplyBatch
	 * @return void
	 * @author WangMingShun
	 * @date 2016-2-3
	 * @throws
	 */
	public void updatePosApplyBatch(String posBatchNo, String status) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("posBatchNo", posBatchNo);
		paraMap.put("status", status);
		getSqlMapClientTemplate().update(sqlName("updatePosApplyBatch"), paraMap, 1);
	}
	
	/**
	 * 获取官网非资金类保全操作(联系方式变更,客户资料变更, 保单挂失)一小时后仍未完成的保全
	 * @return List<String>
	 * @author WangMingShun
	 * @date 2016-3-9
	 */
	public List<String> queryCanclePosInfoList() {
		return queryForList("queryCanclePosInfoList", null);
	}
	
	/**
	 * 锁住 待撤销保全记录
	 * @return boolean
	 * @author WangMingShun
	 * @date 2016-3-9
	 */
	public boolean lockCanclePosInfo(String posNo) {
		try {
			return "Y".equals((String) queryForObject("lockCanclePosInfo", posNo));
		} catch (Exception e) {
			return false;
		}
	}
}
