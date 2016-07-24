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
@Table(name = "\"CLIENT_QUALITY\"")
public class ClientQualityDTO implements Serializable {
	private static final long serialVersionUID = 5454155825314635342L;

	// 别名，页面中使用
	public static final String TABLE_ALIAS = "客户品质信息表";
	public static final String ALIAS_CLIENT_NO = "客户号";
	public static final String ALIAS_START_DATE = "开始日期";
	public static final String ALIAS_CLIENT_QUALITY_CODE = "客户品质代码";
	public static final String ALIAS_END_DATE = "结束日期";
	public static final String ALIAS_PK_SERIAL = "品质代码流水号（写日志用）";
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
	 * 开始日期 db_column: START_DATE
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startDate;
	/**
	 * 客户品质代码 db_column: CLIENT_QUALITY_CODE
	 */
	private String clientQualityCode;
	/**
	 * 结束日期 db_column: END_DATE
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endDate;
	/**
	 * 品质代码流水号（写日志用） db_column: PK_SERIAL
	 */
	private String pkSerial;
	/**
	 * 创建人 db_column: CREATED_USER
	 */
	private String createdUser;
	/**
	 * 创建时间 db_column: CREATED_DATE
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdDate;
	/**
	 * 更新人 db_column: UPDATED_USER
	 */
	private String updatedUser;
	/**
	 * 更新时间 db_column: UPDATED_DATE
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date updatedDate;

	// columns END
	// 构造函数

	public ClientQualityDTO() {
	}

	public ClientQualityDTO(String clientNo, Date startDate) {
		this.clientNo = clientNo;
		this.startDate = startDate;
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
	@Column(name = "\"START_DATE\"", unique = false, nullable = false)
	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date value) {
		this.startDate = value;
	}

	// 非主键getter setter方法生成

	@Column(name = "\"CLIENT_QUALITY_CODE\"", unique = false, nullable = false)
	public String getClientQualityCode() {
		return this.clientQualityCode;
	}

	public void setClientQualityCode(String value) {
		this.clientQualityCode = value;
	}

	@Column(name = "\"END_DATE\"", unique = false, nullable = true)
	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date value) {
		this.endDate = value;
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
	/*
	 * 代码自动生成多对一的关系，处于注释掉状态，实际开发中可以去掉注释或者删掉该部分内容
	 */

	/* 非表属性 */
	private String clientQualityDesc;
	private String behaviorItem;
	private Date occurDate;
	private String behaviorDesc;
	private String businessNoType;
	private String businessNo;
	
	public String getClientQualityDesc() {
		return clientQualityDesc;
	}

	public void setClientQualityDesc(String clientQualityDesc) {
		this.clientQualityDesc = clientQualityDesc;
	}

	public String getBehaviorItem() {
		return behaviorItem;
	}

	public void setBehaviorItem(String behaviorItem) {
		this.behaviorItem = behaviorItem;
	}

	public Date getOccurDate() {
		return occurDate;
	}

	public void setOccurDate(Date occurDate) {
		this.occurDate = occurDate;
	}

	public String getBusinessNoType() {
		return businessNoType;
	}

	public void setBusinessNoType(String businessNoType) {
		this.businessNoType = businessNoType;
	}

	public String getBusinessNo() {
		return businessNo;
	}

	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}

	public String getBehaviorDesc() {
		return behaviorDesc;
	}

	public void setBehaviorDesc(String behaviorDesc) {
		this.behaviorDesc = behaviorDesc;
	}
	
}
