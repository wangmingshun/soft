 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单报表&gt;&gt;问题件跟踪清单</sf:override>
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
<form id="queryForm" action="${ctx}report/new/submit" class="noBlockUI" method="post">
<input type="hidden" name="ibatisSqlId" value=".posProblemListQuery" />
<input type="hidden" name="sheetName" value="问题件跟踪清单" />
<table class="layouttable">
    <tr>
     <td width="10%" class="layouttable_td_label">
                   保单所在机构：<span class="requred_font">*</span>
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
                          问题件下发时间范围：<span class="requred_font">*</span>
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
                 问题件状态：
     </td>
     <td width="50%" class="layouttable_td_widget">
       <select name="problemStatus">
	       <option value="0">全部</option>
	       <option value="1">待处理</option>
	       <option value="2">待函件回销</option>
	       <option value="3">已函件回销</option>
	       <option value="4">已处理</option>
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
<jsp:include page="/WEB-INF/views/report/share.jsp"></jsp:include>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>