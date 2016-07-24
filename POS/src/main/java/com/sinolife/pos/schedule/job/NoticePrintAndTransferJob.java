package com.sinolife.pos.schedule.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import net.unihub.framework.util.common.DateUtil;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.util.FTPUtil;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.print.dto.PosNoteDTO;
import com.sinolife.pos.print.notice.NoticePrintHandler;
import com.sinolife.pos.print.notice.NoticePrintHandlerFactory;
import com.sinolife.pos.print.service.PrintService;
import com.sinolife.pos.schedule.dao.ScheduleDAO;
import com.sinolife.sf.config.RuntimeConfig;
import com.sinolife.sf.platform.runtime.PlatformContext;

/**
 * 通知书PDF生成并传输到前置机批处理
 */
@Component("noticePrintAndTransferJob")
public class NoticePrintAndTransferJob {

	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private ScheduleDAO scheduleDAO;
	
	@Autowired
	private PrintService printService;
	
	@Autowired
	private CommonQueryDAO commonQueryDAO;
	
	@Autowired
	private TransactionTemplate txTmpl;
	
	public void execute() {
		//先获得运行许可，没有抢到运行资格的话就杯具了，只能返回 -_-!!!
		if(!this.getTaskPermission())
			return;
		
		Date taskStartDate = new Date();//任务开始时间
		logger.info("NoticePrintAndTransferJob started at:" + PosUtils.formatDate(commonQueryDAO.getSystemDate(), "yyyy-MM-dd HH:mm:ss"));
		
		//每个PDF文件的最大通知书个数
		final int pdfMaxFiles = getPdfMaxFiles();
		logger.info("one pdf file contains " + pdfMaxFiles + " file(s).");
		
		//判断是否为本地邮政编码的匹配模式
		final String localZip = getLocalZip();
		logger.info("local zip pattern like " + localZip);
		
		//服务器运行该任务的最长时间，避免数据太多，一直运行到工作时间，导致服务器响应缓慢
		float taskRunningHours = getTaskRunningHours();
		
		
		
		String curDate=DateUtil.getCurrentDateString();
		
		if ( "2016-07-16".equals(curDate)||"2016-07-17".equals(curDate))
		{
			
			taskRunningHours =18 ;
		}
		logger.info("max running hours is " + taskRunningHours);
		
		
		
		//每回合休息的时间
		long sleepTimeInLoop = getSleepTimeInLoop();
		logger.info("sleep " + sleepTimeInLoop + " ms when a pdf process complete.");
		
		//由于匿名内部类只能访问final变量，用其他类型变量的话，就不能改值了，这里拿HashSet当标志，HashSet为空表示需要运行，HashSet有值，就退出循环
		final Set<String> flagSet = new HashSet<String>();
		int todayFileNum = 1;		//当日处理的文件计数
		int exceptionLimit = 100;	//当日处理的异常次数限制
		while(flagSet.isEmpty()) {
			
			//休息sleepTimeInLoop秒，让VM有点时间去做GC吧
			try {
				logger.info("sleep " + sleepTimeInLoop + " ms.");
				Thread.sleep(sleepTimeInLoop);
			} catch (InterruptedException e) {
				logger.error(e);
				//睡觉被打断一般都是服务器要重启了，所以直接跳出循环
				break;
			}
			
			//运行时间检查，看到这里当前线程已经跑了多长时间了，如果大于配置的时长，就跳出循环
			float runningHours = (new Date().getTime() - taskStartDate.getTime()) * 1.0F / (1000 * 60 * 60);
			if(taskRunningHours > 0 && runningHours > taskRunningHours) {
				break;
			}
			
			//为防止出现FTP连接超时情况导致所有所有循环均上传失败，在每个循环内部重新建立FTP连接
			final FTPUtil ftp = FTPUtil.newInstance();
			try {
				//连接到服务器并切换到配置的默认目录
				ftp.connect();
				//初始化日期目录
				ftp.init(getDateString(), true);
				
				//设置事务超时时间为1800秒，应该够长
				txTmpl.setTimeout(1800);
				txTmpl.execute(new TransactionCallbackWithoutResult() {
					
					/* (non-Javadoc)
					 * @see org.springframework.transaction.support.TransactionCallbackWithoutResult#doInTransactionWithoutResult(org.springframework.transaction.TransactionStatus)
					 */
					@Override
					protected void doInTransactionWithoutResult(TransactionStatus trasactionStatus) {
						File pdfFile = null;		//本地临时文件
						File serverPdfFile = null;	//服务器下载的校验文件
						String tmpFileName = null;	//本地临时文件名
						String eLetterEmptyFlag = "N";
						try {
                            /* 总公司自动打印电子信函的处理 edit by gaojiaming start */
                            // 查询需要发送电子信函的数据
							try {
	                            Map<String, Object> paraMap = new HashMap<String, Object>();
	                            // 批次数量
	                            paraMap.put("batchSize", "300");
	                            List<PosNoteDTO> posNoteELetterList = scheduleDAO.queryPosNoteNeedELetter(paraMap);
	                            // 电子信函处理
	                            paraMap.put("taskFlag", "autoPrint");
	                            paraMap.put("posNoteList", posNoteELetterList);
	                            printService.manageELetter(paraMap);	
	                            
	                            if (posNoteELetterList == null || posNoteELetterList.isEmpty()){
	                            	eLetterEmptyFlag = "Y";
	                            }
							} catch (Exception e) {
					            e.printStackTrace();								
							}
                            /* 总公司自动打印电子信函的处理 edit by gaojiaming end */
                            
							//查询并锁定pdfMaxFiles条记录，这些记录已经按照note_type, is_local排序了，最乐观估计的话，这pdfMaxFiles条是同一种类型的通知书，并且是否异地的标志也一样
							List<PosNoteDTO> posNoteList = scheduleDAO.queryPosNoteNeedProcess(localZip, pdfMaxFiles);
							
							//但是也有杯具情况，比如，查询出来，前面有多种类型的通知书，或者异地标志不一致，那么就把后面的干掉，本次不处理，留给后面的循环处理
							this.removePosNoteShouldBeInDifferenteFile(posNoteList);
							
							//如果找不到记录，那么说明活干完了，往运行标志flagSet中加一个值，退出循环，结束今天的任务处理，睡觉去
							if((posNoteList == null || posNoteList.isEmpty()) && "Y".equals(eLetterEmptyFlag)) {
								flagSet.add("X");
								return;
							}
							
							//剩下的就都是一个PDF文件的内容了，开工了，生成PDF
							
							//1.先在临时文件夹中生成PDF文件
							logger.info("start generating local temp pdf file, this file contains " + posNoteList.size() + " record(s)...");
							pdfFile = generatePdfFile(ftp, posNoteList.get(0).getNoteType(), posNoteList.get(0).getIsLocal(), posNoteList);
							logger.info("local temp pdf file generated. file name is " + pdfFile.getName());
								
							//2.将临时文件上传至FTP服务器，文件名末尾加上".tmp"后缀，表明为临时文件
							tmpFileName = pdfFile.getName() + ".tmp";
							logger.info("start uploading local temp pdf file to ftp server as file name " + tmpFileName + " ...");
							ftp.uploadFile(pdfFile, tmpFileName);
							logger.info("local temp pdf file has uploaded to ftp server.");
								
							//3.将刚上传的文件下载回临时文件夹，当然，下载回来是为了验证传输有没有问题
							logger.info("start downloading remote file which is uploaded just now for verify...");
							serverPdfFile = ftp.downloadFile(tmpFileName, pdfFile.getParentFile());
							logger.info("remote file which is for verify downloaded.");
								
							//4.对2个文件进行对比，确认文件没有错误
							logger.info("start checking the local file and the remote file...");
							if(checkFile(pdfFile, serverPdfFile)) {
								logger.info("file check ok!");
							} else {
								ftp.delete(tmpFileName);
								throw new RuntimeException("file check failed!");
							}
							
								
							//5.更新通知书的打印日期，上传日期及文件名
							logger.info("start updating print date and file name...");
							updatePosNotePdfNameAndPDFUploadDate(posNoteList, pdfFile.getName());
							logger.info("update print date and file name complete.");
								
							//6.将上传至服务器的文件名末尾的.tmp后缀去掉，成为最终结果文件
							logger.info("start renaming remote file from " + tmpFileName + " to " + pdfFile.getName() + " ...");
							if(ftp.renameFile(tmpFileName, pdfFile.getName())) {
								logger.info("rename remote file success!");
							} else {
								ftp.delete(tmpFileName);
								throw new RuntimeException("rename remote file failed, file name is " + tmpFileName);
							}
						} catch(RuntimeException re) {
							trasactionStatus.setRollbackOnly();
							throw re;
						} catch(Exception e) {
							trasactionStatus.setRollbackOnly();
							throw new RuntimeException(e.getMessage(), e);
						} finally {
							PosUtils.deleteFile(pdfFile);
							PosUtils.deleteFile(serverPdfFile);
						}
					}
					
					/**
					 * 更新通知书打印日期、邮储打印日期，及邮储的文件名
					 * @param group
					 * @param fileName 文件名
					 */
					private void updatePosNotePdfNameAndPDFUploadDate(List<PosNoteDTO> group, String fileName) {
						for(PosNoteDTO posNote : group) {
							printService.updatePosNotePdfNameAndPDFUploadDate(posNote.getDetailSequenceNo(), posNote.getIsLocal(), fileName);
						}
					}
					
					/**
					 * 干掉不应该属于同一个文件的
					 */
					private void removePosNoteShouldBeInDifferenteFile(List<PosNoteDTO> posNoteList) {
						if(posNoteList != null && !posNoteList.isEmpty()) {
							String noteType = posNoteList.get(0).getNoteType();
							String isLocal = posNoteList.get(0).getIsLocal();
							Iterator<PosNoteDTO> it = posNoteList.iterator();
							while(it.hasNext()) {
								PosNoteDTO posNote = it.next();
								if(!noteType.equals(posNote.getNoteType()) || !isLocal.equals(posNote.getIsLocal())) {
									it.remove();
								}
							}
						}
					}
				});
				

				//好，到这里一轮完毕
				logger.info((todayFileNum++) + " pdf file process sucess!");
			} catch(Exception e) {
				logger.error(e.getMessage(), e);
				//暂时无法确定什么异常在这里会被捕捉到，所以设置一个重试次数，连续异常100次，就退出循环
				if(exceptionLimit-- <= 0) {
					break;
				}
			} finally {
				if(ftp != null) {
					ftp.disconnect();
					FTPUtil.destroy();
				}
			}
		}
		logger.info("NoticePrintAndTransferJob complete at:" + PosUtils.formatDate(commonQueryDAO.getSystemDate(), "yyyy-MM-dd HH:mm:ss"));
	}
	
