package com.sinolife.pos.common.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PosPointcut {
	
	@Pointcut("within(com.sinolife.pos..*)")
	public void inPos() {}
	
	/**
	 * 公共方法切点
	 */
	@Pointcut("execution(public * *(..))")
	public void isPublic() {}
	
	/**
	 * 控制器切点
	 */
	@Pointcut("inPos() && within(*..web.*Controller)")
	public void inPosController() {}
	
	/**
	 * Web请求处理切点
	 */
	@Pointcut("isPublic() && inPos() && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public void isPosRequestMappingHandler() {}
	
	@Pointcut("inPos() && @annotation(com.sinolife.pos.common.annotation.AuditLog)")
	public void enabledAuditLog() {}
	
	/**
	 * 服务切点
	 */
	@Pointcut("inPos() && within(*..service.*Service)")
	public void inPosService() {}
	
}
