package com.sinolife.pos.acceptance.branch.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 机构受理-逐单受理-客户定位查询条件DTO
 */

public class ClientLocateCriteriaDTO implements Serializable {

	private static final long serialVersionUID = -6346805057782461439L;
	private String clientNo;			//客户号
	private String policyNo;			//保单号
	private String applicantOrInsured;	//投被保人标志
	private String idTypeCode;			//证件类型代码
	private String idNo;				//证件号码
	private String clientName;			//客户姓名
	private String queryType;			//查询类型
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthday;				//生日
	private String sex;					//性别
	private String phoneNo;				//电话号码

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
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

	public String getApplicantOrInsured() {
		return applicantOrInsured;
	}

	public void setApplicantOrInsured(String applicantOrInsured) {
		this.applicantOrInsured = applicantOrInsured;
	}

	public String getIdTypeCode() {
		return idTypeCode;
	}

	public void setIdTypeCode(String clientIdTypeCode) {
		this.idTypeCode = clientIdTypeCode;
	}

	public String getClientIdNo() {
		return idNo;
	}

	public void setClientIdNo(String clientIdNo) {
		this.idNo = clientIdNo;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

}
