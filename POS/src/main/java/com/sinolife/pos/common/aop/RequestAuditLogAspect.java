package com.sinolife.pos.common.aop;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import com.sinolife.pos.common.annotation.AuditLog;
import com.sinolife.pos.common.annotation.AuditSuccessType;
import com.sinolife.pos.common.util.AuditLogUtil;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.sf.platform.runtime.PlatformContext;

/**
 * 审计日志处理类
 */
@Aspect
@Component
public class RequestAuditLogAspect {

	private Logger logger = Logger.getLogger("PosAuditLogger");
	
	private ThreadLocal<Document> document = new ThreadLocal<Document>();
	
	@Around("com.sinolife.pos.common.aop.PosPointcut.isPosRequestMappingHandler() || com.sinolife.pos.common.aop.PosPointcut.enabledAuditLog()")
	public Object doWithAuditLog(ProceedingJoinPoint pjp) throws Throwable {
		//记录登录用户名
		String userId = PlatformContext.getCurrentUser();
		if(StringUtils.isNotBlank(userId)) {
			appendLogToXML("user", userId);
		}
		
		//记录线程ID
		appendLogToXML("threadId", String.valueOf(Thread.currentThread().getId()));
		
		//记录执行的类
		appendLogToXML("class", pjp.getTarget().getClass().getName());
		
		MethodSignature sign = (MethodSignature) pjp.getStaticPart().getSignature();
		Method method = sign.getMethod();
		//记录拦截的方法名
		appendLogToXML("method", method.getName());
		
		HttpServletRequest request = PlatformContext.getHttpServletRequest();
		//记录请求的URI和请求方法
		if(request != null) {
			appendLogToXML("uri", request.getRequestURI());
			appendLogToXML("requestMehotd", request.getMethod());
		}
		
		AuditLog config = method.getAnnotation(AuditLog.class);
		AuditSuccessType successFor = AuditSuccessType.NO_EXCEPTION;//默认不抛出异常则判定请求处理成功
		if(config != null) {
			successFor = config.value();
		}
		
		Object[] args = pjp.getArgs(); 
		Object retObj = null;
		Throwable throwable = null;
		long timestamp = 0;
		AuditLogUtil.clearContext();
		try {
			//记录请求处理开始时间
			timestamp = System.currentTimeMillis();
			appendLogToXML("beginTime", PosUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss:SSS"));
			retObj = pjp.proceed(args);
			return retObj;
		} catch(Throwable t) {
			throwable = t;
			throw t;
		} finally {
			//记录请求处理结束时间，请求耗费时间
			long endTime = System.currentTimeMillis();
			appendLogToXML("cost", String.valueOf(endTime - timestamp));
			
			boolean success = true;
			if(successFor == AuditSuccessType.NO_EXCEPTION) {
				success = (throwable == null);
			} else if(successFor == AuditSuccessType.RETURN_EQUALS) {
				String compareValue = config.compareValue();
				success = (compareValue != null && compareValue.equals(String.valueOf(retObj)));
			} else if(successFor == AuditSuccessType.RETURN_NESTED_PROP_EQUALS) {
				String compareValue = config.compareValue();
				String nestedPath = config.nestedPath();
				success = (compareValue != null && compareValue.equals(BeanUtils.getNestedProperty(retObj, nestedPath)));
			}
			
			//写入程序中调用AuditLogUtil工具类添加的日志内容
			Map<String, String> logContent = AuditLogUtil.getLogContent();
			if(PosUtils.isNotNullOrEmpty(logContent)) {
				for(Entry<String, String> entry : logContent.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					if("success".equals(key)) {
						success = Boolean.valueOf(value);
					} else {
						appendLogToXML(key, value);
					}
				}
			}
			appendLogToXML("success", String.valueOf(success));
			
			logger.info(getXMLLogAndClear(), throwable);
			
			document.remove();
			AuditLogUtil.clearContext();
		}
	}
	
	private void appendLogToXML(String key, String value) {
		Document doc = document.get();
		if(doc == null) {
			doc = DocumentHelper.createDocument();
			doc.addElement("AuditLog");
			document.set(doc);
		}
		if(doc != null) {
			Element ele = doc.getRootElement().addElement(key);
			ele.addText(value);
		}
	}
	
	private String getXMLLogAndClear() {
		Document doc = document.get();
		if(doc != null) {
			document.remove();
			return doc.asXML();
		}
		return null;
	}
}
