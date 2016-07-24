package com.sinolife.pos.common.dto;

import java.io.Serializable;

public class PolicyDividendTransAccountDTO implements Serializable{

	/**
	 * 红利转账授权信息
	 */
	private static final long serialVersionUID = 1L;
	private String policyNo;				//保单号
	private String transfer_grant_flag;     //转账授权标志 Y/N
	private String bank_code;               //银行代码
	private String account_no;               //账号
	private String account_owner;             //户名
	private boolean check;//checkbox选择
	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}
	public String getPolicyNo() {
		return policyNo;
	}
	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}
	public String getTransfer_grant_flag() {
		return transfer_grant_flag;
	}
	public void setTransfer_grant_flag(String transfer_grant_flag) {
		this.transfer_grant_flag = transfer_grant_flag;
	}
	public String getBank_code() {
		return bank_code;
	}
	public void setBank_code(String bank_code) {
		this.bank_code = bank_code;
	}
	public String getAccount_no() {
		return account_no;
	}
	public void setAccount_no(String account_no) {
		this.account_no = account_no;
	}
	public String getAccount_owner() {
		return account_owner;
	}
	public void setAccount_owner(String account_owner) {
		this.account_owner = account_owner;
	}

}
