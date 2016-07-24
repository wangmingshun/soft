package com.sinolife.pos.pub.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sinolife.pos.pub.web.DateJsonValueProcessor;
import com.sinolife.pos.pub.dto.UlinkPrice;
import com.sinolife.pos.pub.service.UlinkPriceService;
import com.sinolife.sf.store.SFFile;
import com.sinolife.sf.store.TempFileFactory;


@Controller
@RequestMapping(value="/pub/UlinkPrice")
public class UlinkPriceController {
	
	private static final Logger logger = Logger.getLogger(UlinkPriceController.class);
	
	private TempFileFactory tempFileFactory=TempFileFactory.getInstance(UlinkPriceController.class);
	
    @Autowired
	private UlinkPriceService ulinkPriceService; 
	
	@RequestMapping(value="/main")
	public String  upload(Model model,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		
		model.addAttribute("ulinkPrice", new UlinkPrice());
		return "/pub/toolbar";
	}
	
	@RequestMapping(value = "/getUlinkPriceList")
	public void getUlinkPriceList(UlinkPrice ulinkPrice,HttpServletRequest request, HttpServletResponse response) throws IOException {
		List<UlinkPrice> list = new ArrayList<UlinkPrice>();
		int total = 0;
		String beginDate=request.getParameter("beginDate");
		String finishDate=request.getParameter("finishDate");
		if(beginDate!=null&&!"".equals(beginDate)&&finishDate!=null&&!"".equals(finishDate)){
            //ulinkPrice.setStartDate(ContollerUtil.convertStringtoDate(beginDate));
			//ulinkPrice.setEndDate(ContollerUtil.convertStringtoDate(finishDate));
			ulinkPrice.setStartDate(UlinkPriceController.convertStringtoDate(beginDate));
			ulinkPrice.setEndDate(UlinkPriceController.convertStringtoDate(finishDate));
			list = ulinkPriceService.ulinkPricePageData(ulinkPrice);
			total = ulinkPriceService.ulinkPricePageCount(ulinkPrice);
		}
		JsonConfig config = new JsonConfig();
		config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor()); 
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("total", total);
		map.put("rows",  list);
		JSONObject json =JSONObject.fromObject(map, config);
		PrintWriter write = response.getWriter();
		write.write(json.toString());
		write.flush();
		write.close();
	}	
	
	@RequestMapping(value="/uploadExcel/main")
	public String  uploadMain(Model model,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		return "/pub/upload";
	}
	
	@RequestMapping(value="/uploadExcel")
	public String uploadExcel(@RequestParam MultipartFile content,HttpServletRequest request, HttpServletResponse response) throws Exception{
		SFFile tempFile=null;
		try{
			tempFile=tempFileFactory.createTempFile();
			content.transferTo(tempFile);
			ulinkPriceService.uploadUnlinkPrice(tempFile);
		    request.setAttribute("msgs", "导入Excel成功");
		    request.setAttribute("forwardUrl", "pub/UlinkPrice/uploadExcel/main");
		}
		catch (Throwable e) {
			throw new RuntimeException("导入Excel失败:"+e.getMessage());
		} 
		   finally{
			try
			{
				if(tempFile!=null)
				{
					tempFile.delete();
				}
			}catch(Throwable e)
			{
				logger.error("delete update load temp file error",e);
			}
			
		}
		return "/pub/msgAndforward";	
	}
	
	/*
	 *辅助方法:将string转换为date类型
	 */
	public static Date convertStringtoDate(String strDate){
		  Date date = null;
		  SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd"); //加上时间 
		  //必须捕获异常 

		  try 
		  { 
		   date=dateFormat.parse(strDate); 
		  } 
		  catch(ParseException px) 
		  { 
		   px.printStackTrace(); 
		  } 
		  return date;
		} 
}
