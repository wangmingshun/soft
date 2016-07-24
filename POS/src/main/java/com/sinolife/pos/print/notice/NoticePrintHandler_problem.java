package com.sinolife.pos.print.notice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.unihub.framework.util.common.DateUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.sinolife.pos.common.consts.CodeTableNames;
import com.sinolife.pos.common.dto.ClientInformationDTO;
import com.sinolife.pos.common.dto.PosInfoDTO;
import com.sinolife.pos.common.util.PosUtils;

/**
 * 保全问题函
 */
@Component("NoticePrintHandler_problem")
public class NoticePrintHandler_problem extends NoticePrintHandlerBase {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sinolife.pos.print.notice.NoticePrintHandler#getTempleteInputStream
	 * (java.lang.Object)
	 */
	@Override
	public InputStream getTempleteInputStream(Object posNote)
			throws IOException {
		if(((PosInfoDTO)posNote).getPremFlag().equals("-1")){
		return this.templateStore.getTemplateStream("POS", "print",
				"problemLetter");
		}
		else{
			return this.templateStore.getTemplateStream("POS", "print",
			"problemLetter+1");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sinolife.pos.print.notice.NoticePrintHandler#getFileName(java.lang
	 * .String)
	 */
	@Override
	public String getFileName(String key) {
		if (StringUtils.isNotBlank(key)) {
			return "保全问题函(" + key + ").pdf";
		} else {
			return "保全问题函-" + getTimeString() + ".pdf";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sinolife.pos.print.notice.NoticePrintHandler#queryPosNoteById(java
	 * .lang.String)
	 */
	@Override
	public Object queryPosNoteById(String id) {
		return commonQueryDAO.queryPosInfoRecord(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sinolife.pos.print.notice.NoticePrintHandler#getParameterMap(java
	 * .lang.Object)
	 */
	@Override
	public Map<String, Object> getParameterMap(Object posNote) {
		PosInfoDTO posInfo = (PosInfoDTO) posNote;
		String policyNo = posInfo.getPolicyNo();
		String posNo = posInfo.getPosNo();
		String clientNo = posInfo.getClientNo();
		String premFlag = posInfo.getPremFlag();
		Date applyDate = commonQueryDAO.getApplyDateByPosNo(posNo);
		ClientInformationDTO clientInfo = clientInfoDAO
				.selClientinfoForClientno(clientNo).get(0);
		ClientInformationDTO clientInfoApp = null;
		ClientInformationDTO clientInfoIns = null;
		String applicantNo = commonQueryDAO.getApplicantByPolicyNo(policyNo);
		String insuredNo = commonQueryDAO
				.getInsuredOfPrimaryPlanByPolicyNo(policyNo);
		if (clientNo.equals(applicantNo)) {
			clientInfoApp = clientInfo;
		} else {
			clientInfoApp = clientInfoDAO.selClientinfoForClientno(applicantNo)
					.get(0);
		}
		if (clientNo.equals(insuredNo)) {
			clientInfoIns = clientInfo;
		} else if (applicantNo.equals(insuredNo)) {
			clientInfoIns = clientInfoApp;
		} else {
			clientInfoIns = clientInfoDAO.selClientinfoForClientno(insuredNo)
					.get(0);
		}
		Map<String, Object> paraMap = new HashMap<String, Object>();

		// 客户姓名
		paraMap.put("CLIENT_NAME", this.getThreadParameter("CLIENT_NAME"));
		// 保单号
		paraMap.put("POLICY_NO", policyNo);
		// 保全号
		paraMap.put("POS_NO", posNo);
		//收付标识
		paraMap.put("PREM_FLAG", premFlag);
		// 条形码
		paraMap.put("BARCODE_NO", posInfo.getBarcodeNo());
		// 投保人姓名
		paraMap.put("APPLICANT_NAME", clientInfoApp.getClientName());
		// 被保人姓名
		paraMap.put("INSURED_NAME", clientInfoIns.getClientName());
		// 申请日期
		paraMap.put("APPLY_DATE", PosUtils.formatDate(applyDate, "yyyy年MM月dd日"));
		// 问题件内容
		paraMap.put("PROBLEM_CONTENT",
				this.getThreadParameter("PROBLEM_CONTENT"));
		// 打印日期
		paraMap.put("PRINT_DATE", PosUtils.formatDate(
				commonQueryDAO.getSystemDate(), "yyyy年MM月dd日"));
		// 业务员姓名
		paraMap.put("EMP_NAME", this.getThreadParameter("EMP_NAME"));
		// 业务员部门
		paraMap.put("EMP_DEPT", this.getThreadParameter("EMP_DEPT"));
		// 业务员电话
		paraMap.put("EMP_TEL", this.getThreadParameter("EMP_TEL"));
		// 业务员号码
		paraMap.put("EMP_NO", this.getThreadParameter("EMP_NO"));
		// 客户电话
		paraMap.put("CLIENT_TEL", this.getThreadParameter("CLIENT_TEL"));
		// 客户联系地址
		paraMap.put("CLIENT_ADDRESS", this.getThreadParameter("CLIENT_ADDRESS"));
		// 客户联系地址邮编
		paraMap.put("CLIENT_ZIP", this.getThreadParameter("CLIENT_ZIP"));
		// 打印条码
		paraMap.put("PRINT_BARCODE", this.getThreadParameter("PRINT_BARCODE"));
		// 问题号
		paraMap.put("PROBLEM_ITEM_NO",
				this.getThreadParameter("PROBLEM_ITEM_NO"));
		// 保全项目
		paraMap.put("SERVICE_ITEMS",
				PosUtils.getDescByCodeFromCodeTable(
						CodeTableNames.PRODUCT_SERVICE_ITEMS,
						posInfo.getServiceItems()));
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
		paraMap.put("IMAGE_SRC_PATH", fileAbsPath);

		// 问题件接受截止时间
		Map<String, Object> map = commonQueryDAO
				.queryProblemLetterEndDate(posNo);
		
		String endDate = map.get("p_cancel_date") == null ? "下发函件后的10个工作日" : DateUtil
				.dateToString(((Date) map.get("p_cancel_date")));

		paraMap.put("END_DAYS", endDate);

		return paraMap;
	}
}
