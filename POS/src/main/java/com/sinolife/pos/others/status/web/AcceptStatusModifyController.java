package com.sinolife.pos.others.status.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.sinolife.pos.common.consts.SessionKeys;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.others.status.service.AcceptStatusModifyService;

/**
 * 保全受理状态修改
 * 目前只涉及保单补发
 */
@Controller("acceptStatusModifyController")
@SuppressWarnings("rawtypes")
@SessionAttributes(value =SessionKeys.ACCEPTANCE_ITEM_INPUT)
public class AcceptStatusModifyController {

	@Autowired
	AcceptStatusModifyService modifyService;
	
	@RequestMapping("/others/statusModify")
	public String entry(){
		return "/others/status/modify";
	}
	
	@ModelAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT)
	public ServiceItemsInputDTO getServiceItemsInputDTO() {
		return new ServiceItemsInputDTO();
	}
	
	@RequestMapping("/others/statusModify/query")
	public String query( @ModelAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT) ServiceItemsInputDTO itemsInputDTO, Model model){
		Map map = modifyService.query(itemsInputDTO.getPosNo());
		
		if(map==null){
			model.addAttribute("RETURN_MSG", "查询不到合适的数据，请核实");
		}else{
			model.addAttribute("posInfo", map);
		}
		
		return "/others/status/modify";
	}
	
	@RequestMapping("/others/statusModify/submit")
	public String submit(@ModelAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT) ServiceItemsInputDTO itemsInputDTO, Model model){
		
		modifyService.submit(itemsInputDTO);
		//PlatformContext.getHttpServletRequest().getSession().removeAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT);
		model.addAttribute("RETURN_MSG", "状态修改成功");
		
		return "/others/status/modify";
	}
	
}
