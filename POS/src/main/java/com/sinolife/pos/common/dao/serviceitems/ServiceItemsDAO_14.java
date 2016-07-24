package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyProductPremDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_14;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSUWBOMPlanDto;

/**
 * 14 交费年期变更
 */
@SuppressWarnings({"rawtypes","unchecked"})
@Repository("serviceItemsDAO_14")
public class ServiceItemsDAO_14 extends ServiceItemsDAO {

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#queryServiceItemsInfoExtra(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_14 itemDTO = (ServiceItemsInputDTO_14)serviceItemsInputDTO;
		String sql = ServiceItemsDAO_13.class.getName()+".QUERY_PRODUCT_INFO_INPUT";//和项目13的取数逻辑一致，字段差不多
		itemDTO.setProductList(getSqlMapClientTemplate().queryForList(sql, itemDTO.getPolicyNo()));
		
		String policyNo = itemDTO.getPolicyNo();
		Integer prodOfPrimaryPlan = commonQueryDAO.queryProdSeqOfPrimaryPlanByPolicyNo(policyNo);
		String productCodeOfPrimaryPlan = commonQueryDAO.getProductCodeByPolicyNoAndProdSeq(policyNo, prodOfPrimaryPlan);
		if("CNAN_BR1".equals(productCodeOfPrimaryPlan)) {
			//如变更的保单主险为红玫瑰时，增加受理提醒（在项目明细录入页面弹出提醒）“红玫瑰险种退费必须退入被保人账户，如该项目受理产生退费，请仔细核对退费账户持有人姓名必须与被保人一致”
			itemDTO.setRemindMessage("红玫瑰险种退费必须退入被保人账户，如该项目受理产生退费，请仔细核对退费账户持有人姓名必须与被保人一致");
		}
		
		return itemDTO;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#generateAcceptDetailDTOList(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_14 itemDTO = (ServiceItemsInputDTO_14)serviceItemsInputDTO;
		ServiceItemsInputDTO_14 originDTO = (ServiceItemsInputDTO_14)serviceItemsInputDTO.getOriginServiceItemsInputDTO();
		
		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();
		
		List<PolicyProductPremDTO> productList = itemDTO.getProductList();
		for (int i = 0; productList != null && i < productList.size(); i++) {
			PolicyProductPremDTO product = productList.get(i);
			if(product.isChecked()){
				beginGroupNo++;
				PolicyProductPremDTO origin = getOriginProduct(originDTO.getProductList(),product.getPolicyNo(),product.getProdSeq());
				acceptDetailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(),"2","1",product.getProdSeq(),"001", ""+origin.getPremTerm(), ""+product.getPremTerm(),""+beginGroupNo));
				acceptDetailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(),"2","1",product.getProdSeq(),"002",origin.getPremPeriodType(),product.getPremPeriodType(),""+beginGroupNo));	
			}
		}
		return acceptDetailList;
	}
	/**
	 * 查找
	 * @param list
	 * @param policyNo
	 * @param prodSeq
	 * @return
	 */
	private PolicyProductPremDTO getOriginProduct(List<PolicyProductPremDTO> list, String policyNo,String prodSeq){
		PolicyProductPremDTO product = null;
		for (int i = 0; list!=null && i < list.size(); i++) {
			PolicyProductPremDTO origin = list.get(i);
			if(policyNo.equals(origin.getPolicyNo())&&prodSeq.equals(origin.getProdSeq())){
				product = origin;
				break;
			}
			
		}
		return product;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#validate(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(ServiceItemsInputDTO serviceItemsInputDTO, Errors err) {
		super.validate(serviceItemsInputDTO, err);
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#fillBOMForProcessRuleCheck(com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto)
	 */
	@Override
	public void fillBOMForProcessRuleCheck(POSBOMPolicyDto bom) {
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "2");
		item.put("itemNo", "001");
		posObjectAndItemNoList.add(item);
		item = new HashMap<String, String>();
		item.put("posObject", "2");
		item.put("itemNo", "002");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(bom.getPosNo(), posObjectAndItemNoList);
		if(detailList != null && !detailList.isEmpty()) {
			for(PosAcceptDetailDTO detail : detailList) {
				if("2".equals(detail.getPosObject()) && ("001".equals(detail.getItemNo()) || "002".equals(detail.getItemNo()))) {
					for(POSUWBOMPlanDto plan : bom.getPlansList()) {
						if(plan.getProdSeq() == Integer.parseInt(detail.getObjectValue())) {
							plan.setCurrentPosPlan(true);
							Map pcMap = commonQueryDAO.getProductPremCoverage(bom.getPosNo(), plan.getProductCode(), bom.getPolicyNo(), detail.getObjectValue(), null);
							plan.setLeftPremTerm(Integer.parseInt((String)pcMap.get("leftPremTerm")));
						}
					}
				}
			}
		}
		//变更后的缴费年期对应的缴清日小于等于险种的缴至日
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_pos_no", bom.getPosNo());
		queryForObject("isPremOverDateBeforePayToDate", paraMap);
		if(!"0".equals(paraMap.get("p_flag"))) {
			throw new RuntimeException("判断变更之后的缴费年期对应的缴清日是否小于等于险种的缴至日出错：" + paraMap.get("p_message"));
		}
		boolean isPremOverDateBeforePayToDate = "Y".equals(paraMap.get("p_paid_up_pre_pay_flag"));
		bom.setPremOverDateBeforePayToDate(isPremOverDateBeforePayToDate);
		
		super.fillBOMForProcessRuleCheck(bom);
	}
	
}