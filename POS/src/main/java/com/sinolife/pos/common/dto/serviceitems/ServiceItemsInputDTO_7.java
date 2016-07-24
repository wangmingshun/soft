package com.sinolife.pos.common.dto.serviceitems;

import java.util.List;

import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyProductDTO;



/**
 * 7 减额交清
 */

public class ServiceItemsInputDTO_7 extends ServiceItemsInputDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 401289346071330919L;
	private List<PolicyProductDTO> policyProductList;	//险种信息
	
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
