package com.sinolife.pos.others.dealrefundfail.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinolife.pos.others.dealrefundfail.dao.DealRefundFailDAO;

@Service("dealRefundFailService")
public class DealRefundFailService {
	@Autowired
	DealRefundFailDAO dealRefundFailDAO;

	/**
	 * 退费失败件查询
	 * 
	 * @param paraMap
	 * @return List
	 * @author GaoJiaMing
	 * @time 2014-11-6
	 */
	public List queryDealRefundFailPage(Map paraMap) {
		return dealRefundFailDAO.queryDealRefundFailPage(paraMap);
	}
}
