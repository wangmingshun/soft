package com.sinolife.pos.common.dto.serviceitems;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.BeneficiaryInfoDTO;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.PolicyBeneficiaryDTO;
import com.sinolife.pos.common.util.PosValidateWrapper;


/**
 * 22 受益人变更
 */
public class ServiceItemsInputDTO_22 extends ServiceItemsInputDTO {

	private static final long serialVersionUID = -7547199304259350759L;
	
	private List<PolicyBeneficiaryDTO> beneficiaryInfoList = new ArrayList<PolicyBeneficiaryDTO>();
	private int beneficiaryInfoListSize = 1;
	private String benefitType;
	private List<CodeTableItemDTO> beneficiaryTypeList;
	private ClientInformationDTO clientInfoApp;
	private ClientInformationDTO clientInfoIns;
	private String survBenefSpecialConvention;					//生存金受益人特殊约定
	private String productCode;									//险种代码
	//edit by wangmingshun
	private boolean isPwm;										//是否类信托业务
	
	public boolean getIsPwm() {
		return isPwm;
	}
	
	public void setIsPwm(boolean isPwm) {
		this.isPwm = isPwm;
	}
	
	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	
	public List<PolicyBeneficiaryDTO> getBeneficiaryInfoList() {
		return beneficiaryInfoList;
	}

	public void setBeneficiaryInfoList(
			List<PolicyBeneficiaryDTO> beneficiaryInfoList) {
		this.beneficiaryInfoList = beneficiaryInfoList;
	}

	public int getBeneficiaryInfoListSize() {
		return beneficiaryInfoListSize;
	}

	public void setBeneficiaryInfoListSize(int beneficiaryInfoListSize) {
		this.beneficiaryInfoListSize = beneficiaryInfoListSize;
	}

	public String getBenefitType() {
		return benefitType;
	}

	public void setBenefitType(String benefitType) {
		this.benefitType = benefitType;
	}

	public void setBeneficiaryTypeList(List<CodeTableItemDTO> beneficiaryTypeList) {
		this.beneficiaryTypeList = beneficiaryTypeList;
	}
	
	public ClientInformationDTO getClientInfoApp() {
		return clientInfoApp;
	}

	public void setClientInfoApp(ClientInformationDTO clientInfoApp) {
		this.clientInfoApp = clientInfoApp;
	}

	public ClientInformationDTO getClientInfoIns() {
		return clientInfoIns;
	}

	public void setClientInfoIns(ClientInformationDTO clientInfoIns) {
		this.clientInfoIns = clientInfoIns;
	}

	public List<CodeTableItemDTO> getBeneficiaryTypeList() {
		return beneficiaryTypeList;
	}

	public String getSurvBenefSpecialConvention() {
		return survBenefSpecialConvention;
	}

	public void setSurvBenefSpecialConvention(String survBenefSpecialConvention) {
		this.survBenefSpecialConvention = survBenefSpecialConvention;
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
		PosValidateWrapper wrapper = new PosValidateWrapper(err);
		for(int i = 0; i < beneficiaryInfoList.size(); i++) {
			String msgPrefix = "beneficiaryInfoList[" + i + "].";
			PolicyBeneficiaryDTO benefInfo = beneficiaryInfoList.get(i);
			//只有与选中的受益人类型相同的受益人记录才会被写入数据库，因此，也只有这部分数据需要校验
			//B00000000001为法定受益人，不需要校验
			if(benefitType != null && benefitType.equals(benefInfo.getBenefitType()) && !"B00000000001".equals(benefInfo.getBeneficiaryNo())) {
				wrapper.rejectIfEmpty(msgPrefix + "benefitType", benefInfo.getBenefitType(), "受益人性质不能为空");
				String relationship = benefInfo.getRelationship();
				wrapper.rejectIfEmpty(msgPrefix + "relationship", relationship, "与被保人关系不能为空");
				if("99".equals(relationship)) {
					wrapper.rejectIfEmpty(msgPrefix + "relationDesc", benefInfo.getRelationDesc(), "与被保人其他关系描述不能为空");
				}
				wrapper.rejectIfNull(msgPrefix + "benefitRate", benefInfo.getBenefitRate(), "受益比例不能为空");
				wrapper.rejectIfNull(msgPrefix + "benefitSeq", benefInfo.getBenefitSeq(), "受益顺序不能为空");
				
				BeneficiaryInfoDTO bf = benefInfo.getBenefInfo();
				if(bf != null) {
					msgPrefix += "benefInfo.";
					wrapper.rejectIfEmpty(msgPrefix + "beneficiaryName", bf.getBeneficiaryName(), "受益人姓名不能为空");
					wrapper.rejectIfEmpty(msgPrefix + "idType", bf.getIdType(), "受益人证件类型不能为空");
					wrapper.rejectIfEmpty(msgPrefix + "sexCode", bf.getSexCode(), "受益人性别不能为空");
					wrapper.rejectIfNull(msgPrefix + "birthdate", bf.getBirthdate(), "受益人生日不能为空");
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#onPropertyChange()
	 */
	protected void onPropertyChange() {
		//由于删除的元素无法通过绑定删除，在这里对除绑定元素以外的剩余元素进行移除
		while(beneficiaryInfoListSize < beneficiaryInfoList.size()) {
			beneficiaryInfoList.remove(beneficiaryInfoListSize);
		}
		for(int i = 0; i < beneficiaryInfoList.size(); i++) {
			PolicyBeneficiaryDTO benefInfo = beneficiaryInfoList.get(i);
			
			//选择其他关系时才允许输入关系说明字段
			if(!"99".equals(benefInfo.getRelationship())) {
				benefInfo.setRelationDesc(null);
			}
			
			//对于新增的元素，默认绑定时保单号为空，在生成detail时会报ObjectValue为空的错误，所以在这里加上保单号
			if(StringUtils.isBlank(benefInfo.getPolicyNo())) {
				benefInfo.setPolicyNo(this.getPolicyNo());
			}
		}
	}
	
}
