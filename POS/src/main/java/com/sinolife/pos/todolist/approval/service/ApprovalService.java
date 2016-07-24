package com.sinolife.pos.todolist.approval.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.hamcrest.core.IsAnything;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.esbpos.oa.GoaPosService;
import com.sinolife.esbpos.product.ProductService;
import com.sinolife.im.http.client.IM;
import com.sinolife.im.http.client.exception.ClientException;
import com.sinolife.im.http.client.exception.ResultException;
import com.sinolife.im.http.model.Constant;
import com.sinolife.im.http.model.ImageInfo;
import com.sinolife.pos.acceptance.branch.service.BranchAcceptService;
import com.sinolife.pos.common.consts.CodeTableNames;
import com.sinolife.pos.common.dao.CommonAcceptDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO;
import com.sinolife.pos.common.dto.PosApproveInfo;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.PosStatusChangeHistoryDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.others.sms.dao.SmsDAO;
import com.sinolife.pos.setting.user.privilege.dao.UserPrivilegeDAO;
import com.sinolife.pos.todolist.approval.dao.ApprovalDAO;
import com.sinolife.sf.config.RuntimeConfig;
import com.sinolife.sf.framework.comm.BizException;
import com.sinolife.sf.framework.email.Mail;
import com.sinolife.sf.framework.email.MailService;
import com.sinolife.sf.framework.email.MailType;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.um.user.domain.User;
import com.sinolife.um.user.spi.UserService;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Service("approvalService")
public class ApprovalService {

	@Autowired
	ApprovalDAO approvalDAO;

	@Autowired
	CommonQueryDAO queryDAO;

	@Autowired
	CommonAcceptDAO acceptDAO;

	@Autowired
	UserPrivilegeDAO userDAO;

	@Autowired
	SmsDAO smsDAO;

	@Autowired
	private IM im;

	@Autowired
	BranchAcceptService acceptService;

	@Autowired @Qualifier("mailService01")
	private MailService mailService;
	
	@Autowired
	private UserService userService;

	Logger logger = Logger.getLogger(getClass());

	/**
	 * 查询待审批列表，展示于代办任务页面
	 * 
	 * @param user
	 * @return
	 */
	public List queryApproveList(String user) {
		return approvalDAO.queryApproveList(user, null);
	}

	/**
	 * 查询公共待审批列表，展示于代办任务页面
	 * 
	 * @param user
	 * @return
	 */
	public List queryPublicApproveList(String user) {
		return approvalDAO.queryPublicApproveList(user);
	}

	/**
	 * 查询待审批信息，用于逐单入口
	 * 
	 * @return
	 */
	public String queryApproveSingle(String posNo) {
		String str = null;
		List list = approvalDAO.queryApproveList(
				PlatformContext.getCurrentUser(), posNo);
		if (list != null && list.size() > 0) {
			Map map = (Map) list.get(0);
			str = posNo + "/" + map.get("SUBMIT_NO") + "/"
					+ map.get("APPROVE_NO") + "/" + map.get("BARCODE_NO") + "/"
					+ map.get("POLICY_NO") + "/" + map.get("CLIENT_NO");
		}
		return str;
	}

	/**
	 * 查询签名影像对应的barcode
	 * 
	 * @param posNo
	 * @return
	 */
	public Map<String, Object> querySignBarcode(String policyNo, String clientNo) {
		return queryDAO.querySignBarcode(policyNo, clientNo);
	}

	/**
	 * 获取签名影像 返回签名影像的非tif格式图片的url
	 * 
	 * @param barcodeNo
	 * @param docType
	 * @return
	 */
	public String querySignJPGUrl(String barcodeNo, String docType) {
		String url = null;
		try {
			List<ImageInfo> imageList = im.queryImageByBarcodeAndDocType2(
					barcodeNo, docType, Constant.CONTENT_YES);
			for (int i = 0; imageList != null && i < imageList.size(); i++) {
				ImageInfo image = imageList.get(i);
				if (!("tif".equals(image.getMime().toLowerCase()) || "tiff"
						.equals(image.getMime().toLowerCase()))) {
					url = image.getURL();
					break;
				}
			}
		} catch (Exception e) {
			logger.error("查询签名影像图片", e);
			// 忽略掉查询不到签名影像的异常
		}
		return url;
	}

	public String queryPolicyApplyBarcode(String policyNo) {
		return queryDAO.queryPolicyApplyBarcode(policyNo);
	}

