package com.sinolife.pos.rpc.ilogjrules.posrules;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.PolicyDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.rpc.ilogjrules.posrules.dao.PosRulesDAO;
import com.sinolife.sf.ruleengine.service.RuleEngineClient;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMBeneficiaryDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMInsuredDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMOwnerDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPlanDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPolicyDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.UWBOMVerifyResultDto;

@Component
public class UWJrulesCheck {
	
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	@Qualifier("ilogRulesUW")
	private RuleEngineClient ruleEngineClient;
	
	@Autowired
	private CommonQueryDAO commonQueryDAO;
	
	@Autowired
	private PosRulesDAO posRulesDAO;
	
	/**
	 * 执行投保规则检查
	 * @param bom
	 * @return
	 */
	public UWBOMVerifyResultDto excuteRules(SUWBOMPolicyDto bom) {
		logger.info("投保规则检查SUWBOMPolicyDto:" + bom);
		UWBOMVerifyResultDto result = (UWBOMVerifyResultDto) ruleEngineClient.excuteRules(bom);
		logger.info("投保规则检查UWBOMVerifyResultDto:" + result);
		return result;
	}
	
	/**
	 * 创建投保规则检查BOM
	 * @param posNo
	 * @return
	 */
	public SUWBOMPolicyDto createBOM(String posNo) {
		//查保全信息
		PosInfoDTO posInfoDTO = commonQueryDAO.queryPosInfoRecord(posNo);
		String policyNo = posInfoDTO.getPolicyNo();
		
		//*************** 保单BOM *******************
		SUWBOMPolicyDto bom = SUWBOMPolicyDto.create();
		
		//查保单信息
		PolicyDTO policyDTO = commonQueryDAO.queryPolicyInfoByPolicyNo(policyNo);
		if(policyDTO != null) {
			//保险合同号
			bom.setPolicyID(policyDTO.getPolicyNo());
			
			//投保日期
			bom.setSubmitDate(policyDTO.getApplyDate());
			
			//获取中支代码，如果为空则使用policyDTO.getBranchCode()
			String branchCode = 
				commonQueryDAO.getSeniorBranchInfo(policyDTO.getBranchCode(), 
						"03", "CODE");
			if(PosUtils.isNotNullOrEmpty(branchCode)) {
				bom.setBranchCode(branchCode);
			} else {
				//受理机构代码
				bom.setBranchCode(policyDTO.getBranchCode());
			}
			//渠道码
			bom.setChannelCode(policyDTO.getChannelType());
			//主险代码
			bom.setBasicPlanCode(policyDTO.getPrimaryPlanProductCode());
			//缴费周期
			bom.setPaymentFreqType(policyDTO.getPremInfo().getFrequency());
		}
		
		//查BOM信息
		Map<String, Object> retMap = posRulesDAO.getSuwbomElement(posNo, policyNo);
		String flag = (String) retMap.get("p_flag");
		String message = (String) retMap.get("p_message");
		if(!"0".equals(flag)) {
			throw new RuntimeException("查询投保规则检查BOM出错：" + message);
		}
		
		//主附险缴费期间是否一致
		String similarlyColyrind = (String) retMap.get("p_prem_term_chk");
		bom.setSimilarlyColyrind(similarlyColyrind);
		
		//投保人
		SUWBOMOwnerDto owner = (SUWBOMOwnerDto) retMap.get("p_app_client_rec");
		bom.setOwner(owner);
		
		//被保人
		SUWBOMInsuredDto[] insuredArr = (SUWBOMInsuredDto[]) retMap.get("p_ins_client_list");
		bom.setInsureds(insuredArr);
		
		//当前保单的险种列表
		SUWBOMPlanDto[] productArr = (SUWBOMPlanDto[]) retMap.get("p_policy_product_list");
		if(productArr != null && productArr.length > 0) {
			for(SUWBOMPlanDto plan : productArr) {
				//份数
				String calType = commonQueryDAO.getProductPremCalTypeByProductCode(plan.getPlanCode());
				if("2".equals(calType)||"3".equals(calType)) {//3有个固定保费的如CBED_FN1
					//2-保费算保额
					BigDecimal premUnit = commonQueryDAO.getProductPremUnitByProductCode(plan.getPlanCode());
					plan.setQuantity(plan.getBasicPremium() / premUnit.doubleValue());
				}
			}
		}
		bom.setCurrentPlans(productArr);
		
		//受益人列表
		SUWBOMBeneficiaryDto[] beneficiaryArr = (SUWBOMBeneficiaryDto[]) retMap.get("p_beneficiary_list");
		bom.setBeneficiaries(beneficiaryArr);
		
		//关联保单号
		retMap = posRulesDAO.getRelatePolicyInfo(policyNo);
		if("Y".equals(retMap.get("hasRelatedPolicy"))) {
			String relatedPolicyNo = (String) retMap.get("relatedPolicyNo");
			SUWBOMPolicyDto[] associatedPolicyArr = new SUWBOMPolicyDto[1];
			SUWBOMPolicyDto associatedPolicy = SUWBOMPolicyDto.create();
			associatedPolicy.setPolicyID(relatedPolicyNo);
			associatedPolicyArr[0] = associatedPolicy;
			bom.setAssociatedPolicy(associatedPolicyArr);
		}
		
		//是否是保全投保规则
		bom.setPosTB(true);
		
		return bom;
	}
	
}
