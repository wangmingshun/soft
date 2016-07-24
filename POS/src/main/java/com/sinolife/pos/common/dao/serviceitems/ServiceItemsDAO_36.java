package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_36;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSUWBOMPlanDto;

/**
 * 36 基本保费变更
 */
@Repository("serviceItemsDAO_36")
public class ServiceItemsDAO_36 extends ServiceItemsDAO {
	
	private static final Map<String, String> PROC_PROPERTY_CONFIG_MAP;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("periodPrem", 		"1|054|1|3|policyNo");//变更后基本期缴保费
		map.put("finProductCode", 	"1|102|1|3|policyNo");//基本保费变更产品代码
		map.put("finProdSeq", 		"1|101|1|3|policyNo");//基本保费变更产品序号
		PROC_PROPERTY_CONFIG_MAP = map;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#queryServiceItemsInfoExtra(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_36 item = (ServiceItemsInputDTO_36)serviceItemsInputDTO;
		
		//查询理财产品代码和理财类型
		Map<String, Object> retMap = commonQueryDAO.getFinancialPolicyProductByPolicyNo(item.getPolicyNo());
		String flag = (String) retMap.get("p_flag");
		String msg = (String) retMap.get("p_message");
		if(!"0".equals(flag))
			throw new RuntimeException(msg);
		
		String prodSeq = (String) retMap.get("p_prod_seq");
		String productCode = (String) retMap.get("p_product_code");
		String financialType = (String) retMap.get("p_financial_type");
		
		if(!"1".equals(financialType) && !"2".equals(financialType))
			throw new RuntimeException("非投连万能产品");
		
		item.setFinProdSeq(prodSeq);
		item.setFinProductCode(productCode);
		
		item.setFinancialTypeProductFullName(commonQueryDAO.getProductFullNameByProductCode(productCode));
		
		//查询险种信息
		List<PolicyProductDTO> policyProductList = commonQueryDAO.queryPolicyProductListByPolicyNo(item.getPolicyNo());
		item.setPolicyProductList(policyProductList);
		
		return item;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#generateAcceptDetailDTOList(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_36 item = (ServiceItemsInputDTO_36)serviceItemsInputDTO;
		
		AcceptDetailGenerator generator = new AcceptDetailGenerator(PROC_PROPERTY_CONFIG_MAP, item.getPosNo(), beginGroupNo);
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
		//基本保费变更产品序号
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "101");
		posObjectAndItemNoList.add(item);
		//变更后基本期缴保费
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "054");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(bom.getPosNo(), posObjectAndItemNoList);
		if(detailList != null && !detailList.isEmpty() && bom.getPlansList() != null && bom.getPlansList().length > 0) {
			for(PosAcceptDetailDTO detail : detailList) {
				if("1".equals(detail.getPosObject()) && "101".equals(detail.getItemNo())) {
					for(POSUWBOMPlanDto plan : bom.getPlansList()) {
						if(plan.getProdSeq() == Integer.parseInt(detail.getNewValue())) {
							plan.setCurrentPosPlan(true);
							for(PosAcceptDetailDTO detailInternal : detailList) {
								if("1".equals(detailInternal.getPosObject()) && "054".equals(detailInternal.getItemNo())) {
									//保全变更后的期交保费
									plan.setNewPeriodTermSum(Double.parseDouble(detailInternal.getNewValue()));
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
