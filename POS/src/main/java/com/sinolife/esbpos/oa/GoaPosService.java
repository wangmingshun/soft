package com.sinolife.esbpos.oa;

import java.util.List;
import java.util.Map;

import com.sinolife.sf.esb.EsbMethod;

public interface GoaPosService {
	
	/**
	 * @Description: 将审批流程写入GOA系统
	 * @methodName: createFlow
	 * @param tid 模板号
	 * @param templateVersion 版本号
	 * @param createUser 提交用户
	 * @param title 标题
	 * @param content 审批内容
	 * @param attachements Map<附件名,URL> fileName,filePath
	 * @param otherParam 
	 * @param approverList 审批链
	 * @return Map<String,String>
	 */
	@EsbMethod(esbServiceId = "com.sinolife.goa.CreateApproveWithApprover")
	public Map<String, String> createFlow(String tid, String templateVersion,
			String createUser, String title, String content,
			Map<String, String> attachements, 
			Map<String, String> otherParam,
			List<Map<String, Object>> approverList); 

	/**
	  * 获取审批单历程
	  * 
	  * @param approveId  审批单ID
	  * @return List<Map<String, Object>>  公文在OA中的审批历程信息
	  */
	@EsbMethod(esbServiceId = "com.sinolife.goa.getApproveHistory")
	public List<Map<String, Object>> getApproveHistory(String approveId); 

}
