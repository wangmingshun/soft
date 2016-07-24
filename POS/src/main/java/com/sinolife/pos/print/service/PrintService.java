package com.sinolife.pos.print.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.MimeUtility;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinolife.intf.print.client.IndigoPrintService;
import com.sinolife.pos.acceptance.branch.service.BranchAcceptService;
import com.sinolife.pos.acceptance.rollback.dao.BranchRollbackDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.print.dao.PrintDAO;
import com.sinolife.pos.print.dto.EndorsementPrintCriteriaDTO;
import com.sinolife.pos.print.dto.NoticePrintCriteriaDTO;
import com.sinolife.pos.print.dto.PosNoteDTO;
import com.sinolife.pos.print.notice.NoticePrintHandler;
import com.sinolife.pos.print.notice.NoticePrintHandlerFactory;
import com.sinolife.pos.schedule.thread.NoticePrintThread;
import com.sinolife.sf.framework.email.Mail;
import com.sinolife.sf.framework.email.MailService;
import com.sinolife.sf.framework.email.MailType;
import com.sinolife.sf.platform.runtime.PlatformContext;
import com.sinolife.sf.store.MimeType;
import com.sinolife.sf.store.SFFile;
import com.sinolife.sf.store.SFFilePath;
import com.sinolife.sf.store.StogeType;
import com.sinolife.sf.store.TempFileFactory;

@Service("printService")
public class PrintService {

	protected Logger logger = Logger.getLogger(getClass());

	@Autowired
	private PrintDAO printDAO;

	@Autowired
	private IndigoPrintService indigoPrintService;

	@Autowired
	private BranchAcceptService branchAcceptService;
	
    @Autowired @Qualifier("mailService02")
    private MailService mailService;
    
    @Autowired
    private CommonQueryDAO commonQueryDAO;
    
    @Autowired
    private BranchRollbackDAO rollbackDAO;
    
    private TempFileFactory TEM_FILE_FACTORY = TempFileFactory
    .getInstance(NoticePrintThread.class);
	/**
	 * 根据批单号查询批单打印相关信息
	 * 
	 * @param posNo
	 * @return
	 */
	public Map<String, Object> queryEndorsementInfoByPosNo(String posNo) {
		return printDAO.queryEndorsementInfoByPosNo(posNo);
	}

	/**
	 * 根据条件查询批单批次打印相关信息
	 * 
	 * @param criteria
	 * @return
	 */
	public List<Map<String, Object>> queryEndorsementInfoByCriteria(
			EndorsementPrintCriteriaDTO criteria) {
		return printDAO.queryEndorsementInfoByCriteria(criteria);
	}

	/**
	 * 根据条件查询批单批次打印符合条件的数据集大小
	 * 
	 * @param criteria
	 * @return
	 */
	public Integer countEndorsementByCriteria(
			EndorsementPrintCriteriaDTO criteria) {
		return printDAO.countEndorsementByCriteria(criteria);
	}

	/**
	 * 根据条件查询通知书信息
	 * 
	 * @param criteria
	 * @return
	 */
	public List<String> queryPosNoteByCriteria(NoticePrintCriteriaDTO criteria) {
		return printDAO.queryPosNoteByCriteria(criteria);
	}

	/**
	 * 根据条件查询满足条件的通知书数据集大小
	 * 
	 * @param criteria
	 * @return
	 */
	public Integer countPosNoteByCriteria(NoticePrintCriteriaDTO criteria) {
		return printDAO.countPosNoteByCriteria(criteria);
	}

	/**
	 * 根据detailSequenceNo查询通知书
	 * 
	 * @param detailSequenceNo
	 * @return
	 */
	public PosNoteDTO queryPosNoteByDetailSequenceNo(String detailSequenceNo) {
		PosNoteDTO posNote = printDAO.queryPosNoteMainByID(detailSequenceNo);
		if (posNote != null) {
			queryPosNoteDetail(posNote);
		}
		return posNote;
	}

