package com.sinolife.pos.pubInterface.biz.service;

import java.util.Map;

import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.pubInterface.biz.dto.PosApplyInfoDTO;

public interface BiaPosAcceptInterface {
	/**
	 * 保全受理，从申请书录入一直到生效的全部过程都在这里
	 * @param sii
	 * @param applyInfo
	 * @return
	 */
	public Map<String, Object> acceptInternal(ServiceItemsInputDTO sii, final PosApplyInfoDTO applyInfo) ;

	/**
	 * 保全受理，从申请书录入一直到生效的全部过程都在这里(传入Map形式)
	 * @param sii
	 * @param applyInfo
	 * @return
	 */
	public Map handleBiaRequest(Map map);
}
