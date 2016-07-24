package com.sinolife.pos.receipt.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.file.service.PosFileService;
import com.sinolife.pos.common.include.service.IncludeService;
import com.sinolife.pos.common.util.Excel;
import com.sinolife.pos.common.util.PaginationDataWrapper;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.receipt.service.ReceiptManageService;
import com.sinolife.pos.rpc.um.PosUMService;
import com.sinolife.sf.platform.runtime.PlatformContext;

/**
 * 回执管理
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class ReceiptManageController {
	
	private static Logger logger = Logger.getLogger("ReceiptManageController.class");

	@Autowired
	ReceiptManageService receiptService;

	@Autowired
	PosFileService fileService;

	@Autowired
	PosUMService umService;

	@Autowired
	IncludeService includeService;
	
	@Autowired
	CommonQueryDAO commonQueryDAO;

	/**
	 * 回销取单入口
	 */
	@RequestMapping("/receipt/feedback/fetch")
	public String feedbackEntry(HttpServletRequest request,String singleFetch) {
		String flag = umService.hasRole(PlatformContext.getCurrentUser(),
				"POS_SENIOR_BIZ") ? "Y" : "N";
		request.getSession().setAttribute("seniorBizFlag", flag);// 特权，高级业管员就可以复制粘贴
		return "/receipt/fetch";
	}
	
	/**
	 * 逐单回销入口
	 */
	@RequestMapping("/receipt/feedback/singleFetch")
	public String singleFeedbackEntry(HttpServletRequest request,String singleFetch) {
		String flag = umService.hasRole(PlatformContext.getCurrentUser(),
		"POS_SENIOR_BIZ") ? "Y" : "N";
		request.getSession().setAttribute("seniorBizFlag", flag);// 特权，高级业管员就可以复制粘贴
		return "/receipt/singleFetch";
	}

	/**
	 * 签署日期修改入口
	 */
	@RequestMapping("/receipt/feedback/signupdate")
	public String feedbackSignUpdate(HttpServletRequest request, Model model) {
		String flag = umService.hasRole(PlatformContext.getCurrentUser(),
				"POS_SENIOR_BIZ") ? "Y" : "N";
		request.getSession().setAttribute("seniorBizFlag", flag);// 特权，高级业管员就可以复制粘贴
		model.addAttribute("entryFlag", "S");
		return "/receipt/update";
	}

	/**
	 * 回销日期修改入口,与上面的相比，仅用flag区分
	 */
	@RequestMapping("/receipt/feedback/confirmupdate")
	public String feedbackConfirmUpdate(HttpServletRequest request, Model model) {
		String flag = umService.hasRole(PlatformContext.getCurrentUser(),
				"POS_SENIOR_BIZ") ? "Y" : "N";
		request.getSession().setAttribute("seniorBizFlag", flag);// 特权，高级业管员就可以复制粘贴
		model.addAttribute("entryFlag", "C");
		return "/receipt/update";
	}
	
	/**
	 * 回执回销待处理的任务列表入口
	 */
	@RequestMapping("/receipt/toDoTasks")
	public String entry(){
		return "/receipt/toDoTasks";
	}
	
	/**
	 * 移动端信息处理
	 */
	@RequestMapping("/receipt/mobileList")
	@ResponseBody
	public Map<String, Object> getMobileList(@RequestParam int page, @RequestParam int rows) {
		//移动端待处理回执列表
		String loginUserID = PlatformContext.getCurrentUser();
		List<Map<String, Object>> mobileEntryList = receiptService.queryMobileList(loginUserID);
		
		//移动端取页面信息
		PaginationDataWrapper<Map<String, Object>> wrapper = new PaginationDataWrapper<Map<String, Object>>(mobileEntryList, rows);
		wrapper.gotoPage(page);
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("total", wrapper.getTotalDataSize());
		retMap.put("rows", wrapper.getCurrentPageDataList());
		logger.info("====移动端====loginUserID : "+loginUserID+"===total : "+wrapper.getTotalDataSize()+"===rows : "+wrapper.getCurrentPageDataList());
		return retMap;
	}
	/**
	 * 传统信息处理
	 */
	@RequestMapping("/receipt/notMobileList")
	@ResponseBody
	public Map<String, Object> getNotMobileList(@RequestParam int page, @RequestParam int rows) {
		//传统待处理回执列表
		String loginUserID =  PlatformContext.getCurrentUser();
		List<Map<String, Object>> notMobileEntryList = receiptService.queryTraditionalList(loginUserID);
		//传统端取页面信息
		PaginationDataWrapper<Map<String, Object>> wrapper = new PaginationDataWrapper<Map<String, Object>>(notMobileEntryList, rows);
		wrapper.gotoPage(page);
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("total", wrapper.getTotalDataSize());
		retMap.put("rows", wrapper.getCurrentPageDataList());
		logger.info("====传统====loginUserID : "+loginUserID+"===total : "+wrapper.getTotalDataSize()+"===rows : "+wrapper.getCurrentPageDataList());
		return retMap;
	}
	
	/**
	 * 待处理问题信息处理
	 */
	@RequestMapping("/receipt/problemList")
	@ResponseBody
	public Map<String, Object> getProblemList(@RequestParam int page, @RequestParam int rows) {
		//待处理问题件列表
		String loginUserID =  PlatformContext.getCurrentUser();
		List<Map<String, Object>> problemEntryList = receiptService.queryProblemList(loginUserID);
		//待处理问题件取页面信息
		PaginationDataWrapper<Map<String, Object>> wrapper = new PaginationDataWrapper<Map<String, Object>>(problemEntryList, rows);
		wrapper.gotoPage(page);
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("total", wrapper.getTotalDataSize());
		retMap.put("rows", wrapper.getCurrentPageDataList());
		logger.info("====待处理====loginUserID : "+loginUserID+"===total : "+wrapper.getTotalDataSize()+"===rows : "+wrapper.getCurrentPageDataList());
		return retMap;
	}
	
	/**
	 * 点击处理回执待处理任务
	 * @return String
	 * @author TangJing
	 * @date 2016-4-20
	 */
	@RequestMapping("/receipt/fetch")
	public String fetch(String policyNo, Model model) {
		String view = null;
		Map map = null;
		
		map = receiptService.fetchPolicyNo(policyNo);
		Map pMap = receiptService.queryPolicyContractInfo(policyNo);
		map.put("client_sign_date", pMap.get("signDate"));
		map.put("sign_flag", pMap.get("signFlag"));
		
		if ("0".equals(map.get("flag"))) {// 无结果
			view = "/receipt/toDoTasks";
			model.addAttribute("RETURN_MSG", map.get("message"));

		} else if ("1".equals(map.get("flag"))) {// 已回销,页面将展示信息
			view = "/receipt/toDoTasks";
			model.mergeAttributes(map);

		} else {
			view = "/receipt/feedback";
			model.mergeAttributes(map);
		}

		return view;
	}

	/**
	 * 回销取单 fetchType P--按保单号，N--直接取下一单，T--待处理页面
	 */
	@RequestMapping("/receipt/feedback/fetchOne") 
	public String fetchOne(String policyNo, String fetchType, String channel,
			Model model) {
		String view = null;
		Map map = null;

		if ("P".equals(fetchType) || "S".equals(fetchType) || "T".equals(fetchType)) {
			map = receiptService.fetchPolicyNo(policyNo);
			Map pMap = receiptService.queryPolicyContractInfo(policyNo);
			map.put("client_sign_date", pMap.get("signDate"));
			map.put("sign_flag", pMap.get("signFlag"));

		} else if ("N".equals(fetchType)) {
			map = receiptService.fetchNext(channel);
			if((String)map.get("policyNo")!=null){
				/*取投保单barCode*/
				String uwApplyBarcode = receiptService.getPolicyNoReceiptBarCode01((String)map.get("policyNo"));
				Map pMap = receiptService.queryPolicyContractInfo((String)map.get("policyNo"));
				map.put("client_sign_date", pMap.get("signDate"));
				map.put("sign_flag", pMap.get("signFlag"));
				map.put("policyApplyBarcode", uwApplyBarcode);
			}
		}

		if ("0".equals(map.get("flag"))) {// 无结果
			if("S".equals(fetchType)){
				view = "/receipt/singleFetch";
			}else if ("T".equals(fetchType)){
				view = "/receipt/toDoTasks";
			}else {
				view = "/receipt/fetch";
			}
			model.addAttribute("RETURN_MSG", map.get("message"));

		} else if ("1".equals(map.get("flag"))) {// 已回销,页面将展示信息
			if("S".equals(fetchType)){
				view = "/receipt/singleFetch";
			}else if ("T".equals(fetchType)){
				view = "/receipt/toDoTasks";
			}else {
				view = "/receipt/fetch";
			}
			model.mergeAttributes(map);

		} else {
			view = "/receipt/feedback";
			model.mergeAttributes(map);
		}

		return view;
	}

	/**
	 * 回销录入
	 * 
	 * @return
	 */
	@RequestMapping("/receipt/feedback/input")
	@Transactional(propagation = Propagation.REQUIRED)
	public String feedbackInput(@RequestParam MultipartFile feedbackFile,
			String policyNo, String signDate, String remark, Model model) {

		fileService.uploadAndRecord(feedbackFile, policyNo, null, "1");

		receiptService.feedbackInput(policyNo, signDate, remark);

		model.addAttribute("RETURN_MSG", "保单："+policyNo+" 回执回销成功！");
		return "/receipt/toDoTasks";
	}

	/**
	 * 回执回销标注问题件
	 * 
	 * @param policyNo
	 * @param model
	 * @return
	 */
	@RequestMapping("/receipt/feedback/problem")
	public String feedbackProblem(String policyNo, String remark, Model model) {
		receiptService.feedbackProblem(policyNo, remark);
		receiptService.updateSignFlag(policyNo);
		model.addAttribute("RETURN_MSG", "问题件标注成功");
		return "/receipt/fetch";
	}

	/**
	 * 在回销页面，用户不想干了，点返回，此时取消对该单的锁定 即将pos_receipt_deal的标志改成2或3，2--问题件，3--非问题件
	 * 这样导致的小问题是3在这里完成的意思并不是说回执回销完成，只是用户的一趟操作完成
	 * 
	 * @param policyNo
	 * @return
	 */
	@RequestMapping("/receipt/feedback/cancel")
	public String feedbackCancel(String policyNo, String receiptStatus) {

		receiptService.feedbackCancel(policyNo, "2".equals(receiptStatus) ? "2"
				: "3");

		return "/receipt/fetch";
	}

	/**
	 * 方法feedbackCancel的Ajax版
	 * 
	 * @param policyNo
	 * @return
	 */
	@RequestMapping("/receipt/feedback/autoCancel")
	public void feedbackAutoCancel(String policyNo, String receiptStatus) {
		receiptService.feedbackCancel(policyNo, "2".equals(receiptStatus) ? "2"
				: "3");
	}

	/**
	 * 修改日期，输入保单号后查询
	 * 
	 * @param policyNo
	 * @param entryFlag
	 * @param model
	 * @return
	 */
	@RequestMapping("/receipt/feedback/updateQuery")
	public String dateUpdateQuery(String policyNo, String entryFlag, Model model) {

		Map map = receiptService.dateUpdateQuery(policyNo);

		if (map != null) {
			map.put("attachs",
					fileService.fileNameUrlList(policyNo, null, "1;2;3"));
			model.addAttribute("contract", map);

		} else {
			model.addAttribute("RETURN_MSG", "保单回执未处于可修改状态");
		}

		model.addAttribute("entryFlag", entryFlag);
		return "/receipt/update";
	}

	/**
	 * 提交修改日期
	 * 
	 * @param files
	 * @param entryFlag
	 * @param policyNoInput
	 * @param newDate
	 * @param remark
	 * @param model
	 * @return
	 */
	@RequestMapping("/receipt/feedback/updateSubmit")
	@Transactional(propagation = Propagation.REQUIRED)
	public String dateUpdateSubmit(@RequestParam MultipartFile updateFile,
			String entryFlag, String policyNoInput, String oldDate, String newDate,
			String remark, Model model) {

		fileService.uploadAndRecord(updateFile, policyNoInput, null, "2");

		receiptService.dateUpdateSubmit(entryFlag, policyNoInput, newDate,
				remark);
		
		//写 入保全保单介质数据调整日志表
		String processor = PlatformContext.getCurrentUser();
		Date processDate = commonQueryDAO.getSystemDate();
		String columnName = null;
		if("S".equals(entryFlag)) {
			columnName = "CLIENT_SIGN_DATE";
		} else if("C".equals(entryFlag)) {
			columnName = "CONFIRM_DATE";
		}
		receiptService.addPosPolContractLog(policyNoInput, processor, processDate, 
				columnName, oldDate, newDate, remark);

		model.addAttribute("entryFlag", entryFlag);
		model.addAttribute("RETURN_MSG", "操作成功");
		return "/receipt/update";
	}

	/**
	 * 延期回销报备 入口
	 */
	@RequestMapping("/receipt/delaymemo")
	public String memoEntry() {
		return "/receipt/delaymemo";
	}

	/**
	 * 延期回销报备 查询输入保单
	 * 
	 * @return
	 */
	@RequestMapping("/receipt/delaymemo/policyQuery")
	public String memoPolicyQuery(String policyNo, Model model) {

		model.mergeAttributes(receiptService.memoPolicyQuery(policyNo));

		return "/receipt/delaymemo";
	}

	/**
	 * 延期回销报备 提交报备信息
	 * 
	 * @return
	 */
	@RequestMapping("/receipt/delaymemo/submit")
	@Transactional(propagation = Propagation.REQUIRED)
	public String memoSubmit(@RequestParam MultipartFile memoFile,
			String thePolicyNo, String memoDate, String provideDate,
			String unDeliveryCause, String channel, Model model) {

		String msg = receiptService.memoCheck(memoDate, provideDate, channel);
		if ("Y".equals(msg)) {

			fileService.uploadAndRecord(memoFile, thePolicyNo, null, "3");
			receiptService.memoSubmit(thePolicyNo, memoDate, unDeliveryCause);

			model.addAttribute("RETURN_MSG", "报备信息提交成功");

		} else {
			model.addAttribute("RETURN_MSG", msg);
		}

		return "/receipt/delaymemo";
	}

	/**
	 * 回执清单 入口
	 */
	@RequestMapping("/receipt/list/{path}")
	public String listEntry(@PathVariable("path") String path, String listFlag,
			Model model) {
		model.addAttribute("listFlag", listFlag);

		model.addAttribute("branchAcceptorFlag",
				receiptService.branchAcceptor());
		model.addAttribute("userBranchCode",
				includeService.userBranchCode(PlatformContext.getCurrentUser()));
		return "/receipt/" + path;
	}

	/**
	 * 回执清单 -- 其他追踪报表
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/receipt/list/otherReport/submit")
	public void otherReport(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		Map pMap = new HashMap();
		pMap.put("listType", request.getParameter("listType"));
		pMap.put("channel", request.getParameter("channel"));
		pMap.put("timeConsume", request.getParameter("timeConsume"));
		pMap.put("confirmUser", request.getParameter("confirmUser"));
		pMap.put("branchCode", request.getParameter("branchCode"));
		pMap.put("startDate", request.getParameter("startDate"));
		pMap.put("endDate", request.getParameter("endDate"));

		List data = receiptService.otherReport(pMap);

		List<String> head = new ArrayList<String>();
		List<String> key = new ArrayList<String>();

		String fileName = null;
		if ("3".equals(pMap.get("listType"))) {
			head.add("保单号");
			head.add("险种名称");
			head.add("首期保费");
			head.add("投保人姓名");
			head.add("保单打印日");
			head.add("回执签署日");
			head.add("系统回销日");
			head.add("业务员代码");
			head.add("操作员工号");
			head.add("保单部门名称");
			head.add("回执来源");
			head.add("保单来源");

			key.add("POLICY_NO");
			key.add("FULL_NAME");
			key.add("RECEIVABLE");
			key.add("CLIENT_NAME");
			key.add("PROVIDE_DATE");
			key.add("CLIENT_SIGN_DATE");
			key.add("CONFIRM_DATE");
			key.add("AGENT_NO");
			key.add("CONFIRM_USER");
			key.add("DEPT_FULL_NAME");
			key.add("RECEIPT_BUSINESS_SOURCE");
			key.add("APPLY_SOURCE_DESC");

			fileName = pMap.get("startDate") + "至" + pMap.get("endDate")
					+ "回执录入信息清单.xls";

		} else {
			head.add("机构名称");
			head.add("渠道");
			head.add("保单号");
			head.add("投保人姓名");
			// head.add("投保人性别");
			// head.add("被保人姓名");
			// head.add("缴费期限");
			// head.add("保险年期");
			// head.add("保费");
			head.add("险种");
			head.add("承保日期");
			head.add("保单打印日期");
			head.add("保单签收日期");
			head.add("保单回执扫描日期");
			head.add("保单回执回销日期");
			// head.add("投保人联系电话");
			head.add("业务员代码");
			head.add("业务员姓名");
			head.add("业务员电话");
			head.add("保单部门名称");
			head.add("保单有效状态");
			head.add("回执回销人");
			head.add("回销状态");
			head.add("回执来源");
			head.add("保单来源");

			key.add("BRANCH_FULL_NAME");
			key.add("CHANNEL_DESC");
			key.add("POLICY_NO");
			key.add("APP_NAME");
			// key.add("APP_SEX");
			// key.add("INS_NAME");
			// key.add("PREM_TERM");
			// key.add("COVERAGE_PERIOD");
			// key.add("MODAL_TOTAL_PREM");
			key.add("PRODUCT_NAME");
			key.add("ISSUE_DATE");
			key.add("PROVIDE_DATE");
			key.add("CLIENT_SIGN_DATE");
			key.add("SCAN_DATE");
			key.add("CONFIRM_DATE");
			// key.add("APP_PHONES");
			key.add("AGENT_NO");
			key.add("EMP_NAME");
			key.add("EMP_PHONE");
			key.add("DEPT_FULL_NAME");
			key.add("DUTY_STATUS");
			key.add("CONFIRM_USER");
			key.add("SIGN_FLAG");
			key.add("RECEIPT_BUSINESS_SOURCE");
			key.add("APPLY_SOURCE_DESC");

			fileName = pMap.get("startDate") + "至" + pMap.get("endDate")
					+ "回执回销信息清单.xls";
		}
		File file = new File(Excel.listToExcel(head, key, data));
		int i;
		response.setContentType("application/vnd.ms-excel");
		response.addHeader("Content-Disposition", "attachment;filename=\""
				+ URLEncoder.encode(fileName, "UTF-8") + "\"");
		FileInputStream fis = null;
		ServletOutputStream out = null;
		try {
			fis = new FileInputStream(file);
			out = response.getOutputStream();
			while ((i = fis.read()) != -1) {
				out.write(i);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);

		} finally {
			PosUtils.safeCloseInputStream(fis);
			PosUtils.safeCloseOuputStream(out);
			PosUtils.deleteFile(file);
		}
	}

	/**
	 * 跳过当前单取上一单或下一单
	 * 
	 * @param policyNo
	 * @param model
	 * @return
	 */
	@RequestMapping("/receipt/feedback/skip")
	public String skipCurrent(String policyNo, String receiptStatus,
			String skipFlag, String channel, Model model) {
		feedbackCancel(policyNo, receiptStatus);

		Map map = receiptService.skipCurrent(policyNo, skipFlag, channel);
		if (map.get("policyNo") != null) {
			model.addAttribute("channelChosed", channel);

			return fetchOne((String) map.get("policyNo"), "P", null, model);

		} else {
			model.addAttribute("RETURN_MSG", "作业池没有数据了");
			return "/receipt/fetch";
		}
	}

	/**
	 * 查询当前用户作业池中及所属机构作业池中为回销保单的总数
	 * 
	 * @param channel
	 * @return
	 */
	@RequestMapping("/receipt/feedback/queryUnReceiptCount")
	@ResponseBody
	public String queryCurUserUnReceiptCount(String channel) {

		return receiptService.queryCurUserUnReceiptCount(channel);

	}

	/**
	 * 预修改回执主索引及扉页号
	 * 
	 * @param request
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-12-23
	 */
	@RequestMapping("/receipt/modifyReceipt/prepareModifyReceipt")
	public String prepareModifyReceipt(HttpServletRequest request) {
		return "/receipt/modifyReceipt";
	}

	/**
	 * 查询修改前的扉页号
	 * 
	 * @param policyNo
	 * @return Map<String,Object>
	 * @author GaoJiaMing
	 * @time 2014-12-23
	 */
	@RequestMapping(value = "/receipt/modifyReceipt/queryOldPressNo", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> queryOldPageNo(String policyNo) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 预留中介系统查询修改前的扉页号
		String oldPressNo = "";
		oldPressNo=receiptService.queryPressNo(policyNo);
		map.put("oldPressNo", oldPressNo);
		return map;
	}

	/**
	 * 查询扉页号
	 * 
	 * @param barCodeNo
	 * @return Map<String,Object>
	 * @author GaoJiaMing
	 * @time 2014-12-23
	 */
	@RequestMapping(value = "/receipt/modifyReceipt/queryPressNo", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> queryOldIndex(String barCodeNo) {
		Map<String, Object> map = new HashMap<String, Object>();
		String pressNo = "oldPressNo";
		pressNo = receiptService.queryPressNo(barCodeNo);
		map.put("oldPressNo", pressNo);
		return map;
	}

	/**
	 * 修改扉页号
	 * 
	 * @param policyNo
	 * @param newPageNo
	 * @return Map<String,Object>
	 * @author GaoJiaMing
	 * @time 2014-12-23
	 */
	@RequestMapping(value = "/receipt/modifyReceipt/modifyOldPageNo", method = RequestMethod.GET)
	@ResponseBody
	public String modifyOldPageNo(String policyNo, String newPageNo) {
		// 预留中介系统修改扉页号
		String result = "Y";
		result = receiptService.modifyPressNo(policyNo, "", newPageNo);
		return result;
	}

	/**
	 * 查询影像主索引
	 * 
	 * @param policyNo
	 * @param newPageNo
	 * @return Map<String,Object>
	 * @author GaoJiaMing
	 * @time 2014-12-23
	 */
	@RequestMapping(value = "/receipt/modifyReceipt/queryImageMainIndex", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> queryImageMainIndex(String barCodeNo) {
		Map<String, Object> map = new HashMap<String, Object>();
		String imageMainIndex = "";
		imageMainIndex = receiptService.queryImageMainIndex(barCodeNo);
		map.put("oldImageMainIndex", imageMainIndex);

		return map;
	}

	/**
	 * 修改影像主索引
	 * 
	 * @param barCodeNo
	 * @param newIndex
	 * @return Map<String,Object>
	 * @author GaoJiaMing
	 * @time 2014-12-23
	 */
	@RequestMapping(value = "/receipt/modifyReceipt/modifyOldIndex", method = RequestMethod.GET)
	@ResponseBody
	public String modifyOldIndex(String barCodeNo, String oldIndex,
			String newIndex) {
		// 预留影像系统修改影像主索引
		String result = "Y";
		result = receiptService.modifyImageBarcode(barCodeNo, oldIndex,
				newIndex);
		return result;
	}
}
