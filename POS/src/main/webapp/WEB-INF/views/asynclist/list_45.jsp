 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单报表&gt;&gt;线上保全处理清单</sf:override>
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
	  
	  $("#counterChooseBt").click(function(){
		showCounterWindow($(this).prev(),"${userBranchCode}");
		return false;
	  });
	  
		/* 保全项目选择按钮click事件处理，弹出保全项目选择窗口 */
		$("#serviceItemsLink").click(function(event){
			var $link = $(this);
			openProductServiceItemSelectWindow(function(labelData, valueData){
				//给文本框赋值
				$link.siblings(":text").val(valueData).trigger("change");
			}, $link.siblings(":text").val());
			return false;
		});
		
		
  });
  dataValidateSubmit = function() {
		var dateStart = $("#startDate").val();
		var dateEnd = $("#endDate").val();
		var branchCode= $("#branchCode").val();
		var acceptChannel= $("#acceptChannel").val();
		var time = new Date(); //获得当前时间
		var curhour= time.getHours();
		if (branchCode=='86'&&acceptChannel!='7'&&acceptChannel!='11'){
			$.messager.alert("提示", "此清单不能整个机构进行查询！");
			return false;
		}		
		if (daysBetween(dateStart, dateEnd) > 31) {
			$.messager.alert("提示", "查询时间范围不能超过31天。");
			return false;
		}
		/*if (9<=curhour && curhour<18) {
			$.messager.alert("提示", "9点至18点之间不能查询");
			return false;
		}*/
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
	<input type="hidden" name="ibatisSqlId" value=".processListQuery" />
	<input type="hidden" name="sheetName" value="保全处理清单" />
	<input type="hidden" id="listCode" name="listCode" value="45" />
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
			<td width="10%" class="layouttable_td_label">保单渠道：</td>
			<td width="50%" class="layouttable_td_widget"><select
				name="policyChannel" id="policyChannel">
				<option value="">全部</option>
				<c:forEach items="${CodeTable.posTableMap.CHANNEL_TYPE_TBL}"
					var="item">
					<option value="${item.code}">${item.description}</option>
				</c:forEach>
			</select></td>
             <td>&nbsp;		        
		      </td>
		</tr>
		
		 <tr>
			<td width="10%" class="layouttable_td_label">受理渠道：</td>
			<td width="50%" class="layouttable_td_widget"><select
				name="acceptChannel" id="acceptChannel">
				        <option value="">全部</option>		
					    <option value="7">网络</option>		 
					    <option value="11">移动保全</option>
					    <option value="14">淘宝受理</option>
					    <option value="15">自助终端</option>
					    <option value="16">微信受理</option>
					    <option value="17">网银(除招行网银)</option>
					    <option value="18">银行自助终端</option>
					    <option value="19">银基通</option>
					    <option value="24">百度受理</option>
					    <option value="25">手机银行</option>
					    <option value="26">银行电商</option>
					    <option value="27">银保APP</option>
					    <option value="28">招行网银</option>
					    <option value="29">陆金所受理</option>
					    <option value="30">苏宁易购</option>
					    <option value="32">平安财富宝</option>
			</select></td>
             <td>&nbsp;		        
		      </td>
		</tr>
		 <tr>
			<td width="10%" class="layouttable_td_label">保全项目：</td>
			<td class="layouttable_td_widget">
										<input type="text" name="serviceItems" />
										 <a href="javascript:void(0);" id="serviceItemsLink">选择</a>
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
<jsp:include page="/WEB-INF/views/include/counterChoose.jsp"></jsp:include>
<jsp:include page="/WEB-INF/views/include/productServiceItemsSelectWindow.jsp" />
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>