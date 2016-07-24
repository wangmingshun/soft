<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单报表&gt;&gt;受理状态修改清单</sf:override>
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
<form id="queryForm" action="${ctx}async/task/submit" class="noBlockUI" method="post">
<input type="hidden" name="ibatisSqlId" value=".stateChangeList" />
<input type="hidden" name="sheetName" value="受理状态修改清单" />
<input type="hidden" id="listCode" name="listCode" value="21" />
<table class="layouttable">
	   <tr>
		     <td width="10%" class="layouttable_td_label">
		                      查询机构：<span class="requred_font">*</span>
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <input type="text" name="branchCode" class="{required:true}" value="${userBranchCode}"/>
		          <a href="javascript:void(0)" id="branchChooseBt">选择机构</a>
		      </td>
		      <td>&nbsp;
		        
		      </td>
	    </tr>
	    <tr>
		      <td width="10%" class="layouttable_td_label">
		                           状态修改完成时间：<span class="requred_font">*</span>
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
		     <td width="10%" class="layouttable_td_label">
		                  查询类型：
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <select name="updateType">
			         <option value="">全部</option>
			         <option value="1">保单打印状态修改</option>			        
			         <option value="2">受理规则修改</option>
			          <option value="3">投保规则/处理规则状态修改</option>
			      </select>
		     </td>
		     <td align="right">
	          <a  class='easyui-linkbutton' href="javascript:void(0)" onclick="$('#queryForm').submit();return false;"  iconCls="icon-ok" id="sub">确定</a>
	         </td>
	    </tr>
	</table>
	<hr class="hr_default"/>
</form>
<jsp:include page="/WEB-INF/views/asynclist/share.jsp"></jsp:include>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>