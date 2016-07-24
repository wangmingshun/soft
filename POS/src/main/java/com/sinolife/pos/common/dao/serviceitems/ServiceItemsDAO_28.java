package com.sinolife.pos.common.dao.serviceitems;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_28;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;

/**
 * 28 保单挂失及挂失解除
 */
@SuppressWarnings("rawtypes")
@Repository("serviceItemsDAO_28")
public class ServiceItemsDAO_28 extends ServiceItemsDAO {
	
	@Autowired
	CommonQueryDAO queryDAO;
	
	private static final Map<String, String> PROC_PROPERTY_CONFIG_MAP;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("isLoss", 	"1|003|1|3|policyNo");	//保单挂失状态
		
		PROC_PROPERTY_CONFIG_MAP = map;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#queryServiceItemsInfoExtra(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_28 item = (ServiceItemsInputDTO_28)serviceItemsInputDTO;
		
		Map map = queryDAO.policyLossStatus(item.getPolicyNo());
		item.setIsLoss((String)map.get("lossFlag"));
		
		return item;
	}
	/**
	 * 提供给外围系统
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtra_28(ServiceItemsInputDTO serviceItemsInputDTO) {
			return queryServiceItemsInfoExtra(serviceItemsInputDTO);
	}
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#generateAcceptDetailDTOList(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_28 item = (ServiceItemsInputDTO_28)serviceItemsInputDTO;
		ServiceItemsInputDTO_28 snapshot = (ServiceItemsInputDTO_28)serviceItemsInputDTO.getOriginServiceItemsInputDTO();
		
		if("Y".equals(snapshot.getIsLoss())) {
			item.setIsLoss("N");
		} else {
			item.setIsLoss("Y");
		}
		
		AcceptDetailGenerator generator = new AcceptDetailGenerator(PROC_PROPERTY_CONFIG_MAP, item.getPosNo(), beginGroupNo);
		generator.processSimpleDTO(item, snapshot);
		
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
