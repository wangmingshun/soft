<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
<sf:override name="pathString">维护设置&gt;&gt;录入等级设置</sf:override>
<jsp:include page="/WEB-INF/views/include/selectJQuery.jsp"></jsp:include>
<script type="text/javascript">
  $(function(){
	  
	  $("#okBt").click(function(){
		  var sItem = "";
		  $("#selected option").each(function(){
			  sItem += $(this).val()+";";
		  });
		  
          $.post("${ctx}setAcceptLevel", 
			{serviceItems:sItem,
			 gradeSelect:$("[name='gradeSelect']").val()},
			function(data) {
				if(data=="Y"){
					$.messager.alert("提示","设置成功！");
				}
		    }, "text");
          return false;
      });
	  
	  $("#queryBt").click(function(){
		  $("form").submit();
		  return false;
	  });
  })
	
</script>
</sf:override>
<sf:override name="content">

<sfform:form action="${ctx}queryAcceptLevelInfo" >

<div class="easyui-panel">
  <table class="layouttable">
    <tr>
      <td class="layouttable_td_label">等级名称:
      </td>
      <td class="layouttable_td_widget">
        <select name="gradeSelect">
          <c:forEach items="${CodeTable.posTableMap.POS_INPUT_GRADE_CODE}" var="item">
            <option value="${item.code}" >${item.description}</option>
          </c:forEach>
        </select>      
      </td>
      <td align="right">
        <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryBt">查询</a>
      </td>
    </tr>
  </table>
</div>
<br />

<div class="easyui-panel" title="设置操作">
<table>
  <tr>
    <td>
      <fieldset class="fieldsetdefault"><legend>可增加操作</legend>
      <select multiple="multiple" style="height:220px; width:180px" id="forSelect">
        <c:forEach items="${CodeTable.posTableMap.PRODUCT_SERVICE_ITEMS}" var="item">
          <option value="${item.code}">${item.description}</option>
        </c:forEach>
      </select>
      </fieldset>
    </td>
    <td align="center" width="15%">
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="add">&nbsp;&nbsp;增加&nbsp;&nbsp;</a><br /><br />
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-remove" id="delete">&nbsp;&nbsp;删除&nbsp;&nbsp;</a><br /><br />
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="addAll">全部增加</a><br /><br />
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-remove" id="deleteAll">全部删除</a><br /><br />
    </td>
    <td>
      <fieldset class="fieldsetdefault"><legend>已设置操作</legend>
      <select multiple="multiple" style="height:220px; width:180px" id="selected" name="serviceItems" >
      </select>
      </fieldset>
    </td>
    <td width="50%"></td>
  </tr>
  <tr>
    <td align="right" colspan="4">
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="okBt">确定</a>
    </td>
  </tr>
</table>

</div>

</sfform:form>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>

<c:if test="${not empty LEVEL}">
  <script type="text/javascript">
     $("[name='gradeSelect']").val("${LEVEL}");
  </script>
</c:if>
<c:forEach items="${SETED_PRIVS}" var="item">
  <script type="text/javascript">
     $("#forSelect option").each(function(){ 
	    if($(this).val()=="${item}"){
         $("#selected").append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option"); 
         $(this).remove();  
		}

     });
  </script>
</c:forEach>