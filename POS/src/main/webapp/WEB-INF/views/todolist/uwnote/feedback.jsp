 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">核保函回销</sf:override>
<sf:override name="head">
<script type="text/javascript">
$(function(){
	$("form").validate();

	$("#queryBt").click(function(){
		if($("[name=posNo]").val()==""){
			$.messager.alert("提示","请输入保全批单号！");
			return false;
		}
		$("form").attr("action","${ctx}todolist/uwnoteFeedback/query");
		$("form").submit();
		return false;
	});
	$("#feedbackSubmitBt").click(function(){
		$("form").attr("action","${ctx}todolist/uwnoteFeedback/submit");
		$("form").submit();
		return false;
	});

});

</script>
</sf:override>
<sf:override name="content">

<form>

<div id="p1" class="easyui-panel">
<table class="layouttable">
  <tr>
    <td class="layouttable_td_label">保全批单号：<span class="requred_font">*</span></td>
    <td class="layouttable_td_widget">
      <input type="text" name="posNo" />
    </td>
    <td align="right">
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryBt">查询</a>
    </td>
  </tr>
</table>
</div>

<br />

<c:if test="${not empty posInfo}">
<table class="infoDisTab">
  <tr>
    <th>保全批单号</th>
    <th>保单号</th>
    <th>保全项目</th>
    <th>条形码</th>
    <th>受理人</th>
    <th>受理日期</th>
    <th>已打印次数</th>
  </tr>
  <c:forEach items="${posInfo}" var="item"><!-- 其实只会有一条记录 -->
  <tr class="odd_column">
    <td>${item.POS_NO}<input type="hidden" name="feedbackPosNo" value="${item.POS_NO}" /></td>
    <td>${item.POLICY_NO}</td>
    <td>${item.SERVICE_ITEMS_DESC}<input type="hidden" name="taskType" value="${item.TASK_TYPE}" /></td>
    <td>${item.BARCODE_NO}</td>
    <td>${item.ACCEPTOR}</td>
    <td>${item.ACCEPT_DATE}</td>
    <td>${item.PRINT_TIME}</td>
  </tr>
  </c:forEach>
</table>

<table width="100%">
  <tr>
    <td align="left">
        回销意见：
        <input type="radio" value="Y" name="feedbackResult" class="{required:true}" />同意
        <input type="radio" value="N" name="feedbackResult" class="{required:true}" />不同意
    </td>
    <td align="right">
    	<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="feedbackSubmitBt">确定</a>
    </td>
  </tr>
</table>
</c:if>

</form>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>