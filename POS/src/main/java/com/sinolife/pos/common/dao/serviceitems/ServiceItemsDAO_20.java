package com.sinolife.pos.common.dao.serviceitems;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.acceptance.branch.dto.QueryAndAddClientDTO;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.PersonalNoticeDTO;
import com.sinolife.pos.common.dto.PolicyContactInfoDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_20;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMInsuredDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMOwnerDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSUWBOMPlanDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMOwnerDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPolicyDto;

/**
 * 20 投保人变更
 */
@Repository("serviceItemsDAO_20")
public class ServiceItemsDAO_20 extends ServiceItemsDAO {

	public static final Map<String, String> CLIENT_CONFIG_MAP;//需经办的客户明细
	public static final Map<String, String> CLIENT_RECORD_MAP;//已提交到数据库，仅作记录的客户明细
	public static final Map<String, String> POLICY_CONFIG_MAP;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("applicantNo", 		"1|029|1|3|policyNo");	//新投保人客户号
		map.put("relation", 		"2|003|1|3|policyNo");	//新投保人与被保人关系
		map.put("relationDesc",		"2|004|1|3|policyNo");	//新投保人与被保人其他关系描述
		
		map.put("clientName", 		"3|047|1|3|clientNo");	//客户姓名
		map.put("birthday", 		"3|048|1|3|clientNo");	//客户生日
		map.put("sexCode", 			"3|049|1|3|clientNo");	//客户性别代码
		map.put("idTypeCode", 		"3|050|1|3|clientNo");	//客户证件类型代码
		map.put("idNo", 			"3|051|1|3|clientNo");	//客户证件号
		map.put("occupationCode",	"3|052|1|3|clientNo");	//客户职业代码
		map.put("countryCode", 		"3|053|1|3|clientNo");	//客户国籍
		map.put("nationCode", 		"3|054|1|3|clientNo");	//客户民族
		map.put("registerPlace",	"3|055|1|3|clientNo");	//客户户籍
		map.put("yearIncome", 		"3|056|1|3|clientNo");	//客户年收入
		map.put("marriageCode",		"3|057|1|3|clientNo");	//客户婚姻情况代码
		map.put("workUnit", 		"3|058|1|3|clientNo");	//客户工作单位
		map.put("position", 		"3|059|1|3|clientNo");	//客户职务
		
		map.put("phoneType", 		"3|016|1|3|clientNo");	//电话类型
		map.put("areaNo", 	    	"3|017|1|3|clientNo");	//电话区号
		map.put("phoneNo", 			"3|018|1|3|clientNo");	//电话号码

		map.put("provinceCode", 	"3|025|1|3|clientNo");	//省代码
		map.put("cityCode", 		"3|026|1|3|clientNo");	//市代码
		map.put("areaCode", 		"3|027|1|3|clientNo");	//区县代码
		map.put("detailAddress",	"3|028|1|3|clientNo");	//地址详细
		map.put("postalcode", 		"3|029|1|3|clientNo");	//邮编
		
		map.put("emailAddress", 	"3|034|1|3|clientNo");	//邮件地址

		CLIENT_CONFIG_MAP = map;
		
		Map<String, String> recordMap = new ConcurrentHashMap<String, String>();	
		recordMap.put("clientName", 	"3|094|1|3|clientNo");	//客户姓名
		recordMap.put("birthday", 		"3|093|1|3|clientNo");	//客户生日
		recordMap.put("sexCode", 		"3|092|1|3|clientNo");	//客户性别代码
		recordMap.put("idTypeCode", 	"3|091|1|3|clientNo");	//客户证件类型代码
		recordMap.put("idNo", 			"3|090|1|3|clientNo");	//客户证件号
		recordMap.put("occupationCode",	"3|089|1|3|clientNo");	//客户职业代码
		recordMap.put("countryCode", 	"3|088|1|3|clientNo");	//客户国籍
		recordMap.put("nationCode", 	"3|087|1|3|clientNo");	//客户民族
		recordMap.put("registerPlace",	"3|086|1|3|clientNo");	//客户户籍
		recordMap.put("yearIncome", 	"3|085|1|3|clientNo");	//客户年收入
		recordMap.put("marriageCode",	"3|084|1|3|clientNo");	//客户婚姻情况代码
		recordMap.put("workUnit", 		"3|083|1|3|clientNo");	//客户工作单位
		recordMap.put("position", 		"3|082|1|3|clientNo");	//客户职务
		
