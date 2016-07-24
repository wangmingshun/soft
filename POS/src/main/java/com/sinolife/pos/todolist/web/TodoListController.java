package com.sinolife.pos.todolist.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.sinolife.esbpos.oa.GoaPosService;
import com.sinolife.pos.common.consts.SessionKeys;
import com.sinolife.pos.common.dto.PosProblemItemsDTO;
import com.sinolife.pos.common.util.PaginationDataWrapper;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.todolist.acceptentry.service.AcceptEntryService;
import com.sinolife.pos.todolist.approval.service.ApprovalService;
import com.sinolife.pos.todolist.problem.service.ProblemService;
import com.sinolife.pos.todolist.uwnote.service.UwnotePrintFeedbackService;
import com.sinolife.sf.platform.runtime.PlatformContext;

@Controller
@SessionAttributes({SessionKeys.TODOLIST_PROBLEM_LIST, SessionKeys.TODOLIST_ACCEPT_ENTRY_LIST, SessionKeys.TODOLIST_APPROVE_LIST, SessionKeys.TODOLIST_UNDWR_PRINT_LIST, SessionKeys.TODOLIST_OA_APPROVE_LIST})
public class TodoListController {

	@Autowired
	private ApprovalService aprovService;
	
	@Autowired
	private ProblemService problemService;
	
	@Autowired
	private UwnotePrintFeedbackService uwnoteService;
	
	@Autowired
	private AcceptEntryService acceptEntryService;
	
	/**
	 * 待审批列表
	 */
	@ModelAttribute(SessionKeys.TODOLIST_APPROVE_LIST)
	public List<?> approveList() {
		String loginUserID = PosUtils.getLoginUserInfo().getLoginUserID();
		return aprovService.queryApproveList(loginUserID);
	}
	
	/**
	 * 公共待审批列表
	 */
	@ModelAttribute(SessionKeys.TODOLIST_PUBLIC_APPROVE_LIST)
	public List<?> publicApproveList() {
		String loginUserID = PosUtils.getLoginUserInfo().getLoginUserID();
		return aprovService.queryPublicApproveList(loginUserID);
	}
	
	
	/**
	 * 问题件代办列表
	 */
	@ModelAttribute(SessionKeys.TODOLIST_PROBLEM_LIST)
	public List<PosProblemItemsDTO> problemList() {
		String loginUserID = PosUtils.getLoginUserInfo().getLoginUserID();
		return problemService.queryProblemTodoList(loginUserID);
	}
	
	/**
	 * 待打印核保函列表
	 */
	@ModelAttribute(SessionKeys.TODOLIST_UNDWR_PRINT_LIST)
	public List<?> undwrPrintList() {
		String loginUserID = PosUtils.getLoginUserInfo().getLoginUserID();
		return uwnoteService.queryTodoPrintList(loginUserID);
	}
	
	/**
	 * 待录入件列表
	 */
	@ModelAttribute(SessionKeys.TODOLIST_ACCEPT_ENTRY_LIST)
	public List<Map<String, Object>> acceptEntryList() {
		String loginUserID = PosUtils.getLoginUserInfo().getLoginUserID();
		return acceptEntryService.queryAcceptEntryTodolist(loginUserID);
	}
	
	/**
	 * @Description: 待OA审批任务列表
	 * @methodName: oaApproveList
	 * @return
	 * @return List<Map<String,Object>>
	 * @author WangMingShun
	 * @date 2015-12-1
	 * @throws
	 */
	@ModelAttribute(SessionKeys.TODOLIST_OA_APPROVE_LIST)
	public List<Map<String, Object>> oaApproveList() {
		String loginUserID = PosUtils.getLoginUserInfo().getLoginUserID();
		return aprovService.queryOaApproveList(loginUserID);
	}
	
	/**
	 * 入口，查询出用户待处理的任务列表
	 * @param model
	 * @return
	 */
	@RequestMapping("/todolist/index")
	public String entry(Model model, SessionStatus sessionStatus){
		sessionStatus.setComplete();
		model.addAttribute("UW_URL", PosUtils.getPosProperty("uwUrl"));//核保函调用uw用到
		return "/todolist/index";
	}
	
	/**
	 * 问题件取页面信息
	 */
	@RequestMapping("/todolist/problemList")
	@ResponseBody
	public Map<String, Object> getProblemList(@RequestParam int page, @RequestParam int rows, @ModelAttribute(SessionKeys.TODOLIST_PROBLEM_LIST) List<PosProblemItemsDTO> problemList) {
		PaginationDataWrapper<PosProblemItemsDTO> wrapper = new PaginationDataWrapper<PosProblemItemsDTO>(problemList, rows);
		wrapper.gotoPage(page);
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("total", wrapper.getTotalDataSize());
		retMap.put("rows", wrapper.getCurrentPageDataList());
		return retMap;
	}
	
