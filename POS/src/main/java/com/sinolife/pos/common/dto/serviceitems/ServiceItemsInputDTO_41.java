package com.sinolife.pos.common.dto.serviceitems;

import java.util.List;

import com.sinolife.pos.common.dto.PolicyProductDTO;


public class ServiceItemsInputDTO_41  extends ServiceItemsInputDTO {

	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4545790662692364346L;
	/**
	 * 险种信息
	 */
	private List<PolicyProductDTO> productList;
	
	public List<PolicyProductDTO> getProductList() {
		return productList;
	}

	public void setProductList(List<PolicyProductDTO> productList) {
		this.productList = productList;
	}
	
}
