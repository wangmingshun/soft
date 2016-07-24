package com.sinolife.pos.common.util.detailgenerator;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.util.PosUtils;

/**
 * 列处理器抽象类
 */
public abstract class AbstractAcceptDetailColumnHandler implements
		AcceptDetailColumnHandler {
	
	protected Logger logger = Logger.getLogger(getClass());
	
	/**
	 * 配置DTO
	 */
	protected static class ConfigDTO {
		private String posObject;
		private String itemNo;
		private String processTypeEdit;
		private String processTypeAdd;
		private String objectValueNameEdit;
		private String objectValueNameAdd;
		
		public String getPosObject() {
			return posObject;
		}
		public void setPosObject(String posObject) {
			this.posObject = posObject;
		}
		public String getItemNo() {
			return itemNo;
		}
		public void setItemNo(String itemNo) {
			this.itemNo = itemNo;
		}
		public String getProcessTypeEdit() {
			return processTypeEdit;
		}
		public void setProcessTypeEdit(String processTypeEdit) {
			this.processTypeEdit = processTypeEdit;
		}
		public String getProcessTypeAdd() {
			return processTypeAdd;
		}
		public void setProcessTypeAdd(String processTypeAdd) {
			this.processTypeAdd = processTypeAdd;
		}
		public String getObjectValueNameAdd() {
			return objectValueNameAdd;
		}
		public void setObjectValueNameAdd(String objectValueNameAdd) {
			this.objectValueNameAdd = objectValueNameAdd;
		}
		public String getObjectValueNameEdit() {
			return objectValueNameEdit;
		}
		public void setObjectValueNameEdit(String objectValueNameEdit) {
			this.objectValueNameEdit = objectValueNameEdit;
		}
		
	}
	
	protected Map<String, String> configMap;
	protected String defaultObjectValue;
	protected String defaultPosNo;
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.util.detailgenerator.AcceptDetailColumnHandler#processColumn(java.lang.String, java.lang.Object, java.lang.Object, com.sinolife.pos.common.util.detailgenerator.GroupNo, java.util.Map)
	 */
	@Override
	public List<PosAcceptDetailDTO> processColumn(String propertyName, Object newValue, Object oldValue, GroupNo groupNo, Map<String, Object> listItem) {
		if(configMap.containsKey(propertyName)) {
			String[] configArr = configMap.get(propertyName).split("\\|");
			
			ConfigDTO config = new ConfigDTO();
			config.setPosObject(configArr[0]);
			config.setItemNo(configArr[1]);
			config.setProcessTypeEdit(configArr[2]);
			config.setProcessTypeAdd(configArr[3]);
			if(configArr.length == 6) {
				config.setObjectValueNameEdit(configArr[4]);
				config.setObjectValueNameAdd(configArr[5]);
			} else {
				config.setObjectValueNameEdit(configArr[4]);
				config.setObjectValueNameAdd(configArr[4]);
			}
			
			PosAcceptDetailDTO detail = new PosAcceptDetailDTO();
			
			if(StringUtils.isNotBlank(defaultPosNo)) {
				detail.setPosNo(defaultPosNo);
			}
			
			detail.setPosObject(config.getPosObject());
			detail.setItemNo(config.getItemNo());
			detail.setOldValue(PosUtils.transToString(oldValue));
			detail.setNewValue(PosUtils.transToString(newValue));
			detail.setInfoGroupNo(groupNo.get());
			return processColumn(propertyName, newValue, oldValue, groupNo, listItem, detail, config);
		}
		return null;
	}

	/**
	 * 列处理方法
	 * @param propertyName 属性名
	 * @param newValue 属性当前值
	 * @param oldValue 属性原始值
	 * @param groupNo 当前组号
	 * @param listItem 当前处理的DTO MAP对象
	 * @param generatedDetail 根据默认值组装好的PosAcceptDetailDTO对象
	 * @param config configMap中当前属性的配置值
	 * @return 生成的PosAcceptDetailDTO列表，如果忽略当前列则返回null
	 */
	protected abstract List<PosAcceptDetailDTO> processColumn(String propertyName, Object newValue, Object oldValue, GroupNo groupNo,
			Map<String, Object> listItem, PosAcceptDetailDTO generatedDetail, ConfigDTO config);
	
	@Override
	public void setConfigMap(Map<String, String> configMap) {
		this.configMap = configMap;
	}
	
	@Override
	public void setDefaultObjectValue(String value) {
		this.defaultObjectValue = value;
	}
	
	@Override
	public void setDefaultPosNo(String value) {
		this.defaultPosNo = value;
	}
	
}
