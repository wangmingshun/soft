package com.sinolife.pos.common.dto.serviceitems;

import java.util.List;

import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyProductCoverageDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;



/**
 * 3 加保
 */

public class ServiceItemsInputDTO_3 extends ServiceItemsInputDTO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5554783939344953393L;
	private List<PolicyProductDTO> policyProductList;	//产品信息列表
	private List<PolicyProductCoverageDTO> policyProductCoverageList;//保单险种责任信息列表
	

	public List<PolicyProductCoverageDTO> getPolicyProductCoverageList() {
		return policyProductCoverageList;
	}

	public void setPolicyProductCoverageList(
			List<PolicyProductCoverageDTO> policyProductCoverageList) {
		this.policyProductCoverageList = policyProductCoverageList;
	}

	@Override
	protected void onPropertyChange() {
		ServiceItemsInputDTO_3 origin = (ServiceItemsInputDTO_3) getOriginServiceItemsInputDTO();
		List<PolicyProductDTO> originPolicyProductList = null;
		PolicyProductDTO originPlan = null;
		if(origin != null)
			originPolicyProductList = origin.getPolicyProductList();
		
		if(policyProductList != null && !policyProductList.isEmpty()) {
			for(int i = 0; i < policyProductList.size(); i++) {
				PolicyProductDTO pp = policyProductList.get(i);
				if("0".equals(pp.getCalType()) || "1".equals(pp.getCalType()) || "Y".equals(pp.getFinancialFlag())) {
					//当险种费率的计算方式为 1-保额算保费 或 为投连万能险种，则只允许录入变更后保额
					pp.setNewAnnStandardPrem(null);
					if(originPolicyProductList != null) {
						originPlan = originPolicyProductList.get(i);
						originPlan.setNewAnnStandardPrem(null);
						originPlan.setNewBaseSumIns(pp.getBaseSumIns());
					}
				} else if("2".equals(pp.getCalType()) && !"Y".equals(pp.getFinancialFlag())) {
					//当险种费率的计算方式为 2-保费算保额 且 不为投连万能险种，则只允许录入变更后保费
					pp.setNewBaseSumIns(null);
					if(originPolicyProductList != null) {
						originPlan = originPolicyProductList.get(i);
						if(originPlan.getPremInfo() != null) {
							originPlan.setNewBaseSumIns(null);
							originPlan.setNewAnnStandardPrem(originPlan.getPremInfo().getAnnStandardPrem());
						}
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}
	
	public List<PolicyProductDTO> getPolicyProductList() {
		return policyProductList;
	}

	public void setPolicyProductList(List<PolicyProductDTO> policyProductList) {
		this.policyProductList = policyProductList;
	}
}
