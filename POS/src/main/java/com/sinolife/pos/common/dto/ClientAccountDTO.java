package com.sinolife.pos.common.dto;

import java.io.Serializable;

/**
 * 客户账号DTO
 */
public class ClientAccountDTO implements Serializable {

	private static final long serialVersionUID = -966285056674701L;
	
	private String accountSeq;		//银行账号序号
	private String bankCode;		//银行代码
	private String bankName;		//银行名称
	private String accountNoType;   //账号类型	
	private String clientNo;		//客户号
	private String clientAccount;	//客户账号
	private String accountNo;		//账号
	private String accountStatus;	//账户状态
	
	public String getAccountSeq() {
		return accountSeq;
	}
	public void setAccountSeq(String accountSeq) {
		this.accountSeq = accountSeq;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getClientNo() {
		return clientNo;
	}
	public void setClientNo(String clientNo) {
		this.clientNo = clientNo;
	}
	public String getClientAccount() {
		return clientAccount;
	}
	public void setClientAccount(String clientAccount) {
		this.clientAccount = clientAccount;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getAccountStatus() {
		return accountStatus;
	}
	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getAccountNoType() {
		return accountNoType;
	}
	public void setAccountNoType(String accountNoType) {
		this.accountNoType = accountNoType;
	}
}
