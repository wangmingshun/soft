package com.sinolife.pos.common.dao.serviceitems;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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

import com.sinolife.pos.common.consts.CodeTableNames;
import com.sinolife.pos.common.dto.BeneficiaryInfoDTO;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.PersonalNoticeDTO;
import com.sinolife.pos.common.dto.PolicyBeneficiaryDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_22;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.common.util.PosValidateWrapper;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;
import com.sinolife.pos.common.util.detailgenerator.DefaultDeleteHanlder;
import com.sinolife.pos.common.util.detailgenerator.ItemProcessFilter;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMBeneficiaryDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMBeneficiaryDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPolicyDto;

/**
 * 受益人变更
 */
@Repository("serviceItemsDAO_22")
public class ServiceItemsDAO_22 extends ServiceItemsDAO {
	
	public static final Map<String, String> PROC_PROPERTY_CONFIG_MAP;
	public static final Map<String, String> PROC_PROPERTY_CONFIG_MAP_FOR_BENEF_TYPE;
	public static final Set<String> CLIENT_LEVEL_ITEM_SET;			//受益人客户层信息
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("relationship", 	"4|002|1|3|beneficiaryNo|policyNo");	//受益人与被保人的关系
		map.put("relationDesc", 	"4|003|1|3|beneficiaryNo|policyNo");	//受益人与被保人其他关系说明
		map.put("benefitRatePercent","4|004|1|3|beneficiaryNo|policyNo");	//受益比例
		map.put("benefitType", 		"4|005|1|3|beneficiaryNo|policyNo");	//受益性质
		map.put("benefitSeq", 		"4|006|1|3|beneficiaryNo|policyNo");	//受益顺序
		map.put("beneficiaryName", 	"4|007|1|3|beneficiaryNo|policyNo");	//受益人姓名
		map.put("sexCode", 			"4|008|1|3|beneficiaryNo|policyNo");	//受益人性别
		map.put("idType", 			"4|009|1|3|beneficiaryNo|policyNo");	//受益人证件类型
		map.put("idno", 			"4|011|1|3|beneficiaryNo|policyNo");	//受益人证件号码
		map.put("birthdate", 		"4|012|2|4|beneficiaryNo|policyNo");	//受益人出生日期
		/* edit by wangmingshun start*/
		map.put("claimPremDrawType","4|027|1|3|beneficiaryNo|policyNo");	//类信托类型
		map.put("promiseRate", 		"4|028|1|3|beneficiaryNo|policyNo");	//类信托约定比例
		map.put("promiseAge", 		"4|029|1|3|beneficiaryNo|policyNo");	//类信托约定年龄
		map.put("idValidDate", 		"4|030|2|4|beneficiaryNo|policyNo");	//证件有效期
		map.put("countryCode", 		"4|040|1|3|beneficiaryNo|policyNo");	//国籍
		map.put("accountOwner", 	"4|031|1|3|beneficiaryNo|policyNo");	//账户名
		map.put("bankCode", 		"4|032|1|3|beneficiaryNo|policyNo");	//银行
		map.put("accountNo", 		"4|033|1|3|beneficiaryNo|policyNo");	//账号
		map.put("transferGrantFlag","4|034|1|3|beneficiaryNo|policyNo");	// 授权状态
		/* edit by wangmingshun end*/
//		map.put("phone", 			"4|014|1|3|beneficiaryNo|policyNo");	//受益人联系电话
//		map.put("address", 			"4|015|1|3|beneficiaryNo|policyNo");	//受益人联系地址
		//删除元素为4|001|1|beneficiaryNo
		
		PROC_PROPERTY_CONFIG_MAP = map;
		
		map = new ConcurrentHashMap<String, String>();
		map.put("benefitType", 		"4|020|1|3|policyNo|policyNo");	//受益性质
		PROC_PROPERTY_CONFIG_MAP_FOR_BENEF_TYPE = map;
		
