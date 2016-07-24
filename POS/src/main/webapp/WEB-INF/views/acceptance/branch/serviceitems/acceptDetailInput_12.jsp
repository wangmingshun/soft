<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<%-- 年龄性别错误更正 --%>
<sf:override name="headBlock">
	<script type="text/javascript" language="javascript">
	var ignoreSexUnmatch = false; //忽略性别不匹配
	var ignoreBirthdayUnmatch = false; //忽略生日不匹配
	var originBirthday = "<fmt:formatDate value="${acceptance__item_input.originServiceItemsInputDTO.clientInfo.birthday}" pattern="yyyy-MM-dd"/>";
	var originSexCode = "${acceptance__item_input.originServiceItemsInputDTO.clientInfo.sexCode}";
	var originIdNo = "${acceptance__item_input.originServiceItemsInputDTO.clientInfo.idNo}";
	var originIdTypeCode = "${acceptance__item_input.originServiceItemsInputDTO.clientInfo.idTypeCode}";
	$(function() {
		$("#selectAll").change(function() {
			if ($(this).is(":checked")) {
				$(".policy").attr("checked", true);
			} else {
				$(".policy").attr("checked", false);
			}
		});
		$(".policy").change(function() {
			if ($(".policy:checked").length == $(".policy").length) {
				$("#selectAll").attr("checked", true);
			} else {
				$("#selectAll").attr("checked", false);
			}
		});
		$(".idTypeCode").change(
				function() {
					$("#idTypeCode").val(
							$(".idTypeCode option:selected").val() ? $(
									".idTypeCode option:selected").val()
									: originIdTypeCode);
				});
		$(".policy").trigger("change");
		$(".idTypeCode").trigger("change");

		$(".idNo")
				.blur(
						function() {
							var newIdTypeCode = $(".idTypeCode option:selected")
									.val();
							if (newIdTypeCode == 1) {
								var key = $(this).val().substr(
										$(this).val().length - 1,
										$(this).val().length);

								if (key == 'x') {//找到输入是小写字母的ascII码的范围
									$(this).val(
											$(this).val().substr(0,
													$(this).val().length - 1)
													+ 'X');
								}
							}
						});
	});
	posValidateHandler = function() {
		var birthdayAndSexChanged = false;
		var newBirthday = $(".birthday").val();
		var newSexCode = $(".sexCode:checked").val();
		var newIdTypeCode = $(".idTypeCode option:selected").val();
		var newIdNo = $(".idNo").val();

		var oldBirthday = $("#originBirthday").val();
		var oldSexCode = $("#originSexCode").val();
		var oldIdTypeCode = $("#originIdTypeCode").val();
		var oldIdNo = $("#originIdNo").val();

		birthdayAndSexChanged = birthdayAndSexChanged || newBirthday
				&& newBirthday != oldBirthday;
		birthdayAndSexChanged = birthdayAndSexChanged || newSexCode
				&& newSexCode != oldSexCode;

		var $canBeSelectedFlag = $(".canBeSelectedFlag");

		if (!birthdayAndSexChanged) {
			if ($($canBeSelectedFlag[0]).val() == "N") {
				if ($canBeSelectedFlag.filter("[value='Y']").length > 0) {
					$.messager.alert("提示", "入口保单客户层及保单层年龄性别均未发生变化，请选择需要进行变更的保单"
							+ $($canBeSelectedFlag.filter("[value='Y']")[0])
									.siblings(".policyNo").val()
							+ "做为入口保单进行保全受理");
					return false;
				} else {
					$.messager
							.alert("提示",
									"保单客户层及保单层年龄性别均未发生变化，无需进行变更。如仅变更证件类型及证件号码，请申请客户资料变更。");
					return false;
				}
			} else if ($($canBeSelectedFlag[0]).val() == "Y") {
				var success = true;
				var $leftCanBeSelectedFlag = $canBeSelectedFlag
						.filter(":not(:eq(1))[value='N']");
				$leftCanBeSelectedFlag.each(function(idx, content) {
					if (success) {
						if ($($leftCanBeSelectedFlag[idx]).siblings(
								".policy:checked").length > 0) {
							success = false;
							$.messager.alert("提示", "保单"
									+ $($leftCanBeSelectedFlag[idx]).siblings(
											".policyNo").val()
									+ "客户层及保单层年龄性别均未发生变化，无需进行变更");
							return;
						}
					}
				});
				if (!success) {
					return false;
				}
			}
		}
		//校验身份证号码与性别、生日是否匹配
		var idType = newIdTypeCode ? newIdTypeCode : originIdTypeCode;
		var birthday = newBirthday ? newBirthday : originBirthday;
		var sexCode = newSexCode ? newSexCode : originSexCode;
		var idNo = newIdNo ? newIdNo : originIdNo;
		if (idType == "01") {
			if (!ignoreSexUnmatch && !validateIdNoMatchesSexCode(idNo, sexCode)) {
				$.messager.confirm("提示", "身份证号码与性别不匹配，是否继续？", function(yes) {
					if (yes) {
						ignoreSexUnmatch = true;
						$("form[name=itemsInputForm]").submit();
					}
				});
				return false;
			}
			if (!ignoreBirthdayUnmatch
					&& !validateIdNoMatchesBirthday(idNo, birthday)) {
				$.messager.confirm("提示", "身份证号码与生日不匹配，是否继续？", function(yes) {
					if (yes) {
						ignoreBirthdayUnmatch = true;
						$("form[name=itemsInputForm]").submit();
					}
				});
				return false;
			}

			var key = newIdNo.substr(newIdNo.length - 1, newIdNo.length);

			if (key == 'x') {//找到输入是小写字母的ascII码的范围
				$.messager.alert("提示", "身份证号码最后一位不可录入为小写x。");
				return false;
			}

		}

		newIdTypeCode = $(".idTypeCode option:selected").val();
		if ($('#isLongidnoValidityDate').attr('checked')==true)
		{
		 if($('.idnoValidityDate').val()!='')
			 {
				$.messager.alert("提示", "身份证的有效期勾选为长期时，请不要再录入日期。");
				return false;
			 
			 }
		
		}
		return true;
		
	};
