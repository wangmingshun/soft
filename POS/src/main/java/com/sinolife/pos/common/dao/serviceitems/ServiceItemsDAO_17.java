package com.sinolife.pos.common.dao.serviceitems;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_17;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;

/**
 * 17 续期保费退费
 */
@Repository("serviceItemsDAO_17")
public class ServiceItemsDAO_17 extends ServiceItemsDAO {
	
	private static final Map<String, String> PROC_PROPERTY_CONFIG_MAP;
	private static final boolean  String = false;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("refundAmount",		"1|083|1|3|policyNo");//退费金额
		map.put("refundCauseCode",	"1|084|1|3|policyNo");//退费原因代码
		
		PROC_PROPERTY_CONFIG_MAP = map;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#queryServiceItemsInfoExtra(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_17 item = (ServiceItemsInputDTO_17)serviceItemsInputDTO;
		
		//查询保单余额
		BigDecimal policyBalance = (BigDecimal) queryForObject("queryPolicyBalanceByPolicyNo", item.getPolicyNo());
		item.setPolicyBalance(policyBalance);
		
		//查询续期保费
		BigDecimal periodPrem = (BigDecimal) queryForObject("queryPeriodPrem", item.getPolicyNo());
		item.setPeriodPrem(periodPrem);
		
		return item;
	}
	/**
	 * 提供给外围系统
	 */
	public ServiceItemsInputDTO_17 queryServiceItemsInfoExtra_17(ServiceItemsInputDTO serviceItemsInputDTO){
		ServiceItemsInputDTO_17 item = (ServiceItemsInputDTO_17)serviceItemsInputDTO;
		
		//查询保单余额
		BigDecimal policyBalance = (BigDecimal) queryForObject("queryPolicyBalanceByPolicyNo", item.getPolicyNo());
		item.setPolicyBalance(policyBalance);
		item.setRefundAmount(policyBalance);
		return item;
	}
	/**
	 * 提供给外围系统
	 */
	public ServiceItemsInputDTO_17 queryServiceItemsInfoMcc_17(ServiceItemsInputDTO serviceItemsInputDTO){	
		return (ServiceItemsInputDTO_17) queryServiceItemsInfoExtra(serviceItemsInputDTO);
	}
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#generateAcceptDetailDTOList(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_17 item = (ServiceItemsInputDTO_17)serviceItemsInputDTO;
		AcceptDetailGenerator generator = new AcceptDetailGenerator(PROC_PROPERTY_CONFIG_MAP, item.getPosNo(), beginGroupNo);
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

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#fillBOMForProcessRuleCheck(com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto)
	 */
	@Override
	public void fillBOMForProcessRuleCheck(POSBOMPolicyDto bom) {
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		//退费金额
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "083");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(bom.getPosNo(), posObjectAndItemNoList);
		if(detailList != null && !detailList.isEmpty()) {
			double outMoney = Double.parseDouble(detailList.get(0).getNewValue());
			bom.setOutMoney(outMoney);
		}
		//续期保费退费是否可退入信用卡中
        boolean isRnUseCreditCard = ((String) queryForObject("canRnUseCreditCard",bom.getPosNo())).equals("Y") ;		
		bom.setRnUseCreditCard(isRnUseCreditCard);
		super.fillBOMForProcessRuleCheck(bom);
	}
}
