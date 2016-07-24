package com.sinolife.pos.common.dao.typehandler;

import java.util.Map;

public interface ArrayTypeResultHandler {
	@SuppressWarnings("rawtypes")
	Map handleResult(Map map);
}
