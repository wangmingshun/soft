package com.sinolife.pos.todolist.uwnote.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinolife.pos.common.dao.CommonAcceptDAO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.todolist.uwnote.dao.UwnotePrintFeedbackDAO;
import com.sinolife.sf.platform.runtime.PlatformContext;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Service("undwrtPrintFeedbackService")
public class UwnotePrintFeedbackService {
	
	@Autowired
	UwnotePrintFeedbackDAO uwDAO;

	@Autowired
	CommonAcceptDAO acptDAO;
	
	/**
	 * 查询用户代办任务列表里待打印的核保函
	 */
	public List queryTodoPrintList(String userId){
		return uwDAO.queryTodoPrintList(userId);
	}
	
	public void updateDetailPrintTimes(String posNo){
		if(uwDAO.printed(posNo)){
			uwDAO.printTimeAddOne(posNo);
			
		}else{
			acptDAO.insertPosAcceptDetail(new PosAcceptDetailDTO(posNo, "5", "3", posNo, "008", null, "1"));
		}
	}
	
	/**
	 * 查询处于送人工核保状态的受理出来打印核保函，能打印多次
	 */
	public List queryForPrintList(String startDate, String endDate, String userId){
		return uwDAO.queryForPrintList(startDate, endDate, userId);
	}
	
	public List feedbackQueryPosInfo(String posNo,String userId){
		return uwDAO.feedbackQueryPosInfo(posNo, userId);
	}
	
	/**
	 * 将核保函回销意见记录入detail表
	 * @param posNo
	 * @param feedbackResult
	 */
	public void insertAcceptDetail(String posNo, String feedbackResult){
		acptDAO.insertPosAcceptDetail(new PosAcceptDetailDTO(posNo, "5", "3", posNo, "010", null, feedbackResult));
		
	}
	
	/**
	 * 回销动作通知uw
	 * @param feedbackPosNo
	 * @param taskType
	 */
	public void undwrtNoteWriteOff(String feedbackPosNo, String taskType, String feedbackResult){
		Map pMap = new HashMap();
		pMap.put("taskType", taskType);
		pMap.put("controlNo", feedbackPosNo);
		pMap.put("writeOffStaff", PlatformContext.getCurrentUser());
		pMap.put("clientOption", feedbackResult);
		
		uwDAO.undwrtNoteWriteOff(pMap);
	}
}
