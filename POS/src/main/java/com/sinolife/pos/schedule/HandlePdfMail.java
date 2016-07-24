package com.sinolife.pos.schedule;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeUtility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.indigopacific.idg.IDGClientException;
import com.indigopacific.idg.IDGClientImpl;
import com.indigopacific.idg.IIDGResult;
import com.sinolife.efs.rpc.domain.UserInfo;
import com.sinolife.pos.acceptance.rollback.dao.BranchRollbackDAO;
import com.sinolife.pos.common.file.dao.PosFileDAO;
import com.sinolife.pos.print.dao.PrintDAO;
import com.sinolife.pos.print.dto.PosNoteDTO;
import com.sinolife.pos.print.notice.NoticePrintHandler;
import com.sinolife.pos.print.notice.NoticePrintHandlerFactory;
import com.sinolife.pos.print.service.PrintService;
import com.sinolife.pos.pubInterface.biz.impl.DevelopPlatformInterfaceImpl;
import com.sinolife.pos.schedule.dao.ScheduleDAO;
import com.sinolife.sf.config.RuntimeConfig;
import com.sinolife.sf.framework.email.Mail;
import com.sinolife.sf.framework.email.MailService;
import com.sinolife.sf.framework.email.MailType;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.store.SFFile;
import com.sinolife.sf.store.SFFilePath;
import com.sinolife.sf.store.StogeType;
import com.sinolife.sf.util.Util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
@Service("HandlePdfMail")
public class HandlePdfMail {
	@Autowired
	private PrintService printService;
	@Autowired
	private PrintDAO printDAO;
	@Autowired
	private PosFileDAO posFileDAO;
	@Autowired
	private BranchRollbackDAO rollbackDAO;
	@Autowired @Qualifier("mailService03")
	private MailService mailService;
	@Autowired
	private ScheduleDAO scheduleDAO;
	Logger logger = Logger.getLogger(HandlePdfMail.class
			.getName());

	public HandlePdfMail() {
		String ips = RuntimeConfig.get("indogoIp", String.class);
		sigIp = ips.split(",");
	}

	synchronized String nextSigIP() {
		sigIpIndex++;
		sigIpIndex = sigIpIndex % sigIp.length;
		logger.info("========indogoIp:" + sigIp[sigIpIndex]);
		return sigIp[sigIpIndex];
	}

