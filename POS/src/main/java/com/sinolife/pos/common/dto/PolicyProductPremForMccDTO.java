package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 保单险种缴费信息
 */
public class PolicyProductPremForMccDTO implements Serializable {

	private static final long serialVersionUID = -3640285684050967129L;

	private String policyNo; // 保单号
	private String prodSeq; // 产品序号
	private String frequency; // 缴费频次
	private String premSource; // 保费来源
	private String premStatus; // 缴费状态
	private BigDecimal premTerm; // 缴费年期
	private BigDecimal annStandardPrem; // 年缴标准保费
	private BigDecimal annWeakAddPrem; // 年缴弱体加费
	private BigDecimal annOccuAddPrem; // 年缴职业加费
	private BigDecimal periodStandardPrem; // 期缴标准保费
	private BigDecimal periodOccuAddPrem; // 期缴职业加费
	private BigDecimal periodWeakAddPrem; // 期缴弱体加费
	private BigDecimal periodPremSum; // 期缴保费合计
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date payToDate; // 缴至日期
	private BigDecimal addPremTerm; // 加费期限
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date addPremEffDate; // 加费生效日
	private String pkSerial; // 业务流水
	private String premPeriodType; // 缴费期类型
	private String sexCode; // 被保人性别（用于保费计算）
	private BigDecimal insAge; // 被保人年龄（用于保费计算）
	private String occupationCode; // 被保人职业（用于保费计算）

	private String premPeriodTypeDesc; // 缴费期类型描述
	private String premSourceDesc; // 保费来源描述
	private String premStatusDesc; // 缴费状态描述
	private String applicantName; // 投保人姓名
	private String sexDesc; // 被保人性别描述
	private String occupationDesc; // 被保人职业描述
	private String insuredName; // 被保人姓名
	private String frequencyDesc; // 缴费频次描述
	private String dutyStatusDesc; // 责任状态描述

	private String productCode; // 产品代码
	private String productName; // 产品名称
	private String insuredNo; // 被保人号码
	private String dutyStatus; // 责任状态

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date effectDate; // 生效日，该字段是policy_product表的
	private boolean checked; // 页面是否选中

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProdSeq() {
		return prodSeq;
	}

	public void setProdSeq(String prodSeq) {
		this.prodSeq = prodSeq;
	}

	public String getInsuredNo() {
		return insuredNo;
	}

	public void setInsuredNo(String insuredNo) {
		this.insuredNo = insuredNo;
	}

	public String getInsuredName() {
		return insuredName;
	}

	public void setInsuredName(String insuredName) {
		this.insuredName = insuredName;
	}

	public String getDutyStatus() {
		return dutyStatus;
	}

