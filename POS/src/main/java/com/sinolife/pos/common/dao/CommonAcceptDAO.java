package com.sinolife.pos.common.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO;
import com.sinolife.pos.common.dto.PosAcceptDetailDTO;
import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.common.dto.PosApplyFileMaterialsDTO;
import com.sinolife.pos.common.dto.PosApplyFilesDTO;
import com.sinolife.pos.common.dto.PosApproveInfoDTO;
import com.sinolife.pos.common.dto.PosBiaCheckDto;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.PosPremDTO;
import com.sinolife.pos.common.dto.PosProcessDetailDTO;
import com.sinolife.pos.common.dto.PosStatusChangeHistoryDTO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.report.dao.ListDAO;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Repository("commonAcceptDAO")
@Transactional(propagation = Propagation.REQUIRED)
public class CommonAcceptDAO extends AbstractPosDAO {

	/**
	 * 保全公共流转流转方法
	 * 
	 * @param paraMap
	 * @return
	 */
	public void workflowControl(Map paraMap) {
		queryForObject("PROC_WORKFLOW_CONTROL", paraMap);
	}

	/******************
	 * 写保全申请批次表
	 * 
	 * @param applyBatchDTO
	 */
	public void insertPosApplyBatch(PosApplyBatchDTO applyBatchDTO) {
		queryForObject("INSERT_POS_APPLY_BATCH", applyBatchDTO);
		if (!"0".equals(applyBatchDTO.getFlag())) {
			throw new RuntimeException(applyBatchDTO.getMessage());
		}
	}

	/******************************
	 * 写保全申请书表
	 * 
	 * @param applyFilesDTO
	 */
	public void insertPosApplyFiles(PosApplyFilesDTO applyFilesDTO) {
		queryForObject("INSERT_POS_APPLY_FILES", applyFilesDTO);
		if (!"0".equals(applyFilesDTO.getFlag())) {
			throw new RuntimeException(applyFilesDTO.getMessage());
		}
	}

	/*********************
	 * 写保全信息表
	 * 
	 * @param posInfoDTO
	 */
	public void insertPosInfo(PosInfoDTO posInfoDTO) {
		queryForObject("INSERT_POS_INFO", posInfoDTO);
		if (!"0".equals(posInfoDTO.getFlag())) {
			throw new RuntimeException(posInfoDTO.getMessage());
		}
	}

	/*********************
	 * 写保全受理明细表
	 * 
	 * @param acceptDetailDTO
	 */
	public void insertPosAcceptDetail(PosAcceptDetailDTO acceptDetailDTO) {
		queryForObject("INSERT_POS_ACCEPT_DETAIL", acceptDetailDTO);
		if (!"0".equals(acceptDetailDTO.getFlag())) {
			throw new RuntimeException(acceptDetailDTO.getMessage());
		}
	}

	/**
	 * 写保全受理明细表，专门用于写入规则检查不通过信息，因为这种记录可能不停写入，导致accept_detail_no超过允许的最大值<br/>
	 * 做法是先查找当前受理明细的detail是否已经存在相同的记录，如果已经存在相同的记录，则只update pos_object字段，
	 * 否则执行插入操作
	 * 
	 * @param acceptDetailDTO
	 */
	public void insertOrReplaceAcceptDetailForRuleMsg(
			PosAcceptDetailDTO acceptDetailDTO) {
		if (acceptDetailDTO != null
				&& "9".equals(acceptDetailDTO.getPosObject())
				&& "000".equals(acceptDetailDTO.getItemNo())) {
			String pkSerial = (String) queryForObject(
					"existsSameRuleMsgDetail", acceptDetailDTO);
			if (StringUtils.isNotBlank(pkSerial)) {
				getSqlMapClientTemplate().update(
						sqlName("updateRuleMsgDetail"), pkSerial, 1);
				return;
			}
		}
		this.insertPosAcceptDetail(acceptDetailDTO);
	}

