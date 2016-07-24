<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">机构受理&gt;&gt;逐单受理&gt;&gt;保全项目确认</sf:override>
<sf:override name="head">
	<script type="text/javascript" language="javascript">
		$(function(){
			$("#fm").validate();
			$("#back").click(function(){
				window.location.href = "${ctx}acceptance/branch/applyInfoInput";
				return false;
			});
			$("#ok").click(function(){
				var $serviceItems = $("[name=serviceItems]");
				var tip = false;
				$serviceItems.each(function(index){
					if($serviceItems[index].value=='20'){
						tip=true;
					}
				});	
				if(tip){
			        $.messager.alert("提示", "操作含有投保人变更项目：富贵全能、富贵花、金满堂、红上红F和红满堂如在交费期间内发生投保人变更，本公司不予豁免保险费！", "info", function () {
						$("#fm").submit();
						return false;					
			        });	
			        return false;	
				}else{
					$("#fm").submit();
					return false;					
				}								
			});
			$("#close").click(function(){
				$("#unpassRuleDetailWindow").window("close");
				return false;
			});
		});
		function viewUnpassDetail(idx) {
			var $contaner = $("#unpassContainer");
			var $winDiv = $("#unpassRuleDetailWindow");
			$contaner.html("加载中，请稍候...");
			$winDiv.window("open");
			$.post("${ctx}acceptance/branch/unpassRuleDetail",{"index":idx}, function(data){
				if(data) {
					if(data.flag == "Y") {
						$contaner.html("");
						for(var i = 0; i < data.unpassRuleInfo.length; i++) {
							$("<li></li>").html(data.unpassRuleInfo[i].description).appendTo($contaner);
						}
					} else {
						$.messager.alert("错误", "加载数据出错，" + data.msg);
						$winDiv.window("close");
					}
				} else {
					$.messager.alert("错误", "加载数据出错!");
					$winDiv.window("close");
				}
			});
		}
	</script>
	<style type="text/css">
	</style>
</sf:override>

