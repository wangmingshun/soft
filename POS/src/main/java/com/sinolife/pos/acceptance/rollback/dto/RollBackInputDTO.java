package com.sinolife.pos.acceptance.rollback.dto;

import java.io.Serializable;

import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.common.dto.PosApplyFilesDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.util.PosUtils;

public class RollBackInputDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4352174861336726941L;
	
	/**
	 * 保全批次表公共信息
	 */
	private PosApplyBatchDTO applyBatchDTO;
	
	/**
	 * 保全表公共信息，
	 * 回退涉及费用的情况下，有银行相关信息
	 */
	private PosInfoDTO posInfoDTO;
	
	/**
	 * 保全申请书表信息
	 */
	private PosApplyFilesDTO applyFilesDTO;
	
	/**
	 * 撤销原因
	 */
	private String causeCode;
	
	public PosApplyBatchDTO getApplyBatchDTO() {
		return applyBatchDTO;
	}

	public void setApplyBatchDTO(PosApplyBatchDTO applyBatchDTO) {
		this.applyBatchDTO = applyBatchDTO;
	}
	
	public PosInfoDTO getPosInfoDTO() {
		return posInfoDTO;
	}

	public void setPosInfoDTO(PosInfoDTO posInfoDTO) {
		this.posInfoDTO = posInfoDTO;
	}
	
	public PosApplyFilesDTO getApplyFilesDTO() {
		return applyFilesDTO;
	}

	public void setApplyFilesDTO(PosApplyFilesDTO applyFilesDTO) {
		this.applyFilesDTO = applyFilesDTO;
	}
	
	public String toString(){
		return PosUtils.describBeanAsJSON(this);
	}

	public String getCauseCode() {
		return causeCode;
	}

	public void setCauseCode(String causeCode) {
		this.causeCode = causeCode;
	}
}
