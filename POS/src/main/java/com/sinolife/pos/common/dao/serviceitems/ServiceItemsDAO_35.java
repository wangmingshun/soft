package com.sinolife.pos.common.dao.serviceitems;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.FinancialProductsDTO;
import com.sinolife.pos.common.dto.PersonalNoticeDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_35;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;
import com.sinolife.pos.common.util.detailgenerator.ItemProcessFilter;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMFundDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSUWBOMPlanDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPolicyDto;

/**
 * 追加保费
 */
@Repository("serviceItemsDAO_35")
public class ServiceItemsDAO_35 extends ServiceItemsDAO {
	public static final List<CodeTableItemDTO> RATE_LIST;
	private static final Map<String, String> PROC_PROPERTY_CONFIG_MAP;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("addPremAmount", 	"1|045|1|3|policyNo");	//追加保费的金额
		map.put("financialProducts","1|046|1|3|policyNo");	//追加保费进入的账户代码
		map.put("rate",				"1|047|1|3|policyNo");	//追加保费的分配比例
		map.put("finProductCode", 	"1|100|1|3|policyNo");	//追加保费产品代码
		map.put("finProdSeq", 		"1|099|1|3|policyNo");	//追加保费产品序号
		
		PROC_PROPERTY_CONFIG_MAP = map;
		
