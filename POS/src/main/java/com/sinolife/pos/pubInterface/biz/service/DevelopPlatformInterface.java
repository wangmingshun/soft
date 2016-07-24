package com.sinolife.pos.pubInterface.biz.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.indigopacific.idg.IDGClientException;
import com.sinolife.efs.rpc.domain.UserInfo;
import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.FinancialProductsDTO;
import com.sinolife.pos.common.dto.PolicyDTO;
import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.common.dto.PosApplyFilesDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.PosPolicy;
import com.sinolife.pos.common.dto.PosProblemItemsDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_1;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_19;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_2;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_21;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_23;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_24;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_26;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_28;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_29;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_33;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_35;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_37;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_38;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_39;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_45;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_5;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_6;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_9;
import com.sinolife.sf.store.SFFile;

public interface DevelopPlatformInterface {

	/**
	 * 查询保全员
	 * 
	 * @param user
	 * @return
	 */
	public Map queryUserPrivsInfo(String user);

	/**
	 * 查询用户受理权限
	 * 
	 * @param user
	 * @return Y/N
	 */
	public String queryUserPosAcceptPrivs(String user);

	/**
	 * 根据证件号码查询客户信息
	 * 
	 * @param idTypeCode
	 *            id类型
	 * @param idNo
	 * @return
	 */
	public List<Map<String, Object>> getClientinfoProById(String idTypeCode,
			String idNo);

	/**
	 * 根据客户姓名查询
	 * 
	 * @param clientName
	 *            客户性别
	 * @param sex
	 *            性别
	 * @param birthday
	 *            生日
	 * @return
	 */
	public List<Map<String, Object>> getClientinfoProbyName(String clientName,
			String sex, Date birthday);

	/**
	 * 根据客户号查询
	 * 
	 * @param clientNo
	 *            客户号
	 * @return
	 */
	public List<Map<String, Object>> getClientinfoProbyClientNo(String clientNo);

	/**
	 * 根据保单号查询
	 * 
	 * @param policyNO
	 * @param applicantOrInsured
	 *            投，被保险人
	 * @return
	 */
	public List<Map<String, Object>> getClientinfoProbyPolicyNo(
			String policyNO, String applicantOrInsured);

	/**
	 * @param clientNo
	 *            客户号
	 * @param clientWill
	 *            是否考虑客户愿意接收短信：Y/N
	 * @return
	 */
	public String getClientMobile(String clientNo, String clientWill);

	/**
	 * @param policyNo
	 *            保单号
	 * @return 是否有未完成保全 Y/N
	 */
	public String isposacceptflag(String policyNo);

	/**
	 * 查询未结案保全信息
	 * 
	 * @param clientNo
	 *            客户号
	 * @return List<PosInfoDTO>
	 */
	public List<PosInfoDTO> queryNotCompletePosInfoRecord(String clientNo);

	/**
	 * 根据投被保人客户号查询 ：保单号，主险名，投保人姓名，被保人姓名
	 * 
	 * @param clientNo
	 * @return
	 */
	public List<Map<String, String>> PolicyNoListByClientno(String clientNo,
			String clientType);

	/**
	 * 查询 客户名下的保单至少有一张是由该服务人员服务
	 * 
	 * @param client_no
	 *            客户号
	 * @param agent_no
	 *            代理人编号
	 * @return 是否 Y/N
	 */
	public String isagenthavepolicy(String client_no, String agent_no);

	/**
	 * 查询有无其他客户手机号和此客户相同
	 * 
	 * @param client_no
	 *            客户号
	 * @param agent_no
	 *            手机号
	 * @return 有无:Y/N
	 */
	public String isotherclientphone(String client_no, String phone_no);

	/**
	 * 移动保全受理信息处理
	 * 
	 * @param applyBatchInfo
	 * @return
	 */
	public PosApplyBatchDTO createBatchHeader(PosApplyBatchDTO applyBatchInfo);

