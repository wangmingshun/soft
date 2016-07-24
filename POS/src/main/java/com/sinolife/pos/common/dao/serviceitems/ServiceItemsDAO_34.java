package com.sinolife.pos.common.dao.serviceitems;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.acceptance.branch.dto.QueryAndAddClientDTO;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.PolicyContactInfoDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_34;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;

/**
 * 34 保险金转换年金
 */
@Repository("serviceItemsDAO_34")
public class ServiceItemsDAO_34 extends ServiceItemsDAO {
	
	private static final Map<String, String> PROC_PROPERTY_CONFIG_MAP;	//保险金转换年金录入项
	private static final Map<String, String> CLIENT_PROC_MAP;			//需提交到数据库的经办记录
	private static final Map<String, String> CLIENT_RECORD_MAP;			//已提交到数据库，仅作记录的客户明细
	private static final Map<String, String> POLICY_CONFIG_MAP;			//保单联系信息
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		
		map.put("convertType",		"1|123|1|3|policyNo");//可转换保险金类型
		map.put("convertAmount",	"1|124|1|3|policyNo");//可转换保险金额
		map.put("applyAmount",		"1|125|1|3|policyNo");//申请转换金额
		map.put("withdrawType",		"1|126|1|3|policyNo");//领取方式
		map.put("withdrawTimeLimit","1|127|1|3|policyNo");//领取期限
		map.put("withdrawFrequency","1|128|1|3|policyNo");//领取频次
		map.put("newApplicantNo",	"1|129|1|3|policyNo");//新保单投保人
		map.put("newInsuredNo",		"1|130|1|3|policyNo");//新保单被保人
		map.put("relationship",		"1|131|1|3|policyNo");//新保单投被保人关系
		map.put("relationDesc",		"1|132|1|3|policyNo");//其它关系描述
		
		PROC_PROPERTY_CONFIG_MAP = map;
		
		Map<String, String> clientMap = new ConcurrentHashMap<String, String>();
		clientMap.put("clientName", 	"3|047|1|3|clientNo");	//客户姓名
		clientMap.put("birthday", 		"3|048|1|3|clientNo");	//客户生日
		clientMap.put("sexCode", 		"3|049|1|3|clientNo");	//客户性别代码
		clientMap.put("idTypeCode", 	"3|050|1|3|clientNo");	//客户证件类型代码
		clientMap.put("idNo", 			"3|051|1|3|clientNo");	//客户证件号
		clientMap.put("occupationCode",	"3|052|1|3|clientNo");	//客户职业代码
		clientMap.put("countryCode", 	"3|053|1|3|clientNo");	//客户国籍
		clientMap.put("nationCode", 	"3|054|1|3|clientNo");	//客户民族
		clientMap.put("registerPlace",	"3|055|1|3|clientNo");	//客户户籍
		clientMap.put("yearIncome", 	"3|056|1|3|clientNo");	//客户年收入
		clientMap.put("marriageCode",	"3|057|1|3|clientNo");	//客户婚姻情况代码
		clientMap.put("workUnit", 		"3|058|1|3|clientNo");	//客户工作单位
		clientMap.put("position", 		"3|059|1|3|clientNo");	//客户职务
		
		clientMap.put("phoneType", 		"3|016|1|3|clientNo");	//电话类型
		clientMap.put("areaNo", 	    "3|017|1|3|clientNo");	//电话区号
		clientMap.put("phoneNo", 		"3|018|1|3|clientNo");	//电话号码

		clientMap.put("provinceCode", 	"3|025|1|3|clientNo");	//省代码
		clientMap.put("cityCode", 		"3|026|1|3|clientNo");	//市代码
		clientMap.put("areaCode", 		"3|027|1|3|clientNo");	//区县代码
		clientMap.put("detailAddress",	"3|028|1|3|clientNo");	//地址详细
		clientMap.put("postalcode", 	"3|029|1|3|clientNo");	//邮编
		
		clientMap.put("emailAddress", 	"3|034|1|3|clientNo");	//邮件地址

		CLIENT_PROC_MAP = clientMap;
		
		Map<String, String> mapPolicy = new ConcurrentHashMap<String, String>();
		mapPolicy.put("addressSeq", "1|006|1|3|policyNo");	//保单地址SEQ
		mapPolicy.put("emailSeq",	"1|007|1|3|policyNo");	//保单电邮SEQ
		mapPolicy.put("phoneSeq",	"1|008|1|3|policyNo");	//保单电话SEQ
		mapPolicy.put("smsService",	"1|009|1|3|policyNo");	//保单短信服务
		
		POLICY_CONFIG_MAP = mapPolicy;
		
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
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#queryServiceItemsInfoExtra(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_34 item = (ServiceItemsInputDTO_34)serviceItemsInputDTO;
		
		String policyNo = item.getPolicyNo();
		
		//查询可转换金额及类型
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy", policyNo);
		
		queryForObject("calcConvertAnnuityAmt", paraMap);
		String flag = (String) paraMap.get("p_flag");
		String message = (String) paraMap.get("p_message");
		if(!"0".equals(flag))
			throw new RuntimeException("查询可转换金额及类型出错：" + message);
		
		String convertType = (String) paraMap.get("p_convert_type");
		BigDecimal convertAmt = (BigDecimal) paraMap.get("p_convert_amt");
		String convertMode = (String) paraMap.get("p_convert_mode");
		item.setConvertType(convertType);
		item.setConvertAmount(convertAmt);
		item.setConvertMode(convertMode);
		
