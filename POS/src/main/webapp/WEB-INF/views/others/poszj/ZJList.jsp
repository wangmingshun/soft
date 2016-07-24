 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">保全质检&gt;&gt;质检清单</sf:override>
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
  
     $("#sub").click(function(event){
		$('#queryForm').submit();
		return false;
	});
		
  });

</script>
</sf:override>
<sf:override name="content">
	<form id="queryForm" action="${ctx}/report/new/submit" class="noBlockUI" method="post">
	<input type="hidden" name="ibatisSqlId" value=".queryQualityList" />
	<input type="hidden" name="sheetName" value="质检清单" />
	<table class="layouttable">
	
	   <tr>
		     <td width="10%" class="layouttable_td_label">
		                      查询机构：<span class="requred_font">*</span>
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <input type="text" name="branchCode" class="{required:true,messages:{required:' 请录入机构'}}" value="${userBranchCode}"/>
		          <a href="javascript:void(0)" id="branchChooseBt">选择机构</a>
		      </td>
		      <td>&nbsp;
		        
		      </td>
	    </tr>
	    <tr>
		      <td width="10%" class="layouttable_td_label">
		                           质检时间：<span class="requred_font">*</span>
		      </td>
		     <td width="50%" class="layouttable_td_widget">
				     <input type="text" id="startDate" name="startDate" class="Wdate {required:true,messages:{required:' 请输入开始日期'}}" />
				     至
				     <input type="text" id="endDate" name="endDate" class="Wdate {required:true,messages:{required:' 请输入结束日期'}}" />
		      </td>
		      <td>&nbsp;
		        
		      </td>
		      
	    </tr>
	    <tr>
		     <td width="10%" class="layouttable_td_label">
		                     保全质检人：
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <input type="text" name="acceptor" value="${userID}"/>
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
<jsp:include page="/WEB-INF/views/report/share.jsp"></jsp:include>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>