<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<%-- 提前满期申请 --%>
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
	保单信息：
	<table class="infoDisTab">
		<thead>
			<tr>
				<th>保单号</th>
				<th>被保人</th>
				<th>投保人</th>
				<th>生效日期</th>
				<th>交至日</th>
				<th>保单状态</th>
				<c:if test="${acceptance__item_input.productCode eq 'CIAN_FR1'}">
				<th width="20%">提前领取日期</th>
				</c:if>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td>${acceptance__item_input.policyNo}</td>
				<td>${acceptance__item_input.insuredName}</td>
				<td>${acceptance__item_input.applicantName}</td>
				<td>
					<fmt:formatDate value="${acceptance__item_input.effectDate}" pattern="yyyy-MM-dd" />
				</td>
				<td>
					<fmt:formatDate value="${acceptance__item_input.payToDate}" pattern="yyyy-MM-dd" />
				</td>
				<td>${acceptance__item_input.policyStatusDesc}</td>
				<c:if test="${acceptance__item_input.productCode eq 'CIAN_FR1'}">
				<td>
					<sfform:input path="advancedMaturityDate" cssClass="textfield_null {required:true} Wdate" onclick="WdatePicker({skin:'whyGreen'});"/>
				</td>
				</c:if>
			</tr>
		</tbody>
	</table>
</sfform:formEnv>
</sf:override>
<sf:override name="buttonBlock">
</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
