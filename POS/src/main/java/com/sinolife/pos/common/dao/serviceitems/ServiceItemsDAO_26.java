package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_26;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSUWBOMPlanDto;

@Repository("serviceItemsDAO_26")
public class ServiceItemsDAO_26 extends ServiceItemsDAO {

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#queryServiceItemsInfoExtra(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_26 item = (ServiceItemsInputDTO_26)serviceItemsInputDTO;
		
		queryForObject("QUERY_ITEMS_INPUT", item.getPolicyNo(), item);
		
		return item;
	}
	/* 提供给外围系统部调用 */
	public ServiceItemsInputDTO_26 queryServiceItemsInfo(ServiceItemsInputDTO serviceItemsInputDTO) {
		return  (ServiceItemsInputDTO_26)queryServiceItemsInfoExtra(serviceItemsInputDTO);		
	}


	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#generateAcceptDetailDTOList(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_26 serviceItemsInputDTO_26 = (ServiceItemsInputDTO_26)serviceItemsInputDTO;
		
		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();
		
		PosAcceptDetailDTO detailDTO = new PosAcceptDetailDTO();
		detailDTO.setPosNo(serviceItemsInputDTO_26.getPosNo());
		detailDTO.setPosObject("1");
		detailDTO.setProcessType("1");
		detailDTO.setObjectValue(serviceItemsInputDTO_26.getPolicyNo());
		detailDTO.setItemNo("001");
		detailDTO.setOldValue(serviceItemsInputDTO_26.getOldDividendSelection());
		detailDTO.setNewValue(serviceItemsInputDTO_26.getNewDividendSelection());
		
		acceptDetailList.add(detailDTO);
		return acceptDetailList;
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
		item.put("posObject", "1");
		item.put("itemNo", "001");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(bom.getPosNo(), posObjectAndItemNoList);
		if(detailList != null && !detailList.isEmpty()) {
			for(PosAcceptDetailDTO detail : detailList) {
				if("1".equals(detail.getPosObject()) && "001".equals(detail.getItemNo())) {
					for(POSUWBOMPlanDto plan : bom.getPlansList()) {
						//红利选择方式
						plan.setBonusOption(detail.getNewValue());
					}
				}
			}
		}
		super.fillBOMForProcessRuleCheck(bom);
	}
	
}
