package com.sinolife.pos.common.dto.serviceitems;

import org.springframework.validation.Errors;


/**
 * 18 签名变更
 * @author wangkui
 *
 */
public class ServiceItemsInputDTO_18 extends ServiceItemsInputDTO{

	private static final long serialVersionUID = -524744411675678047L;

	private String applicantNo;//投保人
	private String insuredNo;//被保人
	
	/**
	 * 变更对象
	 */
	private String changeObj;
	
	/**
	 * 变更原因 
	 * 1：签名风格变化（客户）
	 * 2：补签名（涉及保单）
	 */
	private String changeCause;
	
	/**
	 * 变更原因数组 
	 * 1：签名风格变化（客户）
	 * 2：补签名（涉及保单）
	 * 3: 补抄录风险提示语
	 */
	private String[] changeCauses;
	
	/**
	 * 补签名单证
	 */
	private String reSignFile;
	
	/**
	 * 代签名原因
	 * 由于可能会是一个“其他”录入项，所以这个字段直接记录的是描述
	 */
	private String replaceSignCause;
	
	/**
	 * 代签名原因其他
	 */
	private String replaceSignCauseOther;
	
	public String getChangeObj() {
		return changeObj;
	}

	public void setChangeObj(String changeObj) {
		this.changeObj = changeObj;
	}

	public String getChangeCause() {
		return changeCause;
	}

	public void setChangeCause(String changeCause) {
		this.changeCause = changeCause;
	}
	
	public String[] getChangeCauses() {
		return changeCauses;
	}

	public void setChangeCauses(String[] changeCauses) {
		this.changeCauses = changeCauses;
	}

	public String getReSignFile() {
		return reSignFile;
	}

	public void setReSignFile(String reSignFile) {
		if(reSignFile.endsWith(",")){
			reSignFile = reSignFile.substring(0, reSignFile.length()-1);
		}
		this.reSignFile = reSignFile;
	}

	public String getReplaceSignCause() {
		return replaceSignCause;
	}

	public void setReplaceSignCause(String replaceSignCause) {
		this.replaceSignCause = replaceSignCause;
	}

	public String getReplaceSignCauseOther() {
		return replaceSignCauseOther;
	}

	public void setReplaceSignCauseOther(String replaceSignCauseOther) {
		this.replaceSignCauseOther = replaceSignCauseOther.trim();
		if(!"".equals(this.replaceSignCauseOther)){
			setReplaceSignCause("");
		}//如果填了其他的代签名原因，选择框的值则不要
	}
	
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

	public String getInsuredNo() {
		return insuredNo;
	}

	public void setInsuredNo(String insuredNo) {
		this.insuredNo = insuredNo;
	}
	
	/**
	 * 得到申请客户标志
	 * @return 1--投保人，2--被保人， 3--同一人
	 */
	public String getClientFlag(){
		String flag = null;
		
		if(getClientNo().equals(applicantNo) && getClientNo().equals(insuredNo)){
			flag = "3";
		}else if(getClientNo().equals(applicantNo)){
			flag = "1";
		}else if(getClientNo().equals(insuredNo)){
			flag = "2";
		}
		return flag;
	}
	
}
