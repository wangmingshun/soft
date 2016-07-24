package com.sinolife.pos.common.dto.serviceitems;

import org.springframework.validation.Errors;


/**
 * 27 保单补发
 */
public class ServiceItemsInputDTO_27 extends ServiceItemsInputDTO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9160581349371707800L;

	/**
	 * 补发原因
	 */
	private String reProvideCause;
	
	/**
	 * 是否收取工本费
	 */
	private String chargeOption;
	
	/**
	 * 保单领取方式
	 */
	private String deliveryType;
	
	/**
	 * 补发原因描述
	 */
	private String reProvideCauseDesc;
	
	
	

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}

	public String getReProvideCause() {
		return reProvideCause;
	}

	public void setReProvideCause(String reProvideCause) {
		this.reProvideCause = reProvideCause;
	}

	public String getChargeOption() {
		return chargeOption;
	}

	public void setChargeOption(String chargeOption) {
		this.chargeOption = chargeOption;
	}

	public String getDeliveryType() {
		return deliveryType;
	}

	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}

	public String getReProvideCauseDesc() {
		return reProvideCauseDesc;
	}

	public void setReProvideCauseDesc(String reProvideCauseDesc) {
		this.reProvideCauseDesc = reProvideCauseDesc;
	}
}
