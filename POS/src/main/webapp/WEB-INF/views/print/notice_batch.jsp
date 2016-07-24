<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">清单打印&gt;&gt;打印&gt;&gt;通知书批次打印</sf:override>
<sf:override name="head">
	<script type="text/javascript" language="javascript">
		$(function(){
			
			$("#back").click(function(){
				window.location.href="${ctx}print/entry";
				return false;
			});
			
			$("#branchCodeLink").click(function(){
				showBranchTree($("[name='branchCode']"), "03", "${LOGIN_USER_INFO.loginUserUMBranchCode}");
				return false;
			});
			
			$("[name='businessDateStart'],[name='businessDateEnd']").focus(function(){
				WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}'});
			});
			
			/*
			$("#noticeType").change(function() {
				if($(this).val() =="51" || $(this).val() =="52" || $(this).val() =="53" || $(this).val() =="54" ){
					$("#policyChannel").attr("disabled","true");
				}
				else{
					$("#policyChannel").removeAttr("disabled");
				}
			});
			*/
			
			$("#fm").validate();
			//init();
		});
		/*
		function init(){
			if($("#noticeType").val() =="51" || $("#noticeType").val() =="52" || $("#noticeType").val() =="53" || $("#noticeType").val() =="54" ){
				$("#policyChannel").attr("disabled","true");
			}
			else{
				$("#policyChannel").removeAttr("disabled");
			}
		};
		*/
	</script>
</sf:override>

<sf:override name="content">
	<sfform:form commandName="print_notice_criteria" id="fm" class="noBlockUI">
	<table class="layouttable">	
	   <tr>
		     <td width="10%" class="layouttable_td_label">
		                    机构：
		       <span class="requred_font">*</span>
		     </td>
		     <td width="50%" class="layouttable_td_widget">		        
				 <sfform:input path="branchCode" cssClass="input_text {required:true}" readonly="true"/>
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
				<c:choose>
					<c:when test="${isShenzhenUser}">
						<sfform:select path="noticeType" cssClass="input_select {required:true}">
							<sfform:option value="51" label="现金分红红利通知信(银代渠道)" />
							<sfform:option value="52" label="现金分红红利通知信(非银代渠道)" />
							<sfform:option value="53" label="保额分红红利通知信(银代渠道)" />
							<sfform:option value="54" label="保额分红红利通知信(非银代渠道)" />
							<sfform:options items="${CodeTable.posTableMap.POS_NOTE_TYPE_BATCH}" itemLabel="description" itemValue="code"	/>
						</sfform:select>
					</c:when>
					<c:otherwise>
						<sfform:select path="noticeType" items="${CodeTable.posTableMap.POS_NOTE_TYPE}" itemLabel="description" itemValue="code"
							cssClass="input_select {required:true}"/>
					</c:otherwise>
				</c:choose>
		      </td>
		      <td>
		        &nbsp;
		      </td>
	     </tr>
	     	         
	     <tr>
		     <td width="10%" class="layouttable_td_label">
		                    时间：
		       <span class="requred_font">*</span>
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		         <sfform:input path="businessDateStart" cssClass="Wdate {required:true}"/>
					至
				 <sfform:input path="businessDateEnd" cssClass="Wdate {required:true}"/>
		     </td>
		     
	    </tr>
	    <tr>
			<td width="10%" class="layouttable_td_label">保单渠道：
			<span >*</span></td>
			<td width="50%" class="layouttable_td_widget"><select
				name="policyChannel" id="policyChannel">
				<option value="">全部</option>
				<c:forEach items="${CodeTable.posTableMap.CHANNEL_TYPE_TBL}"
					var="item">
					<option value="${item.code}">${item.description}</option>
				</c:forEach>
			</select></td>
             <td>&nbsp;		        
		      </td>
		</tr>
	    <tr>
	    <td width="10%" class="layouttable_td_label">
		                     业务/服务人员：
		    <span >*</span></td>                 
		     </td>
		     <td width="50%" class="layouttable_td_widget">
		          <input type="text" name="serviceNo" />
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
