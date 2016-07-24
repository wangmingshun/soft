 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单报表&gt;&gt;重复电话号码停用清单</sf:override>
<sf:override name="head">

<script type="text/javascript">
  $(function(){
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

<form id="queryForm" action="${ctx}async/task/submit" class="noBlockUI">
	<input type="hidden" name="ibatisSqlId" value=".processListQuery" />
	<input type="hidden" name="sheetName" value="重复电话号码停用清单" />
	<input type="hidden" id="listCode" name="listCode" value="31" />

	<table class="layouttable">
	      <tr>
		       <td width="10%" class="layouttable_td_label">
		                      查询机构：<span class="requred_font">*</span>
		       </td>
		       <td  width="50%" class="layouttable_td_widget">
		            <input type="text" name="branchCode" class="{required:true}" value="${userBranchCode}"/>
		            <a href="javascript:void(0)" id="branchChooseBt">选择机构</a>
		       </td>
		       <td>&nbsp;</td>
	     </tr>
	     <tr>   
	        <td width="10%" class="layouttable_td_label">
	                               停用时间范围：<span class="requred_font">*</span>
	        </td>
	        <td  class="layouttable_td_widget">
		          <input type="text" name="startDate" class="Wdate {required:true,messages:{required:' 请输入开始日期'}}" />
	                                  至
	              <input type="text" name="endDate" class="Wdate {required:true,messages:{required:' 请输入结束日期'}}" />
	        </td> 
	        <td>&nbsp;</td>  
	        <td align="right">
	        	<a  class='easyui-linkbutton' href="javascript:void(0)" onclick="$('#queryForm').submit();return false;"  iconCls="icon-ok" id="sub">确定</a>
	        </td>
	     </tr>
	        
	</table>


</form>
<hr class="hr_default"/>
<jsp:include page="/WEB-INF/views/asynclist/share.jsp"></jsp:include>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>