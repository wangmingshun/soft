package com.sinolife.pos.common.util.detailgenerator;

import java.util.List;
import java.util.Map;

import com.sinolife.pos.common.dto.PosAcceptDetailDTO;

/**
 * 列处理器接口
 */
public interface AcceptDetailColumnHandler {
	
	/**
	 * 处理属性列
	 * @param propertyName 属性名称
	 * @param newValue 属性当前值
	 * @param oldValue 属性原始值
	 * @param groupNo 当前组号
	 * @param listItem 当前处理的DTO MAP
	 * @return
	 */
	List<PosAcceptDetailDTO> processColumn(String propertyName, Object newValue, Object oldValue, GroupNo groupNo, Map<String, Object> listItem);
	
	/**
	 * configMap注入方法
	 * @param configMap
	 */
	void setConfigMap(Map<String, String> configMap);
	
	/**
	 * 设置处理时使用的ObjectValue值
	 * @param value
	 */
	void setDefaultObjectValue(String value);
	
	/**
	 * 设置处理时的PosNo
	 * @param value
	 */
	void setDefaultPosNo(String value);
}