	/**
	 * 获取当前服务器的定时任务运行许可，通过创建日期目录来实现，在邮储的FTP
	 * 服务器上创建日期目录成功的线程才允许执行任务，该机制是为了控制集群中的并发，
	 * 保证只有一个服务器能够执行该任务
	 */
	private boolean getTaskPermission() {
		FTPUtil ftp = FTPUtil.newInstance();
		try {
			//连接到服务器并切换到配置的默认目录
			ftp.connect();
			
			//初始化日期目录，如果目录已经存在或者创建失败，则认为已经有并发线程抢占了任务处理，直接抛异常退出
			ftp.init(getDateString(), false);
			return true;
		} catch(Exception e) {
			logger.info(e.getMessage(), e);
			return false;
		} finally {
			if(ftp != null) {
				ftp.disconnect();
				FTPUtil.destroy();
			}
		}
	}
	
	/**
	 * 检查文件内容是否一致
	 * @param pdfFile
	 * @param serverPdfFile
	 * @return
	 */
	private boolean checkFile(File pdfFile, File serverPdfFile) {
		InputStream is1 = null;
		InputStream is2 = null;
		try {
			is1 = new FileInputStream(pdfFile);
			is2 = new FileInputStream(serverPdfFile);
			return IOUtils.contentEquals(is1, is2);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			PosUtils.safeCloseInputStream(is1);
			PosUtils.safeCloseInputStream(is2);
		}
	}

