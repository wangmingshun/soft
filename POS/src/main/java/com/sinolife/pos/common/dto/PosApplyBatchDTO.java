package com.sinolife.pos.common.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 保全申请批次
 */
@Entity
public class PosApplyBatchDTO extends PosComDTO {
	private static final long serialVersionUID = 5454155825314635342L;

	/* 表结构属性 */
	private String posBatchNo;				//批次号
	private String acceptor;				//受理人
	private String counterNo;				//柜面号
	private String applyTypeCode;			//申请类型
	private String acceptChannelCode;		//受理渠道
	private String clientNo;				//客户号
	private String representor;				//代办人姓名
	private String idType;					//代办人证件类型代码
	private String representIdno;			//代办人证件号码
	private String batchStatus;				//批次状态
	private String pkSerial;				//数据主键
	/* 证件有效期属性增加 edit by wangmingshun start */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date idnoValidityDate;			//申请人证件有效期
	
	
	public Date getIdnoValidityDate() {
		return idnoValidityDate;
	}

	public void setIdnoValidityDate(Date idnoValidityDate) {
		this.idnoValidityDate = idnoValidityDate;
	}
	/* 证件有效期属性增加 edit by wangmingshun end */

	/* 非表结构属性 */
	private ClientInformationDTO clientInfo;//申请人客户信息
	private String empNo;					//申请人客户号
	private List<String> clientPolicyNoList = new ArrayList<String>();				//申请人作为投被保人的保单号List
	private List<PosInfoDTO> posInfoList = new ArrayList<PosInfoDTO>();				//本批次的保全申请List
	private List<PosApplyFilesDTO> applyFiles = new ArrayList<PosApplyFilesDTO>();	//本批次的申请书List
	private List<PosInfoDTO> notCompletePosInfoList;								//未结案保全信息
	
	/* edit by gaojiaming start */
	private String approveEmpNo; // 代审人工号
	private String approveDeptNo; // 代审网点编码
	private String examType; // 代审类型
	private List<FillFormDTO> fillFormList = new ArrayList<FillFormDTO>(); // 免填单List
	
	/* edit by wangmingshun start */
	private boolean isDoubtTransaction; //是否可疑交易
	private String doubtTransactionRemark; //可疑交易备注
	
	private String marking; //标示(是否为电销)
	

	public String getMarking() {
		return marking;
	}

	public void setMarking(String marking) {
		this.marking = marking;
	}

	public boolean getIsDoubtTransaction() {
		return isDoubtTransaction;
	}

	public void setIsDoubtTransaction(boolean isDoubtTransaction) {
		this.isDoubtTransaction = isDoubtTransaction;
	}

	public String getDoubtTransactionRemark() {
		return doubtTransactionRemark;
	}

	public void setDoubtTransactionRemark(String doubtTransactionRemark) {
		this.doubtTransactionRemark = doubtTransactionRemark;
	}

	public String getApproveEmpNo() {
		return approveEmpNo;
	}

	public void setApproveEmpNo(String approveEmpNo) {
		this.approveEmpNo = approveEmpNo;
	}

	public String getApproveDeptNo() {
		return approveDeptNo;
	}

	public void setApproveDeptNo(String approveDeptNo) {
		this.approveDeptNo = approveDeptNo;
	}

	public List<FillFormDTO> getFillFormList() {
		return fillFormList;
	}

	public void setFillFormList(List<FillFormDTO> fillFormList) {
		this.fillFormList = fillFormList;
	}

    public String getExamType() {
		return examType;
	}

	public void setExamType(String examType) {
		this.examType = examType;
	}
	/* edit by gaojiaming end */    

	/* 构造函数  */
	public PosApplyBatchDTO() {
		applyFiles.add(new PosApplyFilesDTO());
	}

	public PosApplyBatchDTO(String posBatchNo) {
		this.posBatchNo = posBatchNo;
		applyFiles.add(new PosApplyFilesDTO());
	}
	
	/* 特殊存取 */
	
	/**
	 * 取得所有的条码
	 * 
	 * @return
	 */
	public List<CodeTableItemDTO> getBarcodeNoList() {
		List<CodeTableItemDTO> barcodeNoList = new ArrayList<CodeTableItemDTO>();
		Set<String> barcodeNoSet = new HashSet<String>();
		for (PosApplyFilesDTO files : applyFiles) {
			String barcodeNo = files.getBarcodeNo();
			if(!barcodeNoSet.contains(barcodeNo)) {
				barcodeNoSet.add(barcodeNo);
				CodeTableItemDTO selectOption = new CodeTableItemDTO(barcodeNo, barcodeNo);
				barcodeNoList.add(selectOption);
			}
		}
		return barcodeNoList;
	}
	
	public void setBarcodeNoList(List<CodeTableItemDTO> value) {
		//do nothding
	}

	/* 属性存取 */

	public String getPosBatchNo() {
		return this.posBatchNo;
	}

	public void setPosBatchNo(String value) {
		this.posBatchNo = value;
	}

	public String getAcceptor() {
		return this.acceptor;
	}

	public void setAcceptor(String value) {
		this.acceptor = value;
	}

	public String getCounterNo() {
		return this.counterNo;
	}

	public void setCounterNo(String value) {
		this.counterNo = value;
	}

	public String getApplyTypeCode() {
		return this.applyTypeCode;
	}

	public void setApplyTypeCode(String value) {
		this.applyTypeCode = value;
	}

	public String getAcceptChannelCode() {
		return this.acceptChannelCode;
	}

	public void setAcceptChannelCode(String value) {
		this.acceptChannelCode = value;
	}

	public String getClientNo() {
		return this.clientNo;
	}

	public void setClientNo(String value) {
		this.clientNo = value;
	}

	public String getRepresentor() {
		return this.representor;
	}

	public void setRepresentor(String value) {
		this.representor = value;
	}

	public String getIdType() {
		return this.idType;
	}

	public void setIdType(String value) {
		this.idType = value;
	}

	public String getRepresentIdno() {
		return this.representIdno;
	}

	public void setRepresentIdno(String value) {
		this.representIdno = value;
	}

	public String getBatchStatus() {
		return this.batchStatus;
	}

	public void setBatchStatus(String value) {
		this.batchStatus = value;
	}

	public String getPkSerial() {
		return this.pkSerial;
	}

	public void setPkSerial(String value) {
		this.pkSerial = value;
	}

	public List<PosInfoDTO> getPosInfoList() {
		return posInfoList;
	}

	public void setPosInfoList(List<PosInfoDTO> posInfoList) {
		this.posInfoList = posInfoList;
	}

	public List<PosApplyFilesDTO> getApplyFiles() {
		return applyFiles;
	}

	public void setApplyFiles(List<PosApplyFilesDTO> applyFiles) {
		this.applyFiles = applyFiles;
	}

	public ClientInformationDTO getClientInfo() {
		return clientInfo;
	}

	public void setClientInfo(ClientInformationDTO clientInfo) {
		this.clientInfo = clientInfo;
	}

	public String getEmpNo() {
		return empNo;
	}

	public void setEmpNo(String empNo) {
		this.empNo = empNo;
	}

	public List<String> getClientPolicyNoList() {
		return clientPolicyNoList;
	}

	public void setClientPolicyNoList(List<String> clientPolicyNoList) {
		this.clientPolicyNoList = clientPolicyNoList;
	}

	public List<PosInfoDTO> getNotCompletePosInfoList() {
		return notCompletePosInfoList;
	}

	public void setNotCompletePosInfoList(List<PosInfoDTO> notCompletePosInfoList) {
		this.notCompletePosInfoList = notCompletePosInfoList;
	}
}
