<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
<sf:override name="pathString">待办任务&gt;&gt;逐单入口</sf:override>
<script type="text/javascript">
$(function(){
	$("form").validate();
	$("#submitBt").click(function(){
		$("form").submit();
	});
});
	
</script>
</sf:override>
<sf:override name="content">

<form action="${ctx}todolist/goToSingleTask" name="mainform">

<div class="easyui-panel">
<table class="layouttable">
  <tr>
    <td class="layouttable_td_label">任务类型：</td>
    <td class="layouttable_td_widget">
    	<select name="type">
        	<option value="1">受理件</option>
            <option value="2">审批件</option>
            <option value="3">问题件</option>
        </select>
    </td>
    <td class="layouttable_td_label">保全批单号：</td>
    <td class="layouttable_td_widget">
    	<input type="text" name="posNo" class="{required:true}" />
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