	/**
	 * 生成通知书PDF文件
	 * @param ftp
	 * @param noteType
	 * @param posNoteList
	 * @return
	 */
	private File generatePdfFile(FTPUtil ftp, String noteType, String isLocal, List<PosNoteDTO> posNoteList) {
		NoticePrintHandler handler = NoticePrintHandlerFactory.getNoticePrintHandler(noteType);
		List<String> noteIdList = new ArrayList<String>();
		OutputStream os = null;
		List<File> tmpFileList = null;
		File tmpFile = null;
		try {
			for(PosNoteDTO posNote : posNoteList) {
				noteIdList.add(posNote.getDetailSequenceNo());
			}
			//生成单个通知书的文件PDF文件
			tmpFileList = handler.handleBatchNoticePrint(noteIdList);
			
			//在临时文件夹中存放生成的PDF
			String tmpFilePath = PosUtils.getPosProperty("tmpFilePath");
			if(!tmpFilePath.endsWith("/") && !tmpFilePath.endsWith("\\")) {
				tmpFilePath += "/" + getDateString();
			} else {
				tmpFilePath += getDateString();
			}
			File directory = new File(tmpFilePath);
			if(!directory.exists()) {
				directory.mkdir();
			}
			
			//生成文件名，这个文件名和上传到服务器的文件名一致
			String fileName = generateTempFileName(ftp, noteType, isLocal, getDateString());
			tmpFile =  new File(tmpFilePath + "/" + fileName);
			if(tmpFile.exists()) {
				throw new RuntimeException("文件 " + tmpFile.getName() + " 已经存在!");
			}
			
			//将单个通知书文件合并成一个文件
			os = new FileOutputStream(tmpFile);
			handler.mergePdfFiles(tmpFileList, os);
			os.flush();
			os.close();
			return tmpFile;
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception e) {
			e.printStackTrace();
			PosUtils.deleteFile(tmpFile);
			throw new RuntimeException(e);
		} finally {
			PosUtils.safeCloseOuputStream(os);
			PosUtils.deleteFiles(tmpFileList);
		}
		
	}
	
