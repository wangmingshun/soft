package com.sinolife.pos.common.dao.serviceitems;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_8;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;

/**
 * 8 保单贷款
 */
@SuppressWarnings({ "unchecked" })
@Repository("serviceItemsDAO_8")
public class ServiceItemsDAO_8 extends ServiceItemsDAO {

	private static final Map<String, String> PROC_PROPERTY_CONFIG_MAP;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("loanAmount", "1|055|1|3|policyNo");// 贷款金额
		map.put("cashValueSum", "1|056|1|3|policyNo");// 贷款时保单现金价值

		PROC_PROPERTY_CONFIG_MAP = map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#
	 * queryServiceItemsInfoExtra
	 * (com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(
			ServiceItemsInputDTO serviceItemsInputDTO) {

		ServiceItemsInputDTO_8 item = (ServiceItemsInputDTO_8) serviceItemsInputDTO;

		item.setSpecialFuncList(null);

		Integer i = (Integer) (queryForObject("QUERY_NOTPAID_LOAN_COUNT",
				item.getPolicyNo()));
         //不存在未清偿或贷款记录，可以选择功能特殊件
		if (i.intValue() == 0) {

			// 查询功能特殊件选项
			List<CodeTableItemDTO> specialFuncList = getSqlMapClientTemplate()
					.queryForList(
							ServiceItemsDAO.class.getName()
									+ ".querySpecialFuncListByServiceItems",
							item.getServiceItems());
			item.setSpecialFuncList(specialFuncList);
		} else {
			item.setSpecialFuncEnabled(false);

		}
		return calPolicyLoanInfo(item);

	}

	/**
	 * @param serviceItemsInputDTO
	 * @return ServiceItemsInputDTO
	 */
	public ServiceItemsInputDTO calPolicyLoanInfo(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_8 item = (ServiceItemsInputDTO_8) serviceItemsInputDTO;
		Date applyDate = commonQueryDAO.getApplyDateByPosNo(item.getPosNo());
		String policyNo = item.getPolicyNo();
		Map<String, Object> resultMap = null;
		String flag = null;

		// 查询产品信息
		List<PolicyProductDTO> policyProductList = commonQueryDAO
				.queryPolicyProductListByPolicyNo(policyNo);
		item.setPolicyProductList(policyProductList);

		BigDecimal loanMaxSum = new BigDecimal(0);
		BigDecimal cashValueSum = new BigDecimal(0);
		BigDecimal loanMax = new BigDecimal(0);
		BigDecimal cashValue = new BigDecimal(0);
		
		//查贷款利率 edit by wangmingshun start
		resultMap = commonQueryDAO.getLoanRateType(policyNo, applyDate);
		if (!"0".equals(resultMap.get("p_flag"))) {
			throw new RuntimeException("查询保单贷款利率出错:"
					+ resultMap.get("p_message"));
		}
		//所有险种利率都一样
		BigDecimal loanInterestRate = (BigDecimal) resultMap.get("interestRate");
		//此处查询出来的loanRateType放入serviceItem中以便写入明细表，前端不可修改。
		String loanRateType = (String) resultMap.get("loanRateType");
		item.setLoanRateType(loanRateType);
		//查贷款利率 edit by wangmingshun end
		
		if (policyProductList != null && !policyProductList.isEmpty()) {
			for (PolicyProductDTO pp : policyProductList) {

				// 判断主险是否为万能险产品
				if (pp.getProdSeq().intValue() == 1) {

					item.setUlifeMainProduct(posRulesDAO.isUniversal(pp
							.getProductCode()));

				}

				// 查询现在申请日最大可贷金额及现金价值
				resultMap = commonQueryDAO.calcLoanMaxAndCashValueByProduct(
						pp.getPolicyNo(), pp.getProductCode(), pp.getProdSeq(),
						applyDate, serviceItemsInputDTO.getSpecialFunc());
				if (!"0".equals(resultMap.get("p_flag"))) {
					throw new RuntimeException("查询最大可贷金额及现金价值出错:"
							+ resultMap.get("p_message"));
				}
				loanMax = (BigDecimal) resultMap.get("p_loan_max");
				cashValue = (BigDecimal) resultMap.get("p_cash_value");

				if (cashValue != null) {
					cashValueSum = cashValueSum.add(cashValue);
					pp.setCashValue(cashValue);
				}
				if (loanMax != null) {
					// 累加最大可贷金额
					loanMaxSum = loanMaxSum.add(loanMax);
					pp.setLoanMax(loanMax);
				}

				// 查贷款利率
				/*
				resultMap = commonQueryDAO.getProductLoanRate(
						pp.getProductCode(), applyDate);
				if (!"0".equals(resultMap.get("p_flag"))) {
					throw new RuntimeException("查询险种贷款利率出错:"
							+ resultMap.get("p_message"));
				}
				BigDecimal loanInterestRate = (BigDecimal) resultMap
						.get("p_rate");*/
				pp.setLoanInterestRate(loanInterestRate);
			}
		}
		item.setCashValueSum(cashValueSum);

		// 查询最小贷款金额
		double policyLoanMinMoney = posRulesDAO.policyLoanMinMoney(policyNo);
		item.setLoanMinSum(new BigDecimal(policyLoanMinMoney));

		// 查询既有贷款及利息累计
		resultMap = commonQueryDAO.calcPolicyLoanInterestByPolicyNo(policyNo,
				applyDate);
		flag = (String) resultMap.get("p_flag");
		if ("0".equals(flag)) {
			BigDecimal loanAllSum = (BigDecimal) resultMap
					.get("p_loan_all_sum");
			item.setLoanAllSum(loanAllSum);

			if (loanAllSum != null) {
				loanMaxSum = loanMaxSum.subtract(loanAllSum);
			}
		}
		if (loanMaxSum.compareTo(new BigDecimal(0)) < 0) {
			loanMaxSum = new BigDecimal(0);
		}
		item.setLoanMaxSum(loanMaxSum);

		return item;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#
	 * generateAcceptDetailDTOList
	 * (com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(
			ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_8 item = (ServiceItemsInputDTO_8) serviceItemsInputDTO;
		
		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();
		//将初始化中的贷款模式loan_rate_type写入明细表 edit by wangmingshun
		acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "1", "6", 
				item.getPolicyNo(), "164", "", item.getLoanRateType()));
		//贷款使用利率写入明细表,利率都相同，所以取第一个产品即可
		String loanInterestRate = item.getPolicyProductList().get(0) == null ? null
				: item.getPolicyProductList().get(0).getLoanInterestRate().toString();
		acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "1", "6", 
				item.getPolicyNo(), "072", "", loanInterestRate));
		
		AcceptDetailGenerator generator = new AcceptDetailGenerator(
				PROC_PROPERTY_CONFIG_MAP, item.getPosNo(), beginGroupNo);
		generator.processSimpleDTO(item);
		
		acceptDetailList.addAll(generator.getResult());
		
		return acceptDetailList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#validate(com
	 * .sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO,
	 * org.springframework.validation.Errors)
	 */
	@Override
	public void validate(ServiceItemsInputDTO serviceItemsInputDTO, Errors err) {
		super.validate(serviceItemsInputDTO, err);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#
	 * fillBOMForProcessRuleCheck
	 * (com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto)
	 */
	@Override
	public void fillBOMForProcessRuleCheck(POSBOMPolicyDto bom) {
		// 默认为500
		bom.setLoanMoneyApplied(500);

		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		// 贷款金额
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "055");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(
				bom.getPosNo(), posObjectAndItemNoList);
		if (detailList != null && !detailList.isEmpty()) {
			double loanMoneyApplied = Double.parseDouble(detailList.get(0)
					.getNewValue());
			// 录入的申请贷款金额
			bom.setLoanMoneyApplied(loanMoneyApplied);
		}
		super.fillBOMForProcessRuleCheck(bom);
	}

}
