package com.sinolife.pos.common.util;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * 生成Excel
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class Excel {

	/*********************************
	 * 将list数据转换成Excel
	 * 返回临时文件全路径
	 * @param head
	 * @param key
	 * @param data
	 * @return
	 */
	public static String listToExcel(List<String> head, List<String> key, List data){
		if(data!=null && data.size()>20000){
			throw new RuntimeException(data.size()+"查询结果过多，请缩小查询范围");
		}
		
		String newFile = PosUtils.getPosProperty("tmpFilePath")+Math.random()+".xls";
		WritableWorkbook book = null;
		try {
			book = Workbook.createWorkbook(new FileOutputStream(newFile));
			WritableSheet sheet = book.createSheet("sheet1", 0);
			
			sheet.setRowView(0, 20);
			WritableFont font = new WritableFont(WritableFont.TIMES,12,WritableFont.BOLD);
			WritableCellFormat format = new WritableCellFormat(font);
			for (int i = 0; i < head.size(); i++) {
				sheet.setColumnView(i, head.get(i).getBytes().length+5);
				sheet.addCell(new Label(i,0,head.get(i),format));
			}
			
			if(data != null && data.size() > 0){
				for (int i = 0; i < data.size(); i++) {
					Map map = (Map) data.get(i);
					sheet.setRowView(i+1, 18);
					for (int j = 0; j < key.size(); j++) {
						sheet.addCell(new Label(j,i+1,map.get(key.get(j))==null?"":String.valueOf(map.get(key.get(j)))));		
					}
				}			
			}
			book.write();
			book.close();
		} catch (Exception e) {
			if(book!=null){
				try {
					book.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}				
			}
			throw new RuntimeException("数据生成异常",e);
		}
		return newFile;
	}

	/**
	 * 保全清单用
	 * 但不限于
	 * @param pMap
	 * @return
	 */
	public static String listToExcel(Map pMap) {
		return listToExcel((List) pMap.get("head"), (List) pMap.get("key"), (List) pMap.get("queryResult"));
	}
}
