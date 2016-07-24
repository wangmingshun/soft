package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;

/**
 * 保单受益人信息
 */

@Entity
public class PolicyBeneficiaryDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2414851639968945727L;
	private String beneficiaryNo;		//受益人编号
	private String applyNo;				//投保单系统编号
	private String relationship;		//人际关系
	private String policyNo;			//保单号
	private String benefitType;			//受益性质
	private String relationDesc;		//与被保人关系描述
	private BigDecimal benefitRate;			//受益比例
	private String benefitStatus;		//受益状态:Y有效；N无效
	private Integer benefitSeq;			//受益顺位
	private String pkSerial;			//数据主键
	private String bankCode;			//银行代码
	private String transferGrantFlag;	//转账授权标志 Y/N
	private String accountNo;			//账号
	private String accountOwner;		//帐户户主名称
	
	/* 非表字段属性 */
	private String bankName;					//银行名称
	private String relationshipDesc;			//人际关系描述
	private String benefitTypeDesc;				//受益性质描述
	private BeneficiaryInfoDTO benefInfo;		//受益人详细信息
	private boolean checked;					//是否选中
	
	//edit by wangmingshun
	private String claimPremDrawType; 	//理赔金领取方式
	private BigDecimal promiseRate;		//约定比例
	private String promiseAge;			//约定年龄
	
	/* 构造函数  */

	public PolicyBeneficiaryDTO() {
		benefInfo = new BeneficiaryInfoDTO();
	}

	/* 特殊存取 */
	public BigDecimal getBenefitRatePercent() {
		if(benefitRate == null) {
			return null;
		}
		return benefitRate.multiply(new BigDecimal(100L));
	}
	
	public void setBenefitRatePercent(BigDecimal value) {
		if(value == null) {
			benefitRate = null;
		} else {
			benefitRate = value.divide(new BigDecimal(100L));
		}
	}
	
	public BigDecimal getPromiseRate() {
		if(promiseRate == null) 
			return null;
		return promiseRate.multiply(new BigDecimal(100L));
	}
	
	public void setPromiseRate(BigDecimal promiseRate) {
		if(promiseRate == null)
			this.promiseRate = null;
		else 
			this.promiseRate = promiseRate.divide(new BigDecimal(100L));
	}
	
	/* 属性存取 */
	
	public String getBeneficiaryNo() {
		return this.beneficiaryNo;
	}
	public String getClaimPremDrawType() {
		return claimPremDrawType;
	}

	public void setClaimPremDrawType(String claimPremDrawType) {
		this.claimPremDrawType = claimPremDrawType;
	}

	public String getPromiseAge() {
		return promiseAge;
	}

	public void setPromiseAge(String promiseAge) {
		this.promiseAge = promiseAge;
	}

	public void setBeneficiaryNo(String value) {
		this.beneficiaryNo = value;
	}
	public String getApplyNo() {
		return this.applyNo;
	}
	public void setApplyNo(String value) {
		this.applyNo = value;
	}
	public String getRelationship() {
		return this.relationship;
	}
	public void setRelationship(String value) {
		this.relationship = value;
	}
	public String getPolicyNo() {
		return this.policyNo;
	}
	public void setPolicyNo(String value) {
		this.policyNo = value;
	}
	public String getBenefitType() {
		return this.benefitType;
	}
	public void setBenefitType(String value) {
		this.benefitType = value;
	}
	public String getRelationDesc() {
		return this.relationDesc;
	}
	public void setRelationDesc(String value) {
		this.relationDesc = value;
	}
	public BigDecimal getBenefitRate() {
		return this.benefitRate;
	}
	public void setBenefitRate(BigDecimal value) {
		this.benefitRate = value;
	}
	public String getBenefitStatus() {
		return this.benefitStatus;
	}
	public void setBenefitStatus(String value) {
		this.benefitStatus = value;
	}
	public Integer getBenefitSeq() {
		return this.benefitSeq;
	}
	public void setBenefitSeq(Integer value) {
		this.benefitSeq = value;
	}
	public String getPkSerial() {
		return this.pkSerial;
	}
	public void setPkSerial(String value) {
		this.pkSerial = value;
	}
	public BeneficiaryInfoDTO getBenefInfo() {
		return benefInfo;
	}
	public void setBenefInfo(BeneficiaryInfoDTO benefInfo) {
		this.benefInfo = benefInfo;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getTransferGrantFlag() {
		return transferGrantFlag;
	}
	public void setTransferGrantFlag(String transferGrantFlag) {
		this.transferGrantFlag = transferGrantFlag;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getAccountOwner() {
		return accountOwner;
	}
	public void setAccountOwner(String accountOwner) {
		this.accountOwner = accountOwner;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getRelationshipDesc() {
		return relationshipDesc;
	}
	public void setRelationshipDesc(String relationshipDesc) {
		this.relationshipDesc = relationshipDesc;
	}
	public String getBenefitTypeDesc() {
		return benefitTypeDesc;
	}
	public void setBenefitTypeDesc(String benefitTypeDesc) {
		this.benefitTypeDesc = benefitTypeDesc;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
