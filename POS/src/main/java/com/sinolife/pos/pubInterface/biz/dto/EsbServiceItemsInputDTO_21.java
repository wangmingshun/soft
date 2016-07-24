package com.sinolife.pos.pubInterface.biz.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;


public class EsbServiceItemsInputDTO_21 extends EsbServiceItemsInputDTO {

	/**
	 * //客户资料变更
	 */
	private static final long serialVersionUID = 1L;
	private String clientType;//投被保人标志       I：被保人   A:投保人
	private String countryCode;				//国籍代码
	private String countryDesc;				//国籍描述
	private String marriageCode;			//婚姻状况代码
	private String marriageDesc;			//婚姻状况描述
	private String idTypeCode;				//证件类型代码
	private String idTypeDesc;				//证件类型描述
	private String idNo;					//证件号码
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date idnoValidityDate;			//证件有效截止日期
	public Date getIdnoValidityDate() {
		return idnoValidityDate;
	}
	public void setIdnoValidityDate(Date idnoValidityDate) {
		this.idnoValidityDate = idnoValidityDate;
	}
	public String getClientType() {
		return clientType;
	}
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getCountryDesc() {
		return countryDesc;
	}
	public void setCountryDesc(String countryDesc) {
		this.countryDesc = countryDesc;
	}
	public String getMarriageCode() {
		return marriageCode;
	}
	public void setMarriageCode(String marriageCode) {
		this.marriageCode = marriageCode;
	}
	public String getMarriageDesc() {
		return marriageDesc;
	}
	public void setMarriageDesc(String marriageDesc) {
		this.marriageDesc = marriageDesc;
	}
	public String getIdTypeCode() {
		return idTypeCode;
	}
	public void setIdTypeCode(String idTypeCode) {
		this.idTypeCode = idTypeCode;
	}
	public String getIdTypeDesc() {
		return idTypeDesc;
	}
	public void setIdTypeDesc(String idTypeDesc) {
		this.idTypeDesc = idTypeDesc;
	}
	public String getIdNo() {
		return idNo;
	}
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}
}
