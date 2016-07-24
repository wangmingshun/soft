<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">退信数据上传</sf:override>
<sf:override name="head">
	<script type="text/javascript">
	$(function() {
		//$("form[name=upForm]").validate();
		$("form[name=listForm]").validate();

		$("[name=startDate],[name=endDate]").focus(function() {
			WdatePicker({
				skin : 'whyGreen'
			});
		});

		$("#branchChooseBt").click(function() {
			showBranchTree($(this).prev(), "03", "${userBranchCode}");
			return false;
		});

		$(".submitBt").click(function() {
			var file = $("#file").val();
			if (file == '') {
				$.messager.alert("提示", "请选择上传文件！", "info");
				return false;
			} else {
				$.messager.alert("提示", "文件上传中请耐心等待，上传成功后将返回处理结果！", "info");
			}
			$("form[name=" + $(this).attr("id") + "]").submit();
			return false;
		});

	});
</script>

</sf:override>

<sf:override name="content">

	<form action="${ctx}others/undelivery/upload"
		enctype="multipart/form-data" method="post" name="upForm">

	<div class="easyui-panel">
	<table class="layouttable">
		<tr>
			<td class="layouttable_td_label">请选择文件：</td>
			<td class="layouttable_td_widget"><input type="file" name="file"
				class="{required:true}" id="file" /></td>
			<td align="left"><a class="easyui-linkbutton submitBt"
				href="javascript:void(0)" iconCls="icon-search" id="upForm">上传</a> <span
				class="requred_font">*</span>附件展名须为.xls
		</tr>
	</table>
	</div>

	</form>

	<form id="queryForm" action="${ctx}report/new/submit" class="noBlockUI"
		method="post"><br />
	<div class="easyui-panel" title="退信数据清单查询"><input type="hidden"
		name="ibatisSqlId" value=".posReturnNoteList" /> <input type="hidden"
		name="sheetName" value="退信数据清单" />
	<table class="layouttable">
		<tr>
			<td width="10%" class="layouttable_td_label">查询机构：<span
				class="requred_font">*</span></td>
			<td width="50%" class="layouttable_td_widget"><input type="text"
				name="branchCode" class="{required:true}" value="${userBranchCode}" />
			<a href="javascript:void(0)" id="branchChooseBt">选择机构</a></td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td width="10%" class="layouttable_td_label">时间范围：<span
				class="requred_font">*</span></td>
			<td width="50%" class="layouttable_td_widget"><input type="text"
				name="startDate"
				class="Wdate {required:true,messages:{required:' 请输入开始日期'}}" /> 至 <input
				type="text" name="endDate"
				class="Wdate {required:true,messages:{required:' 请输入结束日期'}}" /></td>
			<td>&nbsp;</td>
			<td align="right"><a class='easyui-linkbutton'
				href="javascript:void(0)"
				onclick="$('#queryForm').submit();return false;" iconCls="icon-ok"
				id="sub">确定</a></td>
		</tr>
	</table>
	<hr class="hr_default" />
	</div>
	</form>

	<jsp:include page="/WEB-INF/views/report/share.jsp"></jsp:include>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>