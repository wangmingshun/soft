package com.sinolife.esbpos.web;

import java.util.Map;

import com.sinolife.efs.util.pub.domain.CallResult;
import com.sinolife.sf.esb.EsbMethod;

public interface WebPosService {

	/**
	 * 保全完成时对现有官网电话进行修改
	 * 
	 * @param clientNo
	 * @return
	 */
	@EsbMethod(esbServiceId = "com.sinolife.efs.modifyUserMobileByClientNo")
	public void modifyUserMobile(String clientNo, String mobile);

	/**
	 * 根据客户号提供现有官网电话的接口
	 * 
	 * @param clientNo
	 * @return
	 */
	@EsbMethod(esbServiceId = "com.sinolife.efs.getUserMobileByClientNo")
	public String getUserMobileByClientNo(String clientNo);

	/**
	 * 根据客户号判断是否允许加挂保单
	 * 
	 * @param clientNo
	 * @param mobile
	 * @return map
	 */
	@EsbMethod(esbServiceId = "com.sinolife.efs.checkClientMobile")
	public Map<String, Object> checkClientMobile(String clientNo, String mobile);

	/**
	 * 获取与当前客户使用相同手机号的其它客户，排除家庭关系
	 * 
	 * @param clientNo
	 * @param mobile
	 * @return ArrayList<String>
	 */
	@EsbMethod(esbServiceId = "com.sinolife.efs.getOtherClientsUsingSamePhone")
	public Map<String, Object> getOtherClientsUsingSamePhone(String clientNo,
			String mobile);

	/**
	 * 退保检查及成功通知接口
	 * 
	 * @param reqMap
	 *            key-businessNo：1001-退保检查 1002-退保成功通知 key-policyNo： 保单号
	 *            key-applyNo: 投保单号
	 * @return CallResult<Map<String, String>> key-返回码 value-描述 resultCode-操作结果
	 *         resultMessage-结果描述 key-value: 1000-有未完成保全，不能进行退保 4000-没有找到该业务编码
	 *         5000-LEM接口异常 9999-操作成功
	 */

	@EsbMethod(esbServiceId = "com.sinolife.lem.surrenderNotify")
	public CallResult<Map<String, String>> surrenderNotify(
			Map<String, String> reqMap);

	/**
	 * 线下加保通知接口
	 * 
	 * @param reqMap
	 *            * key-policyNo： 保单号 类型String key-applyBarCode: 投保单号 类型String
	 *            key-preSum:本次保费 类型BigDecimal key-calSum:本次保额 类型BigDecimal
	 *            key-posNo:保全号 类型String key-effectDate：生效日期
	 *            类型：string，格式:yyyy-MM-dd hh:mi:ss
	 * @return CallResult<Map<String, String>> key-返回码 value-描述 resultCode-操作结果
	 *         resultMessage-结果描述 key-value: 9999-操作成功
	 */
	@EsbMethod(esbServiceId = "com.sinolife.lem.envetNoticeWithPolicyAdd")
	public  CallResult<Map<String, String>> envetNoticeWithPolicyAdd(
			Map<String, Object> reqMap);
    
    /**
     * 解除加挂保单
     * @param String policyNo 保单号,
     * @param client_no 客户号
     * @param channelType 渠道类型
     * @param  systemCode 数据源来源
     * @return resultCode  操作结果: Y-成功, N-失败
     *   resultMessage  返回消息
     */
    @EsbMethod(esbServiceId = "com.sinolife.efs.relieveAddPolicy")
    public abstract CallResult relieveAddPolicy(String policyNo, String client_no, String channelType,String systemCode);
    
   /**
    * 异步消息通知接口
    * @param map
    * @return
    */
	@EsbMethod(esbServiceId="com.sinolife.efs.biapp.sendAsynMessage")
	public Map<String, Object> sendAsynMessage(Map<String, Object> map);
	
	@EsbMethod(esbServiceId = "com.sinolife.efs.getUserInfoByClientNo")
    public Map<String, Object> getUserInfoByClientNo(Map<String, Object> map);

}
