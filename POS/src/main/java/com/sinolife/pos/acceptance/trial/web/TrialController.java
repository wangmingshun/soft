package com.sinolife.pos.acceptance.trial.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.sinolife.pos.acceptance.branch.dto.ClientLocateCriteriaDTO;
import com.sinolife.pos.acceptance.branch.service.BranchQueryService;
import com.sinolife.pos.acceptance.branch.web.ClientLocateCriteriaValidator;
import com.sinolife.pos.acceptance.trial.dto.TrialInfoDTO;
import com.sinolife.pos.acceptance.trial.service.TrialService;
import com.sinolife.pos.common.consts.SessionKeys;
import com.sinolife.pos.common.consts.ViewNames;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.web.PosAbstractController;

/**
 * 试算控制器
 */
@Controller
@RequestMapping("/acceptance/trial")
@SessionAttributes({SessionKeys.ACCEPTANCE_CLIENT_LOCATE_CRETERIA, SessionKeys.ACCEPTANCE_CLIENT_INFO_LIST, SessionKeys.ACCEPTANCE_TRIAL_INFO, SessionKeys.ACCEPTANCE_ITEM_INPUT})
public class TrialController extends PosAbstractController{
	
	/* 服务 */
	@Autowired
	private BranchQueryService queryService;
	
	@Autowired
	private TrialService trialService;
	
	@Autowired
	private TransactionTemplate transactionTemplate;
	
	/* 校验器 */
	@Autowired
	private ClientLocateCriteriaValidator clientLocateCriteriaValidator;
	
	@ModelAttribute(SessionKeys.ACCEPTANCE_APPLY_INFO)
	public PosApplyBatchDTO getPosApplyBatchDTO() {
		return new PosApplyBatchDTO();
	}

	@ModelAttribute(SessionKeys.ACCEPTANCE_CLIENT_LOCATE_CRETERIA)
	public ClientLocateCriteriaDTO getClientLocateCriteriaDTO() {
		return new ClientLocateCriteriaDTO();
	}
	
	@ModelAttribute(SessionKeys.ACCEPTANCE_TRIAL_INFO)
	public TrialInfoDTO getTrialInfoDTO() {
		return new TrialInfoDTO();
	}
	
	/**
	 * 试算入口
	 */
	@RequestMapping("/entry")
	public String entry(SessionStatus sessionStatus) {
		sessionStatus.setComplete();
		return "redirect:/acceptance/trial/clientLocate.do";
	}
	
	/**
	 * 展示或者返回客户定位页面，录入条件
	 */
	@RequestMapping(value = "/clientLocate", method = RequestMethod.GET)
	public String clientLocateEntry(ModelMap model) {
		model.addAttribute("TRIAL_FLAG", "Y");
		return ViewNames.ACCEPTANCE_BRANCH_CLIENT_LOCATE;
	}

	/**
	 * 客户查询，提交查询
	 */
	@RequestMapping(value = "/clientLocate", method = RequestMethod.POST)
	public ModelAndView clientLocateSubmit(@ModelAttribute(SessionKeys.ACCEPTANCE_CLIENT_LOCATE_CRETERIA) ClientLocateCriteriaDTO criteria,
			BindingResult bindingResult) {
		ModelAndView mav = new ModelAndView(ViewNames.ACCEPTANCE_BRANCH_CLIENT_LOCATE);
		mav.addObject("TRIAL_FLAG", "Y");
		
		if (criteria != null) {
			// 校验参数合法性
			clientLocateCriteriaValidator.validate(criteria, bindingResult);
			if (!bindingResult.hasErrors()) {
				List<ClientInformationDTO> clientList = queryService.queryClientByCriteria(criteria);
				if (clientList != null && !clientList.isEmpty()) {
					mav.addObject(SessionKeys.ACCEPTANCE_CLIENT_INFO_LIST, clientList);
					// 查询到客户则跳转到客户选择页面
					mav.setViewName("redirect:/acceptance/trial/clientSelect.do");
				} else {
					mav.addObject("errorMsg", "找不到客户");
				}
			}
		}
		return mav;
	}
	
	/**
	 * 展示或返回客户查询结果列表页面
	 */
	@RequestMapping(value = "/clientSelect", method = RequestMethod.GET)
	public String clientSelectEntry(ModelMap model) {
		model.addAttribute("TRIAL_FLAG", "Y");
		return ViewNames.ACCEPTANCE_BRANCH_CLIENT_SELECT;
	}

