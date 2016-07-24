package com.sinolife.pos.pubInterface.biz.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.acceptance.trial.dto.TrialInfoDTO;
import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;

public interface CcsPosAcceptInterface {

	/**
	 * 选定受理客户，保单，项目等提交
	 */
	public ServiceItemsInputDTO clientSelectSubmit(final TrialInfoDTO trialInfo);

	/**
	 * 受理明细录入提交，并试算出批文等信息
	 */
	public Map<String, Object> acceptDetailInputSubmit(
			final ServiceItemsInputDTO itemsInputDTO,
			final TrialInfoDTO trialInfo);

	public String generateCCSBarcode();

	/**
	 * 实际保全退保操作受理入口
	 * 
	 * @param applyBatchInfo
	 * @return
	 */
	public Map<String, Object> createApplyInfo(PosApplyBatchDTO applyInfo,
			List<String> lsSelectedProdSeq, String agreePremSum,
			String branchPercent, String specialFunc,String surrenderToNewApplyBarCode,
			String newApplyBarCodePrem) ;

	/**
	 * 实际保全退保操作受理入口(咨诉系统41保全项目)
	 * 
	 * @param applyBatchInfo
	 * @return
	 */
	public Map<String, Object> createApplyInfoFor41(Map<String, Object> map);

	/**
	 * 受理撤销，对于已经受理的保全，强制改掉受理状态为C02
	 * 
	 * @param posInfo
	 */
	public void cancelAccept(String posNo);

	// 审批一个案件（包含多个保全）
	public void ccsApproveList(List<Map<String, Object>> ls);

	// 审批
	public void ccsApprove(Map<String, Object> pMap);

	// 跳规则处理
	public String skipRuleService(String posNo);

	/**
	 * 协议满期实际保全操作受理入口
	 * 
	 * @param applyInfo
	 * @param specialFunc
	 *            功能特殊件类型
	 * @param agreeMaturitySum满期协议金额
	 * @param branchPercent分公司承担比例
	 * @param backBenefit
	 *            既得利益
	 * @return
	 */
	public Map<String, Object> createApplyInfoForMaturity(
			PosApplyBatchDTO applyInfo, String specialFunc,
			String agreeMaturitySum, String branchPercent, String backBenefit
			,String surrenderToNewApplyBarCode,String newApplyBarCodePrem	
	);

}
