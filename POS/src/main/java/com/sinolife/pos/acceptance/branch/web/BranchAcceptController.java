package com.sinolife.pos.acceptance.branch.web;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.unihub.framework.util.common.DateUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.sinolife.pos.acceptance.branch.dto.ClientLocateCriteriaDTO;
import com.sinolife.pos.acceptance.branch.service.BranchAcceptService;
import com.sinolife.pos.acceptance.branch.service.BranchQueryService;
import com.sinolife.pos.common.consts.SessionKeys;
import com.sinolife.pos.common.consts.ViewNames;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.LoginUserInfoDTO;
import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.common.dto.PosApplyFilesDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.common.web.PosAbstractController;
import com.sinolife.sf.platform.http.HttpServletRequest;
import com.sinolife.sf.platform.runtime.PlatformContext;

/**
 * 机构端逐单受理控制器.
 */
@Controller
@RequestMapping("/acceptance/branch/")
@SessionAttributes(value = { SessionKeys.ACCEPTANCE_CLIENT_LOCATE_CRETERIA, SessionKeys.ACCEPTANCE_CLIENT_INFO_LIST, SessionKeys.ACCEPTANCE_APPLY_INFO })
public class BranchAcceptController extends PosAbstractController {

	/* 服务 */
	@Autowired
	private BranchQueryService queryService;

	@Autowired
	private BranchAcceptService acceptService;

	/* 校验器 */
	@Autowired
	private ClientLocateCriteriaValidator clientLocateCriteriaValidator;
	
	@Autowired
	private PosApplyBatchValidator posApplyBatchValidator;

	@ModelAttribute(SessionKeys.ACCEPTANCE_APPLY_INFO)
	public PosApplyBatchDTO getPosApplyBatchDTO() {
		return new PosApplyBatchDTO();
	}

	@ModelAttribute(SessionKeys.ACCEPTANCE_CLIENT_LOCATE_CRETERIA)
	public ClientLocateCriteriaDTO getClientLocateCriteriaDTO() {
		return new ClientLocateCriteriaDTO();
	}

	/**
	 * 受理入口：
	 * 通过sf框架portal各模块菜单配置文件/SL_POS/src/main/resource/SF-Menu.xml“逐单受理”入口
	 * 进入逐单受理
	 */
	@RequestMapping(value = "/entry")
	public String entry(SessionStatus status) {
		// 清除Session数据
		status.setComplete();
		return "redirect:/acceptance/branch/clientLocate.do";
	}

	/**
	 * 展示或者返回客户定位页面，录入条件
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/clientLocate", method = RequestMethod.GET)
	public String clientLocateEntry() {
		return ViewNames.ACCEPTANCE_BRANCH_CLIENT_LOCATE;
	}

	/**
	 * 客户查询，提交查询
	 * 
	 * @param criteria
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value = "/clientLocate", method = RequestMethod.POST)
	public ModelAndView clientLocateSubmit(@ModelAttribute(SessionKeys.ACCEPTANCE_CLIENT_LOCATE_CRETERIA) ClientLocateCriteriaDTO criteria, BindingResult bindingResult) {
		ModelAndView mav = new ModelAndView(ViewNames.ACCEPTANCE_BRANCH_CLIENT_LOCATE);
		if (criteria != null) {
			// 校验参数合法性
			clientLocateCriteriaValidator.validate(criteria, bindingResult);
			if (!bindingResult.hasErrors()) {
				List<ClientInformationDTO> clientList = queryService.queryClientByCriteria(criteria);
				if (PosUtils.isNotNullOrEmpty(clientList)) {
					mav.addObject(SessionKeys.ACCEPTANCE_CLIENT_INFO_LIST, clientList);
					// 查询到客户则跳转到客户选择页面
					mav.setViewName("redirect:/acceptance/branch/clientSelect.do");
				} else {
					mav.addObject("errorMsg", "该客户不存在");
				}
			}
		} else {
			logger.info("criteria is null, maybe session is time out. redirect to entry");
			mav.setViewName("redirect:/acceptance/branch/entry.do");
		}
		return mav;
	}

	/**
	 * 展示或返回客户查询结果列表页面
	 * @return ModelAndView
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/clientSelect", method = RequestMethod.GET)
	public String clientSelectEntry() {
		List<ClientInformationDTO> clientList = (List<ClientInformationDTO>) getHttpSession().getAttribute(SessionKeys.ACCEPTANCE_CLIENT_INFO_LIST);
		if(clientList == null || clientList.isEmpty()) {
			logger.info("clientList is null, maybe session is time out. redirect to entry");
			return "redirect:/acceptance/branch/entry.do";
		}
		return ViewNames.ACCEPTANCE_BRANCH_CLIENT_SELECT;
	}
	
	
	/**
	 * 判断是否有生存金未领取
	 * @param empNo
	 * @return
	 */
	@RequestMapping(value = "/checkHasSurvivalNotPayByPolicyNo")
	@ResponseBody
	public boolean queryStaffByEmpNo(String policyNo) {
		boolean isHasSurvivalNotPay=queryService.checkHasSurvivalNotPay(policyNo);
		
		
		return isHasSurvivalNotPay;
	}
	
