<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
<sf:override name="pathString">待OA审批任务&gt;&gt;详细信息</sf:override>
<script type="text/javascript">

</script>
</sf:override>
<sf:override name="content">

<div class="easyui-panel">
<table class="infoDisTab">
<thead>
	<th class="td_widget">序号</th>
	<th class="td_widget">审批人</th>
	<th class="td_widget">审批时间</th>
	<th class="td_widget">审批状态</th>
	<th class="td_widget">审批意见</th>
	<th class="td_widget">节点描述</th>
	<th class="td_widget">节点序号</th>
</thead>
<tbody>
<c:forEach items="${history }" var="map" varStatus="status">
	<c:if test="${not empty map['taskStep']}">
	<tr>
		<td>${status.index + 1 }</td>
		<td>${map['oaFlowApprover'] }:
		<c:choose>
			<c:when test="${map['oaFlowApproverId'] eq ''}">${map['userId'] }</c:when>
			<c:otherwise>${map['oaFlowApproverId'] }</c:otherwise>
		</c:choose>
		</td>
		<td>${map['approveTime'] }</td>
		<td>${map['oaFlowOpinion'] }</td>
		<td>${map['oaFlowRemark'] }</td>
		<td>${map['oaFlowNode'] }</td>
		<td>${map['taskStep'] }</td>
	</tr>
	</c:if>
</c:forEach>
	<tr><td colspan="7" align="right"><a class="easyui-linkbutton" onclick="javascript:history.back();"
				iconCls="icon-search">返回</a></td></tr>
</tbody>
</table>
</div>
<a></a>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>