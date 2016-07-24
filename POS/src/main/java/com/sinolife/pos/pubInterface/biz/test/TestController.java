package com.sinolife.pos.pubInterface.biz.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.common.web.PosAbstractController;
import com.sinolife.pos.print.dao.PrintDAO;
import com.sinolife.pos.pubInterface.biz.service.DevelopPlatformInterface;
import com.sinolife.sf.framework.controller.BrowserNoCache;


@Controller
public class TestController extends PosAbstractController {
    @Autowired
	CommonQueryDAO commonQueryDAO;
    @Autowired
    PrintDAO printDAO;
    @Autowired @Qualifier("posForMspPlatformInterface")
    DevelopPlatformInterface developPlatformInterface;

    @RequestMapping(value = "/test/test/test")
    @BrowserNoCache(enableNoCache=false)
	@Transactional(propagation=Propagation.REQUIRED)
    public ModelAndView clientWeathNoticePreview(@RequestParam String clientNo,
    		HttpServletResponse response) {

    	Map map = developPlatformInterface.getClientWeathNoticeStream(clientNo);
    	OutputStream os = null;
    	InputStream is = null;
		File file = null;
		try {
			os = response.getOutputStream();
			File files = map.get("file") != null ? (File) map.get("file") : null;
			is = new FileInputStream(files);
			logger.info(is);
			//((NoticePrintHandler)map.get("handler")).processPdfFileNameHeader(getHttpServletRequest(), getHttpServletResponse(), clientNo);
			IOUtils.copy(is, os);
			logger.error("-------------------------------------clientNo:" + clientNo);
			return null;
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("打印资产证明书出错：" + e.getMessage());
		} finally {
			PosUtils.safeCloseOuputStream(os);
			PosUtils.safeCloseInputStream(is);
		}
    }
}
