 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单报表&gt;&gt;预计超停清单</sf:override>
<sf:override name="head">

<script type="text/javascript">
  $(function(){
	  $("form").validate();

      $("[name=startDate],[name=endDate]").focus(function(){
		WdatePicker({skin:'whyGreen'});
	  });
		
  });

</script>
</sf:override>
<sf:override name="content">

<form id="queryForm"  action="${ctx}report/submit" class="noBlockUI">
<input type="hidden" name="sql" value="policyToLapseListQuery" />
	<table class="layouttable">
		    <tr>
			     <td width="10%" class="layouttable_td_label">
			                     业务类型：
			     </td>
			     <td width="50%" class="layouttable_td_widget">
			          <select name="bizType">
				        <option value="0">全部</option>
				        <option value="1">自垫</option>
				        <option value="2">贷款</option>
				      </select>
			      </td>
			      <td>&nbsp;
			        
			      </td>
		    </tr>
		     <tr>
			     <td width="10%" class="layouttable_td_label">
			                  保单来源：
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
			                          预计超停起止日期范围：<span class="requred_font">*</span>
			      </td>
			      <td width="50%" class="layouttable_td_widget">
					     <input type="text" name="startDate" class="Wdate {required:true,messages:{required:' 请输入开始日期'}}" />
					     至
					     <input type="text" name="endDate" class="Wdate {required:true,messages:{required:' 请输入结束日期'}}" />
			      </td>
			      <td align="right">
			       <a  class='easyui-linkbutton' href="javascript:void(0)" onclick="$('#queryForm').submit();return false;"  iconCls="icon-ok" id="sub">确定</a>
			      </td>
		     </tr>
		</table>
		<hr class="hr_default"/>
</form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>