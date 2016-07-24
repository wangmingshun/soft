package com.sinolife.pos.setting.user.moneyapproval.dto;

import java.io.Serializable;

import com.sinolife.pos.common.util.PosUtils;

public class PosAmountDaysPrivsDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5688715855080002459L;
	
	/**
	 * 金额天数等级
	 */
	private String amountDaysGrade;
	
	/**
	 * 服务项目";"分割的字符串
	 */
	private String serviceItemStr;
	
	/**
	 * 服务项目的数组,由serviceItemStr变化来的
	 */
	private String[] serviceItems;
	
	/**
	 * 单个服务项目
	 */
	private String serviceItemOdd;
	
	/**
	 * 受理渠道
	 */
	private String acceptChannel;
	
	/**
	 * 转账金额（原账户）
	 */
	private String sourceTransferSum;
	
	/**
	 * 转账金额（非原账户）
	 */
	private String notsourceTransferSum;
	
	/**
	 * 代办现金金额
	 */
	private String notselfCashSum;
	
	/**
	 * 亲办现金金额
	 */
	private String selfCashSum;
	
	/**
	 * 协议超出金额
	 */
	private String treatyExceedSum;
	
	/**
	 * 免息金额
	 */
	private String interestFreeSum;
	
	/**
	 * 签回天数
	 */
	private String signDays;

	public String getAmountDaysGrade() {
		return amountDaysGrade;
	}

	public void setAmountDaysGrade(String amountDaysGrade) {
		this.amountDaysGrade = amountDaysGrade;
	}

	public String getServiceItemStr() {
		return serviceItemStr;
	}

	public void setServiceItemStr(String serviceItemStr) {
		this.serviceItemStr = serviceItemStr;
		this.serviceItems = serviceItemStr.split(",");
	}

	public String[] getServiceItems() {
		return serviceItems;
	}

	public void setServiceItems(String[] serviceItems) {
		this.serviceItems = serviceItems;
	}
	
	public String getAcceptChannel() {
		return acceptChannel;
	}

	public void setAcceptChannel(String acceptChannel) {
		this.acceptChannel = acceptChannel;
	}

	public String getSourceTransferSum() {
		return sourceTransferSum;
	}

	public void setSourceTransferSum(String sourceTransferSum) {
		this.sourceTransferSum = sourceTransferSum;
	}

	public String getNotsourceTransferSum() {
		return notsourceTransferSum;
	}

	public void setNotsourceTransferSum(String notsourceTransferSum) {
		this.notsourceTransferSum = notsourceTransferSum;
	}

	public String getNotselfCashSum() {
		return notselfCashSum;
	}

	public void setNotselfCashSum(String notselfCashSum) {
		this.notselfCashSum = notselfCashSum;
	}

	public String getSelfCashSum() {
		return selfCashSum;
	}

	public void setSelfCashSum(String selfCashSum) {
		this.selfCashSum = selfCashSum;
	}

	public String getTreatyExceedSum() {
		return treatyExceedSum;
	}

	public void setTreatyExceedSum(String treatyExceedSum) {
		this.treatyExceedSum = treatyExceedSum;
	}

	public String getInterestFreeSum() {
		return interestFreeSum;
	}

	public void setInterestFreeSum(String interestFreeSum) {
		this.interestFreeSum = interestFreeSum;
	}

	public String getSignDays() {
		return signDays;
	}

	public void setSignDays(String signDays) {
		this.signDays = signDays;
	}

	public String getServiceItemOdd() {
		return serviceItemOdd;
	}

	public void setServiceItemOdd(String serviceItemOdd) {
		this.serviceItemOdd = serviceItemOdd;
	}
	
	public String toString(){
		return PosUtils.describBeanAsJSON(this);
	}
	
}
