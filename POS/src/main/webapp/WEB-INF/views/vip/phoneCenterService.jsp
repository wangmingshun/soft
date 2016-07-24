<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">电话中心服务记录查询&gt;&gt;服务记录</sf:override>
<sf:override name="head">
	<script type="text/javascript">
	/*
	$(".phoneDetail").live(
			"click",
			function(){
				var $this = $(this);
				var taskId = $this.attr("title");
				window.location.htef = "${ctx}vip/serviceDetail.do?taskId=" + taskId;
			});*/
	function phoneDetail(taskId) {
		window.location.href = "${ctx}vip/serviceDetail.do?taskId=" + taskId;
	}
	</script>
</sf:override>
<sf:override name="content">
	<div  class="easyui-panel" title="任务信息">
		<table class="infoDisTab" id="productList">
		<thead>
			<tr>
				<th width="13%" class="td_widget">任务号</th>
				<th width="10%" class="td_widget">所属活动</th>
				<th width="15%" class="td_widget">座席ID</th>
				<th width="5%" class="td_widget">座席姓名</th>
				<th width="17%" class="td_widget">来电时间</th>
				<th width="8%" class="td_widget">服务渠道</th>
				<th width="8%" class="td_widget">任务状态</th>
				<th width="8%" class="td_widget">所属专线</th>
				<th width="8%" class="td_widget">详细信息</th>
			</tr>
		</thead>

		<tbody>
			<c:forEach items="${callInRecord}" var="map" varStatus="status">
				<tr>
					<td>${map['taskId']}</td>
					<td>${map['activityDesc']}</td>
					<td>${map['dealUserId']}</td>
					<td>${map['dealUserName']}</td>
					<td>${map['callTime']}</td>
					<td>${map['channelDesc']}</td>
					<td>${map['taskStatus']}</td>
					<td>${map['lineDesc']}</td>
					<td><a href="javascript:phoneDetail('${map['taskId']}')">点击详细</a></td>
				</tr>
			</c:forEach>
			<tr><td colspan="9">&nbsp;</td></tr>
			<tr><td colspan="9" align="right"><a class="easyui-linkbutton" onclick="javascript:history.back();"
				iconCls="icon-search">返回</a></td></tr>
		</tbody>
	</table>
	</div>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
