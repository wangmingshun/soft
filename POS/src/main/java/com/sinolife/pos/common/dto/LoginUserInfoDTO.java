package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 当前登录用户的用户信息
 */

public class LoginUserInfoDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1875875023634056887L;
	private boolean posUser;						//是否为POS系统用户，即在POS用户权限设置中添加了该登录用户
	private boolean loginUserSupervisorExists;		//是否已经在系统中设置了上级主管
	
	private String loginUserID;						//登录用户ID
	private String loginUserName;					//登录用户姓名
	private String loginUserRankCode;				//登录用户职级代码
	private boolean loginUserHasDifferentPlacePrivs;//登录用户是否具有异地权限
	private String loginUserCounterNo;				//登录用户柜面号
	private String loginUserBranchCode;				//登录用户机构代码
	private String loginUserAmountDaysGrade;		//登录用户审批金额等级
	private String loginUserInputGrade;				//登录用户录入等级
	private String loginUserInquiryPrivs;			//登录用户预审权限
	
	private String supervisorID;					//上级主管ID
	private String supervisorName;					//上级主管姓名
	private String supervisorRankCode;				//上级主管职级代码
	private boolean supervisorHasDifferentPlacePrivs;//上级主管是否具有异地权限
	private String supervisorCounterNo;				//上级主管柜面号
	
	private List<String> privsServiceItems;			//用户有权限操作的保全项目列表
	
	
	private String loginUserUMBranchCode;			//登录用户的UM机构代码
	

	public boolean isPosUser() {
		return posUser;
	}

	public void setPosUser(boolean posUser) {
		this.posUser = posUser;
	}

	public boolean isLoginUserSupervisorExists() {
		return loginUserSupervisorExists;
	}

	public void setLoginUserSupervisorExists(boolean loginUserSupervisorExists) {
		this.loginUserSupervisorExists = loginUserSupervisorExists;
	}

	public String getLoginUserID() {
		return loginUserID;
	}

	public void setLoginUserID(String loginUserID) {
		this.loginUserID = loginUserID;
	}

	public String getLoginUserName() {
		return loginUserName;
	}

	public void setLoginUserName(String loginUserName) {
		this.loginUserName = loginUserName;
	}

	public String getLoginUserRankCode() {
		return loginUserRankCode;
	}

	public void setLoginUserRankCode(String loginUserRankCode) {
		this.loginUserRankCode = loginUserRankCode;
	}

	public boolean isLoginUserHasDifferentPlacePrivs() {
		return loginUserHasDifferentPlacePrivs;
	}

	public void setLoginUserHasDifferentPlacePrivs(
			boolean loginUserHasDifferentPlacePrivs) {
		this.loginUserHasDifferentPlacePrivs = loginUserHasDifferentPlacePrivs;
	}

	public String getSupervisorID() {
		return supervisorID;
	}

	public void setSupervisorID(String supervisorID) {
		this.supervisorID = supervisorID;
	}

	public String getSupervisorName() {
		return supervisorName;
	}

	public void setSupervisorName(String supervisorName) {
		this.supervisorName = supervisorName;
	}

	public String getSupervisorRankCode() {
		return supervisorRankCode;
	}

	public void setSupervisorRankCode(String supervisorRankCode) {
		this.supervisorRankCode = supervisorRankCode;
	}

	public boolean isSupervisorHasDifferentPlacePrivs() {
		return supervisorHasDifferentPlacePrivs;
	}

	public void setSupervisorHasDifferentPlacePrivs(
			boolean supervisorHasDifferentPlacePrivs) {
		this.supervisorHasDifferentPlacePrivs = supervisorHasDifferentPlacePrivs;
	}

	public List<String> getPrivsServiceItems() {
		return privsServiceItems;
	}

	public void setPrivsServiceItems(List<String> privsServiceItems) {
		this.privsServiceItems = privsServiceItems;
	}

	public String getLoginUserCounterNo() {
		return loginUserCounterNo;
	}

	public void setLoginUserCounterNo(String loginUserCounterNo) {
		this.loginUserCounterNo = loginUserCounterNo;
	}

	public String getSupervisorCounterNo() {
		return supervisorCounterNo;
	}

	public void setSupervisorCounterNo(String supervisorCounterNo) {
		this.supervisorCounterNo = supervisorCounterNo;
	}

	public String getLoginUserBranchCode() {
		return loginUserBranchCode;
	}

	public void setLoginUserBranchCode(String loginUserBranchCode) {
		this.loginUserBranchCode = loginUserBranchCode;
	}

	public String getLoginUserAmountDaysGrade() {
		return loginUserAmountDaysGrade;
	}

	public void setLoginUserAmountDaysGrade(String loginUserAmountDaysGrade) {
		this.loginUserAmountDaysGrade = loginUserAmountDaysGrade;
	}

	public String getLoginUserInputGrade() {
		return loginUserInputGrade;
	}

	public void setLoginUserInputGrade(String loginUserInputGrade) {
		this.loginUserInputGrade = loginUserInputGrade;
	}

	public String getLoginUserInquiryPrivs() {
		return loginUserInquiryPrivs;
	}

	public void setLoginUserInquiryPrivs(String loginUserInquiryPrivs) {
		this.loginUserInquiryPrivs = loginUserInquiryPrivs;
	}

	public String getLoginUserUMBranchCode() {
		return loginUserUMBranchCode;
	}

	public void setLoginUserUMBranchCode(String loginUserUMBranchCode) {
		this.loginUserUMBranchCode = loginUserUMBranchCode;
	}
	
}
