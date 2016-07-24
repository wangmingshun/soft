package com.sinolife.pos.print.notice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.sinolife.pos.print.dto.PosNoteDTO;

/**
 * 11 投资连结保险周年报告
 */
@Component("NoticePrintHandler_11")
public class NoticePrintHandler_11 extends NoticePrintHandlerBase {

	@Override
	public InputStream getTempleteInputStream(Object posNote) throws IOException {
		return this.templateStore.getTemplateStream("POS", "print", "notice_11_" + ((PosNoteDTO)posNote).getTemplateVersion());
	}

	@Override
	public String getFileName(String key) {
		if(StringUtils.isNotBlank(key)) {
			return "投资连结保险周年报告-" + key + ".pdf";
		} else {
			return "投资连结保险周年报告-" + getTimeString() + ".pdf";
		}
	}

	@Override
	public Object queryPosNoteById(String id) {
		return printService.queryPosNoteByDetailSequenceNo(id);
	}

	@Override
	public Map<String, Object> getParameterMap(Object posNote) {
		return ((PosNoteDTO)posNote).getDetailMap();
	}
	
}