	/*********************
	 * 写保全申请书资料表
	 * 
	 * @param materialsDTO
	 */
	public void insertPosApplyFileMaterials(
			PosApplyFileMaterialsDTO materialsDTO) {
		queryForObject("INSERT_POS_APPLY_FILE_MTL", materialsDTO);
		if (!"0".equals(materialsDTO.getFlag())) {
			throw new RuntimeException(materialsDTO.getMessage());
		}
	}

	/*********************
	 * 写保银保通对账表
	 * 
	 * @param materialsDTO
	 */
	public void insertPosBiaCheck(PosBiaCheckDto biaCheckDto) {
		queryForObject("INSERT_POS_BIA_CHECK", biaCheckDto);
		if (!"0".equals(biaCheckDto.getFlag())) {
			throw new RuntimeException(biaCheckDto.getMessage());
		}
	}

	/**********************
	 * 写保全审批表
	 * 
	 * @param approveInfoDTO
	 */
	public void insertPosApprove(PosApproveInfoDTO approveInfoDTO) {
		queryForObject("INSERT_POS_APPROVE", approveInfoDTO);
	}

	/******************
	 * 写保全费用表
	 * 
	 * @param premDTO
	 */
	public void insertPosPrem(PosPremDTO premDTO) {
		queryForObject("INSERT_POS_PREM", premDTO);
	}

	/*******************************
	 * 写保全变更明细表
	 * 
	 * @param processDetailDTO
	 */
	public void insertPosProcessDetail(PosProcessDetailDTO processDetailDTO) {
		queryForObject("INSERT_POS_PROCESS_DETAIL", processDetailDTO);
	}

	/************************
	 * 写状态变迁表
	 * 
	 * @param changeHistoryDTO
	 */
	public void insertPosStatusChangeHistory(
			PosStatusChangeHistoryDTO changeHistoryDTO) {
		queryForObject("INSERT_POS_STATUS_CHANGE_HIS", changeHistoryDTO);
	}

	/**
	 * 查询批次中第一 个状态为A01的受理
	 * 
	 * @param posBatchNo
	 *            批次号
	 * @return
	 */
	public String getNextPosNoInBatch(String posBatchNo) {
		return (String) getSqlMapClientTemplate().queryForObject(
				CommonAcceptDAO.class.getName() + ".getNextPosNoInBatch",
				posBatchNo);
	}

	/**
	 * 判断同一受理批次下，前一受理单是否已经生效
	 * 
	 * @param posNo
	 * @return
	 */
	public boolean prePosnoIsEnd(String posNo) {
		return "Y".equals(getSqlMapClientTemplate().queryForObject(
				CommonAcceptDAO.class.getName() + ".prePosnoIsEnd", posNo));
	}

	/**
	 * 调用->财务接口8：收付费用修改接口
	 * 
	 * @param paraMap
	 */
	public void finModifyBusinessCost(Map<String, Object> paraMap) {
		PosUtils.setDefaultValue(paraMap, "p_query_type", "1");
		queryForObject("finModifyBusinessCost", paraMap);
	}

	/**
	 * 更新pos_info某字段
	 * 
	 * @param pMap
	 */
	public void updatePosInfo(String posNo, String updateKey, String updateValue) {
		Map pMap = new HashMap();
		pMap.put("posNo", posNo);
		pMap.put("updateKey", updateKey);
		pMap.put("updateValue", updateValue);
		getSqlMapClientTemplate().update(
				CommonAcceptDAO.class.getName() + ".UPDATE_POS_INFO", pMap);
		if (!"0".equals(pMap.get("flag"))) {
			throw new RuntimeException("" + pMap.get("message"));
		}
	}

