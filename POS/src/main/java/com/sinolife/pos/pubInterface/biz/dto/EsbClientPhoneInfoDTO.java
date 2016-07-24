package com.sinolife.pos.pubInterface.biz.dto;

import java.io.Serializable;
import java.util.Date;

public class EsbClientPhoneInfoDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String isCheck; // 是否选中（Y/N）
	private String clientNo; // 客户号
	private String clientName; // 姓名
	private String idType; // 证 件类型
	private String idNo; // 证件号
	private String sex; // 性别
	private String birthday; // 生日
	private String isVip; // 是否vip（Y/N）
	private String phoneNo; // 电话号码
	private String processor; // 停用操作员
	private Date processed_date; // 停用操作时间
	private Date rollbacked_date; // 恢复时间
	private String rollbacked_user; // 恢复人员
	private String batch_no; // 批次号

	public String getIsCheck() {
		return isCheck;
	}

	public void setIsCheck(String isCheck) {
		this.isCheck = isCheck;
	}

	public String getClientNo() {
		return clientNo;
	}

	public void setClientNo(String clientNo) {
		this.clientNo = clientNo;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getIsVip() {
		return isVip;
	}

	public void setIsVip(String isVip) {
		this.isVip = isVip;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getProcessor() {
		return processor;
	}

	public void setProcessor(String processor) {
		this.processor = processor;
	}

	public Date getProcessed_date() {
		return processed_date;
	}

	public void setProcessed_date(Date processed_date) {
		this.processed_date = processed_date;
	}

	public Date getRollbacked_date() {
		return rollbacked_date;
	}

	public void setRollbacked_date(Date rollbacked_date) {
		this.rollbacked_date = rollbacked_date;
	}

	public String getRollbacked_user() {
		return rollbacked_user;
	}

	public void setRollbacked_user(String rollbacked_user) {
		this.rollbacked_user = rollbacked_user;
	}

	public String getBatch_no() {
		return batch_no;
	}

	public void setBatch_no(String batch_no) {
		this.batch_no = batch_no;
	}

	@Override
	public String toString() {
		return "EsbClientPhoneInfoDTO [isCheck=" + isCheck + ", clientNo="
				+ clientNo + ", clientName=" + clientName + ", idType="
				+ idType + ", idNo=" + idNo + ", sex=" + sex + ", birthday="
				+ birthday + ", isVip=" + isVip + ", phoneNo=" + phoneNo
				+ ", processor=" + processor + ", processed_date="
				+ processed_date + ", rollbacked_date=" + rollbacked_date
				+ ", rollbacked_user=" + rollbacked_user + ", batch_no="
				+ batch_no + "]";
	}

}
