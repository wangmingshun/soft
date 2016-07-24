package com.sinolife.pos.pubInterface.biz.dto;

import com.sinolife.pos.common.dto.ClientAccountDTO;

public class EsbServiceItemsInputDTO_23 extends EsbServiceItemsInputDTO  {

	/**
	 * 续期缴费方式变更
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 账户信息
	 */
	private ClientAccountDTO account;
	/**
	 * 保单的投保人
	 */
	private String applicantName;
	/**
	 * 缴费方式
	 */
	private String chargingMethod;
	public ClientAccountDTO getAccount() {
		return account;
	}
	public void setAccount(ClientAccountDTO account) {
		this.account = account;
	}
	public String getApplicantName() {
		return applicantName;
	}
	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}
	public String getChargingMethod() {
		return chargingMethod;
	}
	public void setChargingMethod(String chargingMethod) {
		this.chargingMethod = chargingMethod;
	}
}
