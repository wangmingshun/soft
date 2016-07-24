package com.sinolife.pos.vip.dao;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.sql.ARRAY;
import oracle.sql.STRUCT;

import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;
import com.sinolife.pos.common.dao.typehandler.ArrayTypeResultHandler;
import com.sinolife.pos.common.dto.VipProductInfoDTO;

@SuppressWarnings("rawtypes")
@Repository("vipManageDAO")
public class VipManageDAO extends AbstractPosDAO {

	public String clientApplyBranch(String clientNo) {
		return (String) queryForObject("QUERY_CLIENT_APPLY_BRANCH", clientNo);
	}

	public String judgeClientNoVip(String clientNo) {

		return (String) queryForObject("QUERY_CLIENTNO_IS_VIP", clientNo);
	}

	public Map exeVipProcedure(String sql, Map pMap) {
		queryForObject(sql, pMap);
		return pMap;
	}

	public List exeVipProcedureList(String sql, Map<String, Object> pMap) {

		return queryForList(sql, pMap);

	}

	public List getDesc(String sql) {
		return getSqlMapClientTemplate().queryForList(
				VipManageDAO.class.getName() + "." + sql);
	}

	/**
	 * 查询VIP等级
	 * 
	 * @param clientNo
	 *            客户号
	 * @return Map&lt;String, Object&gt;包含如下key:<br/>
	 *         p_client_no: 客户号<br/>
	 *         p_vip_grade:VIP等级代码<br/>
	 *         p_vip_desc:VIP等级描述<br/>
	 *         p_flag:成功标志 0:成功 1:失败<br/>
	 *         p_message:返回信息
	 */
	public Map<String, Object> getClientVipGradeByClientNo(String clientNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_client_no", clientNo);
		queryForObject("getClientVipGrade", paraMap);
		return paraMap;
	}

	public String queryTypeGradePremsum(Map pMap) {
		return (String) queryForObject("QUERY_TYPE_GRADE_PREMSUM", pMap);
	}

	/**
	 * @param pMap
	 */
	public void insertTypeGradePremsum(Map pMap) {
		getSqlMapClientTemplate().insert(sqlName("INSERT_POS_VIP_TYPE_GRADE"),
				pMap);
	}
	
	
	
	/**
	 * 增加vip服务项目
	 * @param pMap
	 */
	public void insertServiceInstanceInfo(Map pMap) {
		getSqlMapClientTemplate().insert(sqlName("INSERT_POS_VIP_SERVICE__INSTANCE_INFO"),
				pMap);
	}
	
	

	/**
	 * @param pMap
	 */
	public void updateTypeGradePremsum(Map pMap) {
		getSqlMapClientTemplate().update(sqlName("UPDATE_POS_VIP_TYPE_GRADE"),
				pMap);
	}

	// VIP卡号原有时间结束
	public void updatePosVipCardInfo(Map pMap) {
		getSqlMapClientTemplate().update(sqlName("UPDATE_POS_VIP_CARD_INFO"),
				pMap);
	}
	
	
	
	
	
	
	
	
	

	// VIP服务专员原有时间结束

	public void updatePosVipServiceInfo(Map pMap) {
		getSqlMapClientTemplate().update(
				sqlName("UPDATE_POS_VIP_SERVICE_INFO"), pMap);
	}

	public void insertVipDetailInfo(String sql, Map pMap) {
		getSqlMapClientTemplate().insert(sqlName(sql), pMap);
	}

	public void updateVipDetailInfo(String sql, Map pMap) {
		getSqlMapClientTemplate().update(sqlName(sql), pMap);
		
	}

