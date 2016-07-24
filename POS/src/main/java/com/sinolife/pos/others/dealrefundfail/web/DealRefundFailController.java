package com.sinolife.pos.others.dealrefundfail.web;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.unihub.framework.util.common.DateUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sinolife.pos.common.dto.PosProblemItemsDTO;
import com.sinolife.pos.common.util.PaginationDataWrapper;
import com.sinolife.pos.common.web.PosAbstractController;
import com.sinolife.pos.others.dealrefundfail.service.DealRefundFailService;
import com.sinolife.pos.others.unsuspendbankaccount.service.UnsuspendBankAccountService;
import com.sinolife.pos.todolist.problem.service.ProblemService;

/**
 * 网销渠道退费失败件处理
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller("dealRefundFailController")
public class DealRefundFailController extends PosAbstractController {

	@Autowired
	DealRefundFailService dealRefundFailService;

	@Autowired
	private ProblemService problemService;
	@Autowired
	private UnsuspendBankAccountService unsuspendBankAccountService;

	/**
	 * 跳转网销渠道退费失败件查询页面
	 * 
	 * @param request
	 * @param model
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-11-6
	 */
	@RequestMapping("/pub/others/dealRefundFail")
	public String dealRefundFail(HttpServletRequest request, Model model) {
		model.addAttribute("startDate",
				DateUtil.dateIncreaseByDay(DateUtil.dateToString(new Date()), "yyyy-MM-dd", -30));
		model.addAttribute("endDate", DateUtil.dateToString(new Date()));
		return "/others/dealrefundfail/dealRefundFail";
	}

	/**
	 * 退费失败件分页查询
	 * 
	 * @param request
	 * @param page
	 * @param rows
	 * @return Map<String,Object>
	 * @author GaoJiaMing
	 * @time 2014-11-6
	 */
	@RequestMapping("/pub/others/queryDealRefundFailPage")
	@ResponseBody
	public Map<String, Object> queryDealRefundFailPage(HttpServletRequest request, @RequestParam int page,
			@RequestParam int rows) {
		Map pMap = new HashMap();
		Enumeration<String> en = request.getParameterNames();
		while (en.hasMoreElements()) {
			String k = en.nextElement();
			Object v = request.getParameter(k);
			pMap.put(k, v);
		}
		List dealRefundFailList = dealRefundFailService.queryDealRefundFailPage(pMap);
		PaginationDataWrapper<PosProblemItemsDTO> wrapper = new PaginationDataWrapper<PosProblemItemsDTO>(
				dealRefundFailList, rows);
		wrapper.gotoPage(page);
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("total", wrapper.getTotalDataSize());
		retMap.put("rows", wrapper.getCurrentPageDataList());
		return retMap;
	}

	/**
	 * 取消转账暂停
	 * 
	 * @param posNo
	 * @param approveResult
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-11-6
	 */
	@RequestMapping(value = "/pub/others/unsuspendBankAccount", method = RequestMethod.GET)
	@ResponseBody
	public String unsuspendBankAccount(String posNo, String problemItemNo) {
		if (StringUtils.isBlank(posNo) || StringUtils.isBlank(problemItemNo)) {
			return "N";
		}
		PosProblemItemsDTO problemItemsDTO = new PosProblemItemsDTO();
		problemItemsDTO.setPosNo(posNo);// 保全号
		problemItemsDTO.setProblemItemNo(problemItemNo);// 问题件号
		try {
			unsuspendBankAccountService.unsuspendBankTransfer(problemItemsDTO);
		} catch (Exception e) {
			e.printStackTrace();
			return "N";
		}
		return "Y";
	}

	/**
	 * 补充支行
	 * 
	 * @param posNo
	 * @param bankCode
	 * @param problemItemNo
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-11-6
	 */
	@RequestMapping(value = "/pub/others/addBank", method = RequestMethod.GET)
	@ResponseBody
	public String addBank(String posNo, String bankCode, String problemItemNo) {
		if (StringUtils.isBlank(posNo) || StringUtils.isBlank(bankCode) || StringUtils.isBlank(problemItemNo)) {
			return "N";
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("posNo", posNo);
		map.put("bankCode", bankCode);
		map.put("problemItemNo", problemItemNo);
		try {
			unsuspendBankAccountService.modifyAccountAndUnsuspend(map);
		} catch (Exception e) {
			e.printStackTrace();
			return "N";
		}
		return "Y";
	}
}