	/**
	 * 查询审批页面展示的信息
	 * 
	 * @param posNo
	 * @param submitNo
	 * @param approveNo
	 * @return
	 */
	public Map queryApproveRecord(String posNo, String submitNo,
			String approveNo) {
		Map map = new HashMap();
		map.put("posNo", posNo);
		map.put("submitNo", submitNo);
		map.put("approveNo", approveNo);
		// 返回到页面用

		map.put("selfApproveFlag", approvalDAO.selfApproveFlag(map));

		map.put("posInfo", approvalDAO.queryApprovePosInfo(posNo));

		map.put("approvedList", approvalDAO.queryApprovedList(posNo, submitNo));

		map.put("problemInfo", approvalDAO.queryProblemInfo(posNo));

		return map;
	}

	public String newApproverCheck(String newApprover) {
		return userDAO.userPosExist(newApprover);
	}

	public String approvalCheck(String posNo, String status, String opType) {
		String flag = "0";
		if ("A".equals(opType)
				&& "N".equals(approvalDAO.posStatusCheck(posNo, status))) {
			flag = "1";

		} else if ("P".equals(opType)
				&& "Y".equals(approvalDAO.approveProblemCheck(posNo))) {
			flag = "2";
		}
		return flag;
	}

	/**
	 * 提交审批结果，并对pos_info做相应的处理
	 * 
	 * @param pMap
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Map approveSubmit(Map pMap) {

		String curUser = PlatformContext.getCurrentUser();
		pMap.put("approver", curUser);

		// 检查重复提交的问题
		if (StringUtils.isNotBlank(approvalDAO.checkAfterApprove(pMap))) {

			throw new RuntimeException("审批前数据检查异常，重复提交审批意见");
		}
		if ("N".equals(approvalDAO.checkBeforeApprove(pMap))) {
			throw new RuntimeException("审批前数据检查异常，请核实审批任务状态");
		}

		String posNo = (String) pMap.get("posNo");

		if ("3".equals(pMap.get("decision"))) {// 转他人
			approvalDAO.updateApproveOpnion(pMap);
			approvalDAO.updateApproveListNo(pMap);
			approvalDAO.insertApproveNo(pMap);

		} else {
			// approvalDAO.updateApproveDecision(pMap);

			approvalDAO.updateApproveDecisions(pMap);
			
			PosStatusChangeHistoryDTO change = new PosStatusChangeHistoryDTO();
			change.setPosNo(posNo);
			change.setChangeDate(queryDAO.getSystemDate());
			change.setChangeUser(PlatformContext.getCurrentUser());

			ServiceItemsDAO serviceItemsDAO = (ServiceItemsDAO) PlatformContext
					.getApplicationContext().getBean("serviceItemsDAO_" + "23");
			ServiceItemsInputDTO ServiceItemsInputDTO = serviceItemsDAO
					.queryCommonAcceptDetailInput(posNo);
			
			/* edit by wangmingshun start */
			
			//查询下一个审批人是否是oa审批人，如果是则写入oa系统
			boolean isOaApproval = false;
			String submitNo = (String) pMap.get("submitNo");
			String approveNo = (String) pMap.get("approveNo");
			String barcodeNo = (String) pMap.get("barcodeNo");
			String policyNo = (String) pMap.get("policyNo");
			List<PosApproveInfo> list = 
				approvalDAO.checkPosApproveInfo(posNo, submitNo, approveNo);
			//排序后的第一条记录为oa审批则将之后的所有审批人发送到oa
			if(list.size() > 0 && "Y".equals(list.get(0).getIsOaApprove()))
				isOaApproval = true;
			
