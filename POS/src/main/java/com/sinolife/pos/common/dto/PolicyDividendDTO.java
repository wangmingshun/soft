package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 保单分红信息DTO
 */
public class PolicyDividendDTO implements Serializable {

	private static final long serialVersionUID = 4041991736058026923L;

	private String policyNo;				//保单号
	private String prodSeq;					//产品序号
	private String productCode;				//产品代码
	private String dividendSelection;		//分红选择
	private Date partToDate;				//红利分至日
	private BigDecimal interestDividendBal;	//累积生息红利账户余额
	private BigDecimal interestRate;		//红利比例利息
	private BigDecimal cashDividendBal;		//现金红利账户余额
	private String productFullName;         //险种全称

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getProdSeq() {
		return prodSeq;
	}

	public void setProdSeq(String prodSeq) {
		this.prodSeq = prodSeq;
	}

	public String getDividendSelection() {
		return dividendSelection;
	}

	public void setDividendSelection(String dividendSelection) {
		this.dividendSelection = dividendSelection;
	}

	public Date getPartToDate() {
		return partToDate;
	}

	public void setPartToDate(Date partToDate) {
		this.partToDate = partToDate;
	}

	public BigDecimal getInterestDividendBal() {
		return interestDividendBal;
	}

	public void setInterestDividendBal(BigDecimal interestDividendBal) {
		this.interestDividendBal = interestDividendBal;
	}

	public BigDecimal getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}

	public BigDecimal getCashDividendBal() {
		return cashDividendBal;
	}

	public void setCashDividendBal(BigDecimal cashDividendBal) {
		this.cashDividendBal = cashDividendBal;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductFullName() {
		return productFullName;
	}

	public void setProductFullName(String productFullName) {
		this.productFullName = productFullName;
	}

}
