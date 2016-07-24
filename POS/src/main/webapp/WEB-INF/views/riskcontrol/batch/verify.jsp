<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">风险管控&gt;&gt;批处理抽样复核</sf:override>
<sf:override name="head">
<script type="text/javascript">
$(function(){
	$("form[name=verifyForm]").validate();
	$("[name=reviewGetDueDate]").focus(function(){
		WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}'});
	});
	
	$("#nextBt").click(function(){
		$("form[name=verifyForm]").attr("action","${ctx}riskcontrol/batch/verifyNext");
		$("form[name=verifyForm]").submit();
		return false;
	});
	$("#verifyBt").click(function(){
		$("form[name=verifyForm]").attr("action","${ctx}riskcontrol/batch/verifySubmit");
		$("form[name=verifyForm]").submit();
		return false;
	});
	

	$("#dateMonth").click(function(){
		WdatePicker({skin:'whyGreen',dateFmt:'yyyy-MM',startDate:'${CodeTable.posTableMap.SYSDATE}'});
	});
	$("#branchChooseBt").click(function(){
		showBranchTree($(this).prev(),"02");
		return false;
	});
	$(".listQueryBt").click(function(){
		if($(this).attr("name")=="branchBt"){
			if($("#branchCode").val()==""){
				$.messager.alert("提示","请输入机构");
				return false;
			}
			if($("#dateMonth").val()==""){
				$.messager.alert("提示","请输入月份");
				return false;
			}
		}else{
			$("#branchCode").val("");
		}
		$("form[name=listForm]").submit();
		return false;
	});

});

</script>

</sf:override>

<sf:override name="content">

<form name="verifyForm">

<div class="easyui-panel">
  <table class="layouttable">
    <tr>
      <td class="layouttable_td_label">批处理类型：</td>
      <td class="layouttable_td_widget">
      <select name="sampleTypeSel">
        <option value="1">生存金</option>
        <!-- option value="2">红利</option -->
      </select>      
      </td>
      <td align="right">
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="nextBt">取下一单</a>
      </td>
    </tr>
  </table>
</div>

<br />
<c:if test="${not empty verifyData}">
<table class="infoDisTab">
  <tr>
    <th>保单号</th>
    <th>险种名称（代码）</th>
    <th>红利保额</th>   	
    <th>产品序号</th>
    <c:if test="${verifyData.TYPE eq 1}">
      <th>生存金类型</th>
      <th>给付责任描述</th>
    </c:if>
    <c:if test="${verifyData.TYPE eq 2}">
      <th>应领日期</th>
    </c:if>
  </tr>
  <tr>
    <td>${verifyData.POLICY_NO}</td>
    <td>${verifyData.PRODUCT}</td>    
   	<td>${verifyData.SUM_INS}</td>   	
    <td>${verifyData.PROD_SEQ}</td>
    <c:if test="${verifyData.TYPE eq 1}">
      <td>${verifyData.PAY_TIME_TYPE_DESC}</td>
      <td>${verifyData.DUTY_NAME}</td>
    </c:if>
    <c:if test="${verifyData.TYPE eq 2}">
      <td>${verifyData.GET_DUE_DATE}<br></br></td>
    </c:if>
  </tr>
</table>
<div class="easyui-panel">
  <table class="layouttable">
    <tr>
    <c:if test="${verifyData.SAMPLE_TYPE eq 1}">
      <td class="layouttable_td_label">应领日期：</td>
      <td class="layouttable_td_widget">
        <input type="text" name="reviewGetDueDate" class="Wdate {required:true}" />
        <input type="hidden" name="getDueDate" value="${verifyData.GET_DUE_DATE}" />
      </td>
      <td class="layouttable_td_label">人工计算金额：</td>
      <td class="layouttable_td_widget">
        <input type="text" class="easyui-numberbox {required:true}" precision="2" name="reviewGetDueSum" />
        <input type="hidden" name="getDueSum" value="${verifyData.GET_DUE_SUM}" />
      </td>
        <!--
        人工计算利息：
        <input type="text" class="easyui-numberbox {required:true}" precision="2" name="reviewInterestSum" />
        <input type="hidden" name="interestSum" value="${verifyData.INTEREST_SUM}" />
        -->
    </c:if>        
    <c:if test="${verifyData.SAMPLE_TYPE eq 2}">
      <td class="layouttable_td_label">复核金额：</td>
      <td class="layouttable_td_widget">
        <input type="text" class="easyui-numberbox {required:true}" precision="2" name="reviewGetDueSum" />
        <input type="hidden" name="getDueSum" value="${verifyData.GET_DUE_SUM}" />现金分红的保单请录入现金余额，保额分红的保单请录入分红保额
      </td>
    </c:if>
      <td class="layouttable_td_label">备注：</td>
      <td class="layouttable_td_widget">
        <input type="text" name="resultDesc" size="50" class="{byteRangeLength:[0,4000]}"/>
        <input type="hidden" name="sampleSeq" value="${verifyData.SAMPLE_SEQ}" />
        <input type="hidden" name="sampleType" value="${verifyData.SAMPLE_TYPE}" />
      </td>
      <td align="right">
        <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="verifyBt">确定</a>    
      </td>
    </tr>
  </table>
</div>
<br />
</c:if>

</form>

<form name="listForm" action="${ctx}report/submit" class="noBlockUI">
<br />
<input type="hidden" name="sql" id="sql" value="selectTodealSampleInfo" />
<div class="easyui-panel" title="待处理数据清单查询">
<table class="layouttable">
  <tr>
    <td class="layouttable_td_label">抽样机构：</td>
    <td class="layouttable_td_widget" colspan="2">
      <input type="text" name="branchCode" id="branchCode"/><a id="branchChooseBt" href="javascript:void(0)">选择机构</a>
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label">应领月份：</td>
    <td class="layouttable_td_widget">
      <input type="text" name="dateMonth" id="dateMonth" class="Wdate" />
    </td>
    <td align="right">
      <a class="easyui-linkbutton listQueryBt" href="javascript:void(0)" iconCls="icon-ok" name="branchBt">查询</a>
      <a class="easyui-linkbutton listQueryBt" href="javascript:void(0)" iconCls="icon-search">全国待处理清单</a>
    </td>
  </tr>
</table>
</div>
</form>

<br />
<div align="right"><a href="javascript:void(0)" onclick="window.open('${ctx}pub/posGQ','apGQ','resizable=yes,height=700px,width=1000px');">综合查询</a></div>

<jsp:include page="/WEB-INF/views/include/branchTree.jsp"></jsp:include>

</sf:override>

<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>