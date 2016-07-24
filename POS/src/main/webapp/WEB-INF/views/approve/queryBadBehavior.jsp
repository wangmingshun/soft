 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">代审权限管理&gt;&gt;不良记录查询</sf:override>
<sf:override name="head">

<script type="text/javascript">
$(function(){
	$("form").validate();
	$("#objectType1").hide();
	queryBadBehavior();
	$("[name=startDate],[name=endDate]").click(function(){
		WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}'});
	});
	
	$("#queryBt1").click(function(){	
		queryBadBehavior();
		return false;
	});
	$("#queryBt2").click(function(){	
		queryBadBehavior();
		return false;
	});
	$("#approveBt").click(function(){	
		window.location.href = "${ctx}approve/examApprove"; 
		return false;
	});
	$("#CancelBt").click(function(){	
		window.location.href = "${ctx}approve/examCancel"; 
		return false;
	});

	$("#objectType").change(function(){
		var objectType=$("#objectType").val();
		if(objectType=='1'){
			$("#objectType2").show();
			$("#objectType1").hide();			
		}else{
			$("#objectType2").hide();
			$("#objectType1").show();					
		}

	});
	$("#empNo").blur(function(){
		viewPrivsByObjectNoAndObjectType($(this).val());
	});
	$("#deptNo").blur(function(){
		viewPrivsByObjectNoAndObjectType($(this).val());
	});	
	function viewPrivsByObjectNoAndObjectType(objectNo){
		if(objectNo==null||objectNo==''){
			return false;			
		}
		var objectType=$("#objectType").val();
		$.ajax({type: "GET",
			url:"${ctx}approve/viewPrivsByObjectNoAndObjectType.do?needCheckPrivs=N&objectType="+objectType,
			data:{"objectNo" :objectNo},
			cache: false,
			async: false,
			dataType:"json",
			success:function(data){
				if(data.privsTip==""||data.privsTip==null){
					$.messager.alert("提示", "获取权限信息失败！");		
					return false;
				}else{
					if(objectType=='1'){
						$("#privsDes1").html(data.privsTip);		
						return false;
					}
					if(objectType=='2'){
						$("#privsDes2").html(data.privsTip);		
						return false;
					}
					return false;
				}
			}
		});			
	}	
	function queryBadBehavior(){
		var objectType = $("#objectType").val(); 
		var columns;
		var wd = $("#form").width();
		if (objectType=='2'){
			columns=[[{field:"OBJECT_NO", title:"号码", align : 'center', width:wd*10*1.2/100},
			         {field:"OBJECT_NAME", title:"名称", align : 'center', width:wd*5*1.2/100},
			         {field:"EMP_IDNO", title:"证件号", align : 'center', width:wd*10*1.2/100},
			         {field:"DEPT_NAME", title:"所属网点", align : 'center', width:wd*12*1.2/100},
			         {field:"RECORD_DATE", title:"时间", align : 'center', width:wd*8*1.2/100},
			         {field:"RECORD_USER", title:"记录人", align : 'center', width:wd*15*1.2/100},
			         {field:"POLICY_NO", title:"保单号", align : 'center', width:wd*12*1.2/100},
			         {field:"BEHAVIOR_TYPE", title:"行为描述", align : 'center', width:wd*8*1.2/100},
			         {field:"DESCRPITON", title:"原因", align : 'center', width:wd*15*1.2/100}
			         ]];			
		}else{
			columns=[[{field:"OBJECT_NO", title:"号码", align : 'center', width:wd*10*1.2/100},
				         {field:"OBJECT_NAME", title:"名称", align : 'center', width:wd*12*1.2/100},
				         {field:"BRANCH_NAME", title:"对应机构", align : 'center', width:wd*12*1.2/100},
				         {field:"RECORD_DATE", title:"时间", align : 'center', width:wd*8*1.2/100},
				         {field:"RECORD_USER", title:"记录人", align : 'center', width:wd*15*1.2/100},
				         {field:"POLICY_NO", title:"保单号", align : 'center', width:wd*12*1.2/100},
				         {field:"BEHAVIOR_TYPE", title:"行为描述", align : 'center', width:wd*8*1.2/100},
				         {field:"DESCRPITON", title:"原因", align : 'center', width:wd*15*1.2/100}
				         ]];		
		}
		$("#badBehavior").datagrid({
			url:"${ctx}approve/queryBadBehaviorPage.do?"+$(form).formSerialize(),
			pagination:true,
			rownumbers:true,
			singleSelect:true,
			striped:true,
			width : wd,
			nowrap : true,
			striped : true,		
			columns:columns
		});			
	}
});

</script>
</sf:override>
<sf:override name="content">

<form id="form" >

<div id="p1" class="easyui-panel">
<table class="layouttable">
	  <tr>
	    <td width="10%" class="layouttable_td_label">类别：</td>
	    <td class="layouttable_td_widget">
		    <select id="objectType" name="objectType">					
			    <option value="1">网点</option>
			    <option value="2">银代经理</option>
			</select>
	    </td>
	  </tr>
</table>
</div>
	  <div id="objectType1">
	  <table class="layouttable">
		  <tr>
				<td width="10%" class="layouttable_td_label">
				工号：
				</td>
				<td width="80%" class="layouttable_td_widget">
				   <input type="text" name="empNo" id="empNo"/>
				   <span style="color: red;font-size:15px;font-weight:bold;" id="privsDes2">
				</td>
	         </td>				
		  </tr>
		  <tr>				
				<td width="10%" class="layouttable_td_label">
				姓名：
				</td>
				<td width="80%" class="layouttable_td_widget">
				   <input type="text" name="empName" id="empName"/>
				</td>		
		  </tr>
		  <tr>
				<td width="10%" class="layouttable_td_label">
				证件类型：
				</td>
				<td class="layouttable_td_widget"><select
					name="posInfoDTO.idType" id="idTypeCode">
					<c:forEach items="${CodeTable.posTableMap.ID_TYPE}" var="item">
						<option value="${item.code}"
							<c:if test="${posInfo.ID_TYPE eq item.code}">selected</c:if>>${item.description}</option>
					</c:forEach>
				</select></td>
		  </tr>
		  <tr>				
				<td width="10%" class="layouttable_td_label">
				证件号码：
				</td>
				<td width="80%" class="layouttable_td_widget">
				   <input type="text" name="empIdNo" id="empIdNo"/>
				</td>
    <td align="right">
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryBt1">确定</a>         
    </td>						
		  </tr> 
	</table>		  
	</div>
	  <div id="objectType2">
	  <table class="layouttable">
		  <tr>
				<td width="10%" class="layouttable_td_label">
				网点代码：
				</td>
				<td width="80%" class="layouttable_td_widget">
				   <input type="text" name="deptNo" id="deptNo" />
				<span style="color: red;font-size:15px;font-weight:bold;" id="privsDes1">				   
				</td>
	         </td>					
		  </tr>
		  <tr>				
				<td width="10%" class="layouttable_td_label">
				网点名称：
				</td>
				<td width="80%" class="layouttable_td_widget">
				   <input type="text" name="deptName" id="deptName" />
				</td>	
    <td align="right">
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryBt2">确定</a>
	<!--<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="insertBadBehaviorBt">新增不良记录</a> -->
          
    </td>					
		  </tr>	
	</table>		  
	</div>	
<br />
	<div class="layout_div_top_spance">
		<table width="100%" id="badBehavior" class="easyui-datagrid"></table>
	</div>

</form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>