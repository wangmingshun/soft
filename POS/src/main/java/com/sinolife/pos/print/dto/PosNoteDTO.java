package com.sinolife.pos.print.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 保全通知书临时表主表
 */

public class PosNoteDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2415823958656400453L;
	private String noteType;				//通知书类型
	private String clientNo;				//客户号
	private String policyNo;				//保单号
	private Date businessDate;				//业务日期
	private String detailSequenceNo;		//明细信息序列号
	private String templateVersion;			//模板版本号
	private String clientName;				//客户姓名
	private String clientAddress;			//客户地址
	private String clientPostalcode;		//客户邮编
	private String companyServiceName;		//分公司客服中心名称
	private String companyServiceAddress;	//分公司客服中心地址
	private String companyServicePostalcode;//分公司客服中心邮编
	private String companyServiceTelephone;	//分公司客服中心服务电话
	private String companyName;				//分公司名称
	private Date printDate;					//打印日期
	private String serviceTelephoneA;		//全国服务热线（短号）
	private String serviceTelephoneB;		//全国服务热线（400号码）
	private String returnFlag;				//退信标志
	private String returnReason;			//退信原因
	
	private String isLocal;					//是否为本地邮编
	private String businessDateString;		//日期字符串
	
	private Map<String, Object> detailMap;	//明细信息Map

	public String getNoteType() {
		return noteType;
	}

	public void setNoteType(String noteType) {
		this.noteType = noteType;
	}

	public String getClientNo() {
		return clientNo;
	}

	public void setClientNo(String clientNo) {
		this.clientNo = clientNo;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public Date getBusinessDate() {
		return businessDate;
	}

	public void setBusinessDate(Date businessDate) {
		this.businessDate = businessDate;
	}

	public String getDetailSequenceNo() {
		return detailSequenceNo;
	}

	public void setDetailSequenceNo(String detailSequenceNo) {
		this.detailSequenceNo = detailSequenceNo;
	}

	public String getTemplateVersion() {
		return templateVersion;
	}

	public void setTemplateVersion(String templateVersion) {
		this.templateVersion = templateVersion;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getClientPostalcode() {
		return clientPostalcode;
	}

	public void setClientPostalcode(String clientPostalcode) {
		this.clientPostalcode = clientPostalcode;
	}

	public String getCompanyServiceName() {
		return companyServiceName;
	}

	public void setCompanyServiceName(String companyServiceName) {
		this.companyServiceName = companyServiceName;
	}

	public String getCompanyServiceAddress() {
		return companyServiceAddress;
	}

	public void setCompanyServiceAddress(String companyServiceAddress) {
		this.companyServiceAddress = companyServiceAddress;
	}

	public String getCompanyServicePostalcode() {
		return companyServicePostalcode;
	}

	public void setCompanyServicePostalcode(String companyServicePostalcode) {
		this.companyServicePostalcode = companyServicePostalcode;
	}

	public String getCompanyServiceTelephone() {
		return companyServiceTelephone;
	}

	public void setCompanyServiceTelephone(String companyServiceTelephone) {
		this.companyServiceTelephone = companyServiceTelephone;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Date getPrintDate() {
		return printDate;
	}

	public void setPrintDate(Date printDate) {
		this.printDate = printDate;
	}

	public String getServiceTelephoneA() {
		return serviceTelephoneA;
	}

	public void setServiceTelephoneA(String serviceTelephoneA) {
		this.serviceTelephoneA = serviceTelephoneA;
	}

	public String getServiceTelephoneB() {
		return serviceTelephoneB;
	}

	public void setServiceTelephoneB(String serviceTelephoneB) {
		this.serviceTelephoneB = serviceTelephoneB;
	}

	public String getReturnFlag() {
		return returnFlag;
	}

	public void setReturnFlag(String returnFlag) {
		this.returnFlag = returnFlag;
	}

	public String getReturnReason() {
		return returnReason;
	}

	public void setReturnReason(String returnReason) {
		this.returnReason = returnReason;
	}

	public Map<String, Object> getDetailMap() {
		return detailMap;
	}

	public void setDetailMap(Map<String, Object> detailMap) {
		this.detailMap = detailMap;
	}

	public String getIsLocal() {
		return isLocal;
	}

	public void setIsLocal(String isLocal) {
		this.isLocal = isLocal;
	}

	public String getBusinessDateString() {
		return businessDateString;
	}

	public void setBusinessDateString(String businessDateString) {
		this.businessDateString = businessDateString;
	}
	
}
