<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单报表&gt;&gt;邮储通知书打印清单</sf:override>
<sf:override name="head">
<script type="text/javascript">


  $(function(){

      $("[name=dateFrom],[name=dateTo]").focus(function(){
		WdatePicker({skin:'whyGreen'});
	  });	  
	  $("[name=startDate],[name=endDate]").focus(function(){
		WdatePicker({skin:'whyGreen'});
	  });
		
      $("#branchChooseBt").click(function(){
		  showBranchTree($(this).prev(),"03","${userBranchCode}");
		  return false;
	  });
  	$("#sub").click(function(){
		var policyNo=$("#policyNo").val();
		var startDate=$("#startDate").val();
		var endDate=$("#endDate").val();
		if(policyNo==''){
			if(startDate==''||endDate==''){
				$.messager.alert("校验错误", "请输入保单号或者明细清单时间范围！");	
				return false;
			}
			if (daysBetween(startDate, endDate) > 31) {
				$.messager.alert("提示", "查询明细清单时间范围不能超过31天。");
				return false;
			}
		}
	    if(!checkReportTaskStatus($("#listCode").val())){
	       return false;    	   
	    }		
		$("#queryForm").submit();
		return false;
	}); 
  	$("#sub1").click(function(){
		var dateFrom=$("#dateFrom").val();
		var dateTo=$("#dateTo").val();
		if(dateFrom==''||dateTo==''){
			$.messager.alert("校验错误", "请输入汇总清单时间范围！");	
			return false;
		}
		$.messager.alert("提示", "系统提交中，请耐心等待返回结果！");			
		$("#queryForm1").submit();	
		return false;
	});    	
  });

</script>
</sf:override>
<sf:override name="content">

<form id="queryForm" action="${ctx}async/task/submit" class="noBlockUI" method="post">
<div class="easyui-panel" title="明细清单">
<input type="hidden" name="ibatisSqlId" value=".selectPostalNotes" />
<input type="hidden" name="sheetName" value="邮储通知书打印清单" />
<input type="hidden" id="listCode" name="listCode" value="20" />
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
	                         时间范围：
	    </td>
	    <td width="50%" class="layouttable_td_widget">
	     <input type="text" name="startDate" id="startDate"  class="Wdate {required:false,messages:{required:' 请输入开始日期'}}" />
	     至
	     <input type="text" name="endDate" id="endDate"  class="Wdate {required:false,messages:{required:' 请输入结束日期'}}" />
	    </td>
	    <td>&nbsp;	      
	    </td>
     </tr>
     <tr>
		<td width="10%" class="layouttable_td_label">
		保单号：
		</td>
		<td width="50%" class="layouttable_td_widget">
		          <input type="text" name="policyNo" id="policyNo" />
		</td>
		<td>&nbsp;
			        
		</td>
	</tr>
     <tr>
		<td width="10%" class="layouttable_td_label">
		信函类型：
		</td>
		<td width="50%" class="layouttable_td_widget">
				    <select id="noteType" name="noteType">					
					    <option value="">全部</option>
					    <option value="1">现金分红红利通知信</option>
					    <option value="2">保额分红红利通知信</option>
					    <option value="10">个人保单年度报告书</option>
					    <option value="11">投资连结保险周年报告</option>
					</select>
		</td>
	    <td align="right">
	       <a  class='easyui-linkbutton' href="javascript:void(0)" iconCls="icon-ok" id="sub">确定</a>
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
</div>		
</form>

<form id="queryForm1" action="${ctx}report/submit" class="noBlockUI">
<div class="easyui-panel" title="汇总清单">
<input type="hidden" name="sql" value="selectPostalNotesGather" />
	<table class="layouttable">
	     <tr>   
	        <td width="10%" class="layouttable_td_label">
	                               日期范围：<span class="requred_font">*</span>
	        </td>
	        <td  class="layouttable_td_widget">
		          <input type="text" name="dateFrom" id="dateFrom" class="Wdate {required:true,messages:{required:' 请输入开始日期'}}" />
	                                  至
	              <input type="text" name="dateTo" id="dateTo" class="Wdate {required:true,messages:{required:' 请输入结束日期'}}" />
	        </td> 
            <td align="right">
                <a  class='easyui-linkbutton' href="javascript:void(0)"  iconCls="icon-ok" id="sub1">确定</a>
	        </td> 
	      </tr>    
	</table>

</div>		
</form>	    	
<jsp:include page="/WEB-INF/views/asynclist/share.jsp"></jsp:include>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>