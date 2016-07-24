package com.sinolife.pos.pubInterface.biz.dto;

public class EsbServiceItemsInputDTO_28 extends EsbServiceItemsInputDTO {
	
	/**
	 * 保单挂失录入信息
	 */
	private static final long serialVersionUID = 1L;
	private String isLoss;  //挂失状态
	public String getIsLoss() {
		return isLoss;
	}
	public void setIsLoss(String isLoss) {
		this.isLoss = isLoss;
	}
}
