package com.sinolife.esbpos.oa;

import java.util.List;
import java.util.Map;

import com.sinolife.sf.esb.EsbMethod;

/**
 * oa系统回审批结果回调接口
 * 审批结束才会回调POS系统，可以是不同意和同意。
 * @ClassName: GoaPosCallBackService
 * @Description: TODO
 * @author WangMingShun
 * @date 2015-11-20 下午04:24:02
 *
 */
public interface GoaPosCallBackService {

	/**
     * OA审批结果写入接口
     * @param flowId 流程Id
     * @param templateId 模板Id
     * @param templateVersion 模板版本
     * @param opinion 是否审批通过(Y/N)
     * @param remark  具体审批说明
     * @param createFlowParam 创建流程的参数
     * @param history 审批历史
     */
	@EsbMethod(esbServiceId="com.sinolife.pos.oaCallback")
    public void callBackFlowOver(String flowId, String templateId,
            String templateVersion, String opinion, String remark,
            Map<String, Object> createFlowParam,
            List<Map<String, Object>> history);
}
