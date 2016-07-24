package com.sinolife.pos.acceptance.branch.web;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.sinolife.pos.acceptance.branch.dto.ClientLocateCriteriaDTO;
import com.sinolife.pos.common.dto.LoginUserInfoDTO;
import com.sinolife.pos.common.util.PosUtils;
import com.sinolife.pos.common.util.PosValidateWrapper;

@Component
public class ClientLocateCriteriaValidator implements Validator {

	@Override
	public boolean supports(Class<?> cls) {
		return ClientLocateCriteriaDTO.class.isAssignableFrom(cls);
	}

	@Override
	public void validate(Object validateTarget, Errors errors) {
		System.out.println("validate ClientLocateCriteriaDTO...");
		PosValidateWrapper wrapper = new PosValidateWrapper(errors);
		//如果是用户在保全系统中操作保全受理，则校验登录用户的柜面号不为空
		LoginUserInfoDTO loginUserInfo = PosUtils.getLoginUserInfo();
		if(loginUserInfo != null) {
			//wrapper.rejectIfEmpty("counterNo", loginUserInfo.getLoginUserCounterNo(), "您的账户未在SL_POS系统中设置柜面号，无法操作批次受理");
		
			//对于其他系统提供试算菜单设置虚拟柜面号
			if(loginUserInfo.getLoginUserCounterNo()==null)
			{
				loginUserInfo.setLoginUserCounterNo("907");
				
			}
		}
		
		ClientLocateCriteriaDTO criteria = (ClientLocateCriteriaDTO)validateTarget;
		if(criteria != null) {
			String queryType = criteria.getQueryType();
			wrapper.rejectIfEmpty("queryType", queryType, "查询方式不能为空");
			wrapper.rejectIfNotInEnum("queryType", queryType, new String[]{"byId", "byName", "byClientNo", "byPolicy"}, "无效的查询方式");
			if("byId".equals(queryType)){
				wrapper.rejectIfEmpty("idTypeCode", criteria.getIdTypeCode(), "证件类型不能为空");
				wrapper.rejectIfEmpty("idNo", criteria.getClientIdNo(), "证件号码不能为空");
			} else if("byName".equals(queryType)) {
				wrapper.rejectIfEmpty("clientName", criteria.getClientName(), "客户姓名不能为空");
				//wrapper.rejectIfNull("birthday", criteria.getBirthday(), "客户生日不能为空");
				//wrapper.rejectIfEmpty("sex", criteria.getSex(), "客户性别不能为空");
			} else if("byClientNo".equals(queryType)) {
				wrapper.rejectIfEmpty("clientNo", criteria.getClientNo(), "客户号不能为空");
			} else if("byPolicy".equals(queryType)) {
				wrapper.rejectIfEmpty("policyNo", criteria.getPolicyNo(), "保单号不能为空");
			}
		}
	}
}
