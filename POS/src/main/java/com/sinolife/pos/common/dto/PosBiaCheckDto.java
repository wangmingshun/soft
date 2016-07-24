package com.sinolife.pos.common.dto;

import java.util.Date;

import javax.persistence.Entity;
@Entity
public class PosBiaCheckDto extends PosComDTO {
private static final long serialVersionUID = 5454155825314635342L;
	
	/* 表结构属性 */
	private String policy_no;				//保单号
	private String pos_no;				   //保全号
	private String pkSerial;				//数据主键
	private String service_items ;        //保全项目
	private Date accept_date;             //受理时间  
	private String barcode_no;       //条形码
	private Double totel_amount;       //收付总额
	private String prem_flag  ;       //收付标志
	
	/* 构造函数  */
	
	public PosBiaCheckDto() {
	}

	public PosBiaCheckDto(String policy_no, String pos_no,String service_items,Date accept_date,String barcode_no ,Double totel_amount,String prem_flag) {
		this.policy_no = policy_no;
		this.pos_no = pos_no;
		this.service_items = service_items;
		this.accept_date = accept_date;
		this.barcode_no = barcode_no;
		this.totel_amount = totel_amount;
		this.prem_flag = prem_flag;
	}
	/* 属性存取 */
	public String getPolicy_no() {
		return policy_no;
	}

	public void setPolicy_no(String policy_no) {
		this.policy_no = policy_no;
	}

	public String getPos_no() {
		return pos_no;
	}

	public void setPos_no(String pos_no) {
		this.pos_no = pos_no;
	}

	public String getService_items() {
		return service_items;
	}

	public void setService_items(String service_items) {
		this.service_items = service_items;
	}

	public Date getAccept_date() {
		return accept_date;
	}

	public void setAccept_date(Date accept_date) {
		this.accept_date = accept_date;
	}

	public String getBarcode_no() {
		return barcode_no;
	}

	public void setBarcode_no(String barcode_no) {
		this.barcode_no = barcode_no;
	}

	public Double getTotel_amount() {
		return totel_amount;
	}

	public void setTotel_amount(Double totel_amount) {
		this.totel_amount = totel_amount;
	}

	public String getPrem_flag() {
		return prem_flag;
	}

	public void setPrem_flag(String prem_flag) {
		this.prem_flag = prem_flag;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getPkSerial() {
		return pkSerial;
	}

	public void setPkSerial(String pkSerial) {
		this.pkSerial = pkSerial;
	}


	
	
}
