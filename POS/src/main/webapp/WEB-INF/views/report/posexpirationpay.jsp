
<%@ page contentType="text/html;charset=UTF-8"%>

<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单报表&gt;&gt;银代清单提取码短信发送</sf:override>

<sf:override name="head">
	<script type="text/javascript"><!--
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
	dataValidateSubmit = function() {
	

		var branchCode=$("#branchCode").val();
        var deptNo=$("#deptNo").val();
		
		
		var dateStart = $("#startDate").val();
		var dateEnd = $("#endDate").val();

		if(branchCode==""){						
			alert("机构不能为空!");
			return false;
			
		}
		if(deptNo==""){
			alert("银行渠道不能为空!");
			return false;
		}
		if(dateStart==""){			
			
			alert("开始时间不能为空!");
			return false;
			
		}
		if(dateEnd==""){
			alert("结束时间不能为空!");
			return false;
		}
		if(dateStart>dateEnd){
			
			alert("开始时间不能大于结束时间!");
			return false;
		}

		date1 = dateEnd.split('-');
		
		date3=parseInt(date1[2],10);
		// 得到月数
		
	
		date1 = parseInt(date1[0],10) * 12 + parseInt(date1[1],10);
		// 拆分年月日		
		date2 = dateStart.split('-');
		// 得到月数
		date4=parseInt(date2[2],10);	
	
		
		date2 = parseInt(date2[0],10) * 12 + parseInt(date2[1],10);
			
		var m = Math.abs(date1 - date2);
	
		var d=date3 - date4;

		if(m>6){
			
			alert("查询时间不能超过6个月！");
			return false;
		}
		if(m==6&&d>0){
			
			
			alert("查询时间不能超过6个月！");
			return false;
		}
     
          	
        $.post("${ctx}report/new/sendMessageCode",
        		{branchCode:$("#branchCode").val(),
        	     contact:$("#contact").val(),
        	     deptNo:$("#deptNo").val(),
        	     startDate:$("#startDate").val(),
        	     endDate:$("#endDate").val(),
        	     listCode:$("#listCode").val(),
        	     transAuthIndi:$("#transAuthIndi").val()
        		}, function(data){
	      
        	
	        	if(data.flag=="NOBRANCHCODE"){
	        	 	alert("该清单暂无提取码接收人员，请先联系总公司设置提取码的接收人员！");
	        		
	        	}  
	        	if(data.flag=="YBRANCHCODE"){
	        	 	alert("提数申请提交成功，请尽快与交接人员联系！");

	        		
	        	} 
	        	if(data.flag=="E"){
	        		
	        		alert("系统发生异常");
	        		
	        	}
	        	
	        	$('#queryForm').submit();	

		});
		
		
		return true;
	};
	
	
	


</script>
</sf:override>
<sf:override name="content">
	<form id="queryForm"  name="queryForm" action="${ctx}/report/posexpirationpay" class="noBlockUI"
		method="post"><input type="hidden" name="ibatisSqlId"
		value=".effectiveListQuery" /> <input type="hidden" name="sheetName"
		value="满期金给付清单提取" />
	<table class="layouttable">
		<tr>
			<td width="10%" class="layouttable_td_label">
		                     清单类型:<span class="requred_font">*</span>
		     </td>
			<td width="50%" class="layouttable_td_widget">
				<select id="listCode" name="listCode">
				    <option value="25">满期金给付清单</option>
					<option value="30">退保与满期给付客户信息清单</option>
				</select>
			  </td>
			 <td>&nbsp;
		        
		      </td>
		</tr>
		
		<tr>
		     <td width="10%" class="layouttable_td_label">
		                      查询机构：<span class="requred_font">*</span>
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <input type="text" id="branchCode" name="branchCode" class="{required:true}" value="${userBranchCode}"/>
		          <a href="javascript:void(0)" id="branchChooseBt">选择机构</a>
		      </td>
		      
		</tr>

		<tr>
			<td width="10%" class="layouttable_td_label">保单渠道：<span
				class="requred_font">*</span></td>

			<td width="50%" class="layouttable_td_widget">
			<span>银代</span> 			
			<select
				name="deptNo" id="deptNo">
               <option>请选择</option>
				<c:forEach items="${CodeTable.posTableMap.DEPARTMENT_INFO}"
					var="item">
					<option value="${item.code}">${item.description}</option>
				</c:forEach>

			</select>

			<td>&nbsp;</td>
		</tr>


		<tr>
			<td width="10%" class="layouttable_td_label">时间范围：<span
				class="requred_font">*</span></td>
			<td width="50%" class="layouttable_td_widget"><input type="text"
				id="startDate" name="startDate"
				class="Wdate {required:true,messages:{required:' 请输入开始日期'}}" /> 至 <input
				type="text" id="endDate" name="endDate"
				class="Wdate {required:true,messages:{required:' 请输入结束日期'}}" /></td>

		</tr>
		<tr>
			<td width="10%" class="layouttable_td_label">是否转账授权：</td>
			<td width="50%" class="layouttable_td_widget">
			<select id="transAuthIndi" name="transAuthIndi">
			    <option value="">全部</option>
				<option value="Y">是</option>
				<option value="N">否</option>
			</select></td>
			<td align="right"><a class='easyui-linkbutton'
				href="javascript:void(0)"
				onclick="dataValidateSubmit();return false;" iconCls="icon-ok"
				id="sub">确定</a></td>
		</tr>

	</table>
	<hr class="hr_default" />
	</form>
	<jsp:include page="/WEB-INF/views/report/share.jsp"></jsp:include>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>