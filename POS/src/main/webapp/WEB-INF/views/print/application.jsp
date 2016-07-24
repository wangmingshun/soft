<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单打印&gt;&gt;打印&gt;&gt;免填单申请书打印</sf:override>
<sf:override name="head">
	<script type="text/javascript" language="javascript">
	$(function() {
		$("#back").click(function() {
			window.location.href = "${ctx}print/entry";
			return false;
		});

		$("#fm").validate();
	});
</script>
</sf:override>

<sf:override name="content">
	<sfform:form id="fm" action="${ctx}print/applicationPrintPreview"
		class="noBlockUI">
		<table class="layouttable">
			<tr>
				<td width="10%" class="layouttable_td_label">批单号： <span
					class="requred_font">*</span></td>
				<td width="50%" class="layouttable_td_widget"><input
					type="text" name="posNo" size="20" value="${param.posNo}"
					class="input_text {required:true}" /></td>
				<td align="right"><a class='easyui-linkbutton'
					href="javascript:void(0)" onclick="$('#fm').submit();return false;"
					iconCls="icon-ok" id="confirm">确定</a> <a class='easyui-linkbutton'
					href="javascript:void(0)" iconCls="icon-back" id="back">返回</a></td>
			</tr>
		</table>
		<hr class="hr_default" />
		<div class="left" style="color: red;">${message}</div>

	</sfform:form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
