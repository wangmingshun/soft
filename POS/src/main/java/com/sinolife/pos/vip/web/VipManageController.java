package com.sinolife.pos.vip.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.sinolife.esbpos.mcc.MccPosService;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.LoginUserInfoDTO;
import com.sinolife.pos.common.dto.VipApplyDetailDto;
import com.sinolife.pos.common.dto.VipProductInfoDTO;
import com.sinolife.pos.common.file.dao.PosFileDAO;
import com.sinolife.pos.common.file.service.PosFileService;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.vip.service.VipManageService;
import com.sinolife.sf.attatch.UrlSignature;
import com.sinolife.sf.platform.runtime.PlatformContext;

/**
 * VIP管理
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@SessionAttributes(value = { "vipQueryType", "vipQueryKey", "clientNo" })
public class VipManageController {

	@Autowired
	private VipManageService vipService;
	
	@Autowired
	PosFileService fileService;

	@Autowired
	private CommonQueryDAO commonQueryDAO;

	@Autowired
	PosFileDAO posFileDAO;

	@ModelAttribute("vipApplyDetailDto")
	public VipApplyDetailDto getVipApplyDetailDto() {
		return new VipApplyDetailDto();
	}

	@RequestMapping("/vip/grade")
	public String entry() {
		return "/vip/grade";
	}

	@RequestMapping("/vip/query")
	public String listEntry(Model model) {

		List vipGradeList = vipService.getDesc("QUERY_VIP_GRADE");

		model.addAttribute("vipGradeList", vipGradeList);
		LoginUserInfoDTO userInfo = PosUtils.getLoginUserInfo();
		String userBranchCode = userInfo.getLoginUserUMBranchCode();
		model.addAttribute("userBranchCode",userBranchCode);
		return "/vip/query";
	}

	/**
	 * 类型和级别规则关系设置入口
	 * 
	 * @return
	 */
	@RequestMapping("/vip/typeGrade")
	public String ruleEntry() {

		return "/vip/typeGrade";
	}

	/**
	 * 根据客户号试算VIP信息
	 * 
	 * @return
	 */
	@RequestMapping("/vip/grade/trial")
	public String trialVipInfo(@RequestParam String clientNo, Model model) {
		Map pMap = new HashMap();
		pMap.put("clientNo", clientNo);

		vipService.exeVipProcedure("CALC_CLIENT_VIP_GRADE", pMap);

		model.addAttribute("trialInfo", pMap);

		return "/vip/grade";
	}

	/**
	 * 根据客户号查询vip信息
	 * 
	 * @return
	 */
	@RequestMapping("/vip/grade/query")
	public String queryClient(@RequestParam String clientNo, Model model) {
		Map pMap = new HashMap();
		pMap.put("clientNo", clientNo);

		//vipService.exeVipProcedure("GET_CLIENT_VIP_GRADE_DTL", pMap);
		vipService.exeVipProcedure("CALC_CLIENT_VIP_GRADE", pMap);

		model.addAttribute("vipInfo", pMap);

		return "/vip/grade";
	}

	/**
	 * 修改vip等级
	 * 
	 * @return
	 */
	@RequestMapping("/vip/grade/update")
	public String updateGrade(HttpServletRequest request, Model model) {
		Map pMap = new HashMap();
		/*
		pMap.put("clientNo", request.getParameter("clientNo"));
		pMap.put("vipType", request.getParameter("vipType"));
		pMap.put("vipGrade", request.getParameter("vipGrade"));
		pMap.put("vipEffectDate", commonQueryDAO.getSystemDate());
		pMap.put("vipPremSum", request.getParameter("vipPremSum"));
		pMap.put("vipBranch",
				vipService.clientApplyBranch(request.getParameter("clientNo")));
		pMap.put("remark", request.getParameter("remark"));
		pMap.put("isManual", "Y");
		*/
		//0:系统逐单标记  1:手工标记
		String isManual = request.getParameter("operationType");
		String clientNo = request.getParameter("clientNo");
		String vipGrade = request.getParameter("vipGrade");
		String vipPremSum = request.getParameter("vipPremSum");
		String branchCode = request.getParameter("branchCode");
		String remark = request.getParameter("remark");
		pMap.put("clientNo", clientNo);
		pMap.put("vipType", "3");
		pMap.put("vipGrade",  vipGrade);
		pMap.put("vipEffectDate", commonQueryDAO.getSystemDate());
		if("0".equals(isManual)) {
			pMap.put("vipPremSum", vipPremSum);
			pMap.put("vipBranch", vipService.clientApplyBranch(request.getParameter("clientNo")));
			pMap.put("remark",    "");
			pMap.put("isManual",  "N");
		} else {
			pMap.put("vipPremSum", "");
			pMap.put("vipBranch", branchCode);
			pMap.put("remark",    remark);
			pMap.put("isManual",  "Y");
		}

		//vipService.exeVipProcedure("MAINTAIN_CLIENT_VIP_GRADE", pMap);
		vipService.exeVipProcedure("marking_client_info", pMap);

		model.addAttribute("RETURN_MSG", "VIP等级修改成功！");
		return "/vip/grade";
	}

	/**
	 * 查询已有设置
	 * 
	 * @param vipType
	 * @param vipGrade
	 * @param branchClassify
	 * @return
	 */
	@RequestMapping("vip/typeGrade/query")
	@ResponseBody
	public String queryTypeGradePremsum(String vipType, String vipGrade,
			String branchClassify) {
		Map pMap = new HashMap();
		pMap.put("vipType", vipType);
		pMap.put("vipGrade", vipGrade);
		pMap.put("branchClassify", branchClassify);

		return vipService.queryTypeGradePremsum(pMap);
	}

	/**
	 * 提交规则设置
	 * 
	 * @param vipType
	 * @param vipGrade
	 * @param branchClassify
	 * @param premSum
	 * @param model
	 * @return
	 */
	@RequestMapping("vip/typeGrade/set")
	public String typeGradePremsumSet(String vipType, String vipGrade,
			String branchClassify, String premSum, Model model) {
		Map pMap = new HashMap();
		pMap.put("vipType", vipType);
		pMap.put("vipGrade", vipGrade);
		pMap.put("branchClassify", branchClassify);
		pMap.put("premSum", premSum);

		vipService.typeGradePremsumSet(pMap);

		model.addAttribute("RETURN_MSG", "设置成功");
		return "/vip/typeGrade";
	}

	/**
	 * 进入综合查询页面
	 * 
	 * @return
	 */
	@RequestMapping("/vip/customerQuery")
	public String vipCustomerInformationQuery(Model model) {

		return "/vip/customerQuery";
	}

	/**
	 * vip详细信息查询
	 * 
	 * @param type
	 * @param key
	 * @param model
	 * @return
	 */
	@RequestMapping("/vip/detailQuery")
	public ModelAndView vipDetailQuery(@RequestParam String type,
			@RequestParam String key) {
		String clientNo = null;
		ModelAndView mav = new ModelAndView("/vip/customerInfo");
		Map pMap = new HashMap();
		pMap.put("query_type", type);
		pMap.put("query_string", key);
		//基本资料
		vipService.exeVipProcedure("QUERY_VIP_BASEINFO", pMap);
		if (pMap.get("client_no") != null) {
			String vipNum = vipService.judgeClientNoVip((String) pMap.get("client_no"));
			if (Integer.parseInt(vipNum) > 0) {
				clientNo = (String) pMap.get("client_no");
			} else {
				mav = new ModelAndView("/vip/noData");
				return mav;
			}

		
		Map<String, Object> Map = new HashMap<String, Object>();
		Map.put("query_type", type);
		Map.put("query_string", key);
		//保单信息
		vipService.queryVipProductInfo(Map);

		Map<String, Object> vipMap = new HashMap<String, Object>();
		vipMap.put("clientNo", clientNo);
		List<VipProductInfoDTO> vipList = new ArrayList<VipProductInfoDTO>();

		//VIP等级变动信息
		vipList = vipService.exeVipProcedureList("QUERY_VIP_GRADE_CHANGE", vipMap);
		mav.addObject("gradeChangeList", vipList);

		//VIP卡号变动信息
		vipList = vipService.exeVipProcedureList("QUERY_VIP_CARD_CHANGE", vipMap);
		mav.addObject("cardChangeList", vipList);

		//VIP服务专员变动信息
		vipList = vipService.exeVipProcedureList("QUERY_VIP_EMP_CHANGE", vipMap);
		mav.addObject("empChangeList", vipList);

		//享受VIP服务情况
		vipList = vipService.exeVipProcedureList("QUERY_VIP_SHARED_SERVICE", vipMap);
		mav.addObject("sharedList", vipList);
		
		//edit by wangmingshun start 客户的服务方案
		String clientServicePlan = 
			(String) vipService.getclientServicePlanInfo(vipMap).get("clientServicePlan");
		mav.addObject("clientServicePlan", clientServicePlan);
		//edit by wangmingshun end 客户的服务方案
		
		mav.addObject("vipBaseInfo", pMap);
		mav.addObject("vipProductInfo", Map);

		mav.addObject("clientNo", clientNo);
		mav.addObject("vipQueryType", type);
		mav.addObject("vipQueryKey", key);
		}
		else
		{
			mav = new ModelAndView("/vip/noData");
			return mav;
			
		}

		return mav;
	}

	/**
	 * 
	 * vip卡号变更菜单
	 * 
	 * @return
	 */
	@RequestMapping("/vip/cardChange")
	public String cardChangeEntry(Model model) {

		List modifiedReasonList = vipService
				.getDesc("QUERY_VIP_MODIFIED_REASON");

		model.addAttribute("modifiedReasonList", modifiedReasonList);
		return "/vip/cardChange";
	}

	@RequestMapping("/vip/empChange")
	public String empChangeEntry() {

		return "/vip/empChange";
	}

	/**
	 * 进入vip尊荣服务添加菜单
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/vip/sharedService")
	public String sharedServiceEntry(Model model) {

		List vipBigServiceItemList = vipService
				.getDesc("QUERY_VIP_BIG_SERVICE_ITEM");
		model.addAttribute("vipBigServiceItemList", vipBigServiceItemList);
		return "/vip/sharedService";
	}

	/**
	 * @param vipQueryType
	 * @param vipQueryKey
	 * @param clientNo
	 * @param vipApplyDetailDto
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/vip/empChangeInsert")
	public ModelAndView vipEmpChange(
			@ModelAttribute("vipQueryType") String vipQueryType,
			@ModelAttribute("vipQueryKey") String vipQueryKey,
			@ModelAttribute("clientNo") String clientNo,
			@ModelAttribute("vipApplyDetailDto") VipApplyDetailDto vipApplyDetailDto,
			Model model) {

		ModelAndView mav = new ModelAndView();

		vipService.vipEmpChange(clientNo, vipApplyDetailDto);

		mav.addObject("RETURN_MSG", "增加成功！");
		mav.setViewName("forward:/vip/detailQuery.do?type=" + vipQueryType
				+ "&key=" + vipQueryKey);

		return mav;

	}

	/**
	 * 协议退费原因查询
	 * 
	 * @param specialFunc
	 * @return
	 */
	@RequestMapping(value = "/vip/queryVipSamllService")
	@ResponseBody
	public List<String> queryVipSamllService(String bigServiceItem) {
		Map pMap = new HashMap();
		pMap.put("bigServiceItem", bigServiceItem);
		return vipService.exeVipProcedureList("QUERY_VIP_SMALL_SERVICE_ITEM",
				pMap);
	}

	/**
	 * 获取任务结果的下载地址
	 */
	@RequestMapping("/vip/judgeNewCardNo")
	@ResponseBody
	public Map<String, Object> judegNewCardNo(@RequestParam String cardNo,
			@RequestParam String reason,
			@ModelAttribute("clientNo") String clientNo) {

		Map<String, Object> retMap = new HashMap<String, Object>();

		String alertMessage = "";
		LoginUserInfoDTO userInfo = PosUtils.getLoginUserInfo();
		String userBranchCode = userInfo.getLoginUserUMBranchCode();

		retMap.put("p_voucher_flow", cardNo);
		retMap.put("p_business_series", "L");
		retMap.put("p_branch_code", userBranchCode);
		vipService.exeVipProcedureList("judgeImportSystem", retMap);
		String isImport = (String) retMap.get("p_flag");
		if (isImport.equals("N")) {

			alertMessage = "该卡号还未导入系统或已经使用过，请确认！";
			retMap.put("alertMessage", alertMessage);
			return retMap;
		}

		if (!reason.equals("5")) {

			Map<String, Object> vcMap = new HashMap<String, Object>();
			vcMap.put("p_card_no", cardNo);
			vcMap.put("p_client_no", clientNo);
			vipService.insertVipDetailInfo("judgeCardAndGrade", vcMap);
			if (((String) vcMap.get("p_match")).equals("N")) {
				alertMessage = "该客户当前的VIP等级与录入的卡号等级不匹配，不能添加";
				vcMap.put("alertMessage", alertMessage);
				return vcMap;

			}

		}

		return retMap;
	}

	/**
	 * @param vipQueryType
	 * @param vipQueryKey
	 * @param clientNo
	 * @param vipApplyDetailDto
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/vip/cardChangeInsert")
	public ModelAndView vipCardChange(
			@ModelAttribute("vipQueryType") String vipQueryType,
			@ModelAttribute("vipQueryKey") String vipQueryKey,
			@ModelAttribute("clientNo") String clientNo,
			@ModelAttribute("vipApplyDetailDto") VipApplyDetailDto vipApplyDetailDto) {

		ModelAndView mav = new ModelAndView();

		vipService.vipCardChange(clientNo, vipApplyDetailDto);
		mav.addObject("RETURN_MSG", "增加成功！");
		mav.setViewName("forward:/vip/detailQuery.do?type=" + vipQueryType
				+ "&key=" + vipQueryKey);

		return mav;

	}

	@RequestMapping(value = "/vip/sharedServiceInsert")
	public ModelAndView vipSharedService(
			@RequestParam MultipartFile attechmentFileName,
			@ModelAttribute("vipQueryType") String vipQueryType,
			@ModelAttribute("vipQueryKey") String vipQueryKey,
			@ModelAttribute("clientNo") String clientNo,
			@ModelAttribute("vipApplyDetailDto") VipApplyDetailDto vipApplyDetailDto,
			Model model) {

		ModelAndView mav = new ModelAndView();
		fileService.uploadAndRecord(attechmentFileName, "6");

		vipService.vipSharedService(clientNo, vipApplyDetailDto);

		model.addAttribute("RETURN_MSG", "增加成功！");
		mav.setViewName("forward:/vip/detailQuery.do?type=" + vipQueryType
				+ "&key=" + vipQueryKey);

		return mav;

	}

	@RequestMapping(value = "/vip/clearAttechment")
	@ResponseBody
	public Map<String, Object> clearAttechment(
			@ModelAttribute("vipApplyDetailDto") VipApplyDetailDto vipApplyDetailDto) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		vipApplyDetailDto.setAttechmentFileId(null);
		vipApplyDetailDto.setAttechmentFileName(null);
		retMap.put("flag", "Y");
		return retMap;
	}

	/**
	 * 获取任务结果的下载地址
	 */
	@RequestMapping("/vip/getDownloadURL")
	@ResponseBody
	public Map<String, Object> getDownloadURL(@RequestParam String fileId) {

		VipProductInfoDTO vipProductInfoDTO = new VipProductInfoDTO();

		Map<String, Object> retMap = new HashMap<String, Object>();

		Map pMap = new HashMap();
		pMap.put("fileId", fileId);
		List fileList = vipService.exeVipProcedureList(
				"QUERY_VIP_POS_FILE_INFO", pMap);
		if (fileList != null && fileList.size() > 0) {
			vipProductInfoDTO = (VipProductInfoDTO) fileList.get(0);

			String downloadURL = null;
			if (vipProductInfoDTO.getFileId() != null) {
				downloadURL = PlatformContext.getIMFileService().getFileURL(
						vipProductInfoDTO.getFileId(), new UrlSignature(),
						true, vipProductInfoDTO.getFileName(), null, null);

			}
			if (StringUtils.isNotBlank(downloadURL)) {
				retMap.put("flag", "Y");
				retMap.put("url", downloadURL);
			} else {
				retMap.put("flag", "N");
				retMap.put("message", "找不到下载地址");
			}
		}
		return retMap;
	}	
	
	/**
	 * @methodName: vipPolicyCellQuery
	 * @Description: vip保单层清单查询入口
	 * @param model
	 * @return String 
	 * @author WangMingShun
	 * @date 2015-4-29
	 * @throws
	 */ 	
	@RequestMapping("/vip/policyCellQuery")
	public String vipPolicyCellQuery(Model model){
		
		List vipGradeList = vipService.getDesc("QUERY_VIP_GRADE");

		model.addAttribute("vipGradeList", vipGradeList);
		LoginUserInfoDTO userInfo = PosUtils.getLoginUserInfo();
		String userBranchCode = userInfo.getLoginUserUMBranchCode();
		model.addAttribute("userBranchCode",userBranchCode);
		return "/vip/policyCellQuery";
	}
	
	/**
	 * @methodName: vipClientCellQuery
	 * @Description: vip客户层清单查询入口
	 * @param model
	 * @return String 
	 * @author WangMingShun
	 * @date 2015-4-29
	 * @throws
	 */ 	
	@RequestMapping("/vip/clientCellQuery")
	public String vipClientCellQuery(Model model){
		
		List vipGradeList = vipService.getDesc("QUERY_VIP_GRADE");
		
		model.addAttribute("vipGradeList", vipGradeList);
		LoginUserInfoDTO userInfo = PosUtils.getLoginUserInfo();
		String userBranchCode = userInfo.getLoginUserUMBranchCode();
		model.addAttribute("userBranchCode",userBranchCode);
		return "/vip/clientCellQuery";
	}
	
	/**
	 * @methodName: vipSharedServiceQuery
	 * @Description: VIP尊荣服务记录清单 入口
	 * @param model
	 * @return String 
	 * @author WangMingShun
	 * @date 2015-4-29
	 * @throws
	 */ 	
	@RequestMapping("/vip/sharedServiceQuery")
	public String vipSharedServiceQuery(Model model){
		
		List vipGradeList = vipService.getDesc("QUERY_VIP_GRADE");
		
		model.addAttribute("vipGradeList", vipGradeList);
		LoginUserInfoDTO userInfo = PosUtils.getLoginUserInfo();
		String userBranchCode = userInfo.getLoginUserUMBranchCode();
		model.addAttribute("userBranchCode",userBranchCode);
		List vipBigServiceItemList = vipService.getDesc("QUERY_VIP_BIG_SERVICE_ITEM");
		model.addAttribute("vipBigServiceItemList", vipBigServiceItemList);
		
		return "/vip/sharedServiceQuery";
	}
	
	/**
	 * 根据客户号查询呼入记录(电话中心)
	 * @methodName: getCallInRecord
	 * @Description: 
	 * @param clientNo
	 * @return String 
	 * @author WangMingShun
	 * @date 2015-6-12
	 * @throws
	 */
	@RequestMapping("/vip/phoneCenterService")
	public ModelAndView getCallInRecord(@ModelAttribute("clientNo") String clientNo) {
		
		ModelAndView mav = new ModelAndView("/vip/phoneCenterService");
		
		MccPosService mccPosService = PlatformContext
			.getEsbContext().getEsbService(MccPosService.class);
		
		List<Map<String, Object>> list = mccPosService.getCallInRecord(clientNo);
		
		mav.addObject("callInRecord", list);
		
		return mav;
	}
	
	/**
	 * 根据任务号查询服务明细(电话中心)
	 * @methodName: getCallInRecord
	 * @Description: 
	 * @param taskId
	 * @return String 
	 * @author WangMingShun
	 * @date 2015-6-12
	 * @throws
	 */
	@RequestMapping("/vip/serviceDetail")
	public ModelAndView getServiceDetail(@RequestParam("taskId") String taskId) {
		
		ModelAndView mav = new ModelAndView("/vip/phoneCenterServiceTaskDetail");
		
		MccPosService mccPosService = PlatformContext
			.getEsbContext().getEsbService(MccPosService.class);
		
		List<Map<String, Object>> list = mccPosService.getServiceDetail(taskId);
		
		mav.addObject("serviceDetail", list);
		
		return mav;
	}
	
	/**
	 * @Description: 更新VIP服务情况表的服务信息状态
	 * @methodName: updateVipServiceInstanceInfo
	 * @param clientNo
	 * @param isValid
	 * @param seqNo
	 * @return
	 * @return String
	 * @author WangMingShun
	 * @date 2015-8-20
	 * @throws
	 */
	@RequestMapping("/vip/updateVipServiceInstanceInfo")
	@ResponseBody
	public String updateVipServiceInstanceInfo(String clientNo, 
			String isValid, String seqNo) {
		return vipService.updateVipServiceInstanceInfo(clientNo, isValid, seqNo);
	}
	
	@RequestMapping("/vip/calcClientVipGrade")
	@ResponseBody
	public Map calcClientVipGrade(String clientNo) {
		Map pMap = new HashMap();
		pMap.put("clientNo", clientNo);
		vipService.exeVipProcedure("CALC_CLIENT_VIP_GRADE", pMap);
		return pMap;
	}
}
