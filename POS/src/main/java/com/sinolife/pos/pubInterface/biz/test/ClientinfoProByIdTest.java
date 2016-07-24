package com.sinolife.pos.pubInterface.biz.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.unihub.framework.util.common.DateUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sinolife.esbpos.oa.GoaPosService;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.todolist.approval.dao.ApprovalDAO;
import com.sinolife.pos.todolist.approval.service.ApprovalService;
import com.sinolife.sf.platform.runtime.PlatformContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/AppContext.xml")
public class ClientinfoProByIdTest {

	@Autowired
	private CommonQueryDAO commonQueryDAO;

	@Autowired
	// BranchAcceptService branchAcceptService;
	// @Autowired
	// private ScheduleDAO scheduleDAO;
	@Test
	public void test() {

		String c = commonQueryDAO.canDoServiceItems("P000000000095656", "1",
				"7");
		System.out.print(c);
		// List<Map<String,Object>> list=
		// developPlatformInterface.queryBatchAcceptList("junbo.ren@sino-life.com",
		// DateUtil.stringToDate("2012-8-3"),
		// DateUtil.stringToDate("2012-10-3"));
		//

		//
		// BranchAcceptService branchAcceptService=new BranchAcceptService();
		//
		//
		// Map<String, Object> retMap =
		// branchAcceptService.workflowControl("06", "E2013000000013103284",
		// false);
		//
		// System.out.print((String) retMap.get("flag"));
		//
		//
		// boolean success = false;
		//
		// try {
		// //logger.info("processing ... " + posNo);
		// boolean locked =
		// scheduleDAO.lockPosInfoRecord("E2013000000013103284");
		// if(locked && scheduleDAO.verifyAcceptStatus("E2013000000013103284",
		// "A10")) {
		// //logger.info("lock pos_info->" + posNo + " success.");
		// Map<String, Object> retMap =
		// branchAcceptService.workflowControl("06", "E2013000000013103284",
		// false);
		// //logger.info("workflowControl return:" + retMap);
		// String flag = (String) retMap.get("flag");
		// if("A12".equals(flag)) {
		// //已送人工核保
		// success = true;
		// }else if("A15".equals(flag)) {
		// //待收费
		// success = true;
		// } else if("E01".equals(flag) || "A17".equals(flag)) {
		// //生效完成 或 待保单打印
		// success = true;
		// }
		// } else {
		// //logger.info("lock -> " + posNo + " failed");
		// }
		// } catch(Exception e) {
		// //logger.info("process -> " + posNo + " caught an exception", e);
		// e.printStackTrace();
		// }

		// ApplicationContext applicationContext = new
		// FileSystemXmlApplicationContext(
		// "D:\\sinopower\\workspace\\SL_POS_0.4.0\\src\\main\\webapp\\WEB-INF\\spring\\AppContext.xml");
		// BranchAcceptService branchAcceptService = (BranchAcceptService)
		// applicationContext
		// .getBean("branchAcceptService");
		//
		// Map<String, Object> retMap =
		// branchAcceptService.workflowControl("06",
		// "E2013000000013108387", false);
		// // logger.info("workflowControl return:" + retMap);
		// String flag = (String) retMap.get("flag");
		//
		// System.out.println(flag);

		Date d1 = DateUtil.stringToDate("2013-10-1 01:1:1",
				"yyyy-MM-dd hh:mm:ss");

		Date d2 = DateUtil.stringToDate("2013-10-1 01:2:1",
				"yyyy-MM-dd hh:mm:ss");

		System.out.println(DateUtil.daysBetween(d1, d2));
	}

