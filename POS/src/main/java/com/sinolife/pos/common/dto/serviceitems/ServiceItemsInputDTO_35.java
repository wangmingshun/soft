package com.sinolife.pos.common.dto.serviceitems;

import java.math.BigDecimal;
import java.util.List;

import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.FinancialProductsDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;

/**
 * 35 追加保费
 */

public class ServiceItemsInputDTO_35 extends ServiceItemsInputDTO{

	/**
	 * 
	 */
	private static final long serialVersionUID = -701311126636931209L;
	private String unitLinkedFlag;								//是否为投连产品 Y/N
	private String universalFlag;								//是否为万能产品 Y/N
	private BigDecimal addPremAmount;							//追加保费金额
	private List<FinancialProductsDTO> financialProductsList;	//投连账户信息
	private List<PolicyProductDTO> policyProductList;			//万能账户信息(险种信息)
	private List<CodeTableItemDTO> rateList;					//分配比例下拉框数据源
	private String finProdSeq;									//理财产品序号
	private String finProductCode;								//理财产品代码

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
	public BigDecimal getAddPremAmount() {
		return addPremAmount;
	}
	public void setAddPremAmount(BigDecimal addPremAmount) {
		this.addPremAmount = addPremAmount;
	}
	public List<PolicyProductDTO> getPolicyProductList() {
		return policyProductList;
	}
	public void setPolicyProductList(List<PolicyProductDTO> policyProductList) {
		this.policyProductList = policyProductList;
	}
	public List<CodeTableItemDTO> getRateList() {
		return rateList;
	}
	public void setRateList(List<CodeTableItemDTO> rateList) {
		this.rateList = rateList;
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
