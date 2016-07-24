package com.sinolife.pos.common.util.detailgenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.util.PosUtils;

/**
 * 默认删除处理器
 * processType为配置中指明的编辑processType
 */
public class DefaultDeleteHanlder implements AcceptDetailColumnHandler {

	private Logger logger = Logger.getLogger(getClass());
	
	private String _posObject;
	private String _itemNo;
	private String _objectValueName;
	private String _processType;
	private String _oldValue;
	private String _newValue;
	
	private String defaultObjectValue;
	private String defaultPosNo;
	
	public DefaultDeleteHanlder(String posObject, String itemNo, String processType, String objectValueName, String oldValue, String newValue) {
		this._posObject = posObject;
		this._itemNo = itemNo;
		this._objectValueName = objectValueName;
		this._processType = processType;
		this._newValue = newValue;
		this._oldValue = oldValue;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.util.detailgenerator.AcceptDetailColumnHandler#processColumn(java.lang.String, java.lang.Object, java.lang.Object, com.sinolife.pos.common.util.detailgenerator.GroupNo, java.util.Map)
	 */
	@Override
	public List<PosAcceptDetailDTO> processColumn(String propertyName, Object newValue, Object oldValue, GroupNo groupNo, Map<String, Object> listItem) {
		PosAcceptDetailDTO detail = new PosAcceptDetailDTO();
		detail.setProcessType(_processType);
		detail.setPosObject(_posObject);
		detail.setItemNo(_itemNo);
		detail.setObjectValue(PosUtils.transToString(listItem.get(_objectValueName)));
		detail.setOldValue(_oldValue);
		detail.setNewValue(_newValue);
		detail.setInfoGroupNo(groupNo.get());
		
		if(StringUtils.isNotBlank(defaultPosNo)) {
			detail.setPosNo(defaultPosNo);
		}
		
		if(StringUtils.isBlank(detail.getObjectValue())) {
			detail.setObjectValue(defaultObjectValue);
		}
		
		
		logger.info(new StringBuffer("generate detail DELETE[")
							.append(detail.getInfoGroupNo())
							.append("](propName:")
							.append(propertyName)
							.append(", posNo:")
							.append(detail.getPosNo())
							.append(", posObject:")
							.append(detail.getPosObject())
							.append(", itemNo:")
							.append(detail.getItemNo())
							.append(", processType:")
							.append(detail.getProcessType())
							.append(", oldValue:")
							.append(detail.getOldValue())
							.append(", newValue:")
							.append(detail.getNewValue())
							.append(")").toString());
		
		List<PosAcceptDetailDTO> retList = new ArrayList<PosAcceptDetailDTO>();
		retList.add(detail);
		return retList;
	}
	
	@Override
	public void setConfigMap(Map<String, String> configMap) {
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
