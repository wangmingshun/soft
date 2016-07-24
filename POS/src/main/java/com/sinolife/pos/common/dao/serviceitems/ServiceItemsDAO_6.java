package com.sinolife.pos.common.dao.serviceitems;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PersonalNoticeDTO;
import com.sinolife.pos.common.dto.PolicyDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PolicyProductPremDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_1;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_6;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;
import com.sinolife.pos.common.util.detailgenerator.ItemProcessFilter;
import com.sinolife.pos.rpc.ilogjrules.posrules.dao.PosRulesDAO;
import com.sinolife.pos.vip.dao.VipManageDAO;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSUWBOMPlanDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMInsuredDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPolicyDto;

/**
 * 6 复效
 */
@Repository("serviceItemsDAO_6")
public class ServiceItemsDAO_6 extends ServiceItemsDAO {

	@Autowired
	private PosRulesDAO posRulesDAO;
	@Autowired
	private VipManageDAO vipManageDAO;

	private static final Map<String, String> PROC_PROPERTY_CONFIG_MAP;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("renewType", "1|074|1|3|policyNo"); // 复效类型
		map.put("prodSeq", "1|075|1|3|prodSeq"); // 险种序号
		map.put("renewInterest", "1|082|1|3|policyNo"); // 复效实付利息
		map.put("vipGrade", "1|149|1|3|policyNo"); // vip等级

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
		ServiceItemsInputDTO_6 item = (ServiceItemsInputDTO_6) serviceItemsInputDTO;

		String policyNo = item.getPolicyNo();
		PolicyDTO policyInfo = commonQueryDAO
				.queryPolicyInfoByPolicyNo(policyNo);
		Date applyDate = commonQueryDAO.getApplyDateByPosNo(item.getPosNo());
		Map<String, Object> resultMap = null;
		String flag = null;

		// 查询产品信息
		List<PolicyProductDTO> policyProductList = commonQueryDAO
				.queryPolicyProductListByPolicyNo(policyNo);
		this.setCanBeSelectedFlag(policyProductList);
		item.setPolicyProductList(policyProductList);
		if (policyProductList != null) {
			for (PolicyProductDTO pp : policyProductList) {
				if ("Y".equals(pp.getCanBeSelectedFlag())) {
					boolean isShortTerm = posRulesDAO.isShortTerm(pp
							.getProductCode());
					if (isShortTerm) {
						// 短期险判断险种是否在售，不在售的险种不能复效
						PolicyProductPremDTO premInfo = pp.getPremInfo();
						resultMap = commonQueryDAO.checkProductIsOnSale(
								pp.getProductCode(),
								policyInfo.getChannelType(),
								policyInfo.getBranchCode(),
								premInfo.getPremPeriodType(),
								premInfo.getPremTerm(),
								pp.getCoverPeriodType(),
								new BigDecimal(pp.getCoveragePeriod()),
								applyDate);
						if (!"Y".equals(resultMap.get("p_sign"))) {
							throw new RuntimeException("查询险种是否在售失败："
									+ resultMap.get("p_message"));
						}
						if (!"Y".equals(resultMap.get("p_sale_flag"))) {
							pp.setCanBeSelectedFlag("N");
							pp.setMessage("该险种已经停售");
						}
					} else if (!"1".equals(commonQueryDAO
							.pdSelPrdParameterValuePro(pp.getProductCode(),
									"500"))) {
						pp.setCanBeSelectedFlag("N");
						pp.setMessage("该险种不可复效");
					}
				}
				if ("Y".equals(pp.getIsPrimaryPlan())
						&& !"Y".equals(pp.getCanBeSelectedFlag())
						&& !pp.getProductCode().equals("UIAN_DN1")) {
					throw new RuntimeException("主险不可复效，无法操作复效项目");
				}
			}
		}

		// 查询是否存在有效贷款
		resultMap = commonQueryDAO.isPolicyHasValidLoan(policyNo);
		flag = (String) resultMap.get("p_flag");
		String loanFlag = (String) resultMap.get("p_is_has_valid_loan");
		if ("0".equals(flag) && "Y".equals(loanFlag)) {
			// 查询贷款信息
			resultMap = commonQueryDAO.calcPolicyLoanInterestByPolicyNo(
					policyNo, applyDate);
			flag = (String) resultMap.get("p_flag");
			if ("0".equals(flag)) {
				BigDecimal loanAllSum = (BigDecimal) resultMap
						.get("p_loan_all_sum");
				BigDecimal interestSum = (BigDecimal) resultMap
						.get("p_interest_sum");
				item.setLoanAllSum(loanAllSum);
				item.setInterestSum(interestSum);
			}
			item.setHasValidLoanOrApl(true);
		}

