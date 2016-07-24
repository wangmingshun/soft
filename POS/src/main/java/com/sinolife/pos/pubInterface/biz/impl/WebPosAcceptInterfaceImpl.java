package com.sinolife.pos.pubInterface.biz.impl;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sinolife.pos.acceptance.branch.service.BranchAcceptService;
import com.sinolife.pos.acceptance.rollback.dao.BranchRollbackDAO;
import com.sinolife.pos.acceptance.trial.service.TrialService;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_19;
import com.sinolife.pos.common.dao.serviceitems.ServiceItemsDAO_20;
import com.sinolife.pos.common.dto.PolicyContactInfoDTO;
import com.sinolife.pos.common.dto.PolicyProductDTO;
import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO;
import com.sinolife.pos.common.dto.serviceitems.ServiceItemsInputDTO_2;
import com.sinolife.pos.common.file.dao.PosFileDAO;
import com.sinolife.pos.common.util.Excel;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.others.unsuspendbankaccount.service.UnsuspendBankAccountService;
import com.sinolife.pos.print.dao.PrintDAO;
import com.sinolife.pos.print.dto.PosNoteDTO;
import com.sinolife.pos.print.notice.NoticePrintHandler;
import com.sinolife.pos.print.notice.NoticePrintHandlerFactory;
import com.sinolife.pos.print.service.PrintService;
import com.sinolife.pos.pubInterface.biz.service.WebPosAcceptInterface;
import com.sinolife.pos.report.dao.ListDAO;
import com.sinolife.pos.report.service.ListService;
import com.sinolife.pos.rpc.ilogjrules.posrules.dao.PosRulesDAO;
import com.sinolife.pos.schedule.dao.ScheduleDAO;
import com.sinolife.sf.framework.template.VelocityService;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.store.SFFile;
import com.sinolife.sf.store.TempFileFactory;

