package com.sinolife.pos.common.dao.serviceitems;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.PersonalNoticeDTO;
import com.sinolife.pos.common.dto.PolicyDTO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_12;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.common.util.detailgenerator.AcceptDetailGenerator;
import com.sinolife.pos.common.util.detailgenerator.GroupNo;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMInsuredDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMOwnerDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMInsuredDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMOwnerDto;
import com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPolicyDto;

/**
 * 12 年龄性别错误更正
 */
@Repository("serviceItemsDAO_12")
public class ServiceItemsDAO_12 extends ServiceItemsDAO {
	
	private static final Map<String, String> PROC_PROPERTY_CONFIG_MAP;
	static {
		Map<String, String> map = new ConcurrentHashMap<String, String>();
		map.put("birthday",		"3|069|2|4|clientNo");//生日
		map.put("sexCode",		"3|070|1|3|clientNo");//性别
		map.put("idTypeCode",	"3|071|1|3|clientNo");//证件类型
		map.put("idNo",			"3|072|1|3|clientNo");//证件号码

		PROC_PROPERTY_CONFIG_MAP = map;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#queryServiceItemsInfoExtra(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO)
	 */
	@Override
	protected ServiceItemsInputDTO queryServiceItemsInfoExtra(ServiceItemsInputDTO serviceItemsInputDTO) {
		ServiceItemsInputDTO_12 item = (ServiceItemsInputDTO_12)serviceItemsInputDTO;
		String policyNo = item.getPolicyNo();
		String clientNo = item.getClientNo();
		
		//查询客户信息
		List<ClientInformationDTO> clientList = clientInfoDAO.selClientinfoForClientno(clientNo);
		if(clientList == null || clientList.size() != 1)
			throw new RuntimeException("找不到匹配的客户信息:" + clientNo);
		
		ClientInformationDTO clientInfo = clientList.get(0);
		item.setClientInfo(clientInfo);
		
		//由于界面显示默认为空，所以用于绑定的新的客户信息为空，通过ServiceItemsInputDTO_12.onPropertyChange()方法同步新值
		item.setClientInfoNew(new ClientInformationDTO());
		
		//查询客户作为投保人和被保人的保单信息
		List<PolicyDTO> policyList = new ArrayList<PolicyDTO>();
		
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("clientNo",  item.getClientNo());
		map.put("policyNo",item.getPolicyNo());
		List<String> policyNoList =  queryForList("queryPolicyNoListByClientNoAndChannelcd", map);
		if(policyNoList != null && !policyNoList.isEmpty()) {
			for(String policyNoTmp : policyNoList) {
				PolicyDTO policyInfo = commonQueryDAO.queryPolicyInfoByPolicyNo(policyNoTmp);
				if(policyInfo != null) {
					if(policyNoTmp.equals(policyNo)) {
						//申请保单放在第一个
						policyList.add(0, policyInfo);
					} else {
						policyList.add(policyInfo);
					}
				}
			}
		}
		for(PolicyDTO policy : policyList) {
			if(!item.getPolicyNo().equals(policy.getPolicyNo())) {
				policy.setVerifyResult(acceptRuleCheck(serviceItemsInputDTO, policy.getPolicyNo()));
			}
			boolean needProcess = isPolicyNeedProcess(policy.getPolicyNo(), clientInfo.getClientNo(), clientInfo.getSexCode(), clientInfo.getBirthday());
			policy.setCanBeSelectedFlag(needProcess ? "Y" : "N");
		}
		item.setPolicyList(policyList);
		
		//年龄性别错误更正可选择变更客户对象
		item.setClientSelectEnabled(true);
		
		Integer prodOfPrimaryPlan = commonQueryDAO.queryProdSeqOfPrimaryPlanByPolicyNo(policyNo);
		String productCodeOfPrimaryPlan = commonQueryDAO.getProductCodeByPolicyNoAndProdSeq(policyNo, prodOfPrimaryPlan);
		if("CNAN_BR1".equals(productCodeOfPrimaryPlan)) {
			//如变更的保单主险为红玫瑰时，增加受理提醒（在项目明细录入页面弹出提醒）“红玫瑰险种退费必须退入被保人账户，如该项目受理产生退费，请仔细核对退费账户持有人姓名必须与被保人一致”
			item.setRemindMessage("红玫瑰险种退费必须退入被保人账户，如该项目受理产生退费，请仔细核对退费账户持有人姓名必须与被保人一致");
		}
		
		return item;
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#generateAcceptDetailDTOList(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, int)
	 */
	@Override
	protected List<PosAcceptDetailDTO> generateAcceptDetailDTOList(ServiceItemsInputDTO serviceItemsInputDTO, int beginGroupNo) {
		ServiceItemsInputDTO_12 item = (ServiceItemsInputDTO_12)serviceItemsInputDTO;
		ServiceItemsInputDTO_12 snapshot = (ServiceItemsInputDTO_12) item.getOriginServiceItemsInputDTO();
		
		List<PolicyDTO> policyList = item.getPolicyList();

		List<PosAcceptDetailDTO> acceptDetailList = new ArrayList<PosAcceptDetailDTO>();
		AcceptDetailGenerator generator = new AcceptDetailGenerator(PROC_PROPERTY_CONFIG_MAP, item.getPosNo(), beginGroupNo);
		
		//公共录入域的detail list
		List<PosAcceptDetailDTO> pubDetail = getPubDetailList();
		for(int i = 0; i < policyList.size(); i++) {
			PolicyDTO policyInfo = policyList.get(i);
			if(policyInfo.isSelected() || policyInfo.getPolicyNo().equals(item.getPolicyNo())) {
				String policyNo = policyInfo.getPolicyNo();
				String posNo = insertPosInfoRecord(item, policyNo);
				
				//新增的PosNo也需要生成公共录入域的detail
				if(!posNo.equals(item.getPosNo())) {
					acceptDetailList.addAll(replaceDetailListPosNo(pubDetail, posNo));
					//证件有效期
					if(item.getClientInfoNew().getIdnoValidityDate()!=null){
				          acceptDetailList.add(new PosAcceptDetailDTO(posNo, "3", "1",
								snapshot.getClientNo(), "099", snapshot.getClientInfoNew().getIdnoValidityDate()==null?"":new SimpleDateFormat("yyyy-MM-dd").format(snapshot.getClientInfoNew().getIdnoValidityDate()),
								new SimpleDateFormat("yyyy-MM-dd").format(item.getClientInfoNew().getIdnoValidityDate())));
					}else if (item.getClientInfoNew().getIsLongidnoValidityDate()){
					 acceptDetailList.add(new PosAcceptDetailDTO(posNo, "3", "1",
								snapshot.getClientNo(), "099", snapshot.getClientInfoNew().getIdnoValidityDate()==null?"":new SimpleDateFormat("yyyy-MM-dd").format(snapshot.getClientInfoNew().getIdnoValidityDate()),
								"9999-01-01"));
				}
			}
				
				//为当前的PosNo生成Detail
				generator.setDefaultPosNo(posNo);
				generator.setGroupNo(new GroupNo(beginGroupNo));//每个PosNo的公共部分相同，因此都从指定的GroupNo开始递增
				generator.processSimpleDTO(item.getClientInfo(), snapshot.getClientInfo());
			}
		}
		acceptDetailList.addAll(generator.getResult());
		//证件有效期
			if(item.getClientInfoNew().getIdnoValidityDate()!=null){
		          acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "3", "1",
						snapshot.getClientNo(), "099", snapshot.getClientInfoNew().getIdnoValidityDate()==null?"":new SimpleDateFormat("yyyy-MM-dd").format(snapshot.getClientInfoNew().getIdnoValidityDate()),
						new SimpleDateFormat("yyyy-MM-dd").format(item.getClientInfoNew().getIdnoValidityDate())));
			}else if (item.getClientInfoNew().getIsLongidnoValidityDate()){
			 acceptDetailList.add(new PosAcceptDetailDTO(item.getPosNo(), "3", "1",
						snapshot.getClientNo(), "099", snapshot.getClientInfoNew().getIdnoValidityDate()==null?"":new SimpleDateFormat("yyyy-MM-dd").format(snapshot.getClientInfoNew().getIdnoValidityDate()),
						"9999-01-01"));
		}
		

		return acceptDetailList;
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#validate(com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(ServiceItemsInputDTO serviceItemsInputDTO, Errors err) {
		super.validate(serviceItemsInputDTO, err);
	}

