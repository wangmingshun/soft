 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">保单回执查询清单
&gt;&gt;
<c:if test="${listFlag eq 1}">待回销清单</c:if>
<c:if test="${listFlag eq 2}">回执日期修改清单</c:if>
<c:if test="${listFlag eq 3}">回执录入日期修改清单</c:if>
<c:if test="${listFlag eq 4}">问题件清单</c:if>
<c:if test="${listFlag eq 5}">报备保单清单</c:if>
<c:if test="${listFlag eq 7}">统计报表</c:if>
</sf:override>
<sf:override name="head">

<script type="text/javascript">
$(function(){
	$("form").validate();

	$(".Wdate").click(function(){
	WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}'});
	});
	
	$("#chooseBranchBt").click(function(){
	  showBranchTree($(this).prev());
	  return false;
	});

	$(".inputPara").click(function(){
		$("#submitBt").attr("disabled",false);
	});
	$("#submitBt").click(function(){
		$(this).attr("disabled",true);
		$("form").submit();
		return false;
	});

	<c:if test="${listFlag eq 7}">
		$("[name=dateFrom]").attr("name","startDate");
		$("[name=dateTo]").attr("name","endDate");
	</c:if>

});

</script>
</sf:override>
<sf:override name="content">

<form action="${ctx}report/submit" class="noBlockUI">

<c:if test="${listFlag eq 1}"><input type="hidden" name="sql" value="selectToConfirmReceipt" /></c:if>
<c:if test="${listFlag eq 2}">
	<input type="hidden" name="sql" value="selectDateChangedReceipt" />
    <input type="hidden" name="columnName" value="CLIENT_SIGN_DATE" />
    <input type="hidden" name="listTitle" value="客户签署日期" />
</c:if>
<c:if test="${listFlag eq 3}">
	<input type="hidden" name="sql" value="selectDateChangedReceipt" />
    <input type="hidden" name="columnName" value="CONFIRM_DATE" />
    <input type="hidden" name="listTitle" value="回销录入日期" />
</c:if>
<c:if test="${listFlag eq 4}"><input type="hidden" name="sql" value="selectProblemReceipt" /></c:if>
<c:if test="${listFlag eq 5}"><input type="hidden" name="sql" value="selectDelayMemoReceipt" /></c:if>

<c:if test="${listFlag eq 7}"><input type="hidden" name="sql" value="queryReceiptFeedbackProportion" /></c:if>

<div class="easyui-panel">
<table class="layouttable">
  <tr>
    <td class="layouttable_td_label">保单机构：</td>
    <td class="layouttable_td_widget">
      <input type="text" name="branchCode" class="{required:true} inputPara" /><a id="chooseBranchBt" href="javascript:void(0)">选择机构</a>
    </td>
  </tr>
  <tr class="list_td">
    <td class="layouttable_td_label">时间范围：</td>
    <td class="layouttable_td_widget">
      <input type="text" name="dateFrom" class="Wdate {required:true,messages:{required:' 请输入开始日期'}} inputPara" />
      至
      <input type="text" name="dateTo" class="Wdate {required:true,messages:{required:' 请输入结束日期'}} inputPara" />
    </td>
  </tr>
  <c:if test="${listFlag eq 7}">
  <tr>
    <td class="layouttable_td_label">渠道：</td>
    <td class="layouttable_td_widget">
      <select name="policyChannel" class="inputPara">
        <option value="">全部</option>
        <c:forEach items="${CodeTable.posTableMap.CHANNEL_TYPE_TBL}" var="item">
          <option value="${item.code}">${item.description}</option>
        </c:forEach>
      </select>
    </td>
  </tr>
  </c:if>
</table>
</div>
<div align="right">
	<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="submitBt">确定</a>
</div>

</form>

<jsp:include page="/WEB-INF/views/include/branchTree.jsp"></jsp:include>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>