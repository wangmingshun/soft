package com.sinolife.pos.common.dto.serviceitems;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.Errors;


/**
 * 32 提前满期申请
 */

public class ServiceItemsInputDTO_32 extends ServiceItemsInputDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4720914931937937673L;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date payToDate;				//交至日
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date effectDate;			//生效日期
	private String insuredName;			//被保人姓名
	private String applicantName;		//投保人姓名
	private String policyStatusDesc;	//保单状态
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date maturityDate;			//满期日期 
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date advancedMaturityDate;	//提前满期日期
	private String productCode;			//产品代码
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}
	
	
	@Override
	protected void onPropertyChange() {
		//只有CIAN_FR1险种允许录入提前满期日期
		if(!"CIAN_FR1".equals(productCode)) {
			advancedMaturityDate = null;
		}
	}

	/* getters and setters */

	public Date getAdvancedMaturityDate() {
		return advancedMaturityDate;
	}

	public void setAdvancedMaturityDate(Date advancedMaturityDate) {
		this.advancedMaturityDate = advancedMaturityDate;
	}

	public Date getPayToDate() {
		return payToDate;
	}

	public void setPayToDate(Date payToDate) {
		this.payToDate = payToDate;
	}

	public Date getEffectDate() {
		return effectDate;
	}

	public void setEffectDate(Date effectDate) {
		this.effectDate = effectDate;
	}

	public String getInsuredName() {
		return insuredName;
	}

	public void setInsuredName(String insuredName) {
		this.insuredName = insuredName;
	}

	public String getApplicantName() {
		return applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public String getPolicyStatusDesc() {
		return policyStatusDesc;
	}

	public void setPolicyStatusDesc(String policyStatusDesc) {
		this.policyStatusDesc = policyStatusDesc;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	
}
