<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">回执管理&gt;&gt;待处理任务</sf:override>
<sf:override name="head">
	<script type="text/javascript">
$(function() {
	$("form").validate();
	
	$("#queryBt").click(function(){
		$("#fetchType").val($(this).attr("name"));
		if($("#fetchType").val()=="S" && $.trim($("#policyNo").val())==""){
			$.messager.alert("提示", "请输入保单号");
			return;
		}
		$("form").submit();
		return false;
	});
	$("#mobileEntryTable").datagrid({
		title:"移动端待处理回执列表",
		url:"${ctx}/receipt/mobileList",
		pagination:true,
		rownumbers:true,
		singleSelect:true,
		striped:true,
		columns:[[{field:"CHANNEL_TYPE", title:"渠道", width:70},
		          {field:"POLICY_NO", title:"保单号", width:140},
		         {field:"APPLY_SOURCE_DESC", title:"保单来源", width:150},
		         {field:"CLIENT_NAME", title:"投保人", width:120},
		         {field:"RECEIPT_BUSINESS_SOURCE", title:"回执来源", width:70},
		         {field:"SCAN_TIME", title:"回执扫描时间", width:140},
		         {field:"operation", title:"操作", formatter:function(value, rowData, rowIndex){
		        	 return "<a href=\"${ctx}/receipt/fetch?policyNo=" +rowData.POLICY_NO+ "\">处理</a>";
		        	 }}
		         ]
		]
	});
	$("#traditionalEntryTable").datagrid({
		title:"传统待处理回执列表",
		url:"${ctx}/receipt/notMobileList",
		pagination:true,
		rownumbers:true,
		singleSelect:true,
		striped:true,
		columns:[[{field:"CHANNEL_TYPE", title:"渠道", width:70},
		          {field:"POLICY_NO", title:"保单号", width:140},
		         {field:"APPLY_SOURCE_DESC", title:"保单来源", width:150},
		         {field:"CLIENT_NAME", title:"投保人", width:120},
		         {field:"RECEIPT_BUSINESS_SOURCE", title:"回执来源", width:70},
		         {field:"SCAN_TIME", title:"回执扫描时间", width:140},
		         {field:"operation", title:"操作", formatter:function(value, rowData, rowIndex){
		        	 return "<a href=\"${ctx}/receipt/fetch?policyNo=" +rowData.POLICY_NO+ "\">处理</a>";
		        	 }}
		         ]
		]
	});
	/*渠道  保单号  投保人  保单打印日期  客户签收日期  回执扫描时间  业务员号  业务员姓名  问题件备注 回执来源*/
	$("#problemTable").datagrid({
		title:"待处理问题件列表",
		url:"${ctx}/receipt/problemList",
		pagination:true,
		rownumbers:true,
		singleSelect:true,
		striped:true,
		columns:[[{field:"CHANNEL_TYPE", title:"渠道", width:40},
		          {field:"POLICY_NO", title:"保单号", width:110},
		         {field:"APPLICANT_NAME", title:"投保人", width:50},
		         {field:"PROVIDE_DATE", title:"保单打印日期", width:80},
		         {field:"CLIENT_SIGN_DATE", title:"客户签收日期", width:80},
		         {field:"SCAN_TIME", title:"回执扫描时间", formatter:function(value, rowData, rowIndex){
		        	 	return $.format.date(new Date(value), "yyyy-MM-dd");
		         	}, width:80},
		         {field:"AGENT_NO", title:"业务员号", width:80},
		         {field:"AGENT_NAME", title:"业务员姓名", width:70},
		         {field:"NOTE", title:"问题件备注", width:280},
		         {field:"RECEIPT_BUSINESS_SOURCE", title:"回执来源", width:60},
		         {field:"operation", title:"操作", width:60, formatter:function(value, rowData, rowIndex){
		        	 return "<a href=\"${ctx}/receipt/fetch?policyNo=" +rowData.POLICY_NO+ "\">处理</a>";
		        	 }}
		         ]
		]
	});
	
});

//im提供的这个文件下载链接，可能会把当前页面内容洗刷掉，搞来搞去，只好在新窗口打开
var theWin;
function fileDownWindow(url){
	theWin = window.open(url);
	setTimeout("theWin.close();",8000);
}

</script>
</sf:override>

<sf:override name="content">
	<form action="${ctx}receipt/feedback/fetchOne" name="mainform">
	<input type="hidden" name="fetchType" id="fetchType" />
	<input type="hidden" name="singleFetch" id="singleFetch" />
	<div class="easyui-panel">
	<table class="layouttable">
	  <tr>
	    <td class="layouttable_td_label">保单号：</td>
	    <td class="layouttable_td_widget">
	      <input type="text" name="policyNo" id="policyNo" 
	      	<c:if test="${seniorBizFlag eq 'N'}">onpaste="return false" ondragenter="return false"</c:if> />
	    </td>
	    <td>
	    	<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryBt" name="T">查询</a>
	    </td>
	  </tr>
	</table>
	</div>
	
	<c:if test="${SIGN_FLAG eq 'Y'}">
	<br />
	<div class="easyui-panel" title="保单回执已回销信息">
	<table class="layouttable">
	  <tr>
	    <td class="layouttable_td_label">保单号：</td>
	    <td class="layouttable_td_widget">${POLICY_NO}</td>
	    <td class="layouttable_td_label">发放时间：</td>
	    <td class="layouttable_td_widget">${PROVIDE_DATE}</td>
	    <td class="layouttable_td_label">客户签署日期：</td>
	    <td class="layouttable_td_widget">${CLIENT_SIGN_DATE}</td>
	  </tr>
	  <tr>
	    <td class="layouttable_td_label">回销人：</td>
	    <td class="layouttable_td_widget">${CONFIRM_USER}</td>
	    <td class="layouttable_td_label">回销时间：</td>
	    <td class="layouttable_td_widget">${CONFIRM_DATE}</td>
	    <td class="layouttable_td_label">附件：</td>
	    <td class="layouttable_td_widget">
	        <c:forEach items="${attachs}" var="item">
	            <a href="javascript:fileDownWindow('${item.fileUrl}')">${item.fileName}</a>
	        </c:forEach>
	    </td>
	  </tr>
	</table>
	</div>
	</c:if>
	
	</form>
	<div class="layout_div_top_spance">
	<table width="100%" id="traditionalEntryTable" class="easyui-datagrid"></table>
	</div>
	<div class="layout_div_top_spance">
	<table width="100%" id="mobileEntryTable" class="easyui-datagrid"></table>
	</div>
	<div class="layout_div_top_spance">
	<table width="100%" id="problemTable" class="easyui-datagrid"></table>
	</div>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
