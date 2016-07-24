package com.sinolife.pos.schedule.thread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.sinolife.pos.common.consts.CodeTableNames;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.print.dao.PrintDAO;
import com.sinolife.pos.print.dto.EndorsementPrintCriteriaDTO;
import com.sinolife.pos.print.dto.NoticePrintCriteriaDTO;
import com.sinolife.pos.print.dto.PosNoteDTO;
import com.sinolife.pos.print.notice.NoticePrintHandler;
import com.sinolife.pos.print.notice.NoticePrintHandlerFactory;
import com.sinolife.pos.print.service.PrintService;
import com.sinolife.pos.schedule.HandlePdfMail;
import com.sinolife.pos.schedule.dao.ScheduleDAO;
import com.sinolife.pos.schedule.dto.NoticePrintBatchTask;
import com.sinolife.sf.attatch.UrlSignature;
import com.sinolife.sf.framework.email.Mail;
import com.sinolife.sf.framework.email.MailService;
import com.sinolife.sf.framework.email.MailType;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.store.MimeType;
import com.sinolife.sf.store.SFFile;
import com.sinolife.sf.store.SFFilePath;
import com.sinolife.sf.store.StogeType;
import com.sinolife.sf.store.TempFileFactory;

@Component
public class NoticePrintThread implements Runnable {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private ScheduleDAO scheduleDAO;

	@Autowired
	private TransactionTemplate txTmpl;

	@Autowired @Qualifier("mailService01")
	private MailService mailService;

	@Autowired
	private PrintService printService;

	@Autowired
	private PrintDAO printDAO;

	@Autowired
	private CommonQueryDAO commonQueryDAO;

	public boolean running = false;

	private ThreadLocal<List<String>> noticeIdListNeedToUpdate;

	private TempFileFactory TEM_FILE_FACTORY = TempFileFactory
			.getInstance(NoticePrintThread.class);

	private long delayTime = -1; // 延迟时间
	private long intervalTime = 100; // 任务执行间隔时间
	private String localHostIp; // 本机IP
	
	@Autowired
	private HandlePdfMail handlePdfMail;

	public void setDelayTime(long delayTime) {
		this.delayTime = delayTime;
	}

	public void setIntervalTime(long intervalTime) {
		this.intervalTime = intervalTime;
	}

	@PostConstruct
	public void init() {
		// 不设置事务超时时间
		txTmpl.setTimeout(-1);
		if (noticeIdListNeedToUpdate != null) {
			noticeIdListNeedToUpdate.remove();
		} else {
			noticeIdListNeedToUpdate = new ThreadLocal<List<String>>();
		}
		localHostIp = getLocalHostIp();
		running = true;
	}

	@PreDestroy
	public void destroy() {
		if (noticeIdListNeedToUpdate != null) {
			noticeIdListNeedToUpdate.remove();
			noticeIdListNeedToUpdate = null;
		}
		running = false;
	}

