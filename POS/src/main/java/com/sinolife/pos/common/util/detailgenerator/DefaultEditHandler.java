package com.sinolife.pos.common.util.detailgenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.util.PosUtils;

/**
 * 默认编辑处理器
 * processType为配置中指明的编辑processType
 */
public class DefaultEditHandler extends AbstractAcceptDetailColumnHandler
		implements AcceptDetailColumnHandler {

	/* (non-Javadoc)
	 * @see com.sinolife.pos.common.util.detailgenerator.AbstractAcceptDetailColumnHandler#processColumn(java.lang.String, java.lang.Object, java.lang.Object, com.sinolife.pos.common.util.detailgenerator.GroupNo, java.util.Map, com.sinolife.pos.common.dto.PosAcceptDetailDTO, com.sinolife.pos.common.util.detailgenerator.AbstractAcceptDetailColumnHandler.ConfigDTO)
	 */
	@Override
	protected List<PosAcceptDetailDTO> processColumn(String propertyName,
			Object newValue, Object oldValue, GroupNo groupNo,
			Map<String, Object> listItem, PosAcceptDetailDTO generatedDetail,
			ConfigDTO config) {
		
		Object objectValue = listItem.get(config.getObjectValueNameEdit());
		if(StringUtils.isNotBlank(defaultObjectValue)) {
			generatedDetail.setObjectValue(defaultObjectValue);
		}else if(objectValue != null) {
			generatedDetail.setObjectValue(PosUtils.transToString(objectValue));
		}
		
		generatedDetail.setProcessType(config.getProcessTypeEdit());
		
		logger.info(new StringBuffer("generate detail EDIT[")
							.append(generatedDetail.getInfoGroupNo())
							.append("](propName:")
							.append(propertyName)
							.append(", posNo:")
							.append(generatedDetail.getPosNo())
							.append(", posObject:")
							.append(generatedDetail.getPosObject())
							.append(", itemNo:")
							.append(generatedDetail.getItemNo())
							.append(", processType:")
							.append(generatedDetail.getProcessType())
							.append(", oldValue:")
							.append(generatedDetail.getOldValue())
							.append(", newValue:")
							.append(generatedDetail.getNewValue())
							.append(", objectValue:")
							.append(generatedDetail.getObjectValue())
							.append(")").toString());
		
		List<PosAcceptDetailDTO> retList = new ArrayList<PosAcceptDetailDTO>();
		retList.add(generatedDetail);
		return retList;
	}

}
