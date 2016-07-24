package com.sinolife.pos.pubInterface.biz.esb.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sinolife.pos.common.dto.BankInfoDTO;
import com.sinolife.pos.common.dto.ClientPhoneDTO;
import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.PolicyProductPremForMccDTO;
import com.sinolife.pos.pubInterface.biz.dto.EsbClientPhoneInfoDTO;
import com.sinolife.pos.pubInterface.biz.dto.EsbPolicyContactInfoDTO;
import com.sinolife.pos.pubInterface.biz.dto.EsbServiceItemsInputDTO;
import com.sinolife.pos.pubInterface.biz.dto.PosApplyInfoDTO;
import com.sinolife.sf.esb.EsbMethod;

public interface MccpPosAcceptInterfaceEsb {
	/**
	 * 查询基表
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.queryCodeTableList")
	public List<CodeTableItemDTO> queryCodeTableList(String codeTableName);

	@EsbMethod(esbServiceId = "com.sinolife.pos.queryBranchTreeRoot")
	public List queryBranchTreeRoot(Map pMap);

	@EsbMethod(esbServiceId = "com.sinolife.pos.queryBranchTree")
	public List queryBranchTree(Map pMap);

	@EsbMethod(esbServiceId = "com.sinolife.pos.queryCityByProvince")
	public List<CodeTableItemDTO> queryCityByProvince(String province);

	@EsbMethod(esbServiceId = "com.sinolife.pos.queryAreaByCity")
	public List<CodeTableItemDTO> queryAreaByCity(String city);

	@EsbMethod(esbServiceId = "com.sinolife.pos.queryBranchInProvinceCity")
	public List queryBranchInProvinceCity(String province, String city,
			String channel);

	@EsbMethod(esbServiceId = "com.sinolife.pos.queryCounter")
	public List queryCounter(String branchCode);

	/**
	 * 财务接口10：查询银行代码列表接口
	 * 
	 * @param countryCode
	 *            国家代码（非空）
	 * @param provinceCode
	 *            省/直辖市代码（非空）
	 * @param cityCode
	 *            城市代码（非空）
	 * @param bankCategory
	 *            银行大类编码（非空）
	 * @return
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.queryBankList")
	public List<BankInfoDTO> queryBankList(String countryCode,
			String provinceCode, String cityCode, String bankCategory);

	/***
	 * 根据保单号查询保单犹豫期截止日 update:20140522 DMP-3766 根据保单号查询保单犹豫期截止日的ESB接口迁移到GQS系统
	 * 
	 * @param policy_no
	 * @return
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.querypCruplePeriodPolicy")
	public Map<String, Object> querypCruplePeriodPolicy(String p_policy_no);

	/**
	 * 查询保单挂失状态
	 * 
	 * @param policyNo
	 * @return {lossFlag[Y-N],lossDate}
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.policyLossStatus")
	public Map policyLossStatus(String policyNo);

	/**
	 * 保全试算外部接口
	 * 
	 * @param posInfo
	 * @param acceptUser
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.acceptDetailInputSubmit")
	public List<Map<String, Object>> acceptDetailInputSubmit(
			final EsbServiceItemsInputDTO esii, final PosApplyInfoDTO applyInfo);

	/**
	 * 经办结果确认
	 * 
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.processResultSubmit")
	public List<Map<String, Object>> processResultSubmit(
			EsbServiceItemsInputDTO esii, PosApplyInfoDTO applyInfo);

	/**
	 * 查询保单联系信息
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.queryEsbPolicyContactInfoList")
	public List<EsbPolicyContactInfoDTO> queryEsbPolicyContactInfoList(
			String clientNo, String policyNo);

	/**
	 * 根据客户号查询已经加挂的电话
	 * 
	 * @param clientNo
	 * @return
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.getUserMobileByClientNo")
	public String getUserMobileByClientNo(String clientNo);

	/**
	 * 根据客户号判断手机号是否允许加挂保单
	 * 
	 * @param clientNo
	 * @param mobile
	 * @return map (key:flag (Y/N),message)
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.checkClientMobile")
	public Map<String, Object> checkClientMobile(String clientNo, String mobile);

	/**
	 * 获取与当前客户使用相同手机号的其它客户，排除家庭关系，自保件
	 * 
	 * @param clientNo
	 * @param phoneNo
	 * @return ArrayList<EsbClientPhoneInfoDTO>
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.getDifClientPhoneInfoByMobile")
	public ArrayList<EsbClientPhoneInfoDTO> getDifClientPhoneInfoByPhoneNo(
			String clientNo, String phoneNo);

	/**
	 * 根据MCC返回的客户电话列表停用电话
	 * 
	 * @param ArrayList
	 *            <EsbClientPhoneInfoDTO>
	 * @return Map<String, Object>
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.stopClientPhoneInfo")
	public Map<String, Object> stopClientPhoneInfo(
			ArrayList<EsbClientPhoneInfoDTO> clientPhoneList);

	/**
	 * 根据批次号返回客户保单列表
	 * 
	 * @param batch_no
	 * @return ArrayList<Map<String, String>>
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.getStopClientPolicy")
	public ArrayList<Map<String, String>> getStopClientPolicy(String batch_no);

	/**
	 * 根据批次号，电话号码 查询需要恢复的客户电话列表
	 * 
	 * @param BATCH_NO
	 * @param phoneNo
	 * @return ArrayList<EsbClientPhoneInfoDTO>
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.getClientPhoneInfoByBachtNo")
	public ArrayList<EsbClientPhoneInfoDTO> getClientPhoneInfoByBachtNo(
			String batchNo, String phoneNo);

	/**
	 * 根据MCC返回的需要恢复的客户电话列表 恢复停用电话
	 * 
	 * @param ArrayList
	 *            <EsbClientPhoneInfoDTO>
	 * @return Y/N
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.rollbackClientPhoneInfo")
	public String rollbackClientPhoneInfo(
			ArrayList<EsbClientPhoneInfoDTO> clientPhoneList);

	/**
	 * 检查电话号码的格式是否正确
	 * 
	 * 
	 * @param List
	 *            <ClientPhoneDTO> 需要检查的号码信息列表
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.checkPhone")
	public List<ClientPhoneDTO> checkPhone(List<ClientPhoneDTO> phoneList);

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
	@EsbMethod(esbServiceId = "com.sinolife.pos.queryPosNoteHistory")
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
	@EsbMethod(esbServiceId = "com.sinolife.pos.getLoadFileId")
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
	@EsbMethod(esbServiceId = "com.sinolife.pos.resendELetter")
	public String resendELetter(String clientNo, String policyNo,
			String detailSequenceNo);

	/**
	 * 函件发送记录查询接口
	 * 
	 * @param detailSequenceNo
	 * @return List<Map<String, Object>> KEY:EMAIL --邮箱地址 KEY:SEND_DATE --发送时间
	 *         KEY:BUSINESS_NO --业务号
	 * @author GaoJiaMing
	 * @time 2014-11-19
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.getMailLogByDetailSequenceNo")
	public List<Map<String, Object>> getMailLogByDetailSequenceNo(
			String detailSequenceNo);

	/**
	 * 取消保单自动退保
	 * 
	 * @param policyNo
	 * @return Map<String, Object>
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.cancelAutoSurrender")
	public Map<String, Object> cancelAutoSurrender(String policyNo);

	/**
	 * 根据保单号查保单自垫及借款信息
	 * 
	 * @param policyNo
	 *            key loanAllSum 借款本金 key interestSum 借款款利息 key aplLoanAllSum
	 *            自垫本息 key aplInterestSum 自垫利息 key aplExtraFee 应补差额保费 key
	 *            loanPayBackSum 还款金额
	 * @return Map<String,Object>
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.getAplLoanAndAplExtraFeeInfo")
	public Map<String, Object> getAplLoanAndAplExtraFeeInfo(String policyNo);

	/**
	 * 根据保单号查询该保单下可以做收付款方式调整失败的所有保全项目 Key POSNO 保全号
	 * 
	 * @param policyNo
	 *            保单号 Key SERVICEITEMNAME 保全项目名称 Key ACCOUNTNOTYPE 卡类型 Key
	 *            PREMNAME 账户 Key BANKNAME 银行 Key ACCOUNTNO 账号
	 * @return Map<String, Object>
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.getPayServiceItemsByPolicyNo")
	public List<Map<String, Object>> getPayServiceItemsByPolicyNo(
			String policyNo);

	/**
	 * 根据保全号查询原收付款信息
	 * 
	 * @param posNo
	 *            保全号 Key SERVICEITEMNAME 保全项目名称 Key PREMSUM 金额 Key PAYMENTTYPE
	 *            原收付方式 Key PREMFLAG 原收付标志 Key PREMNAME 原户主 Key BANKNAME 原转账银行
	 *            Key ACCOUNTNO 原转账账户 Key PREMSUCCESSSTATUS 原转账状态
	 * @return Map<String, Object>
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.getOriginalPaymentInfoByPosNo")
	public Map<String, Object> getOriginalPaymentInfoByPosNo(String posNo);

	/**
	 * 根据条件查询可取消转账暂停数据
	 * 
	 * @param policyNo
	 * @return Map<String, Object>
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.getCancelTransferStopData")
	public List<Map<String, Object>> getCancelTransferStopData(String clientNo,
			String posNo, String policyNo);

	/**
	 * 取消转账暂停操作
	 * 
	 * @param policyNo
	 *            key result 成功失败标志 key message 附加信息
	 * @return Map<String, Object>
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.cancelTransferStopOperation")
	public Map<String, Object> cancelTransferStopOperation(String posNo);

	/**
	 * VIP赠险回访 查询客户VIP信息
	 * 
	 * @param clientNo
	 *            客户号 key VIPEFFECTDATE VIP评定日 key VIPGRADE VIP等级 key
	 *            VIPGRADENAME VIP等级描述 key VIPPLAN VIP方案
	 * @return List<Map<String, Object>>
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.getClientVIPInfoByClientNo")
	public Map<String, Object> getClientVIPInfoByClientNo(String clientNo);

	/**
	 * 收付款失败回访 查询保单保全列表
	 * 
	 * @param clientNo
	 *            客户号 key POSNO 保单号 key POLICYNO 保全号 key PROJECTNAME 保全项目 key
	 *            ACCEPTDATE 受理日期 key ACCEPTSTATUSCODE 受理状态 key
	 *            ACCEPTSTATUSDESCRIPTION 受理状态描述
	 * @return List<Map<String, Object>>
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.getPayFailPolicyInfoByClientNo")
	public List<Map<String, Object>> getPayFailPolicyInfoByClientNo(
			String clientNo);

	/**
	 * 查询当前保单的缴费频次
	 * 
	 * @param policyNo
	 * @return Frequency 缴费频次
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.getFrequencyByPolicyNo")
	public List<CodeTableItemDTO> getFrequencyByPolicyNo(String policyNo);

	/**
	 * 查询保单险种缴费信息列表
	 * 
	 * @param policyNo
	 * @return List<PolicyProductPremDTO>保单险种缴费信息
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.getProductPremListByPolicyNo")
	public List<PolicyProductPremForMccDTO> getProductPremListByPolicyNo(
			String policyNo);
	
	/**
	 * 根据保单号查询主险年利率，取最新时间的
	 * @return BigDecimal
	 * @author WangMingShun
	 * @date 2016-2-24
	 */
	@EsbMethod(esbServiceId = "com.sinolife.pos.getPrimaryPlanYearRateByPolicyNo")
	public BigDecimal getPrimaryPlanYearRateByPolicyNo(String policyNo);
}
