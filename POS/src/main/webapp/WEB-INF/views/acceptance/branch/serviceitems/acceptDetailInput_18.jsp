<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<sf:override name="headBlock">
	<script type="text/javascript">
	$(function() {

		$("#reSignFileTx").hide();
		$("#otherCause").hide();
		$(".optionInput").hide();

		$("#replaceSignCause").change(function() {
			if ($(this).val() == "其他") {
				$("#otherCause").show();
			} else {
				$("#otherCause").hide();
			}
		});

		function hidejudge() {
			if ($("#reSignRd").attr("checked") == false
					&& $("#reSignRisk").attr("checked") == false) {
				$(".optionInput").hide();
			}
		}

		$("#signChangeRd").click(function() {
			hidejudge();
		});

		$("#reSignRd").click(function() {
			if ($(this).is(':checked')) {
				$(".optionInput").show();
			}
			hidejudge();
		});

		$("#reSignRisk").click(function() {
			if ($(this).is(':checked')) {
				$(".optionInput").show();
			}
			hidejudge();
		});

		$("[name=reSignFileRd]").live("click", function() {
			if ($(this).val() == "1") {
				$("#reSignFileTx").hide();
				$("#reSignFileTx").val("投保单");
			} else {
				$("#reSignFileTx").val("");
				$("#reSignFileTx").show();
			}
		});

		$("[name=changeObj]").click(
				function() {
					if ($(this).val() == "1"
							&& "${acceptance__item_input.clientFlag}" == "2") {
						$.messager.alert("提示", "申请客户为被保人，不能选择投保人");
						$(this).attr("checked", false);
					}
					if ($(this).val() == "2"
							&& "${acceptance__item_input.clientFlag}" == "1") {
						$.messager.alert("提示", "申请客户为投保人，只能选择投保人");
						$(this).attr("checked", false);
					}
				});

		$(':checkbox').click(function() {
			if ($(this).is(':checked')) {
				if (this.id == 'signChangeRd') {
					$('#reSignRd').removeAttr('checked', 'checked');
					hidejudge();
				}
				if (this.id == 'reSignRd') {
					$('#signChangeRd').removeAttr('checked', 'checked');
					hidejudge();
				}
			}
		});

		posValidateHandler = function() {
			var b = true;
			if ($("#signChangeRd").attr("checked") == false
					&& $("#reSignRd").attr("checked") == false
					&& $("#reSignRisk").attr("checked") == false) {
				$.messager.alert("提示", "请至少选择一个变更原因!");
				return false;
			}

			if ($(".optionInput").is(":hidden") == false) {
				if ($("#replaceSignCause").val() == "其他"
						&& $("#otherCause").val() == "") {
					$.messager.alert("提示", "代签原因选择为其他时必须输入具体原因!");
					return false;
				}
				if ($("#reSignFileRdx").attr("checked") == false
						&& $("#reSignFileRdy").attr("checked") == false) {
					$.messager.alert("提示", "签名变更单证必须选择一个!");
					return false;
				}
				if ($("#reSignFileRdy").val() == "2"
						&& $("#reSignFileTx").val() == "") {
					$.messager.alert("提示", "签名变更单证选择为其他时必须输入单证名称!");
					return false;
				}
			}

			return b;
		};

	});
</script>
</sf:override>

<sf:override name="serviceItemsInput">
	<sfform:formEnv name="itemsInputForm"
		commandName="acceptance__item_input">

		<table class="layouttable">
			<tr>
				<td width="15%" class="layouttable_td_label">变更对象：</td>
				<td class="layouttable_td_widget"><sfform:radiobutton
					path="changeObj" value="1" cssClass="{required:true}" />投保人 <!--<sfform:radiobutton
					path="changeObj" value="2" cssClass="{required:true}" />被保险人(或其法定监护人)-->
					被保险人(或其法定监护人):
					<c:forEach var="clientNoInsList" items="${acceptance__item_input.clientNoInsList}" varStatus="status">	
						<sfform:radiobutton path="changeObj" value="2,${clientNoInsList.insuredNo}" cssClass="{required:true}" label="${clientNoInsList.insuredName}"/>
					</c:forEach>
				</td>
			</tr>
			<tr>
				<td class="layouttable_td_label">变更原因：</td>
				<td class="layouttable_td_widget"><input type="checkbox"
					name="changeCauses" id="signChangeRd" value="1" />签名风格变化 <input
					type="checkbox" name="changeCauses" id="reSignRd" value="2" />补签名
				<input type="checkbox" name="changeCauses" id="reSignRisk" value="3" />补抄录风险提示语
				<!--  
    <sfform:radiobutton path="changeCause" id="signChangeRd" value="1" cssClass="{required:true}" />签名风格变化
    <sfform:radiobutton path="changeCause" id="reSignRd" value="2" cssClass="{required:true}" />补签名
    --></td>
			</tr>
			<tr class="optionInput">
				<td class="layouttable_td_label">签名变更单证：</td>
				<td class="layouttable_td_widget"><input type="radio"
					name="reSignFileRd" id="reSignFileRdx" value="1" />投保单 <input
					type="radio" name="reSignFileRd" id="reSignFileRdy" value="2" />其他：<input
					type="text" name="reSignFile" id="reSignFileTx" /></td>
			</tr>
			<tr class="optionInput">
				<td class="layouttable_td_label">代签原因：</td>
				<td class="layouttable_td_widget"><sfform:select
					path="replaceSignCause">
					<c:forEach
						items="${CodeTable.posTableMap.POS_REPLACE_SIGN_CAUSE_CODE}"
						var="item">
						<sfform:option value="${item.description}">${item.description}</sfform:option>
					</c:forEach>
				</sfform:select> <sfform:input path="replaceSignCauseOther" id="otherCause" /></td>
			</tr>
		</table>

	</sfform:formEnv>

</sf:override>
<jsp:include
	page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
