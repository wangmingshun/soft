package com.sinolife.pos.receipt.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.im.http.client.IM;
import com.sinolife.im.http.model.Constant;
import com.sinolife.im.http.model.ImageInfo;
import com.sinolife.pos.common.dao.AbstractPosDAO;
import com.sinolife.sf.platform.runtime.PlatformContext;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Repository("receiptManageDAO")
public class ReceiptManageDAO extends AbstractPosDAO {

	@Autowired
	private IM imageClient;
	
	/**
	 * 移动端待处理回执列表
	 */
	public List queryMobileList(String user){
		return queryForList("QUERY_TO_APPRECEIPT_LIST", user);
	}
	
	/**
	 * 传统待处理回执列表
	 */
	public List queryTraditionalList(String user){
		return queryForList("QUERY_TO_NOTAPPRECEIPT_LIST", user);
	}
	
	/**
	 * 待处理问题件列表
	 */
	public List queryProblemList(String user){
		return queryForList("QUERY_TO_RECEIPT_PROBLEM_LIST", user);
	}

	/**
	 * 查询回销信息
	 * 
	 * @param policyNo
	 * @param centerRole
	 *            true总公司用户
	 * @return
	 */
	public Map queryContractInfo(String policyNo, boolean centerRole,
			String user) {
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("centerRole", centerRole ? "Y" : "N");
		pMap.put("user", user);

		return (Map) queryForObject("QUERY_POLICY_CONTRACT_INFO", pMap);
	}

	/**
	 * 我尝试取一单进行处理，但中途意外没处理完（仅在断电，电脑死机等极端情况下），该单就始终挂在我名下了 下次取任务的时候，优先取这些单
	 * 
	 * @param user
	 * @return
	 */
	public List myFeedbackTask(String user) {
		return queryForList("QUERY_MY_FEEDBACK_TASK", user);
	}

	/**
	 * 锁定policyNo对应的一条记录
	 * 
	 * @param policyNo
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String lockContractPolicyNo(String policyNo, String locker) {
		try {
			queryForObject("LOCK_POLICY_CONTRACT_RECORD", policyNo);
			createPosReceiptDeal(policyNo, null, "1");

			Map pMap = new HashMap();
			pMap.put("policyNo", policyNo);
			pMap.put("confirmUser", locker);
			pubProcUpPolContract(pMap);

		} catch (Exception e) {
			if (e.getMessage().indexOf("ORA-00054") > 0) {
				return "有人试图和您获取同一单任务，发生了冲突，请重试";
			} else {
				throw new RuntimeException(e);
			}
		}
		return "Y";
	}

	/**
	 * 根据保单号计算回执barcode
	 * 
	 * @param policyNo
	 * @return barCode
	 */
	public String getPolicyNoReceiptBarCode(String policyNo) {
		Map map = (Map) queryForObject("QUERY_POLICY_APPLY_CARCODE", policyNo);

		String prefix = null;
		if ("3".equals(map.get("BUSINESS_SOURCE"))) {

			String barcodeNo = (String) map.get("APPLY_BAR_CODE");
			try {
				List<ImageInfo> imageList = null;
				ImageInfo im = null;
				imageList = imageClient.queryImageByMainIndex(barcodeNo,
						Constant.CONTENT_YES);
				if (imageList != null && imageList.size() > 0) {
					for (int i = 0; i < imageList.size(); i++) {
						im = (ImageInfo) imageList.get(i);
						if (im.getBar_code().startsWith("1179") == true) {
							return im.getBar_code();

						}
					}
				}

			} catch (Exception e) {

				e.printStackTrace();
				throw new RuntimeException("查询银保通回执影像信息出错", e);
			}
			return "";

		} else {
			prefix = "116701" + map.get("APPLY_BAR_CODE");// 其他回执类型"1167"+版本号"01"+投保单号

			map.put("barCode", prefix);

			queryForObject("QUERY_POLICYNO_BAR_CODE_SUM", map);
			if ("N".equals(map.get("flag"))) {
				throw new RuntimeException("" + map.get("message"));
			}

			return prefix + map.get("barCodeSum");

		}

	}

