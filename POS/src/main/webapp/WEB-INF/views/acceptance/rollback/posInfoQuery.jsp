<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
<sf:override name="pathString">机构受理&gt;&gt;保全撤销与回退</sf:override>
<script type="text/javascript">
  $(function(){
	  $("form").validate();
	  
	$("#queryBt").click(function(){
		$("form").attr("action","${ctx}acceptance/rollback/queryByPosNo");
		$("form").submit();
		return false;
	});
	$("#processBt").click(function(){
		$("[name=posNo]").removeClass();
		$("form").attr("action","${ctx}acceptance/rollback/process");
		$("form").submit();
		return false;
	});

  });


</script>
</sf:override>
<sf:override name="content">

<form name="mainForm">

<div id="p1" class="easyui-panel">
  <table class="layouttable">
    <tr>
      <td class="layouttable_td_label">保全批单号：</td>
      <td class="layouttable_td_widget">
        <input type="text" name="posNo" value="${posInfo.POS_NO}" class="{required:true}" size="30" />
      </td>
      <td align="right">
        <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryBt">查询</a>
      </td>
    </tr>
  </table>
</div>

<br />

<c:if test="${not empty posInfo}">
<div id="p2" class="easyui-panel" title="待撤销或回退受理信息">
    <table class="infoDisTab">
      <tr>
        <th>保单号</th>
        <th>保全批单号</th>
        <th>条形码</th>
        <th>受理项目名称</th>
        <th>受理状态</th>
        <th>受理时间</th>
      </tr>
      <tr>
        <td>${posInfo.POLICY_NO}</td>
        <td>${posInfo.POS_NO}<input type="hidden" name="posNoHid" value="${posInfo.POS_NO}" /></td>
        <td>${posInfo.BARCODE_NO}</td>
        <td>
          ${posInfo.SERVICE_ITEMS_DESC}<input type="hidden" name="serviceItemsHid" value="${SERVICE_ITEMS}" />
        </td>
        <td>${posInfo.ACCEPT_STATUS_DESC}</td>
        <td>${posInfo.ACCEPT_DATE}</td>
      </tr>
    </table>
    <p align="right">
      <c:choose>
        <c:when test="${posInfo.flag eq '0'}">
          <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="processBt">确定</a>
        </c:when>
        <c:otherwise>
          <span class="requred_font">${posInfo.message}</span>
        </c:otherwise>
      </c:choose>
    </p>

</div>
</c:if>

</form>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