	public void setDutyStatus(String dutyStatus) {
		this.dutyStatus = dutyStatus;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getDutyStatusDesc() {
		return dutyStatusDesc;
	}

	public void setDutyStatusDesc(String dutyStatusDesc) {
		this.dutyStatusDesc = dutyStatusDesc;
	}

	public String getFrequencyDesc() {
		return frequencyDesc;
	}

	public void setFrequencyDesc(String frequencyDesc) {
		this.frequencyDesc = frequencyDesc;
	}

	public String getPremSource() {
		return premSource;
	}

	public void setPremSource(String premSource) {
		this.premSource = premSource;
	}

	public String getPremStatus() {
		return premStatus;
	}

	public void setPremStatus(String premStatus) {
		this.premStatus = premStatus;
	}

	public BigDecimal getAnnStandardPrem() {
		return annStandardPrem;
	}

	public void setAnnStandardPrem(BigDecimal annStandardPrem) {
		this.annStandardPrem = annStandardPrem;
	}

	public BigDecimal getAnnWeakAddPrem() {
		return annWeakAddPrem;
	}

	public void setAnnWeakAddPrem(BigDecimal annWeakAddPrem) {
		this.annWeakAddPrem = annWeakAddPrem;
	}

	public BigDecimal getAnnOccuAddPrem() {
		return annOccuAddPrem;
	}

	public void setAnnOccuAddPrem(BigDecimal annOccuAddPrem) {
		this.annOccuAddPrem = annOccuAddPrem;
	}

	public BigDecimal getPeriodStandardPrem() {
		return periodStandardPrem;
	}

	public void setPeriodStandardPrem(BigDecimal periodStandardPrem) {
		this.periodStandardPrem = periodStandardPrem;
	}

	public BigDecimal getPeriodOccuAddPrem() {
		return periodOccuAddPrem;
	}

	public void setPeriodOccuAddPrem(BigDecimal periodOccuAddPrem) {
		this.periodOccuAddPrem = periodOccuAddPrem;
	}

	public BigDecimal getPeriodWeakAddPrem() {
		return periodWeakAddPrem;
	}

	public void setPeriodWeakAddPrem(BigDecimal periodWeakAddPrem) {
		this.periodWeakAddPrem = periodWeakAddPrem;
	}

	public BigDecimal getPeriodPremSum() {
		return periodPremSum;
	}

	public void setPeriodPremSum(BigDecimal periodPremSum) {
		this.periodPremSum = periodPremSum;
	}

	public Date getPayToDate() {
		return payToDate;
	}

	public void setPayToDate(Date payToDate) {
		this.payToDate = payToDate;
	}

	public BigDecimal getAddPremTerm() {
		return addPremTerm;
	}

	public void setAddPremTerm(BigDecimal addPremTerm) {
		this.addPremTerm = addPremTerm;
	}

	public Date getAddPremEffDate() {
		return addPremEffDate;
	}

	public void setAddPremEffDate(Date addPremEffDate) {
		this.addPremEffDate = addPremEffDate;
	}

	public String getPkSerial() {
		return pkSerial;
	}

	public void setPkSerial(String pkSerial) {
		this.pkSerial = pkSerial;
	}

	public String getPremPeriodType() {
		return premPeriodType;
	}

	public void setPremPeriodType(String premPeriodType) {
		this.premPeriodType = premPeriodType;
	}

	public String getPremPeriodTypeDesc() {
		return premPeriodTypeDesc;
	}

	public void setPremPeriodTypeDesc(String premPeriodTypeDesc) {
		this.premPeriodTypeDesc = premPeriodTypeDesc;
	}

	public String getPremSourceDesc() {
		return premSourceDesc;
	}

	public void setPremSourceDesc(String premSourceDesc) {
		this.premSourceDesc = premSourceDesc;
	}

	public String getPremStatusDesc() {
		return premStatusDesc;
	}

	public void setPremStatusDesc(String premStatusDesc) {
		this.premStatusDesc = premStatusDesc;
	}

	public String getApplicantName() {
		return applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public BigDecimal getPremTerm() {
		return premTerm;
	}

	public void setPremTerm(BigDecimal premTerm) {
		this.premTerm = premTerm;
	}

	public Date getEffectDate() {
		return effectDate;
	}

	public void setEffectDate(Date effectDate) {
		this.effectDate = effectDate;
	}

	public String getOccupationCode() {
		return occupationCode;
	}

	public void setOccupationCode(String occupationCode) {
		this.occupationCode = occupationCode;
	}

	public String getSexCode() {
		return sexCode;
	}

	public void setSexCode(String sexCode) {
		this.sexCode = sexCode;
	}

	public BigDecimal getInsAge() {
		return insAge;
	}

	public void setInsAge(BigDecimal insAge) {
		this.insAge = insAge;
	}

	public String getSexDesc() {
		return sexDesc;
	}

	public void setSexDesc(String sexDesc) {
		this.sexDesc = sexDesc;
	}

	public String getOccupationDesc() {
		return occupationDesc;
	}

	public void setOccupationDesc(String occupationDesc) {
		this.occupationDesc = occupationDesc;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Override
	public String toString() {
		return "PolicyProductPremForMccDTO [policyNo=" + policyNo
				+ ", prodSeq=" + prodSeq + ", frequency=" + frequency
				+ ", premSource=" + premSource + ", premStatus=" + premStatus
				+ ", premTerm=" + premTerm + ", annStandardPrem="
				+ annStandardPrem + ", annWeakAddPrem=" + annWeakAddPrem
				+ ", annOccuAddPrem=" + annOccuAddPrem
				+ ", periodStandardPrem=" + periodStandardPrem
				+ ", periodOccuAddPrem=" + periodOccuAddPrem
				+ ", periodWeakAddPrem=" + periodWeakAddPrem
				+ ", periodPremSum=" + periodPremSum + ", payToDate="
				+ payToDate + ", addPremTerm=" + addPremTerm
				+ ", addPremEffDate=" + addPremEffDate + ", pkSerial="
				+ pkSerial + ", premPeriodType=" + premPeriodType
				+ ", sexCode=" + sexCode + ", insAge=" + insAge
				+ ", occupationCode=" + occupationCode
				+ ", premPeriodTypeDesc=" + premPeriodTypeDesc
				+ ", premSourceDesc=" + premSourceDesc + ", premStatusDesc="
				+ premStatusDesc + ", applicantName=" + applicantName
				+ ", sexDesc=" + sexDesc + ", occupationDesc=" + occupationDesc
				+ ", insuredName=" + insuredName + ", frequencyDesc="
				+ frequencyDesc + ", dutyStatusDesc=" + dutyStatusDesc
				+ ", productCode=" + productCode + ", productName="
				+ productName + ", insuredNo=" + insuredNo + ", dutyStatus="
				+ dutyStatus + ", effectDate=" + effectDate + ", checked="
				+ checked + "]";
	}

}
