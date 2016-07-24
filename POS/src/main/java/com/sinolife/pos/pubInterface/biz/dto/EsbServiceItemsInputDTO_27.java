package com.sinolife.pos.pubInterface.biz.dto;

public class EsbServiceItemsInputDTO_27 extends EsbServiceItemsInputDTO {

	/**
	 * 27 保单补发
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 补发原因---投保人补发:1,代理人补发:2,污损换发:3,公司补发:4
	 */
	private String reProvideCause;

	/**
	 * 是否收取工本费---收取工本费:1,免收工本费:2
	 */
	private String chargeOption;

	/**
	 * 保单领取方式 ---邮寄:1,自行领取（自领、代领）:2
	 */
	private String deliveryType;

	/**
	 * 补发原因描述
	 */
	private String reProvideCauseDesc;

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
