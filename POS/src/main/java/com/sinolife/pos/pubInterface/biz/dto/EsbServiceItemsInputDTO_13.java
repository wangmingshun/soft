package com.sinolife.pos.pubInterface.biz.dto;

/**
 * 13 交费频次变更 比14的多两参数，继承14
 */
public class EsbServiceItemsInputDTO_13 extends EsbServiceItemsInputDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 新缴费频次---0:无关,1:日,2:月,3:季,4:半年,5:年, 数据库表：frequency
	 */
	private String frequency;

	/**
	 * 新缴费频次下的期缴保费
	 */
	private String periodPrem;

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getPeriodPrem() {
		return periodPrem;
	}

	public void setPeriodPrem(String periodPrem) {
		this.periodPrem = periodPrem;
	}

}
