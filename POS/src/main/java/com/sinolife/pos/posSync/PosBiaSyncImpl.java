package com.sinolife.pos.posSync;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.aspectj.weaver.tools.ISupportsMessageContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinolife.esbpos.web.BiabPosService;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.posSync.dao.CommonSyncDAO;
import com.sinolife.pos.posSync.dao.PosBiaSyncDAO;
import com.sinolife.sf.platform.runtime.PlatformContext;

@Service("posBiaSyncImpl")
public class PosBiaSyncImpl {
	private Logger logger = Logger.getLogger(getClass());
	@Autowired
	private PosBiaSyncDAO posBiaSyncDAO;
	@Autowired
	CommonQueryDAO commonQueryDAO;
	@Autowired
	CommonSyncDAO commonSyncDAO;
	
	/**
	 * 查询中介机构查询码接口
	 * @param map
	 * @return map
	 */
	public Map<String, Object> queryBranchCheckCode(String pos_no, String businessType,
			String businessNoType, String businessNo){
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		retMap.put("TRAN_CODE", "JGBranchCheck");		//TRAN_CODE	
		retMap.put("PARTNER_ID", "jg_jspt");			//PARTNER_ID
		retMap.put("BUSINESS_NO", businessNo);			//BUSINESS_NO
		/*
		 * TRAN_CODE
		 * PARTNER_ID
		 * BUSINESS_NO
		 * POLICY_LIST(policyList)
		 * 			add--POLICY(policyMap)
		 * 				put--...(4个<String, Object>)
		 * 				put--COVERAGE_LIST(productList)
		 * 						add--COVERAGE(productMap)
		 * 							put--...(1个<String, Object>)
		 * 				put--...(2个<String, Object>)
		 * 				put--ENDORSEMENT_LIST(endOrsementList)
		 * 						add--ENDORSEMENT(endOrsementMap)
		 * 							put--...(4个<String, Object>)
		 */
		//查询保单号
		String policy_no = commonQueryDAO.getPolicyNoByPosNo(pos_no);
		List<Map<String, Object>> agencyInfo = posBiaSyncDAO.getAgencyInfo(pos_no, policy_no);
		String policyRegNo = posBiaSyncDAO.getPolicyRegNo(policy_no);
		if(policyRegNo == null || "".equals(policyRegNo)) {
			logger.error("保单登记码不存在！！！");
			resultMap.put("SL_RSLT_CODE", "999901");
			resultMap.put("SL_RSLT_MESG", "保单登记码不存在！！！");
			commonSyncDAO.updateSyncControl(businessType, businessNoType, businessNo, "N", "保单登记码不存在！！！");
		} else {
			//POLICY_LIST start-----
			List<Map<String,Object>> policyList = new ArrayList<Map<String,Object>>();
			for(Map<String, Object> map : agencyInfo) {
				//POLICY_Map start ---
				Map<String,Object> policyMap = new HashMap<String,Object>();
				policyMap.put("SerialNo", map.get("SerialNo"));		//序号
				policyMap.put("AgencyCode", map.get("AgencyCode"));	//中介机构组织机构代码
				policyMap.put("AgencyType", map.get("AgencyType"));	//中介机构代理类型 01:专业代理；02：兼业代理
				policyMap.put("ComCode", map.get("ComCode"));			//保单归属保险公司代码	
				
				//COVERAGE_LIST START-------------------
				List<Map<String,Object>> productList = new ArrayList<Map<String,Object>>();
				//COVERAGE  START
				Map<String,Object> productMap = new HashMap<String,Object>();
				productMap.put("CoverageType", map.get("CoverageType"));	//险类代码
				//COVERAGE  END
				productList.add(productMap);
				policyMap.put("COVERAGE_LIST", productList);
				//COVERAGE_LIST END------------------------
				
				policyMap.put("PolicyApplyDate", map.get("PolicyApplyDate"));	//签投保单日期
				policyMap.put("PolicyFlag", "01");		//保单标记
				
				//ENDORSEMENT_LIST START-----------------
				List<Map<String,Object>> endOrsementList = new ArrayList<Map<String,Object>>();
				//ENDORSEMENT  START
				Map<String,Object> endOrsementMap = new HashMap<String,Object>();
				endOrsementMap.put("SerialNo", map.get("SerialNo"));				//序号
				endOrsementMap.put("PolicyRegNo", policyRegNo);	//保单登记码
				endOrsementMap.put("ComCode", map.get("ComCode"));		//批单归属保险公司代码
				endOrsementMap.put("EndorsementApplyDate", map.get("EndorsementApplyDate"));//批改申请日期；精确到分钟
				//ENDORSEMENT  END
				endOrsementList.add(endOrsementMap);
				policyMap.put("ENDORSEMENT_LIST", endOrsementList);
				//ENDORSEMENT_LIST END-------------------
				
				//POLICY_Map end
				policyList.add(policyMap);
			}
			retMap.put("POLICY_LIST", policyList);
			//POLICY_LIST end-----			
			try {
				BiabPosService biabPosService = PlatformContext.getEsbContext().getEsbService(BiabPosService.class);
				resultMap = biabPosService.handleBiagESBRequestMap(retMap);
				String resultCode = (String) resultMap.get("SL_RSLT_CODE");
				String resultMsg = (String) resultMap.get("SL_RSLT_MESG");
			
				//999999表示成功，其他都表示失败
				if("999999".equals(resultCode)) {
					//获取中介信息查询信息
					Map<String, Object> agencyMap = (Map<String, Object>) resultMap.get("QUERYNO_LIST");
					//取得中介信息查询码
					String queryCode = (String) agencyMap.get("QueryCode");
					//如果成功则将data_key设置为queryCode(中介信息查询码)
					commonSyncDAO.insertSyncDetail(businessType, businessNoType, 
							businessNo, "queryCode", queryCode);
					//将中介信息查询码放入resultMap中
					resultMap.put("queryCode", queryCode);
				}else{
					//记录失败信息，将失败原因更新到同步表
					commonSyncDAO.updateSyncControl(businessType, businessNoType, businessNo, "N", resultMsg);
				}
				
			} catch (Exception e) {
				//记录失败信息，将失败原因更新到同步表
				commonSyncDAO.updateSyncControl(businessType, businessNoType, businessNo, "N", e.getMessage());
				logger.error("查询中介机构查询码异常！", e);
				throw new RuntimeException("查询中介机构查询码异常！");
			}
		}
		return resultMap;
	}
	/**
	 * 批单登记接口
	 * @param map
	 * @return map
	 */
	public Map<String, Object> posRegister(String pos_no, String queryCode, 
			String businessType, String businessNoType, String businessNo){
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		retMap.put("TRAN_CODE", "JGBathRegister");	//TRAN_CODE
		retMap.put("PARTNER_ID", "jg_jspt");	//PARTNER_ID
		retMap.put("BUSINESS_NO", businessNo);			//BUSINESS_NO
		/*
		 * 结构图
		 * PARTNER_ID
		 * TRAN_CODE
		 * BUSINESS_NO
		 * ENDORSEMENT_LIST(endorsementList)
		 * 			add--ENDORSEMENT(endorsementMap)
		 * 				put--.....(8个<String, Object>)
		 * 				put--COVERAGE_LIST(coverageList)
		 * 						add--COVERAGE(coverageMap)
		 * 							put--.....(6个<String, Object>)
		 */
		//Map<String, Object> agencyMap = queryBranchCheckCode(policy_no, pos_no);
		//String flag = (String) agencyMap.get("SL_RSLT_CODE");
		//List<Map<String,>> flag = (String) agencyMap.get("SL_RSLT_CODE");
		//查询保单号
		String policy_no = commonQueryDAO.getPolicyNoByPosNo(pos_no);
		//查询保单信息
		List<Map<String, Object>> posRegister = posBiaSyncDAO.posRegister(policy_no, pos_no, queryCode);
		//ENDORSEMENT_LIST start--------------
		List<Map<String, Object>> endorsementList = new ArrayList<Map<String,Object>>();
		for(Map<String, Object> map : posRegister) {
			//ENDORSEMENT_MAP start
			Map<String, Object> endorsementMap = new HashMap<String, Object>();
			endorsementMap.put("QueryCode", queryCode);	//中介信息查询码
			endorsementMap.put("AgencyCode", map.get("AgencyCode"));//中介机构组织机构代码
			endorsementMap.put("AreaFlag", map.get("AreaFlag"));	//保单归属地
			endorsementMap.put("EndorsementNo", map.get("EndorsementNo"));//批单号	
			endorsementMap.put("EndorsementType", map.get("EndorsementType"));//批改类型	01：批改保费（含部分退保）  02 ：整单退保
			endorsementMap.put("EndorsementDate", map.get("EndorsementDate"));//批改生效日期	
			endorsementMap.put("CurrencyCode", map.get("CurrencyCode"));	//币种	
			endorsementMap.put("TotalPremium", map.get("TotalPremium"));	//签单总保费
			
			//COVERAGE_LIST start-----
			List<Map<String, Object>> coverageList = new ArrayList<Map<String,Object>>();
			//COVERAGE_Map start
			Map<String, Object> coverageMap = new HashMap<String, Object>();
			coverageMap.put("CoverageType", map.get("CoverageType"));		//险类代码
			coverageMap.put("CoverageCode", map.get("CoverageCode"));		//险种代码（寿险叫保险产品）
			coverageMap.put("CoverageName", map.get("CoverageName"));		//险种名称
			coverageMap.put("EffectiveDate", map.get("EffectiveDate"));		//起保日期
			coverageMap.put("ExpireDate", map.get("ExpireDate"));			//终保日期
			coverageMap.put("Premium", map.get("Premium"));					//险种保费金额
			//COVERAGE_Map end
			coverageList.add(coverageMap);
			endorsementMap.put("COVERAGE_LIST", coverageList);
			//COVERAGE_LIST end-----
			
			//ENDORSEMENT_MAP end
			endorsementList.add(endorsementMap);
		}
		retMap.put("ENDORSEMENT_LIST", endorsementList);
		//ENDORSEMENT_LIST end--------------
		
		try {
			 BiabPosService biabPosService = PlatformContext.getEsbContext().getEsbService(BiabPosService.class);
			 resultMap = biabPosService.handleBiagESBRequestMap(retMap);
			 String resultCode = (String) resultMap.get("SL_RSLT_CODE");
			 String resultMsg = (String) resultMap.get("SL_RSLT_MESG");
			
			 if("999999".equals(resultCode)) {
				//批单登记码
				String endorsementRegNo = (String) resultMap.get("EndorsementRegNo");
				//如果成功则将data_key设置为endorsementRegNo(批单登记码)
				commonSyncDAO.insertSyncDetail(businessType, businessNoType, businessNo, "endorsementRegNo", endorsementRegNo);
				commonSyncDAO.updateSyncControl(businessType, businessNoType, businessNo, "Y", "");
			} else {
				//记录失败信息，将失败原因更新到同步表
				commonSyncDAO.updateSyncControl(businessType, businessNoType, businessNo, "N", resultMsg);				
			}
		} catch (Exception e) {
			//记录失败信息，将失败原因更新到同步表
			commonSyncDAO.updateSyncControl(businessType, businessNoType, businessNo, "N", e.getMessage());				
			logger.error("批单登记码异常！", e);
			throw new RuntimeException("批单登记码异常！");
	    }
		return resultMap;
	}

