package com.sinolife.pos.common.dto.serviceitems;

import org.springframework.validation.Errors;

/**
 * 33 保全批单收付方式调整
 */
public class ServiceItemsInputDTO_33 extends ServiceItemsInputDTO{

	private static final long serialVersionUID = -618400483400464653L;

	/**
	 * 需要变更的批单号
	 */
	private String changePosNo;
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}

	public String getChangePosNo() {
		return changePosNo;
	}

	public void setChangePosNo(String changePosNo) {
		this.changePosNo = changePosNo;
	}

}