	/**
	 * 取打印日期两个月内已有回执影像且未回销的保单
	 * 
	 * @return
	 */
	public List unSignPolicyScanedList(String channel) {
		Map pMap = new HashMap();
		pMap.put("channel", channel);
		pMap.put("user", PlatformContext.getCurrentUser());
		return queryForList("QUERY_UN_SIGN_SCANED_POLICY", pMap);
	}

	/**
	 * 回销录入
	 * 
	 * @param pMap
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePolicyContract(Map pMap) {
		pubProcUpPolContract(pMap);
		if ("Y".equals(queryForObject("QUERY_FINANCIAL_POLICY_FLAG",
				pMap.get("policyNo")))) {// 理财产品保单
			if ("N".equals(pMap.get("signFlag"))) {
				pMap.put("signDate", null);
			}
			queryForObject("RECEIPT_WRITEOFF_INSURANCE_TRADE", pMap);
			if (!"0".equals(pMap.get("flag"))) {
				throw new RuntimeException("" + pMap.get("message"));
			}
		}
	}

	/**
	 * 调用接口更新policy_contract
	 * 
	 * @param pMap
	 */
	public void pubProcUpPolContract(Map pMap) {
		queryForObject("PUB_PROC_UP_POL_CONTRACT", pMap);
		if (!"Y".equals(pMap.get("flag"))) {
			throw new RuntimeException("" + pMap.get("message"));
		}
	}
	/**
	 * 回写电子签名回执的file_id
	 * @param policyNo
	 * @param eSignatureFileId
	 * @return Map<String,Object>
	 * @author Zhangyi
	 * @time 2015-3-24
	 */
	public Map<String,Object> updateESignaturePolicyFileId(String policyNo,String eSignatureFileId) {
		Map<String,Object> pMap = new HashMap<String,Object>();
		pMap.put("policyNo", policyNo);
		pMap.put("eSignatureFileId", eSignatureFileId);
		queryForObject("PUB_PROC_UP_POL_RECEIPTID", pMap);
		return pMap;
	}
	/**
	 * 更新pos_receipt_deal表
	 * 
	 * @param policy_no
	 */
	public int updatePosReceiptDeal(String policyNo, String note, String status) {
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("note", note);
		pMap.put("status", status);
		return getSqlMapClientTemplate().update(
				sqlName("UPDATE_POS_RECEIPT_DEAL"), pMap);
	}

	/**
	 * 写入一条记录，若已存在，更新之
	 * 
	 * @param policyNo
	 * @param note
	 * @param status
	 */
	public void createPosReceiptDeal(String policyNo, String note, String status) {
		if (updatePosReceiptDeal(policyNo, note, status) < 1) {
			Map pMap = new HashMap();
			pMap.put("policyNo", policyNo);
			pMap.put("note", note);
			pMap.put("status", status);
			getSqlMapClientTemplate().insert(
					sqlName("INSERT_POS_RECEIPT_DEAL"), pMap);
		}
	}

	public Map memoPolicyQuery(String policyNo, String user) {
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("user", user);
		return (Map) queryForObject("QUERY_POLICY_FOR_MEMO", pMap);
	}

	public void memoSubmit(String policyNo, String memoDate, String cause) {
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("memoDate", memoDate);
		pMap.put("cause", cause);
		getSqlMapClientTemplate().insert(
				sqlName("INSERT_POS_POLICY_RECEIPT_PUTOFF"), pMap);
	}

	/**
	 * 回执清单
	 * 
	 * @param pMap
	 * @return
	 */
	public List otherReport(Map pMap) {
		String sql = "QUERY_RECEIPT_CONFIRM_LIST";
		if ("3".equals(pMap.get("listType"))) {
			sql = "QUERY_RECEIPT_INPUT_LIST";
		}
		return queryForList(sql, pMap);
	}

