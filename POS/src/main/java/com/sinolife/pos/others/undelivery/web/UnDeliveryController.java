package com.sinolife.pos.others.undelivery.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.unihub.framework.util.common.DateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.gwt.user.datepicker.client.DateBox.Format;
import com.sinolife.pos.common.consts.SessionKeys;
import com.sinolife.pos.common.consts.ViewNames;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.PosProblemItemsDTO;
import com.sinolife.pos.common.util.PaginationDataWrapper;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.others.undelivery.dao.UnDeliveryDAO;
import com.sinolife.pos.others.undelivery.service.UnDeliveryService;
import com.sinolife.pos.todolist.problem.dao.ProblemDAO;
import com.sinolife.pos.todolist.problem.service.ProblemService;
import com.sinolife.sf.platform.runtime.PlatformContext;

/**
 * 退信处理
 * 
 */
@Controller
public class UnDeliveryController {

	@Autowired
	UnDeliveryService service;
	
	@Autowired
	private CommonQueryDAO commonQueryDAO;
	
	@Autowired
	private UnDeliveryDAO unDeliveryDAO;
	
	@Autowired
	private UnDeliveryService unDeliveryService;
	
	/**
	 * 入口
	 * 
	 * @return
	 */
	@RequestMapping("/others/undelivery")
	public String entry() {
		return "/others/undelivery/input";
	}
	
	/**
	 * 入口，信函问题件处理待办任务
	 * @param model
	 * @return
	 */
	@RequestMapping("/others/index")
	public String entry(Model model, SessionStatus sessionStatus){
		sessionStatus.setComplete();
		model.addAttribute("UW_URL", PosUtils.getPosProperty("uwUrl"));//核保函调用uw用到
		return "/others/undelivery/index";
	}
	
	/**
	 * 信函问题件处理待办任务页面信息
	 */
	@RequestMapping("/others/problemList")
	@ResponseBody
	public Map<String, Object> getProblemList(@RequestParam int page, @RequestParam int rows, @ModelAttribute(SessionKeys.TODOLIST_NOTE_LIST) List<PosProblemItemsDTO> problemList) {
		PaginationDataWrapper<PosProblemItemsDTO> wrapper = new PaginationDataWrapper<PosProblemItemsDTO>(problemList, rows);
		wrapper.gotoPage(page);
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("total", wrapper.getTotalDataSize());
		retMap.put("rows", wrapper.getCurrentPageDataList());
		return retMap;
	}
	
	/**
	 * 信函问题件处理待办任务列表
	 */
	@ModelAttribute(SessionKeys.TODOLIST_NOTE_LIST)
	public List<PosProblemItemsDTO> problemList() {
		//String loginUserID = PosUtils.getLoginUserInfo().getLoginUserID();
		String loginUserID=PlatformContext.getCurrentUser();
		return unDeliveryService.queryNoteReturnManage(loginUserID);
	}
	
	/**
	 * 进入信函问题件处理
	 * 
	 * @return
	 */
	@RequestMapping("/others/problem")
	public ModelAndView problemItemProcEntry(String barcodeNo,String policyNo,String problemItemNo) {
		ModelAndView mav = new ModelAndView(ViewNames.TODOLIST_UNDELIVER_PROCESS);

		PosProblemItemsDTO problemItemsDTO = unDeliveryService
				.queryBarcodeNoByID(barcodeNo,policyNo,problemItemNo);
		mav.addObject(SessionKeys.TODOLIST_PROBLEM_INFO, problemItemsDTO);
		return mav;
	}
	
	/**
	 * 处理结果为地址正确更改处理状态
	 * @throws ParseException 
	 */
	@RequestMapping("/others/update")
	public String update(PosProblemItemsDTO posProblemItemsDTO) throws ParseException{
		Date sysdate = commonQueryDAO.getSystemDate(); // 获取当前时间
		String detailSequenceNo = posProblemItemsDTO.getDetailSequenceNo(); //条形码
		String problemItemType = posProblemItemsDTO.getProblemItemType();// 类型
		String dealOpinion  = posProblemItemsDTO.getDealOpinion();//处理意见
		unDeliveryDAO.updateStatusDesc(detailSequenceNo, sysdate, problemItemType, "", dealOpinion);
		return "/others/undelivery/index";
	}
	
	/**
	 * 处理结果为地址错误  
	 * @throws ParseException 
	 */
	@RequestMapping("/others/getPolicyNoList")
	public String getPolicyNoList(PosProblemItemsDTO posProblemItemsDTO,String applyDate){
		 unDeliveryService.getPolicyNoList(posProblemItemsDTO);
		 return "/others/undelivery/index";
	}
	
	/**
	 * 文件上传
	 */
	@RequestMapping("/others/undelivery/upload")
	public String uploadFile(@RequestParam MultipartFile file, Model model, HttpServletResponse response)
			throws Exception {
		String filePath = null;
		if (!file.isEmpty()) {
			filePath = PosUtils.getPosProperty("tmpFilePath") + System.currentTimeMillis() + file.getOriginalFilename();
			File tmpFile = new File(filePath);
			FileOutputStream fos = new FileOutputStream(tmpFile);
			InputStream in = file.getInputStream();
			byte[] buf = new byte[1024];
			int length = in.read(buf);
			while (length != -1) {
				fos.write(buf, 0, length);
				length = in.read(buf);
			}
			fos.flush();
			fos.close();
			in.close();
			File outFile = service.parseFile(filePath);// 处理文件
			FileInputStream fis = null;
			ServletOutputStream out = null;
			String fileName = "处理结果-" + file.getOriginalFilename();
			int i;
			response.setContentType("application/vnd.ms-excel");
			response.addHeader("Content-Disposition",
					"attachment;filename=\"" + URLEncoder.encode("" + fileName, "UTF-8") + "\"");
			fis = new FileInputStream(outFile);
			out = response.getOutputStream();
			while ((i = fis.read()) != -1) {
				out.write(i);
			}
			PosUtils.safeCloseInputStream(fis);
			PosUtils.safeCloseOuputStream(out);
			PosUtils.deleteFile(tmpFile);
			PosUtils.deleteFile(outFile);
		}
		return null;
	}

	/**
	 * 退信任务交接页面
	 * @return String
	 * @author zhangyi.wb
	 * @time 2016-6-30
	 */
	@RequestMapping("/others/senderHandover")
	public String senderHandover(){
		return "/others/undelivery/senderHandover";
	}

	/**
	 * 退信任务交接给新管理员
	 * 
	 * @return Map<String,Object>
	 * @author zhangyi.wb
	 * @throws IOException 
	 * @time 2016-6-30
	 */
	@RequestMapping("/others/senderHandoverSubmit")
	public void senderHandoverSubmit(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		String letterManager=request.getParameter("letterManager");
		String letterManagerNew=request.getParameter("letterManagerNew");
		letterManager = letterManager+"@sino-life.com";
		letterManagerNew = letterManagerNew+"@sino-life.com";
		Map<String,Object> cMap = new HashMap<String, Object>();
		String isChanged = "N";
		String checkPass = service.checkLetterManager(letterManagerNew);
		if("Y".equals(checkPass)){
			isChanged = service.senderHandoverSubmit(letterManager, letterManagerNew);
		}
		cMap.put("checkPass", checkPass);
		cMap.put("isChanged", isChanged);
		JSONObject o = JSONObject.fromObject(cMap);
    	PrintWriter write = response.getWriter();
		write.write(o.toString());
		write.flush();
		write.close();
		o.clear();
	}
}
