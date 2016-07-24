package com.sinolife.pos.common.dto.serviceitems;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyProductDTO;



/**
 * 6 复效
 */

public class ServiceItemsInputDTO_6 extends ServiceItemsInputDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3391495975205286267L;
	private List<PolicyProductDTO> policyProductList;	//险种信息
	private String renewType;							//复效类型 
	private BigDecimal loanAllSum;						//保单已贷款本息和
	private BigDecimal interestSum;						//保单已贷款利息和
	private BigDecimal aplLoanAllSum;					//保单自垫本息和
	private BigDecimal aplInterestSum;					//保单自垫利息和
	private boolean hasValidLoanOrApl;					//保单是否有有效的借款或自垫
	private BigDecimal renewInterest;					//复效实付利息 
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}

	@Override
	protected void onPropertyChange() {
		if(policyProductList != null && !policyProductList.isEmpty()) {
			for(PolicyProductDTO pp : policyProductList) {
				if(pp.isSelected()){
						
				if("1".equals(renewType)) { 
					 
						
					//整单复效，能选上的都选上
					 pp.setSelected("Y".equals(pp.getCanBeSelectedFlag()));
				
				} else if("2".equals(renewType)) {
					//仅复效主险
					if("Y".equals(pp.getIsPrimaryPlan())) {						
						
						    pp.setSelected("Y".equals(pp.getCanBeSelectedFlag()));
					
												
					} else {
						pp.setSelected(false);
					}
				} else if("3".equals(renewType)) {
					//复效主险和附加险
					
						pp.setSelected("Y".equals(pp.getCanBeSelectedFlag()));
				
					
					
				}
				if(isSpecialFuncSelected()&&(getSpecialFunc()!=null&&"4".equals(getSpecialFunc())))
				{
					pp.setSelected(true);
				}
			  }
			}	
			
		}
		if(!"1".equals(getSpecialFunc())) {
			//选中了功能特殊件复效免利息时才能输入复效实收利息
			setRenewInterest(null);
		}
	}

	/**
	 * 计算贷款本金
	 * @return
	 */
	public BigDecimal getLoanSum() {
		if(loanAllSum != null && interestSum != null) {
			BigDecimal loanSum = loanAllSum.subtract(interestSum);
			if(loanSum.compareTo(new BigDecimal(0)) > 0)
				return loanSum;
		}
		return null;
	}
	
	/**
	 * 计算自垫本金
	 * @return
	 */
	public BigDecimal getAplLoanSum() {
		if(aplLoanAllSum != null && aplInterestSum != null) {
			BigDecimal aplLoanSum = aplLoanAllSum.subtract(aplInterestSum);
			if(aplLoanSum.compareTo(new BigDecimal(0)) > 0)
				return aplLoanSum;
		}
		return null;
	}
	
	public List<PolicyProductDTO> getPolicyProductList() {
		return policyProductList;
	}

	public void setPolicyProductList(List<PolicyProductDTO> policyProductList) {
		this.policyProductList = policyProductList;
	}

	public String getRenewType() {
		return renewType;
	}

	public void setRenewType(String renewType) {
		this.renewType = renewType;
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

	public boolean isHasValidLoanOrApl() {
		return hasValidLoanOrApl;
	}

	public void setHasValidLoanOrApl(boolean hasValidLoanOrApl) {
		this.hasValidLoanOrApl = hasValidLoanOrApl;
	}

	public BigDecimal getRenewInterest() {
		return renewInterest;
	}

	public void setRenewInterest(BigDecimal renewInterest) {
		this.renewInterest = renewInterest;
	}

}
