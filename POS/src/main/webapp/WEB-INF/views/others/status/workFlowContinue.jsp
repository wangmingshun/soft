<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
<sf:override name="pathString">工作流继续推动</sf:override>
<script type="text/javascript">
  $(function(){
	  $("form").validate(); 

	$("#continue").click(function(){
		$("form").attr("action","${ctx}others/workFlowContinue/submit");
		$("form").submit();
		return false;
	});
  });

</script>
</sf:override>
<sf:override name="content">

<sfform:form commandName="acceptance__item_input" >
<div id="p1" class="easyui-panel">
  <table class="layouttable">
    <tr>
      <td class="layouttable_td_label">保全号：</td>
      <td class="layouttable_td_widget">
      <sfform:input path="posNo" cssClass="input_text {required:true}" />     
      </td>
      <td align="right">
        <a class="easyui-linkbutton"  iconCls="icon-search" id="continue">激活</a>
      </td>
    </tr>
  </table>
</div>
<br />
</sfform:form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
