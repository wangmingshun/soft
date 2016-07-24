package com.sinolife.pos.common.dto.serviceitems;

import org.springframework.validation.Errors;


/**
 * 28 保单挂失、挂失解除
 */
public class ServiceItemsInputDTO_28 extends ServiceItemsInputDTO{

	private static final long serialVersionUID = -5791466847764228312L;

	/**
	 * 当前是否挂失
	 */
	private String isLoss;

	public String getIsLoss() {
		return isLoss;
	}

	public void setIsLoss(String isLoss) {
		this.isLoss = isLoss;
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}
}
