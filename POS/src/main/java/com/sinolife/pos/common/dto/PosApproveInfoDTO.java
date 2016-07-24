package com.sinolife.pos.common.dto;


public class PosApproveInfoDTO extends PosComDTO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7525692762865597156L;
	private String approveDecision;
	private String approveOpnion;
	private String approvetoUm;
	
	public String getApproveDecision() {
		return approveDecision;
	}
	public void setApproveDecision(String approveDecision) {
		this.approveDecision = approveDecision;
	}
	public String getApproveOpnion() {
		return approveOpnion;
	}
	public void setApproveOpnion(String approveOpnion) {
		this.approveOpnion = approveOpnion;
	}
	public String getApprovetoUm() {
		return approvetoUm;
	}
	public void setApprovetoUm(String approvetoUm) {
		this.approvetoUm = approvetoUm;
	}
}
