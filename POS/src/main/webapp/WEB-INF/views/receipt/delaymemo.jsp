<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="head">
<sf:override name="pathString">保单回执延期回销报备</sf:override>
<script type="text/javascript">
  $(function(){
	  $("#policyQueryBt").click(function(){
		  $("form[name=queryForm]").submit();
		  return false;
	  });
	  
	  $("#memoSubmitBt").click(function(){
		  $("form[name=memoForm]").submit();
		  return false;
	  });
	  
	  $("form[name=queryForm]").validate();
	  $("form[name=memoForm]").validate();
	  
  });

  var theWin;
  function fileDownWindow(url){
  	theWin = window.open(url);
  	setTimeout("theWin.close();",8000);
  }
</script>
</sf:override>
<sf:override name="content">

<form name="queryForm" action="${ctx}receipt/delaymemo/policyQuery">

<div class="easyui-panel">
<table class="layouttable">
  <tr>
    <td class="layouttable_td_label">请输入需报备的保单号：</td>
    <td class="layouttable_td_widget">
        <input type="text" name="policyNo" class="{required:true}" value="${POLICY_NO}" id="policyNo" onpaste="return false" ondragenter="return false" />
      再输入一次：<input type="text" name="policyNoRepeat" class="partValidate {equalTo:'#policyNo'}" onpaste="return false" ondragenter="return false" />
    </td>
    <td align="right">
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="policyQueryBt">查询</a>
    </td>
  </tr>
</table>
</div>
<br />

</form>

<form name="memoForm" action="${ctx}receipt/delaymemo/submit" enctype="multipart/form-data" method="post">

<c:if test="${QUERY_FLAG eq 'Y'}">
<input type="hidden" name="thePolicyNo" value="${POLICY_NO}" />

<div class="easyui-panel">
<table class="layouttable">
	<tr>
    	<td class="layouttable_td_label">保单号：</td>
        <td class="layouttable_td_widget">${POLICY_NO}</td>
        <td class="layouttable_td_label">保单所属机构：</td>
        <td class="layouttable_td_widget">${BRANCH}</td>
        <td class="layouttable_td_label">销售渠道：</td>
        <td class="layouttable_td_widget">${CHANNEL_DESC}
        	<input type="hidden" name="channel" value="${CHANNEL_TYPE}" />
        </td>
    </tr>
    <tr>
    	<td class="layouttable_td_label">投保人：</td>
        <td class="layouttable_td_widget">${APPLICANT_NAME}</td>
        <td class="layouttable_td_label">投保人电话：</td>
        <td class="layouttable_td_widget">${APPLICANT_PHONES}</td>
        <td class="layouttable_td_label">合同打印日期：</td>
        <td class="layouttable_td_widget">${PROVIDE_DATE}
        	<input type="hidden" name="provideDate" value="${PROVIDE_DATE}" />
            <input type="hidden" name="memoDate" value="${CodeTable.posTableMap.SYSDATE}" />
        </td>
    </tr>
    <tr>
        <td class="layouttable_td_label">附件：</td>
        <td class="layouttable_td_widget">
        	<c:if test="${MEMO_FLAG eq 'Y'}">
            	<a href="javascript:fileDownWindow('${attachment.fileUrl}')">${attachment.fileName}</a>
            </c:if>
            <c:if test="${MEMO_FLAG eq 'N'}">
               	<input type="file" name="memoFile" class="{required:true}" />
            </c:if>
        </td>
        <td class="layouttable_td_label">未送达原因：</td>
        <td class="layouttable_td_widget" colspan="3">
        	<c:if test="${MEMO_FLAG eq 'Y'}">${UNARRIVE_CAUSE}</c:if>
            <c:if test="${MEMO_FLAG eq 'N'}">
            	<input type="text" name="unDeliveryCause" class="{required:true}" size="40" />
            </c:if>
        </td>
    </tr>
</table>
</div>

<c:if test="${MEMO_FLAG eq 'N'}">
    <div align="right">
    	<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="memoSubmitBt">确定</a>
    </div>
</c:if>
    
</c:if>

</form>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>