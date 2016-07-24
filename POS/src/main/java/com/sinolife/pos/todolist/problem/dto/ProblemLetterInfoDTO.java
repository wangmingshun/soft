package com.sinolife.pos.todolist.problem.dto;

import java.io.Serializable;

/**
 * 问题函信息
 */

public class ProblemLetterInfoDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5984491828704409061L;
	private String problemContent;	//问题件描述
	private String clientName;		//客户姓名
	private String clientPhone;		//客户联系电话
	private String clientAddress;	//客户联系地址
	private String clientZip;		//客户联系地址邮编
	private String empPhone;		//业务员电话
	private String empNo;			//业务员号码
	private String empName;			//业务员姓名
	private String empDeptName;		//业务员部门名称
	
	public String getProblemContent() {
		return problemContent;
	}
	public void setProblemContent(String problemContent) {
		this.problemContent = problemContent;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getClientPhone() {
		return clientPhone;
	}
	public void setClientPhone(String clientPhone) {
		this.clientPhone = clientPhone;
	}
	public String getClientAddress() {
		return clientAddress;
	}
	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}
	public String getClientZip() {
		return clientZip;
	}
	public void setClientZip(String clientZip) {
		this.clientZip = clientZip;
	}
	public String getEmpPhone() {
		return empPhone;
	}
	public void setEmpPhone(String empPhone) {
		this.empPhone = empPhone;
	}
	public String getEmpNo() {
		return empNo;
	}
	public void setEmpNo(String empNo) {
		this.empNo = empNo;
	}
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	public String getEmpDeptName() {
		return empDeptName;
	}
	public void setEmpDeptName(String empDeptName) {
		this.empDeptName = empDeptName;
	}
	
}
