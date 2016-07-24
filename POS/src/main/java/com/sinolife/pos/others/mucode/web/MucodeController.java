package com.sinolife.pos.others.mucode.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sinolife.pos.common.include.service.IncludeService;
import com.sinolife.pos.common.web.PosAbstractController;
import com.sinolife.pos.others.mucode.service.MucodeService;
import com.sinolife.sf.platform.runtime.PlatformContext;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller("mucodeController")
public class MucodeController extends PosAbstractController {
	
	
	@Autowired
	MucodeService mucodeService;
	
	@Autowired
	IncludeService includeService;
	
	@RequestMapping("/others/mucode")
	public String entry(Model model) {	

		model.addAttribute("userBranchCode",
				includeService.userBranchCode(PlatformContext.getCurrentUser()));

		return "/others/mucode/mucode";
	}

	
	/**
	 * 检查银代联系人是否存在
	 */
	@RequestMapping(value="/others/mucode/checkName")
	@ResponseBody
	public Map checkName(HttpServletRequest request) {

		Map pMap = new HashMap();
		pMap.put("BRANCH_CODE", request.getParameter("branchCode"));
		pMap.put("DEPT_NO", request.getParameter("deptNo"));
		pMap.put("CONTACT", request.getParameter("contact"));
		
		Map<String, Object>  returnMap = new HashMap<String, Object>();
		try {
	
		     returnMap=mucodeService.checkName(pMap);				    				     
			 if(returnMap==null|| returnMap.size()==0){
				 
				 returnMap = new HashMap<String, Object>();
				 returnMap.put("flag", "N");						
				 
			 }	
             else {
					                	
            	 returnMap.put("flag", "Y");
				
			}
		
		} catch(Exception e) {
			logger.error(e);
			returnMap.put("flag", "E");
			returnMap.put("message", "系统异常：" + e.getMessage());
		
		}
		return returnMap;
			
	
	}
	
	
	/**
	 * 录入银代联系人信息
	 * @param request
	 * @return
	 */
	@RequestMapping("/others/mucode/submit")
	@ResponseBody
	public Map inputSubmit(HttpServletRequest request){
		Map pMap = new HashMap();
		pMap.put("BRANCH_CODE", request.getParameter("branchCode"));
		pMap.put("DEPT_NO", request.getParameter("deptNo"));
		pMap.put("CONTACT", request.getParameter("contact"));
		pMap.put("MOBILE_NO", request.getParameter("mobileNo"));						
		
    	Map<String, Object>  returnMap = new HashMap<String, Object>();
		
		try {
			
		     returnMap = mucodeService.checkBranchCode(pMap);				    				     
			 if(returnMap==null|| returnMap.size()==0){
				 
				 returnMap = new HashMap<String, Object>();
				 returnMap.put("flag", "NOBRANCHCODE");					 
				
			 }	
            else {
					                	
           	 returnMap.put("flag", "YBRANCHCODE");
           	 return returnMap;
				
			}
		
		} catch(Exception e) {
			logger.error(e);
			returnMap.put("flag", "E");
			returnMap.put("message", "系统异常：" + e.getMessage());
		
		}
		
		
		//验证手机格式是否正确
	    int flag=mucodeService.insertNameAndMobile(pMap);
	    if(flag==2){
	    	
	    	returnMap.put("flag", flag);
	    	return  returnMap;
	    	
	    }
		try {
	
		     returnMap = mucodeService.checkName(pMap);				    				     
			 if(returnMap==null|| returnMap.size()==0){
				 
				 returnMap = new HashMap<String, Object>();
				 returnMap.put("flag", "N");						
				
			 }	
             else {
					                	
            	 returnMap.put("flag", "Y");
				
			}
		
		} catch(Exception e) {
			logger.error(e);
			returnMap.put("flag", "E");
			returnMap.put("message", "系统异常：" + e.getMessage());
		
		}
		return returnMap;

	
	}
	
	/**
	 * 修改银代联系人信息
	 * @param request
	 * @return
	 */
	@RequestMapping("/others/mucode/updateName")
	@ResponseBody
	public Map  updateName(HttpServletRequest request){
		
		Map<String, Object>  returnMap = new HashMap<String, Object>();
		
		Map pMap = new HashMap();
		pMap.put("BRANCH_CODE", request.getParameter("branchCode"));
		pMap.put("DEPT_NO", request.getParameter("deptNo"));
		pMap.put("CONTACT", request.getParameter("contact"));
		pMap.put("MOBILE_NO", request.getParameter("mobileNo"));						
			     		
		try {
			
		     returnMap = mucodeService.checkBranchCode(pMap);				    				     
			 if(returnMap==null|| returnMap.size()==0){
				 
				 returnMap = new HashMap<String, Object>();
				 returnMap.put("flag", "N");				 
				
			 }	
            else {
            	String rcontact= request.getParameter("contact");
        		String rmobileNo= request.getParameter("mobileNo"); 
            	if(!rcontact.equals(returnMap.get("CONTACT"))||!rmobileNo.equals(returnMap.get("MOBILE_NO"))){
            		
            		int flag= mucodeService.updateNameAndMobile(pMap);
            	    returnMap.put("flag", flag);
            	    returnMap.put("rcontact", rcontact);
            	    returnMap.put("rmobileNo", rmobileNo);
            		
            	}
 				
			}
		
		} catch(Exception e) {
			logger.error(e);
			returnMap.put("flag", "E");
			returnMap.put("message", "系统异常：" + e.getMessage());
		
		}				

		return returnMap;
	
	}
}
