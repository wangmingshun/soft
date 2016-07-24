package com.sinolife.pos.todolist.approval.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;
import com.sinolife.pos.common.dto.PosApproveInfo;
import com.sinolife.sf.attatch.UrlSignature;
import com.sinolife.sf.platform.runtime.PlatformContext;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Repository("approvalDAO")
public class ApprovalDAO extends AbstractPosDAO {

	/**
	 * 查询待审批列表
	 * @return
	 */
	public List queryApproveList(String userId, String posNo) {
		Map pMap = new HashMap();
		pMap.put("posNo", posNo);
		pMap.put("userId", userId);
		
		return queryForList("QUERY_TO_APPROVE_LIST", pMap);
	}

	/**
	 * 查询待审批件相关保全信息：
	 * 
	 * @param posNo
	 * @return
	 */
	public Map queryApprovePosInfo(String posNo) {
		Map map = (Map)queryForObject("QUERY_POSINFO_FOR_APPROVE", posNo);
		
		List<Map> list = queryForList("QUERY_POS_ATTACHMENT_LIST", map.get("posNo"));
		if(list!=null && list.size()>0){
			map.put("attachFlag", "Y");
			for (int i = 0; i < list.size(); i++) {
				Map tmp = (Map)list.get(i);
				String phoneFileUrl = PlatformContext.getIMFileService().getFileURL((String)tmp.get("phoneFileId"), new UrlSignature(), true, (String)tmp.get("phoneFileName"), null, null);
				tmp.put("phoneFileUrl", phoneFileUrl);
			}
		}
		map.put("attachments", list);
		
		return map;
	}

	/**
	 * 查询前面的人已审批的信息
	 * 
	 * @param posNo、submitNo
	 * @return
	 */
	public List queryApprovedList(String posNo, String submitNo) {
		Map pMap = new HashMap();
		pMap.put("posNo", posNo);
		pMap.put("submitNo", submitNo);

		return queryForList("QUERY_APPROVED_LIST", pMap);
	}

