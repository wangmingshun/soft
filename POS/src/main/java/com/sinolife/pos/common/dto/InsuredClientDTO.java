package com.sinolife.pos.common.dto;

import java.io.Serializable;

public class InsuredClientDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5512952298525612126L;
	private String insuredNo;				//被保人客户号
	private String insuredName;				//被保人姓名
	private String insuredSeq;				//被保人序号
	public String getInsuredNo() {
		return insuredNo;
	}
	public void setInsuredNo(String insuredNo) {
		this.insuredNo = insuredNo;
	}
	public String getInsuredName() {
		return insuredName;
	}
	public void setInsuredName(String insuredName) {
		this.insuredName = insuredName;
	}
	public String getInsuredSeq() {
		return insuredSeq;
	}
	public void setInsuredSeq(String insuredSeq) {
		this.insuredSeq = insuredSeq;
	}
}
