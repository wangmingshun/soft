package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.PersonalNoticeDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PolicyProductPremDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_15;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMInsuredDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMOwnerDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPlanDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPolicyDto;

/**
 * 15 职业等级变更
 */
@SuppressWarnings({"rawtypes","unchecked"})
@Repository("serviceItemsDAO_15")
public class ServiceItemsDAO_15 extends ServiceItemsDAO {

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#queryServiceItemsInfoExtra(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_15 itemDTO = (ServiceItemsInputDTO_15)serviceItemsInputDTO;
		itemDTO.setClient((ClientInformationDTO)queryForObject("QUERY_CLIENT_OCCUPATION_INFO", itemDTO.getClientNo()));
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("clientNo",  itemDTO.getClientNo());
		map.put("policyNo",itemDTO.getPolicyNo());
		
		List<PolicyProductPremDTO> policyList = queryForList("QUERY_PRODUCT_PREM_INFO", map);
		if(policyList != null && !policyList.isEmpty()) {
			for(PolicyProductPremDTO policy : policyList) {
				if(!itemDTO.getPolicyNo().equals(policy.getPolicyNo())) {
					policy.setVerifyResult(acceptRuleCheck(itemDTO, policy.getPolicyNo()));
				}
			}
		}
		itemDTO.setProductPremList(policyList);
		
		//职业变更可选择变更客户对象
		itemDTO.setClientSelectEnabled(true);
		
		return itemDTO;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#generateAcceptDetailDTOList(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_15 itemDTO = (ServiceItemsInputDTO_15)serviceItemsInputDTO;
		ClientInformationDTO origin = ((ServiceItemsInputDTO_15)serviceItemsInputDTO.getOriginServiceItemsInputDTO()).getClient();

		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();
		
		List<PosAcceptDetailDTO> clientDetail = new ArrayList<PosAcceptDetailDTO>();
		clientDetail.add(new PosAcceptDetailDTO(itemDTO.getPosNo(),"3","1",itemDTO.getClientNo(),"043",origin.getOccupationCode(),itemDTO.getClient().getOccupationCode()));
		clientDetail.add(new PosAcceptDetailDTO(itemDTO.getPosNo(),"3","1",itemDTO.getClientNo(),"044",origin.getWorkUnit(),itemDTO.getClient().getWorkUnit()));
		clientDetail.add(new PosAcceptDetailDTO(itemDTO.getPosNo(),"3","1",itemDTO.getClientNo(),"045",null,itemDTO.getWorkStartDate()));
		clientDetail.add(new PosAcceptDetailDTO(itemDTO.getPosNo(),"3","1",itemDTO.getClientNo(),"046",origin.getPosition(),itemDTO.getClient().getPosition()));
		
		List<PolicyProductPremDTO> productPremList = itemDTO.getProductPremList();
		//家庭单多被保险人需求：删除保单号相同，同时选中的，保单避免重复 add by zhangyi
		if(productPremList!=null && productPremList.size()>1){
			for(int i = productPremList.size() - 1; i > 0; i--) {
				PolicyProductPremDTO productPrem1 = productPremList.get(i);
			    for(int j = i - 1; j >= 0; j--) {
			    	PolicyProductPremDTO productPrem2 = productPremList.get(j);
			    	String policyNo1 = productPrem1.getPolicyNo();
			    	String policyNo2 = productPrem2.getPolicyNo();
			    	if(policyNo1.equals(policyNo2)&&productPrem1.isChecked()&&productPrem2.isChecked()){
			    		productPremList.remove(i);
			    		break;
			    	}
			    }
			}
		}
		for (int i = 0; productPremList!=null && i < productPremList.size(); i++) {
			PolicyProductPremDTO productPrem = productPremList.get(i);
			if(productPrem.isChecked()){
				String policyNo = productPrem.getPolicyNo();
				String posNo = insertPosInfoRecord(itemDTO, policyNo);

				acceptDetailList.add(new PosAcceptDetailDTO(posNo,"2","1",policyNo,"005",productPrem.getOccupationCode(),itemDTO.getClient().getOccupationCode()));
				
				acceptDetailList.addAll(replaceDetailListPosNo(clientDetail, posNo));
				if(!itemDTO.getPolicyNo().equals(policyNo)){
					//当且仅当是新的保全号(即不是第一个保单)，还有公共部分的明细
					acceptDetailList.addAll(replaceDetailListPosNo(getPubDetailList(), posNo));
				}
			}
		}
		
