<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<%-- 退保 --%>
<sf:override name="headBlock">
	<script type="text/javascript">
	initPage = function() {
		$("#newApplyBarCodePremSpan").val("").hide();
		$(".allCb")
				.change(
						function() {
							$(".productCb").attr("disabled", this.checked)
									.attr("checked", this.checked).trigger(
											"change");

							var $specialRetreatReason = $("#specialRetreatReason");

							$("#specialRetreatReason").find("option").remove();

							if ($("#specialFuncSelected").is(":checked") == true
									&& (selectedSpecFunc == '2'
											|| selectedSpecFunc == '8' || selectedSpecFunc == '9')) {
								$("#specialRetreatReasonLabel").attr("style",
										"");
								$("#specialRetreatReason").find("option")
										.remove();
								$
										.post(
												"${ctx}acceptance/branch/querySpecialRetreatReason.do",
												{
													"specialFunc" : selectedSpecFunc
												},
												function(data) {
													$("#specialRetreatReason")
															.find("option")
															.remove();
													for ( var i = 0; data
															&& data.length
															&& i < data.length; i++) {

														$("<option/>")
																.attr(
																		"value",
																		data[i].code)
																.html(
																		data[i].description)
																.appendTo(
																		$specialRetreatReason);
													}
												}, "json");

							} else {
								$("#specialRetreatReason").find("option")
										.remove();
								$("#specialRetreatReasonLabel").attr("style",
										"display:none");

							}

						});

		//选中主险，附加险都要选上
		$(".productCb").change(function() {
			$(this).next().val(this.checked);
			if ($(this).prev().val() == "Y") {
				$(".allCb").attr("checked", this.checked);
				$(".productCb").not(this).attr("disabled", this.checked);
				$(".productCb").attr("checked", this.checked);
				$(".productCb").not(this).trigger("change");
			}else{
				//DMP-13411家庭单修改
				var productCode = $(this).val();
				var sign = this.checked;
				if(productCode=="CPDD_CN1"){
					$(".allCb").attr("checked", this.checked);
					//$(".productCb").not(this).attr("disabled", this.checked);
					$(".productCb").attr("checked", this.checked);
				}
				$(".productCb").not(this).each(
							function() {
								if($(this).val() ==productCode){
									$(this).attr("checked", sign);
								}
				});
				
			}
			$("#specialFuncSelected").trigger("change");
		});

		//选了功能特殊件才有协议退保特殊退费金额录入
		$("#specialFuncSelected,#specialFunc").change(
				function() {
					var permitAgreePremSumInput = $("#specialFuncSelected").is(
							":checked");
					var spValue = $("#specialFunc option:selected").val();
					$(".agreePremSumInput").each(
							function() {
								if (permitAgreePremSumInput
										&& $(this).closest("tr").find(
												".productCb").is(":checked")) {
									$(this).removeAttr("disabled");
									if (spValue == "6") {//特殊退费的话，初始显示特殊退费的原值
										if ($(this).attr("src") == "CIDD_SN1") {
											$(this).attr("disabled", true);
										}
										$(this).val($(this).next().val());
									} else {//协议退保初始显示空
										$(this).val("");
									}
								} else {
									$(this).attr("disabled", true).val("");
								}
							});

					if (permitAgreePremSumInput && spValue == "7") {
						$("#applyBarCodeSpan").show().find("input").attr(
								"disabled", false);
					} else {
						$("#applyBarCodeSpan").val("").hide().find("input")
								.attr("disabled", true);
					}

					if (permitAgreePremSumInput && spValue == "9") {
						$(".branchPercent").show().removeClass("ignore");
					} else {
						$(".branchPercent").hide().removeClass("ignore")
								.addClass("ignore");
					}

					if (permitAgreePremSumInput
							&& (spValue == "8" || spValue == "9")) {
						$(".backbenefit").show().removeClass("ignore");
					} else {
						$(".backbenefit").hide().removeClass("ignore")
								.addClass("ignore");
					}

					$(".productCb").each(
							function() {

								if ($(this).is(":checked") == false) {
									$(this).closest("tr")
											.find(".branchPercent")
											.removeClass("ignore").addClass(
													"ignore");
									$(this).closest("tr").find(".backbenefit")
											.removeClass("ignore").addClass(
													"ignore");
								}
							});
					$("#is_doubt").change(function() {

						if ($("#is_doubt").is(":checked")) {
							$("#doubt").show();
						} else {
							$("#doubt").attr("style", "display:none");
						}
					});

				});

		$("#surrenderToNewApplyBarCode").focusout(function() {
				$("#itemsInputFormSubmitBt").show();
			      var $surrenderToNewApplyBarCode=$("#surrenderToNewApplyBarCode").val();

							if ($surrenderToNewApplyBarCode!= '') {

								$.ajax({
											type : "GET",
											url : "${ctx}acceptance/branch/getTranslateApplyInfo.do",
											data : {
												 "oldApplyBarCode" : ${ acceptance__item_input.policyApplyBarCode },
												 "newApplyBarCode" : $surrenderToNewApplyBarCode
											     },
											cache : false,
											async : false,
											dataType : "json",
											success : function(data) {
												if (data.flag == "N") {
													$.messager.alert("提示",
															data.message);
														$("#newApplyBarCodePrem").val("");
									          $("#newApplyBarCodePremSpan").val("").hide();
									          $("#itemsInputFormSubmitBt").hide();
													    return false;
												}
												else{
													$("#newApplyBarCodePrem").val(data.p_prem);
													$("#newApplyBarCodePremSpan").show();
													
													var policyCashValue=${ acceptance__item_input.policyCashValue };
													
														if ($(".allCb").is(":checked")) {
														
														   if (data.p_old_pri_product_code=="CBED_RR1" && data.p_new_pri_product_code=="UEAN_SN1" && parseFloat(data.p_prem)>policyCashValue)
														   {
																$.messager.alert("提示",
																"转新单保费金额必须小于等于本单退保金额，请确认");
																
																$("#itemsInputFormSubmitBt").hide();
																 return false;
															}
														}
													
													}
											}

										});

							}
							else
								
								{
									$("#newApplyBarCodePrem").val("");
									$("#newApplyBarCodePremSpan").val("").hide();
									}

						});

	};

	posValidateHandler = function() {

		var b = true;
		if ($(".productCb").length == 0) {
			$.messager.alert("提示", "没有险种可以退保！");
			return false;
		}
		if ($(".productCb:checked").length == 0) {
			$.messager.alert("提示", "请至少选择一个险种！");
			return false;
		}
		if ($("#specialFuncSelected").is(":checked")
				&& $("#specialFunc option:selected").val() == "7"
				&& !$(".allCb").is(":checked")) {
			$.messager.alert("提示", "契撤重投必须选择整单退保");
			return false;
		}
		var permitAgreePremSumInput = $("#specialFuncSelected").is(":checked");
		var spValue = $("#specialFunc option:selected").val();
		if (permitAgreePremSumInput && $("#specialFunc").val() == "2"
				&& $("#specialRetreatReason").val() == 0) {
			$.messager.alert("提示", "'特殊件—协议退保（作业品质）'必选退费原因");
			return false;

		}
		if ($("#is_doubt").is(":checked")) {
			if ($(".dbdoubtList:checked").length == 0) {
				$.messager.alert("提示", "如果是可疑交易必须选择可疑交易类型!");
				return false;
			}
		}

		return b;
	};
