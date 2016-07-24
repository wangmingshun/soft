package com.sinolife.report.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

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
import com.sinolife.report.service.AsyncListService;
import com.sinolife.sf.framework.controller.BrowserNoCache;
import com.sinolife.sf.framework.template.VelocityService;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.store.SFFile;


@Controller
public class AsyncListController extends PosAbstractController{
	@Autowired
	IncludeService includeService;
	@Autowired
	AsyncListService asyncListService;
	/**
	 * 入口
	 * @author Luoyonggang
	 * @return
	 */
	
	@RequestMapping("/async/{path}")
	public String entry(@PathVariable("path") String path,
			HttpServletRequest request, Model model) {
		if ("list_13".equals(path) || "list_07".equals(path)) {
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
		return "/asynclist/" + path;
	}
	
	/**
	 * 异步清单提取任务分页查询
	 * 
	 * @param request
	 * @param page
	 * @param rows
	 * @return Map<String,Object>
	 * @author Luoyonggang
	 */
	@RequestMapping("/async/queryReportTaskPage")
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
		List list = asyncListService.queryAsyncTask(paraMap);
		PaginationDataWrapper<PosProblemItemsDTO> wrapper = new PaginationDataWrapper<PosProblemItemsDTO>(list, rows);
		wrapper.gotoPage(page);
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("total", wrapper.getTotalDataSize());
		retMap.put("rows", wrapper.getCurrentPageDataList());
		return retMap;
	}
	
	/**
	 * 生成异步清单提取任务
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return String
	 * @author Luoyonggang
	 */
	@RequestMapping("/async/task/submit")
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
		parametersTemp.append(",#moduleId#:#04#");
		//获取创建人
		String createUser = PlatformContext.getCurrentUserInfo().getUserId().split("@")[0];
		String create_user= createUser.replace(".","");
		paraMap.put("created_user", create_user);
		paraMap.put("user_id", PlatformContext.getCurrentUserInfo().getUserId());
		paraMap.put("parameters", parametersTemp.toString());
		asyncListService.insertReportTask(paraMap);
		return "/asynclist/asynchronousList";
	}
	/**
	 * 清单作废
	 * 
	 * @param listCode
	 * @param userId
	 * @param submitTime
	 * @return String
	 * @author Luoyonggang
	 */
	@RequestMapping(value = "/async/updatePosListControlIsValid", method = RequestMethod.GET)
	@ResponseBody
	public String updatePosListControlIsValid(String listCode, String userId, String submitTime) {
		Map paraMap = new HashMap();
		paraMap.put("userId", userId);
		paraMap.put("listCode", listCode);
		paraMap.put("submitTime", submitTime);
		return asyncListService.updatePosListControlIsValid(paraMap);
	}
	
	/**
	 * 清单任务数据下载
	 * 
	 * @param startDate
	 * @param endDate
	 * @param imFileId
	 * @param userId
	 * @return String
	 * @author Luoyonggang
	 */
	@RequestMapping("/async/downLoadEexelData")
	public ModelAndView downLoadEexelData(@RequestParam String startDate, @RequestParam String endDate,
			@RequestParam String imFileId, @RequestParam String userId, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("/asynclist/asynchronousList");
		File file = null;
		FileInputStream fis = null;
		ServletOutputStream out = null;
		try {
			SFFile tmpFile = PlatformContext.getIMFileService().getFileFormId(imFileId);
			file = new File(tmpFile.getPath());
			Map paraMapForFileName = new HashMap();
			paraMapForFileName.put("userId", userId);
			paraMapForFileName.put("imFileId", imFileId);
			String fileName = asyncListService.getDownLoadFileName(paraMapForFileName);
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
			List list = asyncListService.queryAsyncTask(paraMap);
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
	 * @author Luoyonggang
	 */
	@RequestMapping(value = "/async/checkReportTaskStatus", method = RequestMethod.GET)
	@ResponseBody
	public String checkReportTaskStatus(String listCode) {
		Map paraMap = new HashMap();
		paraMap.put("user_id", PlatformContext.getCurrentUserInfo().getUserId());
		paraMap.put("listCode", listCode);
		return asyncListService.checkReportTaskStatus(paraMap);
	}
	
	/**
	 * 生成清单
	 * 
	 * @param request
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/async/new/submit")
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
		asyncListService.queryList(pMap);

		// 查询清单显示字段基表
		String listCode = (String) pMap.get("listCode");
		List<Map<String, String>> codeList = asyncListService.getListHeader(listCode);

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
		final int len = asyncListService.getListCount(queryIndex);
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
				reportList = asyncListService.getListResult(queryIndex,
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
	
	/**
	 * 生成清单
	 * 
	 * @param request
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/async/submit")
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

		asyncListService.getReport(pMap);

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
}
