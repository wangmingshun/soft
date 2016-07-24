package com.sinolife.pos.common.dto.serviceitems;

import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.ClientInformationDTO;


/**
 * 21 客户资料变更
 * 
 * @author wangyunzhao
 * 
 */
public class ServiceItemsInputDTO_21 extends ServiceItemsInputDTO {

	private static final long serialVersionUID = -541100369413892200L;
	
	/*
	 * 现有客户资料
	 */
	private ClientInformationDTO clientInformationDTO;
	
	private ClientInformationDTO originClientInformationDTO;
	
	public ClientInformationDTO getClientInformationDTO() {
		return clientInformationDTO;
	}

	public void setClientInformationDTO(ClientInformationDTO clientInformationDTO) {
		this.clientInformationDTO = clientInformationDTO;
	}

	public ClientInformationDTO getOriginClientInformationDTO() {
		return originClientInformationDTO;
	}

	public void setOriginClientInformationDTO(
			ClientInformationDTO originClientInformationDTO) {
		this.originClientInformationDTO = originClientInformationDTO;
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}
}
