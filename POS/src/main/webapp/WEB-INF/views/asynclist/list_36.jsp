 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单报表&gt;&gt;银代前置保全处理清单</sf:override>
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
	  
		/* 保全项目选择按钮click事件处理，弹出保全项目选择窗口 */
		$("#serviceItemsLink").click(function(event){
			var $link = $(this);
			var items = [1,2,10];//控制该页面只能显示的保全项目
			var flag = false;//记录弹出框是否全选了
			openProductServiceItemSelectWindow(function(labelData, valueData){
				//给文本框赋值
				$link.siblings(":text").val(flag == true ? items.join(",") : valueData);
			}, "");
			//此处加入全选框改变事件，记录下最终的状态，方便给文本框赋值。默认全选框是所有保全项目，该页面只允许录入特定的保全
			$(".selectAllCheckBox").change(function() {
				if($(".selectAllCheckBox").attr("checked") == true) 
					flag = true;
				else 
					flag = false;
			});
			var $checkboxes = $("#productServiceItemsSelectWindowDiv :checkbox:not(.selectAllCheckBox)");
			$checkboxes.each(function() {
				if(items.indexOf($(this).val()) == -1)
					$(this).closest("div").hide();
			});
			return false;
		});
		
		
  });
  dataValidateSubmit = function() {
		var dateStart = $("#startDate").val();
		var dateEnd = $("#endDate").val();
		
		if (daysBetween(dateStart, dateEnd) > 31) {
			$.messager.alert("提示", "查询时间范围不能超过31天。");
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
	<form id="queryForm" action="${ctx}async/task/submit" class="noBlockUI" method="post">
	<input type="hidden" name="sheetName" value="银代前置保全处理清单" />
	<input type="hidden" id="listCode" name="listCode" value="36" />
	<table class="layouttable">
	   <tr>
		     <td width="10%" class="layouttable_td_label">
		                      查询机构：<span class="requred_font">*</span>
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <input type="text" name="branchCode"  id="branchCode" class="{required:true}" value="${userBranchCode}"/>
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
				     <input type="text" name="startDate"  id="startDate" class="Wdate {required:true,messages:{required:' 请输入开始日期'}}" />
				     至
				     <input type="text" name="endDate" id="endDate" class="Wdate {required:true,messages:{required:' 请输入结束日期'}}" />
		      </td>
		      <td>&nbsp;
		        
		      </td>
	     </tr>
	     
	     <tr>
			<td width="10%" class="layouttable_td_label">银行渠道：</td>
			<td class="layouttable_td_widget">
				<select name="bankCategory" >
			       <option value="">全部</option>
			       <c:forEach items="${CodeTable.posTableMap.POS_BANK_CATEGORY}" var="item" >
			         <option value="${item.code}">${item.description}</option>
			       </c:forEach>
	     		</select>
			</td>
            <td>&nbsp;
            
            </td>
		</tr>
	     
		 <tr>
			<td width="10%" class="layouttable_td_label">保全项目：</td>
			<td class="layouttable_td_widget">
				<input type="text" name="serviceItems" value="1,2,10"/>
				<a href="javascript:void(0);" id="serviceItemsLink">选择</a>
			</td>
            <td>&nbsp;
            
            </td>
		</tr>
	    
	    <tr>
		     <td width="10%" class="layouttable_td_label">
		                     保全状态：
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <select name="acceptStatus">
		          	<option value="">全部</option>
			        <option value="1">已完成</option>
			        <option value="0">未完成</option>
			      </select>
		     </td>
		     <td align="right">
	          <a  class='easyui-linkbutton' href="javascript:void(0)" onclick="dataValidateSubmit();return false;"  iconCls="icon-ok" id="sub">确定</a>
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
<jsp:include page="/WEB-INF/views/asynclist/share.jsp"></jsp:include>
<jsp:include page="/WEB-INF/views/include/productServiceItemsSelectWindow.jsp" />
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>