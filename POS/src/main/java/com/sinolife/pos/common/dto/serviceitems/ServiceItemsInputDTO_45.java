package com.sinolife.pos.common.dto.serviceitems;

import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PosAppointmentInfoDTO;


public class ServiceItemsInputDTO_45 extends ServiceItemsInputDTO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8416080708583726026L;
	
	private String appointmentType; //预约类型 0：保全预约，1：取消预约
	private String appointmentServiceItem; //预约服务项目
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private java.util.Date appointmentDate;	//预约日期
	private String accountName; //领款人
	private String accountNo;	//银行账号
	private String bankCode;	//银行代码
	private String accountNoType;//银行卡类型
	private String idType;//证件类型
	private String idNo;//证件号码
	List<PosAppointmentInfoDTO> posAppointmentInfoList = new ArrayList<PosAppointmentInfoDTO>(); //可取消保全预约list

	public List<PosAppointmentInfoDTO> getPosAppointmentInfoList() {
		return posAppointmentInfoList;
	}

	public void setPosAppointmentInfoList(
			List<PosAppointmentInfoDTO> posAppointmentInfoList) {
		this.posAppointmentInfoList = posAppointmentInfoList;
	}

	public String getAppointmentType() {
		return appointmentType;
	}

	public void setAppointmentType(String appointmentType) {
		this.appointmentType = appointmentType;
	}

	public String getAppointmentServiceItem() {
		return appointmentServiceItem;
	}

	public void setAppointmentServiceItem(String appointmentServiceItem) {
		this.appointmentServiceItem = appointmentServiceItem;
	}

	public java.util.Date getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(java.util.Date appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getAccountNoType() {
		return accountNoType;
	}

	public void setAccountNoType(String accountNoType) {
		this.accountNoType = accountNoType;
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

	@Override
	public void validate(Errors err) {
		super.validate(err);
	}
}
