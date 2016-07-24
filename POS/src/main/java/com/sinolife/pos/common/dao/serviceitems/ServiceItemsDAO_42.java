package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_42;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;

/**
 * 11 红利领取
 */
@Repository("serviceItemsDAO_42")
public class ServiceItemsDAO_42 extends ServiceItemsDAO {

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
		ServiceItemsInputDTO_42 itemDTO = (ServiceItemsInputDTO_42) serviceItemsInputDTO;

		Map map = (Map) queryForObject("QUERY_EFFECTDATE",
				serviceItemsInputDTO.getPolicyNo());
		if (map != null) {

			if (!"".equals(map.get("MATURITY_DATE"))
					&& null != map.get("MATURITY_DATE")) {

				itemDTO.setOldMaturityDate(map.get("MATURITY_DATE").toString());
			}

			if (!"".equals(map.get("EFFECT_DATE"))
					&& null != map.get("EFFECT_DATE")) {

				itemDTO.setOldEffectDate(map.get("EFFECT_DATE").toString());
			}

		}

		return itemDTO;
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
		ServiceItemsInputDTO_42 itemDTO = (ServiceItemsInputDTO_42) serviceItemsInputDTO;

		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();
		acceptDetailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "1",
				"2", itemDTO.getPolicyNo(), "147", itemDTO.getOldEffectDate(),
				itemDTO.getNewEffectDate()));
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

	@Override
	public void fillBOMForProcessRuleCheck(POSBOMPolicyDto bom) {

		// 判断保单新生效日是否有效
		boolean effectIsCorrect = posRulesDAO.effectIsCorrect(bom.getPosNo(),
				bom.getPolicyNo());
		bom.setEffectDateIsCorrect(effectIsCorrect);
		super.fillBOMForProcessRuleCheck(bom);
	}
}
