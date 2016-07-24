package com.sinolife.pos.setting.user.moneyapproval.service;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinolife.pos.setting.user.moneyapproval.dao.MoneyApprovalDAO;
import com.sinolife.pos.setting.user.moneyapproval.dto.PosAmountDaysPrivsDTO;

@Service("moneyApprovalService")
public class MoneyApprovalService {
	
	@Autowired
	MoneyApprovalDAO maDAO;
	
	public void moneyApprovalSet(PosAmountDaysPrivsDTO amountDaysDTO){
		maDAO.moneyApprovalSet(amountDaysDTO);
	}

	@SuppressWarnings("rawtypes")
	public Map querySet(String amountDaysGrade,String serviceItem, String acceptChannel){
		return maDAO.querySet(amountDaysGrade, serviceItem, acceptChannel);
	}
}
