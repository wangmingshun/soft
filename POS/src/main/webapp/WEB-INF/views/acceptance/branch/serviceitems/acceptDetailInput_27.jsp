<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<!-- 保单补发 -->
<sf:override name="headBlock">
	<script type="text/javascript">
	var vipGrade = "${acceptance__item_input.vipGrade}";
	/**
	 * 设置页面初始状态
	 */

	$(function() {

		var reason = $(".reProvideCause").val();

		$("#specialFuncSelected,#specialFunc")
				.change(
						function() {
							var reason = $(".reProvideCause:checked").val();
							var $specialFunc = $("#specialFunc");
							if ($specialFunc.is(":enabled")
									&& $specialFunc.val() == "5") {
								$(".chargeOption[value='1']").attr("disabled",
										true).attr("checked", false);
								$(".chargeOption[value='2']").attr("disabled",
										false).attr("checked", true);
								$(".reProvideCause[value='4']").attr(
										"disabled", false);

							} else if ((reason == "1" || reason == "3")
									&& vipGrade != "0") {
								$(".chargeOption[value='1']").attr("disabled",
										true).attr("checked", false);
								$(".chargeOption[value='2']").attr("disabled",
										false).attr("checked", true);

								$(".reProvideCause[value='4']").attr(
										"disabled", true)
										.attr("checked", false);

							}

							else

							{
								$(".chargeOption[value='1']").attr("disabled",
										false).attr("checked", true);
								$(".chargeOption[value='2']").attr("disabled",
										true);
								$(".reProvideCause[value='4']").attr(
										"disabled", true)
										.attr("checked", false);
							}
						});

		$(".reProvideCause")
				.change(
						function() {
							var $specialFunc = $("#specialFunc");
							var reason = $(".reProvideCause:checked").val();
							if ((reason == "1" || reason == "3")
									&& vipGrade != "0") {
								$(".chargeOption[value='1']").attr("disabled",
										true).attr("checked", false);
								$(".chargeOption[value='2']").attr("disabled",
										false).attr("checked", true);

							}

							else if ($specialFunc.is(":enabled")
									&& $specialFunc.val() == "5") {

								$(".chargeOption[value='1']").attr("disabled",
										true).attr("checked", false);
								$(".chargeOption[value='2']").attr("disabled",
										false).attr("checked", true);
								$(".reProvideCause[value='4']").attr(
										"disabled", false);
							} else {
								$(".chargeOption[value='1']").attr("disabled",
										false).attr("checked", true);
								$(".chargeOption[value='2']").attr("disabled",
										true);

							}

						});
	});
</script>
</sf:override>

<sf:override name="serviceItemsInput">

	<sfform:formEnv name="itemsInputForm"
		commandName="acceptance__item_input">

		<table class="layouttable">
			<tr>
				<td class="layouttable_td_label" width="15%">请选择补发原因：</td>
				<td class="layouttable_td_widget"><sfform:radiobutton
					path="reProvideCause" value="1" label="投保人补发"
					cssClass="reProvideCause {required:true}" /> <sfform:radiobutton
					path="reProvideCause" value="2" label="代理人补发"
					cssClass="reProvideCause {required:true}" /> <sfform:radiobutton
					path="reProvideCause" value="3" label="污损换发"
					cssClass="reProvideCause {required:true}" /> <sfform:radiobutton
					path="reProvideCause" value="4" label="公司补发"
					cssClass="reProvideCause {required:true}" disabled="true" /></td>
			</tr>
			<tr>
				<td class="layouttable_td_label">是否收取工本费：</td>
				<td class="layouttable_td_widget"><sfform:radiobutton
					path="chargeOption" value="1" label="收取工本费"
					cssClass="chargeOption {required:true}" /> <sfform:radiobutton
					path="chargeOption" value="2" label="免收工本费"
					cssClass="chargeOption {required:true}" disabled="true" /></td>
			</tr>
			<tr>
				<td class="layouttable_td_label">保单领取方式：</td>
				<td class="layouttable_td_widget"><sfform:radiobutton
					path="deliveryType" value="1" label="邮寄" cssClass="{required:true}" />
				<sfform:radiobutton path="deliveryType" value="2"
					label="自行领取（自领、代领）" cssClass="{required:true}" /></td>
			</tr>
			<tr>
				<td class="layouttable_td_label_multiple">补发原因描述：</td>
				<td class="layouttable_td_widget"><sfform:textarea
					path="reProvideCauseDesc" wrap="off" cols="40" rows="3"
					cssClass="multi_text {byteRangeLength:[0,200],required:true}" /></td>
			</tr>
		</table>

	</sfform:formEnv>

</sf:override>
<jsp:include
	page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
