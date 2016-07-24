 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">保全质检&gt;&gt;质检结果录入</sf:override>
<sf:override name="head">

<script type="text/javascript">
  $(function(){
	  $("form").validate();

      $("[name=startDate],[name=endDate]").focus(function(){
		WdatePicker({skin:'whyGreen'});
	  });
      
       if("N"=="${posInfo.RESULTSET}"){
     	  $(this).find("[name=prot]").show();
     	  $("#problems").attr("checked",true);
     	 // $("#payType")[0].selectedIndex = 2;
     	  $("#payType").each(function(){
               $(this).children("option").each(function(){
                if($(this).val()=="${posInfo.PAYTYPE}"){
                  $(this).attr("selected","selected");
                }  
           	 });
     	   });
       }else{
     	  $(this).find("[name=prot]").hide();
     	  if("Y"=="${posInfo.RESULTSET}"){
     		  $("#pass").attr("checked",true);
     	  }
       } 
      
      
      
      $(this).find("[name=resultSet]").click(function(){
    	  if("Y"==$("input[name=resultSet]:checked").val()){
    		  $("tr[name='prot']").hide();
    		  $("#problem").val("");
    		  $("#score").val("");
    		  $("#payType")[0].selectedIndex = 0;
    	  }else{
    		  $("tr[name='prot']").show();
    	  }
      })
      
      $("#sub").click(function(){
    	  if($("#score").val()==""){
    		  $.messager.alert("提示", "分数不能为空!"); 
    		  return ;
    	  }
    	  
    	  if("Y"==$("input[name=resultSet]:checked").val()){
    		  $("#payType").empty(); 
    	  }
    	  $("#queryForm").submit();
    	  return false;
      })
      

      $("#score").bind("input propertychange", function() { 
    	  if(parseInt($("#score").val())>100){
    		  $.messager.alert("提示", "分数不能超过100分!");  
    		  $("#score").val('')
    	  }
	  }); 
      
      $("#posId").click(function(){
    	  window.open("${GQ_URL}SL_GQS/GQQuery.do?type=posChangeInfo&pos_no="+$("#posId").text()+"&isShowBackFlag=N");
    	  //window.location.href = "${GQ_URL}SL_GQS/GQQuery.do?type=posChangeInfo&pos_no="+$("#posId").text()+"&isShowBackFlag=N";
    	  return false;
      });
     
  });

</script>
</sf:override>
<sf:override name="content">
	<form id="queryForm" action="${ctx}others/poszj/addZJ" class="noBlockUI" method="post">
	<input type="hidden" name="posNo" value="${posInfo.posNo}" /> 
	<input type="hidden" name="take" value="${posInfo.take}" /> 
	<table class="layouttable" id="tab" >
		<tr>
			 <td width="10%" class="layouttable_td_label">
		                    保全号：
		     </td>
		     <td>
		     	<!--  <a href="javascript:void(0);" id="posId" >${posInfo.posNo}</a>-->
		     	${posInfo.posNo}
		     </td>
		     <td>&nbsp;

		      </td>
		</tr>
		<tr>
		     <td width="10%" class="layouttable_td_label">
		                    质检结果：
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		     	<input type="radio" id="pass" name="resultSet"  value="Y" checked >通过</input> 
				<input type="radio" id="problems" name="resultSet"value="N">问题件</input>
			 </td>
		     <td>&nbsp;

		      </td>
	    </tr>
	    <tr name="prot">
		     <td width="10%" class="layouttable_td_label">
		                     问题件类型： 
		     </td>
	          <td width="50%" class="layouttable_td_widget">
	              <select  id="payType" name="payType">
								<option value="01" >申请书填写问题</option>
								<option value="02" >系统操作不规范</option>
								<option value="03" >影像问题</option>
								<option value="04" >资料不完整或有误</option>
								<option value="05" >证件审核问题</option>
								<option value="06" >其他</option>
				</select>
			  </td>
		      <td>&nbsp;
		        
		      </td>
	    </tr>
	   <tr name="prot">
		     <td width="10%" class="layouttable_td_label">
		                     问题件描述栏：
		     </td>
		     <td width="50%" class="layouttable_td_widget" align="center" >
		          <textarea rows="5" cols="100" id="problem"  name="problem"  >${posInfo.PROBLEM}</textarea>
		      </td>
		      <td>&nbsp;
		        
		      </td>
	    </tr>
	   <tr>
		     <td width="10%" class="layouttable_td_label">
		                     分数：<span class="requred_font">*</span>
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <input type="text" name="score" id="score" value="${posInfo.SCORE}"
		          		 class="easyui-numberbox"  />
		      </td>
		      <td>&nbsp;
		        
		      </td>
		      
	    </tr>
	    <tr>
	      <td align="right" colspan="3" >
	         	 <a  class='easyui-linkbutton' href="javascript:void(0)" onclick=""  iconCls="icon-ok" id="sub">确定</a>
	      </td>
	    </tr>
	</table>
	<hr class="hr_default"/>
	</form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>