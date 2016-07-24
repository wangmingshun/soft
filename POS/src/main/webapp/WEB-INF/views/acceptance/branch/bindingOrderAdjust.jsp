<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">机构受理&gt;&gt;逐单受理&gt;&gt;受理捆绑顺序调整</sf:override>
<sf:override name="head">
	<script type="text/javascript" language="javascript">
		$(document).ready(function(){
			$("#fm").validate();
			$("#back").click(function(event){
				window.location.href = "acceptItemConfirm";
				return false;
			});
			$("#ok").click(function(){
				$("#fm").submit();
				return false;
			});
			$(".up").click(function(event){
				var $thisTR = $(":radio:checked").closest("tr");
				if($thisTR.length == 0) {
					$.messager.alert("提示", "请选择调整对象");
					return false;
				}
				var $prevTR = $thisTR.prev("tr:has(.adjustItem)");
				var tmp = $thisTR.find(":text").val();
				var cls = $thisTR.attr("class");
				if($thisTR.prevAll("tr:has(.adjustItem)").length > 0) {
					$thisTR.insertBefore($prevTR);
					//交换序号
					$thisTR.find(":text").val($prevTR.find(":text").val());
					$thisTR.removeClass().addClass($prevTR.attr("class"));
					$prevTR.removeClass().addClass(cls).find(":text").val(tmp);
				} else {
					$.messager.alert("提示", "已经为第一个元素，无法上移");
				}
				return false;
			});
			$(".down").click(function(event){
				var $thisTR = $(":radio:checked").closest("tr");
				if($thisTR.length == 0) {
					$.messager.alert("提示", "请选择调整对象");
					return false;
				}
				var $nextTR = $thisTR.next("tr:has(.adjustItem)");
				var tmp = $thisTR.find(":text").val();
				var cls = $thisTR.attr("class");
				if($nextTR.length > 0) {
					$thisTR.insertAfter($nextTR);
					$thisTR.find(":text").val($nextTR.find(":text").val());
					$thisTR.removeClass().addClass($nextTR.attr("class"));
					$nextTR.removeClass().addClass(cls).find(":text").val(tmp);
				} else {
					$.messager.alert("提示", "已经为最后个元素，无法下移");
				}
				return false;
			});
			$(":radio[name='adjustItem']").click(function(){
				$(this).closest("tr").removeClass("odd").addClass("even")
					.siblings("tr:not(.table_head_tr):not(:has(.odd))").removeClass("even").addClass("odd");
				return true;
			});
		});
	</script>
	<style type="text/css">
	</style>
</sf:override>

<sf:override name="content">
	<sfform:form action="bindingOrderAdjust" commandName="acceptance__apply_info" id="fm">
	<div class="easyui-panel" title="受理捆绑顺序" collapsible="true">
		<table class="infoDisTab">
			<thead>
				<tr>
					<th>顺序调整</th>
					<th>捆绑顺序</th>
					<th>条形码</th>
					<th>保单号</th>
					<th>项目代码</th>
					<th>项目名称</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${acceptance__apply_info.posInfoList}" var="item" varStatus="status">
					<tr class="<c:choose><c:when test="${status.index mod 2 eq 0}">odd_column</c:when><c:otherwise>even_column</c:otherwise></c:choose>">
						<td>
							<c:choose>
								<c:when test="${empty item.verifyResult.ruleInfos}">
									<input type="radio" name="adjustItem" class="adjustItem" />
								</c:when>
								<c:otherwise>
									规则检查不通过
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${empty item.verifyResult.ruleInfos}">
									<sfform:input path="posInfoList[${status.index}].acceptSeq" cssClass="input_text" readonly="true" size="3"/>
								</c:when>
								<c:otherwise>
									${item.acceptSeq}
								</c:otherwise>
							</c:choose>
						</td>
						<td>${item.barcodeNo}</td>
						<td>${item.policyNo}</td>
						<td>${item.serviceItems}</td>
						<td>${item.serviceItemsDesc}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<div id="errorMsg" style="color:red;text-align:left;">
		<c:if test="${not empty errorMsg}">
			${errorMsg}<br/>
		</c:if>
		<sfform:errors path="*"></sfform:errors>
	</div>
	<table width="100%">
		<tr>
			<td style="text-align:right;">
				<input type="button" value="上移" class="up"/>
				<input type="button" value="下移" class="down"/>
				<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-back" id="back">返回</a>
				<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="ok">确定</a>
			</td>
		</tr>
	</table>
	</sfform:form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