	/**
	 * 查询对应保全相关问题件信息
	 * 
	 * @param posNo
	 * @return
	 */
	public Map queryProblemInfo(String posNo) {
		Map pMap = new HashMap();
		
		List list = queryForList("QUERY_POS_PROBLEM_FLAG", posNo);
		if (list != null && list.size() > 0) {
			pMap.put("problemFlag", "Y");
			pMap.put("problemList", list);
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map)list.get(i);
				if("2".equals(map.get("PROBLEM_ITEM_TYPE")) &&("1".equals(map.get("PROBLEM_STATUS"))||"2".equals(map.get("PROBLEM_STATUS")))){
					pMap.put("appProblemFlag", "Y");
				}
			}
		}
		return pMap;
	}
	
	/**
	 * 是否是审批不通过退回到受理人在审批
	 * @param pMap
	 * @return Y N
	 */
	public String selfApproveFlag(Map pMap){
		return (String)queryForObject("QUERY_SELF_APPROVE_FLAG", pMap);
	}

	/**
	 * 写入审批结果
	 * @param pMap
	 */
	public void updateApproveDecision(Map pMap) {
		getSqlMapClientTemplate().update(sqlName("UPDATE_APPROVE_DECISION"), pMap);
	}
	
	/**
	 * 更新审批备注，主要是用于转办时
	 * @param pMap
	 */
	public void updateApproveOpnion(Map pMap){
		if(StringUtils.isNotBlank((String)pMap.get("remark"))){
			getSqlMapClientTemplate().update(sqlName("UPDATE_APPROVE_OPNION"), pMap);
		}
	}

	/**
	 * 我转他人处理，在我头上插入一行
	 * @param pMap
	 */
	public void insertApproveNo(Map pMap) {
		getSqlMapClientTemplate().insert(sqlName("INSERT_APPROVE_NO"), pMap);
	}

	/**
	 * 转他人处理插入一行之前，插入行及后面的行序号增加1
	 * @param pMap
	 */
	public void updateApproveListNo(Map pMap) {
		getSqlMapClientTemplate().update(sqlName("UPDATE_APPROVE_LIST_NO"), pMap);
	}

	/**
	 * 是否全审批完了
	 * @param pMap
	 * @return
	 */
	public boolean allApproved(Map pMap) {
		boolean b = true;
		List list = queryForList("QUERY_ALL_APPROVED", pMap);
		if (list != null && list.size() > 0) {
			b = false;
		}
		return b;
	}
	
	/**
	 * 更新pos_info的审批结果
	 * @param pMap
	 */
	public void updatePosApproved(Map pMap){
		getSqlMapClientTemplate().update(sqlName("UPDATE_POSINFO_APPROVED"),pMap);
	}

	/**
	 * 重新送审，生成新的一条审批链
	 * @param posNo
	 */
	public void approveAgain(String posNo){
		Map pMap = new HashMap();
		pMap.put("posNo", posNo);
		
		queryForObject("PROC_APPROVE_POS", pMap);
		
		if("2".equals(pMap.get("approveFlag"))){
			throw new RuntimeException(""+pMap.get("message"));
		}
	}
	
	/**
	 * posNo是否处于status状态
	 * @param posNo
	 * @param status
	 * @return Y N
	 */
	public String posStatusCheck(String posNo, String status){
		Map pMap = new HashMap();
		pMap.put("posNo", posNo);
		pMap.put("status", status);
		
		return (String)queryForObject("QUERY_POS_STATUS_CHECK", pMap);
	}
	
	/**
	 * 该保全是否已下发审批问题件
	 * @param posNo
	 * @return Y--存在未完成的问题件  N--不存在
	 */
	public String approveProblemCheck(String posNo){
		return (String)queryForObject("QUERY_POS_APPROVE_PROBLEM_CHECK", posNo);
	}

	/**
	 * 审批页面的提交操作有不能解决的重复提交问题
	 * 在提交时锁定记录，防止重复操作
	 * @param posNo
	 */
	public void lockPosApprove(String posNo){
		queryForObject("SELECT_POS_APPROVE_FOR_UPDATE", posNo);
	}
	
	/**
	 * 审批前检查
	 * @param posNo
	 * @param submitNo
	 * @param approveNo
	 * @return
	 */
	public String checkBeforeApprove(Map pMap){
		pMap.put("approver", PlatformContext.getCurrentUser());
		String flag = (String)queryForObject("QUERY_SUBMIT_APPROVE_NO_CHECK", pMap);
		return StringUtils.isNotBlank(flag)?"Y":"N";
	}
	
	public String checkAfterApprove(String posNo){
		String s1 = (String)queryForObject("QUERY_APPROVE_CHECK1", posNo);//是否重复新建了审批流
		//String s2 = (String)queryForObject("QUERY_APPROVE_CHECK2", posNo);//是否重复转办了
		
		//if(StringUtils.isNotBlank(s1)||StringUtils.isNotBlank(s2)){
			if(StringUtils.isNotBlank(s1)){	
			return "N";
		}
		return "Y";
	}
	
	public String nextApprover(String posNo){
		return (String)queryForObject("QUERY_POS_NEXT_APPROVER", posNo);
	}
	
	
	

	/**
	 * 查询公共待审批列表
	 * @return
	 */
	public List queryPublicApproveList(String userId) {
		
		
		return queryForList("QUERY_TO_PUBLIC_APPROVE_LIST", userId);
	}
	
	/**
	 * 移动保全
	 * 更新pos_approve的审批人
	 * @param pMap
	 */
	public void updatePosApproveApprover(Map pMap){
		getSqlMapClientTemplate().update(sqlName("UPDATE_APPROVE_APPROVER"),pMap);
	}
	
	
	/**
	 * 保全审批时对工作交接时产生的审批链中相邻为同一人的情况，由该审批人一同审批
	 * @param posNo
	 */
	public void updateApproveDecisions(Map pMap){
		
		queryForObject("update_approve_decisions", pMap);
		
		if(!"0".equals(pMap.get("flag"))){
			throw new RuntimeException(""+pMap.get("message"));
		}
	}
	
	public String checkAfterApprove(Map pMap){
		String posNo = (String)queryForObject("CHECK_APPROVE_DUPSUBMIT", pMap);
		
		
		return posNo;
	}
	
	/**
	 * @Description: 根据oa回调的flowId查询审批表信息
	 * @methodName: getPosApproveInfoByFlowId
	 * @param flowId
	 * @return
	 * @return List<PosApproveInfo>
	 * @author WangMingShun
	 * @date 2015-11-20
	 * @throws
	 */
	public List<PosApproveInfo> getPosApproveInfoByFlowId(String flowId) {
		return queryForList("getPosApproveInfoByFlowId", flowId);
	}
	
	/**
	 * @Description: 根据posNo,submitNo,approveNo查询审批表信息
	 * @methodName: getPosApproveInfo
	 * @return List<PosApproveInfo>
	 * @author WangMingShun
	 * @date 2015-11-20
	 * @throws
	 */
	public List<PosApproveInfo> checkPosApproveInfo(String posNo,
			String submitNo, String approveNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("posNo", posNo);
		paraMap.put("submitNo", submitNo);
		paraMap.put("approveNo", approveNo);
		return queryForList("checkPosApproveInfo", paraMap);
	}
	
	/**
	 * @Description: 根据posNo,submitNo查询审批表信息
	 * @methodName: getPosApproveInfo
	 * @return List<PosApproveInfo>
	 * @author WangMingShun
	 * @date 2015-11-20
	 * @throws
	 */
	public List<PosApproveInfo> getPosApproveInfo(String posNo, String submitNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("posNo", posNo);
		paraMap.put("submitNo", submitNo);
		return queryForList("getPosApproveInfo", paraMap);
	}
	
	/**
	 * @Description: 更新pos_approve表的oa_approve_id字段，该字段是在oa生成的
	 * @methodName: updateApproveOaApproveId
	 * @param posNo
	 * @param flowId
	 * @return void
	 * @author WangMingShun
	 * @date 2015-12-2
	 * @throws
	 */
	public void updateApproveOaApproveId(String posNo, String submitNo, String flowId) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("posNo", posNo);
		paraMap.put("submitNo", submitNo);
		paraMap.put("flowId", flowId);
		getSqlMapClientTemplate().update(sqlName("updateApproveOaApproveId"), paraMap);
	}
	
	/**
	 * @Description: 获取oa送审标题中的机构名称
	 * @methodName: queryOaTitle
	 * @param posNo
	 * @return
	 * @return String
	 * @author WangMingShun
	 * @date 2015-12-9
	 * @throws
	 */
	public String queryOaTitle(String posNo) {
		return (String) queryForObject("queryOaTitle", posNo);
	}
	
	/**
	 * @Description: 查询用户保单的oa审批记录
	 * @methodName: queryOaApproveList
	 * @param user
	 * @return
	 * @return List
	 * @author WangMingShun
	 * @date 2015-12-11
	 * @throws
	 */
	public List queryOaApproveList(String user) {
		return queryForList("queryOaApproveList", user);
	}
	
	/**
	 * @Description: 生成新的一条审批链
	 * @methodName: approveAgain
	 * @param posNo
	 * @return void
	 * @author WangMingShun
	 * @date 2015-12-14
	 * @throws
	 */
	public void approveAgainAll(String posNo){
		Map pMap = new HashMap();
		pMap.put("posNo", posNo);
		
		queryForObject("PROC_APPROVE_POS_ALL", pMap);
		
		if("2".equals(pMap.get("approveFlag"))){
			throw new RuntimeException(""+pMap.get("message"));
		}
	}
	
	/**
	 * @Description: 更新pos_approve表的oa审批时间、意见、状态
	 * @methodName: updatePosApprove
	 * @param map
	 * @return void
	 * @author WangMingShun
	 * @date 2015-12-14
	 * @throws
	 */
	public void updatePosApprove(Map paraMap) {
		getSqlMapClientTemplate().update(sqlName("updatePosApprove"), paraMap);
	}
}
