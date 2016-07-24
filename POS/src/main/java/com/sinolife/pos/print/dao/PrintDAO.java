package com.sinolife.pos.print.dao;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sinolife.pos.common.dao.AbstractPosDAO;
import com.sinolife.pos.common.dto.CodeTableItemDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.print.dto.EndorsementPrintCriteriaDTO;
import com.sinolife.pos.print.dto.NoticePrintCriteriaDTO;
import com.sinolife.pos.print.dto.PosNoteDTO;
import com.sinolife.pos.rpc.ilogjrules.posrules.dao.PosRulesDAO;

/**
 * 打印DAO
 */
@Repository("printDAO")
public class PrintDAO extends AbstractPosDAO {
	
	
	@Autowired
	private PosRulesDAO posRulesDAO;

	/**
	 * 根据条件查询批单批次打印相关信息
	 * @param criteria
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryEndorsementInfoByCriteria(EndorsementPrintCriteriaDTO criteria) {
		return queryForList("queryEndorsementInfoByCriteria", criteria);
	}
	
	/**
	 * 根据条件查询批单批次打印符合条件的数据集大小
	 * @param criteria
	 * @return
	 */
	public Integer countEndorsementByCriteria(EndorsementPrintCriteriaDTO criteria) {
		return (Integer) queryForObject("countEndorsementByCriteria", criteria);
	}
	
	/**
	 * 根据批单号查询批单明细，供打印使用
	 * @param posNo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryEndorsementInfoByPosNo(String posNo) {
		Map<String, Object> result = (Map<String, Object>) queryForObject("queryEndorsementInfoByPosNo", posNo);
		//由于jasper对于半角的符号长度解释问题，导致空格部分都被缩小，所以这里将2个半角空格替换成一个全角空格
		if(result != null && !result.isEmpty()) {
			
			
			// 是否是淘宝渠道保单
			boolean isTaobaoPolicy = posRulesDAO.isTaobaoPolicy((String) result.get("POLICY_NO"));
			//受理渠道
			String acceptChannelCode=(String) result.get("ACCEPT_CHANNEL_CODE");
			//添加微信渠道
			if (isTaobaoPolicy&&"14".equals(acceptChannelCode)||"7".equals(acceptChannelCode)||"16".equals(acceptChannelCode))//对淘宝渠道的保单在淘宝网上受理时
			{
				result.put("ACCEPTOR_NAME", "");
				
			}
			String approveText = (String) result.get("APPROVE_TEXT");
			if(StringUtils.isNotBlank(approveText)) {
				approveText = approveText.replaceAll("  ", "　");
				result.put("APPROVE_TEXT", approveText);
			}
		}
		return result;
	}
	
	/**
	 * 根据条件查询通知书信息
	 * @param criteria
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> queryPosNoteByCriteria(NoticePrintCriteriaDTO criteria) {
		return queryForList("queryPosNoteByCriteria", transParaMapForNoteQuery(criteria));
	}
	
	/**
	 * 根据条件查询满足条件的通知书数据集大小
	 * @param criteria
	 * @return
	 */
	public Integer countPosNoteByCriteria(NoticePrintCriteriaDTO criteria) {
		return (Integer) queryForObject("countPosNoteByCriteria", transParaMapForNoteQuery(criteria));
	}
	
	/**
	 * 转换查询条件为MAP（为了支持非常规参数）
	 * @param criteria
	 * @return
	 */
	private Map<String, Object> transParaMapForNoteQuery(NoticePrintCriteriaDTO criteria) {
		String noticeType = criteria.getNoticeType();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		if("51".equals(noticeType)) {
			//现金分红红利通知信(银代渠道)
			paraMap.put("noticeType", "1");
			paraMap.put("channelType", "1");
		} else if("52".equals(noticeType)) {
			//现金分红红利通知信(非银代渠道)
			paraMap.put("noticeType", "1");
			paraMap.put("channelType", "2");
		} else if("53".equals(noticeType)) {
			//保额分红红利通知信(银代渠道)
			paraMap.put("noticeType", "2");
			paraMap.put("channelType", "1");
		} else if("54".equals(noticeType)) {
			//保额分红红利通知信(非银代渠道)
			paraMap.put("noticeType", "2");
			paraMap.put("channelType", "2");
		} else {
			paraMap.put("noticeType", noticeType);
		}
		paraMap.put("noticeYear", criteria.getNoticeYear());
		paraMap.put("branchCode", criteria.getBranchCode());
		paraMap.put("businessDateEnd", criteria.getBusinessDateEnd());
		paraMap.put("businessDateStart", criteria.getBusinessDateStart());
		paraMap.put("detailSequenceNo", criteria.getDetailSequenceNo());
		paraMap.put("policyNo", criteria.getPolicyNo());
		paraMap.put("queryDateEnd", criteria.getQueryDateEnd());
		paraMap.put("queryDateStart", criteria.getQueryDateStart());
		paraMap.put("singlePrintFlag", criteria.getSinglePrintFlag());
		paraMap.put("policyChannel", criteria.getPolicyChannel());
		paraMap.put("serviceNo", criteria.getServiceNo());
	    paraMap.put("isELetterFlag", criteria.getIsELetterFlag());
		return paraMap;
	}
	
