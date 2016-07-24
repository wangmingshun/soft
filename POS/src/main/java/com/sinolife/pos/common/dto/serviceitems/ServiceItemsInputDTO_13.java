package com.sinolife.pos.common.dto.serviceitems;

import org.springframework.validation.Errors;

/**
 * 13 交费频次变更 比14的多两参数，继承14
 */
public class ServiceItemsInputDTO_13 extends ServiceItemsInputDTO_14 {

	/**
	 * 13 交费频次变更
	 */
	private static final long serialVersionUID = -8017245905484318530L;

	/**
	 * 新缴费频次
	 */
	private String frequency;

	/**
	 * 新缴费频次下的期缴保费
	 */
	private String periodPrem;

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

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String[] getFrequencyArr() {
		return frequency == null ? null : frequency.split(";");
	}

	public String getPeriodPrem() {
		return periodPrem;
	}

	public void setPeriodPrem(String periodPrem) {
		this.periodPrem = periodPrem;
	}

}
