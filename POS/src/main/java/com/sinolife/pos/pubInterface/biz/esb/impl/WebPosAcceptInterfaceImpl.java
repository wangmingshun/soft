package com.sinolife.pos.pubInterface.biz.esb.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sinolife.esbpos.web.WebPosService;
import com.sinolife.pos.acceptance.branch.service.BranchAcceptService;
import com.sinolife.pos.acceptance.branch.service.BranchQueryService;
import com.sinolife.pos.common.dao.ClientInfoDAO;
import com.sinolife.pos.common.dao.CommonAcceptDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dao.PosCodeTableDAO;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_1;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_17;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_19;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_2;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_21;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_23;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_28;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_6;
import com.sinolife.pos.common.dto.BankInfoDTO;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.ClientPhoneDTO;
import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.PolicyContactInfoDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.common.dto.PosApplyFilesDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.PosStatusChangeHistoryDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_1;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_17;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_19;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_2;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_21;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_23;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_28;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_6;
import com.sinolife.pos.common.include.dao.IncludeDAO;
import com.sinolife.pos.others.unsuspendbankaccount.service.UnsuspendBankAccountService;
import com.sinolife.pos.pubInterface.biz.dto.EsbClientPhoneInfoDTO;
import com.sinolife.pos.pubInterface.biz.dto.EsbPolicyContactInfoDTO;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO_1;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO_17;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO_19;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO_2;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO_21;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO_23;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO_28;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO_6;
import com.sinolife.pos.pubInterface.biz.dto.PosApplyInfoDTO;
import com.sinolife.pos.pubInterface.biz.esb.service.MccpPosAcceptInterfaceEsb;
import com.sinolife.pos.pubInterface.biz.esb.service.WebPosAcceptInterfaceEsb;
import com.sinolife.pos.pubInterface.biz.impl.DevelopPlatformInterfaceImpl;
import com.sinolife.pos.pubInterface.biz.service.WebPosAcceptInterface;
import com.sinolife.sf.esb.EsbMethod;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMRuleInfoDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSVerifyResultDto;

@Service("WebPosAcceptInterfaceImpl")
public class WebPosAcceptInterfaceImpl extends DevelopPlatformInterfaceImpl
		implements WebPosAcceptInterfaceEsb {
	@Autowired
	private CommonQueryDAO commonQueryDAO;

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
	public Map<String, Object> queryAutoPolicyAddPremByWX(String policyNo, String  orderID){
		return commonQueryDAO.queryAutoPolicyAddPremByWX(policyNo, orderID);
	}
}
