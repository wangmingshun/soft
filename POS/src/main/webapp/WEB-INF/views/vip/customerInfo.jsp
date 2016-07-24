<%@page import="java.text.SimpleDateFormat;"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/head.jsp"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">VIP客户资料查询&gt;&gt;详细信息</sf:override>

<jsp:include page="out_customerInfo.jsp"></jsp:include>

<table align="center">
		<tr>
			<td><a href="${ctx}vip/cardChange.do">【VIP卡号新增】</a> ┊</td>
			<td><a href="${ctx}vip/empChange.do">【服务专员变更】</a> ┊</td>
			<td><a href="${ctx}vip/sharedService.do">【尊荣服务记录】</a> ┊</td>
			<td><a href="${ctx}vip/phoneCenterService.do">【电话中心服务记录查询】</a></td>

		</tr>
	</table>






