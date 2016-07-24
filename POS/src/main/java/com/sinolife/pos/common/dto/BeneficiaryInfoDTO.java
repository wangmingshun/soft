package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 受益人表DTO
 */
/**
 * @author hanguang.yang
 *
 */
@Entity
public class BeneficiaryInfoDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8526272013999466820L;
	private String beneficiaryNo;		//受益人编号
	private String clientNo;			//客户号
	private String sexCode;				//性别代码
	private String idType;				//证件类型代码 
	private String beneficiaryName;		//受益人姓名
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthdate;				//受益人生日
	private String idno;				//受益人证件号码
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date idValidDate;			//证件有效日期
	private boolean longIdDate;	 //是否长期有效
	private String isLongidvarDate;
	private String zip;					//受益人邮编
	private String address;				//受益人住址
	private String phone;				//受益人电话
	private String mobile;				//移动电话
	private String email;				//电子邮件
	private String pkSerial;			//数据主键
	private String countryCode;			//国家代号 edit by wangmingshun

	/* 非表字段属性 */
	private String sexDesc;				//性别描述
	private String idTypeDesc;			//证件类型描述
	
	/* 构造函数  */

	public BeneficiaryInfoDTO() {
	}

	public BeneficiaryInfoDTO(String beneficiaryNo) {
		this.beneficiaryNo = beneficiaryNo;
	}

	/* 属性存取 */
	
	public String getBeneficiaryNo() {
		return this.beneficiaryNo;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public void setBeneficiaryNo(String value) {
		this.beneficiaryNo = value;
	}
	public String getClientNo() {
		return this.clientNo;
	}
	public void setClientNo(String value) {
		this.clientNo = value;
	}
	public String getSexCode() {
		return this.sexCode;
	}
	public void setSexCode(String value) {
		this.sexCode = value;
	}
	public String getIdType() {
		return this.idType;
	}
	public void setIdType(String value) {
		this.idType = value;
	}
	public String getBeneficiaryName() {
		return this.beneficiaryName;
	}
	public void setBeneficiaryName(String value) {
		this.beneficiaryName = value;
	}
	public Date getBirthdate() {
		return this.birthdate;
	}
	public void setBirthdate(Date value) {
		this.birthdate = value;
	}
	public String getIdno() {
		return this.idno;
	}
	public void setIdno(String value) {
		this.idno = value;
	}
	public Date getIdValidDate() {
		return this.idValidDate;
	}
	public void setIdValidDate(Date value) {
		this.idValidDate = value;
	}
	public String getZip() {
		return this.zip;
	}
	public void setZip(String value) {
		this.zip = value;
	}
	public String getAddress() {
		return this.address;
	}
	public void setAddress(String value) {
		this.address = value;
	}
	public String getPhone() {
		return this.phone;
	}
	public void setPhone(String value) {
		this.phone = value;
	}
	public String getMobile() {
		return this.mobile;
	}
	public void setMobile(String value) {
		this.mobile = value;
	}
	public String getEmail() {
		return this.email;
	}
	public void setEmail(String value) {
		this.email = value;
	}
	public String getPkSerial() {
		return this.pkSerial;
	}
	public void setPkSerial(String value) {
		this.pkSerial = value;
	}
	public String getSexDesc() {
		return sexDesc;
	}
	public void setSexDesc(String sexDesc) {
		this.sexDesc = sexDesc;
	}
	public String getIdTypeDesc() {
		return idTypeDesc;
	}
	public void setIdTypeDesc(String idTypeDesc) {
		this.idTypeDesc = idTypeDesc;
	}



	public boolean getLongIdDate() {
		return longIdDate;
	}

	public void setLongIdDate(boolean longIdDate) {
		this.longIdDate = longIdDate;
	}

	public String getIsLongidvarDate() {
		if ("Y".equals(isLongidvarDate)){
			longIdDate=true;
		}
		return isLongidvarDate;
	}

	public void setIsLongidvarDate(String isLongidvarDate) {
		this.isLongidvarDate = isLongidvarDate;
	}






}
