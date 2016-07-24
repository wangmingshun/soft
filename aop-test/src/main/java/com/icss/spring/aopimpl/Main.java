package com.icss.spring.aopimpl;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("aop.xml");
        ArithmeticCalculator  arithmeticCalculator= applicationContext.getBean(ArithmeticCalculator.class);
        int result = arithmeticCalculator.add(3, 9);
        System.out.println("result = "+result);
	}

}
