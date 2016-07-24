package com.sinolife.pos.common.dao.serviceitems;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.FinancialProductsDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_1;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_37;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;
import com.sinolife.pos.common.util.detailgenerator.ItemProcessFilter;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSUWBOMPlanDto;

/**
 * 37 部分领取
 * 
 * 注意：该保全项目默认一个保单下只有一个投连万能险种
 */
@Repository("serviceItemsDAO_37")
public class ServiceItemsDAO_37 extends ServiceItemsDAO {
	
	private static final Map<String, String> PROC_PROPERTY_CONFIG_MAP;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("withdrawAmount", 	"1|051|1|3|policyNo");//部分领取金额
		map.put("financialProducts","1|052|1|3|policyNo");//部分领取账户代码
		map.put("withdrawUnits", 	"1|053|1|3|policyNo");//部分领取单位数
		map.put("finProductCode", 	"1|098|1|3|policyNo");//部分领取产品代码
		map.put("finProdSeq", 		"1|097|1|3|policyNo");//部分领取产品序号
		map.put("payPolicy", 		"1|150|1|3|policyNo");//抵交续期保费保单号
		
		PROC_PROPERTY_CONFIG_MAP = map;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#queryServiceItemsInfoExtra(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_37 item = (ServiceItemsInputDTO_37)serviceItemsInputDTO;
		
		String policyNo = item.getPolicyNo();
		
		//查询理财产品代码和理财类型
		Map<String, Object> retMap = commonQueryDAO.getFinancialPolicyProductByPolicyNo(policyNo);
		String flag = (String) retMap.get("p_flag");
		String msg = (String) retMap.get("p_message");
		if(!"0".equals(flag))
			throw new RuntimeException(msg);
		
		String prodSeq = (String) retMap.get("p_prod_seq");
		String productCode = (String) retMap.get("p_product_code");
		String financialType = (String) retMap.get("p_financial_type");
		
		item.setFinProdSeq(prodSeq);
		item.setFinProductCode(productCode);
		
		if("1".equals(financialType)) {
			//投连账户
			item.setUnitLinkedFlag("Y");
			
			List<FinancialProductsDTO> financialProductsList = commonQueryDAO.getFinancialProductsList(item.getPolicyNo(), prodSeq);
			item.setFinancialProductsList(financialProductsList);
		}else if("2".equals(financialType)) {
			//万能账户
			item.setUniversalFlag("Y");
			
			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("p_policy_no", item.getPolicyNo());
			queryForObject("getValueAndWithdrawTime", paraMap);
			
			BigDecimal policyValue = (BigDecimal) paraMap.get("p_policy_value");
			BigDecimal withdrawTime = (BigDecimal) paraMap.get("p_withdraw_time");
			
			item.setPolicyValue(policyValue);
			item.setWithdrawTime(withdrawTime);
		} else {
			throw new RuntimeException("非投连万能产品");
		}
		
		item.setPolicyProvideTimeEditable(true);
		
		return item;
	}
	/**
	 * 提供给外围保全
	 */
	public ServiceItemsInputDTO_37 queryServiceItemsInfoExtra_37(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return (ServiceItemsInputDTO_37)queryServiceItemsInfoExtra(serviceItemsInputDTO);
	}
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#generateAcceptDetailDTOList(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_37 item = (ServiceItemsInputDTO_37)serviceItemsInputDTO;
		
		AcceptDetailGenerator generator = new AcceptDetailGenerator(PROC_PROPERTY_CONFIG_MAP, item.getPosNo(), beginGroupNo);
		generator.setDefaultObjectValue(item.getPolicyNo());
		
		if("Y".equals(item.getUnitLinkedFlag())) {
			//处理投连
			generator.processSimpleListDTO(item.getFinancialProductsList(), new ItemProcessFilter() {
				@Override
				public boolean needProcess(Map<String, Object> listItem, Map<String, Object> oldValueItem) {
					//只传领取单位大于0的值
					BigDecimal withdrawUnits = (BigDecimal) listItem.get("withdrawUnits");
					return withdrawUnits != null && withdrawUnits.compareTo(new BigDecimal(0)) > 0;
				}
			});
		}
		generator.setDefaultObjectValue(null);
		generator.processSimpleDTO(item);
		return generator.getResult();
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#validate(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(ServiceItemsInputDTO serviceItemsInputDTO, Errors err) {
		super.validate(serviceItemsInputDTO, err);
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#fillBOMForProcessRuleCheck(com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto)
	 */
	@Override
	public void fillBOMForProcessRuleCheck(POSBOMPolicyDto bom) {
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		//领取金额
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "051");
		posObjectAndItemNoList.add(item);
		//部分领取账户代码
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "052");
		posObjectAndItemNoList.add(item);
		//领取单位数
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "053");
		posObjectAndItemNoList.add(item);
		//部分领取产品序号
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "097");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(bom.getPosNo(), posObjectAndItemNoList);
		
		//查询理财产品代码和理财类型
		Map<String, Object> retMap = commonQueryDAO.getFinancialPolicyProductByPolicyNo(bom.getPolicyNo());
		String flag = (String) retMap.get("p_flag");
		String msg = (String) retMap.get("p_message");
		if(!"0".equals(flag))
			throw new RuntimeException(msg);
		
