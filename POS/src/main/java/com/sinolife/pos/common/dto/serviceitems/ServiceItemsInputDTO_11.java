package com.sinolife.pos.common.dto.serviceitems;

import java.util.List;

import net.unihub.framework.util.common.MathUtil;

import org.springframework.validation.Errors;

import com.sinolife.pos.common.dto.PolicyDividendDTO;
import com.sinolife.pos.common.dto.PolicyDividendTransAccountDTO;


/**
 * 11 红利领取
 */
/**
 * @author yonghua.lei
 *
 */
public class ServiceItemsInputDTO_11 extends ServiceItemsInputDTO {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6909314174512960727L;

	/**
	 * 险种红利信息
	 */
	private List<PolicyDividendDTO> dividendList;
	
	/**
	 * 本次领取金额
	 */
	private String drawSum;
	
	/**
	 * 红利余额总和，即可以领取的最大值
	 */
	private String dividendBalSum;

	



	/**
	 * 红利领取操作类型(1现金领取，2转账授权)
	 */
	private String drawType;
	/**
	 * 红利转账授权信息
	 */
	private PolicyDividendTransAccountDTO  TransAccountDTO;


	private List<PolicyDividendTransAccountDTO> transList;
	 
	public List<PolicyDividendTransAccountDTO> getTransList() {
		return transList;
	}

	public void setTransList(List<PolicyDividendTransAccountDTO> transList) {
		this.transList = transList;
	}

	private String clientName;
	private String clientIdType;
	private String idNo;

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}




	public String getClientIdType() {
		return clientIdType;
	}

	public void setClientIdType(String clientIdType) {
		this.clientIdType = clientIdType;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO#validate(org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Errors err) {
		super.validate(err);
	}

	public List<PolicyDividendDTO> getDividendList() {
		return dividendList;
	}

	public void setDividendList(List<PolicyDividendDTO> dividendList) {
		this.dividendList = dividendList;
	}

	public String getDrawSum() {
		return drawSum;
	}

	public void setDrawSum(String drawSum) {
		this.drawSum = drawSum;
	}
	
	/**
	 * 计算可领红利余额总和
	 */
	public String countDividendBalSum() {
		double d = 0.0d;
		for (int i = 0; dividendList!=null && i < dividendList.size(); i++) {
			PolicyDividendDTO divid = dividendList.get(i);
			d += divid.getCashDividendBal().doubleValue();
			d += divid.getInterestDividendBal().doubleValue();
		}
	    d= MathUtil.setDoubleScale(d, 2);
		dividendBalSum = String.valueOf(d);
		return dividendBalSum;
	}

	public String getDividendBalSum() {
		return dividendBalSum;
	}

	public void setDividendBalSum(String dividendBalSum) {
		this.dividendBalSum = dividendBalSum;
	}


	public String getDrawType() {
		return drawType;
	}

	public void setDrawType(String drawType) {
		this.drawType = drawType;
	}

	public PolicyDividendTransAccountDTO getTransAccountDTO() {
		return TransAccountDTO;
	}

	public void setTransAccountDTO(PolicyDividendTransAccountDTO transAccountDTO) {
		TransAccountDTO = transAccountDTO;
	}


}
