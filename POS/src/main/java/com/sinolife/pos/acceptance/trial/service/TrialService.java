package com.sinolife.pos.acceptance.trial.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.sinolife.pos.acceptance.branch.service.BranchAcceptService;
import com.sinolife.pos.acceptance.branch.service.BranchQueryService;
import com.sinolife.pos.acceptance.trial.dto.TrialInfoDTO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.LoginUserInfoDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.common.dto.PosApplyFilesDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.util.PosUtils;

@Service
public class TrialService {

	@Autowired
	private BranchAcceptService acceptService;

	@Autowired
	private BranchQueryService queryService;

	@Autowired
	private CommonQueryDAO commonQueryDAO;

	/**
	 * 批次受理、明细录入、试算
	 * 
	 * @param trialInfo
	 * @param itemsInputDTO
	 * @param processResultList
	 * @param bindingResult
	 */
	public List<Map<String, Object>> trialDetailSubmit(TrialInfoDTO trialInfo,
			ServiceItemsInputDTO itemsInputDTO, BindingResult bindingResult) {
		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		ServiceItemsInputDTO sii = trialBatchAccept(trialInfo);
		String posNo = sii.getPosNo();
		itemsInputDTO.setPosNo(posNo);
		itemsInputDTO.setPosNoList(null);

		// 校验
		acceptService
				.validateServiceItemsInputDTO(itemsInputDTO, bindingResult);
		if (bindingResult.hasErrors()) {
			return null;
		}

		// 写受理明细
		acceptService.acceptDetailInput(itemsInputDTO);

		List<String> posNoList = itemsInputDTO.getPosNoList();
		for (int i = 0; i < posNoList.size(); i++) {
			posNo = posNoList.get(i);

			acceptService.updatePosInfo(posNo, "accept_status_code", "A03");

			// 调用流转接口
			Map<String, Object> retMap = acceptService.workflowControl("03",
					posNo, true);

			String flag = (String) retMap.get("flag");
			String msg = (String) retMap.get("message");

			retMap = queryService.queryProcessResult(posNo);
			if ("C01".equals(flag)) {
				// 规则检查不通过
				retMap.put("RULE_CHECK_MSG", msg);
			} else if ("A19".equals(flag)) {
				// 待审批判断处理
			}
			retList.add(retMap);
		}
		return retList;
	}

	/**
	 * 咨诉系统批次受理、明细录入、试算
	 * 
	 * @param trialInfo
	 * @param itemsInputDTO
	 * @param processResultList
	 * @param bindingResult
	 */
	public Map<String, Object> ccsTrialDetailSubmit(TrialInfoDTO trialInfo,
			ServiceItemsInputDTO itemsInputDTO, BindingResult bindingResult) {

		Map<String, Object> returnMap = new HashMap<String, Object>();
		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();

		String posNo = itemsInputDTO.getPosNo();

		itemsInputDTO.setPosNoList(null);

		String specialFunc = itemsInputDTO.getSpecialFunc();

		// 校验
		acceptService
				.validateServiceItemsInputDTO(itemsInputDTO, bindingResult);
		if (bindingResult.hasErrors()) {
			return null;
		}

		itemsInputDTO.setSpecialFunc(specialFunc);
		if (StringUtils.isBlank(specialFunc)) {

			itemsInputDTO.setSpecialFunc("9");
		}

		// 写受理明细
		acceptService.acceptDetailInput(itemsInputDTO);

		List<String> posNoList = itemsInputDTO.getPosNoList();
		for (int i = 0; i < posNoList.size(); i++) {
			posNo = posNoList.get(i);

			acceptService.updatePosInfo(posNo, "accept_status_code", "A03");

			// 调用流转接口
			Map<String, Object> retMap = acceptService.workflowControl("03",
					posNo, true);

			String flag = (String) retMap.get("flag");
			String msg = (String) retMap.get("message");

			retMap = queryService.queryProcessResult(posNo);
			if ("C01".equals(flag)) {
				// 规则检查不通过
				retMap.put("RULE_CHECK_MSG", msg);
			} else if ("A19".equals(flag)) {
				// 待审批判断处理
			}
			retList.add(retMap);
		}
		returnMap.put("retMap", retList.get(0));

		PosInfoDTO posInfoDTO = commonQueryDAO.queryPosInfoRecord(posNo);
		List<PosAcceptDetailDTO> ls = commonQueryDAO
				.queryPosAcceptDetailByPosNoAndPosObject(posNo, "7");
		returnMap.put("lsPrem", ls);

		List<String> lsFin = commonQueryDAO.queryPosAcceptDetailoldvalue(posNo,
				"10", "001");
		double dbFin = 0.0d;// 万能投连模糊金额
		if (lsFin != null && lsFin.size() > 0) {

			dbFin = Double.parseDouble((String) (lsFin.get(0)));
			double dbPreSum = 0.0d;
			if (posInfoDTO.getPremSum() != null) {
				dbPreSum = posInfoDTO.getPremSum().doubleValue()
						* (posInfoDTO.getPremFlag().equals("+1") ? (-1) : 1);

			}

			BigDecimal bdSum = new BigDecimal(dbPreSum + dbFin).setScale(2,
					BigDecimal.ROUND_HALF_UP);

			if (bdSum.doubleValue()>0)
			{
				posInfoDTO.setPremFlag("-1");
			}
			else if(bdSum.doubleValue()<0)
			{
				
				posInfoDTO.setPremFlag("+1");
			}
			
			posInfoDTO.setPremSum(new BigDecimal(Math.abs(bdSum.doubleValue())).setScale(2,
					BigDecimal.ROUND_HALF_UP));
		}

		returnMap.put("posInfoDTO", posInfoDTO);
		return returnMap;

	}

