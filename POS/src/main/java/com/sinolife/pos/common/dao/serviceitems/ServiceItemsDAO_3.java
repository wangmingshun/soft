package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyProductCoverageDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_3;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;
import com.sinolife.pos.common.util.detailgenerator.ItemProcessFilter;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSUWBOMPlanDto;

/**
 * 3 加保
 */
@Repository("serviceItemsDAO_3")
public class ServiceItemsDAO_3 extends ServiceItemsDAO {

	private static final Map<String, String> PROC_PROPERTY_CONFIG_MAP;

	// private static final Map<String, String>
	// PROC_PROPERTY_CONFIG_MAP_FOR_COVERAGE;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("newBaseSumIns", "2|012|1|3|prodSeq");// 变更后保额
		map.put("newAnnStandardPrem", "2|015|1|3|prodSeq");// 变更后保费

		PROC_PROPERTY_CONFIG_MAP = map;

		map = new ConcurrentHashMap<String, String>();

		map.put("newBaseSumIns", "2|012|1|3|pkSerial");// 变更后保额

		// PROC_PROPERTY_CONFIG_MAP_FOR_COVERAGE = map;

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
		ServiceItemsInputDTO_3 item = (ServiceItemsInputDTO_3) serviceItemsInputDTO;
		String policyNo = item.getPolicyNo();
		String policy_channel_type = commonQueryDAO
				.getChannelTypeByPolicyNo(policyNo);
		item.setPolicyChannelType(policy_channel_type);
		// 团险渠道的保单包含险种责任特殊处理
		if ("11".equals(policy_channel_type)) {

			List<PolicyProductCoverageDTO> policyProductCoverageList = commonQueryDAO
					.queryPolicyProductCoverageListByPolicyNo(policyNo);

			item.setPolicyProductCoverageList(policyProductCoverageList);

		} else {
			// 查询险种信息
			List<PolicyProductDTO> policyProductList = commonQueryDAO
					.queryPolicyProductListByPolicyNo(item.getPolicyNo());
			this.setCanBeSelectedFlag(policyProductList);

			if (policyProductList != null && !policyProductList.isEmpty()) {
				for (Iterator<PolicyProductDTO> it = policyProductList
						.iterator(); it.hasNext();) {
					PolicyProductDTO pp = it.next();
					String productCode = pp.getProductCode();

					if ("Y".equalsIgnoreCase(pp.getIsPrimaryPlan())) {
						item.setMainProductCode(productCode);

					}
					// 查询险种费率的计算方式
					String calType = commonQueryDAO
							.getProductPremCalTypeByProductCode(productCode);
					if ("3".equals(calType)) {
						logger.info("policyNo:" + policyNo + ", prodSeq"
								+ pp.getProdSeq() + " 险种费率计算方式为3，无法操作加保");
						pp.setCanBeSelectedFlag("N");
						pp.setMessage("险种费率计算方式为3");
					}

					pp.setCalType(calType);

					// 查询是否为投连险
					String unitLinkedFlag = commonQueryDAO
							.pdSelProductPropertPro(productCode, "18");
					pp.setUnitLinkedFlag(unitLinkedFlag);

					// 查询是否为万能险
					String universalFlag = commonQueryDAO
							.pdSelProductPropertPro(productCode, "19");
					pp.setUniversalFlag(universalFlag);
				}
			}
			item.setPolicyProductList(policyProductList);
		}

