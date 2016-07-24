package com.sinolife.pos.common.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.net.aso.p;
import oracle.sql.ARRAY;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_22;
import com.sinolife.pos.common.dao.typehandler.ArrayTypeResultHandler;
import com.sinolife.pos.common.dao.typehandler.FinancialProductsArrayHandler;
import com.sinolife.pos.common.dto.FinPremDTO;
import com.sinolife.pos.common.dto.FinancialProductsDTO;
import com.sinolife.pos.common.dto.InsuredClientDTO;
import com.sinolife.pos.common.dto.PolicyBeneficiaryDTO;
import com.sinolife.pos.common.dto.PolicyDTO;
import com.sinolife.pos.common.dto.PolicyProductCoverageDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.common.dto.PosApplyFilesDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.PosPolicy;
import com.sinolife.pos.common.dto.PosServiceItemSequenceSetDTO;
import com.sinolife.pos.common.dto.PosSurvivalDueDTO;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Repository("commonQueryDAO")
@Transactional(propagation = Propagation.SUPPORTS)
public class CommonQueryDAO extends AbstractPosDAO {

	/**
	 * 查询保单渠道
	 * 
	 * @param policyNo
	 * @return
	 */
	public String getChannelTypeByPolicyNo(String policyNo) {
		return (String) this.queryForObject("getChannelTypeByPolicyNo",
				policyNo);
	}

	/**
	 * 查询保单投保人客户号
	 * 
	 * @param policyNo
	 * @return
	 */
	public String getApplicantByPolicyNo(String policyNo) {
		return (String) this.queryForObject("getApplicantByPolicyNo", policyNo);
	}

	/**
	 * 查询保单被保人客户号
	 * 
	 * @param policyNo
	 * @return
	 */
	public List<String> getInsuredByPolicyNo(String policyNo) {
		return this.queryForList("getInsuredByPolicyNo", policyNo);
	}

	/**
	 * 查询保受理渠道
	 * 
	 * @param posNo
	 * @return
	 */
	public String getacceptChannelByPosNo(String posNo) {
		return (String) this.queryForObject("getacceptChannelByPosNo", posNo);
	}

	/**
	 * 根据保全号查受理客戶號
	 * 
	 * @param posNo
	 * @return
	 */
	public String getacceptClientNoByPosNo(String posNo) {
		return (String) this.queryForObject("getacceptClientNoByPosNo", posNo);
	}

	/**
	 * 
	 * 
	 * @param policyNo
	 * @return 保单有未完成的保全数量
	 */
	public int isposacceptflag(String policyNo) {
		return ((Integer) this.queryForObject("isposacceptflag", policyNo))
				.intValue();
	}

	/**
	 * 查保单主险被保人客户号
	 * 
	 * @param policyNo
	 * @return
	 */
	public String getInsuredOfPrimaryPlanByPolicyNo(String policyNo) {
		return (String) this.queryForObject(
				"getInsuredOfPrimaryPlanByPolicyNo", policyNo);
	}

	/**
	 * 根据客户号查询客户作为投保人和被保人的保单号
	 * 
	 * @param clientNo
	 * @return
	 */
	public List<String> queryPolicyNoListByClientNo(String clientNo) {
		return this.queryForList("queryPolicyNoListByClientNo", clientNo);
	}

	/**
	 * 根据投保人客户号查询所以有效的保单号
	 * 
	 * @param clientNo
	 * @return
	 */
	public List<String> queryPolicyNoListByApplicantno(String clientNo) {
		return this.queryForList("queryPolicyNoListByApplicantno", clientNo);
	}

	/**
	 * 根据被保人客户号查询所以有效的保单号
	 * 
	 * @param clientNo
	 * @return
	 */
	public List<String> queryPolicyNoListByInsuredno(String clientNo) {
		return this.queryForList("queryPolicyNoListByInsuredno", clientNo);
	}

	/**
	 * 根据保全号查询服务项目（即保全项目）
	 * 
	 * @param posNo
	 * @return
	 */
	public String queryServiceItemsByPosNo(String posNo) {
		return (String) getSqlMapClientTemplate().queryForObject(
				getClass().getName() + ".queryServiceItemsByPosNo", posNo);
	}

	/**
	 * 根据保单号查询保单主险产品代码
	 * 
	 * @param policyNo
	 *            保单号
	 * @return 主险险种代码
	 */
	public String queryProductCodeOfPrimaryPlanByPolicyNo(String policyNo) {
		return (String) queryForObject(
				"queryProductCodeOfPrimaryPlanByPolicyNo", policyNo);
	}

	/**
	 * 根据保单号查询保单主险产品序号
	 * 
	 * @param policyNo
	 * @return
	 */
	public Integer queryProdSeqOfPrimaryPlanByPolicyNo(String policyNo) {
		return (Integer) queryForObject("queryProdSeqOfPrimaryPlanByPolicyNo",
				policyNo);
	}

	/***
	 * 查询处理经办结果
	 * 
	 * @param posNo
	 * @return
	 */
	public Map queryProcessResult(String posNo) {
		return (Map) getSqlMapClientTemplate().queryForObject(
				getClass().getName() + ".queryProcessResult", posNo);
	}

	/***
	 * 根据被保人查询 保单号，主险，投保人，被保人
	 * 
	 * @param posNo
	 * @return
	 */
	public List<Map<String, String>> PolicyNoListByinsNo(String client_no) {
		return (List<Map<String, String>>) getSqlMapClientTemplate()
				.queryForList(getClass().getName() + ".PolicyNoListByinsNo",
						client_no);
	}

	/***
	 * 根据投保人查询 保单号，主险，投保人，被保人
	 * 
	 * @param posNo
	 * @return
	 */
	public List<Map<String, String>> PolicyNoListByappNo(String client_no) {
		return (List<Map<String, String>>) getSqlMapClientTemplate()
				.queryForList(getClass().getName() + ".PolicyNoListByappNo",
						client_no);
	}

	/***
	 * 根据保单号查险种信息
	 * 
	 * @param posNo
	 * @return
	 */
	public List<Map<String, String>> ProductInfoListByPolicyNo(String policy_no) {
		return (List<Map<String, String>>) getSqlMapClientTemplate()
				.queryForList(
						getClass().getName() + ".ProductInfoListByPolicyNo",
						policy_no);
	}

