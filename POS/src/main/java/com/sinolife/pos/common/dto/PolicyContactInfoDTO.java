package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.util.Date;

import com.sinolife.sf.ruleengine.pos.bom.dto.POSVerifyResultDto;

/**
 * 保单联系信息DTO
 */
public class PolicyContactInfoDTO implements Serializable {

	private static final long serialVersionUID = -8446918491764735756L;
	
	private String policyNo;		//保单号
	private String client;			//客户号
	private String addressSeq;		//地址序号
	private String addressDesc;		//地址描述
	private String emailSeq;		//EMAIL序号
	private String emailDesc;		//EMAIL地址
	private String phoneSeq;		//电话序号
	private String phoneDesc;		//电话号码
	private String smsService;		//是否选择短信服务
	private String mobilePhoneSeq;   //移动电话序号
	private String mobilePhoneDesc;   //移动电话号码
	private String homePhoneSeq;   //家庭电话序号
	private String homePhoneDesc;   //家庭电话号码
	private String officePhoneSeq;   //办公电话序号
	private String officePhoneDesc;   //办公电话号码
	
	private String branchCode;		//机构代码
	private String branchName;		//机构名称
	
	private String checked;			//页面操作属性，是否选中
	private POSVerifyResultDto verifyResult;//受理规则检查结果
	
	private String postalCode;     //邮编
	
	private Date issueDate;         //承保时间
	
	private  String  dutyStatus;    //保单状态

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public String getDutyStatus() {
		return dutyStatus;
	}

	public void setDutyStatus(String dutyStatus) {
		this.dutyStatus = dutyStatus;
	}
	
	public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    public String getPolicyNo() {
		return policyNo;
	}
	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	public String getAddressSeq() {
		return addressSeq;
	}
	public void setAddressSeq(String addressSeq) {
		this.addressSeq = addressSeq;
	}
	public String getEmailSeq() {
		return emailSeq;
	}
	public void setEmailSeq(String emailSeq) {
		this.emailSeq = emailSeq;
	}
	public String getPhoneSeq() {
		return phoneSeq;
	}
	public void setPhoneSeq(String phoneSeq) {
		this.phoneSeq = phoneSeq;
	}
	public String getSmsService() {
		return smsService;
	}
	public void setSmsService(String smsService) {
		this.smsService = smsService;
	}
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public String getChecked() {
		return checked;
	}
	public void setChecked(String checked) {
		this.checked = checked;
	}
	public String getAddressDesc() {
		return addressDesc;
	}
	public void setAddressDesc(String addressDesc) {
		this.addressDesc = addressDesc;
	}
	public String getEmailDesc() {
		return emailDesc;
	}
	public void setEmailDesc(String emailDesc) {
		this.emailDesc = emailDesc;
	}
	public String getPhoneDesc() {
		return phoneDesc;
	}
	public void setPhoneDesc(String phoneDesc) {
		this.phoneDesc = phoneDesc;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public POSVerifyResultDto getVerifyResult() {
		return verifyResult;
	}
	public void setVerifyResult(POSVerifyResultDto verifyResult) {
		this.verifyResult = verifyResult;
	}
	
	public String getMobilePhoneSeq() {
		return mobilePhoneSeq;
	}
	public void setMobilePhoneSeq(String mobilePhoneSeq) {
		this.mobilePhoneSeq = mobilePhoneSeq;
	}
	public String getMobilePhoneDesc() {
		return mobilePhoneDesc;
	}
	public void setMobilePhoneDesc(String mobilePhoneDesc) {
		this.mobilePhoneDesc = mobilePhoneDesc;
	}
	public String getHomePhoneSeq() {
		return homePhoneSeq;
	}
	public void setHomePhoneSeq(String homePhoneSeq) {
		this.homePhoneSeq = homePhoneSeq;
	}
	public String getHomePhoneDesc() {
		return homePhoneDesc;
	}
	public void setHomePhoneDesc(String homePhoneDesc) {
		this.homePhoneDesc = homePhoneDesc;
	}
	public String getOfficePhoneSeq() {
		return officePhoneSeq;
	}
	public void setOfficePhoneSeq(String officePhoneSeq) {
		this.officePhoneSeq = officePhoneSeq;
	}
	public String getOfficePhoneDesc() {
		return officePhoneDesc;
	}
	public void setOfficePhoneDesc(String officePhoneDesc) {
		this.officePhoneDesc = officePhoneDesc;
	}
}
