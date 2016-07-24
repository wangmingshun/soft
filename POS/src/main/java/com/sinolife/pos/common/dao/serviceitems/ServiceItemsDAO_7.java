package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PolicyProductPremDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_7;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;
import com.sinolife.pos.common.util.detailgenerator.ItemProcessFilter;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSUWBOMPlanDto;

/**
 * 7 减额交清
 */
@Repository("serviceItemsDAO_7")
public class ServiceItemsDAO_7 extends ServiceItemsDAO {
	
	private static final Map<String, String> PROC_PROPERTY_CONFIG_MAP;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("prodSeq",	"2|019|1|3|prodSeq");
		
		PROC_PROPERTY_CONFIG_MAP = map;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#queryServiceItemsInfoExtra(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_7 item = (ServiceItemsInputDTO_7)serviceItemsInputDTO;
		
		//查询产品信息
		List<PolicyProductDTO> policyProductList = commonQueryDAO.queryPolicyProductListByPolicyNo(item.getPolicyNo());
		if(policyProductList != null && !policyProductList.isEmpty()) {
			for(PolicyProductDTO policyProduct : policyProductList) {
				//险种责任状态为有效，或者责任状态为无效但失效原因为8 且险种收费状态为有效，或者险种收费状态为缴费中止，保费来源为客户
				PolicyProductPremDTO premInfo = policyProduct.getPremInfo();
				String dutyStatus = policyProduct.getDutyStatus();//1有效 2无效
				String lapseReason = policyProduct.getLapseReason();
				String premStatus = null;//0缴清1缴费2缴费中止
				String premSource = null;//1客户2保单现金价值减额缴清3保单现金价值垫缴4保险公司
				if(premInfo != null) {
					premStatus = premInfo.getPremStatus();
					premSource = premInfo.getPremSource();
				}
				if(("1".equals(dutyStatus) || "2".equals(dutyStatus) && "8".equals(lapseReason)) && ("1".equals(premStatus) || "2".equals(premStatus) && "1".equals(premSource))) {
					policyProduct.setCanBeSelectedFlag("Y");
				} else {
					policyProduct.setCanBeSelectedFlag("N");
				}
			}
		}
		item.setPolicyProductList(policyProductList);
		
		return item;
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#generateAcceptDetailDTOList(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_7 item = (ServiceItemsInputDTO_7)serviceItemsInputDTO;
		AcceptDetailGenerator generator = new AcceptDetailGenerator(PROC_PROPERTY_CONFIG_MAP, item.getPosNo(), beginGroupNo);
		generator.processSimpleListDTO(item.getPolicyProductList(), new ItemProcessFilter() {
			@Override
			public boolean needProcess(Map<String, Object> listItem, Map<String, Object> oldValueItem) {
				String canBeSelectedFlag = (String) listItem.get("canBeSelectedFlag");
				Boolean selected = (Boolean) listItem.get("selected");
				return "Y".equals(canBeSelectedFlag) && selected;
			}
			
		});
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
		//险种序号
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "2");
		item.put("itemNo", "019");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(bom.getPosNo(), posObjectAndItemNoList);
		if(detailList != null && !detailList.isEmpty()) {
			for(PosAcceptDetailDTO detail : detailList) {
				if("2".equals(detail.getPosObject()) && "019".equals(detail.getItemNo())) {
					for(POSUWBOMPlanDto plan : bom.getPlansList()) {
						if(plan.getProdSeq() == Integer.parseInt(detail.getNewValue())) {
							plan.setCurrentPosPlan(true);
						}
					}
				}
			}
		}
		super.fillBOMForProcessRuleCheck(bom);
	}

}
