package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.util.Date;

public class FinPremDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7991429458873777908L;

	String policyNo;
	// 理财保费类型 1、首期保费 4、追加保费
	String policyPremChgType;
	// 缴至日期
	Date payToDate;
	// 频次
	String frequency;


	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getPolicyPremChgType() {
		return policyPremChgType;
	}

	public void setPolicyPremChgType(String policyPremChgType) {
		this.policyPremChgType = policyPremChgType;
	}

	public Date getPayToDate() {
		return payToDate==null?null:new java.sql.Date(payToDate.getTime());
		
	}

	public void setPayToDate(Date payToDate) {		
		this.payToDate=payToDate;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	
}