	/**
	 * 待录入取页面信息
	 */
	@RequestMapping("/todolist/acceptEntryList")
	@ResponseBody
	public Map<String, Object> getAcceptEntryList(@RequestParam int page, @RequestParam int rows, @ModelAttribute(SessionKeys.TODOLIST_ACCEPT_ENTRY_LIST) List<Map<String, Object>> acceptEntryList) {
		PaginationDataWrapper<Map<String, Object>> wrapper = new PaginationDataWrapper<Map<String, Object>>(acceptEntryList, rows);
		wrapper.gotoPage(page);
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("total", wrapper.getTotalDataSize());
		retMap.put("rows", wrapper.getCurrentPageDataList());
		return retMap;
	}
	
	/**
	 * 待审批取页面信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/todolist/approveList")
	@ResponseBody
	public Map<String, Object> getApproveList(@RequestParam int page, @RequestParam int rows, @ModelAttribute(SessionKeys.TODOLIST_APPROVE_LIST) List<?> approveList) {
		PaginationDataWrapper wrapper = new PaginationDataWrapper(approveList, rows);
		wrapper.gotoPage(page);
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("total", wrapper.getTotalDataSize());
		retMap.put("rows", wrapper.getCurrentPageDataList());
		return retMap;
	}
	
	/**
	 * 公共待审批取页面信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/todolist/publicApproveList")
	@ResponseBody
	public Map<String, Object> getPublicApproveList(@RequestParam int page, @RequestParam int rows, @ModelAttribute(SessionKeys.TODOLIST_PUBLIC_APPROVE_LIST) List<?> publicApproveList) {
		PaginationDataWrapper wrapper = new PaginationDataWrapper(publicApproveList, rows);
		wrapper.gotoPage(page);
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("total", wrapper.getTotalDataSize());
		retMap.put("rows", wrapper.getCurrentPageDataList());
		return retMap;
	}
	
	
	
	/**
	 * 待核保函打印取页面信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/todolist/undwrPrintList")
	@ResponseBody
	public Map<String, Object> getUndwrPrintList(@RequestParam int page, @RequestParam int rows, @ModelAttribute(SessionKeys.TODOLIST_UNDWR_PRINT_LIST) List<?> undwrPrintList) {
		PaginationDataWrapper wrapper = new PaginationDataWrapper(undwrPrintList, rows);
		wrapper.gotoPage(page);
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("total", wrapper.getTotalDataSize());
		retMap.put("rows", wrapper.getCurrentPageDataList());
		return retMap;
	}
	
	/**
	 * @Description: 待OA审批页面信息
	 * @methodName: getOaApproveList
	 * @param page
	 * @param rows
	 * @param oaApproveList
	 * @return
	 * @return Map<String,Object>
	 * @author WangMingShun
	 * @date 2015-12-1
	 * @throws
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/todolist/oaApproveList")
	@ResponseBody
	public Map<String, Object> getOaApproveList(@RequestParam int page, @RequestParam int rows, @ModelAttribute(SessionKeys.TODOLIST_OA_APPROVE_LIST) List<?> oaApproveList) {
		PaginationDataWrapper wrapper = new PaginationDataWrapper(oaApproveList, rows);
		wrapper.gotoPage(page);
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("total", wrapper.getTotalDataSize());
		retMap.put("rows", wrapper.getCurrentPageDataList());
		return retMap;
	}
	
	/**
	 * 逐单待办任务的入口，防止笨重的待办任务列表页面刷不出来,添加此功能应急
	 * @return
	 */
	@RequestMapping("/todolist/single")
	public String singleEntry(){
		return "/todolist/single";
	}
	
	/**
	 * 根据录入的保全号获取任务页面需要的信息后进入处理页面
	 * @param type
	 * @param posNo
	 * @return
	 */
	@RequestMapping("/todolist/goToSingleTask")
	public String goToSingleTask(String type, String posNo, Model model){
		String view = null;
		
		String no = null;
		if("1".equals(type)){//受理件
			no = acceptEntryService.queryPosNoBatchNo(posNo);
			view = "redirect:/acceptance/branch/continueFromBreak.do?posBatchNo=";
			
		}else if("2".equals(type)){//审批件
			no = aprovService.queryApproveSingle(posNo);
			view = "redirect:/todolist/approve/";

		}else if("3".equals(type)){//问题件
			no = problemService.queryPosNoProblemNo(posNo);
			view = "redirect:/todolist/problem/";
		}
		
		if(StringUtils.isBlank(no)){
			view = "/todolist/single";
			model.addAttribute("RETURN_MSG", "没有此单待办任务");
		}else{
			view = view + no;
		}
			
		return view;
	}
	
	@RequestMapping("/todolist/getApproveHistory")
	public String getApproveHistory(String approveId, Model model) {
		//获取esb实例
		GoaPosService goaPosService = 
			PlatformContext.getEsbContext().getEsbService(GoaPosService.class);
		List<Map<String, Object>> list = null;
		if(PosUtils.isNotNullOrEmpty(approveId)) {
			try {
				list = goaPosService.getApproveHistory(approveId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(list != null && list.size() > 0) {
			model.addAttribute("history", list);
		} else {
			model.addAttribute("RETURN_MSG", "没有审批历史");
		}
		return "/todolist/approval/oaApproveHistory";
	}
	
}
