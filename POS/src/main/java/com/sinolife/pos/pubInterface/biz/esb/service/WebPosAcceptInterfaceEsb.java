package com.sinolife.pos.pubInterface.biz.esb.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.sinolife.esbpos.web.WebPosService;
import com.sinolife.pos.common.dto.BankInfoDTO;
import com.sinolife.pos.common.dto.ClientPhoneDTO;
import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_19;
import com.sinolife.pos.pubInterface.biz.dto.EsbClientPhoneInfoDTO;
import com.sinolife.pos.pubInterface.biz.dto.EsbPolicyContactInfoDTO;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO;
import com.sinolife.pos.pubInterface.biz.dto.PosApplyInfoDTO;
import com.sinolife.sf.esb.EsbMethod;
import com.sinolife.sf.platform.runtime.PlatformContext;

public interface  WebPosAcceptInterfaceEsb {
	/**
	 * @Description: 成长红包在微信定投加保后通知微信端结果
	 * @methodName: queryAutoPolicyAddPremByWX
	 * @param policyNo  保单号
	 * @param orderID   订单号
	 * @return Map<String,Object>
	 * @param applyBarCode 投保单号
	 * @param posNo    保全号
	 * @param policy_no 保单号
	 * @param effectDate 保全生效日
	 * @param posStatus 保全状态
	 * @param preSum    加保保费
	 * @param calSum    加保保额
	 * @date 2015-9-23
	 * @throws
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.queryAutoPolicyAddPremByWX")
	public Map<String, Object> queryAutoPolicyAddPremByWX(String policyNo, String  orderID);
	
}
