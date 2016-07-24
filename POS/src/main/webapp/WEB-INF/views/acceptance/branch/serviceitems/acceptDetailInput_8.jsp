<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<%-- 保单贷款 --%>
<sf:override name="headBlock">
	<script type="text/javascript" language="javascript">
	posValidateHandler = function() {
		var loanAmount = $("#loanAmount").val();
		var loanMinSum = "${acceptance__item_input.loanMinSum}";
		if (loanAmount) {
			if (parseFloat(loanAmount) < loanMinSum) {
				$.messager.alert("提示", "贷款金额必须大于等于最小可贷款金额" + loanMinSum);
				return false;
			}
		}
		var specialRuleMaxLoanCash = $("#specialRuleMaxLoanCash").html();
		if (specialRuleMaxLoanCash != "") {

			if (parseFloat(specialRuleMaxLoanCash) < loanAmount) {
				$.messager.alert("提示", "贷款金额不能大于最大可贷款金额"
						+ specialRuleMaxLoanCash);
				return false;
			}
		}

		return true;
	};

	initPage = function() {
		$("#specialFuncSelected").change(function() {
			if ($("#specialFuncSelected").is(":checked") == true) {
				$.post("${ctx}acceptance/branch/queryMaxLoanCash.do", {
					policyNo : "${acceptance__item_input.policyNo}",
					posNo : "${acceptance__item_input.posNo}",
					specialFunc : $("#specialFunc").val()
				}, function(data) {
					$("#inintMaxLoanCash").html("");
					$("#specialRuleMaxLoanCash").html(data);

				}, "json");
			} else {

				$.post("${ctx}acceptance/branch/queryMaxLoanCash.do", {
					policyNo : "${acceptance__item_input.policyNo}",
					posNo : "${acceptance__item_input.posNo}",
					specialFunc : ""
				}, function(data) {
					$("#inintMaxLoanCash").html("");
					$("#specialRuleMaxLoanCash").html(data);

				}, "json");
			}

		});
		if(!$("#specialFuncSelected")[0]) {
			/*
			<c:if test="${not TRIAL_FLAG eq 'Y'}">
				$.post("${ctx}acceptance/branch/queryMaxLoanCash.do", {
					policyNo : "${acceptance__item_input.policyNo}",
					posNo : "${acceptance__item_input.posNo}",
					specialFunc : ""
				}, function(data) {
					$("#inintMaxLoanCash").html("");
					$("#specialRuleMaxLoanCash").html(data);
	
				}, "json");
			</c:if>
			<c:if test="${TRIAL_FLAG eq 'Y'}">
				$("#inintMaxLoanCash").html("");
				$("#specialRuleMaxLoanCash").html("${acceptance__item_input.loanMaxSum}");
			</c:if>*/
			<c:choose>
				<c:when test="${TRIAL_FLAG eq 'Y'}">
					$("#inintMaxLoanCash").html("");
					$("#specialRuleMaxLoanCash").html("${acceptance__item_input.loanMaxSum}");
				</c:when>
				<c:otherwise>
					$.post("${ctx}acceptance/branch/queryMaxLoanCash.do", {
						policyNo : "${acceptance__item_input.policyNo}",
						posNo : "${acceptance__item_input.posNo}",
						specialFunc : ""
					}, function(data) {
						$("#inintMaxLoanCash").html("");
						$("#specialRuleMaxLoanCash").html(data);
					}, "json");
				</c:otherwise>
			</c:choose>
		}
	}
</script>
</sf:override>
<sf:override name="serviceItemsInput">
	<sfform:formEnv commandName="acceptance__item_input">
		<div class="layout_div_top_spance">
		&nbsp;币种：人民币&nbsp;&nbsp;&nbsp;
		保单已贷款本息和：${acceptance__item_input.loanAllSum}</div>
		<div class="layout_div_top_spance">
		<fieldset class="fieldsetdefault" style="padding: 1px;"><legend>保单${acceptance__item_input.policyNo}可贷险种明细：</legend>
		<table class="infoDisTab">
			<thead>
				<tr>
					<th>序号</th>
					<th>险种（代码）</th>
					<th>险种状态</th>
					<th>被保人</th>
					<th class="td_data">份数</th>
					<th class="td_data">保额</th>
					<c:if test="${acceptance__item_input.ulifeMainProduct}">
						<th class="td_data">账户价值</th>
					</c:if>
					<c:if test="${not acceptance__item_input.ulifeMainProduct}">
						<th class="td_data">现金价值</th>
					</c:if>
					<th class="td_data">贷款年利率</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${acceptance__item_input.policyProductList}"
					var="item" varStatus="status">
					<tr
						class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
						<td>${item.prodSeq}</td>
						<td>${item.productFullName}（${item.productCode}）</td>
						<td>${item.dutyStatusDesc}</td>
						<td>${item.insuredName}</td>
						<td class="td_data">${item.units}</td>
						<td class="td_data">${item.baseSumIns}</td>
						<td class="td_data">${item.cashValue}</td>
						<td class="td_data">${item.loanInterestRate eq 0 ? "不可贷款" :
						item.loanInterestRate}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		</fieldset>
		</div>
		<div class="layout_div_top_spance">
		<table class="layouttable">
			<tbody>
				<tr>
					<th class="layouttable_td_label" width="20%">本次最大可贷金额：</th>
					<td><span id="specialRuleMaxLoanCash"></span> <span
						id="inintMaxLoanCash"> ${acceptance__item_input.loanMaxSum}</span>
					</td>
				</tr>
				<tr>
					<th class="layouttable_td_label" width="20%">本次贷款金额： <span
						class="requred_font">*</span></th>
					<td class="layouttable_td_widget"><sfform:input
						id="loanAmount" path="loanAmount"
						cssClass="input_text {required:true,number:true}" /></td>
				</tr>
			</tbody>
		</table>
		</div>
	</sfform:formEnv>
</sf:override>
<jsp:include
	page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