	public static void main(String args[]) {
		ApplicationContext applicationContext = new FileSystemXmlApplicationContext(
				"D:\\sinopower\\workspace1\\SL_POS_4\\src\\main\\webapp\\WEB-INF\\spring\\AppContext.xml");
		// UndwrtRuleCheckJob undwrtRuleCheckJob
		// =(UndwrtRuleCheckJob)applicationContext.getBean("undwrtRuleCheckJob");
		// undwrtRuleCheckJob.execute();

		// MccpPosAcceptInterfaceImpl mccpPosAcceptInterface
		// =(MccpPosAcceptInterfaceImpl)applicationContext.getBean("MccpPosAcceptInterface");
		// mccpPosAcceptInterface.getDifClientPhoneInfoByPhoneNo("C00001563562","13089994840");
		// ReceiptManageDAO dpi = (ReceiptManageDAO)
		// applicationContext.getBean("receiptManageDAO");
		// PosAppointmentSyncJob cas =
		// (PosAppointmentSyncJob)applicationContext.getBean("posAppointmentSyncJob");
		ApprovalService dpi = (ApprovalService)
		 applicationContext.getBean("approvalService");
		ApprovalDAO approvalDAO = (ApprovalDAO)
		applicationContext.getBean("approvalDAO");
		// boolean b = PosUtils.isNotNullOrEmpty("123");
		// dpi.surrenderOperation("P000000000128611","2015-9-9");
		// cas.updateAppointmentDetailStatusCode("E2015000000028259869");
		// cas.execute();
		// dpi.updateReceiptSource("P000000009632921", "1");
//		GoaPosService goaPosService = PlatformContext.getEsbContext()
//				.getEsbService(GoaPosService.class);

		// goaPosService.createFlow("123", "123", "wms", "test", "contentText",
		// attachements, otherParam, approverList)
		//goaPosService.getApproveHistory("20150000000000094784");
		handle1();
//		List<PosApproveInfo> list = 
//			approvalDAO.getPosApproveInfo("E2015000000029055289", "1", "1");
//		dpi.toOaHandle("E2015000000029055289", list);

		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = sf.parse("2015-6-22");
			endDate = sf.parse("2016-1-13");
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// List<Map<String, Object>> list =
		// dpi.getFinValueInfoList("P000000009555370", startDate, endDate);
		// for(Map map: list) {
		// System.out.println(map.get("calcDate"));
		// }
		// List<Map<String, Object>> list =
		// dpi.getPolicyPwmInfo("0330002990697932");
		// String s = dpi.getPolicyIsPwm("0330002990697932");

		// Map<String, Object> map =
		// dpi.queryBranchCheckCode("P000000009561952", "E2015000000033373448");

		// EDCXC01SLICJS1501438943547426176
		// EDCXC01SLICJS1501438943547426176
		// dpi.posRegister("E2015000000033373448",
		// "EDCXC01SLICJS1501438943547426176");

		// Map<String, Object> map = new HashMap<String, Object>();
		// DateUtils.formatDate(new Date(), "yyyy-MM-dd hh:mm:ss");
		// String bkAcctTime = DateUtils.formatDate(new Date(),
		// "yyyy-MM-dd hh:mm:ss");
		// map.put("TRAN_CODE", "SurrAllRealVerify");
		// map.put("PARTNER_ID", "web_alipay");
		// map.put("BK_ACCT_TIME", bkAcctTime);
		// map.put("POLICYNO", "P000000005255188");
		// map.put("OPERATION_TYPE", "BEFOREHAND_REDEEM"); //
		// BEFOREHAND_REDEEM:提前退保
		// // CANCEL_REDEEM:犹豫期退保
		// Map<String, Object> resultMap = null;
		try {

			// CommonQueryDAO commonQueryDAO=new CommonQueryDAO();
			// String c=
			// commonQueryDAO.canDoServiceItems("P000000000095656","1","7");

			// commonQueryDAO.getProjectCodeByPolicyNo("P000000000095656");
			// System.out.print(c);
			// ImageEventReceive imageEventReceive = (ImageEventReceive)
			// applicationContext
			// .getBean("ImageEventReceive");
			//
			//
			// imageEventReceive.receiveImageMessage(null, "1194010000018110",
			// null, null, null, null, null);

			// WebPosService webPosService = PlatformContext.getEsbContext()
			// .getEsbService(WebPosService.class);
			// String s=webPosService.getUserMobileByClientNo("fsdfs");
			// System.out.println();
			// BiabPosService biabPosService = PlatformContext.getEsbContext()
			// .getEsbService(BiabPosService.class);
			// resultMap = biabPosService.handleBiagESBRequestMap(map);
			// resultMap = new HashMap<String, Object>();
			// resultMap.put("SL_RSLT_CODE", "999999");
			// resultMap.put("SL_RSLT_MESG", "");
		} catch (Exception e) {
			e.printStackTrace();
			// logger.error("Get policy applyinfo failed!",e);
			// model.addAttribute("failMsg", "发送支付宝验证操作异常！");
			// return "/Pos/Change/approve_result";
		}
		// String resultCode = (String) resultMap.get("SL_RSLT_CODE");
		// logger.info("policyNo={},serviceItem={},SL_RSLT_CODE={},SL_RSLT_MESG={}",
		// new Object[]{policyNo,serviceItem,resultCode,(String)
		// resultMap.get("SL_RSLT_MESG")});
		// if (!"999999".equals(resultCode)) {
		// if("0101".equals(resultCode)) {
		// //TODO 包装错误信息
		// model.addAttribute("failMsg", "对不起，您的资产在支付宝不存在。");
		// }else if("0102".equals(resultCode)) {
		// model.addAttribute("failMsg", "对不起，您的资产在支付宝已冻结。");
		// }else if("0103".equals(resultCode)) {
		// model.addAttribute("failMsg", "对不起，您的资产已到期。");
		// }else if("0104".equals(resultCode)) {
		// model.addAttribute("failMsg", "对不起，您的资产已在支付宝申请退保。");
		// }else if("9998".equals(resultCode)) {
		// model.addAttribute("failMsg", "退保申请正在处理中。");
		// }else if("9999".equals(resultCode)) {
		// model.addAttribute("failMsg", "其他错误。");
		// }else if("800001".equals(resultCode)) {
		// model.addAttribute("failMsg", "支付宝没有数据返回。");
		// }else {
		// model.addAttribute("failMsg", "发送支付宝验证操作失败！");
		// }

		// }

	}

