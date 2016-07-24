package com.sinolife.pos.todolist.problem.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.acceptance.branch.service.BranchAcceptService;
import com.sinolife.pos.common.consts.CodeTableNames;
import com.sinolife.pos.common.dao.CommonAcceptDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.PosProblemItemsDTO;
import com.sinolife.pos.common.dto.PosProblemStatusChangeDTO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.others.sms.dao.SmsDAO;
import com.sinolife.pos.others.unsuspendbankaccount.service.UnsuspendBankAccountService;
import com.sinolife.pos.schedule.dao.ScheduleDAO;
import com.sinolife.pos.todolist.problem.dao.ProblemDAO;
import com.sinolife.pos.todolist.problem.dto.ProblemLetterInfoDTO;
import com.sinolife.sf.framework.email.Mail;
import com.sinolife.sf.framework.email.MailService;
import com.sinolife.sf.framework.email.MailType;
import com.sinolife.sf.platform.runtime.PlatformContext;

@Service("problemService")
public class ProblemService {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private ProblemDAO problemDAO;

	@Autowired
	private CommonQueryDAO commonQueryDAO;

	@Autowired
	private CommonAcceptDAO commonAcceptDAO;

	@Autowired
	private BranchAcceptService branchAcceptService;

	@Autowired
	private UnsuspendBankAccountService unsuspendBankAccountService;

	@Autowired @Qualifier("mailService01")
	private MailService mailService;

	@Autowired
	SmsDAO smsDAO;

	@Autowired
	private ScheduleDAO scheduleDAO;

	/**
	 * 查询保全问题件代办列表
	 * 
	 * @param accessor
	 * @return List<PosProblemItemsDTO>
	 */
	public List<PosProblemItemsDTO> queryProblemTodoList(String accessor) {
		return problemDAO.queryProblemTodoList(accessor);
	}

	/**
	 * 根据保全号查询对应问题件号
	 * 
	 * @param posNo
	 */
	public String queryPosNoProblemNo(String posNo) {
		return problemDAO.queryPosNoProblemNo(posNo,
				PlatformContext.getCurrentUser());
	}

	/**
	 * 根据问题件号查询问题件
	 * 
	 * @param problemItemNo
	 * @return
	 */
	public PosProblemItemsDTO queryProblemItemsByID(String problemItemNo) {
		PosProblemItemsDTO posProblemItemsDTO = problemDAO
				.queryProblemItemsByID(problemItemNo);

		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("business_no_type", "5");
		paraMap.put("business_no", posProblemItemsDTO.getPosNo());

		problemDAO.queryTransFailureCause(paraMap);

		if (paraMap.get("trans_failure_cause") != null) {
			posProblemItemsDTO.setTransferFailureCause((String) paraMap
					.get("trans_failure_cause"));

		}

		return posProblemItemsDTO;

	}

