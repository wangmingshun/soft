package com.sinolife.pos.approve.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sinolife.pos.approve.service.ApproveManageService;
import com.sinolife.pos.common.dto.PosProblemItemsDTO;
import com.sinolife.pos.common.util.PaginationDataWrapper;
import com.sinolife.pos.common.web.PosAbstractController;
import com.sinolife.sf.platform.runtime.PlatformContext;

/**
 * 代审权限管理
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
public class ApproveManageController extends PosAbstractController {

	@Autowired
	private ApproveManageService approveManageService;

	/**
	 * 跳转不良记录查询
	 * 
	 * @param request
	 * @param model
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-8-19
	 */
	@RequestMapping("/approve/queryBadBehavior")
	public String queryBadBehavior(HttpServletRequest request, Model model) {
		return "/approve/queryBadBehavior";
	}

	/**
	 * 不良记录分页查询
	 * 
	 * @param request
	 * @param page
	 * @param rows
	 * @return Map<String,Object>
	 * @author GaoJiaMing
	 * @time 2014-8-22
	 */
	@RequestMapping("/approve/queryBadBehaviorPage")
	@ResponseBody
	public Map<String, Object> queryBadBehaviorPage(HttpServletRequest request, @RequestParam int page,
			@RequestParam int rows) {
		Map pMap = new HashMap();
		Enumeration<String> en = request.getParameterNames();
		while (en.hasMoreElements()) {
			String k = en.nextElement();
			Object v = request.getParameter(k);

			pMap.put(k, v);
		}
		List badBehaviorList = approveManageService.queryBadBehavior(pMap);
		PaginationDataWrapper<PosProblemItemsDTO> wrapper = new PaginationDataWrapper<PosProblemItemsDTO>(
				badBehaviorList, rows);
		wrapper.gotoPage(page);
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("total", wrapper.getTotalDataSize());
		retMap.put("rows", wrapper.getCurrentPageDataList());
		return retMap;
	}

	/**
	 * 跳转不良记录新增页面
	 * 
	 * @param request
	 * @param model
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-8-21
	 */
	@RequestMapping("/approve/prepareInsertBadBehavior")
	public String prepareInsertBadBehavior(HttpServletRequest request, Model model) {
		return "/approve/insertBadBehavior";
	}

	/**
	 * 不良记录新增
	 * 
	 * @param request
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-8-21
	 */
	@RequestMapping(value = "/approve/insertBadBehavior", method = RequestMethod.GET)
	@ResponseBody
	public String insertBadBehavior(HttpServletRequest request) {
		Map pMap = new HashMap();
		Enumeration<String> en = request.getParameterNames();
		while (en.hasMoreElements()) {
			String k = en.nextElement();
			Object v = request.getParameter(k);

			pMap.put(k, v);
		}
		pMap.put("recordUser", PlatformContext.getCurrentUserInfo().getUserId());
		return approveManageService.insertBadBehavior(pMap);
	}

	/**
	 * 根据对象编码获取对象信息
	 * 
	 * @param objectNo
	 * @param objectType
	 * @return Map<String, Object>
	 * @author GaoJiaMing
	 * @time 2014-8-21
	 */
	@RequestMapping(value = "/approve/getObjectInfoByObjectNo", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getObjectInfoByObjectNo(String objectNo, String objectType, String needCheckPrivs) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("objectNo", objectNo);
		map.put("objectType", objectType);
		map.put("needCheckPrivs", needCheckPrivs);
		approveManageService.getObjectInfoByObjectNo(map);
		return map;
	}

	/**
	 * 跳转代审审核页面
	 * 
	 * @param request
	 * @param model
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-8-19
	 */
	@RequestMapping("/approve/examApprove")
	public String examApprove(HttpServletRequest request, Model model) {
		return "/approve/examApprove";
	}

	/**
	 * 跳转代审撤销页面
	 * 
	 * @param request
	 * @param model
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-8-19
	 */
	@RequestMapping("/approve/examCancel")
	public String examCancel(HttpServletRequest request, Model model) {
		return "/approve/examCancel";
		// return "todolist/approval/approve";
	}

	/**
	 * 代审审批分页查询
	 * 
	 * @param request
	 * @param page
	 * @param rows
	 * @return Map<String,Object>
	 * @author GaoJiaMing
	 * @time 2014-8-22
	 */
	@RequestMapping("/approve/queryExamPrivsApprovePage")
	@ResponseBody
	public Map<String, Object> queryExamPrivsApprovePage(HttpServletRequest request, @RequestParam int page,
			@RequestParam int rows) {
		Map pMap = new HashMap();
		String queryFlag=request.getParameter("queryFlag");
		if("onlySelf".equals(queryFlag)){
			pMap.put("approveUser", PlatformContext.getCurrentUserInfo().getUserId());
		}
		Enumeration<String> en = request.getParameterNames();
		while (en.hasMoreElements()) {
			String k = en.nextElement();
			Object v = request.getParameter(k);
			pMap.put(k, v);
		}
		List examPrivsApproveList = approveManageService.queryExamPrivsApprovePage(pMap);
		PaginationDataWrapper<PosProblemItemsDTO> wrapper = new PaginationDataWrapper<PosProblemItemsDTO>(
				examPrivsApproveList, rows);
		wrapper.gotoPage(page);
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("total", wrapper.getTotalDataSize());
		retMap.put("rows", wrapper.getCurrentPageDataList());
		return retMap;
	}

	/**
	 * 代审审批
	 * 
	 * @param approvePk
	 * @param approveResult
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-8-25
	 */
	@RequestMapping(value = "/approve/examPrivsApprove", method = RequestMethod.GET)
	@ResponseBody
	public String examPrivsApprove(String approvePk, String approveResult, String approveDesc) {
		if (StringUtils.isBlank(approvePk) || StringUtils.isBlank(approveResult) || StringUtils.isBlank(approveDesc)) {
			return "N";
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("approvePk", approvePk);
		map.put("approveResult", approveResult);
		map.put("approveDesc", approveDesc);
		map.put("userId", PlatformContext.getCurrentUserInfo().getUserId());
		try {
			approveManageService.examPrivsApprove(map);
		} catch (Exception e) {
			return "N";
		}
		return "Y";
	}

	/**
	 * 撤销黑名单
	 * 
	 * @param approvePk
	 * @param approveResult
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-8-25
	 */
	@RequestMapping(value = "/approve/examPrivsCancel", method = RequestMethod.GET)
	@ResponseBody
	public String examPrivsCancel(String approvePk, String cancelReason, String isCanceled) {
		if (StringUtils.isBlank(approvePk) || StringUtils.isBlank(isCanceled) || StringUtils.isBlank(cancelReason)) {
			return "N";
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("approvePk", approvePk);
		map.put("isCanceled", isCanceled);
		map.put("cancelReason", cancelReason);
		map.put("userId", PlatformContext.getCurrentUserInfo().getUserId());
		try {
			approveManageService.examPrivsCancel(map);
		} catch (Exception e) {
			return "N";
		}
		return "Y";
	}
		
	/**
	 * 根据保全号获取代审信息
	 * 
	 * @param posNo
	 * @return Map<String,Object>
	 * @author GaoJiaMing
	 * @time 2014-9-4
	 */
	@RequestMapping(value = "/approve/getBadBehaviorInfoByPosNo", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getBadBehaviorInfoByPosNo(String posNo) {
		return approveManageService.getBadBehaviorInfoByPosNo(posNo);
	}
	
	/**
	 * 查看某银代经理或网点代审权限状态
	 * 
	 * @param objectNo
	 * @param objectType
	 * @return Map<String, Object>
	 * @author GaoJiaMing
	 * @time 2014-9-9
	 */
	@RequestMapping(value = "/approve/viewPrivsByObjectNoAndObjectType", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> viewPrivsByObjectNoAndObjectType(String objectNo, String objectType) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("objectNo", objectNo);
		map.put("objectType", objectType);
		String privsTip=approveManageService.viewPrivsByObjectNoAndObjectType(map);
		map.put("privsTip", privsTip);
		return map;
	}
	
	/**
	 * 结合保单号校验代审权限
	 * 
	 * @param objectNo
	 * @param objectType
	 * @param policyNo
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-9-18
	 */
	@RequestMapping(value = "/approve/checkPrivsByPolicyNo", method = RequestMethod.GET)
	@ResponseBody
	public String checkPrivsByPolicyNo(String objectNo, String objectType, String policyNo) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("objectNo", objectNo);
		map.put("objectType", objectType);
		map.put("policyNo", policyNo);
		return approveManageService.checkPrivsByPolicyNo(map);
	}
	
	/**
	 *  检查是否是代办代审的试点机构
	 * @param map
	 * @return String
	 * @author WangMingShun
	 * @time 2015-4-7
	 */
	@RequestMapping(value = "/approve/checkExamBranch", method = RequestMethod.GET)
	@ResponseBody
	public String checkExamBranch(String objectNo, String objectType, String policyNo) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("objectNo", objectNo);
		map.put("objectType", objectType);
		map.put("policyNo", policyNo);
		return approveManageService.checkExamBranch(map);
	}
}
