package com.sinolife.esbpos.product;

public interface SmsSatisfactionCallBackService {
	
	
	/**
     * 短信回复进行客户满意度写入
     *  注：一旦涉及到短信回复的必须新建一个类，该类中必须存在这个方法，方法名，参数都不可变。
     * @param businessNo 业务号
     * @param id   短信信息序列号
     * @param mobile 手机号
     * @param replyMsg 回复内容
     * 
     * @return 成功与失败标志 Y-成功；N-失败
     */
    public String smsReplyCallBack(String businessNo,String id,String mobile,String replyMsg);


}
