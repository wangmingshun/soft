package com.sinolife.esbpos.product;

import java.util.Map;

import com.sinolife.sf.esb.EsbMethod;


public interface ProductService {
	
	
	
	 /**
     * 通过企业服务总线(ESB),实时发送需要回复的短信
     * @param map  key=BUSINESS_NO 业务号 (1)  ，key=MODULE_ID 模块ID(00006) ，key=ENV_NAME 环境名称，
     *             key=SERVICE_URL 回调服务url(com.sinolife.pos.serviceurl) ，key=SERVICE_ID 回调服务id(com.sinolife.pos.sms.sendCallSms) ，
     *             key=MOBILE 手机号 ，key=SEND_MSG 发送内容 
     * 
     * @return map key=BUSINESS_NO 业务号   ，key=ID 短信信息序列号 ，key=FLAG 成功与失败 Y-成功 N-失败
     * 
     */

	@EsbMethod(esbServiceId="com.sinolife.intf.sms.sendReplySms")
    public Map sendNeedReplySmsRealTime(Map map);

}