</script>
</sf:override>



<sf:override name="serviceItemsInput">
	<sfform:formEnv commandName="acceptance__item_input">
		<c:if test="${TRIAL_FLAG ne 'Y'}">
            是否可疑交易：
<input type="checkbox" id="is_doubt" name="douBt" />
			<br />
			<table class="infoDisTab" id="doubt" style="display: none">
				<thead class="odd_column">
					<tr>
						<th width="9%">选择</th>
						<th>可疑交易类型</th>
					</tr>
				</thead>
				<tbody class="even_column">
					<tr>
						<th><input type="checkbox" name="doubtList" value="1304"
							class="dbdoubtList" /></th>
						<th>犹豫期退保时称大额发票丢失的，或者同一投保人短期内多次退保遗失发票总额达到大额的</th>
					</tr>
					<tr>
						<th><input type="checkbox" name="doubtList" value="1308"
							class="dbdoubtList" /></th>
						<th>大额保费保单犹豫期退保、保险合同生效日后短期内退保或者提取现金价值，并要求退保金转入第三方账户或者非缴费账户的</th>
					</tr>
					<tr>
						<th><input type="checkbox" name="doubtList" value="1309"
							class="dbdoubtList" /></th>
						<th>不关注退保可能带来的较大金钱损失，而坚决要求退保，且不能合理解释退保原因的</th>
					</tr>
				</tbody>
			</table>
			<br />
		</c:if>
	退保方式：
	<sfform:checkbox path="allSurrender" cssClass="allCb" />整单退保
	&nbsp;&nbsp;币种：人民币&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<span id="specialRetreatReasonLabel" style="display: none">
		协议退费原因： <select id="specialRetreatReason" name="specialRetreatReason">
			<option value="">--</option>
		</select> </span>
		<br />
		<input type="hidden" id="premPaid"
			value="${acceptance__item_input.premPaid}" />
		<table class="infoDisTab">
			<thead>
				<tr>
					<th>选择</th>
					<th>险种序号</th>
					<th>险种</th>
					<th>是否主险</th>
					<th>险种状态</th>
					<th>被保人</th>
					<th>生效日期</th>
					<th>期交保费</th>
					<th>保额</th>
					<th>现金价值</th>
					<th>协议退保金额</th>
					<c:if test="${TRIAL_FLAG ne 'Y'}">
						<th class="branchPercent" style="display: none">分公司承担比例(%)</th>
						<th class="backbenefit" style="display: none">应追回既得利益</th>
					</c:if>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${acceptance__item_input.productList}" var="item"
					varStatus="status">
					<tr
						class="${status.index mod 2 eq 0 ? 'odd_column' : 'even_column'}">
						<td><c:choose>
							<c:when test="${item.canBeSelectedFlag eq 'Y'}">
								<input type="hidden" class="planFlagHid"
									value="${item.isPrimaryPlan}"/>
								<input type="checkbox" name="productCb${status.index}"
									${item.selected ? "checked='checked'
									" : ''} class="productCb {required:function(){ return $('.productCb:checked').length < 1}}" value="${item.productCode}"/>
								<sfform:hidden path="productList[${status.index}].selected" />
								<input type="hidden" class="lapseReasonH"
									value="${item.lapseReason}" />
							</c:when>
							<c:otherwise>
							无法退保，${item.message}
						</c:otherwise>
						</c:choose></td>
						<td>${item.prodSeq}</td>
						<td>${item.productFullName}（${item.productCode}）</td>
						<td>${item.isPrimaryPlan}</td>
						<td>${item.dutyStatus}</td>
						<td>${item.insuredName}</td>
						<td><fmt:formatDate value="${item.effectDate}"
							pattern="yyyy-MM-dd" /></td>
						<td>${item.periodStandardPrem}</td>
						<td>${item.baseSumIns}</td>
						<td>${item.cashValue}</td>
						<td><c:choose>
							<c:when test="${item.canBeSelectedFlag eq 'Y'}">
								<input type="text" id="agreePremSum${status.index}"
									name="productList[${status.index}].agreePremSum"
									disabled="disabled" size="6"
									class="agreePremSumInput easyui-numberbox {required:true}"
									precision="2" src="${item.productCode}"
									value="${item.selected and not empty item.agreePremSum and item.specialFunc eq '6' ? item.agreePremSum : ''}" />
								<input type="hidden" value="${item.specialSurSum}" />
							</c:when>
							<c:otherwise>
							&nbsp;
						</c:otherwise>
						</c:choose></td>
						<c:if test="${TRIAL_FLAG ne 'Y'}">
							<c:if test="${item.channelType eq '06'}">
								<td class="branchPercent" style="display: none"><input
									name="productList[${status.index}].branchPercent" type="text"
									size=5 value="0" readonly="readonly"
									class="input_text {required:function(){return ${item.canBeSelectedFlag eq 'Y'}}}" /></td>
							</c:if>
							<c:if test="${item.channelType ne '06'}">
								<td class="branchPercent" style="display: none"><input
									name="productList[${status.index}].branchPercent" type="text"
									size=5
									class="input_text {required:function(){return ${item.canBeSelectedFlag eq 'Y'}},number:true,min:0,max:100}" /></td>
							</c:if>
							<td class="backbenefit" style="display: none"><input
								name="productList[${status.index}].backBenefit" type="text"
								size=5
								class="input_text {required:function(){return ${item.canBeSelectedFlag eq 'Y'}},number:true}" /></td>
						</c:if>
					</tr>
				</c:forEach>
			</tbody>
		</table>

		<br />
      保单暂收余额：${acceptance__item_input.policyBalance}&nbsp;&nbsp;
      保单贷款本息和：${acceptance__item_input.loanSum}&nbsp;&nbsp;
      保单自垫本息和：${acceptance__item_input.aplSum}&nbsp;&nbsp;
      <br />
      退保原因：
      <sfform:select path="surrenderCause"
			items="${CodeTable.posTableMap.POS_SURRENDER_CAUSE_CODE}"
			itemLabel="description" itemValue="code"></sfform:select>

		<c:if test="${TRIAL_FLAG ne 'Y'}">
			<span id="applyBarCodeSpan">&nbsp;&nbsp;新投保单号：<input
				type="text" name="applyBarCode" class="{required:true}" /></span>
		</c:if>


		<c:if test="${TRIAL_FLAG ne 'Y'}">
			<c:if
				test="${acceptance__item_input.canDoSurrenderToNewApplyBarCode eq 'Y' }">
				<span id="surrenderToapplyBarCodeSpan">&nbsp;&nbsp;转投新单投保单号：<input
					type="text" id="surrenderToNewApplyBarCode"
					name="surrenderToNewApplyBarCode"/></span>
				<span id="newApplyBarCodePremSpan">&nbsp;&nbsp;新投保单保费:<input    name="newApplyBarCodePrem"   id="newApplyBarCodePrem" readonly="readonly" />
			</span>				
			</c:if>
		</c:if>


	</sfform:formEnv>
</sf:override>
<jsp:include
	page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