	/**
	 * 批量受理信息录入
	 * 
	 * @param applyBatchInfo
	 * @param mposMobile
	 * @return
	 */
	public PosApplyBatchDTO insertBatchAccept(PosApplyBatchDTO applyBatchInfo,
			String mposMobile);

	/**
	 * 投保，处理规则检查及流转结果
	 * 
	 * @param ServiceItemsInputDTO
	 *            sii
	 * @param PosApplyBatchDTO
	 *            applyBatchInfo
	 * @return
	 */
	public Map<String, Object> acceptInternal(ServiceItemsInputDTO sii,
			PosApplyBatchDTO applyBatchInfo);

	/**
	 * 经办结果确认
	 * 
	 * @param processResultList
	 * @param itemsInputDTO
	 * @return
	 */
	public Map<String, Object> processResultSubmit(
			List<Map<String, Object>> processResultList,
			ServiceItemsInputDTO itemsInputDTO);

	/**
	 * 断点恢复，用于代办任务时，重新恢复到最后的处理点
	 * 
	 * @param posBatchNo
	 * @return
	 */
	public Map<String, Object> continueFromBreak(String posBatchNo);

	/**
	 * 查询客户下所有有效手机号码
	 * 
	 * @param client_no
	 * @return
	 */
	public List<String> getClientMobileAll(String client_no);

	/**
	 * 受理撤销，对于已经是结束状态的受理，强制改掉受理状态为C02
	 * 
	 * @param posInfo
	 * @param acceptUser
	 */
	public void cancelAccept(PosInfoDTO posInfo, String acceptUser);

	/**
	 * 受理撤销并重新生成受理
	 * 
	 * @param posNo
	 * @param rollbackCause
	 *            撤销原因
	 * @param rollbackCauseDetail
	 *            撤销原因描述
	 * @param acceptUser
	 *            受理用户
	 * @return
	 */
	public Map<String, Object> cancelAndReaccept(String posNo,
			String rollbackCause, String rollbackCauseDetail, String acceptUser);

	/**
	 * 录受理明细时的撤销
	 */
	public Map<String, Object> cancelService(final String posNo,
			final String cancelCause, final String cancelRemark);

	/**
	 * 经办结果撤销
	 * 
	 * @param cancelCause
	 * @param cancelRemark
	 * @param processResultList
	 * @param itemsInputDTO
	 * @return
	 */
	public Map<String, Object> processResultCancel(String cancelCause,
			String cancelRemark, List<Map<String, Object>> processResultList,
			ServiceItemsInputDTO itemsInputDTO);

	/**
	 * 查询待受理录入保全件列表
	 * 
	 * @param acceptor
	 *            受理人
	 * @return List&lt;Map&lt;String, Object&gt;&gt;
	 */
	public List<Map<String, Object>> queryAcceptEntryTodolist(String acceptor);

	/**
	 * 红利方式选择经办信息录入页面
	 */
	public ServiceItemsInputDTO_26 queryServiceItemsInfo_26(
			ServiceItemsInputDTO serviceItemsInputDTO);

	/**
	 * 挂失及解挂经办信息录入页面
	 */
	public ServiceItemsInputDTO_28 queryServiceItemsInfo_28(
			ServiceItemsInputDTO serviceItemsInputDTO);

	/**
	 * 保单还款经办信息录入页面
	 */
	public ServiceItemsInputDTO_9 queryServiceItemsInfo_9(
			ServiceItemsInputDTO serviceItemsInputDTO);

	/**
	 * 续期交费方式变更经办信息录入页面
	 */
	public ServiceItemsInputDTO_23 queryServiceItemsInfo_23(
			ServiceItemsInputDTO serviceItemsInputDTO);

	/**
	 * 联系方式变更经办信息录入页面
	 */
	public ServiceItemsInputDTO_19 queryServiceItemsInfo_19(
			ServiceItemsInputDTO serviceItemsInputDTO);

