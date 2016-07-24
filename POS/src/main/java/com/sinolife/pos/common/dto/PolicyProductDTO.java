package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 保单险种信息
 */
@Entity
public class PolicyProductDTO implements Serializable {
	private static final long serialVersionUID = 5454155825314635342L;
	
	@Id private String policyNo;		//保单号
	@Id	private Integer prodSeq;		//产品序号
	private String productCode;			//产品代码
	private String relationship;		//被保人与投保人关系 
	private String insuredNo;			//被保人客户号
	private String dutyStatus;			//责任状态
	private String lapseReason;			//失效原因
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date effectDate;			//生效日期
	private Long units;					//份数
	private String productLevel;		//险种档次
	private BigDecimal baseSumIns;		//和客户约定的基本保额
	private BigDecimal dividendSumIns;	//分红保额
	private Integer coveragePeriod;		//保险年期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date maturityDate;			//满期日期
	private String specialTerm;			//特别约定条款
	private String isPrimaryPlan;		//是否主险标志
	private String pkSerial;			//业务流水
	private String coverPeriodType;		//保障期类型
	private String relationDesc;		//投被保人关系为其他时的描述
	
	/* 非表字段属性 */
	private String productFullName;			//险种全称
	private String productAbbrName;			//险种简称
	private String relationshipDesc;		//投被保人关系描述
	private String insuredName;				//被保人姓名
	private String coverPeriodTypeDesc;		//保障期类型描述
	private BigDecimal periodPremSum;		//期缴保费
	private boolean selected;				//是否选中
	private PolicyProductPremDTO premInfo;	//险种缴费信息
	private BigDecimal loanMax;				//最大可贷金额
	private BigDecimal cashValue;			//现金价值
	private String dutyStatusDesc;			//险种状态
	
	private BigDecimal addPremSum;	         //加保总金额
	private BigDecimal addBonusPrem;	     //加保红包金额
	private BigDecimal addPayPrem;	         //加保自主缴费金额
	private BigDecimal userBounsPrem;	         //用户红包金额
	private BigDecimal newBaseSumIns;		//变更后保额(3加保使用)
	private BigDecimal newAnnStandardPrem;	//变更后年缴保费(3加保使用)
	private String calType;					//险种费率的计算方式
	private String unitLinkedFlag;			//是否投连险种标志
	private String universalFlag;			//是否万能险种标志
	private BigDecimal periodStandardPrem;	//险种缴费表的期缴标准保费
	private BigDecimal annStandardPrem;		//险种缴费表的年缴标准保费
	private String agreePremSum;			//协议 减保/退保 金额
	private String dividend;                //红利分配方式
	private String autoContinue;            //是否自动续保
	private BigDecimal premTerm;            //缴费年期
	private String premPeriodType;			//缴费期类型
	private String longFlag;                //是否长期险 Y长期 N短期
	
	private String canBeSelectedFlag;		//是否可选择标志
	private String message;					//不可选原因
	private BigDecimal loanInterestRate;	//贷款利率(8保单贷款使用)
	
	private String specialSurSum;           //特殊退费的金额
	private String branchPercent;           //分公司承担比例(%)
	private String backBenefit;             //“协议退保-业务品质|投诉业务”新增“应追回既得利益”
	private String channelType;             //业务渠道
	private String agentNo;					//业务员编码 
	
	private String premStatus;				//缴费状态（0缴清 1缴费 2缴费中止）
	/* 构造函数  */

	public String getPremStatus() {
		return premStatus;
	}

	public void setPremStatus(String premStatus) {
		this.premStatus = premStatus;
	}

