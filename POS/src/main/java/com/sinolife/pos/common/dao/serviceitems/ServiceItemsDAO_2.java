package com.sinolife.pos.common.dao.serviceitems;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.unihub.framework.util.common.DateUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.PosSurvivalDueDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_2;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSUWBOMPlanDto;

/**
 * 退保
 * 
 */
@SuppressWarnings("unchecked")
@Repository("serviceItemsDAO_2")
public class ServiceItemsDAO_2 extends ServiceItemsDAO {

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
		ServiceItemsInputDTO_2 itemDTO = (ServiceItemsInputDTO_2) serviceItemsInputDTO;
		itemDTO.setProductList(queryForList("QUERY_PRODUCT_INFO", itemDTO));
		this.setCanBeSelectedFlag(itemDTO.getProductList());
		queryForObject("QUERY_PREM_LOAN_APL", itemDTO);

		// 退保需确认保单补发次数
		itemDTO.setPolicyProvideTimeEditable(true);

		// 能否操作退保金转新投保单(转投保业务适用于除投连险的以外的所有我司承保保单)
		List<PolicyProductDTO> lsProduct = itemDTO.getProductList();

		/*
		 * if (lsProduct != null && lsProduct.size() > 0) { PolicyProductDTO
		 * policyProductDTO = (PolicyProductDTO) lsProduct .get(0);
		 * 
		 * String flag = commonQueryDAO.pdSelProductPropertPro(
		 * policyProductDTO.getProductCode(), "18"); if (!"Y".equals(flag) &&
		 * "1".equals(policyProductDTO.getDutyStatus()))
		 * 
		 * {
		 * 
		 * double proPremSum = commonQueryDAO.getProductPremSum(
		 * itemDTO.getPolicyNo(), policyProductDTO.getProdSeq() .toString(),
		 * DateUtil.stringToDate(itemDTO .getApplyDate())).doubleValue();
		 * 
		 * if (itemDTO.getLoanSum() != null &&
		 * !"0".equals(itemDTO.getLoanSum())) { proPremSum = proPremSum - new
		 * Double(itemDTO.getLoanSum()).doubleValue() - new
		 * Double(itemDTO.getAplSum()).doubleValue();
		 * 
		 * 
		 * } itemDTO.setPolicyCashValue(proPremSum);
		 * 
		 * 
		 * itemDTO.setCanDoSurrenderToNewApplyBarCode("Y"); } }
		 */

		if (lsProduct != null && lsProduct.size() > 0) {
			double proPremSum = 0;
			// 根据保单号查寻是否可转保
			String isCannotToNew = (String) queryForObject(
					"getCannotToNewByPolicyNo", itemDTO.getPolicyNo());
			if ("Y".equals(isCannotToNew)) {
				for (PolicyProductDTO policyProductDTO : lsProduct) {

					String flag = commonQueryDAO.pdSelProductPropertPro(
							policyProductDTO.getProductCode(), "18");
					if (!"Y".equals(flag)
							&& "1".equals(policyProductDTO.getDutyStatus())) {
						proPremSum += commonQueryDAO.getProductPremSum(
								itemDTO.getPolicyNo(),
								policyProductDTO.getProdSeq().toString(),
								DateUtil.stringToDate(itemDTO.getApplyDate()))
								.doubleValue();
					}
				}

				if (proPremSum > 0) {
					if (itemDTO.getLoanSum() != null
							&& !"0".equals(itemDTO.getLoanSum())) {
						proPremSum = proPremSum
								- new Double(itemDTO.getLoanSum())
										.doubleValue();
					}
					if (itemDTO.getAplSum() != null
							&& !"0".equals(itemDTO.getAplSum())) {
						proPremSum = proPremSum
								- new Double(itemDTO.getAplSum()).doubleValue();
					}
					itemDTO.setPolicyCashValue(proPremSum);
					itemDTO.setCanDoSurrenderToNewApplyBarCode("Y");
				}
			}
		}

