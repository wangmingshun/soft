/*
 * Powered By sino-life
 * Web Site: http://www.sino-life.com
 * Since 2010 - 2011
 */

package com.sinolife.pos.common.dto;

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
@Table(name = "\"STAFF_INFO\"")
public class StaffInfoDTO implements java.io.Serializable {
	private static final long serialVersionUID = 5454155825314635342L;

	// 别名，页面中使用
	public static final String TABLE_ALIAS = "人员信息";
	public static final String ALIAS_EMP_NO = "员工代码";
	public static final String ALIAS_OUT_EMP_NO = "外部员工代码";
	public static final String ALIAS_DEPT_NO = "部门代码";
	public static final String ALIAS_EMP_NAME = "姓名";
	public static final String ALIAS_SEX_CODE = "性别";
	public static final String ALIAS_BIRTH_DATE = "生日";
	public static final String ALIAS_ID_TYPE = "证件类型代码";
	public static final String ALIAS_EMP_IDNO = "证件号码";
	public static final String ALIAS_HOME_ADDRESS = "家庭地址";
	public static final String ALIAS_PHONE = "固定电话";
	public static final String ALIAS_MOBILE = "移动电话";
	public static final String ALIAS_POSTCODE = "邮编";
	public static final String ALIAS_HIRE_DATE = "入司时间";
	public static final String ALIAS_REG_DATE = "转正时间";
	public static final String ALIAS_LEAVE_DATE = "离职时间";
	public static final String ALIAS_POSITION = "职务";
	public static final String ALIAS_RANK = "职级";
	public static final String ALIAS_EMP_TYPE = "员工类型";
	public static final String ALIAS_CREATED_USER = "录入人";
	public static final String ALIAS_CREATED_DATE = "录入日期";
	public static final String ALIAS_UPDATED_USER = "修改人";
	public static final String ALIAS_UPDATED_DATE = "修改日期";
	public static final String ALIAS_IS_VALID = "是否有效";

	// 属性
	// columns START
	/**
	 * 员工代码 db_column: EMP_NO
	 */
	private String empNo;
	/**
	 * 外部员工代码 db_column: OUT_EMP_NO
	 */
	private String outEmpNo;
	/**
	 * 部门代码 db_column: DEPT_NO
	 */
	private String deptNo;
	/**
	 * 姓名 db_column: EMP_NAME
	 */
	private String empName;
	/**
	 * 性别 db_column: SEX_CODE
	 */
	private String sexCode;
	/**
	 * 生日 db_column: BIRTH_DATE
	 */
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date birthDate;
	/**
	 * 证件类型代码 db_column: ID_TYPE
	 */
	private String idType;
	/**
	 * 证件号码 db_column: EMP_IDNO
	 */
	private String empIdno;
	/**
	 * 家庭地址 db_column: HOME_ADDRESS
	 */
	private String homeAddress;
	/**
	 * 固定电话 db_column: PHONE
	 */
	private String phone;
	/**
	 * 移动电话 db_column: MOBILE
	 */
	private String mobile;
	/**
	 * 邮编 db_column: POSTCODE
	 */
	private String postcode;
	/**
	 * 入司时间 db_column: HIRE_DATE
	 */
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date hireDate;
	/**
	 * 转正时间 db_column: REG_DATE
	 */
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date regDate;
	/**
	 * 离职时间 db_column: LEAVE_DATE
	 */
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date leaveDate;
	/**
	 * 职务 db_column: POSITION
	 */
	private String position;
	/**
	 * 职级 db_column: RANK
	 */
	private String rank;
	/**
	 * 员工类型 db_column: EMP_TYPE
	 */
	private String empType;
	/**
	 * 录入人 db_column: CREATED_USER
	 */
	private String createdUser;
	/**
	 * 录入日期 db_column: CREATED_DATE
	 */
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date createdDate;
	/**
	 * 修改人 db_column: UPDATED_USER
	 */
	private String updatedUser;
	/**
	 * 修改日期 db_column: UPDATED_DATE
	 */
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date updatedDate;
	/**
	 * 是否有效 db_column: IS_VALID
	 */
	private String isValid;

	// columns END
	// 构造函数

	public StaffInfoDTO() {
	}

	public StaffInfoDTO(String empNo) {
		this.empNo = empNo;
	}

