package com.sinolife.pos.common.dto.serviceitems;

import org.springframework.validation.Errors;

public class ServiceItemsInputDTO_43 extends ServiceItemsInputDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1253180232751197338L;
	
	//权限标志(1表示初级，2表示高级)
	private String privsFlag;
	
	

	public String getPrivsFlag() {
		return privsFlag;
	}



	public void setPrivsFlag(String privsFlag) {
		this.privsFlag = privsFlag;
	}



	@Override
	public void validate(Errors err) {
		super.validate(err);
	}
}
