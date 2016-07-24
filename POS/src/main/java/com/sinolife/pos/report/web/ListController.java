package com.sinolife.pos.report.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.unihub.framework.util.common.DateUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.life.foundation.common.lang.DateUtils;
import com.sinolife.pos.common.dto.PosProblemItemsDTO;
import com.sinolife.pos.common.include.service.IncludeService;
import com.sinolife.pos.common.util.Excel;
import com.sinolife.pos.common.util.PaginationDataWrapper;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.common.web.PosAbstractController;
import com.sinolife.pos.others.mucode.service.MucodeService;
import com.sinolife.pos.pubInterface.biz.service.WebPosAcceptInterface;
import com.sinolife.pos.report.service.ListService;
import com.sinolife.sf.framework.controller.BrowserNoCache;
import com.sinolife.sf.framework.template.VelocityService;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.store.SFFile;

/**
 * 保全清单
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class ListController extends PosAbstractController {

	@Autowired
	ListService service;

	@Autowired
	IncludeService includeService;

	@Autowired
	MucodeService mucodeService;

	@Autowired
	WebPosAcceptInterface webPosAcceptInterface;

	/**
	 * 入口
	 * 
	 * @return
	 */
	@RequestMapping("/report/{path}")
	public String entry(@PathVariable("path") String path,
			HttpServletRequest request, Model model) {
		if ("mortgage".equals(path) || "survplan".equals(path)) {
			model.addAttribute("products",
					JSONArray.fromObject(includeService.productsList())
							.toString());
		}

		model.addAttribute("userBranchCode",
				includeService.userBranchCode(PlatformContext.getCurrentUser()));

		String policyChannelType = request.getParameter("policyChannelType");

		if (policyChannelType!= null&&policyChannelType.equals("06")) {
			model.addAttribute("userBranchCode", "86");
			
		}
		model.addAttribute("policyChannelType", policyChannelType);
		return "/report/" + path;
	}

	/**
	 * 生成清单
	 * 
	 * @param request
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/report/submit")
	@BrowserNoCache(enableNoCache = false)
	public void getReport(HttpServletRequest request,
			HttpServletResponse response) {
		Map pMap = new HashMap();
		Enumeration<String> en = request.getParameterNames();
		while (en.hasMoreElements()) {
			String k = en.nextElement();
			Object v = request.getParameter(k);
			if (k.endsWith("Date")) {
				// 日期值按这个结构来命名，以Date结尾
				try {
					v = new SimpleDateFormat("yyyy-MM-dd").parse((String) v);
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			}
			if (k.endsWith("UserId")) {
				// 用户账号按这个结构来命名，以UserId结尾
				v = PosUtils.parseInputUser((String) v);
			}
			pMap.put(k, v);
		}// 生成参数map

		service.getReport(pMap);

		File file = new File(Excel.listToExcel(pMap));
		FileInputStream fis = null;
		ServletOutputStream out = null;
		try {
			int i;
			response.setContentType("application/vnd.ms-excel");
			response.addHeader("Content-Disposition", "attachment;filename=\""
					+ URLEncoder.encode("" + pMap.get("listName"), "UTF-8")
					+ "\"");
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
	 * 生成清单
	 * 
	 * @param request
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/report/new/submit")
	@BrowserNoCache(enableNoCache = false)
	public void exportList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map pMap = new HashMap();
		Enumeration<String> en = request.getParameterNames();
		while (en.hasMoreElements()) {
			String k = en.nextElement();
			Object v = request.getParameter(k);

			if (k.endsWith("Date")) {
				// 日期值按这个结构来命名，以Date结尾
				try {
					if (!StringUtils.isBlank((String) v)) {
						v = new SimpleDateFormat("yyyy-MM-dd").parse((String) v);
					} else {
						v = null;
					}
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			}
			if (k.endsWith("UserId")) {
				// 用户账号按这个结构来命名，以UserId结尾
				v = PosUtils.parseInputUser((String) v);
			}
			pMap.put(k, v);

		}
		// 生成参数map
		String fileName = (String) pMap.get("sheetName");
		String exportName = URLEncoder.encode(fileName, "UTF-8");
		response.setContentType("application/vnd.ms-excel");

		response.addHeader(
				"content-Disposition",
				"attachment;filename=\""
						+ exportName
						+ DateUtils.date2String(new Date(),
								"yyyy-MM-dd HH:mm:ss") + ".xls\"");
		exportListExcel(request, response, pMap);
	}

	public static String formatDate(Date date, String fmt) {
		SimpleDateFormat sDateFormat = new SimpleDateFormat(fmt);
		String formateDate = sDateFormat.format(date);
		return formateDate;
	}

	/**
	 * 导出为excel
	 */
	public void exportListExcel(HttpServletRequest request,
			HttpServletResponse response, Map pMap) throws Exception {
		StringBuilder sb = new StringBuilder();
		final PrintWriter out = response.getWriter();
		VelocityService velocityService = PlatformContext.getGoalbalContext(
				VelocityService.class, VelocityService.class);
		List<String> displayDescList = new ArrayList<String>();
		StringBuffer headerSql = new StringBuffer();

		// 查询清单，并插入临时表
		service.queryList(pMap);

		// 查询清单显示字段基表
		String listCode = (String) pMap.get("listCode");
		List<Map<String, String>> codeList = service.getListHeader(listCode);

		// 组装Excel显示字段和查询字段变量
		for (int i = 0; i < codeList.size(); i++) {
			displayDescList.add(codeList.get(i).get("DISPLAYDESC"));
			if (i == 0) {
				headerSql = headerSql.append(codeList.get(i).get("COLUMNNAME")
						+ " cel0");
			} else {
				headerSql = headerSql.append(","
						+ codeList.get(i).get("COLUMNNAME") + " cel" + i);
			}
		}
		// 从临时表查询清单记录
		String queryIndex = (String) pMap.get("queryIndex");
		final int len = service.getListCount(queryIndex);
		Date curDate = new Date();
		pMap.put("createdDate", formatDate(curDate, "yyyy-MM-dd HH:mm:ss"));
		pMap.put("lastSavedDate", formatDate(curDate, "yyyy-MM-dd HH:mm:ss"));
		pMap.put("expandedRowCount", (len + 2) + "");
		pMap.put("expandedColumnCount", String.valueOf(codeList.size() + 10));
		pMap.put("mergeAcross", String.valueOf(codeList.size() - 1));
		pMap.put("sheetName", (String) pMap.get("sheetName"));
		sb.append(velocityService
				.mergeTemplateIntoString("pos_header.vm", pMap));

		sb.append("<Row ss:StyleID=\"s26\">");
		for (int i = 0; i < displayDescList.size(); i++) {
			sb.append("<Cell><Data ss:Type=\"String\">")
					.append(displayDescList.get(i)).append("</Data></Cell>");
		}
		sb.append("</Row>");
		out.write(sb.toString());
		sb = null;
		sb = new StringBuilder();

		if (len > 0) {
			sb = null;
			sb = new StringBuilder();
			int limitedNum = 1000;
			int start = 0;
			List<Map<String, Object>> reportList = null;
			for (start = 0; start < len / limitedNum + 1; start++) {
				reportList = service.getListResult(queryIndex,
						headerSql.toString(), start * limitedNum + 1,
						(start + 1) * limitedNum);

				for (int i = 0; i < reportList.size(); i++) {

					sb.append("<Row ss:StyleID=\"s26\">");

					Map<String, Object> reviewMap = (Map<String, Object>) reportList
							.get(i);
					for (int j = 0; j < reviewMap.size(); j++) {

						sb.append("<Cell><Data ss:Type=\"String\">")

								.append(reviewMap.get("CEL" + j) == null ? ""
										: xmlFilter(reviewMap.get("CEL" + j)
												.toString()))
								.append("</Data></Cell>");

					}

					sb.append("</Row>");

				}

				out.write(sb.toString());
				sb = null;
				sb = new StringBuilder();

			}

		}

		// 输入footer
		sb.append(velocityService
				.mergeTemplateIntoString("pos_footer.vm", null));
		out.write(sb.toString());
		out.flush();
		out.close();
		sb = null;
	}

	public static String xmlFilter(String value) {
		if (value == null)
			return null;
		char content[] = new char[value.length()];
		value.getChars(0, value.length(), content, 0);
		StringBuffer result = new StringBuffer(content.length + 50);
		for (int i = 0; i < content.length; i++)
			switch (content[i]) {
			case 60: // '<'
				result.append("&lt;");
				break;

			case 62: // '>'
				result.append("&gt;");
				break;

			case 38: // '&'
				result.append("&amp;");
				break;

			case 34: // '"'
				result.append("&quot;");
				break;

			case 39: // '\''
				result.append("&#39;");
				break;

			default:
				result.append(content[i]);
				break;
			}

		return result.toString();
	}

	@RequestMapping("/report/posexpirationpay")
	public String entry(Model model) {

		model.addAttribute("userBranchCode",
				includeService.userBranchCode(PlatformContext.getCurrentUser()));

		return "/report/posexpirationpay";
	}

	/**
	 * 满期金插入提取码
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/report/new/sendMessageCode")
	@ResponseBody
	public Map sendMessageCode(HttpServletRequest request) {

		Map pMap = new HashMap();
		pMap.put("BRANCH_CODE", request.getParameter("branchCode"));
		pMap.put("DEPT_NO", request.getParameter("deptNo"));
		pMap.put("MATURE_PAY_START_DATE", request.getParameter("startDate"));
		pMap.put("MATURE_PAY_END_DATE", request.getParameter("endDate"));
		pMap.put("TRANS_AUTH_INDI", request.getParameter("transAuthIndi"));
		pMap.put("EXTRACT_FLAG", "N");
		pMap.put("LIST_CODE", request.getParameter("listCode"));

		Map<String, Object> returnMap = new HashMap<String, Object>();

		int length = 6;
		PosUtils p = new PosUtils();
		String code = p.randomCode(length);
		// 查询提取码是否存在
		int count = service.queryRandomCode(code);

		while (count > 0) {
			code = p.randomCode(length);
			count = service.queryRandomCode(code);
		}

		pMap.put("EXTRACT_CODE", code);

		try {

			// 根据机构和银行网点查询记录
			returnMap = mucodeService.checkName(pMap);
			if (returnMap == null || returnMap.size() == 0) {

				returnMap = new HashMap<String, Object>();
				returnMap.put("flag", "NOBRANCHCODE");

				return returnMap;

			} else {

				pMap.put("MOBILE_NO", returnMap.get("MOBILE_NO"));

				returnMap.put("flag", "YBRANCHCODE");

			}

		} catch (Exception e) {
			logger.error(e);
			returnMap.put("flag", "E");
			returnMap.put("message", "系统异常：" + e.getMessage());

		}
		// 查询sequence
		int seq = service.querySequence();

		String strSeq = String.valueOf(seq);
		String newSeq = strSeq;
		for (int i = 0; i < 6 - strSeq.length(); i++) {

			newSeq = "0" + newSeq;

		}

		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);

		// 拼装fileName
		String fileName = String.valueOf(year) + newSeq;
		pMap.put("FILE_NAME", fileName);
		// 插入提取码
		try {

			service.insertCode(pMap);

		} catch (Exception e) {
			logger.error(e);
			returnMap.put("flag", "E");
			returnMap.put("message", e.getMessage());

		}

		return returnMap;

	}

	/**
	 * 生成异步清单提取任务
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-8-8
	 */
	@RequestMapping("/report/task/submit")
	@BrowserNoCache(enableNoCache = false)
	public String insertReportTask(HttpServletRequest request, HttpServletResponse response, Model model) {
		Map paraMap = new HashMap();
		StringBuffer parametersTemp = new StringBuffer();
		int num = 0;
		Enumeration<String> en = request.getParameterNames();
		while (en.hasMoreElements()) {
			if (num > 0) {
				parametersTemp.append(",");
			}
			String k = en.nextElement();
			Object v = request.getParameter(k);
			if (k.endsWith("UserId")) {
				// 用户账号按这个结构来命名，以UserId结尾
				v = PosUtils.parseInputUser((String) v);
			}
			if ("listCode".equals(k)) {
				paraMap.put("list_code", v);
			}
			parametersTemp.append("#").append(k).append("#").append(":").append("#").append(v).append("#");
			num++;
		}
		paraMap.put("user_id", PlatformContext.getCurrentUserInfo().getUserId());
		paraMap.put("parameters", parametersTemp.toString());
		service.insertReportTask(paraMap);
		return "/report/reporttask";
	}

	/**
	 * 跳转异步清单提取任务查询
	 * 
	 * @param request
	 * @param model
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-8-19
	 */
	@RequestMapping("/report/queryReportTask")
	public String queryReportTask(HttpServletRequest request, Model model) {
		return "/report/reporttask";
	}

	/**
	 * 异步清单提取任务分页查询
	 * 
	 * @param request
	 * @param page
	 * @param rows
	 * @return Map<String,Object>
	 * @author GaoJiaMing
	 * @time 2014-8-25
	 */
	@RequestMapping("/report/queryReportTaskPage")
	@ResponseBody
	public Map<String, Object> queryReportTaskPage(HttpServletRequest request, @RequestParam int page,
			@RequestParam int rows) {
		Map paraMap = new HashMap();
		Enumeration<String> en = request.getParameterNames();
		while (en.hasMoreElements()) {
			String k = en.nextElement();
			Object v = request.getParameter(k);

			paraMap.put(k, v);
		}
		paraMap.put("user_id", PlatformContext.getCurrentUserInfo().getUserId());
		List list = service.queryReportTask(paraMap);
		PaginationDataWrapper<PosProblemItemsDTO> wrapper = new PaginationDataWrapper<PosProblemItemsDTO>(list, rows);
		wrapper.gotoPage(page);
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("total", wrapper.getTotalDataSize());
		retMap.put("rows", wrapper.getCurrentPageDataList());
		return retMap;
	}

	/**
	 * 清单任务数据下载
	 * 
	 * @param startDate
	 * @param endDate
	 * @param imFileId
	 * @param userId
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-8-12
	 */
	@RequestMapping("/report/downLoadEexelData")
	public ModelAndView downLoadEexelData(@RequestParam String startDate, @RequestParam String endDate,
			@RequestParam String imFileId, @RequestParam String userId, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("/report/reporttask");
		File file = null;
		FileInputStream fis = null;
		ServletOutputStream out = null;
		try {
			SFFile tmpFile = PlatformContext.getIMFileService().getFileFormId(imFileId);
			file = new File(tmpFile.getPath());
			Map paraMapForFileName = new HashMap();
			paraMapForFileName.put("userId", userId);
			paraMapForFileName.put("imFileId", imFileId);
			String fileName = service.getDownLoadFileName(paraMapForFileName);
			int i;
			response.setContentType("application/vnd.ms-excel");
			response.addHeader("Content-Disposition",
					"attachment;filename=\"" + URLEncoder.encode("" + fileName, "UTF-8") + "\"");
			fis = new FileInputStream(file);
			out = response.getOutputStream();
			while ((i = fis.read()) != -1) {
				out.write(i);
			}
			return null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			mav.addObject("RETURN_MSG", "下载出错");
			mav.addObject("startDate", startDate);
			mav.addObject("endDate", endDate);
			Map paraMap = new HashMap();
			paraMap.put("user_id", PlatformContext.getCurrentUserInfo().getUserId());
			paraMap.put("startDate", startDate);
			paraMap.put("endDate", endDate);
			List list = service.queryReportTask(paraMap);
			mav.addObject("taskList", list);
			return mav;
		} finally {
			PosUtils.safeCloseInputStream(fis);
			PosUtils.safeCloseOuputStream(out);
			PosUtils.deleteFile(file);
		}
	}

	/**
	 * 校验同一用户同一清单是否重复提交
	 * 
	 * @param listCode
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-8-13
	 */
	@RequestMapping(value = "/report/checkReportTaskStatus", method = RequestMethod.GET)
	@ResponseBody
	public String checkReportTaskStatus(String listCode) {
		Map paraMap = new HashMap();
		paraMap.put("user_id", PlatformContext.getCurrentUserInfo().getUserId());
		paraMap.put("listCode", listCode);
		return service.checkReportTaskStatus(paraMap);
	}

	/**
	 * 清单作废
	 * 
	 * @param listCode
	 * @param userId
	 * @param submitTime
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-9-17
	 */
	@RequestMapping(value = "/report/updatePosListControlIsValid", method = RequestMethod.GET)
	@ResponseBody
	public String updatePosListControlIsValid(String listCode, String userId, String submitTime) {
		Map paraMap = new HashMap();
		paraMap.put("userId", userId);
		paraMap.put("listCode", listCode);
		paraMap.put("submitTime", submitTime);
		return service.updatePosListControlIsValid(paraMap);
	}
}
