package com.sinolife.pos.approve.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinolife.pos.approve.dao.ApproveManageDAO;
import com.sinolife.pos.print.service.PrintService;

@SuppressWarnings({ "rawtypes" })
@Service("ApproveManageService")
public class ApproveManageService {

	@Autowired
	ApproveManageDAO approveManageDAO;
	
	@Autowired
	private PrintService printService;

	/**
	 * 不良记录查询
	 * 
	 * @param paraMap
	 * @return List
	 * @author GaoJiaMing
	 * @time 2014-8-19
	 */
	public List queryBadBehavior(Map paraMap) {
		return approveManageDAO.queryBadBehavior(paraMap);
	}

	/**
	 * 不良记录新增
	 * 
	 * @param paraMap
	 * @return void
	 * @author GaoJiaMing
	 * @time 2014-8-21
	 */
	public String insertBadBehavior(Map paraMap) {
		approveManageDAO.insertBadBehavior(paraMap);
		// 是否需要送审,返回审批人需要送审，返回空不需要
		String result = approveManageDAO.needExamPrivsApproved(paraMap);
		if (!StringUtils.isBlank(result)) {
			paraMap.put("approveUser", result);
			approveManageDAO.insertPosExamPrivsApprove(paraMap);
			// 给审批人发送邮件
			String content = "尊敬的" + result + "用户:" + "<br/>&nbsp;&nbsp;&nbsp;&nbsp;编号为"
					+ (String) paraMap.get("objectNo") + "的代审人员/网点" + "需要您进行审批，请尽快处理！";
			printService.sendEMail(result, null, "富德生命人寿代审权限审批通知", content, (String) paraMap.get("objectNo"));
		}
		return "Y";
	}

	/**
	 * 根据对象编码获取对象信息
	 * 
	 * @param paraMap
	 * @return Map<String,Object>
	 * @author GaoJiaMing
	 * @time 2014-8-21
	 */
	public Map<String, Object> getObjectInfoByObjectNo(Map<String, Object> paraMap) {
		// 代审权限判断
		if ("Y".equals(paraMap.get("needCheckPrivs"))) {
			Map<String, Object> resultMap = approveManageDAO.checkApprovePrivs(paraMap);
			paraMap.put("privs", resultMap.get("PRIVS"));
			paraMap.put("blacklist", resultMap.get("BLACKLIST"));
			paraMap.put("approving", resultMap.get("APPROVING"));
		}
		// 网点
		if ("1".equals(paraMap.get("objectType"))) {
			paraMap.put("objectName", approveManageDAO.getObjectNameByObjectNo1(paraMap));
		}
		// 银代
		if ("2".equals(paraMap.get("objectType"))) {
			paraMap.put("objectName", approveManageDAO.getObjectNameByObjectNo2(paraMap));
		}
		// 不良次数
		paraMap.put("badBehaviorCount", approveManageDAO.getBadBehaviorCountByObjectNo(paraMap));
		return paraMap;
	}

	/**
	 * 代审审批查询
	 * 
	 * @param paraMap
	 * @return List
	 * @author GaoJiaMing
	 * @time 2014-8-19
	 */
	public List queryExamPrivsApprovePage(Map paraMap) {
		return approveManageDAO.queryExamPrivsApprovePage(paraMap);
	}

	/**
	 * 代审审核
	 * 
	 * @param paraMap
	 * @return void
	 * @author GaoJiaMing
	 * @time 2014-8-25
	 */
	public void examPrivsApprove(Map paraMap) {
		approveManageDAO.examPrivsApprove(paraMap);
	}

	/**
	 * 撤销黑名单
	 * 
	 * @param paraMap
	 * @return void
	 * @author GaoJiaMing
	 * @time 2014-8-25
	 */
	public void examPrivsCancel(Map paraMap) {
		approveManageDAO.examPrivsCancel(paraMap);
	}

	/**
	 * 根据保全号获取代审信息
	 * 
	 * @param posNo
	 * @return Map<String,Object>
	 * @author GaoJiaMing
	 * @time 2014-9-4
	 */
	public Map<String, Object> getBadBehaviorInfoByPosNo(String posNo) {
		return approveManageDAO.getBadBehaviorInfoByPosNo(posNo);
	}
	
	/**
	 * 查看某银代经理或网点代审权限状态
	 * 
	 * @param paraMap
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-9-9
	 */
	public String viewPrivsByObjectNoAndObjectType(Map<String, Object> paraMap) {
		String result = "代审权限状态：拥有代审权限";
		// 网点
		if ("1".equals(paraMap.get("objectType"))) {
			if (StringUtils.isBlank(approveManageDAO.getObjectNameByObjectNo1(paraMap))) {
				result = "代审权限状态：查无此网点";
				return result;
			}
		}
		// 银代
		if ("2".equals(paraMap.get("objectType"))) {
			if (StringUtils.isBlank(approveManageDAO.getObjectNameByObjectNo2(paraMap))) {
				result = "代审权限状态：查无此银代经理";
				return result;
			}
		}
		Map<String, Object> resultMap = approveManageDAO.checkApprovePrivs(paraMap);
		if ("N".equals(resultMap.get("PRIVS"))) {
			result = "代审权限状态：没有代审权限,未经授权";
			return result;
		} else {
			if ("Y".equals(resultMap.get("BLACKLIST"))) {
				result = "代审权限状态：没有代审权限,已进入代审黑名单";
				return result;
			}
			if ("Y".equals(resultMap.get("APPROVING"))) {
				paraMap.put("approveResult", "A");
				List resultList = approveManageDAO.queryExamPrivsApprovePage(paraMap);
				String approveUser = (String) ((Map) resultList.get(0)).get("APPROVE_USER");
				result = "代审权限状态：拥有代审权限，代审权限目前正在" + approveUser + "名下审批";
				return result;
			}
		}
		return result;
	}
	
	/**
	 * 结合保单号校验代审权限
	 * 
	 * @param paraMap
	 * @return String
	 * @author GaoJiaMing
	 * @time 2014-9-18
	 */
	public String checkPrivsByPolicyNo(Map paraMap) {
		return approveManageDAO.checkPrivsByPolicyNo(paraMap);
	}
	
	/**
	 *  检查是否是代办代审的试点机构
	 * @param map
	 * @return String
	 * @author WangMingShun
	 * @time 2015-4-7
	 */
	public String checkExamBranch(Map paraMap) {
		return approveManageDAO.checkExamBranch(paraMap);
	}
}