package com.sinolife.pos.common.util.detailgenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.util.PosUtils;

/**
 * 受理明细自动生成器，通过该类，可自动生成受理明细
 * 配置信息按照如下格式写：
 * posObject|itemNo|processTypeEdit|processTypeAdd|objectValueNameEdit|objectValueNameAdd<br/>
 * 如objectValueName新增与修改一致，则可以简写为<br/>
 * posObject|itemNo|processTypeEdit|processTypeAdd|objectValueName
 */
@SuppressWarnings("unchecked" )
public class AcceptDetailGenerator {
	private GroupNo groupNo;							//组号对象
	private AcceptDetailColumnHandler deleteHandler;	//删除处理器
	private AcceptDetailColumnHandler editHandler;		//编辑处理器
	private AcceptDetailColumnHandler addHandler;		//新增处理器
	private List<PosAcceptDetailDTO> resultList;		//处理的结果对象
	private String defaultObjectValue;					//ObjectValue值
	private String defaultPosNo;						//PosNO值
	private Map<String, String> configMap;				//属性配置Map
	private boolean incGroupNoEveryDetail;				//是否在处理每行的每列时均自增groupNO标志
	private boolean includeIdColumn;					//处理行时，是否包含ID列标志
	
	/**
	 * 构造函数
	 * @param configMap 属性配置Map
	 * @param defaultPosNo PosNo
	 * @param beginGroupNo 起始groupNo
	 */
	public AcceptDetailGenerator(Map<String, String> configMap, String defaultPosNo, int beginGroupNo) {
		this.groupNo = new GroupNo(beginGroupNo);
		this.configMap = configMap;
		this.defaultPosNo = defaultPosNo;
		this.resultList = new ArrayList<PosAcceptDetailDTO>();
		this.addHandler = new DefaultAddHandler();
		this.editHandler = new DefaultEditHandler();
	}
	
	/**
	 * 手动添加detail记录
	 * @param posNo
	 * @param posObject
	 * @param itemNo
	 * @param processType
	 * @param oldValue
	 * @param newValue
	 */
	public void addAcceptDetail(String posNo, String posObject, String itemNo, String objectValue, String processType, String oldValue, String newValue) {
		PosAcceptDetailDTO detail = new PosAcceptDetailDTO();
		detail.setPosNo(posNo);
		detail.setInfoGroupNo(String.valueOf(groupNo.inc()));
		detail.setItemNo(itemNo);
		detail.setNewValue(newValue);
		detail.setOldValue(oldValue);
		detail.setPosObject(posObject);
		detail.setProcessType(processType);
		detail.setObjectValue(objectValue);
		resultList.add(detail);
	}
	
	/**
	 * 设置删除处理器，默认为不处理删除
	 * @param handler
	 */
	public void setDeleteHandler(AcceptDetailColumnHandler handler) {
		this.deleteHandler = handler;
	}
	
	/**
	 * 设置编辑处理器，默认为DefaultEditHandler
	 * @param handler
	 */
	public void setEditHandler(AcceptDetailColumnHandler handler) {
		this.editHandler = handler;
	}
	
	/**
	 * 设置新增处理器，默认为DefaultAddHandler
	 * @param handler
	 */
	public void setAddHandler(AcceptDetailColumnHandler handler) {
		this.addHandler = handler;
	}
	
	/**
	 * 强制设置ObjectValue值，不管configMap中指定的ObjectValue列是否有值
	 * @param value
	 */
	public void setDefaultObjectValue(String value) {
		this.defaultObjectValue = value;
	}
	
	/**
	 * 设置默认PosNo
	 * @param value
	 */
	public void setDefaultPosNo(String value) {
		this.defaultPosNo = value;
	}
	
	/**
	 * 取得处理结果
	 * @return
	 */
	public List<PosAcceptDetailDTO> getResult() {
		return this.resultList;
	}
	
	/**
	 * 清除受理明细list结果
	 */
	public void clearResultList(){
		this.resultList = new ArrayList<PosAcceptDetailDTO>();
	}
	
	/**
	 * 取得当前的ConfigMap对象
	 * @return
	 */
	public Map<String, String> getConfigMap() {
		return configMap;
	}

	/**
	 * 设置ConfigMap
	 * @param configMap
	 */
	public void setConfigMap(Map<String, String> configMap) {
		this.configMap = configMap;
	}
	
	public GroupNo getGroupNo() {
		return groupNo;
	}

	public void setGroupNo(GroupNo groupNo) {
		this.groupNo = groupNo;
	}

	/**
	 * 处理简单DTO，该方法简单的比较configMap中配置的属性，不为空则生成受理明细，
	 * 每生成一个受理明细，groupNo加1
	 * @param newVal 新值DTO
	 */
	public void processSimpleDTO(Object newVal) {
		this.processSimpleDTO(newVal, null, true);
	}
	
