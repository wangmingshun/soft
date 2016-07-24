package com.sinolife.pos.print.web;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.common.util.PosValidateWrapper;
import com.sinolife.pos.print.dao.PrintDAO;
import com.sinolife.pos.print.dto.EndorsementPrintCriteriaDTO;
import com.sinolife.pos.print.dto.NoticePrintCriteriaDTO;

@Component
public class NoticePrintCriteriaValidator {

	@Autowired
	private PrintDAO printDAO;
	
	/**
	 * 逐单打印校验
	 * @param criteria
	 * @param errors
	 */
	public void validateNoticeSingle(NoticePrintCriteriaDTO criteria, Errors errors) {
		PosValidateWrapper wrapper = new PosValidateWrapper(errors);
		wrapper.rejectIfEmpty("policyNo", criteria.getPolicyNo(), "保单号不能为空");
		wrapper.rejectIfEmpty("branchCode", criteria.getBranchCode(), "机构代码不能为空");
		wrapper.rejectIfEmpty("noticeType", criteria.getNoticeType(), "通知书类型不能为空");
		if("10".equals(criteria.getNoticeType()) || "11".equals(criteria.getNoticeType())) {
			wrapper.rejectIfNull("queryDateStart", criteria.getQueryDateStart(), "开始日期不能为空");
		}
	}

	/**
	 * 批次打印校验
	 * @param criteria
	 * @param errors
	 */
	public void validateNoticeBatch(NoticePrintCriteriaDTO criteria, Errors errors) {
		PosValidateWrapper wrapper = new PosValidateWrapper(errors);
		wrapper.rejectIfEmpty("branchCode", criteria.getBranchCode(), "机构代码不能为空");
		wrapper.rejectIfEmpty("noticeType", criteria.getNoticeType(), "通知书类型不能为空");
		wrapper.rejectIfNull("businessDateStart", criteria.getBusinessDateStart(), "开始时间不能为空");
		wrapper.rejectIfNull("businessDateEnd", criteria.getBusinessDateEnd(), "结束时间不能为空");
		Calendar calBegin = Calendar.getInstance();
		Calendar calEnd = Calendar.getInstance();
		calBegin.setTime(criteria.getBusinessDateStart());
		calEnd.setTime(criteria.getBusinessDateEnd());
		if(calBegin.compareTo(calEnd) > 0) {
			wrapper.addErrMsg("businessDateStart", "开始时间不能晚于结束时间");
		}
		calBegin.add(Calendar.MONTH, 1);
		if(calBegin.compareTo(calEnd) < 0) {
			wrapper.addErrMsg("businessDateStart", "查询时间范围不能超过1个月");
		}
		if(printDAO.isShenzhenUser(PosUtils.getLoginUserInfo().getLoginUserUMBranchCode())) {
			wrapper.rejectIfNotInEnum("noticeType", criteria.getNoticeType(), new String[] {"51","52", "53", "54", "3","4", "5", "6", "7", "8", "9", "10", "11", "12", "13"}, "无效的通知书类型");
		} else {
			wrapper.rejectIfNotInEnum("noticeType", criteria.getNoticeType(), new String[] {"1","2","3","4", "5", "6", "7", "8", "9", "10", "11", "12", "13"}, "无效的通知书类型");
		}
	}
	
	/**
	 * 批单批次打印校验
	 * @param criteria
	 * @param errors
	 */
	public void validateEndorsementBatch(EndorsementPrintCriteriaDTO  criteria, Errors errors) {
		PosValidateWrapper wrapper = new PosValidateWrapper(errors);
		wrapper.rejectIfEmpty("acceptBranchCode", criteria.getAcceptBranchCode(), "受理机构代码不能为空");
		wrapper.rejectIfNull("effectDateStart", criteria.getEffectDateStart(), "生效开始时间不能为空");
		wrapper.rejectIfNull("effectDateEnd", criteria.getEffectDateEnd(), "生效结束时间不能为空");
		Calendar calBegin = Calendar.getInstance();
		Calendar calEnd = Calendar.getInstance();
		calBegin.setTime(criteria.getEffectDateStart());
		calEnd.setTime(criteria.getEffectDateEnd());
		if(calBegin.compareTo(calEnd) > 0) {
			wrapper.addErrMsg("effectDateStart", "开始时间不能晚于结束时间");
		}
		calBegin.add(Calendar.DAY_OF_YEAR, 14);
		if(calBegin.compareTo(calEnd) < 0) {
			wrapper.addErrMsg("effectDateEnd", "查询时间范围不能超过15天");
		}
	}
}
