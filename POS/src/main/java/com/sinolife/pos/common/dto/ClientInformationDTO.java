package com.sinolife.pos.common.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;

import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

public class ClientInformationDTO extends PosComDTO {

	private static final long serialVersionUID = -2115234322533238705L;
	
	@Id
	private String clientNo;				//客户号
	private String clientName;				//客户姓名
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birthday;					//生日
	private String sexCode;					//性别代码
	private String sexDesc;					//性别描述
	private String idTypeCode;				//证件类型代码
	private String idTypeDesc;				//证件类型描述
	private String idNo;					//证件号码
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date idnoValidityDate;			//证件有效截止日期
	private boolean isLongidnoValidityDate;	 //是否长期有效
	private String nationCode;				//民族代码
	private String nationDesc;				//民族代码描述
	private String countryCode;				//国籍代码
	private String countryDesc;				//国籍描述
	private String educationCode;			//学历代码
	private String educationDesc;			//学历描述
	private String marriageCode;			//婚姻状况代码
	private String marriageDesc;			//婚姻状况描述
	private String occupationCode;			//职业代码
	private String occupationDesc;			//职业描述
	private String occupationGradeCode;		//职业等级代码
	private String occupationGradeDesc;		//职业等级描述
	private String pkSerial;				//业务流水号
	private String workUnit;				//工作单位
	private String position;				//职位
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date deathDate;					//死亡日期
	private String registerPlace;			//户籍
	private String phoneticizeLastName;		//姓拼音
	private String phoneticizeFirstName;	//名拼音
	
	
	private String vipGrade;				//VIP级别
	private String vipGradeDesc;			//vip级别描述
	private List<ClientAddressDTO> clientAddressList = new ArrayList<ClientAddressDTO>();				//客户地址列表
	private List<ClientPhoneDTO> clientPhoneList = new ArrayList<ClientPhoneDTO>();						//客户电话列表
	private List<ClientTouchHistoryDTO> clientTouchHistoryList = new ArrayList<ClientTouchHistoryDTO>();//客户接触历史列表
	private List<ClientQualityDTO> clientQualityList = new ArrayList<ClientQualityDTO>();				//客户品质列表
	private List<String> clientProductList = new ArrayList<String>();									//客户产品列表
	private List<ClientEmailDTO> clientEmailList = new ArrayList<ClientEmailDTO>();						//客户EMAIL列表
	
	/**
	 * l_pos_pub_interface.pub_ins_client_information_pro
	 * 新增客户信息的接口返回参数为客户信息列表
	 * 此字段就是为了接收这个返回参数而定义的
	 */
	private List<ClientInformationDTO> clientsReturn;
	
	/**
	 * 获取字符串格式电话明细
	 * @return
	 */
	public String getPhoneNoString() {
		StringBuffer sbPhone = new StringBuffer();
		if(clientPhoneList != null && !clientPhoneList.isEmpty()) {
			for(ClientPhoneDTO phone : clientPhoneList) {
				if(sbPhone.length() > 0)
					sbPhone.append(";");
				
				if(StringUtils.isNotBlank(phone.getPhoneTypeDesc()))
					sbPhone.append(phone.getPhoneTypeDesc()).append(":");
				
				if(StringUtils.isNotBlank(phone.getAreaNo()))
					sbPhone.append(phone.getAreaNo()).append("-");
				
				sbPhone.append(phone.getPhoneNo());
			}
		}
		return sbPhone.toString();
	}
	
	/**
	 * 获取字符串格式地址明细
	 * @return
	 */
	public String getFullAddressString() {
		StringBuffer sbAddress = new StringBuffer();
		if(clientAddressList != null && !clientAddressList.isEmpty()) {
			for(ClientAddressDTO address : clientAddressList) {
				if(sbAddress.length() > 0)
					sbAddress.append(";");
				if(StringUtils.isNotBlank(address.getAddressTypeDesc()))
					sbAddress.append(address.getAddressTypeDesc()).append(":");
				sbAddress.append(address.getFullAddress());
			}
		}
		return sbAddress.toString();
	}
	
	/**
	 * 取得首选电话字符串
	 * @return
	 */
	public String getFirstPhoneList() {
		StringBuffer sb = new StringBuffer();
		if(clientPhoneList != null && !clientPhoneList.isEmpty()) {
			for(ClientPhoneDTO phone : clientPhoneList) {
				if(phone.getIsFirstPhone() != null && phone.getIsFirstPhone().intValue() == 1) {
					if(sb.length() > 0)
						sb.append(";");
					if(StringUtils.isNotBlank(phone.getPhoneTypeDesc()))
						sb.append(phone.getPhoneTypeDesc()).append(":");
					if(StringUtils.isNotBlank(phone.getAreaNo()))
						sb.append(phone.getAreaNo()).append("-");
					sb.append(phone.getPhoneNo());
				}
			}
		}
		return sb.toString();
	}
	
	/* getters and setters begin */
	
	public List<ClientAddressDTO> getClientAddressList() {
		return clientAddressList;
	}

	public void setClientAddressList(List<ClientAddressDTO> clientAddressList) {
		this.clientAddressList = clientAddressList;
	}
	
	public List<ClientPhoneDTO> getClientPhoneList() {
		return clientPhoneList;
	}

	public void setClientPhoneList(List<ClientPhoneDTO> clientPhoneList) {
		this.clientPhoneList = clientPhoneList;
	}

	public String getClientNo() {
		return clientNo;
	}

