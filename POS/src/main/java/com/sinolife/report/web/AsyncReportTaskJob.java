package com.sinolife.report.web;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.print.service.PrintService;
import com.sinolife.report.dao.AsyncListDao;
import com.sinolife.report.dao.AsyncScheduleDAO;
import com.sinolife.report.service.AsyncListService;
import com.sinolife.sf.attatch.IMFileService;
import com.sinolife.sf.im.IMFileServiceImpl;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.store.MimeType;
import com.sinolife.sf.store.SFFile;
import com.sinolife.sf.store.SFFilePath;
import com.sinolife.sf.store.StogeType;
import org.apache.log4j.Logger;

@Component("asyncReportTaskJob")
public class AsyncReportTaskJob {
	@Autowired
	private CommonQueryDAO commonQueryDAO;
	private Logger logger = Logger.getLogger(getClass());
	@Autowired
	private PrintService printService;
	@Autowired
	private TransactionTemplate txTmpl;
	@Autowired
	private AsyncScheduleDAO asyncScheduledao;
	@Autowired
	private AsyncListDao asyncListdao;
	@Autowired
	private AsyncListService asyncListService; 
	public void execute() {
		logger.info("asyncReportTaskJob " + Thread.currentThread().getId() + " started at "
				+ PosUtils.formatDate(commonQueryDAO.getSystemDate(), "yyyy-MM-dd HH:mm:ss:SSS"));
		// 处理报表数据
		List<Map<String, Object>> reportTaskList = asyncScheduledao.queryReportTask();
		if (reportTaskList != null && !reportTaskList.isEmpty()) {
			for (final Map<String, Object> map : reportTaskList) {
				txTmpl.execute(new TransactionCallback<Boolean>() {
					@Override
					public Boolean doInTransaction(TransactionStatus transactionstatus) {
						// 锁定单条记录
						boolean locked = asyncScheduledao.lockReportTask(map);
						if (!locked) {
							return false;
						}
						List<Map<String, Object>> sffileList = new ArrayList<Map<String, Object>>();
						Map<String, Object> fileMap = new HashMap<String, Object>();
						SFFile tmpFile = null;
						String fileName = null;
						String content = null;
						String fileID = null;
						try {
							logger.info("LIST_CODE:" + map.get("LIST_CODE")
									+ ",USER_ID:" + map.get("USER_ID")
									+ ",SUBMIT_TIME:" + map.get("SUBMIT_TIME")
									+ ",SUBMIT_TIME_MAIL" + map.get("SUBMIT_TIME_MAIL")
									+ ",QUERY_INDEX:" + map.get("QUERY_INDEX")
									+ ",DESCRIPTION:" + map.get("DESCRIPTION")
									+ ",DATA_AMOUNT:" + map.get("DATA_AMOUNT"));
							// 任务开始，更新状态和开始时间
							asyncListdao.updateReportTaskStart(map);
							logger.info("更新状态表listintf.async_list_control的status_code = '3'完成！");
							if (map.get("DATA_AMOUNT") != null && ((BigDecimal) map.get("DATA_AMOUNT")).intValue() > 0) {
								String listCode = (String) map.get("LIST_CODE");
								//先用03保全处理清单做实验 edit by wangmingshun start
								if("03".equals(listCode)) {
									//分批读取数据方法
									logger.info("addToTempFile 03" + Thread.currentThread().getId() + " started at "
											+ PosUtils.formatDate(commonQueryDAO.getSystemDate(), "yyyy-MM-dd HH:mm:ss:SSS"));
									Map<String, Object> pMap = 
										asyncListService.createReportTempFile(
												(String) map.get("LIST_CODE"),
												(String) map.get("QUERY_INDEX"));
									tmpFile = (SFFile) pMap.get("file");
									fileID = (String) pMap.get("fileId");
									logger.info("addToTempFile 03" + Thread.currentThread().getId() + " ended at "
											+ PosUtils.formatDate(commonQueryDAO.getSystemDate(), "yyyy-MM-dd HH:mm:ss:SSS"));
								//先用03保全处理清单做实验 edit by wangmingshun start
								} else {
									//通过query_index从临时表取数据并生成excel文件
									tmpFile = getDateAndToExcel(map);
									// 上传IM服务器
									fileID = uploadFileToIM(tmpFile);
								}
								logger.info("fileID:" + fileID);
								if (StringUtils.isBlank(fileID)) {
									transactionstatus.setRollbackOnly();
									return false;
								} else {
									fileName = (String) map.get("DESCRIPTION") + (String) map.get("SUBMIT_TIME")
											+ ".xls";
									logger.info("fileName:" + fileName);
								}
								// 给用户发邮件
								long length = tmpFile.length();
								/*
								 * 小于10M才加附件
								 * 注意限制小于10M发附件的时候，最好将10*1024*1024缩小处理(例如，改为8*1024*1024)
								 * 否则会出现在10M的临界点时，系统判断小于10M，但邮箱服务器判断大于10M，实际发送失败的情况
								 */
								if (length < 8 * 1024 * 1024) {
									fileMap.put("fileName", fileName);
									fileMap.put("file", tmpFile);
									sffileList.add(fileMap);
									content = "尊敬的" + map.get("USER_ID") + "用户:" + "<br/>&nbsp;&nbsp;&nbsp;&nbsp;您于"
											+ (String) map.get("SUBMIT_TIME_MAIL") + "提取的"
											+ (String) map.get("DESCRIPTION") + "现发送给您，详见附件，请注意查收。";
								} else {
									content = "尊敬的" + map.get("USER_ID") + "用户:" + "<br/>&nbsp;&nbsp;&nbsp;&nbsp;您于"
											+ (String) map.get("SUBMIT_TIME_MAIL") + "提取的"
											+ (String) map.get("DESCRIPTION") + "已经处理完毕，由于附件过大，不能在邮件中发送，请到清单报表任务查询中下载。";
								}
							} else {
								content = "尊敬的" + map.get("USER_ID") + "用户:" + "<br/>&nbsp;&nbsp;&nbsp;&nbsp;您于"
										+ (String) map.get("SUBMIT_TIME_MAIL") + "提取的"
										+ (String) map.get("DESCRIPTION") + "已经处理完毕，但是没有数据，请知悉！";
							}
							printService.sendEMail((String) map.get("USER_ID"), sffileList, "富德生命人寿清单提取结果", content,
									(String) map.get("DESCRIPTION") + (String) map.get("SUBMIT_TIME"));
							logger.info("用户邮件发送完毕！");
							// 回写状态和处理数据
							map.put("fileID", fileID);
							map.put("fileName", fileName);
							asyncListdao.updateReportTaskEnd(map);
							logger.info("更新状态表listintf.async_list_control的status_code = '4'完成！");
							
						} catch (Exception e) {
							e.printStackTrace();
							transactionstatus.setRollbackOnly();
							logger.error("fileID:" + fileID + ",fileName" + fileName);
							logger.error("生成excel失败！！" + e.getMessage());
							return false;
						} finally {
							if(tmpFile!=null){
								tmpFile.getRpcAttach().setDeleteAfterReturn(true);
								PosUtils.deleteFile(tmpFile);								
							}
						}
						return true;
					}
				});
			}
		}
		logger.info("asyncReportTaskJob " + Thread.currentThread().getId() + " ended at "
				+ PosUtils.formatDate(commonQueryDAO.getSystemDate(), "yyyy-MM-dd HH:mm:ss:SSS"));
	}

