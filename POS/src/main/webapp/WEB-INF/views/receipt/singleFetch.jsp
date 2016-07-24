<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
<sf:override name="pathString">逐单回销&gt;&gt;回销取单</sf:override>
<script type="text/javascript">
$(function(){
	$("form").validate();
	
	$("#queryBt").click(function(){
		$("#fetchType").val($(this).attr("name"));
		if($("#fetchType").val()=="S" && $.trim($("#policyNo").val())==""){
			$.messager.alert("提示", "请输入保单号");
			return;
		}
		$("form").submit();
		return false;
	});
	
});

//im提供的这个文件下载链接，可能会把当前页面内容洗刷掉，搞来搞去，只好在新窗口打开
var theWin;
function fileDownWindow(url){
	theWin = window.open(url);
	setTimeout("theWin.close();",8000);
}
</script>
</sf:override>
<sf:override name="content">

<form action="${ctx}receipt/feedback/fetchOne" name="mainform">
<input type="hidden" name="fetchType" id="fetchType" />
<input type="hidden" name="singleFetch" id="singleFetch" />
<div class="easyui-panel">
<table class="layouttable">
  <tr>
    <td class="layouttable_td_label">保单号：</td>
    <td class="layouttable_td_widget">
      <input type="text" name="policyNo" id="policyNo" 
      	<c:if test="${seniorBizFlag eq 'N'}">onpaste="return false" ondragenter="return false"</c:if> />
    </td>
    <td align="right">
    	<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryBt" name="S">查询</a>
    </td>
  </tr>
</table>
</div>

<c:if test="${SIGN_FLAG eq 'Y'}">
<br />
<div class="easyui-panel" title="保单回执已回销信息">
<table class="layouttable">
  <tr>
    <td class="layouttable_td_label">保单号：</td>
    <td class="layouttable_td_widget">${POLICY_NO}</td>
    <td class="layouttable_td_label">发放时间：</td>
    <td class="layouttable_td_widget">${PROVIDE_DATE}</td>
    <td class="layouttable_td_label">客户签署日期：</td>
    <td class="layouttable_td_widget">${CLIENT_SIGN_DATE}</td>
  </tr>
  <tr>
    <td class="layouttable_td_label">回销人：</td>
    <td class="layouttable_td_widget">${CONFIRM_USER}</td>
    <td class="layouttable_td_label">回销时间：</td>
    <td class="layouttable_td_widget">${CONFIRM_DATE}</td>
    <td class="layouttable_td_label">附件：</td>
    <td class="layouttable_td_widget">
        <c:forEach items="${attachs}" var="item">
            <a href="javascript:fileDownWindow('${item.fileUrl}')">${item.fileName}</a>
        </c:forEach>
    </td>
  </tr>
</table>
</div>
</c:if>

</form>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>