	/**
	 * 查询批单的保证价值表信息
	 * 
	 * @param posNo
	 * @return
	 */
	public List<PosNoteDTO> queryPosNoteMainByPosNo(String posNo) {
		return printDAO.queryPosNoteMainByPosNo(posNo);
	}

	/**
	 * 查询通知书明细信息
	 * 
	 * @param posNote
	 */
	@SuppressWarnings("unchecked")
	public void queryPosNoteDetail(PosNoteDTO posNote) {
		// 图片的路径
		String fileAbsPath = null;
		try {
			File imageSrcPath = new ClassPathResource("/jasper_tmpl/image/")
					.getFile();
			if (imageSrcPath.exists() && imageSrcPath.isDirectory()) {
				String absPath = imageSrcPath.getAbsolutePath();
				if (!absPath.endsWith("/") && !absPath.endsWith("\\"))
					absPath += File.separator;
				fileAbsPath = absPath;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 补充通知书明细信息，将明细信息放入detailMap中，对于子表（table_name与row_no不为空）数据
		// 会在detailMap中以表名(table_name)为Key放入一个List<Map<String, Object>>数据
		// List中每个Map对应一行数据
		String detailSequenceNo = posNote.getDetailSequenceNo();

		// 准备detailMap
		Map<String, Object> noteDetailMap = posNote.getDetailMap();
		if (noteDetailMap == null) {
			noteDetailMap = new HashMap<String, Object>();
			posNote.setDetailMap(noteDetailMap);
		}

		// 处理公共信息
		noteDetailMap.put("BUSINESS_DATE", posNote.getBusinessDate());
		noteDetailMap.put("CLIENT_ADDRESS", posNote.getClientAddress());
		noteDetailMap.put("CLIENT_NAME", posNote.getClientName());
		noteDetailMap.put("CLIENT_NO", posNote.getClientNo());
		noteDetailMap.put("CLIENT_POSTALCODE", posNote.getClientPostalcode());
		noteDetailMap.put("COMPANY_NAME", posNote.getCompanyName());
		noteDetailMap.put("COMPANY_SERVICE_ADDRESS",
				posNote.getCompanyServiceAddress());
		noteDetailMap.put("COMPANY_SERVICE_NAME",
				posNote.getCompanyServiceName());
		noteDetailMap.put("COMPANY_SERVICE_POSTALCODE",
				posNote.getCompanyServicePostalcode());
		noteDetailMap.put("COMPANY_SERVICE_TELEPHONE",
				posNote.getCompanyServiceTelephone());
		noteDetailMap.put("POLICY_NO", posNote.getPolicyNo());
		noteDetailMap
				.put("SERVICE_TELEPHONE_A", posNote.getServiceTelephoneA());
		noteDetailMap
				.put("SERVICE_TELEPHONE_B", posNote.getServiceTelephoneB());
		noteDetailMap.put("RETURN_REASON", posNote.getReturnReason());

		// 计算打印条形码
		String printBarcodeNo = posNote.getDetailSequenceNo();
		// printBarcodeNo += BarCodeUtil.getBarCodeSum(printBarcodeNo);
		noteDetailMap.put("PRINT_BARCODE_NO", printBarcodeNo);
		noteDetailMap.put("IMAGE_SRC_PATH", fileAbsPath);

		// 处理单个detail记录
		List<Map<String, Object>> detailMapList = printDAO
				.queryPosNoteDetailByID(detailSequenceNo);
		if (detailMapList != null && !detailMapList.isEmpty()) {
			// 子表集合
			Set<String> tableNameSet = new HashSet<String>();

			for (Map<String, Object> detailMap : detailMapList) {
				String tableName = (String) detailMap.get("TABLE_NAME");
				BigDecimal rowNo = (BigDecimal) detailMap.get("ROW_NO");
				String columnName = (String) detailMap.get("COLUMN_NAME");
				String columnValue = (String) detailMap.get("COLUMN_VALUE");

				if (StringUtils.isNotBlank(tableName)) {
					tableNameSet.add(tableName);

					// 准备表数据
					List<Map<String, Object>> tableData = (List<Map<String, Object>>) noteDetailMap
							.get(tableName);
					if (tableData == null) {
						tableData = new ArrayList<Map<String, Object>>();
						noteDetailMap.put(tableName, tableData);
					}

					// 准备表的数据行数据
					int iRowNo = 0;
					if (rowNo != null) {
						iRowNo = rowNo.intValue();
					}
					Map<String, Object> rowMap;
					if (iRowNo < tableData.size()) {
						rowMap = tableData.get(iRowNo);
						if (rowMap == null) {
							rowMap = new HashMap<String, Object>();
							tableData.set(iRowNo, rowMap);
						}
					} else {
						rowMap = new HashMap<String, Object>();
						PosUtils.appendNullToSize(tableData, iRowNo + 1);
						tableData.add(iRowNo, rowMap);
					}

					// 以COLUMN_NAME为Key将数据置入
					rowMap.put(columnName, columnValue);
				} else {
					// tableName属性为空的，直接置入
					noteDetailMap.put(columnName, columnValue);
				}
			}

			// 移除Table中的空行
			for (String tableName : tableNameSet) {
				List<Map<String, Object>> tableData = (List<Map<String, Object>>) noteDetailMap
						.get(tableName);
				if (tableData != null && !tableData.isEmpty()) {
					for (Iterator<Map<String, Object>> it = tableData
							.iterator(); it.hasNext();) {
						Map<String, Object> rowMap = it.next();
						if (rowMap == null || rowMap.isEmpty())
							it.remove();
					}
				}
			}
		}
	}

	/**
	 * 更新通知书打印日期
	 * 
	 * @param detailSequenceNo
	 */
	public void updatePosNoteMainPrintDate(String detailSequenceNo) {
		printDAO.updatePosNoteMainPrintDate(detailSequenceNo);
	}

	/**
	 * 更新通知书邮储打印日期，是否本地标志，及邮储的文件名
	 * 
	 * @param detailSequenceNo
	 * @param isLocal
	 * @param fileName
	 */
	public void updatePosNotePdfNameAndPDFUploadDate(String detailSequenceNo,
			String isLocal, String fileName) {
		printDAO.updatePosNotePdfNameAndPDFUploadDate(detailSequenceNo,
				isLocal, fileName);
	}

	/**
	 * 更新通知书打印日期、及邮储的文件名
	 * 
	 * @param detailSequenceNo
	 * @param pdfName
	 */
	public void updatePosNoteMainPrintDateAndPdfName(String detailSequenceNo,
			String pdfName) {
		printDAO.updatePosNoteMainPrintDateAndPdfName(detailSequenceNo, pdfName);
	}

	/**
	 * 保单打印.
	 * 
	 * @param policyNo
	 * @param userId
	 * @param requestIp
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public String printPolicyByPolicyNo(String policyNo, String userId,
			String requestIp) {
		// 查询并锁定保全记录
		String posNo = printDAO.verifyAndLockPosInfoForPolicyPrint(policyNo);
		if (StringUtils.isBlank(posNo))
			return "保单[" + policyNo + "]未申请保单补发服务";

		// 查询打印任务号
		String taskNo = printDAO.queryPrintTaskNoByPolicyNo(policyNo);
		if (StringUtils.isBlank(taskNo))
			return "找不到保单[" + policyNo + "]的待打印任务";

		String branchCode = printDAO.queryPolicyPrintBranchCodeLV2(posNo);
		if (StringUtils.isBlank(branchCode)) {
			return "找不到保全受理[" + posNo + "]的受理柜面所在机构";
		}

		if (StringUtils.isBlank(userId)) {
			return "找不到登录用户信息";
		}

		// 更新受理状态
		Map<String, Object> retMap = branchAcceptService.workflowControl("12",
				posNo, false);
		String flag = (String) retMap.get("flag");
		if (!"E01".equals(flag))
			throw new RuntimeException("更新保单补发受理状态失败" + flag);

		// 调用打印系统进行保单打印，出现异常则抛出RuntimeException回滚事务
		try {
			List<String> taskList = new ArrayList<String>();// 打印任务流水号列表
			taskList.add(taskNo);
			indigoPrintService.doPolicyPringTask(taskList, userId, requestIp,
					branchCode);
		} catch (Exception e) {
			throw new RuntimeException("打印系统接口异常", e);
		}

		return null;
	}

	/**
	 * 更新通知书寄送地址
	 * 
	 * @param detailSequenceNo
	 * @return
	 */
	public Map<String, Object> updateNoteAddress(String detailSequenceNo) {
		return printDAO.updateNoteAddress(detailSequenceNo);
	}
	
    /**
     * 根据批单号查询免填单相关信息
     * 
     * @param posNo
     * @return Map<String,Object>
     * @author GaoJiaMing
     * @time 2014-6-9
     */
    public Map<String, Object> queryFillFormApplicationInfoByPosNo(String posNo) {
        Map<String, Object> resultMap = printDAO.queryFillFormApplicationInfoByPosNo(posNo);
        if (resultMap != null && !resultMap.isEmpty()) {
            // 根据不同的保全项目获取不同的批注
            String serviceItems = (String) resultMap.get("SERVICE_ITEMS");
            StringBuffer messageTemp = new StringBuffer();
            if ("8".equals(serviceItems)) {
                messageTemp.append("特别提示：\n");
                messageTemp.append("1、如当地监管部门要求不得采用现金领取方式的,本公司仅支持银行转账方式支付;\n");
                messageTemp.append("2、以死亡为给付保险金条件的合同申请保单贷款，除投保人签名外，还需被保险人（或其监护人）书面签名同意；\n");
                messageTemp.append("3、如您决定还款，请到我司柜面办理还款，或联系我司工作人员咨询办理。如有疑问，请致电95535/400－820－0035咨询。\n");
                messageTemp.append("本人已全部阅读并充分理解以下条款内容，同意遵守以下有关贷款的约定：\n");
                messageTemp.append("1、贷款申请必须符合贵公司保险合同条款的约定。\n");
                messageTemp.append("2、贷款利息根据中国人民银行颁布的同期五年期人民币贷款基准利率计算。\n");
                messageTemp.append("3、贷款期限按照保险合同条款约定执行，若保险合同条款中约定贷款期限不超过六个月的，逾期期间的利率按上述基准利率再加一个百分点执行。\n");
                messageTemp
                        .append("4、在保单贷款期间，当发生保险事故、保险期满或存在需退还合同现金价值、保险费的情况时，同意贵公司在给付的保险金中扣除任何欠交的保险费及利息、保单贷款本金及利息。\n");
                messageTemp.append("5、当保险合同的现金价值不足以抵偿欠交的保险费及利息、保单贷款本金及利息时，保险合同的效力中止。\n");
                messageTemp.append("6、保险合同效力中止超过二年，贵公司有权按解除合同处理，解除合同后应退还的款项优先用于偿还保单贷款本金及利息。\n");
            } else if ("12".equals(serviceItems) || "15".equals(serviceItems)) {
                messageTemp.append("注：1、若投保人在公司有多份保单，年龄信息将同步变更，若涉及收付费将一并进行收取及退还。\n");
                messageTemp.append("  2、若被保险人在公司有多份保单，涉及收付费将一并进行收取及退还。\n");
            } else if ("20".equals(serviceItems)) {
                messageTemp.append("注：变更投保人请同时变更续期交费方式和联系方式，并填写《更换人员告知书》。\n");
            } else if ("16".equals(serviceItems)) {
                messageTemp.append("注：变更健康情况时，请同时填写《最新个人资料告知书》。\n");
            } else if ("17".equals(serviceItems)) {
                messageTemp.append("注：若由短交费期变更为长交费期，请同时填写《最新个人资料告知书》。\n");
            } else if ("5".equals(serviceItems)) {
                messageTemp
                        .append("注：新增附加险时，请同时填写《最新个人资料告知书》；若您新增的附加险为医疗险时，请同时填写《医疗保险特别约定声明书》；若您新增的附加险为一年期短险，请选择是否自动续保。\n");
            } else if ("42".equals(serviceItems)) {
                messageTemp.append("注： （调整后保险期间顺延）\n");
            } else if ("27".equals(serviceItems)) {
                messageTemp.append("注：投保人声明：自保险单补发之日起，原保险单作废。\n");
            } else if ("9".equals(serviceItems)) {
                messageTemp.append("注：还款利息的计算截止日以本公司收到您的书面申请为准，每次部分还款金额不得低于500元。\n");
            } else if ("4".equals(serviceItems)) {
                messageTemp.append("注：保险金额中基本保险金额和累积红利保险金额同比例减少，主险和附加险保险金额有搭配比例要求的，附加险应同时办理减保。\n");
            } else if ("10".equals(serviceItems)) {
                messageTemp.append("注：1、申请满期转账授权，保单保障期满并向客户支付满期金后，保单自动作废）\n");
                messageTemp.append("  2、转入的保单/投保单中的投保人或被保险人须与本保单生存金受益人为同一人，且生存金受益人（或其法定监护人）须在保险金受益人处签名确认。\n");
            }
            resultMap.put("REMINDING_TEXT", messageTemp.toString());
            String barcodeNo = (String) resultMap.get("BARCODE_NO");
            BigDecimal premSum = (BigDecimal) resultMap.get("PREM_SUM");
            // 核保类加提示,受益人变更不加提示
            if (barcodeNo.startsWith("1212") && premSum!=null&&premSum.longValue() > 0) {
                resultMap.put("TIPS", "以上补退费金额仅供参考，具体补退费金额以核保意见为准！");
            }
             // 取DB返回批文
//            Map<String, Object> applicationTextResultMap = printDAO.getApplicationText(posNo);
//            if (applicationTextResultMap != null && !applicationTextResultMap.isEmpty()
//                    && "0".equals(applicationTextResultMap.get("p_flag"))) {
//                resultMap.put("APPROVE_TEXT", applicationTextResultMap.get("p_content_text"));
//            } else {
//                resultMap.put("MESSAGE", applicationTextResultMap.get("p_message"));
//            }
            return resultMap;

        } else {
            return null;
        }
    }
    
    /**
     * 根据保全号查关联保全号
     * 
     * @param posNo
     * @return List<PosInfoDTO>
     * @author GaoJiaMing
     * @time 2014-6-16
     */
    public List<PosInfoDTO> queryPosNosByPosNo(String posNo) {
        return printDAO.queryPosNosByPosNo(posNo);
    }

    /**
     * 根据保全号查询申请书信息（没有跳转页面，用于校验）
     * 
     * @param posNo
     * @return String
     * @author GaoJiaMing
     * @time 2014-6-16
     */
    public String checkFillFormApplicationInfoByPosNo(String posNo) {
        List<PosInfoDTO> PosInfoDTOList = this.queryPosNosByPosNo(posNo);
        String resultFlag = "";
        if (PosInfoDTOList.size() > 0) {
            for (PosInfoDTO posInfoDTO : PosInfoDTOList) {
                Map<String, Object> posInfo = this.queryFillFormApplicationInfoByPosNo(posInfoDTO.getPosNo());
                if (posInfo == null || posInfo.isEmpty()) {
                    resultFlag = "N";
                    return resultFlag;
                }
            }
            resultFlag = "Y";
        } else {
            resultFlag = "N";
        }
        return resultFlag;
    }
    

    /**
     * 处理电子信函
     * 
     * @param dataMap
     * @return void
     * @author GaoJiaMing
     * @time 2014-6-26
     */

    public void manageELetter(Map<String, Object> dataMap) {
        if ("autoPrint".equals(dataMap.get("taskFlag"))) {
            List<PosNoteDTO> posNoteList = (List<PosNoteDTO>) dataMap.get("posNoteList");
            if (posNoteList != null && posNoteList.size() > 0) {
                manageAutoPrintELetter(posNoteList);
            } else {
                return;
            }

        } 
        //此逻辑已经合并到autoPrint里
//        else if ("queryPosNote".equals(dataMap.get("taskFlag"))) {
//            List<String> noticeIdList = (List<String>) dataMap.get("noticeIdList");
//            String noticeType = (String) dataMap.get("noticeType");
//            if (noticeIdList != null && noticeIdList.size() > 0) {
//                manageQueryPosNoteELetter(noticeIdList, noticeType);
//            } else {
//                return;
//            }
//        }
    }

    /**
     * 处理自动打印任务电子信函
     * 
     * @param posNoteList
     * @return void
     * @author GaoJiaMing
     * @time 2014-6-27
     */
    public void manageAutoPrintELetter(List<PosNoteDTO> posNoteList) {
        List<Map<String, Object>> sffileList = new ArrayList<Map<String, Object>>();
        Map<String, Object> fileMap = new HashMap<String, Object>();
        SFFile pdfFile = null;
        String imFileId = "";
        String content = "";
        String appName = "";
        String email = "";
        Map<String, Object> dataMap = null;
        for (PosNoteDTO posNoteDTO : posNoteList) {
            sffileList.clear();
            try {
                email = getEmailAddressForELetter(posNoteDTO.getPolicyNo());
                if (StringUtils.isBlank(email)) {
                    continue;
                }
                // 1.先在临时文件夹中生成PDF文件
                pdfFile = generatePdfFileForNotice(posNoteDTO);

                // 2.将临时文件上传至IM服务器

                imFileId = uploadFileToIM(pdfFile);
                
                if (StringUtils.isBlank(imFileId)) {
                    continue;
                }

                // 3.发送邮件
                dataMap = printDAO.getNoteTypeDescriptionAndPolicyNo(posNoteDTO.getDetailSequenceNo());
                appName = commonQueryDAO.getAppNameByPolicyNo((String) dataMap.get("POLICY_NO"));
                fileMap.put("fileName", (String) dataMap.get("POLICY_NO") + (String) dataMap.get("DESCRIPTION")+".pdf");
                fileMap.put("file", pdfFile);
                sffileList.add(fileMap);
                content = "尊敬的" + appName + "先生/女士:" + "<br/>&nbsp;&nbsp;&nbsp;&nbsp;您在我司购买的保单"
                        + (String) dataMap.get("POLICY_NO") + "的" + (String) dataMap.get("DESCRIPTION")
                        + "现发送给您，详见附件，请注意查收。";
				this.sendEMail(email, sffileList, "富德生命人寿" + (String) dataMap.get("DESCRIPTION"), content,
						posNoteDTO.getDetailSequenceNo());
                // 4.更新通知书上传路径、文件名和时间
                this.updatePosNotePdfNameAndImFileId(posNoteDTO.getDetailSequenceNo(), imFileId,
                        (String) dataMap.get("POLICY_NO") + (String) dataMap.get("DESCRIPTION")+".pdf");

            } catch (RuntimeException re) {
                throw re;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                PosUtils.deleteFile(pdfFile);
            }
        }
    }

    /**
     * 处理查询通知信打印任务电子信函
     * 
     * @param noticeIdList
     * @param noticeType
     * @return void
     * @author GaoJiaMing
     * @time 2014-6-27
     */
//    public void manageQueryPosNoteELetter(List<String> noticeIdList, String noticeType) {
//        List<Map<String, Object>> sffileList = new ArrayList<Map<String, Object>>();
//        Map<String, Object> fileMap = new HashMap<String, Object>();
//        SFFile pdfFile = null;
//        String imFileId = "";
//        String content = "";
//        String appName = "";
//        String email = "";
//        Map<String, Object> dataMap = null;
//        PosNoteDTO posNoteDTO = new PosNoteDTO();
//        posNoteDTO.setNoteType(noticeType);
//        for (String detailSequenceNo : noticeIdList) {
//            posNoteDTO.setDetailSequenceNo(detailSequenceNo);
//            sffileList.clear();
//            try {
//                email = getEmailAddressForELetter(getclientNoByDetailSequenceNoAndNoticeType(detailSequenceNo,
//                        noticeType));
//                if (StringUtils.isBlank(email)) {
//                    continue;
//                }
//                // 1.先在临时文件夹中生成PDF文件
//                pdfFile = generatePdfFileForNotice(posNoteDTO);
//
//                // 2.将临时文件上传至IM服务器
//
//                imFileId = uploadFileToIM(pdfFile);
//                
//                if (StringUtils.isBlank(imFileId)) {
//                    continue;
//                }
//
//                // 3.发送邮件
//                dataMap = printDAO.getNoteTypeDescriptionAndPolicyNo(detailSequenceNo);
//                appName = commonQueryDAO.getAppNameByPolicyNo((String) dataMap.get("POLICY_NO"));
//                fileMap.put("fileName", (String) dataMap.get("POLICY_NO") + (String) dataMap.get("DESCRIPTION"));
//                fileMap.put("file", pdfFile);
//                sffileList.add(fileMap);
//                content = "尊敬的" + appName + "先生/女士:" + "<br/>&nbsp;&nbsp;&nbsp;&nbsp;您在我司购买的保单"
//                        + (String) dataMap.get("POLICY_NO") + "的" + (String) dataMap.get("DESCRIPTION")
//                        + "现发送给您，详见附件，请注意查收。";
//                this.sendEMail(email, sffileList, "生命人寿" + (String) dataMap.get("DESCRIPTION"), content);
//
//                // 4.更新通知书上传路径、文件名和时间
//                this.updatePosNotePdfNameAndImFileId(posNoteDTO.getDetailSequenceNo(), imFileId,
//                        (String) dataMap.get("POLICY_NO") + (String) dataMap.get("DESCRIPTION")+".pdf");
//
//            } catch (RuntimeException re) {
//                throw re;
//            } catch (Exception e) {
//                throw new RuntimeException(e.getMessage(), e);
//            } finally {
//                PosUtils.deleteFile(pdfFile);
//            }
//        }
//    }

    /**
     * 通知书PDF生成
     * 
     * @param posNoteDTO
     * @return SFFile
     * @author GaoJiaMing
     * @time 2014-6-27
     */
    public SFFile generatePdfFileForNotice(PosNoteDTO posNoteDTO) {
        NoticePrintHandler handler = NoticePrintHandlerFactory.getNoticePrintHandler(posNoteDTO.getNoteType());
        SFFile tmpFile = new SFFile();
        try {
            handler.handleSingleNoticePrint(posNoteDTO.getDetailSequenceNo()).renameTo(tmpFile);
            return tmpFile;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            tmpFile.getRpcAttach().setDeleteAfterReturn(true);
        }
    }



    /**
     * 上传PDF到IM服务器
     * 
     * @param tempFile
     * @return String
     * @author GaoJiaMing
     * @time 2014-6-27
     */
    public String uploadFileToIM(SFFile tempFile) {
        try {
            SFFilePath fileDestinationPath = new SFFilePath();
            fileDestinationPath.setModule("pos");
            fileDestinationPath.setModuleSubPath(new String[] { "notice_e_letter" });
            fileDestinationPath.setStogeType(StogeType.WEEK);
            fileDestinationPath.setMimeType(MimeType.application_pdf);
            String fileId = PlatformContext.getIMFileService().putFile(tempFile, fileDestinationPath);
            return fileId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 发送电子批单邮件
     * 
     * @param email
     * @param sffileList
     * @param mailHead
     * @param content
     * @return void
     * @author GaoJiaMing
     * @time 2014-6-27
     */
	public void sendEMail(String email, List<Map<String, Object>> sffileList, String mailHead, String content,
			String businessNo) {
		Mail mail = new Mail();
		mail.setSubject(mailHead);// 邮件标题
		mail.setTo(new String[] { email });// 收件人
		//mail.setTo(new String[] { "gaojiaming.wb@sino-life.com" });// 收件人
		mail.setForm("notice.epos@sino-life.com");// 发件人
		mail.setMailType(MailType.HTML_CONTENT);// 邮件类型
		mail.setContent(content);// 邮件内容
		if (sffileList != null) {
			for (Map<String, Object> map : sffileList) {
				String fileName = (String) map.get("fileName");
				try {
					mail.addAttachment(MimeUtility.encodeText(fileName), (SFFile) map.get("file"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		mailService.send(mail);
		// 写发送日志
		Map<String, Object> mapMailLog = new HashMap<String, Object>();
		mapMailLog.put("email", email);
		if (StringUtils.isBlank(businessNo)) {
			mapMailLog.put("businessNo", "未能获取业务号");
		} else {
			mapMailLog.put("businessNo", businessNo);
		}
		printDAO.insertPosMailLog(mapMailLog);
	}

    /**
     * 电子信函更新im_file_id
     * 
     * @param detailSequenceNo
     * @param imFileId
     * @param fileName
     * @return void
     * @author GaoJiaMing
     * @time 2014-6-27
     */
    public void updatePosNotePdfNameAndImFileId(String detailSequenceNo, String imFileId, String fileName) {
        printDAO.updatePosNotePdfNameAndImFileId(detailSequenceNo, imFileId, fileName);
    }
    
    /**
     * 根据保单号获取邮箱地址
     * @param policyNo
     * @return String
     * @author GaoJiaMing 
     * @time 2014-6-30
     */
    public String getEmailAddressForELetter(String policyNo) {
        return printDAO.getEmailAddressForELetter(policyNo);
    }
    
    /**
     * 获取客户号
     * @param detailSequenceNo
     * @param noticeType
     * @return String
     * @author GaoJiaMing 
     * @time 2014-6-30
     */
    public String getclientNoByDetailSequenceNoAndNoticeType(String detailSequenceNo, String noticeType) {
        return printDAO.getclientNoByDetailSequenceNoAndNoticeType(detailSequenceNo, noticeType);
    }
    
    /**
     * 根据保全号在明细表里获取邮箱
     * @param posNo
     * @return String
     * @author GaoJiaMing 
     * @time 2014-6-30
     */
    public String getEmailAddressByPosNo(String posNo) {
        return printDAO.getEmailAddressByPosNo(posNo);
    }
    
	/**
	 * 通知信生成并上传
	 * 
	 * @param policyNo
	 * @param detailSequenceNo
	 * @param noteType
	 * @return String
	 * @author GaoJiaMing
	 * @time 2015-5-11
	 */
	public String generatePdfFileForNoticeAndUpload(String policyNo, String detailSequenceNo, String noteType) {
		SFFile pdfFile = null;
		String imFileId = null;
		try {
			// 1.先在临时文件夹中生成PDF文件
			PosNoteDTO posNoteDTO = new PosNoteDTO();
			posNoteDTO.setNoteType(noteType);
			posNoteDTO.setDetailSequenceNo(detailSequenceNo);
			pdfFile = this.generatePdfFileForNotice(posNoteDTO);
			// 2.将临时文件上传至IM服务器
			imFileId = this.uploadFileToIM(pdfFile);
			if (StringUtils.isBlank(imFileId)) {
				return null;
			}
			// 3.更新通知书上传路径、文件名和时间
			Map<String, Object> dataMap = printDAO.getNoteTypeDescriptionAndPolicyNo(posNoteDTO.getDetailSequenceNo());
			this.updatePosNotePdfNameAndImFileId(posNoteDTO.getDetailSequenceNo(), imFileId,
					(String) dataMap.get("POLICY_NO") + (String) dataMap.get("DESCRIPTION") + ".pdf");

		} catch (RuntimeException re) {
			throw re;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			PosUtils.deleteFile(pdfFile);
		}
		return imFileId;
	}
}
