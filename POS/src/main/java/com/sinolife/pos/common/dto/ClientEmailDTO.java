package com.sinolife.pos.common.dto;

import org.apache.commons.lang.StringUtils;

/**
 * 客户EMAIL明细
 */
public class ClientEmailDTO extends PosComDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String emailSeq;		//EMAIL序号
	private String emailType;		//EMAIL类型
	private String emailTypeDesc;	//EMAIL类型描述
	private String clientNo;		//EMAIL所有者客户号
	private String emailAddress;	//EMAIL地址
	private Integer priLevel;		//EMAIL优先级
	private Integer emailStatus;	//EMAIL状态
	
	private String checked;			//页面是否选中
	
	public String getTypeAndAddress(){
		return emailTypeDesc + "：" + emailAddress;
	}
	public Integer getPriLevel() {
		if(priLevel==null){
			//保全目前没有优先级别的概念，都是1
			priLevel = 1;
		}		
		return priLevel;
	}
	public void setPriLevel(Integer priLevel) {
		this.priLevel = priLevel;
	}
	public String getEmailType() {
		if(StringUtils.isBlank(emailType)){
			emailType="1";
		}
		return emailType;
	}
	public void setEmailType(String emailType) {
		this.emailType = emailType;
	}
	public String getClientNo() {
		return clientNo;
	}
	public void setClientNo(String clientNo) {
		this.clientNo = clientNo;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getEmailTypeDesc() {
		return emailTypeDesc;
	}
	public void setEmailTypeDesc(String emailTypeDesc) {
		this.emailTypeDesc = emailTypeDesc;
	}
	public String getChecked() {
		return checked;
	}
	public void setChecked(String checked) {
		this.checked = checked;
	}
	public Integer getEmailStatus() {
		return emailStatus;
	}
	public void setEmailStatus(Integer emailStatus) {
		this.emailStatus = emailStatus;
	}
	public String getEmailSeq() {
		return emailSeq;
	}
	public void setEmailSeq(String emailSeq) {
		this.emailSeq = emailSeq;
	}
	
	public String getFakeSeq(){
		return "FAKE_"+this.emailAddress;
	}
}