	public void handler(String policyNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("TRAN_CODE", "SDPolicyRegister");
		paraMap.put("PARTNER_ID", "sd_warn");
		
		//Head start
		Map<String, Object> Head = new HashMap<String, Object>();
		Head.put("RequestType", "C01");
		Head.put("UserCode", "");
		Head.put("PassWord", "");
		//Head添加到paraMap中
		paraMap.put("Head", Head);
		//Head end
		
		//Body(Map) start
		Map<String, Object> Body = new HashMap<String, Object>();
		
		//----->BaseInfo(List) start
		List<Map<String, Object>> BaseInfo = new ArrayList<Map<String,Object>>();
		//BaseInfoMap start
		Map<String, Object> BaseInfoMap = new HashMap<String, Object>();
		BaseInfoMap.put("NoDataFlag", "1");
		//BaseInfoMap 添加到BaseInfo(List)中
		BaseInfo.add(BaseInfoMap);
		//BaseInfoMap end
		//BaseInfo(List) 添加到Body中
		Body.put("BaseInfo", BaseInfo);
		//BaseInfo(List) end
		
		//----->Policy(List) start
		/*
		List<Map<String,Object>> Policy = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> policyList = null;//posBiaSyncDAO.getPolicyInfo(policyNo);
		for(Map<String, Object> map : policyList) {
			String banchCode = ((String) map.get("AREAFLAG"));
			map.put("AREAFLAG", (banchCode.equals("3700") ? "3701" : banchCode) + "00");
			
			//policyMap(Map) start
			Map<String, Object> policyMap = new HashMap<String, Object>();
			policyMap.put("PolicyNo", map.get("POLICYNO"));
			policyMap.put("InsureDate", map.get("INSUREDATE"));
			policyMap.put("AreaFlag", map.get("AREAFLAG"));
			policyMap.put("BillDate", map.get("BILLDATE"));
			policyMap.put("ReceiptDate", map.get("RECEIPTDATE"));
			policyMap.put("StartDate", map.get("STARTDATE"));
			policyMap.put("EndDate", map.get("ENDDATE"));
			policyMap.put("PolicyStatus", map.get("POLICYSTATUS"));
			policyMap.put("InsureForm", map.get("INSUREFORM"));
			policyMap.put("ApplicantType", map.get("APPLICANTTYPE"));
			policyMap.put("ProducerType", map.get("PRODUCERTYPE"));
			policyMap.put("ProducerName", map.get("PRODUCERNAME"));
			policyMap.put("PolCheckResult", map.get("POLCHECKRESULT"));
			policyMap.put("CheckResultDate", map.get("CHECKRESULTDATE"));
			policyMap.put("RefuseReason", map.get("REFUSEREASON"));
			
			//PersonalApplicant(List) start
			List<Map<String,Object>> PersonalApplicant = new ArrayList<Map<String,Object>>();
			//PersonalApplicantMap start
			Map<String, Object> PersonalApplicantMap = new HashMap<String, Object>();
			PersonalApplicantMap.put("ApplicantName", "");
			PersonalApplicantMap.put("Gender", "");
			PersonalApplicantMap.put("Birthday", "");
			PersonalApplicantMap.put("CredentialType", "");
			PersonalApplicantMap.put("CredentialNo", "");
			PersonalApplicantMap.put("InsuredRelationship", "");
			//PersonalApplicantMap 添加到PersonalApplicant(List)中
			PersonalApplicant.add(PersonalApplicantMap);
			//PersonalApplicantMap end
			//PersonalApplicant(List) 添加到policyMap中
			policyMap.put("PersonalApplicant", PersonalApplicant);
			//PersonalApplicant(List) end
			
			//GroupApplicant(List) start
			List<Map<String,Object>> GroupApplicant = new ArrayList<Map<String,Object>>();
			//GroupApplicantMap start
			Map<String, Object> GroupApplicantMap = new HashMap<String, Object>();
			GroupApplicantMap.put("GroupName", "");
			GroupApplicantMap.put("GroupAddress", "");
			GroupApplicantMap.put("GroCredentialCode", "");
			GroupApplicantMap.put("GroCredentialNo", "");
			GroupApplicantMap.put("OrganizationType", "");
			GroupApplicantMap.put("IndustryClassification", "");
			GroupApplicantMap.put("Corporation", "");
			GroupApplicantMap.put("ContactName", "");
			//GroupApplicantMap 添加到GroupApplicant(List)中
			GroupApplicant.add(PersonalApplicantMap);
			//GroupApplicantMap end
			//GroupApplicant(List) 添加到policyMap中
			policyMap.put("GroupApplicant", GroupApplicant);
			//GroupApplicant(List) end
			
			//Insured(List) start
			List<Map<String,Object>> Insured = new ArrayList<Map<String,Object>>();
			//InsuredMap start
			Map<String, Object> InsuredMap = new HashMap<String, Object>();
			InsuredMap.put("InsuredName", "");
			InsuredMap.put("Gender", "");
			InsuredMap.put("Birthday", "");
			InsuredMap.put("CredentialType", "");
			InsuredMap.put("CredentialNo", "");
			InsuredMap.put("ApplicantRelationship", "");
			InsuredMap.put("Address", "");
			InsuredMap.put("IsAbnormal", "");
			InsuredMap.put("InsuredType", "");
			InsuredMap.put("MainInsRelationship", "");
			InsuredMap.put("IsDesignBeneficiary", "");
			
			//Coverage(List) start
			List<Map<String, Object>> Coverage = new ArrayList<Map<String,Object>>();
			//coverageMap start
			Map<String, Object> coverageMap = new HashMap<String, Object>();
			coverageMap.put("CovPolicyNo", "");
			coverageMap.put("KindCode", "");
			coverageMap.put("CoverageCode", "");
			coverageMap.put("ComCoverageCode", "");
			coverageMap.put("ComCoverageName", "");
			coverageMap.put("CovStartDate", "");
			coverageMap.put("CovEndDate", "");
			coverageMap.put("CoverageNature", "");
			coverageMap.put("CovBasicAmount", "");
			coverageMap.put("CovMaxAmount", "");
			coverageMap.put("CoverageStatus", "");
			coverageMap.put("CovCheckResult", "");
			coverageMap.put("CovCheckResultDate", "");
			coverageMap.put("RefuseReason", "");
			//coverageMap 添加到Coverage(List)中
			Coverage.add(coverageMap);
			//coverageMap end
			//Coverage(List) 添加到InsuredMap中
			InsuredMap.put("Coverage", Coverage);
			//Coverage(List) end
			
			//Beneficiary(List) start
			List<Map<String, Object>> Beneficiary = new ArrayList<Map<String,Object>>();
			//beneficiaryMap start
			Map<String, Object> beneficiaryMap = new HashMap<String, Object>();
			beneficiaryMap.put("BeneficiaryName", "");
			beneficiaryMap.put("Gender", "");
			beneficiaryMap.put("Birthday", "");
			beneficiaryMap.put("CredentialType", "");
			beneficiaryMap.put("CredentialNo", "");
			beneficiaryMap.put("InsuredRelationship", "");
			beneficiaryMap.put("BenefitAllocation", "");
			beneficiaryMap.put("BeneficiaryNo", "");
			beneficiaryMap.put("Rate", "");
			//beneficiaryMap 添加到 Beneficiary(List)中
			Beneficiary.add(beneficiaryMap);
			//beneficiaryMap end
			//Beneficiary(List) 添加到InsuredMap中
			InsuredMap.put("Beneficiary", Beneficiary);
			//Beneficiary(List) end
			
			
			//InsuredMap 添加到Insured(List)中
			Insured.add(InsuredMap);
			//InsuredMap end
			//Insured(List) 添加到policyMap中
			policyMap.put("Insured", Insured);
			//Insured(List) end
			
			//policyMap(Map)添加到Policy(List中)
			Policy.add(policyMap);
			//policyMap(Map) end
		}
		
		
		//Policy(List)添加到Body中
		Body.put("Policy", Policy);
		//Policy(List) end
		*/
		
		//Body添加到paraMap中
		paraMap.put("Body", Body);
		//Body(Map) end
		
		BiabPosService biabPosService = PlatformContext.getEsbContext().getEsbService(BiabPosService.class);
		Map<String, Object> resultMap = biabPosService.toPartnerEsbHandle(paraMap);
		System.out.println();
		
	}
	
