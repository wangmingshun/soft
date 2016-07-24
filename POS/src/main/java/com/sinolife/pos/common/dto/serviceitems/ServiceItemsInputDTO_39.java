package com.sinolife.pos.common.dto.serviceitems;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.sinolife.pos.common.dto.FinancialProductsDTO;
import com.sinolife.pos.common.dto.FinancialProductsTransDTO;

/**
 * 39 投资账户转换
 */

public class ServiceItemsInputDTO_39 extends ServiceItemsInputDTO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4076377929462606514L;
	private List<FinancialProductsDTO> financialProductsList;			//理财产品信息列表
	private List<FinancialProductsTransDTO> financialProductsTransList;	//转换信息列表
	private int financialProductsTransListSize;							//转换信息列表大小
	private String finProdSeq;											//理财产品序号
	private String finProductCode;										//理财产品代码
	
	
	/**
	 * 取得账户价值大于0的账户列表
	 * @return
	 */
	public List<FinancialProductsDTO> getFilteredFinancialProductsList() {
		List<FinancialProductsDTO> retList = new ArrayList<FinancialProductsDTO>();
		for(FinancialProductsDTO dto : financialProductsList) {
			if(dto.getAmount().compareTo(new BigDecimal(0)) > 0) {
				retList.add(dto);
			}
		}
		return retList;
	}
	
	public List<FinancialProductsDTO> getFinancialProductsList() {
		return financialProductsList;
	}
	public void setFinancialProductsList(
			List<FinancialProductsDTO> financialProductsList) {
		this.financialProductsList = financialProductsList;
	}
	public List<FinancialProductsTransDTO> getFinancialProductsTransList() {
		return financialProductsTransList;
	}
	public void setFinancialProductsTransList(
			List<FinancialProductsTransDTO> financialProductsTransList) {
		this.financialProductsTransList = financialProductsTransList;
	}
	public int getFinancialProductsTransListSize() {
		return financialProductsTransListSize;
	}
	public void setFinancialProductsTransListSize(int financialProductsTransListSize) {
		this.financialProductsTransListSize = financialProductsTransListSize;
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

	@Override
	protected void onPropertyChange() {
		//由于删除的元素无法通过绑定删除，在这里对除绑定元素以外的剩余元素进行移除
		while(financialProductsTransListSize < financialProductsTransList.size()) {
			financialProductsTransList.remove(financialProductsTransListSize);
		}
	}
}
