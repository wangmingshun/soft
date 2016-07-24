package com.sinolife.pos.common.dto.serviceitems;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.InsuredClientDTO;
import com.sinolife.pos.common.dto.PersonalNoticeDTO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSVerifyResultDto;

/**
 * 服务项目（保全项目）公共信息对应的DTO
 */
@SuppressWarnings("rawtypes")
public class ServiceItemsInputDTO implements Serializable {

	private static final long serialVersionUID = 5088300133055216467L;

	private String clientNo; // 客户号
	private String acceptClientNo; // 受理客戶号

	private String batchNo; // 批次号
	private String posNo; // 保全号
	private String serviceItems; // 服务项目（保全项目）
	private String serviceItemsDesc;// 服务项目（保全项目）描述
	private String applyType; // 申请类型
	private String acceptChannel; // 受理渠道
	private String policyChannelType;// 保单渠道

	private String representor; // 代办人姓名
	private String idType; // 代办人证件类型
	private String representIdNo; // 代办人证件号码
	private String barcodeNo; // 条形码
	private String relateBarcode; // 关联条形码
	private String filePages; // 资料页数
	private String signAccordFlag; // 签名不符标记
	private String policyProvideTime;// 保单补发次数
	private String specialFunc; // 特殊件标记
	private String specialRuleType; // 规则特殊件类型
	private String specialRuleReason; // 规则特殊件类型原因
	private String callConfirm; // 电话回访确认件
	private String approveNo; // 业务审批号

	private String remark; // 备注
	private String acceptDate; // 受理日期yyyy-mm-dd
	private String applyDate; // 申请日期yyyy-mm-dd
	private String policyNo; // 保单号
	private String policyApplyBarCode;//保单投保单号	
	private String acceptSeq; // 受理序号，即当前受理为第几个
	private String acceptCount; // 受理总计
	private String groupNo; // detail表里最大的分组号
	private List<String> posNoList; // 部分项目会根据保单数目新产生posNo
	private PersonalNoticeDTO appPersonalNotice; // 投保人个人资料告知信息
	private PersonalNoticeDTO insPersonalNotice; // 被保人个人资料告知
	private boolean appInsTheSame; // 投被保人是同一人
	private List<CodeTableItemDTO> specialFuncList; // 可选功能特殊件选项
	private List<CodeTableItemDTO> specialRuleList; // 可选规则特殊件类型选项
	private List<CodeTableItemDTO> specialRuleReasonList; // 可选规则特殊件原因选项
	private ServiceItemsInputDTO originServiceItemsInputDTO; // 变更之前的值
	private boolean policyProvideTimeEditable; // 保单补发次数是否可编辑标志
	private boolean specialFuncEnabled; // 功能特殊件是否可选标志
	private boolean specialFuncSelected; // 是否选择了功能特殊功能件
	private String specialRule; // 是否选择了规则特殊件
	private String otherSpecialRuleType; // 规则特殊件其它类型描述
	private String otherSpecialRuleReason; // 规则特殊件其它原因描述
	private String autographImageURL; // 客户签名影像图片地址

	private String funcSpecPriv; // 用户所能操作的功能特殊件编号，用逗号分隔，页面用indexof判断
	private POSVerifyResultDto verifyResult; // 规则检查结果
	private boolean selfInsured; // 是否为业务员自保件
	private String attechmentFileId; // 附件在IM中的ID
	private String attechmentFileName; // 附件的文件名
	private String clientNoApp; // 投保人客户号
	private String clientNoIns; // 被保人客户号
	private boolean clientSelectEnabled; // 是否允许选择投被保人
	private String remindMessage; // 提醒消息，设置了该消息时，进入受理界面会显示该消息
	private List<CodeTableItemDTO> specialRetreatReasonList; // 协议退费原因选项
	private String specialRetreatReason; // 协议退费原因
	private String specialRuleLoan; // 规则特殊件贷款
	private BigDecimal loanMaxSum; // 保单最大可贷金额
	private String vipGrade; // vip等级
	private String mobile; // 官网加挂的手机号
	private String webEmail; // 官网注册邮箱

