package com.sinolife.pos.common.dto;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;

import org.springframework.format.annotation.DateTimeFormat;

import com.sinolife.sf.ruleengine.pos.bom.dto.POSVerifyResultDto;

/**
 * 保全信息
 */
@Entity
public class PosInfoDTO extends PosComDTO {
	private static final long serialVersionUID = 5454155825314635342L;

	/* 表结构属性 */
	private String posNo; // 保全号
	private String serviceItems; // 服务项目
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date acceptDate; // 受理日期
	private String acceptStatusCode; // 受理状态
	private String approveText; // 变更批文
	private String policyNo; // 保单号
	private String clientNo; // 客户号
	private String rollbackCause; // 回退原因
	private String abandonedDetail; // 作废详细信息
	private String barcodeNo; // 条形码
	private String premName; // 收付款人姓名
	private String idType; // 收付款人证件类型代码
	private String premIdno; // 收付款人证件号码
	private BigDecimal premSum; // 合计收付金额
	private String premFlag; // 收付标志
	private String premSuccessFlag; // 收付成功标志
	private String bankCode; // 银行代码
	private String accountNo; // 转账账号
	private String accountNoType; // 银行卡类型
	private String branchCode; // 保单所属机构
	private String primaryProductName; // 主险名称

	private String foreignExchangeType; // 外币账户类型
	private String acceptor; // 受理人
	private String remark; // 备注
	private String pkSerial; // 数据主键
	private Integer acceptSeq; // 受理序号
	private String transferResult; // 转账结果
	private String paymentType; // 收付方式
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date premSuccessDate; // 收付成功日期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date handledDate; // 经办完成日期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date approvedDate; // 审批完成日期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date underwritedDate; // 核保完成日期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date effectiveDate; // 生效日期

	private String businessSourceDesc;// 业务来源

	/* 非表结构属性 */
	private String serviceItemsDesc; // 服务项目名称
	private String acceptStatusDesc; // 受理状态描述
	private boolean hasRiskCaution = false; // 是否存在风险提示
	private String riskCautionDesc; // 风险提示信息
	private boolean hasAcceptCaution = false; // 是否存在受理提醒
	private String acceptCautionDesc; // 受理提醒信息
	private ClientInformationDTO applicantInfo; // 保单投保人信息
	private ClientInformationDTO insuredInfo; // 保单被保人信息
	private String bankName; // 银行名称
	private String transferResultDesc; // 转账失败原因描述
	private String transferSuspStatus; // 是否可以取消转账暂停标志
	private String clientName; // 客户姓名
	private POSVerifyResultDto verifyResult; // 规则检查结果

	/* 构造函数 */

	public PosInfoDTO() {
	}

	public PosInfoDTO(String posNo) {
		this.posNo = posNo;
	}

	public String getBusinessSourceDesc() {
		return businessSourceDesc;
	}

	public void setBusinessSourceDesc(String businessSourceDesc) {
		this.businessSourceDesc = businessSourceDesc;
	}

	/* 属性存取 */

	public String getPosNo() {
		return this.posNo;
	}

	public void setPosNo(String value) {
		this.posNo = value;
	}

	public String getServiceItems() {
		return this.serviceItems;
	}

	public void setServiceItems(String value) {
		this.serviceItems = value;
	}

	public Date getAcceptDate() {
		return this.acceptDate;
	}

	public void setAcceptDate(Date value) {
		this.acceptDate = value;
	}

	public String getAcceptStatusCode() {
		return this.acceptStatusCode;
	}

	public void setAcceptStatusCode(String value) {
		this.acceptStatusCode = value;
	}

	public String getApproveText() {
		return this.approveText;
	}

	public void setApproveText(String value) {
		this.approveText = value;
	}

	public String getPolicyNo() {
		return this.policyNo;
	}

	public void setPolicyNo(String value) {
		this.policyNo = value;
	}

	public String getClientNo() {
		return this.clientNo;
	}

	public void setClientNo(String value) {
		this.clientNo = value;
	}

	public String getRollbackCause() {
		return this.rollbackCause;
	}

	public void setRollbackCause(String value) {
		this.rollbackCause = value;
	}

	public String getAbandonedDetail() {
		return this.abandonedDetail;
	}

	public void setAbandonedDetail(String value) {
		this.abandonedDetail = value;
	}

	public String getBarcodeNo() {
		return this.barcodeNo;
	}

	public void setBarcodeNo(String value) {
		this.barcodeNo = value;
	}

	public String getPremName() {
		return this.premName;
	}

	public void setPremName(String value) {
		this.premName = value;
	}

	public String getIdType() {
		return this.idType;
	}

	public void setIdType(String value) {
		this.idType = value;
	}

	public String getPremIdno() {
		return this.premIdno;
	}

