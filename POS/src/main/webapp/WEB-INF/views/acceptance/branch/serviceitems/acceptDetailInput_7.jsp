<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<%-- 减额交清 --%>
<sf:override name="headBlock">
	<script type="text/javascript" language="javascript">
		posValidateHandler = function() {
			if($(".selected:enabled").length == 0) {
				$.messager.alert("提示", "没有可操作的险种。");
				return false;
			}
			if($(".selected:enabled:checked").length == 0) {
				$.messager.alert("提示", "请勾选要操作的险种。");
				return false;
			}
			//<c:if test="${empty TRIAL_FLAG}">
			if($(".selected:enabled").length != $(".selected:enabled:checked").length) {
				$.messager.alert("提示", "保单操作减额缴清，必须所有险种同时进行操作，如其他险种不进行操作，请先终止附加险或操作附加险退保。");
				return false;
			}
			//</c:if>
			return true;
		};
	</script>
</sf:override>
<sf:override name="serviceItemsInput">
<sfform:formEnv commandName="acceptance__item_input">
	<div class="layout_div_top_spance">
		<fieldset class="fieldsetdefault" style="padding:1px;">
			<legend>
				保单${acceptance__item_input.policyNo}险种明细：
				&nbsp;&nbsp;&nbsp;
				币种：人民币
			</legend>
			<table class="infoDisTab">
				<thead>
					<tr>
						<th>选择</th>
						<th width="20%">险种（代码）</th>
						<th>险种状态</th>
						<th>被保人</th>
						<th class="td_data">份数</th>
						<th class="td_data">保额</th>
						<th dataType="date">生效日期</th>
						<th dataType="date">交至日期</th>
						<th>缴费年期</th>
						<th>期缴保费</th>
						<th>缴费频次</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${acceptance__item_input.policyProductList}" var="item" varStatus="status">
					<tr class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
						<td><sfform:checkbox path="policyProductList[${status.index}].selected" disabled="${item.canBeSelectedFlag eq 'Y' ? false : true}" cssClass="selected"/></td>
						<td>${item.productFullName}（${item.productCode}）</td>
						<td>${item.dutyStatusDesc}</td>
						<td>${item.insuredName}</td>
						<td class="td_data">${item.units}</td>
						<td class="td_data">${item.baseSumIns}</td>
						<td><fmt:formatDate value="${item.effectDate}" pattern="yyyy-MM-dd"/></td>
						<td><fmt:formatDate value="${item.premInfo.payToDate}" pattern="yyyy-MM-dd"/></td>
						<td>${item.premInfo.premTerm}</td>
						<td>${item.premInfo.periodPremSum}</td>
						<td>${item.premInfo.frequencyDesc}</td>
					</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>
	</div>
</sfform:formEnv>
</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
