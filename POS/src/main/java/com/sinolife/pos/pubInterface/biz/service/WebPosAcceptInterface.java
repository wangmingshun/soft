package com.sinolife.pos.pubInterface.biz.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.sinolife.pos.common.dto.PolicyContactInfoDTO;
import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.print.dto.PosNoteDTO;
import com.sinolife.sf.store.SFFile;

public interface WebPosAcceptInterface extends DevelopPlatformInterface {

	/**
	 * 生成官网保全用的条码
	 * 
	 * @param serviceItems
	 * @return
	 */
	public String generateWPOSBarcode();

	/**
	 * 根据保全项目生成E终端保全用的条码
	 * 
	 * @param serviceItems
	 * @return
	 */
	public String generateEpointBarcode(String serviceItems);

	/**
	 * 官网保全撤销
	 * 
	 */
	public void cancelWebPos(String applicantNo);

	/**
	 * 撤销最后完成失败的保全项目
	 * 
	 */
	public void cancelFinalPos(String posNo);

	/**
	 * 根据保单号和投保人id查询客户的联系信息
	 * 
	 * @param clientNo
	 * @param policyNo
	 * @return
	 */
	public List<PolicyContactInfoDTO> queryAppContactInfo(String clientNo,
			String policyNo);

	/**
	 * 生成批单文件流
	 * 
	 * @param posNo
	 * @param submiter
	 * @return
	 */
	public SFFile createOutputStreamForEnt(String posNo, String submiter);

	/**
	 * 记录电子批单到文件表
	 * 
	 * @param fileId
	 * @param fileName
	 * @param policyNo
	 * @param posNo
	 * @param userId
	 */
	public void insertPosFileInfo(String fileId, String fileName,
			String policyNo, String posNo, String userId);

	/**
	 * 根据保全号返回电子批单文件ID Map 包括key（file_id,file_name,upload_time, upload_user）
	 * 
	 * @param posNo
	 * @return
	 */
	public Map<String, String> queryPosFileInfoForEnt(String posNo);

	/** 满期金提取清单 */

	public SFFile posexpirationpayList(Map map);

	/**
	 * 根据不同的保全项目找出可以在官网操作的保单
	 * 
	 * @param policyNos
	 * @param serviceItems
	 * @return
	 */
	public List<String> getCanDoPolicys(List<String> policyNos,
			String serviceItems);

	/**
	 * 根据不同的保全项目找出E终端可以操作的保单
	 * 
	 * @param policyNos
	 * @param serviceItems
	 * @return
	 */
	public List<String> getEpointCanDoPolicys(List<String> policyNos,
			String serviceItems);

	/**
	 * 根据受理机构查询柜面号
	 * 
	 * @param branchCode
	 *            机构代码
	 * @return 柜面号
	 */
	public String getEpointCounterNo(String branchCode);

	/**
	 * 取消指定保单号的转账暂停
	 * 
	 * @param policyNo
	 * @return
	 */
	public void unsuspendBankAccount(String policyNo);

	/**
	 * 官网查询保全转账失败详情
	 * 
	 * @param policyNo
	 * @return
	 */
	public PosInfoDTO queryTransferFailInfo(String policyNo);

	/**
	 * 根据理财合同号和缴费金额、缴费日期，计算初始费用金额 返回追加保费实际追加金额
	 * 
	 * @return
	 */
	public Double calcInitialExpenses(String policyNo, String p_prem_amount);

	/**
	 * 判断是否是投连或万能险种
	 * 
	 * @param policyNo
	 * @return 理财类型 1:投连 2:万能
	 */
	public String getFinancialPolicyProductByPolicyNo(String policyNo);

	/**
	 * 查询客户e服务权限等级
	 * 
	 * @param policyNo
	 * @return
	 */
	public String queryClientInputPrivs(String clientNo);

	/**
	 * 获取险种除贷款自垫及保单余额外的退费总额
	 * 
	 * @param p_policy_no
	 *            p_prod_seq p_calc_date
	 * @return
	 */
	public BigDecimal getProductPremSum(String p_policy_no, String p_prod_seq,
			Date p_calc_date);

	/**
	 * 获取保单余额
	 * 
	 * @param p_policy_no
	 * @return
	 */
	public BigDecimal getPolicyBlance(String p_policy_no);

	/**
	 * 获取险种应退金额
	 * 
	 * @param p_policy_no
	 * @return
	 */
	public BigDecimal getPremPaid(String p_policy_no, String p_prod_seq,
			Date p_calc_date);

	/**
	 * 电子函件订阅/退订接口
	 * 
	 * @param clientNo
	 * @param eLetterService
	 *            电子信函订阅标志：Y表示订阅，N表示退订
	 * @param emailAddress
	 *            邮箱
	 * @return Map<String, Object>
	 *         key="p_flag"：Y-成功，N-失败，E-异常；key="p_message"：异常信息
	 *         ；key="p_client_no"：客户号；
	 * @author GaoJiaMing
	 * @time 2014-7-1
	 */
	public Map<String, Object> procELetterService(String clientNo,
			String eLetterService, String emailAddress);

