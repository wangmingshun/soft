package com.sinolife.pos.print.notice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.sinolife.pos.print.dto.PosNoteDTO;

/**
 * 2 保额分红红利通知信
 */
@Component("NoticePrintHandler_2")
public class NoticePrintHandler_2 extends NoticePrintHandlerBase {

	@Override
	public InputStream getTempleteInputStream(Object posNote) throws IOException {
		return this.templateStore.getTemplateStream("POS", "print", "notice_2_" + ((PosNoteDTO)posNote).getTemplateVersion());
	}

	@Override
	public String getFileName(String key) {
		if(StringUtils.isNotBlank(key)) {
			return "保额分红红利通知信-" + key + ".pdf";
		} else {
			return "保额分红红利通知信-" + getTimeString() + ".pdf";
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
