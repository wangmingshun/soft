package com.sinolife.pos.common.dao.serviceitems;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.FinancialProductsDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_37;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_38;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSUWBOMPlanDto;

/**
 * 账户分配比例变更
 */
@Repository("serviceItemsDAO_38")
public class ServiceItemsDAO_38 extends ServiceItemsDAO {
	
	private static final Map<String, String> PROC_PROPERTY_CONFIG_MAP;
	public static final List<CodeTableItemDTO> RATE_LIST;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("financialProducts","1|038|1|3|policyNo");//账户分配比例变更账户代码
		map.put("rate", 			"1|039|1|3|policyNo");//基本投资账户分配比例
		map.put("finProductCode", 	"1|094|1|3|policyNo");//账户分配比例变更产品代码
		map.put("finProdSeq", 		"1|093|1|3|policyNo");//账户分配比例变更产品序号
		
		PROC_PROPERTY_CONFIG_MAP = map;
		
		List<CodeTableItemDTO> list = Collections.synchronizedList(new ArrayList<CodeTableItemDTO>());
		for(int i = 0; i <= 100; i += 5) {
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
		ServiceItemsInputDTO_38 item = (ServiceItemsInputDTO_38)serviceItemsInputDTO;
		
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
		
		//查询投资账户信息
		List<FinancialProductsDTO> financialProductsList = commonQueryDAO.getFinancialProductsList(item.getPolicyNo(), prodSeq);
		item.setFinancialProductsList(financialProductsList);
		
		//设置转换比例下拉框数据源
		item.setRateList(RATE_LIST);
		
		return item;
	}
	/**
	 * 提供给外围保全
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtra_38(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return queryServiceItemsInfoExtra(serviceItemsInputDTO);
	}
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#generateAcceptDetailDTOList(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_38 item = (ServiceItemsInputDTO_38)serviceItemsInputDTO;
		ServiceItemsInputDTO_38 snapshot = (ServiceItemsInputDTO_38)serviceItemsInputDTO.getOriginServiceItemsInputDTO();
		
		AcceptDetailGenerator generator = new AcceptDetailGenerator(PROC_PROPERTY_CONFIG_MAP, item.getPosNo(), beginGroupNo);
		//设置ObjectValue值
		generator.setDefaultObjectValue(item.getPolicyNo());
		generator.processSimpleListDTO(item.getFinancialProductsList(), snapshot.getFinancialProductsList(), "financialProducts", true);
		
		//恢复ObjectValue值
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
		//根据 账户分配比例变更产品序号 设置当前操作的险种
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "093");
		posObjectAndItemNoList.add(item);
		//分配比例
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "039");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(bom.getPosNo(), posObjectAndItemNoList);
		
		//由于受理明细只写了变更的比例，因此假定原始账户比例之和为100%，只要本次变更的新旧值相加的值相等即可说明，在变更之后仍是100%
		BigDecimal oldSum = new BigDecimal(0D);
		BigDecimal newSum = new BigDecimal(0D);
		
		if(detailList != null && !detailList.isEmpty() && bom.getPlansList() != null && bom.getPlansList().length > 0) {
			for(PosAcceptDetailDTO detail : detailList) {
				if("1".equals(detail.getPosObject()) && "093".equals(detail.getItemNo())) {
					for(POSUWBOMPlanDto plan : bom.getPlansList()) {
						if(plan.getProdSeq() == Integer.parseInt(detail.getNewValue())) {
							plan.setCurrentPosPlan(true);
						}
					}
				}
				if("1".equals(detail.getPosObject()) && "039".equals(detail.getItemNo())) {
					if(StringUtils.isNotBlank(detail.getOldValue())) {
						oldSum = oldSum.add(new BigDecimal(detail.getOldValue()));
					}
					if(StringUtils.isNotBlank(detail.getNewValue())) {
						newSum = newSum.add(new BigDecimal(detail.getNewValue()));
					}
				}
			}
		}
		//各账户的分配比例之和
		BigDecimal allFundsRateSum = new BigDecimal(1D).add(newSum.subtract(oldSum));
		bom.setAllFundsRateSum(allFundsRateSum.doubleValue());
		
		super.fillBOMForProcessRuleCheck(bom);
	}
}
