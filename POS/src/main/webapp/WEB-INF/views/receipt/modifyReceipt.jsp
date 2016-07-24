 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">回执管理&gt;&gt;回执主索引及扉页号修改</sf:override>
<sf:override name="head">

<script type="text/javascript">
$(function(){
	$("form").validate();
	$("#objectType1").hide();	
	$("#modifyBt1").click(function(){
		//预留校验：只有未回执回销状态的保单可以修改
		var policyNo=$("#policyNo").val();
		var newPageNo=$("#newPageNo").val();
		var oldPageNo=$("#oldPageNo").val();
		if(oldPageNo==null||oldPageNo==''){
			$.messager.alert("提示", "请录入正确保单号！");
			return false;			
		}
		if(newPageNo==null||newPageNo==''){
			$.messager.alert("提示", "请录入新扉页号！");
			return false;			
		}
		$.ajax({type: "GET",
			url:"${ctx}receipt/modifyReceipt/modifyOldPageNo.do?newPageNo="+newPageNo,
			data:{"policyNo" :policyNo},
			cache: false,
			async: false,
			dataType:"text",
			success:function(data){
				if(data=="Y"){
					$.messager.alert("提示", "修改成功！");
					$("#oldPageNo").val(newPageNo);
					return false;
				}else{
					$.messager.alert("提示", "修改失败，请联系管理员！");		
					return false;
				}
			}
		});	
		return false;
	});
	$("#modifyBt2").click(function(){
		//预留校验：只有未回执回销状态的保单可以修改
		var barCodeNo=$("#barCodeNo").val();
		var newIndex=$("#newIndex").val();
		var oldIndex=$("#oldIndex").val();
		if(oldIndex==null||oldIndex==''){
			$.messager.alert("提示", "请录入正确回执条码！");
			return false;			
		}
		if(newIndex==null||newIndex==''){
			$.messager.alert("提示", "请录入新影像主索引！");
			return false;			
		}
		$.ajax({type: "GET",
			url:"${ctx}receipt/modifyReceipt/modifyOldIndex.do?oldIndex="+oldIndex+"&newIndex="+newIndex,
			data:{"barCodeNo" :barCodeNo},
			cache: false,
			async: false,
			dataType:"text",
			success:function(data){
				if(data=="Y"){
					$.messager.alert("提示", "修改成功！");
					$("#oldIndex").val(newIndex);
					return false;
				}else{
					$.messager.alert("提示", "修改失败，请联系管理员！");		
					return false;
				}
			}
		});
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
	$("#policyNo").blur(function(){
		var policyNo=$(this).val();
		if(policyNo==null||policyNo==''){
			return false;			
		}
		$.ajax({type: "GET",
			url:"${ctx}receipt/modifyReceipt/queryOldPressNo.do",
			data:{"policyNo" :policyNo},
			cache: false,
			async: false,
			dataType:"json",
			success:function(data){
				if(data.oldPressNo==""||data.oldPressNo==null){
					$.messager.alert("提示", "查询修改前的扉页号失败！");		
					return false;
				}else{
					$("#oldPageNo").val(data.oldPressNo);		
					return false;
				}
			}
		});	
	});
	$("#barCodeNo").blur(function(){
		var barCodeNo=$(this).val();
		if(barCodeNo==null||barCodeNo==''){
			return false;			
		}
		$.ajax({type: "GET",
			url:"${ctx}receipt/modifyReceipt/queryImageMainIndex.do",
			data:{"barCodeNo" :barCodeNo},
			cache: false,
			async: false,
			dataType:"json",
			success:function(data){
				if(data.oldImageMainIndex==""||data.oldImageMainIndex==null){
					$.messager.alert("提示", "查询修改前的影像主索引失败！");		
					return false;
				}else{
					$("#oldIndex").val(data.oldImageMainIndex);		
					return false;
				}
			}
		});		
	});		
});

</script>
</sf:override>
<sf:override name="content">

<form id="form" >

<div id="p1" class="easyui-panel">
<table class="layouttable">
	  <tr>
	    <td width="20%" class="layouttable_td_label">修改类型：</td>
	    <td class="layouttable_td_widget">
		    <select id="objectType" name="objectType">					
			    <option value="1">影像主索引</option>
			    <option value="2">扉页号</option>
			</select>
	    </td>
	  </tr>
</table>
</div>
	  <div id="objectType1">
	  <table class="layouttable">
		  <tr>
				<td width="20%" class="layouttable_td_label">
				保单号：
				</td>
				<td width="50%" class="layouttable_td_widget">
				   <input type="text" name="policyNo" id="policyNo" />			   
				</td>
	         </td>					
		  </tr>
		  <tr>
				<td width="20%" class="layouttable_td_label">
				修改前的扉页号：
				</td>
				<td width="50%" class="layouttable_td_widget">
				   <input type="text" readonly name="oldPageNo" id="oldPageNo" />			   
				</td>
	         </td>					
		  </tr>		  
		  <tr>				
				<td width="20%" class="layouttable_td_label">
				修改后的扉页号：
				</td>
				<td width="50%" class="layouttable_td_widget">
				   <input type="text" name="newPageNo" id="newPageNo" />
				</td>	
    <td align="right">
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="modifyBt1">修改</a>          
    </td>					
	</tr>	
	</table>		  
	</div>
	  <div id="objectType2">
	  <table class="layouttable">
		  <tr>
				<td width="20%" class="layouttable_td_label">
				回执条码：
				</td>
				<td width="50%" class="layouttable_td_widget">
				   <input type="text" name="barCodeNo" id="barCodeNo"/>
				</td>
	         </td>				
		  </tr>
		  <tr>				
				<td width="20%" class="layouttable_td_label">
				修改前的影像主索引：
				</td>
				<td width="50%" class="layouttable_td_widget">
				   <input type="text" readonly name="oldIndex" id="oldIndex"/>
				</td>		
		  </tr>
		  <tr>				
				<td width="20%" class="layouttable_td_label">
				修改后的影像主索引：
				</td>
				<td width="50%" class="layouttable_td_widget">
				   <input type="text" name="newIndex" id="newIndex"/>
				</td>
    <td align="right">
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="modifyBt2">修改</a>         
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