	/**
	 * 处理简单DTO，该方法简单的比较configMap中配置的属性，不为空则生成受理明细，
	 * 通过incGroupNoEveryDetail决定是否每生成一个受理明细，groupNo加1
	 * @param newVal 新值DTO
	 * @param incGroupNoEveryDetail 是否每生成一个受理明细，groupNo加1
	 */
	public void processSimpleDTO(Object newVal, boolean incGroupNoEveryDetail) {
		this.processSimpleDTO(newVal, null, incGroupNoEveryDetail);
	}
	
	/**
	 * 处理简单DTO，该方法直接比较两个传入参数，对于configMap中配置过的属性，只要值不相等，
	 * 就生成受理明细，每生成一个受理明细，groupNo加1
	 * @param newVal 新值DTO
	 * @param oldVal 旧值DTO
	 */
	public void processSimpleDTO(Object newVal, Object oldVal) {
		this.processSimpleDTO(newVal, oldVal, true);
	}
	
	/**
	 * 处理简单DTO，该方法直接比较两个传入参数，对于configMap中配置过的属性，只要值不相等，
	 * 就生成受理明细，通过incGroupNoEveryDetail决定是否每生成一个受理明细，groupNo加1
	 * @param newVal 新值DTO
	 * @param oldVal 旧值DTO
	 * @param incGroupNoEveryDetail 是否每生成一个受理明细，groupNo加1
	 */
	public void processSimpleDTO(Object newVal, Object oldVal, boolean incGroupNoEveryDetail) {
		this.processSimpleMap(PosUtils.getMapFromBean(newVal), PosUtils.getMapFromBean(oldVal), incGroupNoEveryDetail);
	}
	
	/**
	 * 处理简单DTO Map，该方法简单的比较configMap中配置的属性，不为空则生成受理明细，
	 * 每生成一个受理明细，groupNo加1
	 * @param newMap 新值DTO Map
	 */
	public void processSimpleMap(Map<String, Object> newMap) {
		this.processSimpleMap(newMap, null, true);
	}
	
	/**
	 * 处理简单DTO Map，该方法简单的比较configMap中配置的属性，不为空则生成受理明细，
	 * 通过incGroupNoEveryDetail决定是否每生成一个受理明细，groupNo加1
	 * @param newMap 新值DTO Map
	 * @param incGroupNoEveryDetail 是否每生成一个受理明细，groupNo加1
	 */
	public void processSimpleMap(Map<String, Object> newMap, boolean incGroupNoEveryDetail) {
		this.processSimpleMap(newMap, null, incGroupNoEveryDetail);
	}
	
	/**
	 * 处理简单DTO Map，该方法直接比较两个传入参数，对于configMap中配置过的属性，只要值不相等，
	 * 就生成受理明细，每生成一个受理明细，groupNo即加1
	 * @param newMap 新值DTO Map
	 * @param oldMap 旧值DTO Map
	 */
	public void processSimpleMap(Map<String, Object> newMap, Map<String, Object> oldMap) {
		this.processSimpleMap(newMap, oldMap, true);
	}
	
	/**
	 * 处理简单DTO Map，该方法直接比较两个传入参数，对于configMap中配置过的属性，只要值不相等，
	 * 就生成受理明细，通过incGroupNoEveryDetail决定是否每生成一个受理明细，groupNo加1
	 * @param newMap 新值DTO Map
	 * @param oldMap 旧值DTO Map
	 * @param incGroupNoEveryDetail 是否每生成一个受理明细，groupNo加1
	 */
	public void processSimpleMap(Map<String, Object> newMap, Map<String, Object> oldMap, boolean incGroupNoEveryDetail) {
		if(PosUtils.isNullOrEmpty(newMap))return;//传入newMap是空，直接返回不做处理
		
		List<PosAcceptDetailDTO> retList;
		groupNo.inc();
		boolean changed = false;
		for(Entry<String, Object> entry : newMap.entrySet()) {
			String propertyName = entry.getKey();
			Object propertyValue = entry.getValue();
			Object oldValue = null;
			if(oldMap != null) {
				oldValue = oldMap.get(propertyName);
				//当新值与旧值不同时调用handler生成受理明细，这里不处理ID列，只有当除ID列之外的列有变更时，才处理ID列
				if(!PosUtils.isTheSame(propertyValue, oldValue) && editHandler != null) {
					prepareHandler(editHandler);
					retList = editHandler.processColumn(propertyName, propertyValue, oldValue, groupNo, newMap);
					if(PosUtils.isNotNullOrEmpty(retList)) {
						changed = true;
						resultList.addAll(retList);
						if(incGroupNoEveryDetail) {
							groupNo.inc();
						}
					}
				}
			} else {
				if(PosUtils.isNotNullOrEmpty(propertyValue) && addHandler != null) {
					prepareHandler(addHandler);
					retList = addHandler.processColumn(propertyName, propertyValue, oldValue, groupNo, newMap);
					if(PosUtils.isNotNullOrEmpty(retList)) {
						changed = true;
						resultList.addAll(retList);
						if(incGroupNoEveryDetail) {
							groupNo.inc();
						}
					}
				}
			}
		}
		if(!changed || changed && incGroupNoEveryDetail) {
			groupNo.dec();
		}
	}
	
