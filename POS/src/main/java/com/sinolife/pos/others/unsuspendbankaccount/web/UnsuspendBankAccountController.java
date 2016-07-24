package com.sinolife.pos.others.unsuspendbankaccount.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sinolife.pos.common.consts.ViewNames;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.others.unsuspendbankaccount.service.UnsuspendBankAccountService;

/**
 * 取消转账暂停控制器
 */
@Controller
@RequestMapping(value="/others/unsuspendbankaccount")
public class UnsuspendBankAccountController {

	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private UnsuspendBankAccountService unsuspendBankAccountService;
	
	/**
	 * 取消转账暂停入口
	 * @return
	 */
	@RequestMapping(value="/entry")
	public String entry() {
		return "redirect:/others/unsuspendbankaccount/transferFailPosList";
	}
	
	/**
	 * 进入取消转账暂停页面
	 * @return
	 */
	@RequestMapping(value="/transferFailPosList", method=RequestMethod.GET)
	public String transferFailPosListEntry() {
		return ViewNames.OTHERS_TRANSFER_FAIL_POS_LIST;
	}
	
	/**
	 * 根据保全号码查询可取消转账暂停的保全信息列表
	 * @param posNo
	 * @return
	 */
	@RequestMapping(value="/transferFailPosList", method=RequestMethod.POST)
	public ModelAndView transferFailPosListSubmit(String posNo) {
		ModelAndView mav = new ModelAndView(ViewNames.OTHERS_TRANSFER_FAIL_POS_LIST);
		List<PosInfoDTO> transferFailPosList = unsuspendBankAccountService.queryPosInfoByPosNoForUnsuspendBankAccount(posNo);
		mav.addObject("transferFailPosList", transferFailPosList);
		mav.addObject("posNo", posNo);
		return mav;
	}
	
	/**
	 * 取消指定保全号码的转账暂停状态
	 * @param posNo
	 * @return
	 */
	@RequestMapping(value="/unsuspendBankAccount")
	@ResponseBody
	public Map<String, String> unsuspendBankAccount(String posNo) {
		Map<String, String> retMap = new HashMap<String, String>();
		try {
			unsuspendBankAccountService.unsuspendBankAccount(posNo);
			retMap.put("flag", "Y");
			retMap.put("msg", "取消转账暂停成功");
		} catch(Exception e) {
			retMap.put("flag", "N");
			retMap.put("msg", e.getMessage());
			logger.error("取消指定保全号码的转账暂停状态失败:" + e.getMessage(), e);
		}
		return retMap;
	}
}
