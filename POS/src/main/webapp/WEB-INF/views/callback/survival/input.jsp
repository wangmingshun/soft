<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">生存调查结果录入/修改</sf:override>
<sf:override name="head">
<script type="text/javascript">
$(function(){
	$("#queryBt").click(function(){
		if($.trim($("#policyNoInput").val())==""){
			$.messager.alert("提示","请输入保单号！");
			return false;
		}
		$.post("${ctx}callback/survival/query", 
		      {policyNo:$("#policyNoInput").val()},
              function(data) {
				  if(data){
					  $("#insuredName").text(data.INSURED_NAME);
					  $("#sex").text(data.SEX);
					  $("#birthday").text(data.BIRTHDAY);
					  $("#idType").text(data.ID_TYPE);
					  $("#idNo").text(data.IDNO);
					  $("#deathDate").val(data.DEATH_DATE);
					  $("#policyNo").val(data.POLICY_NO);
					  $("#investState").text(data.INVEST_STATE_DESC);
					  $("#sampleMonth").text(data.SAMPLE_MONTH);
					  $("#submitType").val(data.INVEST_STATE);
					  $("#clientNo").val(data.CLIENT_NO);
				  }else{
					  $.messager.alert("提示","未查询到数据！");
					  $("#insuredName").text("");
					  $("#sex").text("");
					  $("#birthday").text("");
					  $("#idType").text("");
					  $("#idNo").text("");
					  $("#deathDate").val("");
					  $("#policyNo").val("");
					  $("#investState").text("");
					  $("#sampleMonth").text("");
					  $("#submitType").val("");
					  $("#clientNo").val("");
				  }
              }, "json");
	});
	$("#policyNoInput").keydown(function(e){
		if(e.keyCode==13){
			$("#queryBt").trigger("click");
		}
	});
	
	$("#deathDate").click(function(){
		WdatePicker({skin:'whyGreen',maxDate:'${CodeTable.posTableMap.SYSDATE}'});
	});
	
	$("#inputBt, #modifyBt, #newInputBt").click(function(){
		if($("#submitType").val()==""){
			$.messager.alert("提示","请先输入保单号查询！");
			return false;
		}
		if(!$(this).hasClass($("#submitType").val())){
			$.messager.alert("提示","该保单当前调查状态不能进行此操作！");
			return false;
		}
		$("#submitType").val($(this).attr("name"));
		$("form").submit();
		return false;
	});
	
	$("form").validate();
	
});

</script>

</sf:override>

<sf:override name="content">

<form name="queryForm" action="${ctx}callback/survival/inputsubmit">

<div class="easyui-panel">
<table class="layouttable">
 <tbody>
  <tr>
    <td class="layouttable_td_label" width="15%">保单号：</td>
    <td class="layouttable_td_widget">
      <input type="hidden" name="policyNo" id="policyNo" />
      <input type="text" id="policyNoInput" name="policyNoInput" />
      <a id="queryBt" href="javascript:void(0)">查询</a>
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label">被保人姓名：</td>
    <td class="layouttable_td_widget" id="insuredName">
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label">性别：</td>
    <td class="layouttable_td_widget" id="sex">
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label">生日：</td>
    <td class="layouttable_td_widget" id="birthday">
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label">证件类型：</td>
    <td class="layouttable_td_widget" id="idType">
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label">证件号码：</td>
    <td class="layouttable_td_widget" id="idNo">
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label">调查状态：</td>
    <td class="layouttable_td_widget" id="investState">
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label">抽档月份：</td>
    <td class="layouttable_td_widget" id="sampleMonth">
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label">死亡日期：</td>
    <td class="layouttable_td_widget">
      <input type="text" name="deathDate" id="deathDate" class="Wdate" />
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label_multiple" valign="top">备注：</td>
    <td class="layouttable_td_widget">
      <textarea name="remark" wrap="off" cols="22" class="{byteRangeLength:[0,200]}"></textarea>
    </td>
  </tr>
 </tbody>
</table>
</div>
<div align="right">
	<a class="easyui-linkbutton 2" href="javascript:void(0)" iconCls="icon-edit" id="modifyBt" name="2">修改</a>
    <a class="easyui-linkbutton 1" href="javascript:void(0)" iconCls="icon-ok" id="inputBt" name="1">回销</a>
    <a class="easyui-linkbutton 2 3" href="javascript:void(0)" iconCls="icon-add" id="newInputBt" name="3">新录入</a>
    <input type="hidden" name="submitType" id="submitType" />
    <input type="hidden" name="clientNo" id="clientNo" />
</div>

</form>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
