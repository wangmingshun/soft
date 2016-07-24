<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">权限设置&gt;&gt;利率设置</sf:override>
<sf:override name="head">
	<script type="text/javascript">
	
	$(function() {
		$("form").validate();

		$("[name=startDate],[name=endDate]").focus(function() {
			WdatePicker({
				skin : 'whyGreen'
			});
		});

		$("#saveInterest").click(function() {
			$("form[name='rateSetForm']").submit();
			
			return true;

		});

	});
</script>
</sf:override>
<sf:override name="content">
	<form name="rateSetForm" method="post">
	<table class="layouttable">
		<tr>
			<td width="25%" class="layouttable_td_label">利率类型: <span
				style="color: red;">*</span></td>
			<td width="25%" class="layouttable_td_widget"><select
				name="interestTypeRate">
				<option value="7">生存金未满整年领取利率</option>
			</select></td>
		</tr>
		<tr>
			<td width="25%" class="layouttable_td_label">利率单位: <span
				style="color: red;">*</span></td>
			<td width="25%" class="layouttable_td_widget"><select
				name="interestUnit">
				<c:forEach items="${interestUnits}" var="item">
					<option value="${item.CODE}">${item.DESCRIPTION}</option>
				</c:forEach>
			</select></td>

			<td width="25%" class="layouttable_td_label">利率: <span
				style="color: red;">*</span></td>
			<td width="25%" class="layouttable_td_widget"><input type="text"
				name="interestRate"
				class="input_text {required:true,number:true,min:0,max:9.99999}"></input></td>

		</tr>
		<tr>
			<td width="25%" class="layouttable_td_label">产品代码:</td>
			<td width="25%" class="layouttable_td_widget"><input type="text"
				name="productCode"></input></td>
		</tr>
		<tr>
			<td width="25%" class="layouttable_td_label">开始时间: <span
				style="color: red;">*</span></td>
			<td width="25%" class="layouttable_td_widget"><input type="text"
				name="startDate"
				class="Wdate {required:true,messages:{required:' 请输入开始日期'}}" /></td>

			<td width="25%" class="layouttable_td_label">结束时间: <span
				style="color: red;">*</span></td>
			<td width="25%" class="layouttable_td_widget"><input type="text"
				name="endDate"
				class="Wdate {required:true,messages:{required:' 请输入结束日期'}}" /></td>

		</tr>

	</table>

	<div align="center"><a class="easyui-linkbutton"
		id="saveInterest" iconCls="icon-save">保存</a></div>
	</form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
