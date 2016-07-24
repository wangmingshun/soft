package com.sinolife.pos.pubInterface.biz.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.sinolife.pos.print.dto.PosNoteDTO;
import com.sinolife.pos.print.service.PrintService;
import com.sinolife.pos.schedule.dao.ScheduleDAO;

public class PrintServiceTest {

	 public static void main(String args[]) {
		 ApplicationContext applicationContext = new
		 FileSystemXmlApplicationContext(
		 "D:\\sinopower\\workspace\\SL_POS_0.4.0\\src\\main\\webapp\\WEB-INF\\spring\\AppContext.xml");
//		 UndwrtRuleCheckJob undwrtRuleCheckJob =(UndwrtRuleCheckJob)applicationContext.getBean("undwrtRuleCheckJob");
//		 undwrtRuleCheckJob.execute();

		 ScheduleDAO dpi = (ScheduleDAO) applicationContext.getBean("scheduleDAO");
		  Map<String, Object> paraMap = new HashMap<String, Object>();
	      // 批次数量
	      paraMap.put("batchSize", "300");
	      List<PosNoteDTO> posNoteELetterList = dpi.queryPosNoteNeedELetter(paraMap);
	      // 电子信函处理
	      paraMap.put("taskFlag", "autoPrint");
	      paraMap.put("posNoteList", posNoteELetterList);
	      
	      PrintService printService = (PrintService) applicationContext.getBean("printService");
	      printService.manageELetter(paraMap);
	 }
}
