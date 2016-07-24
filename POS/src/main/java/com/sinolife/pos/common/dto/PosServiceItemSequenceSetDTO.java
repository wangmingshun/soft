/*
 * Powered By sino-life
 * Web Site: http://www.sino-life.com
 * Since 2010 - 2011
 */

package com.sinolife.pos.common.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 表名和字段名前加双引号，是因为当这些标识是oralce中的关键字时，会报错，加上双引号是防止出错
 */
@Entity
public class PosServiceItemSequenceSetDTO implements Serializable {
	private static final long serialVersionUID = 5454155825314635342L;

	// 别名，页面中使用
	public static final String TABLE_ALIAS = "保全项目捆绑顺序设置表";
	public static final String ALIAS_SERVICE_ITEMS = "服务项目";
	public static final String ALIAS_PRO_SERVICE_ITEMS = "服务项目";
	public static final String ALIAS_CREATED_USER = "录入人";
	public static final String ALIAS_CREATED_DATE = "录入日期";
	public static final String ALIAS_UPDATED_USER = "修改人";
	public static final String ALIAS_UPDATED_DATE = "修改日期";

	// 属性
	// columns START
	/**
	 * 服务项目 db_column: SERVICE_ITEMS
	 */
	private String serviceItems;
	/**
	 * 服务项目 db_column: PRO_SERVICE_ITEMS
	 */
	private String proServiceItems;
	/**
	 * 录入人 db_column: CREATED_USER
	 */
	private String createdUser;
	/**
	 * 录入日期 db_column: CREATED_DATE
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createdDate;
	/**
	 * 修改人 db_column: UPDATED_USER
	 */
	private String updatedUser;
	/**
	 * 修改日期 db_column: UPDATED_DATE
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date updatedDate;

	// columns END
	// 构造函数

	public PosServiceItemSequenceSetDTO() {
	}

	public PosServiceItemSequenceSetDTO(String serviceItems, String proServiceItems) {
		this.serviceItems = serviceItems;
		this.proServiceItems = proServiceItems;
	}

	// 默认主键自动生成，具体开发中需要开发人员根据实际情况进行适当修改

	public String getServiceItems() {
		return this.serviceItems;
	}

	public void setServiceItems(String value) {
		this.serviceItems = value;
	}

	public String getProServiceItems() {
		return this.proServiceItems;
	}

	public void setProServiceItems(String value) {
		this.proServiceItems = value;
	}

	// 非主键getter setter方法生成

	public String getCreatedUser() {
		return this.createdUser;
	}

	public void setCreatedUser(String value) {
		this.createdUser = value;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date value) {
		this.createdDate = value;
	}

	public String getUpdatedUser() {
		return this.updatedUser;
	}

	public void setUpdatedUser(String value) {
		this.updatedUser = value;
	}

	public Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(Date value) {
		this.updatedDate = value;
	}

}
