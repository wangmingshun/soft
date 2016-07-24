package com.sinolife.pos.todolist.problem.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;
import com.sinolife.pos.common.dto.PosProblemItemsDTO;
import com.sinolife.pos.common.dto.PosProblemStatusChangeDTO;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Repository("problemDAO")
public class ProblemDAO extends AbstractPosDAO {

	/**
	 * 查询保全问题件
	 * 
	 * @param criteriaMap
	 *            查询条件Map，包含如下可用Key：<br/>
	 *            problemStatusList java.util.List&lt;java.lang.String&gt; 问题件状态<br/>
	 *            problemItemNo java.lang.String 问题件号码<br/>
	 *            accessor java.lang.String 问题件处理人<br/>
	 *            accessor 和 problemItemNo 不可同时为空
	 * @return List<PosProblemItemsDTO>
	 */
	public List<PosProblemItemsDTO> queryProblemByCriteria(
			Map<String, Object> criteriaMap) {
		if (StringUtils.isBlank((String) criteriaMap.get("accessor"))
				&& StringUtils.isBlank((String) criteriaMap
						.get("problemItemNo")))
			throw new RuntimeException("问题件处理人和问题件号码不可同时为空");

		return queryForList("queryProblemByCriteria", criteriaMap);
	}

	public List<PosProblemItemsDTO> queryProblemByCriteriaForMPos(
			Map<String, Object> criteriaMap) {
		if (StringUtils.isBlank((String) criteriaMap.get("accessor"))
				&& StringUtils.isBlank((String) criteriaMap
						.get("problemItemNo")))
			throw new RuntimeException("问题件处理人和问题件号码不可同时为空");

		return queryForList("queryProblemByCriteriaForMPos", criteriaMap);
	}

	public String queryPosNoProblemNo(String posNo, String userId) {
		Map pMap = new HashMap();
		pMap.put("posNo", posNo);
		pMap.put("userId", userId);

		return (String) queryForObject("queryPosNoProblemNo", pMap);
	}

	/**
	 * 查询保全问题件代办列表
	 * 
	 * @param accessor
	 *            处理人ID
	 * @return List&lt;PosProblemItemsDTO&gt;
	 */
	public List<PosProblemItemsDTO> queryProblemTodoList(String accessor) {
		Map<String, Object> criteriaMap = new HashMap<String, Object>();
		List<String> problemStatusList = new ArrayList<String>();
		problemStatusList.add("1");
		problemStatusList.add("2");
		problemStatusList.add("3");
		criteriaMap.put("problemStatusList", problemStatusList);
		criteriaMap.put("accessor", accessor);
		return queryProblemByCriteria(criteriaMap);
	}

	/**
	 * 查询移动保全问题件代办列表
	 * 
	 * @param accessor
	 *            处理人ID
	 * @return List&lt;PosProblemItemsDTO&gt;
	 */
	public List<PosProblemItemsDTO> queryProblemTodoListForMPos(String accessor) {
		Map<String, Object> criteriaMap = new HashMap<String, Object>();
		List<String> problemStatusList = new ArrayList<String>();
		problemStatusList.add("1");
		problemStatusList.add("2");
		problemStatusList.add("3");
		criteriaMap.put("problemStatusList", problemStatusList);
		criteriaMap.put("accessor", accessor);
		return queryProblemByCriteriaForMPos(criteriaMap);
	}

	/**
	 * 根据问题件号查询问题件
	 * 
	 * @param problemItemNo
	 *            问题号
	 * @return List&lt;PosProblemItemsDTO&gt;
	 */
	public PosProblemItemsDTO queryProblemItemsByID(String problemItemNo) {
		Map<String, Object> criteriaMap = new HashMap<String, Object>();
		criteriaMap.put("problemItemNo", problemItemNo);
		List<PosProblemItemsDTO> retList = queryProblemByCriteria(criteriaMap);
		if (retList != null && retList.size() == 1)
			return retList.get(0);

		return null;
	}

	/**
	 * 更新问题件记录
	 * 
	 * @param problemItemNo
	 * @param fieldType
	 *            1:PROBLEM_STATUS
	 * @param newValue
	 * @return effectiveRows
	 */
	public int updatePosProblemItems(String problemItemNo, String fieldType,
			Object newValue) {
		Set<String> limitedFieldType = new HashSet<String>();
		limitedFieldType.add("1");
		limitedFieldType.add("2");
		limitedFieldType.add("3");
		limitedFieldType.add("4");
		limitedFieldType.add("5");
		if (!limitedFieldType.contains(fieldType))
			throw new RuntimeException("非法的更新字段类型：" + fieldType);

		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("problemItemNo", problemItemNo);
		paraMap.put("fieldType", fieldType);
		paraMap.put("newValue", newValue);
		return getSqlMapClientTemplate().update(
				ProblemDAO.class.getName() + ".updatePosProblemItems", paraMap);
	}

	/**
	 * 新增保全问题件
	 * 
	 * @param ppi
	 */
	public void insertPosProblemItems(PosProblemItemsDTO ppi) {
		getSqlMapClientTemplate().insert(
				ProblemDAO.class.getName() + ".insertPosProblemItems", ppi);
	}

	/**
	 * 新增保全问题件状态变迁记录
	 * 
	 * @param pps
	 */
	public void insertPosProblemStatusChange(PosProblemStatusChangeDTO pps) {
		getSqlMapClientTemplate().insert(
				ProblemDAO.class.getName() + ".insertPosProblemStatusChange",
				pps);
	}

	/**
	 * 查询函件下发需要的其他信息，如客户联系信息，业务员信息等
	 * 
	 * @param paraMap
	 *            参数Map clientNo/policyNo
	 * @return
	 */
	public Map<String, Object> queryExtraInfoForLetter(
			Map<String, Object> paraMap) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		mergeMap(
				retMap,
				(Map<String, Object>) queryForObject(
						"queryExtraInfoForLetter1", paraMap));
		mergeMap(
				retMap,
				(Map<String, Object>) queryForObject(
						"queryExtraInfoForLetter2", paraMap));
		mergeMap(
				retMap,
				(Map<String, Object>) queryForObject(
						"queryExtraInfoForLetter3", paraMap));
		mergeMap(
				retMap,
				(Map<String, Object>) queryForObject(
						"queryExtraInfoForLetter4", paraMap));
		return retMap;
	}

	private void mergeMap(Map<String, Object> source, Map<String, Object> dest) {
		if (source != null && dest != null) {
			source.putAll(dest);
		}
	}

	/**
	 * 根据保全号码生成问题号
	 * 
	 * @param posNo
	 * @return
	 */
	public String generatePosProblemItemNo(String posNo) {
		return (String) queryForObject("generatePosProblemItemNo", posNo);
	}

	/**
	 * 
	 * 财务接口:查询最近一次转账失败原因
	 * @param paraMap
	 * @return
	 */
	public Map<String, Object> queryTransFailureCause(
			Map<String, Object> paraMap) {

		return (Map<String, Object>) queryForObject("queryTransFailureCause",
				paraMap);
	}

}