	// 签章服务IP
	private static String[] sigIp;
	private static int sigIpIndex = 0;
	/**
	 * 生成批单文件流
	 * 
	 * @param posNo
	 * @param submiter
	 * @return
	 */
	public SFFile createOutputStreamForEnt(String posNo, String submiter) {

		Map<String, Object> posInfo = printService
				.queryEndorsementInfoByPosNo(posNo);
		if (posInfo == null || posInfo.isEmpty()) {
			throw new RuntimeException("找不到批单，或者保全申请还未生效");

		}
		// 更新批单打印人员信息
		printDAO.updatePosInfoEntData(posNo, submiter);
		NoticePrintHandler handler = NoticePrintHandlerFactory
				.getNoticePrintHandler("mail");
		SFFile tmpFile = new SFFile();
		try {
			handler.handleSingleNoticePrint(posNo).renameTo(tmpFile);
			return tmpFile;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);

		} finally {

			tmpFile.getRpcAttach().setDeleteAfterReturn(true);
		}
	}

	/**
	 * 处理电子批单 1.根据保全号获取电子批单 2.电子批单加盖电子印章 3.电子批单上传服务器 4.发送邮件
	 * 处理现价表     1.根据保全号获取现价表      2.现价表上传服务器 3.发送邮件
	 */
	public List<Map<String, String>> handPDFEnt(String posNo, String email, String appName)
			throws IOException, IDGClientException {
		List<Map<String, String>> entUrlList = new ArrayList<Map<String, String>>();
		List<Map<String, Object>> sffileList = new ArrayList<Map<String, Object>>();
		// 从POS获得电子批单
		SFFile file = createOutputStreamForEnt(posNo, email);
		// 电子印章
		SFFile sigFile = signature(file, posNo,"toprsement");
		// 上传服务器
		logger.info("######## handPDFEnt before saveFile 上传服务器" + posNo);
		String url = saveFile(posNo, email, sigFile);
		logger.info("######## handPDFEnt after saveFile 上传服务器");

		// 返回页面链接名称和地址
//		Map<String, String> entUrlMap = new HashMap<String, String>();
//		entUrlMap.put("posNo", posNo);
//		entUrlMap.put("url", url);
//		entUrlList.add(entUrlMap);
		// 添加电子批单为邮件附件
		Map<String, Object> fileMap = new HashMap<String, Object>();
		fileMap.put("fileName", posNo + "保险批单.pdf");
		fileMap.put("file", sigFile);
		sffileList.add(fileMap);
		sigFile.getRpcAttach().setDeleteAfterReturn(true);
		//file.getRpcAttach().setDeleteAfterReturn(true);
		//获取现价表
		List<File> fileList=cashValueForNote(posNo);
		for(int i=0;i<fileList.size();i++){
			SFFile cashFlie =(SFFile) fileList.get(i);
			Map<String, Object> cashfileMap = new HashMap<String, Object>();
			Map<String, String> entUrlMap1 = new HashMap<String, String>();
			logger.info("######## handPDFEnt before saveFile 上传服务器");
			String url1 = saveFile(posNo, email, cashFlie);
			logger.info("######## handPDFEnt after saveFile 上传服务器");
			// 返回页面链接名称和地址
			entUrlMap1.put("posNo", posNo);
			entUrlMap1.put("url", url1);
			entUrlList.add(entUrlMap1);
			// 添加电子批单为邮件附件
			cashfileMap.put("fileName", posNo + "现价表.pdf");
			cashfileMap.put("file", cashFlie);
			sffileList.add(cashfileMap);
			cashFlie.getRpcAttach().setDeleteAfterReturn(true);
		}
        String contentStr = "";
        String mailHead = "";
        if (fileList != null && fileList.size() > 0) {
            contentStr = "的保险批单及现价表电子版发送给您，请注意查收。";
            mailHead = "富德生命人寿电子批单及现价表";
        } else {
            contentStr = "的保险批单电子版发送给您，请注意查收。";
            mailHead = "富德生命人寿电子批单";
        }
		Map rstMap = rollbackDAO.queryOriginalPosInfo(posNo);
		String content = "尊敬的" +appName+"先生/女士:"
			+	"<br/>&nbsp;&nbsp;&nbsp;&nbsp;您对保单"
			+ (String) rstMap.get("POLICY_NO")
			+ "申请的"
			+ (String) rstMap.get("SERVICE_ITEMS_DESC")
			+ "操作成功！现将保全号为"
			+ posNo
			+ contentStr;
        if (!StringUtils.isBlank(url)) {
            logger.info("######## handPDFEnt before sendPosEntMail 发送邮件");
			printService.sendEMail(email, sffileList, mailHead, content, posNo);        
            //sendPosEntMail(userInfo.getEmail(), posNo, sffileList, mailHead, content);
            logger.info("######## handPDFEnt after sendPosEntMail 发送邮件");
            // 发送完电子批单后更新E_ENT_FILE_ID
            scheduleDAO.updatePosInfoEfileID(posNo, url);
        } else {
            logger.info("######## 保全号" + posNo + "上传IM失败，未能发送邮件！");
        }

		return null;
	}
	/**
	 * 处理电子批单 1.根据保全号获取电子批单 2.电子批单加盖电子印章 3.电子批单上传服务器 
	 */
	public List<Map<String, String>> handPDFEntIm(String posNo)
			throws IOException, IDGClientException {
		// 从POS获得电子批单
		SFFile file = createOutputStreamForEnt(posNo, "sys");
		// 电子印章
		//SFFile sigFile = signature(file, posNo,"endorsement");
		// 上传服务器
		logger.info("######## handPDFEnt before saveFile 上传服务器" + posNo);
		String url = saveFile(posNo, "sys", file);
		logger.info("######## handPDFEnt after saveFile 上传服务器");
        if (!StringUtils.isBlank(url)) {
            // 更新E_ENT_FILE_ID
            scheduleDAO.updatePosInfoEfileID(posNo, url);
        } else {
            logger.info("######## 保全号" + posNo + "上传IM失败");
        }

		return null;
	}
	/**
	 * 电子批单加盖电子印章 
	 *  pdftype  右下角 endorsement ，右上角 toprsement
	 */
	public SFFile signature(SFFile file, String po,String pdftype) throws IOException,
			IDGClientException {
		IDGClientImpl idg = new IDGClientImpl(nextSigIP(), RuntimeConfig.get(
				"indogoPort", String.class));
		logger.info("======================indogoPort"
				+ RuntimeConfig.get("indogoPort", String.class));
		int timeout = 10000000;
		idg.setTimeOut(timeout);
		IIDGResult result = null;
		InputStream[] is = null;

		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<root>"
				+ "<rule>" + "<fileid>" + po + "</fileid>" + "<pdfname>" + po
				+ ".pdf" + "</pdfname>" + "<pdftype>" + pdftype 
				+ "</pdftype>" + "</rule>" + "</root>";

		is = new InputStream[2];
		InputStream returnIn = null;
		FileOutputStream fos = null;
		SFFile sffile = new SFFile();
		try {
			is[0] = new ByteArrayInputStream(xml.getBytes("utf-8"));
			is[1] = new FileInputStream(file);
			result = idg.submitJob(is, new String[] { "RULEXML", "PDF" }, "",
					"", "", new String[] { "", "" });
			InputStream[] streams = result.getStreams();
			if (streams != null && streams.length > 0) {
				for (int j = 0; j < streams.length; j++) {
					returnIn = streams[j];
					break;
				}
			}

			fos = new FileOutputStream(sffile);
			Util.copyStream(returnIn, fos);
		} finally {
			try {
				is[1].close();
			} catch (Throwable e) {

			}
			try {
				is[0].close();
			} catch (Throwable e) {

			}
			try {
				returnIn.close();
			} catch (Throwable e) {

			}

			try {
				fos.close();
			} catch (Throwable e) {

			}
		}
		return sffile;
	}

	/**
	 * 上传电子保单到IM服务器
	 */
	public String saveFile(String posNo, String saveBy, SFFile sigFile) {
		SFFilePath destPath = new SFFilePath();
		destPath.setModule("pos");
		destPath.setModuleSubPath(new String[] { "ent" });
		destPath.setStogeType(StogeType.DAY);
		logger.info("######## PlatformContext.getIMFileService().putFile(sigFile, destPath) begin");
		String fileId = PlatformContext.getIMFileService().putFile(sigFile,
				destPath);
		logger.info("######## PlatformContext.getIMFileService().putFile(sigFile, destPath) end. filedId:"
				+ fileId);
		// 记录电子批单到文件表
		posFileDAO.insertPosFileInfo(fileId, posNo, null, posNo, "7", saveBy);

		logger.info("######## insertPosFileInfo");
		return fileId;
	}

	/**
	 * 发送电子批单邮件
	 */
	public void sendPosEntMail(String email, String posNo,List<Map<String, Object>> sffileList,String mailHead,String content) {
		Mail mail = new Mail();
		mail.setSubject(mailHead);// 邮件标题
		mail.setTo(new String[] { email });// 收件人
        //mail.setTo(new String[] { "gaojiaming.wb@sino-life.com" });// 收件人
		mail.setForm("epos@sino-life.com");// 发件人
		mail.setMailType(MailType.HTML_CONTENT);// 邮件类型
		mail.setContent(content);// 邮件内容
		for (Map<String, Object> map : sffileList) {
	    String fileName=(String)map.get("fileName")+ ".pdf";
        try {
			mail.addAttachment(MimeUtility.encodeText(fileName),(SFFile) map.get("sigfile"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		mailService.send(mail);
	}

	/**
	 * 处理现价表
	 */
	public  List<File> cashValueForNote(String posNo) {			
		//保证价值表
		List<PosNoteDTO> posNoteList = printService.queryPosNoteMainByPosNo(posNo);
		NoticePrintHandler handler = NoticePrintHandlerFactory.getNoticePrintHandler("ent");
		File tmpFile = null;
		List<File> resultFileList = new ArrayList<File>();
		try {
			if(posNoteList != null && !posNoteList.isEmpty()) {
				for(PosNoteDTO posNote : posNoteList) {
					handler = NoticePrintHandlerFactory.getNoticePrintHandler(posNote.getNoteType());
					tmpFile = handler.handleSingleNoticePrint(posNote.getDetailSequenceNo());
					resultFileList.add(tmpFile);
				}
			
			}
			return resultFileList;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		} 
	}
}
