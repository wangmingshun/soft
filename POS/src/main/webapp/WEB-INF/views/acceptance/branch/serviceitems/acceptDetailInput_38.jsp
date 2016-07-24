<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<%-- 账户分配比例变更 --%>
<sf:override name="headBlock">
	<script type="text/javascript" language="javascript">
		$(function(){
			$(".newRatePercent").change(function(){
				var $newRatePercent = $(".newRatePercent");
				var total = 0;
				$newRatePercent.each(function(idx, content){
					total += parseInt($($newRatePercent[idx]).val());
				});
				$("#newRatePercentTotal").html(total);
			});
			
			$("#resetRate").click(function() {
				$(".newRatePercent option[value='0']").attr("selected", true);
				return false;
			});
			
			$(".newRatePercent").trigger("change");
		});
		posValidateHandler = function() {
			var changed = false;
			var $newRatePercent = $(".newRatePercent");
			var $i;
			$newRatePercent.each(function(idx, content){
				$i = $($newRatePercent[idx]);
				if($i.val() != $i.attr("title"))
					changed = true;
			});
			if(!changed) {
				$.messager.alert("提示","没有值变更，请选择变更后的比例");
				return false;
			}
			if($("#newRatePercentTotal").html() != 100) {
				$.messager.alert("提示","新值之和必须为100，请重新选择变更后的比例");
				return false;
			}
			
			return true;
		};
	</script>
	<style type="text/css">
	</style>
</sf:override>
<sf:override name="serviceItemsInput">
<sfform:formEnv commandName="acceptance__item_input">
	保单${acceptance__item_input.policyNo}账户明细信息：
	<table class="infoDisTab">
		<thead>
			<tr>
				<th rowspan="2">投资账户</th>
				<th colspan="2" align="center">保险费账户分配比例（%）</th>
			</tr>
			<tr>
				<th>原值</th>
				<th>新值</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${acceptance__item_input.financialProductsList}" var="item" varStatus="status">
				<tr class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
					<td>${item.finProductsDesc}</td>
					<td>${acceptance__item_input.originServiceItemsInputDTO.financialProductsList[status.index].ratePercent}</td>
					<td>
						<sfform:select path="financialProductsList[${status.index}].ratePercent" items="${acceptance__item_input.rateList}" itemLabel="description" itemValue="code"
							cssClass="newRatePercent {required:true}" title="${acceptance__item_input.originServiceItemsInputDTO.financialProductsList[status.index].ratePercent}"/>
					</td>
				</tr>
				<c:if test="${status.last}">
					<tr class="<c:choose><c:when test="${status.index mod 2 eq 0}">even_column</c:when><c:otherwise>odd_column</c:otherwise></c:choose>">
						<td>合计</td>
						<td>100</td>
						<td><div id="newRatePercentTotal"></div></td>
					</tr>
				</c:if>
			</c:forEach>
		</tbody>
	</table>
</sfform:formEnv>
</sf:override>
<sf:override name="buttonBlock">
    <a id="resetRate" href="javascript:void(0)">清空分配比例</a>
</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
