<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
	<sf:override name="pathString">保全撤销与回退&gt;&gt;结果展示与操作</sf:override>

	<script type="text/javascript">
	$(function() {
		$("form[name=mainForm]").validate();

		$("#bankChooseBt").click(function() {
			var $bankCode = $(".bankCodeInput");
			showBankSelectWindow($bankCode, function(code, label) {
				$("#bankDisplay").val(label);
			});
			return false;
		});

		$("#submitBt").click(function() {
			$("form[name=mainForm]").submit();
			return false;
		});

		//取消，直接回到初始页面
		$("#cancelBt").click(function() {
			$("form[name=cancelForm]").submit();
			return false;
		});

		$("#paymentTypeSel").change(function() {
			if ($(this).val() == "2") {
				$("#accountNoAgain").attr("disabled", false);
			} else {
				$("#accountNoAgain").attr("disabled", true);//disabled掉是为了躲避校验
			}
		});
		$("#paymentTypeSel").trigger("change");

	});
</script>
</sf:override>
<sf:override name="content">

	<form name="mainForm" action="${ctx}acceptance/rollback/submit"
		method="post"><input type="hidden"
		name="applyBatchDTO.clientNo" value="${posInfo.CLIENT_NO}" /> <input
		type="hidden" name="posInfoDTO.policyNo" value="${posInfo.POLICY_NO}" />
	<input type="hidden" name="posInfoDTO.clientNo"
		value="${posInfo.CLIENT_NO}" /> <input type="hidden"
		name="posInfoDTO.acceptStatusCode"
		value="${posInfo.ACCEPT_STATUS_CODE}" />

	<div class="easyui-panel" title="保全回退与撤销结果">
	<table class="layouttable">
		<tr>
			<td class="layouttable_td_label" width="15%">原保全批单号：</td>
			<td class="layouttable_td_widget" width="25%">${posInfo.POS_NO}
			<input type="hidden" name="posInfoDTO.posNo"
				value="${posInfo.POS_NO}" /></td>
			<td class="layouttable_td_label" width="12%">处理时间：</td>
			<td class="layouttable_td_widget" width="20%">${posInfo.UPDATED_DATE}</td>
			<td class="layouttable_td_label" width="11%">处理人：</td>
			<td class="layouttable_td_widget">${posInfo.ACCEPTOR}</td>
		</tr>
		<tr class="list_td">
			<td class="layouttable_td_label_multiple">批文内容：</td>
			<td class="layouttable_td_widget" colspan="5"><pre>${posInfo.processText}</pre>
			</td>
		</tr>
	</table>
	</div>

	<br />

	<c:if test="${posInfo.premFlag eq '1'}">
		<div class="easyui-panel">
		<table class="layouttable" id="bankInfoTb">
			<tr>
				<td class="layouttable_td_label"  >收付方式：</td>
				<td class="layouttable_td_widget" width="32%"><select
					name="posInfoDTO.paymentType" id="paymentTypeSel">
					<c:forEach items="${CodeTable.posTableMap.CHARGING_METHOD}"
						var="item">
						<option value="${item.code}"
							<c:if test="${posInfo.PAYMENT_TYPE eq item.code}">selected</c:if>>${item.description}</option>
					</c:forEach>
				</select></td>

				<td class="layouttable_td_label">账户类型：</td>
				<td class="layouttable_td_widget"><select
					name="posInfoDTO.foreignExchangeType">
					<c:forEach
						items="${CodeTable.posTableMap.FIN_FOREIGN_EXCHANGE_TYPE}"
						var="item">
						<option value="${item.code}"
							<c:if test="${posInfo.FOREIGN_EXCHANGE_TYPE eq item.code}">selected</c:if>>${item.description}</option>
					</c:forEach>
				</select></td>
				<td class="layouttable_td_label">银行卡类型: </td>
				<td class="layouttable_td_widget"><select
					name="posInfoDTO.accountNoType">
					<c:if test="${posInfo.CHANNEL_TYPE eq '06'}">
						<option value='1'>卡</option>
						<option value='2'>存折</option>
						<option value='3'>信用卡</option>					
					</c:if>
					<c:if test="${posInfo.CHANNEL_TYPE ne '06'}">
						<option value='1'>卡</option>
					</c:if>
				</select></td>
			</tr>


			<tr>
				<td class="layouttable_td_label">账户户主姓名：</td>
				<td class="layouttable_td_widget"><input type="text"
					name="posInfoDTO.premName" value="${posInfo.PREM_NAME}"
					class="{required:function(){return $('#paymentTypeSel').val()=='2'}}" />
				</td>
				<td class="layouttable_td_label">证件类型：</td>
				<td class="layouttable_td_widget"><select
					name="posInfoDTO.idType" id="idTypeCode">
					<c:forEach items="${CodeTable.posTableMap.ID_TYPE}" var="item">
						<option value="${item.code}"
							<c:if test="${posInfo.ID_TYPE eq item.code}">selected</c:if>>${item.description}</option>
					</c:forEach>
				</select></td>
				<td class="layouttable_td_label">证件号：</td>
				<td class="layouttable_td_widget"><input type="text"
					name="posInfoDTO.premIdno" value="${posInfo.PREM_IDNO}"
					class="{required:true,idNo:{target:'#idTypeCode',value:'01'}}" />
				</td>
				<td></td>

			</tr>
			<tr>
				<td class="layouttable_td_label">开户银行：</td>
				<td class="layouttable_td_widget"><input type="hidden"
					name="posInfoDTO.bankCode" value="${posInfo.BANK_CODE}"
					class="bankCodeInput {required:function(){return $('#paymentTypeSel').val()=='2'},messages:'请选择银行'}" />
				<input type="text" size="35" id="bankDisplay"
					value="${posInfo.BANK_ABBR}" disabled="disabled" /> <a
					href="javascript:void(0)" id="bankChooseBt">选择银行</a></td>
				<td class="layouttable_td_label">银行账号：</td>
				<td class="layouttable_td_widget"><input type="text"
					name="posInfoDTO.accountNo" value="${posInfo.ACCOUNT_NO}"
					class="nopaste {required:function(){return $('#paymentTypeSel').val()=='2'}}"
					style="width: 155px;" /></td>
				<td class="layouttable_td_label">再次输入账号：</td>
				<td class="layouttable_td_widget"><input type="text"
					id="accountNoAgain"
					class="nopaste {equalTo:'[name=posInfoDTO.accountNo]'}"
					style="width: 155px;" /></td>
			</tr>

		</table>
		</div>
		<br />
	</c:if>

	<div class="easyui-panel">
	<table class="layouttable">
		<tr>
			<td class="layouttable_td_label" width="15%">回退申请书条形码：</td>
			<td class="layouttable_td_widget" width="19%"><input type="text"
				id="applyFilesBarcodeNo" name="applyFilesDTO.barcodeNo"
				class="{required:true,remote:{url:'${ctx}include/isBarcodeNoNotUsed.do',type:'post',data:{barcodeNo:function(){return $('#applyFilesBarcodeNo').val();}}},messages:{remote:'条码校验不正确或已经被使用'}}" />
			<input type="hidden" name="barcodeNoTypeHid"
				class="{required:function(){return $('#applyFilesBarcodeNo').val().length>0&&$('#applyFilesBarcodeNo').val().substr(0,4)!='1216'},messages:{required:'条码类型不正确'}}" />
			</td>
			<td class="layouttable_td_label" width="13%">申请类型：</td>
			<td class="layouttable_td_widget" width="20%"><select
				name="applyBatchDTO.applyTypeCode">
				<c:forEach items="${CodeTable.posTableMap.POS_APPLY_TYPE_CODE}"
					var="item">
					<option value="${item.code}">${item.description}</option>
				</c:forEach>
			</select></td>
			<td class="layouttable_td_label" width="13%">回退原因：</td>
			<td class="layouttable_td_widget"><select name="causeCode">
				<c:forEach items="${CodeTable.posTableMap.POS_ROLLBACK_CAUSE_CODE}"
					var="item">
					<option value="${item.code}">${item.description}</option>
				</c:forEach>
			</select></td>
		</tr>
		<tr>
			<td class="layouttable_td_label_multiple">备注：</td>
			<td><textarea rows="5" cols="34" wrap="off"
				name="applyFilesDTO.remark" class="{byteRangeLength:[0,200]}"></textarea>
			</td>
		</tr>
	</table>
	</div>
	<div align="right"><a class="easyui-linkbutton cancel"
		href="javascript:void(0)" iconCls="icon-cancel" id="cancelBt">取消</a> <a
		class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok"
		id="submitBt">确定</a></div>

	</form>

	<form name="cancelForm" action="${ctx}acceptance/rollback/entry">
	</form>

	<jsp:include page="/WEB-INF/views/include/bankSelectWindow.jsp" />

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>

<script type="text/javascript">
	<c:if test="${not empty RESULT}">
	<c:choose>
	<c:when test="${RESULT.flag eq 'A08'}">
	alert("受理已送审，请等待审批结果");
	</c:when>
	<c:when test="${RESULT.flag eq 'A15'}">
	alert("待收费，受理在收费成功后生效");
	</c:when>
	<c:when test="${RESULT.flag eq 'E01'}">
	alert("受理生效完成，操作结束");
	</c:when>
	<c:otherwise>
	alert("⊙v⊙不太正常的结果：${RESULT.flag}${RESULT.message}");
	</c:otherwise>
	</c:choose>
	$("form[name=cancelForm]").submit();
	</c:if>
</script>