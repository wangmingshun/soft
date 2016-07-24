package com.sinolife.pos.common.dto;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class VipApplyDetailDto {
	private static final long serialVersionUID = 5454155825314635342L;

	private String cardNo;
	private String modifiedReason;
	private String applyDate;
	private String providedUser;
	private String clientNo;
	private String empNo;
	public String getServcieDate() {
		return servcieDate;
	}

	public void setServcieDate(String servcieDate) {
		this.servcieDate = servcieDate;
	}

	public String getVipBigServiceItem() {
		return vipBigServiceItem;
	}

	public void setVipBigServiceItem(String vipBigServiceItem) {
		this.vipBigServiceItem = vipBigServiceItem;
	}

	public String getVipSmallServiceItem() {
		return vipSmallServiceItem;
	}

	public void setVipSmallServiceItem(String vipSmallServiceItem) {
		this.vipSmallServiceItem = vipSmallServiceItem;
	}

	public String getServiceReason() {
		return serviceReason;
	}

	public void setServiceReason(String serviceReason) {
		this.serviceReason = serviceReason;
	}

	public String getAttechmentFileId() {
		return attechmentFileId;
	}

	public void setAttechmentFileId(String attechmentFileId) {
		this.attechmentFileId = attechmentFileId;
	}

	public CommonsMultipartFile getAttechmentFileName() {
		return attechmentFileName;
	}

	public void setAttechmentFileName(CommonsMultipartFile attechmentFileName) {
		this.attechmentFileName = attechmentFileName;
	}

	private String empChangeReason;
	private String empPhone;
    private String servcieDate;
    private String vipBigServiceItem;
    private String  vipSmallServiceItem;
    private String   serviceReason;
    
	private String attechmentFileId;							//附件在IM中的ID
	private CommonsMultipartFile attechmentFileName;							//附件的文件名
    
	public String getEmpPhone() {
		return empPhone;
	}

	public void setEmpPhone(String empPhone) {
		this.empPhone = empPhone;
	}

	public String getEmpNo() {
		return empNo;
	}

	public void setEmpNo(String empNo) {
		this.empNo = empNo;
	}

	public String getEmpChangeReason() {
		return empChangeReason;
	}

	public void setEmpChangeReason(String empChangeReason) {
		this.empChangeReason = empChangeReason;
	}

	public String getCardNo() {
		return cardNo;
	}

	public String getClientNo() {
		return clientNo;
	}

	public void setClientNo(String clientNo) {
		this.clientNo = clientNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getModifiedReason() {
		return modifiedReason;
	}

	public void setModifiedReason(String modifiedReason) {
		this.modifiedReason = modifiedReason;
	}

	public String getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}

	public String getProvidedUser() {
		return providedUser;
	}

	public void setProvidedUser(String providedUser) {
		this.providedUser = providedUser;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
