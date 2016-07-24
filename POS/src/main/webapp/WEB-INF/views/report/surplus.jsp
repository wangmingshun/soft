 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单报表&gt;&gt;保全贴费业务清单</sf:override>
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
    	$("#sub").click(function(){
    		var startDate=$("#startDate").val();
    		var endDate=$("#endDate").val();
			if (daysBetween(startDate, endDate) > 31) {
				$.messager.alert("提示", "查询时间范围不能超过31天。");
				return false;
			}
    	    if(!checkReportTaskStatus($("#listCode").val())){
    	       return false;    	   
    	    }		
    		$("form").submit();
    		return false;
    	});        
  });

</script>
</sf:override>
<sf:override name="content">

<form id="queryForm" action="${ctx}report/task/submit" class="noBlockUI" method="post">
<input type="hidden" name="ibatisSqlId" value=".surplusListQuery" />
<input type="hidden" name="sheetName" value="保全贴费业务清单" />
<input type="hidden" id="listCode" name="listCode" value="29" />
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
		保单渠道：
		</td>
		<td width="50%" class="layouttable_td_widget">
			<select name="policyChannel">
				<option value="">全部</option>
				<c:forEach items="${CodeTable.posTableMap.CHANNEL_TYPE_TBL}" var="item">
				    <option value="${item.code}">${item.description}</option>
				</c:forEach>
			</select>
		</td>
		<td>&nbsp;
			        
		</td>
	</tr>
	
    <tr>
	    <td width="10%" class="layouttable_td_label">
	                         保全生效时间范围：<span class="requred_font">*</span>
	    </td>
	    <td width="50%" class="layouttable_td_widget">
	     <input type="text" name="startDate" id="startDate" class="Wdate {required:true,messages:{required:' 请输入开始日期'}}" />
	     至
	     <input type="text" name="endDate"  id="endDate" class="Wdate {required:true,messages:{required:' 请输入结束日期'}}" />
	    </td>
	    <td>&nbsp;
	      
	    </td>
	    <td align="right">
	       <a  class='easyui-linkbutton' href="javascript:void(0)" iconCls="icon-ok" id="sub">确定</a>
	    </td>
     </tr>
	</table>
	<hr class="hr_default"/>
</form>
	     <tr>
		     <td>
				<span style="color: red;font-size:20px;font-weight:bold;">
				温馨提示：<br>
				1、本清单为异步清单，点击“确认”后，无需停留页面等待，后台完成清单提取后系统会邮件通知。<br>
				2、本次清单提取成功后才能发起下次清单的提取。
				</span>
	         </td>
	    </tr>	
<jsp:include page="/WEB-INF/views/report/share.jsp"></jsp:include>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>