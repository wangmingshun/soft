package com.sinolife.pos.common.dto.serviceitems;

import org.springframework.validation.Errors;

public class ServiceItemsInputDTO_44 extends ServiceItemsInputDTO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3068206336953212789L;
	
	private String changeableProduct;

	public String getChangeableProduct() {
		return changeableProduct;
	}

	public void setChangeableProduct(String changeableProduct) {
		this.changeableProduct = changeableProduct;
	}

	@Override
	public void validate(Errors err) {
		super.validate(err);
	}
}
