package com.sinolife.pos.common.dto.serviceitems;

import org.springframework.validation.Errors;


/**
 * 31 保单质押贷款-还款解冻
 */
public class ServiceItemsInputDTO_31 extends ServiceItemsInputDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8309893649505049214L;

	/**
	 * 保单号
	 */
	private String policyNo;
	
	/**
	 * 保单冻结状态
	 */
	private String freezeStatus;
	
	/**
	 * 保单险种状态
	 */
	private String productStatus;
	
	/**
	 * 保单险种名称
	 */
	private String productName;
	
	/**
	 * 保单险种代码
	 */
	private String productCode;
	
	/**
	 * 保单险种序号
	 */
	private String prodSeq;
	
	/**
	 * 投保人姓名
	 */
	private String applicantName;
	
	/**
	 * 投保人证件类型
	 */
	private String applicantIdType;
	
	/**
	 * 投保人证件号
	 */
	private String applicantIdNo;
	
	/**
	 * 被保人姓名
	 */
	private String insuredName;
	
	/**
	 * 被保人证件类型
	 */
	private String insuredIdType;
	
	/**
	 * 被保人证件号
	 */
	private String insuredIdNo;
	
	/**
	 * 保单现金价值
	 */
	private String cashValue;
	
	/**
	 * 最大可贷金额
	 */
	private String maxLoanSum;
	
	/**
	 * 贷款利率
	 */
	private String interestRate;
	
	/**
	 * 满期日yyyy-mm-dd
	 */
	private String maturityDate;
	
	/**
	 * 贷款金额
	 */
	private String loanSum;
	
	/**
	 * 贷款期限
	 */
	private String loanDeadline;
	
	/**
	 * 贷款网点编号
	 */
	private String deptNo;
	
	/**
	 * 贷款网点名称
	 */
	private String deptName;
	
	/**
	 * 解冻原因
	 */
	private String unfreezeCause;
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}

	public String getFreezeStatus() {
		return freezeStatus;
	}

	public void setFreezeStatus(String freezeStatus) {
		this.freezeStatus = freezeStatus;
	}

	public String getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(String productStatus) {
		this.productStatus = productStatus;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getApplicantName() {
		return applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public String getApplicantIdType() {
		return applicantIdType;
	}

	public void setApplicantIdType(String applicantIdType) {
		this.applicantIdType = applicantIdType;
	}

	public String getApplicantIdNo() {
		return applicantIdNo;
	}

	public void setApplicantIdNo(String applicantIdNo) {
		this.applicantIdNo = applicantIdNo;
	}

	public String getInsuredName() {
		return insuredName;
	}

	public void setInsuredName(String insuredName) {
		this.insuredName = insuredName;
	}

	public String getInsuredIdType() {
		return insuredIdType;
	}

	public void setInsuredIdType(String insuredIdType) {
		this.insuredIdType = insuredIdType;
	}

	public String getInsuredIdNo() {
		return insuredIdNo;
	}

	public void setInsuredIdNo(String insuredIdNo) {
		this.insuredIdNo = insuredIdNo;
	}

	public String getCashValue() {
		return cashValue;
	}

	public void setCashValue(String cashValue) {
		this.cashValue = cashValue;
	}

	public String getMaxLoanSum() {
		return maxLoanSum;
	}

	public void setMaxLoanSum(String maxLoanSum) {
		this.maxLoanSum = maxLoanSum;
	}

	public String getInterestRate() {
		if(interestRate!=null && interestRate.startsWith(".")){
			interestRate= "0"+interestRate;
		}
		return interestRate;
	}

	public void setInterestRate(String interestRate) {
		this.interestRate = interestRate;
	}

	public String getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(String maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getLoanSum() {
		return loanSum;
	}

	public void setLoanSum(String loanSum) {
		this.loanSum = loanSum;
	}

	public String getLoanDeadline() {
		return loanDeadline;
	}

	public void setLoanDeadline(String loanDeadline) {
		this.loanDeadline = loanDeadline;
	}

	public String getDeptNo() {
		return deptNo;
	}

	public void setDeptNo(String deptNo) {
		this.deptNo = deptNo;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getUnfreezeCause() {
		return unfreezeCause;
	}

	public void setUnfreezeCause(String unfreezeCause) {
		this.unfreezeCause = unfreezeCause;
	}

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
	
}
