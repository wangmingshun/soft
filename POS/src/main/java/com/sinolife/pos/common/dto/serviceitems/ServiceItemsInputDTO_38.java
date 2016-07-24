package com.sinolife.pos.common.dto.serviceitems;

import java.util.List;

import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.FinancialProductsDTO;

/**
 * 38 账户分配比例变更
 */

public class ServiceItemsInputDTO_38 extends ServiceItemsInputDTO{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7123536536281986977L;
	private List<FinancialProductsDTO> financialProductsList;	//理财产品信息列表
	private List<CodeTableItemDTO> rateList;					//分配比例下拉框数据源
	private String finProdSeq;									//理财产品序号
	private String finProductCode;								//理财产品代码
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}

	public List<FinancialProductsDTO> getFinancialProductsList() {
		return financialProductsList;
	}

	public void setFinancialProductsList(
			List<FinancialProductsDTO> financialProductsList) {
		this.financialProductsList = financialProductsList;
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
