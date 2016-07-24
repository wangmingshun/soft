package com.sinolife.pos.common.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinolife.pos.common.consts.SessionKeys;
import com.sinolife.pos.common.dao.CommonQueryDAO;
import com.sinolife.pos.common.dto.LoginUserInfoDTO;
import com.sinolife.pos.common.include.dao.IncludeDAO;
import com.sinolife.pos.setting.user.privilege.dao.UserPrivilegeDAO;
import com.sinolife.sf.login.hander.SsoListener;
import com.sinolife.sf.platform.runtime.PlatformContext;

@Service("PosSsoListener")
public class PosSsoListener implements SsoListener {

	@Autowired
	private UserPrivilegeDAO userPrivilegeDAO;
	
	@Autowired
	private CommonQueryDAO commonQueryDAO;
	
	@Autowired
	private IncludeDAO includeDAO;
	
	@Override
	public void beforeLogout(HttpServletRequest request, HttpServletResponse response) {

	}

	@Override
	public void afterLogout(HttpServletRequest request, HttpServletResponse response) {

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void onLogin(HttpServletRequest request, HttpServletResponse response) {
		String currentUser = PlatformContext.getCurrentUser();
		
		HttpSession session = request.getSession();
		LoginUserInfoDTO userInfo = (LoginUserInfoDTO) session.getAttribute(SessionKeys.LOGIN_USER_INFO);
		if(userInfo == null) {
			Map retMap = userPrivilegeDAO.queryUserPrivsInfo(currentUser);
			userInfo = new LoginUserInfoDTO();
			userInfo.setLoginUserUMBranchCode(includeDAO.userBranchCode(currentUser));
			if(retMap != null && "Y".equals(retMap.get("userExist"))) {
				userInfo.setPosUser(true);
				userInfo.setPrivsServiceItems((List<String>) retMap.get("inputItems"));
				userInfo.setLoginUserID(currentUser.trim().toLowerCase());
				Map user = (Map) retMap.get("userInfo");
				if(user != null) {
					userInfo.setLoginUserName((String) user.get("USER_NAME"));
					userInfo.setLoginUserRankCode((String) user.get("RANK_CODE"));
					userInfo.setLoginUserHasDifferentPlacePrivs("Y".equals(user.get("DIFFERENT_PLACE_PRIVS")));
					String counterNo = (String) user.get("COUNTER_NO");
					userInfo.setLoginUserCounterNo(counterNo);
					
					Map<String, Object> counterInfo = commonQueryDAO.queryCounterInfoByCounterNo(counterNo);
					if(counterInfo != null) {
						userInfo.setLoginUserBranchCode((String) counterInfo.get("BRANCH_CODE"));
					}
					
					userInfo.setLoginUserAmountDaysGrade((String)user.get("AMOUNT_DAYS_GRADE"));
					userInfo.setLoginUserInputGrade((String)user.get("INPUT_GRADE"));
				}
				
				user = (Map) retMap.get("upperInfo");
				if(user != null) {
					userInfo.setLoginUserSupervisorExists(true);
					userInfo.setSupervisorID(((String) user.get("USER_ID")));
					userInfo.setSupervisorName((String) user.get("USER_NAME"));
					userInfo.setSupervisorRankCode((String) user.get("RANK_CODE"));
					userInfo.setSupervisorHasDifferentPlacePrivs("Y".equals(user.get("DIFFERENT_PLACE_PRIVS")));
					userInfo.setSupervisorCounterNo((String) user.get("COUNTER_NO"));
				}
			}
			session.setAttribute(SessionKeys.LOGIN_USER_INFO, userInfo);
		}
	}

	@Override
	public void onSessionTimeout(String currentUserId, HttpSession session) {
		session.removeAttribute(SessionKeys.LOGIN_USER_INFO);
	}

}
