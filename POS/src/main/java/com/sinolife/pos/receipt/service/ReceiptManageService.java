package com.sinolife.pos.receipt.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.file.service.PosFileService;
import com.sinolife.pos.receipt.dao.ReceiptManageDAO;
import com.sinolife.pos.rpc.um.PosUMService;
import com.sinolife.pos.todolist.approval.service.ApprovalService;
import com.sinolife.sf.platform.runtime.PlatformContext;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Service("receiptManageService")
public class ReceiptManageService {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	ReceiptManageDAO receiptDAO;

	@Autowired
	CommonQueryDAO queryDAO;

	@Autowired
	ApprovalService approvalService;

	@Autowired
	PosUMService umService;

	@Autowired
	PosFileService fileService;

	/**
	 * 移动端待处理回执列表
	 */
	public List queryMobileList(String user){
		return receiptDAO.queryMobileList(user);
	}
	
	/**
	 * 传统待处理回执列表
	 */
	public List queryTraditionalList(String user){
		return receiptDAO.queryTraditionalList(user);
	}
	
	/**
	 * 待处理问题件列表
	 */
	public List queryProblemList(String user){
		return receiptDAO.queryProblemList(user);
	}
	
	/**
	 * 按保单号取
	 */
	public Map fetchPolicyNo(String policyNo) {
		Map map = new HashMap();
		logger.info("fetchPolicyNo: " + policyNo + " started ");

		Map info = receiptDAO.queryContractInfo(policyNo, false,
				PlatformContext.getCurrentUser());

		logger.info("fetchPolicyNo: " + policyNo + " return ContractInfo");
		if (info == null) {
			map.put("flag", "0");
			map.put("message", "保单" + policyNo + "未查询到回执影像，请先扫描回执！");
			return map;
		}

		if ("Y".equals(info.get("SIGN_FLAG"))) {
			map.put("flag", "1");
			map.putAll(info);
			map.put("attachs",
					fileService.fileNameUrlList(policyNo, null, "1;2"));

		} else {

			String msg = receiptDAO.lockContractPolicyNo(policyNo,
					PlatformContext.getCurrentUser());
			if (!"Y".equals(msg)) {
				map.put("flag", "0");
				map.put("message", msg);

			} else {

				logger.info("fetchPolicyNo: " + policyNo
						+ " imageSearch  start");
				map.put("imageBarcodeNo", (String) info.get("BAR_CODE"));// 回执影像barCode

				Map sign = queryDAO.querySignBarcode(policyNo,
						(String) info.get("APPLICANT_NO"));
				String url = approvalService.querySignJPGUrl(
						(String) sign.get("barcodeNo"),
						(String) sign.get("image"));

				logger.info("fetchPolicyNo: " + policyNo
						+ " imageSearch end at:");
				map.put("signJpgUrl", url);// 签名影像url
				map.put("policyApplyBarcode", info.get("APPLY_BAR_CODE"));
				map.put("provideDate", info.get("PROVIDE_DATE"));
				map.put("policyNo", info.get("POLICY_NO"));
				map.put("receiptStatus", info.get("RECEIPT_STATUS"));
			}
		}

		return map;
	}

	/**
	 * 直接取下一单
	 * 
	 * @return
	 */
	public Map fetchNext(String channel) {
		Map map = new HashMap();
		String curUser = PlatformContext.getCurrentUser();

		logger.info(curUser + "fetchNextmyFeedbackTaskstart...");
		List policy = receiptDAO.myFeedbackTask(curUser);

		logger.info(curUser + "fetchNextmyFeedbackTaskend ...");
		if (policy == null || policy.size() < 1) {

			logger.info(curUser + "fetchNextunSignPolicyScanedListstart....");

			policy = receiptDAO.unSignPolicyScanedList(channel);

			logger.info(curUser
					+ "fetchNextunSignPolicyScanedListendpolicyNo : " + policy);

		}

		if (policy == null || policy.size() < 1) {
			map.put("flag", "0");
			map.put("message", "目前没有待回执回销的保单");
			return map;
		}

		Map tmp = (Map) policy.get(0);
		String policyNo = (String) tmp.get("POLICY_NO");

		String msg = receiptDAO.lockContractPolicyNo(policyNo, curUser);
		if (!"Y".equals(msg)) {
			map.put("flag", "0");
			map.put("message", msg);

		} else {
			// Map eMap = receiptDAO.queryPolicyImage(policyNo);
			String barCode = (String) tmp.get("BAR_CODE");
			map.put("policyNo", policyNo);
			map.put("provideDate", tmp.get("PROVIDE_DATE"));
			map.put("imageBarcodeNo", barCode);// 回执影像barCode
			logger.info(curUser + "fetchNextquerySignImage" + policyNo
					+ " start... ");

			Map sign = queryDAO.querySignBarcode(policyNo,
					(String) tmp.get("APPLICANT_NO"));
			String url = approvalService.querySignJPGUrl(
					(String) sign.get("barcodeNo"), (String) sign.get("image"));

			logger.info(curUser + "fetchNextquerySignImage" + policyNo
					+ " end ... ");
			map.put("signJpgUrl", url);// 签名影像url
		}

		map.put("channelChosed", channel);
		return map;
	}