	/**
	 * 选定受理客户,提交
	 * 
	 * @param clientNo
	 * @param clientList
	 * @return
	 */
	@RequestMapping(value = "/clientSelect", method = RequestMethod.POST)
	public ModelAndView clientSelectSubmit(@RequestParam("selectClientNo") String clientNo, @ModelAttribute(SessionKeys.ACCEPTANCE_CLIENT_INFO_LIST) List<ClientInformationDTO> clientList) {
		ModelAndView mav = new ModelAndView(ViewNames.ACCEPTANCE_BRANCH_CLIENT_SELECT);
		
		if(clientList == null || clientList.isEmpty()) {
			logger.info("clientList is null, maybe session is time out. redirect to entry");
			mav.setViewName("redirect:/acceptance/branch/entry.do");
			return mav;
		}
		
		// 根据客户号从查询出的客户列表缓存中匹配客户
		ClientInformationDTO foundClient = (ClientInformationDTO) PosUtils.findInCollectionByProperty(clientList, "clientNo", clientNo);
		if (foundClient == null) {
			mav.addObject("errorMsg", "找不到客户：" + clientNo);
			return mav;
		}
		
		
		
		//选定客户，则强制初始化申请信息
		PosApplyBatchDTO applyInfo = new PosApplyBatchDTO();
		LoginUserInfoDTO userInfo = PosUtils.getLoginUserInfo();
		applyInfo.setClientNo(clientNo);
		applyInfo.setClientInfo(foundClient);
		applyInfo.setAcceptor(userInfo.getLoginUserID());
		applyInfo.setCounterNo(userInfo.getLoginUserCounterNo());
		
		//查询客户作为投保人和被保人的保单，作为页面限制保单号录入的条件
		List<String> policyNoList = queryService.queryPolicyNoListByClientNo(clientNo);
		applyInfo.setClientPolicyNoList(policyNoList);
		
		
		
		
		if(PosUtils.isNullOrEmpty(policyNoList)) {
			logger.info("clientNo:" + clientNo + " found no policy");
			mav.addObject("errorMsg", "客户名下没有该客户作为投保人或被保人的保单");
		} else {
			mav.addObject(SessionKeys.ACCEPTANCE_APPLY_INFO, applyInfo);
			/*判断客户数据库证件有效期是否存在 edit by wangmingshun start */
			getHttpSession().setAttribute("clientIdnoValidityDateIsExist", 
					foundClient.getIdnoValidityDate()!=null?"true":"false");
			/*判断客户数据库证件有效期是否存在 edit by wangmingshun end */
		//	mav.addObject("isHasSurvivalNotPay",isHasSurvivalNotPay);
			mav.setViewName("redirect:/acceptance/branch/applyInfoInput.do");
		}
		
		//edit by wangmingshun,此处是获取登陆用户是否需要进行保全项目录入过滤
		//获取默认机构
		String approveBranchDefault = 
			PlatformContext.getSystemDefination().getAttribute("approveBranch");
		//获取当前用户
		String userId = PlatformContext.getCurrentUser();
		//根据用户获取用户所在机构
		String approveBranch = queryService.getUserApproveBranch(userId);
		getHttpSession().setAttribute("isFilterServiceItems", approveBranchDefault.equals(approveBranch));
		if(approveBranchDefault.equals(approveBranch)) {
			getHttpSession().setAttribute("serviceItems", queryService.getUserCanDoServiceItems(userId));
		}
		
		return mav;
	}