	public void setPremIdno(String value) {
		this.premIdno = value;
	}

	public BigDecimal getPremSum() {
		return this.premSum;
	}

	public void setPremSum(BigDecimal value) {
		this.premSum = value;
	}

	public String getPremFlag() {
		return this.premFlag;
	}

	public void setPremFlag(String value) {
		this.premFlag = value;
	}

	public String getPremSuccessFlag() {
		return this.premSuccessFlag;
	}

	public void setPremSuccessFlag(String value) {
		this.premSuccessFlag = value;
	}

	public String getBankCode() {
		return this.bankCode;
	}

	public void setBankCode(String value) {
		this.bankCode = value;
	}

	public String getAccountNo() {
		return this.accountNo;
	}

	public void setAccountNo(String value) {
		this.accountNo = value;
	}

	public String getForeignExchangeType() {
		return this.foreignExchangeType;
	}

	public void setForeignExchangeType(String value) {
		this.foreignExchangeType = value;
	}

	public String getTransferResult() {
		return transferResult;
	}

	public void setTransferResult(String transferResult) {
		this.transferResult = transferResult;
	}

	public String getAcceptor() {
		return this.acceptor;
	}

	public void setAcceptor(String value) {
		this.acceptor = value;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String value) {
		this.remark = value;
	}

	public String getPkSerial() {
		return this.pkSerial;
	}

	public void setPkSerial(String value) {
		this.pkSerial = value;
	}

	public Integer getAcceptSeq() {
		return acceptSeq;
	}

	public void setAcceptSeq(Integer acceptSeq) {
		this.acceptSeq = acceptSeq;
	}

	public String getServiceItemsDesc() {
		return serviceItemsDesc;
	}

	public void setServiceItemsDesc(String serviceItemsDesc) {
		this.serviceItemsDesc = serviceItemsDesc;
	}

	public boolean isHasRiskCaution() {
		return hasRiskCaution;
	}

	public void setHasRiskCaution(boolean hasRiskCaution) {
		this.hasRiskCaution = hasRiskCaution;
	}

	public String getRiskCautionDesc() {
		return riskCautionDesc;
	}

	public void setRiskCautionDesc(String riskCautionDesc) {
		this.riskCautionDesc = riskCautionDesc;
	}

	public boolean isHasAcceptCaution() {
		return hasAcceptCaution;
	}

	public void setHasAcceptCaution(boolean hasAcceptCaution) {
		this.hasAcceptCaution = hasAcceptCaution;
	}

	public String getAcceptCautionDesc() {
		return acceptCautionDesc;
	}

	public void setAcceptCautionDesc(String acceptCautionDesc) {
		this.acceptCautionDesc = acceptCautionDesc;
	}

	public String getAcceptStatusDesc() {
		return acceptStatusDesc;
	}

	public void setAcceptStatusDesc(String acceptStatusDesc) {
		this.acceptStatusDesc = acceptStatusDesc;
	}

	public ClientInformationDTO getApplicantInfo() {
		return applicantInfo;
	}

	public void setApplicantInfo(ClientInformationDTO applicantInfo) {
		this.applicantInfo = applicantInfo;
	}

	public ClientInformationDTO getInsuredInfo() {
		return insuredInfo;
	}

	public void setInsuredInfo(ClientInformationDTO insuredInfo) {
		this.insuredInfo = insuredInfo;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getTransferResultDesc() {
		return transferResultDesc;
	}

	public void setTransferResultDesc(String transferResultDesc) {
		this.transferResultDesc = transferResultDesc;
	}

	public String getTransferSuspStatus() {
		return transferSuspStatus;
	}

	public void setTransferSuspStatus(String transferSuspStatus) {
		this.transferSuspStatus = transferSuspStatus;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public POSVerifyResultDto getVerifyResult() {
		return verifyResult;
	}

	public void setVerifyResult(POSVerifyResultDto verifyResult) {
		this.verifyResult = verifyResult;
	}

	public Date getPremSuccessDate() {
		return premSuccessDate;
	}

	public void setPremSuccessDate(Date premSuccessDate) {
		this.premSuccessDate = premSuccessDate;
	}

	public Date getHandledDate() {
		return handledDate;
	}

	public void setHandledDate(Date handledDate) {
		this.handledDate = handledDate;
	}

	public Date getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	public Date getUnderwritedDate() {
		return underwritedDate;
	}

	public void setUnderwritedDate(Date underwritedDate) {
		this.underwritedDate = underwritedDate;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getAccountNoType() {
		return accountNoType;
	}

	public void setAccountNoType(String accountNoType) {
		this.accountNoType = accountNoType;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getPrimaryProductName() {
		return primaryProductName;
	}

	public void setPrimaryProductName(String primaryProductName) {
		this.primaryProductName = primaryProductName;
	}
}
