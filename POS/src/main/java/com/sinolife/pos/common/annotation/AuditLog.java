package com.sinolife.pos.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuditLog {
	AuditSuccessType value() default AuditSuccessType.NO_EXCEPTION;
	String nestedPath() default "";
	String compareValue() default "";
}
