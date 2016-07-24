<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<sf:override name="headBlock">

	<script type="text/javascript">
	$(function() {

		$("#bankChooseBt").click(function() {
			if ($(".chargingMethodInput").val() == '1') {
				return false;
			}
			var $bankCode = $(".bankCodeInput");
			showBankSelectWindow($bankCode, function(code, label) {
				$("#bankDisplay").val(label);
			});
			return false;
		});

		//保单行选
		$(".policyPremCb").change(function() {
			if (this.checked == true) {
				$(this).next().val("Y");
			} else {
				$(this).next().val("N");
			}
		});

		//选择现金的话就没有账户信息
		$(".chargingMethodInput").change(
				function() {
					if ($(this).val() == '1') {
						$("#accountTr :input").not(".btn").val("");
						$("#accountTr :input").attr("disabled", true);
					} else {
						$("#accountTr :input").not("#bankDisplay").removeAttr(
								"disabled");
					}
				});

		//初始化
		$(".policyPremCb").each(function() {
			if ($(this).prev().val() == "${acceptance__item_input.policyNo}") {
				$(this).attr("checked", true);
				$(this).next().val("Y");
				$(this).attr("disabled", true);
			}
		});

	});

	//提交前的验证和操作
	posValidateHandler = function() {
		if ($(".policyPremCb:checked").length < 1) {
			$.messager.alert("提示", "至少需要选中一个保单进行变更");
			return false;
		}
		return true;
	};
</script>

</sf:override>

<sf:override name="serviceItemsInput">

	<sfform:formEnv commandName="acceptance__item_input">

请选择保单续期缴费方式：
<sfform:select path="chargingMethod" cssClass="chargingMethodInput"
			items="${CodeTable.posTableMap.CHARGING_METHOD}"
			itemLabel="description" itemValue="code"></sfform:select>

		<hr class="hr_default" />

请录入转账账户信息：
<table class="infoDisTab">
			<tr>
				<th width="8%">账户户主姓名</th>
				<th width="30%">开户银行</th>
				<c:if test="${'06' eq  acceptance__item_input.policyChannelType}">
					<th width="10%">银行卡类型</th>
				</c:if>
				<th width="20%">银行账号</th>
				<th width="20%">再次录入银行账号</th>
			</tr>
			<tr id="accountTr">
				<td align="center">${acceptance__item_input.applicantName}</td>
				<td><input type="hidden" name="account.bankCode"
					class="bankCodeInput {required:function(){return $('.chargingMethodInput').val()=='2'},messages:'请选择银行'}" />
				<input type="text" id="bankDisplay" size="30" readonly="readonly" />
				<a href="javascript:void(0)" id="bankChooseBt">选择银行</a></td>
				<c:if
					test="${'06' eq  acceptance__item_input.policyChannelType}">
				<td>
					<sfform:select path="account.accountNoType"
						items="${CodeTable.posTableMap.FIN_ACCOUNT_NO_TYPE}"
						itemLabel="description" itemValue="code"></sfform:select></td>
				</td>
				</c:if>
				<td><sfform:input path="account.accountNo" size="30"
					cssClass="accountNoInput {required:true} nopaste"
					onkeyup="value=value.replace(/[^\d]/g,'')" /></td>
				<td><input type="text" size="30" name="accountAgain"
					class="{equalTo:'.accountNoInput'} nopaste" /></td>
			</tr>
		</table>

		<br />

请选择需同时变更的保单：
<table class="infoDisTab">
			<tr>
				<th width="10%">选择/序号</th>
				<th align="left">保单号</th>
			</tr>
			<c:forEach items="${acceptance__item_input.policyPremList}"
				var="item" varStatus="status">
				<c:if test="${status.index%2==0}">
					<tr class="odd_column">
				</c:if>
				<c:if test="${status.index%2!=0}">
					<tr class="even_column">
				</c:if>
				<td><c:choose>
					<c:when test="${empty item.verifyResult.ruleInfos}">
						<sfform:hidden path="policyPremList[${status.index}].policyNo" />
						<input type="checkbox" class="policyPremCb" />${status.index+1}
			    <sfform:hidden path="policyPremList[${status.index}].checked" />
					</c:when>
					<c:otherwise>
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
					</c:otherwise>
				</c:choose></td>
				<td align="left">${item.policyNo}</td>
				</tr>
			</c:forEach>
		</table>

		<jsp:include page="/WEB-INF/views/include/bankSelectWindow.jsp" />

	</sfform:formEnv>
</sf:override>
<jsp:include
	page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>