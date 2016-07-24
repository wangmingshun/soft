package com.sinolife.pos.pub.service;

import java.util.List;

import com.sinolife.pos.pub.dto.UlinkPrice;

public interface UlinkPriceService {
	
	void addUnLinkPrice(UlinkPrice ulinkPrice)  ;
	void   uploadUnlinkPrice(Object excel) throws Exception;
    Integer ulinkPricePageCount(UlinkPrice ulinkPrice);
    List<UlinkPrice> ulinkPricePageData(UlinkPrice ulinkPrice);

}
