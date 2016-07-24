package com.sinolife.pos.print.notice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.sinolife.pos.common.consts.CodeTableNames;
import com.sinolife.pos.common.dto.PolicyDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.util.PosUtils;

/**
 * 大额退费申请单（业务退费申请单）
 */
@Component("NoticePrintHandler_refundApp")
public class NoticePrintHandler_refundApp extends NoticePrintHandlerBase {

	/* (non-Javadoc)
	 * @see com.sinolife.pos.print.notice.NoticePrintHandler#getTempleteInputStream(java.lang.Object)
	 */
	@Override
	public InputStream getTempleteInputStream(Object posNote) throws IOException {
		return this.templateStore.getTemplateStream("POS", "print", "bigRefundApplicationForm");
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.print.notice.NoticePrintHandler#getFileName(java.lang.String)
	 */
	@Override
	public String getFileName(String key) {
		if(StringUtils.isNotBlank(key)) {
			return "大额退费申请单(" + key + ").pdf";
		} else {
			return "大额退费申请单-" + getTimeString() + ".pdf";
		}
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.print.notice.NoticePrintHandler#queryPosNoteById(java.lang.String)
	 */
	@Override
	public Object queryPosNoteById(String id) {
		PosInfoDTO posInfo = commonQueryDAO.queryPosInfoRecord(id);
		//由于非传统险在审批时还未进行交易，因此无法直接获取金额，退费金额通过试算获取
		posInfo.setPremSum(commonQueryDAO.getPosRefundSum(id));
		return posInfo;
	}

	/* (non-Javadoc)
	 * @see com.sinolife.pos.print.notice.NoticePrintHandler#getParameterMap(java.lang.Object)
	 */
	@Override
	public Map<String, Object> getParameterMap(Object posNote) {
		PosInfoDTO posInfo = (PosInfoDTO)posNote;
		String posNo = posInfo.getPosNo();
		String policyNo = posInfo.getPolicyNo();
		PolicyDTO policyInfo = commonQueryDAO.queryPolicyInfoByPolicyNo(policyNo);
		Map<String, Object> policyContractInfo = printDAO.queryPolicyContractInfo(policyNo);
		Date policyProvideDate = (Date) policyContractInfo.get("PROVIDE_DATE");
		Date clientSignDate = (Date) policyContractInfo.get("CLIENT_SIGN_DATE");
		Map<String, Object> paraMap = new HashMap<String, Object>();
		NumberFormat numberFormat = new DecimalFormat();
		
		//保全号
		paraMap.put("POS_NO", posNo);
		//保单号
		paraMap.put("POLICY_NO", policyNo);
		//退费分公司
		paraMap.put("REFUND_BRANCH", commonQueryDAO.getBranchFullNameByPosNo(posNo));
		//销售渠道
		paraMap.put("SALE_CHANNEL", policyInfo.getChannelTypeDesc());
		//保单主险名称
		paraMap.put("PRIMARY_PLAN_NAME", policyInfo.getPrimaryPlanFullName());
		//退费申请日期
		paraMap.put("REFUND_APPLY_DATE", PosUtils.formatDate(commonQueryDAO.getApplyDateByPosNo(posNo), "yyyy-MM-dd"));
		//保单生效日期
		paraMap.put("POLICY_EFFECT_DATE", PosUtils.formatDate(policyInfo.getEffectDate(), "yyyy-MM-dd"));
		//保单打印日期
		paraMap.put("POLICY_PRINT_DATE", PosUtils.formatDate(policyProvideDate, "yyyy-MM-dd"));
		//回执签收日期
		paraMap.put("POLICY_RECEIPT_SIGN_DATE", PosUtils.formatDate(clientSignDate, "yyyy-MM-dd"));
		//保全项目
		paraMap.put("SERVICE_ITEMS_DESC", PosUtils.getDescByCodeFromCodeTable(CodeTableNames.PRODUCT_SERVICE_ITEMS, posInfo.getServiceItems()));
		//退费方式
		paraMap.put("REFUND_TYPE", "1".equals(posInfo.getPaymentType()) ? "现金" : new StringBuilder("银行转账　　")
			.append(posInfo.getPremName())
			.append("　　")
			.append(PosUtils.getDescByCodeFromCodeTable(CodeTableNames.ID_TYPE, posInfo.getIdType()))
			.append("　　")
			.append(posInfo.getPremIdno())
			.toString());
		//若为合同撤销或解除，发票是否收回
		paraMap.put("INVOICE_GOT_BACK", printDAO.isInvoiceGotBack(posNo) ? "是" : "否");
		//退费金额
		paraMap.put("REFUND_SUM", posInfo.getPremSum() == null ? "" : numberFormat.format(posInfo.getPremSum()));
		//退费操作人
		paraMap.put("REFUND_OPERATOR", commonQueryDAO.getUserNameByUserId(posInfo.getAcceptor()));
		//退费操作备注
		paraMap.put("REFUND_MEMO", this.getThreadParameter("memo"));
		//图片的路径
		String fileAbsPath = null;
		try {
			File imageSrcPath = new ClassPathResource("/jasper_tmpl/image/").getFile();
			if(imageSrcPath.exists() && imageSrcPath.isDirectory()) {
				String absPath = imageSrcPath.getAbsolutePath();
				if(!absPath.endsWith("/") && !absPath.endsWith("\\"))
					absPath += File.separator;
				fileAbsPath = absPath;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		paraMap.put("IMAGE_SRC_PATH", fileAbsPath);
		
		return paraMap;
	}
}
