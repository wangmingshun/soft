package com.sinolife.pos.print.notice;

import com.sinolife.sf.platform.runtime.PlatformContext;

public class NoticePrintHandlerFactory {

	public static final String NOTICE_PRINT_HANDLER_FOR = "NoticePrintHandler_";
	
	/**
	 * 取得通知书处理器
	 * @param noticeType
	 * @return
	 */
	public static NoticePrintHandler getNoticePrintHandler(String noticeType) {
		if("51".equals(noticeType) || "52".equals(noticeType)) {
			return (NoticePrintHandler) PlatformContext.getApplicationContext().getBean(NOTICE_PRINT_HANDLER_FOR + "1");
		} else if("53".equals(noticeType) || "54".equals(noticeType)) {
			return (NoticePrintHandler) PlatformContext.getApplicationContext().getBean(NOTICE_PRINT_HANDLER_FOR + "2");
		} else {		
			return (NoticePrintHandler) PlatformContext.getApplicationContext().getBean(NOTICE_PRINT_HANDLER_FOR + noticeType);
		}
	}
}
