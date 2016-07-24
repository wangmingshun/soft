<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
<sf:override name="pathString">保全质检&gt;&gt;修改质检结果</sf:override>
<script type="text/javascript">
  $(function(){
	  $("form").validate();
	  
	$("#queryBt").click(function(){
		$("form").attr("action","${ctx}others/poszj/queryInfor");
		$("form").submit();
		return false;
	});



	
  });


</script>
</sf:override>
<sf:override name="content">

<sfform:form name="mainForm" commandName="acceptance__item_input">
<div id="p1" class="easyui-panel">
<input type="hidden" name="take" value="Y" /> 
  <table class="layouttable">
    <tr>
      <td width="10%" class="layouttable_td_label">
		                    保全号：
	  </td>
      <td width="50%" class="layouttable_td_widget">
          <input type="text" name="posNo" class="{required:true,messages:{required:' 请录入保全号'}}"  />
      </td>
      <td>&nbsp;
        
      </td>
      <td align="right">
        <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryBt">查询</a>
      </td>

    </tr>
  </table>
</div>

<br />

<c:if test="${not empty posInfo}">

</c:if>

</sfform:form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
