package com.sinolife.pos.common.dto;

import javax.persistence.Entity;

/**
 * 申请材料
 */
@Entity
public class PosApplyFileMaterialsDTO extends PosComDTO {
	private static final long serialVersionUID = 5454155825314635342L;
	
	/* 表结构属性 */
	private String applyMaterialCode;				//申请资料代码
	private String barcodeNo;						//条形码
	private String pkSerial;						//数据主键
	
	/* 构造函数  */
	
	public PosApplyFileMaterialsDTO() {
	}

	public PosApplyFileMaterialsDTO(String applyMaterialCode, String barcodeNo) {
		this.applyMaterialCode = applyMaterialCode;
		this.barcodeNo = barcodeNo;
	}

	/* 属性存取 */
	
	public String getApplyMaterialCode() {
		return this.applyMaterialCode;
	}

	public void setApplyMaterialCode(String value) {
		this.applyMaterialCode = value;
	}

	public String getBarcodeNo() {
		return this.barcodeNo;
	}

	public void setBarcodeNo(String value) {
		this.barcodeNo = value;
	}

	public String getPkSerial() {
		return this.pkSerial;
	}

	public void setPkSerial(String value) {
		this.pkSerial = value;
	}

}
