package com.sinolife.pos.print.notice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;

public interface NoticePrintHandler {

	/**
	 * 取得模板流
	 * @param posNote 当前通知书数据
	 * @return 模板流
	 * @throws IOException
	 */
	InputStream getTempleteInputStream(Object posNote) throws IOException;
	
	/**
	 * 取得ParameterMap
	 * @param posNote 当前通知书数据
	 * @return
	 */
	Map<String, Object> getParameterMap(Object posNote);
	
	/**
	 * 取得报表数据源
	 * @param posNote 当前通知书数据
	 * @return
	 */
	JRDataSource getJRDataSource(Object posNote);
	
	/**
	 * 取得文件名
	 * @param key 关键字
	 * @return
	 */
	String getFileName(String key);
	
	/**
	 * 处理通知书批次下载，返回临时文件集
	 * @param noticeIdList
	 */
	List<File> handleBatchNoticePrint(List<String> noticeIdList);
	
	/**
	 * 处理通知书批次下载，直接合并到输出流.
	 * @param noticeIdList
	 * @param outputStream
	 */
	void handleBatchNoticePrint(List<String> noticeIdList, OutputStream outputStream);
	
	/**
	 * 处理通知书逐单下载
	 * @param criteria
	 * @param respone
	 */
	File handleSingleNoticePrint(String id);
	
	/**
	 * 根据ID查询通知书对象
	 * @param id
	 * @return
	 */
	Object queryPosNoteById(String id);
	
	/**
	 * 处理http文件名头
	 * @param request
	 * @param response
	 * @param fileNameParam
	 * @param isBatch
	 */
	void processPdfFileNameHeader(HttpServletRequest request, HttpServletResponse response, String fileNameParam);
	
	/**
	 * 通过迭代器合并PDF文件
	 * @param fileIterator
	 * @param outputStream
	 */
	void mergePdfFiles(Iterator<List<File>> fileIterator, OutputStream outputStream);
	
	/**
	 * 合并PDF文件
	 * @param fileList
	 * @param outputStream
	 */
	void mergePdfFiles(List<File> fileList, OutputStream outputStream);
	
	/**
	 * 在当前线程中绑定变量
	 * @param paramName
	 * @param paramValue
	 */
	void setThreadParameter(String paramName, Object paramValue);
	
	/**
	 * 在当前线程中获取变量
	 * @param paramName
	 * @return
	 */
	Object getThreadParameter(String paramName);
	
	/**
	 * 清空线程绑定变量
	 */
	void clearThreadParameters();

    /**
     * 免填单申请书打印
     * @param posNo
     * @param printType
     * @param entOrApplication
     * @return File
     * @author GaoJiaMing 
     * @time 2014-6-16
     */
    File handleApplicationFillFormPrint(String posNo, String printType, String entOrApplication);

}
