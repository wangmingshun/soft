package com.sinolife.pos.print.notice;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 保全申请书
 */

@Component("NoticePrintHandler_application")
public class NoticePrintHandler_application extends NoticePrintHandlerBase {

    @Override
    public InputStream getTempleteInputStream(Object posNote) throws IOException {

        return this.templateStore.getTemplateStream("POS", "print", "pos_application");

    }

	@Override
	public String getFileName(String key) {
		if (StringUtils.isNotBlank(key)) {
			return key + "(申请书).pdf";
		} else {
			return "申请书-" + getTimeString() + ".pdf";
		}
	}

	@Override
	public Object queryPosNoteById(String id) {
		logger.info("queryFillFormApplicationInfoByPosNo:" + id);
		return printService.queryFillFormApplicationInfoByPosNo(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getParameterMap(Object posNote) {
		return (Map<String, Object>) posNote;
	}
}
