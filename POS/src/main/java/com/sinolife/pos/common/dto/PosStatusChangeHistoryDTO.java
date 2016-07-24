package com.sinolife.pos.common.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 状态变迁表DTO
 */

public class PosStatusChangeHistoryDTO extends PosComDTO{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2000878273443091638L;
	private String posNo;			//保全号
	private String statusChangeNo;	//状态变迁序号
	private String oldStatusCode;	//受理状态旧值
	private String newStatusCode;	//受理状态新值
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date changeDate;		//受理状态变更日期
	private String changeUser;		//受理状态变更人
	private String pkSerial;		//数据主键
	
	/* 非表字段属性 */
	private String oldStatusDesc;	//受理状态旧值描述
	private String newStatusDesc;	//受理状态新值描述
	
	/* 属性存取 */
	
	public String getPosNo() {
		return posNo;
	}
	public void setPosNo(String posNo) {
		this.posNo = posNo;
	}
	public String getStatusChangeNo() {
		return statusChangeNo;
	}
	public void setStatusChangeNo(String statusChangeNo) {
		this.statusChangeNo = statusChangeNo;
	}
	public String getOldStatusCode() {
		return oldStatusCode;
	}
	public void setOldStatusCode(String oldStatusCode) {
		this.oldStatusCode = oldStatusCode;
	}
	public String getNewStatusCode() {
		return newStatusCode;
	}
	public void setNewStatusCode(String newStatusCode) {
		this.newStatusCode = newStatusCode;
	}
	public Date getChangeDate() {
		return changeDate;
	}
	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}
	public String getChangeUser() {
		return changeUser;
	}
	public void setChangeUser(String changeUser) {
		this.changeUser = changeUser;
	}
	public String getPkSerial() {
		return pkSerial;
	}
	public void setPkSerial(String pkSerial) {
		this.pkSerial = pkSerial;
	}
	public String getOldStatusDesc() {
		return oldStatusDesc;
	}
	public void setOldStatusDesc(String oldStatusDesc) {
		this.oldStatusDesc = oldStatusDesc;
	}
	public String getNewStatusDesc() {
		return newStatusDesc;
	}
	public void setNewStatusDesc(String newStatusDesc) {
		this.newStatusDesc = newStatusDesc;
	}
	
}
