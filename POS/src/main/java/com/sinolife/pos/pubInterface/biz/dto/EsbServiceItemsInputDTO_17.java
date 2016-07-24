package com.sinolife.pos.pubInterface.biz.dto;

import java.math.BigDecimal;

public class EsbServiceItemsInputDTO_17 extends EsbServiceItemsInputDTO {

	/**
	 * 续期保费退费
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal refundAmount;	//退费金额（续期保费退费传）
	private String refundCauseCode;		//退费原因代码（续期保费退费传）
	public BigDecimal getRefundAmount() {
		return refundAmount;
	}
	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}
	public String getRefundCauseCode() {
		return refundCauseCode;
	}
	public void setRefundCauseCode(String refundCauseCode) {
		this.refundCauseCode = refundCauseCode;
	}
}
