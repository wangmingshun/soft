package com.sinolife.pos.pubInterface.biz.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class PosApplyInfoDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String clientNo;
	private String policyNo;
	private String serviceItems;
	private String acceptor;
	private String acceptChannelCode;
	private Date applyDate;
	private String applyType;
	private String representor;
	private String representIdno;
	private String idType;
	private String approvalServiceType;
	private String address;
	private String zip;
	private String paymentType;
	private String bankCode;
	private String transferAccountno;
	private String transferAccountOwner;
	private String transferAccountOwnerIdType;
	private String transferAccountOwnerIdNo;
	private String barCode;//条形码
	private String accountNoType   ;         //银行卡类型
	private String is_wechat_attention;     //是否添加微信关注
	private String is_weixin_auto_add_prem;     //是否微信定投
	private String order_id; 
	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getIs_weixin_auto_add_prem() {
		return is_weixin_auto_add_prem;
	}

	public void setIs_weixin_auto_add_prem(String is_weixin_auto_add_prem) {
		this.is_weixin_auto_add_prem = is_weixin_auto_add_prem;
	}
	private String deptNo;//部门代码
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

	public String getDeptNo() {
		return deptNo;
	}

	public void setDeptNo(String deptNo) {
		this.deptNo = deptNo;
	}
	
	public String getIs_wechat_attention() {
		return is_wechat_attention;
	}
	public void setIs_wechat_attention(String is_wechat_attention) {
		this.is_wechat_attention = is_wechat_attention;
	}
	public String getAccountNoType() {
		return accountNoType;
	}
	public void setAccountNoType(String accountNoType) {
		this.accountNoType = accountNoType;
	}
	public String getClientNo() {
		return clientNo;
	}
	public void setClientNo(String clientNo) {
		this.clientNo = clientNo;
	}
	public String getPolicyNo() {
		return policyNo;
	}
	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}
	public String getServiceItems() {
		return serviceItems;
	}
	public void setServiceItems(String serviceItems) {
		this.serviceItems = serviceItems;
	}
	public String getAcceptor() {
		return acceptor;
	}
	public void setAcceptor(String acceptor) {
		this.acceptor = acceptor;
	}
	public String getAcceptChannelCode() {
		return acceptChannelCode;
	}
	public void setAcceptChannelCode(String acceptChannelCode) {
		this.acceptChannelCode = acceptChannelCode;
	}
	public Date getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}
	public String getApplyType() {
		return applyType;
	}
	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}
	public String getRepresentor() {
		return representor;
	}
	public void setRepresentor(String representor) {
		this.representor = representor;
	}
	public String getRepresentIdno() {
		return representIdno;
	}
	public void setRepresentIdno(String representIdno) {
		this.representIdno = representIdno;
	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getApprovalServiceType() {
		return approvalServiceType;
	}
	public void setApprovalServiceType(String approvalServiceType) {
		this.approvalServiceType = approvalServiceType;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getTransferAccountno() {
		return transferAccountno;
	}
	public void setTransferAccountno(String transferAccountno) {
		this.transferAccountno = transferAccountno;
	}
	public String getTransferAccountOwner() {
		return transferAccountOwner;
	}
	public void setTransferAccountOwner(String transferAccountOwner) {
		this.transferAccountOwner = transferAccountOwner;
	}
	public String getTransferAccountOwnerIdType() {
		return transferAccountOwnerIdType;
	}
	public void setTransferAccountOwnerIdType(String transferAccountOwnerIdType) {
		this.transferAccountOwnerIdType = transferAccountOwnerIdType;
	}
	public String getTransferAccountOwnerIdNo() {
		return transferAccountOwnerIdNo;
	}
	public void setTransferAccountOwnerIdNo(String transferAccountOwnerIdNo) {
		this.transferAccountOwnerIdNo = transferAccountOwnerIdNo;
	}
	public String getBarCode() {
		return barCode;
	}
	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}
	
}