		Set<String> set = Collections.synchronizedSet(new HashSet<String>());
		set.add("007");
		set.add("008");
		set.add("009");
		set.add("011");
		set.add("012");
		CLIENT_LEVEL_ITEM_SET = set;
	}
	
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#queryServiceItemsInfoExtra(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	public ServiceItemsInputDTO queryServiceItemsInfoExtra(ServiceItemsInputDTO serviceItemsInputDTO) {
		if(!(serviceItemsInputDTO instanceof ServiceItemsInputDTO_22))
			throw new RuntimeException("错误的传入参数类型：" + serviceItemsInputDTO.getClass().getName() + ", 预期为:" + ServiceItemsInputDTO_22.class.getName());
		
		ServiceItemsInputDTO_22 item = (ServiceItemsInputDTO_22)serviceItemsInputDTO;
		
		//查询受益人信息
		Map<String, Object> criteriaMap = new HashMap<String, Object>();
		criteriaMap.put("policyNo", item.getPolicyNo());
		criteriaMap.put("benefitStatus", "Y");
		List<PolicyBeneficiaryDTO> beneficiaryInfoList = commonQueryDAO.queryBeneficiaryByCriteria(criteriaMap);
		
		//在第一个元素的位置加入一个空对象，以生成模板，供页面JS作为模板使用
		if(beneficiaryInfoList == null) {
			beneficiaryInfoList = new ArrayList<PolicyBeneficiaryDTO>();
		}
		if(beneficiaryInfoList.size() == 0) {
			PolicyBeneficiaryDTO tmplBenef = new PolicyBeneficiaryDTO();
			//默认设置为身故受益人
			tmplBenef.setBenefitType("2");
			beneficiaryInfoList.add(tmplBenef);
		}
		item.setBeneficiaryInfoList(beneficiaryInfoList);
		item.setBeneficiaryInfoListSize(beneficiaryInfoList.size());
		
		/* 查询保单类信托信息  edit by wangmingshun start */
		String isPolicyPwm = commonQueryDAO.getPolicyIsPwm(item.getPolicyNo());
		if("Y".equals(isPolicyPwm)) {
			item.setIsPwm(true);
			//为每个类信托身故受益人赋值
			List<Map<String, Object>> policyPwmInfo = commonQueryDAO.getPolicyPwmInfo(item.getPolicyNo());
			for(PolicyBeneficiaryDTO p : item.getBeneficiaryInfoList()) {
				//只处理身故受益人
				if("2".equals(p.getBenefitType())) {
					for(Map<String, Object> map : policyPwmInfo) {
						String beneficiaryNo = (String) map.get("beneficiaryNo");
						if(p.getBeneficiaryNo().equals(beneficiaryNo)) {
							String claimPremDrawType = (String) map.get("claimPremDrawType");
							BigDecimal promiseRate = (BigDecimal) map.get("promiseRate");
							BigDecimal promiseAge = (BigDecimal) map.get("promiseAge");
							String accountOwner = (String) map.get("accountOwner");
							String accountNo = (String) map.get("accountNo");
							String bankCode = (String) map.get("bankCode");
							String bankName = (String) map.get("bankName");
							String transferGrantFlag = (String) map.get("transferGrantFlag");
							p.setClaimPremDrawType(claimPremDrawType);
							p.setPromiseRate(promiseRate == null ? null 
									:promiseRate.multiply(new BigDecimal(100L)));
							p.setPromiseAge(promiseAge + "");
							p.setAccountOwner(accountOwner);
							p.setAccountNo(accountNo);
							p.setBankCode(bankCode);
							p.setBankName(bankName);
							p.setTransferGrantFlag(transferGrantFlag);
							break;
						}
					}
				}
			}
		}
		/* 查询保单类信托信息  edit by wangmingshun end */
		
		//查投保人信息
		String applicantNo = commonQueryDAO.getApplicantByPolicyNo(item.getPolicyNo());
		item.setClientInfoApp(clientInfoDAO.selClientinfoForClientno(applicantNo).get(0));
		
		//查被保人信息
		String insuredNo = commonQueryDAO.getInsuredOfPrimaryPlanByPolicyNo(item.getPolicyNo());
		item.setClientInfoIns(clientInfoDAO.selClientinfoForClientno(insuredNo).get(0));
		
		//默认设置为身故受益人
		item.setBenefitType("2");
		
		//受益人类型单选按钮
		Set<String> benefTypeSet = new HashSet<String>();
		for(PolicyBeneficiaryDTO pb : beneficiaryInfoList){
				benefTypeSet.add(pb.getBenefitType());
		}
		/*
		for(PolicyProductDTO pp : commonQueryDAO.queryPolicyProductListByPolicyNo(item.getPolicyNo())) {
			String productCode = pp.getProductCode();
			String survBenefSpecialConvention = commonQueryDAO.pdSelPrdParameterValuePro(productCode, "99");
			if("Y".equals(pp.getIsPrimaryPlan())) {
				item.setSurvBenefSpecialConvention(survBenefSpecialConvention);
			}
			if("1".equals(survBenefSpecialConvention)) {
				benefTypeSet.add("5");
			} else if("2".equals(survBenefSpecialConvention)) {
				benefTypeSet.add("4");
				benefTypeSet.add("5");
			}
			//保证给付责任
			if(commonQueryDAO.hasPromisePayDuty(productCode)) {
				benefTypeSet.add("3");
			}
		}
		*/
		//查询险种代码 edit by gaojiaming
		String productCode =commonQueryDAO.getProductCodeByPolicyNoAndProdSeq(item.getPolicyNo(), 1);
		item.setProductCode(productCode);	
		List<CodeTableItemDTO> beneficiaryTypeList = new ArrayList<CodeTableItemDTO>();
		if(benefTypeSet.contains("1")) {
			beneficiaryTypeList.add(new CodeTableItemDTO("1", "生存金受益人"));
		}
		if(benefTypeSet.contains("4")) {
			beneficiaryTypeList.add(new CodeTableItemDTO("4", "满期金受益人"));
		}
		if(benefTypeSet.contains("5")) {
			beneficiaryTypeList.add(new CodeTableItemDTO("5", "年金受益人"));
		}

			beneficiaryTypeList.add(new CodeTableItemDTO("2", "身故受益人"));
		//如果险种为至祥养老，则添加“剩余养老金受益人”按钮 edit by gaojiaming
		if("CIAN_CP1".equals(productCode)) {
			beneficiaryTypeList.add(new CodeTableItemDTO("3", "剩余养老金受益人"));
		}