	/**
	 * 批次受理方法
	 * 
	 * @param trialInfo
	 * @return
	 */
	public ServiceItemsInputDTO trialBatchAccept(TrialInfoDTO trialInfo) {
		LoginUserInfoDTO userInfo = PosUtils.getLoginUserInfo();

		PosApplyBatchDTO applyInfo = new PosApplyBatchDTO();
		applyInfo.setClientNo(trialInfo.getClientNo());
		applyInfo.setAcceptChannelCode("1");
		if (userInfo != null) {
			if (userInfo.getLoginUserID() == null) {
				userInfo.setLoginUserID("ccsor");// 设置虚拟用户
			} else {
				applyInfo.setAcceptor(userInfo.getLoginUserID());
			}
			applyInfo.setCounterNo(userInfo.getLoginUserCounterNo());

		} else {
			applyInfo.setAcceptor("cmpuser");
			applyInfo.setCounterNo("901");
		}
		applyInfo.setApplyTypeCode("1");
		// applyInfo.setCounterNo(userInfo.getLoginUserCounterNo());
		List<PosApplyFilesDTO> fileList = new ArrayList<PosApplyFilesDTO>();
		PosApplyFilesDTO file = new PosApplyFilesDTO();
		file.setBarcodeNo("trial" + System.currentTimeMillis());
		file.setApplyDate(trialInfo.getApplyDate());
		file.setApprovalServiceType("1");
		file.setPaymentType("1");
		file.setPolicyNo(trialInfo.getPolicyNo());
		file.setServiceItems(trialInfo.getServiceItems());
		fileList.add(file);

		applyInfo.setApplyFiles(fileList);

		// 生成受理
		acceptService.generatePosInfoList(applyInfo, true);

		// 生成捆绑顺序
		acceptService.generateBindingOrder(applyInfo);

		// 写受理
		PosApplyBatchDTO pab = acceptService.batchAccept(applyInfo);
		String posNo = pab.getPosInfoList().get(0).getPosNo();
		ServiceItemsInputDTO itemsInputDTO = queryService
				.queryAcceptDetailInputByPosNo(posNo);
		return itemsInputDTO;
	}

	/**
	 * 咨诉系统批次受理方法
	 * 
	 * @param trialInfo
	 * @return
	 */
	public ServiceItemsInputDTO ccsTrialBatchAccept(TrialInfoDTO trialInfo) {
		PosApplyBatchDTO applyInfo = new PosApplyBatchDTO();
		applyInfo.setClientNo(trialInfo.getClientNo());
		applyInfo.setAcceptChannelCode("13");
		applyInfo.setAcceptor(trialInfo.getAcceptor());
		applyInfo.setApplyTypeCode("1");
		applyInfo.setCounterNo("907");

		List<PosApplyFilesDTO> fileList = new ArrayList<PosApplyFilesDTO>();
		PosApplyFilesDTO file = new PosApplyFilesDTO();
		file.setBarcodeNo("trial" + System.currentTimeMillis());
		file.setApplyDate(trialInfo.getApplyDate());
		file.setApprovalServiceType("1");
		file.setPaymentType("1");
		file.setPolicyNo(trialInfo.getPolicyNo());
		file.setServiceItems(trialInfo.getServiceItems());
		fileList.add(file);

		applyInfo.setApplyFiles(fileList);

		// 生成受理
		acceptService.generatePosInfoList(applyInfo, true);

		// 生成捆绑顺序
		acceptService.generateBindingOrder(applyInfo);

		// 写受理
		PosApplyBatchDTO pab = acceptService.batchAccept(applyInfo);
		String posNo = pab.getPosInfoList().get(0).getPosNo();
		ServiceItemsInputDTO itemsInputDTO = queryService
				.queryAcceptDetailInputByPosNo(posNo);
		return itemsInputDTO;
	}

}
