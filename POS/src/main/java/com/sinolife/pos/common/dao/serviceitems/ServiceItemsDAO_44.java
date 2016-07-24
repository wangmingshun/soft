package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_44;

@Repository("serviceItemsDAO_44")
public class ServiceItemsDAO_44 extends ServiceItemsDAO {

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
		ServiceItemsInputDTO_44 item = (ServiceItemsInputDTO_44) serviceItemsInputDTO;
		//设置可转换险种
		item.setChangeableProduct(commonQueryDAO.
				getchangeableProduct(item.getPolicyNo()));
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
		ServiceItemsInputDTO_44 items = (ServiceItemsInputDTO_44) serviceItemsInputDTO;
		ServiceItemsInputDTO_44 snapshot = (ServiceItemsInputDTO_44) items
				.getOriginServiceItemsInputDTO();

		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();

		PosAcceptDetailDTO detailDTO = new PosAcceptDetailDTO();
		detailDTO.setPosNo(items.getPosNo());
		detailDTO.setPosObject("1");
		detailDTO.setProcessType("6");
		detailDTO.setObjectValue(items.getPolicyNo());
		detailDTO.setItemNo("155");
		detailDTO.setOldValue(snapshot.getChangeableProduct() == null ? "CIME_PN0" : snapshot
				.getChangeableProduct());
		detailDTO.setNewValue(items.getChangeableProduct());

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
