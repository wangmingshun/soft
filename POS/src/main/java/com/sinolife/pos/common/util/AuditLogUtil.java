package com.sinolife.pos.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 审计日志工具类，可调用工具方法在审计日志中添加记录
 */
public class AuditLogUtil {

	private AuditLogUtil() {}
	
	
	private Map<String, String> logContent = new HashMap<String, String>();
	private static ThreadLocal<AuditLogUtil> intance = new ThreadLocal<AuditLogUtil>();
	
	private static AuditLogUtil getInstance() {
		AuditLogUtil alu = intance.get();
		if(alu == null) {
			alu = new AuditLogUtil();
			intance.set(alu);
		}
		return alu;
	}
	
	public static void setSuccess(boolean success) {
		getInstance().logContent.put("success", String.valueOf(success));
	}
	
	public static void setBusinessId(String businessId) {
		getInstance().logContent.put("businessId", businessId);
	}
	
	public static void addAuditLog(String logKey, String logValue) {
		getInstance().logContent.put(logKey, logValue);
	}
	
	public static Map<String, String> getLogContent() {
		return getInstance().logContent;
	}
	
	public static void clearContext() {
		getInstance().logContent.clear();
	}
}
