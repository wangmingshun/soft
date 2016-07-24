package com.sinolife.pos.pubInterface.commonaccept;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinolife.pos.acceptance.branch.service.BranchAcceptService;
import com.sinolife.pos.acceptance.branch.service.BranchQueryService;
import com.sinolife.pos.common.dao.ClientInfoDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.common.dto.PosApplyFilesDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.PosStatusChangeHistoryDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.pubInterface.biz.dto.PosApplyInfoDTO;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMRuleInfoDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSVerifyResultDto;

@Service("commonAcceptImpl")
public class CommonAcceptImpl {
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private CommonQueryDAO commonQueryDAO;
	@Autowired
	private BranchQueryService branchQueryService;
	@Autowired
	private BranchAcceptService branchAcceptService;
	@Autowired
	private ClientInfoDAO clientInfoDAO;
	
	/**
	 * @Description: 保全受理，从申请书录入一直到生效的全部过程都在这里（传入Bean）
	 * @methodName: acceptInternal
	 * @param sii
	 * @param applyInfo
	 * @return
	 * @return Map<String,Object>
	 * @author WangMingShun
	 * @date 2015-9-22
	 * @throws
	 */
	public Map<String, Object> acceptInternal(final ServiceItemsInputDTO sii,
			final PosApplyInfoDTO applyInfo) {

		Map<String, Object> retMap = new HashMap<String, Object>();

		//写入批次记录、申请书记录，及保全记录
		PosApplyBatchDTO batch = createBatch(applyInfo);
		PosInfoDTO posInfo = new PosInfoDTO();
		//获取新生成的保全号
		String posNo = batch.getPosInfoList().get(0).getPosNo();
		sii.setPosNo(posNo);
		sii.setPosNoList(null);
		sii.setBarcodeNo(batch.getApplyFiles().get(0).getBarcodeNo());
		sii.setBatchNo(batch.getPosBatchNo());
		ServiceItemsInputDTO oriItemsInputDTO = branchQueryService
				.queryAcceptDetailInputByPosNo(posNo);
		sii.setOriginServiceItemsInputDTO(oriItemsInputDTO
				.getOriginServiceItemsInputDTO());
		sii.setClientNo(applyInfo.getClientNo());
			
		// 写受理明细
		branchAcceptService.acceptDetailInput(sii);
		List<String> posNoList = sii.getPosNoList();
		for (int i = 0; i < posNoList.size(); i++) {
			posNo = posNoList.get(i);
			// 更新备注字段
			branchAcceptService.updatePosInfo(posNo, "remark", sii.getRemark());
			// 写状态变迁记录
			PosStatusChangeHistoryDTO statusChange = new PosStatusChangeHistoryDTO();
			statusChange.setPosNo(posNo);
			statusChange.setOldStatusCode("A01");
			statusChange.setNewStatusCode("A03");
			statusChange.setChangeDate(new Date());
			statusChange.setChangeUser(batch.getAcceptor());
			branchAcceptService.insertPosStatusChangeHistory(statusChange);
			if (!"0".equals(statusChange.getFlag())) {
				String SL_RSLT_MESG = "写保全状态变更记录失败：" + statusChange.getMessage();
				branchAcceptService.cancelAccept(posInfo, batch.getAcceptor());
				throw new RuntimeException(SL_RSLT_MESG);
			}

			// 这些渠道都是逐单受理，因此前一单受理完成，更新状态为 A03待处理规则检查
			branchAcceptService.updatePosInfo(posNo, "accept_status_code", "A03");

			// 调用流转接口
			retMap = branchAcceptService.workflowControl("03", posNo, false);
			logger.info("acceptDetailInputSubmit workflowControl return:" + retMap);
			String flag = (String) retMap.get("flag");
			String msg = (String) retMap.get("message");
			String resultDesc = (String) retMap.get("resultDesc");
			retMap = branchQueryService.queryProcessResult(posNo);
			
			if ("C01".equals(flag)) {
				// 规则检查不通过
				throw new RuntimeException(msg);
			} else if ("A19".equals(flag)) {
				// 待审批判断处理
				retMap = branchAcceptService.workflowControl("05", posNo, false);
				posInfo = commonQueryDAO.queryPosInfoRecord(posNo);
				logger.info("acceptDetailInputSubmit workflowControl return:" + retMap);
				flag = (String) retMap.get("flag");
				msg = (String) retMap.get("message");
				resultDesc = (String) retMap.get("resultDesc");
			} else if ("A08".equals(flag) || "A12".equals(flag)
					|| "A15".equals(flag) || "A16".equals(flag)
					|| "E01".equals(flag)) {
				//A19 处理后这些状态下，不做任何处理
			} else {
				throw new RuntimeException(msg);
			}
		}
		
		return retMap;
	}
	
