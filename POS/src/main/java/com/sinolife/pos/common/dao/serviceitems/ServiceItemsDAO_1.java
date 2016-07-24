package com.sinolife.pos.common.dao.serviceitems;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.unihub.framework.util.common.DateUtil;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_1;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;
import com.sinolife.pos.common.util.detailgenerator.ItemProcessFilter;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSUWBOMPlanDto;

/**
 * 1 犹豫期退保
 */
@Repository("serviceItemsDAO_1")
public class ServiceItemsDAO_1 extends ServiceItemsDAO {

	private static final Map<String, String> PROC_PROPERTY_CONFIG_MAP;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("surrenderCauseCode", "1|092|1|3|policyNo");// 退保原因代码
		map.put("prodSeq", "2|011|1|3|prodSeq"); // 险种序号
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
		ServiceItemsInputDTO_1 item = (ServiceItemsInputDTO_1) serviceItemsInputDTO;

		String policyNo = item.getPolicyNo();

		// 查询产品信息
		List<PolicyProductDTO> policyProductList = commonQueryDAO
				.queryPolicyProductListByPolicyNo(policyNo);
		this.setCanBeSelectedFlag(policyProductList);
		item.setPolicyProductList(policyProductList);

		// 查询保单回执延期回销报备时间
		Date receiptPutoffDate = (Date) queryForObject(
				"getReceiptPutoffDateByPolicyNo", policyNo);
		item.setReceiptPutoffDate(receiptPutoffDate);

		// 犹豫期退保需确认保单补发次数
		item.setPolicyProvideTimeEditable(true);

		return item;
	}

	/**
	 * 提供给外围保全（银保通除外）
	 */
	public ServiceItemsInputDTO_1 queryServiceItemsInfoExtra_1(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return (ServiceItemsInputDTO_1) queryServiceItemsInfoExtra(serviceItemsInputDTO);
	}

	/**
	 * 提供给银保通接口
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtraToBia_1(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_1 item = (ServiceItemsInputDTO_1) serviceItemsInputDTO;

		String policyNo = item.getPolicyNo();

		// 查询产品信息
		List<PolicyProductDTO> policyProductList = commonQueryDAO
				.queryPolicyProductListByPolicyNo(policyNo);
		this.setCanBeSelectedFlag(policyProductList);
		item.setPolicyProductList(policyProductList);

		// 查询保单回执延期回销报备时间
		Date receiptPutoffDate = (Date) queryForObject(
				"getReceiptPutoffDateByPolicyNo", policyNo);
		item.setReceiptPutoffDate(receiptPutoffDate);

		// 犹豫期退保需确认保单补发次数
		item.setPolicyProvideTimeEditable(false);

		return item;
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
		ServiceItemsInputDTO_1 item = (ServiceItemsInputDTO_1) serviceItemsInputDTO;
		List<PosAcceptDetailDTO> detailList = new ArrayList<PosAcceptDetailDTO>();
		detailList.add(new PosAcceptDetailDTO(item.getPosNo(), "1", "1", item
				.getPolicyNo(), "140", null, item.isDouBt() ? "Y" : "N"));// 是否可疑交易
		if (item.isDouBt() == true && item.getDoubtList().length > 0) {
			for (int j = 0; j < item.getDoubtList().length; j++) {
				detailList.add(new PosAcceptDetailDTO(item.getPosNo(), "2",
						"1", "" + j, "024", null, (item.getDoubtList())[j]
								.toString()));// 可疑交易类型
			}
		}
		AcceptDetailGenerator generator = new AcceptDetailGenerator(
				PROC_PROPERTY_CONFIG_MAP, item.getPosNo(), beginGroupNo);
		generator.processSimpleDTO(item);
		generator.processSimpleListDTO(item.getPolicyProductList(),
				new ItemProcessFilter() {
					@Override
					public boolean needProcess(Map<String, Object> listItem,
							Map<String, Object> oldValueItem) {
						return "Y".equals(listItem.get("canBeSelectedFlag"))
								&& (Boolean) listItem.get("selected");
					}
				});
		generator.getResult().addAll(detailList);
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void validate(ServiceItemsInputDTO serviceItemsInputDTO, Errors err) {
		super.validate(serviceItemsInputDTO, err);

		Map pMap = new HashMap();
		pMap.put("policyNo", serviceItemsInputDTO.getPolicyNo());
		queryForObject("RULE_UIAN_AC_SURVIVAL", pMap);
		if ("N".equals(pMap.get("flag"))) {
			err.reject("product", (String) pMap.get("message"));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#
	 * fillBOMForProcessRuleCheck
	 * (com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto)
	 */
	@Override
	public void fillBOMForProcessRuleCheck(POSBOMPolicyDto bom) {
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		// 选中的险种
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "2");
		item.put("itemNo", "011");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(
				bom.getPosNo(), posObjectAndItemNoList);
		if (detailList != null && !detailList.isEmpty()) {
			for (PosAcceptDetailDTO detail : detailList) {
				if ("2".equals(detail.getPosObject())
						&& "011".equals(detail.getItemNo())) {
					for (POSUWBOMPlanDto plan : bom.getPlansList()) {
						if (plan.getProdSeq() == Integer.parseInt(detail
								.getNewValue())) {
							plan.setCurrentPosPlan(true);
							if ("7".equals(bom.getAcceptSource())
									&& "CEAN_AN1".equals(plan.getProductCode())) {

								Map<String, Object> retMap = commonQueryDAO
										.getPremPaid(bom.getPolicyNo(), String
												.valueOf(plan.getProdSeq()),
												DateUtil.getCurrentDateTime());
								BigDecimal b = (BigDecimal) retMap
										.get("p_prem_paid");
								bom.setPremSum(b.doubleValue());
								bom.setPremFlag("-1");

							}

						}
					}
				}
			}
		}

		if ("7".equals(bom.getAcceptSource())) {

		}
		super.fillBOMForProcessRuleCheck(bom);
	}

}
