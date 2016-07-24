package com.sinolife.pos.common.dto;

import javax.persistence.Entity;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 表名和字段名前加双引号，是因为当这些标识是oralce中的关键字时，会报错，加上双引号是防止出错
 */
@Entity
public class PosAcceptDetailDTO extends PosComDTO {
	private static final long serialVersionUID = 5454155825314635342L;

	// 别名，页面中使用
	public static final String TABLE_ALIAS = "保全受理明细";
	public static final String ALIAS_POS_NO = "保全号";
	public static final String ALIAS_ACCEPT_DETAIL_NO = "受理明细号";
	public static final String ALIAS_POS_OBJECT = "保全变更对象";
	public static final String ALIAS_PROCESS_TYPE = "保全变更类型";
	public static final String ALIAS_OBJECT_VALUE = "对象值";
	public static final String ALIAS_ITEM_NO = "保全变更项";
	public static final String ALIAS_OLD_VALUE = "变更项旧值";
	public static final String ALIAS_NEW_VALUE = "变更项新值";
	public static final String ALIAS_CREATED_USER = "录入人";
	public static final String ALIAS_CREATED_DATE = "录入日期";
	public static final String ALIAS_UPDATED_USER = "修改人";
	public static final String ALIAS_UPDATED_DATE = "修改日期";
	public static final String ALIAS_PK_SERIAL = "数据主键";

	// 属性
	// columns START
	/**
	 * 保全号 db_column: POS_NO
	 */
	private String posNo;
	/**
	 * 受理明细号 db_column: ACCEPT_DETAIL_NO
	 */
	private String acceptDetailNo;
	/**
	 * 保全变更对象 db_column: POS_OBJECT
	 */
	private String posObject;
	/**
	 * 保全变更类型 db_column: PROCESS_TYPE
	 */
	private String processType;
	/**
	 * 对象值 db_column: OBJECT_VALUE
	 */
	private String objectValue;
	/**
	 * 保全变更项 db_column: ITEM_NO
	 */
	private String itemNo;
	/**
	 * 变更项旧值 db_column: OLD_VALUE
	 */
	private String oldValue;
	/**
	 * 变更项新值 db_column: NEW_VALUE
	 */
	private String newValue;
	/**
	 * 录入人 db_column: CREATED_USER
	 */
	private String createdUser;
	/**
	 * 录入日期 db_column: CREATED_DATE
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private java.util.Date createdDate;
	/**
	 * 修改人 db_column: UPDATED_USER
	 */
	private String updatedUser;
	/**
	 * 修改日期 db_column: UPDATED_DATE
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private java.util.Date updatedDate;
	/**
	 * 数据主键 db_column: PK_SERIAL
	 */
	private String pkSerial;

	private String infoGroupNo;
	private String queryPkSerial;
	private String queryString;
	private String changeTable;

	// columns END
	// 构造函数
	public PosAcceptDetailDTO() {
	}

	public PosAcceptDetailDTO(String posNo, String acceptDetailNo) {
		this.posNo = posNo;
		this.acceptDetailNo = acceptDetailNo;
	}

	/**
	 * 构造函数之一 
	 * 构造一个常用的detail格式就这7个参数
	 */
	public PosAcceptDetailDTO(String posNo, String posObject,
			String processType, String objectValue, String itemNo,
			String oldValue, String newValue) {
		this.posNo = posNo;
		this.posObject = posObject;
		this.processType = processType;
		this.objectValue = objectValue;
		this.itemNo = itemNo;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	/**
	 * 构造函数之二
	 */
	public PosAcceptDetailDTO(String posNo, String posObject,
			String processType, String objectValue, String itemNo,
			String oldValue, String newValue, String infoGroupNo) {
		this.posNo = posNo;
		this.posObject = posObject;
		this.processType = processType;
		this.objectValue = objectValue;
		this.itemNo = itemNo;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.infoGroupNo = infoGroupNo;
	}

	// 默认主键自动生成，具体开发中需要开发人员根据实际情况进行适当修改

	public String getPosNo() {
		return this.posNo;
	}

	public void setPosNo(String value) {
		this.posNo = value;
	}

	public String getAcceptDetailNo() {
		return this.acceptDetailNo;
	}

	public void setAcceptDetailNo(String value) {
		this.acceptDetailNo = value;
	}

	// 非主键getter setter方法生成

	public String getPosObject() {
		return this.posObject;
	}

	public void setPosObject(String value) {
		this.posObject = value;
	}

	public String getProcessType() {
		return this.processType;
	}

	public void setProcessType(String value) {
		this.processType = value;
	}

	public String getObjectValue() {
		return this.objectValue;
	}

	public void setObjectValue(String value) {
		this.objectValue = value;
	}

	public String getItemNo() {
		return this.itemNo;
	}

	public void setItemNo(String value) {
		this.itemNo = value;
	}

	public String getOldValue() {
		return this.oldValue;
	}

	public void setOldValue(String value) {
		this.oldValue = value;
	}

	public String getNewValue() {
		return this.newValue;
	}

	public void setNewValue(String value) {
		this.newValue = value;
	}

	public String getCreatedUser() {
		return this.createdUser;
	}

	public void setCreatedUser(String value) {
		this.createdUser = value;
	}

	public java.util.Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(java.util.Date value) {
		this.createdDate = value;
	}

	public String getUpdatedUser() {
		return this.updatedUser;
	}

	public void setUpdatedUser(String value) {
		this.updatedUser = value;
	}

	public java.util.Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(java.util.Date value) {
		this.updatedDate = value;
	}

	public String getPkSerial() {
		return this.pkSerial;
	}

	public void setPkSerial(String value) {
		this.pkSerial = value;
	}

	public String getInfoGroupNo() {
		return infoGroupNo;
	}

	public void setInfoGroupNo(String infoGroupNo) {
		this.infoGroupNo = infoGroupNo;
	}

	public String getQueryPkSerial() {
		return queryPkSerial;
	}

	public void setQueryPkSerial(String queryPkSerial) {
		this.queryPkSerial = queryPkSerial;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getChangeTable() {
		return changeTable;
	}

	public void setChangeTable(String changeTable) {
		this.changeTable = changeTable;
	}

}
