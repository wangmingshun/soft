package com.sinolife.pos.posappointmentsync;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.unihub.framework.util.common.DateUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinolife.pos.acceptance.branch.service.BranchAcceptService;
import com.sinolife.pos.acceptance.branch.service.BranchQueryService;
import com.sinolife.pos.common.dao.ClientInfoDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_2;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_9;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.common.dto.PosApplyFilesDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.PosProblemItemsDTO;
import com.sinolife.pos.common.dto.PosStatusChangeHistoryDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_2;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_9;
import com.sinolife.pos.posappointmentsync.dao.AppointmentSyncDAO;
import com.sinolife.pos.posappointmentsync.dao.CommonAppointmentSyncDAO;
import com.sinolife.pos.print.service.PrintService;
import com.sinolife.pos.pubInterface.biz.dto.PosApplyInfoDTO;
import com.sinolife.pos.pubInterface.biz.impl.DevelopPlatformInterfaceImpl;
import com.sinolife.pos.todolist.problem.dao.ProblemDAO;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSBOMRuleInfoDto;
import com.sinolife.sf.ruleengine.pos.bom.dto.POSVerifyResultDto;

@Service("posAppointmentSyncImpl")
public class PosAppointmentSyncImpl extends DevelopPlatformInterfaceImpl {
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private CommonQueryDAO commonQueryDAO;
	@Autowired
	private AppointmentSyncDAO appointmentSyncDAO;
	@Autowired
	private ServiceItemsDAO_2 serviceItemsDAO_2;
	@Autowired
	private ServiceItemsDAO_9 serviceItemsDAO_9;
	@Autowired
	private ClientInfoDAO clientInfoDAO;
	@Autowired
	private BranchQueryService branchQueryService;
	@Autowired
	private BranchAcceptService branchAcceptService;
	@Autowired
	private PrintService printService;
	@Autowired
	private CommonAppointmentSyncDAO commonAppointmentSyncDAO; 
	@Autowired
	private ProblemDAO problemDAO;
	
	
	/**
	 * @Description: 预约保全项目统一处理入口
	 * @methodName: surrenderOperation
	 * @param posNo
	 * @return void
	 * @author WangMingShun
	 * @date 2015-8-31
	 * @throws
	 */
	public Map<String, Object> handleOperation(String posNo, String applyDate,
			String serviceItems) {
		Map<String, Object> result = new HashMap<String, Object>();
		//从数据库获取PosApplyInfoDTO相关信息
		Map<String, Object> pMap = appointmentSyncDAO.getAppointmentInfo(posNo);
		//设置PosApplyInfoDTO的值
		PosApplyInfoDTO applyInfo = new PosApplyInfoDTO();
		MapToBean(applyInfo, pMap);
		//此处判断获取到的serviceItems和传递进来的是否一致，不一致说明数据有问题。
		if(serviceItems != null && !serviceItems.equals(applyInfo.getServiceItems())) {
			logger.error("两次获取的保全项目不相同，数据异常。定时任务serviceItems:" + serviceItems 
					+ ",内部调用获取的serviceItems:" + applyInfo.getServiceItems());
			throw new RuntimeException("两次获取的保全项目不相同，数据异常。定时任务serviceItems:" + serviceItems 
					+ ",内部调用获取的serviceItems:" + applyInfo.getServiceItems());
		}
		applyInfo.setApplyType("1");//默认为1
		applyInfo.setApplyDate(DateUtil.stringToDate(applyDate,"yyyy-MM-dd"));
		applyInfo.setApprovalServiceType("1");
		applyInfo.setPaymentType("2");
		try {
			ServiceItemsInputDTO sii = new ServiceItemsInputDTO();
			logger.info("保全项目：" + (String) pMap.get("serviceItemsDesc") + "start。"
					+ "AppoPosNo:" +  posNo);
			//此处进行保全项目的判断，决定调用某个经办信息接口
			if("2".equals(applyInfo.getServiceItems())) {
				//退保
				sii = queryServiceItemsInfoExtra_2(applyInfo);
			} else if ("9".equals(applyInfo.getServiceItems())) {
				//还款
				sii = queryServiceItemsInfoExtra_9(applyInfo);
			} else {
				logger.error("指定的保全项目没有开启预约保全功能！请核实处理！");
				throw new RuntimeException("指定的保全项目没有开启预约保全功能！请核实处理！");
			}
			sii.setApplyDate(applyDate);
			sii.setAcceptDate(applyDate);
			result = acceptInternal(sii, applyInfo, posNo);//调用受理
		} catch (Exception e) {
			//出现异常或者规则不通过，则发送邮件给受理人
			logger.info("开始发送保全失败邮件。。。。。。。。。。。。。。。。。");
			sendMail(applyInfo, (String) pMap.get("serviceItemsDesc"), e.getMessage());
			logger.info(e.getMessage()+"发送完成。。。。。。。。。。。。。。。。。。。。。。。。");
		}
		return result;
	}
	
