<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
<sf:override name="pathString">生存金红利逐单抽档</sf:override>
<script type="text/javascript">
  $(function(){
	  $("form").validate();
	  
	  //输入保单查询保单生存或红利险种信息
	  $("#policyNo").keydown(function(e,k){
		  if (e.keyCode == 13 || k=="R"){
			  var v = $.trim($("#policyNo").val());
			  if(v==""){
				  $.messager.alert("提示","请输入保单号！");
				  return;
			  }
			 
		  }
	  });
	  
	  $("#dueMonth").click(function(){		 
			var date = "${CodeTable.posTableMap.SYSDATE}";
			var y = parseInt(date.substr(0,4));
			var m = parseInt(date.substr(5,2),10);
			if(m==12){
				y = y+1;
				m = 1;
			}else {
				m = m+1;
			}
		  WdatePicker({skin:'whyGreen',dateFmt:'yyyy-MM',maxDate:y+'-'+m});
	  });
	
	  $("#submitBt").click(function(){
		  $("form").submit();
		  return false;
	  });
	  
	  $("#sampleType").change(function(){
		  if($.trim($("#policyNo").val())!=""){
			  $("#policyNo").trigger("keydown","R");//如果输入保单查了后，又去改抽档类型，就重新查询
		  }
		  if($(this).val()=="1"){
			  $("#dueMonthTr").show();
		  }else{
			  $("#dueMonthTr").hide();
		  }
	  });
	  
  });
	
</script>
</sf:override>

<sf:override name="content">

<form action="${ctx}others/survdiv/sample">

<div class="easyui-panel">
<table class="layouttable">
  <tr>
    <td class="layouttable_td_label">抽档类型：</td>
    <td class="layouttable_td_widget">
      <select name="sampleType" id="sampleType">
        <option value="1">生存金</option>
        <option value="2">红利</option>
      </select>
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label">保单号：</td>
    <td class="layouttable_td_widget">
      <input type="text" id="policyNo" name="policyNo" class="{required:true}" title="输入后请按回车键" />
    </td>
  </tr>
  <tr style="display:none">
    <td class="layouttable_td_label">险种：</td>
    <td class="layouttable_td_widget">
      <select id="productList" name="prodSeq"}">
      </select>
    </td>
  </tr>
  <tr id="dueMonthTr">
    <td class="layouttable_td_label">应领月份：</td>
    <td class="layouttable_td_widget">
      <input type="text" id="dueMonth" name="dueMonth" class="Wdate {required:function(){return $('#sampleType').val()=='1'}}" />
    </td>
  </tr>
</table>
</div>
<div align="right">
	<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="submitBt">确定</a>
</div>

</form>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>