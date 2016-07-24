<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
	<sf:override name="pathString">保单回执管理&gt;&gt;回销录入</sf:override>
	<script type="text/javascript">
	var autoCancel = true;
	$(function() {
		$("form").validate();

		$("#signDate,#signDateRepeat").click(function() {
			WdatePicker({
				skin : 'whyGreen',
				minDate : '${provideDate}',
				maxDate : '${CodeTable.posTableMap.SYSDATE}',
				startDate : '${CodeTable.posTableMap.SYSDATE}',
				isShowToday : false
			});
		});

		$("#inputBt")
				.click(
						function() {
							if ($("#signDate").val() == ""
									|| $("#signDate").val() != $(
											"#signDateRepeat").val()) {
								$.messager.alert("提示", "请正确录入签收日期");
								return false;
							}

							if ($("#signDate").val() < '${provideDate}') {
								$.messager
										.alert("提示",
												"客户签收日期必须大于或等于保单打印日期('${provideDate}')!");
								return false;

							}

							if ($("#signDate").val() > '${CodeTable.posTableMap.SYSDATE}') {
								$.messager
										.alert("提示",
												"客户签收日期必须小于或等于当前日期('${CodeTable.posTableMap.SYSDATE}')!");
								return false;

							}

							if ("${receiptStatus}" == "2"
									&& $("#feedbackFile").val() == "") {
								$.messager.alert("提示", "请上传问题件处理的附件");
								return false;
							}

							autoCancel = false;
							$("form").submit();
							return false;
						});

		$("#problemBt").click(function() {
			if ("${receiptStatus}" == "2") {
				$.messager.alert("提示", "该单已下发问题件");
				return false;
			}

			if ($("#remark").val().length < 5) {
				$.messager.alert("提示", "请填写5个字以上的备注");
				return false;
			}
			$("form").attr("action", "${ctx}receipt/feedback/problem");

			autoCancel = false;
			$("form").submit();
			return false;
		});

		$("#cancelBt").click(function() {
			$("form").attr("action", "${ctx}receipt/feedback/cancel");

			autoCancel = false;
			$("form").submit();
			return false;
		});

		$(".skipBt").click(function() {
			$("form").attr("action", "${ctx}receipt/feedback/skip");
			$("#skipFlag").val($(this).attr("name"));
			autoCancel = false;
			$("form").submit();
			return false;
		});

		/* 作业池剩余量展示 */
		$("#unReceiptCount").click(function(event) {
			var channel = $("#inputChannel").val();
			var warningMsg = null;
			$.ajax({
				type : "GET",
				url : "${ctx}receipt/feedback/queryUnReceiptCount.do",
				data : {
					"channel" : channel
				},
				cache : false,
				async : false,
				dataType : "text",
				success : function(data) {
					warningMsg = data;
				}
			});

			if (warningMsg) {

				$.messager.alert("提示信息", "个人名下及部门作业池剩余量分别为："+warningMsg+"(件)");
				
				return false;
			}

			return false;
		});

	});

	//如果中途离开，将任务处理中的标志取消
	window.onunload = function() {
		if (autoCancel) {
			$.post("${ctx}receipt/feedback/autoCancel", {
				policyNo : "${policyNo}",
				receiptStatus : "${receiptStatus}"
			}, function(data) {
			}, "text");
		}
	}
</script>
</sf:override>
<sf:override name="content">

	<form action="${ctx}receipt/feedback/input" name="mainform"
		enctype="multipart/form-data" method="post"><input
		type="hidden" name="receiptStatus" value="${receiptStatus}" /> <input
		type="hidden" name="skipFlag" id="skipFlag" /> <input type="hidden"
		name="channel" id="inputChannel" value="${channelChosed}" />

	<div style="height: 380px; overflow: scroll; width: auto"><img
		alt="查询签名影像图片失败" src="${signJpgUrl}" /></div>
	<table class="layouttable" width="100%">
		<tr>
			<td class="layouttable_td_label">保单号：</td>
			<td class="layouttable_td_widget">${policyNo} <input
				type="hidden" name="policyNo" value="${policyNo}" /></td>
		</tr>
		<tr>
			<td class="layouttable_td_label">客户签收日期：</td>
			<td class="layouttable_td_widget">
				<c:choose>
			        <c:when test="${(not empty client_sign_date) and (sign_flag eq 'E')}">
			          <input type="text" value="${client_sign_date}"  name="signDate" id="signDate" disabled="disabled"  />
			          <input type="hidden" value="${client_sign_date}" name="signDate" id="signDate"/>
			          <input type="hidden" name="signDateRepeat" id="signDateRepeat" value="${client_sign_date}" /> 
			        </c:when>
			        <c:otherwise>
			          <input type="text" name="signDate" id="signDate" class="Wdate" /> 再输入一次：
			          <input type="text" id="signDateRepeat" class="Wdate" onpaste="return false"
							ondragenter="return false" /> （下发问题件无需录入此日期）
			        </c:otherwise>
		      	</c:choose>
		     </td>
		</tr>
		<tr>
			<td class="layouttable_td_label">附件：</td>
			<td class="layouttable_td_widget"><input type="file"
				name="feedbackFile" id="feedbackFile" /></td>
		</tr>
		<tr>
			<td class="layouttable_td_label_multiple">备注：</td>
			<td class="layouttable_td_widget"><textarea name="remark"
				wrap="off" cols="45" id="remark" class="{byteRangeLength:[0,1000]}"></textarea>
			</td>
		</tr>
	</table>
	<div align="right"><a href="javascript:void(0)"
		onclick="window.open('${ctx}include/getImageByBarcodeNo.do?barcodeNo=${policyApplyBarcode}','apUwBarCode','resizable=yes,height=700px,width=600px');">查看投保单影像</a>
	<a class="easyui-linkbutton" href="javascript:void(0)"
		iconCls="icon-back" id="cancelBt">返回</a> <a class="easyui-linkbutton"
		href="javascript:void(0)" id="unReceiptCount">查询未回销单量</a> <a
		class="easyui-linkbutton skipBt" href="javascript:void(0)"
		iconCls="icon-redo" name="N">下一单</a> <a class="easyui-linkbutton"
		href="javascript:void(0)" iconCls="icon-edit" id="problemBt">下发问题件</a>
	<a class="easyui-linkbutton" href="javascript:void(0)"
		iconCls="icon-ok" id="inputBt">回销</a></div>


	</form>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosSimpleImageViewLayout.jsp"></jsp:include>