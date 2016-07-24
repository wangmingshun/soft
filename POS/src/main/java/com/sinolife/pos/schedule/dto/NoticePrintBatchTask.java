package com.sinolife.pos.schedule.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 通知书批次打印任务
 */
public class NoticePrintBatchTask implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String taskId;				//任务ID
	private String submitUserId;		//提交用户UM ID
	private String submitUserName;		//提交用户姓名
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date submitDate;			//提交日期
	private String UMBranchCode;		//提交用户UM机构代码
	private String queryNoticeType;		//查询通知书类型
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date queryDateStart;		//查询开始时间
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date queryDateEnd;			//查询结束时间
	private String queryBranchCode;		//查询机构代码
	private String queryBranchName;		//查询机构名称
	private String queryChannelCode;	//查询渠道
	private String queryPolicyChannelCode;   //保单渠道	
	private String queryUserId;			//查询用户ID
	private String queryServiceItems;	//查询保全项目
	private String needSendEmail;		//是否选择发送EMAIL Y发送 N不发送
	private String email;				//发送邮件地址
	private String noticeFileName;		//生成通知书的文件名
	private Long genCostTime;			//通知书生成耗时
	private Long genRecordNum;			//文件包含通知书的数目
	private String IMFileId;			//上传到IM的文件ID
	private Long fileSize;				//生成的文件大小
	private String taskStatus;			//任务状态：  1 新建 2 处理中(procTouchTime超过2h则认为任务被中断) 3 成功 4 失败，原因由taskFailReason记录
	private String taskFailReason;		//任务失败原因:  1 PDF生成失败 2 邮件发送失败
	private String procTouchTime;		//当taskStatus为2时，该字段表示最近活动时间, 任务状态更新时间
	private String procServerIP;		//任务处理的服务器IP
	private String validFlag;			//有效标志 Y有效 N无效
	private String queryApprovalServiceType; //批单寄送方式
	private String printStatus;			//打印状态
	

	public String getPrintStatus() {
		return printStatus;
	}
	public void setPrintStatus(String printStatus) {
		this.printStatus = printStatus;
	}
	public String getQueryPolicyChannelCode() {
		return queryPolicyChannelCode;
	}
	public void setQueryPolicyChannelCode(String queryPolicyChannelCode) {
		this.queryPolicyChannelCode = queryPolicyChannelCode;
	}
	public String getQueryApprovalServiceType() {
		return queryApprovalServiceType;
	}
	public void setQueryApprovalServiceType(String queryApprovalServiceType) {
		this.queryApprovalServiceType = queryApprovalServiceType;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getSubmitUserId() {
		return submitUserId;
	}
	public void setSubmitUserId(String submitUserId) {
		this.submitUserId = submitUserId;
	}
	public String getSubmitUserName() {
		return submitUserName;
	}
	public void setSubmitUserName(String submitUserName) {
		this.submitUserName = submitUserName;
	}
	public Date getSubmitDate() {
		return submitDate;
	}
	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}
	public String getUMBranchCode() {
		return UMBranchCode;
	}
	public void setUMBranchCode(String uMBranchCode) {
		UMBranchCode = uMBranchCode;
	}
	public String getQueryNoticeType() {
		return queryNoticeType;
	}
	public void setQueryNoticeType(String queryNoticeType) {
		this.queryNoticeType = queryNoticeType;
	}
	public Date getQueryDateStart() {
		return queryDateStart;
	}
	public void setQueryDateStart(Date queryDateStart) {
		this.queryDateStart = queryDateStart;
	}
	public Date getQueryDateEnd() {
		return queryDateEnd;
	}
	public void setQueryDateEnd(Date queryDateEnd) {
		this.queryDateEnd = queryDateEnd;
	}
	public String getQueryBranchCode() {
		return queryBranchCode;
	}
	public void setQueryBranchCode(String queryBranchCode) {
		this.queryBranchCode = queryBranchCode;
	}
	public String getQueryBranchName() {
		return queryBranchName;
	}
	public void setQueryBranchName(String queryBranchName) {
		this.queryBranchName = queryBranchName;
	}
	public String getNeedSendEmail() {
		return needSendEmail;
	}
	public void setNeedSendEmail(String needSendEmail) {
		this.needSendEmail = needSendEmail;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNoticeFileName() {
		return noticeFileName;
	}
	public void setNoticeFileName(String noticeFileName) {
		this.noticeFileName = noticeFileName;
	}
	public Long getGenCostTime() {
		return genCostTime;
	}
	public void setGenCostTime(Long genCostTime) {
		this.genCostTime = genCostTime;
	}
	public Long getGenRecordNum() {
		return genRecordNum;
	}
	public void setGenRecordNum(Long genRecordNum) {
		this.genRecordNum = genRecordNum;
	}
	public String getIMFileId() {
		return IMFileId;
	}
	public void setIMFileId(String iMFileId) {
		IMFileId = iMFileId;
	}
	public Long getFileSize() {
		return fileSize;
	}
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	public String getTaskStatus() {
		return taskStatus;
	}
	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}
	public String getTaskFailReason() {
		return taskFailReason;
	}
	public void setTaskFailReason(String taskFailReason) {
		this.taskFailReason = taskFailReason;
	}
	public String getProcTouchTime() {
		return procTouchTime;
	}
	public void setProcTouchTime(String procTouchTime) {
		this.procTouchTime = procTouchTime;
	}
	public String getProcServerIP() {
		return procServerIP;
	}
	public void setProcServerIP(String procServerIP) {
		this.procServerIP = procServerIP;
	}
	public String getValidFlag() {
		return validFlag;
	}
	public void setValidFlag(String validFlag) {
		this.validFlag = validFlag;
	}
	public String getQueryChannelCode() {
		return queryChannelCode;
	}
	public void setQueryChannelCode(String queryChannelCode) {
		this.queryChannelCode = queryChannelCode;
	}
	public String getQueryUserId() {
		return queryUserId;
	}
	public void setQueryUserId(String queryUserId) {
		this.queryUserId = queryUserId;
	}
	public String getQueryServiceItems() {
		return queryServiceItems;
	}
	public void setQueryServiceItems(String queryServiceItems) {
		this.queryServiceItems = queryServiceItems;
	}
}
