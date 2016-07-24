<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">VIP客户资料查询&gt;&gt;VIP卡号新增</sf:override>
<sf:override name="head">
	<script type="text/javascript">
	posValidateHandler = function() {

		return true;
	};
	$(function() {

		$("#insertBt").click(function() {
			var b = false;
			var $cardNo = $("#cardNo").val();

			var $modifiedReason = $("#modifiedReason").val();

			$.ajax({
				type : "post",
				url : "${ctx}vip/judgeNewCardNo.do",
				async : false,
				data : {
					"cardNo" : $cardNo,
					"reason" : $modifiedReason
				},
				success : function(data) {

					if ( data.alertMessage!=undefined && data.alertMessage != "") {
						$.messager.alert("提示", data.alertMessage);
						b = false;
						return;
					} else {
						b = true;
						return;
					}

				}

			});
			if (b == true) {
				$("#fm").submit();
			}

			return b;
		});

		$("#applyDate").focus(function() {
			WdatePicker({
				skin : 'whyGreen'
			});
		});
		$("#fm").validate();
	});
</script>
</sf:override>
<sf:override name="content">

	<sfform:form commandName="vipApplyDetailDto"
		action="${ctx}vip/cardChangeInsert" id="fm">
		<div class="easyui-panel">
		<table class="layouttable">
			<tr>
				<td class="layouttable_td_label">VIP卡号：<span
					class="requred_font">*</span></td>
				<td class="layouttable_td_widget"><input type="text"
					id="cardNo" class="{required:true}" name="cardNo" size="20" /></td>
			</tr>
			<tr>
				<td class="layouttable_td_label">变更事由：</td>
				<td><sfform:select id="modifiedReason" path="modifiedReason"
					items="${modifiedReasonList}" itemLabel="description"
					itemValue="code" cssClass="input_select" /></td>
			</tr>
			<tr>
				<td class="layouttable_td_label">时间：<span class="requred_font">*</span></td>
				<td class="layouttable_td_widget"><sfform:date id="applyDate"
					path="applyDate"
					class="Wdate {required:true,messages:{required:'请输入日期'}}" /></td>
			</tr>
			<tr>
				<td class="layouttable_td_label">发放人员：</td>
				<td class="layouttable_td_widget"><input type="text"
					class="{required:true}" name="providedUser" size="20" /></td>
			</tr>
			<tr>
				<td></td>
				<td align="right"><a class="easyui-linkbutton"
					href="javascript:void(0)" iconCls="icon-search" id="insertBt">新增</a>
				<a class="easyui-linkbutton" onclick="javascript:history.back();"
					iconCls="icon-search" id="backBt">返回</a></td>
			</tr>
		</table>


		</div>

	</sfform:form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
