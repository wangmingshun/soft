package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;

import org.springframework.format.annotation.DateTimeFormat;

import com.sinolife.sf.ruleengine.pos.bom.dto.POSVerifyResultDto;

/**
 * 表名和字段名前加双引号，是因为当这些标识是oralce中的关键字时，会报错，加上双引号是防止出错
 */
@Entity
public class PolicyDTO implements Serializable {
	private static final long serialVersionUID = 5454155825314635342L;

	private String policyNo;				//保单号
	private String applicantNo;				//投保人客户号
	private String branchCode;				//机构代码
	private String departmentNo;			//部门代码
	private String dutyStatus;				//理赔责任状态
	private String aplOption;				//自垫选择
	private String channelType;				//业务渠道
	private String agentNo;					//业务员编码 
	private String applyNo;					//投保单号
	private String oldPolicyNo;				//原保单号
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date applyDate;					//投保日期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date issueDate;					//承保日期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date effectDate;				//保单生效日期
	private String serviceStatus;			//保单服务状态
	private String pkSerial;				//业务流水
	private String updatedUser;				//修改人
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date updatedDate;				//修改日期
	private String createdUser;				//录入人
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdDate;				//录入日期
	private String combinationCode;			//产品组合代码
	private String businessSource;			//业务来源

	private List<PolicyProductDTO> policyProductList;	//保单产品信息
	private PolicyPremInfoDTO premInfo;					//保单缴费信息
	private String applicantName;						//投保人姓名
	private String insuredNo;							//被保人ID
	private String insuredName;							//被保人姓名
	private String dutyStatusDesc;						//理赔责任状态描述
	private String aplOptionDesc;						//自垫选择描述
	private String channelTypeDesc;						//业务渠道描述
	private String combinationCodeDesc;					//产品组合代码描述
	private String primaryPlanFullName;					//主险全称
	private String primaryPlanAbbrName;					//主险简称
	private String primaryPlanProductCode;				//主险产品代码
	private Date payToDate;								//交至日
	private boolean selected;							//是否选中
	private String canBeSelectedFlag;					//是否可选标志
	private POSVerifyResultDto verifyResult;			//受理规则检查结果
	// columns END
	// 构造函数

	public PolicyDTO() {
		policyProductList = new ArrayList<PolicyProductDTO>();
	}

	public PolicyDTO(String policyNo) {
		this.policyNo = policyNo;
		policyProductList = new ArrayList<PolicyProductDTO>();
	}

	// 默认主键自动生成，具体开发中需要开发人员根据实际情况进行适当修改

	public List<PolicyProductDTO> getPolicyProductList() {
		return policyProductList;
	}

	public void setPolicyProductList(List<PolicyProductDTO> policyProductList) {
		this.policyProductList = policyProductList;
	}

	public String getPolicyNo() {
		return this.policyNo;
	}

	public void setPolicyNo(String value) {
		this.policyNo = value;
	}

	// 非主键getter setter方法生成

	public String getApplicantNo() {
		return this.applicantNo;
	}

	public void setApplicantNo(String value) {
		this.applicantNo = value;
	}

	public String getBranchCode() {
		return this.branchCode;
	}

	public void setBranchCode(String value) {
		this.branchCode = value;
	}

	public String getDepartmentNo() {
		return this.departmentNo;
	}

	public void setDepartmentNo(String value) {
		this.departmentNo = value;
	}

	public String getDutyStatus() {
		return this.dutyStatus;
	}

	public void setDutyStatus(String value) {
		this.dutyStatus = value;
	}

	public String getAplOption() {
		return this.aplOption;
	}

