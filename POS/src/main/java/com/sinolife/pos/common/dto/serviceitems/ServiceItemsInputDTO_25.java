package com.sinolife.pos.common.dto.serviceitems;

import org.springframework.validation.Errors;


/**
 * 25 自垫选择权变更
 * @author wangkui
 *
 */
public class ServiceItemsInputDTO_25 extends ServiceItemsInputDTO{

	private static final long serialVersionUID = -550817145063469055L;
	
	/**
	 * 自垫方式
	 */
	private String aplOption;
	

	public String getAplOption() {
		return aplOption;
	}

	public void setAplOption(String aplOption) {
		this.aplOption = aplOption;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}
}
