package com.sinolife.pos.print.notice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.sinolife.intf.print.TemplateStore;
import com.sinolife.pos.common.dao.ClientInfoDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.print.dao.PrintDAO;
import com.sinolife.pos.print.service.PrintService;
import com.sinolife.sf.store.TempFileFactory;

public abstract class NoticePrintHandlerBase implements NoticePrintHandler {

	protected Logger logger = Logger.getLogger(getClass());
	
	
	@Autowired
	protected PrintService printService;
	
	@Autowired
	protected PrintDAO printDAO;
	
	@Autowired
	protected CommonQueryDAO commonQueryDAO;
	
	@Autowired
	protected ClientInfoDAO clientInfoDAO;
	
	@Autowired
	protected TemplateStore templateStore;
	
	private TempFileFactory tempFileFactory = TempFileFactory.getInstance(getClass());
	
	private ThreadLocal<Map<String, Object>> threadParameterMapHolder = new ThreadLocal<Map<String, Object>>();
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.print.notice.NoticePrintHandler#setThreadParameter(java.lang.String, java.lang.Object)
	 */
	public void setThreadParameter(String paramName, Object paramValue) {
		Map<String, Object> threadParameterMap = threadParameterMapHolder.get();
		if(threadParameterMap == null) {
			threadParameterMap = new HashMap<String, Object>();
			threadParameterMapHolder.set(threadParameterMap);
		}
		threadParameterMap.put(paramName, paramValue);
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.print.notice.NoticePrintHandler#getThreadParameter(java.lang.String)
	 */
	public Object getThreadParameter(String paramName) {
		Map<String, Object> threadParameterMap = threadParameterMapHolder.get();
		if(threadParameterMap != null) {
			return threadParameterMap.get(paramName);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.print.notice.NoticePrintHandler#clearThreadParameters()
	 */
	public void clearThreadParameters() {
		threadParameterMapHolder.remove();
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.print.notice.NoticePrintHandler#processPdfFileNameHeader(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String)
	 */
	@Override
	public void processPdfFileNameHeader(HttpServletRequest request, HttpServletResponse response, String fileNameParam) {
		String fileName = null;
		if(StringUtils.isNotBlank(fileNameParam) && fileNameParam.startsWith(":")) {
			fileName = fileNameParam.substring(1);
		} else {
			fileName = this.getFileName(fileNameParam);
		}
		String userAgent = request.getHeader("User-Agent").toLowerCase();
		try {
			if(userAgent.indexOf("firefox") > 0) {
				//firefox浏览器
				fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
			} else if(userAgent.indexOf("msie") > 0) {
				//IE浏览器
				fileName = URLEncoder.encode(fileName, "UTF-8");
			} else {
				fileName = URLEncoder.encode(fileName, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
	}
	
	/**
	 * 生成单个通知书文件，并将其存放到临时文件夹
	 * @param detailSequenceNo
	 * @return
	 */
	@Override
	public File handleSingleNoticePrint(String id){
		Object posNote = this.queryPosNoteById(id);
		if(posNote == null)
			throw new RuntimeException("找不到待打印的数据:" + id);
		
		InputStream tmplInputStream = null;
		File tempFile = null;
		try {
			//获取打印模板流
			tmplInputStream = this.getTempleteInputStream(posNote);
			
			//向模板填充数据
			Map<String, Object> parameterMap = this.getParameterMap(posNote);
			JRDataSource jrDataSource = this.getJRDataSource(posNote);
			//判断是否为个险，是否增加二维码 start
			String policyNo = (String) parameterMap.get("POLICY_NO");
			String flag = printDAO.findChannelType(policyNo);
			if ("01".equals(flag)){
				parameterMap.put("image_path", templateStore.getImagePath() + "/image/");
				parameterMap.put("isShowImage", "true");
			}else {
				parameterMap.put("image_path", templateStore.getImagePath() + "/image//");
				parameterMap.put("isShowImage", "false");
			}
			//判断是否为个险，是否增加二维码 end
			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			logger.info("filling report ... " + id);
			logger.debug("parameterMap:" + parameterMap);
			JasperPrint jasperPrint = JasperFillManager.fillReport(tmplInputStream, parameterMap, jrDataSource);
			logger.info("end filling report ..." + id);
			jasperPrintList.add(jasperPrint);
			
			//输出PDF
			JRPdfExporter exporter = new JRPdfExporter();
			tempFile = tempFileFactory.createTempFile();
			exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
			exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE, tempFile);
			logger.info("export report ... " + id);
			exporter.exportReport();
			logger.info("export report finished." + id);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			PosUtils.safeCloseInputStream(tmplInputStream);
		}
		return tempFile;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.print.notice.NoticePrintHandler#handleBatchNoticePrint(java.util.List)
	 */
	@Override
	public List<File> handleBatchNoticePrint(List<String> noticeIdList) {
		List<File> tmpFileList = new ArrayList<File>();
		if(noticeIdList != null && !noticeIdList.isEmpty()) {
			//每PAGE_SIZE个通知书处理成一个文件
			int PAGE_SIZE = 200;
			for(int i = 0; i < ((int)noticeIdList.size() / PAGE_SIZE) + (noticeIdList.size() % PAGE_SIZE == 0 ? 0 : 1); i++) {
				File tempFile = null;
				try {
					List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
					InputStream tmplInputStream = null;
					for(int j = i * PAGE_SIZE; j < (i + 1) * PAGE_SIZE && j < noticeIdList.size(); j++) {
						try {
							String id = noticeIdList.get(j);
							Object posNote = this.queryPosNoteById(id);
							
							//获取打印模板流
							tmplInputStream = this.getTempleteInputStream(posNote);
							
							//向模板填充数据
							Map<String, Object> parameterMap = this.getParameterMap(posNote);
							JRDataSource jrDataSource = this.getJRDataSource(posNote);
							//判断是否为个险，是否增加二维码 start
							String policyNo = (String) parameterMap.get("POLICY_NO");
							String flag = printDAO.findChannelType(policyNo);
							if ("01".equals(flag)){
								parameterMap.put("image_path", templateStore.getImagePath() + "/image/");
								parameterMap.put("isShowImage", "true");
							}else {
								parameterMap.put("image_path", templateStore.getImagePath() + "/image//");
								parameterMap.put("isShowImage", "false");
							}
							//判断是否为个险，是否增加二维码 end
							
							logger.info("filling report ... " + id);
							logger.debug("parameterMap:" + parameterMap);

							JasperPrint jasperPrint = JasperFillManager.fillReport(tmplInputStream, parameterMap, jrDataSource);
							logger.info("end filling report ..." + id);
							jasperPrintList.add(jasperPrint);
						} catch (Exception e) {
							throw new RuntimeException(e);
						} finally {
							PosUtils.safeCloseInputStream(tmplInputStream);
						}
					}
					//输出PDF
					JRPdfExporter exporter = new JRPdfExporter();
					tempFile = tempFileFactory.createTempFile();
					exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
					exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
					exporter.setParameter(JRExporterParameter.OUTPUT_FILE, tempFile);
					exporter.exportReport();
					logger.info("export report finished." );
				} catch (Exception e) {
					PosUtils.deleteFile(tempFile);
					throw new RuntimeException(e);
				}
				tmpFileList.add(tempFile);
			}
		}
		return tmpFileList;
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.print.notice.NoticePrintHandler#handleBatchNoticePrint(java.util.List, java.io.OutputStream)
	 */
	public void handleBatchNoticePrint(List<String> noticeIdList, OutputStream outputStream) {
		if(noticeIdList == null || noticeIdList.isEmpty()) {
			return;
		}
		if(outputStream == null) {
			throw new RuntimeException("outputStream is null");
		}
		List<File> files = this.handleBatchNoticePrint(noticeIdList);
		this.mergePdfFiles(files, outputStream);
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.print.notice.NoticePrintHandler#mergePdfFiles(java.util.List, java.io.OutputStream)
	 */
	@Override
	public void mergePdfFiles(List<File> fileList, OutputStream outputStream) {
		if(fileList == null || fileList.isEmpty()) {
			return;
		}
		
		if(outputStream == null) {
			throw new RuntimeException("outputStream is null");
		}
		
		PdfCopyFields copy = null;
		boolean closed = false;
		File mergedFile = null;
		List<File> mergedFileList = new ArrayList<File>();
		try {
			copy = new PdfCopyFields(outputStream);
			copy.open();
			List<File> tempFileList = new ArrayList<File>();
			for(int i = 0; i < fileList.size(); i++) {
				tempFileList.add(fileList.get(i));
				//linux系统中对同一进程打开文件数有限制,每300个文件合并成一个，并删除，以减少打开文件数
				if(tempFileList.size() >= 300 || i == fileList.size() - 1) {
					logger.info("merge file internal ... " + tempFileList.size());
					mergedFile = this.mergeFileInternal(tempFileList);
					logger.info("merge file internal complete. fileName:" + mergedFile.getAbsolutePath());
					mergedFileList.add(mergedFile);
					tempFileList.clear();
					PdfReader pdfReader = new PdfReader(new RandomAccessFileOrArray(mergedFile.getAbsolutePath()), null);
					copy.addDocument(pdfReader);
					copy.getWriter().freeReader(pdfReader);
		            pdfReader.close();
				}
			}
			copy.close();
			closed = true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if(copy != null && !closed) {
				try {
					copy.close();
				} catch(Exception e) {
					logger.error(e);
				}
			}
			PosUtils.deleteFile(mergedFile);
			PosUtils.deleteFiles(mergedFileList);
			PosUtils.deleteFiles(fileList);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.print.notice.NoticePrintHandler#mergePdfFiles(java.util.Iterator, java.io.OutputStream)
	 */
	@Override
	public void mergePdfFiles(Iterator<List<File>> fileIterator, OutputStream outputStream) {
		if(fileIterator == null || !fileIterator.hasNext()) {
			return;
		}
		
		if(outputStream == null) {
			throw new RuntimeException("outputStream is null");
		}
		
		PdfCopyFields copy = null;
		boolean closed = false;
		File mergedFile = null;
		List<File> mergedFileList = new ArrayList<File>();
		List<File> tempFileList = new ArrayList<File>();
		try {
			copy = new PdfCopyFields(outputStream);
			copy.open();
			while(fileIterator.hasNext()) {
				List<File> tmpFiles = fileIterator.next();
				tempFileList.addAll(tmpFiles);
				//linux系统中对同一进程打开文件数有限制,每300个文件合并成一个，并删除，以减少打开文件数
				if(tempFileList.size() >= 300 || !fileIterator.hasNext()) {
					logger.info("merge file internal ... " + tempFileList.size());
					mergedFile = this.mergeFileInternal(tempFileList);
					logger.info("merge file internal complete. fileName:" + mergedFile.getAbsolutePath());
					mergedFileList.add(mergedFile);
					tempFileList.clear();
					PdfReader pdfReader = new PdfReader(new RandomAccessFileOrArray(mergedFile.getAbsolutePath()), null);
					copy.addDocument(pdfReader);
					copy.getWriter().freeReader(pdfReader);
		            pdfReader.close();
				}
			}
			copy.close();
			closed = true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if(copy != null && !closed) {
				try {
					copy.close();
				} catch(Exception e) {
					logger.error(e);
				}
			}
			PosUtils.deleteFiles(tempFileList);
			PosUtils.deleteFile(mergedFile);
			PosUtils.deleteFiles(mergedFileList);
		}
	}
	
	/**
	 * 将多个PDF合并成一个
	 * @param fileList
	 * @return
	 */
	protected File mergeFileInternal(List<File> fileList) {
		PdfCopyFields copy = null;
		OutputStream outputStream = null;
		File descFile = tempFileFactory.createTempFile();
		boolean closed = false;
		try {
			outputStream = new FileOutputStream(descFile);
			copy = new PdfCopyFields(outputStream);
			copy.open();
			for(File tmpFile : fileList) {
				PdfReader pdfReader = new PdfReader(new RandomAccessFileOrArray(tmpFile.getAbsolutePath()), null);
				copy.addDocument(pdfReader);
				copy.getWriter().freeReader(pdfReader);
	            pdfReader.close();
			}
			copy.close();
			PosUtils.safeCloseOuputStream(outputStream);
			closed = true;
			return descFile;
		} catch (Exception e) {
			PosUtils.safeCloseOuputStream(outputStream);
			PosUtils.deleteFiles(fileList);
			PosUtils.deleteFile(descFile);
			if(copy != null && !closed) {
				copy.close();
			}
			throw new RuntimeException(e);
		} finally {
			PosUtils.safeCloseOuputStream(outputStream);
			if(copy != null && !closed) {
				try {
					copy.close();
				} catch(Exception e) {
					logger.error(e);
				}
			}
			PosUtils.deleteFiles(fileList);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.sinolife.pos.print.notice.NoticePrintHandler#getJRDataSource(java.lang.Object)
	 */
	@Override
	public JRDataSource getJRDataSource(Object posNote) {
		return emptyJRDataSource();
	}
	
	/**
	 * 工具方法，取得时间戳字符串
	 * @return
	 */
	protected String getTimeString() {
		return PosUtils.formatDate(commonQueryDAO.getSystemDate(), "yyyyMMddHHmmss");
	}
	
	/**
	 * 工具方法，构造空的JRDataSource;
	 * @return
	 */
	protected JRDataSource emptyJRDataSource() {
		List<Object> list = new ArrayList<Object>();
		list.add(new Object());
		return new JRBeanCollectionDataSource(list);
	}
	
	/**
	 * 工具方法，构造空的ParameterMap;
	 * @return
	 */
	protected Map<String, Object> emptyParameterMap() {
		return new HashMap<String, Object>();
	}

    /**
     * 免填单申请书打印
     * 
     * @param posNo
     * @param printType
     * @param entOrApplication
     * @return File
     * @author GaoJiaMing
     * @time 2014-6-16
     */
	@Override
	public File handleApplicationFillFormPrint(String id, String printType, String entOrApplication) {
		// 查询关联保全号
		List<PosInfoDTO> PosInfoDTOList = this.queryPosNosByPosNo(id);
		File tempFile = null;
		List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		// 联数
		int joint = 2;
		try {
			for (PosInfoDTO posInfoDTO : PosInfoDTOList) {
				InputStream tmplInputStream = null;
				try {
					Object posNote = this.queryPosNoteById(posInfoDTO.getPosNo());
					if (posNote == null)
						throw new RuntimeException("找不到待打印的数据:" + id);
					String serviceItems = (String) ((Map) posNote).get("SERVICE_ITEMS");
					String isApplicationFree = printDAO.checkIsApplicationFree(id);
					if ("0".equals(printType) && "application".equals(entOrApplication)) {
						joint = 3;
					} else if ("0".equals(printType)
							&& "ent".equals(entOrApplication)
							/*&& "Y".equals(isApplicationFree)
							&& ("19".equals(serviceItems) || "23".equals(serviceItems) || "21".equals(serviceItems)
									|| "18".equals(serviceItems) || "28".equals(serviceItems)
									|| "7".equals(serviceItems) || "26".equals(serviceItems)
									|| "25".equals(serviceItems) || "24".equals(serviceItems)
									|| "30".equals(serviceItems) || "31".equals(serviceItems)
									|| "32".equals(serviceItems) || "29".equals(serviceItems) || "43"
									.equals(serviceItems))*/) {
						joint = 3;
					}
					// 两联打印
					for (int i = 1; i < joint; i++) {
						// 向模板填充数据
						Map<String, Object> parameterMap = this.getParameterMap(posNote);
						JRDataSource jrDataSource = this.getJRDataSource(posNote);
						parameterMap.put("image_path", templateStore.getImagePath() + "/image/");
						parameterMap.put("PRINTTYPE", printType);
						if (joint == 3) {
							if (i == 2) {
								parameterMap.put("joint", "第一联：公司联");
							}
							if (i == 1) {
								parameterMap.put("joint", "第二联：客户联");
							}
						}
						logger.info("filling report ... " + id);
						logger.debug("parameterMap:" + parameterMap);
						// 获取打印模板流
						parameterMap.put("ENTTYPE", "logo");
						tmplInputStream = this.getTempleteInputStream(posNote);
						JasperPrint jasperPrint = JasperFillManager.fillReport(tmplInputStream, parameterMap,
								jrDataSource);
						logger.info("end filling report ..." + id);
						jasperPrintList.add(jasperPrint);
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				} finally {
					PosUtils.safeCloseInputStream(tmplInputStream);
				}
			}
			// 输出PDF
			JRPdfExporter exporter = new JRPdfExporter();
			tempFile = tempFileFactory.createTempFile();
			exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
			exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE, tempFile);
			logger.info("export report ... " + id);
			exporter.exportReport();
			logger.info("export report finished." + id);
		} catch (Exception e) {
			PosUtils.deleteFile(tempFile);
			throw new RuntimeException(e);
		}
		return tempFile;
	}

    /**
     * 根据保全号查关联保全号
     * 
     * @param id
     * @return List<PosInfoDTO>
     * @author GaoJiaMing
     * @time 2014-6-16
     */
    public List<PosInfoDTO> queryPosNosByPosNo(String id) {
        return printService.queryPosNosByPosNo(id);
    }
	
}
