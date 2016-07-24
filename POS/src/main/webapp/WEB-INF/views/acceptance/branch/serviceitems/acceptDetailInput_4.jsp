<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<%-- 减保 --%>

<sf:override name="headBlock">
	<script type="text/javascript">
	var policyChannelType = "${acceptance__item_input.policyChannelType}";
	posValidateHandler = function() {

		if (policyChannelType != '11') {

			if ($(".productCb").length == 0) {
				$.messager.alert("提示", "没有险种可以减保！");
				return false;
			}
			if ($(".productCb:checked").length == 0) {
				$.messager.alert("提示", "请至少选择一个险种！");
				return false;
			}
		} else {

			if ($(".productCoverage").length == 0) {
				$.messager.alert("提示", "无可操作减保的险种责任!");
				return false;
			}
			if ($(".productCoverage:checked").length == 0) {
				$.messager.alert("校验错误", "请勾选需要操作减保的险种责任!");
				return false;
			}
		}
		return true;
	};

	initPage = function() {
		//选中才让修改
		$(".productCb")
				.change(
						function() {
							$(this).next().val(this.checked);
							if (this.checked == true) {
								$(this)
										.parent()
										.parent()
										.find(
												"input:not(.productCb,.agreePremSumInput)")
										.removeAttr("disabled");
							} else {
								$(this)
										.parent()
										.parent()
										.find(
												"input:not(.productCb,.planFlagHid,.selectedValueHid)")
										.val("");
								$(this).parent().parent().find(
										"input:not(.productCb)").attr(
										"disabled", true);
							}
							var sign = this.checked;
							var productCode = $(this).val();
							$(".productCb").not(this).each(
												function() {										
													if($(this).val() ==productCode){
														$(this).attr("checked", sign);
													}
							});
							$("#specialFuncSelected").trigger("change");
						});

		//选了功能特殊件才有协议减保金额录入
		$("#specialFuncSelected")
				.change(
						function() {
							var b = this.checked;
							$(".agreePremSumInput")
									.each(
											function() {
												if ($(this).parent().parent()
														.find(".productCb")
														.attr("checked") == true
														&& b) {
													$(this).removeAttr(
															"disabled");
												} else {
													$(this).attr("disabled",
															true);
													$(this).val("");
												}
											});
						});

		//基本保额变化决定了新的红利保额
		$(".baseSumInsInput, .periodStandardPremInput").change(
				function() {
					var n = $(this).val();//新值

					if ($(this).hasClass("baseSumInsInput")) {
						var d = $(this).parent().parent().find(".baseSumInsTd")
								.text();//原保额
					} else if ($(this).hasClass("periodStandardPremInput")) {
						var d = $(this).parent().parent().find(
								".annStandardPremTd").text();//原保费
					}

					var m = $(this).parent().parent().find(".dividendSumIns")
							.text();//原红利保额
					var c = $(this).parent().parent().find(".oldCashValue")
							.val();//原现价
					if (d != 0) {
						$(this).parent().parent().find(".newDividendSumInsTd")
								.text((n / d * m).toFixed(2));

						$(this).parent().parent().find(".newCashValue").val(
								((d - n) / d * c).toFixed(2));
					}
				});

		$(".agreePremSumInput").attr("disabled", true).val("");

		if ($(".productCoverage").length > 0) {
			$(".productCoverage")
					.change(
							function() {

								//更新全选按钮状态
								if ($(".productCoveraget").length == $(".productCoverage:checked").length) {
									$("#selectAllCoverage").attr("checked",
											true);
								} else {
									$("#selectAllCoverage").attr("checked",
											false);
								}

							});

			$("#selectAllCoverage").change(function() {
				if (this.checked) {
					$(".productCoverage").attr("checked", true);
				} else {
					$(".productCoverage").attr("checked", false);
				}
			});

		} else {

			$("#selectAllCoverage").attr("disabled", true);
		}

	};
</script>
</sf:override>