	public void setClientNo(String clientNo) {
		this.clientNo = clientNo;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public java.util.Date getBirthday() {
		return birthday;
	}

	public void setBirthday(java.util.Date birthday) {
		this.birthday = birthday;
	}

	public String getSexCode() {
		return sexCode;
	}

	public void setSexCode(String sexCode) {
		this.sexCode = sexCode;
	}

	public String getSexDesc() {
		return sexDesc;
	}

	public void setSexDesc(String sexDesc) {
		this.sexDesc = sexDesc;
	}

	public String getIdTypeCode() {
		return idTypeCode;
	}

	public void setIdTypeCode(String idTypeCode) {
		this.idTypeCode = idTypeCode;
	}

	public String getIdTypeDesc() {
		return idTypeDesc;
	}

	public void setIdTypeDesc(String idTypeDesc) {
		this.idTypeDesc = idTypeDesc;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public java.util.Date getIdnoValidityDate() {
		return idnoValidityDate;
	}

	public void setIdnoValidityDate(java.util.Date idnoValidityDate) {
		this.idnoValidityDate = idnoValidityDate;
	}

	public String getNationCode() {
		return nationCode;
	}

	public void setNationCode(String nationCode) {
		this.nationCode = nationCode;
	}

	public String getNationDesc() {
		return nationDesc;
	}

	public void setNationDesc(String nationDesc) {
		this.nationDesc = nationDesc;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryDesc() {
		return countryDesc;
	}

	public void setCountryDesc(String countryDesc) {
		this.countryDesc = countryDesc;
	}

	public String getEducationCode() {
		return educationCode;
	}

	public void setEducationCode(String educationCode) {
		this.educationCode = educationCode;
	}

	public String getEducationDesc() {
		return educationDesc;
	}

	public void setEducationDesc(String educationDesc) {
		this.educationDesc = educationDesc;
	}

	public String getMarriageCode() {
		return marriageCode;
	}

	public void setMarriageCode(String marriageCode) {
		this.marriageCode = marriageCode;
	}

	public String getMarriageDesc() {
		return marriageDesc;
	}

	public void setMarriageDesc(String marriageDesc) {
		this.marriageDesc = marriageDesc;
	}

	public String getOccupationCode() {
		return occupationCode;
	}

	public void setOccupationCode(String occupationCode) {
		this.occupationCode = occupationCode;
	}

	public String getOccupationDesc() {
		return occupationDesc;
	}

	public void setOccupationDesc(String occupationDesc) {
		this.occupationDesc = occupationDesc;
	}

	public String getPkSerial() {
		return pkSerial;
	}

	public void setPkSerial(String pkSerial) {
		this.pkSerial = pkSerial;
	}

	public String getWorkUnit() {
		return workUnit;
	}

	public void setWorkUnit(String workUnit) {
		this.workUnit = workUnit;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public java.util.Date getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(java.util.Date deathDate) {
		this.deathDate = deathDate;
	}

	public String getRegisterPlace() {
		return registerPlace;
	}

	public void setRegisterPlace(String registerPlace) {
		this.registerPlace = registerPlace;
	}

	public String getPhoneticizeLastName() {
		return phoneticizeLastName;
	}

	public void setPhoneticizeLastName(String phoneticizeLastName) {
		this.phoneticizeLastName = phoneticizeLastName;
	}

	public String getPhoneticizeFirstName() {
		return phoneticizeFirstName;
	}

	public void setPhoneticizeFirstName(String phoneticizeFirstName) {
		this.phoneticizeFirstName = phoneticizeFirstName;
	}

	public List<ClientTouchHistoryDTO> getClientTouchHistoryList() {
		return clientTouchHistoryList;
	}

	public void setClientTouchHistoryList(
			List<ClientTouchHistoryDTO> clientTouchHistoryList) {
		this.clientTouchHistoryList = clientTouchHistoryList;
	}

	public List<ClientQualityDTO> getClientQualityList() {
		return clientQualityList;
	}

	public void setClientQualityList(List<ClientQualityDTO> clientQualityList) {
		this.clientQualityList = clientQualityList;
	}

	public String getOccupationGradeCode() {
		return occupationGradeCode;
	}

	public void setOccupationGradeCode(String occupationGradeCode) {
		this.occupationGradeCode = occupationGradeCode;
	}

	public String getOccupationGradeDesc() {
		return occupationGradeDesc;
	}

	public void setOccupationGradeDesc(String occupationGradeDesc) {
		this.occupationGradeDesc = occupationGradeDesc;
	}

	public List<ClientEmailDTO> getClientEmailList() {
		return clientEmailList;
	}

	public void setClientEmailList(List<ClientEmailDTO> clientEmailList) {
		this.clientEmailList = clientEmailList;
	}

	public String getVipGrade() {
		return vipGrade;
	}

	public void setVipGrade(String vipGrade) {
		this.vipGrade = vipGrade;
	}

	public String getVipGradeDesc() {
		return vipGradeDesc;
	}

	public void setVipGradeDesc(String vipGradeDesc) {
		this.vipGradeDesc = vipGradeDesc;
	}

	public List<ClientInformationDTO> getClientsReturn() {
		return clientsReturn;
	}

	public void setClientsReturn(List<ClientInformationDTO> clientsReturn) {
		this.clientsReturn = clientsReturn;
	}

	public List<String> getClientProductList() {
		return clientProductList;
	}

	public void setClientProductList(List<String> clientProductList) {
		this.clientProductList = clientProductList;
	}

	public boolean getIsLongidnoValidityDate() {
		return isLongidnoValidityDate;
	}

	public void setIsLongidnoValidityDate(boolean isLongidnoValidityDate) {
		this.isLongidnoValidityDate = isLongidnoValidityDate;
	}

}
