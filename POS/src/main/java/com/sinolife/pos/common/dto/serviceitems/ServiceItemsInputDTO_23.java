package com.sinolife.pos.common.dto.serviceitems;

import java.util.List;

import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.ClientAccountDTO;
import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.PolicyPremInfoDTO;

/**
 * 23 续期交费方式变更
 */
public class ServiceItemsInputDTO_23 extends ServiceItemsInputDTO{

	private static final long serialVersionUID = 3134643490632047513L;

	/**
	 * 保单的投保人，被保人账户信息
	 */
	private ClientAccountDTO account;
	
	private String applicantName;
	
	/**
	 * 保单列表
	 */
	private List<PolicyPremInfoDTO> policyPremList;
	
	/**
	 * 缴费方式
	 */
	private String chargingMethod;
	
	public boolean isCheckedCreditCard() {
		return checkedCreditCard;
	}

	public void setCheckedCreditCard(boolean checkedCreditCard) {
		this.checkedCreditCard = checkedCreditCard;
	}

	private boolean checkedCreditCard;
	

	
	//银行卡类型列表
	private List<CodeTableItemDTO> accountNoTypeList;
	
	
	//银行卡所属省份列表
	private List<CodeTableItemDTO> bankProvinceList;
	
	
	//银行名称列表
	private List<CodeTableItemDTO> bankCategoryList;
	
	public List<CodeTableItemDTO> getBankCategoryList() {
		return bankCategoryList;
	}

	public void setBankCategoryList(List<CodeTableItemDTO> bankCategoryList) {
		this.bankCategoryList = bankCategoryList;
	}

	public List<CodeTableItemDTO> getBankProvinceList() {
		return bankProvinceList;
	}

	public void setBankProvinceList(List<CodeTableItemDTO> bankProvinceList) {
		this.bankProvinceList = bankProvinceList;
	}

	public List<CodeTableItemDTO> getAccountNoTypeList() {
		return accountNoTypeList;
	}

	public void setAccountNoTypeList(List<CodeTableItemDTO> accountNoTypeList) {
		this.accountNoTypeList = accountNoTypeList;
	}


	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}
	
	public ClientAccountDTO getAccount() {
		return account;
	}
	public void setAccount(ClientAccountDTO account) {
		this.account = account;
	}

	public List<PolicyPremInfoDTO> getPolicyPremList() {
		return policyPremList;
	}

	public void setPolicyPremList(List<PolicyPremInfoDTO> policyPremList) {
		this.policyPremList = policyPremList;
	}

	public String getChargingMethod() {
		return chargingMethod;
	}

	public void setChargingMethod(String chargingMethod) {
		this.chargingMethod = chargingMethod;
	}

	public String getApplicantName() {
		return applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

}
