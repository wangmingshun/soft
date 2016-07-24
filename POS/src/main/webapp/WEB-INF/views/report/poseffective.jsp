
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单报表&gt;&gt;保全时效清单</sf:override>
<sf:override name="head">

	<script type="text/javascript">
	$(function() {

		$("[name=startDate],[name=endDate]").focus(function() {
			WdatePicker({
				skin : 'whyGreen'
			});
		});

		$("#branchChooseBt").click(function() {
			showBranchTree($(this).prev(), "03", "${userBranchCode}");
			return false;
		});
	});
	dataValidateSubmit = function() {
		var dateStart = $("#startDate").val();
		var dateEnd = $("#endDate").val();

		if (daysBetween(dateStart, dateEnd) > 200) {
			$.messager.alert("提示", "查询时间范围不能超过200天。");
			return false;
		}

		$('#queryForm').submit();
		return true;
	};
</script>
</sf:override>
<sf:override name="content">
	<form id="queryForm" action="${ctx}report/new/submit" class="noBlockUI"
		method="post"><input type="hidden" name="ibatisSqlId"
		value=".effectiveListQuery" /> <input type="hidden" name="sheetName"
		value="保全时效清单" />
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
			<td width="10%" class="layouttable_td_label">保全生效时间范围：<span
				class="requred_font">*</span></td>
			<td width="50%" class="layouttable_td_widget"><input type="text"
				id="startDate" name="startDate"
				class="Wdate {required:true,messages:{required:' 请输入开始日期'}}" /> 至 <input
				type="text" id="endDate" name="endDate"
				class="Wdate {required:true,messages:{required:' 请输入结束日期'}}" /></td>
			<td align="right">
	          <a  class='easyui-linkbutton' href="javascript:void(0)" onclick="dataValidateSubmit();return false;"  iconCls="icon-ok" id="sub">确定</a>
	         </td>
		</tr>


	</table>
	<hr class="hr_default" />
	</form>
	<jsp:include page="/WEB-INF/views/report/share.jsp"></jsp:include>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>