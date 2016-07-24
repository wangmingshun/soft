package com.sinolife.pos.common.web;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.sinolife.pos.common.consts.SessionKeys;
import com.sinolife.pos.common.dto.LoginUserInfoDTO;
import com.sinolife.sf.platform.Plugin;
import com.sinolife.sf.platform.http.HttpInterceptor;
import com.sinolife.sf.platform.http.HttpInterceptorChain;

@Plugin(implementExtensionPoint = "com.sinolife.sf.Platfrom.HttpInterceptorExtensionPoint", pluginName = "PosAuthorizePlugin")
public class PosAuthorizePlugin implements HttpInterceptor {

	private Logger logger = Logger.getLogger(getClass());

	private static Set<String> RESTRICT_PREFIX_SET; // 检查的前缀，基本都是受理相关的，这类受理未设置上下级关系的话会报错
	static {
		Set<String> set = Collections.synchronizedSet(new HashSet<String>());
		set.add("acceptance"); // 机构受理
		RESTRICT_PREFIX_SET = set;
	}

	@Override
	public void interceptor(HttpServletRequest request,
			HttpServletResponse response, HttpInterceptorChain chain)
			throws IOException, ServletException {
		HttpSession session = request.getSession();
		LoginUserInfoDTO userInfo = (LoginUserInfoDTO) session
				.getAttribute(SessionKeys.LOGIN_USER_INFO);

		String requestURI = request.getRequestURI();
		int beginIndex = "/SL_POS/".length();
		int endIndex = requestURI.indexOf("/", "/SL_POS/".length());
		if (endIndex == -1) {
			endIndex = requestURI.length();
		}
		String prefix = requestURI.substring(beginIndex, endIndex);
		
		if (RESTRICT_PREFIX_SET.contains(prefix) && userInfo != null) {
			// 判断用户是否为POS用户
			if (!userInfo.isPosUser()) {
				logger.info("用户[" + userInfo.getLoginUserID()
						+ "]未在POS系统中设置权限，被阻止访问!");
				request.setAttribute("message", "您还未在POS系统中设置权限!");
				request.getRequestDispatcher(
						"/WEB-INF/views/commons/message.jsp?displayExitLink=Y")
						.forward(request, response);
				return;
			}

			// 判断是否有上级主管
			if (!userInfo.isLoginUserSupervisorExists()) {
				logger.info("用户[" + userInfo.getLoginUserID()
						+ "]未在POS系统中设置上级主管，被阻止访问!");
				request.setAttribute("message", "您还未在POS系统中设置上级主管!");
				request.getRequestDispatcher(
						"/WEB-INF/views/commons/message.jsp?displayExitLink=Y")
						.forward(request, response);
				return;
			}
		}

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		chain.interceptor(request, response);
		return;
	}

}