	public void setAplOption(String value) {
		this.aplOption = value;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	public String getAgentNo() {
		return this.agentNo;
	}

	public void setAgentNo(String value) {
		this.agentNo = value;
	}

	public String getApplyNo() {
		return this.applyNo;
	}

	public void setApplyNo(String value) {
		this.applyNo = value;
	}

	public String getOldPolicyNo() {
		return this.oldPolicyNo;
	}

	public void setOldPolicyNo(String value) {
		this.oldPolicyNo = value;
	}

	public Date getApplyDate() {
		return this.applyDate;
	}

	public void setApplyDate(Date value) {
		this.applyDate = value;
	}

	public Date getIssueDate() {
		return this.issueDate;
	}

	public void setIssueDate(Date value) {
		this.issueDate = value;
	}

	public Date getEffectDate() {
		return this.effectDate;
	}

	public void setEffectDate(Date value) {
		this.effectDate = value;
	}
	
	public String getServiceStatus() {
		return this.serviceStatus;
	}

	public void setServiceStatus(String value) {
		this.serviceStatus = value;
	}

	public String getPkSerial() {
		return this.pkSerial;
	}

	public void setPkSerial(String value) {
		this.pkSerial = value;
	}

	public String getUpdatedUser() {
		return this.updatedUser;
	}

	public void setUpdatedUser(String value) {
		this.updatedUser = value;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date value) {
		this.updatedDate = value;
	}

	public String getCreatedUser() {
		return this.createdUser;
	}

	public void setCreatedUser(String value) {
		this.createdUser = value;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date value) {
		this.createdDate = value;
	}

	public String getCombinationCode() {
		return this.combinationCode;
	}

	public void setCombinationCode(String value) {
		this.combinationCode = value;
	}

	public PolicyPremInfoDTO getPremInfo() {
		return premInfo;
	}

	public void setPremInfo(PolicyPremInfoDTO premInfo) {
		this.premInfo = premInfo;
	}

	public String getApplicantName() {
		return applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public String getDutyStatusDesc() {
		return dutyStatusDesc;
	}

	public void setDutyStatusDesc(String dutyStatusDesc) {
		this.dutyStatusDesc = dutyStatusDesc;
	}

	public String getAplOptionDesc() {
		return aplOptionDesc;
	}

	public void setAplOptionDesc(String aplOptionDesc) {
		this.aplOptionDesc = aplOptionDesc;
	}

	public String getChannelTypeDesc() {
		return channelTypeDesc;
	}

	public void setChannelTypeDesc(String channelTypeDesc) {
		this.channelTypeDesc = channelTypeDesc;
	}

	public String getCombinationCodeDesc() {
		return combinationCodeDesc;
	}

	public void setCombinationCodeDesc(String combinationCodeDesc) {
		this.combinationCodeDesc = combinationCodeDesc;
	}

	public String getPrimaryPlanFullName() {
		return primaryPlanFullName;
	}

	public void setPrimaryPlanFullName(String primaryPlanFullName) {
		this.primaryPlanFullName = primaryPlanFullName;
	}

	public String getPrimaryPlanAbbrName() {
		return primaryPlanAbbrName;
	}

	public void setPrimaryPlanAbbrName(String primaryPlanAbbrName) {
		this.primaryPlanAbbrName = primaryPlanAbbrName;
	}

	public String getPrimaryPlanProductCode() {
		return primaryPlanProductCode;
	}

	public void setPrimaryPlanProductCode(String primaryPlanProductCode) {
		this.primaryPlanProductCode = primaryPlanProductCode;
	}

	public String getInsuredNo() {
		return insuredNo;
	}

	public void setInsuredNo(String insuredNo) {
		this.insuredNo = insuredNo;
	}

	public String getInsuredName() {
		return insuredName;
	}

	public void setInsuredName(String insuredName) {
		this.insuredName = insuredName;
	}

	public Date getPayToDate() {
		return payToDate;
	}

	public void setPayToDate(Date payToDate) {
		this.payToDate = payToDate;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getBusinessSource() {
		return businessSource;
	}

	public void setBusinessSource(String businessSource) {
		this.businessSource = businessSource;
	}

	public POSVerifyResultDto getVerifyResult() {
		return verifyResult;
	}

	public void setVerifyResult(POSVerifyResultDto verifyResult) {
		this.verifyResult = verifyResult;
	}

	public String getCanBeSelectedFlag() {
		return canBeSelectedFlag;
	}

	public void setCanBeSelectedFlag(String canBeSelectedFlag) {
		this.canBeSelectedFlag = canBeSelectedFlag;
	}

}
