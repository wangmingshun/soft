package com.sinolife.pos.print.notice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.sinolife.pos.print.dto.PosNoteDTO;

/**
 * 13 保证价值表（非保额分红） 
 */
@Component("NoticePrintHandler_13")
public class NoticePrintHandler_13 extends NoticePrintHandlerBase {

	@Override
	public InputStream getTempleteInputStream(Object posNote) throws IOException {
		return this.templateStore.getTemplateStream("POS", "print", "notice_13_" + ((PosNoteDTO)posNote).getTemplateVersion());
	}

	@Override
	public String getFileName(String key) {
		if(StringUtils.isNotBlank(key)) {
			return "保证价值表（非保额分红）-" + key + ".pdf";
		} else {
			return "保证价值表（非保额分红）-" + getTimeString() + ".pdf";
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