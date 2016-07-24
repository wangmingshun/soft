package com.sinolife.pos.setting.user.privilege.dto;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.setting.user.moneyapproval.dto.PosAmountDaysPrivsDTO;

public class UserPrivilegeInfoDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2131765789806640772L;

	/**
	 * 用户的金额天数等级
	 */
	private PosAmountDaysPrivsDTO amountDaysPrivsDTO;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 用户名称
	 */
	private String userName;

	/**
	 * 用户职级
	 */
	private String rankCode;

	/**
	 * 用户上级
	 */
	private String upperUserId;

	/**
	 * 用户所在柜面
	 */
	private String counterNo;

	/**
	 * 用户审批等级
	 */
	private String amountDaysGrade;

	/**
	 * 用户录入等级
	 */
	private String inputGrade;

	/**
	 * 用户预审权限
	 */
	private String inquiryPrivs;

	/**
	 * 用户异地权限
	 */
	private String differentPlacePrivs;

	/**
	 * 用户审批规则特殊件权限
	 */
	private String aprovRulePrivs;

	/**
	 * 用户审批受理撤销回退的权限
	 */
	private String aprovRollbackPrivs;

	/**
	 * 用户录入项目和特殊件权限 两种内容的混在一起的,后者加了个前缀"S_"和后缀"_保全项目编号"
	 */
	private String serviceItems;

	/**
	 * 录入项目权限的数组,由serviceItems变化来的
	 */
	private String[] serviceItemArr;

	/**
	 * 单个的录入项目，用于逐单写表
	 */
	private String serviceItem;

	/**
	 * 用户审批功能特殊件权限字符串
	 */
	private String aprovFuncSpecials;

	/**
	 * 审批功能特殊件权限的数组,由aprovFuncSpecials变化来的
	 */
	private String[] aprovFuncSpecialArr;

	/**
	 * 单个的审批功能特殊件权限，用于逐单写表
	 */
	private String aprovFuncSpecial;

	/**
	 * 审批权限类型 1-规则，2-功能，3-撤销回退
	 */
	private String specType;

	/*
	 * 移动保全权限设置
	 */
	private String mposPrivs;

	/*
	 * 银保前置保全处理员权限
	 */

	private String biaTransFailPrivs;
	
	/*
	 * 移动终端权限
	 */

	private String epointPrivs;
	/*
	 * 电销保单续期保费退费权限
	 */
	private String returnpremPrivs;
	/*
	 * 退信处理权限
	 */
	private String  posNoteReturnPrivs;


	public String getPosNoteReturnPrivs() {
		return posNoteReturnPrivs;
	}

	public void setPosNoteReturnPrivs(String posNoteReturnPrivs) {
		this.posNoteReturnPrivs = posNoteReturnPrivs;
	}

	public String getBiaTransFailPrivs() {
		return biaTransFailPrivs;
	}

	public void setBiaTransFailPrivs(String biaTransFailPrivs) {
		this.biaTransFailPrivs = biaTransFailPrivs;
	}

	public String getMposPrivs() {
		return mposPrivs;
	}

	public void setMposPrivs(String mposPrivs) {
		this.mposPrivs = mposPrivs;
	}

	public PosAmountDaysPrivsDTO getAmountDaysPrivsDTO() {
		return amountDaysPrivsDTO;
	}

	public void setAmountDaysPrivsDTO(PosAmountDaysPrivsDTO amountDaysPrivsDTO) {
		this.amountDaysPrivsDTO = amountDaysPrivsDTO;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = PosUtils.parseInputUser(userId);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRankCode() {
		return rankCode;
	}

	public void setRankCode(String rankCode) {
		this.rankCode = rankCode;
	}

	public String getCounterNo() {
		return counterNo;
	}

	public void setCounterNo(String counterNo) {
		this.counterNo = counterNo;
	}

	public String getDifferentPlacePrivs() {
		return differentPlacePrivs;
	}

	public void setDifferentPlacePrivs(String differentPlacePrivs) {
		this.differentPlacePrivs = differentPlacePrivs;
	}

	public String getServiceItems() {
		return serviceItems;
	}

	public void setServiceItems(String serviceItems) {
		this.serviceItems = serviceItems;
		this.serviceItemArr = serviceItems.split(",");
	}

	public String[] getServiceItemArr() {
		return serviceItemArr;
	}

	public void setServiceItemArr(String[] serviceItemArr) {
		this.serviceItemArr = serviceItemArr;
	}

	public String getServiceItem() {
		return serviceItem;
	}

	public void setServiceItem(String serviceItem) {
		this.serviceItem = serviceItem;
	}

	public String getUpperUserId() {
		return upperUserId;
	}

	public void setUpperUserId(String upperUserId) {
		this.upperUserId = PosUtils.parseInputUser(upperUserId);
	}

	public String toString() {
		return PosUtils.describBeanAsJSON(this);
	}

	public String getAprovRulePrivs() {
		return aprovRulePrivs;
	}

	public void setAprovRulePrivs(String aprovRulePrivs) {
		this.aprovRulePrivs = aprovRulePrivs;
	}

	public String getAprovFuncSpecials() {
		return aprovFuncSpecials;
	}

	public void setAprovFuncSpecials(String aprovFuncSpecials) {
		this.aprovFuncSpecials = aprovFuncSpecials;
		this.aprovFuncSpecialArr = aprovFuncSpecials.split(",");
	}

	public String[] getAprovFuncSpecialArr() {
		return aprovFuncSpecialArr;
	}

	public void setAprovFuncSpecialArr(String[] aprovFuncSpecialArr) {
		this.aprovFuncSpecialArr = aprovFuncSpecialArr;
	}

	public String getAprovFuncSpecial() {
		return aprovFuncSpecial;
	}

	public void setAprovFuncSpecial(String aprovFuncSpecial) {
		this.aprovFuncSpecial = aprovFuncSpecial;
	}

	public String getAprovRollbackPrivs() {
		return aprovRollbackPrivs;
	}

	public void setAprovRollbackPrivs(String aprovRollbackPrivs) {
		this.aprovRollbackPrivs = aprovRollbackPrivs;
	}

	public String getSpecType() {
		return specType;
	}

	public void setSpecType(String specType) {
		this.specType = specType;
	}

	public String getAmountDaysGrade() {
		return amountDaysGrade;
	}

	public void setAmountDaysGrade(String amountDaysGrade) {
		this.amountDaysGrade = amountDaysGrade;
	}

	public String getInputGrade() {
		return inputGrade;
	}

	public void setInputGrade(String inputGrade) {
		this.inputGrade = inputGrade;
	}

	public String getInquiryPrivs() {
		if (StringUtils.isBlank(inquiryPrivs)) {
			inquiryPrivs = "N";// 表结构该字段非空
		}
		return inquiryPrivs;
	}

	public void setInquiryPrivs(String inquiryPrivs) {
		this.inquiryPrivs = inquiryPrivs;
	}

	public String getEpointPrivs() {
		return epointPrivs;
	}

	public void setEpointPrivs(String epointPrivs) {
		this.epointPrivs = epointPrivs;
	}

	public String getReturnpremPrivs() {
		return returnpremPrivs;
	}

	public void setReturnpremPrivs(String returnpremPrivs) {
		this.returnpremPrivs = returnpremPrivs;
	}


}