	/***
	 * 根据保单号，险种序号查保单缴费信息
	 * 
	 * @param posNo
	 * @return
	 */
	public Map<String, Object> queryProdctPremInfo(String p_policy_no,
			String p_prod_seq) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", p_policy_no);
		paraMap.put("p_prod_seq", p_prod_seq);
		queryForObject("queryProdctPremInfo", paraMap);
		return paraMap;
	}

	/***
	 * 根据保单号查询保单账号信息
	 * 
	 * @param posNo
	 * @return
	 */
	public Map<String, Object> queryAccountInfo(String p_policy_no) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", p_policy_no);
		queryForObject("queryAccountInfo", paraMap);
		return paraMap;
	}

	/***
	 * 根据客户号查询客户姓名证件类型证件号
	 * 
	 * @param client_no
	 * @return
	 */
	public Map QueryClientInfoByclientNo(String client_no) {
		return (Map) getSqlMapClientTemplate().queryForObject(
				getClass().getName() + ".QueryClientInfoByclientNo", client_no);
	}

	/**
	 * 查询绑定顺序设置
	 * 
	 * @return
	 */
	public List<PosServiceItemSequenceSetDTO> queryPosServiceItemSequenceSet() {
		return getSqlMapClientTemplate().queryForList(
				getClass().getName() + ".queryPosServiceItemSequenceSet", null);
	}

	/**
	 * 查询受理提醒/风险提示
	 * 
	 * @param remindType
	 *            "1"受理提醒 "2"风险提示
	 * @param serviceItems
	 *            保全项目类型
	 * @return
	 */
	public String getRiskRemind(String remindType, String serviceItems) {
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("remind_type", remindType);
		paraMap.put("service_items", serviceItems);
		return (String) getSqlMapClientTemplate().queryForObject(
				getClass().getName() + ".getRiskRemind", paraMap);
	}

	/**
	 * 查询受客户名下的保单至少有一张是由该服务人员服务
	 * 
	 * @param client_no
	 *            客户号
	 * @param agent_no
	 *            代理人
	 * @return
	 */
	public String isagenthavepolicy(String client_no, String agent_no) {
		Map<String, String> paraMap = new HashMap<String, String>();
		int count;
		String flag = "N";
		paraMap.put("client_no", client_no);
		paraMap.put("agent_no", agent_no);
		count = (Integer) getSqlMapClientTemplate().queryForObject(
				getClass().getName() + ".isagenthavepolicy", paraMap);
		if (count > 0) {
			flag = "Y";
		}
		return flag;
	}

	/**
	 * 查询有无其他客户手机号和此客户相同
	 * 
	 * @param client_no
	 *            客户号
	 * @param agent_no
	 *            手机号
	 * @return
	 */
	public String isotherclientphone(String client_no, String phone_no) {
		Map<String, String> paraMap = new HashMap<String, String>();
		int count;
		String flag = "N";
		paraMap.put("client_no", client_no);
		paraMap.put("phone_no", phone_no);
		count = (Integer) getSqlMapClientTemplate().queryForObject(
				getClass().getName() + ".isotherclientphone", paraMap);
		if (count > 0) {
			flag = "Y";
		}
		return flag;
	}

	/**
	 * 查询客户下面有没有相同的电话号码
	 * 
	 * @param client_no
	 *            客户号
	 * @param phone_no
	 *            手机号
	 * @return
	 */
	public String isclientphone(String client_no, String phone_no) {
		Map<String, String> paraMap = new HashMap<String, String>();
		int count;
		String flag = "N";
		paraMap.put("client_no", client_no);
		paraMap.put("phone_no", phone_no);
		count = (Integer) getSqlMapClientTemplate().queryForObject(
				getClass().getName() + ".isclientphone", paraMap);
		if (count > 0) {
			flag = "Y";
		}
		return flag;
	}

	/**
	 * 查询简要批次受理信息
	 * 
	 * @param posBatchNo
	 * @return
	 */
	public List<Map<String, String>> queryBatchPosInfo(String posBatchNo) {
		return (List<Map<String, String>>) getSqlMapClientTemplate()
				.queryForList(getClass().getName() + ".queryBatchPosInfo",
						posBatchNo);
	}

	/**
	 * 根据查询条件查询险种信息
	 * 
	 * @param criteriaMap
	 *            key:policyNo, isPrimaryPlan,prodSeq
	 * @return
	 */
	public List<PolicyProductDTO> queryPolicyProductListByCriteria(
			Map criteriaMap) {
		return queryForList("queryPolicyProductListByCriteria", criteriaMap);
	}

	/**
	 * 根据查询条件查询保单险种责任信息表信息
	 * 
	 * @param criteriaMap
	 *            key:policyNo, isPrimaryPlan,prodSeq
	 * @return
	 */
	public List<PolicyProductCoverageDTO> queryPolicyProductCoverageListByCriteria(
			Map criteriaMap) {
		return queryForList("queryPolicyProductCoverageListByCriteria",
				criteriaMap);
	}

	/**
	 * 根据保单号查询险种信息 与上面这个方法相比，本方法只将policyNo作为条件 省得在调用处创建criteriaMap
	 * 
	 * @param policyNo
	 * @return
	 */
	public List<PolicyProductDTO> queryPolicyProductListByPolicyNo(
			String policyNo) {
		Map criteriaMap = new HashMap();
		criteriaMap.put("policyNo", policyNo);
		return queryPolicyProductListByCriteria(criteriaMap);
	}

	/**
	 * 根据保单号查询险种信息 与上面这个方法相比，本方法只将policyNo作为条件 省得在调用处创建criteriaMap
	 * 
	 * @param policyNo
	 * @return
	 */
	public List<PolicyProductCoverageDTO> queryPolicyProductCoverageListByPolicyNo(
			String policyNo) {
		Map criteriaMap = new HashMap();
		criteriaMap.put("policyNo", policyNo);
		return queryPolicyProductCoverageListByCriteria(criteriaMap);
	}

	/**
	 * 根据保单号和险种序号查询险种信息
	 * 
	 * @param policyNo
	 * @param prodSeq
	 * @return
	 */
	public PolicyProductDTO queryPolicyProductByPolicyNoAndProdSeq(
			String policyNo, Integer prodSeq) {
		Map criteriaMap = new HashMap();
		criteriaMap.put("policyNo", policyNo);
		criteriaMap.put("prodSeq", prodSeq);
		List<PolicyProductDTO> resultList = queryPolicyProductListByCriteria(criteriaMap);
		if (resultList != null && !resultList.isEmpty()) {
			return resultList.get(0);
		}
		return null;
	}

	/**
	 * 查询保全信息
	 * 
	 * @param posNo
	 * @return
	 */
	public PosInfoDTO queryPosInfoRecord(String posNo) {
		return (PosInfoDTO) queryForObject("queryPosInfoRecord", posNo);
	}

	/**
	 * 查询未结案保全信息
	 * 
	 * @param clientNo
	 *            客户号
	 * @return List<PosInfoDTO>
	 */
	public List<PosInfoDTO> queryNotCompletePosInfoRecord(String clientNo) {
		return queryForList("queryNotCompletePosInfoRecord", clientNo);
	}

	/**
	 * 查询投连账户信息
	 * 
	 * @param policyNo
	 * @return
	 */
	public List<FinancialProductsDTO> getFinancialProductsList(String policyNo,
			String prodSeq) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_prod_seq", prodSeq);
		queryForObject("getFinancialProductsList", paraMap);
		String flag = (String) paraMap.get("p_flag");
		String msg = (String) paraMap.get("p_message");
		if (!"0".equals(flag))
			throw new RuntimeException("查询投连账户信息失败：" + msg);

		ArrayTypeResultHandler handler = new FinancialProductsArrayHandler();
		handler.handleResult(paraMap);

		List<FinancialProductsDTO> financialProductsList = (List<FinancialProductsDTO>) paraMap
				.get("p_financial_products_list");
		return financialProductsList;
	}

	/**
	 * 查询客户下所有有效手机号码
	 * 
	 * @param client_no
	 * @return
	 */
	public List<String> getClientMobileAll(String client_no) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		List moblieList = new ArrayList();
		paraMap.put("p_client_no", client_no);
		queryForObject("getClientMobileAll", paraMap);
		String flag = (String) paraMap.get("p_flag");
		String msg = (String) paraMap.get("p_message");
		if (!"Y".equals(flag))
			throw new RuntimeException("查询客户手机号失败：" + msg);
		ARRAY insArray = (ARRAY) paraMap.get("p_mobile_array");
		Object[] moblieArray;
		try {
			moblieArray = (Object[]) insArray.getArray();
			if (moblieArray != null && moblieArray.length > 0) {
				for (int i = 0; i < moblieArray.length; i++) {
					moblieList.add(moblieArray[i]);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return moblieList;
	}

	/**
	 * 查询一个产品的属性分类，投连万能(18,19)属性除外，
	 * 投连万能属性查询见getFinancialPolicyProductByPolicyNo方法
	 * 
	 * @param productCode
	 *            产品代码
	 * @param classCode
	 *            属性分类代码，参见基表：product_class_code；
	 * @return Y/N
	 * @see #getFinancialPolicyProductByPolicyNo(String)
	 */
	public String pdSelProductPropertPro(String productCode, String classCode) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_product_code", productCode);
		paraMap.put("p_class_code", classCode);
		queryForObject("pdSelProductPropertPro", paraMap);
		logger.info("pdSelProductPropertPro returned: " + paraMap);
		return (String) paraMap.get("v_sign");
	}

	/**
	 * 功能说明: 财务接口45：查询银行网点对应的银行代码接口
	 * 
	 * @param 参数说明
	 *            : p_dept_no IN 银行网点代码(非空）
	 * @param p_bank_code
	 *            OUT 银行代码
	 * @param P_FLAG
	 *            OUT 执行成功失败标志(非空,1--成功,2--失败)
	 * @param P_MES
	 *            OUT 执行结果信息 修改记录: CREATE BY LEIYONGHUA 2012.11
	 */
	public String queryBankCodeByBanDdeptno(String p_dept_no) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_dept_no", p_dept_no);
		queryForObject("queryBankCodeByBanDdeptno", paraMap);
		String flag = (String) paraMap.get("p_flag");
		String msg = (String) paraMap.get("p_mes");
		if (!"1".equals(flag)) {
			throw new RuntimeException("查询银行网点：" + msg);
		}
		logger.info("queryBankCodeByBanDdeptno returned: " + paraMap);
		return (String) paraMap.get("p_bank_code");
	}

	/**
	 * 查询某保单下的理财产品代码和理财类型
	 * 
	 * @param policyNo
	 * @return Map keys:<br/>
	 *         p_flag :返回标志 '0' 成功 '1' 失败<br/>
	 *         p_message :报错信息<br/>
	 *         p_prod_code :产品序号 <br/>
	 *         p_product_code :产品代码<br/>
	 *         p_financial_type :理财类型 1:投连 2:万能<br/>
	 */
	public Map<String, Object> getFinancialPolicyProductByPolicyNo(
			String policyNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		queryForObject("getFinancialPolicyProductByPolicyNo", paraMap);
		return paraMap;
	}

	/**
	 * 查询帐户价值接口<br/>
	 * 投连账户价值按照传入日期的下一评估日价格计算，如下一评估日未定价，则按照最近价格计算
	 * 
	 * @param policyNo
	 *            保单号
	 * @param prodSeq
	 *            产品序号
	 * @param requestDate
	 *            查询日期
	 * @return Map<String, Object> 包含如下Key：<br/>
	 *         p_flag:返回标志 '0' 成功 '1' 失败<br/>
	 *         p_message:报错信息<br/>
	 *         p_total_amount:帐户价值总金额<br/>
	 *         p_financial_products_list:理财产品数组List&lt;FinancialProductsDTO&gt;
	 */
	public Map<String, Object> queryFinancialProductsValue(String policyNo,
			String prodSeq, Date requestDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_prod_seq", prodSeq);
		paraMap.put("p_request_date", requestDate);
		queryForObject("queryFinancialProductsValue", paraMap);

		ArrayTypeResultHandler handler = new FinancialProductsArrayHandler();
		handler.handleResult(paraMap);

		return paraMap;
	}

	/**
	 * --功能说明： 查询帐户信息接口 理财合同状态为“合同终止”时:投连账户价值按照传入日期的下一评估日价格计算，
	 * 如下一评估日未定价，则按照最近价格计算 理财合同状态为“合同非终止”时：则按照最近价格计算 参数说明： 输入参数： p_policy_no
	 * :保单号 p_prod_seq :产品序号 p_product_code :产品代码 p_request_date :查询日期
	 * p_contract_status :理财合同状态　　　　 1：合同非终止 2：合同终止 输出参数： p_total_amount
	 * :帐户价值总金额 p_settle_day_rate :日结算利率 p_settle_year_rate :年结算利率 p_start_date
	 * :利率起始日期 p_end_date :利率终止日期
	 */
	public Map<String, Object> queryFinancialProductsInfo(String policyNo,
			String product_code, Date requestDate, String contract_status) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_product_code", product_code);
		paraMap.put("p_request_date", requestDate);
		paraMap.put("p_contract_status", contract_status);
		queryForObject("queryFinancialProductsInfo", paraMap);

		ArrayTypeResultHandler handler = new FinancialProductsArrayHandler();
		handler.handleResult(paraMap);

		return paraMap;
	}

	/**
	 * 根据产品代码查询贷款利率
	 * 
	 * @param policyNo
	 *            保单号
	 * @param calcDate
	 *            计算日期
	 * @return Map keys:<br/>
	 *         p_product_code 产品<br/>
	 *         p_calc_date 计算日期<br/>
	 *         p_flag 结果标志 0成功1失败<br/>
	 *         p_message 出错消息<br/>
	 *         p_rate 利率（返回0表示不允许贷款）<br/>
	 */
	public Map<String, Object> getProductLoanRate(String productCode,
			Date calcDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_product_code", productCode);
		paraMap.put("p_calc_date", calcDate);
		queryForObject("getProductLoanRate", paraMap);
		return paraMap;
	}

	/**
	 * 用于计算保单险种在某个日期的保单周年日
	 * 
	 * @param policyNo
	 * @param prodSeq
	 * @param calcDate
	 * @return Map keys:<br/>
	 *         p_message 返回信息<br/>
	 */
	public Map<String, Object> calcProductAnniversary(String policyNo,
			Integer prodSeq, Date calcDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_prod_seq", prodSeq);
		paraMap.put("p_calc_date", calcDate);
		queryForObject("calcProductAnniversary", paraMap);
		return paraMap;
	}

	/**
	 * 查询该险种是否有生存给付责任
	 * 
	 * @param productCode
	 * @return Map keys:<br/>
	 *         p_flag ='0' 表示有生存金责任 '1' 表示没有生存金责任 p_message 返回信息<br/>
	 */
	public Map<String, Object> isSurvivalDutyProduct(String productCode) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_product_code", productCode);
		queryForObject("isSurvivalDutyProduct", paraMap);
		return paraMap;
	}

	/**
	 * 根据保单号、产品代码、序号计算保单产品最大可贷金额
	 * 
	 * @param policyNo
	 * @param productCode
	 * @param prodSeq
	 * @param calcDate
	 * @param specialFunc
	 * @return Map keys:<br/>
	 *         p_flag 返回标志 0 成功<br/>
	 *         p_message 返回信息<br/>
	 *         p_loan_max 最大可贷金额 java.math.BigDecimal<br/>
	 *         p_cash_value 现金价值 java.math.BigDecimal
	 */
	public Map<String, Object> calcLoanMaxAndCashValueByProduct(
			String policyNo, String productCode, Integer prodSeq,
			Date calcDate, String specialFunc) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_product_code", productCode);
		paraMap.put("p_prod_seq", prodSeq);
		paraMap.put("p_special_func", specialFunc);
		paraMap.put("p_calc_date", calcDate);
		queryForObject("calcLoanMaxAndCashValueByProduct", paraMap);
		return paraMap;
	}

	/**
	 * 根据保单号计算保单既有贷款本金、利息累计、本息和累计
	 * 
	 * @param policyNo
	 *            保单号
	 * @param endDate
	 *            计息时间 （不包括endDate当天）
	 * @return Map keys:<br/>
	 *         p_flag 返回标志<br/>
	 *         p_message 返回信息<br/>
	 *         p_loan_all_sum 既有贷款本息和（二位小数）<br/>
	 *         p_interest_sum 既有贷款利息累计（二位小数）<br/>
	 */
	public Map<String, Object> calcPolicyLoanInterestByPolicyNo(
			String policyNo, Date endDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_end_date", endDate);
		queryForObject("calcPolicyLoanInterestByPolicyNo", paraMap);
		return paraMap;
	}

	/**
	 * 根据保单号查询保单详细信息
	 * 
	 * @param policyNo
	 * @return PolicyDTO premInfo可用
	 */
	public PolicyDTO queryPolicyInfoByPolicyNo(String policyNo) {
		return (PolicyDTO) queryForObject("queryPolicyInfoByPolicyNo", policyNo);
	}

	/**
	 * 根据保单号计算保单既有自垫本金、利息累计、本息和累计
	 * 
	 * @param policyNo
	 * @param endDate
	 * @return Map keys:<br/>
	 *         p_interest_sum 既有自垫利息累计（二位小数）<br/>
	 *         p_loan_all_sum 既有自垫本息和（二位小数）<br/>
	 *         p_flag 返回标志<br/>
	 *         p_message 返回信息<br/>
	 */
	public Map<String, Object> calcPolicyAplInterestByPolicyNo(String policyNo,
			Date endDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_end_date", endDate);
		queryForObject("calcPolicyAplInterestByPolicyNo", paraMap);
		return paraMap;
	}

	/**
	 * 根据保单号计算保单自垫应补保费
	 * 
	 * @param policyNo
	 * @return Map keys:<br/>
	 *         p_policy_apl_extra_fee 自垫应补保费（二位小数）<br/>
	 *         p_flag 返回标志<br/>
	 *         p_message 返回信息<br/>
	 */
	public Map<String, Object> calcPolicyAplExtraFeeByPolicyNo(String policyNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		queryForObject("calcPolicyAplExtraFeeByPolicyNo", paraMap);
		return paraMap;
	}

	/**
	 * 获取险种费率的计算方式
	 * 
	 * @param productCode
	 *            险种代码
	 * @return 险种的费率计算方式：0-保费保额相互独立，1-保额算保费，2-保费算保额，3-固定保费
	 */
	public String getProductPremCalTypeByProductCode(String productCode) {
		return (String) queryForObject("getProductPremCalTypeByProductCode",
				productCode);
	}

	/**
	 * 判断保单是否有有效的借款
	 * 
	 * @param policyNo
	 *            保单号
	 * @return Map keys:<br/>
	 *         p_policy_no 保单号<br/>
	 *         p_is_has_valid_loan Y/N是否有有效的借款<br/>
	 *         p_flag 返回标志0成功<br/>
	 *         p_message 返回信息<br/>
	 */
	public Map<String, Object> isPolicyHasValidLoan(String policyNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		queryForObject("isPolicyHasValidLoan", paraMap);
		return paraMap;
	}

	/**
	 * 判断保单是否有有效的自垫
	 * 
	 * @param policyNo
	 *            保单号
	 * @return Map keys:<br/>
	 *         p_policy_no 保单号<br/>
	 *         p_is_has_valid_apl Y/N是否有有效的自垫<br/>
	 *         p_flag 返回标志0成功<br/>
	 *         p_message 返回信息<br/>
	 */
	public Map<String, Object> isPolicyHasValidApl(String policyNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		queryForObject("isPolicyHasValidApl", paraMap);
		return paraMap;
	}

	/**
	 * 根据保单号查询保单受益人信息
	 * 
	 * @param criteriaMap
	 *            查询条件Map：支持如下Key：<br/>
	 *            policyNo(必选)benefitType(可选)benefitStatus(可选)
	 * @return List&lt;PolicyBeneficiaryDTO&gt;
	 */
	public List<PolicyBeneficiaryDTO> queryBeneficiaryByCriteria(
			Map<String, Object> criteriaMap) {
		return queryForList("queryBeneficiaryByCriteria", criteriaMap);
	}

	/**
	 * 查询生存金应领信息
	 * 
	 * @param criteriaMap
	 *            查询条件Map：支持如下Key：<br/>
	 *            policyNo(必选)prodSeq(可选)survivalSeq(可选)
	 * @return List&lt;PosSurvivalDueDTO&gt;
	 */
	public List<PosSurvivalDueDTO> queryPosSurvivalDueByCriteria(
			Map<String, Object> criteriaMap) {
		return queryForList("queryPosSurvivalDueByCriteria", criteriaMap);
	}

	/**
	 * 查询保单下可领取各种生存金金的接口
	 * 
	 * @param criteriaMap
	 *            查询条件Map：支持如下Key：<br/>
	 *            policyNo(必选)prodSeq(可选)survivalSeq(可选)survivaltype（可选）
	 * @return List&lt;PosSurvivalDueDTO&gt;
	 */
	public List<PosSurvivalDueDTO> querySurvivalDueByCriteria(
			Map<String, Object> criteriaMap) {
		return queryForList("querySurvivalDueByCriteria", criteriaMap);
	}

	/**
	 * 查询该险种下可领取各种生存金金的接口
	 * 
	 * @param criteriaMap
	 *            查询条件Map：支持如下Key：<br/>
	 *            policyNo(必选)prodSeq(可选)survivalSeq(可选)survivaltype（可选）
	 * @return List&lt;PosSurvivalDueDTO&gt;
	 */
	public List<PosSurvivalDueDTO> queryProductSurvivalDueByCriteria(
			Map<String, Object> criteriaMap) {
		return queryForList("queryProductSurvivalDueByCriteria", criteriaMap);
	}

	/**
	 * 根据保全号码和查询健康告知信息
	 * 
	 * @param posNo
	 * @param clientNo
	 * @return
	 */
	public List<Map<String, Object>> queryPosPersonalNoticeByPosNoAndClientNo(
			String posNo, String clientNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("posNo", posNo);
		paraMap.put("clientNo", clientNo);
		return queryForList("queryPosPersonalNoticeByPosNo", paraMap);
	}

	/**
	 * 根据保单号和客户号查询签名影像对应的barcode和影像类型
	 * 
	 * @param policyNo
	 * @param posNo
	 * @return
	 */
	public Map<String, Object> querySignBarcode(String policyNo, String clientNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("clientNo", clientNo);
		queryForObject("PROC_CLIENT_SIGN_BARCODE", paraMap);
		return paraMap;
	}

	/**
	 * 查询一个产品的对应参数值
	 * 
	 * @param productCode
	 *            产品代码
	 * @param parameter
	 *            参数:<br/>
	 *            10是否可复效
	 * @return 产品参数值
	 */
	public String pdSelPrdParameterValuePro(String productCode, String parameter) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_product_code", productCode);
		paraMap.put("p_parameter", parameter);
		queryForObject("pdSelPrdParameterValuePro", paraMap);
		return (String) paraMap.get("v_parameter_value");
	}

	/**
	 * 根据保单号、计算日期计算保单现价价值
	 * 
	 * @param policyNo
	 *            保单号
	 * @param calcDate
	 *            计算日期
	 * @return Map<String, Object> 包含如下Key：<br/>
	 *         p_policy_cash_value 计算时间保单现金价值（二位小数） java.math.BigDecimal p_flag
	 *         返回标志 0 成功 其他失败 p_message 返回消息
	 */
	public Map<String, Object> calcPolicyCashValue(String policyNo,
			Date calcDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_end_date", calcDate);
		queryForObject("calcPolicyCashValue", paraMap);
		return paraMap;
	}

	/**
	 * 查询保全项目断点(中途退出)信息
	 * 
	 * @param posBatchNo
	 * @return
	 */
	public List<Map<String, Object>> queryBreakInfoByPosBatchNo(
			String posBatchNo) {
		return queryForList("queryBreakInfoByPosBatchNo", posBatchNo);
	}

	/**
	 * 查询续期缴费账户信息
	 */
	public List<Map<String, Object>> queryAccountByPolicyNo(String policy_no) {
		return queryForList("queryAccountByPolicyNo", policy_no);
	}

	/**
	 * 查询柜面信息
	 * 
	 * @param counterNo
	 * @return
	 */
	public Map<String, Object> queryCounterInfoByCounterNo(String counterNo) {
		return (Map<String, Object>) queryForObject(
				"queryCounterInfoByCounterNo", counterNo);
	}

	/**
	 * 查询保单挂失状态
	 * 
	 * @param policyNo
	 * @return {lossFlag[Y-N],lossDate}
	 */
	public Map policyLossStatus(String policyNo) {
		Map map = new HashMap();
		map.put("policyNo", policyNo);

		queryForObject("QUERY_POLICY_LOSS_STATUS", map);

		return map;
	}

	/**
	 * 查询保单是否在挂失期
	 * 
	 * @param policyNo
	 * @param accept_date
	 *            保全受理时间
	 * @return loss_period_flag[Y-N]
	 */
	public Map policyLossPeriodStatus(String policyNo, Date accept_date) {
		Map map = new HashMap();
		map.put("policyNo", policyNo);
		map.put("accept_date", accept_date);
		queryForObject("QUERY_POLICY_LOSS_PERIOD_STATUS", map);

		return map;
	}

	/**
	 * 根据保全号判定是否是满期金领取
	 * 
	 * @param POSNO
	 * @param p_is_maturi_draw
	 * @return p_is_maturi_draw[Y-N]
	 */
	public Map<String, Object> isMaturitySurvivalDraw(String posNo) {
		Map<String, Object> map = new HashMap();
		map.put("pos_no", posNo);
		queryForObject("QUERY_IS_MATURITY_DRAW", map);

		return map;
	}

	/**
	 * 根据条码查询关联条码
	 * 
	 * @param barcodeNo
	 * @return
	 */
	public String getRelateBarcodeNo(String barcodeNo) {
		return (String) queryForObject("getRelateBarcodeNo", barcodeNo);
	}

	/**
	 * 调用l_pos_pub.get_seq获取某表某字段下一序号
	 * 
	 * @param pMap
	 *            {table,column,owner}
	 * @return
	 */
	public String getSeq(Map pMap) {
		queryForObject("PROC_PUB_GET_SEQ", pMap);
		return (String) pMap.get("sequence");
	}

	/**
	 * 查询保单补发次数
	 * 
	 * @param policyNo
	 * @return
	 */
	public Integer getPolicyProvideTimeByPolicyNo(String policyNo) {
		return (Integer) queryForObject("getPolicyProvideTimeByPolicyNo",
				policyNo);
	}

	/**
	 * 取得保单投保日期
	 * 
	 * @return
	 */
	public Date getPolicyApplyDate(String policyNo) {
		return (Date) queryForObject("getPolicyApplyDate", policyNo);
	}

	/**
	 * 取得数据库当前时间
	 * 
	 * @return
	 */
	public Date getSystemDate() {
		return (Date) queryForObject("getSystemDate", null);
	}

	/**
	 * 判断保全受理是否为非转账在途的待收付处理件
	 * 
	 * @param posNo
	 *            变更的保全号
	 * @return
	 */
	public boolean canAdjustChargingMethod(String posNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_pos_no", posNo);
		queryForObject("canAdjustChargingMethod", paraMap);
		String flag = (String) paraMap.get("p_flag");
		String message = (String) paraMap.get("p_message");
		if (!"0".equals(flag))
			throw new RuntimeException("flag:" + flag + ", message:" + message);

		return "Y".equals(paraMap.get("p_can_adjust_flag"));
	}

	/**
	 * 获取险种的缴费年期/保险年期/计算日期的剩余缴费年期/剩余保险年期
	 * 
	 * @param posNo
	 *            参数组a
	 * @param productCode
	 *            参数组a
	 * @param policyNo
	 *            参数组b
	 * @param prodSeq
	 *            参数组b
	 * @param calcDate
	 *            默认取系统时间 参数组ab
	 * @return
	 */
	public Map getProductPremCoverage(String posNo, String productCode,
			String policyNo, String prodSeq, Date calcDate) {
		Map pMap = new HashMap();
		pMap.put("posNo", posNo);
		pMap.put("productCode", productCode);
		pMap.put("policyNo", policyNo);
		pMap.put("prodSeq", prodSeq);
		pMap.put("calcDate", calcDate == null ? getSystemDate() : calcDate);

		queryForObject("GET_POL_PROD_PREM_COVERAGE", pMap);

		return pMap;
	}

	/**
	 * 查询险种代码
	 * 
	 * @param policyNo
	 * @param prodSeq
	 * @return
	 */
	public String getProductCodeByPolicyNoAndProdSeq(String policyNo,
			Integer prodSeq) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("prodSeq", prodSeq);
		return (String) queryForObject("getProductCodeByPolicyNoAndProdSeq",
				paraMap);
	}

	/**
	 * 查询保单原账户信息
	 * 
	 * @param policyNo
	 * @return
	 */
	public Map getPolicyBankacctno(String policyNo) {
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);

		queryForObject("GET_POLICY_BANKACCTNO", pMap);

		return pMap;
	}

	/**
	 * 查询银行大类
	 * 
	 * @param policyNo
	 * @return
	 */
	public List<Map<String, Object>> queryBankCategory(String bankCode) {

		return queryForList("queryBankCategory", bankCode);

	}

	/**
	 * 根据产品代码查询产品全称
	 * 
	 * @param productCode
	 * @return
	 */
	public String getProductFullNameByProductCode(String productCode) {
		return (String) queryForObject("getProductFullNameByProductCode",
				productCode);
	}

	/**
	 * 根据保单号和险种序号查失效日期
	 * 
	 * @param policyNo
	 * @param prodSeq
	 * @return
	 */
	public Date queryLapseDateByPolicyNoAndProdSeq(String policyNo,
			Integer prodSeq) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("prodSeq", prodSeq);
		return (Date) queryForObject("queryLapseDateByPolicyNoAndProdSeq",
				paraMap);
	}

	/**
	 * 根据保单号查失效日期，结果为主险的失效日期
	 * 
	 * @param policyNo
	 * @return
	 */
	public Date queryLapseDateByPolicyNo(String policyNo) {
		return (Date) queryForObject("queryLapseDateByPolicyNo", policyNo);
	}

	/**
	 * 获取险种的基本保额/保费 保费算保额险种获取险种基本保费 保额算保费险种获取险种基本保额
	 * 
	 * @param productCode
	 * @return
	 */
	public BigDecimal getProductPremUnitByProductCode(String productCode) {
		return (BigDecimal) queryForObject("getProductPremUnitByProductCode",
				productCode);
	}

	/**
	 * 根据保全号码查询批次号
	 * 
	 * @param posNo
	 * @return
	 */
	public String queryPosBatchNoByPosNo(String posNo) {
		return (String) queryForObject("queryPosBatchNoByPosNo", posNo);
	}

	/**
	 * 判断产品是否在售
	 * 
	 * @param productCode
	 * @param channelCode
	 * @param branchCode
	 * @param premPeriodType
	 * @param premPeriod
	 * @param coverPeriodType
	 * @param coverPeriod
	 * @param bizDate
	 * @return Map&lt;String, Object&gt;<br/>
	 *         输入：<br/>
	 *         p_product_code 产品代码<br/>
	 *         p_channel_code 渠道<br/>
	 *         p_branch_code 机构<br/>
	 *         p_prem_period_type 缴费期类型<br/>
	 *         p_prem_period 缴费期<br/>
	 *         p_cover_period_type 保障期类型<br/>
	 *         p_cover_period 保障期<br/>
	 *         p_biz_date 业务日期<br/>
	 *         输出：<br/>
	 *         p_sale_flag 是否在售标志：Y-在售；N-停售；<br/>
	 *         p_sign 执行结果: Y-成功；N-失败；E-异常；<br/>
	 *         p_message 结果描述<br/>
	 */
	public Map<String, Object> checkProductIsOnSale(String productCode,
			String channelCode, String branchCode, String premPeriodType,
			BigDecimal premPeriod, String coverPeriodType,
			BigDecimal coverPeriod, Date bizDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_product_code", productCode);
		paraMap.put("p_channel_type", channelCode);
		paraMap.put("p_branch_code", branchCode);
		paraMap.put("p_prem_period_type", premPeriodType);
		paraMap.put("p_prem_period", premPeriod);
		paraMap.put("p_cover_period_type", coverPeriodType);
		paraMap.put("p_cover_period", coverPeriod);
		paraMap.put("p_biz_date", bizDate);
		queryForObject("checkProductIsOnSale", paraMap);
		return paraMap;
	}

	/**
	 * 查询保全规则检查不通过信息
	 * 
	 * @param posNo
	 * @return
	 */
	public List<String> queryRuleCheckMsgByPosNo(String posNo) {
		return queryForList("queryRuleCheckMsgByPosNo", posNo);
	}

	/**
	 * 查询保全受理信息
	 * 
	 * @param posNo
	 * @return
	 */
	public List<String> queryPosAcceptDetailInfo(String posNo,
			String posObject, String itemNo) {

		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("posNo", posNo);
		paraMap.put("posObject", posObject);
		paraMap.put("itemNo", itemNo);
		return queryForList("queryPosAcceptDetailInfo", paraMap);
	}

	/**
	 * 查询保全受理信息Old_value
	 * 
	 * @param posNo
	 * @return
	 */
	public List<String> queryPosAcceptDetailoldvalue(String posNo,
			String posObject, String itemNo) {

		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("posNo", posNo);
		paraMap.put("posObject", posObject);
		paraMap.put("itemNo", itemNo);
		return queryForList("queryPosAcceptDetailoldvalue", paraMap);
	}

	/**
	 * 根据条码号及保全类型从指定的批次中查询保全项目
	 * 
	 * @param posBatchNo
	 * @param breakBarcodeNo
	 * @param breakServiceItems
	 * @return
	 */
	public List<String> queryBreakPosNoByBarcodeAndServiceItems(
			String posBatchNo, String breakBarcodeNo, String breakServiceItems) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("posBatchNo", posBatchNo);
		paraMap.put("breakBarcodeNo", breakBarcodeNo);
		paraMap.put("breakServiceItems", breakServiceItems);
		return queryForList("queryBreakPosNoByBarcodeAndServiceItems", paraMap);
	}

	/**
	 * 根据条码号及保全类型从指定的批次中查询保全项目
	 * 
	 * @param posBatchNo
	 * @param breakBarcodeNo
	 * @param breakServiceItems
	 * @return
	 */
	public List<String> queryPosNoByBarcodeAndServiceItems(
			String breakBarcodeNo, String breakServiceItems) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("breakBarcodeNo", breakBarcodeNo);
		paraMap.put("breakServiceItems", breakServiceItems);
		return queryForList("queryPosNoByBarcodeAndServiceItems", paraMap);
	}

	/**
	 * 根据投保单号获取投保人、被保人的客户号
	 * 
	 * @param applyNo
	 * @return Map&lt;String, Object&gt;<br/>
	 *         参数说明: p_apply_bar_code 投保单条码号<br/>
	 *         p_applicant_no 投保人客户号<br/>
	 *         p_insured_no 主被保人客户号<br/>
	 *         p_flag 成功标识（1--成功 2--失败）<br/>
	 *         p_message 失败的原因描述，计算成功时为空<br/>
	 */
	public Map<String, Object> getApplyClient(String applyBarcode) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_apply_bar_code", applyBarcode);
		queryForObject("getApplyClient", paraMap);
		return paraMap;
	}

	/**
	 * 查询申请日期
	 * 
	 * @param posNo
	 * @return
	 */
	public Date getApplyDateByPosNo(String posNo) {
		return (Date) queryForObject("getApplyDateByPosNo", posNo);
	}

	/**
	 * 判断投被保人是否为同一人
	 * 
	 * @param policyNo
	 * @return
	 */
	public boolean isAppInsTheSame(String policyNo) {
		return "Y".equals(queryForObject("isAppInsTheSame", policyNo));
	}

	/**
	 * 转账账户户名是否可以与投保人不一致，该判断适用于1214类申请书（投资账户变更申请书）转账 账户户名可以与投保人不一致特殊情况的鉴定
	 * 
	 * @param policyNo
	 * @return
	 */
	public boolean accountNameCanBeNotSameWithApplicant(String policyNo) {
		return "Y".equals(queryForObject(
				"accountNameCanBeNotSameWithApplicant", policyNo));
	}

	/**
	 * 校验投保单是否可收费
	 * 
	 * @param applyBarcode
	 *            投保单号(非空)
	 * @return Map&lt;String, Object&gt;<br/>
	 *         参数说明: p_apply_bar_code in 投保单号(非空)<br/>
	 *         -- p_receipt_flag out 是否可收费标志（非空；Y 可收费，N不可收费）<br/>
	 *         -- p_channel_type out 业务渠道(非空)<br/>
	 *         -- p_prem_due out 应收金额(非空)<br/>
	 *         -- p_branch_code out 机构代码<br/>
	 *         -- p_flag out 执行成功失败标志(非空,1--成功，2--失败)<br/>
	 *         -- p_message out 执行结果信息<br/>
	 */
	public Map<String, Object> uwValidateApplyReceipt(String applyBarcode) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_apply_bar_code", applyBarcode);
		queryForObject("uwValidateApplyReceipt", paraMap);
		return paraMap;
	}

	/**
	 * 查询用户姓名
	 * 
	 * @param userId
	 * @return
	 */
	public String getUserNameByUserId(String userId) {
		return (String) queryForObject("getUserNameByUserId", userId);
	}

	/**
	 * 查询机构名称
	 * 
	 * @param branchCode
	 * @return
	 */
	public String getBranchNameByBranchCode(String branchCode) {
		return (String) queryForObject("getBranchNameByBranchCode", branchCode);
	}

	/**
	 * 根据受理号查受理机构名称
	 * 
	 * @param posNo
	 * @return
	 */
	public String getBranchFullNameByPosNo(String posNo) {
		return (String) queryForObject("getBranchFullNameByPosNo", posNo);
	}

	/**
	 * 根据受理号查受理机构代码
	 * 
	 * @param posNo
	 * @return
	 */
	public String getAcceptBranchCodeByPosNo(String posNo) {
		return (String) queryForObject("getBranchCodeByPosNo", posNo);
	}

	/**
	 * 查询险种是否有保证给付条款
	 * 
	 * @param productCode
	 * @return
	 */
	public boolean hasPromisePayDuty(String productCode) {
		return "Y".equals(queryForObject("hasPromisePayDuty", productCode));
	}

	/**
	 * 获取保全项目退费的试算金额
	 * 
	 * @param posNo
	 * @return
	 */
	public BigDecimal getPosRefundSum(String posNo) {
		return (BigDecimal) queryForObject("getPosRefundSum", posNo);
	}

	/**
	 * 查询保单的投保单条码
	 * 
	 * @param policyNo
	 * @return
	 */
	public String queryPolicyApplyBarcode(String policyNo) {
		return (String) queryForObject("QUERY_POLICY_APPLY_BAR_CODE", policyNo);
	}

	/**
	 * 查询保单的投保单保单号码
	 * 
	 * @param policyNo
	 * @return
	 */
	public String queryUWPolicyNo(String applyBarCode) {
		return (String) queryForObject("QUERY_UW_POLICY_NO", applyBarCode);
	}

	/**
	 * 校验银行账号及银行代码
	 * 
	 * @param bankCode
	 * @param bankNo
	 * @return
	 */
	public Map<String, Object> validateBankAccount(String bankCode,
			String bankNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_bank_code", bankCode);
		paraMap.put("p_account_no", bankNo);
		queryForObject("validateBankAccount", paraMap);
		return paraMap;
	}

	/**
	 * 查询保全对应的影像是否已存在了
	 * 
	 * @param posNo
	 * @return acceptor--受理人，barCode--条形码
	 */
	public Map queryImageExist(String posNo) {
		return (Map) queryForObject("QUERY_IMAGE_EXIST_POS_NO", posNo);
	}

	/**
	 * 协议退费查询
	 * 
	 * @param specialFunc
	 * @return
	 */
	public List querySpecialRetreatReason(String specialFunc) {
		return queryForList("querySpecialRetreatReason", specialFunc);
	}

	/**
	 * 查询问题函下发截止时间
	 * 
	 * @param posNo
	 * @return
	 */
	public Map<String, Object> queryProblemLetterEndDate(String posNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_pos_no", posNo);
		queryForObject("queryProblemLetterEndDate", paraMap);
		return paraMap;
	}

	/**
	 * 查询保全申请批次信息
	 * 
	 * @param posNo
	 * @return
	 */
	public PosApplyBatchDTO queryPosApplyBatchRecord(String posBatchNo) {
		return (PosApplyBatchDTO) queryForObject("queryPosApplyBatchRecord",
				posBatchNo);
	}

	/**
	 * 查询保全申请书信息
	 * 
	 * @param posNo
	 * @return
	 */
	public PosApplyFilesDTO queryPosApplyfilesRecord(String barcodeNo) {
		return (PosApplyFilesDTO) queryForObject("queryPosApplyfilesRecord",
				barcodeNo);
	}

	/**
	 * 根据posNo查询detail表信息
	 * 
	 * @param posNo
	 * @return
	 */
	public List<Map<String, Object>> getPosAcceptDetailByPosNo(String posNo) {
		return (List<Map<String, Object>>) queryForList(
				"getPosAcceptDetailByPosNo", posNo);
	}

	/**
	 * 查询保单状态
	 * 
	 * @param policyNo
	 * @return
	 */
	public String getPolicyDutyStatus(String policyNo) {

		return (String) queryForObject("getPolicyDutyStatus", policyNo);
	}

	/**
	 * posNo是否处于status状态
	 * 
	 * @param posNo
	 * @param status
	 * @return Y N
	 */
	public String posStatusCheck(String posNo, String status) {
		Map pMap = new HashMap();
		pMap.put("posNo", posNo);
		pMap.put("status", status);

		return (String) queryForObject("QUERY_POS_STATUS_CHECK", pMap);
	}

	/**
	 * 根据受理渠道及投保人id查询未完成的保全
	 * 
	 * @param acceptChannelCode
	 * @param applicantNo
	 * @return
	 */
	public List<String> queryNotCompletePosNos(String acceptChannelCode,
			String applicantNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("acceptChannelCode", acceptChannelCode);
		paraMap.put("applicantNo", applicantNo);
		return queryForList("QUERY_NOT_COMPLETE_POSNOS", paraMap);
	}

	/**
	 * 查询银行大类
	 * 
	 * @param bankCode
	 * @return
	 */
	public String getBankCategoryByBankCode(String bankCode) {

		return (String) queryForObject("getBankCategoryByBankCode", bankCode);
	}

	/**
	 * 查询保单联系地址
	 * 
	 * @param policy_no
	 * @return
	 */

	public String getPolicyAddressByPolicyNo(String policy_no) {

		return (String) queryForObject("getPolicyAddressByPolicyNo", policy_no);
	}

	/**
	 * @param posNo
	 * @param posObject
	 * @return
	 */
	public List<PosAcceptDetailDTO> queryPosAcceptDetailByPosNoAndPosObject(
			String posNo, String posObject) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("posNo", posNo);
		paraMap.put("posObject", posObject);
		return queryForList("queryPosAcceptDetailByPosNoAndPosObject", paraMap);
	}

	/***
	 * 根据保单号，查询应退健康服务费信息
	 * 
	 * @param policy_no
	 * @return
	 */
	public Map<String, Object> queryexaminationbal(String p_policy_no) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", p_policy_no);
		queryForObject("queryexaminationbal", paraMap);
		return paraMap;
	}

	/***
	 * 根据保单号，查询保单犹豫期截止日
	 * 
	 * @param policy_no
	 * @return
	 */
	public Map<String, Object> querypCruplePeriodPolicy(String p_policy_no) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", p_policy_no);
		queryForObject("querypcrupleperiodpolicy", paraMap);
		return paraMap;
	}

	/**
	 * 根据保淘宝订单号查询该订单完成的保全
	 * 
	 * @param bankCode
	 * @return
	 */
	public String queryPosNoByAccountNo(String accountNo, String policyNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("accountNo", accountNo);
		paraMap.put("policyNo", policyNo);
		return (String) queryForObject("queryPosNoByAccountNo", paraMap);
	}

	/**
	 * 查询保单生存金领取次数
	 * 
	 * @param policyNo
	 * @return
	 */
	public Integer getSurvivalPayCount(String policyNo) {
		return (Integer) queryForObject("getSurvivalPayCount", policyNo);
	}

	/**
	 * 检查保单理赔历史（包括已理赔和正在理赔的有效案件） 参数说明: p_policy_no 保单号 p_start_date 开始日期
	 * p_end_date 结束日期 p_is_policy_claimed 保单是否存在理赔，Y存在，N不存在 p_flag
	 * 是否成功标志，Y成功，N不成功 p_message 返回信息
	 */
	public Map<String, Object> checkPolicyClaimHistory(String policyNo,
			Date effectDate, Date curDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_start_date", effectDate);
		paraMap.put("p_end_date", curDate);
		queryForObject("checkPolicyClaimHistory", paraMap);
		return paraMap;
	}

	/***
	 * 查询保单缴次次数
	 * 
	 * @param policy_no
	 * @return
	 */
	public Map<String, Object> queryPolicyPremTimes(String p_policy_no) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", p_policy_no);
		queryForObject("queryPolicyPremTimes", paraMap);
		return paraMap;
	}

	/**
	 * 查询是否淘宝通过网销操作保全（退保，契撤）的保单
	 * 
	 * @param policy_no
	 * @param prodSeq
	 * @return Y/N
	 */
	public String isTbToWxPolicy(String policyNo, String prodSeq) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("prodSeq", prodSeq);
		return (String) queryForObject("isTbToWxPolicy", paraMap);
	}

	/**
	 * <!-- 根据保单号，附加信息定义类型，查询保单附加信息 -->
	 */
	public Map<String, Object> getPolicySpinfoDetail(String policyNo,
			String defcode) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_def_code", defcode);
		queryForObject("getPolicySpinfoDetail", paraMap);
		return paraMap;
	}

	/**
	 * 查询保单生存金领取次数
	 * 
	 * @param policyNo
	 * @return
	 */
	public Integer getProductPaidSurvivalCount(String policyNo, Integer prodSeq) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("prodSeq", prodSeq);

		return (Integer) queryForObject("getProductPaidSurvivalCount", paraMap);
	}

	/**
	 * 根据保全项目判断条形码是否匹配
	 * 
	 * @param posNo
	 * @param posObject
	 * @return
	 */
	public String isMatchBarcodeNo(String serviceItems, String barcodeNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("serviceItems", serviceItems);
		paraMap.put("barcodeNo", barcodeNo);
		return (String) queryForObject("isMatchBarcodeNo", paraMap);
	}

	/**
	 * 根据受理机构查询柜面号
	 * 
	 * @param branchCode
	 *            机构代码
	 * @return 柜面号
	 */
	public String getEpointCounterNo(String branchCode) {
		return (String) queryForObject("getEpointCounterNo", branchCode);
	}

	/**
	 * 官网保全对契约撤销和退保的保单限制
	 * 
	 * @param posNo
	 * @param posObject
	 * @return
	 */
	public List<PolicyProductDTO> getWebAcceptPolicys(Map criteriaMap) {

		List<PolicyProductDTO> lsWebAcceptPolicys = queryForList(
				"getWebAcceptPolicys", criteriaMap);

		return lsWebAcceptPolicys;
	}

	/**
	 * E终端保全对契约撤销和退保的保单限制
	 * 
	 * @param posNo
	 * @param posObject
	 * @return
	 */
	public List<PolicyProductDTO> getEpointAcceptPolicys(Map criteriaMap) {

		List<PolicyProductDTO> lsWebAcceptPolicys = queryForList(
				"getEpointAcceptPolicys", criteriaMap);

		return lsWebAcceptPolicys;
	}

	/**
	 * 存在自垫或贷款
	 * 
	 * @param policyNo
	 * @return "Y,N"
	 */
	public String canDoPayBack(String policyNo) {

		return (String) queryForObject("canDoPayBack", policyNo);
	}

	/**
	 * 能否操作预约终止附加险
	 * 
	 * @param policyNo
	 * @return "Y,N"
	 */
	public String canDoCancelProduct(String policyNo) {

		return (String) queryForObject("canDoCancelProduct", policyNo);
	}

	/**
	 * 能否操作保全收付费方式调整
	 * 
	 * @param policyNo
	 * @return
	 */
	public String canDoModifyTransferAccountInfo(String policyNo) {

		return (String) queryForObject("canDoModifyTransferAccountInfo",
				policyNo);
	}

	/**
	 * 官网查询保全转账失败详情
	 * 
	 * @param policyNo
	 * @return
	 */
	public PosInfoDTO queryTransferFailInfo(String policyNo) {

		return (PosInfoDTO) (queryForList("queryTransferFailInfo", policyNo))
				.get(0);
	}

	/**
	 * 官网保全追加保费的保单限制
	 * 
	 * @param posNo
	 * @param posObject
	 * @return
	 */
	public List<PolicyProductDTO> getAddPremPolicys(Map criteriaMap) {

		List<PolicyProductDTO> lsAddPremPolicys = queryForList(
				"getAddPremPolicys", criteriaMap);

		return lsAddPremPolicys;
	}

	/**
	 * @param policyNo
	 * @return
	 */
	public String queryFinancialContractNo(String policyNo) {
		return (String) queryForObject("queryFinancialContractNo", policyNo);

	}

	/**
	 * 根据理财合同号和缴费金额、缴费日期，计算初始费用金额
	 * 
	 * @return
	 */
	public Map<String, Object> calcInitialExpenses(String policyNo,
			String p_prem_amount) {

		String financialContractNo = queryFinancialContractNo(policyNo);
		FinPremDTO finPremDTO = new FinPremDTO();
		finPremDTO.setPolicyNo(policyNo);
		finPremDTO.setPolicyPremChgType("4");
		finPremDTO.setFrequency("0");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("p_financial_contract_no", financialContractNo);
		map.put("p_policy_prem_chg_type", "4");
		map.put("p_prem_amount", new BigDecimal(p_prem_amount));
		map.put("p_rec_fin_prem", finPremDTO);
		map.put("p_prem_date", getSystemDate());

		queryForObject("calcInitialExpenses", map);
		return map;

	}

	/**
	 * 续期缴费方式变更
	 * 
	 * @param criteriaMap
	 * @return
	 */
	public List<String> getRepayTypeChangePolicys(Map criteriaMap) {
		List<String> lsWebAcceptPolicys = queryForList(
				"getRepayTypeChangePolicys", criteriaMap);

		return lsWebAcceptPolicys;

	}

	// 查询咨诉渠道barcodeNosequence
	public String queryBarcodeNoSequence() {

		return (String) queryForObject("queryCssChannelBarcodeNoSequence", null);
	}

	/**
	 * 查询客户e服务权限等级
	 * 
	 * @param policyNo
	 * @return
	 */
	public String queryClientInputPrivs(String clientNo) {
		return (String) queryForObject("QUERY_INPUT_PRIVS", clientNo);

	}

	// 查询咨诉渠道barcodeNosequence
	public String queryEpointBarcodeNoSequence() {
		return (String) queryForObject("queryEpointChannelBarcodeNoSequence",
				null);
	}

	/**
	 * 条形码获取后两位
	 * 
	 * @param map
	 * @return Map<String, Object>
	 * @author GaoJiaMing
	 * @time 2014-5-29
	 */
	public Map<String, Object> getBarCodeSum(Map<String, Object> map) {
		return (Map<String, Object>) queryForObject(
				"QUERY_POLICYNO_BAR_CODE_SUM", map);
	}

	/**
	 * 获取险种除贷款自垫及保单余额外的退费总额
	 * 
	 * @param p_policy_no
	 *            p_prod_seq p_calc_date
	 * @return
	 */
	public BigDecimal getProductPremSum(String p_policy_no, String p_prod_seq,
			Date p_calc_date) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", p_policy_no);
		paraMap.put("p_prod_seq", p_prod_seq);
		paraMap.put("p_calc_date", p_calc_date);
		return (BigDecimal) queryForObject("getProductPremSum", paraMap);
	}

	/**
	 * 获取保单余额
	 * 
	 * @param p_policy_no
	 * @return
	 */
	public BigDecimal getPolicyBlance(String p_policy_no) {
		return (BigDecimal) queryForObject("getPolicyBlance", p_policy_no);
	}

	/**
	 * 获取收费金额
	 * 
	 * @param p_policy_no
	 * @return
	 */
	public Map<String, Object> getPremPaid(String p_policy_no,
			String p_prod_seq, Date p_calc_date) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", p_policy_no);
		paraMap.put("p_prod_seq", p_prod_seq);
		paraMap.put("p_calc_date", p_calc_date);
		queryForObject("getPremPaid", paraMap);
		return paraMap;
	}

	/**
	 * 获取投保人姓名
	 * 
	 * @param p_policy_no
	 * @return
	 */
	public String getAppNameByPolicyNo(String p_policy_no) {
		return (String) queryForObject("getAppNameByPolicyNo", p_policy_no);
	}

	/**
	 * 能否操作操作年金给付方式变更
	 * 
	 * @param policyNo
	 * @return "Y,N"
	 */
	public String canDoServiceItems_24(String policyNo) {
		return (String) queryForObject("canDoServiceItems_24", policyNo);
	}

	/**
	 * 查询保单能否操作账户分配比例变更
	 * 
	 * @param policyNo
	 * @return "Y,N"
	 */
	public String canDoServiceItems_38(String policyNo) {
		return (String) queryForObject("canDoServiceItems_38", policyNo);
	}

	/**
	 * 查询保单能否操作投资账户转换变更
	 * 
	 * @param policyNo
	 * @return "Y,N"
	 */
	public String canDoServiceItems_39(String policyNo) {
		return (String) queryForObject("canDoServiceItems_39", policyNo);
	}

	/**
	 * 查询保单能否在官网操作客户资料变更
	 * 
	 * @param policyNo
	 * @return "Y,N"
	 */
	public String canDoServiceItems_21(String policyNo) {
		return (String) queryForObject("canDoServiceItems_21", policyNo);
	}

	/**
	 * 查询保单能否在官网操作生存金领取
	 * 
	 * @param policyNo
	 * @return "Y,N"
	 */
	public String canDoServiceItems_10(String policyNo) {
		return (String) queryForObject("canDoServiceItems_10", policyNo);
	}

	/**
	 * 根据保单号和订单号查询该保单的订单是否已经加保交易完成
	 * 
	 * @param policy_no
	 * @param orderNo
	 * @return Y/N
	 */
	public String checkOrderNoIsDeal(String policyNo, String orderNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("orderNo", orderNo);
		return (String) queryForObject("checkOrderNoIsDeal", paraMap);
	}

	/**
	 * 根据保单号和订单号查询该保单的订单是否处于待收费状态
	 * 
	 * @param policy_no
	 * @param orderNo
	 * @return Y/N
	 */
	public String checkOrderNoIsFinStaus(String policyNo, String orderNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("orderNo", orderNo);
		return (String) queryForObject("checkOrderNoIsFinStaus", paraMap);
	}

	/**
	 * 根据加保保单号和订单号查询该保单的订单处于待收费状态的保全号
	 * 
	 * @param policy_no
	 * @param orderNo
	 * @return Y/N
	 */
	public String queryFinStausPosNo(String policyNo, String orderNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("orderNo", orderNo);
		return (String) queryForObject("queryFinStausPosNo", paraMap);
	}

	/**
	 * 根据客户号，电话号码，查询客户信息
	 * 
	 * @param clientNO
	 *            phoneNo
	 * @return
	 */
	public Map<String, Object> getClientPhoneInfo(String clientNo,
			String phoneNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("clientNo", clientNo);
		paraMap.put("phoneNo", phoneNo);
		return (Map<String, Object>) queryForObject("getClientPhoneInfo",
				paraMap);
	}

	/**
	 * 停用客户电话
	 * 
	 * @param
	 * @return
	 */

	public Map<String, Object> stopClientPhone(String p_batch_no,
			String p_client_no, String p_phone_no, String p_processor,
			Date p_processed_date) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_batch_no", p_batch_no);
		paraMap.put("p_client_no", p_client_no);
		paraMap.put("p_phone_no", p_phone_no);
		paraMap.put("p_processor", p_processor);
		paraMap.put("p_processed_date", p_processed_date);
		queryForObject("stopClientPhone", paraMap);
		return paraMap;
	}

	/**
	 * 根据批次号和手机号查询需要恢复的客户电话信息
	 * 
	 * @param clientNO
	 *            batchNo
	 * @return
	 */
	public List<Map<String, Object>> getClientPhoneInfoByBachtNo(
			String batchNo, String phoneNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("batchNo", batchNo);
		paraMap.put("phoneNo", phoneNo);
		return queryForList("getClientPhoneInfoByBachtNo", paraMap);
	}

	/**
	 * 恢复客户电话
	 * 
	 * @param
	 * @return
	 */

	public Map<String, Object> rollbackClientPhone(String p_batch_no,
			String p_client_no, String p_phone_no, String p_rollbacked_user,
			Date p_rollbacked_date) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_batch_no", p_batch_no);
		paraMap.put("p_client_no", p_client_no);
		paraMap.put("p_phone_no", p_phone_no);
		paraMap.put("p_rollbacked_user", p_rollbacked_user);
		paraMap.put("p_rollbacked_date", p_rollbacked_date);
		queryForObject("rollbackClientPhone", paraMap);
		return paraMap;
	}

	/***
	 * 根据批次号查询客户停用的保单
	 * 
	 * @param batch_no
	 * @return
	 */
	public List<Map<String, String>> getStopClientPolicy(String batch_no) {
		return (List<Map<String, String>>) getSqlMapClientTemplate()
				.queryForList(getClass().getName() + ".getStopClientPolicy",
						batch_no);
	}

	/***
	 * 根据保单号查询电销项目代码
	 * 
	 * @param policyNo
	 * @return projectCode
	 */
	public String getProjectCodeByPolicyNo(String policyNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		queryForObject("queryTssPolicyPeoject", paraMap);
		return (String) paraMap.get("p_project_code");

	}

	/**
	 * 校验代办代审退费金额超限制
	 * 
	 * @param posNo
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-9-19
	 */
	public String checkIsExceedExamPrivs(String posNo) {
		return (String) queryForObject("checkIsExceedExamPrivs", posNo);
	}

	/**
	 * 查询规则特殊件原因
	 * 
	 * @param posNo
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-9-19
	 */
	public String getSpecialRuleReason(String posNo) {
		return (String) queryForObject("getSpecialRuleReason", posNo);
	}

	/**
	 * 官网/自助终端/微官网上，保单是否展示
	 * 
	 * @param policyNo
	 *            serviceItems acceptChannel
	 * @return
	 */
	public String canDoServiceItems(String policyNo, String serviceItems,
			String acceptChannel) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_service_items", serviceItems);
		paraMap.put("p_accept_channel", acceptChannel);
		return (String) queryForObject("canDoServiceItems", paraMap);
	}

	/**
	 * 取代审类别
	 * 
	 * @param posNo
	 * @return String
	 * @author yezhi.qin
	 * @time 2015-1
	 */
	public String getExamType(String posNo) {
		return (String) queryForObject("getExamType", posNo);
	}

	/**
	 * 本次保全加保的保费及保额
	 * 
	 * @param clientNO
	 *            batchNo
	 * @return
	 */
	public List<Map<String, Object>> getPosPolChangeList(String policyNo,
			String posNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("posNo", posNo);
		return queryForList("getPosPolChangeList", paraMap);
	}

	/**
	 * 取消自动退保
	 * 
	 * @param
	 * @return
	 */

	public Map<String, Object> cancelAutoSurrender(String policyNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		queryForObject("cancelAutoSurrender", paraMap);
		return paraMap;
	}

	/**
	 * 获取当前保单保签收信息
	 * 
	 * @param policyNo
	 * @param agentNo
	 * @return Map<String,Object>
	 * @author Zhangyi
	 * @time 2015-3-26
	 */
	public Map<String, Object> queryReceivePolicyInfo(String policyNo,
			String agentNo) {
		// 通过保单号获取代理人ID和投保人联系方式

		Map<String, Object> pMap = new HashMap<String, Object>();
		String gagentNo = (String) queryForObject("getAgentNoByPolicyNo",
				policyNo);
		if (gagentNo.equals(agentNo)) {
			pMap.put("p_policy_no", policyNo);
			queryForObject("queryReceivePolicyInfo", pMap);
			if ("2".equals(pMap.get("p_flag"))) {
				pMap.put("flag", "N");
				pMap.put("message", "查询失败");
				return pMap;
			}
			pMap.put("flag", "Y");
			pMap.put("message", "成功");
			return pMap;
		}
		pMap.put("flag", "N");
		pMap.put("message", "该保单不属于当前代理人");
		return pMap;
	}

	/**
	 * 查询保单号是否存在
	 * 
	 * @param policyNo
	 * @return String
	 * @author Zhangyi
	 * @time 2015-3-30
	 */
	public String existByPolicyNo(String policyNo) {
		return (String) queryForObject("existByPolicyNo", policyNo);
	}

	/**
	 * 查询保单签收状态
	 * 
	 * @param policyNo
	 * @return String
	 * @author Zhangyi
	 * @time 2015-4-2
	 */
	public String querySignFlagByPolicyNo(String policyNo) {
		return (String) queryForObject("querySignFlagByPolicyNo", policyNo);
	}

	/**
	 * 查询保单发放状态
	 * 
	 * @param policyNo
	 * @return String
	 * @author Zhangyi
	 * @time 2015-4-2
	 */
	public String queryPrintFlagByPolicyNo(String policyNo) {
		return (String) queryForObject("queryPrintFlagByPolicyNo", policyNo);
	}

	/**
	 * 查询帐户价值接口<br/>
	 * 投连账户价值按照传入日期的下一评估日价格计算，如下一评估日未定价，则按照最近价格计算
	 * 
	 * @param policyNo
	 *            保单号
	 * @param prodSeq
	 *            产品序号
	 * @param requestDate
	 *            查询日期
	 * @return Map<String, Object> 包含如下Key：<br/>
	 *         p_flag:返回标志 '0' 成功 '1' 失败<br/>
	 *         p_message:报错信息<br/>
	 *         p_product_cash_value 现价 p_amt_type 费用类型 p_amt_amount 费用金额
	 *         p_total_amount:帐户价值总金额<br/>
	 * 
	 */
	public Map<String, Object> getProductValueDetail(String policyNo,
			String p_product_code, Date requestDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_product_code", p_product_code);
		paraMap.put("p_calc_date", requestDate);
		queryForObject("getProductValueDetail", paraMap);

		ArrayTypeResultHandler handler = new FinancialProductsArrayHandler();
		handler.handleResult(paraMap);

		return paraMap;
	}

	/**
	 * --功能说明： 取理财险的账户价值明细 注意:对于投连险种,如不能实时交易,则以最近交易价格进行估算 参数说明： 输入参数：
	 * p_policy_no ：保单号 p_calc_date : 计算日期(受理申请日期) 输出参数： p_fin_type 理财类型 N 非理财
	 * ，U 万能，F投连 p_first_prem 首期保费, p_first_add_prem 首期追加, p_add_prem 保全追加,
	 * p_withdraw_prem 保全部分领取, p_total_amount 账户价值, p_benefit_amount 累计收益,
	 * p_flag 返回标志 '0' 成功 '1' 失败0 p_message 报错信息
	 * 
	 */
	public Map<String, Object> getFinValueInfo(String policyNo, Date requestDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_calc_date", requestDate);
		queryForObject("getFinValueInfo", paraMap);
		return paraMap;
	}

	/**
	 * 取理财险某个时间段的账户价值明细
	 * 
	 * @methodName: getFinValueInfoList
	 * @Description:
	 * @param policyNo
	 * @param startDate
	 * @param endDate
	 * @return List<String>
	 * @author WangMingShun
	 * @date 2015-6-30
	 * @throws
	 */
	public List getFinValueInfoList(String policyNo, Date startDate,
			Date endDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("startDate", startDate);
		paraMap.put("endDate", endDate);

		queryForObject("getFinValueInfoList", paraMap);
		String flag = (String) paraMap.get("p_flag");
		String msg = (String) paraMap.get("p_message");
		if (!"0".equals(flag))
			throw new RuntimeException("查询账户价值明细失败失败：" + msg);

		return new FinancialProductsArrayHandler()
				.handleFinAccountArrayResult(paraMap);
	}

	/**
	 * 根据险种和到期时间查询保单客户信息
	 * 
	 * @param productCode
	 *            endDate
	 * @return
	 */
	public List<Map<String, Object>> getPolicyClientListByproductCode(
			String productCode, Date endDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("productCode", productCode);
		paraMap.put("endDate", endDate);
		return (List<Map<String, Object>>) queryForList(
				"getPolicyClientListByproductCode", paraMap);
	}

	/**
	 * 获取ServiceItemsDAO_44中的可转换险种
	 * 
	 * @methodName: getchangeableProduct
	 * @Description:
	 * @param policyNo
	 * @return String
	 * @author WangMingShun
	 * @date 2015-7-27
	 * @throws
	 */
	public String getchangeableProduct(String policyNo) {
		return (String) queryForObject("getChangeableProduct", policyNo);
	}

	/**
	 * @Description: 查询保单是否做了类信托
	 * @methodName: getPolicyIsPwm
	 * @param policyNo
	 * @return
	 * @return String
	 * @author WangMingShun
	 * @date 2015-7-31
	 * @throws
	 */
	public String getPolicyIsPwm(String policyNo) {
		return (String) queryForObject("getPolicyIsPwm", policyNo);
	}

	/**
	 * @Description: 查询保单的类信托详细信息
	 * @methodName: getPolicyPwmInfo
	 * @param policyNo
	 * @return
	 * @return List<Map<String,Object>>
	 * @author WangMingShun
	 * @date 2015-7-31
	 * @throws
	 */
	public List<Map<String, Object>> getPolicyPwmInfo(String policyNo) {
		return queryForList("getPolicyPwmInfo", policyNo);
	}

	/**
	 * @Description: 根据保全号查询保单号
	 * @methodName: getPolicyNoByPosNo
	 * @param posNo
	 * @return
	 * @return String
	 * @author WangMingShun
	 * @date 2015-8-9
	 * @throws
	 */
	public String getPolicyNoByPosNo(String posNo) {
		return (String) queryForObject("getPolicyNoByPosNo", posNo);
	}

	/**
	 * @Description: 查看保单是否能做类信托
	 * @methodName: getCanDoPwm
	 * @param policyNo
	 * @return
	 * @return String
	 * @author WangMingShun
	 * @date 2015-8-12
	 * @throws
	 */
	public String getPolicyCanDoPwm(String policyNo) {
		return (String) getSqlMapClientTemplate().queryForObject(
				ServiceItemsDAO_22.class.getName() + ".getCanDoPwm", policyNo);
	}

	/**
	 * 根据保单号、计算日期,部分领取金额计算手续费
	 * 
	 * @param policyNo
	 *            保单号
	 * @param p_withdraw_amount
	 *            领取金额
	 * @param calcDate
	 *            计算日期
	 * @return Map<String, Object> 包含如下Key：<br/>
	 *         p_withdraw_charge_amount :领取手续费金额 p_calculate_formula :计算公式
	 *         p_calculate_parameter :计算参数值 p_flag :返回标志 '0' 成功 ,'1'
	 *         失败，调用者需要进行异常处理 p_message :返回信息
	 */
	public Map<String, Object> calcWithdrawCharge(String policyNo,
			String p_withdraw_amount, Date calcDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_withdraw_date", calcDate);
		paraMap.put("p_withdraw_amount", new BigDecimal(p_withdraw_amount));
		queryForObject("calcWithdrawCharge", paraMap);
		return paraMap;
	}

	/**
	 * @Description: 根据保单号查询最近一期续期保费退费信息
	 * @methodName: getFinPosPayInfo
	 * @param policyNo
	 * @return
	 * @return Map<String,Object>
	 * @author WangMingShun
	 * @date 2015-9-2
	 * @throws
	 */
	public Map<String, Object> getFinPosPayInfo(String policyNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		queryForObject("getFinPosPayInfo", paraMap);
		return paraMap;
	}

	/**
	 * @Description: 根据用户名查询柜面号
	 * @methodName: getCounterNoByUserId
	 * @param acceptor
	 * @return
	 * @return String
	 * @author WangMingShun
	 * @date 2015-9-22
	 * @throws
	 */
	public String getCounterNoByUserId(String userId) {
		return (String) queryForObject("getCounterNoByUserId", userId);
	}

	/**
	 * @Description: 根据保单号和计算日期获取贷款模式及贷款利率
	 * @methodName: getLoanRateType
	 * @param policyNo
	 * @param calcDate
	 * @return
	 * @return Map<String,Object>
	 * @author WangMingShun
	 * @date 2015-9-23
	 * @throws
	 */
	public Map<String, Object> getLoanRateType(String policyNo, Date calcDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("calcDate", calcDate);
		queryForObject("getLoanRateType", paraMap);
		return paraMap;
	}

	/**
	 * @Description: 成长红包在微信定投加保后通知微信端结果
	 * @methodName: queryAutoPolicyAddPremByWX
	 * @param policyNo
	 *            保单号
	 * @param orderID
	 *            订单号
	 * @return Map<String,Object>
	 * @param applyBarCode
	 *            投保单号
	 * @param posNo
	 *            保全号
	 * @param policy_no
	 *            保单号
	 * @param effectDate
	 *            保全生效日
	 * @param posStatus
	 *            保全状态
	 * @param preSum
	 *            加保保费
	 * @param calSum
	 *            加保保额
	 * @date 2015-9-23
	 * @throws
	 */
	public Map<String, Object> queryAutoPolicyAddPremByWX(String policyNo,
			String orderID) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		Map<String, Object> retMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("orderID", orderID);
		retMap = (Map<String, Object>) queryForObject(
				"queryAutoPolicyAddPremByWX", paraMap);
		String posNO = "";
		if (retMap != null) {
			posNO = (String) retMap.get("POSNO");
		} else {
			return retMap;
		}
		List<Map<String, Object>> lm = getPosPolChangeList(policyNo, posNO);
		Map<String, Object> map = new HashMap<String, Object>();
		String changeType;
		BigDecimal changeValue;
		// 加保保费
		BigDecimal preSum = new BigDecimal(0);
		// 加保保额
		BigDecimal calSum = new BigDecimal(0);
		if (lm != null && lm.size() > 0) {
			for (int i = 0; i < lm.size(); i++) {
				map = (Map<String, Object>) (lm.get(i));
				changeType = (String) map.get("CHANGETYPE");
				changeValue = (BigDecimal) map.get("CHANGEVALUE");
				if ("3".equals(changeType)) {
					preSum = changeValue;
				} else if ("1".equals(changeType)) {
					calSum = changeValue;
				}
			}
		}
		retMap.put("preSum", preSum);
		retMap.put("calSum", calSum);
		return retMap;
	}

	/**
	 * 保单号查询保全信息 (参数Map里面只能传policyNo 保单号)
	 */
	public List<PosPolicy> getPosPolicyList(Map map) {
		return queryForList("getPosPolicyList", map);
	}

	public int getIsEixstAcceptDetail(String posNo) {
		return (Integer) queryForObject("getIsEixstAcceptDetail", posNo);
	}

	/**
	 * @Description: 根据客户号和产品代码获取对应的保单号
	 * @methodName: getPolicyListByClientNoAndProductCode
	 * @param map
	 * @return
	 * @return List<String>
	 * @author WangMingShun
	 * @date 2015-11-17
	 * @throws
	 */
	public List<String> getPolicyListByClientNoAndProductCode(Map map) {
		return queryForList("getPolicyListByClientNoAndProductCode", map);
	}

	/**
	 * 功能说明: 根据部门代码查询该银行是否需要传保全申请条码
	 * 
	 * @param p_dept_no
	 *            部门代码 return Map<String,Object>
	 * @param p_is_need
	 * @param p_flag
	 * @param p_message
	 * @return
	 */
	public Map<String, Object> isNeedBankBarcode(String p_dept_no) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_dept_no", p_dept_no);
		queryForObject("isNeedBankBarcode", paraMap);
		return paraMap;
	}

	/**
	 * @Description: 获取用户所在的二级机构编码
	 * @methodName: getUserApproveBranch
	 * @param userId
	 * @return
	 * @return String
	 * @author WangMingShun
	 * @date 2015-12-18
	 * @throws
	 */
	public String getUserApproveBranch(String userId) {
		return (String) queryForObject("getUserApproveBranch", userId);
	}

	/**
	 * @Description: 获取指定用户所能受理的保全项目
	 * @methodName: getUserCanDoServiceItems
	 * @param userId
	 * @return
	 * @return List<String>
	 * @author WangMingShun
	 * @date 2015-12-18
	 * @throws
	 */
	public List<String> getUserCanDoServiceItems(String userId) {
		return queryForList("getUserCanDoServiceItems", userId);
	}

	public String getSeniorBranchInfo(String branchCode, String branchLevel,
			String codeOrName) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("branchCode", branchCode);
		paraMap.put("branchLevel", branchLevel);
		paraMap.put("codeOrName", codeOrName);
		return (String) queryForObject("getSeniorBranchInfo", paraMap);
	}

	/**
	 * 获取保单主险名称，保单生效日期，保单状态
	 * 
	 * @methodName: getPolicyProdutInfo
	 * @param policyNo
	 * @return
	 * @return Map
	 * @author WangMingShun
	 * @date 2016-1-27
	 * @throws
	 */
	public Map getPolicyProdutInfo(String policyNo) {
		return (Map) this.queryForObject("getPolicyProdutInfo", policyNo);
	}

	@SuppressWarnings("unchecked")
	public List<String> getClientNoByPhoneNo(String phoneNo, String clientNo) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("phoneNo", phoneNo);
		param.put("clientNo", clientNo);
		return (List<String>) queryForList("getClientNoByPhoneNo", param);
	}

	public String isFamilyPhoneNo(String clientNo, String otherClientNo) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("clientNo", clientNo);
		param.put("otherClientNo", otherClientNo);
		return (String) queryForObject("isFamilyPhoneNo", param);
	}

	public Date getAfterDatesOfTime(Date time, int afterDates) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("time", time);
		param.put("afterDates", afterDates);
		return (Date) queryForObject("getAfterDatesOfTime", param);
	}
	
	/**
	 * 根据保单号查询主险年利率，取最新时间的
	 * @return BigDecimal 没有的返回null
	 * @author WangMingShun
	 * @date 2016-2-24
	 */
	public BigDecimal getPrimaryPlanYearRateByPolicyNo(String policyNo) {
		return (BigDecimal) queryForObject("getPrimaryPlanYearRateByPolicyNo", policyNo);
	}
	
	/**
	 * @Description: 官网复效是否需要职业告知
	 * @param policyNo
	 * @return String
	 * @author TangJing
	 * @date 2016-5-3
	 */
	public String websiteAnswerEffect(String policyNo) {
		return (String) queryForObject("websiteAnswerEffect", policyNo);
	}
	
	/**
	 * @Description: 官网复效是否需要投保人告知
	 * @param policyNo
	 * @return String
	 * @author TangJing
	 * @date 2016-5-3
	 */
	public String websiteAnswerEffect2(String policyNo) {
		return (String) queryForObject("websiteAnswerEffect2", policyNo);
	}

	
	/**
	 * 根据保单号查询被保人信息列表
	 * @param policyNo
	 * @return List<InsuredClientDTO>
	 * @author zhangyi.wb
	 * @time 2016-5-23
	 */
	public List<InsuredClientDTO> getInsuredClientList(String policyNo){
		return queryForList("getInsuredInfoList", policyNo);
	}
	
	/**
	 * 查保单的被保人客户号(家庭单多被保险人,查询被保人客户号时按序号查)
	 * @param policyNo
	 * @param insuredSeq
	 * @return String
	 * @author zhangyi.wb
	 * @time 2016-5-23
	 */
	public String getInsuredOfPrimaryPlanByPolicyNoNew(String policyNo,String insuredSeq) {
		Map<String, Object> qMap = new HashMap<String,Object>();
		if(StringUtils.isBlank(insuredSeq)){
			insuredSeq = "1";
		}
		qMap.put("policyNo", policyNo);
		qMap.put("insuredSeq", insuredSeq);
		if("1".equals(insuredSeq)){
			qMap.put("isPrimaryPlan", "Y");
		}else{
			qMap.put("isPrimaryPlan", "N");
		}
		return (String) this.queryForObject(
				"getInsuredOfPrimaryPlanByPolicyNoNew", qMap);
	}
	
	/**
	 * 官网复效是否满足二核规则
	 * @param policyNo
	 * @return String
	 * @author TangJing
	 * @date 2016-5-30
	 */
	public String checkPolicyClientRisk(String policyNo){
		return (String) this.queryForObject("checkPolicyClientRisk", policyNo);
	}

	/**
	 * 判断保全受理人是否属于 山东、江西的保单
	 * @param policyNo
	 * @return 
	 */
	public String isBranchFlag(String posNo) {
		return (String) this.queryForObject("isBranchFlag", posNo);
	}

}