	/**
	 * 根据ID查询PosNoteMain
	 * @param detailSequenceNo
	 * @return
	 */
	public PosNoteDTO queryPosNoteMainByID(String detailSequenceNo) {
		return (PosNoteDTO) queryForObject("queryPosNoteMainByID", detailSequenceNo);
	}
	
	/**
	 * 查询批单的保证价值表信息
	 * @param posNo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PosNoteDTO> queryPosNoteMainByPosNo(String posNo) {
		return queryForList("queryPosNoteByPosNo", posNo);
	}
	
	/**
	 * 根据ID查询通知书明细信息
	 * @param detailSequenceNo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryPosNoteDetailByID(String detailSequenceNo) {
		return queryForList("queryPosNoteDetailByID", detailSequenceNo);
	}
	
	/**
	 * 更新通知书打印日期
	 * @param detailSequenceNo
	 */
	public void updatePosNoteMainPrintDate(String detailSequenceNo) {
		getSqlMapClientTemplate().update(PrintDAO.class.getName() + ".updatePosNoteMainPrintDate", detailSequenceNo);
	}
	
	
	/**
	 * 更保全主表批单打印日期及提交用户
	 * @param detailSequenceNo
	 */
	public void updatePosInfoEntData(String posNo,String submitUserId) {
		
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("posNo", posNo);
		paraMap.put("submitUserId", submitUserId);
		getSqlMapClientTemplate().update(PrintDAO.class.getName() + ".updatePosInfoEntData", paraMap);
	}

	/**
	 * 验证存在保单待补发的保全项目，将该记录锁定，并返回保全号码
	 * @param policyNo
	 * @return
	 */
	public String verifyAndLockPosInfoForPolicyPrint(String policyNo) {
		try {
			return (String) queryForObject("verifyAndLockPosInfoForPolicyPrint", policyNo);
		} catch(Exception e) {
			throw new RuntimeException("锁定保全记录失败", e);
		}
	}
	
	/**
	 * 查询保单的打印任务.
	 * @param policyNo
	 * @return
	 */
	public String queryPrintTaskNoByPolicyNo(String policyNo) {
		return (String) queryForObject("queryPrintTaskNoByPolicyNo", policyNo);
	}
	
	/**
	 * 查询新增的附加险种
	 * @param posNo
	 * @return
	 */
	public Set<String> getAddProduct(String posNo) {
		String productStr = (String) queryForObject("getAddProduct", posNo);
		Set<String> retSet = new HashSet<String>();
		if(StringUtils.isNotBlank(productStr)) {
			List<String> productList = Arrays.asList(productStr.replaceAll(";", " ; ").split(";"));
			if(productList != null && !productList.isEmpty()) {
				for(String productCode : productList) {
					if(StringUtils.isBlank(productCode)) {
						continue;
					}
					retSet.add(productCode.trim());
				}
			}
		}
		return retSet;
	}
	
	/**
	 * 查询险种条款
	 * @param productCode
	 * @param applyDate
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public byte[] queryPlanProvisionByProductCode(String productCode, Date applyDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("productCode", productCode);
		paraMap.put("applyDate", applyDate);
		Map<String, Object> retMap = (Map<String, Object>) queryForObject("queryPlanProvisionByProductCode", paraMap);
		if(retMap == null || retMap.isEmpty())
			return null;
		return (byte[]) retMap.get("content");
	}

	/**
	 * 更新通知书寄送地址
	 * @param detailSequenceNo
	 * @return
	 */
	public Map<String, Object> updateNoteAddress(String detailSequenceNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_detail_sequence_no", detailSequenceNo);
		queryForObject("updateNoteAddress", paraMap);
		return paraMap;
	}
	
