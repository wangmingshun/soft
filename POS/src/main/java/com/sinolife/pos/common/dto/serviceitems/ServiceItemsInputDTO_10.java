package com.sinolife.pos.common.dto.serviceitems;

import java.util.List;

import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.PolicyBeneficiaryDTO;
import com.sinolife.pos.common.dto.PosSurvivalDueDTO;

/**
 * 10 生存保险金领取
 */
public class ServiceItemsInputDTO_10 extends ServiceItemsInputDTO {
	private static final long serialVersionUID = -7743473262006529048L;
	private List<PolicyBeneficiaryDTO> beneficiaryInfoList; // 受益人信息
	private List<PosSurvivalDueDTO> survivalDueList; // 生存金应领信息
	private String payType; // 领取方式
	private String toPolicyNo; // 转入保单号
	private String toApplyBarcode; // 转入投保单号
	private String benefitTypeSelected; // 选中的授权受益人类型
	private List<CodeTableItemDTO> beneficiaryTypeList; // 受益人类型
	private String productCodes; // 险种信息
	private String payDate; // 红满堂领取日期
	   
	private String  canDoSurrenderToNewApplyBarCode;//能否操作退保金转新投保单
	
	
	private String surrenderToNewApplyBarCode;//退保金额转新投保单
	
	
	public String getSurrenderToNewApplyBarCode() {
		return surrenderToNewApplyBarCode;
	}

	public void setSurrenderToNewApplyBarCode(String surrenderToNewApplyBarCode) {
		this.surrenderToNewApplyBarCode = surrenderToNewApplyBarCode;
	}

	private String  newApplyBarCodePrem;//退保金转新投保单金额

	public String getNewApplyBarCodePrem() {
		return newApplyBarCodePrem;
	}

	public void setNewApplyBarCodePrem(String newApplyBarCodePrem) {
		this.newApplyBarCodePrem = newApplyBarCodePrem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate
	 * (org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}

	@Override
	protected void onPropertyChange() {
		if (!"5".equals(payType)) {
			toPolicyNo = null;
		}

		if (!"6".equals(payType)) {
			toApplyBarcode = null;
		}

		if (beneficiaryInfoList != null && beneficiaryInfoList.size() > 0) {
			for (PolicyBeneficiaryDTO pb : beneficiaryInfoList) {
				if ("2".equals(payType)) {
					if (pb.isChecked()
							&& !"Y".equals(pb.getTransferGrantFlag())) {
						pb.setAccountNo(null);
						pb.setBankCode(null);
						pb.setBankName(null);
						pb.setAccountOwner(null);
					}
				} else {
					pb.setChecked(false);
				}
			}
		}
	}	

	public String getPayDate() {
		return payDate;
	}

	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}

	public String getProductCodes() {
		return productCodes;
	}

	public void setProductCodes(String productCodes) {
		this.productCodes = productCodes;
	}

	public List<PolicyBeneficiaryDTO> getBeneficiaryInfoList() {
		return beneficiaryInfoList;
	}

	public void setBeneficiaryInfoList(
			List<PolicyBeneficiaryDTO> beneficiaryInfoList) {
		this.beneficiaryInfoList = beneficiaryInfoList;
	}

	public List<PosSurvivalDueDTO> getSurvivalDueList() {
		return survivalDueList;
	}

	public void setSurvivalDueList(List<PosSurvivalDueDTO> survivalDueList) {
		this.survivalDueList = survivalDueList;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getToPolicyNo() {
		return toPolicyNo;
	}

	public void setToPolicyNo(String toPolicyNo) {
		this.toPolicyNo = toPolicyNo;
	}

	public String getToApplyBarcode() {
		return toApplyBarcode;
	}

	public void setToApplyBarcode(String toApplyBarcode) {
		this.toApplyBarcode = toApplyBarcode;
	}

	public String getBenefitTypeSelected() {
		return benefitTypeSelected;
	}

	public void setBenefitTypeSelected(String benefitTypeSelected) {
		this.benefitTypeSelected = benefitTypeSelected;
	}

	public List<CodeTableItemDTO> getBeneficiaryTypeList() {
		return beneficiaryTypeList;
	}

	public void setBeneficiaryTypeList(
			List<CodeTableItemDTO> beneficiaryTypeList) {
		this.beneficiaryTypeList = beneficiaryTypeList;
	}
}