	// 默认主键自动生成，具体开发中需要开发人员根据实际情况进行适当修改

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "\"EMP_NO\"", unique = true, nullable = false)
	public String getEmpNo() {
		return this.empNo;
	}

	public void setEmpNo(String value) {
		this.empNo = value;
	}

	// 非主键getter setter方法生成

	@Column(name = "\"OUT_EMP_NO\"", unique = false, nullable = true)
	public String getOutEmpNo() {
		return this.outEmpNo;
	}

	public void setOutEmpNo(String value) {
		this.outEmpNo = value;
	}

	@Column(name = "\"DEPT_NO\"", unique = false, nullable = false)
	public String getDeptNo() {
		return this.deptNo;
	}

	public void setDeptNo(String value) {
		this.deptNo = value;
	}

	@Column(name = "\"EMP_NAME\"", unique = false, nullable = true)
	public String getEmpName() {
		return this.empName;
	}

	public void setEmpName(String value) {
		this.empName = value;
	}

	@Column(name = "\"SEX_CODE\"", unique = false, nullable = true)
	public String getSexCode() {
		return this.sexCode;
	}

	public void setSexCode(String value) {
		this.sexCode = value;
	}

	@Column(name = "\"BIRTH_DATE\"", unique = false, nullable = true)
	public Date getBirthDate() {
		return this.birthDate;
	}

	public void setBirthDate(Date value) {
		this.birthDate = value;
	}

	@Column(name = "\"ID_TYPE\"", unique = false, nullable = true)
	public String getIdType() {
		return this.idType;
	}

	public void setIdType(String value) {
		this.idType = value;
	}

	@Column(name = "\"EMP_IDNO\"", unique = false, nullable = true)
	public String getEmpIdno() {
		return this.empIdno;
	}

	public void setEmpIdno(String value) {
		this.empIdno = value;
	}

	@Column(name = "\"HOME_ADDRESS\"", unique = false, nullable = true)
	public String getHomeAddress() {
		return this.homeAddress;
	}

	public void setHomeAddress(String value) {
		this.homeAddress = value;
	}

	@Column(name = "\"PHONE\"", unique = false, nullable = true)
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String value) {
		this.phone = value;
	}

	@Column(name = "\"MOBILE\"", unique = false, nullable = true)
	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String value) {
		this.mobile = value;
	}

	@Column(name = "\"POSTCODE\"", unique = false, nullable = true)
	public String getPostcode() {
		return this.postcode;
	}

	public void setPostcode(String value) {
		this.postcode = value;
	}

	@Column(name = "\"HIRE_DATE\"", unique = false, nullable = true)
	public Date getHireDate() {
		return this.hireDate;
	}

	public void setHireDate(Date value) {
		this.hireDate = value;
	}

	@Column(name = "\"REG_DATE\"", unique = false, nullable = true)
	public Date getRegDate() {
		return this.regDate;
	}

	public void setRegDate(Date value) {
		this.regDate = value;
	}

	@Column(name = "\"LEAVE_DATE\"", unique = false, nullable = true)
	public Date getLeaveDate() {
		return this.leaveDate;
	}

	public void setLeaveDate(Date value) {
		this.leaveDate = value;
	}

	@Column(name = "\"POSITION\"", unique = false, nullable = true)
	public String getPosition() {
		return this.position;
	}

	public void setPosition(String value) {
		this.position = value;
	}

	@Column(name = "\"RANK\"", unique = false, nullable = true)
	public String getRank() {
		return this.rank;
	}

	public void setRank(String value) {
		this.rank = value;
	}

	@Column(name = "\"EMP_TYPE\"", unique = false, nullable = true)
	public String getEmpType() {
		return this.empType;
	}

	public void setEmpType(String value) {
		this.empType = value;
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

	@Column(name = "\"IS_VALID\"", unique = false, nullable = true)
	public String getIsValid() {
		return this.isValid;
	}

	public void setIsValid(String value) {
		this.isValid = value;
	}

	/*
	 * 代码自动生成一对多的关系，处于注释掉状态，实际开发中可以去掉注释或者删掉该部分内容
	 */
	/*
	 * 代码自动生成多对一的关系，处于注释掉状态，实际开发中可以去掉注释或者删掉该部分内容
	 */

	/* 以下为非表属性 */
	private String sexDesc;
	private String idTypeDesc;
	private String empTypeDesc;

	public String getSexDesc() {
		return sexDesc;
	}

	public void setSexDesc(String sexDesc) {
		this.sexDesc = sexDesc;
	}

	public String getIdTypeDesc() {
		return idTypeDesc;
	}

	public void setIdTypeDesc(String idTypeDesc) {
		this.idTypeDesc = idTypeDesc;
	}

	public String getEmpTypeDesc() {
		return empTypeDesc;
	}

	public void setEmpTypeDesc(String empTypeDesc) {
		this.empTypeDesc = empTypeDesc;
	}

}