	/**
	 * 展示或返回申请信息录入页面，录入
	 * @param applyInfo
	 * @return
	 */
	@RequestMapping(value = "/applyInfoInput", method = RequestMethod.GET)
	public String applyInfoInputEntry(@ModelAttribute(SessionKeys.ACCEPTANCE_APPLY_INFO) PosApplyBatchDTO applyInfo) {
		if(applyInfo.getClientInfo() == null) {
			logger.info("clientInfo is null, maybe session is time out. redirect to entry");
			return "redirect:/acceptance/branch/entry.do";
		}
		return ViewNames.ACCEPTANCE_BRANCH_APPLY_INFO_INPUT;
	}

	/**
	 * 新增申请书
	 * 
	 * @param batch
	 * @return
	 */
	@RequestMapping(value = "/applyInfoInput", method = RequestMethod.POST, params = "new")
	public ModelAndView addNewApplyInfo(@ModelAttribute(SessionKeys.ACCEPTANCE_APPLY_INFO) PosApplyBatchDTO applyInfo) {
		ModelAndView mav = new ModelAndView(ViewNames.ACCEPTANCE_BRANCH_APPLY_INFO_INPUT);
		
		if(applyInfo.getClientInfo() == null) {
			logger.info("clientInfo is null, maybe session is time out. redirect to entry");
			mav.setViewName("redirect:/acceptance/branch/entry.do");
			return mav;
		}
		
		//新增一个空的申请书对象
		applyInfo.getApplyFiles().add(new PosApplyFilesDTO());
		
		//将新增的申请书显示在最前面
		mav.addObject("selectedIndex", applyInfo.getApplyFiles().size() - 1);
		
		return mav;
	}

	/**
	 * 删除指定申请书
	 * 
	 * @param applyInfo
	 * @param index
	 * @return
	 */
	@RequestMapping(value = "/applyInfoInput", method = RequestMethod.POST, params = "delete")
	public ModelAndView deleteApplyInfo(@ModelAttribute(SessionKeys.ACCEPTANCE_APPLY_INFO) PosApplyBatchDTO applyInfo, @RequestParam("index") int[] index) {
		ModelAndView mav = new ModelAndView(ViewNames.ACCEPTANCE_BRANCH_APPLY_INFO_INPUT);
		
		if(applyInfo.getClientInfo() == null) {
			logger.info("clientInfo is null, maybe session is time out. redirect to entry");
			mav.setViewName("redirect:/acceptance/branch/entry.do");
			return mav;
		}
		
		//根据元素在数组中的索引进行删除，因此需要保证从索引大的元素开始删除
		List<PosApplyFilesDTO> files = applyInfo.getApplyFiles();
		int idxFirst = 0;
		if (index != null && index.length > 0) {
			Arrays.sort(index);
			for (int i = index.length - 1; i >= 0; i--) {
				files.remove(index[i]);
				idxFirst = index[i];
				logger.info("delete by index -> " + idxFirst);
			}
		}
		//如果申请书全部被删除，则需要新增一个
		if (files.size() == 0) {
			files.add(new PosApplyFilesDTO());
		}
		
		//删除后展示占据该申请书位置的后一个申请书，不存在，则展示最后一个
		if(idxFirst > applyInfo.getApplyFiles().size() - 1) {
			mav.addObject("selectedIndex", applyInfo.getApplyFiles().size() - 1);
		} else {
			mav.addObject("selectedIndex", idxFirst);
		}
		return mav;
	}
	
