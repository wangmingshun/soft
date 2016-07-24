package com.sinolife.pos.common.dto.serviceitems;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyProductDTO;



/**
 * 8 保单贷款
 */

public class ServiceItemsInputDTO_8 extends ServiceItemsInputDTO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5414096033209245681L;
	private List<PolicyProductDTO> policyProductList;//产品信息列表
	private BigDecimal loanAmount;					//贷款金额
	private BigDecimal loanAllSum;					//保单已贷本息和
	private BigDecimal loanInterestRate;			//主险贷款利率
	private BigDecimal loanMinSum;					//保单最小贷款金额
	private BigDecimal cashValueSum;				//保单现金价值总和，由险种现价累加得到
	private boolean    ulifeMainProduct;            //主险是否是万能险
	private String 	   loanRateType;				//贷款模式（Y表示使用新贷款模式,N表示使用旧贷款模式）
	




	public boolean isUlifeMainProduct() {
		return ulifeMainProduct;
	}

	public void setUlifeMainProduct(boolean ulifeMainProduct) {
		this.ulifeMainProduct = ulifeMainProduct;
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

	public BigDecimal getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(BigDecimal loanAmount) {
		this.loanAmount = loanAmount;
	}

	public BigDecimal getLoanAllSum() {
		return loanAllSum;
	}

	public void setLoanAllSum(BigDecimal loanAllSum) {
		this.loanAllSum = loanAllSum;
	}

	public BigDecimal getLoanInterestRate() {
		return loanInterestRate;
	}

	public void setLoanInterestRate(BigDecimal loanInterestRate) {
		this.loanInterestRate = loanInterestRate;
	}


	public BigDecimal getLoanMinSum() {
		return loanMinSum;
	}

	public void setLoanMinSum(BigDecimal loanMinSum) {
		this.loanMinSum = loanMinSum;
	}

	public BigDecimal getCashValueSum() {
		return cashValueSum;
	}

	public void setCashValueSum(BigDecimal cashValueSum) {
		this.cashValueSum = cashValueSum;
	}

	public String getLoanRateType() {
		return loanRateType;
	}

	public void setLoanRateType(String loanRateType) {
		this.loanRateType = loanRateType;
	}
	
}