	/**
	 * 从临时表里获取数据并生成Excel表
	 * 
	 * @param map
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	private SFFile getDateAndToExcel(Map<String, Object> map) {
		StringBuffer headerSql = new StringBuffer();
		SFFile file = new SFFile();
		// 查询清单显示字段基表
		String listCode = (String) map.get("LIST_CODE");
		List<Map<String, String>> codeList = asyncListService.getListHeader(listCode);
		// 从临时表查询清单记录写入Excel
		String queryIndex = (String) map.get("QUERY_INDEX");
		final int len = asyncListService.getListCount(queryIndex);
		WritableWorkbook book = null;
		int limitedSheetNum = 50000;
		int sheetNum = 0;
		int limitedNum = 1000;
		int start = 0;
		List<Map<String, Object>> reportList = null;
		WritableSheet sheet = null;
		try {
			book = Workbook.createWorkbook(new FileOutputStream(file.getPath()));
			if (len > 0) {
				// 组装查询字段变量
				for (int i = 0; i < codeList.size(); i++) {
					if (i == 0) {
						headerSql = headerSql.append(codeList.get(i).get("COLUMNNAME") + " cel0");
					} else {
						headerSql = headerSql.append("," + codeList.get(i).get("COLUMNNAME") + " cel" + i);
					}
				}
				//查询临时表数据(后面两个参数是分页用的,现在暂时用不上)
				reportList = asyncListService.getListResult(queryIndex, headerSql.toString(),0,0);
				for (int i = 0; i < reportList.size(); i++) {
					if (i % limitedSheetNum == 0) {
						sheetNum++;
						sheet = book.createSheet("sheet" + sheetNum, 0);
						sheet.setRowView(0, 20);
						WritableFont font = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD);
						WritableCellFormat format = new WritableCellFormat(font);
						// 组装Excel显示字段
						for (int j = 0; j < codeList.size(); j++) {
							sheet.setColumnView(j, codeList.get(j).get("DISPLAYDESC").getBytes().length + 5);
							sheet.addCell(new Label(j, 0, codeList.get(j).get("DISPLAYDESC"), format));
						}
					}
					Map<String, Object> reviewMap = (Map<String, Object>) reportList.get(i);
					sheet.setRowView((start * limitedNum + i) % limitedSheetNum + 1, 18);
					for (int j = 0; j < reviewMap.size(); j++) {
						sheet.addCell(new Label(j, (start * limitedNum + i) % limitedSheetNum + 1, reviewMap
								.get("CEL" + j) == null ? "" : String.valueOf(reviewMap.get("CEL" + j))));
					}
				}

			}
			book.write();
			book.close();
		} catch (Exception e) {
			if (book != null) {
				try {
					book.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			throw new RuntimeException("数据生成异常", e);
		}
		return file;
	}

	/**
	 * 上传Excel到IM服务器
	 * 
	 * @param tmpFile
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-6-27
	 */
	public String uploadFileToIM(SFFile tmpFile) {
		try {
			SFFilePath fileDestinationPath = new SFFilePath();
			fileDestinationPath.setModule("pos");
			fileDestinationPath.setModuleSubPath(new String[] { "report_list" });
			fileDestinationPath.setStogeType(StogeType.WEEK);
			fileDestinationPath.setMimeType(MimeType.application_msexcel_xls);
			String fileId = PlatformContext.getIMFileService().putFile(tmpFile, fileDestinationPath);
			return fileId;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
