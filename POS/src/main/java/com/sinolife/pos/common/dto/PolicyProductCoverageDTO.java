package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class PolicyProductCoverageDTO implements Serializable {
	private static final long serialVersionUID = -3640245684050967129L;
	private String policyNo; // 保单号
	private String prodSeq; // 产品序号
	private String productCode; // 产品代码
	private String coverageCode; // 责任项目
	private String coverageSeq;  //责任序号
	public String getCoverageSeq() {
		return coverageSeq;
	}

	public void setCoverageSeq(String coverageSeq) {
		this.coverageSeq = coverageSeq;
	}

	private String coverageName;// 责任名称
	private String dutyStatus; // 责任状态
	private String productFullName; // 险种全称
	private String dutyStatusDesc;  //责任状态
	private String insuredName;  //被保险人
	

	private BigDecimal prem; // 保费
	private BigDecimal sumIns;// 保额
	private BigDecimal newSumIns;//新保额
	private  String pkSerial;
	private boolean selected;		//是否选中
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	
	public String getPkSerial() {
		return pkSerial;
	}

	public void setPkSerial(String pkSerial) {
		this.pkSerial = pkSerial;
	}

	public BigDecimal getNewSumIns() {
		return newSumIns;
	}

	public void setNewSumIns(BigDecimal newSumIns) {
		this.newSumIns = newSumIns;
	}

	public String getDutyStatusDesc() {
		return dutyStatusDesc;
	}

	public void setDutyStatusDesc(String dutyStatusDesc) {
		this.dutyStatusDesc = dutyStatusDesc;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getProdSeq() {
		return prodSeq;
	}

	public void setProdSeq(String prodSeq) {
		this.prodSeq = prodSeq;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getCoverageCode() {
		return coverageCode;
	}

	public void setCoverageCode(String coverageCode) {
		this.coverageCode = coverageCode;
	}

	public String getCoverageName() {
		return coverageName;
	}

	public void setCoverageName(String coverageName) {
		this.coverageName = coverageName;
	}

	public String getDutyStatus() {
		return dutyStatus;
	}

	public void setDutyStatus(String dutyStatus) {
		this.dutyStatus = dutyStatus;
	}

	public String getProductFullName() {
		return productFullName;
	}

	public void setProductFullName(String productFullName) {
		this.productFullName = productFullName;
	}

	public BigDecimal getPrem() {
		return prem;
	}

	public void setPrem(BigDecimal prem) {
		this.prem = prem;
	}

	public BigDecimal getSumIns() {
		return sumIns;
	}

	public void setSumIns(BigDecimal sumIns) {
		this.sumIns = sumIns;
	}
	public String getInsuredName() {
		return insuredName;
	}

	public void setInsuredName(String insuredName) {
		this.insuredName = insuredName;
	}

}
