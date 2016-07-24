<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">
	<c:choose>
		<c:when test="${empty TRIAL_FLAG}">机构受理&gt;&gt;逐单受理&gt;&gt;客户查询</c:when>
		<c:otherwise>机构受理&gt;&gt;试算&gt;&gt;客户查询</c:otherwise>
	</c:choose>
</sf:override>
<sf:override name="head">
	<script type="text/javascript" language="javascript">
		$(document).ready(function(){
			var $form = $("#queryCriteriaTable");
			$("#queryType").change(function(event){
				var $query_option_rows = $form.find("tr:not(:has(#queryType))");
				var selVal = event.target.value;
				$query_option_rows.filter("tr:not(." + selVal + ")").hide().find("input").removeClass("ignore").addClass("ignore");
				$query_option_rows.filter("tr." + selVal).show().find("input").removeClass("ignore");
			}).trigger("change");
			
			$("#search").click(function(){
				$("#fm").submit();
				return false;
			});
			
			$("#birthday").click(function(){
				WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}',maxDate:'${CodeTable.posTableMap.SYSDATE}',isShowToday:false});
			});
			
			$("#fm").validate({ignore: ".ignore :input,.ignore"});
		});
	</script>
</sf:override>
<sf:override name="content">
<sfform:form commandName="acceptance__client_locate_creteria" id="fm">
	<spring:hasBindErrors name="clientLocateCriteriaDTO">
		<div class="error"><spring:bind path="clientLocateCriteriaDTO.*">
			<c:forEach items="${status.errorMessages}" var="error">
				<c:out value="${error}" />
				<br/>
			</c:forEach>
		</spring:bind></div>
	</spring:hasBindErrors>
	<div id="p" class="easyui-panel" title="">
		<table class="layouttable" id="queryCriteriaTable">
			<tbody>
				<tr>
					<th class="layouttable_td_label">
						客户查询方式:
						<span class="requred_font">*</span>
					</th>
					<td class="layouttable_td_widget">
						<sfform:select id="queryType" path="queryType" cssClass="input_select">
							<sfform:option value="byPolicy">按保单号</sfform:option>
							<sfform:option value="byClientNo">按客户号</sfform:option>
							<sfform:option value="byName">按姓名</sfform:option>
							<sfform:option value="byId">按证件号码</sfform:option>
						</sfform:select>
					</td>
				</tr>
				<tr class="byClientNo" style="display:none;">
					<th class="layouttable_td_label">
						客户号:
						<span class="requred_font">*</span>
					</th>
					<td class="layouttable_td_widget">
						<sfform:input id="clientNo" path="clientNo" cssClass="input_text {required:function(element){return $('#queryType')[0].value=='byClientNo';}}"/>
					</td>
				</tr>
				<tr class="byPolicy" style="display:none;">
					<th class="layouttable_td_label">
						保单号:
						<span class="requred_font">*</span>
					</th>
					<td class="layouttable_td_widget">
						<sfform:input id="policyNo" path="policyNo" cssClass="input_text {required:function(element){return $('#queryType')[0].value=='byPolicy';}}"/>
					</td>
				</tr>
				<tr class="byPolicy" style="display:none;">
					<th class="layouttable_td_label">
						投被保人:
					</th>
					<td class="layouttable_td_widget">
						<sfform:radiobutton path="applicantOrInsured" value="A" label="投保人"/>
						<sfform:radiobutton path="applicantOrInsured" value="I" label="被保人"/>
					</td>
				</tr>
				<tr class="byId" style="display:none;">
					<th class="layouttable_td_label">
						客户证件类型:
						<span class="requred_font">*</span>
					</th>
					<td class="layouttable_td_widget">
						<sfform:select path="idTypeCode" id="idTypeCode" cssClass="input_select {required:function(element){return $('#queryType')[0].value=='byId';}}">
							<sfform:options items="${CodeTable.posTableMap.ID_TYPE}" itemLabel="description" itemValue="code"/>
						</sfform:select>
					</td>
				</tr>
				<tr class="byId" style="display:none;">
					<th class="layouttable_td_label">
						客户证件号码:
						<span class="requred_font">*</span>
					</th>
					<td class="layouttable_td_widget">
						<sfform:input id="idNo" path="clientIdNo" cssClass="input_text {required:function(element){return $('#queryType')[0].value=='byId';},idNo:{target:'#idTypeCode',value:'01'}}"/>
					</td>
				</tr>
				<tr class="byName" style="display:none;">
					<th class="layouttable_td_label">
						客户姓名:
						<span class="requred_font">*</span>
					</th>
					<td class="layouttable_td_widget">
						<sfform:input id="clientName" path="clientName" cssClass="input_text {required:function(element){return $('#queryType')[0].value=='byName';}}"/>
					</td>
				</tr>
				<tr class="byName" style="display:none;">
					<th class="layouttable_td_label">
						客户性别:
					</th>
					<td class="layouttable_td_widget">
						<sfform:radiobuttons path="sex" items="${CodeTable.posTableMap.SEX}" itemLabel="description" itemValue="code" />
					</td>
				</tr>
				<tr class="byName" style="display:none;">
					<th class="layouttable_td_label">
						客户生日:
					</th>
					<td class="layouttable_td_widget">
						<sfform:date path="birthday" cssClass="Wdate"/>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
	<div id="errorMsg" style="color:red;text-align:left;">
		<c:if test="${not empty errorMsg}">
			${errorMsg}<br/>
		</c:if>
		<sfform:errors path="*"></sfform:errors>
	</div>
	<br/>
	<table width="100%">
		<tr>
			<td style="text-align:right;">
				<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="search">查询</a>
			</td>
		</tr>
	</table>
</sfform:form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
