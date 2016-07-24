package com.sinolife.pos.pub.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.sinolife.esbpos.web.WebPosService;
import com.sinolife.pos.acceptance.branch.service.BranchQueryService;
import com.sinolife.pos.common.consts.ViewNames;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.PersonalNoticeDTO;
import com.sinolife.pos.common.dto.VipProductInfoDTO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.common.web.PosAbstractController;
import com.sinolife.pos.vip.service.VipManageService;
import com.sinolife.sf.platform.runtime.PlatformContext;

/**
 * 外部页面控制器
 */
@RequestMapping("/pub")
@Controller
public class PubController extends PosAbstractController{

	@Autowired
	private BranchQueryService branchQueryService;
	@Autowired
	private VipManageService vipService;
	@Autowired
	private CommonQueryDAO commonQueryDAO;
	/**
	 * 调阅健康告知
	 */
	@RequestMapping("/personalNotice")
	public String viewPersonalNotice(@RequestParam String posNo, ModelMap model) {
		List<PersonalNoticeDTO> personalNoticeList = null;
		personalNoticeList = branchQueryService.queryPosPersonalNoticeByPosNo(posNo, "all");
		if(personalNoticeList == null || personalNoticeList.isEmpty()) {
			model.addAttribute("message", "找不到该健康告知记录：" + posNo);
		} else {
			model.addAttribute("personalNoticeList", personalNoticeList);
		}
		return ViewNames.PUB_PERSONAL_NOTICE_VIEW;
	}
	
	/**
	 * 进入综合查询页面
	 * @return
	 */
	@RequestMapping("/posGQ")
	public String posGqsEntry(Model model){
		model.addAttribute("GQ_URL", PosUtils.getPosProperty("gqUrl"));
		return "/pub/posGQ";
	}
	
	@RequestMapping("/policy_single")
	public String policySingleEntry(HttpServletRequest request, HttpServletResponse response) {
		try {
			request.getRequestDispatcher("/print/policy_single.do").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return null;
	}
	
	@RequestMapping("/policy_single_submit")
	public String policy_single_submit(HttpServletRequest request, HttpServletResponse response) {
		try {
			request.getRequestDispatcher("/print/policy_single_submit.do").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return null;
	}

	/**
	 * vip详细信息查询
	 * 
	 * @param type
	 * @param key
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/query_vip_info")
	public ModelAndView vipDetailQuery(@RequestParam String type,
			@RequestParam String key) {
		String clientNo = null;
		ModelAndView mav = new ModelAndView("/vip/out_customerInfo");
		Map pMap = new HashMap();
		pMap.put("query_type", type);
		pMap.put("query_string", key);
		vipService.exeVipProcedure("QUERY_VIP_BASEINFO", pMap);
		if (pMap.get("client_no") != null) {
			String vipNum = vipService.judgeClientNoVip((String) pMap
					.get("client_no"));
			if (Integer.parseInt(vipNum) > 0) {
				clientNo = (String) pMap.get("client_no");
			} else {
				mav = new ModelAndView("/vip/noData");
				return mav;
			}

		
		Map<String, Object> Map = new HashMap<String, Object>();
		Map.put("query_type", type);
		Map.put("query_string", key);
		vipService.queryVipProductInfo(Map);

		Map<String, Object> vipMap = new HashMap<String, Object>();
		vipMap.put("clientNo", clientNo);
		List<VipProductInfoDTO> vipList = new ArrayList<VipProductInfoDTO>();

		vipList = vipService.exeVipProcedureList("QUERY_VIP_GRADE_CHANGE",
				vipMap);
		mav.addObject("gradeChangeList", vipList);

		vipList = vipService.exeVipProcedureList("QUERY_VIP_CARD_CHANGE",
				vipMap);
		mav.addObject("cardChangeList", vipList);

		vipList = vipService
				.exeVipProcedureList("QUERY_VIP_EMP_CHANGE", vipMap);
		mav.addObject("empChangeList", vipList);

		vipList = vipService.exeVipProcedureList("QUERY_VIP_SHARED_SERVICE",
				vipMap);
		mav.addObject("sharedList", vipList);

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
	
	@RequestMapping("/PosQuery")
	public String queryEServiceInfo(String clientNo, Model model) {
		WebPosService wps = PlatformContext.getEsbContext().getEsbService(WebPosService.class);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("clientNo", clientNo);
		try {
			map = wps.getUserInfoByClientNo(map);
			logger.error("查询成功！！客户号：" + clientNo);
		} catch (Exception e) {
			logger.error("调用接口查询信息失败！！" + e.getMessage());
			map.put("msg", "调用接口查询信息失败！！");
		}
		if("Y".equals(map.get("successFlag"))) {
			//查询保单相关信息
			List<Map> bindPolicyList = (List<Map>) map.get("bindPolicyList");
			for(Map m : bindPolicyList) {
				String policyNo = (String) m.get("POLICYNO");
				Map policyInfo = commonQueryDAO.getPolicyProdutInfo(policyNo);
				if(PosUtils.isNotNullOrEmpty(policyInfo)) {
					m.put("NAME", policyInfo.get("NAME"));
					m.put("EFFECTDATE", policyInfo.get("EFFECTDATE"));
					m.put("STATUS", policyInfo.get("STATUS"));
				}
			}
			model.addAttribute("userInfo", map);
		} else {
			model.addAttribute("RETURN_MSG", map.get("msg"));
		}
		return "/pub/eServiceInfo";
	}
}