		// 查询是否存在有效自垫
		resultMap = commonQueryDAO.isPolicyHasValidApl(policyNo);
		flag = (String) resultMap.get("p_flag");
		String aplFlag = (String) resultMap.get("p_is_has_valid_apl");
		if ("0".equals(flag) && "Y".equals(aplFlag)) {
			// 查询自垫信息
			resultMap = commonQueryDAO.calcPolicyAplInterestByPolicyNo(
					policyNo, applyDate);
			flag = (String) resultMap.get("p_flag");
			if ("0".equals(flag)) {
				BigDecimal aplLoanAllSum = (BigDecimal) resultMap
						.get("p_loan_all_sum");
				BigDecimal aplInterestSum = (BigDecimal) resultMap
						.get("p_interest_sum");
				item.setAplLoanAllSum(aplLoanAllSum);
				item.setAplInterestSum(aplInterestSum);
			}
			item.setHasValidLoanOrApl(true);
		}

		// 查询客户是否为VIP
		Map<String, Object> retMap = vipManageDAO
				.getClientVipGradeByClientNo(item.getClientNo());

		if ("0".equals(retMap.get("p_flag"))) {
			String vipGrade = (String) retMap.get("p_vip_grade");
			// String vipDesc = (String) retMap.get("p_vip_desc");
			item.setVipGrade(vipGrade);
		}
		

		return item;
		}
	
	/**
	 * 提供给外围保全
	 */
	public ServiceItemsInputDTO_6 queryServiceItemsInfoExtra_6(
			ServiceItemsInputDTO serviceItemsInputDTO) {
		return (ServiceItemsInputDTO_6) queryServiceItemsInfoExtra(serviceItemsInputDTO);
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
		ServiceItemsInputDTO_6 item = (ServiceItemsInputDTO_6) serviceItemsInputDTO;
		AcceptDetailGenerator generator = new AcceptDetailGenerator(
				PROC_PROPERTY_CONFIG_MAP, item.getPosNo(), beginGroupNo);
		generator.processSimpleDTO(item);
		
		generator.processSimpleListDTO(item.getPolicyProductList(),
				new ItemProcessFilter() {
					@Override
					public boolean needProcess(Map<String, Object> listItem,
							Map<String, Object> oldValueItem) {
						// return (Boolean) listItem.get("selected") &&
						// "Y".equals(listItem.get("canBeSelectedFlag"));
						return (Boolean) listItem.get("selected");
					}
				});
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
		item.put("posObject", "1");
		item.put("itemNo", "075");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(
				bom.getPosNo(), posObjectAndItemNoList);
		if (detailList != null && !detailList.isEmpty()) {
			for (PosAcceptDetailDTO detailInternal : detailList) {
				if ("1".equals(detailInternal.getPosObject())
						&& "075".equals(detailInternal.getItemNo())) {
					for (POSUWBOMPlanDto plan : bom.getPlansList()) {
						if ((plan.getProdSeq() == Integer
								.parseInt(detailInternal.getNewValue()))) {
							plan.setCurrentPosPlan(true);
						}
					}
				}
			}
		}
		
		// 保单是否能够享受vip免息复效
		//boolean isFreeInterest =posRulesDAO.isFreeInterest(bom.getPolicyNo(), bom.getApplyDate());
		
		bom.setFreeInterest(true);
		
		super.fillBOMForProcessRuleCheck(bom);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#
	 * fillBOMForUnwrtRuleCheck
	 * (com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPolicyDto, java.lang.String,
	 * com.sinolife.pos.common.dto.PersonalNoticeDTO,
	 * com.sinolife.pos.common.dto.PersonalNoticeDTO)
	 */
	@Override
	public void fillBOMForUnwrtRuleCheck(SUWBOMPolicyDto bom, String posNo,
			PersonalNoticeDTO appPersonalNotice,
			PersonalNoticeDTO insPersonalNotice) {
		super.fillBOMForUnwrtRuleCheck(bom, posNo, appPersonalNotice,
				insPersonalNotice);
		// 复效时，被保人的投保年龄按照申请日期来算
		SUWBOMInsuredDto insured = bom.getInsureds()[0];
		Date applyDate = commonQueryDAO.getApplyDateByPosNo(posNo);
		// 投保年龄(年)
		insured.setIssueAgeYear(PosUtils.calcAgeYearsFromBirthday(
				insured.getBirthday(), applyDate));
		// 投保年龄(天)
		insured.setIssueAgeDay(PosUtils.calcAgeDaysFromBirthday(
				insured.getBirthday(), applyDate));
	}

}
