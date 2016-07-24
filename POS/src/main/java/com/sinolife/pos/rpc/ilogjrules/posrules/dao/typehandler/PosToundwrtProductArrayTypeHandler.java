package com.sinolife.pos.rpc.ilogjrules.posrules.dao.typehandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import oracle.sql.ARRAY;
import oracle.sql.STRUCT;

import com.sinolife.pos.common.dao.typehandler.ArrayTypeResultHandler;

public class PosToundwrtProductArrayTypeHandler implements ArrayTypeResultHandler{

	private Logger logger = Logger.getLogger(getClass());
	
	private String arrayTypePropertyName;
	
	public String getArrayTypePropertyName() {
		return arrayTypePropertyName;
	}

	public void setArrayTypePropertyName(String arrayTypePropertyName) {
		this.arrayTypePropertyName = arrayTypePropertyName;
	}

	public PosToundwrtProductArrayTypeHandler() {}
	
	public PosToundwrtProductArrayTypeHandler(String arrayTypePropertyName) {
		setArrayTypePropertyName(arrayTypePropertyName);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map handleResult(Map map) {
		ARRAY array = (ARRAY) map.get(arrayTypePropertyName);
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			if(array != null) {
				Object[] obj = (Object[]) array.getArray();// mydata可以取出来
				STRUCT struct = null;
				for (int i = 0; i < obj.length; i++) {
					struct = (STRUCT) obj[i];
					Object[] tmp = (Object[]) struct.getAttributes();
					Map<String, Object> item = new HashMap<String, Object>();
					item.put("POLICYNO", String.valueOf(tmp[0]));
					item.put("PRODSEQ", String.valueOf(tmp[1]));
					item.put("PRODUCTCODE", String.valueOf(tmp[2]));
					item.put("UNDWRTTASKTYPE", String.valueOf(tmp[3]));
					item.put("CONTROLNO", String.valueOf(tmp[4]));
					result.add(item);
				}
			}
			map.put(arrayTypePropertyName, result);
		} catch (SQLException e) {
			String errMsg = "ArrayTypeResultHandler error:" + e.getMessage();
			logger.error(errMsg, e);
			throw new RuntimeException(errMsg);
		}
		return map;
	}

}