	/**
	 * 处理简单DTO列表，所有属性均为新增。该方法简单的比较每个对象在configMap中配置的属性，不为空则生成受理明细，对于List
	 * 中的每个DTO，都使用相同的groupNo
	 * @param newList 当前值DTO列表
	 */
	public void processSimpleListDTO(List<? extends Object> newList) {
		this.processSimpleListDTO(newList, null, null, false);
	}
	
	/**
	 * 处理简单DTO列表，所有属性均为新增。该方法简单的比较每个对象在configMap中配置的属性，不为空则生成受理明细，对于List
	 * 中的每个DTO，都使用相同的groupNo
	 * @param newList 当前值DTO列表
	 * @param filter 行过滤器
	 */
	public void processSimpleListDTO(List<? extends Object> newList, ItemProcessFilter filter) {
		this.processSimpleListDTO(newList, null, null, false, filter);
	}
	
	/**
	 * 处理简单DTO列表，该方法通过idPropertyName参数指明的属性标识对象，
	 * 并比较相同对象的新旧值生成受理明细，对于List中的每个DTO，都使用相同的groupNo
	 * @param newList 当前值DTO列表
	 * @param oldList 原始值DTO列表
	 * @param idPropertyName ID列的名称
	 * @param includeIdColumn 是否在detail中包含ID列
	 */
	public void processSimpleListDTO(List<? extends Object> newList, List<? extends Object> oldList, String idPropertyName, boolean includeIdColumn) {
		this.processListMap(transFromListDTOToListMap(newList), transFromListDTOToListMap(oldList), idPropertyName, includeIdColumn);
	}
	
	/**
	 * 处理简单DTO列表，该方法通过idPropertyName参数指明的属性标识对象，
	 * 并比较相同对象的新旧值生成受理明细，对于List中的每个DTO，都使用相同的groupNo
	 * @param newList 当前值DTO列表
	 * @param oldList 原始值DTO列表
	 * @param idPropertyName ID列的名称
	 * @param includeIdColumn 是否在detail中包含ID列
	 * @param filter 行过滤器
	 */
	public void processSimpleListDTO(List<? extends Object> newList, List<? extends Object> oldList, String idPropertyName, boolean includeIdColumn, ItemProcessFilter filter) {
		this.processListMap(transFromListDTOToListMap(newList), transFromListDTOToListMap(oldList), idPropertyName, includeIdColumn, filter);
	}
	
	/**
	 * 处理简单DTO Map，所有属性均为新增。该方法简单的比较每个对象在configMap中配置的属性，不为空则生成受理明细，对于List
	 * 中的每个DTO，都使用相同的groupNo
	 * @param newList 当前值DTO Map列表
	 */
	public void processListMap(List<Map<String, Object>> newList) {
		this.processListMap(newList, null, null, false);
	}
	
	/**
	 * 处理简单DTO Map，所有属性均为新增。该方法简单的比较每个对象在configMap中配置的属性，不为空则生成受理明细，对于List
	 * 中的每个DTO，都使用相同的groupNo
	 * @param newList 当前值DTO Map列表
	 * @param filter 行过滤器
	 */
	public void processListMap(List<Map<String, Object>> newList, ItemProcessFilter filter) {
		this.processListMap(newList, null, null, false, filter);
	}
	
	/**
	 * 处理简单DTO Mao，该方法通过idPropertyName参数指明的属性标识对象，
	 * 并比较相同对象的新旧值生成受理明细，对于List中的每个DTO，都使用相同的groupNo
	 * @param newList 当前值DTO Map列表
	 * @param oldList 原始值DTO Mao列表
	 * @param idPropertyName ID列的名称
	 * @param includeIdColumn 是否在detail中包含ID列
	 */
	public void processListMap(List<Map<String, Object>> newList, List<Map<String, Object>> oldList, String idPropertyName, boolean includeIdColumn) {
		this.incGroupNoEveryDetail = false;
		this.includeIdColumn = includeIdColumn;
		processListMapInternal(newList, oldList, idPropertyName, null);
	}
	