	public boolean isPolicyNeedProcess(String policyNo, String clientNo, String sexCode, Date birthday) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("clientNo", clientNo);
		paraMap.put("sexCode", sexCode);
		paraMap.put("birthday", birthday);
		return "Y".equals(queryForObject("isPolicyNeedProcess", paraMap));
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#fillBOMForUnwrtRuleCheck(com.sinolife.sf.ruleengine.uw.bom.dto.SUWBOMPolicyDto, java.lang.String, com.sinolife.pos.common.dto.PersonalNoticeDTO, com.sinolife.pos.common.dto.PersonalNoticeDTO)
	 */
	@Override
	public void fillBOMForUnwrtRuleCheck(SUWBOMPolicyDto bom, String posNo,	PersonalNoticeDTO appPersonalNotice, PersonalNoticeDTO insPersonalNotice) {
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		Map<String, String> item = new HashMap<String, String>();
		//生日
		item.put("posObject", "3");
		item.put("itemNo", "069");
		posObjectAndItemNoList.add(item);
		//性别
		item = new HashMap<String, String>();
		item.put("posObject", "3");
		item.put("itemNo", "070");
		posObjectAndItemNoList.add(item);
		//证件类型
		item = new HashMap<String, String>();
		item.put("posObject", "3");
		item.put("itemNo", "071");
		posObjectAndItemNoList.add(item);
		//证件号码
		item = new HashMap<String, String>();
		item.put("posObject", "3");
		item.put("itemNo", "072");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(posNo, posObjectAndItemNoList);
		if(detailList != null && !detailList.isEmpty()) {
			PolicyDTO policyDTO = commonQueryDAO.queryPolicyInfoByPolicyNo(bom.getPolicyID());
			Date applyDate = commonQueryDAO.getApplyDateByPosNo(posNo);
			for(PosAcceptDetailDTO detail : detailList) {
				String clientNo = detail.getObjectValue();
				SUWBOMOwnerDto owner = null;
				SUWBOMInsuredDto insured = null;
				if(bom.getOwner().getOwnerID().equals(clientNo)) {
					owner = bom.getOwner();
					owner.setIsCurrentPosObj(true);
				}
				if(bom.getInsureds() != null) {
					for(SUWBOMInsuredDto tmp : bom.getInsureds()) {
						if(tmp.getInsuredID().equals(clientNo)) {
							insured = tmp;
							insured.setIsCurrentPosObj(true);
						}
					}
				}
				if("3".equals(detail.getPosObject()) && "069".equals(detail.getItemNo())) {
					Date birthday;
					Date oldBirthday;
					try {
						birthday = new SimpleDateFormat("yyyy-MM-dd").parse(detail.getNewValue());
						oldBirthday= new SimpleDateFormat("yyyy-MM-dd").parse(detail.getOldValue());
						
					} catch (ParseException e) {
						throw new RuntimeException(e.getMessage(), e);
					}
					if(owner != null) {
						//投保人生日
						owner.setBirthday(birthday);
						//投保人年龄
						owner.setIssueAge(PosUtils.calcAgeYearsFromBirthday(birthday, applyDate));
						
					}
					if(insured != null) {
						//被保人生日
						insured.setBirthday(birthday);
						//被保人投保年龄（天）
						insured.setIssueAgeDay(PosUtils.calcAgeDaysFromBirthday(birthday, policyDTO.getEffectDate()));
						//被保人投保年龄
						insured.setIssueAgeYear(PosUtils.calcAgeYearsFromBirthday(birthday, policyDTO.getEffectDate()));
						insured.setPosIssueAgeYear(PosUtils.calcAgeYearsFromBirthday(oldBirthday,policyDTO.getEffectDate()));
					
					}
				} else if("3".equals(detail.getPosObject()) && "070".equals(detail.getItemNo())) {
					String sexCode = detail.getNewValue();
					if(owner != null) {
						owner.setGender(sexCode);
					}
					if(insured != null) {
						insured.setGender(sexCode);
					}
				} else if("3".equals(detail.getPosObject()) && "071".equals(detail.getItemNo())) {
					String idType = detail.getNewValue();
					if(owner != null) {
						owner.setIdentityType(idType);
					}
					if(insured != null) {
						insured.setIdentityType(idType);
					}
				} else if("3".equals(detail.getPosObject()) && "072".equals(detail.getItemNo())) {
					String idNo = detail.getNewValue();
					if(owner != null) {
						owner.setIdentityCode(idNo);
					}
					if(insured != null) {
						insured.setIdentityCode(idNo);
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO#fillBOMForProcessRuleCheck(com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMPolicyDto)
	 */
	@Override
	public void fillBOMForProcessRuleCheck(POSBOMPolicyDto bom) {
		super.fillBOMForProcessRuleCheck(bom);
		List<Map<String, String>> posObjectAndItemNoList = new ArrayList<Map<String, String>>();
		Map<String, String> item = new HashMap<String, String>();
		//生日
		item.put("posObject", "3");
		item.put("itemNo", "069");
		posObjectAndItemNoList.add(item);
		//性别
		item = new HashMap<String, String>();
		item.put("posObject", "3");
		item.put("itemNo", "070");
		posObjectAndItemNoList.add(item);
		//证件类型
		item = new HashMap<String, String>();
		item.put("posObject", "3");
		item.put("itemNo", "071");
		posObjectAndItemNoList.add(item);
		//证件号码
		item = new HashMap<String, String>();
		item.put("posObject", "3");
		item.put("itemNo", "072");
		posObjectAndItemNoList.add(item);
		List<PosAcceptDetailDTO> detailList = queryPosAcceptDetail(bom.getPosNo(), posObjectAndItemNoList);
		if(detailList != null && !detailList.isEmpty()) {
			for(PosAcceptDetailDTO detail : detailList) {
				String clientNo = detail.getObjectValue();
				POSBOMOwnerDto owner = null;
				POSBOMInsuredDto insured = null;
				if(bom.getApplicant().getId().equals(clientNo)) {
					owner = bom.getApplicant();
					owner.setCurrentPosObj(true);
				}
				if(bom.getInsurantList() != null) {
					for(POSBOMInsuredDto tmp : bom.getInsurantList()) {
						if(tmp.getId().equals(clientNo)) {
							insured = tmp;
							insured.setCurrentPosObj(true);
						}
					}
				}
				if("3".equals(detail.getPosObject()) && "069".equals(detail.getItemNo())) {
					Date birthday;
					try {
						birthday = new SimpleDateFormat("yyyy-MM-dd").parse(detail.getNewValue());
					} catch (ParseException e) {
						throw new RuntimeException(e.getMessage(), e);
					}
					if(owner != null) {
						owner.setBirthDate(birthday);
					}
					if(insured != null) {
						insured.setBirthDate(birthday);
					}
				} else if("3".equals(detail.getPosObject()) && "070".equals(detail.getItemNo())) {
					String sexCode = detail.getNewValue();
					if(owner != null) {
						owner.setSex(sexCode);
					}
					if(insured != null) {
						insured.setSex(sexCode);
					}
				} else if("3".equals(detail.getPosObject()) && "071".equals(detail.getItemNo())) {
					String idType = detail.getNewValue();
					if(owner != null) {
						owner.setIdType(idType);
					}
					if(insured != null) {
						insured.setIdType(idType);
					}
				} else if("3".equals(detail.getPosObject()) && "072".equals(detail.getItemNo())) {
					String idNo = detail.getNewValue();
					if(owner != null) {
						owner.setIdNo(idNo);
					}
					if(insured != null) {
						insured.setIdNo(idNo);
					}
				}
			}
		}
	}

}