	/**
	 * @param pMap
	 * @return
	 */
	public Map queryVipProductInfo(Map pMap) {

		queryForObject("queryVipProductInfo", pMap);
		ArrayTypeResultHandler handler = new ArrayTypeResultHandler() {
			@Override
			public Map handleResult(Map map) {
				ARRAY array = (ARRAY) map.get("productList");
				List<VipProductInfoDTO> result = new ArrayList<VipProductInfoDTO>();
				VipProductInfoDTO bi = null;
				try {
					Object[] obj = (Object[]) array.getArray();
					STRUCT struct = null;
					for (int i = 0; i < obj.length; i++) {
						struct = (STRUCT) obj[i];
						Object[] tmp = (Object[]) struct.getAttributes();
						bi = new VipProductInfoDTO();
						bi.setBranchName((String) tmp[0]);
						bi.setPolicyNo((String) tmp[1]);
						bi.setEffectDate((Date) tmp[2]);
						bi.setDutyDescription((String) tmp[3]);
						bi.setProductName((String) tmp[4]);
						bi.setAppName((String) tmp[5]);
						bi.setInsName((String) tmp[6]);
						bi.setPremSum(tmp[7] == null ? "" : DecimalFormat
								.getNumberInstance().format(tmp[7]));
						bi.setPeriodType((String) tmp[8]);
						bi.setVipPremSum(tmp[9] == null ? "" : DecimalFormat
								.getNumberInstance().format(tmp[9]));
						bi.setEmpName((String) tmp[10]);
						result.add(bi);
					}
					map.put("vipProductInfoList", result);
				} catch (SQLException e) {
					String errMsg = "ArrayTypeResultHandler error:"
							+ e.getMessage();
					logger.error(errMsg, e);
					throw new RuntimeException(errMsg);
				}
				return map;

			}

		};
		handler.handleResult(pMap);
		return pMap;
	}
	/**
	 * 查询VIP等级,卡号
	 * 
	 * @param clientNo
	 *            客户号
	 * @return Map&lt;String, Object&gt;包含如下key:<br/>
	 *         p_client_no: 客户号<br/>
	 *         p_vip_grade:VIP等级代码<br/>
	 *         p_vip_desc:VIP等级描述<br/>
	 *         p_card_no：vip卡号
	 *         p_flag:成功标志 0:成功 1:失败<br/>
	 *         p_message:返回信息
	 */
	public Map<String, Object> getVipInfo(String clientNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_client_no", clientNo);
		queryForObject("getVipInfo", paraMap);
		return paraMap;
	}
	/**
	 * 查询VIP等级,卡号
	 * 
	 * @param 
	 *       p_client_no                         :vip客户号(非空)
     *       p_card_no                           :vip卡号(非空)
     *       p_modified_date                     ：变更时间
     *       p_modified_reason                   ：变更原因
     *       p_provided_user                     ：发放人
     *       p_start_date                       :开始时间
	 * @return 
	 *         p_flag:成功标志 0:成功 1:失败<br/>
	 *         p_message:返回信息
	 */
	public Map<String, Object> updateVipCard(String p_client_no,String p_card_no,Date p_modified_date,String p_modified_reason, String  p_provided_user,Date p_start_date) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_client_no", p_client_no);
		paraMap.put("p_card_no", p_card_no);
		paraMap.put("p_modified_date", p_modified_date);
		paraMap.put("p_modified_reason", p_modified_reason);
		paraMap.put("p_provided_user", p_provided_user);
		paraMap.put("p_start_date", p_start_date);
		queryForObject("updateVipCard", paraMap);
		return paraMap;
	}
	/**
	 * @methodName: getclientServicePlanInfo
	 * @Description: 获取客户的服务方案 
	 * @param map
	 * @return String 
	 * @author WangMingShun
	 * @date 2015-6-1
	 * @throws
	 */ 	
	public Map getclientServicePlanInfo(Map pMap) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("clientNo", pMap.get("clientNo"));
		queryForObject("clientServicePlan", pMap);
		return pMap;
	}
	
	/**
	 * @Description: 更新VIP服务情况表的服务信息状态
	 * @methodName: updateVipServiceInstanceInfo
	 * @param pMap
	 * @return
	 * @return int
	 * @author WangMingShun
	 * @date 2015-8-20
	 * @throws
	 */
	public int updateVipServiceInstanceInfo(Map pMap) {
		return getSqlMapClientTemplate().update(
				sqlName("UPDATE_POS_VIP_SERVICE_INSTANCE_INFO"), pMap);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> queryPayFailPolicyInfoByClientNo(String clientNo) {
		return  (Map<String,Object>)queryForObject("queryPayFailPolicyInfoByClientNo", clientNo);
	}
	
}
