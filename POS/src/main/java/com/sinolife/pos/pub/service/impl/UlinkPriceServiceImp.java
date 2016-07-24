package com.sinolife.pos.pub.service.impl;


import java.io.File;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.pos.pub.dao.UlinkPriceDao;
import com.sinolife.pos.pub.dto.UlinkPrice;
import com.sinolife.pos.pub.service.UlinkPriceService;
import com.sinolife.sf.framework.comm.BizException;
import com.sinolife.sf.framework.excel.read.CellIterator;
import com.sinolife.sf.framework.excel.read.ReadableWorkbookProcesser;


@Service("ulinkPriceService")
public class UlinkPriceServiceImp implements UlinkPriceService {
	private static final Logger logger = Logger.getLogger(UlinkPriceServiceImp.class);
	@Autowired
	private UlinkPriceDao ulinkPriceDao;
	
	public void addUnLinkPrice(UlinkPrice ulinkPrice) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("p_price_date", ulinkPrice.getPriceDate());
		map.put("p_assets_code", ulinkPrice.getAssetsCode());
		map.put("p_sold_price", ulinkPrice.getSoldPrice());
		map.put("p_tot_assets_value", ulinkPrice.getTotAssetsValue());
		map.put("p_real_assets_value", ulinkPrice.getRealAssetsValue());
		Map<String, Object> resultmap = ulinkPriceDao.addUlinkPrice(map);
		if (resultmap.get("p_flag")!=null&&resultmap.get("p_flag").equals("1")) {
				// 如果失败，回滚
				logger.error(map.get("p_assets_code")+"/"+map.get("p_price_date")+":增加投连价格记录时出错:"
						+ resultmap.get("p_message"));
				throw new BizException(""+ resultmap.get("p_message"));
	  }
	}
	
	@Transactional
	public void   uploadUnlinkPrice(Object excel) throws Exception{
		
		ReadableWorkbookProcesser.iterator((File)excel, new CellIterator(){
			UlinkPrice ulinkPrice = new UlinkPrice();
			public String[] awareSheet(String[] sheetNames)
			{
				return sheetNames;
			}
			//整个Cell迭代顺序是 从左上角到右下角的顺序
			
			public void onCell(int row, int col, String content) {

				if(row==2){
					switch(col){
					case 0:{
						if(null!=content&&!"".equalsIgnoreCase(content))
						ulinkPrice.setPriceDate(Date.valueOf(content));
						break;
					}
					default:{
						break;
					}
					}
				}
				
				if(row>=5){
					switch(col){
					case 1:{
						if(null!=content&&!"".equalsIgnoreCase(content))
						ulinkPrice.setRealAssetsValue(Double.parseDouble(getNewString(content)));
						break;
					}
					case 3:{
						if(null!=content&&!"".equalsIgnoreCase(content))
							ulinkPrice.setSoldPrice(Double.parseDouble(getNewString(content)));
						break;
					}
					case 4:{
						if(null!=content&&!"".equalsIgnoreCase(content))
							ulinkPrice.setTotAssetsValue(Double.parseDouble(getNewString(content)));
						break;
					}
					case 6:{

                            ulinkPrice.setAssetsCode(content);
								if(ulinkPrice.getAssetsCode()!=null&&!"".equals(ulinkPrice.getAssetsCode())){
									try{
										addUnLinkPrice(ulinkPrice);
									}catch(Exception e){
										logger.error("资产代码:"+ulinkPrice.getAssetsCode()+"/资产评估日:"+ulinkPrice.getPriceDate()+":增加投连价格记录失败:"+e.getMessage());
										throw new BizException("资产代码:"+ulinkPrice.getAssetsCode()+"/资产评估日:"+ulinkPrice.getPriceDate()+":增加投连价格记录失败:"+e.getMessage());
									}
								}


							break;
					}
					default:{
						break;
					}
					}
				}
			}
			
			public void onSheetStart(String sheetName,int firstrow, int lastrow, int firstcol, int lastcol) {
				//System.out.println("Start Sheet:"+sheetName+" firstrow："+firstrow+" lastrow:"+lastrow+" firstcol:"+firstcol+" lastcol:"+lastcol);
			}
			@Override
			public void onSheetEnd(String sheetName) {
				
			}
		});
		
	}
	
	@Override
	public Integer ulinkPricePageCount(UlinkPrice ulinkPrice) {
		return ulinkPriceDao.ulinkPricePageCount(ulinkPrice);
	}

	@Override
	public List<UlinkPrice> ulinkPricePageData(UlinkPrice ulinkPrice) {
		return ulinkPriceDao.ulinkPricePageData(ulinkPrice);
	}
	
	
	String getNewString(String s){
		String[] ss = s.split(",");
	    StringBuilder sl = new StringBuilder();
	    for (int i = 0; i < ss.length; i++) {
	    	sl=sl.append(ss[i]);
	    } 
	    return sl.toString();
	}

}
