package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_25;

@Repository("serviceItemsDAO_25")
public class ServiceItemsDAO_25 extends ServiceItemsDAO {

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#queryServiceItemsInfoExtra(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_25 item = (ServiceItemsInputDTO_25)serviceItemsInputDTO;
		//查询自垫状态
		String aplOption = (String) queryForObject("getAplOptionByPolicy", item.getPosNo());
		item.setAplOption(aplOption);		
		return item;
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#generateAcceptDetailDTOList(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_25 items = (ServiceItemsInputDTO_25)serviceItemsInputDTO;
		ServiceItemsInputDTO_25 snapshot = (ServiceItemsInputDTO_25) items.getOriginServiceItemsInputDTO();
		
		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();
		
		PosAcceptDetailDTO detailDTO = new PosAcceptDetailDTO();
		detailDTO.setPosNo(items.getPosNo());
		detailDTO.setPosObject("1");
		detailDTO.setProcessType("1");
		detailDTO.setObjectValue(items.getPolicyNo());
		detailDTO.setItemNo("002");
		detailDTO.setOldValue(snapshot.getAplOption());
		detailDTO.setNewValue(items.getAplOption());

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
}
