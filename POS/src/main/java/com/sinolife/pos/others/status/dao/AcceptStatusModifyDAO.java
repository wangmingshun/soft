package com.sinolife.pos.others.status.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@SuppressWarnings("rawtypes")
@Repository("acceptStatusModifyDAO")
public class AcceptStatusModifyDAO extends AbstractPosDAO {

	public Map query(String posNo) {
		return (Map) queryForObject("QUERY_POS_INFO", posNo);
	}

	public Map<String, Object> insertDataUpdateLog(String p_module_id,
			String p_data_update_type, String p_policy_no,
			String p_business_no_type, String p_business_no,
			String p_approve_no, String p_remark) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_module_id", p_module_id);
		paraMap.put("p_data_update_type", p_data_update_type);
		paraMap.put("p_policy_no", p_policy_no);
		paraMap.put("p_business_no_type", p_business_no_type);
		paraMap.put("p_business_no", p_business_no);
		paraMap.put("p_approve_no", p_approve_no);
		paraMap.put("p_remark", p_remark);
		getSqlMapClientTemplate().insert(sqlName("insertDataUpdateLog"),
				paraMap);
		return paraMap;
	}

	public String queryProblemItmeNo(String posNo) {

		return (String) queryForObject("queryProblemItmeNo", posNo);
	}

}
