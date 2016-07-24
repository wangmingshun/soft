package com.sinolife.pos.common.dto.serviceitems;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.PolicyDTO;



/**
 * 12 年龄性别错误更正
 */
public class ServiceItemsInputDTO_12 extends ServiceItemsInputDTO {

	private static final long serialVersionUID = -8199008551881428541L;
	private ClientInformationDTO clientInfo;
	private ClientInformationDTO clientInfoNew;
	
	private List<PolicyDTO> policyList;
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#onPropertyChange()
	 */
	@Override
	protected void onPropertyChange() {
		if(clientInfo != null && clientInfoNew != null) {
			if(clientInfoNew.getBirthday() != null)
				clientInfo.setBirthday(clientInfoNew.getBirthday());
			
			if(StringUtils.isNotBlank(clientInfoNew.getSexCode()))
				clientInfo.setSexCode(clientInfoNew.getSexCode());
			
			if(StringUtils.isNotBlank(clientInfoNew.getIdTypeCode()))
				clientInfo.setIdTypeCode(clientInfoNew.getIdTypeCode());
			
			if(StringUtils.isNotBlank(clientInfoNew.getIdNo()))
				clientInfo.setIdNo(clientInfoNew.getIdNo());
		}
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}

	public ClientInformationDTO getClientInfo() {
		return clientInfo;
	}

	public void setClientInfo(ClientInformationDTO clientInfo) {
		this.clientInfo = clientInfo;
	}

	public List<PolicyDTO> getPolicyList() {
		return policyList;
	}

	public void setPolicyList(List<PolicyDTO> policyList) {
		this.policyList = policyList;
	}

	public ClientInformationDTO getClientInfoNew() {
		return clientInfoNew;
	}

	public void setClientInfoNew(ClientInformationDTO clientInfoNew) {
		this.clientInfoNew = clientInfoNew;
	}
	
}
