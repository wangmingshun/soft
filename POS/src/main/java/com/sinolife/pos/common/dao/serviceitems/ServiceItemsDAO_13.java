package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyProductPremDTO;
import com.sinolife.pos.common.dto.PolicyProductPremForMccDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_13;

/**
 * 13 交费频次变更
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Repository("serviceItemsDAO_13")
public class ServiceItemsDAO_13 extends ServiceItemsDAO {

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
		ServiceItemsInputDTO_13 itemDTO = (ServiceItemsInputDTO_13) serviceItemsInputDTO;
		itemDTO.setProductList(queryForList("QUERY_PRODUCT_INFO_INPUT",
				itemDTO.getPolicyNo()));

		Map pMap = new HashMap();
		pMap.put("policyNo", itemDTO.getPolicyNo());
		queryForObject("GET_PREM_FREQUENCY_CONVERT", pMap);
		if ("1".equals(pMap.get("flag"))) {
			throw new RuntimeException("" + pMap.get("message"));
		} else {
			itemDTO.setFrequency((String) pMap.get("premFrequency"));
		}

		return itemDTO;
	}

	public String getFrequencyByPolicyNo(String policyNo) {
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		queryForObject("GET_PREM_FREQUENCY_CONVERT", pMap);
		if ("1".equals(pMap.get("flag"))) {
			throw new RuntimeException("" + pMap.get("message"));
		} else {
			return (String) pMap.get("premFrequency");
		}
	}

	public List<PolicyProductPremForMccDTO> getProductPremListByPolicyNo(
			String policyNo) {
		List<PolicyProductPremForMccDTO> dataList = queryForList(
				"QUERY_PRODUCT_INFO_INPUT_FOEMCC", policyNo);
		return dataList;
	}

	/**
	 * 提供给外围系统
	 */
	public ServiceItemsInputDTO_13 queryServiceItemsInfoExtra_13(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return (ServiceItemsInputDTO_13) queryServiceItemsInfoExtra(serviceItemsInputDTO);
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
		ServiceItemsInputDTO_13 itemDTO = (ServiceItemsInputDTO_13) serviceItemsInputDTO;
		ServiceItemsInputDTO_13 originDTO = (ServiceItemsInputDTO_13) serviceItemsInputDTO
				.getOriginServiceItemsInputDTO();

		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();

		List<PolicyProductPremDTO> originPremList = originDTO.getProductList();
		Double d = new Double(0);
		for (int i = 0; i < originPremList.size(); i++) {
			d += originPremList.get(i).getPeriodPremSum().doubleValue();
		}// 计算所有险种的期缴保费合计,而频率都是一样的，取第一个就是了
		acceptDetailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "1",
				"1", itemDTO.getPolicyNo(), "035", originDTO.getProductList()
						.get(0).getFrequency(), itemDTO.getFrequency()));
		acceptDetailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "1",
				"1", itemDTO.getPolicyNo(), "036", "" + d, itemDTO
						.getPeriodPrem()));

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