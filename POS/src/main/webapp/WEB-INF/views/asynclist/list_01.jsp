<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<sf:override name="pathString">生存调查数据提取</sf:override>
<sf:override name="head">
<script type="text/javascript">
$(function(){
	$("form").validate();
	$("#yearMonth").focus(function(){
		WdatePicker({skin:'whyGreen',dateFmt:'yyyy-MM'});
	});
	
	$("#branchChooseBt").click(function(){
		showBranchTree($(this).prev(),"03","${userBranchCode}");
		return false;
	});
	  
});
  
</script>

</sf:override>

<sf:override name="content">

<form id="queryForm" name="queryForm" action="${ctx}async/task/submit" class="noBlockUI">
    <input type="hidden" name="sql" value="selectSurvivalInvestList" />	
	<input type="hidden" id="listCode" name="listCode" value="01" />   
	    <table class="layouttable">
	      <tr>
		       <td width="10%" class="layouttable_td_label">
		                        抽档月份:<span class="requred_font">*</span>
		       </td>
		       <td width="25%" class="layouttable_td_widget">
		            <input type="text" id="yearMonth" name="yearMonth" class="{required:true} Wdate" /> 
		        </td>
		        <td width="10%" class="layouttable_td_label">
		                               保单机构:<span class="requred_font">*</span>
		        </td>
		        <td width="25%" class="layouttable_td_widget">
		            <input type="text" name="branchCode" class="{required:true}" value="${userBranchCode}"/>
		             <a href="javascript:void(0)" id="branchChooseBt">选择机构</a>
		        </td>
		         <td align="right">
		            <a  class='easyui-linkbutton' href="javascript:void(0)" iconCls="icon-search" onclick="$('#queryForm').submit();return false;"  id="sub">提取数据</a>
		        </td>
	        </tr>
	    </table>
</form>
<hr class="hr_default"/>
<jsp:include page="/WEB-INF/views/asynclist/share.jsp"></jsp:include>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>