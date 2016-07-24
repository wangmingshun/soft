package com.sinolife.pos.common.dao.serviceitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyProductCoverageDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_4;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSUWBOMPlanDto;

/**
 * 4 减保
 */
@SuppressWarnings("unchecked")
@Repository("serviceItemsDAO_4")
public class ServiceItemsDAO_4 extends ServiceItemsDAO {

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
		ServiceItemsInputDTO_4 itemDTO = (ServiceItemsInputDTO_4) serviceItemsInputDTO;
		String policyNo = itemDTO.getPolicyNo();
		String policy_channel_type = commonQueryDAO
				.getChannelTypeByPolicyNo(policyNo);
		itemDTO.setPolicyChannelType(policy_channel_type);
		// 团险渠道的保单包含险种责任特殊处理
		if ("11".equals(policy_channel_type)) {

			List<PolicyProductCoverageDTO> policyProductCoverageList = commonQueryDAO
					.queryPolicyProductCoverageListByPolicyNo(policyNo);

			itemDTO.setPolicyProductCoverageList(policyProductCoverageList);

		} else {

			String sql = ServiceItemsDAO_2.class.getName()
					+ ".QUERY_PRODUCT_INFO";// 和项目2的取数逻辑 字段 差不多
			itemDTO.setProductList(getSqlMapClientTemplate().queryForList(sql,
					itemDTO));
			this.setCanBeSelectedFlag(itemDTO.getProductList());
			// 豁免险不可选择
			List<PolicyProductDTO> productList = itemDTO.getProductList();
			if (productList != null && !productList.isEmpty()) {
				for (PolicyProductDTO prd : productList) {
					if ("Y".equals(prd.getCanBeSelectedFlag())
							&& posRulesDAO.isExemptPlan(prd.getProductCode())) {
						prd.setCanBeSelectedFlag("N");
						prd.setMessage("豁免险种不可选择减保");
					}
				}
			}

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
		ServiceItemsInputDTO_4 itemDTO = (ServiceItemsInputDTO_4) serviceItemsInputDTO;
		ServiceItemsInputDTO_4 originDTO = (ServiceItemsInputDTO_4) serviceItemsInputDTO
				.getOriginServiceItemsInputDTO();
		String posNo = itemDTO.getPosNo();
		List<PosAcceptDetailDTO> detailList = new ArrayList<PosAcceptDetailDTO>();
		String policy_channel_type = commonQueryDAO
				.getChannelTypeByPolicyNo(serviceItemsInputDTO.getPolicyNo());

		// 团险渠道的保单包含险种责任特殊处理
		if ("11".equals(policy_channel_type)) {

			List<PolicyProductCoverageDTO> policyProductCoverageList = itemDTO
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

		} else {

			List<PolicyProductDTO> productList = itemDTO.getProductList();
			for (int i = 0; productList != null && i < productList.size(); i++) {
				PolicyProductDTO product = productList.get(i);
				PolicyProductDTO origin = originDTO.getProductList().get(i);
				if (product.isSelected()
						&& "Y".equals(product.getCanBeSelectedFlag())) {
					String seq = "" + product.getProdSeq();
					String no = "" + (++beginGroupNo);
					if ("2".equals(product.getCalType())) {
						detailList.add(new PosAcceptDetailDTO(posNo, "2", "1",
								seq, "006",
								"" + origin.getPeriodStandardPrem(), ""
										+ product.getPeriodStandardPrem(), no));
						// 富德生命安康保年金保险新的保额作为附件险'富德生命附加安康保防癌疾病保险'的保费
						// 特殊处理勾选主险时附件险也需要重新计算保额

						if ("CBAN_LN1".equals(product.getProductCode())) {
							PolicyProductDTO v_product = new PolicyProductDTO();

							for (int j = 0; productList != null
									&& j < productList.size(); j++) {
								v_product = ((PolicyProductDTO) productList
										.get(j));

								if ("CBDD_HN0".equals(v_product
										.getProductCode())) {

									detailList
											.add(new PosAcceptDetailDTO(
													posNo,
													"2",
													"1",
													"" + v_product.getProdSeq(),
			
													"006",
													""
															+ v_product
																	.getPeriodStandardPrem(),
													""
															+

															v_product
																	.getPeriodStandardPrem()
																	.doubleValue()
															* product
																	.getPeriodStandardPrem()
																	.doubleValue()

															/ origin.getPeriodStandardPrem()
																	.doubleValue(),
													no));

								}

							}

						}

					} else {
						detailList.add(new PosAcceptDetailDTO(posNo, "2", "1",
								seq, "007", "" + origin.getBaseSumIns(), ""
										+ product.getBaseSumIns(), no));
					}
					if (StringUtils.isNotBlank(product.getAgreePremSum())) {
						String cash = product.getCashValue() == null ? "0"
								: product.getCashValue().toString();// 按比例缩小后的现金价值
						detailList
								.add(new PosAcceptDetailDTO(posNo, "2", "1",
										seq, "008", cash, product
												.getAgreePremSum(), no));
					}
				}
			}
		}
		return detailList;
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
			item.put("itemNo", "006");
			posObjectAndItemNoList.add(item);
			// 新基本保额 保额
			item = new HashMap<String, String>();
			item.put("posObject", "2");
			item.put("itemNo", "007");
			posObjectAndItemNoList.add(item);
			List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(
					bom.getPosNo(), posObjectAndItemNoList);
			if (detailList != null && !detailList.isEmpty()) {
				for (PosAcceptDetailDTO detail : detailList) {
					if ("2".equals(detail.getPosObject())
							&& "006".equals(detail.getItemNo())) {
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
							&& "007".equals(detail.getItemNo())) {
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

		}

		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_pos_no", bom.getPosNo());
		queryForObject("isAcceptReduce", paraMap);
		if (!"0".equals(paraMap.get("p_flag"))) {
			throw new RuntimeException("判断附加提前给付重疾可以减保出错："
					+ paraMap.get("p_message"));
		}
		boolean isDeductCNDD_IN1 = "Y".equals(paraMap.get("valid_flag"));
		bom.setDeductCNDD_IN1(isDeductCNDD_IN1);
		super.fillBOMForProcessRuleCheck(bom);

	}

}
