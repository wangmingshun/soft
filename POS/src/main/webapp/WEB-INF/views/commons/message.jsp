<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="content">
	<div class="font_headline" style="text-align:center;">
		<br/>
		${message}
		<c:if test="${param.displayExitLink eq 'Y'}">
			<a href="${ctx}SsoLogoutHtml.sso">退出系统</a>
		</c:if>
		<c:if test="${param.displayCloseWindowLink eq 'Y'}">
			<a href="javascript:void(0)" onclick="window.close();return false;">关闭窗口</a>
		</c:if>
	</div>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/NoLayout.jsp" />