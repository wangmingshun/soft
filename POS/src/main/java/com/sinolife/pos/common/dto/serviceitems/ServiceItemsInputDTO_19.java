package com.sinolife.pos.common.dto.serviceitems;

import java.util.List;

import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.ClientAddressDTO;
import com.sinolife.pos.common.dto.ClientEmailDTO;
import com.sinolife.pos.common.dto.ClientPhoneDTO;
import com.sinolife.pos.common.dto.PolicyContactInfoDTO;

/**
 * 19 客户联系方式变更
 * @author wangkui
 *
 */
public class ServiceItemsInputDTO_19 extends ServiceItemsInputDTO{

	private static final long serialVersionUID = 1039859011651318976L;

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
	
	
	
	/*是否寄送邮件
	 * 
	 */
	public String getIsSendLetter() {
		return isSendLetter;
	}

	public void setIsSendLetter(String isSendLetter) {
		this.isSendLetter = isSendLetter;
	}

	private String   isSendLetter;
	
	/**
	 * 保单联系信息
	 */
	private List<PolicyContactInfoDTO> policyContactList;
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}
	
	public List<ClientPhoneDTO> getPhoneList() {
		return phoneList;
	}

	public void setPhoneList(List<ClientPhoneDTO> phoneList) {
		this.phoneList = phoneList;
	}

	public List<PolicyContactInfoDTO> getPolicyContactList() {
		return policyContactList;
	}

	public void setPolicyContactList(List<PolicyContactInfoDTO> policyContactList) {
		this.policyContactList = policyContactList;
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
}
