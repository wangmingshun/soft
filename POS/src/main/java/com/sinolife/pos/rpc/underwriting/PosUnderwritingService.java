package com.sinolife.pos.rpc.underwriting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sinolife.pos.acceptance.branch.service.BranchQueryService;
import com.sinolife.pos.common.dao.ClientInfoDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.PersonalNoticeDTO;
import com.sinolife.pos.common.dto.PolicyDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PolicyProductPremDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.rpc.ilogjrules.posrules.dao.PosRulesDAO;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMInsuredDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMOwnerDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPlanDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPolicyDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPosServiceItemDto;

/**
 * 保全送自核及自核不通过送人工核保合并接口
 */
@Component
public class PosUnderwritingService {

	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private UnderwritingRuleEnginService underwritingRuleEnginService;
	
	@Autowired
	private BranchQueryService branchQueryService;
	
	@Autowired
	private CommonQueryDAO commonQueryDAO;
	
	@Autowired
	private ClientInfoDAO clientInfoDAO;
	
	@Autowired
	private PosRulesDAO posRulesDAO;
	
	/**
	 * 自核并送人工核保
	 * @param posNo
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> ruleCheckAndSendToManualWork(String posNo, String userId) {
		Map<String, Object> retMap = posRulesDAO.getToundwrtProductDatail(posNo);
		logger.info("getToundwrtProductDatail return:" + PosUtils.describBeanAsJSON(retMap));
		
		String applicantNo = (String) retMap.get("applicantNo");
		String insuredNo = (String) retMap.get("insuredNo");
		Map<String, Object> clientInfoApp = (Map<String, Object>) retMap.get("clientInfoApp");
		Map<String, Object> clientInfoIns = (Map<String, Object>) retMap.get("clientInfoIns");
		
		//送核险种序号集合，后面的代码根据这个集合判断是否为送核险种
		Set<String> undwtProductSeqSet = new HashSet<String>();
		
		if(clientInfoApp != null) {
			clientInfoApp.put("ACTORID", userId);
			clientInfoApp.put("PROCESSNAME", "undwrt_main");
			clientInfoApp.put("UNDERWRITERNO", "");
			clientInfoApp.put("ISCOMPLEX", "Y");
			List<Map<String, Object>> undwrtInfo = (List<Map<String, Object>>) clientInfoApp.get("UNWRTINFO");
			if(undwrtInfo != null && !undwrtInfo.isEmpty()) {
				for(Map<String, Object> item : undwrtInfo) {
					String prodSeq = (String) item.get("PRODSEQ");
					undwtProductSeqSet.add(prodSeq);
				}
			}
		}
		if(clientInfoIns != null) {
			clientInfoIns.put("ACTORID", userId);
			clientInfoIns.put("PROCESSNAME", "undwrt_main");
			clientInfoIns.put("UNDERWRITERNO", "");
			clientInfoIns.put("ISCOMPLEX", "Y");
			List<Map<String, Object>> undwrtInfo = (List<Map<String, Object>>) clientInfoIns.get("UNWRTINFO");
			if(undwrtInfo != null && !undwrtInfo.isEmpty()) {
				for(Map<String, Object> item : undwrtInfo) {
					String prodSeq = (String) item.get("PRODSEQ");
					undwtProductSeqSet.add(prodSeq);
				}
			}
		}
		
		//查保全信息
		PosInfoDTO posInfoDTO = commonQueryDAO.queryPosInfoRecord(posNo);
		String policyNo = posInfoDTO.getPolicyNo();
		String serviceItems = posInfoDTO.getServiceItems();
		
		Map<String, Object> posExtraInfo = posRulesDAO.queryPosExtraInfoByPosNo(posNo);
		Date applyDate = null;
		if(posExtraInfo != null) {
			applyDate = (Date) posExtraInfo.get("APPLY_DATE");
		}
		
		//查健康告知信息
		List<PersonalNoticeDTO> personalNoticeList = branchQueryService.queryPosPersonalNoticeByPosNo(posNo, "all");
		PersonalNoticeDTO appPersonalNotice = null;
		PersonalNoticeDTO insPersonalNotice = null;
		for(PersonalNoticeDTO tmp : personalNoticeList) {
			if(tmp.getClientInfo().getClientNo().equals(applicantNo)) {
				appPersonalNotice = tmp;
			} 
			if(tmp.getClientInfo().getClientNo().equals(insuredNo)) {
				insPersonalNotice = tmp;
			}
		}
		
		logger.info("appPersonalNotice:" + PosUtils.describBeanAsJSON(appPersonalNotice));
		logger.info("insPersonalNotice" + PosUtils.describBeanAsJSON(insPersonalNotice));
		
		//*************** 保单BOM *******************
		SUWBOMPolicyDto bom = SUWBOMPolicyDto.create();
		
		//查保单信息
		PolicyDTO policyDTO = commonQueryDAO.queryPolicyInfoByPolicyNo(policyNo);
		
		if(policyDTO != null) {
			//保险合同号
			bom.setPolicyID(policyDTO.getPolicyNo());
			//受理机构代码
//			bom.setBranchCode(policyDTO.getBranchCode());
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
			//投保日期
			bom.setSubmitDate(policyDTO.getApplyDate());
		}
		
		//*************** 投保人 *******************
		SUWBOMOwnerDto owner = SUWBOMOwnerDto.create();
		
		ClientInformationDTO applicantInfo = clientInfoDAO.selClientinfoForClientno(applicantNo).get(0);
		
		//投保人ID
		owner.setOwnerID(applicantInfo.getClientNo());
		//投保人姓名
		owner.setOwnerName(applicantInfo.getClientName());
		//证件类型
		owner.setIdentityType(applicantInfo.getIdTypeCode());
		//证件号
		owner.setIdentityCode(applicantInfo.getIdNo());
		//出生日期
		owner.setBirthday(applicantInfo.getBirthday());
		//投保人年龄
		owner.setIssueAge(PosUtils.calcAgeYearsFromBirthday(applicantInfo.getBirthday(), applyDate));
		//国籍
		owner.setNationality(applicantInfo.getCountryCode());
		//婚姻状况
		owner.setMarriageStatus(applicantInfo.getMarriageCode());
		//性别
		owner.setGender(applicantInfo.getSexCode());
		//职业代码
		owner.setOccuCode(applicantInfo.getOccupationCode());
		//职务内容
		owner.setOccuContent(applicantInfo.getPosition());
		//（二核）保全变更前生日
		owner.setPosBirthday(applicantInfo.getBirthday());
		//（二核）保全变更前性别
		owner.setPosGender(applicantInfo.getSexCode());
		//与主被保险人关系
		//此处加入保全项目判断，如果是16，并且对应的pos_accept_detail表中存在关系变更，则取该关系，否则取product中的关系
		String newValue = posRulesDAO.getAcceptDetailNewValue(posNo);
		if(PosUtils.isNotNullOrEmpty(newValue)) {
			owner.setRelationWithInsured(newValue);
		} else {
			owner.setRelationWithInsured(posRulesDAO.getRelationWithInsured(policyNo));
		}
		//职业等级
		owner.setOccuGrade(posRulesDAO.getOccupationGradeByCode(applicantInfo.getOccupationCode()));
		//是否黑名单客户
		owner.setBlackList(posRulesDAO.isBlackList(applicantNo, applyDate));
		//健康告知相关
		if(appPersonalNotice != null) {
			Set<String> options = appPersonalNotice.getItemOption();
			if(options.contains("2")) {
				//身高
				if(appPersonalNotice.getHeight() != null) {
					owner.setHeight(appPersonalNotice.getHeight().doubleValue());
				}
				//体重
				if(appPersonalNotice.getWeight() != null) {
					owner.setWeight(appPersonalNotice.getWeight().doubleValue());
				}
				//BMI＝体重(kg)/(身高(m)*身高(m))
				if(appPersonalNotice.getHeight() != null && appPersonalNotice.getWeight() != null && appPersonalNotice.getWeight().doubleValue() != 0D) {
					owner.setBMI(appPersonalNotice.getWeight().doubleValue() * 100D * 100D / (appPersonalNotice.getHeight().doubleValue() * appPersonalNotice.getHeight().doubleValue()));
				}
			}
			if(options.contains("4")) {
				//有无出国计划
				owner.setHasGoabroadPlan("Y".equals(appPersonalNotice.getItemAnswer_4()));
			}
			//补充告知中是否有选是的
			owner.setIsPosDeclarationYes(appPersonalNotice.isAnyDeclarationYes());
		}
		bom.setOwner(owner);
		
		//*************** 被保人 *******************
		SUWBOMInsuredDto[] insuredArr = new SUWBOMInsuredDto[1];
		ClientInformationDTO insuredInfo = clientInfoDAO.selClientinfoForClientno(insuredNo).get(0);
		SUWBOMInsuredDto insured = SUWBOMInsuredDto.create();
		//被保险人ID
		insured.setInsuredID(insuredNo);
		//被保险人姓名
		insured.setInsuredName(insuredInfo.getClientName());
		//证件类型
		insured.setIdentityType(insuredInfo.getIdTypeCode());
		//证件号
		insured.setIdentityCode(insuredInfo.getIdNo());
		//出生日期
		insured.setBirthday(insuredInfo.getBirthday());
		//投保年龄(年)
		insured.setIssueAgeYear(PosUtils.calcAgeYearsFromBirthday(insuredInfo.getBirthday(), policyDTO.getEffectDate()));
		//投保年龄(天)
		insured.setIssueAgeDay(PosUtils.calcAgeDaysFromBirthday(insuredInfo.getBirthday(), policyDTO.getEffectDate()));
		//国籍
		insured.setNationality(insuredInfo.getCountryCode());
		//婚姻状况
		insured.setMarriageStatus(insuredInfo.getMarriageCode());
		//性别
		insured.setGender(insuredInfo.getSexCode());
		//职业代码
		insured.setOccuCode(insuredInfo.getOccupationCode());
		//职务内容
		insured.setOccuContent(insuredInfo.getPosition());
		//（二核）保全变更前生日
		insured.setPosBirthday(insuredInfo.getBirthday());
		//（二核）保全变更前性别
		insured.setPosGender(insuredInfo.getSexCode());
		//职业等级
		insured.setOccuGrade(posRulesDAO.getOccupationGradeByCode(insuredInfo.getOccupationCode()));
		//是否黑名单客户
		insured.setBlackList(posRulesDAO.isBlackList(insuredNo, applyDate));
		//健康告知相关
		if(insPersonalNotice != null) {
			Set<String> options = insPersonalNotice.getItemOption();
			if(options.contains("2")) {
				//身高
				if(insPersonalNotice.getHeight() != null) {
					insured.setHeight(insPersonalNotice.getHeight().doubleValue());
				}
				//体重
				if(insPersonalNotice.getWeight() != null) {
					insured.setWeight(insPersonalNotice.getWeight().doubleValue());
				}
				//BMI＝体重(kg)/(身高(m)*身高(m))
				if(insPersonalNotice.getHeight() != null && insPersonalNotice.getWeight() != null && insPersonalNotice.getWeight().doubleValue() != 0D) {
					insured.setBMI(insPersonalNotice.getWeight().doubleValue() * 100D * 100D / (insPersonalNotice.getHeight().doubleValue() * insPersonalNotice.getHeight().doubleValue()));
				}
			}
			if(options.contains("12")) {
				//是否孕妇
				boolean pregnancy = "Y".equals(insPersonalNotice.getItemAnswer_12_1());
				insured.setPregnancy(pregnancy);
				if(pregnancy) {
					//怀孕周数
					if(insPersonalNotice.getFetationWeeks() != null) {
						insured.setPregnancyWeeks(insPersonalNotice.getFetationWeeks().intValue());
					}
				}
			}
			if(options.contains("4")) {
				//有无出国计划
				insured.setHasGoabroadPlan("Y".equals(insPersonalNotice.getItemAnswer_4()));
			}
			//补充告知中是否有选是的
			insured.setIsPosDeclarationYes(insPersonalNotice.isAnyDeclarationYes());
		}
		insuredArr[0] = insured;
		bom.setInsureds(insuredArr);
		
		//*************** 险种信息 *******************
		
		//原始险种信息
		List<PolicyProductDTO> productList = commonQueryDAO.queryPolicyProductListByPolicyNo(policyNo);
		if(productList == null) {
			productList = new ArrayList<PolicyProductDTO>();
		}
		//查询送核的险种信息
		List<PolicyProductDTO> productTmpList = posRulesDAO.queryPosPolicyProductTmpListByPosNo(posNo);
		
		//用临时表中存在的记录替换原始险种信息中的记录，如不存在，说明为新增险种直接增加
		
		//新旧值映射，用于找到变更前的字段值
		Map<PolicyProductDTO, PolicyProductDTO> replaceMapping = new HashMap<PolicyProductDTO, PolicyProductDTO>();
		
		if(productTmpList != null && !productTmpList.isEmpty()) {
			for(int i = 0; i < productTmpList.size(); i++) {
				PolicyProductDTO policyProductTmp = productTmpList.get(i);
				boolean found = false;//是否找到了原险种标志
				for(int j = 0; j < productList.size(); j++) {
					PolicyProductDTO policyProduct = productList.get(j);
					if(policyProduct.getProdSeq().intValue() == policyProductTmp.getProdSeq().intValue()) {
						//找到原险种，替换它
						found = true;
						productList.set(j, policyProductTmp);
						replaceMapping.put(policyProductTmp, policyProduct);
						break;
					}
				}
				if(!found) {
					//没找到，则为新增的险种，直接增加，新增险种肯定是当前送核险种
					undwtProductSeqSet.add(String.valueOf(policyProductTmp.getProdSeq()));
					replaceMapping.put(policyProductTmp, policyProductTmp);
					productList.add(policyProductTmp);
				}
			}
		}
		//险种列表只保留临时表中存在的记录
		productList.clear();
		productList.addAll(replaceMapping.keySet());
		
		if(productList != null && !productList.isEmpty()) {
			SUWBOMPlanDto[] productArr = new SUWBOMPlanDto[productList.size()];
			for(int i = 0; i < productList.size(); i++) {
				PolicyProductDTO policyProduct = productList.get(i);
				PolicyProductPremDTO premInfo = policyProduct.getPremInfo();
				String productCode = policyProduct.getProductCode();
				SUWBOMPlanDto product = SUWBOMPlanDto.create();
				//险种序号
				product.setProdSeq(policyProduct.getProdSeq());
				//险种代码
				product.setPlanCode(productCode);
				//险种名称
				product.setPlanName(policyProduct.getProductFullName());
				if(policyProduct.getBaseSumIns() != null) {
					//基本保额
					product.setSumAssured(policyProduct.getBaseSumIns().doubleValue());
				}
				//保险期间类型
				product.setCoveragePeriodType(policyProduct.getCoverPeriodType());
				if(policyProduct.getCoveragePeriod() != null) {
					//保险期间值
					product.setCoveragePeriodValue(policyProduct.getCoveragePeriod());
				}
				PolicyProductDTO originProduct = replaceMapping.get(policyProduct);
				if(originProduct != null && originProduct.getPremInfo() != null && originProduct.getPremInfo().getPremTerm() != null) {
					//（二核）保全变更前缴费期限值
					product.setPosPaymentPeriodValue(originProduct.getPremInfo().getPremTerm().doubleValue());
				}
				//是否定期寿险
				product.setIsTermInsurance("Y".equals(commonQueryDAO.pdSelProductPropertPro(productCode, "34")));
				//是否终身寿险
				product.setIsLifeInsurance("Y".equals(commonQueryDAO.pdSelProductPropertPro(productCode, "32")));
				//是否豁免险
				product.setWPPlan("Y".equals(commonQueryDAO.pdSelProductPropertPro(productCode, "98")));
				//是否医疗险
				product.setHospitalizationPlan("Y".equals(commonQueryDAO.pdSelProductPropertPro(productCode, "15")));
				//是否养老金产品
				product.setEndowmentPlan("Y".equals(commonQueryDAO.pdSelProductPropertPro(productCode, "24")));
				//（二核）是否是当前保全项目变更的险种
				product.setIsCurrentPosPlan(undwtProductSeqSet.contains(String.valueOf(policyProduct.getProdSeq())));
				//（二核）失效日期
				Date lapseDate = commonQueryDAO.queryLapseDateByPolicyNoAndProdSeq(policyProduct.getPolicyNo(), policyProduct.getProdSeq());
				if(lapseDate == null) {
					//查不到险种的失效日期，可能为新增的险种，查主险的实效日期
					lapseDate = commonQueryDAO.queryLapseDateByPolicyNo(policyProduct.getPolicyNo());
				}
				product.setLapseDate(lapseDate);
				if(premInfo != null) {
					if(premInfo.getPeriodStandardPrem() != null) {
						//基本保费
						product.setBasicPremium(premInfo.getPeriodStandardPrem().doubleValue());
					}
					//缴费期限类型
					product.setPaymentPeriodType(premInfo.getPremPeriodType());
					if(premInfo.getPremTerm() != null) {
						//缴费期限值
						product.setPaymentPeriodValue(premInfo.getPremTerm().doubleValue());
					}
					//职业级别
					product.setOccuGrade(posRulesDAO.getOccupationGradeByCode(premInfo.getOccupationCode()));
					//缴费周期
					product.setPaymentFreqType(premInfo.getFrequency());
					String calType = commonQueryDAO.getProductPremCalTypeByProductCode(productCode);
					//份数
					if(("2".equals(calType)||"3".equals(calType)) && premInfo.getAnnStandardPrem() != null) {
						//2-保费算保额
						BigDecimal premUnit = commonQueryDAO.getProductPremUnitByProductCode(productCode);
						product.setQuantity(premInfo.getAnnStandardPrem().divide(premUnit).doubleValue());
					} else if(policyProduct.getUnits() != null) {
						product.setQuantity(policyProduct.getUnits());
					}
				}
				productArr[i] = product;
			}
			bom.setCurrentPlans(productArr);
		}
		
		//*************** 当前保全项目 *******************
		SUWBOMPosServiceItemDto serviceItemDTO = SUWBOMPosServiceItemDto.create();
		serviceItemDTO.setAcceptDate(applyDate);
		serviceItemDTO.setCurrentServiceItem(serviceItems);
		bom.setCurrentServiceItem(serviceItemDTO);
		
		ServiceItemsDAO itemsDAO = (ServiceItemsDAO)PlatformContext.getApplicationContext().getBean("serviceItemsDAO_" + serviceItems);
		itemsDAO.fillBOMForUnwrtRuleCheck(bom, posNo, appPersonalNotice, insPersonalNotice);
		
		logger.info("================= 送自核与人工核保接口 ===================");
		logger.info("clientInfoApp:" + clientInfoApp);
		logger.info("clientInfoIns:" + clientInfoIns);
		logger.info("SUWBOMPolicyDto:" + bom);
		Map<String, Object> resultMap = underwritingRuleEnginService.ruleCheckAndSendToManualWork(clientInfoApp, clientInfoIns, bom);
		logger.info("ruleCheckAndSendToManualWork result:" + resultMap);
		return resultMap;
	}
	
}