//		String prodSeq = (String) retMap.get("p_prod_seq");
//		String productCode = (String) retMap.get("p_product_code");
		if(detailList != null && !detailList.isEmpty() && bom.getPlansList() != null && bom.getPlansList().length > 0) {
			Integer currentPosProdSeq = null;
			for(PosAcceptDetailDTO detail : detailList) {
				if("1".equals(detail.getPosObject()) && "097".equals(detail.getItemNo())) {
					for(POSUWBOMPlanDto plan : bom.getPlansList()) {
						if(plan.getProdSeq() == Integer.parseInt(detail.getNewValue())) {
							plan.setCurrentPosPlan(true);
							currentPosProdSeq = Integer.parseInt(detail.getNewValue());
						}
					}
				}
			}
			String financialType = (String) retMap.get("p_financial_type");
			if(currentPosProdSeq != null) {
				if("1".equals(financialType)) {
					//投连账户 查询部分领取单位数
					List<FinancialProductsDTO> financialProductsList = commonQueryDAO.getFinancialProductsList(bom.getPolicyNo(), String.valueOf(currentPosProdSeq));
					Map<String, Object> paraMap = new HashMap<String, Object>();
					paraMap.put("p_policy_no", bom.getPolicyNo());
					paraMap.put("p_prod_seq", currentPosProdSeq);
					paraMap.put("p_request_date", bom.getApplyDate());
					paraMap.put("p_financial_products_list", financialProductsList);
					//计算领取前的账户价值
					queryForObject("queryPolicyFundAmount", paraMap);
					if(!"0".equals(paraMap.get("p_flag"))) {
						throw new RuntimeException("查询部分领取前个人账户价值出错：" + paraMap.get("p_message"));
					}
					BigDecimal personalAccountNetValue = (BigDecimal) paraMap.get("p_total_amount");
					bom.setPersonalAccountNetValue(personalAccountNetValue.doubleValue());
					for(PosAcceptDetailDTO detail : detailList) {
						if("1".equals(detail.getPosObject()) && "053".equals(detail.getItemNo())) {//单位数
							//减去领取的单位数
							for(PosAcceptDetailDTO detailInternal : detailList) {
								if("1".equals(detailInternal.getPosObject()) && "052".equals(detailInternal.getItemNo()) && detailInternal.getInfoGroupNo().equals(detail.getInfoGroupNo())) {//投连账户代码
									for(FinancialProductsDTO fp : financialProductsList) {
										if(fp.getFinancialProducts().equals(detailInternal.getNewValue())) {
											fp.setUnits(fp.getUnits().subtract(new BigDecimal(detail.getNewValue())));
										}
									}
								}
							}
						}
					}
					//得到领取后的账户价值
					queryForObject("queryPolicyFundAmount", paraMap);
					if(!"0".equals(paraMap.get("p_flag"))) {
						throw new RuntimeException("查询部分领取后个人账户价值出错：" + paraMap.get("p_message"));
					}
					BigDecimal posedPersonalAccountNetValue = (BigDecimal) paraMap.get("p_total_amount");
					bom.setPosedPersonalAccountNetValue(posedPersonalAccountNetValue.doubleValue());
					//领取金额=领取前账户价值-领取后账户价值
					for(POSUWBOMPlanDto plan : bom.getPlansList()) {
						if(currentPosProdSeq != null && plan.getProdSeq() == currentPosProdSeq.intValue()) {
							plan.setWithdrawMoney(personalAccountNetValue.subtract(posedPersonalAccountNetValue).doubleValue());
						}
					}
				} else if("2".equals(financialType)) {
					double personalAccountValue = bom.getPersonalAccountValue();	//个人账户价值
					double personalAccountNetValue = personalAccountValue;			//个人账户价值净值
					if(personalAccountValue > 0) {
						//当前领取的险种代码
						String productCode = commonQueryDAO.getProductCodeByPolicyNoAndProdSeq(bom.getPolicyNo(), currentPosProdSeq);
						//查询险种是否为可贷险种
						boolean isLoanable = posRulesDAO.isLoanablePlan(productCode);
						if(isLoanable) {
							//查询贷款信息
							retMap = commonQueryDAO.calcPolicyLoanInterestByPolicyNo(bom.getPolicyNo(), bom.getApplyDate());
							if("0".equals(retMap.get("p_flag"))) {
								//已贷本息和
								BigDecimal loanAllSum = (BigDecimal) retMap.get("p_loan_all_sum");
								if(loanAllSum != null) {
									//险种为可贷险种，则个人账户价值净值需要减去已贷本息和
									personalAccountNetValue = personalAccountNetValue - loanAllSum.doubleValue();
								}
							}
						}
					}
					//个人账户价值净值
					bom.setPersonalAccountNetValue(personalAccountNetValue);
					//万能账户 查部分领取金额
					for(PosAcceptDetailDTO detail : detailList) {
						if("1".equals(detail.getPosObject()) && "051".equals(detail.getItemNo())) {	//金额
							for(POSUWBOMPlanDto plan : bom.getPlansList()) {
								if(currentPosProdSeq != null && plan.getProdSeq() == currentPosProdSeq.intValue()) {
									//部分领取金额
									plan.setWithdrawMoney(Double.parseDouble(detail.getNewValue()));
									//领取后剩余个人账户价值净值  万能：personalAccountNetValue - 险种下累计withdrawMoney
									bom.setPosedPersonalAccountNetValue(personalAccountNetValue - plan.getWithdrawMoney());
								    
								}
								if("UIWL_AN1".equals(plan.getProductCode()))
								{
									double  baseSumIns=commonQueryDAO.queryPolicyProductByPolicyNoAndProdSeq(bom.getPolicyNo(), plan.getProdSeq()).getBaseSumIns().doubleValue();
								   //万能险变更后的保额
									plan.setChangedBaseSumIns(baseSumIns-Double.parseDouble(detail.getNewValue()));
								}
							}
						}
					}
				}
			}
		}
		super.fillBOMForProcessRuleCheck(bom);
	}
	
}
