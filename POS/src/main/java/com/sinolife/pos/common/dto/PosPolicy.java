package com.sinolife.pos.common.dto;

import java.io.Serializable;

import org.springframework.format.annotation.DateTimeFormat;

public class PosPolicy implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1463996488056404416L;
	/**
	 * 
	 */
	
	public static final String TABLE_ALIAS = "保单保全信息";
	public static final String ALIAS_POS_NO = "保全号";
	public static final String ALIAS_SERVICE_ITEMS = "保全项目";
	public static final String ALIAS_ACCEPT_DATE = "受理日期";
	public static final String ALIAS_ACCEPT_STATUS_CODE = "受理状态代码";
	public static final String ALIAS_ACCEPT_STATUS_CODE_DESC = "受理状态";
	public static final String ALIAS_POLICY_NO = "保单号";
	public static final String ALIAS_BARCODE_NO= "条形码";

	
	private String posNo;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private java.util.Date acceptDate;
	private String acceptStatusCode;
	private String serviceItems;
	private String serviceItemsDesc;
	private String acceptStatusCodeDesc;
	private String  policyNo;
	private String barCodeNo;
	private String relatedBarCodeNo;
	
	private String eEntFileId;

	public String getPosNo() {
		return posNo;
	}

	public void setPosNo(String posNo) {
		this.posNo = posNo;
	}

	public java.util.Date getAcceptDate() {
		return acceptDate;
	}

	public void setAcceptDate(java.util.Date acceptDate) {
		this.acceptDate = acceptDate;
	}

	public String getAcceptStatusCode() {
		return acceptStatusCode;
	}

	public void setAcceptStatusCode(String acceptStatusCode) {
		this.acceptStatusCode = acceptStatusCode;
	}

	public String getServiceItems() {
		return serviceItems;
	}

	public void setServiceItems(String serviceItems) {
		this.serviceItems = serviceItems;
	}

	public String getServiceItemsDesc() {
		return serviceItemsDesc;
	}

	public void setServiceItemsDesc(String serviceItemsDesc) {
		this.serviceItemsDesc = serviceItemsDesc;
	}

	public String getAcceptStatusCodeDesc() {
		return acceptStatusCodeDesc;
	}

	public void setAcceptStatusCodeDesc(String acceptStatusCodeDesc) {
		this.acceptStatusCodeDesc = acceptStatusCodeDesc;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getBarCodeNo() {
		return barCodeNo;
	}

	public void setBarCodeNo(String barCodeNo) {
		this.barCodeNo = barCodeNo;
	}

	public String getRelatedBarCodeNo() {
		return relatedBarCodeNo;
	}

	public void setRelatedBarCodeNo(String relatedBarCodeNo) {
		this.relatedBarCodeNo = relatedBarCodeNo;
	}

	public String geteEntFileId() {
		return eEntFileId;
	}

	public void seteEntFileId(String eEntFileId) {
		this.eEntFileId = eEntFileId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public static String getTableAlias() {
		return TABLE_ALIAS;
	}

	public static String getAliasPosNo() {
		return ALIAS_POS_NO;
	}

	public static String getAliasServiceItems() {
		return ALIAS_SERVICE_ITEMS;
	}

	public static String getAliasAcceptDate() {
		return ALIAS_ACCEPT_DATE;
	}

	public static String getAliasAcceptStatusCode() {
		return ALIAS_ACCEPT_STATUS_CODE;
	}

	public static String getAliasAcceptStatusCodeDesc() {
		return ALIAS_ACCEPT_STATUS_CODE_DESC;
	}

	public static String getAliasPolicyNo() {
		return ALIAS_POLICY_NO;
	}

	public static String getAliasBarcodeNo() {
		return ALIAS_BARCODE_NO;
	}
}
