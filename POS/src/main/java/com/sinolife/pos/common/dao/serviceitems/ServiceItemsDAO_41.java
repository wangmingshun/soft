package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_41;

@SuppressWarnings("unchecked")
@Repository("serviceItemsDAO_41")
public class ServiceItemsDAO_41 extends ServiceItemsDAO {

	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_41 itemDTO = (ServiceItemsInputDTO_41) serviceItemsInputDTO;
		
		itemDTO.setProductList((List<PolicyProductDTO>)getSqlMapClientTemplate().queryForList(ServiceItemsDAO_2.class.getName()
				+ ".QUERY_PRODUCT_INFO", itemDTO));
		// this.setCanBeSelectedFlag(itemDTO.getProductList());

		// 退保需确认保单补发次数
		// itemDTO.setPolicyProvideTimeEditable(true);

		return itemDTO;
	}

	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(
			ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {

		List<PosAcceptDetailDTO> detailList = new ArrayList<PosAcceptDetailDTO>();
		ServiceItemsInputDTO_41 itemDTO = (ServiceItemsInputDTO_41) serviceItemsInputDTO;

		List<PolicyProductDTO> productList = itemDTO.getProductList();
		if (productList != null && productList.size() > 0) {

			PolicyProductDTO product = productList.get(0);
//			String prem = product.getCashValue() == null ? "0" : String
//					.valueOf(product.getCashValue());

			detailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "2", "1",
					"1", "010", "0", product.getAgreePremSum()));

			if (StringUtils.isNotBlank(product.getBranchPercent())) {
				detailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "2",
						"1", "1", "022", null, product.getBranchPercent()));// 特殊件—协议退保（投诉业务）分公司承担比例'

			}
		}

		return detailList;
	}

}
