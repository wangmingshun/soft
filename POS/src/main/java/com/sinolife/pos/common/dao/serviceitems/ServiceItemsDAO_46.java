package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_46;
import com.sinolife.pos.common.util.PosValidateWrapper;

@Repository("serviceItemsDAO_46")
public class ServiceItemsDAO_46 extends ServiceItemsDAO {

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
		ServiceItemsInputDTO_46 item = (ServiceItemsInputDTO_46) serviceItemsInputDTO;
		//设置保单状态 Y:已冻结  N:未冻结,查询到记录表示保单处于冻结状态
		int policyStatus = (Integer) queryForObject("queryPolicyStatus", item.getPolicyNo());
		item.setPolicyStatus(policyStatus > 0 ? "Y" : "N");
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
		ServiceItemsInputDTO_46 items = (ServiceItemsInputDTO_46) serviceItemsInputDTO;
		ServiceItemsInputDTO_46 snapshot = (ServiceItemsInputDTO_46) items
				.getOriginServiceItemsInputDTO();

		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();
		
		//保单冻结或者解冻 此处写入Y表示冻结保单，N表示解冻
		acceptDetailList.add(new PosAcceptDetailDTO(items.getPosNo(), "2", "1", 
				items.getPolicyNo(), "273", snapshot.getPolicyStatus(), items.getPolicyStatus()));
		//冻结或者解冻备注
		acceptDetailList.add(new PosAcceptDetailDTO(items.getPosNo(), "2", "1", 
				items.getPolicyNo(), "274", "", items.getFreezeOrThawRemark()));

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
		if(serviceItemsInputDTO instanceof ServiceItemsInputDTO_46) {
			ServiceItemsInputDTO_46 sii = (ServiceItemsInputDTO_46) serviceItemsInputDTO;
			ServiceItemsInputDTO_46 snapshot = (ServiceItemsInputDTO_46) serviceItemsInputDTO
				.getOriginServiceItemsInputDTO();
			PosValidateWrapper wrapper = new PosValidateWrapper(err);
			
			if(sii.getPolicyStatus() != null 
					&& sii.getPolicyStatus().equals(snapshot.getPolicyStatus())) {
				wrapper.addErrMsg("policyStatus", "处理出错！请从待办任务列表重新载入！！");
			}
		}
	}
}
