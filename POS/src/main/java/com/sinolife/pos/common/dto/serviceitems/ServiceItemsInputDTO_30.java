package com.sinolife.pos.common.dto.serviceitems;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.CodeTableItemDTO;


/**
 * 30 保单质押贷款-贷款受理
 */

public class ServiceItemsInputDTO_30 extends ServiceItemsInputDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9026091177014109199L;
	private BigDecimal loanInterest;			//贷款利率
	private BigDecimal loanSum;					//贷款金额
	private BigDecimal loanLimitDate;			//贷款年限
	private String loanDeptNo;					//贷款网点
	private String policyMortgageDesc;			//保单冻结状态
	private String productAbbrName;				//险种简称
	private String productFullName;				//险种全称
	private Date maturityDate;					//满期日
	private String applicantName;				//投保人姓名
	private String applicantIdTypeName;			//投保人证件类型
	private String applicantIdNo;				//投保人证件号码
	private String insuredName;					//被保人姓名
	private String insuredIdTypeName;			//被保人证件类型
	private String insuredIdNo;					//被保人证件号码
	private String productStatusDesc;			//险种状态
	private BigDecimal policyCashValue;			//保单现金价值
	private List<CodeTableItemDTO> loanDeptList;//贷款网点列表
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}
	
	
	/* getters and setters */
	
	public BigDecimal getLoanSum() {
		return loanSum;
	}

	public void setLoanSum(BigDecimal loanSum) {
		this.loanSum = loanSum;
	}

	public BigDecimal getLoanLimitDate() {
		return loanLimitDate;
	}

	public void setLoanLimitDate(BigDecimal loanLimitDate) {
		this.loanLimitDate = loanLimitDate;
	}

	public String getLoanDeptNo() {
		return loanDeptNo;
	}
	public void setLoanDeptNo(String loanDeptNo) {
		this.loanDeptNo = loanDeptNo;
	}

	public BigDecimal getLoanInterest() {
		return loanInterest;
	}

	public void setLoanInterest(BigDecimal loanInterest) {
		this.loanInterest = loanInterest;
	}

	public String getPolicyMortgageDesc() {
		return policyMortgageDesc;
	}

	public void setPolicyMortgageDesc(String policyMortgageDesc) {
		this.policyMortgageDesc = policyMortgageDesc;
	}

	public String getProductAbbrName() {
		return productAbbrName;
	}

	public void setProductAbbrName(String productAbbrName) {
		this.productAbbrName = productAbbrName;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getApplicantName() {
		return applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public String getApplicantIdTypeName() {
		return applicantIdTypeName;
	}

	public void setApplicantIdTypeName(String applicantIdTypeName) {
		this.applicantIdTypeName = applicantIdTypeName;
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

	public String getInsuredIdTypeName() {
		return insuredIdTypeName;
	}

	public void setInsuredIdTypeName(String insuredIdTypeName) {
		this.insuredIdTypeName = insuredIdTypeName;
	}

	public String getInsuredIdNo() {
		return insuredIdNo;
	}

	public void setInsuredIdNo(String insuredIdNo) {
		this.insuredIdNo = insuredIdNo;
	}

	public String getProductStatusDesc() {
		return productStatusDesc;
	}

	public void setProductStatusDesc(String productStatusDesc) {
		this.productStatusDesc = productStatusDesc;
	}

	public BigDecimal getPolicyCashValue() {
		return policyCashValue;
	}

	public void setPolicyCashValue(BigDecimal policyCashValue) {
		this.policyCashValue = policyCashValue;
	}

	public List<CodeTableItemDTO> getLoanDeptList() {
		return loanDeptList;
	}

	public void setLoanDeptList(List<CodeTableItemDTO> loanDeptList) {
		this.loanDeptList = loanDeptList;
	}

	public String getProductFullName() {
		return productFullName;
	}

	public void setProductFullName(String productFullName) {
		this.productFullName = productFullName;
	}

}
