package com.sinolife.pos.common.dto.serviceitems;

import java.util.List;

import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyProductDTO;


/**
 * 29 预约终止附加险
 */

public class ServiceItemsInputDTO_29 extends ServiceItemsInputDTO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4982730255889024417L;
	private List<PolicyProductDTO> productList;	//产品信息列表
	
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
