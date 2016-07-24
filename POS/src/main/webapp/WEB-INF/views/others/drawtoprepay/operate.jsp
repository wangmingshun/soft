<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
<sf:override name="pathString">实收保费转预缴</sf:override>
<script type="text/javascript">
  $(function(){
	  $("form[name='queryForm']").validate();
	  $("form[name='operateForm']").validate();
	  
	  $("#policyQueryBt").click(function(){
		  $("form[name='queryForm']").submit();
		  return false;
	  });
	  $("#operateSubmitBt").click(function(){
		  if(""!="${policyInfo.POS_NO}"){
			  $.messager.alert("提示","该保单在其交至日后有操作补退费保全项目（${policyInfo.POS_NO}${policyInfo.SERVICE_ITEM}），不能操作保费转出");
			  return false;
		  }
		  if("N"=="${policyInfo.checkFlag}"){
			  $.messager.alert("提示","${policyInfo.message}");
			  return false;
		  }
		  $("form[name='operateForm']").submit();
		  return false;
	  });

  });

</script>
</sf:override>
<sf:override name="content">

<form name="queryForm" action="${ctx}others/drawtoprepay/query">

<div class="easyui-panel">
<table class="layouttable">
  <tr>
    <td class="layouttable_td_label">保单号：</td>
    <td class="layouttable_td_widget">
      <input type="text" name="policyNoValue" value="${policyInfo.POLICY_NO}" class="{required:true}" />
    </td>
    <td align="right">
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="policyQueryBt">查询</a>
    </td>
  </tr>
</table>
</div>

</form>

<form name="operateForm" action="${ctx}others/drawtoprepay/operate">

<br />
<c:if test="${not empty policyInfo }">
<div class="easyui-panel">
    <table class="infoDisTab">
      <tr>
        <th>保单号</th>
        <th>生效日期</th>
        <th>期缴保费合计</th>
        <th>主险缴至日</th>
        <th>转出保费金额</th>
      </tr>
      <tr>
        <td>${policyInfo.POLICY_NO}<input type="hidden" name="policyNo" value="${policyInfo.POLICY_NO}"/></td>
        <td>${policyInfo.EFFECT_DATE}</td>
        <td>${policyInfo.MODAL_TOTAL_PREM}</td>
        <td>
          ${policyInfo.PAY_TO_DATE}
        </td>
        <td>
        	${policyInfo.PREM_SUM}
        	<input type="hidden" name="premSum" value="${policyInfo.PREM_SUM}" />
        </td>
      </tr>
    </table>
</div>
<div align="right">
	<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="operateSubmitBt" name="operateSubmitBt">确定</a>
</div>
</c:if>

</form>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>