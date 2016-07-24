package com.sinolife.report.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.report.service.HeadKeyDefine;
import com.sinolife.report.dao.AsyncListDao;
import com.sinolife.report.dao.AsyncScheduleDAO;
import com.sinolife.sf.attatch.UrlSignature;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.store.MimeType;
import com.sinolife.sf.store.SFFile;
import com.sinolife.sf.store.SFFilePath;
import com.sinolife.sf.store.SFFilePathUtil;
import com.sinolife.sf.store.StogeType;

@Service("AsyncListService")
@SuppressWarnings({ "unchecked", "rawtypes" })
public class AsyncListService {
	@Autowired
	AsyncListDao asyncListdao;
	@Autowired
	CommonQueryDAO commonQueryDAO;
	@Autowired
	private AsyncScheduleDAO asyncScheduledao;
	
	@SuppressWarnings("rawtypes")
	public Map getReport(Map pMap) {
		String sql = (String) pMap.get("sql");

		asyncListdao.getReport(sql, pMap);

		try {
			Method m = HeadKeyDefine.class.getMethod(sql, Map.class);// sql与方法同名,必须的
			m.invoke(new HeadKeyDefine(), pMap);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return pMap;
	}
	
	
	/**
	 * 异步清单
	 * @param paraMap
	 * @return
	 * @author Luoyonggang
	 */
	public List queryAsyncTask(Map paraMap) {
		return asyncListdao.queryAsyncTask(paraMap);
	}
	
	/**
	 * 生成异步清单提取任务
	 * 
	 * @param paraMap
	 * @return void
	 * @author Luoyonggang
	 */
	public void insertReportTask(Map paraMap) {
		asyncListdao.insertReportTask(paraMap);
	}
	
	/**
	 * 清单作废
	 * 
	 * @param paraMap
	 * @return String
	 * @author Luoyonggang
	 */
	public String updatePosListControlIsValid(Map paraMap) {
		try {
			asyncListdao.updatePosListControlIsValid(paraMap);
		} catch (Exception e) {
			return "N";
		}
		return "Y";
	}
	
	/**
	 * 查询清单报表表头
	 * @author Luoyonggang
	 */
	public List<Map<String, String>> getListHeader(String listCode) {
		return asyncListdao.getListHeader(listCode);
	}
	
	/**
	 * 查询临时表数据
	 * @author Luoyonggang
	 */
	public int getListCount(String queryIndex) {

		return asyncListdao.getListCount(queryIndex);

	}
	
	/**
	 * 查询分页临时表数据
	 * @author Luoyonggang
	 */
	public List getListResult(String queryIndex, String headerSql,
			int startRow, int endRow) {
		return asyncListdao.getListResult(queryIndex, headerSql, startRow, endRow);
	}
	
	/**
	 * 下载获取对应文件名
	 * @author Luoyonggang
	 * @param paraMap
	 * @return String
	 */
	public String getDownLoadFileName(Map paraMap) {
		return asyncListdao.getDownLoadFileName(paraMap);
	}
	
	/**
	 * 异步清单提取任务查询
	 * 
	 * @param paraMap
	 * @return List
	 * @author Luoyonggang
	 */
	public List queryReportTask(Map paraMap) {
		return asyncListdao.queryReportTask(paraMap);
	}
	/**
	 * 校验同一用户同一清单是否重复提交
	 * 
	 * @param paraMap
	 * @return String
	 * @author Luoyonggang
	 */
	public String checkReportTaskStatus(Map paraMap) {
		return asyncListdao.checkReportTaskStatus(paraMap);
	}
	
	/**
	 * 查询清单，并把结果写入临时表
	 */
	public void queryList(Map pMap) {
		asyncListdao.queryList(pMap);
	}
	
	/**
	 * 将数据库临时表的数据分批写入到临时文件中
	 * @return String
	 * @author WangMingShun
	 * @date 2016-3-15
	 */
	public Map<String, Object> createReportTempFile(String listCode, String queryIndex) {
		String fileId = null;
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String tmpFilePath = PosUtils.getPosProperty("tmpFilePath");
			String dateString = PosUtils.formatDate(commonQueryDAO.getSystemDate(), "yyyyMMdd");
			if(!tmpFilePath.endsWith("/") && !tmpFilePath.endsWith("\\")) {
				tmpFilePath += "/" + dateString;
			} else {
				tmpFilePath += dateString;
			}
			File directory = new File(tmpFilePath);
			if(!directory.exists()) {
				directory.mkdir();
			}    
			
			String tempFileName = PosUtils.formatDate(commonQueryDAO.getSystemDate(), "yyyyMMddHHmmss");
			SFFilePath path = new SFFilePath();
			path.setBase(tmpFilePath);
			path.setFileName(tempFileName);
			SFFile file = new SFFile(path);
			file.getPath();
			
			final int addToTempFileCount = 
				Integer.parseInt(PosUtils.getPosProperty("addToTempFileCount"));
			//数据开始行
			int startRow = 1;
			//数据结束行，
			int endRow = addToTempFileCount;
			//创建excel临时文件，并插入指定数量的数据
			Map map = createTempExcel(file, listCode, queryIndex, startRow, endRow);
			//总的数据数量
			int totalCount = (Integer) map.get("totalCount");
			//如果临时表数据少于设定的数据量，则不需要追加了
			if(totalCount > addToTempFileCount) {
				//往临时文件追加数据
				//创建时的结束行的下一行为追加的第一行
				startRow = endRow;
				//查询的临时表字段
				String headerSql = (String) map.get("headerSql");
				for(int i = startRow; i < totalCount; i += addToTempFileCount) {
					file = addToTempExcel(file, i+1, addToTempFileCount+i, headerSql, queryIndex);
				}
			}
			fileId = uploadFileToIM(file);
			result.put("fileId", fileId);
			result.put("file", file);
//			String downloadURL = PlatformContext.getIMFileService().
//				getFileURL(fileId, new UrlSignature(), true, "xx.xls", null, null);
//			System.out.println();
		} catch (NumberFormatException e) {
			throw new RuntimeException("分批写入失败！！" + e);
		}
		return result;
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
	
	/**
	 * oracle分页数据查询
	 * @param startRow =< row =< endRow
	 * @return List
	 * @author WangMingShun
	 * @date 2016-3-14
	 */
	public List getOracleListResult(String queryIndex, String headerSql,
			int startRow, int endRow) {
		return asyncListdao.getOracleListResult(queryIndex, headerSql, startRow, endRow);
	}
	
	/**
	 * 创建一个临时的excel文件，并插入指定数量的数据
	 * @return List
	 * @author WangMingShun
	 * @date 2016-3-14
	 */
	public Map<String, Object> createTempExcel(SFFile file, String listCode, 
			String queryIndex, int startRow, int endRow) {
		Map<String, Object> map = new HashMap<String, Object>();
		StringBuffer headerSql = new StringBuffer();
		// 查询清单显示字段基表
		List<Map<String, String>> codeList = getListHeader(listCode);
		// 从临时表查询清单记录写入Excel
		final int len = getListCount(queryIndex);
		map.put("totalCount", len);
		WritableWorkbook book = null;
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
				map.put("headerSql", headerSql.toString());
				//创建工作薄
				sheet = book.createSheet("sheet0", 0);
				//设置第一行的高度
				sheet.setRowView(0, 20);
				//设置字体种类和黑体显示,字体为TIMES,字号大小为12,采用黑体显示
				WritableFont font = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD);
				//生成一个单元格样式控制对象
				WritableCellFormat format = new WritableCellFormat(font);
				// 组装Excel显示字段
				for (int j = 0; j < codeList.size(); j++) {
					//设置第j列的高度
					sheet.setColumnView(j, codeList.get(j).get("DISPLAYDESC").getBytes().length + 5);
					//创建要显示的内容,创建一个单元格，第一个参数为列坐标，第二个参数为行坐标，第三个参数为内容
					sheet.addCell(new Label(j, 0, codeList.get(j).get("DISPLAYDESC"), format));
				} 
				
				//查询临时表数据(后面两个参数是分页用的,现在暂时用不上)
				reportList = getOracleListResult(queryIndex, headerSql.toString(), startRow, endRow);
				for (int i = 0; i < reportList.size(); i++) {
					Map<String, Object> reviewMap = (Map<String, Object>) reportList.get(i);
					sheet.setRowView(i + 1, 18);
					for (int j = 0; j < reviewMap.size(); j++) {
						sheet.addCell(new Label(j, i + 1, reviewMap
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
		
		return map;
	}
	
	/**
	 * 向指定excel中追加数据
	 * @return void
	 * @author WangMingShun
	 * @date 2016-3-14
	 */
	public SFFile addToTempExcel(SFFile file, int startRow, int endRow, 
			String headerSql, String queryIndex) {
		List<Map<String, Object>> reportList = null;
//		int limited = 50000;
		try{
			//获取原来的Excel文件
			Workbook wb = Workbook.getWorkbook(new FileInputStream(file));
			//打开一个文件的副本，并且将制定数据写回到原文件中
			WritableWorkbook wwb = Workbook.createWorkbook(file, wb);
			//对最后创建的工作簿进行更新
			WritableSheet ws = wwb.getSheet(wwb.getNumberOfSheets()-1);
			WritableCell cell = null;
			//查询临时表数据
			reportList = getOracleListResult(queryIndex, headerSql, startRow, endRow);
			//定义追加的数据的起始行
			int addStartRow = ws.getRows();
			for (int i = 0; i < reportList.size(); i++) {
				Map<String, Object> reviewMap = (Map<String, Object>) reportList.get(i);
				ws.setRowView(i + addStartRow, 18);
				//每个sheet限制50000行，超过之后，另起一个sheet
//				if((i + addStartRow - 1) % limited == 0) {
//					//超过limited的限制，重新创建一个sheet
//					//添加一个工作表
//					ws = wwb.createSheet("sheet" + (wwb.getNumberOfSheets()), wwb.getNumberOfSheets());
//					//从新的sheet的第0行开始写入
//					addStartRow = 0;
//				}
				for (int j = 0; j < reviewMap.size(); j++) {
					cell = new Label(j, i + addStartRow, 
							reviewMap.get("CEL" + j) == null 
							? "" : String.valueOf(reviewMap.get("CEL" + j)));
					ws.addCell(cell);
				}
			}
			wwb.write();
			wwb.close();
			wb.close();
		}catch(Exception e){
			throw new RuntimeException("数据追加异常", e);
		}
		return file;
	}
}
