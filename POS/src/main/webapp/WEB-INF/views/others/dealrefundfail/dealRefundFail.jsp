 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">处理电商退费失败件查询</sf:override>
<sf:override name="head">

<script type="text/javascript">
$(function(){
	$("form").validate();
	queryDealRefundFailPage();	
	$("[name=startDate],[name=endDate]").click(function(){
		WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}'});
	});
	
	$("#queryBt").click(function(){	
		queryDealRefundFailPage();
		return false;
	});
	/* 银行选择链接处理 */
	$(".bankSelectLink").click(function(){
		var $bankCode = $(this).siblings("#bankCode");
		showBankSelectWindow($bankCode, function(code, label){
			$bankCode.siblings("div").remove();
			$bankCode.closest("td").append($("<div class='left' id='bankName'/>").html(label));
		});
		return false;
	});	
	$("#save").click(function(){
		var posNo=$("#posNo").val();
		var bankCode=$("#bankCode").val();
		var problemItemNo=$("#problemItemNo").val();
		if(bankCode==''){
			$.messager.alert("提示", "请选择开户银行！", "info");	
			return false;	
		}
		$.ajax({type: "GET",
			url:"${ctx}pub/others/addBank.do?posNo="+posNo+"&problemItemNo="+problemItemNo,
			data:{"bankCode" :bankCode},
			cache: false,
			async: false,
			dataType:"text",
			success:function(data){
				if(data=="Y"){
			        $.messager.alert("提示", "操作成功！", "info", function () {		        	
			        	$("#addBankDiv").window("close");
			        	queryDealRefundFailPage();
						return false;						
			        });	
					return false;
				}else{	
			        $.messager.alert("提示", "操作失败,请联系管理员！", "info", function () {
			        	$("#addBankDiv").window("close");
			        	queryDealRefundFailPage();
						return false;						
			        });	
					return false;										
				}
				
			}
		});	
	});	

});

	function addBank(posNo,policyNo,applySourceDesc,serviceItemsDescription,premSum,transferFailReason,problemItemNo){
		$("#addBankDiv").window("open");
		$("#bankName").remove();
		$("#bankCode").val("");
		$("#posNo").val(posNo);
		$("#problemItemNo").val(problemItemNo);
		$("#policyNoView").html(policyNo);
		$("#applySourceDesc").html(applySourceDesc);
		$("#serviceItemsDescription").html(serviceItemsDescription);
		$("#premSum").html(premSum);
		$("#transferFailReason").html(transferFailReason);
	}
	//退费问题件查询
	function queryDealRefundFailPage(){
	var wd = $("#form").width();
	$("#queryDealRefundFailPage").datagrid({
		title:"电商退费失败件列表",
		url:"${ctx}pub/others/queryDealRefundFailPage.do?"+$(form).formSerialize(),
		pagination:true,
		rownumbers:true,
		singleSelect:true,
		striped:true,
		width : wd,
		nowrap : true,
		striped : true,		
		columns:[[{field:"posNo", title:"保全号", align : 'center', width:wd*10*1.2/100},
		         {field:"orderNo", title:"订单号", align : 'center', width:wd*7*1.2/100},
		         {field:"policyNo", title:"保单号", align : 'center', width:wd*8*1.2/100},
		         {field:"applySourceDesc", title:"保单来源", align : 'center', width:wd*5*1.2/100},
		         {field:"serviceItemsDescription", title:"保全项目", align : 'center', width:wd*8*1.2/100},			         
		         {field:"acceptDate", title:"受理时间", align : 'center', width:wd*10*1.2/100},
		         {field:"premSum", title:"退费金额", align : 'center', width:wd*5*1.2/100},
		         {field:"transferFailReason", title:"转账失败原因", align : 'center', width:wd*20*1.2/100},
		         {field:" ", title:"操作", align : 'center', width:wd*12*1.2/100, formatter:function(value, rowData, rowIndex){
		        	 return "<a href=\"javascript:void(0)\" onclick=\"unsuspendBankAccount('"+rowData.posNo+"','"+rowData.problemItemNo+"')\">取消转账暂停</a>"+
		        	 "&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"addBank('"+rowData.posNo+"','"+rowData.policyNo+"','"+rowData.applySourceDesc+"','"+rowData.serviceItemsDescription+"','"+rowData.premSum+"','"+rowData.transferFailReason+"','"+rowData.problemItemNo+"')\">补充支行</a>";		 	        		 		        	 
	        	 }}			         
		         ]
		]
	});			
	}

	//取消转账暂停
	function unsuspendBankAccount(posNo,problemItemNo){
		$.ajax({type: "GET",
			url:"${ctx}pub/others/unsuspendBankAccount.do?posNo="+posNo,
			data:{"problemItemNo" :problemItemNo},
			cache: false,
			async: false,
			dataType:"text",
			success:function(data){
				if(data=="Y"){
			        $.messager.alert("提示", "操作成功！", "info", function () {
			        	queryDealRefundFailPage();
						return false;						
			        });	
					return false;
				}else{	
			        $.messager.alert("提示", "操作失败,请联系管理员！", "info", function () {
			        	queryDealRefundFailPage();
						return false;						
			        });	
					return false;										
				}
				
			}
		});	
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
		保单号：
		</td>
		<td width="50%" class="layouttable_td_widget">
		          <input type="text" name="policyNo" id="policyNo" />		          
		</td>
	    <td align="right">
	      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryBt">确定</a>            
	    </td>
	</tr>  
</table>
</div>

<br />

	<div class="layout_div_top_spance">
		<table width="100%" id="queryDealRefundFailPage" class="easyui-datagrid"></table>
	</div>
<!-- 补充支行 -->
<div id="addBankDiv" class="easyui-window" closed="true" modal="true"
	title="补充支行" style="width: 500px; height: 250px;" collapsible="false" minimizable="false" maximizable="false">
		<div  class="easyui-panel">
		<table class="layouttable">
		  <tr>    
		    <td class="layouttable_td_label" width="20%" >保单号：</td>
		    <td class="layouttable_td_widget" id="policyNoView" >
		    </td> 	       
		  </tr> 
		  <tr>    
		    <td class="layouttable_td_label" width="20%">保单来源：</td>
		    <td class="layouttable_td_widget" id="applySourceDesc">
		    </td> 	       
		  </tr> 
		  <tr>    
		    <td class="layouttable_td_label" width="20%">保全项目：</td>
		    <td class="layouttable_td_widget" id="serviceItemsDescription">
		    </td> 	       
		  </tr> 
		  <tr>    
		    <td class="layouttable_td_label" width="20%">退费金额：</td>
		    <td class="layouttable_td_widget" id="premSum">
		    </td> 	       
		  </tr> 
		  <tr>    
		    <td class="layouttable_td_label" width="20%">转账失败原因：</td>
		    <td class="layouttable_td_widget" id="transferFailReason"></td> 	       
		  </tr> 
		  <tr> 
		  	<td class="layouttable_td_label" width="20%">开户银行：<span
				class="requred_font">*</span></td>   
			<td class="layouttable_td_widget_1"> <input type="text" name="bankCode" id="bankCode" readonly/><a href="javascript:void(0);"
				class="bankSelectLink">选择银行</a></td>	
				<input type="hidden" name="posNo" id="posNo" value=""/>		
				<input type="hidden" name="problemItemNo" id="problemItemNo" value=""/>		 	       
		  </tr> 		  
	  		  		  		  		     
		</table>
		</div>
		<br />
		<div align="center">
		    <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="save" >提交</a>
		</div>
</div>	
<!-- 补充支行 -->	
</form>
	<jsp:include page="/WEB-INF/views/include/bankSelectWindow.jsp" />
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>