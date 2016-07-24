package com.sinolife.pos.print.notice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 批单
 */
@SuppressWarnings("rawtypes")
@Component("NoticePrintHandler_mail")
public class NoticePrintHandler_mail extends NoticePrintHandlerBase {

	@Override
	public InputStream getTempleteInputStream(Object posNote)
			throws IOException {
			return this.templateStore.getTemplateStream("POS", "print",
					"endorsement_net");
	}

	@Override
	public String getFileName(String key) {
		if (StringUtils.isNotBlank(key)) {
			return key + "(批单).pdf";
		} else {
			return "批单-" + getTimeString() + ".pdf";
		}
	}

	@Override
	public Object queryPosNoteById(String id) {
		logger.info("queryEndorsementInfoByPosNo:" + id);
		return printService.queryEndorsementInfoByPosNo(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getParameterMap(Object posNote) {
		return (Map<String, Object>) posNote;
	}
}
