package com.sinolife.esbpos.mcc;

import java.util.List;
import java.util.Map;

import com.sinolife.sf.esb.EsbMethod;

public interface MccPosService {
	/**
	 * 根据客户号查询呼入记录
	 * @param  clientNo 客户号
	 * @return Map中参数说明如下：
	 * taskId       任务号
	 * activityDesc 所属活动
	 * dealUserId   座席ID
	 * dealUserName 座席姓名
	 * callTime     来电时间
	 * channelDesc  服务渠道
	 * taskStatus   任务状态
	 * lineDesc     所属专线
	 */
	@EsbMethod(esbServiceId="com.mcc.MccServiceEsb.getCallInRecord")
	public List<Map<String, Object>> getCallInRecord(String clientNo);
	
	/**
	 * 根据任务号查询服务明细
	 * @param taskId 任务号
	 * @return Map中参数说明如下：
	 * seq          序号
	 * taskTypeDesc 任务类型
	 * serviceName  服务名称
	 * serviceStatus服务状态
	 * policyNo     保单号
	 * businessNo   业务号
	 */
	@EsbMethod(esbServiceId="com.mcc.MccServiceEsb.getServiceDetail")
	public List<Map<String, Object>> getServiceDetail(String taskId);

}
