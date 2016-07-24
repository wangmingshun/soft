package com.sinolife.pos.common.dto.serviceitems;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.LoanPayBackDTO;

/**
 * 9 保单还款
 */
public class ServiceItemsInputDTO_9 extends ServiceItemsInputDTO {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1012732072369540923L;
	
	private BigDecimal loanAllSum; // 保单已贷款本息和
	private BigDecimal interestSum; // 保单已贷款利息和
	private BigDecimal aplLoanAllSum; // 保单自垫本息和
	private BigDecimal aplInterestSum; // 保单自垫利息和
	private BigDecimal aplExtraFee; // 自垫应补保费
	private BigDecimal loanPayBackSum; // 还款金额
	//enit by wangmingshun
	private String loanPayBackType;	//保单还款类型
	private List<LoanPayBackDTO> loanPayBackList; //保单应还明细

	/**
	 * 计算贷款本金
	 * 
	 * @return
	 */
	public BigDecimal getLoanSum() {
		if (loanAllSum != null && interestSum != null) {
			BigDecimal loanSum = loanAllSum.subtract(interestSum);
			if (loanSum.compareTo(new BigDecimal(0)) > 0)
				return loanSum;
		}
		return null;
	}

	/**
	 * 计算自垫本金
	 * 
	 * @return
	 */
	public BigDecimal getAplLoanSum() {
		if (aplLoanAllSum != null && aplInterestSum != null) {
			BigDecimal aplLoanSum = aplLoanAllSum.subtract(aplInterestSum);
			if (aplLoanSum.compareTo(new BigDecimal(0)) > 0)
				return aplLoanSum;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#
	 * onPropertyChange()
	 */
	@Override
	protected void onPropertyChange() {
		if (loanAllSum == null)
			loanAllSum = new BigDecimal(0);

		if (interestSum == null)
			interestSum = new BigDecimal(0);

		if (aplLoanAllSum == null)
			aplLoanAllSum = new BigDecimal(0);

		if (aplInterestSum == null)
			aplInterestSum = new BigDecimal(0);

		if (aplExtraFee == null)
			aplExtraFee = new BigDecimal(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate
	 * (org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}

	public BigDecimal getLoanAllSum() {
		return loanAllSum;
	}

	public void setLoanAllSum(BigDecimal loanAllSum) {
		this.loanAllSum = loanAllSum;
	}

	public BigDecimal getInterestSum() {
		return interestSum;
	}

	public void setInterestSum(BigDecimal interestSum) {
		this.interestSum = interestSum;
	}

	public BigDecimal getAplLoanAllSum() {
		return aplLoanAllSum;
	}

	public void setAplLoanAllSum(BigDecimal aplLoanAllSum) {
		this.aplLoanAllSum = aplLoanAllSum;
	}

	public BigDecimal getAplInterestSum() {
		return aplInterestSum;
	}

	public void setAplInterestSum(BigDecimal aplInterestSum) {
		this.aplInterestSum = aplInterestSum;
	}

	public BigDecimal getAplExtraFee() {
		return aplExtraFee;
	}

	public void setAplExtraFee(BigDecimal aplExtraFee) {
		this.aplExtraFee = aplExtraFee;
	}

	public BigDecimal getLoanPayBackSum() {
		return loanPayBackSum;
	}

	public void setLoanPayBackSum(BigDecimal loanPayBackSum) {
		this.loanPayBackSum = loanPayBackSum;
	}


	public String getLoanPayBackType() {
		return loanPayBackType;
	}

	public void setLoanPayBackType(String loanPayBackType) {
		this.loanPayBackType = loanPayBackType;
	}

	public List<LoanPayBackDTO> getLoanPayBackList() {
		return loanPayBackList;
	}

	public void setLoanPayBackList(List<LoanPayBackDTO> loanPayBackList) {
		this.loanPayBackList = loanPayBackList;
	}

	@Override
	public String toString() {
		return "ServiceItemsInputDTO_9 [loanAllSum=" + loanAllSum
				+ ", interestSum=" + interestSum + ", aplLoanAllSum="
				+ aplLoanAllSum + ", aplInterestSum=" + aplInterestSum
				+ ", aplExtraFee=" + aplExtraFee + ", loanPayBackSum="
				+ loanPayBackSum + ", loanPayBackType=" + loanPayBackType
				+ ", loanPayBackList=" + loanPayBackList + "]";
	}

}
