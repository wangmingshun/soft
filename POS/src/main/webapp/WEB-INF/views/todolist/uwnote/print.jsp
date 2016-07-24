 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">核保函批次查询/逐单打印</sf:override>
<sf:override name="head">

<script type="text/javascript">
$(function(){
	$("form").validate();
	
	$("[name=startDate],[name=endDate]").click(function(){
		WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}'});
	});
	
	$("#queryBt").click(function(){
		$("form").submit();
		return false;
	});
		
});

//核保函打印的时候，跳转到uws前，先更新detail的打印次数纪录
function jumpToUws(posNoValue, type, applyDate){
	$.post("${ctx}todolist/uwnotePrint/updateTimes", 
			{posNo:posNoValue},
			function(data) {
				if(data=="Y"){
					window.open("${UW_URL}SL_UW/notePrint/undwrtNote.do?controlNo="+posNoValue+"&undwrtTaskType="+type+"&applyDate="+applyDate,posNoValue,"location=no,resizable=yes");
				}else{
					$.messager.alert("抱歉","发生了异常，请联系技术人员支持！");
				}
		    }, "text");
	return false;
}

</script>
</sf:override>
<sf:override name="content">

<form action="${ctx}todolist/uwnotePrint/query">

<div id="p1" class="easyui-panel">
<table class="layouttable">
  <tr>
    <td width="25%" class="layouttable_td_label">保全受理时间范围：<span class="requred_font">*</span></td>
    <td class="layouttable_td_widget">
      <input type="text" id="startDate" name="startDate" class="Wdate {required:true,messages:{required:' 请输入开始日期'}}" />
      至
      <input type="text" id="endDate" name="endDate" class="Wdate {required:true,messages:{required:' 请输入结束日期'}}" />
    </td>
    <td align="right">
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryBt">确定</a>
    </td>
  </tr>
</table>
</div>

<br />

<div id="p2" class="easyui-panel" title="可打印核保函的保全信息列表" collapsible="true">
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
      <c:forEach items="${forUwnoteList}" var="item" varStatus="status">
        <c:if test="${status.index%2==0}">
            <tr class="odd_column">
        </c:if>
        <c:if test="${status.index%2!=0}">
            <tr class="even_column">
        </c:if>
        <td><a href="javascript:void(0)" onclick="jumpToUws('${item.POS_NO}','${item.UW_TYPE}','${item.APPLY_DATE}');return false;">${item.POS_NO}</a></td>
        <td>${item.POLICY_NO}</td>
        <td>${item.SERVICE_ITEMS_DESC}</td>
        <td>${item.BARCODE_NO}</td>
        <td>${item.ACCEPTOR}</td>
        <td>${item.ACCEPT_DATE}</td>
        <td>${item.PRINT_TIME}</td>
      </tr>
      </c:forEach>
    </table>
    
</div>

</form>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>