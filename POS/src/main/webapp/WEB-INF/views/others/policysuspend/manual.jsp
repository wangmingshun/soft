<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
<sf:override name="pathString">保单暂停&gt;&gt;人工暂停或取消</sf:override>
<script type="text/javascript">
  $(function(){
	  $("form").validate();
	  
	$("#queryBt").click(function(){
		$("form").attr("action","${ctx}others/policysuspend/query");
		$("form").submit();
		return false;
	});
	$("#suspendBt").click(function(){
		$("[name=policyNoInput]").removeClass();
		$("form").attr("action","${ctx}others/policysuspend/suspend");
		$("form").submit();
		return false;
	});

  });


</script>
</sf:override>
<sf:override name="content">

<form name="mainForm" method="post">

<div id="p1" class="easyui-panel">
  <table class="layouttable">
    <tr>
      <td class="layouttable_td_label">保单号：</td>
      <td class="layouttable_td_widget">
        <input type="text" name="policyNoInput" value="${policyNo}" class="{required:true}" />
      </td>
      <td align="right">
        <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="queryBt">查询</a>
      </td>
    </tr>
  </table>
</div>

<br />

<c:if test="${not empty policyInfo}">
<div id="p2" class="easyui-panel" title="保单信息">
    <table class="infoDisTab">
      <tr>
        <th width="10%">保单号</th>
        <th>责任状态</th>
        <th>投保人客户号</th>
        <th>投保人姓名</th>
        <th>被保人客户号</th>
        <th>被保人姓名</th>
        <th>主险名称</th>
        <th>人工暂停</th>
        <th>人工暂停原因</th>
        <th width="18%">暂停备注录入</th>
      </tr>
      <tr>
        <td>${policyInfo.POLICY_NO}<input type="hidden" name="policyNo" value="${policyInfo.POLICY_NO}" /></td>
        <td>${policyInfo.DUTY_STATUS}</td>
        <td>${policyInfo.APPLICANT_NO}<input type="hidden" name="clientNo" value="${policyInfo.APPLICANT_NO}" /></td>
        <td>${policyInfo.APPLICANT_NAME}</td>
        <td>${policyInfo.INSURED_NO}</td>
        <td>${policyInfo.INSURED_NAME}</td>
        <td>${policyInfo.FULL_NAME}</td>
        <td>${policyInfo.SUSPEND_FLAG}<input type="hidden" name="suspendFlag" value="${policyInfo.SUSPEND_FLAG}" /></td>
        <td>${policyInfo.SUSPEND_REMARK}</td>
        <td>
          <textarea name="suspendRemark" class="{byteRangeLength:[0,200]}"></textarea>
          <input type="hidden" name="posNo" value="${policyInfo.POS_NO}" />
        </td>
      </tr>
    </table>
    
    <div align="right">
        <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="suspendBt">
          <c:if test="${policyInfo.SUSPEND_FLAG eq 'Y'}">取消暂停</c:if>
          <c:if test="${policyInfo.SUSPEND_FLAG eq 'N'}">置暂停</c:if>
        </a>
    </div>

</div>
</c:if>

</form>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