	/**
	 * 预约终止附加险经办信息录入页面
	 */
	public ServiceItemsInputDTO_29 queryServiceItemsInfo_29(
			ServiceItemsInputDTO serviceItemsInputDTO);

	/**
	 * 保全收付费方式调整经办信息录入页面
	 */
	public ServiceItemsInputDTO_33 queryServiceItemsInfo_33(
			ServiceItemsInputDTO serviceItemsInputDTO);

	/**
	 * 追加保费
	 */
	public ServiceItemsInputDTO_35 queryServiceItemsInfo_35(
			ServiceItemsInputDTO serviceItemsInputDTO);

	/**
	 * 犹豫期退保经办信息录入页面
	 */
	public ServiceItemsInputDTO_1 queryServiceItemsInfo_1(
			ServiceItemsInputDTO serviceItemsInputDTO);

	/**
	 * 退保经办信息录入页面
	 */
	public ServiceItemsInputDTO_2 queryServiceItemsInfo_2(
			ServiceItemsInputDTO serviceItemsInputDTO);

	/**
	 * 根据posNo查询受理录入页面的详细信息
	 * 
	 * @param posNo
	 * @return
	 */
	public ServiceItemsInputDTO queryAcceptDetailInputByPosNo(String posNo);

	/**
	 * 查询批次中第一 个状态为A01的受理
	 * 
	 * @param posBatchNo
	 *            批次号
	 * @return
	 */
	public String getNextPosNoInBatch(String posBatchNo);

	/**
	 * 获取字典表信息
	 * 
	 * @param codeTableName
	 * @return
	 */

	public List<CodeTableItemDTO> queryCodeTable(String codeTableName);

	/**
	 * 查询批次受理断点位置
	 * 
	 * @param posBatchNo
	 *            批次号
	 * @return Map&lt;String, Object&gt;包含Key:<br/>
	 *         breakStatusCode:断点的保全状态<br/>
	 *         posNo:批次受理时需跳转的保全号<br/>
	 *         processResultList:预处理完成时跳转时需要的经办结果
	 */
	public Map<String, Object> queryBreakInfoByPosBatchNo(String posBatchNo);

	/**
	 * 查询保全申请批次信息
	 * 
	 * @param posNo
	 * @return
	 */
	public PosApplyBatchDTO queryPosApplyBatchRecord(String posBatchNo);

	/**
	 * 查询保全申请书信息
	 * 
	 * @param posNo
	 * @return
	 */
	public PosApplyFilesDTO queryPosApplyfilesRecord(String barcodeNo);

	/**
	 * 查询保全信息
	 * 
	 * @param posNo
	 * @return
	 */
	public PosInfoDTO queryPosInfoRecord(String posNo);

	/**
	 * 查询简要批次受理信息
	 * 
	 * @param posBatchNo
	 * @return
	 */
	public List<Map<String, String>> queryBatchPosInfo(String posBatchNo);

	/**
	 * 查询保全受理信息
	 * 
	 * @param posNo
	 * @return
	 */
	public List<String> queryPosAcceptDetailInfo(String posNo,
			String posObject, String itemNo);

	/**
	 * 根据posNo查询detail表信息
	 * 
	 * @param posNo
	 * @return
	 */
	public List<Map<String, Object>> getPosAcceptDetailByPosNo(String posNo);

	/**
	 * 查询保全员相关的保全信息列表
	 * 
	 * @param acceptor
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<Map<String, Object>> queryBatchAcceptList(String acceptor,
			Date startDate, Date endDate);

	// 查询前一单是否已经受理完成
	public boolean prePosnoIsEnd(String posNo);

	/**
	 * 查询保单状态
	 * 
	 * @param policyNo
	 * @return
	 */
	public String getPolicyDutyStatus(String policyNo);

	/**
	 * 生成CMP保全用的条码
	 * 
	 * @param serviceItems
	 * @return
	 */
	public String generateCMPBarcode();

