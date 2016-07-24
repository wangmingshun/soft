package com.sinolife.pos.others.dealrefundfail.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.common.dao.AbstractPosDAO;

@Repository("dealRefundFailDAO")
@SuppressWarnings("rawtypes")
@Transactional(propagation = Propagation.REQUIRED)
public class DealRefundFailDAO extends AbstractPosDAO {
	/**
	 * 退费失败件查询
	 * 
	 * @param map
	 * @return List
	 * @author GaoJiaMing
	 * @time 2014-8-19
	 */
	public List queryDealRefundFailPage(Map map) {
		return queryForList("queryDealRefundFailPage", map);
	}

}
