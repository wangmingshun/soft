package com.sinolife.pos.others.status.service;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.common.dao.CommonAcceptDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.PosProblemItemsDTO;
import com.sinolife.pos.common.dto.PosProblemStatusChangeDTO;
import com.sinolife.pos.common.dto.PosStatusChangeHistoryDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.file.dao.PosFileDAO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.others.status.dao.AcceptStatusModifyDAO;
import com.sinolife.pos.schedule.dao.ScheduleDAO;
import com.sinolife.pos.todolist.problem.dao.ProblemDAO;
import com.sinolife.sf.platform.runtime.PlatformContext;

@Service("acceptStatusModifyService")
public class AcceptStatusModifyService {

	@Autowired
	AcceptStatusModifyDAO modifyDAO;

	@Autowired
	CommonAcceptDAO acceptDAO;

	@Autowired
	CommonQueryDAO queryDAO;

	@Autowired
	PosFileDAO posFileDAO;

	@Autowired
	private ProblemDAO problemDAO;

	@Autowired
	private ScheduleDAO scheduleDAO;

	@SuppressWarnings("rawtypes")
	public Map query(String posNo) {
		return modifyDAO.query(posNo);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void submit(ServiceItemsInputDTO itemsInputDTO) {
		PosInfoDTO posInfoDTO = queryDAO.queryPosInfoRecord(itemsInputDTO
				.getPosNo());
		if (posInfoDTO != null) {
			String statusCode = posInfoDTO.getAcceptStatusCode();
			if (StringUtils.isNotBlank(statusCode)) {
				// PosAcceptDetailDTO posAcceptDetailDTO;
				PosStatusChangeHistoryDTO change = new PosStatusChangeHistoryDTO();
				change.setPosNo(itemsInputDTO.getPosNo());
				change.setChangeDate(queryDAO.getSystemDate());
				change.setChangeUser(PlatformContext.getCurrentUser());
				if ("C01".equals(statusCode)) {

					acceptDAO.updatePosInfo(itemsInputDTO.getPosNo(),
							"accept_status_code", "A05");

					change.setOldStatusCode("C01");
					change.setNewStatusCode("A05");

					modifyDAO.insertDataUpdateLog("04", "3",
							posInfoDTO.getPolicyNo(), "5",
							itemsInputDTO.getPosNo(),
							itemsInputDTO.getApproveNo(),
							itemsInputDTO.getRemark());
					// posAcceptDetailDTO = new PosAcceptDetailDTO(
					// itemsInputDTO.getPosNo(), "5", "1",
					// posInfoDTO.getPolicyNo(), "014", "C01", "A05");//
					// 保全受理状态修改—变更的前后状态

				} else if ("C05".equals(statusCode)) {

					acceptDAO.updatePosInfo(itemsInputDTO.getPosNo(),
							"accept_status_code", "A01");

					change.setOldStatusCode("C05");
					change.setNewStatusCode("A01");
					modifyDAO.insertDataUpdateLog("04", "2",
							posInfoDTO.getPolicyNo(), "5",
							itemsInputDTO.getPosNo(),
							itemsInputDTO.getApproveNo(),
							itemsInputDTO.getRemark());

				} else {
					acceptDAO.updatePosInfo(itemsInputDTO.getPosNo(),
							"accept_status_code", "A17");

					change.setOldStatusCode("E01");
					change.setNewStatusCode("A17");
					modifyDAO.insertDataUpdateLog("04", "1",
							posInfoDTO.getPolicyNo(), "5",
							itemsInputDTO.getPosNo(),
							itemsInputDTO.getApproveNo(),
							itemsInputDTO.getRemark());

				}
				// acceptDAO.insertPosAcceptDetail(posAcceptDetailDTO);
				acceptDAO.insertPosStatusChangeHistory(change);

				// 对保单置暂停
				Date sysdate = queryDAO.getSystemDate();
				acceptDAO.doPolicySuspend(posInfoDTO.getPolicyNo(), "5",
						itemsInputDTO.getPosNo(), posInfoDTO.getServiceItems(),
						sysdate);

				if (StringUtils.isNotBlank(itemsInputDTO.getAttechmentFileId())
						&& StringUtils.isNotBlank(itemsInputDTO
								.getAttechmentFileName())) {

					posFileDAO.insertPosFileInfo(
							itemsInputDTO.getAttechmentFileId(),
							itemsInputDTO.getAttechmentFileName(),
							posInfoDTO.getPolicyNo(), itemsInputDTO.getPosNo(),
							"5", PlatformContext.getCurrentUser());
				}

			}

			itemsInputDTO.setPosNo("");
			itemsInputDTO.setRemark("");
			itemsInputDTO.setAttechmentFileId("");
			itemsInputDTO.setAttechmentFileName("");
			itemsInputDTO.setApproveNo("");

		}
	}

	/**
	 * 失败工作流继续推动
	 * 
	 * @param posNo
	 * @param model
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public String workFlowContinue(String posNo) {

		String returnMessage = "";
		String problemItemNo = modifyDAO.queryProblemItmeNo(posNo);

		if (StringUtils.isBlank(problemItemNo)) {

			return "不存在相应激活保全,请确认!";

		}
		Map<String, Object> map = scheduleDAO.workfloRunAgain(posNo);
		if ("1".equals((String) map.get("p_flag"))) {

			returnMessage = (String) map.get("p_message");
			return returnMessage;
		}

		PosProblemItemsDTO ppi = problemDAO
				.queryProblemItemsByID(problemItemNo);

		// 更新问题件状态为：已下发函件
		problemDAO.updatePosProblemItems(problemItemNo, "1", "4");
		problemDAO.updatePosProblemItems(problemItemNo, "3", "06");
		problemDAO.updatePosProblemItems(problemItemNo, "4", "2");
		// 写问题件状态变迁记录
		String loginUserId = PosUtils.getLoginUserInfo().getLoginUserID();

		// 写问题件状态变迁记录
		PosProblemStatusChangeDTO statusChange = new PosProblemStatusChangeDTO();
		statusChange.setProblemItemNo(problemItemNo);
		statusChange.setStatusChangeDate(queryDAO.getSystemDate());
		statusChange.setStatusChangeUser(loginUserId);
		statusChange.setStatusOld(ppi.getProblemStatus());
		statusChange.setStatusNew("4");
		problemDAO.insertPosProblemStatusChange(statusChange);

		return "激活成功!";

	}

}