	/**
	 * @Description: 退保经办信息录入
	 * @methodName: queryServiceItemsInfoExtraToPosAppointment_2
	 * @param map
	 * @return
	 * @return ServiceItemsInputDTO
	 * @author WangMingShun
	 * @date 2015-9-1
	 * @throws
	 */
	public ServiceItemsInputDTO queryServiceItemsInfoExtra_2(PosApplyInfoDTO pai) {
		boolean ishaveprd = false;
		ServiceItemsInputDTO_2 items2 = new ServiceItemsInputDTO_2();
		items2.setPolicyNo(pai.getPolicyNo());
		items2.setServiceItems("2");
		items2.setApplyDate(DateUtil.dateToString(pai.getApplyDate(),"yyyy-MM-dd"));//申请时间
		items2 = (ServiceItemsInputDTO_2) serviceItemsDAO_2
					.queryServiceItemsInfoExtra_2(items2);
		items2.setAllSurrender(true);//整单退保
		items2.setDouBt(false);//是否可疑交易
		items2.setSurrenderCause("");// 退保原因
		List<PolicyProductDTO> productList = items2.getProductList();
		for (int i = 0; productList != null && i < productList.size(); i++) {
			PolicyProductDTO product = productList.get(i);
			// 险种为有效 或者 失效原因为[10, 7, 9, 8, 13, 11, 12] 才能退保
			if ("1".equals(product.getDutyStatus())|| 
					("2".equals(product.getDutyStatus())
							&& ("7".equals(product.getLapseReason()))
							|| "8".equals(product.getLapseReason())
							|| "9".equals(product.getLapseReason())
							|| "10".equals(product.getLapseReason())
							|| "11".equals(product.getLapseReason())
							|| "12".equals(product.getLapseReason()) 
							|| "13".equals(product.getLapseReason()))) {
				product.setSelected(true);
				ishaveprd = true;
			}
		}
		if (!ishaveprd) {
			throw new RuntimeException("该保单没可操作该保全项目的险种");
		}
		items2.setProductList(productList);
		return items2;
	}
	
