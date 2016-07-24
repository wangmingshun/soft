package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_27;
import com.sinolife.pos.vip.dao.VipManageDAO;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;

@Repository("serviceItemsDAO_27")
public class ServiceItemsDAO_27 extends ServiceItemsDAO {

	@Autowired
	private VipManageDAO vipManageDAO;

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
		ServiceItemsInputDTO_27 itemDTO = (ServiceItemsInputDTO_27) serviceItemsInputDTO;

		// 查询客户是否为VIP
		Map<String, Object> retMap = vipManageDAO
				.getClientVipGradeByClientNo(itemDTO.getClientNo());

		if ("0".equals(retMap.get("p_flag"))) {
			String vipGrade = (String) retMap.get("p_vip_grade");
			// String vipDesc = (String) retMap.get("p_vip_desc");
			itemDTO.setVipGrade(vipGrade);
		}

		return itemDTO;

	}

	/**
	 * 提供给外围系统
	 */
	public ServiceItemsInputDTO_27 queryServiceItemsInfoExtra_27(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return (ServiceItemsInputDTO_27) queryServiceItemsInfoExtra(serviceItemsInputDTO);
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
		ServiceItemsInputDTO_27 itemDTO = (ServiceItemsInputDTO_27) serviceItemsInputDTO;

		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();

		acceptDetailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "1",
				"1", itemDTO.getPolicyNo(), "020", null, itemDTO
						.getReProvideCause()));
		acceptDetailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "1",
				"1", itemDTO.getPolicyNo(), "021", null, itemDTO
						.getChargeOption()));
		acceptDetailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "1",
				"1", itemDTO.getPolicyNo(), "022", null, itemDTO
						.getDeliveryType()));
		acceptDetailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "1",
				"1", itemDTO.getPolicyNo(), "023", null, itemDTO
						.getReProvideCauseDesc()));

		acceptDetailList
				.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "1", "1",
						itemDTO.getPolicyNo(), "149", null, itemDTO
								.getVipGrade()));
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#
	 * fillBOMForProcessRuleCheck
	 * (com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto)
	 */
	@Override
	public void fillBOMForProcessRuleCheck(POSBOMPolicyDto bom) {
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		// 补发原因
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "020");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(
				bom.getPosNo(), posObjectAndItemNoList);
		if (detailList != null && !detailList.isEmpty()) {
			for (PosAcceptDetailDTO detail : detailList) {
				if ("1".equals(detail.getPosObject())
						&& "020".equals(detail.getItemNo())) {
					// 补发原因
					bom.setReIssueApplicantType(detail.getNewValue());
				}
			}
		}
		super.fillBOMForProcessRuleCheck(bom);
	}

}
