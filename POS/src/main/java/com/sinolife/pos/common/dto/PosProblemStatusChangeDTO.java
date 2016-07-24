package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 保全问题件状态变迁记录
 */

public class PosProblemStatusChangeDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5796569846247287091L;
	private String problemItemNo;	//问题件号
	private Long statusChangeNo;	//问题件状态变迁序号
	private String statusOld;		//问题件状态旧值
	private String statusNew;		//问题件状态新值
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date statusChangeDate;	//问题件状态变更日期
	private String statusChangeUser;//问题件状态变更人
	
	/* 非表字段属性 */
	private String statusOldDesc;	//问题件状态旧值描述
	private String statusNewDesc;	//问题件状态新值描述
	
	/* 属性存取 */
	
	public String getProblemItemNo() {
		return problemItemNo;
	}
	public void setProblemItemNo(String problemItemNo) {
		this.problemItemNo = problemItemNo;
	}
	public Long getStatusChangeNo() {
		return statusChangeNo;
	}
	public void setStatusChangeNo(Long statusChangeNo) {
		this.statusChangeNo = statusChangeNo;
	}
	public String getStatusOld() {
		return statusOld;
	}
	public void setStatusOld(String statusOld) {
		this.statusOld = statusOld;
	}
	public String getStatusNew() {
		return statusNew;
	}
	public void setStatusNew(String statusNew) {
		this.statusNew = statusNew;
	}
	public Date getStatusChangeDate() {
		return statusChangeDate;
	}
	public void setStatusChangeDate(Date statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}
	public String getStatusChangeUser() {
		return statusChangeUser;
	}
	public void setStatusChangeUser(String statusChangeUser) {
		this.statusChangeUser = statusChangeUser;
	}
	public String getStatusOldDesc() {
		return statusOldDesc;
	}
	public void setStatusOldDesc(String statusOldDesc) {
		this.statusOldDesc = statusOldDesc;
	}
	public String getStatusNewDesc() {
		return statusNewDesc;
	}
	public void setStatusNewDesc(String statusNewDesc) {
		this.statusNewDesc = statusNewDesc;
	}
}
