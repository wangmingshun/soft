 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单报表&gt;&gt;质押贷款业务清单</sf:override>
<sf:override name="head">

<script type="text/javascript">
var prodRes = eval('${products}');//可选险种源json
  $(function(){
	  $("form").validate();

      $("[name=startDate],[name=endDate]").focus(function(){
		WdatePicker({skin:'whyGreen'});
	  });
	  
	  $("#branchChooseBt").click(function(){
		  showBranchTree($(this).prev(),"03","${userBranchCode}");
		  return false;
	  });
	  $("#productInput").autocomplete({
			source:$.map(prodRes,function(it){
                return it.FULL_NAME;
			}),
			select:function(e,ui){
			var v = ui.item.value;
			v = v.substring(v.lastIndexOf("(")+1,v.lastIndexOf(")"));
			$(this).next().val(v);
		}
	  });
				
  });

</script>
</sf:override>
<sf:override name="content">
<form id="queryForm" action="${ctx}report/submit" class="noBlockUI">
<input type="hidden" name="sql" value="mortgagePosListQuery" />
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
		                     贷款险种：
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <input type="text" id="productInput" size="52" />
                  <input type="hidden" name="productCode" />
		     </td>
		     <td>&nbsp;
		        
		      </td>
	    </tr>    
	     <tr>
		     <td width="10%" class="layouttable_td_label">
		                   银行网点：
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <input type="text" name="bankCode" />
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