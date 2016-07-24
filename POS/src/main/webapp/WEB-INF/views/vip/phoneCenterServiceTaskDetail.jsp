<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">电话中心服务记录查询&gt;&gt;服务记录&gt;&gt;服务记录详细信息</sf:override>
<sf:override name="head">
</sf:override>
<sf:override name="content">
	<div  class="easyui-panel" title="任务详细信息">
		<table class="infoDisTab" id="productList">
		<thead>
			<tr>
				<th width="3%" class="td_widget">序号</th>
				<th width="8%" class="td_widget">任务类型</th>
				<th width="7%" class="td_widget">服务名称</th>
				<th width="5%" class="td_widget">服务状态</th>
				<th width="12%" class="td_widget">保单号</th>
				<th width="20%" class="td_widget">业务号</th>
			</tr>
		</thead>

		<tbody>
			<c:forEach items="${serviceDetail}" var="map" varStatus="status">
				<tr>
					<td>${map['seq']}</td>
					<td>${map['taskTypeDesc']}</td>
					<td>${map['serviceName']}</td>
					<td>${map['serviceStatus']}</td>
					<td>${map['policyNo']}</td>
					<td>${map['businessNo']}</td>
				</tr>
			</c:forEach>
			<tr><td colspan="6" align="right"><a class="easyui-linkbutton" onclick="javascript:history.back();"
				iconCls="icon-search">返回</a></td></tr>
		</tbody>
	</table>
	</div>
	<div></div>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
