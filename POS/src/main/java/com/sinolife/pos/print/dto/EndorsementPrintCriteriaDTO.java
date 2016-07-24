package com.sinolife.pos.print.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 批单打印查询条件
 */

public class EndorsementPrintCriteriaDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6741572817160416887L;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date effectDateStart;			//生效日期开始
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date effectDateEnd;				//生效日期结束	
	private String acceptChannelCode;		//受理渠道
	private String policyChannelCode;       //保单渠道
	private String acceptBranchCode;		//受理机构
	private String acceptor;				//受理人
	private String serviceItems;			//服务项目
	private String posNo;					//保全号码
	private String approvalServiceType;     //批单寄送发送
	private String submitUserId;		    //提交用户UM ID
	private String singlePrintFlag;		//逐单打印标记
	private String printOptions;		// 打印选项
	
	public String getSinglePrintFlag() {
		return singlePrintFlag;
	}

	public void setSinglePrintFlag(String singlePrintFlag) {
		this.singlePrintFlag = singlePrintFlag;
	}

	public String getSubmitUserId() {
		return submitUserId;
	}

	public void setSubmitUserId(String submitUserId) {
		this.submitUserId = submitUserId;
	}

	public String getPolicyChannelCode() {
		return policyChannelCode;
	}

	public void setPolicyChannelCode(String policyChannelCode) {
		this.policyChannelCode = policyChannelCode;
	}
	public String getApprovalServiceType() {
		return approvalServiceType;
	}

	public void setApprovalServiceType(String approvalServiceType) {
		this.approvalServiceType = approvalServiceType;
	}

	public String[] getServiceItemsArr() {
		if(StringUtils.isNotBlank(serviceItems)) {
			return serviceItems.split("\\,");
		}
		return null;
	}
	
	public String getAcceptBranchCode() {
		return acceptBranchCode;
	}
	public void setAcceptBranchCode(String acceptBranchCode) {
		this.acceptBranchCode = acceptBranchCode;
	}
	public String getAcceptor() {
		return acceptor;
	}
	public void setAcceptor(String acceptor) {
		this.acceptor = acceptor;
	}
	public String getAcceptChannelCode() {
		return acceptChannelCode;
	}
	public void setAcceptChannelCode(String acceptChannelCode) {
		this.acceptChannelCode = acceptChannelCode;
	}
	public String getServiceItems() {
		return serviceItems;
	}
	public void setServiceItems(String serviceItems) {
		this.serviceItems = serviceItems;
	}
	public String getPosNo() {
		return posNo;
	}
	public void setPosNo(String posNo) {
		this.posNo = posNo;
	}

	public Date getEffectDateStart() {
		return effectDateStart;
	}
	public void setEffectDateStart(Date effectDateStart) {
		this.effectDateStart = effectDateStart;
	}
	public Date getEffectDateEnd() {
		return effectDateEnd;
	}
	public void setEffectDateEnd(Date effectDateEnd) {
		this.effectDateEnd = effectDateEnd;
	}

	public String getPrintOptions() {
		return printOptions;
	}

	public void setPrintOptions(String printOptions) {
		this.printOptions = printOptions;
	}
	
}
