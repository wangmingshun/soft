<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
<sf:override name="pathString">保单回执管理&gt;&gt;<c:if test="${entryFlag eq 'S'}">签署</c:if><c:if test="${entryFlag eq 'C'}">录入</c:if>日期修改</sf:override>
<script type="text/javascript">
$(function(){
	
	$("#signDate").click(function(){
		WdatePicker({skin:'whyGreen',minDate:'${contract.PROVIDE_DATE}',maxDate:'${contract.MAX_SIGN_DATE}',startDate:'${CodeTable.posTableMap.SYSDATE}',isShowToday:false});
	});
	$("#confirmDate").click(function(){
		WdatePicker({skin:'whyGreen',minDate:'${contract.CLIENT_SIGN_DATE}',maxDate:'${contract.MAX_CONFIRM_DATE}',startDate:'${contract.MAX_CONFIRM_DATE}',isShowToday:false});
	});
	
	$("#queryBt").click(function(){
		$("form[name=mainform]").attr("action","${ctx}receipt/feedback/updateQuery").submit();
		return false;
	});
	$("#updateBt, #removeBt").click(function(){
		if("${contract.SIGN_FLAG}"!="Y"){
			$.messager.alert("提示","此保单回执尚未回销，不能在此操作");
			return false;
		}	
		if($(this).attr("id")=="updateBt" && ($("[name=newDate]").val()==""||$("[name=newDate]").val()==$("#oldDate").val())){
			$.messager.alert("提示","请正确录入新的日期");
			return false;
		}
		if($("#fileChoose").val()==""){
			$.messager.alert("提示","请选择附件");
			return false;
		}
		if($("#remark").val().length < 5){
			$.messager.alert("提示","请填写5个字以上的备注");
			return false;
		}
		if($(this).attr("id")=="removeBt"){
			$("[name=newDate]").val("");
		}
		$("form[name=mainform]").attr("action","${ctx}receipt/feedback/updateSubmit").submit();
		return false;
	});

	$("form").validate();
});

var theWin;
function fileDownWindow(url){
	theWin = window.open(url);
	setTimeout("theWin.close();",8000);
}
</script>
</sf:override>
<sf:override name="content">

<form enctype="multipart/form-data" method="post" name="mainform">

<input type="hidden" name="entryFlag" value="${entryFlag}" />

<div class="easyui-panel">
<table class="layouttable">
  <tr>
    <td class="layouttable_td_label">保单号：</td>
    <td class="layouttable_td_widget">
      <input type="text" name="policyNo" id="policyNo" class="{required:true}" value="${contract.POLICY_NO}" <c:if test="${seniorBizFlag eq 'N'}">onpaste="return false" ondragenter="return false"</c:if>/>
      再输入一次：
      <input type="text" name="policyNoRepeat" class="{equalTo:'#policyNo'}" value="${contract.POLICY_NO}" <c:if test="${seniorBizFlag eq 'N'}">onpaste="return false" ondragenter="return false"</c:if>/>
      <input type="hidden" name="policyNoInput" value="${contract.POLICY_NO}" />
    </td>
    <td align="right" width="80px">
    	<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryBt">查询</a>
    </td>
  </tr>
<c:if test="${not empty contract}">
  <tr>
  	<td class="layouttable_td_label">回销标志：</td>
    <td class="layouttable_td_widget">
    	${contract.SIGN_FLAG=="Y"?"已回销":"未回销"}
    </td>
  </tr>
  <tr>
  	<td class="layouttable_td_label"><c:if test="${entryFlag eq 'S'}">客户签署</c:if><c:if test="${entryFlag eq 'C'}">回执回销</c:if>日期：</td>
    <td class="layouttable_td_widget">
    	<c:if test="${entryFlag eq 'S'}">${contract.CLIENT_SIGN_DATE}</c:if>
        <c:if test="${entryFlag eq 'C'}">${contract.CONFIRM_DATE}</c:if>
        <input type="hidden" id="oldDate" name="oldDate" value="<c:if test='${entryFlag eq "S"}'>${contract.CLIENT_SIGN_DATE}</c:if><c:if test='${entryFlag eq "C"}'>${contract.CONFIRM_DATE}</c:if>" />
    </td>
  </tr>
  <tr>
  	<td class="layouttable_td_label">新<c:if test="${entryFlag eq 'S'}">签署</c:if><c:if test="${entryFlag eq 'C'}">回销</c:if>日期：</td>
    <td class="layouttable_td_widget">
    	<c:if test="${entryFlag eq 'S'}"><input type="text" name="newDate" id="signDate" class="Wdate" /></c:if>
        <c:if test="${entryFlag eq 'C'}"><input type="text" name="newDate" id="confirmDate" class="Wdate" /></c:if>
    </td>
  </tr>
  <tr>
  	<td class="layouttable_td_label">附件：</td>
    <td class="layouttable_td_widget" id="fileTd">
    	<input type="file" name="updateFile" id="fileChoose"/>
        <c:forEach items="${contract.attachs}" var="item">
            <a href="javascript:fileDownWindow('${item.fileUrl}')">${item.fileName}</a>
        </c:forEach>
    </td>
  </tr>
  <tr>
  	<td class="layouttable_td_label_multiple">备注：</td>
    <td class="layouttable_td_widget">
    	<textarea name="remark" wrap="off" cols="45" id="remark" class="{byteRangeLength:[0,1000]}"></textarea>
    </td>
  </tr>
</c:if>
</table>
</div>
<c:if test="${not empty contract}">
<div align="right">
	<a class="easyui-linkbutton" href="javascript:void(0)" onclick="window.history.back();return false;" iconCls="icon-back" >返回</a>
    <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-edit" id="updateBt">修改</a>
    <c:if test="${entryFlag eq 'S'}"><a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-cancel" id="removeBt">删除</a></c:if>
</div>
</c:if>

</form>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>