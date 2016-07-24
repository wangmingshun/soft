package com.sinolife.pos.acceptance.trial.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;


public class TrialInfoDTO implements Serializable {
	private static final long serialVersionUID = -9142464695218680211L;
	private String clientNo;
	private String policyNo;
	private String serviceItems;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date applyDate;
	
	private String acceptor;
	
	public String getAcceptor() {
		return acceptor;
	}
	public void setAcceptor(String acceptor) {
		this.acceptor = acceptor;
	}
	public String getClientNo() {
		return clientNo;
	}
	public void setClientNo(String clientNo) {
		this.clientNo = clientNo;
	}
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
	public Date getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}
	
}