		recordMap.put("phoneType", 		"3|081|1|3|clientNo");	//电话类型
		recordMap.put("areaNo", 	    "3|080|1|3|clientNo");	//电话区号
		recordMap.put("phoneNo", 		"3|079|1|3|clientNo");	//电话号码

		recordMap.put("provinceCode", 	"3|078|1|3|clientNo");	//省代码
		recordMap.put("cityCode", 		"3|077|1|3|clientNo");	//市代码
		recordMap.put("areaCode", 		"3|076|1|3|clientNo");	//区县代码
		recordMap.put("detailAddress",	"3|075|1|3|clientNo");	//地址详细
		recordMap.put("postalcode", 	"3|074|1|3|clientNo");	//邮编
		
		recordMap.put("emailAddress", 	"3|073|1|3|clientNo");	//邮件地址

		CLIENT_RECORD_MAP = recordMap;
		
		Map<String, String> mapPolicy = new ConcurrentHashMap<String, String>();
		mapPolicy.put("addressSeq", "1|006|1|3|policyNo");	//保单地址SEQ
		mapPolicy.put("emailSeq",	"1|007|1|3|policyNo");	//保单电邮SEQ
		mapPolicy.put("phoneSeq",	"1|008|1|3|policyNo");	//保单电话SEQ
		mapPolicy.put("smsService",	"1|009|1|3|policyNo");	//保单短信服务
		
		POLICY_CONFIG_MAP = mapPolicy;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#queryServiceItemsInfoExtra(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_20 itemDTO = (ServiceItemsInputDTO_20)serviceItemsInputDTO;
		
		itemDTO.setOriginApplicantNo(commonQueryDAO.getApplicantByPolicyNo(itemDTO.getPolicyNo()));
		itemDTO.setInsuredNo(commonQueryDAO.getInsuredOfPrimaryPlanByPolicyNo(itemDTO.getPolicyNo()));
		
		itemDTO.setGroupNo(maxGroupNo(itemDTO.getPosNo()));
		return itemDTO;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#generateAcceptDetailDTOList(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_20 itemDTO = (ServiceItemsInputDTO_20)serviceItemsInputDTO;
		ServiceItemsInputDTO_20 originDTO = (ServiceItemsInputDTO_20)itemDTO.getOriginServiceItemsInputDTO();
		originDTO.setApplicantNo(itemDTO.getOriginApplicantNo());
		//当前投保人信息
		QueryAndAddClientDTO newQaa = itemDTO.getQueryAndAddClientDTO();
		//原始投保人信息
		QueryAndAddClientDTO oldQaa = originDTO.getQueryAndAddClientDTO();
		
		AcceptDetailGenerator generator = new AcceptDetailGenerator(CLIENT_CONFIG_MAP, itemDTO.getPosNo(), beginGroupNo);
		