	/* edit by gaojiaming start */
	private String terminalCode; // 终端机编码
	private String is_wechat_attention; // 是微信关注

	private String mainProductCode;// 主险代码
	//家庭单需求 多个被保人(多个个人资料告知) add by zhangyi.wb
	private List<PersonalNoticeDTO> insPersonalNoticeList; // 被保人个人资料告知列表
	private List<InsuredClientDTO> clientNoInsList; // 被保人客户信息
	//人脸识别控件参数
	private String faceControlParameter;
	//人脸识别控件标示
	private String faceControlParameterFlag;
	//判断保单是否是指定机构
	private String isBranchFlag;
	//判断人脸识别控件CAB  irs地址
	private String irsControlCAB;
	//投保人客户信息
	private List<ClientInformationDTO> clientInfoAppList;
	
	public List<ClientInformationDTO> getClientInfoAppList() {
		return clientInfoAppList;
	}

	public void setClientInfoAppList(List<ClientInformationDTO> clientInfoAppList) {
		this.clientInfoAppList = clientInfoAppList;
	}

	public String getIrsControlCAB() {
		return irsControlCAB;
	}

	public void setIrsControlCAB(String irsControlCAB) {
		this.irsControlCAB = irsControlCAB;
	}

	public String getIsBranchFlag() {
		return isBranchFlag;
	}

	public void setIsBranchFlag(String isBranchFlag) {
		this.isBranchFlag = isBranchFlag;
	}

	public String getFaceControlParameter() {
		return faceControlParameter;
	}

	public void setFaceControlParameter(String faceControlParameter) {
		this.faceControlParameter = faceControlParameter;
	}
	
	public String getFaceControlParameterFlag() {
		return faceControlParameterFlag;
	}

	public void setFaceControlParameterFlag(String faceControlParameterFlag) {
		this.faceControlParameterFlag = faceControlParameterFlag;
	}

	public List<InsuredClientDTO> getClientNoInsList() {
		return clientNoInsList;
	}

	public void setClientNoInsList(List<InsuredClientDTO> clientNoInsList) {
		this.clientNoInsList = clientNoInsList;
	}

	public List<PersonalNoticeDTO> getInsPersonalNoticeList() {
		return insPersonalNoticeList;
	}

	public void setInsPersonalNoticeList(
			List<PersonalNoticeDTO> insPersonalNoticeList) {
		this.insPersonalNoticeList = insPersonalNoticeList;
	}

	public String getMainProductCode() {
		return mainProductCode;
	}

	public void setMainProductCode(String mainProductCode) {
		this.mainProductCode = mainProductCode;
	}

	public String getIs_wechat_attention() {
		return is_wechat_attention;
	}

	public void setIs_wechat_attention(String is_wechat_attention) {
		this.is_wechat_attention = is_wechat_attention;
	}

	public String getTerminalCode() {
		return terminalCode;
	}

	public void setTerminalCode(String terminalCode) {
		this.terminalCode = terminalCode;
	}

	/* edit by gaojiaming end */

	public String getAcceptClientNo() {
		return acceptClientNo;
	}

	public void setAcceptClientNo(String acceptClientNo) {
		this.acceptClientNo = acceptClientNo;
	}

	public String getWebEmail() {
		return webEmail;
	}

