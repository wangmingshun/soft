package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.sinolife.pos.todolist.problem.dto.ProblemLetterInfoDTO;

/**
 * 保全问题件
 */
public class PosProblemItemsDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2770695273507187567L;
	
	private String problemItemNo;	//问题件号
	private String problemItemType;	//问题件类型
	private String posNo;			//保全号
	private String statusChangeNo;	//状态变迁序号
	private String problemStatus;	//问题件状态
	private String submitter;		//下发人
	private String problemContent;	//问题件内容
	private String accessor;		//处理人
	private String pkSerial;		//数据主键
	private String dealResult;		//处理结果
	private String dealOpinion;		//处理意见
	private String transferFailureCause="";//财务转账失败原因
	
	
	/* 非表字段属性 */
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date submitDate;						//问题件下发日期
	private String submitterName;					//下发人姓名
	private String accessorName;					//处理人姓名
	private String problemItemTypeDesc;				//问题件类型描述
	private String problemStatusDesc;				//问题件状态描述
	private String dealResultDesc;					//处理结果描述
	private PosInfoDTO posInfo;						//问题件对应保全信息
	private PosStatusChangeHistoryDTO stsChangeHis;	//问题件对应状态变迁信息
	
	private ProblemLetterInfoDTO letterInfo;		//问题函信息
	private List<CodeTableItemDTO> problemDealResultList;//问题件处理结果选项
	
	private String attechmentFileId;				//附件在IM中的ID
	private String attechmentFileName;				//附件的文件名
	
	private String detailSequenceNo; //条形码
	private String noteType; // 信函类型
	private String dealUser;// 受理人
	private String clientName; //客户姓名
	private String clientAddress; //客户地址
	private String phoneNo; //联系电话
	private String dealType; //处理结果类型
	
	private String provinceCode;	//省代码
	private String cityCode;		//市代码
	private String areaCode;		//区代码
	private String detailAddress;	//详细地址
	private String postalcode;		//邮政编码
	
	/* 属性存取 */
	
	public String getProvinceCode() {
		return provinceCode;
	}
	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getDetailAddress() {
		return detailAddress;
	}
	public void setDetailAddress(String detailAddress) {
		this.detailAddress = detailAddress;
	}
	public String getPostalcode() {
		return postalcode;
	}
	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}
	public String getDealType() {
		return dealType;
	}
	public void setDealType(String dealType) {
		this.dealType = dealType;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
	public String getClientAddress() {
		return clientAddress;
	}
	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getDealUser() {
		return dealUser;
	}
	public void setDealUser(String dealUser) {
		this.dealUser = dealUser;
	}
	public String getNoteType() {
		return noteType;
	}
	public void setNoteType(String noteType) {
		this.noteType = noteType;
	}
	public String getDetailSequenceNo() {
		return detailSequenceNo;
	}
	public void setDetailSequenceNo(String detailSequenceNo) {
		this.detailSequenceNo = detailSequenceNo;
	}
	public String getProblemItemNo() {
		return problemItemNo;
	}
	public void setProblemItemNo(String problemItemNo) {
		this.problemItemNo = problemItemNo;
	}
	public String getProblemItemType() {
		return problemItemType;
	}
	public void setProblemItemType(String problemItemType) {
		this.problemItemType = problemItemType;
	}
	public String getPosNo() {
		return posNo;
	}
	public void setPosNo(String posNo) {
		this.posNo = posNo;
	}
	public String getStatusChangeNo() {
		return statusChangeNo;
	}
	public void setStatusChangeNo(String statusChangeNo) {
		this.statusChangeNo = statusChangeNo;
	}
	public String getSubmitter() {
		return submitter;
	}
	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}
	public String getProblemContent() {
		return problemContent;
	}
	public void setProblemContent(String problemContent) {
		this.problemContent = problemContent;
	}
	public String getAccessor() {
		return accessor;
	}
	public void setAccessor(String accessor) {
		this.accessor = accessor;
	}
	public String getPkSerial() {
		return pkSerial;
	}
	public void setPkSerial(String pkSerial) {
		this.pkSerial = pkSerial;
	}
	public String getProblemItemTypeDesc() {
		return problemItemTypeDesc;
	}
	public void setProblemItemTypeDesc(String problemItemTypeDesc) {
		this.problemItemTypeDesc = problemItemTypeDesc;
	}
	public PosInfoDTO getPosInfo() {
		return posInfo;
	}
	public void setPosInfo(PosInfoDTO posInfo) {
		this.posInfo = posInfo;
	}
	public PosStatusChangeHistoryDTO getStsChangeHis() {
		return stsChangeHis;
	}
	public void setStsChangeHis(PosStatusChangeHistoryDTO stsChangeHis) {
		this.stsChangeHis = stsChangeHis;
	}
	public Date getSubmitDate() {
		return submitDate;
	}
	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}
	public String getProblemStatus() {
		return problemStatus;
	}
	public void setProblemStatus(String problemStatus) {
		this.problemStatus = problemStatus;
	}
	public String getDealResult() {
		return dealResult;
	}
	public void setDealResult(String dealResult) {
		this.dealResult = dealResult;
	}
	public String getDealOpinion() {
		return dealOpinion;
	}
	public void setDealOpinion(String dealOpinion) {
		this.dealOpinion = dealOpinion;
	}
	public String getProblemStatusDesc() {
		return problemStatusDesc;
	}
	public void setProblemStatusDesc(String problemStatusDesc) {
		this.problemStatusDesc = problemStatusDesc;
	}
	public String getSubmitterName() {
		return submitterName;
	}
	public void setSubmitterName(String submitterName) {
		this.submitterName = submitterName;
	}
	public String getAccessorName() {
		return accessorName;
	}
	public void setAccessorName(String accessorName) {
		this.accessorName = accessorName;
	}
	public String getDealResultDesc() {
		return dealResultDesc;
	}
	public void setDealResultDesc(String dealResultDesc) {
		this.dealResultDesc = dealResultDesc;
	}
	public ProblemLetterInfoDTO getLetterInfo() {
		return letterInfo;
	}
	public void setLetterInfo(ProblemLetterInfoDTO letterInfo) {
		this.letterInfo = letterInfo;
	}
	public List<CodeTableItemDTO> getProblemDealResultList() {
		return problemDealResultList;
	}
	public void setProblemDealResultList(
			List<CodeTableItemDTO> problemDealResultList) {
		this.problemDealResultList = problemDealResultList;
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
	public String getTransferFailureCause() {
		return transferFailureCause;
	}
	public void setTransferFailureCause(String transferFailureCause) {
		this.transferFailureCause = transferFailureCause;
	}
}
