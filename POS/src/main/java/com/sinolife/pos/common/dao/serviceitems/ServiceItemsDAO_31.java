package com.sinolife.pos.common.dao.serviceitems;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_31;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;

/**
 * 31 保单质押贷款-还款解冻
 */
@SuppressWarnings("rawtypes")
@Repository("serviceItemsDAO_31")
public class ServiceItemsDAO_31 extends ServiceItemsDAO {

	private static final Map<String, String> PROC_PROPERTY_CONFIG_MAP;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("unfreezeCause", "1|019|1|3|policyNo");// 保单质押贷款信息解冻原因

		PROC_PROPERTY_CONFIG_MAP = map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#
	 * queryServiceItemsInfoExtra
	 * (com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_31 itemDTO = (ServiceItemsInputDTO_31) serviceItemsInputDTO;
		getSqlMapClientTemplate().queryForObject(sqlName("QUERY_INPUT_INFO"),
				itemDTO.getPolicyNo(), itemDTO);
		if (StringUtils.isBlank(itemDTO.getProdSeq())) {
			throw new RuntimeException("找不到质押贷款信息！");
		}
		Date applyDate = commonQueryDAO.getApplyDateByPosNo(itemDTO.getPosNo());

		// 查最大可借金额

		Map<String, Object> resultMap = null;
		boolean isSurvivalDutyProduct = false;
		Date nextAnniversary = null;

		BigDecimal nextAnniversaryCashValue = new BigDecimal(0);
		BigDecimal curCashValue = new BigDecimal(0);

		resultMap = commonQueryDAO.isSurvivalDutyProduct(itemDTO
				.getProductCode());
		if ("0".equals(resultMap.get("p_flag")))// 含有生存金产品

		{
			isSurvivalDutyProduct = true;
			resultMap = commonQueryDAO.calcProductAnniversary(
					itemDTO.getPolicyNo(),
					Integer.parseInt(itemDTO.getProdSeq()), applyDate);

			nextAnniversary = (Date) resultMap.get("p_next_anniversary");
			// 保单年度末
			Calendar lastDay = GregorianCalendar.getInstance();
			lastDay.setTime(nextAnniversary);
			lastDay.add(Calendar.DATE, -1);
			// 查询最大可贷金额及现金价值
			resultMap = commonQueryDAO.calcLoanMaxAndCashValueByProduct(
					itemDTO.getPolicyNo(), itemDTO.getProductCode(),
					Integer.parseInt(itemDTO.getProdSeq()), lastDay.getTime(),"");
			if (!"0".equals(resultMap.get("p_flag"))) {
				throw new RuntimeException("查询最大可贷金额及现金价值出错:"
						+ resultMap.get("p_message"));
			}
			nextAnniversaryCashValue = (BigDecimal) resultMap
					.get("p_cash_value");

		}

		Map map = commonQueryDAO.calcLoanMaxAndCashValueByProduct(
				itemDTO.getPolicyNo(), itemDTO.getProductCode(),
				Integer.parseInt(itemDTO.getProdSeq()), applyDate,"");

		if (map.get("p_cash_value") != null) {

			curCashValue = (BigDecimal) map.get("p_cash_value");
		}
		if (isSurvivalDutyProduct) {

			if (nextAnniversaryCashValue.doubleValue() > curCashValue
					.doubleValue()) {

				itemDTO.setCashValue(String.valueOf(map.get("p_cash_value")));
				itemDTO.setMaxLoanSum(String.valueOf(map.get("p_loan_max")));
			} else {

				itemDTO.setCashValue(String.valueOf(resultMap
						.get("p_cash_value")));
				itemDTO.setMaxLoanSum(String.valueOf(resultMap
						.get("p_loan_max")));

			}
		} else {

			itemDTO.setCashValue(String.valueOf(map.get("p_cash_value")));
			itemDTO.setMaxLoanSum(String.valueOf(map.get("p_loan_max")));

		}
		return itemDTO;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#
	 * generateAcceptDetailDTOList
	 * (com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(
			ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_31 item = (ServiceItemsInputDTO_31) serviceItemsInputDTO;

		AcceptDetailGenerator generator = new AcceptDetailGenerator(
				PROC_PROPERTY_CONFIG_MAP, serviceItemsInputDTO.getPosNo(),
				beginGroupNo);
		generator.processSimpleDTO(item);
		return generator.getResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#validate(com
	 * .sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO,
	 * org.springframework.validation.Errors)
	 */
	@Override
	public void validate(ServiceItemsInputDTO serviceItemsInputDTO, Errors err) {
		super.validate(serviceItemsInputDTO, err);
	}
}
