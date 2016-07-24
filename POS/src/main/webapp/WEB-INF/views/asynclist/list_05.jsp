 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单报表&gt;&gt;保全特殊件清单</sf:override>
<sf:override name="head">

<script type="text/javascript">


  $(function(){
	  $("[name=functionType]").find("option[value='2']").remove(); 
	  $("[name=functionType]").find("option[value='8']").remove(); 
	  $("[name=functionType]").find("option[value='9']").remove(); 
	  $("[name=functionType]").find("option[value='10']").remove(); 
	  $("[name=functionType]").find("option[value='11']").remove(); 
	  $("form").validate();

      $("[name=startDate],[name=endDate]").focus(function(){
		WdatePicker({skin:'whyGreen'});
	  });
		
      $("#branchChooseBt").click(function(){
		  showBranchTree($(this).prev(),"03","${userBranchCode}");
		  return false;
	  });
      
  });

</script>
</sf:override>
<sf:override name="content">

<form id="queryForm" action="${ctx}async/task/submit" class="noBlockUI" method="post">
<input type="hidden" name="ibatisSqlId" value=".specialPosListQuery" />
<input type="hidden" name="sheetName" value="保全特殊件清单" />
<input type="hidden" id="listCode" name="listCode" value="05" />
<table class="layouttable">
	<tr>
		<td width="10%" class="layouttable_td_label">
		 查询机构：<span class="requred_font">*</span>
		</td>
		<td width="50%" class="layouttable_td_widget">
			<input type="text" name="branchCode" class="{required:true}" value="86"/>
			<a href="javascript:void(0)" id="branchChooseBt">选择机构</a>
		</td>
		<td>&nbsp;
			        
		</td>
	</tr>
	
    <tr>
	    <td width="10%" class="layouttable_td_label">
	                         保全受理时间范围：<span class="requred_font">*</span>
	    </td>
	    <td width="50%" class="layouttable_td_widget">
	     <input type="text" name="startDate" class="Wdate {required:true,messages:{required:' 请输入开始日期'}}" />
	     至
	     <input type="text" name="endDate"  class="Wdate {required:true,messages:{required:' 请输入结束日期'}}" />
	    </td>
	    <td>&nbsp;
	      
	    </td>
     </tr>
     <tr>
	    <td width="10%" class="layouttable_td_label">
	                    功能特殊件类型：
	    </td>
	    <td width="50%" class="layouttable_td_widget">
	        <select name="functionType" >
	       <option value="ALL">全部</option>
	       <option value="NO">------</option>
	       <c:forEach items="${CodeTable.posTableMap.POS_SPECIAL_FUNC_CODE}" var="item" >
	         <option value="${item.code}">${item.description}</option>
	       </c:forEach>
	     </select>
	     </td>
	     <td>&nbsp;
	       
	     </td>
    </tr>  
    <tr>
	    <td width="10%" class="layouttable_td_label">
	                   规则特殊件类型：
	    </td>
	    <td width="50%" class="layouttable_td_widget">
	         <select name="ruleType">
	        <option value="ALL">全部</option>
	        <option value="NO">------</option>
	        <c:forEach items="${CodeTable.posTableMap.POS_SPECIAL_RULE_CODE}" var="item" >
	         <option value="${item.code}">${item.description}</option>
	        </c:forEach>
	      </select>
	       <input type ="hidden"  name="policyChannelType"  value="${policyChannelType}"/>
	    </td>
	    <td align="right">
	       <a  class='easyui-linkbutton' href="javascript:void(0)" onclick="$('#queryForm').submit();return false;"  iconCls="icon-ok" id="sub">确定</a>
	     </td>
    </tr>    
	</table>
	<hr class="hr_default"/>
</form>
	<div>
		<span style="color: red;font-size:20px;font-weight:bold;">
		温馨提示：<br>
		1、本清单为异步清单，点击“确认”后，无需停留页面等待，后台完成清单提取后系统会邮件通知。<br>
		2、本次清单提取成功后才能发起下次清单的提取。
		</span>
	</div>

<jsp:include page="/WEB-INF/views/asynclist/share.jsp"></jsp:include>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>