@Service("WebPosAcceptInterface")
public class WebPosAcceptInterfaceImpl extends DevelopPlatformInterfaceImpl
		implements WebPosAcceptInterface {

	@Autowired
	private CommonQueryDAO commonQueryDAO;
	@Autowired
	private BranchAcceptService branchAcceptService;

	@Autowired
	private UnsuspendBankAccountService unsuspendBankAccountService;

	@Autowired
	private PosFileDAO posFileDAO;

	@Autowired
	ServiceItemsDAO_19 serviceItemsDAO_19;
	@Autowired
	private BranchRollbackDAO branchRollbackDAO;

	@Autowired
	ListService service;
	@Autowired
	ListDAO dao;
	@Autowired
	private PosRulesDAO posRulesDAO;

	@Autowired
	private PrintDAO printDAO;

	@Autowired
	private PrintService printService;

	@Autowired
	private ScheduleDAO scheduleDAO;

	@Autowired
	private BranchRollbackDAO rollbackDAO;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	private TrialService trialService;
	
	@Autowired
	private ServiceItemsDAO_20 serviceItemsDAO_20;

	/**
	 * 生成官网保全用的条码
	 * 
	 * @param serviceItems
	 * @return
	 */
	public String generateWPOSBarcode() {
		// 条形码统一改为sequence生成方式
		return "WEBN" + commonQueryDAO.queryBarcodeNoSequence();
	}

	/**
	 * 根据保全项目生成E终端保全用的条码
	 * 
	 * @param serviceItems
	 * @return
	 */
	public String generateEpointBarcode(String serviceItems) {
		String v_doc_type = "";
		// 保险合同变更申请书（补退费类）
		if ("1".equals(serviceItems) || "2".equals(serviceItems)
				|| "9".equals(serviceItems) || "33".equals(serviceItems)) {
			v_doc_type = "1213";
		}
		// 保险合同变更申请书（核保类）
		else if ("35".equals(serviceItems)) {
			v_doc_type = "1212";
		}
		// 保险合同变更申请书（非补退费类）
		else if ("19".equals(serviceItems) || "23".equals(serviceItems)
				|| "26".equals(serviceItems) || "24".equals(serviceItems)
				|| "29".equals(serviceItems) || "28".equals(serviceItems)) {
			v_doc_type = "1211";
		}
		// 投资账户变更申请书
		else if ("38".equals(serviceItems) || "39".equals(serviceItems)) {
			v_doc_type = "1214";
		}
		// 个人寿险保单贷款申请书
		else if ("8".equals(serviceItems)) {
			v_doc_type = "1215";
		} else {
			throw new RuntimeException("该保全项目不能再E服务终端受理");
		}
		String barcodeNo = v_doc_type + "99"
				+ commonQueryDAO.queryEpointBarcodeNoSequence();
		return barcodeNo;
	}

	/**
	 * 用户登录 官网保全撤销（把该客户未完成的保全项目撤销掉（待收费除外））
	 * 
	 */
	public void cancelWebPos(String applicantNo) {
		List<String> posNoList = commonQueryDAO.queryNotCompletePosNos("7",
				applicantNo);
		String posNo = "";
		for (int i = 0; i < posNoList.size(); i++) {
			posNo = posNoList.get(i);
			PosInfoDTO posInfo = commonQueryDAO.queryPosInfoRecord(posNo);
			// 对于要收费的保全项目处于待收费状态是不能回退操作
			if (("9".equals(posInfo.getServiceItems()) || "35".equals(posInfo
					.getServiceItems()))
					&& (("A15".equals(posInfo.getAcceptStatusCode())) || "A16"
							.equals(posInfo.getAcceptStatusCode()))) {
				if ("A16".equals(posInfo.getAcceptStatusCode())) {
					Date Canceldate = new Date();
					Map rolMap = branchRollbackDAO.rollBack(Canceldate, posNo);
					String flag = (String) rolMap.get("flag");
					String message = (String) rolMap.get("message");
					if (!"0".equals(flag)) {
						throw new RuntimeException("受理撤销失败：" + message);
					}
				}
			} else {
				Map<String, Object> ret = branchAcceptService.workflowControl(
						"11", posNo, false);
				String flag = (String) ret.get("flag");
				String msg = (String) ret.get("message");
				if (!"C02".equals(flag)) {
					throw new RuntimeException("受理撤销失败：" + msg);
				}
			}
		}

	}
	/**
	 * 用户登录 对应平台保全撤销（把该平台上客户未完成的保全项目撤销掉（待收费除外））
	 * 
	 */
	public void cancelPos(String applicantNo,String acceptChannelCode) {
		List<String> posNoList = commonQueryDAO.queryNotCompletePosNos(acceptChannelCode,
				applicantNo);
		String posNo = "";
		for (int i = 0; i < posNoList.size(); i++) {
			posNo = posNoList.get(i);
			PosInfoDTO posInfo = commonQueryDAO.queryPosInfoRecord(posNo);
			// 对于要收费的保全项目处于待收费状态是不能回退操作
			if (("9".equals(posInfo.getServiceItems()) || "35".equals(posInfo
					.getServiceItems()))
					&& (("A15".equals(posInfo.getAcceptStatusCode())) || "A16"
							.equals(posInfo.getAcceptStatusCode()))) {
				if ("A16".equals(posInfo.getAcceptStatusCode())) {
					Date Canceldate = new Date();
					Map rolMap = branchRollbackDAO.rollBack(Canceldate, posNo);
					String flag = (String) rolMap.get("flag");
					String message = (String) rolMap.get("message");
					if (!"0".equals(flag)) {
						throw new RuntimeException("受理撤销失败：" + message);
					}
				}
			} else {
				Map<String, Object> ret = branchAcceptService.workflowControl(
						"11", posNo, false);
				String flag = (String) ret.get("flag");
				String msg = (String) ret.get("message");
				if (!"C02".equals(flag)) {
					throw new RuntimeException("受理撤销失败：" + msg);
				}
			}
		}

	}

	/**
	 * 撤销最后完成失败的保全项目
	 * 
	 */
	public void cancelFinalPos(String posNo) {

		PosInfoDTO posInfo = commonQueryDAO.queryPosInfoRecord(posNo);

		if ("A16".equals(posInfo.getAcceptStatusCode())) {
			Date Canceldate = new Date();
			Map rolMap = branchRollbackDAO.rollBack(Canceldate, posNo);
			String flag = (String) rolMap.get("flag");
			String message = (String) rolMap.get("message");
			if (!"0".equals(flag)) {
				throw new RuntimeException("受理撤销失败：" + message);
			}

		} else if (!"A15".equals(posInfo.getAcceptStatusCode())
				&& StringUtils.startsWith(posInfo.getAcceptStatusCode(), "A")) {
			Map<String, Object> ret = branchAcceptService.workflowControl("11",
					posNo, false);
			String flag = (String) ret.get("flag");
			String msg = (String) ret.get("message");
			if (!"C02".equals(flag)) {
				throw new RuntimeException("受理撤销失败：" + msg);
			}
		}

	}

	/**
	 * 根据保单号和投保人id查询客户的联系信息
	 * 
	 * @param clientNo
	 * @param policyNo
	 * @return
	 */
	public List<PolicyContactInfoDTO> queryAppContactInfo(String clientNo,
			String policyNo) {

		return serviceItemsDAO_19.queryAppContactInfo(clientNo, policyNo);

	}

	/**
	 * 记录电子批单到文件表
	 * 
	 * @param fileId
	 * @param fileName
	 * @param policyNo
	 * @param posNo
	 * @param userId
	 */
	public void insertPosFileInfo(String fileId, String fileName,
			String policyNo, String posNo, String userId) {

		posFileDAO.insertPosFileInfo(fileId, fileName, policyNo, posNo, "7",
				userId);

	}

	/**
	 * 根据保全号返回电子批单文件ID Map 包括key（file_id,file_name,upload_time, upload_user）
	 * 
	 * @param posNo
	 * @return
	 */
	public Map<String, String> queryPosFileInfoForEnt(String posNo) {
		Map<String, String> map = null;
		List<Map<String, String>> lst = posFileDAO.queryPosFileInfo("", posNo,
				"7");
		if (lst != null && lst.size() > 0) {
			map = (Map<String, String>) lst.get(0);
		}
		return map;
	}

	@Override
	public SFFile posexpirationpayList(Map map) {
		SFFile sf = null;

		try {

			sf = exportListExcel(map);

		} catch (Exception e) {

			e.printStackTrace();
		}

		return sf;
	}

	/**
	 * @param pMap
	 * @return
	 * @throws Exception
	 */
	public SFFile exportListExcel(Map pMap) throws Exception {

		VelocityService velocityService = PlatformContext.getGoalbalContext(
				VelocityService.class, VelocityService.class);
		List<String> displayDescList = new ArrayList<String>();
		StringBuffer headerSql = new StringBuffer();

		// 查询清单，并插入临时表

		Map returnMap = service.queryPayList(pMap);

		// 查询清单显示字段基表
		String listCode = (String) returnMap.get("LISTCODE");
		List<Map<String, String>> codeList = service.getListHeader(listCode);
		List<String> keyList = new ArrayList<String>();

		// 组装Excel显示字段和查询字段变量
		for (int i = 0; i < codeList.size(); i++) {
			displayDescList.add(codeList.get(i).get("DISPLAYDESC"));
			keyList.add("CEL" + i);
			if (i == 0) {
				headerSql = headerSql.append(codeList.get(i).get("COLUMNNAME")
						+ " cel0");

			} else {
				headerSql = headerSql.append(","
						+ codeList.get(i).get("COLUMNNAME") + " cel" + i);
			}
		}
		// 从临时表查询清单记录

		String queryIndex = (String) returnMap.get("QUERYINDEX");
		final int len = service.getListCount(queryIndex);

		List<Map<String, Object>> reportList = null;
		if (len > 0) {

			int limitedNum = 1000;
			int start = 0;

			for (start = 0; start < len / limitedNum + 1; start++) {
				reportList = service.getListResult(queryIndex,
						headerSql.toString(), start * limitedNum + 1,
						(start + 1) * limitedNum);

			}

		}

		File file = new File(Excel.listToExcel(displayDescList, keyList,
				reportList));

		SFFile tmpFile = tempFileFactory.createTempFile();
		file.renameTo(tmpFile);

		return tmpFile;

	}

	public static String formatDate(Date date, String fmt) {
		SimpleDateFormat sDateFormat = new SimpleDateFormat(fmt);
		String formateDate = sDateFormat.format(date);
		return formateDate;
	}

	private TempFileFactory tempFileFactory = TempFileFactory
			.getInstance(getClass());

	/**
	 * 根据不同的保全项目找出官网可以操作的保单
	 * 
	 * @param policyNos
	 * @param serviceItems
	 * @return
	 */
	public List<String> getCanDoPolicys(List<String> policyNos,
			String serviceItems) {
		// 没有加挂保单直接返回
		if (policyNos == null || policyNos.size() == 0)
			return new ArrayList<String>();
		List<String> pNos = new ArrayList<String>();
		List<PolicyProductDTO> policys = new ArrayList<PolicyProductDTO>();
		boolean canDoService = false;
		Map<String, Object> criteriaMap = new HashMap<String, Object>();
		Map<String, Object> retMap = new HashMap<String, Object>();
		criteriaMap.put("policyNosList", policyNos);
		for (String policyNo : policyNos){
			if ("Y".equals(commonQueryDAO.canDoServiceItems(policyNo, serviceItems,
					"7"))) {
				pNos.add(policyNo);
			}
		}
		/*// 契撤
		if ("1".equals(serviceItems)) {

			for (String policyNo : policyNos) {

				if ("Y".equals(commonQueryDAO.canDoServiceItems(policyNo, "1",
						"7"))) {

					pNos.add(policyNo);

				}

			}

		}
		// 退保
		else if ("2".equals(serviceItems)) {

			for (String policyNo : policyNos) {

				if ("Y".equals(commonQueryDAO.canDoServiceItems(policyNo, "2",
						"7"))) {
     				pNos.add(policyNo);
				}
			}
		}

		// 操作保单还款(存在自垫或贷款)
		else if ("9".equals(serviceItems)) {
			for (String policyNo : policyNos) {
				if ("Y".equals(commonQueryDAO.canDoServiceItems(policyNo, "9","7"))) {
			       pNos.add(policyNo);
                 }
               }
		}
		// 预约终止附加险
		else if ("29".equals(serviceItems)) {
			for (String policyNo : policyNos) {
				if ("Y".equals(commonQueryDAO.canDoServiceItems(policyNo, "29","7"))) {
			       pNos.add(policyNo);
                 }
               }
		}
		// 保全收付费方式调整
		else if ("33".equals(serviceItems)) {
			for (String policyNo : policyNos) {
				if ("Y".equals(commonQueryDAO.canDoServiceItems(policyNo, "33","7"))) {
			       pNos.add(policyNo);
                 }
               }
		}
		// 追加保费
		else if ("35".equals(serviceItems)) {
			for (String policyNo : policyNos) {
				if ("Y".equals(commonQueryDAO.canDoServiceItems(policyNo, "35",
						"7"))) {
					pNos.add(policyNo);
				}
			}
		}
		// 续期缴费方式变更
		else if ("23".equals(serviceItems)) {
			for (String policyNo : policyNos) {
				if ("Y".equals(commonQueryDAO.canDoServiceItems(policyNo, "23","7"))) {
			       pNos.add(policyNo);
                 }
               }
		}
		// 年金给付方式变更
		else if ("24".equals(serviceItems)) {
			for (String policyNo : policyNos) {
				if ("Y".equals(commonQueryDAO.canDoServiceItems(policyNo, "24","7"))) {
			       pNos.add(policyNo);
                 }
               }
		}
		// 账户比例变更
		else if ("38".equals(serviceItems)) {
			for (String policyNo : policyNos) {
	            if ("Y".equals(commonQueryDAO.canDoServiceItems(policyNo, "38",
							"7"))) {
						pNos.add(policyNo);
					}
				}
			}
		// 投资账户转换
		else if ("39".equals(serviceItems)) {
			for (String policyNo : policyNos) {
	            if ("Y".equals(commonQueryDAO.canDoServiceItems(policyNo, "39",
							"7"))) {
						pNos.add(policyNo);
					}
				}
			}
		// 生存金领取
		else if ("10".equals(serviceItems)) {
			for (String policyNo : policyNos) {
				if ("Y".equals(commonQueryDAO.canDoServiceItems_10(policyNo))) {
					pNos.add(policyNo);
				}
			}
		}
		// 投资账户转换
		else if ("21".equals(serviceItems)) {
			for (String policyNo : policyNos) {
				if ("Y".equals(commonQueryDAO.canDoServiceItems_21(policyNo))) {
					pNos.add(policyNo);
				}
			}
		}
		// 部分领取
		else if ("37".equals(serviceItems)) {
			for (String policyNo : policyNos) {
            if ("Y".equals(commonQueryDAO.canDoServiceItems(policyNo, "37",
						"7"))) {
					pNos.add(policyNo);
				}
			}

		}*/
		return pNos;
	}

	/**
	 * 根据不同的保全项目找出E终端可以操作的保单
	 * 
	 * @param policyNos
	 * @param serviceItems
	 * @return
	 */
	public List<String> getEpointCanDoPolicys(List<String> policyNos,
			String serviceItems) {
		// 没有加挂保单直接返回
		if (policyNos == null || policyNos.size() == 0)
			return new ArrayList<String>();
		Map<String, Object> criteriaMap = new HashMap<String, Object>();
		Map<String, Object> retMap = new HashMap<String, Object>();
		criteriaMap.put("policyNosList", policyNos);
		List<String> pNos = new ArrayList<String>();
		boolean canDoService = false;
		List<PolicyProductDTO> policys = new ArrayList<PolicyProductDTO>();

		if ("1".equals(serviceItems)) {

			for (String policyNo : policyNos) {

				if ("Y".equals(commonQueryDAO.canDoServiceItems(policyNo, "1",
						"15"))) {

					pNos.add(policyNo);

				}

			}

		}

		else if ("2".equals(serviceItems)) {

			for (String policyNo : policyNos) {

				if ("Y".equals(commonQueryDAO.canDoServiceItems(policyNo, "2",
						"15"))) {

					pNos.add(policyNo);

				}

			}

		}
		// 操作保单还款(存在自垫或贷款)
		else if ("9".equals(serviceItems)) {
			for (String policyNo : policyNos) {
				if ("Y".equals(commonQueryDAO.canDoServiceItems(policyNo, "9","15"))) {
			       pNos.add(policyNo);
                 }
               }
		}
		// 预约终止附加险
		else if ("29".equals(serviceItems)) {
			for (String policyNo : policyNos) {
				if ("Y".equals(commonQueryDAO.canDoServiceItems(policyNo, "29","15"))) {
			       pNos.add(policyNo);
                 }
               }
		}

		// 保全收付费方式调整
		else if ("33".equals(serviceItems)) {
			for (String policyNo : policyNos) {
				if ("Y".equals(commonQueryDAO.canDoServiceItems(policyNo, "33","15"))) {
			       pNos.add(policyNo);
                 }
               }
		}
		// 追加保费
		else if ("35".equals(serviceItems)) {
			for (String policyNo : policyNos) {
				if ("Y".equals(commonQueryDAO.canDoServiceItems(policyNo, "35",
						"15"))) {
					pNos.add(policyNo);
				}
			}
		}
		// 续期缴费方式变更
		else if ("23".equals(serviceItems)) {
			for (String policyNo : policyNos) {
				if ("Y".equals(commonQueryDAO.canDoServiceItems(policyNo, "23","15"))) {
			       pNos.add(policyNo);
                 }
               }
		}
		// 年金给付方式变更
		else if ("24".equals(serviceItems)) {
			for (String policyNo : policyNos) {
				if ("Y".equals(commonQueryDAO.canDoServiceItems(policyNo, "24","15"))) {
			       pNos.add(policyNo);
                 }
               }
		}
		// 账户比例变更
		else if ("38".equals(serviceItems)) {
			for (String policyNo : policyNos) {
				if ("Y".equals(commonQueryDAO.canDoServiceItems_38(policyNo))) {
					pNos.add(policyNo);
				}
			}
		}
		// 投资账户转换
		else if ("39".equals(serviceItems)) {
			for (String policyNo : policyNos) {
				if ("Y".equals(commonQueryDAO.canDoServiceItems_39(policyNo))) {
					pNos.add(policyNo);
				}
			}
		}
		return pNos;
	}

	/**
	 * 根据受理机构查询柜面号
	 * 
	 * @param branchCode
	 *            机构代码
	 * @return 柜面号
	 */
	public String getEpointCounterNo(String branchCode) {
		return commonQueryDAO.getEpointCounterNo(branchCode);
	}

	/**
	 * 取消指定保单号的转账暂停
	 * 
	 * @param policyNo
	 * @return
	 */
	public void unsuspendBankAccount(String policyNo) {

		PosInfoDTO posInfoDTO = commonQueryDAO.queryTransferFailInfo(policyNo);

		unsuspendBankAccountService.unsuspendBankAccount(posInfoDTO.getPosNo());
	}

	/**
	 * 官网查询保全转账失败详情
	 * 
	 * @param policyNo
	 * @return
	 */
	public PosInfoDTO queryTransferFailInfo(String policyNo) {

		return commonQueryDAO.queryTransferFailInfo(policyNo);

	}

	/**
	 * 根据理财合同号和缴费金额、缴费日期，计算初始费用金额 返回追加保费实际追加金额
	 * 
	 * @return
	 */
	public Double calcInitialExpenses(String policyNo, String p_prem_amount) {

		Map<String, Object> map = commonQueryDAO.calcInitialExpenses(policyNo,
				p_prem_amount);
		BigDecimal b = (BigDecimal) map.get("p_init_expenses");
		return Double.valueOf(p_prem_amount) - b.doubleValue();
	}

	/**
	 * 判断是否是投连或万能险种
	 * 
	 * @param policyNo
	 * @return 理财类型 1:投连 2:万能 0:非理财险种
	 */
	public String getFinancialPolicyProductByPolicyNo(String policyNo) {

		Map<String, Object> retMap = commonQueryDAO
				.getFinancialPolicyProductByPolicyNo(policyNo);
		String flag = (String) retMap.get("p_flag");
		if ("1".equals(flag))
			return "0";
		return (String) retMap.get("p_financial_type");

	}

	/**
	 * 查询客户e服务权限等级
	 * 
	 * @param policyNo
	 * @return
	 */
	public String queryClientInputPrivs(String clientNo) {
		return commonQueryDAO.queryClientInputPrivs(clientNo);
	}

	/**
	 * 获取险种除贷款自垫及保单余额外的退费总额
	 * 
	 * @param p_policy_no
	 * @return
	 */
	public BigDecimal getProductPremSum(String p_policy_no, String p_prod_seq,
			Date p_calc_date) {
		return commonQueryDAO.getProductPremSum(p_policy_no, p_prod_seq,
				p_calc_date);
	}

	/**
	 * 获取保单余额
	 * 
	 * @param p_policy_no
	 * @return
	 */
	public BigDecimal getPolicyBlance(String p_policy_no) {
		return commonQueryDAO.getPolicyBlance(p_policy_no);
	}

	/**
	 * 获取险种应退金额
	 * 
	 * @param p_policy_no
	 * @return
	 */
	public BigDecimal getPremPaid(String p_policy_no, String p_prod_seq,
			Date p_calc_date) {
		Map<String, Object> retMap = commonQueryDAO.getPremPaid(p_policy_no,
				p_prod_seq, p_calc_date);
		return (BigDecimal) retMap.get("p_prem_paid");
	}

	/**
	 * 电子函件订阅/退订接口
	 * 
	 * @param clientNo
	 * @param eLetterService
	 *            电子信函订阅标志：Y表示订阅，N表示退订
	 * @param emailAddress
	 *            邮箱
	 * @return Map<String, Object>
	 *         key="p_flag"：Y-成功，N-失败，E-异常；key="p_message"：异常信息
	 *         ；key="p_client_no"：客户号；
	 * @author GaoJiaMing
	 * @time 2014-7-1
	 */
	public Map<String, Object> procELetterService(String clientNo,
			String eLetterService, String emailAddress) {
		return printDAO.procELetterService(clientNo, eLetterService,
				emailAddress);
	}

	/**
	 * 官网保单全部加挂标志同步接口
	 * 
	 * @param clientNo
	 * @param addAll
	 *            是否全部加挂：Y是，N否
	 * @return Map<String, Object>
	 *         key="p_flag"：Y-成功，N-失败，E-异常；key="p_message"：异常信息
	 *         ；key="p_client_no"：客户号；
	 * @author GaoJiaMing
	 * @time 2014-7-2
	 */
	public Map<String, Object> syncAddAllFlag(String clientNo, String addAll) {
		return printDAO.syncAddAllFlag(clientNo, addAll);
	}

	/**
	 * 官网加挂保单同步接口
	 * 
	 * @param policyNoList
	 * @param isAdded
	 *            是否加挂：Y-是；N-否；
	 * @return List<Map<String, Object>>
	 *         key="p_flag"：Y-成功，N-失败，E-异常；key="p_message"：异常信息
	 *         ；key="p_policy_no"：保单号；
	 * @author GaoJiaMing
	 * @time 2014-7-2
	 */
	public List<Map<String, Object>> syncEPolicy(List<String> policyNoList,
			String isAdded) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String, Object> resultMap = null;
		for (String policyNo : policyNoList) {
			resultMap = printDAO.syncEPolicy(policyNo, isAdded);
			resultList.add(resultMap);
		}
		return resultList;
	}

	/**
	 * 查询客户电子信函订阅状态
	 * 
	 * @param clientNo
	 * @return String Y-是；N-否
	 * @author GaoJiaMing
	 * @time 2014-7-3
	 */
	public String getClientELetterStatus(String clientNo) {
		return printDAO.getClientELetterStatus(clientNo);
	}

	/**
	 * 函件历史查询接口
	 * 
	 * @param paraMap
	 *            key-clientNo：客户号；key-startTime(Date)：开始时间；key-endTime(Date)：
	 *            结束时间；key-noteType：通知书类型；
	 * @return List<Map<String,Object>> Key:CLIENT_NO --客户号 KEY:POLICY_NO --保单号
	 *         KEY:PRODUCT_CODE --险种代码 KEY:FULL_NAME --险种名称
	 *         KEY:DETAIL_SEQUENCE_NO --通知书序号 KEY:BASE_SUM_INS --保额
	 *         KEY:EFFECT_DATE --生效日期 KEY:NOTE_TYPE --通知书类型代码 KEY:DESCRIPTION
	 *         --通知书类型名称 KEY:IM_FILE_ID --附件IM服务器ID KEY:NOTE_CREATED_DATE --生成时间
	 *         KEY:PDF_UPLOAD_DATE --最后发送时间
	 * @author GaoJiaMing
	 * @time 2014-7-11
	 */
	public List<Map<String, Object>> queryPosNoteHistory(
			Map<String, Object> paraMap) {
		if (paraMap.get("clientNo") == null) {
			return null;
		} else {
			return printDAO.queryPosNoteHistory(paraMap);
		}
	}

	/**
	 * 获取电子函件ID(电子函件下载接口)
	 * 
	 * @param policyNo
	 * @param detailSequenceNo
	 * @return String 失败：null 成功：fileId
	 * @author GaoJiaMing
	 * @time 2014-7-8
	 */
	public String getLoadFileId(String policyNo, String detailSequenceNo) {
		Map<String, Object> map = printDAO.getLoadFileId(policyNo, detailSequenceNo);
		if (map != null) {
			if (!StringUtils.isBlank((String) map.get("IM_FILE_ID"))) {
				return (String) map.get("IM_FILE_ID");
			} else {
				// IM_FILE_ID为空，生成通知书函件并上传到服务器
				return printService.generatePdfFileForNoticeAndUpload(policyNo, detailSequenceNo,
						(String) map.get("NOTE_TYPE"));
			}
		} else {
			return null;
		}
	}

	/**
	 * 电子函件重新发送
	 * 
	 * @param clientNo
	 * @param policyNo
	 * @param detailSequenceNo
	 * @return String 成功：Y 失败：错误信息
	 * @author GaoJiaMing
	 * @time 2014-7-8
	 */
	public String resendELetter(String clientNo, String policyNo,
			String detailSequenceNo) {
		try {
			List<Map<String, Object>> sffileList = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = printDAO.getLoadFileId(policyNo, detailSequenceNo);
			String loadFileId = null;
			if (map != null) {
				if (!StringUtils.isBlank((String) map.get("IM_FILE_ID"))) {
					loadFileId = (String) map.get("IM_FILE_ID");
				} else {
					// IM_FILE_ID为空，生成通知书函件并上传到服务器
					loadFileId = printService.generatePdfFileForNoticeAndUpload(policyNo, detailSequenceNo,
							(String) map.get("NOTE_TYPE"));
				}
			} else {
				return "未找到下载ID";
			}
			if (StringUtils.isBlank(loadFileId)) {
				return "未找到下载ID";
			}
			SFFile pdfFile = PlatformContext.getIMFileService().getFileFormId(
					loadFileId);
			if (pdfFile == null) {
				return "未找到下载文件";
			}
			// 优先取电子信函订阅邮箱
			String email = printService.getEmailAddressForELetter(policyNo);
			if (StringUtils.isBlank(email)) {
				// 取不到取保单层邮箱
				PolicyContactInfoDTO policyContactInfoDTO = serviceItemsDAO_20.queryPolicyContactInfo(policyNo);
				email = policyContactInfoDTO.getEmailDesc();
			}
			if (StringUtils.isBlank(email)) {
				return "未找到该客户邮箱";
			}			
			if ("ent".equals((String) map.get("NOTE_TYPE"))) {
				Map rstMap = rollbackDAO.queryOriginalPosInfo(detailSequenceNo);
				Map<String, Object> fileMap = new HashMap<String, Object>();
				fileMap.put("fileName", detailSequenceNo + "保险批单");
				fileMap.put("file", pdfFile);
				sffileList.add(fileMap);
				// 获取现价表
				List<PosNoteDTO> posNoteList = printService
						.queryPosNoteMainByPosNo(detailSequenceNo);
				NoticePrintHandler handler = NoticePrintHandlerFactory
						.getNoticePrintHandler("ent");
				SFFile tmpFile = new SFFile();
				if (posNoteList != null && !posNoteList.isEmpty()) {
					for (PosNoteDTO posNote : posNoteList) {
						handler = NoticePrintHandlerFactory
								.getNoticePrintHandler(posNote.getNoteType());
						handler.handleSingleNoticePrint(
								posNote.getDetailSequenceNo())
								.renameTo(tmpFile);
						Map<String, Object> cashfileMap = new HashMap<String, Object>();
						Map<String, String> entUrlMap1 = new HashMap<String, String>();
						// 添加电子批单为邮件附件
						cashfileMap.put("fileName", detailSequenceNo + "现价表"
								+ ".pdf");
						cashfileMap.put("file", tmpFile);
						sffileList.add(cashfileMap);
						tmpFile.getRpcAttach().setDeleteAfterReturn(true);
					}

				}
				String contentStr = "";
				String mailHead = "";
				if (posNoteList != null && posNoteList.size() > 0) {
					contentStr = "的保险批单及现价表电子版发送给您，请注意查收。";
					mailHead = "富德生命人寿电子批单及现价表";
				} else {
					contentStr = "的保险批单电子版发送给您，请注意查收。";
					mailHead = "富德生命人寿电子批单";
				}
				String content = "尊敬的"
						+ commonQueryDAO.getAppNameByPolicyNo((String) map
								.get("POLICY_NO")) + "先生/女士:"
						+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;您对保单"
						+ (String) rstMap.get("POLICY_NO") + "申请的"
						+ (String) rstMap.get("SERVICE_ITEMS_DESC")
						+ "操作成功！现将保全号为" + detailSequenceNo + contentStr;
				printService.sendEMail(email, sffileList, mailHead, content,
						detailSequenceNo);
				scheduleDAO.updatePosInfoEfileID(detailSequenceNo, loadFileId);
			} else {
				Map<String, Object> fileMap = new HashMap<String, Object>();
				Map<String, Object> dataMap = printDAO
						.getNoteTypeDescriptionAndPolicyNo(detailSequenceNo);
				String appName = commonQueryDAO
						.getAppNameByPolicyNo((String) dataMap.get("POLICY_NO"));
				fileMap.put("fileName", (String) dataMap.get("POLICY_NO")
						+ (String) dataMap.get("DESCRIPTION") + ".pdf");
				fileMap.put("file", pdfFile);
				sffileList.add(fileMap);
				String content = "尊敬的" + appName + "先生/女士:"
						+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;您在我司购买的保单"
						+ (String) dataMap.get("POLICY_NO") + "的"
						+ (String) dataMap.get("DESCRIPTION")
						+ "现发送给您，详见附件，请注意查收。";
				printService.sendEMail(email, sffileList, "富德生命人寿"
						+ (String) dataMap.get("DESCRIPTION"), content,
						detailSequenceNo);
				printService.updatePosNotePdfNameAndImFileId(detailSequenceNo,
						loadFileId, (String) dataMap.get("POLICY_NO")
								+ (String) dataMap.get("DESCRIPTION") + ".pdf");
			}
		} catch (Exception e) {
			return e.getMessage();
		}
		return "Y";
	}

	/**
	 * 官网发送邮件后会写发送历史
	 * 
	 * @param posNo
	 * @param eEntFileID
	 *            --批单上传文件ID
	 * @return String 成功：Y 失败：错误信息
	 * @author GaoJiaMing
	 * @time 2014-7-16
	 */
	public String updateEEntFileID(String posNo, String eEntFileID) {
		try {
			scheduleDAO.updatePosInfoEfileID(posNo, eEntFileID);
		} catch (Exception e) {
			return e.getMessage();
		}
		return "Y";
	}
	
	/**
	 * 函件发送记录查询接口
	 * 
	 * @param detailSequenceNo
	 * @return List<Map<String, Object>> KEY:EMAIL --邮箱地址 KEY:SEND_DATE --发送时间
	 *         KEY:BUSINESS_NO --业务号
	 * @author GaoJiaMing
	 * @time 2014-11-19
	 */
	public List<Map<String, Object>> getMailLogByDetailSequenceNo(String detailSequenceNo) {
		return printDAO.getMailLogByDetailSequenceNo(detailSequenceNo);
	}

	public PosApplyBatchDTO insertBatchAcceptTest(
			final PosApplyBatchDTO applyBatchInfo) {

		return txTmpl.execute(new TransactionCallback<PosApplyBatchDTO>() {
			@Override
			public PosApplyBatchDTO doInTransaction(
					TransactionStatus transactionStatus) {
				try {

					PosApplyBatchDTO posApplyBatchDto = createBatchHeader(applyBatchInfo);
					return branchAcceptService.batchAccept(posApplyBatchDto);

				} finally {
					transactionStatus.setRollbackOnly();
				}

			}
		});
	}

	public Map<String, Object> acceptInternalTest(
			final PosApplyBatchDTO applyBatchInfo,
			final ServiceItemsInputDTO itemsInputDTO) {

		return txTmpl.execute(new TransactionCallback<Map<String, Object>>() {
			@Override
			public Map<String, Object> doInTransaction(
					TransactionStatus transactionStatus) {
				try {
					
					applyBatchInfo.getApplyFiles().get(0).setApplyDate(commonQueryDAO.getSystemDate());
					PosApplyBatchDTO posApplyBatchDto = createBatchHeader(applyBatchInfo);
					posApplyBatchDto = branchAcceptService
							.batchAccept(posApplyBatchDto);

					itemsInputDTO.setBatchNo(posApplyBatchDto.getPosBatchNo());
					itemsInputDTO.setPosNo(posApplyBatchDto.getPosInfoList()
							.get(0).getPosNo());

					return acceptInternal(itemsInputDTO, applyBatchInfo);
				} finally {
					transactionStatus.setRollbackOnly();
				}

			}
		});
	}
	/**
	 * @Description: 成长红包在微信定投加保后通知微信端结果
	 * @methodName: queryAutoPolicyAddPremByWX
	 * @param policyNo  保单号
	 * @param orderID   订单号
	 * @return Map<String,Object>
	 * @param applyBarCode 投保单号
	 * @param posNo    保全号
	 * @param policy_no 保单号
	 * @param effectDate 保全生效日
	 * @param posStatus 保全状态
	 * @param preSum    加保保费
	 * @param calSum    加保保额
	 * @date 2015-9-23
	 * @throws
	 */
	public Map<String, Object> queryAutoPolicyAddPremByWX(String policyNo, String  orderID){
		return commonQueryDAO.queryAutoPolicyAddPremByWX(policyNo, orderID);
	}
	/**
	 * 根据不同的保全项目，不同受理渠道查询可以操作的保单
	 * 
	 * @param policyNos
	 * @param serviceItems
	 * @param p_accept_channel_code
	 * @return
	 */
	public List<String> getCanDoPolicysByChannel(List<String> policyNos,
			String serviceItems,String p_accept_channel_code ) {
		// 没有加挂保单直接返回
		if (policyNos == null || policyNos.size() == 0)
			return new ArrayList<String>();
		List<String> pNos = new ArrayList<String>();
		Map<String, Object> criteriaMap = new HashMap<String, Object>();
		criteriaMap.put("policyNosList", policyNos);
		for (String policyNo : policyNos){
			if ("Y".equals(commonQueryDAO.canDoServiceItems(policyNo, serviceItems,
					p_accept_channel_code))) {
				pNos.add(policyNo);
			}
		}
		return pNos;
	}
	
}
