package com.sinolife.pos.posSync.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@Repository("posBiaSyncDAO")
public class PosBiaSyncDAO extends AbstractPosDAO{
	
	/**
	 * @Description: 查询中介机构信息
	 * @methodName: getAgencyInfo
	 * @param posNo
	 * @param policyNo
	 * @return
	 * @return List<Map<String,Object>>
	 * @author WangMingShun
	 * @date 2015-8-7
	 * @throws
	 */
	public List<Map<String, Object>> getAgencyInfo(String posNo, String policyNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("posNo", posNo);
		paraMap.put("policyNo", policyNo);
		return queryForList("getAgencyInfo", paraMap);
	}
	
	/**
	 * @Description: 查询批单登记
	 * @methodName: posRegister
	 * @param policyNo
	 * @param posNo
	 * @param branchCheckCode
	 * @return
	 * @return List<Map<String,Object>>
	 * @author WangMingShun
	 * @date 2015-8-7
	 * @throws
	 */
	public List<Map<String, Object>> posRegister(String policyNo, String posNo,
			String branchCheckCode) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("posNo", posNo);
		paraMap.put("policyNo", policyNo);
		paraMap.put("branchCheckCode", branchCheckCode);
		return queryForList("posRegister", paraMap);
	}
	
	/**
	 * @Description:保单登记码查询
	 * @methodName: getPolicyRegNo
	 * @param policyNo
	 * @return
	 * @return String
	 * @author WangMingShun
	 * @date 2015-8-7
	 * @throws
	 */
	public String getPolicyRegNo(String policyNo) {
		return (String) queryForObject("policyRegNo", policyNo);
	}
	
//	public List<Map<String, Object>> getPolicyInfo(String policyNo) {
//		return (List<Map<String, Object>>) queryForObject("getPolicyInfo", policyNo);
//	}
	
	public Map<String, Object> getPolicyInfo(String policyNo) {
		return (Map<String, Object>) queryForObject("getPolicyInfo", policyNo);
	}
	
	public Map<String, String> getApplicantInfo(String policyNo) {
		return (Map<String, String>) queryForObject("getApplicantInfo", policyNo);
	}
	
	public Map<String, Object> getInsuredInfo(String policyNo) {
		return (Map<String, Object>) queryForObject("getInsuredInfo", policyNo);
	}
	
	public List<Map<String, String>> getCoverage(String policyNo) {
		return queryForList("getCoverage", policyNo);
	}
	
	public List<Map<String, String>> getBeneficiary(String policyNo) {
		return queryForList("getBeneficiary", policyNo);
	}
}