	/**
	 * 选定受理客户，保单，项目等提交
	 */
	@RequestMapping(value = "/clientSelect", method = RequestMethod.POST)
	public ModelAndView clientSelectSubmit(@RequestParam String selectClientNo, @ModelAttribute(SessionKeys.ACCEPTANCE_TRIAL_INFO) final TrialInfoDTO trialInfo, @ModelAttribute(SessionKeys.ACCEPTANCE_CLIENT_INFO_LIST) List<ClientInformationDTO> clientList) {
		ModelAndView mav = new ModelAndView(ViewNames.ACCEPTANCE_BRANCH_CLIENT_SELECT);
		mav.addObject("TRIAL_FLAG", "Y");
		mav.addObject(SessionKeys.ACCEPTANCE_TRIAL_INFO, trialInfo);
		
		// 根据客户号从查询出的客户列表缓存中匹配客户
		ClientInformationDTO foundClient = null;
		for (ClientInformationDTO client : clientList) {
			if (client.getClientNo().equals(selectClientNo)) {
				foundClient = client;
				break;
			}
		}
		if (foundClient == null)
			throw new RuntimeException("找不到客户：" + selectClientNo);

		
		//查询客户作为投保人和被保人的保单
		List<String> clientPolicyNoList = queryService.queryPolicyNoListByClientNo(selectClientNo);
		if(clientPolicyNoList == null || clientPolicyNoList.isEmpty()) {
			mav.addObject("errorMsg", "客户名下没有保单");
		} else if(!clientPolicyNoList.contains(trialInfo.getPolicyNo())) {
			mav.addObject("errorMsg", "选定的客户不是录入保单的投保人或被保人");
		} else {
			//选定客户，则初始化申请信息
			trialInfo.setClientNo(selectClientNo);
			try {
				ServiceItemsInputDTO itemsInputDTO = transactionTemplate.execute(new TransactionCallback<ServiceItemsInputDTO>(){
					@Override
					public ServiceItemsInputDTO doInTransaction(TransactionStatus transactionStatus) {
						try {
							return trialService.trialBatchAccept(trialInfo);
						} finally {
							transactionStatus.setRollbackOnly();
						}
					}
				});
				mav.addObject(SessionKeys.ACCEPTANCE_ITEM_INPUT, itemsInputDTO);
				mav.setViewName("redirect:/acceptance/trial/acceptDetailInput.do");
			} catch(Exception e) {
				logger.error(e);
				mav.addObject("errorMsg", "生成试算信息出错：" + e.getMessage());
			}
		}
		
		return mav;
	}
	
	/**
	 * 受理明细录入入口
	 */
	@RequestMapping(value="/acceptDetailInput", method=RequestMethod.GET)
	public String acceptDetailInputEntry(@ModelAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT) ServiceItemsInputDTO itemsInputDTO, ModelMap model) {
		model.addAttribute("TRIAL_FLAG", "Y");
		return ViewNames.ACCEPTANCE_BRANCH_ACCEPT_DETAIL_INPUT + itemsInputDTO.getServiceItems();
	}
	
	/**
	 * 受理明细录入提交，并试算出批文
	 */
	@RequestMapping(value="/acceptDetailInput", method=RequestMethod.POST)
	public ModelAndView acceptDetailInputSubmit(@ModelAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT) final ServiceItemsInputDTO itemsInputDTO, final BindingResult bindingResult, @ModelAttribute(SessionKeys.ACCEPTANCE_TRIAL_INFO) final TrialInfoDTO trialInfo) {
		ModelAndView mav = new ModelAndView(ViewNames.ACCEPTANCE_BRANCH_ACCEPT_DETAIL_INPUT + itemsInputDTO.getServiceItems());
		mav.addObject("TRIAL_FLAG", "Y");
		try {
			List<Map<String, Object>> processResultList = transactionTemplate.execute(new TransactionCallback<List<Map<String, Object>>>() {
				@Override
				public List<Map<String, Object>> doInTransaction(TransactionStatus transactionStatus) {
					try {
						return trialService.trialDetailSubmit(trialInfo, itemsInputDTO, bindingResult);
					} finally {
						transactionStatus.setRollbackOnly();
					}
				}
			});
			if(!bindingResult.hasErrors()) {
				mav.addObject(SessionKeys.ACCEPTANCE_PROCESS_RESULT_LIST, processResultList);
				mav.setViewName(ViewNames.ACCEPTANCE_BRANCH_PROCESS_RESULT);
			}
		} catch(Exception e) {
			logger.error(e);
			mav.addObject("VALIDATE_MSG", "试算出错：" + e.getMessage());
		}
		return mav;
	}
	

}
