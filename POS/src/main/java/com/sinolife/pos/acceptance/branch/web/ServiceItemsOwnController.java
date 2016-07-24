package com.sinolife.pos.acceptance.branch.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.sinolife.pos.acceptance.branch.dto.QueryAndAddClientDTO;
import com.sinolife.pos.acceptance.branch.service.ServiceItemsOwnService;
import com.sinolife.pos.common.consts.SessionKeys;
import com.sinolife.pos.common.consts.ViewNames;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;

/**
 * 某些保全项目单独需要的操作控制 如受理录入页面中途需要操作后台数据库
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@SessionAttributes(value = SessionKeys.ACCEPTANCE_QUERY_AND_ADD_CLIENT)
@RequestMapping("/acceptance/branch/")
@Controller
public class ServiceItemsOwnController {

	@Autowired
	private ServiceItemsOwnService itemsService;
	
	@Autowired
	private CommonQueryDAO commonQueryDAO;
	/**
	 * 项目33查询要变更的批单转账信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/queryPremInfoByPosNo33")
	@ResponseBody
	public Map queryPremInfoByPosNo(String posNo, String policyNo) {
		Map pMap = new HashMap();
		pMap.put("posNo", posNo);
		pMap.put("policyNo", policyNo);

		return itemsService.queryPremInfoByPosNo(pMap);
	}

	/**
	 * 项目15/20 查询职业号对应的描述
	 * 
	 * @param request
	 * @param response
	 * @param model
	 */
	@RequestMapping(value = "/queryOccupationInfo")
	@ResponseBody
	public Map queryOccupationInfo(String occupationCode) {
		return itemsService.queryOccupationInfo(occupationCode);
	}

	/**
	 * 根据新输入的客户号查询新投保人信息 没输入就直接到新增客户页面，但其中的保单联系信息是要查
	 */
	@RequestMapping(value = "/queryAndAddClient")
	public String queryAndAddClient(HttpServletRequest request,
			HttpSession session, ModelMap model) {
		String clientNo = request.getParameter("clientNo");
		String policyNo = request.getParameter("policyNo");
		String newPolicyFlag = request.getParameter("newPolicyFlag");
		String displayPolicyContact = request
				.getParameter("displayPolicyContact");
		String rewriteClientNoPropName = request
				.getParameter("rewriteClientNoPropName");
		String rewriteQAACPropName = request
				.getParameter("rewriteQAACPropName");

		ServiceItemsInputDTO item = (ServiceItemsInputDTO) session
				.getAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT);
		BeanWrapper bw = new BeanWrapperImpl(item);
		QueryAndAddClientDTO qaa = (QueryAndAddClientDTO) bw
				.getPropertyValue(rewriteQAACPropName);
		if (qaa == null) {
			qaa = new QueryAndAddClientDTO();
			qaa.setClientNo(clientNo);
			qaa.setPolicyNo(policyNo);
			if ("N".equals(displayPolicyContact)) {
				qaa.setDisplayPolicyContact("N");
			} else {
				qaa.setDisplayPolicyContact("Y");
			}
			if ("Y".equals(newPolicyFlag)) {
				qaa.setNewPolicyFlag("Y");
			} else {
				qaa.setNewPolicyFlag("N");
			}

			// 客户新增成功后回写的ServiceItemsInputDTO字段名称
			qaa.setRewriteClientNoPropName(rewriteClientNoPropName);

			// 客户新增成功后回写的客户号字段名称
			qaa.setRewriteQAACPropName(rewriteQAACPropName);

			// 批次号和保全号用于组织返回的URL
			qaa.setPosBatchNo(item.getBatchNo());
			qaa.setPosNo(item.getPosNo());
			itemsService.queryClientInfoForAdd(qaa);
		} else {
			qaa.setMessage(null);
		}

		model.addAttribute(SessionKeys.ACCEPTANCE_QUERY_AND_ADD_CLIENT, qaa);
		return ViewNames.ACCEPTANCE_BRANCH_CLIENT_QUERY_OR_NEW;
	}

	/**
	 * 根据客户号查询客户信息
	 * 
	 * @param itemsInput
	 * @return
	 */
	@RequestMapping("/queryClientByClientNo")
	public String queryClientByClientNo(
			@ModelAttribute(SessionKeys.ACCEPTANCE_QUERY_AND_ADD_CLIENT) QueryAndAddClientDTO qaa,
			HttpSession session, String clientNo) {

		itemsService.queryClientInfoByClientNo(clientNo, qaa);

		ServiceItemsInputDTO item = (ServiceItemsInputDTO) session
				.getAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT);
		BeanWrapper bw = new BeanWrapperImpl(item);
		bw.setPropertyValue(qaa.getRewriteClientNoPropName(), clientNo);

		return ViewNames.ACCEPTANCE_BRANCH_CLIENT_QUERY_OR_NEW;
	}

	/**
	 * 根据5项信息查询客户信息
	 * 
	 * @param itemsInput
	 * @return
	 */
	@RequestMapping("/queryClientByFive")
	public String queryClientByFive(
			@ModelAttribute(SessionKeys.ACCEPTANCE_QUERY_AND_ADD_CLIENT) QueryAndAddClientDTO qaa,
			HttpSession session) {
		itemsService.queryClientByFive(qaa);

		if (StringUtils.isNotBlank(qaa.getRewriteClientNoPropName())) {// 如果查到客户号了就赋上去
			ServiceItemsInputDTO item = (ServiceItemsInputDTO) session
					.getAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT);
			BeanWrapper bw = new BeanWrapperImpl(item);
			bw.setPropertyValue(qaa.getRewriteClientNoPropName(),
					qaa.getClientNo());
		}

		return ViewNames.ACCEPTANCE_BRANCH_CLIENT_QUERY_OR_NEW;
	}

	/**
	 * 提交新增的客户信息
	 * 
	 * @param itemDTO
	 */
	@RequestMapping("/queryAndAddClientSubmit")
	public String queryAndAddClientSubmit(
			@ModelAttribute(SessionKeys.ACCEPTANCE_QUERY_AND_ADD_CLIENT) QueryAndAddClientDTO qaa,
			HttpSession session) {
		// 如果没有客户信息
		if ((qaa.getClientInfo() == null || StringUtils.isBlank(qaa
				.getClientInfo().getClientNo())) && !qaa.isNewClientAdded()) {
			itemsService.insertNewClientInfo(qaa);
			return ViewNames.ACCEPTANCE_BRANCH_CLIENT_QUERY_OR_NEW;
		} else {
			// 最后一次提交时更新了Session中的值，此时新增的值是没有客户号的，需要再更新
			qaa.updateClientNo();

			ServiceItemsInputDTO item = (ServiceItemsInputDTO) session
					.getAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT);
			BeanWrapper bw = new BeanWrapperImpl(item);

			if (StringUtils.isNotBlank(qaa.getRewriteClientNoPropName())) {
				bw.setPropertyValue(qaa.getRewriteClientNoPropName(),
						qaa.getClientNo());
			}

			if (StringUtils.isNotBlank(qaa.getRewriteQAACPropName())) {
				bw.setPropertyValue(qaa.getRewriteQAACPropName(), qaa);
			}

			// 如果是在新增客户信息提交后又修改其他信息，再次点确定
			return "redirect:/acceptance/branch/acceptDetailInput/"
					+ qaa.getPosBatchNo() + "/" + qaa.getPosNo();
		}
	}

	/**
	 * 取消提交客户信息
	 */
	@RequestMapping("/queryAndAddClientCancel")
	public String queryAndAddClientCancel(
			@ModelAttribute(SessionKeys.ACCEPTANCE_QUERY_AND_ADD_CLIENT) QueryAndAddClientDTO qaa,
			HttpSession session) {
		ServiceItemsInputDTO item = (ServiceItemsInputDTO) session
				.getAttribute(SessionKeys.ACCEPTANCE_ITEM_INPUT);
		BeanWrapper bw = new BeanWrapperImpl(item);

		if (StringUtils.isNotBlank(qaa.getRewriteClientNoPropName())) {
			bw.setPropertyValue(qaa.getRewriteClientNoPropName(), null);
		}

		if (StringUtils.isNotBlank(qaa.getRewriteQAACPropName())) {
			bw.setPropertyValue(qaa.getRewriteQAACPropName(), null);
		}
		session.removeAttribute(SessionKeys.ACCEPTANCE_QUERY_AND_ADD_CLIENT);
		return "redirect:/acceptance/branch/acceptDetailInput/"
				+ qaa.getPosBatchNo() + "/" + qaa.getPosNo();
	}

	/**
	 * 查询产品的费率因子
	 * 
	 * @param productCode
	 * @param type
	 *            P-缴费期，C-保险期
	 * @param typeValue
	 *            类型的值，如，交几"年"，保障几"岁"等
	 * @return
	 */
	@RequestMapping("/queryProductPremRate")
	@ResponseBody
	public List queryProductPremPeriod(String productCode, String type,
			String typeValue) {
		return itemsService.queryProductPremRate(productCode, type, typeValue);
	}


	/**
	 * 转保健相关校验，并返回保费等信息
	 * 
	 * @param oldApplyBarCode
	 * @param newApplyBarCode
	 * @return
	 */
	@RequestMapping("/getTranslateApplyInfo")
	@ResponseBody
	public Map<String, Object> getTranslateApplyInfo(String oldApplyBarCode,
			String newApplyBarCode) {
		return itemsService.getTranslateApplyInfo(oldApplyBarCode,
				newApplyBarCode);
	}

	/**
	 * 校验客户证件有效期是否为空
	 * 
	 * @param clientNo
	 * @return String
	 * @author WangMingShun
	 * @time 2015-04-02
	 */
//	@RequestMapping(value = "/checkClientIdnoValidityDate", method = RequestMethod.GET)
//	@ResponseBody
//	public String checkClientIdnoValidityDate(String clientNo){
//		return itemsService.queryIdnoValidityDate(clientNo);
//	}
	
	@RequestMapping("/isExemptPlan")
	@ResponseBody
	public Map<String,Object> isExemptPlan(String productCode) {
		Map<String,Object> rMap = new HashMap<String, Object>();
		rMap.put("isExemptPlan", commonQueryDAO.pdSelProductPropertPro(productCode, "98"));
		return rMap;
	}
	
}
