package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_18;

/**
 * 签名变更
 */
@Repository("serviceItemsDAO_18")
public class ServiceItemsDAO_18 extends ServiceItemsDAO {

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
		ServiceItemsInputDTO_18 items = (ServiceItemsInputDTO_18) serviceItemsInputDTO;

		queryForObject("QUERY_APP_ISUR_NO", items.getPolicyNo(), items);

		// 签名变更已经有客户选择项，注释掉这里，使用原有的选择项
		// //签名变更可选择变更客户对象
		// items.setClientSelectEnabled(true);

		return items;
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
		PosAcceptDetailDTO detailDTO;
		ServiceItemsInputDTO_18 serviceItemsInputDTO_18 = (ServiceItemsInputDTO_18) serviceItemsInputDTO;

		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();

		String[] changeObjs = serviceItemsInputDTO_18.getChangeObj().split(",");
		String posNo = serviceItemsInputDTO_18.getPosNo();
		String clientNo = serviceItemsInputDTO_18.getClientNo();
		String policyNo = serviceItemsInputDTO_18.getPolicyNo();
		String[] changeCauses = serviceItemsInputDTO_18.getChangeCauses();
        boolean jj = false;
        
//		for (int i = 0; i < changeObjs.length; i++) {
//			detailDTO = new PosAcceptDetailDTO();
//			detailDTO.setPosNo(posNo);
//			detailDTO.setPosObject("3");
//			detailDTO.setProcessType("1");
//			detailDTO.setObjectValue(clientNo);
//			detailDTO.setItemNo("013");
//			detailDTO.setNewValue(changeObjs[i]);
//
//			acceptDetailList.add(detailDTO);
//		}
        if("2".equals(changeObjs[0])){
        	clientNo = changeObjs[1];
        }
	    detailDTO = new PosAcceptDetailDTO();
	    detailDTO.setPosNo(posNo);
		detailDTO.setPosObject("3");
		detailDTO.setProcessType("1");
		detailDTO.setObjectValue(clientNo);
		detailDTO.setItemNo("013");
		detailDTO.setNewValue(changeObjs[0]);
		acceptDetailList.add(detailDTO);

		
		for (int j = 0; changeCauses != null && j < changeCauses.length; j++) {
			serviceItemsInputDTO_18.setChangeCause(changeCauses[j]);
			detailDTO = new PosAcceptDetailDTO();
			detailDTO.setPosNo(posNo);
			detailDTO.setPosObject("3");
			detailDTO.setProcessType("1");
			detailDTO.setObjectValue(clientNo);
			detailDTO.setItemNo("014");
			detailDTO.setNewValue(serviceItemsInputDTO_18.getChangeCause());

			acceptDetailList.add(detailDTO);
		}
        for(String s: serviceItemsInputDTO_18.getChangeCauses()){
        	if (s.equals("2") || s.equals("3")){
        		 jj =true;
        		 break;
        	}
        }
		if (jj) {
				detailDTO = new PosAcceptDetailDTO();
				detailDTO.setPosNo(posNo);
				detailDTO.setPosObject("1");
				detailDTO.setProcessType("1");
				detailDTO.setObjectValue(policyNo);
				detailDTO.setItemNo("004");
				detailDTO.setNewValue(serviceItemsInputDTO_18.getReSignFile());

				acceptDetailList.add(detailDTO);

				detailDTO = new PosAcceptDetailDTO();
				detailDTO.setPosNo(posNo);
				detailDTO.setPosObject("1");
				detailDTO.setProcessType("1");
				detailDTO.setObjectValue(policyNo);
				detailDTO.setItemNo("005");
				detailDTO.setNewValue(serviceItemsInputDTO_18
						.getReplaceSignCause()
						+ serviceItemsInputDTO_18.getReplaceSignCauseOther());

				acceptDetailList.add(detailDTO);
			}
		
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