	public void toPartnerEsbHandle(String policyNo, String businessNo, String businessType,
			String businessNoType) {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("TRAN_CODE", "SDPolicyRegister");
		paramMap.put("PARTNER_ID", "jg_sdpt");
		
		Map<String, Object> BaseInfo = new HashMap<String, Object>();
		
		BaseInfo.put("NoDataFlag", "0");
		
		paramMap.put("BaseInfo", BaseInfo);
		Map<String, Object> policy = new HashMap<String, Object>();
		try {
			Map<String, Object> policyTemp = posBiaSyncDAO
					.getPolicyInfo(policyNo);
			String banchCode = ((String) policyTemp.get("AREAFLAG"))
					.substring(2, 6);
			policyTemp.put("AREAFLAG", (banchCode.equals("3700") ? "3701"
					: banchCode) + "00");
			String insureDate = (String) policyTemp.get("INSUREDATE");
			policy.put("PolicyNo", policyTemp.get("POLICYNO"));
			policy.put("InsureDate", policyTemp.get("INSUREDATE"));
			policy.put("AreaFlag", policyTemp.get("AREAFLAG"));
			policy.put("BillDate", policyTemp.get("BILLDATE"));
			policy.put("ReceiptDate", policyTemp.get("RECEIPTDATE"));
			policy.put("StartDate", policyTemp.get("STARTDATE"));
			policy.put("EndDate", policyTemp.get("ENDDATE"));
			policy.put("PolicyStatus", policyTemp.get("POLICYSTATUS"));
			policy.put("InsureForm", policyTemp.get("INSUREFORM"));
			policy.put("ApplicantType", policyTemp.get("APPLICANTTYPE"));
			policy.put("ProducerType", policyTemp.get("PRODUCERTYPE"));
			policy.put("ProducerName", policyTemp.get("PRODUCERNAME"));
			policy.put("PolCheckResult", policyTemp.get("POLCHECKRESULT"));
			policy.put("CheckResultDate", 
					(policyTemp.get("CHECKRESULTDATE") == null || "".equals(policyTemp.get("CHECKRESULTDATE"))) 
					? insureDate : policyTemp.get("CHECKRESULTDATE"));
			policy.put("RefuseReason", policyTemp.get("REFUSEREASON"));

			Map<String, Object> personalApplicanMap = new HashMap<String, Object>();
			Map<String, String> personalApplicanMapTemp = posBiaSyncDAO
					.getApplicantInfo(policyNo);

			String sex_code = personalApplicanMapTemp.get("GENDER");
			if ("1".equals(sex_code)) {
				sex_code = "0";// 男

			} else if ("2".equals(sex_code)) {
				sex_code = "1";// 女
			} else {
				sex_code = "2";// 不详

			}
			;
			personalApplicanMapTemp.put("GENDER", sex_code);

			personalApplicanMap.put("ApplicantName",
					personalApplicanMapTemp.get("APPLICANTNAME"));
			personalApplicanMap.put("Gender",
					personalApplicanMapTemp.get("GENDER"));
			personalApplicanMap.put("Birthday",
					personalApplicanMapTemp.get("BIRTHDAY"));
			personalApplicanMap.put("CredentialType",
					personalApplicanMapTemp.get("CREDENTIALTYPE"));
			personalApplicanMap.put("CredentialNo",
					personalApplicanMapTemp.get("CREDENTIALNO"));
			personalApplicanMap.put("InsuredRelationship",
					personalApplicanMapTemp.get("INSUREDRELATIONSHIP"));

			Map<String, Object> insured = new HashMap<String, Object>();
			Map<String, Object> insuredTemp = posBiaSyncDAO
					.getInsuredInfo(policyNo);

			sex_code = (String) insuredTemp.get("GENDER");
			if ("1".equals(sex_code)) {
				sex_code = "0";// 男

			} else if ("2".equals(sex_code)) {
				sex_code = "1";// 女
			} else {
				sex_code = "2";// 不详

			}
			;

			insuredTemp.put("GENDER", sex_code);

			insured.put("InsuredName", insuredTemp.get("INSUREDNAME"));
			insured.put("Gender", insuredTemp.get("GENDER"));
			insured.put("Birthday", insuredTemp.get("BIRTHDAY"));
			insured.put("CredentialType", insuredTemp.get("CREDENTIALTYPE"));
			insured.put("CredentialNo", insuredTemp.get("CREDENTIALNO"));
			insured.put("ApplicantRelationship",
					insuredTemp.get("APPLICANTRELATIONSHIP"));
			insured.put("Address", insuredTemp.get("ADDRESS"));
			insured.put("IsAbnormal", insuredTemp.get("ISABNORMAL"));
			insured.put("InsuredType", insuredTemp.get("INSUREDTYPE"));
			insured.put("MainInsRelationship",
					insuredTemp.get("MAININSRELATIONSHIP"));
			if (((BigDecimal) (insuredTemp.get("ISDESIGNBENEFICIARY")))
					.intValue() > 0) {

				insuredTemp.put("ISDESIGNBENEFICIARY", "1");
			} else {
				insuredTemp.put("ISDESIGNBENEFICIARY", "0");

			}

			insured.put("IsDesignBeneficiary",
					insuredTemp.get("ISDESIGNBENEFICIARY"));

			List coverageList = new ArrayList();

			Map<String, Object> coverageM = new HashMap<String, Object>();
			Map<String, Object> coverageMap = new HashMap<String, Object>();

			List<Map<String, String>> cList = posBiaSyncDAO
					.getCoverage(policyNo);
			if (cList != null) {
				for (int i = 0; i < cList.size(); i++) {

					Map<String, String> m = (Map<String, String>) cList.get(i);
					coverageM = new HashMap<String, Object>();
					coverageMap = new HashMap<String, Object>();						
					coverageMap.put("CovPolicyNo", m.get("CovPolicyNo"));
					coverageMap.put("KindCode", m.get("KindCode"));
					coverageMap.put("CoverageCode", m.get("CoverageCode"));
					coverageMap.put("ComCoverageCode", m.get("ComCoverageCode"));
					coverageMap.put("ComCoverageName", m.get("ComCoverageName"));
					coverageMap.put("CovStartDate", m.get("CovStartDate"));
					coverageMap.put("CovEndDate", m.get("CovEndDate"));
					coverageMap.put("CoverageNature", m.get("CoverageNature"));
					coverageMap.put("CovBasicAmount", m.get("CovBasicAmount"));
					coverageMap.put("CovMaxAmount", m.get("CovMaxAmount"));
					coverageMap.put("CoverageStatus", m.get("CoverageStatus"));
					coverageMap.put("CovCheckResult", m.get("CovCheckResult"));
					coverageMap.put("CovCheckResultDate", 
							(m.get("CovCheckResultDate") == null || "".equals(m.get("CovCheckResultDate")))
							? insureDate : m.get("CovCheckResultDate"));
					coverageMap.put("RefuseReason", m.get("RefuseReason"));
					
					coverageM.put("Coverage", coverageMap);
					
					coverageList.add(coverageM);

				}
			}

			List beneficiaryList = new ArrayList();
			Map<String, Object> beneficiaryMap = new HashMap<String, Object>();
			
			Map<String, Object> beneficiaryM = new HashMap<String, Object>();
			List<Map<String, String>> bList = posBiaSyncDAO
					.getBeneficiary(policyNo);
			if (bList != null) {
				for (int i = 0; i < bList.size(); i++) {

					Map<String, String> m = (Map<String, String>) bList.get(i);
					beneficiaryMap = new HashMap<String, Object>();
					 beneficiaryM = new HashMap<String, Object>();

					beneficiaryMap.put("BeneficiaryName", m.get("Beneficiaryname"));
					beneficiaryMap.put("Gender", m.get("Gender"));
					beneficiaryMap.put("Birthday", m.get("Birthday"));
					beneficiaryMap.put("CredentialType", m.get("CredentialType"));
					beneficiaryMap.put("CredentialNo", m.get("CredentialNo"));
					beneficiaryMap.put("InsuredRelationship", m.get("InsuredRelationship"));
					beneficiaryMap.put("BenefitAllocation", m.get("BenefitAllocation"));
					beneficiaryMap.put("BeneficiaryNo", m.get("BeneficiaryNo"));
					beneficiaryMap.put("Rate", m.get("Rate"));
					
					beneficiaryM.put("Beneficiary", beneficiaryMap);
					
					beneficiaryList.add(beneficiaryM);
				}
			}

			// policy.put("PersonalApplicant", applicant);

			insured.put("CoverageList", coverageList);
			insured.put("BeneficiaryList", beneficiaryList);

			policy.put("PersonalApplican", personalApplicanMap);
			policy.put("Insured", insured);
			paramMap.put("Policy", policy);
			BiabPosService biabPosService = PlatformContext.getEsbContext().getEsbService(BiabPosService.class);
			Map<String, Object> resultMap = biabPosService.toPartnerEsbHandle(paramMap);
			String resultCode = (String) resultMap.get("SL_RSLT_CODE");
			
			
		    String  responseCode= (String) ((Map)resultMap.get("Head")).get("ResponseCode");

			String errorMessage = (String) resultMap.get("ErrorMessage");
			if ("999999".equals(resultCode)&&"1".equals(responseCode)  ) {
//				uwBiaSyncDAO.updateSyncControl(businessType,
//						businessNoType, businessNo, "Y", "");
//				//批单登记码
//				String endorsementRegNo = (String) resultMap.get("EndorsementRegNo");
//				//如果成功则将data_key设置为endorsementRegNo(批单登记码)
//				commonSyncDAO.insertSyncDetail(businessType, businessNoType, businessNo, "endorsementRegNo", endorsementRegNo);
				commonSyncDAO.updateSyncControl(businessType, 
						businessNoType, businessNo, "Y", "");
			} else {
				// 记录失败信息，将失败原因更新到同步表
				commonSyncDAO.updateSyncControl(businessType,
						businessNoType, businessNo, "N", errorMessage);
			}

		}

		catch (Exception e) {
			// 记录失败信息，将失败原因更新到同步表
			commonSyncDAO.updateSyncControl(businessType, businessNoType,
					businessNo, "N", e.getMessage());
			logger.error("山东承保数据推送异常！", e);
		
		}

	
	}
}
