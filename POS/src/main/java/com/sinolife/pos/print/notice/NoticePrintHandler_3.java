package com.sinolife.pos.print.notice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.sinolife.pos.print.dto.PosNoteDTO;

/**
 * 3 保险合同到期终止通知书
 */
@Component("NoticePrintHandler_3")
public class NoticePrintHandler_3 extends NoticePrintHandlerBase {

	@Override
	public InputStream getTempleteInputStream(Object posNote) throws IOException {
		return this.templateStore.getTemplateStream("POS", "print", "notice_3_" + ((PosNoteDTO)posNote).getTemplateVersion());
	}

	@Override
	public String getFileName(String key) {
		if(StringUtils.isNotBlank(key)) {
			return "保险合同到期终止通知书-" + key + ".pdf";
		} else {
			return "保险合同到期终止通知书-" + getTimeString() + ".pdf";
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