	/**
	 * 处理简单DTO Mao，该方法通过idPropertyName参数指明的属性标识对象，
	 * 并比较相同对象的新旧值生成受理明细，对于List中的每个DTO，都使用相同的groupNo
	 * @param newList 当前值DTO Map列表
	 * @param oldList 原始值DTO Mao列表
	 * @param idPropertyName ID列的名称
	 * @param includeIdColumn 是否在detail中包含ID列
	 * @param filter 行过滤器
	 */
	public void processListMap(List<Map<String, Object>> newList, List<Map<String, Object>> oldList, String idPropertyName, boolean includeIdColumn, ItemProcessFilter filter) {
		this.incGroupNoEveryDetail = false;
		this.includeIdColumn = includeIdColumn;
		processListMapInternal(newList, oldList, idPropertyName, filter);
	}
	
	/**
	 * 内部处理方法
	 * @param newList
	 * @param oldList
	 * @param idPropertyName
	 */
	private void processListMapInternal(List<Map<String, Object>> newList, List<Map<String, Object>> oldList, String idPropertyName, ItemProcessFilter filter) {
		if(PosUtils.isNullOrEmpty(newList))return;//传入newList是空，直接返回不做处理
		
		List<PosAcceptDetailDTO> retList;
		
		//处理删除元素
		if(PosUtils.isNotNullOrEmpty(idPropertyName) && deleteHandler != null) {
			prepareHandler(deleteHandler);
			for (Map<String, Object> map : oldList) {
				//对于每个原始对象，如果在修改后的列表中找不到同ID的对象，则判定为需删除
				Object id = map.get(idPropertyName);
				Map<String, Object> oldMap = (Map<String, Object>) PosUtils.findInCollectionByProperty(newList, idPropertyName, id);
				if(PosUtils.isNotNullOrEmpty(id) && oldMap == null  && (filter == null || filter.needProcess(null, map))) {
					groupNo.inc();
					retList = deleteHandler.processColumn(idPropertyName, id, id, groupNo, map);
					if(PosUtils.isNotNullOrEmpty(retList)) {
						resultList.addAll(retList);
					} else {
						groupNo.dec();
					}
				}
			}
		}
		
		//处理新增及修改元素
		for(Map<String, Object> map : newList) {
			Object id = map.get(idPropertyName);
			boolean changed = false;
			groupNo.inc();
			Map<String, Object> oldMap = (Map<String, Object>) PosUtils.findInCollectionByProperty(oldList, idPropertyName, id);
			if(PosUtils.isNotNullOrEmpty(idPropertyName) && PosUtils.isNotNullOrEmpty(id) && oldMap != null) {
				//修改
				if(editHandler != null && (filter == null || filter.needProcess(map, oldMap))) {
					prepareHandler(editHandler);
					for(Entry<String, Object> entry : map.entrySet()) {
						String propertyName = entry.getKey();
						Object propertyValue = entry.getValue();
						Object oldValue = oldMap.get(propertyName);
						//当新值与旧值不同时调用handler生成受理明细，这里不处理ID列，只有当除ID列之外的列有变更时，才处理ID列
						if(!idPropertyName.equals(propertyName) && !PosUtils.isTheSame(propertyValue, oldValue)) {
							retList = editHandler.processColumn(propertyName, propertyValue, oldValue, groupNo, map);
							if(PosUtils.isNotNullOrEmpty(retList)) {
								changed = true;
								resultList.addAll(retList);
								if(incGroupNoEveryDetail) {
									groupNo.inc();
								}
							}
						}
					}
					if(changed && includeIdColumn) {
						retList = editHandler.processColumn(idPropertyName, id, id, groupNo, map);
						if(PosUtils.isNotNullOrEmpty(retList)) {
							resultList.addAll(retList);
							if(incGroupNoEveryDetail) {
								groupNo.inc();
							}
						}
					}
				}
			} else {
				//新增
				if(addHandler != null && (filter == null || filter.needProcess(map, oldMap))) {
					prepareHandler(addHandler);
					for(Entry<String, Object> entry : map.entrySet()) {
						String propertyName = entry.getKey();
						Object propertyValue = entry.getValue();
						retList = addHandler.processColumn(propertyName, propertyValue, null, groupNo, map);
						if(PosUtils.isNotNullOrEmpty(retList)) {
							changed = true;
							resultList.addAll(retList);
							if(incGroupNoEveryDetail) {
								groupNo.inc();
							}
						}
					}
				}
			}
			if(!changed || changed && incGroupNoEveryDetail)
				groupNo.dec();
		}
	}
	
	private void prepareHandler(AcceptDetailColumnHandler handler) {
		if(handler != null) {
			handler.setConfigMap(configMap);
			handler.setDefaultObjectValue(defaultObjectValue);
			handler.setDefaultPosNo(defaultPosNo);
		}
	}
	
	private List<Map<String, Object>> transFromListDTOToListMap(List<? extends Object> sourceList) {
		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		if(sourceList != null && !sourceList.isEmpty()) {
			for(Object obj : sourceList) {
				retList.add(PosUtils.getMapFromBean(obj));
			}
		}
		return retList;
	}

}
