package com.sinolife.pos.pubInterface.biz.dto;



public class EsbServiceItemsInputDTO_9 extends EsbServiceItemsInputDTO {

	/**
	 * 保单还款
	 */
	private static final long serialVersionUID = 1L;
	
	private String returnPayType;          //还款类型    all-全额还款  part-部分还款
	
	public String getReturnPayType() {
		return returnPayType;
	}
	public void setReturnPayType(String returnPayType) {
		this.returnPayType = returnPayType;
	}
	
}
