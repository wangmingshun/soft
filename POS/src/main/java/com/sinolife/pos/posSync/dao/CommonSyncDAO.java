package com.sinolife.pos.posSync.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@Repository("commonSyncDAO")
public class CommonSyncDAO extends AbstractPosDAO{
	
	/**
	 * 查询未同步的数据
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryNotSyncList() {
		return queryForList("queryNotSyncList", null);
	}
	
	public String lockPosSyncJobDetial(String controlPk) {
		return (String) queryForObject("lockPosSyncJobDetial", controlPk);
	}
	
	/**
	 * @Description: 写入同步额外信息
	 * @methodName: insertSyncDetail
	 * @param businessType 业务类型
	 * @param businessNoType 业务号类型，有基表BUSINESS_NO_TYPE
	 * @param businessNo 业务号
	 * @param dataKey 数据key
	 * @param dataValue 数据value
	 * @return
	 * @return Map<String,Object>
	 * @author WangMingShun
	 * @date 2015-8-9
	 * @throws
	 */
	public Map<String, Object> insertSyncDetail(String businessType, String businessNoType, 
			String businessNo, String dataKey, String dataValue) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_business_type", businessType);
		paraMap.put("p_business_no_type", businessNoType);
		paraMap.put("p_business_no", businessNo);
		paraMap.put("p_data_key", dataKey);
		paraMap.put("p_data_value", dataValue);
		queryForObject("insertSyncDetail", paraMap);
		return paraMap;
	}
	
	/**
	 * @Description: 置废同步控制信息
	 * @methodName: cancelSyncControl
	 * @param businessType 业务类型
	 * @param businessNoType 业务号类型，有基表BUSINESS_NO_TYPE
	 * @param businessNo 业务号
	 * @return
	 * @return Map<String,Object>
	 * @author WangMingShun
	 * @date 2015-8-9
	 * @throws
	 */
	public Map<String, Object> cancelSyncControl(String businessType, String businessNoType,
			String businessNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_business_type", businessType);
		paraMap.put("p_business_no_type", businessNoType);
		paraMap.put("p_business_no", businessNo);
		queryForObject("cancelSyncControl", paraMap);
		return paraMap;
	}
	
	/**
	 * @Description: 更新同步状态
	 * @methodName: updateSyncControl
	 * @param businessType 业务类型
	 * @param businessNoType 业务号类型，有基表BUSINESS_NO_TYPE
	 * @param businessNo 业务号
	 * @param syncStatus 同步状态
	 * @param failureReason 失败原因
	 * @return
	 * @return Map<String,Object>
	 * @author WangMingShun
	 * @date 2015-8-9
	 * @throws
	 */
	public Map<String, Object> updateSyncControl(String businessType, String businessNoType,
			String businessNo, String syncStatus, String failureReason) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_business_type", businessType);
		paraMap.put("p_business_no_type", businessNoType);
		paraMap.put("p_business_no", businessNo);
		paraMap.put("p_sync_status", syncStatus);
		paraMap.put("p_failure_reason", failureReason);
		queryForObject("updateSyncControl", paraMap);
		return paraMap;
	}
	
	/**
	 * @Description: 查询中介机构查询码
	 * @methodName: getAgencyQueryCode
	 * @param businessType
	 * @param businessNo
	 * @param businessNoType
	 * @param dateKey
	 * @return
	 * @return String
	 * @author WangMingShun
	 * @date 2015-8-9
	 * @throws
	 */
	public String getAgencyQueryCode(String businessType, String businessNo,
			String businessNoType, String dateKey) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("businessType", businessType);
		paraMap.put("businessNo", businessNo);
		paraMap.put("businessNoType", businessNoType);
		paraMap.put("dataKey", dateKey);
		return (String) queryForObject("getAgencyQueryCode", paraMap);
	}
}