		return acceptDetailList;
	}
	
	/**
	 * 查询职业代码对应的信息
	 * @param occupationCode
	 * @return
	 */
	public Map queryOccupationInfo(String occupationCode){
		return (Map)queryForObject("QUERY_OCCUPATION_INFO", occupationCode);
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#validate(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(ServiceItemsInputDTO serviceItemsInputDTO, Errors err) {
		super.validate(serviceItemsInputDTO, err);
		if(serviceItemsInputDTO instanceof ServiceItemsInputDTO_15) {
			ServiceItemsInputDTO_15 item = (ServiceItemsInputDTO_15)serviceItemsInputDTO;
			
			List<PolicyProductPremDTO> productList = item.getProductPremList();
			//家庭单多被保险人需求：删除保单号号相同，同时选中的，保单避免重复 add by zhangyi
			if(productList!=null && productList.size()>1){
				for(int i = productList.size() - 1; i > 0; i--) {
					PolicyProductPremDTO productPrem1 = productList.get(i);
				    for(int j = i - 1; j >= 0; j--) {
				    	PolicyProductPremDTO productPrem2 = productList.get(j);
				    	String policyNo1 = productPrem1.getPolicyNo();
				    	String policyNo2 = productPrem2.getPolicyNo();
				    	if(policyNo1.equals(policyNo2)&&productPrem1.isChecked()&&productPrem2.isChecked()){
				    		productList.remove(i);
				    		break;
				    	}
				    }
				}
			}
			for (int i = 0; productList!=null && i < productList.size(); i++) {
				PolicyProductPremDTO product = productList.get(i);
				if(product.isChecked()){
					Map pMap = new HashMap();
					pMap.put("policyNo", product.getPolicyNo());
					pMap.put("clientNo", item.getClientNo());
					pMap.put("occupationCode", item.getClient().getOccupationCode());
					
					queryForObject("PROC_ITEM_15_RULECHECK", pMap);
					
					if("1".equals(pMap.get("flag"))){
						throw new RuntimeException(""+pMap.get("message"));
					}
					if("N".equals(pMap.get("processAble"))){
						err.reject("policyNo", ""+pMap.get("processMessage"));
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#fillBOMForUnwrtRuleCheck(com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPolicyDto, java.lang.String, com.sinolife.pos.common.dto.PersonalNoticeDTO, com.sinolife.pos.common.dto.PersonalNoticeDTO)
	 */
	@Override
	public void fillBOMForUnwrtRuleCheck(SUWBOMPolicyDto bom, String posNo, PersonalNoticeDTO appPersonalNotice, PersonalNoticeDTO insPersonalNotice) {
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		Map<String, String> item = new HashMap<String, String>();
		//职业代码
		item.put("posObject", "3");
		item.put("itemNo", "043");
		posObjectAndItemNoList.add(item);
		//职位
		item = new HashMap<String, String>();
		item.put("posObject", "3");
		item.put("itemNo", "046");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(posNo, posObjectAndItemNoList);
		if(detailList != null && !detailList.isEmpty()) {
			for(PosAcceptDetailDTO detail : detailList) {
				String clientNo = detail.getObjectValue();
				SUWBOMOwnerDto owner = null;
				SUWBOMInsuredDto insured = null;
				if(bom.getOwner().getOwnerID().equals(clientNo)) {
					owner = bom.getOwner();
					owner.setIsCurrentPosObj(true);
				}
				if(bom.getInsureds() != null) {
					for(SUWBOMInsuredDto tmp : bom.getInsureds()) {
						if(tmp.getInsuredID().equals(clientNo)) {
							insured = tmp;
							insured.setIsCurrentPosObj(true);
						}
					}
				}
				if("3".equals(detail.getPosObject()) && "043".equals(detail.getItemNo())) {
					String occuCode = detail.getNewValue();
					String occuGrade = posRulesDAO.getOccupationGradeByCode(occuCode);
					if(owner != null) {
						owner.setOccuCode(occuCode);
						owner.setOccuGrade(occuGrade);
					}
					if(insured != null) {
						insured.setOccuCode(occuCode);
						insured.setOccuGrade(occuGrade);
						//被保人变更，则所有险种如果被保人为该被保人的，险种信息一并变更
						for(SUWBOMPlanDto plan : bom.getCurrentPlans()) {
							PolicyProductDTO pp = commonQueryDAO.queryPolicyProductByPolicyNoAndProdSeq(bom.getPolicyID(), plan.getProdSeq());
							if(pp != null && pp.getInsuredNo().equals(insured.getInsuredID())) {
								plan.setOccuGrade(occuGrade);
							}
						}
					}
				} else if("3".equals(detail.getPosObject()) && "046".equals(detail.getItemNo())) {
					String occuContent = detail.getNewValue();
					if(owner != null) {
						owner.setOccuContent(occuContent);
					}
					if(insured != null) {
						insured.setOccuContent(occuContent);
					}
				}
			}
		}
	}
	
}