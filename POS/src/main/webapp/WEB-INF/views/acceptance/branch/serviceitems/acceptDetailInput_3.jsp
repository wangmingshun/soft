<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<%-- 加保 --%>
<sf:override name="headBlock">

	<script type="text/javascript" language="javascript">
	var policyChannelType = "${acceptance__item_input.policyChannelType}";
	$(function() {
		if ($(".product").length > 0) {
			$(".product")
					.change(
							function() {
								var $thisTR = $(this).closest("tr");
								var $newBaseSumIns = $thisTR
										.find(".newBaseSumIns");
								var $newAnnStandardPrem = $thisTR
										.find(".newAnnStandardPrem");
								var calType = $thisTR.find(".calType").val();
								var financialFlag = $thisTR.find(
										".financialFlag").val();

								if (this.checked) {
									if (calType == "0" || calType == "1"
											|| financialFlag == "Y") {
										//当险种费率的计算方式为 1-保额算保费 或 为投连万能险种，则只允许录入变更后保额
										$newBaseSumIns.attr("disabled", false)
												.removeClass("ignore");
										$newAnnStandardPrem.attr("disabled",
												true).removeClass("ignore")
												.addClass("ignore");
									} else if (calType == "2"
											&& financialFlag != "Y") {
										//当险种费率的计算方式为 2-保费算保额 且 不为投连万能险种，则只允许录入变更后保费
										$newBaseSumIns.attr("disabled", true)
												.removeClass("ignore")
												.addClass("ignore");
										$newAnnStandardPrem.attr("disabled",
												false).removeClass("ignore");
									}
								} else {
									$newBaseSumIns.attr("disabled", true)
											.removeClass("ignore").addClass(
													"ignore");
									$newAnnStandardPrem.attr("disabled", true)
											.removeClass("ignore").addClass(
													"ignore");
								}

								//更新全选按钮状态
								if ($(".product").length == $(".product:checked").length) {
									$("#selectAll").attr("checked", true);
								} else {
									$("#selectAll").attr("checked", false);
								}
							});
			$("#selectAll").change(function() {
				if (this.checked) {
					$(".product").attr("checked", true).trigger("change");
				} else {
					$(".product").attr("checked", false).trigger("change");
				}
			});
			$(".product").trigger("change");
		} else {
			$("#selectAll").attr("disabled", true);
		}

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
	});
	posValidateHandler = function() {
		if (policyChannelType != '11') {
			if ($(".product").length == 0) {
				$.messager.alert("提示", "无可操作加保的险种!");
				return false;
			}
			if ($(".product:checked").length == 0) {
				$.messager.alert("校验错误", "请勾选需要操作加保的险种!");
				return false;
			}
		} else {
			if ($(".productCoverage").length == 0) {
				$.messager.alert("提示", "无可操作加保的险种责任!");
				return false;
			}
			if ($(".productCoverage:checked").length == 0) {
				$.messager.alert("校验错误", "请勾选需要操作加保的险种责任!");
				return false;
			}

		}

		var mainProductCode = "${acceptance__item_input.mainProductCode}";
		var agentNo = $("#agentNo").val();
		if ("CEAN_AN1" == mainProductCode && "" == agentNo) {
			if (window.confirm('确认不录入业务员编号吗？')) {
				//alert("确定");
				return true;
			} else {
				//alert("取消");
				return false;
			}
		}
		return true;
	};
</script>
	<style type="text/css">
