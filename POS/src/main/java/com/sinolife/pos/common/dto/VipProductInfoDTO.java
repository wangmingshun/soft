package com.sinolife.pos.common.dto;

import java.util.Date;

public class VipProductInfoDTO {

	private static final long serialVersionUID = -2115234322533238666L;

	private String branchName;
	private String policyNo;
	private Date effectDate;
	private String dutyDescription;
	private String productName;
	private String appName;
	private String insName;
	private String premSum;
	private String periodType;
	private String vipPremSum;
	private String empName;
	private String vipGrade;
	private Date vipEffectDate;
	/* vip结束日期 edit by wangmingshun*/
	private Date vipEndDate; // 结束日期

	private String cardNo;
	private Date modifiedDate;
	private String modifiedReason;
	private String providedUser;

	private String empPhone;
	private String empDept;

	private Date vipServiceDate;
	private String bigService;
	private String smallService;
	private String seviceDesc;
	private String fileId;
	private String fileName;
	private String uploadUser;
	private String uploadTime;
	//是否有效 edit by wangmingshun
	private String isValid;
	//服务序号 edit by wangmingshun
	private String seqNo;

	public String getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}

	public Date getVipEndDate() {
		return vipEndDate;
	}
	
	public void setVipEndDate(Date vipEndDate) {
		this.vipEndDate = vipEndDate;
	}
	public String getIsValid() {
		return isValid;
	}

	public void setIsValid(String isValid) {
		this.isValid = isValid;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUploadUser() {
		return uploadUser;
	}

	public void setUploadUser(String uploadUser) {
		this.uploadUser = uploadUser;
	}

	public String getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getVipGrade() {
		return vipGrade;
	}

	public void setVipGrade(String vipGrade) {
		this.vipGrade = vipGrade;
	}

	public Date getVipEffectDate() {
		return vipEffectDate;
	}

	public void setVipEffectDate(Date vipEffectDate) {
		this.vipEffectDate = vipEffectDate;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getModifiedReason() {
		return modifiedReason;
	}

	public void setModifiedReason(String modifiedReason) {
		this.modifiedReason = modifiedReason;
	}

	public String getProvidedUser() {
		return providedUser;
	}

	public void setProvidedUser(String providedUser) {
		this.providedUser = providedUser;
	}

	public String getEmpPhone() {
		return empPhone;
	}

	public void setEmpPhone(String empPhone) {
		this.empPhone = empPhone;
	}

	public String getEmpDept() {
		return empDept;
	}

	public void setEmpDept(String empDept) {
		this.empDept = empDept;
	}

	public Date getVipServiceDate() {
		return vipServiceDate;
	}

	public void setVipServiceDate(Date vipServiceDate) {
		this.vipServiceDate = vipServiceDate;
	}

	public String getBigService() {
		return bigService;
	}

	public void setBigService(String bigService) {
		this.bigService = bigService;
	}

	public String getSmallService() {
		return smallService;
	}

	public void setSmallService(String smallService) {
		this.smallService = smallService;
	}

	public String getSeviceDesc() {
		return seviceDesc;
	}

	public void setSeviceDesc(String seviceDesc) {
		this.seviceDesc = seviceDesc;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public Date getEffectDate() {
		return effectDate;
	}

	public void setEffectDate(Date effectDate) {
		this.effectDate = effectDate;
	}

	public String getDutyDescription() {
		return dutyDescription;
	}

	public void setDutyDescription(String dutyDescription) {
		this.dutyDescription = dutyDescription;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getInsName() {
		return insName;
	}

	public void setInsName(String insName) {
		this.insName = insName;
	}

	public String getPremSum() {
		return premSum;
	}

	public void setPremSum(String premSum) {
		this.premSum = premSum;
	}

	public String getPeriodType() {
		return periodType;
	}

	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}

	public String getVipPremSum() {
		return vipPremSum;
	}

	public void setVipPremSum(String vipPremSum) {
		this.vipPremSum = vipPremSum;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
