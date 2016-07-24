package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
/**
 * 保单还款
 */
public class LoanPayBackDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6079276310081884715L;

	private String posNo;	//保全号
	private Date effectDate; //生效日期
	private BigDecimal aplLoanCash;	//自垫本金
	private BigDecimal aplLoanInterest;	//自垫利息
	private BigDecimal aplExtraFee;	//应补差额保费
	private BigDecimal loanCash;	//借款本金
	private BigDecimal loanInterest; //借款利息
	private BigDecimal totalSum;	//本息合计
	private BigDecimal loanRate;	//贷款利率
	private String loanRateType;	//保单贷款类型
	private String loanPayBackTypeDetail;	//保全还款类型
	private BigDecimal loanPayBackSumDetail; 	//本笔还款金额
	
	public String getPosNo() {
		return posNo;
	}
	public void setPosNo(String posNo) {
		this.posNo = posNo;
	}
	public Date getEffectDate() {
		return effectDate;
	}
	public void setEffectDate(Date effectDate) {
		this.effectDate = effectDate;
	}
	public BigDecimal getAplLoanCash() {
		return aplLoanCash;
	}
	public void setAplLoanCash(BigDecimal aplLoanCash) {
		this.aplLoanCash = aplLoanCash;
	}
	public BigDecimal getAplLoanInterest() {
		return aplLoanInterest;
	}
	public void setAplLoanInterest(BigDecimal aplLoanInterest) {
		this.aplLoanInterest = aplLoanInterest;
	}
	public BigDecimal getAplExtraFee() {
		return aplExtraFee;
	}
	public void setAplExtraFee(BigDecimal aplExtraFee) {
		this.aplExtraFee = aplExtraFee;
	}
	public BigDecimal getLoanCash() {
		return loanCash;
	}
	public void setLoanCash(BigDecimal loanCash) {
		this.loanCash = loanCash;
	}
	public BigDecimal getLoanInterest() {
		return loanInterest;
	}
	public void setLoanInterest(BigDecimal loanInterest) {
		this.loanInterest = loanInterest;
	}
	public BigDecimal getTotalSum() {
		return totalSum;
	}
	public void setTotalSum(BigDecimal totalSum) {
		this.totalSum = totalSum;
	}
	public BigDecimal getLoanRate() {
		return loanRate;
	}
	public void setLoanRate(BigDecimal loanRate) {
		this.loanRate = loanRate;
	}
	public String getLoanPayBackTypeDetail() {
		return loanPayBackTypeDetail;
	}
	public void setLoanPayBackTypeDetail(String loanPayBackTypeDetail) {
		this.loanPayBackTypeDetail = loanPayBackTypeDetail;
	}
	public BigDecimal getLoanPayBackSumDetail() {
		return loanPayBackSumDetail;
	}
	public void setLoanPayBackSumDetail(BigDecimal loanPayBackSumDetail) {
		this.loanPayBackSumDetail = loanPayBackSumDetail;
	}
	public String getLoanRateType() {
		return loanRateType;
	}
	public void setLoanRateType(String loanRateType) {
		this.loanRateType = loanRateType;
	}
	
}
