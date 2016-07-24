package com.sinolife.pos.pubInterface.biz.service;

public interface ImageEventReceive  {

	public String receiveImageMessage(String imageType, String imageMainIndex,
			String barCode, String isFailedImage, String scanUserId,
			String branchCode, java.util.Date scanTime);

}
