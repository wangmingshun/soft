package com.sinolife.pos.common.dto.serviceitems;

import org.springframework.validation.Errors;

public class ServiceItemsInputDTO_46 extends ServiceItemsInputDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7158287678994406258L;
	
	private String policyStatus;	//保单状态(N:正常  Y:冻结)
	private String freezeOrThawRemark;			//冻结或者解冻备注
	
	public String getPolicyStatus() {
		return policyStatus;
	}

	public void setPolicyStatus(String policyStatus) {
		this.policyStatus = policyStatus;
	}

	public String getFreezeOrThawRemark() {
		return freezeOrThawRemark;
	}

	public void setFreezeOrThawRemark(String freezeOrThawRemark) {
		this.freezeOrThawRemark = freezeOrThawRemark;
	}

	@Override
	public void validate(Errors err) {
		super.validate(err);
	}
}
