package com.sinolife.pos.common.dto.serviceitems;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyProductDTO;

/**
 * 5 新增附加险
 */
public class ServiceItemsInputDTO_5 extends ServiceItemsInputDTO {

	private static final long serialVersionUID = -5241791691586838580L;

	private List<PolicyProductDTO> productList; // 险种信息列表
	private int productSize;
	private String primaryDivFlag;              // 主险是否分红险
	private String productOption;               // 可选择进行新增的附加险,即主险可搭配的附加险,只读信息,这是个list转换成的json字符串
	private String leftPremTerm;                // 主险剩余缴费年期
	private String leftCoveragePeriod;          // 主险剩余保险年期

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

	public List<PolicyProductDTO> getProductList() {
		return productList;
	}

	public void setProductList(List<PolicyProductDTO> productList) {
		this.productList = productList;
	}

	public String getPrimaryDivFlag() {
		return primaryDivFlag;
	}

	public void setPrimaryDivFlag(String primaryDivFlag) {
		this.primaryDivFlag = primaryDivFlag;
	}

	public int getProductSize() {
		return productSize;
	}

	public void setProductSize(int productSize) {
		this.productSize = productSize;
	}

	public void onPropertyChange(){
		for (int i = 0; productSize>0 && productList!=null && i < productList.size(); i++) {
			//当原险种列表小于新增列表时，将导致新增列表没有保单号
			if(StringUtils.isBlank(productList.get(i).getPolicyNo())){
				productList.get(i).setPolicyNo(getPolicyNo());
			}
			
			//只保留新增的险种信息
			if(productList.size()>productSize){
				productList.remove(i);
				i--;
			}
		}
	}

	public String getProductOption() {
		return productOption;
	}

	public void setProductOption(String productOption) {
		this.productOption = productOption;
	}

	public String getLeftPremTerm() {
		return leftPremTerm;
	}

	public void setLeftPremTerm(String leftPremTerm) {
		this.leftPremTerm = leftPremTerm;
	}

	public String getLeftCoveragePeriod() {
		return leftCoveragePeriod;
	}

	public void setLeftCoveragePeriod(String leftCoveragePeriod) {
		this.leftCoveragePeriod = leftCoveragePeriod;
	}

}