</script>
</sf:override>

<sf:override name="serviceItemsInput">
	<sfform:formEnv commandName="acceptance__item_input">
		<div class="layout_div_top_spance">
		&nbsp;客户号：${acceptance__item_input.clientNo}&nbsp;&nbsp;&nbsp;&nbsp;
		客户姓名：${acceptance__item_input.clientInfo.clientName}</div>
		<div class="layout_div_top_spance">
		<fieldset class="fieldsetdefault" style="padding: 1px;"><legend>客户层信息：</legend>
		<table class="layouttable">
			<tbody>
				<tr>
					<th class="layouttable_td_label">变更前生日：</th>
					<td class="layouttable_td_widget"><fmt:formatDate
						value="${acceptance__item_input.originServiceItemsInputDTO.clientInfo.birthday}"
						pattern="yyyy-MM-dd" /> <input type="hidden" id="originBirthday"
						value="<fmt:formatDate value="${acceptance__item_input.originServiceItemsInputDTO.clientInfo.birthday}" pattern="yyyy-MM-dd"/>" />
					</td>
					<th class="layouttable_td_label">变更前性别：</th>
					<td class="layouttable_td_widget">
					${acceptance__item_input.originServiceItemsInputDTO.clientInfo.sexDesc}
					<input type="hidden" id="originSexCode"
						value="${acceptance__item_input.originServiceItemsInputDTO.clientInfo.sexCode}" />
					</td>
					<th class="layouttable_td_label">变更前证件类型：</th>
					<td class="layouttable_td_widget">
					${acceptance__item_input.originServiceItemsInputDTO.clientInfo.idTypeDesc}
					<input type="hidden" id="originIdTypeCode"
						value="${acceptance__item_input.originServiceItemsInputDTO.clientInfo.idTypeCode}" />
					</td>
					<th class="layouttable_td_label">变更前证件号码：</th>
					<td class="layouttable_td_widget">
					${acceptance__item_input.originServiceItemsInputDTO.clientInfo.idNo}
					<input type="hidden" id="originIdNo"
						value="${acceptance__item_input.originServiceItemsInputDTO.clientInfo.idNo}" />
					</td>
				</tr>
				<tr>
					<th class="layouttable_td_label">变更后生日：</th>
					<td class="layouttable_td_widget"><sfform:input
						path="clientInfoNew.birthday"
						cssClass="input_text birthday ${empty acceptance__item_input.originServiceItemsInputDTO.clientInfo.birthday ? 'required' : ''}"
						cssStyle="width:80px;"
						onfocus="WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}',maxDate:'${CodeTable.posTableMap.SYSDATE}',isShowToday:false});" />
					</td>
					<th class="layouttable_td_label">变更后性别：</th>
					<td class="layouttable_td_widget"><sfform:radiobutton
						path="clientInfoNew.sexCode" value="" label="未选择"
						cssClass="sexCode ${empty acceptance__item_input.originServiceItemsInputDTO.clientInfo.sexCode ? 'required' : ''}" />
					<sfform:radiobuttons path="clientInfoNew.sexCode"
						items="${CodeTable.posTableMap.SEX}" itemLabel="description"
						itemValue="code"
						cssClass="sexCode ${empty acceptance__item_input.originServiceItemsInputDTO.clientInfo.sexCode ? 'required' : ''}" />
					</td>
					<th class="layouttable_td_label">变更后证件类型：</th>
					<td class="layouttable_td_widget"><sfform:select
						path="clientInfoNew.idTypeCode"
						cssClass="idTypeCode ${empty acceptance__item_input.originServiceItemsInputDTO.clientInfo.idTypeCode ? 'required' : ''}">
						<sfform:option value="" label="未选择" />
						<sfform:options items="${CodeTable.posTableMap.ID_TYPE}"
							itemLabel="description" itemValue="code" />unbo
					</sfform:select></td>
					<th class="layouttable_td_label">变更后证件号码：</th>
					<td class="layouttable_td_widget"><sfform:input
						path="clientInfoNew.idNo"
						cssClass="input_text idNo {idNo:{target:'#idTypeCode',value:'01'}${empty acceptance__item_input.originServiceItemsInputDTO.clientInfo.idNo ? ', required:true' : ''}}"
						cssStyle="width:140px;" /></td>
				</tr>
				<tr>
				  	<th class="layouttable_td_label">证件有效期截止日：</th>
					<td class="layouttable_td_widget">
					<sfform:input
						path="clientInfoNew.idnoValidityDate"
						cssClass="input_text idnoValidityDate"
						cssStyle="width:80px;"
						onfocus="WdatePicker({skin:'whyGreen',startDate:'${acceptance__item_input.applyDate}',minDate:'${acceptance__item_input.applyDate}',isShowToday:false});" />
					          长期	<sfform:checkbox path="clientInfoNew.isLongidnoValidityDate"  id="isLongidnoValidityDate" cssClass="isLongidnoValidityDate"/>
					</td>
				</tr>
			</tbody>
		</table>
		</fieldset>
		</div>

		<div class="layout_div_top_spance">
		<fieldset class="fieldsetdefault" style="padding: 1px;"><legend>保单层信息：</legend>
		<table class="infoDisTab">
			<thead>
				<tr>
					<th class="td_widget">全选 <input type="checkbox" id="selectAll" />
					</th>
					<th>保单号</th>
					<th>被保人</th>
					<th>投保人</th>
					<th>生效日期</th>
					<th>交至日</th>
					<th>保单状态</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${acceptance__item_input.policyList}" var="item"
					varStatus="status">
					<tr
						class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
						<td class="td_widget"><c:choose>
							<c:when test="${status.first}">
								<input type="checkbox" disabled="disabled" checked="checked" />
							</c:when>
							<c:when test="${not empty item.verifyResult.ruleInfos}">
								<a href="javascript:void(0)"
									onclick="$('#unpassRuleDetailWindow${status.index}').window('open');return false;">规则检查不通过</a>
								<div id="unpassRuleDetailWindow${status.index}"
									class="easyui-window" title="规则检查不通过详细信息"
									style="width: 500px; height: 300px; padding: 5px; background: #fafafa;"
									closed="true" modal="true" collapsible="false"
									minimizable="false" maximizable="false">
								<div class="easyui-layout" fit="true">
								<div region="center" border="false"
									style="padding: 10px; background: #fff; border: 1px solid #ccc;">
								<ol style="padding: 0; margin: 0 0 0 28px;">
									<c:forEach items="${item.verifyResult.ruleInfos}"
										var="verifyResultItem">
										<li>${verifyResultItem.description}</li>
									</c:forEach>
								</ol>
								</div>
								<div region="south" border="false"
									style="text-align: right; height: 30px; line-height: 30px;">
								<a class="easyui-linkbutton" iconCls="icon-ok"
									href="javascript:void(0)"
									onclick="$('#unpassRuleDetailWindow${status.index}').window('close');return false;">关闭</a>
								</div>
								</div>
								</div>
							</c:when>
							<c:otherwise>
								<sfform:checkbox path="policyList[${status.index}].selected"
									cssClass="policy checkbox" />
							</c:otherwise>
						</c:choose> <input type="hidden" class="canBeSelectedFlag"
							value="${item.canBeSelectedFlag}" /> <input type="hidden"
							class="policyNo" value="${item.policyNo}" /></td>
						<td>${item.policyNo}</td>
						<td>${item.insuredName}</td>
						<td>${item.applicantName}</td>
						<td><fmt:formatDate value="${item.effectDate}"
							pattern="yyyy-MM-dd" /></td>
						<td><fmt:formatDate value="${item.payToDate}"
							pattern="yyyy-MM-dd" /></td>
						<td>${item.dutyStatusDesc}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		</fieldset>
		</div>
	</sfform:formEnv>
</sf:override>
<jsp:include
	page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
