package com.sinolife.pos.common.dto.serviceitems;

import java.util.List;

import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyProductCoverageDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;

/**
 * 4 减保
 */
public class ServiceItemsInputDTO_4 extends ServiceItemsInputDTO {

	private static final long serialVersionUID = 8050041784100211412L;

	private List<PolicyProductDTO> productList;		//产品信息列表
	
	
	private List<PolicyProductCoverageDTO> policyProductCoverageList;//保单险种责任信息列表
	
	
	public List<PolicyProductCoverageDTO> getPolicyProductCoverageList() {
		return policyProductCoverageList;
	}

	public void setPolicyProductCoverageList(
			List<PolicyProductCoverageDTO> policyProductCoverageList) {
		this.policyProductCoverageList = policyProductCoverageList;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}

	public List<PolicyProductDTO> getProductList() {
		return productList;
	}

	public void setProductList(List<PolicyProductDTO> productList) {
		this.productList = productList;
	}
	
}