	/**
	 * 插入个人最新资料告知书记录
	 * 
	 * @param posNo
	 * @param clientNo
	 * @param noticeType
	 * @param noticeCheck
	 */
	public void insertPosPersonalNotice(String posNo, String clientNo,
			String noticeType, String noticeCheck) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("posNo", posNo);
		paraMap.put("clientNo", clientNo);
		paraMap.put("noticeType", noticeType);
		paraMap.put("noticeCheck", noticeCheck);
		getSqlMapClientTemplate().insert(
				CommonAcceptDAO.class.getName() + ".insertPosPersonalNotice",
				paraMap);
	}

	/**
	 * 插入个人最新资料告知书明细
	 * 
	 * @param posNo
	 * @param clientNo
	 * @param noticeType
	 * @param noticeContentCode
	 * @param noticeDescription
	 */
	public void insertPosPersonalNoticeDetail(String posNo, String clientNo,
			String noticeType, String noticeContentCode,
			String noticeDescription) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("posNo", posNo);
		paraMap.put("clientNo", clientNo);
		paraMap.put("noticeType", noticeType);
		paraMap.put("noticeContentCode", noticeContentCode);
		paraMap.put("noticeDescription", noticeDescription);
		getSqlMapClientTemplate().insert(
				CommonAcceptDAO.class.getName()
						+ ".insertPosPersonalNoticeDetail", paraMap);
	}

	/**
	 * 置保单暂停 p_policy_no:保单号 p_business_no_type:业务单号类型 p_business_no:业务号
	 * p_service_suspend_item:保单服务暂停原因 p_set_time:置暂停时间 v_sign:执行结果：Y-成功；N-失败；
	 * v_message:结果描述
	 */
	public void doPolicySuspend(String policyNo, String bizNoType,
			String bizNo, String suspendItem, Date setTime) {

		if ("41".equals(suspendItem))// 贴费
		{
			suspendItem = "58";
		} else if ("42".equals(suspendItem))// 保单生效日调整
		{
			suspendItem = "59";
		} else if ("43".equals(suspendItem))// E服务权限调整
		{
			suspendItem = "60";
		}
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_business_no_type", bizNoType);
		paraMap.put("p_business_no", bizNo);
		paraMap.put("p_service_suspend_item", suspendItem);
		paraMap.put("p_set_time", setTime);

		queryForObject("doPolicySuspend", paraMap);

		if ("N".equals(paraMap.get("v_sign"))) {
			throw new RuntimeException("对保单[" + policyNo + "]置暂停失败："
					+ paraMap.get("v_message"));
		}
	}

	/**
	 * 取消保单暂停
	 * 
	 * @param policyNo
	 * @param posNo
	 * @param serviceItem
	 */
	public void resumePolicySuspend(String policyNo, String posNo,
			String serviceItem) {
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("businessNoType", "5");
		pMap.put("businessNo", posNo);
		pMap.put("suspendItem", serviceItem);
		queryForObject("resumePolicySuspend", pMap);

		if ("N".equals(pMap.get("flag"))) {
			throw new RuntimeException("" + pMap.get("message"));
		}
	}

	/**
	 * 保全内部申请，生成pos申请3张表的相关数据，返回posNo
	 * 
	 * @param clientNo
	 * @param policyNo
	 * @param applyDate
	 * @param serviceItems
	 * @return
	 */
	public String posInsideApply(String clientNo, String policyNo,
			Date applyDate, String serviceItems) {
		Map pMap = new HashMap();
		pMap.put("clientNo", clientNo);
		pMap.put("policyNo", policyNo);
		pMap.put("applyDate", applyDate);
		pMap.put("serviceItems", serviceItems);

		queryForObject("POS_INSIDE_APPLY", pMap);

		if ("1".equals(pMap.get("flag"))) {
			throw new RuntimeException("" + pMap.get("message"));
		}

		return (String) pMap.get("posNo");
	}

	/**
	 * 根据批次号查询并锁定保全记录
	 * 
	 * @param posBatchNo
	 * @return
	 */
	public List<PosInfoDTO> queryAndLockPosInfoByPosBatchNo(String posBatchNo) {
		try {
			return queryForList("queryAndLockPosInfoByPosBatchNo", posBatchNo);
		} catch (Exception e) {
			throw new RuntimeException("锁定保全记录失败：" + e.getMessage(), e);
		}
	}

	/**
	 * 取得pos_accept_detail表中的最大info_group_no，如果不存在，则返回0
	 * 
	 * @param posNo
	 * @return
	 */
	public Integer getAcceptDetailMaxGroupNo(String posNo) {
		String maxGroupNo = (String) getSqlMapClientTemplate().queryForObject(
				ServiceItemsDAO.class.getName() + ".QUERY_DETAIL_MAX_GROUPNO",
				posNo);
		int groupNo = 0;
		if (StringUtils.isNotBlank(maxGroupNo)) {
			try {
				groupNo = new Integer(maxGroupNo);
			} catch (Exception e) {
				logger.debug(e);
			}
		}
		return groupNo;
	}

	/**
	 * 触发修改下一单保全的状态
	 * 
	 * @param posNo
	 *            当前保全号
	 */
	public void nextPosNoTrigger(String posNo) {
		Map pMap = new HashMap();
		pMap.put("posNo", posNo);

		queryForObject("GET_NEXT_POS_NO", pMap);

		if ("1".equals(pMap.get("flag"))) {
			throw new RuntimeException("" + pMap.get("message"));
		}
	}

	public void insertJbzSinolife(Date data_send_date, String policy_no,
			Date last_examination_date, Double examination_time,
			String booking_status, Date booking_date) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("P_data_send_date", data_send_date);
		paraMap.put("p_policy_no", policy_no);
		paraMap.put("p_last_examination_date", last_examination_date);
		paraMap.put("p_examination_time", examination_time);
		paraMap.put("p_booking_status", booking_status);
		paraMap.put("p_booking_date", booking_date);
		queryForObject("INSERT_JBZ_SINOLIFE", paraMap);

		if ("2".equals(paraMap.get("flag"))) {
			throw new RuntimeException("" + paraMap.get("message"));
		}

	}

	/**
	 * 代审信息或免填单标志写入受理明细
	 * 
	 * @param posNo
	 * @return List<PosAcceptDetailDTO>
	 * @author GaoJiaMing
	 * @time 2014-9-11
	 */
	public List<PosAcceptDetailDTO> getPosAcceptDetailDTOList(String posNo) {
		return queryForList("getPosAcceptDetailDTOList", posNo);
	}
	
	/**
	 * 检验抵交续期保费保单号的有效性
	 * 
	 * @param map
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-9-22
	 */
	public String checkPaypolicyValid(Map<String, Object> map) {
		return (String) getSqlMapClientTemplate().queryForObject(
				CommonAcceptDAO.class.getName() + ".checkPaypolicyValid", map);
	}
	
	/**
	 * 根据保单号查询是否是电销(0不代表电销，1代表是电销)
	 * @param policyNo
	 * @return
	 */
	public int getPolicyNoCount(String policyNo) {
		return ((Integer) getSqlMapClientTemplate().queryForObject(
				CommonAcceptDAO.class.getName() + ".getPolicyNoCount", policyNo))
				.intValue();
	}
	
	/**
	 * @Description:更新关联条码
	 * @methodName: updatePosApplyFiles
	 * @param map
	 * @return void
	 * @author WangMingShun
	 * @date 2015-12-28
	 * @throws
	 */
	public void updatePosApplyFiles(Map map) {
		getSqlMapClientTemplate().update(
				CommonAcceptDAO.class.getName() + ".update_pos_apply_files", map);
	}
	
	/**
	 * 查询人脸识别次数
	 * @param posNO
	 * @param itemNo
	 * @return int
	 * @author Zhangyi
	 * @time 2016-5-31
	 */
	public int getFaceRecognitionCount(String posNo,String itemNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("posNO", posNo);
		paraMap.put("itemNo", itemNo);
		int times = 0;
		String count = (String) getSqlMapClientTemplate().queryForObject(
				CommonAcceptDAO.class.getName() + ".getFaceRecognitionCount", paraMap);
		if(StringUtils.isBlank(count)){
			times = 0;
		}else{
			try {
				times = Integer.parseInt(count);
			} catch (NumberFormatException e) {
			    e.printStackTrace();
			}
		}
		return times;
	}
	
	/**
	 * 更新保全受理明细表
	 * @param acceptDetailDTO void
	 * @author Zhangyi
	 * @time 2016-5-31
	 */
	public void updatePosAcceptDetail(PosAcceptDetailDTO acceptDetailDTO) {
		getSqlMapClientTemplate().update(
				CommonAcceptDAO.class.getName() + ".updatePosAcceptDetail", acceptDetailDTO);
	}
}
