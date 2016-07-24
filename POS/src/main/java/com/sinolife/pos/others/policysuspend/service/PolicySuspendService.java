package com.sinolife.pos.others.policysuspend.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.common.dao.CommonAcceptDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.others.policysuspend.dao.PolicySuspendDAO;
import com.sinolife.sf.platform.runtime.PlatformContext;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Service("policySuspendService")
public class PolicySuspendService {

	@Autowired
	PolicySuspendDAO suspendDAO;
	
	@Autowired
	CommonAcceptDAO acceptDAO;
	
	@Autowired
	CommonQueryDAO queryDAO;
	
	public Map policyQuery(String policyNo){
		return suspendDAO.policyQuery(policyNo);
	}
	
	/**
	 * 置人工暂停
	 * @param policyNo
	 * @param clientNo
	 * @param suspendRemark
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void setSuspend(String policyNo, String clientNo, String suspendRemark){
		Date sysdate = queryDAO.getSystemDate();

		String posNo = acceptDAO.posInsideApply(clientNo, policyNo, sysdate, "89");
		
		Map pMap = new HashMap();
		pMap.put("posNo", posNo);
		pMap.put("suspendRemark", "  " + PlatformContext.getCurrentUser() + "：" + suspendRemark);
		suspendDAO.updatePosInfoRemark(pMap);
		
		acceptDAO.doPolicySuspend(policyNo,"5",posNo,"56",sysdate);
	}
	
	/**
	 * 取消人工暂停
	 * @param policyNo
	 * @param clientNo
	 * @param suspendRemark
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void cancelSuspend(String policyNo, String clientNo, String suspendRemark, String posNo){
		Map pMap = new HashMap();
		pMap.put("posNo", posNo);
		pMap.put("suspendRemark", "  " + PlatformContext.getCurrentUser() + "：" + suspendRemark);
		suspendDAO.updatePosInfoStatus(pMap);

		acceptDAO.resumePolicySuspend(policyNo, posNo, "56");
	}
	
}
