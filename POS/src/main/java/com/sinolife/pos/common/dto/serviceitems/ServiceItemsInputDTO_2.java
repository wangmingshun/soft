package com.sinolife.pos.common.dto.serviceitems;

import java.util.List;

import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyProductDTO;


/**
 * 2 退保
 */
public class ServiceItemsInputDTO_2 extends ServiceItemsInputDTO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6034211757844503728L;

	/**
	 * 险种信息
	 */
	private List<PolicyProductDTO> productList;
	
	/**
	 * 退保原因 代码
	 */
	private String surrenderCause;
	
	/**
	 * 是否整单退保标志
	 */
	private boolean allSurrender;
	/**
	 * 是否可疑交易
	 */
	private boolean douBt;
	/**
	 * 是否可疑交易
	 */
	private String doubtList[];
	/**
	 * 保单暂收余额
	 */
	private String policyBalance;
	
	/**
	 * 保单贷款本息和
	 */
	private String loanSum;
	
	/**
	 * 保单自垫本息和
	 */
	private String aplSum;
	
	/**
	 * 保单已交保费和
	 */
	private String premPaid;
	
	//特殊件—协议退保（投诉业务）分公司承担比例
	private String branchPercent;
	
	public String getBranchPercent() {
		return branchPercent;
	}

	public void setBranchPercent(String branchPercent) {
		this.branchPercent = branchPercent;
	}

	/**
	 * 调用pkg用到的两辅助变量
	 */
	private String flag;
	private String message;
	
	private String applyBarCode;//契撤重投的新投保单号
	
	private String surrenderToNewApplyBarCode="";//退保金额转新投保单
	
	
     
	private String  canDoSurrenderToNewApplyBarCode;//能否操作退保金转新投保单
	
	
	private String  newApplyBarCodePrem="";//退保金转新投保单金额
	
	
	private double    policyCashValue;//保单实际退费金额
	
	
	public double getPolicyCashValue() {
		return policyCashValue;
	}

	public void setPolicyCashValue(double policyCashValue) {
		this.policyCashValue = policyCashValue;
	}

	public String getNewApplyBarCodePrem() {
		return newApplyBarCodePrem;
	}

	public void setNewApplyBarCodePrem(String newApplyBarCodePrem) {
		this.newApplyBarCodePrem = newApplyBarCodePrem;
	}

	public String getCanDoSurrenderToNewApplyBarCode() {
		return canDoSurrenderToNewApplyBarCode;
	}

	public void setCanDoSurrenderToNewApplyBarCode(
			String canDoSurrenderToNewApplyBarCode) {
		this.canDoSurrenderToNewApplyBarCode = canDoSurrenderToNewApplyBarCode;
	}

	

	
	public String getSurrenderToNewApplyBarCode() {
		return surrenderToNewApplyBarCode;
	}

	public void setSurrenderToNewApplyBarCode(String surrenderToNewApplyBarCode) {
		this.surrenderToNewApplyBarCode = surrenderToNewApplyBarCode;
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}

	public List<PolicyProductDTO> getProductList() {
		return productList;
	}

	public void setProductList(List<PolicyProductDTO> productList) {
		this.productList = productList;
	}

	public String getSurrenderCause() {
		return surrenderCause;
	}

	public void setSurrenderCause(String surrenderCause) {
		this.surrenderCause = surrenderCause;
	}

	public String getPolicyBalance() {
		return policyBalance;
	}

	public void setPolicyBalance(String policyBalance) {
		this.policyBalance = policyBalance;
	}

	public String getLoanSum() {
		return loanSum;
	}

	public void setLoanSum(String loanSum) {
		this.loanSum = loanSum;
	}

	public String getAplSum() {
		return aplSum;
	}

	public void setAplSum(String aplSum) {
		this.aplSum = aplSum;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isAllSurrender() {
		return allSurrender;
	}

	public void setAllSurrender(boolean allSurrender) {
		this.allSurrender = allSurrender;
	}

	public boolean isDouBt() {
		return douBt;
	}

	public void setDouBt(boolean douBt) {
		this.douBt = douBt;
	}

	public String[] getDoubtList() {
		return doubtList;
	}

	public void setDoubtList(String[] doubtList) {
		this.doubtList = doubtList;
	}

	public String getApplyBarCode() {
		return applyBarCode;
	}

	public void setApplyBarCode(String applyBarCode) {
		this.applyBarCode = applyBarCode;
	}

	public String getPremPaid() {
		return premPaid;
	}

	public void setPremPaid(String premPaid) {
		this.premPaid = premPaid;
	}
	
	

}
