<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">辅助功能&gt;&gt;红利通知信核对信息</sf:override><sf:override name="head">
<jsp:useBean id="sysdate" class="java.util.Date" scope="page"/>
	<script type="text/javascript" language="javascript">
		$(function(){
			$("#back").click(function(){
				window.location.href="${ctx}print/entry";
				return false;
			});
			
			$("#branchCodeLink").click(function(){
				showBranchTree($("[name='branchCode']"));
				return false;
			});
			
			$("#policyNo").change(function(){
				var policyNo = $(this).val();
				var noticeType = $("#noticeType").val();
				var $noticeYear = $("#noticeYear");
				if(policyNo && ["1", "2"].indexOf(noticeType) != -1) {
					$noticeYear.find("option").remove();
					$.post("${ctx}others/queryNoteYearOptions.do", {"policyNo":policyNo, "noticeType" : noticeType}, function(data){
						if(data && data.flag == "Y" && data.options && data.options.length > 0) {
							for(var i = 0; i < data.options.length; i++) {
								$("<option/>").attr("value", data.options[i].code).html(data.options[i].description).appendTo($noticeYear);
							}
						}
					});
				}
			});
			
			$("#noticeType").change(function(){
				var noticeType = $(this).val();
				if(noticeType == "10" || noticeType == "11") {
					$("#timeScope").show().find(":input").attr("disabled", false);
					$("#queryDateStart").removeClass("ignore");
					$("#yearScope").hide().find(":input").attr("disabled", true).removeClass("ignore").addClass("ignore");
				} else if(["1", "2"].indexOf(noticeType) != -1) {
					$("#yearScope").show().find(":input").attr("disabled", false).removeClass("ignore");
					$("#timeScope").hide().find(":input").attr("disabled", true);
					$("#queryDateStart").removeClass("ignore").addClass("ignore");
				} else {
					$("#timeScope").hide().find(":input").attr("disabled", true);
					$("#queryDateStart").removeClass("ignore").addClass("ignore");
					$("#yearScope").hide().find(":input").attr("disabled", true).removeClass("ignore").addClass("ignore");
				}
				$("#policyNo").trigger("change");
			}).trigger("change");
			
			$("#fm").validate();
		});
		
		posValidateHandler = function() {
			if($("#queryDateStart").is(":not(.ignore)")) {
				var dateStart = $("#queryDateStart").val();
				var dateEnd = $("#queryDateEnd").val();
				if(!dateEnd) {
					dateEnd = '<fmt:formatDate pattern="yyyy-MM-dd" value="${sysdate}"/>';
				}
				if(daysBetween(dateStart, dateEnd) > 365) {
					$.messager.alert("提示", "查询时间范围不能超过一年。");
					return false;
				}
			    var startYear = dateStart.substring(0, dateStart.indexOf ('-'));
				var startMonth = dateStart.substring(5, dateStart.lastIndexOf ('-'));
			    var startDay = dateStart.substring(dateStart.lastIndexOf ('-') + 1, dateStart.length);
			    var endYear = dateEnd.substring(0, dateEnd.indexOf ('-'));
			    var endMonth = dateEnd.substring(5, dateEnd.lastIndexOf ('-'));
			    var endDay = dateEnd.substring(dateEnd.lastIndexOf ('-') + 1, dateEnd.length);
			    if(startYear > endYear || startYear == endYear && startMonth > endMonth || startYear == endYear && startMonth == endMonth && startDay > endDay) {
			    	$.messager.alert("提示", "开始时间不能晚于结束时间");
					return false;
			    }
			}
			return true;
		};
	</script>
</sf:override>
<sf:override name="content">
	<sfform:form commandName="print_notice_criteria"  action="${ctx}others/notice_preview"  method="POST" id="fm" class="noBlockUI">
	<table class="layouttable">	
	   <tr>
		     <td width="10%" class="layouttable_td_label">
		                    机构：
		       <span class="requred_font">*</span>
		     </td>
		     <td width="50%" class="layouttable_td_widget">		        
				 <sfform:input path="branchCode" cssClass="input_text {required:true}"/>
				 <a href="javascript:void(0);" id="branchCodeLink">选择</a>
		     </td>
		     <td>
		        &nbsp;
		      </td>
	    </tr>
	    	
	    <tr>
		      <td width="10%" class="layouttable_td_label">
		                           通知书类型：
		          <span class="requred_font">*</span>
		      </td>
		      <td width="50%" class="layouttable_td_widget">
				    <sfform:select path="noticeType" id="noticeType" 
						cssClass="input_select {required:true}">					
					<sfform:option value="1">现金分红红利通知信</sfform:option>
					<sfform:option value="2">保额分红红利通知信</sfform:option>	
				</sfform:select>	
		      </td>
		      <td>
		        &nbsp;
		      </td>
	     </tr>
	     <tr id="timeScope">
	     	<td width="10%" class="layouttable_td_label">
	     		查询时间范围：
	     		<span class="requred_font">*</span>
	     	</td>
	     	<td width="50%" class="layouttable_td_widget">
	     		<sfform:input path="queryDateStart" id="queryDateStart" onclick="WdatePicker({skin:'whyGreen'});" cssClass="Wdate {required:true}"/>
	     		至
	     		<sfform:input path="queryDateEnd" id="queryDateEnd" onclick="WdatePicker({skin:'whyGreen'});" cssClass="Wdate"/>
	     	</td>
	     	<td>&nbsp;</td>
	     </tr> 
	     <tr id="yearScope" style="display:none;">
	     	<td width="10%" class="layouttable_td_label">
	     		查询年度：
	     		<span class="requred_font">*</span>
	     	</td>
	     	<td width="50%" class="layouttable_td_widget">
	     		<sfform:select path="noticeYear" id="noticeYear" cssClass="input_select {required:true,messages:{required:'无此保单的通知书数据'}} ignore"></sfform:select>
	     	</td>
	     	<td>&nbsp;</td>
	     </tr> 
	     <tr>
		     <td width="10%" class="layouttable_td_label">
		                       保单号：
		        <span class="requred_font">*</span>
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		         <sfform:input path="policyNo" id="policyNo" cssClass="input_text {required:true}"/>
		     </td>
		     <td align="right">
	          <a  class='easyui-linkbutton' href="javascript:void(0)" onclick="$('#fm').submit();return false;"  iconCls="icon-ok" id="confirm">确定</a>
	          <a  class='easyui-linkbutton' href="javascript:void(0)" iconCls="icon-back" id="back">返回</a>
	         </td>
	    </tr>
	</table>
	<hr class="hr_default"/>
	<div style="color:red;text-align:left;">
		${message}
		<c:if test="${not empty message}"><br/></c:if>
		<sfform:errors path="*"></sfform:errors>
	</div>
	</sfform:form>
	<jsp:include page="/WEB-INF/views/include/branchTree.jsp" />
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