	public ServiceItemsInputDTO queryServiceItemsInfoExtra_9(PosApplyInfoDTO pai) {
		ServiceItemsInputDTO_9 items9 = new ServiceItemsInputDTO_9();
		items9.setPolicyNo(pai.getPolicyNo());
		items9.setServiceItems("9");
		items9.setApplyDate(DateUtil.dateToString(pai.getApplyDate(),"yyyy-MM-dd"));//申请时间
		items9 = serviceItemsDAO_9.queryServiceItemsInfo(items9);
//		String dutyStatus = commonQueryDAO.getPolicyDutyStatus(pai.getPolicyNo());
//		if(!"1".equals(dutyStatus)) {
//			throw new RuntimeException("保单失效，无法完成还款！");
//		}
		return items9;
	}
	
	
	/**
	 * @Description: 保全受理，从申请书录入一直到生效的全部过程都在这里（传入Bean）
	 * @methodName: acceptInternal
	 * @param sii1
	 * @param applyInfo
	 * @param AppoPosNo  预约表的posNo
	 * @return
	 * @return Map<String,Object>
	 * @author WangMingShun
	 * @date 2015-9-1
	 * @throws
	 */
	public Map<String, Object> acceptInternal(final ServiceItemsInputDTO sii,
			final PosApplyInfoDTO applyInfo, String appoPosNo) {

		Map<String, Object> retMap = new HashMap<String, Object>();

		//写入批次记录、申请书记录，及保全记录
		PosApplyBatchDTO batch = createBatch(applyInfo);
		//获取预约保全的相关信息，关联条码
		String relateBarcode = commonQueryDAO.queryPosInfoRecord(appoPosNo).getBarcodeNo();
		//由于不在同一个批次，所以该条码会被检测为无效关联条码，所以需要单独更新
		PosApplyFilesDTO applyFiles = batch.getApplyFiles().get(0);
		branchAcceptService.updatePosApplyFiles(applyFiles.getBarcodeNo(), 
				"RELATE_BARCODE", relateBarcode);
		PosInfoDTO posInfo = new PosInfoDTO();
		//获取新生成的保全号
		String posNo = batch.getPosInfoList().get(0).getPosNo();
		//更新预约表的预约保全号
		commonAppointmentSyncDAO.updateAppointmentAppointedPosNo(appoPosNo, posNo);
		sii.setPosNo(posNo);
		sii.setPosNoList(null);
		sii.setBarcodeNo(batch.getApplyFiles().get(0).getBarcodeNo());
		sii.setBatchNo(batch.getPosBatchNo());
		ServiceItemsInputDTO oriItemsInputDTO = branchQueryService
				.queryAcceptDetailInputByPosNo(posNo);
		sii.setOriginServiceItemsInputDTO(oriItemsInputDTO
				.getOriginServiceItemsInputDTO());
		sii.setClientNo(applyInfo.getClientNo());
			
		// 写受理明细
		branchAcceptService.acceptDetailInput(sii);
		List<String> posNoList = sii.getPosNoList();
		for (int i = 0; i < posNoList.size(); i++) {
			posNo = posNoList.get(i);
			// 更新备注字段
			branchAcceptService.updatePosInfo(posNo, "remark", sii.getRemark());
			// 写状态变迁记录
			PosStatusChangeHistoryDTO statusChange = new PosStatusChangeHistoryDTO();
			statusChange.setPosNo(posNo);
			statusChange.setOldStatusCode("A01");
			statusChange.setNewStatusCode("A03");
			statusChange.setChangeDate(new Date());
			statusChange.setChangeUser(batch.getAcceptor());
			branchAcceptService.insertPosStatusChangeHistory(statusChange);
			if (!"0".equals(statusChange.getFlag())) {
				String SL_RSLT_MESG = "写保全状态变更记录失败：" + statusChange.getMessage();
				branchAcceptService.cancelAccept(posInfo, batch.getAcceptor());
				//写问题件表
				createProblem(posNo, PlatformContext.getCurrentUser(), "3", SL_RSLT_MESG);
				throw new RuntimeException(SL_RSLT_MESG);
			}

			// 这些渠道都是逐单受理，因此前一单受理完成，更新状态为 A03待处理规则检查
			branchAcceptService.updatePosInfo(posNo, "accept_status_code", "A03");

			// 调用流转接口
			retMap = branchAcceptService.workflowControl("03", posNo, false);
			logger.info("acceptDetailInputSubmit workflowControl return:" + retMap);
			String flag = (String) retMap.get("flag");
			String msg = (String) retMap.get("message");
			String resultDesc = (String) retMap.get("resultDesc");
			retMap = branchQueryService.queryProcessResult(posNo);
			
			if ("C01".equals(flag)) {
				// 规则检查不通过
				//写问题件表
				createProblem(posNo, PlatformContext.getCurrentUser(), "3", msg);
				throw new RuntimeException(msg);
			} else if ("A19".equals(flag)) {
				// 待审批判断处理
				retMap = branchAcceptService.workflowControl("05", posNo, false);
				posInfo = commonQueryDAO.queryPosInfoRecord(posNo);
				logger.info("acceptDetailInputSubmit workflowControl return:" + retMap);
				flag = (String) retMap.get("flag");
				msg = (String) retMap.get("message");
				resultDesc = (String) retMap.get("resultDesc");
			} else if ("A08".equals(flag) || "A12".equals(flag)
					|| "A15".equals(flag) || "A16".equals(flag)
					|| "E01".equals(flag)) {
				//A19 处理后这些状态下，不做任何处理
			} else {
				//不在以上状态的,写入问题件表中
				createProblem(posNo, PlatformContext.getCurrentUser(), "3", msg);
				throw new RuntimeException(msg);
			}
		}
		
		return retMap;
	}
	
