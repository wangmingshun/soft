package com.sinolife.pos.print.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 通知书打印查询条件
 */
public class NoticePrintCriteriaDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String branchCode;		//机构代码
	private String noticeType;		//通知书类型
	private String policyNo;		//保单号
	private Date businessDateStart;	//通知书开始时间
	private Date businessDateEnd;	//通知书结束时间
	private Date queryDateStart;	//查询开始时间
	private Date queryDateEnd;		//查询结束时间
	private String singlePrintFlag;	//逐单打印标志
	private String noticeYear;		//通知书年度
	private String detailSequenceNo;//通知书ID
	private String serviceNo;       //保单服务人员工号
	private String policyChannel;   //保单渠道
	private String isELetterFlag;   //电子信函标志		
	
	public String getIsELetterFlag() {
        return isELetterFlag;
    }
    public void setIsELetterFlag(String isELetterFlag) {
        this.isELetterFlag = isELetterFlag;
    }
    public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public String getNoticeType() {
		return noticeType;
	}
	public void setNoticeType(String noticeType) {
		this.noticeType = noticeType;
	}
	public String getPolicyNo() {
		return policyNo;
	}
	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}
	public Date getBusinessDateStart() {
		return businessDateStart;
	}
	public void setBusinessDateStart(Date businessDateStart) {
		this.businessDateStart = businessDateStart;
	}
	public Date getBusinessDateEnd() {
		return businessDateEnd;
	}
	public void setBusinessDateEnd(Date businessDateEnd) {
		this.businessDateEnd = businessDateEnd;
	}
	public String getSinglePrintFlag() {
		return singlePrintFlag;
	}
	public void setSinglePrintFlag(String singlePrintFlag) {
		this.singlePrintFlag = singlePrintFlag;
	}
	public Date getQueryDateStart() {
		return queryDateStart;
	}
	public void setQueryDateStart(Date queryDateStart) {
		this.queryDateStart = queryDateStart;
	}
	public Date getQueryDateEnd() {
		return queryDateEnd;
	}
	public void setQueryDateEnd(Date queryDateEnd) {
		this.queryDateEnd = queryDateEnd;
	}
	public String getDetailSequenceNo() {
		return detailSequenceNo;
	}
	public void setDetailSequenceNo(String detailSequenceNo) {
		this.detailSequenceNo = detailSequenceNo;
	}
	public String getNoticeYear() {
		return noticeYear;
	}
	public void setNoticeYear(String noticeYear) {
		this.noticeYear = noticeYear;
	}
	public String getServiceNo() {
		return serviceNo;
	}
	public void setServiceNo(String serviceNo) {
		this.serviceNo = serviceNo;
	}
	public String getPolicyChannel() {
		return policyChannel;
	}
	public void setPolicyChannel(String policyChannel) {
		this.policyChannel = policyChannel;
	}
}
