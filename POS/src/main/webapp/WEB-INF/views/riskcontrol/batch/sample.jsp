<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">风险管控&gt;&gt;批处理抽样</sf:override>
<sf:override name="head">
<script type="text/javascript">
$(function(){
	$("form").validate();
	
	$("#sampleMonth").click(function(){
		WdatePicker({skin:'whyGreen',dateFmt:'yyyyMM',startDate:'${CodeTable.posTableMap.SYSDATE}'});
	});
	
	$("#branchChooseBt").click(function(){
		showBranchTree($(this).prev(),"02");
		return false;
	});
	
	$("#submitBt").click(function(){
		$("form").submit();
		return false;
	});
	  
});

</script>

</sf:override>

<sf:override name="content">

<form action="${ctx}riskcontrol/batch/sampleSubmit">

<div class="easyui-panel">
<table class="layouttable">
  <tr>
    <td class="layouttable_td_label">抽样类型：</td>
    <td class="layouttable_td_widget">
      <select name="sampleType">
        <option value="1">生存金</option>
        <!-- option value="2">红利</option -->
      </select>
    </td>
  </tr>
  <tr class="list_td">
    <td class="layouttable_td_label">机构：</td>
    <td class="layouttable_td_widget">
      <input type="text" name="branchCode" class="{required:true}" /><a id="branchChooseBt" href="javascript:void(0)">选择机构</a>
    </td>
  </tr>
  <tr class="list_td">
    <td class="layouttable_td_label">抽样月份：</td>
    <td class="layouttable_td_widget">
      <input type="text" id="sampleMonth" name="sampleMonth" class="Wdate {required:true}" />
    </td>
  </tr>
</table>
</div>
<div align="right">
	<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="submitBt">确定</a>
</div>

</form>

<jsp:include page="/WEB-INF/views/include/branchTree.jsp"></jsp:include>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>