	/**
	 * @Description: 写入批次记录、申请书记录，及保全记录
	 * @methodName: createBatch
	 * @param applyInfo
	 * @param appoPosNo 预约表的posNo,可根据其查到预约生成的pos_info信息
	 * @return
	 * @return PosApplyBatchDTO
	 * @author WangMingShun
	 * @date 2015-9-1
	 * @throws
	 */
	private PosApplyBatchDTO createBatch(final PosApplyInfoDTO applyInfo) {
		List<ClientInformationDTO> clientInfoList = clientInfoDAO
			.selClientinfoForClientno(applyInfo.getClientNo());
		if (clientInfoList == null || clientInfoList.isEmpty()
				|| clientInfoList.size() != 1)
			throw new RuntimeException("无效的保单号：" + applyInfo.getClientNo());
		String applyType = applyInfo.getApplyType();//申请类型 
		String acceptChannelCode = applyInfo.getAcceptChannelCode();
		String approvalServiceType = applyInfo.getApprovalServiceType();
		String paymentType = applyInfo.getPaymentType();
		String clientNo = applyInfo.getClientNo();// 投保人
		String policyNo = applyInfo.getPolicyNo();
		String barcode = applyInfo.getBarCode();
		String accountNoType = applyInfo.getAccountNoType();
		// 预约保全受理的保全的条形码生成
		barcode = generateAppoBarcode(applyInfo.getServiceItems());
		
		List<String> validPolicyNoList = branchQueryService
				.queryPolicyNoListByClientNo(clientNo);
		if (validPolicyNoList == null || validPolicyNoList.isEmpty()) {
			throw new RuntimeException("找不到该保单的客户");
		}
		if (!validPolicyNoList.contains(policyNo)) {
			throw new RuntimeException("申请保单不为客户作为投保人或被保人的保单");
		}
		
		PosApplyBatchDTO batch = new PosApplyBatchDTO();
		batch.setClientNo(clientNo);
		batch.setClientInfo(clientInfoList.get(0));
		batch.setAcceptChannelCode(acceptChannelCode);
		batch.setAcceptor(applyInfo.getAcceptor());
		batch.setCounterNo("905");
		batch.setApplyTypeCode(applyType);
		
		// 默认申请类型为亲办
		if (applyType==null||"".equals(applyType)||StringUtils.isBlank(applyType)){
			batch.setApplyTypeCode("1");
		}
		if ("2".equals(applyType) || "3".equals(applyType)
				|| "4".equals(applyType)) {
			batch.setRepresentor(applyInfo.getRepresentor());
			batch.setRepresentIdno(applyInfo.getRepresentIdno());
			batch.setIdType(applyInfo.getIdType());
		}
		
		List<PosApplyFilesDTO> files = new ArrayList<PosApplyFilesDTO>();
		PosApplyFilesDTO file = new PosApplyFilesDTO();
		file.setBarcodeNo(barcode);// 条形码
		file.setPolicyNo(policyNo);
		file.setApplyDate(applyInfo.getApplyDate());
		file.setForeignExchangeType("1");
		file.setServiceItems(applyInfo.getServiceItems());
		file.setPaymentType(paymentType);
		file.setAccountNoType(accountNoType);
		if (accountNoType == null || "".equals(accountNoType)) {

			file.setAccountNoType("1");
		}

		file.setTransferAccountOwnerIdType(applyInfo.getTransferAccountOwnerIdType());// 证件类型
		file.setTransferAccountOwnerIdNo(applyInfo.getTransferAccountOwnerIdNo());// 证件号码
		file.setTransferAccountOwner(applyInfo.getTransferAccountOwner());// 收款人姓名
		file.setBankCode(applyInfo.getBankCode());//开户银行代码
		file.setTransferAccountno(applyInfo.getTransferAccountno());//账号
		file.setApprovalServiceType(approvalServiceType);
		if (file.getApplyDate() == null) {
			file.setApplyDate(commonQueryDAO.getSystemDate());
		}
		if (approvalServiceType==null||"".equals(approvalServiceType)||StringUtils.isBlank(approvalServiceType)) {
			file.setApprovalServiceType("1");// 自领
		}
		if ("2".equals(approvalServiceType)) {
			file.setAddress(applyInfo.getAddress());
			file.setZip(applyInfo.getZip());
		}
		if (StringUtils.isBlank(paymentType)) {
			file.setPaymentType("1");// 现金
		}
		
		files.add(file);
		batch.setApplyFiles(files);

		// 生成受理
		branchAcceptService.generatePosInfoList(batch, false);

		// 校验是否有规则检查不通过的
		for (PosInfoDTO posInfo : batch.getPosInfoList()) {
			POSVerifyResultDto verifyResult = posInfo.getVerifyResult();
			if (verifyResult != null && verifyResult.getRuleInfos().size() != 0) {
				StringBuilder sb = new StringBuilder("保单号[");
				sb.append(posInfo.getPolicyNo());
				sb.append("]申请");
				sb.append(posInfo.getServiceItemsDesc());
				sb.append("项目受理规则检查不通过:");
				for (POSBOMRuleInfoDto ruleInfo : verifyResult.getRuleInfos()) {
					sb.append(ruleInfo.getDescription() + "；");
				}
				//throw new RuntimeException(sb.toString());
			}
		}

		// 生成捆绑顺序
		branchAcceptService.generateBindingOrder(batch);

		// 写受理
		batch = branchAcceptService.batchAccept(batch);		
		
		return batch;
	}
	