			//下一个人是oa审批人并且当前审批人通过，则直接写入oa系统即可
			if("1".equals(pMap.get("decision")) && isOaApproval) {
				//oa送审处理
				toOaHandle(policyNo, posNo, barcodeNo, submitNo, list);
				
			/* edit by wangmingshun end */
				
				
			} else {
				if ("1".equals(pMap.get("decision"))
						&& approvalDAO.allApproved(pMap)) {// 通过，且全审批完了
					
					pMap.put("acceptStatus", "A10");// 状态走两步，到待投保、核保规则检查状态
					approvalDAO.updatePosApproved(pMap);
					
					change.setOldStatusCode("A08");
					change.setNewStatusCode("A09");
					acceptDAO.insertPosStatusChangeHistory(change);
					
					// 移动保全审批处理（调用短信接口）
					if ("11".equals(ServiceItemsInputDTO.getAcceptChannel())) {
						change.setOldStatusCode("A09");
						change.setNewStatusCode("A22");
						acceptDAO.insertPosStatusChangeHistory(change);
						pMap.put("acceptStatus", "A22");
						approvalDAO.updatePosApproved(pMap);
						
						Map<String, Object> smsMap = new HashMap<String, Object>();
						
						smsMap = smsDAO.sendMposSMS(posNo, "07");
						
						Map<String, Object> map = new HashMap<String, Object>();
						
						map.put("BUSINESS_NO", posNo);// 业务号
						String modle_id = RuntimeConfig.get("sf.system.number",
								String.class);
						map.put("MODULE_ID", modle_id);// 模块ID
						
						String envname = RuntimeConfig.get(
								"com.sinolife.pos.envname", String.class);// 环境名称
						map.put("ENV_NAME", envname);
						String serviceUrl = RuntimeConfig.get(
								"com.sinolife.pos.serviceurl", String.class);// 回调服务url
						map.put("SERVICE_URL", serviceUrl);
						
						map.put("SERVICE_ID", "com.sinolife.pos.sms.sendCallSms");// 回调服务id
						map.put("MOBILE", smsMap.get("phone"));// 手机号
						
						map.put("SEND_MSG", smsMap.get("smsContent"));// 发送内容
						
						ProductService cc = PlatformContext.getEsbContext()
						.getEsbService(ProductService.class);
						cc.sendNeedReplySmsRealTime(map);
						
					} else {
						change.setOldStatusCode("A09");
						change.setNewStatusCode("A10");
						acceptDAO.insertPosStatusChangeHistory(change);
					}
					
				} else if ("2".equals(pMap.get("decision"))) {// 不通过
					
					// 如果是保全受理人自己审批的不通过 或者受理渠道是移动保全，则保全流程结束
					if (PlatformContext.getCurrentUser().equalsIgnoreCase(
							pMap.get("acceptor").toString())
							|| "11".equals(ServiceItemsInputDTO.getAcceptChannel())) {
						pMap.put("acceptStatus", "C03");
						approvalDAO.updatePosApproved(pMap);
						
						change.setOldStatusCode("A08");
						change.setNewStatusCode("C03");
						acceptDAO.insertPosStatusChangeHistory(change);
						
						acceptDAO.resumePolicySuspend(
								(String) pMap.get("policyNo"), posNo,
								(String) pMap.get("serviceItems"));
						
						acceptDAO.nextPosNoTrigger(posNo);
						
						// 移动保全审批处理（调用短信接口）
						if ("11".equals(ServiceItemsInputDTO.getAcceptChannel())) {
							String noPassReason = (String) pMap.get("remark");
							smsDAO.sendAgentMposSms(posNo, "09", noPassReason);
						}
					} else {// 否则，开启一个新审批流程
						//使用新的接口
						//approvalDAO.approveAgain(posNo);
						approvalDAO.approveAgainAll(posNo);
					}
					
				} else if ("4".equals(pMap.get("decision"))) {
					pMap.putAll(acceptService.cancelAndReaccept(posNo, "24",
							"受理人选择结束审批重新录入受理明细", PlatformContext.getCurrentUser()));
				}
			}
		}

		if ("N".equals(approvalDAO.checkAfterApprove(posNo))) {
			throw new RuntimeException("审批后数据异常，重复新建了审批流");
		}

		// 给新审批人(若存在)发送邮件通知
		emailNextApprover(posNo, approvalDAO.nextApprover(posNo));

		return pMap;
	}

	/**
	 * 给下一位审批人发个邮件通知
	 * 
	 * @param posNo
	 * @param newApprover
	 */
	private void emailNextApprover(String posNo, String newApprover) {
		try {
			if (StringUtils.isBlank(posNo) || StringUtils.isBlank(newApprover)) {
				logger.info("approvalEmailNotSend-" + posNo + "-" + newApprover);
				return;
			}
			PosInfoDTO posInfo = queryDAO.queryPosInfoRecord(posNo);
			Mail mail = new Mail();
			mail.setSubject("[SL_POS]保全审批通知"); // 邮件标题
			mail.setTo(new String[] { newApprover }); // 收件人
			mail.setForm("sl_pos_mail_admin@sino-life.com"); // 发件人
			mail.setMailType(MailType.HTML_CONTENT); // 邮件类型
			mail.setContent(new StringBuilder(
					"<html><body style=\"font-size:14px;text-align:left;\">")
					.append("尊敬的保全系统用户 ")
					.append(queryDAO.getUserNameByUserId(newApprover))
					.append(" :<br/>&nbsp;&nbsp;&nbsp;&nbsp;您好!<br/>")
					.append("&nbsp;&nbsp;&nbsp;&nbsp;您的待审批任务中有新的审批业务，请及时审批，谢谢！<br/><br/>")
					.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;条形码：&nbsp;")
					.append(posInfo.getBarcodeNo())
					.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;保单号：&nbsp;")
					.append(posInfo.getPolicyNo())
					.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;批单号：&nbsp;")
					.append(posNo)
					.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;保全项目：&nbsp;")
					.append(PosUtils.getDescByCodeFromCodeTable(
							CodeTableNames.PRODUCT_SERVICE_ITEMS,
							posInfo.getServiceItems()))
					.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;受理日期：&nbsp;")
					.append(PosUtils.formatDate(posInfo.getAcceptDate(),
							"yyyy年MM月dd日"))
					.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;申请日期：&nbsp;")
					.append(PosUtils.formatDate(
							queryDAO.getApplyDateByPosNo(posNo), "yyyy年MM月dd日"))
					.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;受理人：&nbsp;")
					.append(queryDAO.getUserNameByUserId(posInfo.getAcceptor()))
					.append("(")
					.append(posInfo.getAcceptor())
					.append(")")
					.append("<br/><br/><br/><br/><br/><br/><br/><br/>")
					.append("<div style=\"color:gray;font-size:12px;\">本邮件由系统生成，请勿回复</div></body></html>")
					.toString()); // 邮件内容
			logger.info("->email send to->" + newApprover);
			mailService.send(mail);
		} catch (Exception e) {
			logger.error(posNo + "审批给" + newApprover + "发邮件失败", e);
		}
	}

	public void lockPosApprove(String posNo, String status, String opType) {
		approvalDAO.lockPosApprove(posNo);

		if (!"0".equals(approvalCheck(posNo, status, opType))) {
			throw new RuntimeException(
					"保全及审批记录状态异常，这可能是由于客户端重复提交产生的问题，请核实保全数据对应的状态");
		}
	}

	/**
	 * 移动保全 更新pos_approve的审批人
	 * 
	 * @param pMap
	 */
	public void updatePosApproveApprover(Map pMap) {
		approvalDAO.updatePosApproveApprover(pMap);
	}

	/**
	 * @Description: oa送审处理方法
	 * @methodName: toOaHandle
	 * @param posNo
	 * @return void
	 * @author WangMingShun
	 * @date 2015-11-24
	 * @throws
	 */
	public void toOaHandle(String policyNo, String posNo, String barcodeNo, String submitNo, 
			List<PosApproveInfo> list) {
		//提交用户信息
		User user = userService.getUser(PlatformContext.getCurrentUser());
		
		//获取esb实例
		GoaPosService goaPosService = 
			PlatformContext.getEsbContext().getEsbService(GoaPosService.class);
		
		//模板Id
		String tid = 
			PlatformContext.getSystemDefination().getAttribute("oaTemplateId");
		
		//模板版本
		String templateVersion = "1";
		
		//提交用户
		String createUser = user.getUserId();
		
		//标题
		String title = approvalDAO.queryOaTitle(posNo)
			+ "保全业务审批件(" + policyNo + ")";
		
		//内容
		String content = null;
		StringBuffer sb = new StringBuffer();
		//获取保全的受理人
		String acceptor = queryDAO.queryPosInfoRecord(posNo).getAcceptor(); 
		List<PosApproveInfo> li = approvalDAO.getPosApproveInfo(posNo, submitNo);
		for(int i=0; i<li.size(); i++) {
			PosApproveInfo pai = li.get(i);
			sb.append("审批事项：");
			if(pai.getApprover().equals(acceptor)) {
				//审批人和受理人是同一个人的时候，不显示审批事项中的内容
				sb.append("<br/>");
			} else {
				sb.append(pai.getSubmitDesc()==null?"<br/>":pai.getSubmitDesc());
			}
			sb.append("审批意见：");
			String approveDecision = "1".equals(pai.getApproveDesc())?"同意。":"";
			String approveOpnion = 
				PosUtils.isNullOrEmpty(pai.getApproveOption())?"":pai.getApproveOption();
			sb.append(approveDecision + approveOpnion).append("<br/>");
			sb.append("审批人：");
			sb.append(pai.getApproverName()+"["+pai.getApprover()+"]").append("<br/>");
			if(i != (li.size()-1))
				sb.append("<br/>");
		}
		content = sb.toString();
		
		Map<String, String> attachements = new HashMap<String,String>();
		//附件
		
		//影像资料
		List<ImageInfo> imageListTmp = null;
		try {
			imageListTmp = im.queryImageByMainIndex(barcodeNo,Constant.CONTENT_YES);
		} catch (Exception e) {
			logger.error("查询影像资料出错：" + e.getMessage());
			e.printStackTrace();
		}
		if(imageListTmp != null && imageListTmp.size() > 0) {
			int index = 0;
			for(ImageInfo image : imageListTmp) {
				String fileName = image.getDocTypeModuleName() + "." + image.getMime();
				if(attachements.containsKey(fileName)) {
					String newName = image.getDocTypeModuleName()+(index++)+"."+image.getMime();
					attachements.put(newName, image.getURL());
				} else {
					attachements.put(fileName, image.getURL());
				}
			}
		}
		//获取关联条码
		String relateBarcodeNo = queryDAO.getRelateBarcodeNo(barcodeNo);
		try {
			imageListTmp = im.queryImageByMainIndex(relateBarcodeNo,Constant.CONTENT_YES);
		} catch (Exception e) {
			logger.error("查询关联影像资料出错：" + e.getMessage());
			e.printStackTrace();
		}
		if(imageListTmp != null && imageListTmp.size() > 0) {
			int index = 100;
			for(ImageInfo image : imageListTmp) {
				String fileName = image.getDocTypeModuleName() + "." + image.getMime();
				if(attachements.containsKey(fileName)) {
					String newName = image.getDocTypeModuleName()+(index++)+"."+image.getMime();
					attachements.put(newName, image.getURL());
				} else {
					attachements.put(fileName, image.getURL());
				}
			}
		}
		
		//电话录音
		List<Map> phoneList = (List) approvalDAO.queryApprovePosInfo(posNo).get("attachments");
		for(Map m : phoneList) {
			int index = 0;
			if(attachements.containsKey(m.get("phoneFileName"))) {
				String fileName = (String) m.get("phoneFileName");
				String newName= fileName.substring(0, fileName.lastIndexOf(".")) 
					+ String.valueOf(index++)
					+ fileName.substring(fileName.lastIndexOf("."), fileName.length());
				attachements.put(newName, (String) m.get("phoneFileUrl"));
			} else {
				attachements.put((String) m.get("phoneFileName"), (String) m.get("phoneFileUrl"));
			}
		}
		
		
		//其他参数
		Map<String, String> otherParam = new HashMap<String,String>();
		//回调ESB接口
		otherParam.put("serviceId", "com.sinolife.pos.oaCallback");
		//以下为显示在oa的字段
		PosInfoDTO posInfo = queryDAO.queryPosInfoRecord(posNo);
		otherParam.put("POS_APPROVE_REMARK", posInfo.getRemark());
		otherParam.put("POS_ENT_TEXT", posInfo.getApproveText());
		
		//审批链
		List<Map<String, Object>> approverList = new ArrayList<Map<String,Object>>();
		//创建审批连
		for(PosApproveInfo pai : list) {
			Map<String, Object> m = new HashMap<String, Object>();
			//oa审批人角色 做为key,审批人 做为value
			m.put(pai.getApproveRole(), pai.getApprover());
			approverList.add(m);
		}
		
		//提交oa审批
		logger.info("======+oa start==posNo==="+posNo);
		Map<String, String> result = null;
		try{
			result = goaPosService.createFlow(tid, templateVersion, createUser, title, content, attachements, otherParam, approverList);
		}catch(Exception e){
			logger.info("======================goaPosService.createFlow ERROR:posNo="+posNo);
			e.printStackTrace();
			//throw new BizException("调用oa接口进行送审失败,请联系系统管理员！");
			throw new RuntimeException("调用oa接口进行送审失败,请联系系统管理员！");
			
		}
	
		logger.info("=======+oa end====posNo====="+posNo);
		
		//{flag=Y, msg=null, flowId=20150000000000100746}
		String flag = result.get("flag");
		String msg = result.get("msg");
		String flowId = result.get("flowId");
		
		
		if("Y".equals(flag)&& StringUtils.isNotBlank(flowId)) {
			//将flowId更新到对应的审批列表中
			approvalDAO.updateApproveOaApproveId(posNo, submitNo, flowId);
			logger.info("===========================flowId:" + flowId);
		} else {
			logger.error("===========================faild msg:" + msg);
			throw new RuntimeException("调用oa接口进行送审返回结果错误,请联系系统管理员！");
		}
	}
	
	/**
	 * @Description: 查询用户保单的oa审批记录
	 * @methodName: queryOaApproveList
	 * @param user
	 * @return
	 * @return List
	 * @author WangMingShun
	 * @date 2015-12-11
	 * @throws
	 */
	public List queryOaApproveList(String user) {
		return approvalDAO.queryOaApproveList(user);
	}
	
}