<sf:override name="content">
	<c:set var="hasAnyPosVerifyOK" value="N" scope="page"/>
	<sfform:form commandName="acceptance__apply_info" id="fm">
	<div class="easyui-panel" title="保全受理信息" collapsible="true">
	<c:choose>
   <c:when test="${acceptance__apply_info.marking eq 'Y'}">
		<c:forEach items="${acceptance__apply_info.applyFiles}" var="applyFile" varStatus="applyFileStatus">
			<div class="<c:if test="${not applyFileStatus.first}">layout_div_top_spance</c:if>">
				<fieldset class="fieldsetdefault" style="padding:1px;">
					<legend>
						申请书条形码：${applyFile.barcodeNo}
						<input type="hidden" name="serviceItems" id="serviceItems" value="${applyFile.serviceItems}"/>						
						<c:if test="${not empty applyFile.relateBarcode}">
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						关联条形码：${applyFile.relateBarcode}
						</c:if>
					</legend>
					<table class="infoDisTab" width="100%">
						<thead>
							<tr>
								<th>保单号</th>
								<th width="15%">受理项目</th>
								<th width="8%">投保人</th>
								<th width="8%">被保人</th>
								<th width="27%">受理规则检查结果</th>
								<th>受理提醒信息</th>
								<th>风险提醒信息</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${acceptance__apply_info.posInfoList}" var="posInfo" varStatus="status">
								<c:if test="${posInfo.barcodeNo eq applyFile.barcodeNo}">
									<c:choose>
										<c:when test="${empty cssClassName or cssClassName eq 'odd_column'}">
											<c:set var="cssClassName" value="odd_column" scope="page"/>
										</c:when>
										<c:otherwise>
											<c:set var="cssClassName" value="even_column" scope="page"/>
										</c:otherwise>
									</c:choose>
									<tr class="${cssClassName}">
										<td>${posInfo.policyNo}</td>
										<td>${posInfo.serviceItemsDesc}</td>
										<td>${posInfo.applicantInfo.clientName}</td>
										<td>${posInfo.insuredInfo.clientName}</td>
										<td>
											<c:choose>
												<c:when test="${empty posInfo.verifyResult.ruleInfos}">
													<span style="color:green;">通过</span>
												</c:when>
												<c:otherwise>
													<a href="javascript:void(0)" onclick="viewUnpassDetail(${status.index});return false;">查看不通过原因</a>
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											<c:choose>
												<c:when test="${not posInfo.hasAcceptCaution}">无</c:when>
												<c:otherwise>${posInfo.acceptCautionDesc}</c:otherwise>
											</c:choose>
										</td>
										<td>
											<c:choose>
												<c:when test="${not posInfo.hasRiskCaution}">无</c:when>
												<c:otherwise>${posInfo.riskCautionDesc}</c:otherwise>
											</c:choose>
										</td>
									</tr>
								</c:if>
							</c:forEach>
						</tbody>
					</table>
				</fieldset>
			</div>
		</c:forEach>
		</c:when>
		<c:when test="${acceptance__apply_info.marking eq 'N'}">
			<c:forEach items="${acceptance__apply_info.applyFiles}" var="applyFile" varStatus="applyFileStatus">
			<div class="<c:if test="${not applyFileStatus.first}">layout_div_top_spance</c:if>">
				<fieldset class="fieldsetdefault" style="padding:1px;">
					<legend>
						申请书条形码：${applyFile.barcodeNo}
						<input type="hidden" name="serviceItems" id="serviceItems" value="${applyFile.serviceItems}"/>						
						<c:if test="${not empty applyFile.relateBarcode}">
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						关联条形码：${applyFile.relateBarcode}
						</c:if>
					</legend>
					<table class="infoDisTab" width="100%">
						<thead>
							<tr>
								<th>保单号</th>
								<th width="15%">受理项目</th>
								<th width="8%">投保人</th>
								<th width="8%">被保人</th>
								<th width="27%">受理规则检查结果</th>
								<th>受理提醒信息</th>
								<th>风险提醒信息</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="${acceptance__apply_info.posInfoList}" var="posInfo" varStatus="status">
								<c:if test="${posInfo.barcodeNo eq applyFile.barcodeNo}">
									<c:choose>
										<c:when test="${empty cssClassName or cssClassName eq 'odd_column'}">
											<c:set var="cssClassName" value="odd_column" scope="page"/>
										</c:when>
										<c:otherwise>
											<c:set var="cssClassName" value="even_column" scope="page"/>
										</c:otherwise>
									</c:choose>
									<tr class="${cssClassName}">
										<td>${posInfo.policyNo}</td>
										<td>${posInfo.serviceItemsDesc}</td>
										<td>${posInfo.applicantInfo.clientName}</td>
										<td>${posInfo.insuredInfo.clientName}</td>
										<td>
											<c:choose>
												<c:when test="${empty posInfo.verifyResult.ruleInfos}">
													<span style="color:green;">通过</span>
												</c:when>
												<c:otherwise>
													<a href="javascript:void(0)" onclick="viewUnpassDetail(${status.index});return false;">查看不通过原因</a>
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											<c:choose>
												<c:when test="${not posInfo.hasAcceptCaution}">电销渠道保单除复效,犹豫期退保,退保保全项目外,其他收付费类保全项目均不可以用信用卡进行收付费。</c:when>
												<c:otherwise>${posInfo.acceptCautionDesc}。电销渠道保单除复效，犹豫期退保，退保保全项目外，其他收付费类保全项目均不可以用信用卡进行收付费。</c:otherwise>
											</c:choose>
										</td>
										<td>
											<c:choose>
												<c:when test="${not posInfo.hasRiskCaution}">无</c:when>
												<c:otherwise>${posInfo.riskCautionDesc}</c:otherwise>
											</c:choose>
										</td>
									</tr>
								</c:if>
							</c:forEach>
						</tbody>
					</table>
				</fieldset>
			</div>
		</c:forEach>
		</c:when>
		</c:choose>
	</div>
	<div class="layout_div_top_spance">
		<div class="easyui-panel" title="保全未结案受理信息" collapsible="true">
			<table class="infoDisTab" width="100%">
				<thead>
					<tr>
						<th>保单号</th>
						<th>批单号</th>
						<th>受理项目</th>
						<th>条形码</th>
						<th>受理状态</th>
						<th>受理日期</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${empty acceptance__apply_info.notCompletePosInfoList}">
							<tr class="odd">
								<td style="text-align:center;" colspan="6">无未结案受理信息</td>
							</tr>
						</c:when>
						<c:otherwise>
							<c:forEach items="${acceptance__apply_info.notCompletePosInfoList}" var="posInfo">
								<c:choose>
									<c:when test="${empty cssClassName or cssClassName eq 'odd_column'}">
										<c:set var="cssClassName" value="odd_column" scope="page"/>
									</c:when>
									<c:otherwise>
										<c:set var="cssClassName" value="even_column" scope="page"/>
									</c:otherwise>
								</c:choose>
								<tr class="${cssClassName}">
									<td>${posInfo.policyNo}</td>
									<td>${posInfo.posNo}</td>
									<td>${posInfo.serviceItemsDesc}</td>
									<td>${posInfo.barcodeNo}</td>
									<td>${posInfo.acceptStatusDesc}</td>
									<td><fmt:formatDate value="${posInfo.acceptDate}" pattern="yyyy-MM-dd"/></td>
								</tr>
							</c:forEach>
						</c:otherwise>
					</c:choose>
				</tbody>
			</table>
		</div>
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
				<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-back" id="back">返回</a>
				<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="ok">确定</a>
			</td>
		</tr>
	</table>
	</sfform:form>
	<div id="unpassRuleDetailWindow" class="easyui-window" title="规则检查不通过详细信息"
	style="width:500px;height:300px;padding:5px;background: #fafafa;" closed="true" modal="true" collapsible="false" minimizable="false" maximizable="false">
		<div class="easyui-layout" fit="true">
			<div region="center" border="false" style="padding:10px;background:#fff;border:1px solid #ccc;">
				<ol style="padding:0;margin:0 0 0 28px;" id="unpassContainer"></ol>
			</div>
			<div region="south" border="false" style="text-align:right;height:30px;line-height:30px;">
				<a class="easyui-linkbutton" iconCls="icon-ok" href="javascript:void(0)" id="close">关闭</a>
			</div>
		</div>
	</div>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
