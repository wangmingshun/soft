<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
<sf:override name="pathString">用户管理&gt;&gt;职级等级对照关系设置</sf:override>
<jsp:include page="/WEB-INF/views/include/selectJQuery.jsp"></jsp:include>
<script type="text/javascript">
  $(function(){
	  $("select[name='rankSel']").change(function(){
		  if($("select[name='rankSel'] option:selected").val()>0){
			  $("form").submit();
		  }
	  });
	  
	  $("#okBt").click(function(){
		var is = "";
		$("#selected option").each(function(){
		    is += $(this).val()+";";
		});
		var as = "";
		$("#selected2 option").each(function(){
		    as += $(this).val()+";";
		});
        $.post("${ctx}setting/user/setDutyRelations", 
			  {rank:$("select[name='rankSel'] option:selected").val(),
			   inputGrade:$("select[name='inputGradeSel'] option:selected").val(),
			   amountGrade:$("select[name='amountGradeSel'] option:selected").val(),
			   difPlace:$("select[name='difPlaceSel'] option:selected").val(),
			   ruleSpec:$("select[name='ruleSpecSel'] option:selected").val(),
			   rollbackAprov:$("select[name='rollbackApSel'] option:selected").val(),
			   inputSpecials:is,
			   aprovSpecials:as},
			  function(data) {
				  if(data=="Y"){
					  $.messager.alert("提示","设置成功！");
				  }
		      }, "text");
	  });
	 	  
  });
	
</script>
</sf:override>
<sf:override name="content">

<form action="${ctx}setting/dutyrelations/queryInfoByRank">
 <div class="easyui-panel">
  <table class="layouttable">
    <tr>
      <td class="layouttable_td_label" width="15%">请选择职级：</td>
      <td class="layouttable_td_widget">
      <select name="rankSel">
          <option value="">--请选择--</option>
          <c:forEach items="${CodeTable.posTableMap.POS_RANK_CODE}" var="item">
            <option value="${item.code}" >${item.description}</option>
          </c:forEach>
      </select>
      </td>
    </tr>
    <tr>
      <td class="layouttable_td_label">录入等级：</td>
      <td class="layouttable_td_widget">
      <select name="inputGradeSel">
          <option value="">--请选择--</option>
          <c:forEach items="${CodeTable.posTableMap.POS_INPUT_GRADE_CODE}" var="item">
            <option value="${item.code}" >${item.description}</option>
          </c:forEach>
      </select>
      </td>
    </tr>
    <tr>
      <td class="layouttable_td_label">审批金额等级：</td>
      <td class="layouttable_td_widget">
      <select name="amountGradeSel">
          <option value="">--请选择--</option>
          <c:forEach items="${CodeTable.posTableMap.POS_AMOUNT_DAYS_GRADE}" var="item">
            <option value="${item.code}" >${item.description}</option>
          </c:forEach>
      </select>
      </td>
    </tr>
    <tr>
      <td class="layouttable_td_label">异地操作权限：</td>
      <td class="layouttable_td_widget">
      <select name="difPlaceSel">
        <option value="">--请选择--</option>
        <option value="Y">是</option>
        <option value="N">否</option>
      </select>
      </td>
    </tr>
    <tr>
      <td class="layouttable_td_label">规则特殊件审批权限：</td>
      <td class="layouttable_td_widget">
      <select name="ruleSpecSel">
        <option value="">--请选择--</option>
        <option value="Y">是</option>
        <option value="N">否</option>
      </select>
      </td>
    </tr>
    <tr>
      <td class="layouttable_td_label">受理撤销回退审批权限：</td>
      <td class="layouttable_td_widget">
      <select name="rollbackApSel">
        <option value="">--请选择--</option>
        <option value="Y">是</option>
        <option value="N">否</option>
      </select>
      </td>
    </tr>
  </table>
  </div>

  <div class="easyui-panel" title="功能特殊件录入权限设置">
    <table>
      <tr>
        <td>
          <fieldset class="fieldsetdefault"><legend>可增加操作</legend>
          <select multiple="multiple" style="height:220px; width:180px" id="forSelect">
            <c:forEach items="${CodeTable.posTableMap.POS_SPECIAL_FUNC_CODE}" var="item">
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
    </table>
  </div>
  
  <div class="easyui-panel" title="功能特殊件审批权限设置">
    <table>
      <tr>
        <td>
          <fieldset class="fieldsetdefault"><legend>可增加操作</legend>
          <select multiple="multiple" style="height:220px; width:180px" id="forSelect2">
            <c:forEach items="${CodeTable.posTableMap.POS_SPECIAL_FUNC_CODE}" var="item">
              <option value="${item.code}">${item.description}</option>
            </c:forEach>
          </select>
          </fieldset>
        </td>
        <td align="center" width="15%">
          <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="add2">&nbsp;&nbsp;增加&nbsp;&nbsp;</a><br /><br />
          <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-remove" id="delete2">&nbsp;&nbsp;删除&nbsp;&nbsp;</a><br /><br />
          <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="addAll2">全部增加</a><br /><br />
          <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-remove" id="deleteAll2">全部删除</a><br /><br />
        </td>
        <td>
          <fieldset class="fieldsetdefault"><legend>已设置操作</legend>
          <select multiple="multiple" style="height:220px; width:180px" id="selected2" name="serviceItems" >
          </select>
          </fieldset>
        </td>
        <td width="50%"></td>
      </tr>
    </table>
  </div>
  
  <div align="right"><a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="okBt">确定</a></div>

</form>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>

<c:if test="${not empty RANK}">
  <script type="text/javascript">
     $("[name='rankSel']").val("${RANK}");
	 $("[name='inputGradeSel']").val("${PRIVS.INPUT_GRADE}");
	 $("[name='amountGradeSel']").val("${PRIVS.AMOUNT_DAYS_GRADE}");
	 $("[name='difPlaceSel']").val("${PRIVS.DIFFERENT_PLACE_PRIVS}");
	 $("[name='ruleSpecSel']").val("${PRIVS.RULE_SPEC_PRIVS}");
	 $("[name='rollbackApSel']").val("${PRIVS.ROLLBACK_APROV_PRIVS}");
  </script>
</c:if>
<c:forEach items="${INPUT_SPECIAL}" var="item">
  <script type="text/javascript">
     $("#forSelect option").each(function(){ 
	    if($(this).val()=="${item.SPECIAL_FUNC}"){
         $("#selected").append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option"); 
         $(this).remove();  
		}

     });
  </script>
</c:forEach>
<c:forEach items="${APROV_SPECIAL}" var="item">
  <script type="text/javascript">
     $("#forSelect2 option").each(function(){ 
	    if($(this).val()=="${item.SPECIAL_CODE}"){
         $("#selected2").append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option"); 
         $(this).remove();  
		}

     });
  </script>
</c:forEach>