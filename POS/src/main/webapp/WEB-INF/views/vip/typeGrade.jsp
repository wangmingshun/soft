<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">VIP管理&gt;&gt;VIP类型和等级规则设置</sf:override>
<sf:override name="head">
<script type="text/javascript">
$(function() {
	$("form").validate();
	$("#setBt").click(function(){
		$("form").submit();
		return false;
	});
	
	$("select").change(function(){
		 $.post("${ctx}vip/typeGrade/query", 
			{vipType:$("[name=vipType]").val(),
			 vipGrade:$("[name=vipGrade]").val(),
			 branchClassify:$("[name=branchClassify]").val()
			},
			function(data) {
				if(data=="NUL"){
					data = "尚未设置";
				}
				$("#premSumSeted").text(data);
		    }, "text");
	});
	$("select[name=vipType]").trigger("change");
	
});

</script>
</sf:override>

<sf:override name="content">

<form action="${ctx}vip/typeGrade/set">

<div class="easyui-panel">
  <table class="layouttable">
    <tr>
      <td class="layouttable_td_label" width="15%">VIP类型：</td>
      <td class="layouttable_td_widget">
        <select name="vipType">
          <c:forEach items="${CodeTable.posTableMap.POS_VIP_TYPE}" var="item">
            <option value="${item.code}">${item.description}</option>
          </c:forEach>
        </select>
      </td>
    </tr>
    <tr>
      <td class="layouttable_td_label">VIP等级：</td>
      <td class="layouttable_td_widget">
        <select name="vipGrade">
          <c:forEach items="${CodeTable.posTableMap.POS_VIP_GRADE}" var="item">
            <option value="${item.code}">${item.description}</option>
          </c:forEach>
        </select>
      </td>
    </tr>
    <tr>
      <td class="layouttable_td_label">机构分类：</td>
      <td class="layouttable_td_widget">
        <select name="branchClassify">
          <c:forEach items="${CodeTable.posTableMap.POS_BRANCH_CLASSIFY}" var="item">
            <option value="${item.code}">${item.description}</option>
          </c:forEach>
        </select>
      </td>
    </tr>
    <tr>
      <td class="layouttable_td_label">现保费金额：</td>
      <td class="layouttable_td_widget" id="premSumSeted">
      </td>
    </tr>
    <tr>
      <td class="layouttable_td_label">设置保费金额：</td>
      <td class="layouttable_td_widget">
        <input type="text" class="easyui-numberbox {required:true}" precision="2" name="premSum" />
      </td>
    </tr>
  </table>
</div>
<div align="right">
	<a class="easyui-linkbutton" iconCls="icon-ok" href="javascript:void(0)" id="setBt" >确定</a>
</div>

</form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>