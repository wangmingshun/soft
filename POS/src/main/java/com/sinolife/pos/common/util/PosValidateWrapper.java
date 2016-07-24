package com.sinolife.pos.common.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

/**
 * 校验工具类
 */
public class PosValidateWrapper {
	private Errors errors;
	
	/**
	 * Constructor
	 * @param errors
	 */
	public PosValidateWrapper(Errors errors) {
		this.errors = errors;
	}
	
	/**
	 * 直接添加校验错误
	 * @param field 字段名
	 * @param errMsg 错误消息
	 * @return
	 */
	public PosValidateWrapper addErrMsg(String field, String errMsg) {
		errors.rejectValue(field, null, errMsg);
		return this;
	}
	
	/**
	 * 如果给定的值字符串为空则添加校验不通过的消息
	 * @param field
	 * @param value
	 * @param errMsg
	 * @return
	 */
	public PosValidateWrapper rejectIfEmpty(String field, String value, String errMsg) {
		if(StringUtils.isEmpty(value)) {
			addErrMsg(field, errMsg);
		}
		return this;
	}
	
	/**
	 * 如果给定的的值为null则添加校验不通过的消息
	 * @param field
	 * @param value
	 * @param errMsg
	 * @return
	 */
	public PosValidateWrapper rejectIfNull(String field, Object value, String errMsg) {
		if(value == null) {
			addErrMsg(field, errMsg);
		}
		return this;
	}
	
	/**
	 * 如果给定的的值不匹配给定的正则表达式，则添加校验不通过的消息
	 * @param field
	 * @param value
	 * @param regex
	 * @param errMsg
	 * @return
	 */
	public PosValidateWrapper rejectIfNotMatchPattern(String field, String value, String regex, String errMsg) {
		if(!Pattern.matches(regex, value)) {
			addErrMsg(field, errMsg);
		}
		return this;
	}
	
	/**
	 * 如果给定的的字符串值不在给定的字符串数组范围内，则添加校验不通过的消息
	 * @param field
	 * @param value
	 * @param enumArray
	 * @param errMsg
	 * @return
	 */
	public PosValidateWrapper rejectIfNotInEnum(String field, String value, String[] enumArray, String errMsg) {
		if(enumArray != null && enumArray.length > 0) {
			boolean matches = false;
			for(String enu : enumArray) {
				if(StringUtils.isNotEmpty(enu) && enu.equals(value)) {
					matches = true;
					break;
				}
			}
			if(!matches)
				addErrMsg(field, errMsg);
		}
		return this;
	}
	
	/**
	 * 如果给定的字符串不能按照给定的模式转化成DATE类型，则添加校验不通过的消息
	 * @param field
	 * @param value
	 * @param datePattern
	 * @param errMsg
	 * @return
	 */
	public PosValidateWrapper rejectIfInvalidDate(String field, String value, String datePattern, String errMsg) {
		DateFormat df = new SimpleDateFormat(datePattern);
		try {
			df.parse(value);
		} catch(Exception e) {
			addErrMsg(field, errMsg);
		}
		return this;
	}
	
	/**
	 * 如果给定的字符串不能转化为数字，则添加校验不通过的消息
	 * @param field
	 * @param value
	 * @param errMsg
	 * @return
	 */
	public PosValidateWrapper rejectIfInvalidNumber(String field, String value, String errMsg) {
		try {
			new BigDecimal(value);
		} catch(Exception e) {
			addErrMsg(field, errMsg);
		}
		return this;
	}
}
