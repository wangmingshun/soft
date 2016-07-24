package com.sinolife.esbpos.product;

public interface SmsReplyCallBackService {
	
	
	  /**
     * 短信回复
     * @param businessNo 业务号
     * @param id   短信信息序列号
     * @param mobile 手机号
     * @param replyMsg 回复内容
     * 
     * @return 成功与失败标志 Y-成功；N-失败
     */
    public String smsReplyCallBack(String businessNo,String id,String mobile,String replyMsg);


}