</style>
</sf:override>
<sf:override name="serviceItemsInput">
	<sfform:formEnv commandName="acceptance__item_input">
		<div class="layout_div_top_spance">
		<div class="font_heading2">币种：人民币</div>

		<c:if test="${11 ne acceptance__item_input.policyChannelType}">

			<c:if test="${'CEAN_AN1' eq acceptance__item_input.mainProductCode}">
				<table class="infoDisTab" id="infoProductCoverage">
					<input type="hidden" name="checkOneAtLeast"
						class="{required:function(){ return $('.product:checked').length < 1;},              messages:{required:'请至少选择一个险种责任'}}" />
					<thead>
						<tr>
							<th class="td_widget">全选<input type="checkbox"
								id="selectAllCoverage" /></th>
							<th width="25%">险种（代码）</th>
							<th>险种状态</th>
							<th>被保人</th>
							<th>原保额</th>
							<th>原保费</th>
							<th>加保金额</th>
							<th>业务员代码</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${acceptance__item_input.policyProductList}"
							var="item" varStatus="status">
							<tr
								class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
								<td class="td_widget"><sfform:checkbox
									path="policyProductList[${status.index}].selected"
									cssClass="product" /></td>
								<td>${item.productFullName}（${item.productCode}）</td>
								<td>${item.dutyStatusDesc}</td>
								<td>${item.insuredName}</td>
								<td class="td_data">${acceptance__item_input.originServiceItemsInputDTO.policyProductList[status.index].baseSumIns}</td>
								<td class="td_data">${acceptance__item_input.originServiceItemsInputDTO.policyProductList[status.index].premInfo.annStandardPrem}</td>
								<td><sfform:input
									path="policyProductList[${status.index}].AddPremSum"
									cssStyle="width:80px;"
									cssClass="input_text {required:'.product:eq(${status.index}):checked',number:true}" />

								</td>
								<td>${item.agentNo} <sfform:input
									path="policyProductList[${status.index}].agentNo"
									cssStyle="width:80px;" id="agentNo" cssClass="agent" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>
			<c:if test="${'CEAN_AN1' ne acceptance__item_input.mainProductCode}">



				<input type="hidden" name="checkOneAtLeast"
					class="{required:function(){ return $('.product:checked').length < 1;},             messages:{required:'请至少选择一个险种'}}" />
				<table class="infoDisTab">
					<thead>
						<tr>
							<th class="td_widget">全选<input type="checkbox"
								id="selectAll" /></th>
							<th width="25%">险种（代码）</th>
							<th>险种状态</th>
							<th>被保人</th>
							<th class="td_data">原保额</th>
							<th class="td_data">原保费</th>
							<th>变更后保额</th>
							<th>变更后保费</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${acceptance__item_input.policyProductList}"
							var="item" varStatus="status">
							<tr
								class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
								<td class="td_widget"><c:choose>
									<c:when test="${item.canBeSelectedFlag eq 'Y'}">
										<sfform:checkbox
											path="policyProductList[${status.index}].selected"
											cssClass="product" />
									</c:when>
									<c:otherwise>
								无法加保，${item.message}
							</c:otherwise>
								</c:choose></td>
								<td>${item.productFullName}（${item.productCode}）</td>
								<td>${item.dutyStatusDesc}</td>
								<td>${item.insuredName}</td>
								<td class="td_data">${acceptance__item_input.originServiceItemsInputDTO.policyProductList[status.index].baseSumIns}</td>
								<td class="td_data">${acceptance__item_input.originServiceItemsInputDTO.policyProductList[status.index].premInfo.annStandardPrem}</td>
								<td><c:choose>
									<c:when test="${item.canBeSelectedFlag eq 'Y'}">
										<input type="hidden" value="${item.calType}" class="calType" />
										<input type="hidden" value="${item.financialFlag}"
											class="financialFlag" />
										<sfform:input
											path="policyProductList[${status.index}].newBaseSumIns"
											cssStyle="width:80px;"
											cssClass="input_text newBaseSumIns {required:'.product:eq(${status.index}):checked',number:true,min:${item.baseSumIns}}" />
									</c:when>
									<c:otherwise>
								&nbsp;
							</c:otherwise>
								</c:choose></td>
								<td><c:choose>
									<c:when test="${item.canBeSelectedFlag eq 'Y'}">
										<sfform:input
											path="policyProductList[${status.index}].newAnnStandardPrem"
											cssStyle="width:80px;"
											cssClass="input_text newAnnStandardPrem {required:'.product:eq(${status.index}):checked',number:true,min:${item.premInfo.annStandardPrem}}" />
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
		</c:if> <c:if test="${11 eq acceptance__item_input.policyChannelType}">
			<table class="infoDisTab" id="infoProductCoverage">
				<input type="hidden" name="checkOneAtLeast"
					class="{required:function(){ return $('.productCoverage:checked').length < 1;},              messages:{required:'请至少选择一个险种责任'}}" />

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
								cssClass="input_text newSumIns {required:'.productCoverage:eq(${status.index}):checked',number:true, min:${item.sumIns}}" /></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if></div>
	</sfform:formEnv>
</sf:override>
<jsp:include
	page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
