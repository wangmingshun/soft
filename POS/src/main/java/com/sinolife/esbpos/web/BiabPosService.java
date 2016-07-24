package com.sinolife.esbpos.web;

import java.util.Map;

import com.sinolife.sf.esb.EsbMethod;

public interface BiabPosService {

	@EsbMethod(esbServiceId = "com.sinolife.biab.handleBiagESBRequestMap")
    public Map<String,Object> handleBiagESBRequestMap(Map<String,Object> map);

	@EsbMethod(esbServiceId = "com.sinolife.biam.toPartnerEsbHandle")
    public Map<String, Object> toPartnerEsbHandle(Map<String, Object> param);
} 

