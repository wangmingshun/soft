package com.sinolife.pos.print.notice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.sinolife.pos.print.dto.PosNoteDTO;

/**
 * 14 投保人名下保单资产证明书
 */
@Component("NoticePrintHandler_14")
public class NoticePrintHandler_14 extends NoticePrintHandlerBase {

	@Override
	public InputStream getTempleteInputStream(Object posNote) throws IOException {
		String fileReportName = "notice_14_" + ((PosNoteDTO)posNote).getTemplateVersion();
		InputStream in = this.templateStore.getTemplateStream("POS", "print", fileReportName);
		return in;
	}

	@Override
	public String getFileName(String key) {
		if(StringUtils.isNotBlank(key)) {
			return "投保人名下保单资产证明书-" + key + ".pdf";
		} else {
			return "投保人名下保单资产证明书-" + getTimeString() + ".pdf";
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