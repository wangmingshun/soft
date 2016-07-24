<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<%-- 基本保费变更 --%>
<sf:override name="headBlock">
	<script type="text/javascript" language="javascript">
		$(function(){
		});
	</script>
	<style type="text/css">
	</style>
</sf:override>
<sf:override name="serviceItemsInput">
<sfform:formEnv commandName="acceptance__item_input">
	<div class="font_title left">保单${acceptance__item_input.policyNo}险种明细信息</div>
	<table class="infoDisTab">
		<thead>
			<tr>
				<th>险种</th>
				<th>险种状态</th>
				<th>被保人</th>
				<th>保额</th>
				<th>缴费频次</th>
				<th>期缴保费</th>
				<th>缴至日</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${acceptance__item_input.policyProductList}" var="item" varStatus="status">
			<tr class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
				<td>${item.productFullName}（${item.productCode}）</td>
				<td>${item.premInfo.premStatusDesc}</td>
				<td>${item.insuredName}</td>
				<td>${item.baseSumIns}</td>
				<td>${item.premInfo.frequencyDesc}</td>
				<td>${item.premInfo.periodPremSum}</td>
				<td><fmt:formatDate value="${item.premInfo.payToDate}" pattern="yyyy-MM-dd"/></td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
	<br/>
	<div class="left">
		${acceptance__item_input.financialTypeProductFullName}变更后期缴保费金额：
		<sfform:input path="periodPrem" cssClass="textfield {required:true,number:true}"/>
	</div>
</sfform:formEnv>
</sf:override>
<sf:override name="buttonBlock">
</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
