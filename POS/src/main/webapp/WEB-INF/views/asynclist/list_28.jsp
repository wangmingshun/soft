 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单报表&gt;&gt;协议退费清单</sf:override>
<sf:override name="head">

<script type="text/javascript">
  $(function(){
	  $("form").validate();

      $("[name=startDate],[name=endDate]").focus(function(){
		WdatePicker({skin:'whyGreen'});
	  });
      $("#sub").click(function(event){
  		$('#queryForm').submit();
  		$("#sub").attr("disabled",true);
  		return false;
  	 });
  	$(":input").click(function(){
		$("#sub").removeAttr("disabled");
	});
  });

</script>
</sf:override>
<sf:override name="content">

<form id="queryForm" action="${ctx}async/task/submit" class="noBlockUI" method="post">
<input type="hidden" name="ibatisSqlId" value=".agreementPosListQuery" />
<input type="hidden" name="sheetName" value="协议退费清单" />
<input type="hidden" id="listCode" name="listCode" value="28" />
<table class="layouttable">
    <tr> 
	    <td width="10%" class="layouttable_td_label">
	                         保全完成时间范围：<span class="requred_font">*</span>
	    </td>
	    <td width="50%" class="layouttable_td_widget">
	     <input type="text" name="startDate" class="Wdate {required:true,messages:{required:' 请输入开始日期'}}" />
	     至
	     <input type="text" name="endDate" class="Wdate {required:true,messages:{required:' 请输入结束日期'}}" />
	    </td>
	    <td>&nbsp;
	      
	    </td>
     </tr>
     <tr>
     	<!--  
	    <td width="10%" class="layouttable_td_label">
	                    功能特殊件类型：
	    </td>
	    <td width="50%" class="layouttable_td_widget">
	        <select name="functionType">
	       <c:forEach items="${CodeTable.posTableMap.POS_SPECIAL_FUNC_CODE}" var="item">
	        <c:if test="${item.code eq '2' or item.code eq '9' or item.code eq '10' or item.code eq '11'}"> 
	        	<option value="${item.code}">${item.description}</option>
	        </c:if>
	       </c:forEach>
	     </select>
	     -->
	     <td>
	     <input type ="hidden"  name="policyChannelType"  value="${policyChannelType}"/>
	     </td>
	     
	     <td>&nbsp;
	       
	     </td>
	     <td align="right">
	       <a  class='easyui-linkbutton' href="javascript:void(0)" onclick=""  iconCls="icon-ok" id="sub">确定</a>
	     </td>
    </tr>  
	</table>
	<hr class="hr_default"/>
</form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>