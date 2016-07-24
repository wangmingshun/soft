package com.sinolife.pos.common.dto.serviceitems;

import org.springframework.validation.Errors;

import com.sinolife.pos.acceptance.branch.dto.QueryAndAddClientDTO;
import com.sinolife.pos.common.dto.PolicyContactInfoDTO;

/**
 * 20 投保人变更
 */
public class ServiceItemsInputDTO_20 extends ServiceItemsInputDTO{

	private static final long serialVersionUID = 1497200338365263059L;

	private QueryAndAddClientDTO queryAndAddClientDTO;
	
	/**
	 * 原投保人客户号
	 * 涉及页面显示冲突的问题，不能和新投保人号公用一个变量
	 */
	private String originApplicantNo;
	
	/**
	 * 新投保人客户号
	 */
	private String applicantNo;
	
	private String insuredNo;//被保人
	
	/**
	 * 新投保人与被保人关系
	 */
	private String relation;
	
	/**
	 * 其他关系描述
	 */
	private String relationDesc;
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}

	public String getApplicantNo() {
		return applicantNo;
	}

	public void setApplicantNo(String applicantNo) {
		this.applicantNo = applicantNo;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getRelationDesc() {
		return relationDesc;
	}

	public void setRelationDesc(String relationDesc) {
		this.relationDesc = relationDesc;
	}

	public QueryAndAddClientDTO getQueryAndAddClientDTO() {
		return queryAndAddClientDTO;
	}

	public void setQueryAndAddClientDTO(QueryAndAddClientDTO queryAndAddClientDTO) {
		this.queryAndAddClientDTO = queryAndAddClientDTO;
	}

	public String getOriginApplicantNo() {
		return originApplicantNo;
	}

	public void setOriginApplicantNo(String originApplicantNo) {
		this.originApplicantNo = originApplicantNo;
	}

	public String getInsuredNo() {
		return insuredNo;
	}

	public void setInsuredNo(String insuredNo) {
		this.insuredNo = insuredNo;
	}
	
	/**
	 * 保单联系信息是否变化了，若没有，页面提醒他要操作联系方式变更
	 * @return
	 */
	public String getContactChangedFlag(){
		if(queryAndAddClientDTO!=null){
			PolicyContactInfoDTO contactInfo = queryAndAddClientDTO.getContactInfo();
			PolicyContactInfoDTO origin = queryAndAddClientDTO.getOriginContactInfo();
			if(contactInfo.getPhoneSeq()!=null && !contactInfo.getPhoneSeq().equals(origin.getPhoneSeq())){
				return "Y";
			}
			if(contactInfo.getAddressSeq()!=null && !contactInfo.getAddressSeq().equals(origin.getAddressSeq())){
				return "Y";
			}
			if(contactInfo.getEmailSeq()!=null && !contactInfo.getEmailSeq().equals(origin.getEmailSeq())){
				return "Y";
			}
		}
		return "N";
	}
}
