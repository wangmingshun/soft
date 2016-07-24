package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;


public class FinancialProductsTransDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8867288961945763027L;
	private String outFinancialProducts;//转出账户ID
	private BigDecimal outUnits;		//转出单位数
	private String inFinancialProducts;	//转入账户ID
	
	
	public BigDecimal getOutUnits() {
		return outUnits;
	}
	public void setOutUnits(BigDecimal outUnits) {
		this.outUnits = outUnits;
	}
	public String getOutFinancialProducts() {
		return outFinancialProducts;
	}
	public void setOutFinancialProducts(String outFinancialProducts) {
		this.outFinancialProducts = outFinancialProducts;
	}
	public String getInFinancialProducts() {
		return inFinancialProducts;
	}
	public void setInFinancialProducts(String inFinancialProducts) {
		this.inFinancialProducts = inFinancialProducts;
	}
	
}
