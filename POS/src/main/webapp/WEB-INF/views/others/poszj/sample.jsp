 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">保全质检&gt;&gt;保全质检抽样</sf:override>
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
			openProductServiceItemSelectWindow(function(labelData, valueData){
				//给文本框赋值
				$link.siblings(":text").val(valueData).trigger("change");
			}, $link.siblings(":text").val());
			return false;
		});
		
		$("#sub").click(function(event){
			var start =new Date($("#startDate").val().replace(/-/g,"/"));
			var end =new Date($("#endDate").val().replace(/-/g,"/"));
			if(start>end){
				$.messager.alert("提示", "开始日期不能晚于结束日期!");  
				return false;
			}else if((end.getFullYear()*12+end.getMonth()-(start.getFullYear()*12+start.getMonth()))>6){
				$.messager.alert("提示", "开始时间和结束时间时间间隔最多不超过6个月");  
				return false;
			}else if((end.getFullYear()*12+end.getMonth()-(start.getFullYear()*12+start.getMonth()))== 6 && start.getDate()<end.getDate()){
				$.messager.alert("提示", "开始时间和结束时间时间间隔最多不超过6个月");  
				return false;
			}
			
			
			$('#queryForm').submit();
			return false;
		});
		
		
  });

</script>
</sf:override>
<sf:override name="content">
	<form id="queryForm" action="${ctx}/others/poszj/querypos" class="noBlockUI" method="post">
	<input type="hidden" id="posNo"/>
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
			<td width="10%" class="layouttable_td_label">保全项目：<span class="requred_font">*</span></td>
			<td class="layouttable_td_widget">
										<input type="text" name="serviceItems" 
											  class="{required:true,messages:{required:' 请录入保全项目'}}" />
										 <a href="javascript:void(0);" id="serviceItemsLink">选择</a>
									</td>
             <td>&nbsp;		        
		      </td>
		</tr>
		<tr>
		     <td width="10%" class="layouttable_td_label">
		                     保全件数量：<span class="requred_font">*</span>
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <input type="text" name="posNum"   onkeyup="this.value=this.value.replace(/\D/g,'')"
		          		 class="{required:true,messages:{required:' 请录入保全件数量'}}" />
		     </td>
		     <td>&nbsp;
		        
		      </td>
	    </tr>
	    <tr>
		      <td width="10%" class="layouttable_td_label">
		                           保全完成时间：<span class="requred_font">*</span>
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
		                     保全受理人：
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <input type="text" name="accepteUserId" />
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
<jsp:include page="/WEB-INF/views/include/productServiceItemsSelectWindow.jsp" />
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>