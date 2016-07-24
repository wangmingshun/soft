package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_43;

@Repository("serviceItemsDAO_43")
public class ServiceItemsDAO_43 extends ServiceItemsDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#
	 * queryServiceItemsInfoExtra
	 * (com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_43 item = (ServiceItemsInputDTO_43) serviceItemsInputDTO;
		item.setPrivsFlag(commonQueryDAO.queryClientInputPrivs(item
				.getClientNo()));
		return item;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#
	 * generateAcceptDetailDTOList
	 * (com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(
			ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_43 items = (ServiceItemsInputDTO_43) serviceItemsInputDTO;
		ServiceItemsInputDTO_43 snapshot = (ServiceItemsInputDTO_43) items
				.getOriginServiceItemsInputDTO();

		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();

		PosAcceptDetailDTO detailDTO = new PosAcceptDetailDTO();
		detailDTO.setPosNo(items.getPosNo());
		detailDTO.setPosObject("3");
		detailDTO.setProcessType("1");
		detailDTO.setObjectValue(items.getClientNo());
		detailDTO.setItemNo("097");
		detailDTO.setOldValue(snapshot.getPrivsFlag() == null ? "1" : snapshot
				.getPrivsFlag());
		detailDTO.setNewValue(items.getPrivsFlag());

		acceptDetailList.add(detailDTO);

		return acceptDetailList;
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
}
