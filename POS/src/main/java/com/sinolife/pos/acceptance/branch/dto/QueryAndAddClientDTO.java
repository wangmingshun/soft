package com.sinolife.pos.acceptance.branch.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.sinolife.pos.common.dto.ClientAddressDTO;
import com.sinolife.pos.common.dto.ClientEmailDTO;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.ClientPhoneDTO;
import com.sinolife.pos.common.dto.PolicyContactInfoDTO;
import com.sinolife.pos.common.util.PosUtils;

/**
 * 新增客户相关信息DTO
 */

public class QueryAndAddClientDTO implements Serializable {

	private static final long serialVersionUID = -3584716542958492055L;
	private ClientInformationDTO clientInfo;		//客户信息
	private ClientInformationDTO queryCriteria;		//五项信息查询条件
	private ClientInformationDTO originClientInfo;	//原始客户信息
	private PolicyContactInfoDTO contactInfo;		//联系信息
	private PolicyContactInfoDTO originContactInfo;	//原始联系信息
	private String clientNo;						//客户号
	private String policyNo;						//保单号
	private String message;							//页面提示信息
	private String posBatchNo;						//批次号
	private String posNo;							//保全号
	private BigDecimal yearIncome;					//年收入
	private boolean newClientAdded;					//是否新增了客户
	private String rewriteClientNoPropName;			//客户号回写字段名称
	private String rewriteQAACPropName;				//QueryAndAddClientDTO回写字段名称
	
	private String displayPolicyContact;			//是否显示新保单联系方式信息标志
	private String newPolicyFlag;					//是否为新保单联系方式标志
	

	public List<String> getClientNoList() {
		return clientNoList;
	}

	public void setClientNoList(List<String> clientNoList) {
		this.clientNoList = clientNoList;
	}

	private List<String> clientNoList = new ArrayList<String>();	
	public void appendMessage(String message) {
		this.message += ";" + message;
	}
	
	public void updateOriginClientInfo() {
		originClientInfo = (ClientInformationDTO) PosUtils.deepCopy(clientInfo);
	}
	
	public void updateOriginContactInfo() {
		originContactInfo = (PolicyContactInfoDTO) PosUtils.deepCopy(contactInfo);
	}
	
	public void updateClientNo() {
		List<ClientPhoneDTO> phoneList = clientInfo.getClientPhoneList();
		List<ClientAddressDTO> addressList = clientInfo.getClientAddressList();
		List<ClientEmailDTO> emailList = clientInfo.getClientEmailList();
		
		for (int i = 0; phoneList!=null && i < phoneList.size(); i++) {
			if(StringUtils.isBlank(phoneList.get(i).getClientNo())){
				phoneList.get(i).setClientNo(clientNo);
			}
		}
		for (int i = 0; addressList!=null && i < addressList.size(); i++) {
			if(StringUtils.isBlank(addressList.get(i).getClientNo())){
				addressList.get(i).setClientNo(clientNo);
			}
		}
		for (int i = 0; emailList!=null && i < emailList.size(); i++) {
			if(StringUtils.isBlank(emailList.get(i).getClientNo())){
				emailList.get(i).setClientNo(clientNo);
			}
		}
	}
	