		return itemDTO;
	}

	/**
	 * 提供给外围保全（银保通除外）
	 */
	public ServiceItemsInputDTO_2 queryServiceItemsInfoExtra_2(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return (ServiceItemsInputDTO_2) queryServiceItemsInfoExtra(serviceItemsInputDTO);
	}

	/**
	 * 提供给银保通保全专用
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtraToBia_2(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_2 itemDTO = (ServiceItemsInputDTO_2) serviceItemsInputDTO;
		itemDTO.setProductList(queryForList("QUERY_PRODUCT_INFO", itemDTO));
		this.setCanBeSelectedFlag(itemDTO.getProductList());
		queryForObject("QUERY_PREM_LOAN_APL", itemDTO);

		// 退保需确认保单补发次数
		itemDTO.setPolicyProvideTimeEditable(false);

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
		ServiceItemsInputDTO_2 itemDTO = (ServiceItemsInputDTO_2) serviceItemsInputDTO;

		List<PosAcceptDetailDTO> detailList = new ArrayList<PosAcceptDetailDTO>();
		detailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "1", "1",
				itemDTO.getPolicyNo(), "091", null,
				itemDTO.isAllSurrender() ? "Y" : "N"));// 是否整单退保
		detailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "1", "1",
				itemDTO.getPolicyNo(), "140", null, itemDTO.isDouBt() ? "Y"
						: "N"));// 是否可疑交易
		detailList
				.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "2", "1",
						itemDTO.getPolicyNo(), "009", null, itemDTO
								.getSurrenderCause()));// 退保原因
		if (itemDTO.getSpecialRetreatReason() != null) {
			detailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "5", "1",
					itemDTO.getPolicyNo(), "013", null, itemDTO
							.getSpecialRetreatReason()));// 协议退费原因

		}
		if (itemDTO.isDouBt() == true && itemDTO.getDoubtList().length > 0) {
			for (int j = 0; j < itemDTO.getDoubtList().length; j++) {
				detailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "2",
						"1", "" + j, "024", null, (itemDTO.getDoubtList())[j]
								.toString()));// 可疑交易类型

			}
		}
		boolean isPaySurvival = false;
		Map<String, Object> criteriaMap = new HashMap<String, Object>();
		List<PolicyProductDTO> productList = itemDTO.getProductList();
		for (int i = 0; productList != null && i < productList.size(); i++) {
			PolicyProductDTO product = productList.get(i);
			if (product.isSelected()
					&& "Y".equals(product.getCanBeSelectedFlag())) {
				String prem = product.getCashValue() == null ? "0" : String
						.valueOf(product.getCashValue());
				if ("6".equals(itemDTO.getSpecialFunc())) {
					prem = product.getSpecialSurSum();
					if ("CIDD_SN1".equals(product.getProductCode())) {
						product.setAgreePremSum(product.getSpecialSurSum());
					}
				}
				detailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "2",
						"1", "" + product.getProdSeq(), "010", prem, product
								.getAgreePremSum()));
				if (StringUtils.isNotBlank(product.getBranchPercent())
						&& "9".equals(itemDTO.getSpecialFunc())) {// 勾选了投诉业务

					detailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(),
							"2", "1", "" + product.getProdSeq(), "022", null,
							product.getBranchPercent()));// 特殊件—协议退保（投诉业务）分公司承担比例'

				}

				if (StringUtils.isNotBlank(product.getBackBenefit())
						&& ("9".equals(itemDTO.getSpecialFunc()) || "8"
								.equals(itemDTO.getSpecialFunc()))) {
					detailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(),
							"2", "1", "" + product.getProdSeq(), "023", null,
							product.getBackBenefit()));// “协议退保-业务品质|投诉业务”新增“应追回既得利益”

				}

				// 从咨诉系统受理的退保如果投被保人满足一定的条件需要把未领取的生存金一起支付出去 DMP-2813
				if ("13".equals(itemDTO.getAcceptChannel())) {
					criteriaMap = new HashMap<String, Object>();
					criteriaMap.put("policyNo", itemDTO.getPolicyNo());
					criteriaMap.put("prodSeq", product.getProdSeq());
					List<PosSurvivalDueDTO> productSurvivalDueList = commonQueryDAO
							.queryProductSurvivalDueByCriteria(criteriaMap);
					if (productSurvivalDueList != null
							&& productSurvivalDueList.size() > 0) {
						isPaySurvival = true;
						for (Iterator<PosSurvivalDueDTO> it = productSurvivalDueList
								.iterator(); it.hasNext();) {
							PosSurvivalDueDTO posSurvivalDueDTO = it.next();
							detailList.add(new PosAcceptDetailDTO(itemDTO
									.getPosNo(), "2", "3", ""
									+ product.getProdSeq(), "021", null,
									posSurvivalDueDTO.getSurvivalSeq() + ""));
						}
					}

				}
			}

		}
		// 从咨诉领取生存金
		if (isPaySurvival) {
			detailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "1", "3",
					itemDTO.getPolicyNo(), "120", null, "1"));

		}
		if ("7".equals(itemDTO.getSpecialFunc())) {// 契撤重投
			detailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "1", "1",
					itemDTO.getPolicyNo(), "137", null, itemDTO
							.getApplyBarCode()));
		}
		// 退保金转新投保单金额
		if (StringUtils.isNotBlank(itemDTO.getSurrenderToNewApplyBarCode())
				&& StringUtils.isNotBlank(itemDTO.getNewApplyBarCodePrem())) {

			detailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "1", "1",
					itemDTO.getPolicyNo(), "154", null, itemDTO
							.getSurrenderToNewApplyBarCode()));
			BigDecimal newApplyBarCodePrem = new BigDecimal(
					itemDTO.getNewApplyBarCodePrem());
            //咨诉渠道受理
			if ("13".equals(itemDTO.getAcceptChannel())) {
				
				detailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(),
						"2", "1", itemDTO.getPolicyNo(), "270", null, 
						itemDTO.getNewApplyBarCodePrem()));
				

			} else {
				if (newApplyBarCodePrem.doubleValue() > itemDTO
						.getPolicyCashValue()) {
					// 如果新單金額大於退保金額，則轉保金額為退保金額
					detailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(),
							"2", "1", itemDTO.getPolicyNo(), "270", null, ""
									+ itemDTO.getPolicyCashValue()));
				} else {
					// 否則為新單金額
					detailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(),
							"2", "1", itemDTO.getPolicyNo(), "270", null,
							itemDTO.getNewApplyBarCodePrem()));
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
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		// 选中的险种
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "2");
		item.put("itemNo", "010");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(
				bom.getPosNo(), posObjectAndItemNoList);
		if (detailList != null && !detailList.isEmpty()) {
			for (PosAcceptDetailDTO detail : detailList) {
				if ("2".equals(detail.getPosObject())
						&& "010".equals(detail.getItemNo())) {
					for (POSUWBOMPlanDto plan : bom.getPlansList()) {
						if (plan.getProdSeq() == Integer.parseInt(detail
								.getObjectValue())) {
							plan.setCurrentPosPlan(true);
						}
					}
				}
			}
		}

		bom.setEntireSingleSurrender(posRulesDAO.entireSingleSurrender(bom
				.getPosNo()));

		if ("7".equals(bom.getAcceptSource())) {
			ServiceItemsInputDTO_2 itemDTO = new ServiceItemsInputDTO_2();
			itemDTO.setApplyDate(DateUtil.getCurrentDateString());
			itemDTO.setPolicyNo(bom.getPolicyNo());
			itemDTO.setProductList(queryForList("QUERY_PRODUCT_INFO", itemDTO));

			List<PolicyProductDTO> productList = itemDTO.getProductList();

			for (int i = 0; productList != null && i < productList.size(); i++) {
				PolicyProductDTO product = productList.get(i);
				if ("CEAN_AN1".equals(product.getProductCode())) {

					bom.setPremSum(product.getCashValue() == null ? 0.0d
							: product.getCashValue().doubleValue());
					bom.setPremFlag("-1");

				}
			}

		}
		super.fillBOMForProcessRuleCheck(bom);
	}

	/**
	 * 转保健相关校验，并返回保费等信息
	 * 
	 * @param oldApplyBarCode
	 * @param newApplyBarCode
	 * @return
	 */
	public Map<String, Object> getTranslateApplyInfo(String oldApplyBarCode,
			String newApplyBarCode) {

		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_old_apply_bar_code", oldApplyBarCode);
		paraMap.put("p_new_apply_bar_code", newApplyBarCode);
		queryForObject("getTranslateApplyInfo", paraMap);
		return paraMap;
	}
}
