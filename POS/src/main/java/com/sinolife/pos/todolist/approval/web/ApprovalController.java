package com.sinolife.pos.todolist.approval.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.todolist.approval.service.ApprovalService;
import com.sinolife.pos.todolist.problem.service.ProblemService;
import com.sinolife.sf.platform.runtime.PlatformContext;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
public class ApprovalController {

	@Autowired
	private ApprovalService approvalService;
	
	@Autowired
	private ProblemService problemService;
	
	@Autowired
	private CommonQueryDAO commonQueryDAO;
	
	/**
	 * 单个审批件的入口
	 * @param posNo
	 * @param submitNo
	 * @param approveNo
	 * @param model
	 * @return
	 */
	@RequestMapping("/todolist/approve/{posNo}/{submitNo}/{approveNo}/{barcodeNo}/{policyNo}/{clientNo}")
	public String entry(@PathVariable String posNo,@PathVariable String submitNo,@PathVariable String approveNo,@PathVariable String barcodeNo,@PathVariable String policyNo,@PathVariable String clientNo,Model model){
		Map map = approvalService.querySignBarcode(policyNo,clientNo);
		model.addAttribute("signImageInfo", approvalService.querySignJPGUrl((String)map.get("barcodeNo"), (String)map.get("image")));
		model.addAttribute("policyApplyBarcode", approvalService.queryPolicyApplyBarcode(policyNo));
		
		model.mergeAttributes(approvalService.queryApproveRecord(posNo, submitNo, approveNo));
		
		model.addAttribute("imageBarcodeNo", barcodeNo);
		model.addAttribute("acctNo", commonQueryDAO.getPolicyBankacctno(policyNo).get("acctNo"));
		model.addAttribute("imageRelateBarcodeNo", commonQueryDAO.getRelateBarcodeNo(barcodeNo));
		
		
		ServiceItemsDAO serviceItemsDAO = (ServiceItemsDAO) PlatformContext
		.getApplicationContext().getBean(
				"serviceItemsDAO_" + "23");

		ServiceItemsInputDTO ServiceItemsInputDTO=serviceItemsDAO.queryCommonAcceptDetailInput(posNo);
		//移动保全受理的保单先在审批表中写入审批人
		if("11".equals(ServiceItemsInputDTO.getAcceptChannel()))
		{
			Map pMap = new HashMap();
			pMap.put("posNo", posNo);
			pMap.put("approver", PosUtils.getLoginUserInfo().getLoginUserID());
			
			approvalService.updatePosApproveApprover(pMap);
		}
		return "/todolist/approval/approve";
	}
	
	/**
	 * 对转办录入的用户校验是否在pos_user_privs存在
	 * @param newApprover
	 * @return
	 */
	@RequestMapping("/todolist/approve/newApproverCheck")
	@ResponseBody
	public String newApproverCheck(String newApprover){
		return approvalService.newApproverCheck(PosUtils.parseInputUser(newApprover));
	}
	
	/**
	 * 审批操作并发控制
	 * @param posNo
	 * @param status
	 * @param opType 操作类型P下发问题件，A审批
	 * @return 0-通过,1-保全状态不是A08，2-已下发问题件
	 */
	@RequestMapping("/todolist/approve/approvalCheck")
	@ResponseBody
	public String approvalCheck(String posNo, String status, String opType){
		return approvalService.approvalCheck(posNo, status, opType);
	}
	
	/**
	 * 审批确定，审批意见提交
	 * @return
	 */
	@RequestMapping("/todolist/approve/submit")
	@Transactional(propagation = Propagation.REQUIRED)
	public String approveSubmit(HttpServletRequest request,Model model){
		String posNo = request.getParameter("posNo");
		
		approvalService.lockPosApprove(posNo, "A08", "A");
		
		Map pMap = new HashMap();
		pMap.put("posNo", posNo);
		pMap.put("submitNo", request.getParameter("submitNo"));
		pMap.put("approveNo", request.getParameter("approveNo"));
		pMap.put("barcodeNo", request.getParameter("barcodeNo"));
		pMap.put("decision", request.getParameter("approveDecision"));//审批意见
		pMap.put("newApprover", PosUtils.parseInputUser(request.getParameter("newApprover")));//转其他的人
		pMap.put("remark", request.getParameter("approveRemark"));//备注
		pMap.put("acceptor", request.getParameter("acceptor"));//保全受理人
		
		pMap.put("serviceItems", request.getParameter("serviceItems"));//这俩参数取消暂停单独用的
		pMap.put("policyNo", request.getParameter("policyNo"));
		
		approvalService.approveSubmit(pMap);
		
		if("4".equals(pMap.get("decision"))){//要重新录入明细
			return "redirect:/acceptance/branch/acceptDetailInput/" + pMap.get("posBatchNo") + "/" + pMap.get("newPosNo");
			
		}else{
			model.addAttribute("RETURN_MESSAGE", "审批意见提交成功，系统将返回任务列表页面！");
			return "/todolist/approval/approve";
		}
	}
	
	/**
	 * 下发问题件
	 * @param posNo
	 * @return
	 */
	@RequestMapping("/todolist/approve/newproblem")
	@Transactional(propagation = Propagation.REQUIRED)
	public String newProblem(@RequestParam("posNo")String posNo, @RequestParam("problemContent")String problemContent, Model model){
		
		approvalService.lockPosApprove(posNo, "A08", "P");
		
		String no = problemService.createProblem(posNo, PlatformContext.getCurrentUser(), "2", problemContent);
		
		model.addAttribute("RETURN_MESSAGE", "问题件提交成功，问题件号"+no+"，系统将返回任务列表页面！");
		
		return "/todolist/approval/approve";
	}
	
}