	/**
	 * 联系电话提交以后
	 * 保单的联系信息里更新seq
	 */
	public void updateContactSeq(){
		if(contactInfo != null) {
			contactInfo.setPhoneSeq(realPhoneSeq(clientInfo.getClientPhoneList(), contactInfo.getPhoneSeq()));
			contactInfo.setAddressSeq(realAddressSeq(clientInfo.getClientAddressList(), contactInfo.getAddressSeq()));
			contactInfo.setEmailSeq(realEmailSeq(clientInfo.getClientEmailList(), contactInfo.getEmailSeq()));
		}
	}
	/**
	 * 客户电话信息插入后，有了真正的seq
	 * @param phoneList
	 * @param phoneSeq
	 * @return
	 */
	private String realPhoneSeq(List<ClientPhoneDTO> phoneList, String phoneSeq){
		for (int i = 0; phoneList!=null && i < phoneList.size(); i++) {
			ClientPhoneDTO phone = phoneList.get(i);
			if(("FAKE_"+phone.getPhoneType()+"-"+(phone.getAreaNo()==null?"":phone.getAreaNo())+"-"+phone.getPhoneNo()).equals(phoneSeq)){
				phoneSeq = phone.getPhoneSeq();
				break;
			}
		}
		return phoneSeq;
	}
	private String realAddressSeq(List<ClientAddressDTO> addressList, String addressSeq){
		for (int i = 0; addressList!=null && i < addressList.size(); i++) {
			ClientAddressDTO address = addressList.get(i);
			if(("FAKE_"+address.getProvinceCode()+"-"+
					   (address.getCityCode()==null?"":address.getCityCode())+"-"+
					   (address.getAreaCode()==null?"":address.getAreaCode())+"-"+
					    address.getDetailAddress()).equals(addressSeq)){
				addressSeq = address.getAddressSeq();
				break;
			}
		}
		return addressSeq;
	}
	private String realEmailSeq(List<ClientEmailDTO> emailList, String emailSeq){
		for (int i = 0; emailList!=null && i < emailList.size(); i++) {
			ClientEmailDTO email = emailList.get(i);
			if(("FAKE_"+email.getEmailAddress()).equals(emailSeq)){
				emailSeq = email.getEmailSeq();
				break;
			}
		}
		return emailSeq;
	}
	
	public ClientInformationDTO getClientInfo() {
		return clientInfo;
	}

	public void setClientInfo(ClientInformationDTO clientInfo) {
		this.clientInfo = clientInfo;
	}

//	public String getServiceItems() {
//		return serviceItems;
//	}
//
//	public void setServiceItems(String serviceItems) {
//		this.serviceItems = serviceItems;
//	}

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

	public PolicyContactInfoDTO getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(PolicyContactInfoDTO contactInfo) {
		this.contactInfo = contactInfo;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


	public ClientInformationDTO getQueryCriteria() {
		return queryCriteria;
	}


	public void setQueryCriteria(ClientInformationDTO queryCriteria) {
		this.queryCriteria = queryCriteria;
	}

	public String getPosBatchNo() {
		return posBatchNo;
	}

	public void setPosBatchNo(String posBatchNo) {
		this.posBatchNo = posBatchNo;
	}

	public String getPosNo() {
		return posNo;
	}

	public void setPosNo(String posNo) {
		this.posNo = posNo;
	}

	public BigDecimal getYearIncome() {
		return yearIncome;
	}

	public void setYearIncome(BigDecimal yearIncome) {
		this.yearIncome = yearIncome;
	}

	public ClientInformationDTO getOriginClientInfo() {
		return originClientInfo;
	}

	public void setOriginClientInfo(ClientInformationDTO originClientInfo) {
		this.originClientInfo = originClientInfo;
	}

	public PolicyContactInfoDTO getOriginContactInfo() {
		return originContactInfo;
	}

	public void setOriginContactInfo(PolicyContactInfoDTO originContactInfo) {
		this.originContactInfo = originContactInfo;
	}

	public boolean isNewClientAdded() {
		return newClientAdded;
	}

	public void setNewClientAdded(boolean newClientAdded) {
		this.newClientAdded = newClientAdded;
	}

	public String getRewriteClientNoPropName() {
		return rewriteClientNoPropName;
	}

	public void setRewriteClientNoPropName(String rewriteClientNoPropName) {
		this.rewriteClientNoPropName = rewriteClientNoPropName;
	}

	public String getRewriteQAACPropName() {
		return rewriteQAACPropName;
	}

	public void setRewriteQAACPropName(String rewriteQAACPropName) {
		this.rewriteQAACPropName = rewriteQAACPropName;
	}

	public String getDisplayPolicyContact() {
		return displayPolicyContact;
	}

	public void setDisplayPolicyContact(String displayPolicyContact) {
		this.displayPolicyContact = displayPolicyContact;
	}

	public String getNewPolicyFlag() {
		return newPolicyFlag;
	}

	public void setNewPolicyFlag(String newPolicyFlag) {
		this.newPolicyFlag = newPolicyFlag;
	}

}
