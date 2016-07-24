package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.util.Date;
/**
 * 保全审批信息dto
 */
public class PosApproveInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5596336152113835656L;
	
	private String posNo;			//保全号
	private String submitNo;		//送审序号
	private String approveNo;		//审批序号
	private String submiter;		//送审人
	private String approver;		//审批人
	private String approverName;	//审批人名字
	private Date submitDate;		//送审日期
	private Date approveDate;		//审批日期
	private String approveDesc;		//审批决定:1通过、2不通过、3转他人、4重新录入
	private String approveOption;	//审批意见
	private String submitDesc;		//送审原因
	private String oaApproveId;		//oa审批id
	private String approveRole;		//oa审批人角色
	private String isOaApprove;		//是否在oa审批
	public String getApproverName() {
		return approverName;
	}
	public void setApproverName(String approverName) {
		this.approverName = approverName;
	}
	public String getPosNo() {
		return posNo;
	}
	public void setPosNo(String posNo) {
		this.posNo = posNo;
	}
	public String getSubmitNo() {
		return submitNo;
	}
	public void setSubmitNo(String submitNo) {
		this.submitNo = submitNo;
	}
	public String getApproveNo() {
		return approveNo;
	}
	public void setApproveNo(String approveNo) {
		this.approveNo = approveNo;
	}
	public String getSubmiter() {
		return submiter;
	}
	public void setSubmiter(String submiter) {
		this.submiter = submiter;
	}
	public String getApprover() {
		return approver;
	}
	public void setApprover(String approver) {
		this.approver = approver;
	}
	public Date getSubmitDate() {
		return submitDate;
	}
	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}
	public Date getApproveDate() {
		return approveDate;
	}
	public void setApproveDate(Date approveDate) {
		this.approveDate = approveDate;
	}
	public String getApproveDesc() {
		return approveDesc;
	}
	public void setApproveDesc(String approveDesc) {
		this.approveDesc = approveDesc;
	}

	public String getApproveOption() {
		return approveOption;
	}
	public void setApproveOption(String approveOption) {
		this.approveOption = approveOption;
	}
	public String getSubmitDesc() {
		return submitDesc;
	}
	public void setSubmitDesc(String submitDesc) {
		this.submitDesc = submitDesc;
	}
	public String getOaApproveId() {
		return oaApproveId;
	}
	public void setOaApproveId(String oaApproveId) {
		this.oaApproveId = oaApproveId;
	}
	public String getApproveRole() {
		return approveRole;
	}
	public void setApproveRole(String approveRole) {
		this.approveRole = approveRole;
	}
	public String getIsOaApprove() {
		return isOaApprove;
	}
	public void setIsOaApprove(String isOaApprove) {
		this.isOaApprove = isOaApprove;
	}
}
