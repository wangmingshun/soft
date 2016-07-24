 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">保全质检&gt;&gt;结果录入列表</sf:override>
<sf:override name="head">

<script type="text/javascript">
  $(function(){
	  $("#approveTable").datagrid({
			title:"待质检结果录入列表",
			url:"${ctx}others/poszj/waitlist",
			pagination:true,
			rownumbers:true,
			singleSelect:true,
			striped:true,
			columns:[[{field:"POS_NO", title:"保全号", width:140},
				         {field:"POLICY_NO", title:"保单号",formatter:function(value, rowData, rowIndex){
				        	 return "<a href=\"${GQ_URL}SL_GQS/GQQuery.do?type=policyInfo&policy_no="+rowData.POLICY_NO+"&isShowBackFlag=N"
				        			+ "\"  target=\"_blank\" >"+rowData.POLICY_NO+"</a>";
				         }, width:140},
				         {field:"SERVICE_ITEM", title:"保全项目", width:150},
				         {field:"CLIENT_NAME", title:"客户姓名", width:140},
				         {field:"EFFECTIVE_DATE", title:"保全完成时间",formatter:function(value, rowData, rowIndex){
				        	 	return $.format.date(new Date(value), "yyyy-MM-dd HH:mm:ss");}, width:140},
				         {field:"operation", title:"操作", formatter:function(value, rowData, rowIndex){
				        	 return "<a href=\"${ctx}others/poszj/queryInfor.do?take=N&posNo=" +rowData.POS_NO + "\">质检</a>";
				         }, width:60}
				         ]]
		});
		
  });

</script>
</sf:override>
<sf:override name="content">
	<table width="100%" id="approveTable" class="easyui-datagrid"></table>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>