	/**
	 * @Description: 写入批次记录、申请书记录，及保全记录
	 * @methodName: createBatch
	 * @param applyInfo
	 * @return
	 * @return PosApplyBatchDTO
	 * @author WangMingShun
	 * @date 2015-9-22
	 * @throws
	 */
	private PosApplyBatchDTO createBatch(final PosApplyInfoDTO applyInfo) {
		List<ClientInformationDTO> clientInfoList = clientInfoDAO
			.selClientinfoForClientno(applyInfo.getClientNo());
		if (clientInfoList == null || clientInfoList.isEmpty()
				|| clientInfoList.size() != 1)
			throw new RuntimeException("无效的保单号：" + applyInfo.getClientNo());
		String applyType = applyInfo.getApplyType();//申请类型 
		String acceptChannelCode = applyInfo.getAcceptChannelCode();
		String approvalServiceType = applyInfo.getApprovalServiceType();
		String paymentType = applyInfo.getPaymentType();
		String clientNo = applyInfo.getClientNo();// 投保人
		String policyNo = applyInfo.getPolicyNo();
		String barcode = applyInfo.getBarCode();
		String accountNoType = applyInfo.getAccountNoType();
		//柜面号
		String counterNo = commonQueryDAO.getCounterNoByUserId(applyInfo.getAcceptor());
		
		List<String> validPolicyNoList = branchQueryService
				.queryPolicyNoListByClientNo(clientNo);
		if (validPolicyNoList == null || validPolicyNoList.isEmpty()) {
			throw new RuntimeException("找不到该保单的客户");
		}
		if (!validPolicyNoList.contains(policyNo)) {
			throw new RuntimeException("申请保单不为客户作为投保人或被保人的保单");
		}
		
		PosApplyBatchDTO batch = new PosApplyBatchDTO();
		batch.setClientNo(clientNo);
		batch.setClientInfo(clientInfoList.get(0));
		batch.setAcceptChannelCode(acceptChannelCode);
		batch.setAcceptor(applyInfo.getAcceptor());
		batch.setCounterNo(counterNo);
		batch.setApplyTypeCode(applyType);
		
		// 默认申请类型为亲办
		if (applyType==null||"".equals(applyType)||StringUtils.isBlank(applyType)){
			batch.setApplyTypeCode("1");
		}
		if ("2".equals(applyType) || "3".equals(applyType)
				|| "4".equals(applyType)) {
			batch.setRepresentor(applyInfo.getRepresentor());
			batch.setRepresentIdno(applyInfo.getRepresentIdno());
			batch.setIdType(applyInfo.getIdType());
		}
		
		List<PosApplyFilesDTO> files = new ArrayList<PosApplyFilesDTO>();
		PosApplyFilesDTO file = new PosApplyFilesDTO();
		file.setBarcodeNo(barcode);// 条形码
		file.setPolicyNo(policyNo);
		file.setApplyDate(applyInfo.getApplyDate());
		file.setForeignExchangeType("1");
		file.setServiceItems(applyInfo.getServiceItems());
		file.setPaymentType(paymentType);
		file.setAccountNoType(accountNoType);
		if (accountNoType == null || "".equals(accountNoType)) {

			file.setAccountNoType("1");
		}

		file.setTransferAccountOwnerIdType(applyInfo.getTransferAccountOwnerIdType());// 证件类型
		file.setTransferAccountOwnerIdNo(applyInfo.getTransferAccountOwnerIdNo());// 证件号码
		file.setTransferAccountOwner(applyInfo.getTransferAccountOwner());// 收款人姓名
		file.setBankCode(applyInfo.getBankCode());//开户银行代码
		file.setTransferAccountno(applyInfo.getTransferAccountno());//账号
		file.setApprovalServiceType(approvalServiceType);
		if (file.getApplyDate() == null) {
			file.setApplyDate(commonQueryDAO.getSystemDate());
		}
		if (approvalServiceType==null||"".equals(approvalServiceType)||StringUtils.isBlank(approvalServiceType)) {
			file.setApprovalServiceType("1");// 自领
		}
		if ("2".equals(approvalServiceType)) {
			file.setAddress(applyInfo.getAddress());
			file.setZip(applyInfo.getZip());
		}
		if (StringUtils.isBlank(paymentType)) {
			file.setPaymentType("1");// 现金
		}
		
		files.add(file);
		batch.setApplyFiles(files);

		// 生成受理
		branchAcceptService.generatePosInfoList(batch, false);

		// 校验是否有规则检查不通过的
		for (PosInfoDTO posInfo : batch.getPosInfoList()) {
			POSVerifyResultDto verifyResult = posInfo.getVerifyResult();
			if (verifyResult != null && verifyResult.getRuleInfos().size() != 0) {
				StringBuilder sb = new StringBuilder("保单号[");
				sb.append(posInfo.getPolicyNo());
				sb.append("]申请");
				sb.append(posInfo.getServiceItemsDesc());
				sb.append("项目受理规则检查不通过:");
				for (POSBOMRuleInfoDto ruleInfo : verifyResult.getRuleInfos()) {
					sb.append(ruleInfo.getDescription() + "；");
				}
				//throw new RuntimeException(sb.toString());
			}
		}

		// 生成捆绑顺序
		branchAcceptService.generateBindingOrder(batch);

		// 写受理
		batch = branchAcceptService.batchAccept(batch);		
		
		return batch;
	}
}
