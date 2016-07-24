package com.sinolife.pos.common.dao.serviceitems;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PolicyProductPremDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_5;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSUWBOMPlanDto;

/**
 * 5新增附加险
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Repository("serviceItemsDAO_5")
public class ServiceItemsDAO_5 extends ServiceItemsDAO {

	public static final Map<String, String> DETAIL_CONFIG_MAP;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("productCode",		"1|110|1|3|policyNo");//险种代码
		map.put("baseSumIns",		"1|111|1|3|policyNo");//基本保额
		map.put("annStandardPrem",	"1|134|1|3|policyNo");//保费 即年缴标准保费
		map.put("premTerm",			"1|112|1|3|policyNo");//交费年期
		map.put("premPeriodType",	"1|135|1|3|policyNo");//交费期类型
		map.put("coveragePeriod",	"1|113|1|3|policyNo");//保险年期
		map.put("coverPeriodType",	"1|136|1|3|policyNo");//保险年期类型
		map.put("dividend",			"1|114|1|3|policyNo");//红利选择方式
		map.put("autoContinue",		"1|115|1|3|policyNo");//是否自动续保
		map.put("insuredNo",		"1|170|1|3|policyNo");//被保险人客户号
		DETAIL_CONFIG_MAP = map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#
	 * queryServiceItemsInfoExtra
	 * (com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_5 itemDTO = (ServiceItemsInputDTO_5) serviceItemsInputDTO;

		itemDTO.setProductList(commonQueryDAO.queryPolicyProductListByPolicyNo(itemDTO.getPolicyNo()));
		
		Map map = (Map)queryForObject("QUERY_PRIMARY_DIVID", itemDTO.getPolicyNo());
		itemDTO.setPrimaryDivFlag((String)map.get("PRIMARY_DIV_FLAG"));
		
		map = commonQueryDAO.getProductPremCoverage(null, null, itemDTO.getPolicyNo(), String.valueOf(map.get("PROD_SEQ")), null);

		itemDTO.setLeftPremTerm(String.valueOf(map.get("leftPremTerm")));
		itemDTO.setLeftCoveragePeriod(String.valueOf(map.get("leftCoveragePeriod")));

		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", itemDTO.getPolicyNo());
		paraMap.put("applyDate", itemDTO.getApplyDate());
		List list = queryForList("QUERY_FULL_PRODUCT_LIST", paraMap);
		itemDTO.setProductOption(JSONArray.fromObject(list).toString());
		
		return itemDTO;
	}
	
	/**
	 * 提供给外围保全
	 */
	public ServiceItemsInputDTO_5 queryServiceItemsInfoExtra_5(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return (ServiceItemsInputDTO_5) queryServiceItemsInfoExtra(serviceItemsInputDTO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#
	 * generateAcceptDetailDTOList
	 * (com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_5 itemDTO = (ServiceItemsInputDTO_5) serviceItemsInputDTO;

		AcceptDetailGenerator generator = new AcceptDetailGenerator(DETAIL_CONFIG_MAP, itemDTO.getPosNo(), beginGroupNo);
		generator.processSimpleListDTO(itemDTO.getProductList());

		return generator.getResult();
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

	/**
	 * 查询产品费率因子
	 * @param productCode
	 * @param type P-缴费期，C-保险期
	 * @param typeValue 类型的值，如，交几"年"，保障几"岁"等
	 * @return
	 */
	public List queryProductPremRate(String productCode, String type, String typeValue){
		Map pMap = new HashMap();
		pMap.put("productCode", productCode);
		pMap.put("typeValue", typeValue);
		List list = null;
		
		if("P".equals(type)){
			list = queryForList("QUERY_PREM_RATE_PREM_PERIOD", pMap);
			
		}else if("C".equals(type)){
			list = queryForList("QUERY_PREM_RATE_COVER_PERIOD", pMap);
		}
		
		return list;
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#fillBOMForProcessRuleCheck(com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto)
	 */
	@Override
	public void fillBOMForProcessRuleCheck(POSBOMPolicyDto bom) {
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
	    //保单投保日期
	    String policyNo =bom.getPolicyNo();
	    Date  policyApplyDate=commonQueryDAO.getPolicyApplyDate(policyNo);
		bom.setSubmitDate(policyApplyDate);
		//险种代码
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "110");
		posObjectAndItemNoList.add(item);
		//基本保额
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "111");
		posObjectAndItemNoList.add(item);
		//交费年期
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "112");
		posObjectAndItemNoList.add(item);
		//交费期类型
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "135");
		posObjectAndItemNoList.add(item);
		//保险年期
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "113");
		posObjectAndItemNoList.add(item);
		//保险年期类型
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "136");
		posObjectAndItemNoList.add(item);
		//保费 即年缴标准保费
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "134");
		posObjectAndItemNoList.add(item);
		//被保险人客户号
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "170");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(bom.getPosNo(), posObjectAndItemNoList);
		if(detailList != null && !detailList.isEmpty()) {
			List<PolicyProductDTO> addedProductList = new ArrayList<PolicyProductDTO>();
			for(PosAcceptDetailDTO detail : detailList) {
				String posObject = detail.getPosObject();
				String itemNo = detail.getItemNo();
				String groupNo = detail.getInfoGroupNo();
				String objectValue = detail.getObjectValue();
				String newValue = detail.getNewValue();
				if(StringUtils.isNotBlank(groupNo)) {
					int intGroupNo = Integer.parseInt(groupNo);
					PosUtils.appendNullToSize(addedProductList, intGroupNo + 1);
					PolicyProductDTO policyProduct = addedProductList.get(intGroupNo);
					PolicyProductPremDTO premInfo = null;
					if(policyProduct == null) {
						policyProduct = new PolicyProductDTO();
						policyProduct.setPolicyNo(objectValue);
						premInfo = new PolicyProductPremDTO();
						premInfo.setPolicyNo(objectValue);
						policyProduct.setPremInfo(premInfo);
						addedProductList.set(intGroupNo, policyProduct);
					} else {
						premInfo = policyProduct.getPremInfo();
					}
					if("1".equals(posObject) && "110".equals(itemNo)) {
						policyProduct.setProductCode(newValue);
					} else if("1".equals(posObject) && "111".equals(itemNo)) {
						policyProduct.setBaseSumIns(new BigDecimal(newValue));
					} else if("1".equals(posObject) && "112".equals(itemNo)) {
						premInfo.setPremTerm(new BigDecimal(newValue));
					} else if("1".equals(posObject) && "135".equals(itemNo)) {
						premInfo.setPremPeriodType(newValue);
					} else if("1".equals(posObject) && "113".equals(itemNo)) {
						policyProduct.setCoveragePeriod(new Integer(newValue));
					} else if("1".equals(posObject) && "136".equals(itemNo)) {
						policyProduct.setCoverPeriodType(newValue);
					} else if("1".equals(posObject) && "134".equals(itemNo)) {
						premInfo.setAnnStandardPrem(new BigDecimal(newValue));
					} else if("1".equals(posObject) && "170".equals(itemNo)) {
						policyProduct.setInsuredNo(newValue);
					}
				}
			}
			if(addedProductList != null && !addedProductList.isEmpty()) {
				//干掉空值
				for(Iterator<PolicyProductDTO> it = addedProductList.iterator(); it.hasNext(); ) {
					PolicyProductDTO policyProduct = it.next();
					if(policyProduct == null) {
						it.remove();
					}
				}
				//在险种列表中加上本次新增的险种
				if(addedProductList != null && !addedProductList.isEmpty()) {
					POSUWBOMPlanDto[] plansArr = bom.getPlansList();
					List<POSUWBOMPlanDto> plansList = new ArrayList<POSUWBOMPlanDto>();
					for(POSUWBOMPlanDto plan : plansArr) {
						plansList.add(plan);
					}
					for(PolicyProductDTO policyProduct : addedProductList) {
						PolicyProductPremDTO premInfo = policyProduct.getPremInfo();
						String productCode = policyProduct.getProductCode();
						POSUWBOMPlanDto plan = POSUWBOMPlanDto.create();
						//产品代码
						plan.setProductCode(productCode);
						
						Map pcMap = commonQueryDAO.getProductPremCoverage(bom.getPosNo(), productCode, null, null, null);
						//保险年期
						plan.setCoveragePeriod(Integer.parseInt((String)pcMap.get("coveragePeriod")));
						//交费年期
						plan.setPremTerm(Integer.parseInt((String)pcMap.get("premTerm")));
						
						//录入的是保额
						if(policyProduct.getBaseSumIns() != null && policyProduct.getBaseSumIns().compareTo(new BigDecimal(0D)) > 0) {
							plan.setInsLimitIn(true);
						}
						if(premInfo != null) {
							if(premInfo.getPremTerm() != null) {
								//查询险种是否在售
								PolicyDTO policyInfo = commonQueryDAO.queryPolicyInfoByPolicyNo(policyProduct.getPolicyNo());
								Map<String, Object> retMap =commonQueryDAO.checkProductIsOnSale(productCode,
																								policyInfo.getChannelType(),
																								policyInfo.getBranchCode(),
																								premInfo.getPremPeriodType(),
																								premInfo.getPremTerm(),
																								policyProduct.getCoverPeriodType(),
																								new BigDecimal(policyProduct.getCoveragePeriod()),
																								bom.getApplyDate());
								if("Y".equals(retMap.get("p_sign"))) {
									plan.setInSale("Y".equals(retMap.get("p_sale_flag")));
								}
							}
							//录入的是保费
							if(premInfo.getAnnStandardPrem() != null && premInfo.getAnnStandardPrem().compareTo(new BigDecimal(0D)) > 0) {
								plan.setUnitsIn(true);
							}
						}
						//是长期险
						plan.setLongTerm(posRulesDAO.isLongTerm(productCode));
						//是短期险
						plan.setShortTerm(posRulesDAO.isShortTerm(productCode));
						plan.setNewlyAddedPlan(true);
						plan.setCurrentPosPlan(true);
						plan.setInsurantNo(policyProduct.getInsuredNo());
						plansList.add(plan);
					}
					bom.setPlansList(plansList.toArray(new POSUWBOMPlanDto[plansList.size()]));
				}
			}
		}
		super.fillBOMForProcessRuleCheck(bom);
	}

}