	/**
	 * 查询移动保全问题件代办列表
	 * 
	 * @param accessor
	 *            处理人ID
	 * @return List&lt;PosProblemItemsDTO&gt;
	 */
	public List<PosProblemItemsDTO> queryProblemTodoListForMPos(String accessor);

	/**
	 * 问题件处理，回复处理
	 * 
	 * @param problemItemsDTO
	 */
	public void replyProblem(PosProblemItemsDTO problemItemsDTO);

	/**
	 * 根据问题件号查询问题件
	 * 
	 * @param problemItemNo
	 * @return
	 */
	public PosProblemItemsDTO queryProblemItemsByID(String problemItemNo);

	/**
	 * 生成批单文件流
	 * 
	 * @param posNo
	 * @param submiter
	 * @return
	 */
	public SFFile createOutputStreamForEnt(String posNo, String submiter);

	/**
	 * 处理电子批单 1.根据保全号获取电子批单 2.电子批单加盖电子印章 3.电子批单上传服务器 4.发送邮件
	 */
	public List<Map<String, String>> handPDFEnt(String posNo, UserInfo userInfo)
			throws IOException, IDGClientException;

	/**
	 * 电子批单加盖电子印章
	 */
	public SFFile signature(SFFile file, String po) throws IOException,
			IDGClientException;

	/**
	 * 上传电子保单到IM服务器
	 */
	public String saveFile(String posNo, String saveBy, SFFile sigFile);

	/**
	 * 发送电子批单邮件
	 */
	public void sendPosEntMail(String email, String posNo, String userName,
			List<Map<String, Object>> sffileList);

	/**
	 * 查询是否淘宝通过网销操作保全（退保，契撤）的保单
	 * 
	 * @param policy_no
	 * @param prodSeq
	 * @return Y/N
	 */
	public String isTbToWxPolicy(String policyNo, String prodSeq);

	/**
	 * 根据保单号查询支付宝账号
	 * 
	 * @param policy_no
	 * @param
	 * @return
	 */
	public String getZhiFuBaoAccount(String policyNo);
	/**
	 * 查询保单原缴费账户信息
	 * 
	 * @param policyNo
	 * @return
	 */
	public Map getPolicyBankacctno(String policyNo);
	/**
	 * 年金给付方式选择  经办信息录入页面
	 */
	public ServiceItemsInputDTO_24 queryServiceItemsInfo_24(
			ServiceItemsInputDTO serviceItemsInputDTO) ;
	
	/**
	 * 账户分配比例变更  经办信息录入页面
	 */
	public ServiceItemsInputDTO_38 queryServiceItemsInfo_38(
			ServiceItemsInputDTO serviceItemsInputDTO);
	/**
	 * 投资账户转换 经办信息录入页面
	 */
	public ServiceItemsInputDTO_39 queryServiceItemsInfo_39(
			ServiceItemsInputDTO serviceItemsInputDTO);
	/**
	 * 客户资料变更变更  经办信息录入页面
	 */
	public ServiceItemsInputDTO_21 queryServiceItemsInfo_21(
			ServiceItemsInputDTO serviceItemsInputDTO);
	/**
	 * 查询新增附加险保单初始信息
	 */
	public ServiceItemsInputDTO_5 queryServiceItemsInfo_5(
			ServiceItemsInputDTO serviceItemsInputDTO);
			
	/**
	 *部分领取经办信息录入页面
	 */
	public ServiceItemsInputDTO_37 queryServiceItemsInfo_37(
			ServiceItemsInputDTO serviceItemsInputDTO);
	/**
	 * 根据保单号查询保单详细信息
	 * 
	 * @param policyNo
	 * @return
	 */
	public PolicyDTO queryPolicyInfoByPolicyNo(String policyNo);
	
	/**
	 * 判断该保单是否包含万能产品
	 * 
	 * @param policyNo
	 * @return  boolean
	 */
	public boolean isIncludeFinanceProduct(String policyNo);
	