	/**
	 * 生成Appo用的条码
	 * 
	 * @param serviceItems
	 * @return
	 */
	private String generateAppoBarcode(String serviceItems) {
		// 条形码统一改为sequence生成方式
		return "APPO" + commonQueryDAO.queryBarcodeNoSequence();
	}
	
	/**
	 * @Description: 发送电子邮件接口
	 * @methodName: sendMail
	 * @param applyInfo
	 * @param serviceItem 保全项目名称
	 * @param msg	失败消息
	 * @return void
	 * @author WangMingShun
	 * @date 2015-9-2
	 * @throws
	 */
	public void sendMail(PosApplyInfoDTO applyInfo, String serviceItemsDesc, String msg) {
		//失败给受理人发送邮件
		String content = "尊敬的" + applyInfo.getAcceptor() + "用户:" 
		               + "<br/>&nbsp;&nbsp;&nbsp;&nbsp;您于" 
		               + DateUtil.dateToString(applyInfo.getApplyDate(),"yyyy-MM-dd") 
		               + "操作的保单号" + applyInfo.getPolicyNo() + "的预约[" + serviceItemsDesc 
		               + "]保全项目" + "因\"" + msg + "原因\"保全无法受理，请联系客户及时处理。";
		try {
			printService.sendEMail(applyInfo.getAcceptor(), null, "富德生命人寿保全预约处理结果", 
					content, DateUtil.dateToString(applyInfo.getApplyDate(),"yyyy-MM-dd"));
		} catch (Exception e) {
			logger.error("邮件发送失败。。。。");
		}
	}
	
	/**
	 * @Description: 生成问题件
	 * @methodName: createProblem
	 * @param posNo 保全号
	 * @param submitter 问题件创建人 PlatformContext.getCurrentUser()
	 * @param problemItemType 问题件类型  定时工作流都为“3”
	 * @param problemContent 问题件内容
	 * @return
	 * @return String
	 * @author WangMingShun
	 * @date 2015-9-9
	 * @throws
	 */
	public String createProblem(String posNo, String submitter,
			String problemItemType, String problemContent) {
		PosInfoDTO posInfo = commonQueryDAO.queryPosInfoRecord(posNo);
		PosProblemItemsDTO ppi = new PosProblemItemsDTO();
		if(submitter == null || "".equals(submitter)) {
				submitter = "System[00006]";
		}
		// 问题件处理人默认为保全项目受理人
		ppi.setAccessor(posInfo.getAcceptor());
		ppi.setPosNo(posNo);
		ppi.setProblemContent(problemContent);
		ppi.setProblemItemType(problemItemType);
		ppi.setProblemStatus("1");
		ppi.setSubmitDate(commonQueryDAO.getSystemDate());
		ppi.setSubmitter(submitter);
		ppi.setProblemItemNo(problemDAO.generatePosProblemItemNo(posNo));
		problemDAO.insertPosProblemItems(ppi);
		
		return ppi.getProblemItemNo();
	}
	
	/**
	 * @Description: 设置bean的值
	 * @methodName: setBean
	 * @param applyInfo
	 * @param sii
	 * @return void
	 * @author WangMingShun
	 * @date 2015-8-31
	 * @throws
	 */
	public Object MapToBean(Object bean, Map data) {
		Method[] methods = bean.getClass().getDeclaredMethods();
		for(int i=0; i<methods.length; i++) {
			Method method = methods[i];
			try {
				if(method.getName().startsWith("set")) {
					String field = method.getName();
					field = field.substring(field.indexOf("set") + 3);
					field = field.toLowerCase().charAt(0) + field.substring(1);
					Object value = data.get(field);
					if(value instanceof List) {
						List list = new ArrayList();
						List list1 = (List) value;
						for(int j=0; j<list1.size(); j++) {
							Map map = (Map) list1.get(j);
							Set s = map.keySet();
							for(Iterator iter = s.iterator();iter.hasNext();) {
								//获取map中的key
								String beanName = (String) iter.next();
								//实例化对象
								Object obj = Class.forName(beanName).newInstance();
								//获取该key对应的map
								Map data1 = (Map) map.get(beanName);
								//递归调用自己
								MapToBean(obj, data1);
								list.add(obj);
							}
						}
						method.invoke(bean, new Object[]{list});
					} else {
						method.invoke(bean, new Object[]{value});
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return bean;
	}
}
