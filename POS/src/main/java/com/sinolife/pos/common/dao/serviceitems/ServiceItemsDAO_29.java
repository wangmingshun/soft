package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_29;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;
import com.sinolife.pos.common.util.detailgenerator.ItemProcessFilter;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSUWBOMPlanDto;

/**
 * 29 预约终止附加险
 */
@Repository("serviceItemsDAO_29")
public class ServiceItemsDAO_29 extends ServiceItemsDAO {
	
	private static final Map<String, String> DETAIL_CONFIG_MAP;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("prodSeq", 			"2|020|1|3|prodSeq");//附加险序号
		
		DETAIL_CONFIG_MAP = map;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#queryServiceItemsInfoExtra(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_29 item = (ServiceItemsInputDTO_29)serviceItemsInputDTO;
		
		List<PolicyProductDTO> productList = commonQueryDAO.queryPolicyProductListByPolicyNo(item.getPolicyNo());
		if(productList != null && !productList.isEmpty()) {
			String primaryPlanPremStatus = null;
			this.setCanBeSelectedFlag(productList);
			for(PolicyProductDTO product : productList) {
				if("Y".equals(product.getIsPrimaryPlan())) {
					primaryPlanPremStatus= product.getPremStatus();
					product.setCanBeSelectedFlag("N");
					if(StringUtils.isBlank(product.getMessage())) {
						product.setMessage("不能操作主险");
					} else {
						product.setMessage(product.getMessage() + "，不能操作主险");
					}
				} else if("Y".equals(product.getLongFlag())) {
					product.setCanBeSelectedFlag("N");
					if(StringUtils.isBlank(product.getMessage())) {
						product.setMessage("不能操作长期险");
					} else {
						product.setMessage(product.getMessage() + "，不能操作长期险");
					}
				} else if ("1".equals(primaryPlanPremStatus)||"0".equals(primaryPlanPremStatus)){
					product.setCanBeSelectedFlag("Y");

				}else{
					product.setCanBeSelectedFlag("N");
					product.setMessage("主险不是缴清或者缴费状态");
				}
			}
		}
		item.setProductList(productList);
		
		return item;
	}
	/* 
	 * 提供外围系统调用
	 */
	public ServiceItemsInputDTO_29 queryServiceItemsInfo(ServiceItemsInputDTO serviceItemsInputDTO) {
		return (ServiceItemsInputDTO_29) queryServiceItemsInfoExtra(serviceItemsInputDTO);
	}
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#generateAcceptDetailDTOList(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_29 item = (ServiceItemsInputDTO_29)serviceItemsInputDTO;
		AcceptDetailGenerator generator = new AcceptDetailGenerator(DETAIL_CONFIG_MAP, item.getPosNo(), beginGroupNo);
		generator.processSimpleListDTO(item.getProductList(), new ItemProcessFilter() {
			public boolean needProcess(Map<String, Object> listItem, Map<String, Object> oldValueItem) {
				return (Boolean) listItem.get("selected") && "Y".equals(listItem.get("canBeSelectedFlag"));
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
		//选中的险种
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "2");
		item.put("itemNo", "020");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(bom.getPosNo(), posObjectAndItemNoList);
		if(detailList != null && !detailList.isEmpty()) {
			for(PosAcceptDetailDTO detail : detailList) {
				if("2".equals(detail.getPosObject()) && "020".equals(detail.getItemNo())) {
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
