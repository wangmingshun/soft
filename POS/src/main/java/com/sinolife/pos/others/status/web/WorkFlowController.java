package com.sinolife.pos.others.status.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import com.sinolife.pos.common.consts.SessionKeys;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.others.status.service.AcceptStatusModifyService;

@Controller("workFlowController")
public class WorkFlowController {

	@ModelAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT)
	public ServiceItemsInputDTO getServiceItemsInputDTO() {
		return new ServiceItemsInputDTO();
	}

	@Autowired
	AcceptStatusModifyService modifyService;

	@RequestMapping("/others/workFlowContinue")
	public String entry() {
		return "/others/status/workFlowContinue";
	}

	/**
	 * 失败工作流继续推动
	 * 
	 * @param posNo
	 * @param model
	 * @return
	 */
	@RequestMapping("/others/workFlowContinue/submit")
	public String workFlowContinue(
			@ModelAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT) ServiceItemsInputDTO itemsInputDTO,
			Model model) {

		String alterMessage = modifyService.workFlowContinue(itemsInputDTO
				.getPosNo());
		model.addAttribute("RETURN_MSG", alterMessage);
		return "/others/status/workFlowContinue";
	}
}
