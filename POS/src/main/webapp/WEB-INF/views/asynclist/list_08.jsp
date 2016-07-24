 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单报表&gt;&gt;转账结果清单</sf:override>
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
	  
	  $("#bankChooseBt").click(function(){
			var $bankCode = $(this).prev();
			showBankSelectWindow($bankCode, function(code, label){});
			return false;
	  });
		
  });

</script>
</sf:override>
<sf:override name="content">
<form id="queryForm" action="${ctx}async/task/submit" class="noBlockUI">
	<input type="hidden" name="sql" value="transferFailPosListQuery" />
	<input type="hidden" name="sheetName" value="转账结果清单" />
	<input type="hidden" id="listCode" name="listCode" value="08" />
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
			                           保全受理时间范围：<span class="requred_font">*</span>
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
			          <select name="bizType">
				        <option value="0">全部</option>
				        <option value="1">补费</option>
				        <option value="2">退费</option>
				      </select>
			     </td>
			     <td>&nbsp;
			        
			      </td>
		    </tr>    
		    <tr>
		     <td width="10%" class="layouttable_td_label">
		                    保全受理人:
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <input type="text" name="accepteUserId" />
		     </td>
		     <td>&nbsp;
		        
		      </td>
		   </tr>
		    
		    <tr>
		     <td width="10%" class="layouttable_td_label">
		                   银行代码：
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <input type="text" name="bankCode" readonly="readonly"/>
		           <a  id="bankChooseBt" href="javascript:void(0)">选择银行</a>
		     </td>
		     <td>&nbsp;
		        
		      </td>
		   </tr>
		   
		   <tr>
		     <td width="10%" class="layouttable_td_label">
		                  受理渠道：
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <select name="acceptChannel">
                  	<option value="">全部</option>
					   <c:forEach items="${CodeTable.posTableMap.POS_ACCEPT_CHANNEL_CODE}" var="item">
					          <option value="${item.code}">${item.description}</option>
					   </c:forEach>
			      </select>
		     </td>
		     <td>&nbsp;
		        
		      </td>
		   </tr>		    
		   <tr>
			     <td width="10%" class="layouttable_td_label">
			                转账状态：
			     </td>
			     <td width="50%" class="layouttable_td_widget">
			         <select name="transStatus">
				        <option value="">全部</option>
				        <option value="Y">成功</option>
				        <option value="N">失败</option>
				        <option value="P">在途</option>
				      </select>
				      <input type ="hidden"  name="policyChannelType"  value="${policyChannelType}"/>
			     </td>
			     <td align="right">
		          <a  class='easyui-linkbutton' href="javascript:void(0)" onclick="$('#queryForm').submit();return false;"  iconCls="icon-ok" id="sub">确定</a>
		         </td>
		    </tr>
		</table>
		<hr class="hr_default"/>
	</form>
<jsp:include page="/WEB-INF/views/asynclist/share.jsp"></jsp:include>
<jsp:include page="/WEB-INF/views/include/bankSelectWindow.jsp"></jsp:include>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>