	/**
	 * 投连、万能年报通知书逐单入口
	 * @param policyNo
	 * @param startDate
	 * @param endDate
	 * @param noteType
	 * @return Map&lt;String, Object&gt;<br/>
	 * 参数说明: <br/>
	 * 输入参数：<br/>
	 * p_policy_no                     : 保单号<br/>
	 * p_from_date                     : 报告开始时间<br/>
	 * p_to_date                       : 报告结束时间<br/>
	 * p_note_type                     : 通知书类型('10'：万能 '11'：投连)<br/>
	 * 输出参数：<br/>
	 * p_flag                          : 返回标志 '0'：成功; '1' :失败<br/>
	 * p_message                       : 返回信息<br/>
	 * p_detail_sequence_no            : 通知书序列号<br/>
	 */
	public Map<String, Object> investAnnalsPrintSingle(String policyNo, Date startDate, Date endDate, String noteType) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		paraMap.put("p_from_date", startDate);
		paraMap.put("p_to_date", endDate);
		paraMap.put("p_note_type", noteType);
		queryForObject("investAnnalsPrintSingle", paraMap);
		return paraMap;
	}

	/**
	 * 查询保单打印的二级机构，根据保全受理柜面所在机构查询
	 * @param posNo
	 * @return
	 */
	public String queryPolicyPrintBranchCodeLV2(String posNo) {
		return (String) queryForObject("queryPolicyPrintBranchCodeLV2", posNo);
	}
	
	/**
	 * 查询保单介质信息
	 * @param policyNo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryPolicyContractInfo(String policyNo) {
		return (Map<String, Object>) queryForObject("queryPolicyContractInfo", policyNo);
	}
	
	/**
	 * 合同撤销或解除，发票是否收回
	 * @param posNo
	 * @return
	 */
	public boolean isInvoiceGotBack(String posNo) {
		return "Y".equals(queryForObject("isInvoiceGotBack", posNo));
	}
	
	/**
	 * 查询登录用户是否为深分用户（多出是否银代渠道的选择）
	 * @param userBranchCode
	 * @return
	 */
	public boolean isShenzhenUser(String userBranchCode) {
		return "Y".equals(queryForObject("isShenzhenUser", userBranchCode));
	}

	/**
	 * 查询通知书可选年度
	 * @param policyNo
	 * @param noticeType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CodeTableItemDTO> queryNoteYearOptions(String policyNo,	String noticeType) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("policyNo", policyNo);
		paraMap.put("noticeType", noticeType);
		return queryForList("queryNoteYearOptions", paraMap);
	}

	/**
	 * 老红利通知书数据重抽档
	 * @param policyNo
	 * @return
	 */
	public Map<String, Object> divNoticeRedraw(String policyNo) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("p_policy_no", policyNo);
		queryForList("divNoticeRedraw", paraMap);
		return paraMap;
	}

	/**
	 * 更新邮储打印日期，及邮储的文件名，是否本地标志
	 * @param detailSequenceNo
	 * @param isLocal
	 * @param fileName
	 */
	public void updatePosNotePdfNameAndPDFUploadDate(String detailSequenceNo, String isLocal, String fileName) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("detailSequenceNo", detailSequenceNo);
		paraMap.put("isLocal", isLocal);
		paraMap.put("fileName", fileName);
		getSqlMapClientTemplate().update(sqlName("updatePosNotePdfNameAndPDFUploadDate"), paraMap, 1);
	}

	/**
	 * 更新通知书打印日期，及邮储的文件名
	 * @param detailSequenceNo
	 * @param pdfName
	 */
	public void updatePosNoteMainPrintDateAndPdfName(String detailSequenceNo, String fileName) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("detailSequenceNo", detailSequenceNo);
		paraMap.put("fileName", fileName);
		getSqlMapClientTemplate().update(sqlName("updatePosNoteMainPrintDateAndPdfName"), paraMap, 1);
	}
	
    /**
     * 根据批单号查询免填单，供打印使用
     * 
     * @param posNo
     * @return Map<String,Object>
     * @author GaoJiaMing
     * @time 2014-6-9
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> queryFillFormApplicationInfoByPosNo(String posNo) {
        Map<String, Object> result = (Map<String, Object>) queryForObject("queryFillFormApplicationInfoByPosNo", posNo);
        // 由于jasper对于半角的符号长度解释问题，导致空格部分都被缩小，所以这里将2个半角空格替换成一个全角空格
        if (result != null && !result.isEmpty()) {

            // 是否是淘宝渠道保单
            boolean isTaobaoPolicy = posRulesDAO.isTaobaoPolicy((String) result.get("POLICY_NO"));
            // 受理渠道
            String acceptChannelCode = (String) result.get("ACCEPT_CHANNEL_CODE");
            if (isTaobaoPolicy && "14".equals(acceptChannelCode) || "7".equals(acceptChannelCode))// 对淘宝渠道的保单在淘宝网上受理时
            {
                result.put("ACCEPTOR_NAME", "");

            }
            String approveText = (String) result.get("APPROVE_TEXT");
            if (StringUtils.isNotBlank(approveText)) {
                approveText = approveText.replaceAll("  ", "　");
                result.put("APPROVE_TEXT", approveText);
            }
        }
        return result;
    }

    /**
     * 免填单申请书获取模板批文（核保类）和提示信息
     * 
     * @param posNo
     * @return Map<String,Object>
     * @author GaoJiaMing
     * @time 2014-6-10
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getApplicationText(String posNo) {
        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("p_pos_no", posNo);
        queryForList("getApplicationText", paraMap);
        // 由于jasper对于半角的符号长度解释问题，导致空格部分都被缩小，所以这里将2个半角空格替换成一个全角空格
        if (paraMap != null && !paraMap.isEmpty()) {
            String approveText = (String) paraMap.get("p_content_text");
            if (StringUtils.isNotBlank(approveText)) {
                approveText = approveText.replaceAll("  ", "　");
                paraMap.put("p_content_text", approveText);
            }
            String remindingText = (String) paraMap.get("p_reminding_text");
            if (StringUtils.isNotBlank(remindingText)) {
                remindingText = remindingText.replaceAll("  ", "　");
                paraMap.put("p_reminding_text", remindingText);
            }
        }
        return paraMap;
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
        return queryForList("queryPosNosByPosNo", posNo);
    }

    /**
     * 根据保全号查询是否免填单
     * 
     * @param posNo
     * @return String
     * @author GaoJiaMing
     * @time 2014-6-16
     */
    public String checkIsApplicationFree(String posNo) {
        Map<String, Object> result = (Map<String, Object>) queryForObject("checkIsApplicationFree", posNo);
        return (String) result.get("ISAPPLICATIONFREE");
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
        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("detailSequenceNo", detailSequenceNo);
        paraMap.put("imFileId", imFileId);
        paraMap.put("fileName", fileName);
        getSqlMapClientTemplate().update(sqlName("updatePosNotePdfNameAndImFileId"), paraMap, 1);
    }
    
    /**
     * 根据保单号获取邮箱地址
     * @param policyNo
     * @return String
     * @author GaoJiaMing 
     * @time 2014-6-30
     */
    public String getEmailAddressForELetter(String policyNo) {
        return (String) queryForObject("getEmailAddressForELetter", policyNo);
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
        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("detailSequenceNo", detailSequenceNo);
        paraMap.put("noticeType", noticeType);
        return (String) queryForObject("getclientNoByDetailSequenceNoAndNoticeType", paraMap);
    } 
    
    /**
     * 电子函件订阅/退订接口
     * @param clientNo
     * @param eLetterService
     * @param emailAddress 
     * @return Map<String, Object>
     * @author GaoJiaMing 
     * @time 2014-7-1
     */
    public Map<String, Object> procELetterService(String clientNo, String eLetterService, String emailAddress) {
        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("p_client_no", clientNo);
        paraMap.put("p_e_letter_flag", eLetterService);
        paraMap.put("p_email_address", emailAddress);
        queryForList("mergeIntoClientSEervice", paraMap);
        return paraMap;
    }
    
    /**
     * 官网保单全部加挂标志同步接口
     * @param clientNo
     * @param addAll 
     * @return Map<String, Object>
     * @author GaoJiaMing 
     * @time 2014-7-2
     */
    public Map<String, Object> syncAddAllFlag(String clientNo,String addAll) {
        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("p_client_no", clientNo);
        paraMap.put("p_is_auto_added", addAll);
        queryForList("mergeIntoClientSEervice", paraMap);
        return paraMap;
    }

    /**
     * 官网加挂保单同步接口
     * @param policyNo
     * @param isAdded
     * @return Map<String,Object>
     * @author GaoJiaMing 
     * @time 2014-7-3
     */
    public Map<String, Object> syncEPolicy(String policyNo, String isAdded) {
        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("p_policy_no", policyNo);
        paraMap.put("p_is_added", isAdded);
        paraMap.put("p_is_manual", "Y");
        paraMap.put("p_added_time", new Date());
        queryForList("syncEPolicy", paraMap);
        return paraMap;
    }
    
    /**
     * 查询客户电子信函订阅状态
     * @param clientNo
     * @return String
     * @author GaoJiaMing 
     * @time 2014-7-3
     */
    public String getClientELetterStatus(String clientNo) {
        return (String) queryForObject("getClientELetterStatus", clientNo);
    }
    
    /**
     * 函件历史查询接口
     * 
     * @param policyNoList
     * @return List<PosNoteDTO>
     * @author GaoJiaMing
     * @time 2014-7-8
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> queryPosNoteHistory(Map<String, Object> paraMap) {
        return (List<Map<String, Object>>) queryForList("queryPosNoteHistory", paraMap);
    }

    /**
     * 获取电子函件ID(电子函件下载接口)
     * 
     * @param policyNo
     * @param detailSequenceNo
     * @return String
     * @author GaoJiaMing
     * @time 2014-7-8
     */
    public Map<String, Object> getLoadFileId(String policyNo, String detailSequenceNo) {
        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("policyNo", policyNo);
        paraMap.put("detailSequenceNo", detailSequenceNo);
        return (Map<String, Object>) queryForObject("getLoadFileId", paraMap);
    } 
    
    /**
     * 根据保全号在明细表里获取邮箱
     * @param posNo
     * @return String
     * @author GaoJiaMing 
     * @time 2014-6-30
     */
    public String getEmailAddressByPosNo(String posNo) {
        return (String) queryForObject("getEmailAddressByPosNo", posNo);
    }
    
    /**
     * 获取通知书类型
     * @param detailSequenceNo
     * @return String
     * @author GaoJiaMing 
     * @time 2014-6-30
     */
    public Map<String, Object> getNoteTypeDescriptionAndPolicyNo(String detailSequenceNo) {
        return (Map<String, Object>) queryForObject("getNoteTypeDescriptionAndPolicyNo", detailSequenceNo);
    } 
    
	/**
	 * 生成邮件日志
	 * 
	 * @param map
	 * @return void
	 * @author GaoJiaMing
	 * @time 2014-8-8
	 */
	public void insertPosMailLog(Map map) {
		getSqlMapClientTemplate().insert(sqlName("insertPosMailLog"), map);
	}
	
	/**
	 * 函件发送记录查询接口
	 * 
	 * @param detailSequenceNo
	 * @return List<Map<String, Object>>
	 * @author GaoJiaMing
	 * @time 2014-11-19
	 */
	public List<Map<String, Object>> getMailLogByDetailSequenceNo(String detailSequenceNo) {
		return (List<Map<String, Object>>) queryForList("getMailLogByDetailSequenceNo", detailSequenceNo);
	}
	
	/**
	 * 生成投保人名下保单资产证明书
	 * @methodName: procClientWealthNotice
	 * @Description: 
	 * @param clientNo
	 * @param applyDate
	 * @return Map<String,Object> 
	 * @author WangMingShun
	 * @date 2015-7-23
	 * @throws
	 */
	public Map<String, Object> procClientWealthNotice(String clientNo, Date applyDate) {
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("clientNo", clientNo);
		paraMap.put("applyDate", applyDate);
		queryForObject("clientWealthNotice", paraMap);
		return paraMap; 
	}
	
	/**
	 * 查询需增加二维码的个险渠道
	 * @param d
	 * @return Map<String,Object>
	 * @author TangJing
	 * @date 2016-7-4
	 */
	public String findChannelType(String policyNo){
		return (String) queryForObject("findChannelType", policyNo);
	}
	
}
