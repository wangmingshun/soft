package com.sinolife.pos.acceptance.branch.web;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.sinolife.pos.common.consts.CodeTableNames;
import com.sinolife.pos.common.dao.ClientInfoDAO;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.PosApplyBatchDTO;
import com.sinolife.pos.common.dto.PosApplyFilesDTO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.common.util.PosValidateWrapper;

@Component
public class PosApplyBatchValidator implements Validator {

	@Autowired
	private CommonQueryDAO commonQueryDAO;
	
	@Autowired
	private ClientInfoDAO clientInfoDAO;
	
	@Override
	public boolean supports(Class<?> cls) {
		return PosApplyBatchDTO.class.isAssignableFrom(cls);
	}
	
	private static Map<String, String> APPLY_PERMIT_CONFIG_MAP;
	static {
		Map<String, String> config = new HashMap<String, String>();
		config.put("1", "AI");	//1犹豫期撤销   投保人、被保险人
		config.put("2", "AI");	//2退保  投保人、被保险人
		config.put("3", "A");	//3加保  投保人
		config.put("4", "A");	//4减保  投保人
		config.put("5", "A");	//5新增附加险  投保人
		config.put("6", "A");	//6复效   投保人
		config.put("7", "A");	//7减额交清  投保人
		config.put("8", "A");	//8保单贷款  投保人
		config.put("9", "A");	//9保单还款  投保人
		config.put("10", "AI"); //10生存保险金领取  生存金受益人
		config.put("11", "A");	//11红利领取  投保人
		config.put("12", "A");	//12年龄性别错误更正  投保人
		config.put("13", "A");	//13交费频次变更  投保人
		config.put("14", "A");	//14交费年期变更  投保人
		config.put("15", "A");	//15职业变更  投保人
		config.put("16", "A");	//16补充告知  投保人
		config.put("17", "A");	//17续期保费退费  投保人
		config.put("18", "AI");	//18签名变更   投保人、被保险人
		config.put("19", "A");	//19联系方式变更  投保人
		config.put("20", "AI");	//20投保人变更  投保人(被保险人:红玫瑰产品组合)
		config.put("21", "AI");	//21客户资料变更  投保人、被保险人
		config.put("22", "AI");	//22受益人变更  投保人、被保险人
		config.put("23", "A");	//23续期交费方式变更  投保人
		config.put("24", "AI");	//24年金给付方式选择  投保人、被保险人
		config.put("25", "A");	//25自垫选择权变更   投保人
		config.put("26", "A");	//26红利处理方式选择  投保人
		config.put("27", "A");	//27保单补发  投保人
		config.put("28", "A");	//28挂失及挂失解除  投保人
		config.put("29", "A");	//29预约终止附加险  投保人
		config.put("30", "A");	//30保单质押贷款  银行、投保人
		config.put("31", "A");	//31保单质押贷款解冻  银行、投保人
		config.put("32", "A");	//32提前满期申请 投保人
		config.put("33", "AI");	//33保全收付款方式调整  投保人、被保险人
		config.put("34", "A");	//34保险金转换年金 投保人
		config.put("35", "A");	//35追加保费  投保人
		config.put("36", "A");	//36基本保费变更  投保人
		config.put("37", "AI");	//37账户部分领取  投保人、被保险人
		config.put("38", "A");	//38账户分配比例变更  投保人
		config.put("39", "A");	//39投资账户转换  投保人
		config.put("42", "A");	//42保单生效日调整 投保人
		config.put("43", "A");	//43E服务权限调整
		config.put("44", "A");	//44险种转换  edit by wangmingshun
		config.put("45", "A");	//45保全预约  edit by wangmingshun
		config.put("46", "A");	//46保单冻结  edit by wangmingshun
		APPLY_PERMIT_CONFIG_MAP = config;
	}
	
