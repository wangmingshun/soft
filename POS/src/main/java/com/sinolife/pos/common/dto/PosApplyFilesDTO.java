package com.sinolife.pos.common.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;

import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 保全申请书
 */
@Entity
public class PosApplyFilesDTO extends PosComDTO {
	private static final long serialVersionUID = 5454155825314635342L;

	/* 表结构属性 */
	private String barcodeNo;				//条形码
	private String posBatchNo;				//批次号
	private String relateBarcode;			//关联条形码
	private Long filePages;					//资料页数
	private String signAccordFlag;			//签名不符标志
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date applyDate;					//申请日期
	private String paymentType;				//收付方式
	private String transferAccountno;		//转账账号
	private String bankCode;				//银行代码
	private String transferAccountOwner;	//户主姓名
	private String foreignExchangeType;		//外币账户类型
	private String accountNoType;         //银行卡类型
	private String privateFlag;				//对公对私标记
	public String getAccountNoType() {
		return accountNoType;
	}

	public void setAccountNoType(String accountNoType) {
		this.accountNoType = accountNoType;
	}

	private String remark;					//备注
	private String pkSerial;				//数据主键
	private String approvalServiceType;		//批文送达方式
	private String zip;						//批文送达邮编
	private String address;					//批文送达地址
	
	/* 非表结构属性 */
	private List<PosApplyFileMaterialsDTO> applyFileMaterialList = new ArrayList<PosApplyFileMaterialsDTO>();
	private String bankBranchCode;
	private List<BankInfoDTO> bankList = new ArrayList<BankInfoDTO>();
	private String transferAccountOwnerIdType;	// 收款人证件类型
	private String transferAccountOwnerIdNo;	// 收款人证件号码
	private String serviceItems;				//当前选定的保全项目列表
	private String policyNo;					//申请保单号
	private String defaultZip;					//申请人的联系地址邮编
	private String defaultAddress;				//申请人的联系地址
	private String approvalServiceTypeDesc;		//批文送达方式描述

	
	/* 构造函数  */
	
	public String getPrivateFlag() {
		return privateFlag;
	}

	public void setPrivateFlag(String privateFlag) {
		this.privateFlag = privateFlag;
	}

	public PosApplyFilesDTO() {
	}

	public PosApplyFilesDTO(String barcodeNo) {
		this.barcodeNo = barcodeNo;
	}

	/* 特殊存取 */
	
	/**
	 * 获取当前选定的申请材料字符串
	 * 
	 * @return
	 */
	public String getApplyFileMaterials() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < applyFileMaterialList.size(); i++) {
			PosApplyFileMaterialsDTO posInfo = applyFileMaterialList.get(i);
			if (sb.length() > 0)
				sb.append(",");
			sb.append(posInfo.getApplyMaterialCode());
		}
		return sb.toString();
	}

	/**
	 * 设置申请材料
	 * 
	 * @param posTypes
	 */
	public void setApplyFileMaterials(String applyFileMaterials) {
		applyFileMaterialList.clear();
		String[] applyFileMaterialArr = applyFileMaterials.split(",");
		for (int i = 0; i < applyFileMaterialArr.length; i++) {
			if(StringUtils.isNotBlank(applyFileMaterialArr[i])) {
				PosApplyFileMaterialsDTO material = new PosApplyFileMaterialsDTO();
				material.setApplyMaterialCode(applyFileMaterialArr[i]);
				applyFileMaterialList.add(material);
			}
		}
	}
	
	/* 属性存取 */
	
	public String getBarcodeNo() {
		return this.barcodeNo;
	}

	public void setBarcodeNo(String value) {
		this.barcodeNo = value;
	}

	public String getPosBatchNo() {
		return this.posBatchNo;
	}

	public void setPosBatchNo(String value) {
		this.posBatchNo = value;
	}

	public String getRelateBarcode() {
		return this.relateBarcode;
	}

	public void setRelateBarcode(String value) {
		this.relateBarcode = value;
	}

	public java.lang.Long getFilePages() {
		return this.filePages;
	}

	public void setFilePages(java.lang.Long value) {
		this.filePages = value;
	}

	public String getSignAccordFlag() {
		return this.signAccordFlag;
	}

	public void setSignAccordFlag(String value) {
		this.signAccordFlag = value;
	}

	public Date getApplyDate() {
		return this.applyDate;
	}

	public void setApplyDate(Date value) {
		this.applyDate = value;
	}

	public String getPaymentType() {
		return this.paymentType;
	}

	public void setPaymentType(String value) {
		this.paymentType = value;
	}

	public String getTransferAccountno() {
		return this.transferAccountno;
	}

	public void setTransferAccountno(String value) {
		this.transferAccountno = value;
	}

	public String getBankCode() {
		return this.bankCode;
	}

	public void setBankCode(String value) {
		this.bankCode = value;
	}

	public String getTransferAccountOwner() {
		return this.transferAccountOwner;
	}

	public void setTransferAccountOwner(String value) {
		this.transferAccountOwner = value;
	}

	public String getForeignExchangeType() {
		return this.foreignExchangeType;
	}

	public void setForeignExchangeType(String value) {
		this.foreignExchangeType = value;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String value) {
		this.remark = value;
	}

	public String getPkSerial() {
		return this.pkSerial;
	}

	public void setPkSerial(String value) {
		this.pkSerial = value;
	}

	public String getApprovalServiceType() {
		return approvalServiceType;
	}

	public void setApprovalServiceType(String approvalServiceType) {
		this.approvalServiceType = approvalServiceType;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<PosApplyFileMaterialsDTO> getApplyFileMaterialList() {
		return applyFileMaterialList;
	}

	public void setApplyFileMaterialList(List<PosApplyFileMaterialsDTO> applyFileMaterialList) {
		this.applyFileMaterialList = applyFileMaterialList;
	}

	public String getServiceItems() {
		return this.serviceItems;
	}

	public void setServiceItems(String serviceItems) {
		this.serviceItems = serviceItems;
	}

	public String getTransferAccountOwnerIdType() {
		return transferAccountOwnerIdType;
	}

	public void setTransferAccountOwnerIdType(String transferAccountOwnerIdType) {
		this.transferAccountOwnerIdType = transferAccountOwnerIdType;
	}

	public String getTransferAccountOwnerIdNo() {
		return transferAccountOwnerIdNo;
	}

	public void setTransferAccountOwnerIdNo(String transferAccountOwnerIdNo) {
		this.transferAccountOwnerIdNo = transferAccountOwnerIdNo;
	}

	public String getBankBranchCode() {
		return bankBranchCode;
	}

	public void setBankBranchCode(String bankBranchCode) {
		this.bankBranchCode = bankBranchCode;
	}

	public List<BankInfoDTO> getBankList() {
		return bankList;
	}

	public void setBankList(List<BankInfoDTO> bankList) {
		this.bankList = bankList;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getApprovalServiceTypeDesc() {
		return approvalServiceTypeDesc;
	}

	public void setApprovalServiceTypeDesc(String approvalServiceTypeDesc) {
		this.approvalServiceTypeDesc = approvalServiceTypeDesc;
	}

	public String getDefaultZip() {
		return defaultZip;
	}

	public void setDefaultZip(String defaultZip) {
		this.defaultZip = defaultZip;
	}

	public String getDefaultAddress() {
		return defaultAddress;
	}

	public void setDefaultAddress(String defaultAddress) {
		this.defaultAddress = defaultAddress;
	}

}
