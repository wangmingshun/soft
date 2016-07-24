<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<%-- 预约终止附加险  --%>

<sf:override name="headBlock">
<script type="text/javascript">
$(function(){
	
});
posValidateHandler = function() {
	if($(".product").length == 0) {
		$.messager.alert("提示", "没有险种可以操作预约终止附加险！");
		return false;
	}
	if($(".product:checked").length == 0) {
		$.messager.alert("提示", "请至少选择一个险种！");
		return false;
	}
	return true;
};
</script>
</sf:override>

<sf:override name="serviceItemsInput">
	<sfform:formEnv commandName="acceptance__item_input">
		<table class="infoDisTab">
			<thead>
				<tr>
					<th width="5%">选择</th>
					<th width="30%">险种（代码）</th>
					<th>险种状态</th>
					<th>被保人</th>
					<th>生效日期</th>
					<th>期交保费</th>
					<th>是否主险</th>
					<th>保险期限</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${acceptance__item_input.productList}" var="item" varStatus="status">
		            <c:if test="${status.index%2==0}">
		                <tr class="odd_column">
		            </c:if>
		            <c:if test="${status.index%2!=0}">
		                <tr class="even_column">
		            </c:if>
						<td>
							<c:choose>
								<c:when test="${item.canBeSelectedFlag eq 'Y'}">
									<sfform:checkbox path="productList[${status.index}].selected" cssClass="product"/>
								</c:when>
								<c:otherwise>
									无法操作预约终止附加险，${item.message}
								</c:otherwise>
							</c:choose>
						</td>
						<td>${item.productFullName}（${item.productCode}）</td>
						<td>${item.dutyStatus}</td>
						<td>${item.insuredName}</td>
						<td><fmt:formatDate value="${item.effectDate}" pattern="yyyy-MM-dd"/></td>
						<td>${item.premInfo.periodPremSum}</td>
						<td>${item.isPrimaryPlan eq 'Y' ? '是' : '否'}</td>
						<td>${item.coverPeriodLabel}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</sfform:formEnv>
</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
