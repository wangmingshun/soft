package com.sinolife.pos.common.dto.serviceitems;

import java.util.List;

import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.PolicyProductPremDTO;

/**
 * 15 职业等级变更
 */
public class ServiceItemsInputDTO_15 extends ServiceItemsInputDTO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7573816912247917823L;
	
	/**
	 * 客户信息
	 */
	private ClientInformationDTO client;
	
	/**
	 * 主险缴费信息
	 */
	private List<PolicyProductPremDTO> productPremList;

	/**
	 * 就职日期
	 */
	private String workStartDate;
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}

	public ClientInformationDTO getClient() {
		return client;
	}

	public void setClient(ClientInformationDTO client) {
		this.client = client;
	}

	public String getWorkStartDate() {
		return workStartDate;
	}

	public void setWorkStartDate(String workStartDate) {
		this.workStartDate = workStartDate;
	}

	public List<PolicyProductPremDTO> getProductPremList() {
		return productPremList;
	}

	public void setProductPremList(List<PolicyProductPremDTO> productPremList) {
		this.productPremList = productPremList;
	}
}
