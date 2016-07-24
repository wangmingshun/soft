 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单报表&gt;&gt;客户层清单查询</sf:override>
<sf:override name="head">

<script type="text/javascript">


  $(function(){
	  $("form").validate();

      $("[name=startDate],[name=endDate]").click(function(){
		WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}'});
	  });
	  
	  $("#branchChooseBt").click(function(){
		  showBranchTree($(this).prev(),"03","${userBranchCode}");
		  return false;
	  });
	  
	  $(".inputPara").click(function(){
		  $("#sub").attr("disabled",false);
	  });
	  
	  $("#sub").click(function(){
		  var s1=$("[name='startDate']").val();
		  var s2=$("[name='endDate']").val();
		  s1 = s1.replace(/-/g, "/");
		  s2 = s2.replace(/-/g, "/");
		  s1 = new Date(s1);
		  s2 = new Date(s2);		  
		  var days= s2.getTime() - s1.getTime();		  
		  var time = parseInt(days / (1000 * 60 * 60 * 24));		  
		  if(time>365){
			  alert('请将查询的时间范围缩小到一年内！');
		 	 return false;
		  }
		  if(!checkReportTaskStatus($("#listCode").val())){
	    	   return false;    	   
	      }
	  	  $('#queryForm').submit();		 
	  	  $("#sub").attr('disabled',true);
	  	  return true;
	  });
	  
	  /***增加数据类型选择***/
	  $("#dataType option[value='0']").attr("selected", true);
	  
	  $("#dataType").change(function(){
		  var dataType = $("#dataType").val();
		  if(dataType == '0'){
			  $(".isShow").hide();
		  }else{
			  $(".isShow").show();
		  }
	  });
	  
	  $("#dataType").trigger("change");
  });

</script>
</sf:override>
<sf:override name="content">
<form id="queryForm" action="${ctx}report/task/submit" class="noBlockUI">
<input type="hidden" name="ibatisSqlId" value=".queryVipClientCellInfo" />
<input type="hidden" name="sheetName" value="客户层清单查询" />
<input type="hidden" id="listCode" name="listCode" value="37" />
<table class="layouttable">
	   <tr>
		     <td width="10%" class="layouttable_td_label">
		                   机构：
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <input type="text" name="branchCode" class="{required:true} inputPara"/>
		         <a href="javascript:void(0)" id="branchChooseBt">选择机构</a>
		      </td>
		      <td > 
		      </td>
	    </tr>
	    <tr>
		     <td width="10%" class="layouttable_td_label">
		                   数据类型：
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <select name="dataType" id="dataType">
		          	<option value="0">存量VIP数据</option>
		          	<option value="1">历史VIP数据</option>
		          </select>
		     </td>
		      <td > 
		      </td>
	    </tr>
        <tr>
		     <td width="10%" class="layouttable_td_label">
		     	查询类别：
		     </td>
		     <td width="50%" class="layouttable_td_widget">
             	<select name="queryType" class="isShow">
                	<option value="A">综合</option>
                    <option value="0">不变</option>
                    <option value="1">新增</option>
                    <option value="2">升级</option>
                    <option value="3">降级</option>
                    <option value="4">取消</option>
                </select>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                VIP等级:
                
		      <select name="vipGrade" id="vipGrade">	
		         <option value="">全部</option>	       
		         <c:forEach items="${vipGradeList}" var="item">
                 	<option value="${item.code}">${item.description}</option>
                 </c:forEach>		     
               </select>         
		      </td>
		      <td>
		        </td>
	    </tr>
	    <tr class="isShow">
		      <td width="10%" class="layouttable_td_label">
		         VIP生效时间范围：
		      </td>
		      <td width="50%" class="layouttable_td_widget">
				     <input type="text" name="startDate" id="startDate" class="Wdate {required:function(){if($('#dataType').val()=='0'){return false;}return true;},messages:{required:' 请输入开始日期'}} inputPara" />
				     至
				     <input type="text" name="endDate" id="endDate" class="Wdate {required:function(){if($('#dataType').val()=='0'){return false;}return true;},messages:{required:' 请输入结束日期'}} inputPara" />
		      </td>
	     </tr>
	</table>
	<hr class="hr_default"/>
	<div align="right">
		<a href="javascript:void(0)" class='easyui-linkbutton'  iconCls="icon-ok" id="sub" style="">确定</a>
	</div>
</form>
	<div>
		<span style="color: red;font-size:20px;font-weight:bold;">
		温馨提示：<br>
		1、本清单为异步清单，点击“确认”后，无需停留页面等待，后台完成清单提取后系统会邮件通知。<br>
		2、本次清单提取成功后才能发起下次清单的提取。
		</span>
	</div>
<jsp:include page="/WEB-INF/views/report/share.jsp"></jsp:include>	    
<jsp:include page="/WEB-INF/views/include/branchTree.jsp"></jsp:include>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>