	public void setWebEmail(String webEmail) {
		this.webEmail = webEmail;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getVipGrade() {
		return vipGrade;
	}

	public void setVipGrade(String vipGrade) {
		this.vipGrade = vipGrade;
	}

	public BigDecimal getLoanMaxSum() {
		return loanMaxSum;
	}

	public void setLoanMaxSum(BigDecimal loanMaxSum) {
		this.loanMaxSum = loanMaxSum;
	}

	public String getSpecialRuleLoan() {
		return specialRuleLoan;
	}

	public void setSpecialRuleLoan(String specialRuleLoan) {
		this.specialRuleLoan = specialRuleLoan;
	}

	public List<CodeTableItemDTO> getSpecialRetreatReasonList() {
		return specialRetreatReasonList;
	}

	public void setSpecialRetreatReasonList(
			List<CodeTableItemDTO> specialRetreatReasonList) {
		this.specialRetreatReasonList = specialRetreatReasonList;
	}

	public String getSpecialRetreatReason() {
		return specialRetreatReason;
	}

	public void setSpecialRetreatReason(String specialRetreatReason) {
		this.specialRetreatReason = specialRetreatReason;
	}

	/**
	 * 校验方法，子类覆盖时要调用父类的该方法
	 * 
	 * @param err
	 */
	public void validate(Errors err) {
		onPropertyChangeInternal();
	}

	/**
	 * 更新快照，快照保存了调用该方法时的DTO值
	 */
	public void updateSnapshot() {
		try {
			onPropertyChangeInternal();
			ServiceItemsInputDTO snapshot = (ServiceItemsInputDTO) PosUtils
					.deepCopy(this);
			snapshot.setOriginServiceItemsInputDTO(null);
			this.setOriginServiceItemsInputDTO(snapshot);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 实施属性间的依赖关系 用于在执行校验，或生成受理明细之前对数据进行一次处理。
	 * 页面在多次绑定时，DTO可能还保存了本该清空的、或在特定情况才能录入的值，这时，需要该方法最后进行一次检查和处理 for override
	 */
	protected void onPropertyChange() {
	}

	private void onPropertyChangeInternal() {
		// 功能特殊件
		if (isSpecialFuncEnabled()) {
			if (!isSpecialFuncSelected()) {
				setSpecialFunc(null);
			}
		} else {
			setSpecialFunc(null);
		}

		// 规则特殊件
		if (!"Y".equals(getSpecialRule()))
			setSpecialRule(null);

		// 电话回访确认件
		if (!"Y".equals(getCallConfirm())) {
			setCallConfirm(null);
			setAttechmentFileId(null);
			setAttechmentFileName(null);
		}

		// 上传文件ID为空，设置文件名为空，防止写入数据库
		if (StringUtils.isBlank(getAttechmentFileId())) {
			setAttechmentFileName(null);
		}

		// 投被保人一致，只允许录入一个，当保全项目为20投保人变更时录入投保人，否则录入被保人
		if (appInsTheSame) {
			if ("20".equals(serviceItems)) {
				setInsPersonalNotice(null);
			} else {
				setAppPersonalNotice(null);
			}
		}

		onPropertyChange();
	}

	public List<String> getPosNoList() {
		if (this.posNoList == null) {
			this.posNoList = new ArrayList<String>();
			this.posNoList.add(getPosNo());
		}
		return posNoList;
	}

	public void setPosNoList(List<String> posNoList) {
		this.posNoList = posNoList;
	}

	public void addPosNo(String posNo) {
		if (this.posNoList == null) {
			this.posNoList = new ArrayList<String>();
			this.posNoList.add(getPosNo());
		}
		this.posNoList.add(posNo);
	}

	public String getGroupNo() {
		return groupNo;
	}

	/**
	 * 相当于groupNo++
	 * 
	 * @return
	 */
	public void groupNoAddOne() {
		if (groupNo == null || "".equals(groupNo)) {
			groupNo = "0";
		} else {
			groupNo = "" + (Integer.parseInt(groupNo) + 1);
		}
	}

	public void setGroupNo(String groupNo) {
		this.groupNo = groupNo;
	}

	/* ************************
	 * getters and setters ***********************
	 */

	public String getServiceItems() {
		return serviceItems;
	}

	public void setServiceItems(String serviceItems) {
		this.serviceItems = serviceItems;
	}

	public String getApplyType() {
		return applyType;
	}

	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}

	public String getAcceptChannel() {
		return acceptChannel;
	}

	public void setAcceptChannel(String acceptChannel) {
		this.acceptChannel = acceptChannel;
	}

	public String getRepresentor() {
		return representor;
	}

	public void setRepresentor(String representor) {
		this.representor = representor;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getRepresentIdNo() {
		return representIdNo;
	}

	public void setRepresentIdNo(String representIdNo) {
		this.representIdNo = representIdNo;
	}

	public String getBarcodeNo() {
		return barcodeNo;
	}

	public void setBarcodeNo(String barcodeNo) {
		this.barcodeNo = barcodeNo;
	}

	public String getRelateBarcode() {
		return relateBarcode;
	}

	public void setRelateBarcode(String relateBarcode) {
		this.relateBarcode = relateBarcode;
	}

	public String getSignAccordFlag() {
		return signAccordFlag;
	}

	public void setSignAccordFlag(String signAccordFlag) {
		this.signAccordFlag = signAccordFlag;
	}

	public String getPolicyProvideTime() {
		return policyProvideTime;
	}

	public void setPolicyProvideTime(String policyProvideTime) {
		this.policyProvideTime = policyProvideTime;
	}

	public String getSpecialFunc() {
		return specialFunc;
	}

	public void setSpecialFunc(String specialFunc) {
		this.specialFunc = specialFunc;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getAcceptDate() {
		return acceptDate;
	}

	public void setAcceptDate(String acceptDate) {
		this.acceptDate = acceptDate;
	}

	public String getFilePages() {
		return filePages;
	}

	public void setFilePages(String filePages) {
		this.filePages = filePages;
	}

	public String getPosNo() {
		return posNo;
	}

	public void setPosNo(String posNo) {
		this.posNo = posNo;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getCallConfirm() {
		return callConfirm;
	}

	public void setCallConfirm(String callConfirm) {
		this.callConfirm = callConfirm;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public void setClientNo(String clientNo) {
		this.clientNo = clientNo;
	}

	public String getClientNo() {
		return clientNo;
	}

	public String getServiceItemsDesc() {
		return serviceItemsDesc;
	}

	public void setServiceItemsDesc(String serviceItemsDesc) {
		this.serviceItemsDesc = serviceItemsDesc;
	}

	public String getAcceptSeq() {
		return acceptSeq;
	}

	public void setAcceptSeq(String acceptSeq) {
		this.acceptSeq = acceptSeq;
	}

	public String getAcceptCount() {
		return acceptCount;
	}

	public void setAcceptCount(String acceptCount) {
		this.acceptCount = acceptCount;
	}

	public ServiceItemsInputDTO getOriginServiceItemsInputDTO() {
		return originServiceItemsInputDTO;
	}

	public void setOriginServiceItemsInputDTO(
			ServiceItemsInputDTO originServiceItemsInputDTO) {
		this.originServiceItemsInputDTO = originServiceItemsInputDTO;
	}

	public boolean isPolicyProvideTimeEditable() {
		return policyProvideTimeEditable;
	}

	public void setPolicyProvideTimeEditable(boolean policyProvideTimeEditable) {
		this.policyProvideTimeEditable = policyProvideTimeEditable;
	}

	public List<CodeTableItemDTO> getSpecialFuncList() {
		return specialFuncList;
	}

	public void setSpecialFuncList(List<CodeTableItemDTO> specialFuncList) {
		this.specialFuncList = specialFuncList;
	}

	public boolean isSpecialFuncEnabled() {
		return specialFuncEnabled;
	}

	public void setSpecialFuncEnabled(boolean specialFuncEnabled) {
		this.specialFuncEnabled = specialFuncEnabled;
	}

	public boolean isSpecialFuncSelected() {
		return specialFuncSelected;
	}

	public void setSpecialFuncSelected(boolean specialFuncSelected) {
		this.specialFuncSelected = specialFuncSelected;
	}

	public String getSpecialRule() {
		return specialRule;
	}

	public void setSpecialRule(String specialRule) {
		this.specialRule = specialRule;
	}

	public String getApplyDate() {
		return applyDate;
	}

	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}

	public String getFuncSpecPriv() {
		return funcSpecPriv;
	}

	public String getAutographImageURL() {
		return autographImageURL;
	}

	public void setAutographImageURL(String autographImageURL) {
		this.autographImageURL = autographImageURL;
	}

	public void setFuncSpecPriv(String funcSpecPriv) {
		this.funcSpecPriv = funcSpecPriv;
	}

	public void setFuncSpecPriv(List list) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; list != null && i < list.size(); i++) {
			buf.append(list.get(i)).append(",");
		}
		this.funcSpecPriv = buf.toString();
	}

	public POSVerifyResultDto getVerifyResult() {
		return verifyResult;
	}

	public void setVerifyResult(POSVerifyResultDto verifyResult) {
		this.verifyResult = verifyResult;
	}

	public boolean isSelfInsured() {
		return selfInsured;
	}

	public void setSelfInsured(boolean selfInsured) {
		this.selfInsured = selfInsured;
	}

	public String getAttechmentFileId() {
		return attechmentFileId;
	}

	public void setAttechmentFileId(String attechmentFileId) {
		this.attechmentFileId = attechmentFileId;
	}

	public String getAttechmentFileName() {
		return attechmentFileName;
	}

	public void setAttechmentFileName(String attechmentFileName) {
		this.attechmentFileName = attechmentFileName;
	}

	public PersonalNoticeDTO getAppPersonalNotice() {
		return appPersonalNotice;
	}

	public void setAppPersonalNotice(PersonalNoticeDTO appPersonalNotice) {
		this.appPersonalNotice = appPersonalNotice;
	}

	public PersonalNoticeDTO getInsPersonalNotice() {
		return insPersonalNotice;
	}

	public void setInsPersonalNotice(PersonalNoticeDTO insPersonalNotice) {
		this.insPersonalNotice = insPersonalNotice;
	}

	public boolean isAppInsTheSame() {
		return appInsTheSame;
	}

	public void setAppInsTheSame(boolean appInsTheSame) {
		this.appInsTheSame = appInsTheSame;
	}

	public String getClientNoApp() {
		return clientNoApp;
	}

	public void setClientNoApp(String clientNoApp) {
		this.clientNoApp = clientNoApp;
	}

	public String getClientNoIns() {
		return clientNoIns;
	}

	public void setClientNoIns(String clientNoIns) {
		this.clientNoIns = clientNoIns;
	}

	public boolean isClientSelectEnabled() {
		return clientSelectEnabled;
	}

	public void setClientSelectEnabled(boolean clientSelectEnabled) {
		this.clientSelectEnabled = clientSelectEnabled;
	}

	public String getRemindMessage() {
		return remindMessage;
	}

	public void setRemindMessage(String remindMessage) {
		this.remindMessage = remindMessage;
	}

	public String getApproveNo() {
		return approveNo;
	}

	public void setApproveNo(String approveNo) {
		this.approveNo = approveNo;
	}

	public String getPolicyChannelType() {
		return policyChannelType;
	}

	public void setPolicyChannelType(String policyChannelType) {
		this.policyChannelType = policyChannelType;
	}

	public List<CodeTableItemDTO> getSpecialRuleList() {
		return specialRuleList;
	}

	public void setSpecialRuleList(List<CodeTableItemDTO> specialRuleList) {
		this.specialRuleList = specialRuleList;
	}

	public List<CodeTableItemDTO> getSpecialRuleReasonList() {
		return specialRuleReasonList;
	}

	public void setSpecialRuleReasonList(
			List<CodeTableItemDTO> specialRuleReasonList) {
		this.specialRuleReasonList = specialRuleReasonList;
	}

	public String getSpecialRuleType() {
		return specialRuleType;
	}

	public void setSpecialRuleType(String specialRuleType) {
		this.specialRuleType = specialRuleType;
	}

	public String getSpecialRuleReason() {
		return specialRuleReason;
	}

	public void setSpecialRuleReason(String specialRuleReason) {
		this.specialRuleReason = specialRuleReason;
	}

	public String getOtherSpecialRuleType() {
		return otherSpecialRuleType;
	}

	public void setOtherSpecialRuleType(String otherSpecialRuleType) {
		this.otherSpecialRuleType = otherSpecialRuleType;
	}

	public String getOtherSpecialRuleReason() {
		return otherSpecialRuleReason;
	}

	public void setOtherSpecialRuleReason(String otherSpecialRuleReason) {
		this.otherSpecialRuleReason = otherSpecialRuleReason;
	}

	public String getPolicyApplyBarCode() {
		return policyApplyBarCode;
	}

	public void setPolicyApplyBarCode(String policyApplyBarCode) {
		this.policyApplyBarCode = policyApplyBarCode;
	}

}