		List<CodeTableItemDTO> list = Collections.synchronizedList(new ArrayList<CodeTableItemDTO>());
		for(int i = 0; i <= 100; i += 10) {
			CodeTableItemDTO item = new CodeTableItemDTO();
			item.setCode(String.valueOf(i));
			item.setDescription(String.valueOf(i));
			list.add(item);
		}
		RATE_LIST = list;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#queryServiceItemsInfoExtra(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_35 item = (ServiceItemsInputDTO_35)serviceItemsInputDTO;
		
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
		} else if("2".equals(financialType)) {
			//万能账户
			item.setUniversalFlag("Y");
			
			List<PolicyProductDTO> policyProductList = commonQueryDAO.queryPolicyProductListByPolicyNo(item.getPolicyNo());
			item.setPolicyProductList(policyProductList);
		} else {
			throw new RuntimeException("非投连万能产品");
		}
		
		item.setRateList(RATE_LIST);
		return item;
	}
	/**
	 * 提供给银保通接口
	 * @param serviceItemsInputDTO
	 * @return
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtraToBia_35(ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_35 item = (ServiceItemsInputDTO_35)serviceItemsInputDTO;
		
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
		} else if("2".equals(financialType)) {
			//万能账户
			item.setUniversalFlag("Y");
			
			List<PolicyProductDTO> policyProductList = commonQueryDAO.queryPolicyProductListByPolicyNo(item.getPolicyNo());
			item.setPolicyProductList(policyProductList);
		} else {
			throw new RuntimeException("非投连万能产品");
		}
		
		item.setRateList(RATE_LIST);
		return item;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#generateAcceptDetailDTOList(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_35 item = (ServiceItemsInputDTO_35)serviceItemsInputDTO;
		AcceptDetailGenerator generator = new AcceptDetailGenerator(PROC_PROPERTY_CONFIG_MAP, item.getPosNo(), beginGroupNo);
		generator.setDefaultObjectValue(item.getPolicyNo());
		
		if("Y".equals(item.getUnitLinkedFlag())) {
			//处理分配比例变更
			generator.processSimpleListDTO(item.getFinancialProductsList(), new ItemProcessFilter() {
				@Override
				public boolean needProcess(Map<String, Object> listItem, Map<String, Object> oldValueItem) {
					//只传比例大于0的值
					BigDecimal rate = (BigDecimal) listItem.get("rate");
					return rate != null && rate.compareTo(new BigDecimal(0)) > 0;
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
		//追加保费产品序号
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "099");
		posObjectAndItemNoList.add(item);
		//追加保费的金额
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "045");
		posObjectAndItemNoList.add(item);
		//追加保费进入的账户代码
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "046");
		posObjectAndItemNoList.add(item);
		//追加比例
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "047");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(bom.getPosNo(), posObjectAndItemNoList);
		if(detailList != null && !detailList.isEmpty() && bom.getPlansList() != null && bom.getPlansList().length > 0) {
			BigDecimal rateSum = new BigDecimal(0D);
			Integer currentPosProdSeq = null;
			for(PosAcceptDetailDTO detail : detailList) {
				if("1".equals(detail.getPosObject()) && "099".equals(detail.getItemNo())) {
					for(POSUWBOMPlanDto plan : bom.getPlansList()) {
						if(plan.getProdSeq() == Integer.parseInt(detail.getNewValue())) {
							plan.setCurrentPosPlan(true);
							currentPosProdSeq = Integer.parseInt(detail.getNewValue());
						}
					}
				} else if("1".equals(detail.getPosObject()) && "047".equals(detail.getItemNo())) {
					rateSum = rateSum.add(new BigDecimal(detail.getNewValue()));
					for(PosAcceptDetailDTO detailInternal : detailList) {
						if("1".equals(detailInternal.getPosObject()) && "046".equals(detailInternal.getItemNo()) && detail.getInfoGroupNo().equals(detailInternal.getInfoGroupNo())) {
							for(POSBOMFundDto fund : bom.getFundList()) {
								if(fund.getFinancialProducts().equals(detailInternal.getNewValue())) {
									fund.setBaseFundRate(new Double(detail.getNewValue()));
								}
							}
						}
					}
				}
			}
			if(rateSum.equals(new BigDecimal(0D))) {
				bom.setAllFundsRateSum(1D);
			} else {
				bom.setAllFundsRateSum(rateSum.doubleValue());
			}
			
			String appendProductCode="";
			for(PosAcceptDetailDTO detail : detailList) {
				if("1".equals(detail.getPosObject()) && "045".equals(detail.getItemNo())) {
					for(POSUWBOMPlanDto plan : bom.getPlansList()) {
						if(currentPosProdSeq != null && currentPosProdSeq.intValue() == plan.getProdSeq()) {
							//追加保费金额
							plan.setAddupPolicyFee(Double.parseDouble(detail.getNewValue()));
							Map<String, Object> map = new HashMap<String, Object>();						
							map.put("clientNo", (bom.getInsurantList()[0]).getId());
							appendProductCode=plan.getProductCode();
							map.put("productCode", appendProductCode);
							map.put("periodType", "0");							
							queryForObject("getInsuredPayablePremProd", map);
							java.math.BigDecimal sumPremAndAddedPrem=(java.math.BigDecimal)map.get("result");							
							plan.setSumPremAndAddedPrem(sumPremAndAddedPrem.doubleValue());//累计保费及追加保费和
						}
					}
				}
			}
			
			
			boolean productHesitated = false; // 险种是否在犹豫期
			
			productHesitated = posRulesDAO.isHesitatedProduct(
					bom.getPolicyNo(), appendProductCode,
					bom.getApplyDate());
			
			
			for (POSUWBOMPlanDto plan : bom.getPlansList()) {
				
				
			if(	appendProductCode.equals(plan.getProductCode()))
				{
					
				   plan.setHesitated(productHesitated);
				   plan.setCurrentPosPlan(true);
					
				}
			}

		
			
		}
		super.fillBOMForProcessRuleCheck(bom);
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#fillBOMForUnwrtRuleCheck(com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPolicyDto, java.lang.String, com.sinolife.pos.common.dto.PersonalNoticeDTO)
	 */
	@Override
	public void fillBOMForUnwrtRuleCheck(SUWBOMPolicyDto bom, String posNo, PersonalNoticeDTO appPersonalNotice, PersonalNoticeDTO insPersonalNotice) {
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		//追加保费的金额
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "045");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(posNo, posObjectAndItemNoList);
		if(detailList != null && !detailList.isEmpty()) {
			for(PosAcceptDetailDTO detail : detailList) {
				if("1".equals(detail.getPosObject()) && "045".equals(detail.getItemNo())) {
					bom.getCurrentServiceItem().setAddupPolicyFee(Double.parseDouble(detail.getNewValue()));
				}
			}
		}
	}
	
}
