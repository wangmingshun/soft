package com.sinolife.pos.pubInterface.biz.dto;

public class EsbServiceItemsInputDTO_1 extends EsbServiceItemsInputDTO {

	/**
	 * 犹豫期契撤
	 */
	private static final long serialVersionUID = 1L;
	private String surrenderCauseCode;	//退保原因代码
	public String getSurrenderCauseCode() {
		return surrenderCauseCode;
	}
	public void setSurrenderCauseCode(String surrenderCauseCode) {
		this.surrenderCauseCode = surrenderCauseCode;
	}

}
