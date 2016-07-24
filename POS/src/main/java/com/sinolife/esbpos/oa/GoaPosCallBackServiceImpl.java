package com.sinolife.esbpos.oa;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.esbpos.product.ProductService;
import com.sinolife.pos.acceptance.branch.service.BranchAcceptService;
import com.sinolife.pos.common.dao.CommonAcceptDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO;
import com.sinolife.pos.common.dto.PosApproveInfo;
import com.sinolife.pos.common.dto.PosStatusChangeHistoryDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.others.sms.dao.SmsDAO;
import com.sinolife.pos.todolist.approval.dao.ApprovalDAO;
import com.sinolife.sf.config.RuntimeConfig;
import com.sinolife.sf.esb.EsbMethod;
import com.sinolife.sf.framework.comm.BizException;
import com.sinolife.sf.platform.runtime.PlatformContext;

@Service("goaPosCallBackServiceImpl")
public class GoaPosCallBackServiceImpl implements GoaPosCallBackService {
	
	private static Logger logger = Logger.getLogger("GoaPosCallBackServiceImpl.class");
	
	@Autowired
	private ApprovalDAO approvalDAO;
	
	@Autowired
	private CommonQueryDAO queryDAO;
	
	@Autowired
	private CommonAcceptDAO acceptDAO;
	
	@Autowired
	SmsDAO smsDAO;
	
	@Autowired
	BranchAcceptService acceptService;

	/**
     * OA审批结果回调接口
     * @param flowId 流程Id
     * @param templateId 模板Id
     * @param templateVersion 模板版本
     * @param opinion 是否审批通过(Y/N)
     * @param remark  具体审批说明
     * @param createFlowParam 创建流程的参数
     * 				createUser:junbo.ren 创建人
					serviceId:com.sinolife.pos.oaCallback 回调esb服务id
					prehandler:junbo.ren
					currentUserId:pos02hb.m@sino-life.com
					createrMail:junbo.ren
					approveCreateUser:junbo.ren
     * @param history 审批历史
     */
	@EsbMethod(esbServiceId="com.sinolife.pos.oaCallback")
	@Transactional
	public void callBackFlowOver(String flowId, String templateId,
			String templateVersion, String opinion, String remark,
			Map<String, Object> createFlowParam,
			List<Map<String, Object>> history) {
		
		logger.info("=======GoaPosCallBackServiceImpl callback sucess.start=======flowid:"+flowId);
		logger.info("========================opinion:"+opinion+"remark:"+remark+"====date:"+(new Date()));
		
		for(int j=0;j<history.size();j++){
			logger.info("===========================histroy======"+history.get(j));
		}
		
		//根据flowId查询pos_approve的信息
		PosApproveInfo posApproveInfo = new PosApproveInfo();
		try {
			posApproveInfo = approvalDAO.getPosApproveInfoByFlowId(flowId).get(0);
		} catch (Exception e) {
			throw new BizException("根据审批id"+flowId+"查询不到待审批任务。");
		}
		
		logger.info("==OA=======posNo======"+posApproveInfo.getPosNo());
		
		String posNo = posApproveInfo.getPosNo();
		PosStatusChangeHistoryDTO change = new PosStatusChangeHistoryDTO();
		change.setPosNo(posApproveInfo.getPosNo());
		change.setChangeDate(queryDAO.getSystemDate());
		change.setChangeUser(PlatformContext.getCurrentUser());
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("posNo", posNo);
		ServiceItemsDAO serviceItemsDAO = (ServiceItemsDAO) PlatformContext
			.getApplicationContext().getBean("serviceItemsDAO_" + "23");
		ServiceItemsInputDTO serviceItemsInputDTO = serviceItemsDAO
			.queryCommonAcceptDetailInput(posNo);
		
		//更新pos_approve表对应的信息
		for(Map<String, Object> m : history) {
			//如果taskStep为-1则是送审者，为空可能是征询别人意见，不属于审批链。并且审批时间不为空就更新表。
			if(!"".equals(m.get("taskStep")) && !"-1".equals(m.get("taskStep"))
					&& !"".equals(m.get("approveTime"))) {
				Map<String, Object> map = new HashMap<String, Object>();
				Date approveTime = null;
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					approveTime = sdf.parse((String) m.get("approveTime"));
				} catch (ParseException e) {
					logger.error("======日期格式错误：必须为\"yyyy-MM-dd HH:mm:ss\"方可更新。approveTime:"+m.get("approveTime"));
					e.printStackTrace();
				}
				map.put("approveTime", approveTime);
				map.put("approveDecision", "Y".equals(m.get("taskOpinionCode"))?"1":"2");
				map.put("approveOpnion", m.get("oaFlowRemark"));
				map.put("approver", m.get("userId") + "@sino-life.com");
				map.put("flowId", flowId);
				map.put("posNo", posNo);
				try {
					approvalDAO.updatePosApprove(map);
				} catch (Exception e) {
					logger.error("=============审批表pos_approve相关信息更新失败！！！");
					e.printStackTrace();
				}
			}
			logger.info("=============审批表pos_approve相关信息更新完毕");
		}
		
		if("Y".equals(opinion)) {
			logger.info("==OA=========所有审批人都同意========start");
			//审批通过
			
			//推动流程前进
			pMap.put("acceptStatus", "A10");// 状态走两步，到待投保、核保规则检查状态
			approvalDAO.updatePosApproved(pMap);

			change.setOldStatusCode("A08");
			change.setNewStatusCode("A09");
			acceptDAO.insertPosStatusChangeHistory(change);
			
			// 移动保全审批处理（调用短信接口）
			if ("11".equals(serviceItemsInputDTO.getAcceptChannel())) {
				logger.info("=====进入移动保全审批处理========start");
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
				logger.info("=====进入移动保全审批处理========end");
			} else {
				logger.info("===========正常处理流==start");
				change.setOldStatusCode("A09");
				change.setNewStatusCode("A10");
				acceptDAO.insertPosStatusChangeHistory(change);
				logger.info("===========正常处理流==end");
			}
			logger.info("==OA=========所有审批人都同意========end");
		} else {
			logger.info("==OA=========审批链中有人不同意========start");
			//审批不通过
			//推动流程前进
			if ("11".equals(serviceItemsInputDTO.getAcceptChannel())) {
				logger.info("==受理渠道是移动保全，则保全流程结束,不在创建新的审批流==start");
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
				if ("11".equals(serviceItemsInputDTO.getAcceptChannel())) {
					String noPassReason = (String) pMap.get("remark");
					smsDAO.sendAgentMposSms(posNo, "09", noPassReason);
				}
				logger.info("==受理渠道是移动保全，则保全流程结束,不在创建新的审批流==end");
			} else {
				logger.info("==oa系统审批链上有人不同意,开启一个新的审批流程==start");
				approvalDAO.approveAgainAll(posNo);//开启一个新的审批流程
//				String acceptor = queryDAO.queryPosInfoRecord(posNo).getAcceptor();
//				acceptService.cancelAndReaccept(posNo, "24",
//						"oa系统审批链上有人不同意，需要重新生成受理", acceptor);
				logger.info("==oa系统审批链上有人不同意,开启一个新的审批流程==end");
			}
			logger.info("==OA=========审批链中有人不同意========end");
		}
	}

}