	/**
	 * 保单回执签收完成接口
	 * 
	 * @param policyNo
	 * @return Map<String,Object>
	 * @author Zhangyi
	 * @time 2015-3-24
	 */
	public Map<String, Object> receivePolicyMessage(String policyNo);

	/**
	 * 回写电子签名回执的file_id
	 * 
	 * @param policyNo
	 * @param eSignatureFileId
	 * @return Map<String,Object>
	 * @author Zhangyi
	 * @time 2015-3-24
	 */
	public Map<String, Object> updateESignaturePolicyFileId(String policyNo, String eSignatureFileId);

	/**
	 * 代理人的电子回执回销记录查询
	 * 
	 * @param startDate
	 * @param endDate
	 * @param flag
	 * @param policyNo
	 * @param agentNo
	 * @return List<Map<String,Object>>
	 * @author Zhangyi
	 * @time 2015-3-25
	 */
	public List<Map<String, Object>> queryReceivePolicyMessageList(Map param);

	/**
	 * 获取当前保单保签收信息
	 * 
	 * @param policyNo
	 * @param agentNo
	 * @return Map<String,Object>
	 * @author Zhangyi
	 * @time 2015-3-26
	 */
	public Map<String, Object> queryReceivePolicyInfo(String policyNo, String agentNo);
	/**
	 *    --功能说明：
   *    取理财险的账户价值明细
   *注意:对于投连险种,如不能实时交易,则以最近交易价格进行估算
   *参数说明：
   *   输入参数：
   *        p_policy_no    ：保单号
   *        p_calc_date    : 计算日期(受理申请日期)
   *   输出参数：
   * p_fin_type                理财类型   N 非理财 ，U 万能，F投连  
   *  p_first_prem              首期保费,
   *  p_first_add_prem          首期追加,
   *  p_add_prem                保全追加,
   *  p_withdraw_prem           保全部分领取,
   *  p_total_amount            账户价值,
   *  p_benefit_amount          累计收益,
   *    p_flag                    返回标志    '0' 成功     '1' 失败0
   *    p_message                 报错信息
   * 
   */
	public Map<String, Object> getFinValueInfo(String policyNo, Date requestDate);
	/**
	 * 查询投连账户信息
	 * 
	 * @param policyNo
	 * @return
	 */
	public List<FinancialProductsDTO> getFinancialProductsList(String policyNo,
			String prodSeq) ;
	/**
	 * 查询VIP等级,卡号
	 * 
	 * @param clientNo
	 *            客户号
	 * @return Map&lt;String, Object&gt;包含如下key:<br/>
	 *         p_client_no: 客户号<br/>
	 *         p_vip_grade:VIP等级代码<br/>
	 *         p_vip_desc:VIP等级描述<br/>
	 *         p_card_no：vip卡号
	 *         p_flag:成功标志 0:成功 1:失败<br/>
	 *         p_message:返回信息
	 */
	public Map<String, Object> getVipInfo(String clientNo) ;
	/**
	 * 查询VIP等级,卡号
	 * 
	 * @param 
	 *       p_client_no                         :vip客户号(非空)
     *       p_card_no                           :vip卡号(非空)
     *       p_modified_date                     ：变更时间
     *       p_modified_reason                   ：变更原因
     *       p_provided_user                     ：发放人
     *       p_start_date                       :开始时间
	 * @return 
	 *         p_flag:成功标志 0:成功 1:失败<br/>
	 *         p_message:返回信息
	 */
	public Map<String, Object> updateVipCard(String p_client_no,String p_card_no,Date p_modified_date,String p_modified_reason, String  p_provided_user,Date p_start_date);
	/**
	 * 取理财险某个时间段的账户价值明细
	 * @methodName: getFinValueInfoList
	 * @Description: 
	 * @param policyNo				:保单号
	 * @param startDate				:开始日期
	 * @param endDate				:结束日期
	 * @return List<Map<String,Object>> 
	 * @author WangMingShun
	 * @date 2015-6-30
	 * @throws
	 */
	public List<Map<String, Object>> getFinValueInfoList(String policyNo, Date startDate, Date endDate);
	/**
	 * 查询保单是否处于犹豫期内
	 * @param policyNo
	 * 			保单号
	 * @return 
	 * 		p_item_type : 保单是否过犹豫期  1:犹豫期内；2:犹豫期外
	 * 		p_flag:成功标志 0:成功 1:失败<br/>
	 * 		p_message:返回信息
	 */
	public Map<String, Object> getPolicyScrupleDate(String policyNo) ;
	/**
	 * 根据保单号、计算日期,部分领取金额计算手续费
	 * 
	 * @param policyNo
	 *            保单号
	 * @param    p_withdraw_amount 领取金额
	 * @param calcDate
	 *            计算日期
	 * @return Map<String, Object> 包含如下Key：<br/>
              p_withdraw_charge_amount     :领取手续费金额
              p_calculate_formula          :计算公式
              p_calculate_parameter        :计算参数值
              p_flag                       :返回标志   '0'  成功   ,'1'  失败，调用者需要进行异常处理                                   
              p_message                    :返回信息
	 */
	public Map<String, Object> calcWithdrawCharge(String policyNo,String p_withdraw_amount,
			Date calcDate);
	