		return item;
	}

	/**
	 * 提供外围系统接口
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtraToBia_3(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return queryServiceItemsInfoExtra(serviceItemsInputDTO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#
	 * generateAcceptDetailDTOList
	 * (com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(
			ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_3 item = (ServiceItemsInputDTO_3) serviceItemsInputDTO;
		ServiceItemsInputDTO_3 originItem = (ServiceItemsInputDTO_3) item
				.getOriginServiceItemsInputDTO();

		String policy_channel_type = commonQueryDAO
				.getChannelTypeByPolicyNo(serviceItemsInputDTO.getPolicyNo());
		// 团险渠道的保单包含险种责任特殊处理
		if ("11".equals(policy_channel_type)) {

			List<PosAcceptDetailDTO> detailList = new ArrayList<PosAcceptDetailDTO>();
			List<PolicyProductCoverageDTO> policyProductCoverageList = item
					.getPolicyProductCoverageList();

			for (int i = 0; i < policyProductCoverageList.size(); i++) {
				PolicyProductCoverageDTO ppcd = (PolicyProductCoverageDTO) policyProductCoverageList
						.get(i);

				if (ppcd.isSelected() && ppcd.getNewSumIns() != null) {
					detailList.add(new PosAcceptDetailDTO(serviceItemsInputDTO
							.getPosNo(), "2", "1", ppcd.getProdSeq() + ";"
							+ ppcd.getCoverageSeq(), "012", ""
							+ ppcd.getSumIns(), "" + ppcd.getNewSumIns()));// 增加后后的责任保额

				}
			}

			return detailList;
		} else {

			List<PolicyProductDTO> policyProductList = item
					.getPolicyProductList();
			List<PolicyProductDTO> originPolicyProductList = originItem
					.getPolicyProductList();
			List<PosAcceptDetailDTO> detailList = new ArrayList<PosAcceptDetailDTO>();
			AcceptDetailGenerator generator = new AcceptDetailGenerator(
					PROC_PROPERTY_CONFIG_MAP, item.getPosNo(), beginGroupNo);
			generator.processSimpleListDTO(policyProductList,
					originPolicyProductList, "prodSeq", false,
					new ItemProcessFilter() {
						@Override
						public boolean needProcess(
								Map<String, Object> listItem,
								Map<String, Object> oldValueItem) {
							return (Boolean) listItem.get("selected")
									&& "Y".equals(listItem
											.get("canBeSelectedFlag"));
						}
					});
			detailList = generator.getResult();
			for (int i = 0; policyProductList != null
					&& i < policyProductList.size(); i++) {
				PolicyProductDTO product = policyProductList.get(i);
				// 成长红包加保特殊处理
				if ("CEAN_AN1".equals(product.getProductCode())) {
					// 非微信受理自主缴费金额等于总金额
					if (item.getAcceptChannel() != null
							&& !"16".equals(item.getAcceptChannel()))

					{
						product.setAddPayPrem(product.getAddPremSum());

						// 红包业务员
						detailList.add(new PosAcceptDetailDTO(
								serviceItemsInputDTO.getPosNo(), "2", "1",
								product.getProdSeq().toString(), "268", null,
								product.getAgentNo()));

						// 是否已通知微信端
						detailList.add(new PosAcceptDetailDTO(
								serviceItemsInputDTO.getPosNo(), "2", "1",
								product.getProdSeq().toString(), "269", null,
								"N"));

					}
					// 总金额
					detailList.add(new PosAcceptDetailDTO(serviceItemsInputDTO
							.getPosNo(), "2", "1", product.getProdSeq()
							.toString(), "264", null, product.getAddPremSum()
							.toString()));
					// 自主缴费金额
					detailList.add(new PosAcceptDetailDTO(serviceItemsInputDTO
							.getPosNo(), "2", "1", product.getProdSeq()
							.toString(), "265", null,
							product.getAddPayPrem() == null ? "0" : product
									.getAddPayPrem().toString()));
					// 红包金额 (公司红包)
					detailList.add(new PosAcceptDetailDTO(serviceItemsInputDTO
							.getPosNo(), "2", "1", product.getProdSeq()
							.toString(), "266", null,
							product.getAddBonusPrem() == null ? "0" : product
									.getAddBonusPrem().toString()));
					// 别人赠送红包金额
					detailList.add(new PosAcceptDetailDTO(serviceItemsInputDTO
							.getPosNo(), "2", "1", product.getProdSeq()
							.toString(), "267", null, product
							.getUserBounsPrem() == null ? "0" : product
							.getUserBounsPrem().toString()));

				}
			}
			return detailList;
		}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#
	 * fillBOMForProcessRuleCheck
	 * (com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto)
	 */
	@Override
	public void fillBOMForProcessRuleCheck(POSBOMPolicyDto bom) {

		String policy_channel_type = commonQueryDAO
				.getChannelTypeByPolicyNo(bom.getPolicyNo());
		if (!"11".equals(policy_channel_type)) {
			List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
			// 新保费 份数
			Map<String, String> item = new HashMap<String, String>();
			item.put("posObject", "2");
			item.put("itemNo", "015");
			posObjectAndItemNoList.add(item);
			// 新基本保额 保额
			item = new HashMap<String, String>();
			item.put("posObject", "2");
			item.put("itemNo", "012");
			posObjectAndItemNoList.add(item);
			List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(
					bom.getPosNo(), posObjectAndItemNoList);
			if (detailList != null && !detailList.isEmpty()) {
				for (PosAcceptDetailDTO detail : detailList) {
					if ("2".equals(detail.getPosObject())
							&& "015".equals(detail.getItemNo())) {
						for (POSUWBOMPlanDto plan : bom.getPlansList()) {
							if (plan.getProdSeq() == Integer.parseInt(detail
									.getObjectValue())) {
								// 录入的是份数
								plan.setUnitsIn(true);
								plan.setCurrentPosPlan(true);
							}
						}
					}
					if ("2".equals(detail.getPosObject())
							&& "012".equals(detail.getItemNo())) {
						for (POSUWBOMPlanDto plan : bom.getPlansList()) {
							if (plan.getProdSeq() == Integer.parseInt(detail
									.getObjectValue())) {
								// 录入的是保额
								plan.setInsLimitIn(true);
								plan.setCurrentPosPlan(true);
							}
						}
					}
				}
			}
			super.fillBOMForProcessRuleCheck(bom);
		}
	}
}
