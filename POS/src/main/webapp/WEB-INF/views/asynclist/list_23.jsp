 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单报表&gt;&gt;保全影像扫描清单</sf:override>
<sf:override name="head">

<script type="text/javascript">


  $(function(){
      
      $("[name=startDate],[name=endDate]").focus(function(){
		WdatePicker({skin:'whyGreen'});
	  });
	  
	  $("#branchChooseBt").click(function(){
		  showBranchTree($(this).prev(),"03","${userBranchCode}");
		  return false;
	  });
  });
  dataValidateSubmit = function() {
			var dateStart = $("#startDate").val();
			var dateEnd = $("#endDate").val();		
	
			if(daysBetween(dateStart, dateEnd) > 60) {
				$.messager.alert("提示", "查询时间范围不能超过60天。");
				return false;
			}	
		    if(!checkReportTaskStatus($("#listCode").val())){
			       return false;    	   
			}			
	$('#queryForm').submit();	
		return true;
	};
	

	
	
	

</script>
</sf:override>
<sf:override name="content">
	<form id="queryForm" action="${ctx}async/task/submit" class="noBlockUI" method="post" >
	<input type="hidden" name="ibatisSqlId" value=".imageListQuery" />
	<input type="hidden" name="sheetName" value="保全影像扫描清单" />
	<input type="hidden" id="listCode" name="listCode" value="23" />
	<table class="layouttable">
	   <tr>
		     <td width="10%" class="layouttable_td_label">
		                      查询机构1：<span class="requred_font">*</span>
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
		                           保全受理时间范围：<span class="requred_font">*</span>
		      </td>
		      <td width="50%" class="layouttable_td_widget">
				     <input type="text"    id="startDate"  name="startDate" class="Wdate {required:true,messages:{required:' 请输入开始日期'}}" />
				     至
				     <input type="text"   id="endDate"   name="endDate" class="Wdate {required:true,messages:{required:' 请输入结束日期'}}" />
		      </td>
		      <td>&nbsp;
		        
		      </td>
	     </tr>
	     
	    <tr>
		     <td width="10%" class="layouttable_td_label">
		                     保全受理人：
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <input type="text" name="accepteUserId" />
		     </td>
		     <td align="right">
	          <a  class='easyui-linkbutton' href="javascript:void(0)" onclick="dataValidateSubmit();return false;"  iconCls="icon-ok" id="sub">确定</a>
	         </td>
	    </tr>
	     <tr>
		     <td width="100%" colspan="3">
				<span style="color: red;font-size:20px;font-weight:bold;">
				温馨提示：<br>
				1、本明细清单为异步清单，点击“确认”后，无需停留页面等待，后台完成清单提取后系统会邮件通知。<br>
				2、本次清单提取成功后才能发起下次清单的提取。
				</span>
	         </td>
	    </tr>
	</table>
	<hr class="hr_default"/>
	</form>
<jsp:include page="/WEB-INF/views/asynclist/share.jsp"></jsp:include>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>