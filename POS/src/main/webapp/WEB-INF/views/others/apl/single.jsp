<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
<sf:override name="pathString">保单逐单自垫</sf:override>
<script type="text/javascript">
  $(function(){
	  $("form").validate();
	  
	  $("#submitBt").click(function(){
		  $("form").submit();
		  return false;
	  });
	  
  });

</script>
</sf:override>
<sf:override name="content">

<form action="${ctx}others/apl/policySubmit">

<div class="easyui-panel">
<table class="layouttable">
  <tr>
    <td class="layouttable_td_label">保单号：</td>
    <td class="layouttable_td_widget">
      <input type="text" name="policyNo" class="{required:true}" />
    </td>
    <td align="right">
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="submitBt">确定</a>
    </td>
  </tr>
</table>
</div>

</form>


</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>