	/**
	 * 官网保单全部加挂标志同步接口
	 * 
	 * @param clientNo
	 * @param addAll
	 *            是否全部加挂：Y是，N否
	 * @return Map<String, Object>
	 *         key="p_flag"：Y-成功，N-失败，E-异常；key="p_message"：异常信息
	 *         ；key="p_client_no"：客户号；
	 * @author GaoJiaMing
	 * @time 2014-7-2
	 */
	public Map<String, Object> syncAddAllFlag(String clientNo, String addAll);

	/**
	 * 官网加挂保单同步接口
	 * 
	 * @param policyNoList
	 * @param isAdded
	 *            是否加挂：Y-是；N-否；
	 * @return List<Map<String, Object>>
	 *         key="p_flag"：Y-成功，N-失败，E-异常；key="p_message"：异常信息
	 *         ；key="p_policy_no"：保单号；
	 * @author GaoJiaMing
	 * @time 2014-7-2
	 */
	public List<Map<String, Object>> syncEPolicy(List<String> policyNoList,
			String isAdded);

	/**
	 * 查询客户电子信函订阅状态
	 * 
	 * @param clientNo
	 * @return String Y-是；N-否
	 * @author GaoJiaMing
	 * @time 2014-7-3
	 */
	public String getClientELetterStatus(String clientNo);

	/**
	 * 函件历史查询接口
	 * 
	 * @param paraMap
	 *            key-clientNo：客户号；key-startTime(Date)：开始时间；key-endTime(Date)：
	 *            结束时间；key-noteType：通知书类型；
	 * @return List<Map<String,Object>> Key:CLIENT_NO --客户号 KEY:POLICY_NO --保单号
	 *         KEY:PRODUCT_CODE --险种代码 KEY:FULL_NAME --险种名称
	 *         KEY:DETAIL_SEQUENCE_NO --通知书序号 KEY:BASE_SUM_INS --保额
	 *         KEY:EFFECT_DATE --生效日期 KEY:NOTE_TYPE --通知书类型代码 KEY:DESCRIPTION
	 *         --通知书类型名称 KEY:IM_FILE_ID --附件IM服务器ID KEY:NOTE_CREATED_DATE --生成时间
	 *         KEY:PDF_UPLOAD_DATE --最后发送时间
	 * @author GaoJiaMing
	 * @time 2014-7-11
	 */
	public List<Map<String, Object>> queryPosNoteHistory(
			Map<String, Object> paraMap);

	/**
	 * 获取电子函件ID(电子函件下载接口)
	 * 
	 * @param policyNo
	 * @param detailSequenceNo
	 * @return String 失败：null 成功：fileId
	 * @author GaoJiaMing
	 * @time 2014-7-8
	 */
	public String getLoadFileId(String policyNo, String detailSequenceNo);

	/**
	 * 电子函件重新发送
	 * 
	 * @param clientNo
	 * @param policyNo
	 * @param detailSequenceNo
	 * @return String 成功：Y 失败：错误信息
	 * @author GaoJiaMing
	 * @time 2014-7-8
	 */
	public String resendELetter(String clientNo, String policyNo,
			String detailSequenceNo);

	/**
	 * 官网发送邮件后会写发送历史
	 * 
	 * @param posNo
	 * @param eEntFileID
	 *            --批单上传文件ID
	 * @return String 成功：Y 失败：错误信息
	 * @author GaoJiaMing
	 * @time 2014-7-16
	 */
	public String updateEEntFileID(String posNo, String eEntFileID);
	
	/**
	 * 函件发送记录查询接口
	 * 
	 * @param detailSequenceNo
	 * @return List<Map<String, Object>> KEY:EMAIL --邮箱地址 KEY:SEND_DATE --发送时间
	 *         KEY:BUSINESS_NO --业务号
	 * @author GaoJiaMing
	 * @time 2014-11-19
	 */
	public List<Map<String, Object>> getMailLogByDetailSequenceNo(String detailSequenceNo);

	public PosApplyBatchDTO insertBatchAcceptTest(
			final PosApplyBatchDTO applyBatchInfo);

	public Map<String, Object> acceptInternalTest(
			final PosApplyBatchDTO applyBatchInfo,
			final ServiceItemsInputDTO itemsInputDTO);
	
	/**
	 * @Description: 成长红包在微信定投加保后通知微信端结果
	 * @methodName: queryAutoPolicyAddPremByWX
	 * @param policyNo  保单号
	 * @param orderID   订单号
	 * @return Map<String,Object>
	 * @param applyBarCode 投保单号
	 * @param posNo    保全号
	 * @param policy_no 保单号
	 * @param effectDate 保全生效日
	 * @param posStatus 保全状态
	 * @param preSum    加保保费
	 * @param calSum    加保保额
	 * @date 2015-9-23
	 * @throws
	 */
	public Map<String, Object> queryAutoPolicyAddPremByWX(String policyNo, String  orderID);
	/**
	 * 用户登录 对应平台保全撤销（把该平台上客户未完成的保全项目撤销掉（待收费除外））
	 * 
	 */
	public void cancelPos(String applicantNo,String acceptChannelCode) ;
	/**
	 * 根据不同的保全项目，不同受理渠道查询可以操作的保单
	 * 
	 * @param policyNos
	 * @param serviceItems
	 * @param p_accept_channel_code
	 * @return
	 */
	public List<String> getCanDoPolicysByChannel(List<String> policyNos,
			String serviceItems,String p_accept_channel_code );
}
