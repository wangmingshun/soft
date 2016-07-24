<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">
	<c:choose>
		<c:when test="${empty TRIAL_FLAG}">机构受理&gt;&gt;逐单受理&gt;&gt;客户选择</c:when>
		<c:otherwise>机构受理&gt;&gt;试算&gt;&gt;客户选择</c:otherwise>
	</c:choose>
</sf:override>
<sf:override name="head">
	<script type="text/javascript" language="javascript">
		posValidateHandler = function() {
			if($("[name='selectClientNo']:radio:checked").length == 0) {
				$.messager.alert("提示","请选择客户");
				return false;
			}
			return true;
		};
		$(document).ready(function(){
			$("#dataTable tbody tr:odd").addClass("odd");
			$("#dataTable tbody tr:even").addClass("even");
			$("#back").click(function(){
				window.location.href = "clientLocate";
				return false;
			});
			$("#ok").click(function(event){
				$("#fm").submit();
				return false;
			});
			$("#fm tr:has(:radio[name='selectClientNo'])").click(function(){
				$(this).find(":radio").attr("checked", true).closest("tr").removeClass("odd").addClass("even")
					.siblings("tr:not(.table_head_tr):not(:has(.odd))").removeClass("even").addClass("odd");;
			});
			
			$("#fm").validate();
			
			$("#selectServiceItems").change(function() {
				var value = $("#selectServiceItems").val();
				if(value == '2'|| value == '6'|| value == '8'|| value == '9') {
					$("#d1").attr("disabled", true);
					$("#d2").attr("disabled", false);
					$("#d1").hide();
					$("#d2").show();
				} else {
					$("#d1").attr("disabled", false);
					$("#d2").attr("disabled", true);
					$("#d2").hide();
					$("#d1").show();
				}
			});
			$("#selectServiceItems").trigger("change");
		});
	</script>
</sf:override>

<sf:override name="content">
	<sfform:form action="clientSelect" id="fm">
	<div class="easyui-panel" title="客户信息列表" collapsible="true">
		<table class="infoDisTab" id="dataTable">
			<thead>
				<tr>
					<th>客户号</th>
					<th>姓名</th>
					<th dataType="date">出生日期</th>
					<th>性别</th>
					<th>证件号码</th>
					<th>地址</th>
					<th>客户联系电话</th>
					<th>客户产品信息</th>
					<th>客户接触信息</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${acceptance__client_info_list}" var="item" varStatus="status">
					<tr class="<c:choose><c:when test='${status.index mod 2 eq 0}'>odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
						<td style="vertical-align:top;">
							<input type="radio" name="selectClientNo" value="${item.clientNo}" <c:if test="${item.clientNo eq acceptance__apply_info.clientNo}">checked="checked"</c:if>/>
						</td>
						<td style="vertical-align:top;">${item.clientName}</td>
						<td style="vertical-align:top;"><fmt:formatDate value="${item.birthday}" pattern="yyyy-MM-dd"/></td>
						<td style="vertical-align:top;">${item.sexDesc}</td>
						<td style="vertical-align:top;">${item.idNo}</td>
						<td style="height:80px;vertical-align:top;">
							<div class="display_more_div">
								<c:forEach items="${item.clientAddressList}" var="addr" varStatus="addrStatus">
									<c:if test="${addr.addressType eq '3'}">
									${addr.addressTypeDesc}:${addr.fullAddress}<br/>
									</c:if>
								</c:forEach>
							</div>
						</td>
						<td style="height:80px;vertical-align:top;">
							<div class="display_more_div">
								<c:forEach items="${item.clientPhoneList}" var="phone" varStatus="phoneStatus">
									<c:if test="${phone.phoneType eq '2'}">
									${phone.phoneTypeDesc}:<c:if test="${not empty phone.areaNo}">${phone.areaNo}-</c:if>${phone.phoneNo}<br/>
									</c:if>
								</c:forEach>
							</div>
						</td>
						<td style="height:80px;vertical-align:top;">
							<div class="display_more_div">
								<c:forEach items="${item.clientProductList}" var="product" varStatus="productStatus">
									${product}<br/>
								</c:forEach>
							</div>
						</td>
						<td style="height:80px;vertical-align:top;">
							<div class="display_more_div">
								<c:forEach items="${item.clientTouchHistoryList}" var="touch" varStatus="touchStatus">
									${touch.touchContent}<br/>
								</c:forEach>
							</div>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<c:if test="${not empty TRIAL_FLAG}">
		<sfform:formEnv commandName="acceptance_trial_info">
			<div style="text-align:left;">
				保单号：<sfform:input path="policyNo" cssClass="input_text {required:true}"/>
				保全项目：
				<sfform:select path="serviceItems" cssClass="input_select {required:true}" id="selectServiceItems">
					<sfform:option value="1">犹豫期退保</sfform:option>
					<sfform:option value="2">退保</sfform:option>
					<sfform:option value="3">加保</sfform:option>
					<sfform:option value="4">减保</sfform:option>
					<sfform:option value="5">新增附加险</sfform:option>
					<sfform:option value="6">复效</sfform:option>
					<sfform:option value="7">减额交清</sfform:option>
					<sfform:option value="8">保单贷款</sfform:option>
					<sfform:option value="9">保单还款</sfform:option>
					<sfform:option value="11">红利领取</sfform:option>
					<sfform:option value="12">年龄性别错误更正</sfform:option>
					<sfform:option value="13">交费频次变更</sfform:option>
					<sfform:option value="14">交费年期变更</sfform:option>
					<sfform:option value="15">职业等级变更 </sfform:option>
					<sfform:option value="32">提前满期 </sfform:option>
				</sfform:select>
				申请日期：
				<sfform:input path="applyDate" id="d1" cssClass="required Wdate" onfocus="WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}',maxDate:'${CodeTable.posTableMap.SYSDATE}',isShowToday:false});"/>
				<sfform:input path="applyDate" id="d2" cssClass="required Wdate" onfocus="WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}',maxDate:'',isShowToday:false});" disabled="true"/>
			</div>
		</sfform:formEnv>
	</c:if>
	<div id="errorMsg" style="color:red;text-align:left;">
		<c:if test="${not empty errorMsg}">
			${errorMsg}<br/>
		</c:if>
		<sfform:errors path="*"></sfform:errors>
	</div>
	<table width="100%">
		<tr>
			<td style="text-align:right;">
				<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-back" id="back">返回</a>
				<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="ok">确定</a>
			</td>
		</tr>
	</table>
	</sfform:form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
