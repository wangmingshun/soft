package com.sinolife.pos.common.dto.serviceitems;

import java.util.Date;
import java.util.List;

import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyProductDTO;



/**
 * 1 犹豫期退保
 */

public class ServiceItemsInputDTO_1 extends ServiceItemsInputDTO {

	private static final long serialVersionUID = -2242639919406043854L;
	private List<PolicyProductDTO> policyProductList;		//险种信息
	private String surrenderCauseCode;						//退保原因代码
	private Date receiptPutoffDate;							//保单报备日期
	/**
	 * 是否可疑交易
	 */
	private boolean douBt;
	/**
	 * 是否可疑交易
	 */
	private String doubtList[];
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}

	public List<PolicyProductDTO> getPolicyProductList() {
		return policyProductList;
	}

	public void setPolicyProductList(List<PolicyProductDTO> policyProductList) {
		this.policyProductList = policyProductList;
	}

	public String getSurrenderCauseCode() {
		return surrenderCauseCode;
	}

	public void setSurrenderCauseCode(String surrenderCauseCode) {
		this.surrenderCauseCode = surrenderCauseCode;
	}

	public Date getReceiptPutoffDate() {
		return receiptPutoffDate;
	}

	public void setReceiptPutoffDate(Date receiptPutoffDate) {
		this.receiptPutoffDate = receiptPutoffDate;
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
	
}
