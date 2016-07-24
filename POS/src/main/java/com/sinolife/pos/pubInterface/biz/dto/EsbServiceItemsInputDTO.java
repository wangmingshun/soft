package com.sinolife.pos.pubInterface.biz.dto;

import java.io.Serializable;

public class EsbServiceItemsInputDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String policyNo; // 保单号
	private String serviceItems; // 服务项目（保全项目）
	private String applyDate; // 申请日期yyyy-mm-dd

	// private String specialFunc; // 特殊件标记
	private String specialRuleType; // 规则特殊件类型
	private String specialRuleReason; // 规则特殊件类型原因

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getServiceItems() {
		return serviceItems;
	}

	public void setServiceItems(String serviceItems) {
		this.serviceItems = serviceItems;
	}

	public String getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}

	/*
	 * public String getSpecialFunc() { return specialFunc; }
	 * 
	 * public void setSpecialFunc(String specialFunc) { this.specialFunc =
	 * specialFunc; }
	 */

	public String getSpecialRuleType() {
		return specialRuleType;
	}

	public void setSpecialRuleType(String specialRuleType) {
		this.specialRuleType = specialRuleType;
	}

	public String getSpecialRuleReason() {
		return specialRuleReason;
	}

	public void setSpecialRuleReason(String specialRuleReason) {
		this.specialRuleReason = specialRuleReason;
	}

}
