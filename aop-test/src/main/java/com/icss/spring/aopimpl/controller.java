package com.icss.spring.aopimpl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class controller {
	
	@Autowired
	ArithmeticCalculator aa;
//	@Autowired
//	LoggingAspect ac;
	
	@RequestMapping("/test")
	public void test() {
		int a = aa.add(3, 9);
		System.out.println(a);
//		System.out.println(ac);
	}
}
