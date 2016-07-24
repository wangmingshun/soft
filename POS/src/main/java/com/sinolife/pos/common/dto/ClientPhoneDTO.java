package com.sinolife.pos.common.dto;

/**
 * 客户电话DTO
 */
public class ClientPhoneDTO extends PosComDTO {
	private static final long serialVersionUID = -8103261060989262829L;
	
	private String phoneSeq;			//电话序号
	private String phoneType;			//电话类型代码
	private String phoneTypeDesc;		//电话类型描述
	private String clientNo;			//客户号
	private String areaNo;				//区号
	private String phoneNo;				//电话号码
	private Integer priLevel;			//优先级
	private Integer isFirstPhone;		// 是否首选电话（1：是；0：否）
	private String phoneStatus;			//电话有效状态
	private String foreignType;         //境外标示(T:是 ，F：否)
	private String invalidReason;       //无效电话号码  
	
	/* edit by wangmingshun start */
	private String stopOriginalPhoneNo; //本次变更停用对应的原联系电话
	
	public String getStopOriginalPhoneNo() {
		return stopOriginalPhoneNo;
	}
	
	public void setStopOriginalPhoneNo(String stopOriginalPhoneNo) {
		this.stopOriginalPhoneNo = stopOriginalPhoneNo;
	}
	/* edit by wangmingshun start*/

	public Integer getPriLevel() {
		if(priLevel==null){
			//保全目前没有优先级别的概念，都是1
			priLevel = 1;
		}
		return priLevel;
	}

	public String getInvalidReason() {
		return invalidReason;
	}

	public void setInvalidReason(String invalidReason) {
		this.invalidReason = invalidReason;
	}

	public String getForeignType() {
		return foreignType;
	}

	public void setForeignType(String foreignType) {
		this.foreignType = foreignType;
	}

	public void setPriLevel(Integer priLevel) {
		this.priLevel = priLevel;
	}

	public Integer getIsFirstPhone() {
		if(isFirstPhone==null){
			//保全目前没有首选的概念，都是1
			isFirstPhone = 1;
		}		
		return isFirstPhone;
	}

	public void setIsFirstPhone(Integer isFirstPhone) {
		this.isFirstPhone = isFirstPhone;
	}

	public String getTypeAndNo(){
		String a = areaNo;
		if(a!=null && !"".equals(a)){
			a += "-";
		}else{
			a = "";
		}
		return phoneTypeDesc + "：" + a + phoneNo;
	}
	
	public String getPhoneSeq() {
		return phoneSeq;
	}

	public void setPhoneSeq(String phoneSeq) {
		this.phoneSeq = phoneSeq;
	}

	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}

	public String getPhoneTypeDesc() {
		return phoneTypeDesc;
	}

	public void setPhoneTypeDesc(String phoneTypeDesc) {
		this.phoneTypeDesc = phoneTypeDesc;
	}

	public String getClientNo() {
		return clientNo;
	}

	public void setClientNo(String clientNo) {
		this.clientNo = clientNo;
	}

	public String getAreaNo() {
		return areaNo;
	}

	public void setAreaNo(String areaNo) {
		this.areaNo = areaNo;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getPhoneStatus() {
		return phoneStatus;
	}

	public void setPhoneStatus(String phoneStatus) {
		this.phoneStatus = phoneStatus;
	}

}