//        if(benefTypeSet.contains("3")) {
//            beneficiaryTypeList.add(new CodeTableItemDTO("3", "剩余养老金受益人"));
//        }		
		item.setBeneficiaryTypeList(beneficiaryTypeList);
		
		//提示消息
		item.setRemindMessage("如保单已经操作生存金转账授权或者生存金抵缴保费授权，生存金受益人或其核心资料变更后系统将取消授权，需要客户重新申请操作授权。");
		
		return item;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#generateAcceptDetailDTOList(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		final ServiceItemsInputDTO_22 item = (ServiceItemsInputDTO_22)serviceItemsInputDTO;
		final ServiceItemsInputDTO_22 snapshot = (ServiceItemsInputDTO_22)serviceItemsInputDTO.getOriginServiceItemsInputDTO();
		
		//当前受益人列表
		List<PolicyBeneficiaryDTO> newPolicyBeneficiaryDTOList = item.getBeneficiaryInfoList();
		//原始受益人列表
		List<PolicyBeneficiaryDTO> oldPolicyBeneficiaryDTOList = snapshot.getBeneficiaryInfoList();
		
		/*加入证件有效期判断 edit by wangminghsun start*/
		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();
		/*加入证件有效期判断 edit by wangminghsun end*/
		
		//将当前受益人列表转化成List<MAP>
		List<Map<String, Object>> nlist = new ArrayList<Map<String, Object>>();
		for(int i = 0; i < newPolicyBeneficiaryDTOList.size(); i++) {
			PolicyBeneficiaryDTO pb = newPolicyBeneficiaryDTOList.get(i);
			/*edit by wangmingshun start*/
			//判断是否类信托，类信托，则将授权状态改为 Y
			if(item.getIsPwm()) {
				pb.setTransferGrantFlag("Y");
			}
			/*edit by wangmingshun end*/
			Map<String, Object> pbInterMap = PosUtils.getMapFromBean(pb.getBenefInfo());
			Map<String, Object> pbMap = PosUtils.getMapFromBean(pb);
			pbInterMap.putAll(pbMap);
			//将beneficiaryNo和benefitType的组合作为受益人的唯一键，因为在不同类型受益人下可能存在同一个人
			pbInterMap.put("beneficiaryID", pbInterMap.get("beneficiaryNo") + "|" + pbInterMap.get("benefitType"));
			nlist.add(pbInterMap);
		}
		
		//将原始受益人列表转化成List<MAP>
		List<Map<String, Object>> olist = new ArrayList<Map<String, Object>>();
		for(int i = 0; i < oldPolicyBeneficiaryDTOList.size(); i++) {
			PolicyBeneficiaryDTO pb = oldPolicyBeneficiaryDTOList.get(i);
			Map<String, Object> pbInterMap = PosUtils.getMapFromBean(pb.getBenefInfo());
			Map<String, Object> pbMap = PosUtils.getMapFromBean(pb);
			pbInterMap.putAll(pbMap);
			//将beneficiaryNo和benefitType的组合作为受益人的唯一键，因为在不同类型受益人下可能存在同一个人
			pbInterMap.put("beneficiaryID", pbInterMap.get("beneficiaryNo") + "|" + pbInterMap.get("benefitType"));
			olist.add(pbInterMap);
		}
		
		AcceptDetailGenerator generator = new AcceptDetailGenerator(PROC_PROPERTY_CONFIG_MAP, item.getPosNo(), beginGroupNo);
		//设置删除处理器
		generator.setDeleteHandler(new DefaultDeleteHanlder("4", "001", "1", "beneficiaryNo", "Y", "N"));
		generator.processListMap(nlist, olist, "beneficiaryID", false, new ItemProcessFilter() {
			@Override
			public boolean needProcess(Map<String, Object> listItem, Map<String, Object> oldValueItem) {
				if(listItem == null) {
					//处理删除元素
					  return item.getBenefitType().equals(oldValueItem.get("benefitType")) && StringUtils.isNotBlank((String) oldValueItem.get("beneficiaryNo"));
				} else {
					//处理新增、修改元素
					return item.getBenefitType().equals(listItem.get("benefitType"));
				}
			}
		});
		
		//加入受益人类型，方便后台处理
		generator.setConfigMap(PROC_PROPERTY_CONFIG_MAP_FOR_BENEF_TYPE);
		generator.processSimpleDTO(item);
		
		acceptDetailList.addAll(generator.getResult());
		
		/* 是否类信托业务处理 edit by wangmingshun start*/
		if(item.getIsPwm()) {
			acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "4", "6", 
					item.getPolicyNo(), "026", "", "Y"));
			//遍历acceptDetailList，将PosAcceptDetailDTO补齐(主要补齐27,28,29)，方便批文处理。
			//使用ＤＴＯ处理器没有变更的项是不会插入的，所以要手动插入
			Set<String> set = new HashSet<String>();
			for(PosAcceptDetailDTO pad: acceptDetailList) {
				if("027".equals(pad.getItemNo()) || "028".equals(pad.getItemNo())
						|| "029".equals(pad.getItemNo()) || "031".equals(pad.getItemNo())
						|| "032".equals(pad.getItemNo()) || "033".equals(pad.getItemNo())
						|| "034".equals(pad.getItemNo())) {
					//满足条件的benefitNo保存到set集合中
					set.add(pad.getObjectValue());
				}
			}
			for(String benefitNo : set) {
				boolean one = false , two = false, three = false,
					four = false, five = false, six = false, seven = false;
				for(PosAcceptDetailDTO pad: acceptDetailList) {
					if(benefitNo.equals(pad.getObjectValue())) {
						if("027".equals(pad.getItemNo())) {
							one = true;
						}
						if("028".equals(pad.getItemNo())) {
							two = true;
						}
						if("029".equals(pad.getItemNo())) {
							three = true;
						}
						if("031".equals(pad.getItemNo())) {
							four = true;
						}
						if("032".equals(pad.getItemNo())) {
							five = true;
						}
						if("033".equals(pad.getItemNo())) {
							six = true;
						}
						if("034".equals(pad.getItemNo())) {
							seven = true;
						}
					}
				}
				//one，two,three其中一个为false，则需要添加一条对应的detail
				for(PolicyBeneficiaryDTO pbd : newPolicyBeneficiaryDTOList) {
					if("2".equals(pbd.getBenefitType()) && 
							benefitNo.equals(pbd.getBeneficiaryNo())) {
						if(!one) {
							acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "4", "6", 
									benefitNo, "027", "", pbd.getClaimPremDrawType()));
						}
						if(!two) {
							acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "4", "6", 
									benefitNo, "028", "", pbd.getPromiseRate() + ""));
						}
						if(!three) {
							acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "4", "6", 
									benefitNo, "029", "", pbd.getPromiseAge()));
						}
						if(!four) {
							acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "4", "6", 
									benefitNo, "031", "", pbd.getAccountOwner()));
						}
						if(!five) {
							acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "4", "6", 
									benefitNo, "032", "", pbd.getBankCode()));
						}
						if(!six) {
							acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "4", "6", 
									benefitNo, "033", "", pbd.getAccountNo()));
						}
						if(!seven) {
							acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "4", "6", 
									benefitNo, "034", "", pbd.getTransferGrantFlag()));
						}
						
					}
				}
			}
		} else if(snapshot.getIsPwm() && !item.getIsPwm()){
			acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "4", "6", 
					item.getPolicyNo(), "026", "", "N"));
		}
		/* 是否类信托业务处理 edit by wangmingshun end*/
		
		return acceptDetailList;
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#validate(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(ServiceItemsInputDTO serviceItemsInputDTO, Errors err) {
		super.validate(serviceItemsInputDTO, err);
		if(serviceItemsInputDTO instanceof ServiceItemsInputDTO_22) {
			ServiceItemsInputDTO_22 dto = (ServiceItemsInputDTO_22)serviceItemsInputDTO;
			PosValidateWrapper wrapper = new PosValidateWrapper(err);
			if(dto.getBeneficiaryInfoList() != null && !dto.getBeneficiaryInfoList().isEmpty()) {
				String applicantName = dto.getClientInfoApp().getClientName();
				String insuredName = dto.getClientInfoIns().getClientName();
				for(PolicyBeneficiaryDTO benef : dto.getBeneficiaryInfoList()) {
					String benefType = benef.getBenefitType();
					int survBenefCount = 0;
					if(StringUtils.isNotBlank(benefType) && benefType.equals(dto.getBenefitType())) {
						String benefName =  benef.getBenefInfo().getBeneficiaryName();
						if("1".equals(benefType)) {
//							if("1".equals(dto.getSurvBenefSpecialConvention()) || "2".equals(dto.getSurvBenefSpecialConvention())) {
//								//产品属性为1、2的生存金受益人只能变更为投保人
//								if(!applicantName.equals(benefName)) {
//									wrapper.addErrMsg("clientNo", "生存金受益人只能变更为投保人：" + applicantName);
//								}
//							} else {
//								 if(++survBenefCount > 1) {
//									wrapper.addErrMsg("clientNo", "只能存在一个生存金受益人");
//								 }
//								//其他险种生存金受益人只能变更为被保险人
//								if(!insuredName.equals(benefName)) {
//									wrapper.addErrMsg("clientNo", "生存金受益人只能变更为被保人：" + insuredName);
//								}
//							}
						} else if("2".equals(benefType)) {
							if(insuredName.equals(benefName)) {
								wrapper.addErrMsg("clientNo", "身故受益人不能变更为被保险人本人：" + insuredName);
							}
						} else if("4".equals(benefType)) {
							if("2".equals(dto.getSurvBenefSpecialConvention())) {
								//产品属性为2的满期金受益人只能变更为投保人
								if(!applicantName.equals(benefName)) {
									wrapper.addErrMsg("clientNo", "满期金受益人只能变更为投保人：" + applicantName);
								}
							}
						} else if("5".equals(benefType)) {
							if("1".equals(dto.getSurvBenefSpecialConvention()) || "2".equals(dto.getSurvBenefSpecialConvention())) {
								//产品属性为2的年金受益人只能变更为被保人
								if(!insuredName.equals(benefName)) {
									wrapper.addErrMsg("clientNo", "年金受益人只能变更为被保人：" + insuredName);
								}
							}
						}
						if(PosUtils.findInCollectionByProperty(dto.getBeneficiaryTypeList(), "code", benefType) == null) {
							wrapper.addErrMsg("clientNo", "该保单不允许录入" + PosUtils.getDescByCodeFromCodeTable(CodeTableNames.BENEFIT_TYPE, benefType));
						}
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#fillBomPostCreated(com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto)
	 */
	@Override
	public void fillBomPostCreated(POSBOMPolicyDto bom) {
		super.fillBomPostCreated(bom);
		
		//已经授权生存金抵交保费
		bom.setPaymentFeeAuthorized(posRulesDAO.isPaymentFeeAuthorized(bom.getPolicyNo()));
		
		//存在生存金转账失败记录
		bom.setPaymentTransferFailure(posRulesDAO.isPaymentTransferFailure(bom.getPolicyNo()));
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#fillBOMForProcessRuleCheck(com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto)
	 */
	@Override
	public void fillBOMForProcessRuleCheck(POSBOMPolicyDto bom) {
		//发生核心资料变更
		boolean isBeneficiaryImportantAlt = "Y".equals(queryForObject("isBeneficiaryImportantAlt", bom.getPosNo()));
		bom.setBeneficiaryImportantAlt(isBeneficiaryImportantAlt);
		
		//存在生存金受益人变更
		boolean isHasLifeBeneficiaryAltered = false;
		
		//生存金受益人约定为被保险人本人 FIXME: 等待老侯的接口
		boolean isBeneficiaryInsuredSameLimit = "Y".equals(queryForObject("isBeneficiaryInsuredSameLimit", bom.getPosNo()));
		bom.setBeneficiaryInsuredSameLimit(isBeneficiaryInsuredSameLimit);
		
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		//删除受益人
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "001");
		posObjectAndItemNoList.add(item);
		//受益人与被保人的关系
		item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "002");
		posObjectAndItemNoList.add(item);
		//受益比例
		item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "004");
		posObjectAndItemNoList.add(item);
		//受益性质
		item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "005");
		posObjectAndItemNoList.add(item);
		//受益顺序
		item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "006");
		posObjectAndItemNoList.add(item);
		//受益人姓名
		item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "007");
		posObjectAndItemNoList.add(item);
		//受益人性别
		item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "008");
		posObjectAndItemNoList.add(item);
		//受益人证件类型
		item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "009");
		posObjectAndItemNoList.add(item);
		//受益人证件号码
		item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "011");
		posObjectAndItemNoList.add(item);
		//受益人出生日期
		item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "012");
		posObjectAndItemNoList.add(item);
		//受益人类型
		item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "020");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(bom.getPosNo(), posObjectAndItemNoList);
		if(detailList != null && !detailList.isEmpty()) {
			POSBOMBeneficiaryDto[] benefitantArr = bom.getBenefitantList();
			//新增的受益人行号与受益人对象的映射Map Key为infoGroupNo
			Map<String, POSBOMBeneficiaryDto> newBenefInfoCacheMap = new HashMap<String, POSBOMBeneficiaryDto>();
			//当前受益人字典Map key为beneficiaryNo + "|" + benefitType
			Map<String, POSBOMBeneficiaryDto> existsBenefInfoMap = new HashMap<String, POSBOMBeneficiaryDto>();
			if(benefitantArr != null && benefitantArr.length > 0) {
				for(POSBOMBeneficiaryDto tmp : benefitantArr) {
					existsBenefInfoMap.put(tmp.getId() + "|" + tmp.getBenefitType(), tmp);
				}
			}
			//查出本次变更的受益人类型
			String benefitType = null;
			for(PosAcceptDetailDTO detail : detailList) {
				if("4".equals(detail.getPosObject()) && "020".equals(detail.getItemNo())) {
					benefitType = detail.getNewValue();
				}
			}
			for(PosAcceptDetailDTO detail : detailList) {
				String posObject = detail.getPosObject();
				String itemNo = detail.getItemNo();
				String processType = detail.getProcessType();
				String groupNo = detail.getInfoGroupNo();
				String newValue = detail.getNewValue();
				String objectValue = detail.getObjectValue();
				if("4".equals(posObject) && "001".equals(itemNo)) {
					//删除，直接从受益人中干掉
					existsBenefInfoMap.remove(objectValue + "|" + benefitType);
				} else if(!"4".equals(detail.getPosObject()) || !"020".equals(detail.getItemNo())){//排除掉受益人类型
					Set<POSBOMBeneficiaryDto> benefSetNeedToUpdate = new HashSet<POSBOMBeneficiaryDto>();
					if("1".equals(processType) || "2".equals(processType)) {
						//对于修改，区分客户层和非客户层，客户层的(beneficiary_info)变更所有同客户的受益人，否则，仅变更该受益人（policy_beneficiary）
						if(!existsBenefInfoMap.values().isEmpty()) {
							for(POSBOMBeneficiaryDto tmp : existsBenefInfoMap.values()) {
								if(objectValue.equals(tmp.getId()) && (CLIENT_LEVEL_ITEM_SET.contains(itemNo) || tmp.getBenefitType().equals(benefitType))) {
									benefSetNeedToUpdate.add(tmp);
									//存在生存金受益人变更
									if("1".equals(tmp.getBenefitType())) {
										isHasLifeBeneficiaryAltered = true;
									}
									//是否为当前变更的受益人
									if(objectValue.equals(tmp.getId()) && tmp.getBenefitType().equals(benefitType)) {
										tmp.setCurrentBeneficiary(true);
									}
								}
							}
						}
					} else if("3".equals(processType) || "4".equals(processType)) {
						//对于新增，从缓存Map中直接根据groupNo查找
						if(newBenefInfoCacheMap.containsKey(groupNo)) {
							benefSetNeedToUpdate.add(newBenefInfoCacheMap.get(groupNo));
						} else {
							POSBOMBeneficiaryDto benef = POSBOMBeneficiaryDto.create();
							benef.setBenefitStatus("Y");
							benef.setHasAuthoriyAccount(false);
							benef.setCurrentBeneficiary(true);
							benef.setBenefitType(benefitType);
							newBenefInfoCacheMap.put(groupNo, benef);
							benefSetNeedToUpdate.add(benef);
						}
					}
					if(benefSetNeedToUpdate.isEmpty()) {
						continue;
					}
					
					for(POSBOMBeneficiaryDto benef : benefSetNeedToUpdate) {
						if("4".equals(posObject) && "002".equals(itemNo)) {
							benef.setRelationship(newValue);
						} else if("4".equals(posObject) && "004".equals(itemNo)) {
							benef.setBenefitRate(newValue);
						} else if("4".equals(posObject) && "005".equals(itemNo)) {
							benef.setBenefitType(newValue);
						} else if("4".equals(posObject) && "006".equals(itemNo)) {
							benef.setBenefitSeq(newValue);
						} else if("4".equals(posObject) && "007".equals(itemNo)) {
							benef.setName(newValue);
						} else if("4".equals(posObject) && "008".equals(itemNo)) {
							benef.setSex(newValue);
						} else if("4".equals(posObject) && "009".equals(itemNo)) {
							benef.setIdType(newValue);
						} else if("4".equals(posObject) && "011".equals(itemNo)) {
							benef.setIdNo(newValue);
						} else if("4".equals(posObject) && "012".equals(itemNo)) {
							try {
								benef.setBirthDate(new SimpleDateFormat("yyyy-MM-dd").parse(newValue));
								benef.setAgeInMonth(PosUtils.calcAgeMonthsFromBirthday(benef.getBirthDate(), bom.getApplyDate()));
								benef.setAgeInYear(PosUtils.calcAgeYearsFromBirthday(benef.getBirthDate(), bom.getApplyDate()));
							} catch (ParseException e) {
								e.printStackTrace();
							}
						} else {
							continue;
						}
					}
				}
			}
			
			//将新增的与原来的受益人集合合并
			List<POSBOMBeneficiaryDto> benefList = new ArrayList<POSBOMBeneficiaryDto>();
			benefList.addAll(existsBenefInfoMap.values());
			benefList.addAll(newBenefInfoCacheMap.values());
			
			//需要根据5项信息查询新的客户号，并更新进去
			for(POSBOMBeneficiaryDto benef : benefList) {
				if(benef.isCurrentBeneficiary()) {
					List<ClientInformationDTO> clientList = clientInfoDAO.selClientinfoPro(benef.getName(), benef.getSex(), benef.getBirthDate(), benef.getIdType(), benef.getIdNo());
					if(clientList != null && clientList.size() == 1) {
						benef.setBenefciaryClientNo(clientList.get(0).getClientNo());
					} else {
						benef.setBenefciaryClientNo(null);
					}
				}
			}
			
			bom.setBenefitantList(benefList.toArray(new POSBOMBeneficiaryDto[benefList.size()]));
		}
		
		bom.setHasLifeBeneficiaryAltered(isHasLifeBeneficiaryAltered);
		super.fillBOMForProcessRuleCheck(bom);
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#fillBOMForUnwrtRuleCheck(com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPolicyDto, java.lang.String, com.sinolife.pos.common.dto.PersonalNoticeDTO)
	 */
	@Override
	public void fillBOMForUnwrtRuleCheck(SUWBOMPolicyDto bom, String posNo,	PersonalNoticeDTO appPersonalNotice, PersonalNoticeDTO insPersonalNotice) {
		super.fillBOMForUnwrtRuleCheck(bom, posNo, appPersonalNotice, insPersonalNotice);
		//查询受益人信息
		Map<String, Object> criteriaMap = new HashMap<String, Object>();
		criteriaMap.put("policyNo", bom.getPolicyID());
		criteriaMap.put("benefitStatus", "Y");
		List<PolicyBeneficiaryDTO> beneficiaryInfoList = commonQueryDAO.queryBeneficiaryByCriteria(criteriaMap);
		if(beneficiaryInfoList == null) {
			beneficiaryInfoList = new ArrayList<PolicyBeneficiaryDTO>();
		}
		//查询受益人变更信息
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		//删除受益人
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "001");
		posObjectAndItemNoList.add(item);
		//受益人与被保人的关系
		item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "002");
		posObjectAndItemNoList.add(item);
		//受益比例
		item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "004");
		posObjectAndItemNoList.add(item);
		//受益性质
		item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "005");
		posObjectAndItemNoList.add(item);
		//受益顺序
		item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "006");
		posObjectAndItemNoList.add(item);
		//受益人姓名
		item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "007");
		posObjectAndItemNoList.add(item);
		//受益人性别
		item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "008");
		posObjectAndItemNoList.add(item);
		//受益人证件类型
		item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "009");
		posObjectAndItemNoList.add(item);
		//受益人证件号码
		item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "011");
		posObjectAndItemNoList.add(item);
		//受益人出生日期
		item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "012");
		posObjectAndItemNoList.add(item);
		//受益人类型
		item = new HashMap<String, String>();
		item.put("posObject", "4");
		item.put("itemNo", "020");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(posNo, posObjectAndItemNoList);
		if(detailList != null && !detailList.isEmpty()) {
			String benefitType = null;
			for(PosAcceptDetailDTO detail : detailList) {
				if("4".equals(detail.getPosObject()) && "020".equals(detail.getItemNo())) {
					benefitType = detail.getNewValue();
				}
			}
			//新增的受益人行号与受益人对象的映射Map Key为infoGroupNo
			Map<String, PolicyBeneficiaryDTO> newBenefInfoCacheMap = new HashMap<String, PolicyBeneficiaryDTO>();
			Map<String, PolicyBeneficiaryDTO> existsBenefInfoMap = new HashMap<String, PolicyBeneficiaryDTO>();
			for(PolicyBeneficiaryDTO tmp : beneficiaryInfoList) {
				existsBenefInfoMap.put(tmp.getBeneficiaryNo() + "|" + tmp.getBenefitType(), tmp);
			}
			for(PosAcceptDetailDTO detail : detailList) {
				String posObject = detail.getPosObject();
				String itemNo = detail.getItemNo();
				String processType = detail.getProcessType();
				String groupNo = detail.getInfoGroupNo();
				String newValue = detail.getNewValue();
				String objectValue = detail.getObjectValue();
				if("4".equals(posObject) && "001".equals(itemNo)) {
					//删除
					existsBenefInfoMap.remove(objectValue + "|" + benefitType);
				} else if(!"4".equals(detail.getPosObject()) || !"020".equals(detail.getItemNo())){
					Set<PolicyBeneficiaryDTO> benefSetNeedToUpdate = new HashSet<PolicyBeneficiaryDTO>();
					if("1".equals(processType) || "2".equals(processType)) {
						//对于修改，区分客户层和非客户层，客户层的(beneficiary_info)变更所有同客户的受益人，否则，仅变更该受益人（policy_beneficiary）
						if(!existsBenefInfoMap.values().isEmpty()) {
							for(PolicyBeneficiaryDTO benef : existsBenefInfoMap.values()) {
								if(objectValue.equals(benef.getBeneficiaryNo()) && (CLIENT_LEVEL_ITEM_SET.contains(itemNo) || benef.getBenefitType().equals(benefitType))) {
									benefSetNeedToUpdate.add(benef);
								}
								
							}
						}
					} else if("3".equals(processType) || "4".equals(processType)) {
						if(newBenefInfoCacheMap.containsKey(groupNo)) {
							benefSetNeedToUpdate.add(newBenefInfoCacheMap.get(groupNo));
						} else {
							PolicyBeneficiaryDTO benef = new PolicyBeneficiaryDTO();
							benef.setBenefInfo(new BeneficiaryInfoDTO());
							benef.setBenefitType(benefitType);
							benef.setBenefitStatus("Y");
							newBenefInfoCacheMap.put(groupNo, benef);
							benefSetNeedToUpdate.add(benef);
						}
					}
					if(benefSetNeedToUpdate.isEmpty()) {
						continue;
					}
					
					for(PolicyBeneficiaryDTO benef : benefSetNeedToUpdate) {
						if("4".equals(posObject) && "002".equals(itemNo)) {
							benef.setRelationship(newValue);
						} else if("4".equals(posObject) && "004".equals(itemNo)) {
							benef.setBenefitRatePercent(new BigDecimal(newValue));
						} else if("4".equals(posObject) && "005".equals(itemNo)) {
							benef.setBenefitType(newValue);
						} else if("4".equals(posObject) && "006".equals(itemNo)) {
							benef.setBenefitSeq(new Integer(newValue));
						} else if("4".equals(posObject) && "007".equals(itemNo)) {
							benef.getBenefInfo().setBeneficiaryName(newValue);
						} else if("4".equals(posObject) && "008".equals(itemNo)) {
							benef.getBenefInfo().setSexCode(newValue);
						} else if("4".equals(posObject) && "009".equals(itemNo)) {
							benef.getBenefInfo().setIdType(newValue);
						} else if("4".equals(posObject) && "011".equals(itemNo)) {
							benef.getBenefInfo().setIdno(newValue);
						} else if("4".equals(posObject) && "012".equals(itemNo)) {
							try {
								benef.getBenefInfo().setBirthdate(new SimpleDateFormat("yyyy-MM-dd").parse(newValue));
							} catch (ParseException e) {
								logger.error(e);
							}
						} else {
							continue;
						}
					}
				}
			}
			//将新增的与原来的受益人集合合并
			List<PolicyBeneficiaryDTO> benefList = new ArrayList<PolicyBeneficiaryDTO>();
			benefList.addAll(existsBenefInfoMap.values());
			benefList.addAll(newBenefInfoCacheMap.values());
			
			SUWBOMBeneficiaryDto[] benefArr = new SUWBOMBeneficiaryDto[benefList.size()];
			for(int i = 0; i < benefList.size(); i++) {
				SUWBOMBeneficiaryDto benef = SUWBOMBeneficiaryDto.create();
				PolicyBeneficiaryDTO tmp = benefList.get(i);
				benef.setBeneficiaryName(tmp.getBenefInfo().getBeneficiaryName());
				benef.setRelationWithInsured(tmp.getRelationship());
				if(tmp.getBenefitRatePercent() != null) {
					benef.setBeneficialPercent(tmp.getBenefitRatePercent().doubleValue());
				}
				if(tmp.getBenefitSeq() != null) {
					benef.setBeneficialSeq(tmp.getBenefitSeq());
				}
				benef.setBeneficialType(tmp.getBenefitType());
				benef.setBirthday(tmp.getBenefInfo().getBirthdate());
				benef.setGender(tmp.getBenefInfo().getSexCode());
				benef.setIdentityType(tmp.getBenefInfo().getIdType());
				benef.setIdentityCode(tmp.getBenefInfo().getIdno());
				benefArr[i] = benef;
			}
			bom.setBeneficiaries(benefArr);
		}
	}	
}
