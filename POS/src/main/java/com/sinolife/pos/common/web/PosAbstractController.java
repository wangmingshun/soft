package com.sinolife.pos.common.web;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import com.sinolife.sf.platform.runtime.PlatformContext;

public abstract class PosAbstractController {

	protected Logger logger = Logger.getLogger(getClass());
	
    @InitBinder
    public void defaultInitBinder(WebDataBinder binder) {
    	//注册日期类型的属性编辑器
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
        //处理文件上传的属性编辑器
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
        initBinder(binder);
    }

    /**
     * For override
     * @param binder
     */
    protected void initBinder(WebDataBinder binder) {
    	
    }
    
    /**
     * 获取HttpServletRequest
     * @return
     */
    protected HttpServletRequest getHttpServletRequest() {
		return PlatformContext.getHttpServletRequest();
	}
    
    /**
     * 获取HttpServletResponse
     * @return
     */
    protected HttpServletResponse getHttpServletResponse() {
		return PlatformContext.getHttpServletResponse();
	}
	
	/**
	 * 获取HttpSession
	 * @return
	 */
	protected HttpSession getHttpSession() {
		return getHttpServletRequest().getSession();
	}
	
}