	/**
	 * 创建问题件
	 * 
	 * @param posNo
	 *            保全号码
	 * @param submitter
	 *            问题件创建人
	 * @param accessor
	 *            问题件处理人
	 * @param problemItemType
	 *            问题件类型
	 * @param problemContent
	 *            问题件内容
	 * @return 问题件号码
	 */
	public String createProblem(String posNo, String submitter,
			String problemItemType, String problemContent) {
		PosInfoDTO posInfo = commonQueryDAO.queryPosInfoRecord(posNo);
		PosProblemItemsDTO ppi = new PosProblemItemsDTO();
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

		if (posInfo.getBarcodeNo().startsWith("MP")) {

			smsDAO.sendAgentMposSms(posNo, "10", problemContent);
		} else {

			// 给问题件处理人发送邮件通知
			Mail mail = new Mail();
			mail.setSubject("[SL_POS]问题件处理通知"); // 邮件标题
			mail.setTo(new String[] { posInfo.getAcceptor() }); // 收件人
			mail.setForm("sl_pos_mail_admin@sino-life.com"); // 发件人
			mail.setMailType(MailType.HTML_CONTENT); // 邮件类型
			mail.setContent(new StringBuilder(
					"<html><body style=\"font-size:14px;text-align:left;\">")
					.append("尊敬的保全系统用户 ")
					.append(commonQueryDAO.getUserNameByUserId(posInfo
							.getAcceptor()))
					.append(" :<br/>&nbsp;&nbsp;&nbsp;&nbsp;你好!<br/>")
					.append("&nbsp;&nbsp;&nbsp;&nbsp;您的问题件任务中有新下发的问题件，请及时处理并按时回销，谢谢！<br/><br/>")
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
							commonQueryDAO.getApplyDateByPosNo(posNo),
							"yyyy年MM月dd日"))
					.append("<br/>&nbsp;&nbsp;&nbsp;&nbsp;受理人：&nbsp;")
					.append(commonQueryDAO.getUserNameByUserId(posInfo
							.getAcceptor()))
					.append("(")
					.append(posInfo.getAcceptor())
					.append(")")
					.append("<br/><br/><br/><br/><br/><br/><br/><br/>")
					.append("<div style=\"color:gray;font-size:12px;\">本邮件由系统生成，请勿回复</div></body></html>")
					.toString()); // 邮件内容
			logger.info("发送邮件给：" + posInfo.getAcceptor());
			mailService.send(mail);
		}

		return ppi.getProblemItemNo();
	}

	/**
	 * 问题件处理，创建问题函
	 * 
	 * @param problemItemsDTO
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void createProblemLetter(PosProblemItemsDTO problemItemsDTO) {
		String problemItemNo = problemItemsDTO.getProblemItemNo();
		// 更新问题件状态，并写入问题件状态变迁记录
		posProblemStatusChange(problemItemNo, "2");
		problemDAO.updatePosProblemItems(problemItemNo, "3",
				problemItemsDTO.getDealResult());
		problemDAO.updatePosProblemItems(problemItemNo, "4",
				problemItemsDTO.getDealOpinion());
		problemDAO.updatePosProblemItems(problemItemNo, "5", problemItemsDTO
				.getLetterInfo().getProblemContent());
		// 保存附件
		if (StringUtils.isNotBlank(problemItemsDTO.getAttechmentFileId())) {
			this.saveAttechment(problemItemsDTO.getPosNo(),
					problemItemsDTO.getAttechmentFileId(),
					problemItemsDTO.getAttechmentFileName());
		}
	}

	/**
	 * 问题件处理，受理撤销
	 * 
	 * @param posNo
	 * @param problemItemsDTO
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void cancelPosApply(PosProblemItemsDTO problemItemsDTO) {
		String problemItemNo = problemItemsDTO.getProblemItemNo();

		// 更新问题件状态，并写入问题件状态变迁记录
		posProblemStatusChange(problemItemNo, "4");
		problemDAO.updatePosProblemItems(problemItemNo, "3",
				problemItemsDTO.getDealResult());
		problemDAO.updatePosProblemItems(problemItemNo, "4",
				problemItemsDTO.getDealOpinion());

		// A15,A08才可以做受理 撤消
		String posNo = problemItemsDTO.getPosNo();
		PosInfoDTO posInfo = commonQueryDAO.queryPosInfoRecord(posNo);
		if (posInfo != null
				&& ("A08".equals(posInfo.getAcceptStatusCode()) || "A15"
						.equals(posInfo.getAcceptStatusCode()))) {
			Map<String, Object> retMap = branchAcceptService.workflowControl(
					"11", posNo, false);
			String flag = (String) retMap.get("flag");
			String msg = (String) retMap.get("message");
			if (!"C02".equals(flag))
				throw new RuntimeException(msg);
		} else {
			throw new RuntimeException(
					"找不到保全受理记录，或者保全受理的状态不为A08审批判断已送审或A15待收费！");
		}
	}

	/**
	 * 问题件处理，取消转账暂停
	 * 
	 * @param problemItemsDTO
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void unsuspendBankAccount(PosProblemItemsDTO problemItemsDTO) {
		String problemItemNo = problemItemsDTO.getProblemItemNo();
		// 更新问题件状态，并写入问题件状态变迁记录
		posProblemStatusChange(problemItemNo, "4");
		problemDAO.updatePosProblemItems(problemItemNo, "3",
				problemItemsDTO.getDealResult());
		problemDAO.updatePosProblemItems(problemItemNo, "4",
				problemItemsDTO.getDealOpinion());
		// 取消转账暂停，需要检查是否收、付费是否成功，即prem_success_flag = N
		String posNo = problemItemsDTO.getPosNo();
		unsuspendBankAccountService.unsuspendBankAccount(posNo);
	}

	/**
	 * 问题件处理，回复处理
	 * 
	 * @param problemItemsDTO
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void replyProblem(PosProblemItemsDTO problemItemsDTO) {
		String problemItemNo = problemItemsDTO.getProblemItemNo();
		String dealResult = problemItemsDTO.getDealResult();
		posProblemStatusChange(problemItemNo, "4");
		problemDAO.updatePosProblemItems(problemItemNo, "3", dealResult);
		problemDAO.updatePosProblemItems(problemItemNo, "4",
				problemItemsDTO.getDealOpinion());
	}

	/**
	 * 问题件处理，回复处理
	 * 
	 * @param problemItemsDTO
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void replyProblemForRpc(PosProblemItemsDTO problemItemsDTO) {
		String problemItemNo = problemItemsDTO.getProblemItemNo();
		String dealResult = problemItemsDTO.getDealResult();
		PosProblemItemsDTO ppi = problemDAO
				.queryProblemItemsByID(problemItemNo);
		// 更新问题件状态为：已下发函件
		problemDAO.updatePosProblemItems(problemItemNo, "1", "4");
		// 写问题件状态变迁记录
		PosProblemStatusChangeDTO statusChange = new PosProblemStatusChangeDTO();
		statusChange.setProblemItemNo(problemItemNo);
		statusChange.setStatusChangeDate(commonQueryDAO.getSystemDate());
		statusChange.setStatusChangeUser(problemItemsDTO.getAccessor());
		statusChange.setStatusOld(ppi.getProblemStatus());
		statusChange.setStatusNew("4");
		problemDAO.insertPosProblemStatusChange(statusChange);
		problemDAO.updatePosProblemItems(problemItemNo, "3", dealResult);
		problemDAO.updatePosProblemItems(problemItemNo, "4",
				problemItemsDTO.getDealOpinion());
	}

	/**
	 * 问题件处理， 定时工作流继续推动
	 * 
	 * @param problemItemsDTO
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void workflowRunAgain(PosProblemItemsDTO problemItemsDTO) {
		String problemItemNo = problemItemsDTO.getProblemItemNo();

		String dealResult = problemItemsDTO.getDealResult();
		posProblemStatusChange(problemItemNo, "4");
		problemDAO.updatePosProblemItems(problemItemNo, "3", dealResult);
		problemDAO.updatePosProblemItems(problemItemNo, "4",
				problemItemsDTO.getDealOpinion());

		String posNo = problemItemsDTO.getPosNo();
		Map<String, Object> map = scheduleDAO.workfloRunAgain(posNo);
		if ("1".equals((String) map.get("p_flag"))) {

			throw new RuntimeException((String) map.get("p_message"));
		}

	}

	/**
	 * 查询函件下发需要的其他信息，如客户联系信息，业务员信息等
	 * 
	 * @param problemInfo
	 */
	public void prepareLetterInfo(PosProblemItemsDTO problemInfo) {
		PosInfoDTO posInfo = problemInfo.getPosInfo();

		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("clientNo", posInfo.getClientNo());
		paraMap.put("policyNo", posInfo.getPolicyNo());
		Map<String, Object> retMap = problemDAO
				.queryExtraInfoForLetter(paraMap);
		if (retMap != null) {
			String address = (String) retMap.get("FULL_ADDRESS");
			String zip = (String) retMap.get("POSTALCODE");
			String clientName = (String) retMap.get("CLIENT_NAME");
			String clientPhone = (String) retMap.get("CLIENT_PHONE");
			String empNo = (String) retMap.get("EMP_NO");
			String empPhone = (String) retMap.get("EMP_PHONE");
			String empName = (String) retMap.get("EMP_NAME");
			String empDeptName = (String) retMap.get("EMP_DEPT_NAME");

			ProblemLetterInfoDTO letterInfo = new ProblemLetterInfoDTO();
			letterInfo.setClientAddress(address);
			letterInfo.setClientZip(zip);
			letterInfo.setClientPhone(clientPhone);
			letterInfo.setClientName(clientName);
			letterInfo.setEmpNo(empNo);
			letterInfo.setEmpPhone(empPhone);
			letterInfo.setEmpName(empName);
			letterInfo.setEmpDeptName(empDeptName);

			problemInfo.setLetterInfo(letterInfo);
		}
	}

	/**
	 * 更新问题件状态，并写入问题件状态变迁记录
	 * 
	 * @param problemItemNo
	 * @param oldProblemStatus
	 * @param newProblemStatus
	 */
	public void posProblemStatusChange(String problemItemNo,
			String newProblemStatus) {
		PosProblemItemsDTO ppi = problemDAO
				.queryProblemItemsByID(problemItemNo);

		// 更新问题件状态为：已下发函件
		problemDAO.updatePosProblemItems(problemItemNo, "1", newProblemStatus);

		// 写问题件状态变迁记录
		String loginUserId = PosUtils.getLoginUserInfo().getLoginUserID();
		PosProblemStatusChangeDTO statusChange = new PosProblemStatusChangeDTO();
		statusChange.setProblemItemNo(problemItemNo);
		statusChange.setStatusChangeDate(commonQueryDAO.getSystemDate());
		statusChange.setStatusChangeUser(loginUserId);
		statusChange.setStatusOld(ppi.getProblemStatus());
		statusChange.setStatusNew(newProblemStatus);
		problemDAO.insertPosProblemStatusChange(statusChange);
	}

	/**
	 * 保存附件
	 * 
	 * @param posNo
	 * @param attechmentFileId
	 * @param attechmentFileName
	 */
	public void saveAttechment(String posNo, String attechmentFileId,
			String attechmentFileName) {
		PosInfoDTO posInfo = commonQueryDAO.queryPosInfoRecord(posNo);
		PosAcceptDetailDTO detail = new PosAcceptDetailDTO();
		detail.setPosNo(posNo);
		detail.setInfoGroupNo(String.valueOf(commonAcceptDAO
				.getAcceptDetailMaxGroupNo(posNo) + 1));
		detail.setPosObject("5");
		detail.setItemNo("009");
		detail.setNewValue(attechmentFileName);
		detail.setOldValue(attechmentFileId);
		detail.setProcessType("1");
		detail.setObjectValue(posInfo.getClientNo());
		commonAcceptDAO.insertPosAcceptDetail(detail);
	}

}
