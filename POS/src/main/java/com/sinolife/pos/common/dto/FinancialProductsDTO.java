package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 理财产品信息DTO
 */

public class FinancialProductsDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6568568765632830121L;
	private String financialProducts;		//理财产品代码
	private String finProductsDesc;			//理财产品中文描述
	private String nextFinancialProducts;	//转换后的理财产品代码
	private String nextFinProductsDesc;		//转换后的理财产品描述
	private BigDecimal amount;				//最近卖出价值
	private BigDecimal rate;				//账户分配比例
	private BigDecimal units;				//累积单位数
	private BigDecimal interestRate;		//利率
	private BigDecimal buyPrice;			//最近买入价
	private BigDecimal soldPrice;			//最近卖出价
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date interestSettleEndDate;		//利息结算截至日期
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date riskPremSettleEndDate;		//风险保费扣除截至日期
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date manPremSettleEndDate;		//保单管理费扣除截至日期
	
	private BigDecimal withdrawUnits;		//本次领取单位数
	
	/**
	 * 账户分配比例的百分比值Get方法
	 * @return BigInteger 账户分配比例的百分比值
	 */
	public BigInteger getRatePercent() {
		if(this.rate == null)
			return null;
		return new BigDecimal(100).multiply(this.rate).toBigInteger();
	}
	/**
	 * 账户分配比例的百分比值Set方法
	 * @param value 账户分配比例的百分比值
	 */
	public void setRatePercent(BigInteger value) {
		this.rate = (value == null ? null : new BigDecimal(value).divide(new BigDecimal(100)));
	}
	
	
	public String getFinancialProducts() {
		return financialProducts;
	}
	public void setFinancialProducts(String financialProducts) {
		this.financialProducts = financialProducts;
	}
	public String getFinProductsDesc() {
		return finProductsDesc;
	}
	public void setFinProductsDesc(String finProductsDesc) {
		this.finProductsDesc = finProductsDesc;
	}
	public String getNextFinancialProducts() {
		return nextFinancialProducts;
	}
	public void setNextFinancialProducts(String nextFinancialProducts) {
		this.nextFinancialProducts = nextFinancialProducts;
	}
	public BigDecimal getRate() {
		return rate;
	}
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	public BigDecimal getUnits() {
		return units;
	}
	public void setUnits(BigDecimal units) {
		this.units = units;
	}
	public BigDecimal getBuyPrice() {
		return buyPrice;
	}
	public void setBuyPrice(BigDecimal buyPrice) {
		this.buyPrice = buyPrice;
	}
	public BigDecimal getSoldPrice() {
		return soldPrice;
	}
	public void setSoldPrice(BigDecimal soldPrice) {
		this.soldPrice = soldPrice;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getWithdrawUnits() {
		return withdrawUnits;
	}
	public void setWithdrawUnits(BigDecimal withdrawUnits) {
		this.withdrawUnits = withdrawUnits;
	}
	public BigDecimal getInterestRate() {
		return interestRate;
	}
	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}
	public String getNextFinProductsDesc() {
		return nextFinProductsDesc;
	}
	public void setNextFinProductsDesc(String nextFinProductsDesc) {
		this.nextFinProductsDesc = nextFinProductsDesc;
	}
	public Date getInterestSettleEndDate() {
		return interestSettleEndDate;
	}
	public void setInterestSettleEndDate(Date interestSettleEndDate) {
		this.interestSettleEndDate = interestSettleEndDate;
	}
	public Date getRiskPremSettleEndDate() {
		return riskPremSettleEndDate;
	}
	public void setRiskPremSettleEndDate(Date riskPremSettleEndDate) {
		this.riskPremSettleEndDate = riskPremSettleEndDate;
	}
	public Date getManPremSettleEndDate() {
		return manPremSettleEndDate;
	}
	public void setManPremSettleEndDate(Date manPremSettleEndDate) {
		this.manPremSettleEndDate = manPremSettleEndDate;
	}
	
}
