package com.sinolife.pos.common.dto.serviceitems;

import java.math.BigDecimal;

import org.springframework.validation.Errors;



/**
 * 17 续期保费退费
 */

public class ServiceItemsInputDTO_17 extends ServiceItemsInputDTO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6056948876737977736L;
	private BigDecimal refundAmount;	//退费金额
	private BigDecimal periodPrem;		//期缴保费
	private String refundCauseCode;		//退费原因代码
	private BigDecimal policyBalance;	//保单余额
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}

	public BigDecimal getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}

	public BigDecimal getPeriodPrem() {
		return periodPrem;
	}

	public void setPeriodPrem(BigDecimal periodPrem) {
		this.periodPrem = periodPrem;
	}

	public BigDecimal getPolicyBalance() {
		return policyBalance;
	}

	public void setPolicyBalance(BigDecimal policyBalance) {
		this.policyBalance = policyBalance;
	}

	public String getRefundCauseCode() {
		return refundCauseCode;
	}

	public void setRefundCauseCode(String refundCauseCode) {
		this.refundCauseCode = refundCauseCode;
	}
	
}
