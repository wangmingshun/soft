package com.sinolife.pos.common.dto;

import java.io.Serializable;

public class PosAppointmentInfoDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7723331841126776484L;
	
	private boolean isCancle;	//是否取消该保全预约
	private String posNo;		//保全号
	private String serviceItem;	//保全项目
	private String serviceItemDesc;//保全项目描述
	private java.util.Date appointmentDate;//受理日期
	private String acceptUser;	//受理人
	private String premName;	//领款人
	private String bankCode;	//银行代码
	private String bankName;	//银行名称
	private String accountNo;	//银行账户
	
	public boolean getIsCancle() {
		return isCancle;
	}
	public void setIsCancle(boolean isCancle) {
		this.isCancle = isCancle;
	}
	public String getPosNo() {
		return posNo;
	}
	public void setPosNo(String posNo) {
		this.posNo = posNo;
	}
	public String getServiceItem() {
		return serviceItem;
	}
	public void setServiceItem(String serviceItem) {
		this.serviceItem = serviceItem;
	}
	public String getServiceItemDesc() {
		return serviceItemDesc;
	}
	public void setServiceItemDesc(String serviceItemDesc) {
		this.serviceItemDesc = serviceItemDesc;
	}
	public java.util.Date getAppointmentDate() {
		return appointmentDate;
	}
	public void setAppointmentDate(java.util.Date appointmentDate) {
		this.appointmentDate = appointmentDate;
	}
	public String getAcceptUser() {
		return acceptUser;
	}
	public void setAcceptUser(String acceptUser) {
		this.acceptUser = acceptUser;
	}
	public String getPremName() {
		return premName;
	}
	public void setPremName(String premName) {
		this.premName = premName;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
}
