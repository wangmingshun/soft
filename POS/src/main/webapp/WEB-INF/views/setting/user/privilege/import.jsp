<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">用户权限设置&gt;&gt;新用户批量导入</sf:override>
<sf:override name="head">
<script type="text/javascript">
$(function(){
	$("form[name=upForm]").validate();

	$("#submitBt").click(function(){
		$("form").submit();
		return false;
	});

});
 
</script>

</sf:override>

<sf:override name="content">

<form action="${ctx}setting/user/importPrivFile" enctype="multipart/form-data" method="post" name="upForm">

<div class="easyui-panel">
<table class="layouttable">
  <tr>
    <td class="layouttable_td_label">请选择文件：</td>
    <td class="layouttable_td_widget">
      <input type="file" name="file" class="{required:true}" />
    </td>
    <td align="right">
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="submitBt">上传</a>
    </td>
  </tr>
</table>
</div>

</form>

<c:if test="${importFlag eq 'Y'}">
	下列数据处理有误：<br />
</c:if>
<table border="1">
	<c:forEach items="${rstList}" var="item">
	<tr>
    	<td>${item.userId}</td><td>${item.resultMsg}</td>
    </tr>
    </c:forEach>
</table>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>