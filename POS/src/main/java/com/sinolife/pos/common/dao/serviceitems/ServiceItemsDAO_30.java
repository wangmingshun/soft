package com.sinolife.pos.common.dao.serviceitems;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_30;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;

/**
 * 30 保单质押贷款-贷款受理
 */
@Repository("serviceItemsDAO_30")
public class ServiceItemsDAO_30 extends ServiceItemsDAO {
	
	private static final Map<String, String> PROC_PROPERTY_CONFIG_MAP;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("loanSum", 			"1|024|1|3|policyNo");//保单质押贷款受理之贷款金额
		map.put("loanInterest", 	"1|025|1|3|policyNo");//保单质押贷款受理之贷款利率
		map.put("loanDeptNo", 		"1|026|1|3|policyNo");//保单质押贷款受理之贷款银行网点
		map.put("policyCashValue", 	"1|027|1|3|policyNo");//保单质押贷款受理之贷款当时保单现金价值
		map.put("loanLimitDate", 	"1|028|1|3|policyNo");//保单质押贷款受理之贷款期限
		
		PROC_PROPERTY_CONFIG_MAP = map;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#queryServiceItemsInfoExtra(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_30 item = (ServiceItemsInputDTO_30)serviceItemsInputDTO;
		String policyNo = item.getPolicyNo();
		queryForObject("queryPolicyLoanInfo30", policyNo, item);
		
		//查询保单现金价值
		Date applyDate = commonQueryDAO.getApplyDateByPosNo(item.getPosNo());
		Map<String, Object> retMap = commonQueryDAO.calcPolicyCashValue(policyNo, applyDate);
		String flag = (String) retMap.get("p_flag");
		String msg = (String) retMap.get("p_message");
		BigDecimal cashValue = (BigDecimal) retMap.get("p_policy_cash_value");
		if(!"0".equals(flag))
			throw new RuntimeException("查询保单现金价值失败：" + msg);
		item.setPolicyCashValue(cashValue);
		
		//查询贷款银行网点下拉框数据
		item.setLoanDeptList(queryForList("queryLoanDeptList", policyNo));
		
		return item;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#generateAcceptDetailDTOList(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_30 item = (ServiceItemsInputDTO_30)serviceItemsInputDTO;
		ServiceItemsInputDTO_30 snapshot = (ServiceItemsInputDTO_30)serviceItemsInputDTO.getOriginServiceItemsInputDTO();
		item.setPolicyCashValue(snapshot.getPolicyCashValue());
		
		AcceptDetailGenerator generator = new AcceptDetailGenerator(PROC_PROPERTY_CONFIG_MAP, serviceItemsInputDTO.getPosNo(), beginGroupNo);
		generator.processSimpleDTO(item);
		return generator.getResult();
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#validate(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(ServiceItemsInputDTO serviceItemsInputDTO, Errors err) {
		super.validate(serviceItemsInputDTO, err);
	}
}
