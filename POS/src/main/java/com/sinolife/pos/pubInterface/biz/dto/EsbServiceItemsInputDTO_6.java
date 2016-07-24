package com.sinolife.pos.pubInterface.biz.dto;

import java.util.List;

import com.sinolife.pos.common.dto.PersonalNoticeDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;

public class EsbServiceItemsInputDTO_6 extends EsbServiceItemsInputDTO {

	/**
	 * 复效
	 */
	private static final long serialVersionUID = 1L;
	private String renewType;							//复效类型   1整单复效  2仅复效主险 3复效主险和附加险
	private PersonalNoticeDTO appPersonalNotice;				//投保人个人资料告知信息
	private PersonalNoticeDTO insPersonalNotice;				//被保人个人资料告知
	public String getRenewType() {
		return renewType;
	}
	public void setRenewType(String renewType) {
		this.renewType = renewType;
	}
	public PersonalNoticeDTO getAppPersonalNotice() {
		return appPersonalNotice;
	}
	public void setAppPersonalNotice(PersonalNoticeDTO appPersonalNotice) {
		this.appPersonalNotice = appPersonalNotice;
	}
	public PersonalNoticeDTO getInsPersonalNotice() {
		return insPersonalNotice;
	}
	public void setInsPersonalNotice(PersonalNoticeDTO insPersonalNotice) {
		this.insPersonalNotice = insPersonalNotice;
	}
    
}
