package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.FinancialProductsDTO;
import com.sinolife.pos.common.dto.FinancialProductsTransDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_38;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_39;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSUWBOMPlanDto;

/**
 * 投资转换
 */
@Repository("serviceItemsDAO_39")
public class ServiceItemsDAO_39 extends ServiceItemsDAO {
	
	private static final Map<String, String> PROC_PROPERTY_CONFIG_MAP;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("outFinancialProducts",	"1|040|1|3|policyNo");//投资转换转出账户代码
		map.put("inFinancialProducts",	"1|041|1|3|policyNo");//投资转换转入账户代码
		map.put("outUnits", 			"1|042|1|3|policyNo");//投资转换单位数
		map.put("finProductCode", 		"1|096|1|3|policyNo");//投资转换的产品代码
		map.put("finProdSeq", 			"1|095|1|3|policyNo");//投资转换产品序号
		
		PROC_PROPERTY_CONFIG_MAP = map;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#queryServiceItemsInfoExtra(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_39 item = (ServiceItemsInputDTO_39)serviceItemsInputDTO;
		
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
		
		//初始化转换信息，为其加入一个空行，作为页面模板
		List<FinancialProductsTransDTO> financialProductsTransList = new ArrayList<FinancialProductsTransDTO>();
		financialProductsTransList.add(new FinancialProductsTransDTO());
		item.setFinancialProductsTransList(financialProductsTransList);
		
		//元素不能通过页面绑定自动删除，这里设置size属性，指示financialProductsTransList应有长度
		item.setFinancialProductsTransListSize(1);
		return item;
	}
	/**
	 * 提供给外围保全
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtra_39(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return queryServiceItemsInfoExtra(serviceItemsInputDTO);
	}
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#generateAcceptDetailDTOList(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_39 item = (ServiceItemsInputDTO_39)serviceItemsInputDTO;
		
		AcceptDetailGenerator generator = new AcceptDetailGenerator(PROC_PROPERTY_CONFIG_MAP, item.getPosNo(), beginGroupNo);
		//设置ObjectValue值
		generator.setDefaultObjectValue(item.getPolicyNo());
		generator.processSimpleListDTO(item.getFinancialProductsTransList());
		
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
		
		//投资转换转出账户代码
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "040");
		posObjectAndItemNoList.add(item);
		
		//投资转换转入账户代码
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "041");
		posObjectAndItemNoList.add(item);
		
		//投资转换产品序号，用于设置当前操作的险种
		item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "095");
		posObjectAndItemNoList.add(item);
		
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(bom.getPosNo(), posObjectAndItemNoList);
		Map<String, String> inMap = new HashMap<String, String>();
		Map<String, String> outMap = new HashMap<String, String>();
		if(detailList != null && !detailList.isEmpty()) {
			for(PosAcceptDetailDTO detail : detailList) {
				if("1".equals(detail.getPosObject()) && "040".equals(detail.getItemNo())) {
					inMap.put(detail.getInfoGroupNo(), detail.getNewValue());
				} else if("1".equals(detail.getPosObject()) && "041".equals(detail.getItemNo())) {
					outMap.put(detail.getInfoGroupNo(), detail.getNewValue());
				} else if("1".equals(detail.getPosObject()) && "095".equals(detail.getItemNo()) && bom.getPlansList() != null) {
					for(POSUWBOMPlanDto plan : bom.getPlansList()) {
						if(plan.getProdSeq() == Integer.parseInt(detail.getNewValue())) {
							plan.setCurrentPosPlan(true);
						}
					}
				}
			}
		}
		for(Iterator<Entry<String, String>> it = inMap.entrySet().iterator(); it.hasNext(); ) {
			Entry<String, String> entry = it.next();
			String groupNo = entry.getKey();
			String inAccount = entry.getValue();
			if(outMap.containsKey(groupNo) && !inAccount.equals(outMap.get(groupNo))) {
				it.remove();
				outMap.remove(groupNo);
			}
		}
		bom.setInOutAccountNotSame(inMap.isEmpty() && outMap.isEmpty());
		
		super.fillBOMForProcessRuleCheck(bom);
	}
	
}