	/**
	 * 查询其中一单的影像等信息
	 * 
	 * @param policyNo
	 * @return
	 */
	public Map queryPolicyImage(String policyNo) {

		return (Map) queryForObject("QUERY_POLICY_IMAGE", policyNo);
	}

	/**
	 * 查询当前用户名下未回销的保单数量
	 * 
	 * @param policyNo
	 * @return
	 */
	public Integer getMyFeedBackTaskCount(String user) {

		return (Integer) queryForObject("QUERY_MY_FEEDBACK_TASK_COUNT", user);
	}

	/**
	 * 查询当前用户名下未回销的保单数量
	 * 
	 * @param policyNo
	 * @return
	 */
	public Integer getUserBranchUnReceiptCount(String channel, String user) {

		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("channel", channel);
		paraMap.put("user", user);

		return (Integer) queryForObject("QUERY_USER_BRANCH_UNRECEIPT_COUNT",
				paraMap);
	}

	/**
	 * 银保通系统接口 --通过保单号修改扉页号
	 * 
	 * @param policyNo
	 * @param pressNoOld
	 * @return
	 */
	public void modifyPressNo(Map map) {

		queryForObject("MODIFY_PRESSNO", map);
	}

	/**
	 * 银保通系统接口 --通过保单号查询扉页号
	 * 
	 * @param policyNo
	 * @return
	 */
	public String queryPressNo(String policyNo) {

		return (String) queryForObject("queryPressNo", policyNo);
	}

	/**
	 * 更新影像主索引
	 * 
	 * @param imageBarcode
	 * @param imageBarcodeOld
	 * @param imageBarcodeNew
	 * @return
	 */
	public void modifyImageBarcode(Map pMap) {

		queryForObject("MODIFY_IMAGE_BARCODE", pMap);
	}

	/**
	 * 查询影像主索引
	 * 
	 * @param barCode
	 * @return
	 */
	public String queryImageMainIndex(String barCodeNo) {

		return (String) queryForObject("queryImageMainIndex", barCodeNo);
	}
	/**
	 * 查询表Policy_Contract信息
	 * @param policyNo
	 * @return
	 */
	public Map<String, Object> queryPolicyContractInfo(String policyNo) {

		return (Map<String, Object>) queryForObject("queryPolicyContractInfo", policyNo);

	}
	public void updateSignFlag(String policyNo) {
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("signFlag", "D");
		pubProcUpPolContract(pMap);
	}
	
	/**
	 * 根据保单号查询barcode(只做查询用)
	 * 
	 * @param policyNo
	 * @return barCode
	 */
	public String getPolicyNoReceiptBarCode01(String policyNo) {
		Map map = (Map) queryForObject("QUERY_POLICY_APPLY_CARCODE", policyNo);
		return (String) map.get("APPLY_BAR_CODE");
	}
	/**
	 * 更新回执来源
	 * 
	 * @param pMap
	 */
	public void updateReceiptSource(String p_policy_no, String p_receipt_business_source) {
		Map pMap = new HashMap();
		pMap.put("p_policy_no", p_policy_no);
		pMap.put("p_receipt_business_source", p_receipt_business_source);
		queryForObject("UPDATE_RECEIPT_SOUCE", pMap);
	}
	
	/**
	 * 更新 保全保单介质数据调整日志表
	 */
	public void addPosPolContractLog(String policyNo, String processor, Date processDate,
			String columnName, String oldValue, String newValue, String remark) {
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("processor", processor);
		pMap.put("processDate", processDate);
		pMap.put("columnName", columnName);
		pMap.put("oldValue", oldValue);
		pMap.put("newValue", newValue);
		pMap.put("remark", remark);
		getSqlMapClientTemplate().insert(sqlName("update_pos_pol_contract_log"), pMap);
	}
}
