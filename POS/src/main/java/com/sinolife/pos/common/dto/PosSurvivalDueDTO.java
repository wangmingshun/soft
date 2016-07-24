package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 生存金应领表DTO
 */

public class PosSurvivalDueDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1001557494096076389L;
	private String policyNo;		//保单号
	private Integer prodSeq;		//产品序号
	private Integer survivalSeq;	//生存金给付序号
	private String productCode;		//产品代码
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date payDueDate;		//应领日期
	private BigDecimal payDueSum;	//应领金额
	private BigDecimal interestSum;	//利息金额
	private String payDueSeq;		//应付流水号
	private String payDueFlag;		//应付标志
	private String validFlag;		//有效标志
	private String payFactFlag;		//实付标志
	private String pkSerial;		//数据主键
	private String survivalType;     //生存金类型
	
	
	/* 非表字段属性 */
	private String productAbbrName;	//险种简称
	private String productFullName;	//险种全称
	private boolean checked;		//是否选中
	private String canBeSelectedFlag;//是否可选
	private String message;			//不可选消息
	
	
	
	private String agreeMaturitySum; // 协议满期金额
	private String branchPercent;// 特殊件—协议满期 分公司承担比例
	private String backBenefit;// 既得利益

	public String getBranchPercent() {
		return branchPercent;
	}

	public void setBranchPercent(String branchPercent) {
		this.branchPercent = branchPercent;
	}

	public String getBackBenefit() {
		return backBenefit;
	}

	public void setBackBenefit(String backBenefit) {
		this.backBenefit = backBenefit;
	}

	public String getAgreeMaturitySum() {
		return agreeMaturitySum;
	}

	public void setAgreeMaturitySum(String agreeMaturitySum) {
		this.agreeMaturitySum = agreeMaturitySum;
	}

	
	/* 属性存取 */
	
	public String getPolicyNo() {
		return policyNo;
	}
	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}
	public Integer getProdSeq() {
		return prodSeq;
	}
	public void setProdSeq(Integer prodSeq) {
		this.prodSeq = prodSeq;
	}
	public Integer getSurvivalSeq() {
		return survivalSeq;
	}
	public void setSurvivalSeq(Integer survivalSeq) {
		this.survivalSeq = survivalSeq;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public Date getPayDueDate() {
		return payDueDate;
	}
	public void setPayDueDate(Date payDueDate) {
		this.payDueDate = payDueDate;
	}
	public BigDecimal getPayDueSum() {
		return payDueSum;
	}
	public void setPayDueSum(BigDecimal payDueSum) {
		this.payDueSum = payDueSum;
	}
	public BigDecimal getInterestSum() {
		return interestSum;
	}
	public void setInterestSum(BigDecimal interestSum) {
		this.interestSum = interestSum;
	}
	public String getPayDueSeq() {
		return payDueSeq;
	}
	public void setPayDueSeq(String payDueSeq) {
		this.payDueSeq = payDueSeq;
	}
	public String getPayDueFlag() {
		return payDueFlag;
	}
	public void setPayDueFlag(String payDueFlag) {
		this.payDueFlag = payDueFlag;
	}
	public String getValidFlag() {
		return validFlag;
	}
	public void setValidFlag(String validFlag) {
		this.validFlag = validFlag;
	}
	public String getPayFactFlag() {
		return payFactFlag;
	}
	public void setPayFactFlag(String payFactFlag) {
		this.payFactFlag = payFactFlag;
	}
	public String getPkSerial() {
		return pkSerial;
	}
	public void setPkSerial(String pkSerial) {
		this.pkSerial = pkSerial;
	}
	public String getProductAbbrName() {
		return productAbbrName;
	}
	public void setProductAbbrName(String productAbbrName) {
		this.productAbbrName = productAbbrName;
	}
	public String getProductFullName() {
		return productFullName;
	}
	public void setProductFullName(String productFullName) {
		this.productFullName = productFullName;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public String getCanBeSelectedFlag() {
		return canBeSelectedFlag;
	}
	public void setCanBeSelectedFlag(String canBeSelectedFlag) {
		this.canBeSelectedFlag = canBeSelectedFlag;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSurvivalType() {
		return survivalType;
	}
	public void setSurvivalType(String survivalType) {
		this.survivalType = survivalType;
	}
}
