package com.sinolife.pos.pubInterface.biz.dto;

import java.util.List;

import com.sinolife.pos.common.dto.ClientAddressDTO;
import com.sinolife.pos.common.dto.ClientEmailDTO;
import com.sinolife.pos.common.dto.ClientPhoneDTO;
import com.sinolife.pos.common.dto.PolicyContactInfoDTO;

public class EsbServiceItemsInputDTO_19 extends EsbServiceItemsInputDTO {

	/**
	 * 联系方式变更
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 客户号
	 */
	private String clientNo;
	
	/**
	 * 客户姓名
	 */
	private String clientName;
	
	/**
	 * 电话信息 可以多个
	 */
	private List<ClientPhoneDTO> phoneList;
	
	/**
	 * 地址信息 只让录入一个
	 */
	private ClientAddressDTO address;
	
	/**
	 * 电邮信息 只让录入一个
	 */
	private ClientEmailDTO email;
	/**
	 * 保单联系信息
	 */
	private List<EsbPolicyContactInfoDTO> policyContactList;
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

	public List<ClientPhoneDTO> getPhoneList() {
		return phoneList;
	}

	public void setPhoneList(List<ClientPhoneDTO> phoneList) {
		this.phoneList = phoneList;
	}

	public ClientAddressDTO getAddress() {
		return address;
	}

	public void setAddress(ClientAddressDTO address) {
		this.address = address;
	}

	public ClientEmailDTO getEmail() {
		return email;
	}

	public void setEmail(ClientEmailDTO email) {
		this.email = email;
	}

	public List<EsbPolicyContactInfoDTO> getPolicyContactList() {
		return policyContactList;
	}

	public void setPolicyContactList(List<EsbPolicyContactInfoDTO> policyContactList) {
		this.policyContactList = policyContactList;
	}
}