		/*增加证件有效期 edit by wangmingshun start*/
		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();
		if(itemDTO.getQueryAndAddClientDTO().getClientInfo().getIsLongidnoValidityDate()){
			acceptDetailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "3", "1",
					newQaa.getClientNo(), "100", newQaa.getClientInfo().getIdnoValidityDate()==null?"":new SimpleDateFormat("yyyy-MM-dd").format(newQaa.getClientInfo().getIdnoValidityDate()),
					"9999-01-01"));
		}else{
			acceptDetailList.add(new PosAcceptDetailDTO(itemDTO.getPosNo(), "3", "1",
					newQaa.getClientNo(), "100", newQaa.getClientInfo().getIdnoValidityDate()==null?"":new SimpleDateFormat("yyyy-MM-dd").format(newQaa.getClientInfo().getIdnoValidityDate()),
					new SimpleDateFormat("yyyy-MM-dd").format(newQaa.getClientInfo().getIdnoValidityDate())));
		}
		/*增加证件有效期edit by wangmingshun start*/
		
		//真正投保人变更的信息明细
		generator.processSimpleDTO(itemDTO, originDTO);
		//年收入
		generator.processSimpleDTO(newQaa);
		//是否通知官网取消加挂电话
		acceptDetailList.add(new PosAcceptDetailDTO(
				serviceItemsInputDTO.getPosNo(), "2", "1",
				originDTO.getClientNo(), "271", null,
				"N"));
		
		//有过客户信息的查询，修改，新增的情况下
		if(newQaa != null){
			ClientInformationDTO originClientInfo = newQaa.getOriginClientInfo();
			ClientInformationDTO clientInfo = newQaa.getClientInfo();
			PolicyContactInfoDTO originContactInfo = newQaa.getOriginContactInfo();
			PolicyContactInfoDTO contactInfo = newQaa.getContactInfo();
			
			//客户联系信息的明细
			generator.processSimpleListDTO(clientInfo.getClientPhoneList(),	originClientInfo.getClientPhoneList(),"phoneSeq", false);
			generator.processSimpleListDTO(clientInfo.getClientAddressList(),originClientInfo.getClientAddressList(), "addressSeq", false);
			generator.processSimpleListDTO(clientInfo.getClientEmailList(), originClientInfo.getClientEmailList(), "emailSeq", false);
			
			//客户信息
			generator.processSimpleDTO(clientInfo, originClientInfo);
			
			//已提交的客户信息，写入detail表仅做记录
			if(newQaa.isNewClientAdded() && newQaa.getClientNo().equals(itemDTO.getApplicantNo())){
				generator.setConfigMap(CLIENT_RECORD_MAP);
				generator.setDefaultObjectValue(clientInfo.getClientNo());
				
				generator.processSimpleDTO(originClientInfo);
				generator.processSimpleListDTO(originClientInfo.getClientPhoneList());
				generator.processSimpleListDTO(originClientInfo.getClientAddressList());
				generator.processSimpleListDTO(originClientInfo.getClientEmailList());
				
				generator.setDefaultObjectValue(null);
			}
			
			//保单联系信息
			generator.setConfigMap(POLICY_CONFIG_MAP);
			generator.processSimpleDTO(contactInfo, originContactInfo);
		}
		
		acceptDetailList.addAll(generator.getResult());
		
		return acceptDetailList;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#validate(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(ServiceItemsInputDTO serviceItemsInputDTO, Errors err) {
		super.validate(serviceItemsInputDTO, err);
	}
	
	/**
	 * 查询保单联系信息
	 * @param itemsInput
	 * @return
	 */
	public PolicyContactInfoDTO queryPolicyContactInfo(String policyNo){
		return (PolicyContactInfoDTO)queryForObject("QUERY_POLICY_CONTACT_BYNO", policyNo);
	}

	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#fillBOMForProcessRuleCheck(com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto)
	 */
	@Override
	public void fillBOMForProcessRuleCheck(POSBOMPolicyDto bom) {
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		Map<String, String> item = new HashMap<String, String>();
		//新投保人号码
		item.put("posObject", "1");
		item.put("itemNo", "029");
		posObjectAndItemNoList.add(item);
		//新投保人与被保人关系
		item = new HashMap<String, String>();
		item.put("posObject", "2");
		item.put("itemNo", "003");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(bom.getPosNo(), posObjectAndItemNoList);
		if(detailList != null && !detailList.isEmpty()) {
			for(PosAcceptDetailDTO detail : detailList) {
				if("1".equals(detail.getPosObject()) && "029".equals(detail.getItemNo())) {
					//投保人信息
					String applicantNo = detail.getNewValue();
					if(StringUtils.isNotBlank(applicantNo)) {
						ClientInformationDTO applicantInfo = clientInfoDAO.selClientinfoForClientno(applicantNo).get(0);
						POSBOMOwnerDto owner = POSBOMOwnerDto.create();
						if(applicantInfo != null) {
							//ID
							owner.setId(applicantInfo.getClientNo());
							//姓名
							owner.setName(applicantInfo.getClientName());
							//性别
							owner.setSex(applicantInfo.getSexCode());
							//出生日期
							owner.setBirthDate(applicantInfo.getBirthday());
							//证件类型
							owner.setIdType(applicantInfo.getIdTypeCode());
							//证件号码
							owner.setIdNo(applicantInfo.getIdNo());
							//已死亡
							owner.setDeath(applicantInfo.getDeathDate() != null);
							//年龄（周岁）
							owner.setAgeInYear(PosUtils.calcAgeYearsFromBirthday(applicantInfo.getBirthday(), bom.getApplyDate()));
							//年龄（月数）
							owner.setAgeInMonth(PosUtils.calcAgeMonthsFromBirthday(applicantInfo.getBirthday(), bom.getApplyDate()));
						}
						for(PosAcceptDetailDTO detailInternal : detailList) {
							if("2".equals(detailInternal.getPosObject()) && "003".equals(detailInternal.getItemNo())) {
								//投被保人关系
								owner.setInsuredRelationship(detailInternal.getNewValue());
							}
						}
						//是当前保全项目变更的客户对象
						owner.setCurrentPosObj(true);
						bom.setApplicant(owner);
						
						//豁免险的被保人信息要替换成新投保人的
						Set<String> currentInsuredSet = new HashSet<String>();
						boolean hasExemptPlan = false;
						for(POSUWBOMPlanDto plan : bom.getPlansList()) {
							if(plan.isExempt()) {
								plan.setInsurantNo(applicantNo);
								hasExemptPlan = true;
							}
							//需要保留的被保人列表，如果有豁免险，取到的是投保人客户号
							currentInsuredSet.add(plan.getInsurantNo());
						}
						if(hasExemptPlan) {
							//没有豁免险的情况，重新组织被保人列表
							List<POSBOMInsuredDto> insuredList = new ArrayList<POSBOMInsuredDto>();
							POSBOMInsuredDto foundInsured = null;
							for(POSBOMInsuredDto insured : bom.getInsurantList()) {
								if(currentInsuredSet.contains(insured.getId())) {
									//留下需要留下的
									insuredList.add(insured);
								}
								//顺便找一下现有被保人列表里是否已经有了当前变更的投保人
								if(applicantNo.equals(insured.getId()) && foundInsured == null) {
									foundInsured = insured;
								}
							}
							if(foundInsured == null) {
								//没找到需要添加
								foundInsured = POSBOMInsuredDto.create();
								//ID
								foundInsured.setId(applicantNo);
								insuredList.add(foundInsured);
							}
							//设置投保人的其他属性
							//姓名
							foundInsured.setName(applicantInfo.getClientName());
							//性别
							foundInsured.setSex(applicantInfo.getSexCode());
							//出生日期
							foundInsured.setBirthDate(applicantInfo.getBirthday());
							//证件类型
							foundInsured.setIdType(applicantInfo.getIdTypeCode());
							//证件号码
							foundInsured.setIdNo(applicantInfo.getIdNo());
							//已死亡
							foundInsured.setDeath(applicantInfo.getDeathDate() != null);
							//年龄（周岁）
							foundInsured.setAgeInYear(PosUtils.calcAgeYearsFromBirthday(applicantInfo.getBirthday(), bom.getApplyDate()));
							//年龄（月数）
							foundInsured.setAgeInMonth(PosUtils.calcAgeMonthsFromBirthday(applicantInfo.getBirthday(), bom.getApplyDate()));
							//变更前的累计身故保额(该属性涉及的规则目前忽略，强制设定为150000)
							foundInsured.setCurrentDeathInsSum(150000D);
							//变更后的累计身故保额(该属性涉及的规则目前忽略，强制设定为10000)
							foundInsured.setPosedDeathInsSum(10000D);
							//是主被保险人
							foundInsured.setPrimaryInsured(applicantNo.equals(commonQueryDAO.getInsuredOfPrimaryPlanByPolicyNo(bom.getPolicyNo())));
							bom.setInsurantList(insuredList.toArray(new POSBOMInsuredDto[insuredList.size()]));
						}
					}
				}
			}
		}
		super.fillBOMForProcessRuleCheck(bom);
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#fillBOMForUnwrtRuleCheck(com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPolicyDto, java.lang.String, com.sinolife.pos.common.dto.PersonalNoticeDTO, com.sinolife.pos.common.dto.PersonalNoticeDTO)
	 */
	@Override
	public void fillBOMForUnwrtRuleCheck(SUWBOMPolicyDto bom, String posNo, PersonalNoticeDTO appPersonalNotice, PersonalNoticeDTO insPersonalNotice) {
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		Map<String, String> item = new HashMap<String, String>();
		//新投保人号码
		item.put("posObject", "1");
		item.put("itemNo", "029");
		posObjectAndItemNoList.add(item);
		//新投保人与被保人关系
		item = new HashMap<String, String>();
		item.put("posObject", "2");
		item.put("itemNo", "003");
		posObjectAndItemNoList.add(item);
		//客户年收入
		item = new HashMap<String, String>();
		item.put("posObject", "3");
		item.put("itemNo", "085");
		posObjectAndItemNoList.add(item);
		//客户年收入
		item = new HashMap<String, String>();
		item.put("posObject", "3");
		item.put("itemNo", "056");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(posNo, posObjectAndItemNoList);
		if(detailList != null && !detailList.isEmpty()) {
			Date applyDate = commonQueryDAO.getApplyDateByPosNo(posNo);
			for(PosAcceptDetailDTO detail : detailList) {
				if("1".equals(detail.getPosObject()) && "029".equals(detail.getItemNo())) {
					SUWBOMOwnerDto owner = SUWBOMOwnerDto.create();
					String applicantNo = detail.getNewValue();
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
					//职业等级
					owner.setOccuGrade(posRulesDAO.getOccupationGradeByCode(applicantInfo.getOccupationCode()));
					//是否黑名单客户
					owner.setBlackList(posRulesDAO.isBlackList(applicantNo, applyDate));
					//（二核）保全变更前生日
					owner.setPosBirthday(applicantInfo.getBirthday());
					//（二核）保全变更前性别
					owner.setPosGender(applicantInfo.getSexCode());
					//健康告知相关
					if(appPersonalNotice != null && appPersonalNotice.getClientInfo().getClientNo().equals(applicantNo)) {
						Set<String> options = appPersonalNotice.getItemOption();
						if(options.contains("2")) {
							//身高、体重
							if(appPersonalNotice.getHeight() != null) {
								owner.setHeight(appPersonalNotice.getHeight().doubleValue());
							}
							if(appPersonalNotice.getWeight() != null) {
								owner.setWeight(appPersonalNotice.getWeight().doubleValue());
							}
						}
						if(options.contains("4")) {
							//有无出国计划
							owner.setHasGoabroadPlan("Y".equals(appPersonalNotice.getItemAnswer_4()));
						}
						//补充告知中是否有选是的
						owner.setIsPosDeclarationYes(appPersonalNotice.isAnyDeclarationYes());
					}
					owner.setIsCurrentPosObj(true);
					bom.setOwner(owner);
				}
			}
			for(PosAcceptDetailDTO detail : detailList) {
				if("2".equals(detail.getPosObject()) && "003".equals(detail.getItemNo())) {
					//与主被保险人关系
					bom.getOwner().setRelationWithInsured(detail.getNewValue());
				} else if("3".equals(detail.getPosObject()) && "056".equals(detail.getItemNo()) && StringUtils.isNotBlank(detail.getNewValue())) {
					//投保人年收入
					try {
						bom.getOwner().setAnnualIncome(new Double(detail.getNewValue()));
					} catch(Exception e) {
						logger.error(e);
					}
				} else if("3".equals(detail.getPosObject()) && "085".equals(detail.getItemNo()) && StringUtils.isNotBlank(detail.getNewValue())) {
					//投保人年收入
					try {
						bom.getOwner().setAnnualIncome(new Double(detail.getNewValue()));
					} catch(Exception e) {
						logger.error(e);
					}
				}
			}
		}
	}

	/**
	 * 从受理明细中查询新投保人号码
	 * @param posNo
	 * @return
	 */
	public String getNewApplicantNoByPosNo(String posNo) {
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		Map<String, String> item = new HashMap<String, String>();
		//新投保人号码
		item.put("posObject", "1");
		item.put("itemNo", "029");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(posNo, posObjectAndItemNoList);
		if(detailList != null && !detailList.isEmpty()) {
			return detailList.get(0).getNewValue();
		}
		return null;
	}
}