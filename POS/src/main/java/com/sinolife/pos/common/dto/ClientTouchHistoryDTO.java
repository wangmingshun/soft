/*
 * Powered By sino-life
 * Web Site: http://www.sino-life.com
 * Since 2010 - 2011
 */

package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 表名和字段名前加双引号，是因为当这些标识是oralce中的关键字时，会报错，加上双引号是防止出错
 */
@Entity
@Table(name = "\"CLIENT_TOUCH_HISTORY\"")
public class ClientTouchHistoryDTO implements Serializable {
	private static final long serialVersionUID = 5454155825314635342L;

	// 别名，页面中使用
	public static final String TABLE_ALIAS = "客户接触历史表";
	public static final String ALIAS_CLIENT_NO = "客户号";
	public static final String ALIAS_TOUCH_DATE = "接触日期";
	public static final String ALIAS_BUSINESS_NO = "业务号码";
	public static final String ALIAS_BUSINESS_NO_TYPE = "业务号类型";
	public static final String ALIAS_CHANNEL_CODE = "渠道代码";
	public static final String ALIAS_TOUCH_CONTENT = "接触内容";
	public static final String ALIAS_PK_SERIAL = "接触流水号（写日志用）";
	public static final String ALIAS_CREATED_USER = "创建人";
	public static final String ALIAS_CREATED_DATE = "创建时间";
	public static final String ALIAS_UPDATED_USER = "更新人";
	public static final String ALIAS_UPDATED_DATE = "更新时间";

	// 属性
	// columns START
	/**
	 * 客户号 db_column: CLIENT_NO
	 */
	private String clientNo;
	/**
	 * 接触日期 db_column: TOUCH_DATE
	 */
	@DateTimeFormat(pattern = "yyyy-dd-MM")
	private Date touchDate;
	/**
	 * 业务号码 db_column: BUSINESS_NO
	 */
	private String businessNo;
	/**
	 * 业务号类型 db_column: BUSINESS_NO_TYPE
	 */
	private String businessNoType;
	/**
	 * 渠道代码 db_column: CHANNEL_CODE
	 */
	private String channelCode;
	/**
	 * 接触内容 db_column: TOUCH_CONTENT
	 */
	private String touchContent;
	/**
	 * 接触流水号（写日志用） db_column: PK_SERIAL
	 */
	private String pkSerial;
	/**
	 * 创建人 db_column: CREATED_USER
	 */
	private String createdUser;
	/**
	 * 创建时间 db_column: CREATED_DATE
	 */
	@DateTimeFormat(pattern = "yyyy-dd-MM")
	private Date createdDate;
	/**
	 * 更新人 db_column: UPDATED_USER
	 */
	private String updatedUser;
	/**
	 * 更新时间 db_column: UPDATED_DATE
	 */
	@DateTimeFormat(pattern = "yyyy-dd-MM")
	private Date updatedDate;

	// columns END
	// 构造函数

	public ClientTouchHistoryDTO() {
	}

	public ClientTouchHistoryDTO(String clientNo, Date touchDate) {
		this.clientNo = clientNo;
		this.touchDate = touchDate;
	}

	// 默认主键自动生成，具体开发中需要开发人员根据实际情况进行适当修改

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "\"CLIENT_NO\"", unique = false, nullable = false)
	public String getClientNo() {
		return this.clientNo;
	}

	public void setClientNo(String value) {
		this.clientNo = value;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "\"TOUCH_DATE\"", unique = false, nullable = false)
	public Date getTouchDate() {
		return this.touchDate;
	}

	public void setTouchDate(Date value) {
		this.touchDate = value;
	}

	// 非主键getter setter方法生成

	@Column(name = "\"BUSINESS_NO\"", unique = false, nullable = true)
	public String getBusinessNo() {
		return this.businessNo;
	}

	public void setBusinessNo(String value) {
		this.businessNo = value;
	}

	@Column(name = "\"BUSINESS_NO_TYPE\"", unique = false, nullable = true)
	public String getBusinessNoType() {
		return this.businessNoType;
	}

	public void setBusinessNoType(String value) {
		this.businessNoType = value;
	}

	@Column(name = "\"CHANNEL_CODE\"", unique = false, nullable = false)
	public String getChannelCode() {
		return this.channelCode;
	}

	public void setChannelCode(String value) {
		this.channelCode = value;
	}

	@Column(name = "\"TOUCH_CONTENT\"", unique = false, nullable = true)
	public String getTouchContent() {
		return this.touchContent;
	}

	public void setTouchContent(String value) {
		this.touchContent = value;
	}

	@Column(name = "\"PK_SERIAL\"", unique = false, nullable = false)
	public String getPkSerial() {
		return this.pkSerial;
	}

	public void setPkSerial(String value) {
		this.pkSerial = value;
	}

	@Column(name = "\"CREATED_USER\"", unique = false, nullable = false)
	public String getCreatedUser() {
		return this.createdUser;
	}

	public void setCreatedUser(String value) {
		this.createdUser = value;
	}

	@Column(name = "\"CREATED_DATE\"", unique = false, nullable = false)
	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date value) {
		this.createdDate = value;
	}

	@Column(name = "\"UPDATED_USER\"", unique = false, nullable = false)
	public String getUpdatedUser() {
		return this.updatedUser;
	}

	public void setUpdatedUser(String value) {
		this.updatedUser = value;
	}

	@Column(name = "\"UPDATED_DATE\"", unique = false, nullable = false)
	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date value) {
		this.updatedDate = value;
	}

	/*
	 * 代码自动生成一对多的关系，处于注释掉状态，实际开发中可以去掉注释或者删掉该部分内容
	 */

	/* 非标属性 */
	private String businessNoTypeDesc;
	private String channelDesc;

	public String getBusinessNoTypeDesc() {
		return businessNoTypeDesc;
	}

	public void setBusinessNoTypeDesc(String businessNoTypeDesc) {
		this.businessNoTypeDesc = businessNoTypeDesc;
	}

	public String getChannelDesc() {
		return channelDesc;
	}

	public void setChannelDesc(String channelDesc) {
		this.channelDesc = channelDesc;
	}

}