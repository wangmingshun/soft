package com.sinolife.pos.common.dto;

import java.io.Serializable;

import javax.persistence.Entity;

/**
 * 银行信息
 */

@Entity
public class BankInfoDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8299532887865362412L;
	private String bankCode;		//银行代码
	private String description;		//描述
	private String bankAbbr;		//银行简称
	private String bankCategory;	//银行大类编码
	private String countryCode;		//国家代码
	private String provinceCode;	//省代码
	private String cityCode;		//市代码
	
	/* 构造函数 */

	public BankInfoDTO() {
	}

	public BankInfoDTO(String bankCode) {
		this.bankCode = bankCode;
	}

	/* 属性存取 */

	public String getBankCode() {
		return this.bankCode;
	}

	public void setBankCode(String value) {
		this.bankCode = value;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String value) {
		this.description = value;
	}

	public String getBankAbbr() {
		return this.bankAbbr;
	}

	public void setBankAbbr(String value) {
		this.bankAbbr = value;
	}

	public String getBankCategory() {
		return this.bankCategory;
	}

	public void setBankCategory(String value) {
		this.bankCategory = value;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	

}
