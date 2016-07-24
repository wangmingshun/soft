package com.icss.spring.aopimpl;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class LoggingAspect {
    
//	@Before("execution(public int com.icss.spring.aopimpl.ArithmeticCalculatorImpl.add(int , int ))")
//	@Before("execution(* com.icss.spring..*(..))")
	 public void beforeMethod(JoinPoint join) {
		 System.out.println("add方法执行之前打印出来");
	}
}