	/**
	 * 回执回销
	 * 
	 * @param policyNo
	 * @param signDate
	 * @param remark
	 */
	public void feedbackInput(String policyNo, String signDate, String remark) {
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("signFlag", "Y");
		try {
			pMap.put("signDate",
					new SimpleDateFormat("yyyy-MM-dd").parse(signDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		pMap.put("confirmDate", queryDAO.getSystemDate());
		pMap.put("confirmUser", PlatformContext.getCurrentUser());
		pMap.put("remark", remark);

		receiptDAO.updatePolicyContract(pMap);
		receiptDAO.updatePosReceiptDeal(policyNo, remark, "3");
	}

	public void feedbackProblem(String policyNo, String note) {
		receiptDAO.updatePosReceiptDeal(policyNo, note, "2");
	}

	public void feedbackCancel(String policyNo, String status) {
		receiptDAO.updatePosReceiptDeal(policyNo, null, status);
	}

	public Map dateUpdateQuery(String policyNo) {
		String user = PlatformContext.getCurrentUser();
		return receiptDAO.queryContractInfo(policyNo,
				umService.hasRole(user, "POS_SENIOR_BIZ"), user);
	}

	/**
	 * 更新回销或录入日期
	 * 
	 * @param entryFlag
	 * @param policyNo
	 * @param newDate
	 * @param remark
	 */
	public void dateUpdateSubmit(String entryFlag, String policyNo,
			String newDate, String remark) {
		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("confirmUser", PlatformContext.getCurrentUser());
		try {
			if ("S".equals(entryFlag)) {
				if (StringUtils.isBlank(newDate)) {
					pMap.put("signFlag", "N");
					pMap.put("nullKeys", "client_sign_date, confirm_date");
				} else {
					pMap.put("signDate",
							new SimpleDateFormat("yyyy-MM-dd").parse(newDate));
				}

			} else if ("C".equals(entryFlag)) {
				pMap.put("confirmDate",
						new SimpleDateFormat("yyyy-MM-dd").parse(newDate));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		receiptDAO.updatePolicyContract(pMap);
		receiptDAO.updatePosReceiptDeal(policyNo, remark, null);
	}

	public Map memoPolicyQuery(String policyNo) {
		Map rstMap = new HashMap();
		Map map = receiptDAO.memoPolicyQuery(policyNo,
				PlatformContext.getCurrentUser());
		if (map == null) {
			rstMap.put("RETURN_MSG", "查询不到保单相关信息，请核实");

		} else if ("Y".equals(map.get("SIGN_FLAG"))
				&& "N".equals(map.get("MEMO_FLAG"))) {
			rstMap.put("RETURN_MSG", "保单回执已签收，不能操作报备");
			rstMap.put("POLICY_NO", policyNo);

		} else if ("N".equals(map.get("MEMO_FLAG"))) {
			rstMap.putAll(map);

		} else {
			rstMap.putAll(map);
			rstMap.put("attachment",
					fileService.oneFileNameUrl(policyNo, null, "3"));
		}
		return rstMap;
	}

	/**
	 * 报备提交时对日期做检查
	 * 
	 * @param policyNo
	 * @param provideDate
	 * @return
	 */
	public String memoCheck(String memoDate, String provideDate, String channel) {
		DateFormat formt = new SimpleDateFormat("yyyy-MM-dd");
		Date mDate = null;// 报备日期
		Date pDate = null;// 打印日期
		try {
			mDate = formt.parse(memoDate);
			pDate = formt.parse(provideDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (pDate == null) {
			return "保单打印日期缺失，不能报备";
		}

		if (mDate.compareTo(pDate) < 0) {
			return "报备日期不能早于保单打印日期";

		} else if (!umService.hasRole(PlatformContext.getCurrentUser(),
				"POS_SENIOR_BIZ")) {
			long l = mDate.getTime() / 86400000 - pDate.getTime() / 86400000;
			if ("00".equals(channel) && l > 12) {
				return "银保通保单报备日期不能晚于打印日期后12天";
			} else if (("01".equals(channel) || "02".equals(channel)) && l > 22) {
				return "个险或银代保单报备日期不能晚于打印日期后22天";
			} else if (l > 25) {
				return "经代等渠道保单报备日期不能晚于打印日期后25天";
			}
		}
		return "Y";
	}

	/**
	 * 报备提交
	 * 
	 * @param policyNo保单号
	 * @param memoDate报备日期
	 * @param cause备注
	 */
	public void memoSubmit(String policyNo, String memoDate, String cause) {
		receiptDAO.memoSubmit(policyNo, memoDate, cause);
	}

	public List otherReport(Map pMap) {
		return receiptDAO.otherReport(pMap);
	}

	/**
	 * 是否是机构回执受理员，此角色只能看到4/7清单
	 * 
	 * @return
	 */
	public String branchAcceptor() {
		String user = PlatformContext.getCurrentUser();
		boolean b = umService.hasRole(user, "POS_SENIOR_BIZ")
				|| umService.hasRole(user, "POS_RECEIPT");
		return b ? "N" : "Y";
	}

	public Map skipCurrent(String policyNo, String skipFlag, String channel) {
		Map rstMap = new HashMap();
		String policy = null;
		Map map = null;
		List list = receiptDAO.unSignPolicyScanedList(channel);
		// DMP-5493优化为每次只抽取出两单，现在只抽取从不是当前单的另外一单就可以了
		for (int i = 0; list != null && i < list.size(); i++) {
			map = (Map) list.get(i);

			if (!policyNo.equals(map.get("POLICY_NO"))) {

				policy = (String) map.get("POLICY_NO");
				break;
			}
		}

		//
		// for (int i = 0; list != null && i < list.size(); i++) {
		// map = (Map) list.get(i);
		// if (policyNo.equals(map.get("POLICY_NO"))) {
		// if ("N".equals(skipFlag) && list.size() > i + 1
		// || "P".equals(skipFlag) && i > 0) {
		// map = (Map) list.get(i + ("N".equals(skipFlag) ? 1 : -1));
		// policy = (String) map.get("POLICY_NO");
		// }
		// break;
		// }
		// }
		// if (policy == null && list != null && list.size() > 0) {
		// map = (Map) list.get("N".equals(skipFlag) ? 0 : list.size() - 1);//
		// 首尾相连
		// if (!policyNo.equals(map.get("POLICY_NO"))) {
		// policy = (String) map.get("POLICY_NO");
		// }
		// }

		rstMap.put("policyNo", policy);
		// rstMap.put("leftPolicyCount", list != null ? list.size() - 1 : 0);
		return rstMap;
	}

	/**
	 * 查询当前用户作业池中及所属机构作业池中为回销保单的总数
	 * 
	 * @param channel
	 * @return
	 */
	public String queryCurUserUnReceiptCount(String channel) {
		String user = PlatformContext.getCurrentUser();
		int curUserCount = receiptDAO.getMyFeedBackTaskCount(user).intValue();
		int branchCount = receiptDAO.getUserBranchUnReceiptCount(channel, user)
				.intValue();
		return curUserCount + "," + branchCount;

	}

	/**
	 * 银保通系统接口 --通过保单号修改扉页号
	 * 
	 * @param policyNo
	 * @param pressNoOld
	 * @return
	 */
	public String modifyPressNo(String policyNo, String pressNoOld,
			String premssNoNew) {

		Map pMap = new HashMap();
		pMap.put("policyNo", policyNo);
		pMap.put("pressNoOld", pressNoOld);
		pMap.put("pressNoNew", premssNoNew);
		receiptDAO.modifyPressNo(pMap);
		return (String) (pMap.get("flag"));

	}

	/**
	 * 银保通系统接口 --通过保单号查询扉页号
	 * 
	 * @param policyNo
	 * @return
	 */
	public String queryPressNo(String policyNo) {

		return receiptDAO.queryPressNo(policyNo);

	}

	/**
	 * 更新影像主索引
	 * 
	 * @param imageBarcode
	 * @param imageBarcodeOld
	 * @param imageBarcodeNew
	 * @return
	 */
	public String modifyImageBarcode(String imageBarcode,
			String imageBarcodeOld, String imageBarcodeNew) {
		Map pMap = new HashMap();
		pMap.put("imageBarcode", imageBarcode);
		pMap.put("imageBarcodeOld", imageBarcodeOld);
		pMap.put("imageBarcodeNew", imageBarcodeNew);
		receiptDAO.modifyImageBarcode(pMap);
		return (String) (pMap.get("flag"));

	}

	/**
	 * 查询影像主索引
	 * 
	 * @param barCodeNo
	 * @return
	 */
	public String queryImageMainIndex(String barCodeNo) {

		return receiptDAO.queryImageMainIndex(barCodeNo);

	}
	/**
	 * 查询表Policy_Contract信息
	 * @param policyNo
	 * @return
	 */
	public Map<String, Object> queryPolicyContractInfo(String policyNo) {
		return (Map<String, Object>)receiptDAO.queryPolicyContractInfo(policyNo);
	}
	public void updateSignFlag(String policyNo) {
		receiptDAO.updateSignFlag(policyNo);
	}
	
	/**
	 * 根据保单号查询barcode(只做查询用)
	 * 
	 * @param policyNo
	 * @return barCode
	 */
	public String getPolicyNoReceiptBarCode01(String policyNo) {
		return receiptDAO.getPolicyNoReceiptBarCode01(policyNo);
	}
	
	/**
	 * 更新 保全保单介质数据调整日志表
	 */
	public void addPosPolContractLog(String policyNo, String processor, Date processDate,
			String columnName, String oldValue, String newValue, String remark) {
		receiptDAO.addPosPolContractLog(policyNo, processor, processDate,
				columnName, oldValue, newValue, remark);
	}
}