	// BranchAcceptService branchAcceptService = (BranchAcceptService)
	// applicationContext
	// .getBean("BranchAcceptService");

	// WebPosAcceptInterface webPosAcceptInterface = (WebPosAcceptInterface)
	// applicationContext
	// .getBean("WebPosAcceptInterface");
	//
	// Double d = webPosAcceptInterface.calcInitialExpenses("0106302080001027",
	// "100000");

	// CcsPosAcceptInterface ccsPosAcceptInterface
	// =(CcsPosAcceptInterface)applicationContext.getBean("CcsPosAcceptInterface");
	// String seq= ccsPosAcceptInterface.generateCCSBarcode();

	// CommonQueryDAO commonQueryDAO=new CommonQueryDAO();
	// ImageService imageService
	// =(ImageService)applicationContext.getBean("WebPosAcceptInterface1");
	// List l= webPosAcceptInterface.getCanDoPolicys(null, "2");
	//
	// System.out.print(l.size());
	//
	// List li =new ArrayList();
	// li.add("0020001040002968");
	// li.add("0020001040002955");
	// l= webPosAcceptInterface.getCanDoPolicys(li, "2");
	// System.out.print(l.size());
	//
	// l= webPosAcceptInterface.getCanDoPolicys(null, "23");
	// System.out.print(l.size());
	//
	// l= webPosAcceptInterface.getCanDoPolicys(null, "35");
	// System.out.print(l.size());
	//
	// li =new ArrayList();
	// li.add("0020001040002968");
	// li.add("0020001040002955");
	// l= webPosAcceptInterface.getCanDoPolicys( li, "35");
	// System.out.print(l.size());
	// li.add("0020001040002968");
	// li.add("0020001040002955");
	// l= webPosAcceptInterface.getCanDoPolicys(li, "23");
	//
	// Map map=new HashMap();
	// map.put("policyNosList", null);
	// l= webPosAcceptInterface.getCanDoPolicys(li, "23");
	// commonQueryDAO.getWebAcceptPolicys(map);
	// System.out.print(l.size());

	// imageService.aferScan("210101004071001");
	
	public static void handle1() {
//		String tid = "20150000000001595142";
//		String tid = "20150000000001749921";
		String tid = "20150000000000099848";
//		String tid = "112122";
		String templateVersion = "1";
		String createUser = "junbo.ren@sino-life.com";
		String title = "测试";
		String content = "测试数据";
		Map attachements = new HashMap();
		Map otherParam = new HashMap();
//		otherParam.put("POS_APPROVE_REMARK", "显示");
//		otherParam.put("POS_ENT_TEXT", "怎么不显示呢？？？？？");
		List approverList = new ArrayList();
		Map m = new HashMap();
		m.put("CCS_MANAGER", "pos02hb.m@sino-life.com");
		approverList.add(m);
		m = new HashMap();
		m.put("CCS_VICE_LEADER", "pos02hb.vl@sino-life.com");
		approverList.add(m);
		m = new HashMap();
		m.put("CCS_LEADER", "pos02hb.l@sino-life.com");
		approverList.add(m);
		GoaPosService goaPosService = PlatformContext.getEsbContext()
			.getEsbService(GoaPosService.class);
//		Map map = null;
//		map = goaPosService.createFlow(tid, templateVersion, createUser, title, content, 
//				attachements, otherParam, approverList);
//		String flowId = (String) map.get("flowId");
//		System.out.println("+=====================================================>>");
//		System.out.println(flowId);
		List<Map<String, Object>> list = goaPosService.getApproveHistory("20150000000000101403");
		for(Map mm : list) {
			System.out.println("approveTime:"+mm.get("approveTime"));
			System.out.println("oaFlowApprover:"+mm.get("oaFlowApprover"));
			System.out.println("oaFlowApproverId:"+mm.get("oaFlowApproverId"));
			System.out.println("oaFlowNode:"+mm.get("oaFlowNode"));
			System.out.println("oaFlowOpinion:"+mm.get("oaFlowOpinion"));
			System.out.println("oaFlowRemark:"+mm.get("oaFlowRemark"));
			System.out.println("realOperator:"+mm.get("realOperator"));
			System.out.println("realOperatorId:"+mm.get("realOperatorId"));
			System.out.println("taskId:"+mm.get("taskId"));
			System.out.println("taskOpinionCode:"+mm.get("taskOpinionCode"));
			System.out.println("taskStatus:"+mm.get("taskStatus"));
			System.out.println("taskStep:"+mm.get("taskStep"));
			System.out.println("-----------------------------");
		}
		
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}
}
