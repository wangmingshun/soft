 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">代审权限管理&gt;&gt;代审权限审批查询</sf:override>
<sf:override name="head">

<script type="text/javascript">
$(function(){
	$("form").validate();
	queryExamPrivsApprovePage();	
	$("[name=startDate],[name=endDate]").click(function(){
		WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}'});
	});
	
	$("#queryBt").click(function(){	
		queryExamPrivsApprovePage();
		return false;
	});
	$("#save").click(function(){
		var approvePk=$("#approvePk").val();
		var approveDesc=$("#approveDesc").val();
		if(approveDesc==''){
			$.messager.alert("提示", "请录入审批意见！", "info");	
			return false;	
		}
		var approveResult=$("#approveResult").val();
		$.ajax({type: "GET",
			url:"${ctx}approve/examPrivsApprove.do?approvePk="+approvePk+"&approveDesc="+encodeURI(approveDesc),
			data:{"approveResult" :approveResult},
			cache: false,
			async: false,
			dataType:"text",
			success:function(data){
				if(data=="Y"){
			        $.messager.alert("提示", "保存成功！", "info", function () {
			        	$("#approvePk").val("");
			        	$("#approveResult").val("");
			        	$("#approveDesc").val("");			        	
			        	$("#approveDescDiv").window("close");
			        	queryExamPrivsApprovePage();
						return false;						
			        });	
					return false;
				}else{	
			        $.messager.alert("提示", "操作失败,请联系管理员！", "info", function () {
			        	$("#approvePk").val("");
			        	$("#approveResult").val("");
			        	$("#approveDesc").val("");
			        	$("#approveDescDiv").window("close");
			        	queryExamPrivsApprovePage();
						return false;						
			        });	
					return false;										
				}
				
			}
		});	
	});	
	function queryExamPrivsApprovePage(){
		var wd = $("#form").width();
		$("#queryExamPrivsApprovePage").datagrid({
			title:"审批列表",
			url:"${ctx}approve/queryExamPrivsApprovePage.do?queryFlag=onlySelf&"+$(form).formSerialize(),
			pagination:true,
			rownumbers:true,
			singleSelect:true,
			striped:true,
			width : wd,
			nowrap : true,
			striped : true,		
			columns:[[{field:"OBJECT_TYPE", title:"类别", align : 'center', width:wd*5*1.2/100},
			         {field:"OBJECT_NO", title:"号码", align : 'center', width:wd*10*1.2/100},
			         {field:"APPROVE_RESULT_DESC", title:"审批结论", align : 'center', width:wd*8*1.2/100},
			         {field:"APPROVE_USER", title:"审批人", align : 'center', width:wd*15*1.2/100},
			         {field:"APPROVE_DATE", title:"审批时间", align : 'center', width:wd*12*1.2/100},			         
			         {field:"APPROVE_DESC", title:"审批意见", align : 'center', width:wd*30*1.2/100},
			         {field:"APPROVE_PK", title:"操作", align : 'center', width:wd*12*1.2/100, formatter:function(value, rowData, rowIndex){
			        	 if(rowData.APPROVE_RESULT=='A'){
				        	 return "<a href=\"javascript:void(0)\" onclick=\"view('"+rowData.OBJECT_TYPE_CODE+"','"+rowData.OBJECT_NO+"')\">查看</a>"+
				        	 "&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"examPrivsApproveInput('"+rowData.APPROVE_PK+"','Y')\">不拉黑</a>"+
				        	 "&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"examPrivsApproveInput('"+rowData.APPROVE_PK+"','N')\">拉黑</a>";		
			        	 }else{
			        		 return "<a href=\"javascript:void(0)\" onclick=\"view('"+rowData.OBJECT_TYPE_CODE+"','"+rowData.OBJECT_NO+"')\">查看</a>";	 	        		 
			        	 }			        	 
		        	 }}			         
			         ]
			]
		});			
	}
});
function view(objectType,objectNo){
	var columns;
	var wd = 680;
	if (objectType=='2'){
		columns=[[{field:"OBJECT_NO", title:"号码", align : 'center', width:wd*7*2/100},
		         {field:"OBJECT_NAME", title:"名称", align : 'center', width:wd*5*2/100},
		         {field:"EMP_IDNO", title:"证件号", align : 'center', width:wd*11*2/100},
		         {field:"DEPT_NAME", title:"所属网点", align : 'center', width:wd*12*2/100},
		         {field:"RECORD_DATE", title:"时间", align : 'center', width:wd*6*2/100},
		         {field:"RECORD_USER", title:"记录人", align : 'center', width:wd*13*2/100},
		         {field:"POLICY_NO", title:"保单号", align : 'center', width:wd*10*2/100},
		         {field:"BEHAVIOR_TYPE", title:"行为描述", align : 'center', width:wd*8*2/100},
		         {field:"DESCRPITON", title:"原因", align : 'center', width:wd*17*2/100}
		         ]];			
	}else{
		columns=[[{field:"OBJECT_NO", title:"号码", align : 'center', width:wd*10*2/100},
			         {field:"OBJECT_NAME", title:"名称", align : 'center', width:wd*12*2/100},
			         {field:"BRANCH_NAME", title:"对应机构", align : 'center', width:wd*10*2/100},
			         {field:"RECORD_DATE", title:"时间", align : 'center', width:wd*6*2/100},
			         {field:"RECORD_USER", title:"记录人", align : 'center', width:wd*13*2/100},
			         {field:"POLICY_NO", title:"保单号", align : 'center', width:wd*10*2/100},
			         {field:"BEHAVIOR_TYPE", title:"行为描述", align : 'center', width:wd*8*2/100},
			         {field:"DESCRPITON", title:"原因", align : 'center', width:wd*20*2/100}
			         ]];		
	}
	$("#badBehavior").datagrid({
		url:"${ctx}approve/queryBadBehaviorPage.do?objectNo="+objectNo+"&objectType="+objectType,
		pagination:true,
		rownumbers:true,
		singleSelect:true,
		striped:true,
		width : wd,
		nowrap : true,
		striped : true,		
		columns:columns
	});	
	$("#badBehaviorWindowDiv").window("open");
}
	function examPrivsApproveInput(approvePk,approveResult){
		$("#approveDescDiv").window("open");
		$("#approvePk").val(approvePk);
		$("#approveResult").val(approveResult);
	}
