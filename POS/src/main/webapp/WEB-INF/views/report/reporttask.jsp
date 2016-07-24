 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单报表&gt;&gt;清单报表任务查询</sf:override>
<sf:override name="head">

<script type="text/javascript">
$(function(){
	$("form").validate();
	queryReportTask();	
	$("[name=startDate],[name=endDate]").click(function(){
		WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}'});
	});
	
	$("#queryBt").click(function(){
		queryReportTask();
		return false;
	});


	
});

//分页查询
function queryReportTask(){
	var wd = $("#form").width();
	var startDate=$("#startDate").val();
	var endDate=$("#endDate").val();
	$("#reportTask").datagrid({
		title:"已提交报表任务列表",
		url:"${ctx}report/queryReportTaskPage.do?"+$(form).formSerialize(),
		pagination:true,
		rownumbers:true,
		singleSelect:true,
		striped:true,
		width : wd,
		nowrap : true,
		striped : true,		
		columns:[[{field:"USER_ID", title:"提交人", align : 'center', width:wd*20/100},
		         {field:"DESCRIPTION", title:"报表类型", align : 'center', width:wd*15/100},
		         {field:"PROC_STATUS", title:"处理状态", align : 'center', width:wd*10/100},
		         {field:"ROW_NUM", title:"数据量", align : 'center', width:wd*10/100},
		         {field:"SUBMIT_TIME", title:"提交时间", align : 'center', width:wd*15/100},
		         {field:"JAVA_END_TIME", title:"完成时间", align : 'center', width:wd*15/100},
		         {field:"IM_FILE_ID", title:"操作", align : 'center', width:wd*10/100, formatter:function(value, rowData, rowIndex){
		        	 if(rowData.IM_FILE_ID==null||rowData.IM_FILE_ID==''){
		        		 return "<a class='easyui-linkbutton' href=\"javascript:void(0)\" onclick=\"updatePosListControlIsValid('"+rowData.LIST_CODE+"','"+rowData.USER_ID+"','"+rowData.SUBMIT_TIME+"')\" iconCls='icon-print'>作废</a>";	 
		        	 }else{
			        	 return "<a class='easyui-linkbutton' href='${ctx}report/downLoadEexelData.do?imFileId="+rowData.IM_FILE_ID+"&userId="+rowData.USER_ID+"&startDate="+startDate +"&endDate="+endDate +"' iconCls='icon-print'>下载</a>"
			        			 +"&nbsp;&nbsp;<a class='easyui-linkbutton' href=\"javascript:void(0)\" onclick=\"updatePosListControlIsValid('"+rowData.LIST_CODE+"','"+rowData.USER_ID+"','"+rowData.SUBMIT_TIME+"')\" iconCls='icon-print'>作废</a>";		        		 
		        	 }			        	 
	        	 }}		         
		         ]
		]
	});			
}

function updatePosListControlIsValid(listCode,userId,submitTime){
	$.messager.confirm('确认作废', '您确认要作废此条记录吗？', function(r){
		if (r){
			$.ajax({type: "GET",
				url:"${ctx}report/updatePosListControlIsValid.do?listCode="+listCode+"&userId="+userId,
				data:{"submitTime" :submitTime},
				cache: false,
				async: false,
				dataType:"text",
				success:function(data){
					if(data=="Y"){
				        $.messager.alert("提示", "操作成功！", "info", function () {
				        	queryReportTask();
							return false;						
				        });	
						return false;
					}else{	
				        $.messager.alert("提示", "操作失败,请联系管理员！", "info", function () {
				        	queryReportTask();
							return false;						
				        });	
						return false;										
					}
					
				}
			});			
		}		
	});
	return false;		
			
}
</script>
</sf:override>
<sf:override name="content">

<form id="form" >

<div id="p1" class="easyui-panel">
<table class="layouttable">
  <tr>
    <td width="25%" class="layouttable_td_label">任务提交时间范围：</td>
    <td class="layouttable_td_widget">
      <input type="text" id="startDate" name="startDate" value="${startDate}" class="Wdate {required:false,messages:{required:' 请输入开始日期'}}" />
      至
      <input type="text" id="endDate" name="endDate" value="${endDate}" class="Wdate {required:false,messages:{required:' 请输入结束日期'}}" />
    </td>
    <td align="right">
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryBt">确定</a>
    </td>
  </tr>
</table>
</div>

<br />
	<div class="layout_div_top_spance">
		<table width="100%" id="reportTask" class="easyui-datagrid"></table>
	</div>
</form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>