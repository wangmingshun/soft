package com.sinolife.pos.common.dao.typehandler;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.sql.ARRAY;
import oracle.sql.STRUCT;

import org.apache.log4j.Logger;

import com.sinolife.pos.common.dto.FinancialProductsDTO;

/**
 * FINANCIAL_PRODUCTS_LIST处理器，Map属性名为p_financial_products_list
 */
public class FinancialProductsArrayHandler extends
		AbstractArrayOfObjectTypeInHandler implements ArrayTypeResultHandler {

	private Logger logger = Logger.getLogger(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dao.typehandler.ArrayTypeResultHandler#handleResult(java.util.Map)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map handleResult(Map map) {
		ARRAY array = (ARRAY) map.get("p_financial_products_list");
		List<FinancialProductsDTO> result = new ArrayList<FinancialProductsDTO>();
		FinancialProductsDTO uia = null;// 根据返回数据的基本类型确定Dto中的数据类型
		try {
			if(array != null) {
				Object[] obj = (Object[]) array.getArray();// mydata可以取出来
				STRUCT struct = null;
				for (int i = 0; i < obj.length; i++) {
					struct = (STRUCT) obj[i];
					Object[] tmp = (Object[]) struct.getAttributes();
					uia = new FinancialProductsDTO();
					uia.setFinancialProducts((String) tmp[0]);
					uia.setFinProductsDesc((String) tmp[1]);
					uia.setNextFinancialProducts((String) tmp[2]);
					uia.setNextFinProductsDesc((String) tmp[3]);
					uia.setAmount((BigDecimal) tmp[4]);
					uia.setRate((BigDecimal) tmp[5]);
					uia.setUnits((BigDecimal) tmp[6]);
					uia.setInterestRate((BigDecimal) tmp[7]);
					uia.setBuyPrice((BigDecimal) tmp[8]);
					uia.setSoldPrice((BigDecimal) tmp[9]);
					uia.setInterestSettleEndDate((Date) tmp[10]);
					uia.setRiskPremSettleEndDate((Date) tmp[11]);
					uia.setManPremSettleEndDate((Date) tmp[12]);
					result.add(uia);
				}
			}
			map.put("p_financial_products_list", result);
		} catch (SQLException e) {
			String errMsg = "ArrayTypeResultHandler error:" + e.getMessage();
			logger.error(errMsg, e);
			throw new RuntimeException(errMsg);
		}
		return map;

	}

	@Override
	protected Object[] getPropertyArrayFromListItem(Object obj) {
		Object[] objs = new Object[13];
		FinancialProductsDTO fin = (FinancialProductsDTO) obj;
		objs[0] = fin.getFinancialProducts();
		objs[1] = fin.getFinProductsDesc();
		objs[2] = fin.getNextFinancialProducts();
		objs[3] = fin.getNextFinProductsDesc();
		objs[4] = fin.getAmount();
		objs[5] = fin.getRate();
		objs[6] = fin.getUnits();
		objs[7] = fin.getInterestRate();
		objs[8] = fin.getBuyPrice();
		objs[9] = fin.getSoldPrice();
		objs[10] = fin.getInterestSettleEndDate();
		objs[11] = fin.getRiskPremSettleEndDate();
		objs[12] = fin.getManPremSettleEndDate();
		return objs;
	}

	@Override
	protected String getDBListTypeName() {
		return "POSCDE.FINANCIAL_PRODUCTS_LIST";
	}

	@Override
	protected String getDBListItemTypeName() {
		return "POSCDE.FINANCIAL_PRODUCTS";
	}

	/**
	 * 将理财险某个时间段的账户价值明细数组转换为List
	 * @methodName: handleFinAccountArrayResult
	 * @Description: 
	 * @param paraMap
	 * @return List 
	 * @author WangMingShun
	 * @date 2015-6-30
	 * @throws
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List handleFinAccountArrayResult(Map paraMap) {
		List finAccountList = new ArrayList();
		//取得具体明细
		ARRAY insArray = (ARRAY) paraMap.get("finAccountArray");
		try {
			if(insArray != null) {
				Object[] finAccountArray = (Object[]) insArray.getArray();
				STRUCT struct = null;
				for (int i = 0; i < finAccountArray.length; i++) {
					struct = (STRUCT) finAccountArray[i];
					Object[] tmp = (Object[])struct.getAttributes();
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("calcDate", tmp[0]);
					map.put("finType", tmp[1]);
					map.put("firstPrem", tmp[2]);
					map.put("firstAddPrem", tmp[3]);
					map.put("addPrem", tmp[4]);
					map.put("withdrawPrem", tmp[5]);
					map.put("totalAmount", tmp[6]);
					map.put("benefitAmount", tmp[7]);
					map.put("dayBenefit", tmp[8]);
					finAccountList.add(map);
				}
			}
		} catch (SQLException e) {
			String errMsg = "FinancialProductsArrayHandler error:" + e.getMessage();
			logger.error(errMsg, e);
			throw new RuntimeException(errMsg);
		}
		return finAccountList;
	}
}
