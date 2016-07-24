
<%@ page contentType="text/html;charset=UTF-8"%> 

<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">辅助功能&gt;&gt;密码接收人员设置</sf:override>
   
<sf:override name="head">
	<script type="text/javascript">
	$(function() {                 

		$("[name=startDate],[name=endDate]").focus(function() {
			WdatePicker({
				skin : 'whyGreen'
			});
		});

		$("#branchChooseBt").click(function() {
			showBranchTree($(this).prev(), "03", "${userBranchCode}");
			return false;
		});
	});
	dataValidateInsertSubmit = function() {
	
		
		var branchCode=$("#branchCode").val();
        var deptNo=$("#deptNo").val();
        var contact=$("#contact").val();
        var mobileNo=$("#mobileNo").val(); 
        var hcontact=$("#hcontact").val();
        var hmobileNo=$("#hmobileNo").val(); 
        
		if(branchCode==""){						
			alert("机构不能为空!");
			return false;
			
		}
		if(deptNo==""){
			alert("银行渠道不能为空!");
			return false;
		}
		if(contact==""){
			alert("联系人姓名不能为空!");
			return false;
		}

		if(mobileNo==""){
			alert("手机号码不能为空!");
			return false;
		}
         
		if(mobileNo.length!=11){
			
			alert("手机号长度必须为11位!");
			return false;
		}

		if(isNaN(mobileNo)){
			
			alert("手机号必须为数字!");
			return false;
		}
		
		
		if((contact==hcontact&&mobileNo==hmobileNo)||(hcontact==""&&hmobileNo=="")){
			
	        $.post("${ctx}others/mucode/submit",
	        		{contact:$("#contact").val(),
	        	     deptNo:$("#deptNo").val(),
	        	     branchCode:$("#branchCode").val(),
	        	     mobileNo:$("#mobileNo").val()}, function(data){
	        	    		
		        	
	        	    if(data.flag=="YBRANCHCODE"){
	        	    	
	        	    	alert("该机构已经设置过银代联系人,不能重复设置！");
	        	    } 	 
		        
		        	if(data.flag=="Y"){
		        	 	alert("密码接收人员设置成功！");
		        	 	
						$("#hcontact").val(contact);
						$("#hmobileNo").val(mobileNo);
		        		
		        	}  
		        	if(data.flag=="2"){
		        		
		        		alert("手机号码格式不正确,请重新输入!");
		        	}
		        	$('#queryForm').submit();	
			});
		
			return true;
		}
		else{
			
	        $.post("${ctx}others/mucode/updateName",
	        		{contact:$("#contact").val(),
	        	     deptNo:$("#deptNo").val(),
	        	     branchCode:$("#branchCode").val(),
	        	     mobileNo:$("#mobileNo").val()}, function(data){
		      
		        	if(data.flag=="1"){
		        	 	alert("修改成功！");
		        	 	
						$("#hcontact").val(data.rcontact);
						$("#hmobileNo").val(data.rmobileNo);
		        	}  
		        	if(data.flag=="2"){
		        		
		        		alert("手机号码格式不正确,请重新输入!");
		        	}
		        	$('#queryForm').submit();	
	
			});
			
		}
	
	};
	


	 function checkName(){
		 
		
			
			$.post("${ctx}others/mucode/checkName",{contact:$("#contact").val(),deptNo:$("#deptNo").val(),branchCode:$("#branchCode").val()}, function(data){
				
				
				if(data.flag == "Y") {					
				
					$("#contact").val(data.CONTACT);
					$("#mobileNo").val(data.MOBILE_NO);
					
					
					$("#contact").attr("disabled", true);
					$("#mobileNo").attr("disabled", true);
					
					
					$("#hcontact").val(data.CONTACT);
					$("#hmobileNo").val(data.MOBILE_NO);
					
				}
				else{
						
					$("#contact").attr("disabled", false);
					$("#mobileNo").attr("disabled", false);

					
				}
		
			});
			return false;
	 }
	 function onCheckName(){
		 			
			
			$.post("${ctx}others/mucode/checkName",{deptNo:$("#deptNo").val(),branchCode:$("#branchCode").val()}, function(data){			
				
				if(data.flag == "Y") {					
				
					$("#contact").val(data.CONTACT);
					$("#mobileNo").val(data.MOBILE_NO);
					
					
					$("#contact").attr("disabled", true);
					$("#mobileNo").attr("disabled", true);
					
					$("#hcontact").val(data.CONTACT);
					$("#hmobileNo").val(data.MOBILE_NO);
				}
				else{
								
				
					$("#contact").attr("disabled", false);
					$("#mobileNo").attr("disabled", false);
					$("#contact").val("");
					$("#mobileNo").val("");
					
					
					
					$("#hcontact").val("");
					$("#hmobileNo").val("");
					
				}
		
			});
			return false;
	 }
	 function dataValidateUpdateSubmit(){
		 
			$("#contact").attr("disabled", false);
			$("#mobileNo").attr("disabled", false);
			
	
	 }

</script>
</sf:override>
<sf:override name="content">
	<form id="queryForm" name="queryForm" action="${ctx}/others/mucode" class="noBlockUI"
		method="post"><input type="hidden" name="ibatisSqlId"
		value=".effectiveListQuery" /> <input type="hidden" name="sheetName"
		value="密码接收人员设置" />
	<table class="layouttable">
	
       <input type="hidden" id="hcontact" name="hcontact" size="20"  />
       <input type="hidden" id="hmobileNo" name="hmobileNo" size="20" />
		<tr>
		     <td width="10%" class="layouttable_td_label">
		                      查询机构：<span class="requred_font">*</span>
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <input type="text" id="branchCode" name="branchCode" class="{required:true}" value="${userBranchCode}" onpropertychange="onCheckName()"/>
		          <a href="javascript:void(0)" id="branchChooseBt" >选择机构</a>
		      </td>
		      <td>&nbsp;
		        
		      </td>
		</tr>

		<tr>
			<td width="10%" class="layouttable_td_label">银行渠道：<span
				class="requred_font">*</span></td>

			<td width="20%" class="layouttable_td_widget">
					
			<select
				name="deptNo" id="deptNo"  onchange="onCheckName()">
				<option value="">全部</option>
				<c:forEach items="${CodeTable.posTableMap.DEPARTMENT_INFO}"
					var="item">
					<option value="${item.code}">${item.description}</option>
				</c:forEach>

			</select>

			<td>&nbsp;</td>
		</tr>


		<tr>
			<td width="10%" class="layouttable_td_label">联系人姓名：<span
				class="requred_font">*</span></td>
			<td width="50%" class="layouttable_td_widget"><input type="text"
				id="contact" name="contact" size="20"  onblur="checkName()"/></td>
		
			<td>&nbsp;</td>

		</tr>
		<tr>
			<td width="10%" class="layouttable_td_label">手机号码：<span
				class="requred_font"  cssClass="inputMobilePhoneSeq" >*</span></td>
			<td width="50%" class="layouttable_td_widget">
			<input type="text"
				id="mobileNo" name="mobileNo" size="20" /></td>
					
					
					
				
			<td align="right"><a class='easyui-linkbutton'
				href="javascript:void(0)"
				onclick="dataValidateUpdateSubmit();return false;" iconCls="icon-ok"
				id="sub">修改</a><a class='easyui-linkbutton'
				href="javascript:void(0)"
				onclick="dataValidateInsertSubmit();return false;" iconCls="icon-ok"
				id="sub">确定</a></td>
		</tr>

	</table>
	<hr class="hr_default" />
	</form>
	<jsp:include page="/WEB-INF/views/report/share.jsp"></jsp:include>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>