<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<%@ include file="/WEB-INF/views/commons/head.jsp"%>
<div class="layout_div">
	<div class="navigation_div">
        <font class="font_heading1">E服务平台信息</font>>><a href="#">客户详细信息</a>      
    </div>  
	<c:if test="${not empty userInfo }">
	<div id="content" class="easyui-panel" title="客户基本信息" collapsible="true">
		<table class="infoDisTab">
			<tbody>
				<tr>
					<td align="right" width="13%" bgcolor="#E9EEFD"> 姓名: </td>
					<td width="20%">${userInfo.clientName }</td>
					<td align="right" width="13%" bgcolor="#E9EEFD"> 客户号: </td>
					<td width="20%">${userInfo.clientNo }</td>
				</tr>
				<tr>
					<td align="right" width="13%" bgcolor="#E9EEFD"> 性别: </td>
					<td width="20%">${userInfo.sexDesc }</td>
					<td align="right" width="13%" bgcolor="#E9EEFD"> 手机号码: </td>
					<td width="20%">${userInfo.mobile }</td>
				</tr>
				<tr>
					<td align="right" width="13%" bgcolor="#E9EEFD"> 出生日期: </td>
					<td width="20%"><fmt:formatDate value="${userInfo.birthday }" pattern="yyyy-MM-dd"/></td>
					<td align="right" width="13%" bgcolor="#E9EEFD"> 绑定邮箱: </td>
					<td width="20%">${userInfo.email }</td>
				</tr>
				<tr>
					<td align="right" width="13%" bgcolor="#E9EEFD"> 证件类型: </td>
					<td width="20%">${userInfo.idTypeName }</td>
					<td align="right" width="13%" bgcolor="#E9EEFD"> 注册时间: </td>
					<td width="20%"><fmt:formatDate value="${userInfo.registerDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				</tr>
				<tr>
					<td align="right" width="13%" bgcolor="#E9EEFD"> 证件号码: </td>
					<td width="20%">${userInfo.idNo }</td>
					<td align="right" width="13%" bgcolor="#E9EEFD"> 上次登录时间: </td>
					<td width="20%"><fmt:formatDate value="${userInfo.lastLoginDate }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
				</tr>
			</tbody>
		</table>
	</div>
	<div class="easyui-panel" title="加挂信息" collapsible="true">
		<table class="infoDisTab">
			<tr>
				<th width="5%" class="td_widget"> 序号 </th>
				<th width="10%" class="td_widget">保单号</th>
				<th width="20%" class="td_widget">主险名称</th>
				<th width="10%" class="td_widget">生效日期</th>
				<th width="5%" class="td_widget">保单状态</th>
				<th width="15%" class="td_widget"> 加挂时间 </th>
			</tr>
			<c:forEach items="${userInfo.bindPolicyList }" var="map" varStatus="status">
			<c:if test="${status.index % 2 == 0 }">
				<tr class="even_column">
			</c:if>
			<c:if test="${status.index % 2 != 0 }">
				<tr class="odd_column">
			</c:if>
				<td  width="5%" class="td_widget"> ${status.index + 1 } </td>
				<td> ${map['POLICYNO']} </td>
				<td> ${map['NAME'] } </td>
				<td><fmt:formatDate value="${map['EFFECTDATE'] }" pattern="yyyy-MM-dd"/></td>
				<td> ${map['STATUS'] } </td>
				<td><fmt:formatDate value="${map['LASTBINDDATE'] }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			</tr>
			</c:forEach>
		</table>
	</div>
	</c:if>
</div>