	public String getAgentNo() {
		return agentNo;
	}

	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}

	public String getBackBenefit() {
		return backBenefit;
	}

	public BigDecimal getAddPremSum() {
		return addPremSum;
	}

	public void setAddPremSum(BigDecimal addPremSum) {
		this.addPremSum = addPremSum;
	}

	public BigDecimal getAddBonusPrem() {
		return addBonusPrem;
	}

	public void setAddBonusPrem(BigDecimal addBonusPrem) {
		this.addBonusPrem = addBonusPrem;
	}

	public BigDecimal getAddPayPrem() {
		return addPayPrem;
	}

	public void setAddPayPrem(BigDecimal addPayPrem) {
		this.addPayPrem = addPayPrem;
	}

	public BigDecimal getUserBounsPrem() {
		return userBounsPrem;
	}

	public void setUserBounsPrem(BigDecimal userBounsPrem) {
		this.userBounsPrem = userBounsPrem;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	public void setBackBenefit(String backBenefit) {
		this.backBenefit = backBenefit;
	}

	public String getBranchPercent() {
		return branchPercent;
	}

	public void setBranchPercent(String branchPercent) {
		this.branchPercent = branchPercent;
	}

	public PolicyProductDTO() {
	}

	public PolicyProductDTO(String policyNo, Integer prodSeq) {
		this.policyNo = policyNo;
		this.prodSeq = prodSeq;
	}

	/* 特殊存取 */
	
	public String getCoverPeriodLabel() {
		if("1".equals(coverPeriodType)) {
			return coveragePeriod + "年";
		} else if("2".equals(coverPeriodType)) {
			return "至" + coveragePeriod + "岁";
		} else if("3".equals(coverPeriodType)) {
			return coveragePeriod + "天";
		} else if("4".equals(coverPeriodType)) {
			return coveragePeriod + "周";
		} else if("5".equals(coverPeriodType)) {
			return coveragePeriod + "个月";
		}
		return String.valueOf(coveragePeriod);
	}
	
	/**
	 * 是否投连万能产品
	 * @return
	 */
	public String getFinancialFlag(){
		if("Y".equals(unitLinkedFlag)||"Y".equals(universalFlag)){
			return "Y";
		}else{
			return "N";
		}
	}
	
	/* 属性存取 */
	
	public String getPolicyNo() {
		return this.policyNo;
	}

	public void setPolicyNo(String value) {
		this.policyNo = value;
	}

	public Integer getProdSeq() {
		return this.prodSeq;
	}

	public void setProdSeq(Integer value) {
		this.prodSeq = value;
	}

	public String getProductCode() {
		return this.productCode;
	}

	public Integer getCoveragePeriod() {
		return coveragePeriod;
	}

	public void setCoveragePeriod(Integer coveragePeriod) {
		this.coveragePeriod = coveragePeriod;
	}

	public String getCoverPeriodType() {
		return coverPeriodType;
	}

	public void setCoverPeriodType(String coverPeriodType) {
		this.coverPeriodType = coverPeriodType;
	}

	public void setProductCode(String value) {
		this.productCode = value;
	}

	public String getRelationship() {
		return this.relationship;
	}

	public void setRelationship(String value) {
		this.relationship = value;
	}

	public String getInsuredNo() {
		return this.insuredNo;
	}

	public void setInsuredNo(String value) {
		this.insuredNo = value;
	}

	public String getDutyStatus() {
		return this.dutyStatus;
	}

	public void setDutyStatus(String value) {
		this.dutyStatus = value;
	}

	public String getLapseReason() {
		return this.lapseReason;
	}

	public void setLapseReason(String value) {
		this.lapseReason = value;
	}

	public Date getEffectDate() {
		return this.effectDate;
	}

	public void setEffectDate(Date value) {
		this.effectDate = value;
	}

	public java.lang.Long getUnits() {
		return this.units;
	}

	public void setUnits(java.lang.Long value) {
		this.units = value;
	}

	public String getProductLevel() {
		return this.productLevel;
	}

	public void setProductLevel(String value) {
		this.productLevel = value;
	}

	public Date getMaturityDate() {
		return this.maturityDate;
	}

	public void setMaturityDate(Date value) {
		this.maturityDate = value;
	}

	public String getSpecialTerm() {
		return this.specialTerm;
	}

	public void setSpecialTerm(String value) {
		this.specialTerm = value;
	}

	public String getIsPrimaryPlan() {
		return this.isPrimaryPlan;
	}

	public void setIsPrimaryPlan(String value) {
		this.isPrimaryPlan = value;
	}

	public String getPkSerial() {
		return this.pkSerial;
	}

	public void setPkSerial(String value) {
		this.pkSerial = value;
	}
	
	public String getProductFullName() {
		return productFullName;
	}

	public void setProductFullName(String productFullName) {
		this.productFullName = productFullName;
	}

	public String getProductAbbrName() {
		return productAbbrName;
	}

	public void setProductAbbrName(String productAbbrName) {
		this.productAbbrName = productAbbrName;
	}

	public String getRelationshipDesc() {
		return relationshipDesc;
	}

	public void setRelationshipDesc(String relationshipDesc) {
		this.relationshipDesc = relationshipDesc;
	}

	public String getInsuredName() {
		return insuredName;
	}

	public void setInsuredName(String insuredName) {
		this.insuredName = insuredName;
	}

	public String getCoverPeriodTypeDesc() {
		return coverPeriodTypeDesc;
	}
	
	public void setCoverPeriodTypeDesc(String coverPeriodTypeDesc) {
		this.coverPeriodTypeDesc = coverPeriodTypeDesc;
	}

	public BigDecimal getPeriodPremSum() {
		return periodPremSum;
	}

	public void setPeriodPremSum(BigDecimal periodPremSum) {
		this.periodPremSum = periodPremSum;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public PolicyProductPremDTO getPremInfo() {
		return premInfo;
	}

	public void setPremInfo(PolicyProductPremDTO premInfo) {
		this.premInfo = premInfo;
	}

	public BigDecimal getLoanMax() {
		return loanMax;
	}

	public void setLoanMax(BigDecimal loanMax) {
		this.loanMax = loanMax;
	}

	public BigDecimal getCashValue() {
		return cashValue;
	}

	public void setCashValue(BigDecimal cashValue) {
		this.cashValue = cashValue;
	}

	public String getDutyStatusDesc() {
		return dutyStatusDesc;
	}

	public void setDutyStatusDesc(String dutyStatusDesc) {
		this.dutyStatusDesc = dutyStatusDesc;
	}

	public String getAgreePremSum() {
		return agreePremSum;
	}

	public void setAgreePremSum(String agreePremSum) {
		this.agreePremSum = agreePremSum;
	}

	public BigDecimal getPeriodStandardPrem() {
		return periodStandardPrem;
	}

	public void setPeriodStandardPrem(BigDecimal periodStandardPrem) {
		this.periodStandardPrem = periodStandardPrem;
	}

	public BigDecimal getAnnStandardPrem() {
		return annStandardPrem;
	}

	public void setAnnStandardPrem(BigDecimal annStandardPrem) {
		this.annStandardPrem = annStandardPrem;
	}

	public String getCalType() {
		return calType;
	}

	public void setCalType(String calType) {
		this.calType = calType;
	}

	public BigDecimal getNewBaseSumIns() {
		return newBaseSumIns;
	}

	public void setNewBaseSumIns(BigDecimal newBaseSumIns) {
		this.newBaseSumIns = newBaseSumIns;
	}

	public String getUnitLinkedFlag() {
		return unitLinkedFlag;
	}

	public void setUnitLinkedFlag(String unitLinkedFlag) {
		this.unitLinkedFlag = unitLinkedFlag;
	}

	public String getUniversalFlag() {
		return universalFlag;
	}

	public void setUniversalFlag(String universalFlag) {
		this.universalFlag = universalFlag;
	}

	public String getRelationDesc() {
		return relationDesc;
	}

	public void setRelationDesc(String relationDesc) {
		this.relationDesc = relationDesc;
	}

	public String getAutoContinue() {
		return autoContinue;
	}

	public void setAutoContinue(String autoContinue) {
		this.autoContinue = autoContinue;
	}

	public String getDividend() {
		return dividend;
	}

	public void setDividend(String dividend) {
		this.dividend = dividend;
	}

	public BigDecimal getPremTerm() {
		return premTerm;
	}

	public void setPremTerm(BigDecimal premTerm) {
		this.premTerm = premTerm;
	}

	public BigDecimal getBaseSumIns() {
		return baseSumIns;
	}

	public void setBaseSumIns(BigDecimal baseSumIns) {
		this.baseSumIns = baseSumIns;
	}

	public BigDecimal getDividendSumIns() {
		return dividendSumIns;
	}

	public void setDividendSumIns(BigDecimal dividendSumIns) {
		this.dividendSumIns = dividendSumIns;
	}

	public String getLongFlag() {
		return longFlag;
	}

	public void setLongFlag(String longFlag) {
		this.longFlag = longFlag;
	}

	public String getPremPeriodType() {
		return premPeriodType;
	}

	public void setPremPeriodType(String premPeriodType) {
		this.premPeriodType = premPeriodType;
	}

	public BigDecimal getNewAnnStandardPrem() {
		return newAnnStandardPrem;
	}

	public void setNewAnnStandardPrem(BigDecimal newAnnStandardPrem) {
		this.newAnnStandardPrem = newAnnStandardPrem;
	}

	public String getCanBeSelectedFlag() {
		return canBeSelectedFlag;
	}

	public void setCanBeSelectedFlag(String canBeSelectedFlag) {
		this.canBeSelectedFlag = canBeSelectedFlag;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public BigDecimal getLoanInterestRate() {
		return loanInterestRate;
	}

	public void setLoanInterestRate(BigDecimal loanInterestRate) {
		this.loanInterestRate = loanInterestRate;
	}

	public String getSpecialSurSum() {
		return specialSurSum;
	}

	public void setSpecialSurSum(String specialSurSum) {
		this.specialSurSum = specialSurSum;
	}
	
}