	@Override
	public void run() {
		logger.info("working thread started.");
		try {
			if (delayTime > 0) {
				logger.info("delay " + delayTime + " ...zzZZ");
				Thread.sleep(delayTime);
			}
			while (true && running) {
				try {
					noticeIdListNeedToUpdate.remove();
					if (intervalTime > 0) {
						Thread.sleep(intervalTime);
					}
					doProcess();
				} catch (InterruptedException e) {
					throw e;
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e);
				}
			}
		} catch (InterruptedException e) {
			logger.info("NoticePrintThread interrupted.");
		}
		logger.info("working thread ended.");
	}

	private void doProcess() {
		final String taskId = retriveTask();
		if (StringUtils.isNotBlank(taskId)) {
			logger.info("start process notice print task : " + taskId);
			Timer touchTimer = new Timer(Thread.currentThread().getName());
			SFFile tempFile = null;
			NoticePrintBatchTask task = null;
			try {
				// 启动定时刷新，每5分钟更新一次任务最新活动时间，保持对任务独占的有效性
				touchTimer.scheduleAtFixedRate(new TimerTask() {
					@Override
					public void run() {
						logger.info("touch task by id: " + taskId);
						scheduleDAO.touchNoticePrintBatchTask(taskId);
					}
				}, 0, 5 * 60 * 1000);

				// 查询任务
				task = scheduleDAO.queryNoticePrintBatchTaskById(taskId);

				// 生成临时文件
				tempFile = TEM_FILE_FACTORY.createTempFile();

				// 生成通知书PDF，并将结果保存至指定的临时文件
				if (!this.generatePdfFile(task, tempFile)) {
					// 生成PDF失败
					this.markTaskFailed(taskId, "1");
					this.sendFailEmail(task, "生成PDF失败");

				} else if (!this.storeToIM(task, tempFile)) {
					// 保存PDF文件至IM失败
					this.markTaskFailed(taskId, "2");
					this.sendFailEmail(task, "保存PDF文件至IM失败");

				} else if (!this.sendEmail(task)) {
					// 邮件发送失败
					this.markTaskFailed(taskId, "3");

				} else {
					// 处理成功
					updateEntPrintInfo(task);
					touchTimer.cancel();
					this.completeTask(task);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
				try {
					touchTimer.cancel();
					this.markTaskFailed(taskId, "4");
					if (task != null) {
						this.sendFailEmail(task, "系统异常");
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					logger.error(e1);
				}
				return;
			} finally {
				try {
					touchTimer.cancel();
					touchTimer.purge();
				} catch (Exception e) {
					logger.error(e);
				}
				PosUtils.deleteFile(tempFile);
			}
		}
	}

	/**
	 * 在批单生成并发送成功后更新批单打印人员信息
	 * 
	 * @param task
	 */
	private void updateEntPrintInfo(NoticePrintBatchTask task) {
		String noticeType = task.getQueryNoticeType();
		if (StringUtils.isNotBlank(noticeType)) {
			noticeType = noticeType.trim().toLowerCase();

			if ("ent".equals(noticeType) || "ent1".equals(noticeType)) {
				// 处理批单
				EndorsementPrintCriteriaDTO criteria = new EndorsementPrintCriteriaDTO();
				criteria.setAcceptBranchCode(task.getQueryBranchCode());
				criteria.setAcceptChannelCode(task.getQueryChannelCode());
				criteria.setAcceptor(task.getQueryUserId());
				criteria.setEffectDateStart(task.getQueryDateStart());
				criteria.setEffectDateEnd(task.getQueryDateEnd());
				criteria.setServiceItems(task.getQueryServiceItems());
				criteria.setPolicyChannelCode(task.getQueryPolicyChannelCode());
				criteria.setApprovalServiceType(task
						.getQueryApprovalServiceType());
				criteria.setSubmitUserId(task.getSubmitUserId());
				List<Map<String, Object>> posInfoList = printService
						.queryEndorsementInfoByCriteria(criteria);
				if (posInfoList != null && !posInfoList.isEmpty()) {
					String submitUserId = criteria.getSubmitUserId();
					Iterator<Map<String, Object>> posInfoIt = posInfoList
							.iterator();
					while (posInfoIt.hasNext()) {
						Map<String, Object> posInfo = posInfoIt.next();
						String posNo = (String) posInfo.get("POS_NO");
						printDAO.updatePosInfoEntData(posNo, submitUserId);

					}

				}
			}
		}

	}

	/**
	 * 给用户发送邮件
	 * 
	 * @param task
	 * @return
	 */
	private boolean sendEmail(NoticePrintBatchTask task) {
		if (!"Y".equalsIgnoreCase(task.getNeedSendEmail())) {
			logger.info("task " + task.getTaskId()
					+ " does not need to send email.");
			return true;
		}
		try {
			logger.info("task " + task.getTaskId() + " send email to "
					+ task.getEmail());
			String downloadURL = PlatformContext.getIMFileService().getFileURL(
					task.getIMFileId(), new UrlSignature(), true,
					task.getNoticeFileName(), null, null);
			Mail mail = new Mail();
			mail.setSubject("[SL_POS]通知书批次打印任务完成通知"); // 邮件标题
			mail.setTo(new String[] { task.getEmail() }); // 收件人
			mail.setForm("sl_pos_mail_admin@sino-life.com"); // 发件人
			mail.setMailType(MailType.HTML_CONTENT); // 邮件类型
			StringBuilder contentSB = new StringBuilder(
					"<html><body style=\"font-size:14px;text-align:left;\">")
					.append("尊敬的保全系统用户 ")
					.append(task.getSubmitUserName())
					.append(":<br/>")
					.append("&nbsp;&nbsp;&nbsp;&nbsp;你好!<br/>")
					.append("&nbsp;&nbsp;&nbsp;&nbsp;很高兴通知您，您于&nbsp;")
					.append(PosUtils.formatDate(task.getSubmitDate(),
							"yyyy年MM月dd日 HH时mm分"))
					.append("&nbsp;提交的查询请求已经处理完成。<br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;查询通知书类型：&nbsp;");
			String noticeType = task.getQueryNoticeType();
			if ("ent".equals(noticeType)) {
				contentSB.append("保全变更批单");
			} else if ("51".equals(noticeType)) {
				contentSB.append("现金分红红利通知信(银代渠道)");
			} else if ("52".equals(noticeType)) {
				contentSB.append("现金分红红利通知信(非银代渠道)");
			} else if ("53".equals(noticeType)) {
				contentSB.append("保额分红红利通知信(银代渠道)");
			} else if ("54".equals(noticeType)) {
				contentSB.append("保额分红红利通知信(非银代渠道)");
			} else {
				contentSB
						.append(PosUtils.getDescByCodeFromCodeTable(
								CodeTableNames.POS_NOTE_TYPE,
								task.getQueryNoticeType()));
			}
			contentSB
					.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;查询机构：&nbsp;")
					.append(task.getQueryBranchCode())
					.append("（")
					.append(task.getQueryBranchName())
					.append("）")
					.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;#开始时间：&nbsp;"
							.replaceAll("\\#", "ent".equals(task
									.getQueryNoticeType()) ? "生效" : "查询"))
					.append(PosUtils.formatDate(task.getQueryDateStart(),
							"yyyy-MM-dd"))
					.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;#结束时间：&nbsp;"
							.replaceAll("\\#", "ent".equals(task
									.getQueryNoticeType()) ? "生效" : "查询"))
					.append(PosUtils.formatDate(task.getQueryDateEnd(),
							"yyyy-MM-dd"));
			if (StringUtils.isNotBlank(task.getQueryChannelCode())) {
				contentSB.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;受理渠道：&nbsp;")
						.append(PosUtils.getDescByCodeFromCodeTable(
								CodeTableNames.POS_ACCEPT_CHANNEL_CODE,
								task.getQueryChannelCode()));
			}
			if (StringUtils.isNotBlank(task.getQueryServiceItems())) {
				contentSB.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;查询保全项目：&nbsp;");
				Iterator<String> it = Arrays.asList(
						task.getQueryServiceItems().split(",")).iterator();
				while (it.hasNext()) {
					String serviceItems = it.next();
					if (StringUtils.isNotBlank(serviceItems)) {
						contentSB.append(serviceItems).append(
								PosUtils.getDescByCodeFromCodeTable(
										CodeTableNames.PRODUCT_SERVICE_ITEMS,
										serviceItems));
						if (it.hasNext()) {
							contentSB.append("/");
						}
					} else if (!it.hasNext()) {
						contentSB.deleteCharAt(contentSB.length() - 1);
					}
				}
			}
			
			if (StringUtils.isNotBlank(task.getQueryPolicyChannelCode())) {
				contentSB
						.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;保单渠道：&nbsp;")
						.append(PosUtils.getDescByCodeFromCodeTable(
								CodeTableNames.CHANNEL_TYPE_TBL,
								task.getQueryPolicyChannelCode()));
			}
			
			if (StringUtils.isNotBlank(task.getQueryUserId()) && StringUtils.isBlank(commonQueryDAO.getUserNameByUserId(task
					.getQueryUserId()))) {
				contentSB
						.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;保单服务人员：&nbsp;")
						.append(task.getQueryUserId());
			}
			
			if(StringUtils.isNotBlank(commonQueryDAO.getUserNameByUserId(task
					.getQueryUserId()))){
			if (StringUtils.isNotBlank(task.getQueryUserId())) {
				contentSB
						.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;受理人：&nbsp;")
						.append(commonQueryDAO.getUserNameByUserId(task
								.getQueryUserId())).append("（")
						.append(task.getQueryUserId()).append("）");
			}
			}
			
			contentSB
					.append("<br/><br/>请点击<a href=\"")
					.append(downloadURL)
					.append("\">这里</a>下载结果文件；如果您的邮箱无法点击链接，请将如下的URL复制至浏览器地址栏下载：<br/>")
					.append(downloadURL)
					.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;谢谢!<br/><br/><br/><br/><br/><br/><br/><br/>")
					.append("<div style=\"color:gray;font-size:12px;\">本邮件由系统生成，请勿回复</div></body></html>");
			mail.setContent(contentSB.toString());// 邮件内容
			mailService.send(mail);
			logger.info("task " + task.getTaskId() + " email send success.");
			return true;
		} catch (Exception e) {
			logger.error("task " + task.getTaskId() + " email send fail.", e);
		}
		return false;
	}

	/**
	 * 发送失败邮件
	 * 
	 * @param task
	 * @param failReason
	 */
	private void sendFailEmail(NoticePrintBatchTask task, String failReason) {
		if (!"Y".equalsIgnoreCase(task.getNeedSendEmail())) {
			logger.info("task " + task.getTaskId()
					+ " does not need to send fail email.");
			return;
		}
		try {
			logger.info("task " + task.getTaskId() + " send email to "
					+ task.getEmail());
			Mail mail = new Mail();
			mail.setSubject("[SL_POS]通知书批次打印任务完成通知"); // 邮件标题
			mail.setTo(new String[] { task.getEmail() }); // 收件人
			mail.setForm("sl_pos_mail_admin@sino-life.com"); // 发件人
			mail.setMailType(MailType.HTML_CONTENT); // 邮件类型
			mail.setContent(new StringBuilder(
					"<html><body style=\"font-size:14px;text-align:left;\">")
					.append("尊敬的保全系统用户 ")
					.append(task.getSubmitUserName())
					.append(":<br/>")
					.append("&nbsp;&nbsp;&nbsp;&nbsp;你好!<br/>")
					.append("&nbsp;&nbsp;&nbsp;&nbsp;很抱歉通知您，您于&nbsp;")
					.append(PosUtils.formatDate(task.getSubmitDate(),
							"yyyy年MM月dd日 HH时mm分"))
					.append("&nbsp;提交的查询请求处理失败，失败原因为：")
					.append(failReason)
					.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;谢谢!<br/><br/><br/><br/><br/><br/><br/><br/>")
					.append("<div style=\"color:gray;font-size:12px;\">本邮件由系统生成，请勿回复，谢谢</div></body></html>")
					.toString());// 邮件内容
			mailService.send(mail);
			logger.info("task " + task.getTaskId()
					+ " fail email send success.");
		} catch (Exception e) {
			logger.error("task " + task.getTaskId() + " fail email send fail.",
					e);
		}
	}

	/**
	 * 生成临时PDF文件
	 * 
	 * @param task
	 * @param tempFile
	 */
	private boolean generatePdfFile(NoticePrintBatchTask task, SFFile tempFile) {
		long startTime = System.currentTimeMillis();
		String noticeType = task.getQueryNoticeType();
		String noticeType2 = null;
		if (StringUtils.isNotBlank(noticeType)) {
			noticeType = noticeType.trim().toLowerCase();
			if("ent1".equals(noticeType)){
				noticeType ="ent";
				noticeType2 = "ent1";
			}
			NoticePrintHandler handler = NoticePrintHandlerFactory
					.getNoticePrintHandler(noticeType);
			if ("ent1".equals(noticeType2)){
				noticeType = "ent1";
			}
			long recordNum = 0;
			logger.info("task " + task.getTaskId()
					+ " start generating pdf file.");
			if ("ent".equals(noticeType)) {
				// 处理批单
				EndorsementPrintCriteriaDTO criteria = new EndorsementPrintCriteriaDTO();
				criteria.setAcceptBranchCode(task.getQueryBranchCode());
				criteria.setAcceptChannelCode(task.getQueryChannelCode());
				criteria.setAcceptor(task.getQueryUserId());
				criteria.setEffectDateStart(task.getQueryDateStart());
				criteria.setEffectDateEnd(task.getQueryDateEnd());
				criteria.setServiceItems(task.getQueryServiceItems());
				criteria.setPolicyChannelCode(task.getQueryPolicyChannelCode());
				criteria.setApprovalServiceType(task
						.getQueryApprovalServiceType());
				criteria.setSubmitUserId(task.getSubmitUserId());
				criteria.setPrintOptions(task.getPrintStatus());
				recordNum = this.handleEntPdfFileGenerate(handler, criteria,
						tempFile);
			 }else if("ent1".equals(noticeType)){
				 EndorsementPrintCriteriaDTO criteria = new EndorsementPrintCriteriaDTO();
					criteria.setAcceptBranchCode(task.getQueryBranchCode());
					criteria.setAcceptChannelCode(task.getQueryChannelCode());
					criteria.setAcceptor(task.getQueryUserId());
					criteria.setEffectDateStart(task.getQueryDateStart());
					criteria.setEffectDateEnd(task.getQueryDateEnd());
					criteria.setServiceItems(task.getQueryServiceItems());
					criteria.setPolicyChannelCode(task.getQueryPolicyChannelCode());
					criteria.setApprovalServiceType(task
							.getQueryApprovalServiceType());
					criteria.setSubmitUserId(task.getSubmitUserId());
					criteria.setPrintOptions(task.getPrintStatus());
					recordNum = this.handleLogoPdfFileGenerate(handler, criteria, tempFile);
			
			} else {
				// 处理通知书
				NoticePrintCriteriaDTO criteria = new NoticePrintCriteriaDTO();
				criteria.setBranchCode(task.getQueryBranchCode());
				criteria.setBusinessDateStart(task.getQueryDateStart());
				criteria.setBusinessDateEnd(task.getQueryDateEnd());
				criteria.setNoticeType(noticeType);
				criteria.setSinglePrintFlag("N");
				criteria.setServiceNo(task.getQueryUserId());
				criteria.setPolicyChannel(task.getQueryPolicyChannelCode());
				recordNum = this.handleNoticePdfFileGenerate(handler, criteria,
						tempFile);
			}
			if (recordNum > 0) {
				task.setNoticeFileName(handler.getFileName(null));
				task.setFileSize(tempFile.length());
				task.setGenCostTime(System.currentTimeMillis() - startTime);
				task.setGenRecordNum(recordNum);
				return true;
			}
		} else {
			logger.info("task " + task.getTaskId() + " query found no data.");
		}
		return false;
	}

	/**
	 * 生成通知书PDF
	 * 
	 * @param handler
	 * @param criteria
	 * @param tempFile
	 * @return
	 */
	private long handleNoticePdfFileGenerate(final NoticePrintHandler handler,
			NoticePrintCriteriaDTO criteria, SFFile tempFile) {
	    
        /* 查询通知书电子信函的处理 edit by gaojiaming start */
	    //电子信函查询标记
//	    criteria.setIsELetterFlag("Y");
//        List<String> noticeIdListELetter = printService.queryPosNoteByCriteria(criteria);
//        Map<String, Object> paraMap = new HashMap<String, Object>();
//        paraMap.put("taskFlag", "queryPosNote");
//        paraMap.put("noticeIdList", noticeIdListELetter);
//        paraMap.put("noticeType", criteria.getNoticeType());
//        printService.manageELetter(paraMap);
        //电子信函查询标记
        criteria.setIsELetterFlag("N");
        /* 查询通知书电子信函的处理 edit by gaojiaming end */  
        
		List<String> noticeIdList = printService
				.queryPosNoteByCriteria(criteria);
		logger.info("handleNoticePdfFileGenerate found "
				+ (noticeIdList == null ? 0 : noticeIdList.size()) + " record");
		if (noticeIdList != null && !noticeIdList.isEmpty()) {
			OutputStream outputStream = null;
			long recordNum = noticeIdList.size();
			try {
				outputStream = tempFile.openOutputStream();
				handler.handleBatchNoticePrint(noticeIdList, outputStream);
				noticeIdListNeedToUpdate.set(noticeIdList);
				return recordNum;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(
						"handleNoticePdfFileGenerate error:" + e.getMessage(),
						e);
			} finally {
				PosUtils.safeCloseOuputStream(outputStream);
			}
		}
		return 0;
	}

	/**
	 * 生成批单PDF
	 * 
	 * @param handler
	 * @param criteria
	 * @param tempFile
	 * @return
	 */
	private long handleEntPdfFileGenerate(final NoticePrintHandler handler,
			EndorsementPrintCriteriaDTO criteria, SFFile tempFile) {	            
		List<Map<String, Object>> posInfoList = printService
				.queryEndorsementInfoByCriteria(criteria);
		logger.info("handleEntPdfFileGenerate found "
				+ (posInfoList == null ? 0 : posInfoList.size()) + " record");

		if (posInfoList != null && !posInfoList.isEmpty()) {

			final Iterator<Map<String, Object>> posInfoIterator = posInfoList
					.iterator();
			long recordNum = posInfoList.size();
			OutputStream outputStream = null;
			try {
				outputStream = tempFile.openOutputStream();
				handler.mergePdfFiles(new Iterator<List<File>>() {
					@Override
					public boolean hasNext() {
						return posInfoIterator.hasNext();
					}

					@Override
					public List<File> next() {
						Map<String, Object> posInfo = posInfoIterator.next();
						InputStream inputStream = null;
						OutputStream fileOutputStream = null;
						List<File> resultFileList = new ArrayList<File>();
						try {
							String posNo = (String) posInfo.get("POS_NO");
							Date applyDate = (Date) posInfo.get("APPLY_DATE");

							// 处理批单
							File tmpFile = handler
									.handleSingleNoticePrint(posNo);
							
							// 电子印章
							SFFile sigFile = handlePdfMail.signature((SFFile) tmpFile, posNo,"toprsement");
							inputStream = new FileInputStream(sigFile);
							resultFileList.add(sigFile);

							// 处理保证价值表
							List<PosNoteDTO> posNoteList = printService
									.queryPosNoteMainByPosNo(posNo);
							if (posNoteList != null && !posNoteList.isEmpty()) {
								for (PosNoteDTO posNote : posNoteList) {
									NoticePrintHandler handler = NoticePrintHandlerFactory
											.getNoticePrintHandler(posNote
													.getNoteType());
									tmpFile = handler
											.handleSingleNoticePrint(posNote
													.getDetailSequenceNo());
									resultFileList.add(tmpFile);
								}
							}

							// 处理产品条款
							Set<String> productSet = printDAO
									.getAddProduct(posNo);
							if (productSet != null && !productSet.isEmpty()) {
								// 添加险种条款信息
								for (String productCode : productSet) {
									byte[] planProvisionContent = printDAO
											.queryPlanProvisionByProductCode(
													productCode, applyDate);
									if (planProvisionContent != null
											&& planProvisionContent.length > 0) {
										tmpFile = TEM_FILE_FACTORY
												.createTempFile();
										fileOutputStream = new FileOutputStream(
												tmpFile);
										fileOutputStream
												.write(planProvisionContent);
										fileOutputStream.flush();
										fileOutputStream.close();
										resultFileList.add(tmpFile);
									}
								}
							}
							return resultFileList;
						} catch (Exception e) {
							PosUtils.deleteFiles(resultFileList);
							throw new RuntimeException(e);
						} finally {
							PosUtils.safeCloseOuputStream(fileOutputStream);
						}
					}

					@Override
					public void remove() {
						posInfoIterator.remove();
					}
				}, outputStream);

				return recordNum;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(
						"handleEntPdfFileGenerate error:" + e.getMessage(), e);
			} finally {
				PosUtils.safeCloseOuputStream(outputStream);
			}
		}
		return 0;
	}
	
	/**
	 * 生成批单PDF(激光打印)
	 * 
	 * @param handler
	 * @param criteria
	 * @param tempFile
	 * @return
	 */
	private long handleLogoPdfFileGenerate(final NoticePrintHandler handler,
			EndorsementPrintCriteriaDTO criteria, SFFile tempFile) {	            
		List<Map<String, Object>> posInfoList = printService
		.queryEndorsementInfoByCriteria(criteria);
		logger.info("handleEntPdfFileGenerate found "
				+ (posInfoList == null ? 0 : posInfoList.size()) + " record");
		
		if (posInfoList != null && !posInfoList.isEmpty()) {
			
			final Iterator<Map<String, Object>> posInfoIterator = posInfoList
			.iterator();
			long recordNum = posInfoList.size();
			OutputStream outputStream = null;
			try {
				outputStream = tempFile.openOutputStream();
				handler.mergePdfFiles(new Iterator<List<File>>() {
					@Override
					public boolean hasNext() {
						return posInfoIterator.hasNext();
					}
					
					@Override
					public List<File> next() {
						Map<String, Object> posInfo = posInfoIterator.next();
						InputStream inputStream = null;
						OutputStream fileOutputStream = null;
						List<File> resultFileList = new ArrayList<File>();
						try {
							String posNo = (String) posInfo.get("POS_NO");
							Date applyDate = (Date) posInfo.get("APPLY_DATE");
							
							// 处理批单
							File tmpFile = handler.handleApplicationFillFormPrint(posNo,"0","ent" );
							
							// 电子印章
							SFFile sigFile = handlePdfMail.signature((SFFile) tmpFile, posNo,"toprsement");
							inputStream = new FileInputStream(sigFile);
							resultFileList.add(sigFile);
							
							// 处理保证价值表
							List<PosNoteDTO> posNoteList = printService
							.queryPosNoteMainByPosNo(posNo);
							if (posNoteList != null && !posNoteList.isEmpty()) {
								for (PosNoteDTO posNote : posNoteList) {
									NoticePrintHandler handler = NoticePrintHandlerFactory
									.getNoticePrintHandler(posNote
											.getNoteType());
									tmpFile = handler
									.handleSingleNoticePrint(posNote
											.getDetailSequenceNo());
									resultFileList.add(tmpFile);
								}
							}
							
							// 处理产品条款
							Set<String> productSet = printDAO
							.getAddProduct(posNo);
							if (productSet != null && !productSet.isEmpty()) {
								// 添加险种条款信息
								for (String productCode : productSet) {
									byte[] planProvisionContent = printDAO
									.queryPlanProvisionByProductCode(
											productCode, applyDate);
									if (planProvisionContent != null
											&& planProvisionContent.length > 0) {
										tmpFile = TEM_FILE_FACTORY
										.createTempFile();
										fileOutputStream = new FileOutputStream(
												tmpFile);
										fileOutputStream
										.write(planProvisionContent);
										fileOutputStream.flush();
										fileOutputStream.close();
										resultFileList.add(tmpFile);
									}
								}
							}
							return resultFileList;
						} catch (Exception e) {
							PosUtils.deleteFiles(resultFileList);
							throw new RuntimeException(e);
						} finally {
							PosUtils.safeCloseOuputStream(fileOutputStream);
						}
					}
					
					@Override
					public void remove() {
						posInfoIterator.remove();
					}
				}, outputStream);
				
				return recordNum;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(
						"handleEntPdfFileGenerate error:" + e.getMessage(), e);
			} finally {
				PosUtils.safeCloseOuputStream(outputStream);
			}
		}
		return 0;
	}

	/**
	 * 将生成的通知书上传至IM
	 * 
	 * @param task
	 * @param tempFile
	 * @return
	 */
	private boolean storeToIM(NoticePrintBatchTask task, SFFile tempFile) {
		try {

			logger.info("new PDF file size  " + tempFile.length());
			logger.info("task " + task.getTaskId() + " store file "
					+ tempFile.getAbsolutePath() + " to im.");
			SFFilePath fileDestinationPath = new SFFilePath();
			fileDestinationPath.setModule("pos");
			fileDestinationPath
					.setModuleSubPath(new String[] { "notice_print_batch" });
			fileDestinationPath.setStogeType(StogeType.WEEK);
			fileDestinationPath.setMimeType(MimeType.application_pdf);
			String fileId = PlatformContext.getIMFileService().putFile(
					tempFile, fileDestinationPath);
			task.setIMFileId(fileId);
			logger.info("task " + task.getTaskId()
					+ " store file success, file id is " + fileId);
			return true;
		} catch (Exception e) {
			logger.info(" storeToIM-task " + task.getTaskId() + " storefilefail", e);
		}
		return false;
	}

	/**
	 * 获取并锁定任务记录
	 * 
	 * @return
	 */
	private String retriveTask() {
		List<String> taskIdList = scheduleDAO.queryPosNoteBatchTask(30);
		while (taskIdList != null && !taskIdList.isEmpty()) {
			String taskId = null;
			if (taskIdList.size() == 1) {
				taskId = taskIdList.remove(0);
			} else {
				taskId = taskIdList.remove(RandomUtils.nextInt(taskIdList
						.size()));
			}
			if (lockTask(taskId)) {
				return taskId;
			}
		}
		return null;
	}

	/**
	 * 锁定任务记录，并更新任务状态为处理中
	 * 
	 * @param taskId
	 * @return
	 */
	private boolean lockTask(final String taskId) {
		return txTmpl.execute(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction(TransactionStatus transactionstatus) {
				boolean locked = scheduleDAO.lockNoticePrintBatchTask(taskId);
				if (locked) {
					scheduleDAO.updateNoticePrintBatchTask(taskId,
							"task_status", "2");
					scheduleDAO.updateNoticePrintBatchTask(taskId,
							"proc_server_ip", localHostIp);
					scheduleDAO.touchNoticePrintBatchTask(taskId);
					return true;
				}
				return false;
			}
		});
	}

	/**
	 * 将任务标注为失败
	 * 
	 * @param taskId
	 * @param failReason
	 */
	private void markTaskFailed(String taskId, String failReason) {
		scheduleDAO.updateNoticePrintBatchTask(taskId, "task_status", "4");
		scheduleDAO.updateNoticePrintBatchTask(taskId, "task_fail_reason",
				failReason);
	}

	/**
	 * 完成任务，将处理结果保存至数据库
	 * 
	 * @param task
	 */
	private void completeTask(final NoticePrintBatchTask task) {
		txTmpl.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(
					TransactionStatus transactionstatus) {
				// 如果有需要，更新通知书打印时间
				List<String> noticeIdList = noticeIdListNeedToUpdate.get();
				if (noticeIdList != null && !noticeIdList.isEmpty()) {
					logger.info("task " + task.getTaskId()
							+ " update print date.");
					for (String detailSequenceNo : noticeIdList) {
						printService
								.updatePosNoteMainPrintDate(detailSequenceNo);
					}
				}
				// 更新打印任务表
				String taskId = task.getTaskId();
				scheduleDAO.updateNoticePrintBatchTask(taskId, "task_status",
						"3");
				scheduleDAO.updateNoticePrintBatchTask(taskId, "gen_cost_time",
						task.getGenCostTime());
				scheduleDAO.updateNoticePrintBatchTask(taskId,
						"gen_record_num", task.getGenRecordNum());
				scheduleDAO.updateNoticePrintBatchTask(taskId, "im_file_id",
						task.getIMFileId());
				scheduleDAO.updateNoticePrintBatchTask(taskId, "file_size",
						task.getFileSize());
				scheduleDAO.updateNoticePrintBatchTask(taskId,
						"notice_file_name", task.getNoticeFileName());
				logger.info("task " + task.getTaskId() + " finished:"
						+ PosUtils.describBeanAsJSON(task));
			}
		});
	}

	/**
	 * 根据网卡取本机配置的IP
	 * 
	 * @return
	 */
	private String getLocalHostIp() {
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface
					.getNetworkInterfaces();
			InetAddress ip = null;
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					ip = (InetAddress) inetAddresses.nextElement();
					if (!ip.isLoopbackAddress()
							&& ip.getHostAddress().indexOf(":") == -1) {
						return ip.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
			logger.error(e);
		}
		return null;
	}

}
