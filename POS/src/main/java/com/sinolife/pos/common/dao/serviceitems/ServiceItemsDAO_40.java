package com.sinolife.pos.common.dao.serviceitems;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;

/**
 * 撤销回退
 * 与通用项目逻辑没有多少共同点，建这个类目前仅为get"serviceItemsDAO_40"不报错
 */
@Repository("serviceItemsDAO_40")
public class ServiceItemsDAO_40 extends ServiceItemsDAO {

	@Override
	public void validate(ServiceItemsInputDTO serviceItemsInputDTO, Errors err) {
	}

	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(ServiceItemsInputDTO itemDTO, int beginGroupNo) {
		return null;
	}

	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(ServiceItemsInputDTO itemDTO) {
		return itemDTO;
	}

}
