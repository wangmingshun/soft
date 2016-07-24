package com.sinolife.pos.common.dto.serviceitems;

import java.math.BigDecimal;

import org.springframework.validation.Errors;

import com.sinolife.pos.acceptance.branch.dto.QueryAndAddClientDTO;



/**
 * 34 保险金转换年金
 */

public class ServiceItemsInputDTO_34 extends ServiceItemsInputDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7794809876649021826L;
	private String relationship;				//新投保人与被保人的关系
	private String relationDesc;				//投保人与被保人关系为其他时的描述
	private String newApplicantNo;				//保单新投保人号码
	private String newInsuredNo;				//保单新被保人号码
	private String convertType;					//可转换保险金类型
	private BigDecimal convertAmount;			//可转换金额
	private String convertMode;					//可转换模式'1'全部转 '2'部分转
	private BigDecimal applyAmount;				//申请转换金额
	private String withdrawType;				//领取方式
	private String withdrawFrequency;			//领取频次
	private String withdrawTimeLimit;			//领取期限
	private QueryAndAddClientDTO qaaForApplicant;//新增投保人信息
	private QueryAndAddClientDTO qaaForInsured;	//新增被保人信息
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#onPropertyChange()
	 */
	@Override
	protected void onPropertyChange() {
		//转换模式为全部转，则置转换金额为可转换金额
		if("1".equals(convertMode)) {
			applyAmount = convertAmount;
		}
		
		//不为 2定期生存年金 且不为 3保证年限的终身年金 则不允许录入领取期限
		if(!"2".equals(withdrawType) && !"3".equals(withdrawType)) {
			withdrawTimeLimit = null;
		}
	}
	
	/* 属性存取 */
	
	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public String getRelationDesc() {
		return relationDesc;
	}

	public void setRelationDesc(String relationDesc) {
		this.relationDesc = relationDesc;
	}

	public String getNewApplicantNo() {
		return newApplicantNo;
	}

	public void setNewApplicantNo(String newApplicantNo) {
		this.newApplicantNo = newApplicantNo;
	}

	public String getNewInsuredNo() {
		return newInsuredNo;
	}

	public void setNewInsuredNo(String newInsuredNo) {
		this.newInsuredNo = newInsuredNo;
	}

	public String getWithdrawType() {
		return withdrawType;
	}

	public void setWithdrawType(String withdrawType) {
		this.withdrawType = withdrawType;
	}

	public String getWithdrawFrequency() {
		return withdrawFrequency;
	}

	public void setWithdrawFrequency(String withdrawFrequency) {
		this.withdrawFrequency = withdrawFrequency;
	}

	public QueryAndAddClientDTO getQaaForApplicant() {
		return qaaForApplicant;
	}

	public void setQaaForApplicant(QueryAndAddClientDTO qaaForApplicant) {
		this.qaaForApplicant = qaaForApplicant;
	}

	public QueryAndAddClientDTO getQaaForInsured() {
		return qaaForInsured;
	}

	public void setQaaForInsured(QueryAndAddClientDTO qaaForInsured) {
		this.qaaForInsured = qaaForInsured;
	}

	public String getWithdrawTimeLimit() {
		return withdrawTimeLimit;
	}

	public void setWithdrawTimeLimit(String withdrawTimeLimit) {
		this.withdrawTimeLimit = withdrawTimeLimit;
	}

	public String getConvertType() {
		return convertType;
	}

	public void setConvertType(String convertType) {
		this.convertType = convertType;
	}

	public BigDecimal getConvertAmount() {
		return convertAmount;
	}

	public void setConvertAmount(BigDecimal convertAmount) {
		this.convertAmount = convertAmount;
	}

	public BigDecimal getApplyAmount() {
		return applyAmount;
	}

	public void setApplyAmount(BigDecimal applyAmount) {
		this.applyAmount = applyAmount;
	}

	public String getConvertMode() {
		return convertMode;
	}

	public void setConvertMode(String convertMode) {
		this.convertMode = convertMode;
	}
	
}
