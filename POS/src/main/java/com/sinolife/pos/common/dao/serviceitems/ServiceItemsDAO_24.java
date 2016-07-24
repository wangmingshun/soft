package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_1;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_24;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSUWBOMPlanDto;

/**
 * 24 年金给付方式选择
 */
@Repository("serviceItemsDAO_24")
public class ServiceItemsDAO_24 extends ServiceItemsDAO {

	private static Set<String> PERMIT_PLAN_SET;
	static {
		Set<String> set = Collections.synchronizedSet(new HashSet<String>());
		set.add("UIAN_AN0");
		set.add("UIAN_BN0");
		set.add("UBAN_AN1");
		set.add("UIAN_CN0");
		set.add("UBAN_BN1");
		set.add("CIAN_CP1");		
		set.add("UIAN_EN1");
		set.add("UNAN_AN1");
		set.add("UIAN_FN0");
		set.add("UEAN_EN1");
		set.add("UEAN_DN1");
		set.add("UBAN_EN0");
		set.add("UIAN_GN0");
		set.add("CBAN_DN1");
		set.add("UBAN_FN1");
		set.add("UEAN_FN1");
		set.add("UEAN_GN1");
		set.add("UEAN_HN1");
		set.add("CNAN_CN1");
		PERMIT_PLAN_SET = set;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#queryServiceItemsInfoExtra(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_24 itemDTO = (ServiceItemsInputDTO_24) serviceItemsInputDTO;
		queryForObject("QUERY_SURVIVAL_COVERAGE", itemDTO.getPolicyNo(), itemDTO);
		
		itemDTO.setProductCode((String)queryForObject("QUERY_SURVIVAL_PRODUCT_CODE", itemDTO.getPolicyNo()));
		
		if("U".equals(itemDTO.getProductType())){
			itemDTO.setNextPayDate((String)queryForObject("QUERY_UIAN_AN0_FIRST_PAY_DATE", itemDTO.getPolicyNo()));
			itemDTO.setEffectedTwo((String)queryForObject("QUERY_EFFECTED_TWO_YEARS", itemDTO.getPolicyNo()));
			
		}else if("C".equals(itemDTO.getProductType())){
			itemDTO.setProductFrequency((String)queryForObject("QUERY_CIAN_CP1_FREQUENCY", itemDTO.getPolicyNo()));
		}
		
		return itemDTO;
	}
	/**
	 * 提供给外围系统保全
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtra_24(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return  queryServiceItemsInfoExtra(serviceItemsInputDTO);
	}
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#generateAcceptDetailDTOList(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(ServiceItemsInputDTO itemDTO, int beginGroupNo) {
		ServiceItemsInputDTO_24 item = (ServiceItemsInputDTO_24)itemDTO;
		ServiceItemsInputDTO_24 origin = (ServiceItemsInputDTO_24)itemDTO.getOriginServiceItemsInputDTO();
		String posNo = item.getPosNo();
		String policyNo = item.getPolicyNo();
		
		List<PosAcceptDetailDTO> detailList = new ArrayList<PosAcceptDetailDTO>();
		detailList.add(new PosAcceptDetailDTO(posNo,"1","1",policyNo,"044",origin.getFrequency(),item.getFrequency()));
		if("U".equals(item.getProductType())){
			detailList.add(new PosAcceptDetailDTO(posNo,"1","1",policyNo,"034",origin.getNextPayDate(),item.getNextPayDate()));
			
		}else if("C".equals(item.getProductType())){
			detailList.add(new PosAcceptDetailDTO(posNo,"1","1",policyNo,"043",origin.getPayType(),item.getPayType()));
//			detailList.add(new PosAcceptDetailDTO(posNo,"1","1",policyNo,"117",origin.getPayTypePara(),item.getPayTypePara()));//弃儿
			detailList.add(new PosAcceptDetailDTO(posNo,"1","1",policyNo,"118",origin.getPayPeriodType(),item.getPayPeriodType()));
			detailList.add(new PosAcceptDetailDTO(posNo,"1","1",policyNo,"119",origin.getPayPeriodTypePara(),item.getPayPeriodTypePara()));			
		}
		
		return detailList;
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
		//年金领取方式
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "043");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(bom.getPosNo(), posObjectAndItemNoList);
		if(detailList != null && !detailList.isEmpty()) {
			for(PosAcceptDetailDTO detail : detailList) {
				if("1".equals(detail.getPosObject()) && "043".equals(detail.getItemNo())) {
					for(POSUWBOMPlanDto plan : bom.getPlansList()) {
						String productCode = plan.getProductCode();
						if(PERMIT_PLAN_SET.contains(productCode)) {
							//年金领取方式
							plan.setAnnualPaymentMode(detail.getNewValue());
							plan.setCurrentPosPlan(true);
						}
					}
				}
			}
		}
		super.fillBOMForProcessRuleCheck(bom);
	}
}