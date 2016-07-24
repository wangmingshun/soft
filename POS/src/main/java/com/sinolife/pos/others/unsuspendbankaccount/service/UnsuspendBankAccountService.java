package com.sinolife.pos.others.unsuspendbankaccount.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.common.dao.CommonAcceptDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.PosProblemItemsDTO;
import com.sinolife.pos.others.unsuspendbankaccount.dao.UnsuspendBankAccountDAO;
import com.sinolife.pos.todolist.problem.dao.ProblemDAO;

@Service("unsuspendBankAccountService")
public class UnsuspendBankAccountService {

	@Autowired
	private UnsuspendBankAccountDAO unsuspendBankAccountDAO;

	@Autowired
	private CommonAcceptDAO commonAcceptDAO;

	@Autowired
	private CommonQueryDAO commonQueryDAO;

	@Autowired
	private ProblemDAO problemDAO;

	/**
	 * 根据保全号查询可以做转账暂停取消的保全列表
	 * 
	 * @param posNo
	 * @return
	 */
	public List<PosInfoDTO> queryPosInfoByPosNoForUnsuspendBankAccount(
			String posNo) {
		return unsuspendBankAccountDAO
				.queryPosInfoByPosNoForUnsuspendBankAccount(posNo);
	}

	/**
	 * 取消指定保单号的转账暂停
	 * 
	 * @param posNo
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void unsuspendBankAccount(String posNo) {

		PosInfoDTO posInfo = commonQueryDAO.queryPosInfoRecord(posNo);
		if (posInfo == null || !"N".equals(posInfo.getPremSuccessFlag())) {
			throw new RuntimeException("收付费未失败，无法取消转账暂停");
		}

		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_query_type", "1");
		paraMap.put("p_business_no", posNo);
		paraMap.put("p_business_no_type", "5");
		// 非转账的根本到不了这，所以不用查了，直接传4(在财务那边，4是转账)
		paraMap.put("p_payment_type", "4");
		commonAcceptDAO.finModifyBusinessCost(paraMap);
		String flag = (String) paraMap.get("p_flag");
		String msg = (String) paraMap.get("p_msg");
		if (!"1".equals(flag))
			throw new RuntimeException(msg);

		if (!unsuspendBankAccountDAO.clearPosPremSuccessFlag(posNo))
			throw new RuntimeException("数据状态异常，更新转账暂停状态失败!");
	}

	/**
	 * 修改账号信息并取消转账暂停
	 * 
	 * @param map
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void modifyAccountAndUnsuspend(Map<String, Object> map) {

		String posNo = (String) map.get("posNo");
		String problemItemNo = (String) map.get("problemItemNo");
		// 更新问题件状态，并写入问题件状态变迁记录

		// 更新问题件状态为：已下发函件
		problemDAO.updatePosProblemItems(problemItemNo, "1", "4");

		PosInfoDTO posInfo = commonQueryDAO.queryPosInfoRecord(posNo);
		if (posInfo == null || !"N".equals(posInfo.getPremSuccessFlag())) {
			throw new RuntimeException("收付费未失败，无法取消转账暂停");
		}

		String bankCode = (String) map.get("bankCode");
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_query_type", "1");
		paraMap.put("p_business_no", posNo);
		paraMap.put("p_business_no_type", "5");
		// 非转账的根本到不了这，所以不用查了，直接传4(在财务那边，4是转账)
		paraMap.put("p_payment_type", "4");
		paraMap.put("p_bank_code", bankCode);
		commonAcceptDAO.finModifyBusinessCost(paraMap);
		String flag = (String) paraMap.get("p_flag");
		String msg = (String) paraMap.get("p_msg");
		if (!"1".equals(flag))
			throw new RuntimeException(msg);

		if (!unsuspendBankAccountDAO.clearPosPremSuccessFlag(posNo))
			throw new RuntimeException("数据状态异常，更新转账暂停状态失败!");
	}
	
	/**
	 * 问题件处理，取消转账暂停
	 * 
	 * @param problemItemsDTO
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void unsuspendBankTransfer(PosProblemItemsDTO problemItemsDTO) {
		String problemItemNo = problemItemsDTO.getProblemItemNo();		
		// 更新问题件状态为：已处理
		problemDAO.updatePosProblemItems(problemItemNo, "1", "4");
		problemDAO.updatePosProblemItems(problemItemNo, "3",
				problemItemsDTO.getDealResult());
		problemDAO.updatePosProblemItems(problemItemNo, "4",
				problemItemsDTO.getDealOpinion());
		// 取消转账暂停，需要检查是否收、付费是否成功，即prem_success_flag = N
		String posNo = problemItemsDTO.getPosNo();
		unsuspendBankAccount(posNo);
	}

}
