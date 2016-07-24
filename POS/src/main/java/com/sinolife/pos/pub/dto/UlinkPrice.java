package com.sinolife.pos.pub.dto;

import java.io.Serializable;

import org.springframework.format.annotation.DateTimeFormat;

import com.sinolife.pos.pub.web.PaginatedHelper;

public class UlinkPrice extends PaginatedHelper implements Serializable {



	/**
	 * 
	 */
	private static final long serialVersionUID = 3407806884968067645L;
	
	public static final String TABLE_ALIAS="投连价格上传";
	public static final String ALIAS_PRICE_DATE="资产评估日";
	public static final String ALIAS_ASSETS_CODE="资产代码";
	public static final String ALIAS_SOLD_PRICE="卖出价";
	public static final String ALIAS_TOT_ASSETS_VALUE="资产净值(含费用)";
	public static final String ALIAS_REAL_ASSETS_VALUE="资产净值";
	public static final String ALIAS_FINANCIAL_PRODUCTS="账户代码";
	public static final String ALIAS_DESCRIPTION="账户描述";
	public static final String ALIAS_BUY_PRICE="买入价";
	
	
	@DateTimeFormat(pattern="dd-MM-yyyy")
	private java.util.Date  priceDate;
    private String assetsCode; 
    private Double soldPrice;
    private Double totAssetsValue;
    private Double realAssetsValue ;
    private String financialProducts;
    private String description;
    private Double buyPrice;
    private String beginDate;
    private String finishDate;
	@DateTimeFormat(pattern="dd-MM-yyyy")
	private java.util.Date  startDate;
	@DateTimeFormat(pattern="dd-MM-yyyy")
	private java.util.Date  endDate;
    
	public java.util.Date getPriceDate() {
		return priceDate;
	}
	public void setPriceDate(java.util.Date priceDate) {
		this.priceDate = priceDate;
	}
	public String getAssetsCode() {
		return assetsCode;
	}
	public void setAssetsCode(String assetsCode) {
		this.assetsCode = assetsCode;
	}
	public Double getSoldPrice() {
		return soldPrice;
	}
	public void setSoldPrice(Double soldPrice) {
		this.soldPrice = soldPrice;
	}
	public Double getTotAssetsValue() {
		return totAssetsValue;
	}
	public void setTotAssetsValue(Double totAssetsValue) {
		this.totAssetsValue = totAssetsValue;
	}
	public Double getRealAssetsValue() {
		return realAssetsValue;
	}
	public void setRealAssetsValue(Double realAssetsValue) {
		this.realAssetsValue = realAssetsValue;
	}
	/**
	 * @param financialProducts the financialProducts to set
	 */
	public void setFinancialProducts(String financialProducts) {
		this.financialProducts = financialProducts;
	}
	/**
	 * @return the financialProducts
	 */
	public String getFinancialProducts() {
		return financialProducts;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param buyPrice the buyPrice to set
	 */
	public void setBuyPrice(Double buyPrice) {
		this.buyPrice = buyPrice;
	}
	/**
	 * @return the buyPrice
	 */
	public Double getBuyPrice() {
		return buyPrice;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(java.util.Date startDate) {
		this.startDate = startDate;
	}
	/**
	 * @return the startDate
	 */
	public java.util.Date getStartDate() {
		return startDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(java.util.Date endDate) {
		this.endDate = endDate;
	}
	/**
	 * @return the endDate
	 */
	public java.util.Date getEndDate() {
		return endDate;
	}
	/**
	 * @param beginDate the beginDate to set
	 */
	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}
	/**
	 * @return the beginDate
	 */
	public String getBeginDate() {
		return beginDate;
	}
	/**
	 * @param finishDate the finishDate to set
	 */
	public void setFinishDate(String finishDate) {
		this.finishDate = finishDate;
	}
	/**
	 * @return the finishDate
	 */
	public String getFinishDate() {
		return finishDate;
	}
    
    


}
