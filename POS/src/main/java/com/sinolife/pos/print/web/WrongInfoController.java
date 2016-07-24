package com.sinolife.pos.print.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sinolife.pos.common.include.service.IncludeService;
import com.sinolife.pos.common.web.PosAbstractController;
import com.sinolife.sf.platform.runtime.PlatformContext;
@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller("wrongInfoController")
public class WrongInfoController extends PosAbstractController{

	@Autowired
	IncludeService includeService;
	
	@RequestMapping("/print/wrongInfo")
	public String entry(Model model) {	

		model.addAttribute("userBranchCode",
				includeService.userBranchCode(PlatformContext.getCurrentUser()));

		return "/report/wronginfo";
	}
}
