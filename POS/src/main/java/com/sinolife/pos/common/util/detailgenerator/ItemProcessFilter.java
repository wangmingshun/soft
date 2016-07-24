package com.sinolife.pos.common.util.detailgenerator;

import java.util.Map;

/**
 * 行处理过滤器
 */
public interface ItemProcessFilter {

	/**
	 * 判断某行是否需要处理
	 * @param listItem 当前行
	 * @param oldValueItem 当前行对应的原始值
	 * @return true 需要处理 false 跳过处理
	 */
	boolean needProcess(Map<String, Object> listItem, Map<String, Object> oldValueItem);
}
