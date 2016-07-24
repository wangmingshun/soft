package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_33;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;

@Repository("serviceItemsDAO_33")
public class ServiceItemsDAO_33 extends ServiceItemsDAO {

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
		ServiceItemsInputDTO_33 item = (ServiceItemsInputDTO_33) serviceItemsInputDTO;// 没有原值需要查询

		return item;
	}

	// 提供给官网使用
	public ServiceItemsInputDTO queryServiceItemsInfo(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_33 item = (ServiceItemsInputDTO_33) queryServiceItemsInfoExtra(serviceItemsInputDTO);// 没有原值需要查询

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
		ServiceItemsInputDTO_33 serviceItemsInputDTO_33 = (ServiceItemsInputDTO_33) serviceItemsInputDTO;

		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();

		acceptDetailList.add(new PosAcceptDetailDTO(serviceItemsInputDTO_33
				.getPosNo(), "5", "1",
				serviceItemsInputDTO_33.getChangePosNo(), "001", null, null));
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

	/**
	 * 查询被变更的批单收付方式
	 * 
	 * @param pMap
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map queryPremInfoByPosNo(Map pMap) {
		return (Map) queryForObject("QUERY_PREM_POS_POLICY_NO", pMap);
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
		Boolean accountNameModified = false;
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		// 变更的保全号
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "5");
		item.put("itemNo", "001");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(
				bom.getPosNo(), posObjectAndItemNoList);
		if (detailList != null && !detailList.isEmpty()) {
			String changePosNo = detailList.get(0).getObjectValue();
			boolean isFeeFixedSuccess = !commonQueryDAO
					.canAdjustChargingMethod(changePosNo);
			bom.setFeeFixedSuccess(isFeeFixedSuccess);
			String oldPremName = commonQueryDAO.queryPosInfoRecord(changePosNo)
					.getPremName();
			String newPremName = commonQueryDAO.queryPosInfoRecord(
					bom.getPosNo()).getPremName();
			// 账号是否修改
			if (newPremName != null && oldPremName != null) {
				if (!oldPremName.equals(newPremName)) {
					accountNameModified = true;
				}
			}

		}
		bom.setAccountNameModified(accountNameModified);
		super.fillBOMForProcessRuleCheck(bom);
	}

	/**
	 * 根据保单号查询该保单下可以做收付款方式调整失败的所有保全项目
	 * 
	 * @param policyNo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List queryPremInfosByPolicyNo(String policyNo) {
		Map<String,String> pMap = new HashMap<String,String>();
		pMap.put("policyNo", policyNo);
		return (List) queryForList("QUERY_PREM_POS_FAIL_BY_POLICY_NO", pMap);
	}
	
	/**
	 * 根据保全号查询原收付款信息
	 * 
	 * @param posNo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map queryPremInfosByPosNo(String posNo) {
		Map<String,String> pMap = new HashMap<String,String>();
		pMap.put("posNo", posNo);
		return  (Map)queryForObject("QUERY_PREM_POS_BY_POS_NO", pMap);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> queryPayFailPremPosInfoByClientNo(String clientNo) {
		return  (List<Map<String,Object>>)queryForList("queryPayFailPremPosInfoByClientNo", clientNo);
	}
}
