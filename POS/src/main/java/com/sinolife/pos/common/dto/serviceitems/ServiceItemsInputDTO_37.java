package com.sinolife.pos.common.dto.serviceitems;

import java.math.BigDecimal;
import java.util.List;

import com.sinolife.pos.common.dto.FinancialProductsDTO;

/**
 * 37 部分领取
 */

public class ServiceItemsInputDTO_37 extends ServiceItemsInputDTO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8380488612745495880L;
	private String unitLinkedFlag;								//是否为投连产品 Y/N
	private String universalFlag;								//是否为万能产品 Y/N
	private List<FinancialProductsDTO> financialProductsList;	//投连账户信息
	private BigDecimal policyValue;								//保单价值
	private BigDecimal withdrawTime;							//保单本年度领取次数
	private BigDecimal withdrawAmount;							//本次领取金额
	private String finProdSeq;									//理财产品序号
	private String finProductCode;								//理财产品代码
	private String payPolicy;									//抵交续期保费保单号
	
	public String getPayPolicy() {
		return payPolicy;
	}
	public void setPayPolicy(String payPolicy) {
		this.payPolicy = payPolicy;
	}
	
	public List<FinancialProductsDTO> getFinancialProductsList() {
		return financialProductsList;
	}
	public void setFinancialProductsList(
			List<FinancialProductsDTO> financialProductsList) {
		this.financialProductsList = financialProductsList;
	}
	public String getUnitLinkedFlag() {
		return unitLinkedFlag;
	}
	public void setUnitLinkedFlag(String unitLinkedFlag) {
		this.unitLinkedFlag = unitLinkedFlag;
	}
	public String getUniversalFlag() {
		return universalFlag;
	}
	public void setUniversalFlag(String universalFlag) {
		this.universalFlag = universalFlag;
	}
	public BigDecimal getPolicyValue() {
		return policyValue;
	}
	public void setPolicyValue(BigDecimal policyValue) {
		this.policyValue = policyValue;
	}
	public BigDecimal getWithdrawTime() {
		return withdrawTime;
	}
	public void setWithdrawTime(BigDecimal withdrawTime) {
		this.withdrawTime = withdrawTime;
	}
	public BigDecimal getWithdrawAmount() {
		return withdrawAmount;
	}
	public void setWithdrawAmount(BigDecimal withdrawAmount) {
		this.withdrawAmount = withdrawAmount;
	}
	public String getFinProdSeq() {
		return finProdSeq;
	}
	public void setFinProdSeq(String finProdSeq) {
		this.finProdSeq = finProdSeq;
	}
	public String getFinProductCode() {
		return finProductCode;
	}
	public void setFinProductCode(String finProductCode) {
		this.finProductCode = finProductCode;
	}
	
}