		//查主被保险人客户号
		String insuredNo = commonQueryDAO.getInsuredOfPrimaryPlanByPolicyNo(policyNo);
		
		//强制设置新保单的投被保人为当前保单的被保人，投被保人关系强制为本人
		item.setNewApplicantNo(insuredNo);
		item.setNewInsuredNo(insuredNo);
		item.setRelationship("01");
		
		return item;
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#generateAcceptDetailDTOList(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_34 item = (ServiceItemsInputDTO_34)serviceItemsInputDTO;
		
		AcceptDetailGenerator generator = new AcceptDetailGenerator(PROC_PROPERTY_CONFIG_MAP, item.getPosNo(), beginGroupNo);
		
		QueryAndAddClientDTO applicantQaa = item.getQaaForApplicant();
		QueryAndAddClientDTO insuredQaa = item.getQaaForInsured();
		
		//保险金转换年金信息明细
		generator.processSimpleDTO(item);
		
		
		//有过投保人客户信息的查询，修改，新增的情况下
		if(applicantQaa != null){
			generator.setConfigMap(CLIENT_PROC_MAP);
			
			ClientInformationDTO originClientInfo = applicantQaa.getOriginClientInfo();
			ClientInformationDTO clientInfo = applicantQaa.getClientInfo();
			PolicyContactInfoDTO contactInfo = applicantQaa.getContactInfo();
			
			//客户联系信息的明细
			generator.processSimpleListDTO(clientInfo.getClientPhoneList(),	originClientInfo.getClientPhoneList(),"phoneSeq", false);
			generator.processSimpleListDTO(clientInfo.getClientAddressList(),originClientInfo.getClientAddressList(), "addressSeq", false);
			generator.processSimpleListDTO(clientInfo.getClientEmailList(), originClientInfo.getClientEmailList(), "emailSeq", false);
			
			//客户信息
			generator.processSimpleDTO(clientInfo, originClientInfo);
			
			//已提交的客户信息，写入detail表仅做记录
			if(applicantQaa.isNewClientAdded() && applicantQaa.getClientNo().equals(item.getNewApplicantNo())){
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
			generator.setDefaultObjectValue("NEW_POLICY_NO");
			generator.processSimpleDTO(contactInfo);
			generator.setDefaultObjectValue(null);
			
			generator.setConfigMap(PROC_PROPERTY_CONFIG_MAP);
		}
		
		//有过被保人客户信息的查询，修改，新增的情况下
		if(insuredQaa != null){
			generator.setConfigMap(CLIENT_PROC_MAP);
			
			ClientInformationDTO originClientInfo = insuredQaa.getOriginClientInfo();
			ClientInformationDTO clientInfo = insuredQaa.getClientInfo();
			
			//客户联系信息的明细
			generator.processSimpleListDTO(clientInfo.getClientPhoneList(),	originClientInfo.getClientPhoneList(),"phoneSeq", false);
			generator.processSimpleListDTO(clientInfo.getClientAddressList(),originClientInfo.getClientAddressList(), "addressSeq", false);
			generator.processSimpleListDTO(clientInfo.getClientEmailList(), originClientInfo.getClientEmailList(), "emailSeq", false);
			
			//客户信息
			generator.processSimpleDTO(clientInfo, originClientInfo);
			
			//已提交的客户信息，写入detail表仅做记录
			if(insuredQaa.isNewClientAdded() && insuredQaa.getClientNo().equals(item.getNewInsuredNo())){
				generator.setConfigMap(CLIENT_RECORD_MAP);
				generator.setDefaultObjectValue(clientInfo.getClientNo());
				
				generator.processSimpleDTO(originClientInfo);
				generator.processSimpleListDTO(originClientInfo.getClientPhoneList());
				generator.processSimpleListDTO(originClientInfo.getClientAddressList());
				generator.processSimpleListDTO(originClientInfo.getClientEmailList());
				
				generator.setDefaultObjectValue(null);
			}
			generator.setConfigMap(PROC_PROPERTY_CONFIG_MAP);
		}
		return generator.getResult();
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#validate(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(ServiceItemsInputDTO serviceItemsInputDTO, Errors err) {
		super.validate(serviceItemsInputDTO, err);
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#fillBomPostCreated(com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto)
	 */
	@Override
	public void fillBomPostCreated(POSBOMPolicyDto bom) {
		super.fillBomPostCreated(bom);
		
		Map<String, Object> retMap = posRulesDAO.isConvertable(bom.getPolicyNo());
		boolean isConvertable = "Y".equals(retMap.get("p_is_convertableplan"));
		String unconvertableReasion = (String) retMap.get("unconvertableReasion");
		if(isConvertable) {
			//不能操作转换原因
			bom.setUnconvertableReasion(unconvertableReasion);
		}
		//是否可以操作保险金转换年金项目
		bom.setConvertable(isConvertable);
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#fillBOMForProcessRuleCheck(com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto)
	 */
	@Override
	public void fillBOMForProcessRuleCheck(POSBOMPolicyDto bom) {
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		//申请转换金额
		Map<String, String> item = new HashMap<String, String>();
		item.put("posObject", "1");
		item.put("itemNo", "125");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(bom.getPosNo(), posObjectAndItemNoList);
		if(detailList != null && !detailList.isEmpty()) {
			for(PosAcceptDetailDTO detail : detailList) {
				if("1".equals(detail.getPosObject()) && "125".equals(detail.getItemNo())) {
					//转换金额
					bom.setConvertedMoney(Double.parseDouble(detail.getNewValue()));
				}
			}
		}
		super.fillBOMForProcessRuleCheck(bom);
	}
}
