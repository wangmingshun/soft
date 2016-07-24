package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.sinolife.sf.ruleengine.pos.bom.dto.POSVerifyResultDto;

/**
 * 保单缴费信息DTO
 */
public class PolicyPremInfoDTO implements Serializable {

	private static final long serialVersionUID = 1878620285959485469L;

	private String chargingMethod;		//收费方式
	private String premStatus;			//缴费状态
	private String premStatusDesc;		//缴费状态描述
	private String chargingMethodDesc;	//收费方式描述
	private String premSource;			//保费来源
	private String premSourceDesc;		//保费来源描述
	private String frequency;			//缴费频次
	private String frequencyDesc;		//缴费频次描述
	private Date premDueDate;			//应缴日
	private BigDecimal modalTotalPrem;	//期缴保费合计
	private BigDecimal policyBalance;	//保单余额
	private String pkSerial;			//业务流水
	private String policyNo;			//保单号
	private String channelType;         //渠道
	private String clientNo;			//付费人号
	private String clientName;			//付费人姓名
	private String idType;				//付费人证件类型
	private String idNo;				//付费人证件号码
	private String checked;				//是否被选中,页面上的值没选中的一律视为未做修改
	private String accountSeq;
	private POSVerifyResultDto verifyResult;//受理规则检查结果
	
	public String getChargingMethod() {
		return chargingMethod;
	}
	public String getChannelType() {
		return channelType;
	}
	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}
	public void setChargingMethod(String chargingMethod) {
		this.chargingMethod = chargingMethod;
	}
	public String getAccountSeq() {
		return accountSeq;
	}
	public void setAccountSeq(String accountSeq) {
		this.accountSeq = accountSeq;
	}
	public String getPolicyNo() {
		return policyNo;
	}
	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}
	public String getClientNo() {
		return clientNo;
	}
	public void setClientNo(String clientNo) {
		this.clientNo = clientNo;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getIdNo() {
		return idNo;
	}
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}
	public String getChecked() {
		return checked;
	}
	public void setChecked(String checked) {
		this.checked = checked;
	}
	public String getPremStatus() {
		return premStatus;
	}
	public void setPremStatus(String premStatus) {
		this.premStatus = premStatus;
	}
	public String getPremStatusDesc() {
		return premStatusDesc;
	}
	public void setPremStatusDesc(String premStatusDesc) {
		this.premStatusDesc = premStatusDesc;
	}
	public String getChargingMethodDesc() {
		return chargingMethodDesc;
	}
	public void setChargingMethodDesc(String chargingMethodDesc) {
		this.chargingMethodDesc = chargingMethodDesc;
	}
	public String getPremSource() {
		return premSource;
	}
	public void setPremSource(String premSource) {
		this.premSource = premSource;
	}
	public String getPremSourceDesc() {
		return premSourceDesc;
	}
	public void setPremSourceDesc(String premSourceDesc) {
		this.premSourceDesc = premSourceDesc;
	}
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public String getFrequencyDesc() {
		return frequencyDesc;
	}
	public void setFrequencyDesc(String frequencyDesc) {
		this.frequencyDesc = frequencyDesc;
	}
	public Date getPremDueDate() {
		return premDueDate;
	}
	public void setPremDueDate(Date premDueDate) {
		this.premDueDate = premDueDate;
	}
	public BigDecimal getModalTotalPrem() {
		return modalTotalPrem;
	}
	public void setModalTotalPrem(BigDecimal modalTotalPrem) {
		this.modalTotalPrem = modalTotalPrem;
	}
	public BigDecimal getPolicyBalance() {
		return policyBalance;
	}
	public void setPolicyBalance(BigDecimal policyBalance) {
		this.policyBalance = policyBalance;
	}
	public String getPkSerial() {
		return pkSerial;
	}
	public void setPkSerial(String pkSerial) {
		this.pkSerial = pkSerial;
	}
	public POSVerifyResultDto getVerifyResult() {
		return verifyResult;
	}
	public void setVerifyResult(POSVerifyResultDto verifyResult) {
		this.verifyResult = verifyResult;
	}
	
}