	@Override
	public void validate(Object validateTarget, Errors errors) {
		PosValidateWrapper wrapper = new PosValidateWrapper(errors);
		PosApplyBatchDTO batch = (PosApplyBatchDTO)validateTarget;
		if(batch != null) {
			for(int i =0; i < batch.getApplyFiles().size();i++) {
				PosApplyFilesDTO file = batch.getApplyFiles().get(i);
				String pathPrefix = "applyFiles[" + i + "].";
				String barcodeNo = file.getBarcodeNo();
				String policyNo = file.getPolicyNo();
				String serviceItems = file.getServiceItems();
				
				wrapper.rejectIfEmpty(pathPrefix + "serviceItems", serviceItems, "申请书[" + (i + 1) + "]保全项目不能为空");
				
				wrapper.rejectIfEmpty(pathPrefix + "barcodeNo", barcodeNo, "申请书[" + (i + 1) + "]条码不能为空");
				
				wrapper.rejectIfEmpty(pathPrefix + "policyNo", policyNo, "申请书[" + (i + 1) + "]保单号不能为空");
				
				//对于1214类申请书（投资账户变更申请书），选择转账方式，账户户主姓名必须与投保人姓名保持一致
				if(file.getBarcodeNo().startsWith("1214") && "2".equals(file.getPaymentType()) && !commonQueryDAO.accountNameCanBeNotSameWithApplicant(policyNo)) {
					String applicantNo = commonQueryDAO.getApplicantByPolicyNo(policyNo);
					String applicantName = clientInfoDAO.selClientinfoForClientno(applicantNo).get(0).getClientName();
					if(!applicantName.equals(file.getTransferAccountOwner())) {
						wrapper.addErrMsg(pathPrefix + "transferAccountOwner", "申请书[" + (i + 1) + "]投资账户变更申请书，选择转账方式，账户户主姓名必须与投保人姓名保持一致");
					}
				}
				//不为1211开头的条码（有银行录入域），选择转账方式，校验银行信息
				if(!(barcodeNo.startsWith("1211") || barcodeNo.startsWith("1234")) && "2".equals(file.getPaymentType())) {
					wrapper.rejectIfEmpty("bankCode", file.getBankCode(), "申请书[" + (i + 1) + "]银行代码不能为空");
					wrapper.rejectIfEmpty("transferAccountno", file.getTransferAccountno(), "申请书[" + (i + 1) + "]银行账号不能为空");
					if(StringUtils.isNotBlank(file.getBankCode()) && StringUtils.isNotBlank(file.getTransferAccountno())) {
						Map<String, Object> retMap = commonQueryDAO.validateBankAccount(file.getBankCode(), file.getTransferAccountno());
						if("1".equals(retMap.get("p_flag")) && "N".equals(retMap.get("p_validate_flag"))) {
							wrapper.addErrMsg(pathPrefix + "bankNo", "申请书[" + (i + 1) + "]账户信息校验不通过：" + retMap.get("p_mes"));
						}
					}
				}
				
				//校验申请资格人
				String[] serviceItemsArr = serviceItems.split(",");
				String applyClientNo = batch.getClientNo();
				String applicantNo = commonQueryDAO.getApplicantByPolicyNo(policyNo);
				String insuredNo = commonQueryDAO.getInsuredOfPrimaryPlanByPolicyNo(policyNo);
				boolean isApplicant = applyClientNo.equals(applicantNo);
				boolean isInsured = applyClientNo.equals(insuredNo);
				for(String item : serviceItemsArr) {
					if(StringUtils.isNotBlank(item)) {
						String permitTarget = APPLY_PERMIT_CONFIG_MAP.get(item);
						if(permitTarget.indexOf("A") != -1 && isApplicant)
							continue;
						
						if(permitTarget.indexOf("I") != -1 && isInsured)
							continue;
						
						wrapper.addErrMsg(pathPrefix + "serviceItems", "申请书[" + (i + 1) + "]当前申请客户不具备保全项目" + item + PosUtils.getDescByCodeFromCodeTable(CodeTableNames.PRODUCT_SERVICE_ITEMS, item) + "的申请资格");
					}
				}
			}
		}
	}
}
