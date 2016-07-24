package com.sinolife.pos.common.dto.serviceitems;

import java.util.List;

import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyProductPremDTO;

/**
 * 14 交费年期变更
 */
public class ServiceItemsInputDTO_14 extends ServiceItemsInputDTO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1041721216936435453L;
	
	/**
	 * 险种信息列表
	 */
	private List<PolicyProductPremDTO> productList;
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}

	public List<PolicyProductPremDTO> getProductList() {
		return productList;
	}

	public void setProductList(List<PolicyProductPremDTO> productList) {
		this.productList = productList;
	}

}
