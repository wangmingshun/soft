<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">信函管理&gt;&gt;信函问题件处理待办任务</sf:override>
<sf:override name="head">
<script type="text/javascript">
$(function() {
	$("#problemTable").datagrid({
		title:"信函问题件处理待办任务",
		url:"${ctx}others/problemList",
		pagination:true,
		rownumbers:true,
		singleSelect:true,
		striped:true,
		columns:[[ 
				 {field:"problemItemNo", title:"问题件号", width:100},
				 {field:"posNo", title:"信函类型", width:100},
		         {field:"policyNo", title:"保单号", width:120, formatter:function(value, rowData, rowIndex){return rowData.posInfo.policyNo;}},
		         {field:"barcodeNo", title:"条形码", width:120, formatter:function(value, rowData, rowIndex){return rowData.posInfo.barcodeNo;}},
		         {field:"submitter", title:"下发人", width:140},
		         {field:"submitDate", title:"下发时间", formatter:function(value, rowData, rowIndex){
		        	 	return $.format.date(new Date(value), "yyyy-MM-dd");
		         	}, width:80},
		         {field:"problemStatusDesc", title:"问题件状态", width:90},
		         {field:"acceptor", title:"受理人", width:160, formatter:function(value, rowData, rowIndex){return rowData.posInfo.acceptor;}},
		         {field:"problemItemTypeDesc", title:"问题件类型", width:70},
		         {field:"operation", title:"操作", width:50, formatter:function(value, rowData, rowIndex){
		        	 return "<a href=\"${ctx}others/problem.do?barcodeNo=" + rowData.posInfo.barcodeNo + "&policyNo="+rowData.posInfo.policyNo+"&problemItemNo="+rowData.problemItemNo+"\">查看</a>";
		        	 }}
		         ]
		]
	})
});
</script>
</sf:override>

<sf:override name="content">
	<table width="100%" id="acceptEntryTable" class="easyui-datagrid"></table>
	<div class="layout_div_top_spance">
		<table width="100%" id="problemTable" class="easyui-datagrid"></table>
	</div>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