	/**
	 * 申请书信息保存,提交
	 * @param applyInfo
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value = "/applyInfoInput", method = RequestMethod.POST)
	public ModelAndView applyInfoInputSubmit(@ModelAttribute(SessionKeys.ACCEPTANCE_APPLY_INFO) PosApplyBatchDTO applyInfo, BindingResult bindingResult,Model model) {
		ModelAndView mav = new ModelAndView(ViewNames.ACCEPTANCE_BRANCH_APPLY_INFO_INPUT);
		logger.info("applyInfoInputSubmit PosApplyBatchDTO:" + PosUtils.describBeanAsJSON(applyInfo));
		// 获取保单号
		String policyNo = "";
		List<PosApplyFilesDTO> posApplyFilesDTOList= applyInfo.getApplyFiles();
		for(int a =0; a<posApplyFilesDTOList.size();a++){
			policyNo= posApplyFilesDTOList.get(a).getPolicyNo();
		}
		//根据保单号查询是否是电销(0不代表电销，1代表是电销)
		int count = acceptService.getPolicyNoCount(policyNo);
		if(count ==0 ){
			applyInfo.setMarking("Y");
			if (applyInfo == null || applyInfo.getClientInfo() == null) {
				logger.info("applyInfo or clientInfo is null, maybe session is time out. redirect to entry");
				mav.setViewName("redirect:/acceptance/branch/entry");
				return mav;
			}
			posApplyBatchValidator.validate(applyInfo, bindingResult);
			if (bindingResult.hasErrors()) {
				return mav;
			}
			
			//从客户信息中获取证件有效期
			if(applyInfo.getClientInfo().getIsLongidnoValidityDate()){
				applyInfo.setIdnoValidityDate(DateUtil.stringToDate("9999-1-1", "yyyy-MM-dd"));
			}else{
				applyInfo.setIdnoValidityDate(applyInfo.getClientInfo().getIdnoValidityDate());
			}
			
			//生成保全项目列表
			acceptService.generatePosInfoList(applyInfo, false);
			
			//查询保全未结案信息
			List<PosInfoDTO> notCompletePosInfoList = queryService.queryNotCompletePosInfoRecord(applyInfo.getClientNo());
			applyInfo.setNotCompletePosInfoList(notCompletePosInfoList);
			
			mav.setViewName("redirect:/acceptance/branch/acceptItemConfirm.do");
		}else {
			applyInfo.setMarking("N");
			if (applyInfo == null || applyInfo.getClientInfo() == null) {
				logger.info("applyInfo or clientInfo is null, maybe session is time out. redirect to entry");
				mav.setViewName("redirect:/acceptance/branch/entry");
				return mav;
			}
			posApplyBatchValidator.validate(applyInfo, bindingResult);
			if (bindingResult.hasErrors()) {
				return mav;
			}
			
			//从客户信息中获取证件有效期
			if(applyInfo.getClientInfo().getIsLongidnoValidityDate()){
				applyInfo.setIdnoValidityDate(DateUtil.stringToDate("9999-1-1", "yyyy-MM-dd"));
			}else{
				applyInfo.setIdnoValidityDate(applyInfo.getClientInfo().getIdnoValidityDate());
			}
			
			//生成保全项目列表
			acceptService.generatePosInfoList(applyInfo, false);
			
			//查询保全未结案信息
			List<PosInfoDTO> notCompletePosInfoList = queryService.queryNotCompletePosInfoRecord(applyInfo.getClientNo());
			applyInfo.setNotCompletePosInfoList(notCompletePosInfoList);
			
			model.addAttribute("policyNo", "policyNo");
			mav.setViewName("redirect:/acceptance/branch/acceptItemConfirm.do");
		}
		return mav;
	}

	/**
	 * 展示或返回保全项目确认页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/acceptItemConfirm", method = RequestMethod.GET)
	public String acceptItemConfirmEntry() {
		PosApplyBatchDTO applyInfo = (PosApplyBatchDTO) getHttpSession().getAttribute(SessionKeys.ACCEPTANCE_APPLY_INFO);
		if (applyInfo == null || applyInfo.getClientInfo() == null) {
			logger.info("applyInfo or clientInfo is null, maybe session is time out. redirect to entry");
			return "redirect:/acceptance/branch/entry.do";
		}
		return ViewNames.ACCEPTANCE_BRANCH_ACCEPT_ITEM_CONFIRM;
	}

	/**
	 * 查看规则检查不通过明细
	 */
	@RequestMapping(value = "/unpassRuleDetail")
	@ResponseBody
	public Map<String, Object> unpassRuleDetail(@RequestParam int index, @ModelAttribute(SessionKeys.ACCEPTANCE_APPLY_INFO) PosApplyBatchDTO applyInfo) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			retMap.put("unpassRuleInfo", applyInfo.getPosInfoList().get(index).getVerifyResult().getRuleInfos());
			retMap.put("flag", "Y");
		}catch(Exception e) {
			retMap.put("flag", "N");
			retMap.put("msg", "无法取得规则不通过明细信息：" + e.getMessage());
		}
		return retMap;
	}
	
	/**
	 * 保全项目确认提交
	 * 
	 * @return
	 */
	@RequestMapping(value = "/acceptItemConfirm", method = RequestMethod.POST)
	public ModelAndView acceptItemConfirmSubmit(@ModelAttribute(SessionKeys.ACCEPTANCE_APPLY_INFO) PosApplyBatchDTO applyInfo) {
		ModelAndView mav = new ModelAndView(ViewNames.ACCEPTANCE_BRANCH_ACCEPT_ITEM_CONFIRM);
		
		if (applyInfo == null || applyInfo.getClientInfo() == null) {
			logger.info("applyInfo or clientInfo is null, maybe session is time out. redirect to entry");
			mav.setViewName("redirect:/acceptance/branch/entry.do");
			return mav;
		}
		
		//调用服务，生成默认的捆绑顺序
		acceptService.generateBindingOrder(applyInfo);
		
		mav.setViewName("redirect:/acceptance/branch/bindingOrderAdjust.do");
		return mav;
	}

	/**
	 * 展示或返回绑定顺序调整页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/bindingOrderAdjust", method = RequestMethod.GET)
	public String bindingOrderAdjustEntry() {
		PosApplyBatchDTO applyInfo = (PosApplyBatchDTO) getHttpSession().getAttribute(SessionKeys.ACCEPTANCE_APPLY_INFO);
		if (applyInfo == null || applyInfo.getClientInfo() == null) {
			logger.info("applyInfo or clientInfo is null, maybe session is time out. redirect to entry");
			return "redirect:/acceptance/branch/entry.do";
		}
		return ViewNames.ACCEPTANCE_BRANCH_BINDING_ORDER_ADJUST;
	}

	/**
	 * 捆绑顺序调整提交
	 * 
	 * @return
	 */
	@RequestMapping(value = "/bindingOrderAdjust", method = RequestMethod.POST)
	@Transactional(propagation=Propagation.REQUIRED)
	public ModelAndView bindingOrderAdjustSubmit(@ModelAttribute(SessionKeys.ACCEPTANCE_APPLY_INFO) PosApplyBatchDTO applyInfo, SessionStatus status) {
		ModelAndView mav = new ModelAndView();
		
		if (applyInfo == null || applyInfo.getClientInfo() == null) {
			logger.info("applyInfo or clientInfo is null, maybe session is time out. redirect to entry");
			mav.setViewName("redirect:/acceptance/branch/entry.do");
			return mav;
		}
		
		// 调用批次受理接口写表
		applyInfo = acceptService.batchAccept(applyInfo);
		
		//清空缓存
		status.setComplete();
		
		String posBatchNo = applyInfo.getPosBatchNo();
		String posNo = acceptService.getNextPosNoInBatch(posBatchNo);
		if(posNo == null) {
			//批次中应该只有受理规则检查不通过的受理了，直接转向批次录入页面
			mav.setViewName("redirect:/acceptance/branch/entry.do");
		} else {
			//跳转至保全详细信息录入界面入口
			mav.setViewName("redirect:/acceptance/branch/acceptDetailInput/" + posBatchNo);
		}
		
		return mav;
	}

	/**
	 * 查询保单原账户信息
	 */
	@RequestMapping(value = "/policybankacctno")
	@ResponseBody
	public Map<String, String> getPolicyBankacctno(String policyNo) {
		return queryService.getPolicyBankacctno(policyNo);
	}
	
	@RequestMapping(value = "/getAutographImageURL")
	@ResponseBody
	public Map<String, Object> getAutographImageURL(@RequestParam String policyNo, @RequestParam String clientNo) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		String url = queryService.getAutographImageURL(policyNo, clientNo);
		if(StringUtils.isBlank(url)) {
			retMap.put("flag", "N");
			retMap.put("msg", "找不到签名影像信息!");
		} else {
			retMap.put("flag", "Y");
			retMap.put("url", url);
		}
		return retMap;
	}
	
	
	/**
	 * 查询保单渠道
	 */
	@RequestMapping(value = "/queryPolicyChannelType")
	@ResponseBody
	public String queryPolicyChannelType(String policyNo) {
		String channel=
		 queryService.queryPolicyChannelType(policyNo);
		return channel;
	}
	
	

	/**
	 * 查询续期缴费账户信息
	 */
	@RequestMapping(value = "/queryAccountByPolicyNo")
	@ResponseBody
	public Map<String, Object> queryAccountByPolicyNo(String policy_no) {
		
	   return queryService.queryAccountByPolicyNo(policy_no);
	
	}
	
	
	/**
	   * 查询银行大类
	 * @param bankCode
	 * @return
	 */
	@RequestMapping(value = "/getBankCategoryByBankCode")
	@ResponseBody
	   public String getBankCategoryByBankCode(String bankCode)
	     {
		  
		  return  queryService.getBankCategoryByBankCode(bankCode);
	     }
	
	/**
	   * 查询保单联系地址
	 * @param policy_no
	 * @return
	 */
	@RequestMapping(value = "/getPolicyAddressByPolicyNo")
	@ResponseBody
    public String getPolicyAddressByPolicyNo(String policy_no){
		return  queryService.getPolicyAddressByPolicyNo(policy_no);
    }
	
    /**
     * 免填单获取条形码
     * 
     * @param serviceItem
     * @return String
     * @author GaoJiaMing
     * @time 2014-5-28
     */
    @RequestMapping(value = "/getBarCodeNo", method = RequestMethod.GET)
    @ResponseBody
    public String getBarCodeNo(String serviceItem) {
        String barCodeNo = queryService.generateBarcodeNo(serviceItem);
        return barCodeNo;
    }
    
    /**
     * 根据保全号查询是否显示打印按钮
     * @param posNo
     * @return String
     * @author GaoJiaMing 
     * @time 2014-6-5
     */
    @RequestMapping(value = "/checkIsShowButton", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> checkIsShowButton(String posNo) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String isApplicationFree = queryService.checkIsApplicationFree(posNo);
        if ("Y".equals(isApplicationFree)) {
            resultMap.put("isApplicationFree", "Y");
        } else {
            resultMap.put("isApplicationFree", "N");
        }
        String barCodeNo = queryService.getBarCodeNoByPosNo(posNo);
        if (!barCodeNo.startsWith("1234")) {
            resultMap.put("isPremClass", "Y");
        } else {
            resultMap.put("isPremClass", "N");
        }
        resultMap.put("barCodeNo", queryService.getBarCodeNoByPosNo(posNo));
        return resultMap;
    }
    
	/**
	 * 查询电销项目代码
	 * 
	 * @param policyNo
	 * @return
	 */
	@RequestMapping(value = "/getProjectCodeByPolicyNo", method = RequestMethod.GET)
	@ResponseBody
	public String getProjectCodeByPolicyNo(String policyNo) {
		return queryService.getProjectCodeByPolicyNo(policyNo);
	}
	
	/**
	 * 校验代办代审退费金额超限制和规则特殊件原因
	 * 
	 * @param posNo
	 * @return Map<String, Object>
	 * @author GaoJiaMing
	 * @time 2014-9-19
	 */
	@RequestMapping(value = "/checkIsExceedExamPrivs", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> checkIsExceedExamPrivs(String posNo) {
		return queryService.checkIsExceedExamPrivs(posNo);
	}
	
}