<sf:override name="serviceItemsInput">

	<sfform:formEnv commandName="acceptance__item_input">


		<c:if test="${11 ne acceptance__item_input.policyChannelType}">

			<table class="infoDisTab">
				<thead>
					<tr>
						<th>选择</th>
						<th>险种序号</th>
						<th>险种</th>
						<th>是否主险</th>
						<th>险种状态</th>
						<th>被保人</th>
						<th>变更前保费</th>
						<th>变更后保费</th>
						<th>变更前基本保额</th>
						<th>变更后基本保额</th>
						<th>变更前红利保额</th>
						<th>变更后红利保额</th>
						<th>现金价值</th>
						<th>协议减保金额</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${acceptance__item_input.productList}" var="item"
						varStatus="status">
						<c:if test="${status.index%2==0}">
							<tr class="odd_column">
						</c:if>
						<c:if test="${status.index%2!=0}">
							<tr class="even_column">
						</c:if>
						<td><c:choose>
							<c:when test="${item.canBeSelectedFlag eq 'Y'}">
								<input type="hidden" class="planFlagHid"
									value="${item.isPrimaryPlan}" />
								<input type="checkbox" name="productCb${status.index}"
									class="productCb {required:function(){ return $('.productCb:checked').length < 1}}"  value="${item.productCode}" />
								<sfform:hidden path="productList[${status.index}].selected" />
							</c:when>
							<c:otherwise>
							无法减保，${item.message}
						</c:otherwise>
						</c:choose></td>
						<td>${item.prodSeq}</td>
						<td>${item.productFullName}（${item.productCode}）</td>
						<td>${item.isPrimaryPlan}</td>
						<td>${item.dutyStatus}</td>
						<td>${item.insuredName}</td>
						<td class="annStandardPremTd">${item.annStandardPrem}</td>
						<td><c:choose>
							<c:when test="${item.canBeSelectedFlag eq 'Y'}">
								<c:choose>
									<c:when test="${item.financialFlag eq 'Y'}">
										<input type="hidden" value="投连万能产品，只能录保额" />
									</c:when>
									<c:when test="${item.calType ne '2'}">
										<input type="hidden" value="保额算保费或其他，只能录保额" />
									</c:when>
									<c:otherwise>
										<input type="hidden" value="${item.periodStandardPrem}"
											class="periodStandardPremHid" />
										<input type="text"
											name="productList[${status.index}].periodStandardPrem"
											disabled="disabled" size="6"
											class="periodStandardPremInput easyui-numberbox {required:true, smallerThan2:'${item.periodStandardPrem}'}" />
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
							&nbsp;
						</c:otherwise>
						</c:choose></td>
						<td class="baseSumInsTd">${item.baseSumIns}</td>
						<td><c:choose>
							<c:when test="${item.canBeSelectedFlag eq 'Y'}">
								<c:choose>
									<c:when test="${item.calType eq '2'}">
										<input type="hidden" value="保费算保额，只能录保费" />
									</c:when>
									<c:otherwise>
										<input type="hidden" value="${item.baseSumIns}"
											class="baseSumInsHid" />
										<input type="text"
											name="productList[${status.index}].baseSumIns"
											disabled="disabled" size="6"
											class="baseSumInsInput easyui-numberbox {required:true, smallerThan2:'${item.baseSumIns }'}" />
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
							&nbsp;
						</c:otherwise>
						</c:choose></td>
						<td class="dividendSumIns">${item.dividendSumIns}</td>
						<td class="newDividendSumInsTd"></td>
						<td>${item.cashValue} <input type="hidden"
							class="oldCashValue" value="${item.cashValue}" /> <input
							type="hidden" name="productList[${status.index}].cashValue"
							class="newCashValue" value="${item.cashValue}" /></td>
						<td><c:choose>
							<c:when test="${item.canBeSelectedFlag eq 'Y'}">
								<input type="text"
									name="productList[${status.index}].agreePremSum"
									disabled="disabled" size="6"
									class="agreePremSumInput easyui-numberbox {required:true}" />
							</c:when>
							<c:otherwise>
							&nbsp;
						</c:otherwise>
						</c:choose></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>

		</c:if>
		<c:if test="${11 eq acceptance__item_input.policyChannelType}">
			<table class="infoDisTab" id="infoProductCoverage">
				<input type="hidden" name="checkOneAtLeast"
					class="{required:function(){ return $('.productCoverage:checked').length < 1;},        messages:{required:'请至少选择一个险种责任'}}" />

				<thead>
					<tr>
						<th class="td_widget">全选<input type="checkbox"
							id="selectAllCoverage" /></th>
						<th width="25%">险种（代码）</th>
						<th>险种状态</th>
						<th>责任（代码）</th>
						<th>被保人</th>
						<th>原保额</th>
						<th>原保费</th>
						<th>变更后保额</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach
						items="${acceptance__item_input.policyProductCoverageList}"
						var="item" varStatus="status">
						<tr
							class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
							<td class="td_widget"><sfform:checkbox
								path="policyProductCoverageList[${status.index}].selected"
								cssClass="productCoverage" /></td>
							<td>${item.productFullName}（${item.productCode}）</td>
							<td>${item.dutyStatusDesc}</td>
							<td>${item.coverageName}（${item.coverageCode}）</td>
							<td>${item.insuredName}</td>
							<td>${item.sumIns}</td>
							<td>${item.prem}</td>
							<td><sfform:input
								path="policyProductCoverageList[${status.index}].newSumIns"
								cssStyle="width:80px;"
								cssClass="input_text newSumIns {required:'.productCoverage:eq(${status.index}):checked',number:true, max:${item.sumIns}}" /></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>

	</sfform:formEnv>
</sf:override>
<jsp:include
	page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