	/**
	 *  保单号查询保全信息 (参数Map里面只能传policyNo   保单号)
	 */
	public List<PosPolicy> getPosPolicyList(Map map);
	
	/**
	 * @Description: 根据客户号和产品代码获取对应的保单号
	 * @methodName: getPosPolicyList
	 * @param map
	 * @return
	 * @return List<String>
	 * @author WangMingShun
	 * @date 2015-11-17
	 * @throws
	 */
	public List<String> getPolicyListByClientNoAndProductCode(
			String clientNo, String productCode);
	
	/**
	 * 根据客户号获取客户对应的资产证明通知书流
	 * @return Map<String, Object> 包含如下key:
	 * 		flag(String)				:0表示成功获取数据，1表示没有获取到数据
	 * 		msg(String)					:失败的时候才会得到失败信息
	 * 		file(File)					:成功的时候才会得到该文件
	 * @author WangMingShun
	 * @date 2016-2-22
	 */
	public Map<String, Object> getClientWeathNoticeStream(String clientNo);
	
	/**
	 * 官网复效是否需要职业告知
	 * @param policyNo
	 * @return String	Y-是	N-否
	 * @author TangJing
	 * @date 2016-5-3
	 */
	public String websiteAnswerEffect(String policyNo);
	
	/**
	 * 官网复效是否需要投保人告知
	 * @param policyNo
	 * @return String	Y-是	N-否
	 * @author TangJing
	 * @date 2016-5-3
	 */
	public String websiteAnswerEffect2(String policyNo);
	
	/**
	 *复效经办信息录入页面
	 */
	public ServiceItemsInputDTO_6 queryServiceItemsInfo_6(
			ServiceItemsInputDTO serviceItemsInputDTO);
	
	/**
	 * 核心系统预约退保
	 * @param serviceItemsInputDTO
	 * @return ServiceItemsInputDTO_45
	 * @author TangJing
	 * @date 2016-5-30
	 */
	public ServiceItemsInputDTO_45 queryServiceItemsInfo_45(
			ServiceItemsInputDTO serviceItemsInputDTO);
	
	/**
	 * 官网复效是否满足二核规则
	 * @param p_policy_no
	 * @return String
	 * @author TangJing
	 * @date 2016-5-30
	 */
	public String checkPolicyClientRisk(String p_policy_no);
	
	/**
	 * 生成移动保全保全用的条码
	 * 
	 * @param serviceItems
	 * @return
	 */
	public String generateMPBarcode();
	/**
	 * 保全试算外部接口
	 * 
	 * @param posInfo
	 * @param acceptUser
	 */
	public Map<String, Object> acceptDetailInputSubmit(
			final ServiceItemsInputDTO sii, final PosApplyBatchDTO applyInfo,
			final String mposMobile);
}
