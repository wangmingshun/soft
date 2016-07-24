package com.sinolife.pos.rpc.ilogjrules.posrules.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.sql.ARRAY;
import oracle.sql.STRUCT;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_16;
import com.sinolife.pos.common.dao.typehandler.ArrayTypeResultHandler;
import com.sinolife.pos.common.dao.typehandler.FinancialProductsArrayHandler;
import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.FinancialProductsDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.rpc.ilogjrules.posrules.dao.typehandler.PosToundwrtProductArrayTypeHandler;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMServiceItemDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.ServiceItemDomainDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMBeneficiaryDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMHistoryPlanDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMInsuredDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMOwnerDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPlanDto;

/**
 * 规则引擎的DAO,调用各条规则的db实现
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Repository("posRulesDAO")
public class PosRulesDAO extends AbstractPosDAO {

	private static final Object ClientName = null;
	private Object agentFlag;
	private Object applyTypeCode;
	private Object agentEndDate;
	private Object objectNo;

	/**
	 * 保单周年日
	 * 
	 * @param policyNo
	 * @param acceptDate
	 * @return
	 */
	public Date policyYearDate(String policyNo) {
		return (Date) queryForObject("QUERY_POLICY_YEAR_DATE", policyNo);
	}

	/**
	 * 受理人员可操作的保全项目列表
	 * 
	 * @param userId
	 * @return
	 */
	public String[] acceptorAllowedServiceItems(String userId) {
		List list = queryForList("QUERY_SELF_INPUT_PRIVS", userId);
		String[] items = null;
		if (list != null) {
			items = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				items[i] = (String) list.get(i);
			}
		}
		return items;
	}

	/**
	 * 在犹豫期内
	 * 
	 * @param policyNo
	 * @param applyDate
	 * @return
	 */
	public boolean isHesitatedProduct(String policyNo, String productCode,
			Date applyDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("productCode", productCode);
		queryForObject("isHesitated", paraMap);
		if (!"0".equals(paraMap.get("flag"))) {
			throw new RuntimeException("保单" + policyNo + "查询是否在犹豫期内出错："
					+ paraMap.get("message"));
		}
		String isScruple = (String) paraMap.get("isScruple");
		if ("Y".equals(isScruple)) {// 是否有犹豫期
			Date scrupleDate = (Date) paraMap.get("scrupleDate");
			if (scrupleDate == null) {
				// 开发阶段出现大量这样的数据，导致报空指针错，报个中文描述的错用户好明白些
				throw new RuntimeException("保单" + policyNo
						+ "无法找到签发日期数据，无法确定保单犹豫期，不能操作保全受理");
			}
			return applyDate.compareTo(scrupleDate) < 0;
		} else {
			return false;
		}

	}

	/**
	 * 只算保单的犹豫期
	 * 
	 * @param policyNo
	 * @param applyDate
	 * @return
	 */
	public boolean isHesitated(String policyNo, Date applyDate) {
		return isHesitatedProduct(policyNo, null, applyDate);
	}

	/**
	 * 是自保件
	 * 
	 * @param policyNo
	 * @return
	 */
	public boolean isSelfInsured(String policyNo) {
		String flag = (String) queryForObject("FUNC_AGENT_SELF_POLICY",
				policyNo);
		return Integer.parseInt(flag) > 0;
	}

	/**
	 * 有未清偿自垫
	 * 
	 * @param policyNo
	 * @return
	 */
	public boolean hasNonRefundAPL(String policyNo) {
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		queryForObject("PROC_IS_POLICY_HAS_VALID_APL", pMap);

		return "Y".equals(pMap.get("hasValidApl"));
	}

	/**
	 * 有未清偿贷款
	 * 
	 * @param policyNo
	 * @return
	 */
	public boolean hasNonRefundLoan(String policyNo) {
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		queryForObject("PROC_IS_POLICY_HAS_VALID_LOAN", pMap);

		return "Y".equals(pMap.get("hasValidLoan"));
	}

	/**
	 * 保单未清偿贷款中存在一个是PAS
	 */

	public boolean hasNonRefundLoanPas(String policyNo) {

		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		queryForObject("PROC_IS_POLICY_HAS_VALID_LOAN_PAS", pMap);

		return "Y".equals(pMap.get("hasValidLoanPas"));

	}

	/**
	 * 处于保费豁免状态
	 * 
	 * @param policyNo
	 * @return
	 */
	public boolean isPolicyFeeFree(String policyNo) {
		String flag = (String) queryForObject("SELECT_IS_POLICY_FEE_FREE",
				policyNo);
		return "Y".equals(flag);
	}

	/**
	 * 已提出个人账户价值分期领取的申请
	 * 
	 * @param policyNo
	 * @return
	 */
	public boolean hasApplyTermWithdraw(String policyNo, Date queryDate) {
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("queryDate", queryDate);

		String flag = (String) queryForObject("IS_APPLY_STAGING_PAYMENT", pMap);
		return "Y".equals(flag);
	}

	/**
	 * 投资账户存在未成交的有效交易记录
	 * 
	 * @param policyNo
	 * @return
	 */
	public boolean hasFundInTrade(String policyNo, Date queryDate) {
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("queryDate", queryDate);

		String flag = (String) queryForObject("IS_EXIST_UNFINISHED_TRADING",
				pMap);
		return "Y".equals(flag);
	}

	/**
	 * 系统回执回销日
	 * 
	 * @param policyNo
	 * @return
	 */
	public Date sysPolicySignBackDate(String policyNo) {
		return (Date) queryForObject("SELECT_POLICY_CONFIRM_DATE", policyNo);
	}

	/**
	 * 投保人回执签署日
	 * 
	 * @param policyNo
	 * @return
	 */
	public Date applicantPolicySignBackDate(String policyNo) {
		return (Date) queryForObject("SELECT_POLICY_SIGN_DATE", policyNo);
	}

	/**
	 * 保单介质
	 * 
	 * @param policyNo
	 * @return
	 */
	public String policyMediaType(String policyNo) {
		return (String) queryForObject("SELECT_POLICY_MEDIA_TYPE", policyNo);
	}

	/**
	 * 保单回执状态
	 * 
	 * @param policyNo
	 * @return
	 */
	public String policySignFlag(String policyNo) {
		return (String) queryForObject("SELECT_POLICY_SIGN_FLAG", policyNo);
	}

	/**
	 * 保单打印次数
	 * 
	 * @param policyNo
	 * @return
	 */
	public Integer policyPrivideTime(String policyNo) {
		return (Integer) queryForObject("SELECT_POLICY_PROVIDE_TIME", policyNo);
	}

	/**
	 * 非转账失败件
	 * 
	 * @param policyNo
	 * @return
	 */
	public boolean isNotTransferFailure(String policyNo) {
		String flag = (String) queryForObject("SELECT_NOT_TRANSFER_FAILURE",
				policyNo);
		return "Y".equals(flag);
	}

	/**
	 * 服务人员电话
	 * 
	 * @param policyNo
	 * @return
	 */
	public String servicerPhone(String policyNo) {
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);

		queryForObject("PROC_RN_GET_SERVICE_INFO", pMap);

		return "" + pMap.get("phone");
	}

	/**
	 * 生存金转账银行账户户主姓名
	 * 
	 * @param policyNo
	 * @param beneficiaryNo
	 * @return
	 */
	public String paymentAccountant(String policyNo, String beneficiaryNo) {
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("beneficiaryNo", beneficiaryNo);

		return (String) queryForObject("SELECT_BENEFICIARY_ACCOUNT_OWNER", pMap);
	}

	/**
	 * 最大可贷金额
	 * 
	 * @param policyNo
	 * @param productCode
	 * @param prodSeq
	 * @param calcDate
	 * @return
	 */
	public double loanableMoney(String policyNo, String productCode,
			String prodSeq, Date calcDate) {
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("productCode", productCode);
		pMap.put("prodSeq", prodSeq);
		pMap.put("calcDate", calcDate);

		queryForObject("CALC_LOANABLE_RELATED_AMT", pMap);

		return Double.parseDouble("" + pMap.get("loanMax"));
	}

	/**
	 * 
	 * @param policyNo
	 * @return Map&lt;String, Object&gt; <br/>
	 *         hasRelatedPolicy :是否有关联的保单 Y/N <br/>
	 *         relatedPolicyNo: 关联的保单号，用","分隔 <br/>
	 *         relatedShortTermCanceled:关联短期险保单是否做过犹豫期退保 Y/N<br/>
	 *         flag ：返回标志['0':成功，'1':失败]<br/>
	 *         message ：返回信息
	 */
	public Map<String, Object> getRelatePolicyInfo(String policyNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		queryForObject("PROC_GET_RELATE_POLICY_INFO", paraMap);
		if (!"0".equals(paraMap.get("flag"))) {
			throw new RuntimeException("根据保单号查询关联保单相关信息失败："
					+ paraMap.get("message"));
		}
		return paraMap;
	}

	/**
	 * 历史保全项目列表
	 * 
	 * @param policyNo
	 * @param acceptDate
	 */
	public POSBOMServiceItemDto[] historyServiceItemsList(String policyNo,
			Date acceptDate) {
		POSBOMServiceItemDto[] items = null;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("acceptDate", acceptDate);
		List list = queryForList("historyServiceItemsList", paraMap);
		if (list != null) {
			items = new POSBOMServiceItemDto[list.size()];
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);
				items[i] = POSBOMServiceItemDto.create();
				items[i].setServiceItemCode(new ServiceItemDomainDto(""
						+ map.get("SERVICE_ITEMS")));
				items[i].setAcceptDate((Date) map.get("ACCEPT_DATE"));
				items[i].setPremSum(map.get("PREM_SUM") == null ? 0 : Double
						.parseDouble("" + map.get("PREM_SUM")));
				items[i].setPremFlag(map.get("PREM_FLAG") == null ? null : ""
						+ map.get("PREM_FLAG"));
				items[i].setPremSuccessFlag(map.get("PREM_SUCCESS_FLAG") == null ? null
						: "" + map.get("PREM_SUCCESS_FLAG"));
				items[i].setAcceptor("" + map.get("ACCEPTOR"));
				items[i].setPaymentType("" + map.get("PAYMENT_TYPE"));
				items[i].setAcceptStatus("" + map.get("ACCEPT_STATUS_CODE"));
				items[i].setEffectedDate((Date) map.get("EFFECTIVE_DATE"));
			}
		}
		return items;
	}

	/**
	 * 生存调查未结束
	 * 
	 * @param policyNo
	 * @return
	 */
	public boolean isInLifeInvestigation(String policyNo) {
		String flag = (String) queryForObject(
				"SELECT_IS_IN_LIFE_INVESTIGATION", policyNo);
		return "Y".equals(flag);
	}

	/**
	 * 是否分红险种
	 * 
	 * @param productCode
	 * @return
	 */
	public boolean isDivPlan(String productCode) {
		String flag = (String) queryForObject("FUNC_IS_DIV_PLAN", productCode);
		return "Y".equals(flag);
	}

	/**
	 * 是否现金分红险种
	 * 
	 * @param productCode
	 * @return
	 */
	public boolean isBonusDivPlan(String productCode) {
		String flag = (String) queryForObject("FUNC_IS_BONUS_DIV_PLAN",
				productCode);
		return "Y".equals(flag);
	}

	/**
	 * 是养老金险种
	 * 
	 * @param productCode
	 * @return
	 */
	public boolean isEndowmentPlan(String productCode) {
		String flag = (String) queryForObject("FUNC_IS_ENDOWMENT_PRODUCT",
				productCode);
		return "Y".equals(flag);
	}

	/**
	 * 是投连险
	 * 
	 * @param productCode
	 * @return
	 */
	public boolean isULink(String productCode) {
		String flag = (String) queryForObject("FUNC_IS_ULINK_PRODUCT",
				productCode);
		return "Y".equals(flag);
	}

	/**
	 * 是万能险
	 * 
	 * @param productCode
	 * @return
	 */
	public boolean isUniversal(String productCode) {
		String flag = (String) queryForObject("FUNC_IS_ULIFE_PRODUCT",
				productCode);
		return "Y".equals(flag);
	}

	/**
	 * 是长期险
	 * 
	 * @param productCode
	 * @return
	 */
	public boolean isLongTerm(String productCode) {
		String flag = (String) queryForObject("FUNC_IS_LONG_PRODUCT",
				productCode);
		return "Y".equals(flag);
	}

	/**
	 * 是短期险
	 * 
	 * @param productCode
	 * @return
	 */
	public boolean isShortTerm(String productCode) {
		String flag = (String) queryForObject("FUNC_IS_LONG_PRODUCT",
				productCode);
		return "N".equals(flag);
	}

	/**
	 * 是可贷险种
	 * 
	 * @param productCode
	 * @return
	 */
	public boolean isLoanablePlan(String productCode) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_product_code", productCode);
		queryForObject("isLoanablePlan", paraMap);
		if (!"0".equals(paraMap.get("p_flag"))) {
			throw new RuntimeException("查询是否可贷险种出错：" + paraMap.get("p_message"));
		}
		return "Y".equals(paraMap.get("p_is_loanable"));
	}

	/**
	 * 查询某保单是否缴纳每期保费
	 * 
	 * @param policyNo
	 * @param payToDate
	 * @return
	 */
	public boolean hasPaidDuePolicyFee(String policyNo, Date queryDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_query_date", queryDate);
		return "Y".equals(queryForObject("hasPaidDuePolicyFee", paraMap));
	}

	/**
	 * 红利选择方式
	 * 
	 * @param policyNo
	 * @return
	 */
	public String bonusOption(String policyNo) {
		return (String) queryForObject("SELECT_DIVIDEND_SELECTION", policyNo);
	}

	/**
	 * 个人账户价值
	 * 
	 * @param policyNo
	 * @param prodSeq
	 * @param productCode
	 * @param requestDate
	 * @param contractStatus
	 * @return
	 */
	public double personalAccountValue(String policyNo, String prodSeq,
			String productCode, Date requestDate, String contractStatus) {
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("prodSeq", prodSeq);
		pMap.put("productCode", productCode);
		pMap.put("requestDate", requestDate);
		pMap.put("contractStatus", contractStatus);

		queryForObject("QUERY_FINANCIAL_PRODUCTS_VALUE", pMap);

		return Double.parseDouble(pMap.get("totalAmount") == null ? "0" : ""
				+ pMap.get("totalAmount"));
	}

	/**
	 * 个人账户价值净值
	 * 
	 * @param policyNo
	 * @param prodSeq
	 * @param productCode
	 * @param requestDate
	 * @param contractStatus
	 * @return
	 */
	public double personalAccountNetValue(String policyNo, String prodSeq,
			String productCode, Date requestDate, String contractStatus) {
		// 总价值
		double value = personalAccountValue(policyNo, prodSeq, productCode,
				requestDate, contractStatus);

		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("prodSeq", prodSeq);
		pMap.put("productCode", productCode);
		pMap.put("endDate", requestDate);
		pMap.put("contractStatus", contractStatus);
		// 贷款
		queryForObject("CALC_POLICY_LOAN_INTEREST", pMap);

		return value
				- Double.parseDouble(pMap.get("loanAllSum") == null ? "0" : ""
						+ pMap.get("loanAllSum"));
	}

	/**
	 * 年金领取开始领取日
	 * 
	 * @param policyNo
	 * @param prodSeq
	 * @return
	 */
	public Date annualPaymentStartDate(String policyNo, Integer prodSeq) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("prodSeq", prodSeq);
		return (Date) queryForObject("annualPaymentStartDate", paraMap);
	}

	/**
	 * 已生效的年数
	 * 
	 * @param policyNo
	 *            保单号
	 * @param prodSeq
	 *            产品序号
	 * @param applyDate
	 *            申请日期
	 * @return
	 */
	public Integer effectedYears(String policyNo, Integer prodSeq,
			Date applyDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("prodSeq", prodSeq);
		paraMap.put("applyDate", applyDate);
		return (Integer) queryForObject("SELECT_EFFECTED_YEARS", paraMap);
	}

	/**
	 * 最小追加保费限额
	 * 
	 * @param policyNo
	 * @param prodSeq
	 * @param reqDate
	 * @return
	 */
	public double minAddupPolicyFee(String policyNo, String prodSeq,
			Date reqDate) {
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("prodSeq", prodSeq);
		pMap.put("reqDate", reqDate);

		queryForObject("GET_POL_ADDTO_MIN_AMOUNT", pMap);

		return Double.parseDouble(pMap.get("addtoMinAmount") == null ? "0"
				: (String) pMap.get("addtoMinAmount"));
	}

	/**
	 * 账户最低余额
	 * 
	 * @param productCode
	 * @return
	 */
	public double minAccountBalance(String productCode) {
		Map pMap = new HashMap();
		pMap.put("productCode", productCode);
		pMap.put("productParameter", "113");

		queryForObject("GET_PRODUCT_MIN_BALANCE", pMap);

		return Double.parseDouble(pMap.get("parameterValue") == null ? "0"
				: (String) pMap.get("parameterValue"));
	}

	/**
	 * 投连账户之间存在冲突
	 * 
	 * @param policyNo
	 * @param prodSeq
	 * @param productCode
	 * @param ulinkProdSeq
	 * @return
	 */
	public boolean isAccountNoConflict(String policyNo, String prodSeq,
			String productCode, String ulinkProdSeq) {
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("prodSeq", prodSeq);
		pMap.put("productCode", productCode);

		// 查到产品列表
		List finList = getFinancialProductsList(policyNo, ulinkProdSeq);
		if (finList != null && finList.size() > 0) {
			pMap.put("p_financial_products_list", finList);
			// 查询是否冲突
			queryForObject("PROC_IS_PRODUCTS_CLASH", pMap);

			return "Y".equals(pMap.get("clashFlag"));

		} else {// 理财产品都不是，就直接不冲突
			return false;
		}
	}

	/**
	 * 查询理财产品信息列表
	 * 
	 * @param policyNo
	 * @param prodSeq
	 * @return
	 */
	public List<FinancialProductsDTO> getFinancialProductsList(String policyNo,
			String prodSeq) {
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("prodSeq", prodSeq);

		queryForObject("GET_FINANCIAL_PRODUCTS_LIST", pMap);
		new FinancialProductsArrayHandler().handleResult(pMap);

		return (List<FinancialProductsDTO>) pMap
				.get("p_financial_products_list");
	}

	/**
	 * 查询借款
	 * 
	 * @param policyNo
	 * @return
	 */
	public double policyLoanMinMoney(String policyNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		queryForObject("policyLoanMinMoney", paraMap);
		if (!"0".equals(paraMap.get("flag"))) {
			throw new RuntimeException("查询保单最小借款金额失败：" + paraMap.get("message"));
		}
		return new Double(paraMap.get("minLoanMoney") == null ? "0"
				: (String) paraMap.get("minLoanMoney"));
	}

	/**
	 * 查询保全的其他信息，如受理渠道等
	 * 
	 * @param posNo
	 * @return
	 */
	public Map<String, Object> queryPosExtraInfoByPosNo(String posNo) {
		return (Map<String, Object>) queryForObject("queryPosExtraInfoByPosNo",
				posNo);
	}

	/**
	 * 查询保单失效日期与失效时间
	 * 
	 * @param policyNo
	 * @return
	 */
	public Map<String, Object> queryLapseDateAndReasonByPolicyNo(String policyNo) {
		return (Map<String, Object>) queryForObject(
				"queryLapseDateAndReasonByPolicyNo", policyNo);
	}

	/**
	 * 是否选择自垫
	 * 
	 * @param policyNo
	 * @return
	 */
	public boolean isAutoPolicyLoan(String policyNo) {
		return "Y".equals(queryForObject("isAutoPolicyLoan", policyNo));
	}

	/**
	 * 是否在宽限期
	 * 
	 * @param policyNo
	 * @param calcDate
	 * @return
	 */
	public boolean isExtendedTerm(String policyNo, Date calcDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("calcDate", calcDate);
		return "Y".equals(queryForObject("isExtendedTerm", paraMap));
	}

	/**
	 * 检查保单是否遭受暂停
	 * 
	 * @param policyNo
	 * @param serviceItems
	 * @param posNo
	 * @return
	 */
	public Map<String, Object> checkPolicySuspend(String policyNo,
			String serviceItems, String posNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_business_no_type", "5");
		paraMap.put("p_business_no", posNo);
		paraMap.put("p_service_suspend_item", serviceItems);
		queryForObject("checkPolicySuspend", paraMap);
		new ArrayTypeResultHandler() {
			@Override
			public Map handleResult(Map map) {
				ARRAY array = (ARRAY) map.get("p_suspend_item_arr");
				List<CodeTableItemDTO> result = new ArrayList<CodeTableItemDTO>();
				CodeTableItemDTO ct = null;// 根据返回数据的基本类型确定Dto中的数据类型
				try {
					if (array != null) {
						Object[] obj = (Object[]) array.getArray();
						STRUCT struct = null;
						for (int i = 0; i < obj.length; i++) {
							struct = (STRUCT) obj[i];
							Object[] tmp = (Object[]) struct.getAttributes();
							ct = new CodeTableItemDTO();
							ct.setCode((String) tmp[0]);
							ct.setDescription((String) tmp[1]);
							result.add(ct);
						}
					}
				} catch (SQLException e) {
					String errMsg = "ArrayTypeResultHandler error:"
							+ e.getMessage();
					logger.error(errMsg, e);
					throw new RuntimeException(errMsg);
				}
				map.put("p_suspend_item_arr", result);
				return map;

			}
		}.handleResult(paraMap);
		return paraMap;
	}

	/**
	 * 查询险种应领满期金
	 * 
	 * @param policyNo
	 * @param prodSeq
	 * @param calcDate
	 * @return
	 */
	public double getMaturityPay(String policyNo, Integer prodSeq, Date calcDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_prod_seq", prodSeq);
		paraMap.put("p_calc_date", calcDate);
		return (Double) queryForObject("getMaturityPay", paraMap);
	}

	/**
	 * 业务单是否已经资金确认
	 * 
	 * @param businessNo
	 * @param businessNoType
	 * @return
	 */
	public boolean financeNoConfirmPrem(String businessNo, String businessNoType) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_business_no", businessNo);
		paraMap.put("p_business_no_type", businessNoType);
		queryForObject("financeNoConfirmPrem", paraMap);
		if ("1".equals(paraMap.get("p_flag"))) {
			return "N".equals(paraMap.get("p_capital_confirm_flag"));
		} else {
			throw new RuntimeException("调用接口查询业务单是否已经资金确认出错：flag:"
					+ paraMap.get("p_flag") + ", msg:" + paraMap.get("p_mes"));
		}
	}

	/**
	 * 业务单是否已经资金确认
	 * 
	 * @param p_policy_no
	 * @param p_apply_bar_code
	 * @return
	 */
	public boolean finCapitalConfirm(String p_policy_no, String p_apply_bar_code) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", p_policy_no);
		queryForObject("finCapitalConfirm", paraMap);
		if ("1".equals(paraMap.get("p_flag"))) {
			return "Y".equals(paraMap.get("p_capital_confirm_flag"));
		} else {
			throw new RuntimeException("调用接口查询业务单是否已经资金确认出错：flag:"
					+ paraMap.get("p_flag") + ", msg:" + paraMap.get("p_mes"));
		}
	}

	/**
	 * 是否原账号
	 * 
	 * @param posNo
	 * @return
	 */
	public boolean currentOriginalAccountSame(String posNo) {
		return "Y".equals(queryForObject("currentOriginalAccountSame", posNo));
	}

	/**
	 * 根据投保单系统编号(apply_no)获取投保单号（apply_bar_code）
	 * 
	 * @param applyNo
	 * @return
	 */
	public String getApplyBarcode(String applyNo) {
		return (String) queryForObject("getApplyBarcode", applyNo);
	}

	/**
	 * 获取险种的缴费年期/保险年期/计算日期的剩余缴费年期/剩余保险年期
	 * 
	 * @param posNo
	 *            保全号
	 * @param productCode
	 *            产品号
	 * @param policyNo
	 *            保单号
	 * @param prodSeq
	 *            险种序号
	 * @param calcDate
	 *            计算日期
	 * @return Map&lt;String, Object&gt;<br/>
	 *         p_prem_term : 险种缴费年期<br/>
	 *         p_left_prem_term : 险种剩余缴费年期<br/>
	 *         p_coverage_period : 险种保险年期<br/>
	 *         p_left_coverage_period ：险种剩余保险年期<br/>
	 *         p_paidenddate : 险种缴费期满日<br/>
	 *         p_flag :返回标志 0--成功 1--失败<br/>
	 *         p_message :返回信息
	 */
	public Map<String, Object> getPolProdPremCoverage(String posNo,
			String productCode, String policyNo, Integer prodSeq, Date calcDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_pos_no", posNo);
		paraMap.put("p_product_code", productCode);
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_prod_seq", prodSeq);
		paraMap.put("p_calc_date", calcDate);
		queryForObject("getPolProdPremCoverage", paraMap);
		if (!"0".equals(paraMap.get("p_flag"))) {
			throw new RuntimeException("获取险种的缴费年期/保险年期/计算日期的剩余缴费年期/剩余保险年期失败："
					+ paraMap.get("p_message"));
		}
		return paraMap;
	}

	/**
	 * 查询本次申请的保全项目
	 * 
	 * @param posNo
	 * @return
	 */
	public ServiceItemDomainDto[] currentServiceItemsList(String posNo) {
		List<ServiceItemDomainDto> ret = queryForList(
				"currentServiceItemsList", posNo);
		if (ret == null)
			return null;
		return ret.toArray(new ServiceItemDomainDto[ret.size()]);
	}

	/**
	 * 查询保全需要送核的险种列表
	 * 
	 * @param posNo
	 * @return Map&lt;String, Map&lt;String, Objec&gt;&gt; clientInfoApp 投保人送核信息<br/>
	 *         clientInfoIns 被保人送核信息<br/>
	 *         applicantNo 投保人客户号<br/>
	 *         insuredNo 被保人客户号<br/>
	 */
	public Map<String, Object> getToundwrtProductDatail(String posNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_pos_no", posNo);
		queryForObject("getToundwrtProductDatail", paraMap);

		ArrayTypeResultHandler appHandler = new PosToundwrtProductArrayTypeHandler(
				"p_app_undwrt_product_list");
		ArrayTypeResultHandler insHandler = new PosToundwrtProductArrayTypeHandler(
				"p_ins_undwrt_product_list");

		appHandler.handleResult(paraMap);
		insHandler.handleResult(paraMap);

		Map<String, Object> retMap = new HashMap<String, Object>();

		Map<String, Object> clientInfoIns = new HashMap<String, Object>();
		clientInfoIns.put("CLIENTNO", paraMap.get("p_ins_clientno"));
		clientInfoIns
				.put("UNWRTINFO", paraMap.get("p_ins_undwrt_product_list"));
		retMap.put("clientInfoIns", clientInfoIns);

		List<Map<String, Object>> undwrtInfo = (List<Map<String, Object>>) paraMap
				.get("p_app_undwrt_product_list");
		if (undwrtInfo != null && !undwrtInfo.isEmpty()) {
			Map<String, Object> clientInfoApp = new HashMap<String, Object>();
			clientInfoApp.put("CLIENTNO", paraMap.get("p_app_clientno"));
			clientInfoApp.put("UNWRTINFO", undwrtInfo);
			retMap.put("clientInfoApp", clientInfoApp);
		}
		retMap.put("applicantNo", paraMap.get("p_app_clientno"));
		retMap.put("insuredNo", paraMap.get("p_ins_clientno"));
		return retMap;
	}

	/**
	 * 根据职业代码查询职业等级
	 * 
	 * @param occupationCode
	 * @return
	 */
	public String getOccupationGradeByCode(String occupationCode) {
		return (String) queryForObject("getOccupationGradeByCode",
				occupationCode);
	}

	/**
	 * 查询保单的投保人与主险被保人的关系
	 * 
	 * @param policyNo
	 * @return
	 */
	public String getRelationWithInsured(String policyNo) {
		return (String) queryForObject("getRelationWithInsured", policyNo);
	}

	/**
	 * 查询送核的险种信息
	 * 
	 * @param posNo
	 * @return
	 */
	public List<PolicyProductDTO> queryPosPolicyProductTmpListByPosNo(
			String posNo) {
		return queryForList("queryPosPolicyProductTmpListByPosNo", posNo);
	}

	/**
	 * 查询客户是否为黑名单客户
	 * 
	 * @param clientNo
	 * @param applyDate
	 *            查询时间
	 * @return
	 */
	public boolean isBlackList(String clientNo, Date applyDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_client_no", clientNo);
		paraMap.put("p_biz_date", applyDate);
		queryForObject("isBlackList", paraMap);
		if (!"Y".equals(paraMap.get("p_sign"))) {
			logger.info("isBlackList return:" + paraMap);
			throw new RuntimeException("查询是否为黑名单客户失败："
					+ paraMap.get("p_message"));
		}
		return "Y".equals(paraMap.get("p_black_flag"));
	}

	/**
	 * 判断一个保单是否可以操作保险金转换年金项目
	 * 
	 * @param policyNo
	 * @return Map&lt;String, Object&gt;<br/>
	 *         输入参数：<br/>
	 *         p_policy_no :保单号<br/>
	 *         输出参数：<br/>
	 *         p_is_convertableplan :可以操作保险金转换年金<br/>
	 *         p_unconvertable_message :不能操作保险金转换年金的原因<br/>
	 *         p_flag :返回标志0成功，非0失败<br/>
	 *         p_message :返回信息
	 */
	public Map<String, Object> isConvertable(String policyNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		queryForObject("isConvertable", paraMap);
		if (!"0".equals(paraMap.get("p_flag"))) {
			throw new RuntimeException("查询保单是否可以操作保险金转换年金项目失败："
					+ paraMap.get("p_message"));
		}
		return paraMap;
	}

	/**
	 * 查询是否为豁免险种
	 * 
	 * @param productCode
	 * @return
	 */
	public boolean isExemptPlan(String productCode) {
		return "Y".equals(queryForObject("isExemptPlan", productCode));
	}

	/**
	 * 是否有异地受理限制
	 * 
	 * @param policyNo
	 * @param acceptor
	 * @return
	 */
	public boolean isOffSideLimit(String policyNo, String acceptor) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_acceptor", acceptor);
		// 是否为异地受理
		boolean isOffSideAccept = "Y".equals(queryForObject("isOffSideAccept",
				paraMap));
		if (isOffSideAccept) {
			// 受理人是否有异地权限
			boolean hasOffSidePriv = "Y".equals(queryForObject(
					"hasOffSidePriv", acceptor));
			if (!hasOffSidePriv) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 历史所投险种列表
	 * 
	 * @param policyNo
	 * @param clientNo
	 * @return
	 */
	public SUWBOMHistoryPlanDto[] historyPlans(String policyNo, String clientNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("clientNo", clientNo);
		List<Map<String, Object>> resultList = queryForList("historyPlans",
				paraMap);
		if (resultList != null && !resultList.isEmpty()) {
			SUWBOMHistoryPlanDto[] resultArr = new SUWBOMHistoryPlanDto[resultList
					.size()];
			for (int i = 0; i < resultList.size(); i++) {
				Map<String, Object> planMap = resultList.get(i);
				BigDecimal firstPremium = (BigDecimal) planMap
						.get("FIRST_PREMIUM");
				BigDecimal baseSumIns = (BigDecimal) planMap
						.get("BASE_SUM_INS");
				String productCode = (String) planMap.get("PRODUCT_CODE");
				SUWBOMHistoryPlanDto planDTO = SUWBOMHistoryPlanDto.create();
				// 险种代码
				planDTO.setPlanCode(productCode);
				// 首期保费
				if (firstPremium != null) {
					planDTO.setFirstPremium(firstPremium.doubleValue());
				}
				// 基本保额
				if (baseSumIns != null) {
					planDTO.setSA(baseSumIns.doubleValue());
				}
				resultArr[i] = planDTO;
			}
			return resultArr;
		}
		return null;
	}

	/**
	 * 领取养老金的年龄
	 * 
	 * @param policyNo
	 * @param prodSeq
	 * @return
	 */
	public Integer drawEndowmentAge(String policyNo, Integer prodSeq) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("prodSeq", prodSeq);
		return (Integer) queryForObject("drawEndowmentAge", paraMap);
	}

	/**
	 * 养老金给付方式
	 * 
	 * @param policyNo
	 * @param prodSeq
	 * @return
	 */
	public Map<String, Object> endowmentPayTypeAndFrequency(String policyNo,
			Integer prodSeq) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("prodSeq", prodSeq);
		return (Map<String, Object>) queryForObject(
				"endowmentPayTypeAndFrequency", paraMap);
	}

	/**
	 * 获取保单险种的最新的核保决定
	 * 
	 * @param policyNo
	 * @param prodSeq
	 * @return
	 */
	public Map<String, Object> uwGetLastDecisionDetail(String policyNo,
			Integer prodSeq) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_prod_seq", prodSeq);
		queryForObject("uwGetLastDecisionDetail", paraMap);
		return paraMap;
	}

	/**
	 * 获取投保规则检查bom所需信息
	 * 
	 * @param posNo
	 * @param policyNo
	 * @return
	 */
	public Map<String, Object> getSuwbomElement(String posNo, String policyNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_pos_no", posNo);
		queryForObject("getSuwbomElement", paraMap);
		STRUCT appStruct = (STRUCT) paraMap.get("p_app_client_rec");
		ARRAY insArray = (ARRAY) paraMap.get("p_ins_client_list");
		ARRAY benefArray = (ARRAY) paraMap.get("p_beneficiary_list");
		ARRAY productArray = (ARRAY) paraMap.get("p_policy_product_list");
		BigDecimal number = null;
		String str = null;
		try {
			if (appStruct != null) {
				Object[] attributes = appStruct.getAttributes();
				SUWBOMOwnerDto owner = SUWBOMOwnerDto.create();
				if (attributes != null && attributes.length > 0) {
					owner.setOwnerID((String) attributes[0]); // 投保人ID
					owner.setOwnerName((String) attributes[1]); // 投保人姓名
					owner.setIdentityType((String) attributes[2]); // 证件类型
					owner.setIdentityCode((String) attributes[3]); // 证件号
					owner.setBirthday((Date) attributes[4]); // 出生日期
					number = (BigDecimal) attributes[5];
					if (number != null) {
						owner.setIssueAge(number.intValue()); // 投保年龄
					}
					owner.setNationality((String) attributes[6]); // 国籍
					owner.setMarriageStatus((String) attributes[7]); // 婚姻状况
					owner.setGender((String) attributes[8]); // 性别
					owner.setOccuCode((String) attributes[9]); // 职业代码
					owner.setOccuContent((String) attributes[10]); // 职务内容
					owner.setRelationWithInsured((String) attributes[11]); // 与主被保险人关系
				}
				paraMap.put("p_app_client_rec", owner);
			}
			if (insArray != null) {
				Object[] insStructArray = (Object[]) insArray.getArray();
				List<SUWBOMInsuredDto> insList = new ArrayList<SUWBOMInsuredDto>();
				if (insStructArray != null && insStructArray.length > 0) {
					for (int i = 0; i < insStructArray.length; i++) {
						STRUCT insStruct = (STRUCT) insStructArray[i];
						if (insStruct != null) {
							Object[] attributes = insStruct.getAttributes();
							SUWBOMInsuredDto insured = SUWBOMInsuredDto
									.create();
							if (attributes != null && attributes.length > 0) {
								String insuredNo = (String) attributes[0];
								insured.setInsuredID(insuredNo); // 被保险人ID
								insured.setInsuredName((String) attributes[1]); // 被保险人姓名
								insured.setIdentityType((String) attributes[2]); // 证件类型
								insured.setIdentityCode((String) attributes[3]); // 证件号
								insured.setBirthday((Date) attributes[4]); // 出生日期	
								number = (BigDecimal) attributes[5];
								if (number != null) {
									insured.setIssueAgeYear(number.intValue()); // 投保年龄(年)
								}
								number = (BigDecimal) attributes[6];
								if (number != null) {
									insured.setIssueAgeDay(number.intValue()); // 投保年龄(天)
								}
								insured.setNationality((String) attributes[7]); // 国籍
								insured.setGender((String) attributes[8]); // 性别
								insured.setOccuCode((String) attributes[9]);// 职业代码
								insured.setOccuContent((String) attributes[10]); // 职务内容
								insured.setOccuGrade((String) attributes[11]);// 职业等级
								String insuredSeq = (String) attributes[12];
								if(StringUtils.isNotBlank(insuredSeq)){
									Integer iSeq = Integer.valueOf(insuredSeq);
									insured.setInsuredSeq(iSeq);//被保人序号
								}else{
									insured.setInsuredSeq(1);//被保人序号
								}
								insured.setHistoryPlans(this.historyPlans(
										policyNo, insuredNo)); // 历史所投险种列表
							}
							insList.add(insured);
						}
					}
				}
				paraMap.put("p_ins_client_list",
						insList.toArray(new SUWBOMInsuredDto[insList.size()]));
			}
			if (benefArray != null) {
				Object[] benefStructArray = (Object[]) benefArray.getArray();
				List<SUWBOMBeneficiaryDto> benefList = new ArrayList<SUWBOMBeneficiaryDto>();
				if (benefStructArray != null && benefStructArray.length > 0) {
					for (int i = 0; i < benefStructArray.length; i++) {
						STRUCT benefStruct = (STRUCT) benefStructArray[i];
						if (benefStruct != null) {
							Object[] attributes = benefStruct.getAttributes();
							SUWBOMBeneficiaryDto benef = SUWBOMBeneficiaryDto
									.create();
							if (attributes != null && attributes.length > 0) {
								benef.setBeneficiaryName((String) attributes[0]); // 受益人姓名
								benef.setBirthday((Date) attributes[1]); // 出生日期
								number = (BigDecimal) attributes[2];
								if (number != null) {
									benef.setAge(number.intValue()); // 年龄
								}
								benef.setGender((String) attributes[3]); // 性别
								benef.setIdentityType((String) attributes[4]); // 证件类型
								benef.setIdentityCode((String) attributes[5]); // 证件号
								benef.setRelationWithInsured((String) attributes[6]); // 与被保险人关系
								benef.setBeneficialType((String) attributes[7]); // 受益类别
								str = (String) attributes[8];
								if (StringUtils.isNotBlank(str)) {
									benef.setBeneficialPercent(Double
											.parseDouble(str)); // 受益份额
								}
								String insuredSeq = (String) attributes[9];
								if(StringUtils.isNotBlank(insuredSeq)){
									Integer iSeq = Integer.valueOf(insuredSeq);
									benef.setInsuredSeq(iSeq);//被保人序号
								}else{
									benef.setInsuredSeq(1);//被保人序号
								}
							}
							benefList.add(benef);
						}
					}
				}
				paraMap.put("p_beneficiary_list", benefList
						.toArray(new SUWBOMBeneficiaryDto[benefList.size()]));
			}
			if (productArray != null) {
				Object[] productStructArray = (Object[]) productArray
						.getArray();
				List<SUWBOMPlanDto> productList = new ArrayList<SUWBOMPlanDto>();
				if (productStructArray != null && productStructArray.length > 0) {
					for (int i = 0; i < productStructArray.length; i++) {
						STRUCT productStruct = (STRUCT) productStructArray[i];
						if (productStruct != null) {
							Object[] attributes = productStruct.getAttributes();
							SUWBOMPlanDto product = SUWBOMPlanDto.create();
							if (attributes != null && attributes.length > 0) {
								product.setPlanCode((String) attributes[0]); // 险种代码
								product.setPlanName((String) attributes[1]); // 险种名称
								number = (BigDecimal) attributes[2];
								if (number != null) {
									product.setSumAssured(number.doubleValue()); // 险种基本保额
								}
								number = (BigDecimal) attributes[3];
								if (number != null) {
									product.setBasicPremium(number
											.doubleValue()); // 险种基本保费
								}
								product.setCalculateWay((String) attributes[4]); // 险种保费计算方式
								product.setOccuGrade((String) attributes[5]); // 被保人职业等级
								product.setCoveragePeriodType((String) attributes[6]); // 险种保障期类型
								str = (String) attributes[7];
								if (StringUtils.isNotBlank(str)) {
									product.setCoveragePeriodValue(Double
											.parseDouble(str)); // 险种保障期
								}
								product.setPaymentPeriodType((String) attributes[8]); // 险种缴费期类型
								str = (String) attributes[9];
								if (StringUtils.isNotBlank(str)) {
									product.setPaymentPeriodValue(Double
											.parseDouble(str)); // 险种缴费期
								}
								product.setPaymentFreqType((String) attributes[10]); // 险种缴费周期
								product.setSaleStatus((String) attributes[11]); // 险种是否在售
								product.setBasicPlan("Y".equals(attributes[12])); // 险种是否主险
								number = (BigDecimal) attributes[13];
								if (number != null) {
									product.setDrawEndowmentAge(number
											.intValue()); // 险种养老金领取年龄
								}
								product.setEndowmentGetType((String) attributes[14]); // 险种养老金领取频次
								product.setEndowmentPayType((String) attributes[15]); // 险种养老金领取方式
								String insuredSeq = (String) attributes[16];
								if(StringUtils.isNotBlank(insuredSeq)){
									Integer iSeq = Integer.valueOf(insuredSeq);
									product.setInsuredSeq(iSeq);//被保人序号
								}else{
									product.setInsuredSeq(1);//被保人序号
								}
							}
							productList.add(product);
						}
					}
				}
				paraMap.put("p_policy_product_list", productList
						.toArray(new SUWBOMPlanDto[productList.size()]));
			}
		} catch (SQLException e) {
			String errMsg = "result handle error:" + e.getMessage();
			logger.error(errMsg, e);
			throw new RuntimeException(errMsg);
		}
		return paraMap;
	}

	/**
	 * 已经授权生存金抵交保费
	 * 
	 * @param policyNo
	 * @return
	 */
	public boolean isPaymentFeeAuthorized(String policyNo) {
		return "Y".equals(queryForObject("isPaymentFeeAuthorized", policyNo));
	}

	/**
	 * 存在生存金转账失败记录
	 * 
	 * @param policyNo
	 * @return
	 */
	public boolean isPaymentTransferFailure(String policyNo) {
		return "Y".equals(queryForObject("isPaymentTransferFailure", policyNo));
	}

	/**
	 * 查询某保单追加保费的最大限额
	 * 
	 * @param policyNo
	 * @param prodSeq
	 * @return Map&lt;String, Object&gt;<br/>
	 *         输入参数：<br/>
	 *         p_policy_no :保单号<br/>
	 *         p_prod_seq :产品序号<br/>
	 *         p_request_date :查询日期<br/>
	 *         输出参数： <br/>
	 *         p_addto_max_amount :追加保费最大限额<br/>
	 *         p_flag :返回标志 '0':成功 '1':失败<br/>
	 *         p_message :返回信息
	 */
	public Map<String, Object> maxAddupPolicyFee(String policyNo,
			Integer prodSeq, Date calcDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_prod_seq", prodSeq);
		paraMap.put("p_request_date", calcDate);
		queryForObject("maxAddupPolicyFee", paraMap);
		return paraMap;
	}

	/**
	 * 保险合同年度内申请加保次数
	 * 
	 * @param policyNo
	 * @param serviceItems
	 * @param applyDate
	 * @return
	 */
	public Integer policyYearAddupTimes(String policyNo, Integer prodSeq,
			Date applyDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("prodSeq", String.valueOf(prodSeq));
		paraMap.put("applyDate", applyDate);
		return (Integer) queryForObject("policyYearAddupTimes", paraMap);
	}

	/**
	 * 保险合同年度内申请减保次数
	 * 
	 * @param policyNo
	 * @param serviceItems
	 * @param applyDate
	 * @return
	 */
	public Integer policyYearDeductTimes(String policyNo, Integer prodSeq,
			Date applyDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("prodSeq", String.valueOf(prodSeq));
		paraMap.put("applyDate", applyDate);
		return (Integer) queryForObject("policyYearDeductTimes", paraMap);
	}

	/**
	 * 获取保单当前保单年度内操作某一具体保全项目的次数，仅支持37部分领取，35追加保费
	 * 
	 * @param policyNo
	 * @param prodSeq
	 * @param serviceItems
	 * @param applyDate
	 * @return
	 */
	public Integer getYearlyServiceItemTimes(String policyNo, Integer prodSeq,
			String serviceItems, Date applyDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("prodSeq", String.valueOf(prodSeq));
		paraMap.put("serviceItems", serviceItems);
		paraMap.put("applyDate", applyDate);
		return (Integer) queryForObject("getYearlyServiceItemTimes", paraMap);
	}

	/**
	 * 当前受理未完成的保全项目列表
	 * 
	 * @return
	 */
	public ServiceItemDomainDto[] acceptingServiceItems(String policyNo) {
		List<String> serviceItemsList = queryForList("acceptingServiceItems",
				policyNo);
		ServiceItemDomainDto[] retArr = null;
		if (serviceItemsList != null) {
			retArr = new ServiceItemDomainDto[serviceItemsList.size()];
			for (int i = 0; i < serviceItemsList.size(); i++) {
				ServiceItemDomainDto sid = ServiceItemDomainDto.create();
				sid.setServiceItem(serviceItemsList.get(i));
				retArr[i] = sid;
			}
		}
		return retArr;
	}

	/**
	 * 判断该保全项目能否继续处理
	 * 
	 * @param posNo
	 * @return Map&lt;String, Object&gt;<br/>
	 *         参数说明：<br/>
	 *         输入参数：<br/>
	 *         p_pos_no ：保全号<br/>
	 *         输出参数：<br/>
	 *         p_suspend_flag ：暂停标志 Y暂停 N非暂停<br/>
	 *         p_suspend_cause ：暂停原因<br/>
	 */
	public Map<String, Object> judgePosSuspend(String posNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_pos_no", posNo);
		queryForObject("judgePosSuspend", paraMap);
		return paraMap;
	}

	/**
	 * 已录入健康告知
	 * 
	 * @param posNo
	 * @return
	 */
	public boolean isHasHealthNote(String posNo) {
		return "Y".equals(queryForObject("isHasHealthNote", posNo));
	}

	/**
	 * 取最新的投被保人关系，补充告知和投保人变更时可能会有变化
	 * 
	 * @param serviceItems
	 * @return
	 */
	public String latestRelationAppIns(String posNo, String serviceItems) {
		Map pMap = new HashMap();
		pMap.put("posNo", posNo);
		pMap.put("serviceItems", serviceItems);

		return (String) queryForObject("QUERY_LATEST_RELATION_CODE", pMap);
	}

	/**
	 * 续期转账在途
	 * 
	 * @param policyNo
	 * @return
	 */
	public boolean isRenewTransfering(String policyNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		queryForObject("isRenewTransfering", paraMap);
		return "Y".equals(paraMap.get("p_flag"));
	}

	/**
	 * 计算受理日期前调天数
	 * 
	 * @param acceptDate
	 * @param applyDate
	 * @return
	 */
	public int forwardAcceptDays(Date acceptDate, Date applyDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_date_start", applyDate);
		paraMap.put("p_date_end", acceptDate);
		return (Integer) queryForObject("forwardAcceptDays", paraMap);
	}

	/**
	 * 判断保单是否已预约
	 */
	public boolean isReservation(String policyNo) {

		return "Y".equals((String) queryForObject("isReservation", policyNo));

	}

	/**
	 * 客户体检次数
	 */

	public int examinaTionNum(String policyNo) {

		return (Integer) (queryForObject("examinaTionTime", policyNo));
	}

	/**
	 * 中介平台保全人工核保待审核数据写入接口
	 * 
	 * @param policyNo
	 * @param posNo
	 * @param acceptBranchCode
	 * @return
	 */
	public Map<String, Object> applyGimUndwrt(String policyNo, String posNo,
			String acceptBranchCode) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_pos_no", posNo);
		paraMap.put("p_branch_code", acceptBranchCode);
		queryForObject("applyGimUndwrt", paraMap);
		return paraMap;

	}

	/**
	 * 判断保单新生效日期是否有效
	 */
	public boolean effectIsCorrect(String posNo, String policyNo) {

		Map rMap = new HashMap();
		rMap.put("posNo", posNo);
		rMap.put("policyNo", policyNo);

		Integer i = (Integer) (queryForObject("queryEffectDate", rMap));

		return (1 == i);
	}

	/**
	 * 判断卡单是否激活
	 */
	public boolean activaTionCard(String policyNo) {

		Integer i = (Integer) (queryForObject("queryBusSource", policyNo));

		return (1 == i);

	}

	/**
	 * 
	 * 
	 */
	public boolean entireSingleSurrender(String posNo) {

		Integer i = (Integer) (queryForObject("querySurrender", posNo));

		return (1 == i);
	}

	/**
	 * 判断保单是否淘宝渠道销售
	 */
	public boolean isTaobaoPolicy(String policyNo) {

		return "Y".equals((String) queryForObject("isTaobaoPolicy", policyNo));

	}

	/**
	 * 判断保单是否淘宝渠道销售
	 */
	public String getEPolicySource(String policyNo) {

		return (String) queryForObject("getEPolicySource", policyNo);

	}

	/**
	 * 犹豫期判断,是否存在犹豫期
	 */
	public Map<String, Object> scruplePeriodPolicy(String policyNo) {

		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		queryForObject("scruplePeriodPolicy", paraMap);

		return paraMap;

	}

	/**
	 * 判断保单能否操作犹豫期内撤销
	 */
	public Map<String, Object> isHesitateCondition(String policyNo,
			Date applyDate) {

		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_apply_date", applyDate);
		queryForObject("isHesitateCondition", paraMap);

		return paraMap;

	}

	/**
	 * 客户是否已报案未受理
	 */

	public boolean isClientReportedByClaim(String clientNo) {

		return "Y".equalsIgnoreCase((String) queryForObject(
				"isClientReportedByClaim", clientNo));

	}

	/**
	 * 产品是否给付过理赔金
	 */
	public boolean isProductPayByClaim(String policyNo, Integer prodSeq)

	{

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("policyNo", policyNo);
		map.put("prodSeq", prodSeq);
		return "Y".equalsIgnoreCase((String) queryForObject(
				"isProductPayByClaim", map));

	}
	

	/**
	 * 保单是否有组合代码
	 */

	public boolean isPolicyCombinationCode(String policyNo) {

		return ((Integer) queryForObject("isPolicyCombinationCode", policyNo))
				.intValue() > 0;

	}

	/**
	 * 保单指定险种是否发生过理赔（包含已结案和正在理赔）
	 * 
	 * @param policyNo
	 * @param prodSeq
	 * @return true/false
	 */
	public boolean isPolicyProdClaimed(String policyNo, String prodSeq) {
		boolean isHasClaime = false;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("prodSeq", prodSeq);
		String ishas = (String) queryForObject("isPolicyProdClaimed", paraMap);
		if ("Y".equals(ishas)) {
			isHasClaime = true;
		}
		return isHasClaime;
	}

	/**
	 * 保单指定险种是否有医疗责任
	 * 
	 * @param policyNo
	 * @param prodSeq
	 * @return true/false
	 */
	public boolean hasMedicalLiability(String policyNo, String prodSeq) {
		boolean isHasLiability = false;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("prodSeq", prodSeq);
		String ishas = (String) queryForObject("hasMedicalLiability", paraMap);
		if ("Y".equals(ishas)) {
			isHasLiability = true;
		}
		return isHasLiability;
	}

	/**
	 * 变更的电话号码是否与业务员一致
	 */

	public boolean isPhoneNoTheAgent(String posNo) {

		return "Y".equalsIgnoreCase((String) queryForObject(
				"isPhoneNoTheAgent", posNo));
	}

	/**
	 * 变更的电话号码是否3个及以上客户共用
	 */

	public boolean isPhoneNoForThreeClient(String posNo) {

		return "Y".equalsIgnoreCase((String) queryForObject(
				"isPhoneNoForThreeClient", posNo));
	}

	/**
	 * 投保人是否在黑名单中
	 * 
	 * @param clientName
	 * @param clientIdno
	 * @return true/false
	 */
	public boolean isApplicantInBlacklist(String clientName, String clientIdno) {
		boolean isInBlacklist = false;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("clientName", clientName);
		paraMap.put("clientIdno", clientIdno);
		String isin = (String) queryForObject("isApplicantInBlacklist", paraMap);
		if ("Y".equals(isin)) {
			isInBlacklist = true;
		}
		return isInBlacklist;
	}

	/**
	 * 被保人是否在黑名单中
	 * 
	 * @param clientName
	 * @param clientIdno
	 * @return true/false
	 */
	public boolean isInsuredInBlacklist(String clientName, String clientIdno) {
		boolean isInBlacklist = false;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("clientName", clientName);
		paraMap.put("clientIdno", clientIdno);
		String isin = (String) queryForObject("isInsuredInBlacklist", paraMap);
		if ("Y".equals(isin)) {
			isInBlacklist = true;
		}
		return isInBlacklist;
	}

	/**
	 * 受益人是否在黑名单中
	 * 
	 * @param clientName
	 * @param clientIdno
	 * @return true/false
	 */
	public boolean isBenefciaryInBlacklist(String clientName, String clientIdno) {
		boolean isInBlacklist = false;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("clientName", clientName);
		paraMap.put("clientIdno", clientIdno);
		String isin = (String) queryForObject("isBenefciaryInBlacklist", paraMap);
		if ("Y".equals(isin)) {
			isInBlacklist = true;
		}
		return isInBlacklist;
	}

	/**
	 * 变更项的姓名是否在黑名单中
	 * 
	 * @param clientName
	 * @param clientIdno
	 * @return true/false
	 */
	public boolean isChangenameInBlacklist(String clientName, String clientIdno) {
		boolean isInBlacklist = false;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("clientName", clientName);
		paraMap.put("clientIdno", clientIdno);
		String isin = (String) queryForObject("isChangenameInBlacklist", paraMap);
		if ("Y".equals(isin)) {
			isInBlacklist = true;
		}
		return isInBlacklist;
	}

	/**
	 * 受理人是否有电销续期保费退费权限
	 */

	public boolean isReturnPremPrivs(String userId) {
		return "Y".equalsIgnoreCase((String) queryForObject(
				"isReturnPremPrivs", userId));
	}

	/**
	 * 是否原交费账户
	 */
	public boolean isFirstAccountNo(String posNo) {
		return "Y".equalsIgnoreCase((String) queryForObject("isFirstAccountNo",
				posNo));
	}

	/**
	 * 销售网点是否有与公司签订协议
	 */
	public boolean isDepartmentHasProtocol(String posNo) {
		return "Y".equalsIgnoreCase((String) queryForObject(
				"isDepartmentHasProtocol", posNo));

	}
	
	/**
	 * 投连首期保费是否未进入理财账户
	 */
	public boolean isUwUntransFund(String policyNo) {
		return "Y".equalsIgnoreCase((String) queryForObject(
				"isUwUntransFund", policyNo));

	}

	/**
	 * 检查银行前置保全相关规则
	 * 
	 * @param posNo
	 * @return
	 */
	public String checkBankPosRules(String posNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_pos_no", posNo);
		queryForObject("checkBankPosRules", paraMap);
		if ("N".equals(paraMap.get("p_flag"))) {
			return (String) paraMap.get("p_message");
		}
		return "";

	}
	
	/**
	 * 保单未清偿贷款次数
	 */

	public int getUnPaidLoanTimes(String policyNo) {

		return (Integer) (queryForObject("getUnPaidLoanTimes", policyNo));
	}

	/**
	 * 保单是否能够享受vip免息复效 
	 * 
	 * @param policyNo
	 * @param applydate
	 * @return
	 */
	public boolean isFreeInterest(String policyNo, Date applydate) {
		boolean isFreeInterest = false;
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_apply_date", applydate);
		queryForObject("isFreeInterest", paraMap);
		if (!"0".equals(paraMap.get("p_flag"))) {
			throw new RuntimeException("查询免息复效出错：" + paraMap.get("p_message"));
		}
		if ("Y".equals(paraMap.get("p_can_free"))){;
		  isFreeInterest =true;
		}
		return isFreeInterest;
	}

	/**
	 * 将调用规则引擎的的入参出参toString后放到数据库 
	 * @param applyNo logString
	 * @return
	 */
	public void insertRulesBom(String controlNo,String ruleType,String bomContent){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("controlNo", controlNo);
		map.put("ruleType", ruleType);
		map.put("bomContent", bomContent);
		getSqlMapClientTemplate().insert(sqlName("insertRulesBom"), map);	
	}
	
	/**
	 * 受益人变更信息是否与险种一致
	 * 
	 * @param policyNo
	 * @param prodSeq
	 * @param benefType
	 * @param benefName
	 * @param benefSexCode
	 * @param benefBirthday
	 * @param benefIdType
	 * @param benefIdno
	 * @return true/false
	 */
	// public boolean isBeneficiaryInfoConsistent(String policyNo, String
	// prodSeq,
	// String benefType,String benefName,String benefSexCode,
	// Date benefBirthday,String benefIdType,String benefIdno) {
	// boolean isConsistent =false;
	// Map<String, Object> paraMap = new HashMap<String, Object>();
	// paraMap.put("policyNo", policyNo);
	// paraMap.put("prodSeq", prodSeq);
	// paraMap.put("benefType", benefType);
	// paraMap.put("benefName", benefName);
	// paraMap.put("benefSexCode", benefSexCode);
	// paraMap.put("benefBirthday", benefBirthday);
	// paraMap.put("benefIdType", benefIdType);
	// paraMap.put("benefIdno", benefIdno);
	// String ishas =(String) queryForObject("isBeneficiaryInfoConsistent",
	// paraMap);
	// if ("Y".equals(ishas)){
	// isConsistent =true;
	// }
	// return isConsistent;
	// }
	
	public String getAcceptDetailNewValue(String posNo) {
		return (String) getSqlMapClientTemplate().queryForObject(
				ServiceItemsDAO_16.class.getName() + ".getAcceptDetailNewValue", posNo);
	}
}
