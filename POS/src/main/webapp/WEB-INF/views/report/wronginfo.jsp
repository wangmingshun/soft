 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单报表&gt;&gt;信息不真实保单清单</sf:override>
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
  onSubmit = function() {
	  
    var   branchCode=$("#branchCode").val();
	var   startDate=$("#startDate").val();
	var   endDate=$("#endDate").val();
    var   num=$("#num").val(); 

	if(branchCode==""){			
		
		alert("机构不能为空!");
		return false;
		
	}
	if(branchCode==86){
		
		alert("请使用分公司或以下级别查询!");
		return false;
	}
	if(startDate==""){			
		
		alert("开始时间不能为空!");
		return false;
		
	}
	if(endDate==""){
		alert("结束时间不能为空!");
		return false;
	}
	if(startDate>endDate){
		
		alert("开始时间不能大于结束时间!");
		return false;
	}
	if(num!=""){

	    if((/^(\+|-)?\d+$/.test( num ))&&num>1){  	   
	
	    }else{  	 	    		
		        alert("重复号码次数必须为正整数且必须为2次或以上！");  
			      
		        return false;  
	    	

	    }  

		
	}





	$('#queryForm').submit();	
  }
</script>
</sf:override>
<sf:override name="content">

<form id="queryForm" action="${ctx}report/submit" class="noBlockUI">

<input type="hidden" name="sql" value="wrongInfoPolicyQuery" />

	<table class="layouttable">
	      <tr>
		       <td width="10%" class="layouttable_td_label">
		                      保单所属机构：<span class="requred_font">*</span>
		       </td>
		       <td  width="50%" class="layouttable_td_widget">
		            <input type="text" id="branchCode" name="branchCode" class="{required:true}" value="${userBranchCode}"/>
		            <a href="javascript:void(0)" id="branchChooseBt">选择机构</a>
		       </td>
		       <td>&nbsp;</td>
	     </tr>
	     <tr>   
	        <td width="10%" class="layouttable_td_label">
	                               生效起止时间范围：<span class="requred_font">*</span>
	        </td>
	        <td  class="layouttable_td_widget">
		          <input type="text" id="startDate" name="startDate" class="Wdate {required:true,messages:{required:' 请输入开始日期'}}" />
	                                  至
	              <input type="text" id="endDate" name="endDate" class="Wdate {required:true,messages:{required:' 请输入结束日期'}}" />
	        </td> 
    
	      </tr> 
	      	     <tr>   
	        <td width="10%" class="layouttable_td_label">
	                               重复号码次数：<span class="requred_font"></span>
	        </td>
	        <td  class="layouttable_td_widget">
		      
            <input type="text" id="num" name="num" size="20"  />

			</select>
	        </td> 
            <td align="right">
            
	                 <a  class='easyui-linkbutton' href="javascript:void(0)" onclick="onSubmit()"  iconCls="icon-ok" id="sub">确定</a>
	     
	        </td> 
	      </tr>    
	</table>

 
</form>
<hr class="hr_default"/>
<jsp:include page="/WEB-INF/views/report/share.jsp"></jsp:include>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>