	/**
	 * 每个PDF文件的通知书个数限制
	 */
	private int getPdfMaxFiles() {
		Integer pdfMaxFiles = 1000;
		try {
			pdfMaxFiles = new Integer(RuntimeConfig.get("com.sinolife.pos.print.notice.pdf.maxFiles", String.class));
		} catch(Exception e) {
			logger.error(e.getMessage(), e );
		}
		return pdfMaxFiles;
	}
	
	/**
	 * 本地邮编模式
	 */
	private String getLocalZip() {
		String localZip = RuntimeConfig.get("com.sinolife.pos.print.notice.pdf.localZip", String.class);
		if(StringUtils.isNotBlank(localZip)) {
			return localZip.trim();
		} else {
			return "518%";
		}
	}
	
	/**
	 * 允许运行的最大时间
	 */
	private float getTaskRunningHours() {
		Float taskRunningHours = -1F;
		try{
			taskRunningHours = new Float(RuntimeConfig.get("com.sinolife.pos.print.notice.taskRunningHours", String.class));
		} catch(Exception e) {
			logger.error(e.getMessage(), e );
		}
		return taskRunningHours;
	}
	
	/**
	 * 每回合休息时间
	 */
	private long getSleepTimeInLoop() {
		Long sleepTimeInLoop = -1L;
		try{
			sleepTimeInLoop = new Long(RuntimeConfig.get("com.sinolife.pos.print.notice.sleepTimeInLoop", String.class));
		} catch(Exception e) {
			logger.error(e.getMessage(), e );
		}
		return sleepTimeInLoop;
	}
	
	/**
	 * 生成文件名
	 */
	private String generateTempFileName(FTPUtil ftp, String noteType, String isLocal, String businessDateString) {
		StringBuilder fileName = new StringBuilder(); 
		fileName.append(businessDateString);
		if("1".equals(noteType)) {
			//现金分红红利通知信
			fileName.append("01");		//noticeType
			fileName.append(isLocal);	//local
			fileName.append("Y");		//doublePrint
			fileName.append("N");		//longPaper
		} else if("2".equals(noteType)) {
			//保额分红红利通知信
			fileName.append("05");
			fileName.append(isLocal);	//local
			fileName.append("Y");		//doublePrint
			fileName.append("N");		//longPaper
		} else if("10".equals(noteType)) {
			//个人保单年度报告书
			fileName.append("02");
			fileName.append(isLocal);	//local
			fileName.append("N");		//doublePrint
			fileName.append("N");		//longPaper
		} else if("11".equals(noteType)) {
			//投资连结保险周年报告
			fileName.append("03");
			fileName.append(isLocal);	//local
			fileName.append("N");		//doublePrint
			fileName.append("Y");		//longPaper
		}
		//四个下划线是占位符，貌似以后会有用处
		fileName.append("___");
		//当日文件索引
		fileName.append(getFileIndex());
		//扩展名
		fileName.append(".pdf");
		return fileName.toString();
	}
	
	/**
	 * 生成当天的文件索引
	 */
	private String getFileIndex() {
		int index = -1;
		int retry = 100;
		while(retry > 0) {
			try {
				index = scheduleDAO.getPrintSeq(commonQueryDAO.getSystemDate());
				break;
			} catch(Exception e) {
				logger.error(e);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				retry--;
			}
		}
		if(retry == 0) {
			throw new RuntimeException("无法取得PDF文件序号");
		}
		if(index <= 0 || index > 10000) {
			throw new RuntimeException("文件数目超限：" + index);
		}
		return StringUtils.leftPad(String.valueOf(index), 4, "0");
	}
	
	/**
	 * 生成目录名yyyyMMdd
	 */
	private String getDateString() {
		return PosUtils.formatDate(commonQueryDAO.getSystemDate(), "yyyyMMdd");
	}
	
	@PostConstruct
	public void onInitialize() {
		PlatformContext.getRuntimeConfig();
	}
	
}