</script>
</sf:override>
<sf:override name="content">

<form id="form" >

<div id="p1" class="easyui-panel">
<table class="layouttable">
  <tr>
    <td width="25%" class="layouttable_td_label">时间范围：</td>
    <td class="layouttable_td_widget">
      <input type="text" id="startDate" name="startDate" value="${startDate}" class="Wdate {required:false,messages:{required:' 请输入开始日期'}}" />
      至
      <input type="text" id="endDate" name="endDate" value="${endDate}" class="Wdate {required:false,messages:{required:' 请输入结束日期'}}" />
    </td>
  </tr>
     <tr>
		<td width="10%" class="layouttable_td_label">
		对象号码：
		</td>
		<td width="50%" class="layouttable_td_widget">
		          <input type="text" name="objectNo" id="objectNo" />		          
		</td>
		<td>&nbsp;
			        
		</td>
	</tr>
     <tr>
		<td width="10%" class="layouttable_td_label">
		审批结论：
		</td>
		<td width="50%" class="layouttable_td_widget">
				    <select  name="approveResult">					
					    <option value="">全部</option>
					    <option value="A">未操作</option>
					    <option value="Y">白名单</option>
					    <option value="N">黑名单</option>
					</select>
		</td>
    <td align="right">
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryBt">确定</a>            
    </td>
	</tr>	  
</table>
</div>

<br />

	<div class="layout_div_top_spance">
		<table width="100%" id="queryExamPrivsApprovePage" class="easyui-datagrid"></table>
	</div>
<!-- 审批意见录入 -->
<div id="approveDescDiv" class="easyui-window" closed="true" modal="true"
	title="审批意见" style="width: 500px; height: 250px;" collapsible="false" minimizable="false" maximizable="false">
		<div  class="easyui-panel">
		<table class="layouttable">
		  <tr>    
		    <td width="10%" class="layouttable_td_label">请录入审批意见：</td>
		    <td class="layouttable_td_widget">
				<textarea id="approveDesc" name="approveDesc" wrap="off" cols="45" rows="8" value="" ></textarea>		
		    </td> 
				<input type="hidden"  id="approvePk" name="approvePk" value=""/>
				<input type="hidden"  id="approveResult" name="approveResult" value=""/>		       
		  </tr>    
		</table>
		</div>
		<br />
		<div align="center">
		    <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="save" >提交</a>
		</div>
</div>	
<!-- 审批意见录入 -->	
<!-- 不良记录明细DIV -->
<div id="badBehaviorWindowDiv" class="easyui-window" closed="true" modal="true"
	title="不良记录明细" style="width: 700px; height: 300px;" collapsible="false" minimizable="false" maximizable="false">
		<table width="100%" id="badBehavior" class="easyui-datagrid"></table>
</div>	
<!-- 不良记录明细DIV -->	
</form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>