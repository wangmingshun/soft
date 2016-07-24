package com.sinolife.pos.common.dto.serviceitems;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyProductDTO;

/**
 * 36 基本保费变更
 */

public class ServiceItemsInputDTO_36 extends ServiceItemsInputDTO{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7598106986295304232L;
	private List<PolicyProductDTO> policyProductList;	//产品信息列表
	private BigDecimal periodPrem;						//期缴保费
	private String finProdSeq;							//理财产品序号
	private String finProductCode;						//理财产品代码
	private String financialTypeProductFullName;        //理财产品名称 
	
	public String getFinancialTypeProductFullName() {
		return financialTypeProductFullName;
	}

	public void setFinancialTypeProductFullName(String financialTypeProductFullName) {
		this.financialTypeProductFullName = financialTypeProductFullName;
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}

	public List<PolicyProductDTO> getPolicyProductList() {
		return policyProductList;
	}

	public void setPolicyProductList(List<PolicyProductDTO> policyProductList) {
		this.policyProductList = policyProductList;
	}

	public BigDecimal getPeriodPrem() {
		return periodPrem;
	}

	public void setPeriodPrem(BigDecimal periodPrem) {
		this.periodPrem = periodPrem;
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
