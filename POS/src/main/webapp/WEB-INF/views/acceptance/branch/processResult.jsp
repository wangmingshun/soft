<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">
	<c:choose>
		<c:when test="${empty TRIAL_FLAG}">机构受理&gt;&gt;逐单受理&gt;&gt;受理经办结果</c:when>
		<c:otherwise>
		 <c:if test="${empty TRIAL_SOURCE}">
		机构受理&gt;&gt;试算&gt;&gt;受理经办结果
		
		</c:if>
		</c:otherwise>
	</c:choose>
</sf:override>
<sf:override name="head">
	<script type="text/javascript">
	$(function() {
		$("form").validate();
		$("#ok").click(function() {
			if(!checkPrintFlag()){
				$.messager.alert("提示", "免填单需打印申请书！");	
				return;	
			}
			/*代办代审退费金额超限制校验 edit by gaojiaming*/	
			var posNo='${acceptance__process_result_list[0].POS_NO}';
	    	 $.ajax({type: "GET",
					url:"${ctx}acceptance/branch/checkIsExceedExamPrivs",
					data:{"posNo" :posNo},
					cache: false,
					async: false,
					dataType:"json",
					success:function(data){
						if(data.isExceedExamPrivs=='Y'){
					        $.messager.alert("提示", "代办代审退费金额超限制，请送审！", "info", function () {
					        	if(data.specialRuleReason=='16'){
									checkFillFormAndSubmit();		
					        	}else{
					        		$.messager.alert("提示", "退费金额超代办代审限制，请通过特殊件申请！");
					        		return false;
					        	}
					        });
						}else{
							checkFillFormAndSubmit();
						}
					}
				});		
			return false;
		});
		
		$("#cancel").click(function() {
			$.messager.confirm("确认撤消", "是否确认撤消本次批文预览界面展示的所有保全项目？", function(yes){
				if(yes) {
					$("form").attr("action", "${ctx}acceptance/branch/processResultCancel.do").submit();
				}
			});
			return false;
		});
		
		$("#toEntry").click(function(){
			/* 免填单打印校验 edit by gaojiaming start */	
			if(!checkPrintFlag()){
				$.messager.alert("提示", "免填单需打印申请书！");					
				return;	
			}			
			/* edit by gaojiaming end */
			window.location.href="${ctx}acceptance/branch/entry.do";
			return false;
		});
		
		var submitting = false;
		$("#cancelAndReaccept").click(function(){
			$.messager.confirm("确认撤消并重新生成受理", "是否确认撤消本次批文预览界面展示的所有保全项目，并重新生成受理？", function(yes){
				if(yes && !submitting) {
					submitting = true;
					$("form").attr("action", "${ctx}acceptance/branch/cancelAndReaccept.do").submit();
				}
			});
			return false;
		});
		
		if($(".ruleCheckMsg:not(.delayedServiceItems").length > 0) {
			var message = "规则检查不通过：<br/><ul>";
			$(".ruleCheckMsg:not(.delayedServiceItems)").each(function(idx, content){
				message += $($(".ruleCheckMsg")[idx]).html().replace(/\n/g, "<br/>");
			});
			message += "</ul><br/>是否重新受理？";
			$.messager.confirm("提示", message, function(yes){
				if(yes) {
					$("#cancelAndReaccept").trigger("click");
				}
			});
		}
		/* 页面加载时执行控制 edit by gaojiaming start */
		if(${empty TRIAL_FLAG}){
			printControl();			
		}
		$("#print").click(function(){
			$("#printFlag").val("Y");
			var posNo='${acceptance__process_result_list[0].POS_NO}';
			var checkflag;
			$.ajax({type: "GET",
				url:"${ctx}print/queryFillFormApplicationInfoByPosNo.do",
				data:{"posNo" :posNo},
				cache: false,
				async: false,
				dataType:"text",
				success:function(data){
					if(data=="Y"){
						checkflag=true;					
					}else{
						$.messager.alert("提示", "未找到申请书信息，不能打印！");							
						checkflag=false;						
					}
				}
			});
			return checkflag;
		});	
		$("#print1").click(function(){
			$("#printFlag").val("Y");
			var posNo='${acceptance__process_result_list[0].POS_NO}';
			var checkflag;
			$.ajax({type: "GET",
				url:"${ctx}print/queryFillFormApplicationInfoByPosNo.do",
				data:{"posNo" :posNo},
				cache: false,
				async: false,
				dataType:"text",
				success:function(data){
					if(data=="Y"){
						checkflag=true;					
					}else{
						$.messager.alert("提示", "未找到申请书信息，不能打印！");							
						checkflag=false;						
					}
				}
			});
			return checkflag;
		});			
		/* edit by gaojiaming end */
		$("#faceRecognition")
				.click(
						function(event) {
							var faceControlParameterFlag = "${acceptance__item_input.faceControlParameterFlag}";
							if(faceControlParameterFlag!="Y"){
								$.messager.alert("提示", "获取人脸识别参数失败，请联系管理员检查人脸识别系统！");	
								return;	
							}else{
								var url = "${ctx}acceptance/branch/openFaceRecognition.do";
								var ret = window.showModalDialog(
										url,
										null,
										"dialogHeight=450px;dialogWidth=900px;center=yes;help=no;resizable=yes;status=yes;");
								if(ret){
									var faceFlag = ret.substr(0, 1);
									var faceMessage = ret.substr(2)
									$("#faceRecognitionFlag").val(faceFlag);
									
									if (faceFlag == "Y") {
										$.messager.confirm("提示", faceMessage+"是否进行下一步操作？", function(yes){
											if(yes) {
												$("#ok").trigger("change");					
											}
										});
									}
									if (faceFlag == "N") {
										$.messager.alert("提示", faceMessage+"请再次比对！");	
										$("#faceRecognition").hide();
										$("#faceRecognitionAgain").show();
								  }
									return false;		
								}
								
							}
						});
		$("#faceRecognitionAgain").click(
						function(event) {
							var faceControlParameterFlag = "${acceptance__item_input.faceControlParameterFlag}";
							if(faceControlParameterFlag!="Y"){
								$.messager.alert("提示", "获取人脸识别参数失败，请联系管理员检查人脸识别系统！");	
								return;	
							}else{
								var url = "${ctx}acceptance/branch/openFaceRecognition.do";
								var ret = window.showModalDialog(
										url,
										null,
										"dialogHeight=450px;dialogWidth=900px;center=yes;help=no;resizable=yes;status=yes;");
								if(ret){
									var faceFlag = ret.substr(0, 1);
									var faceMessage = ret.substr(2)
									$("#faceRecognitionFlag").val(faceFlag);
									
									if (faceFlag == "Y") {
										$.messager.confirm("提示", faceMessage+"是否进行下一步操作？", function(yes){
											if(yes) {
												$("#ok").trigger("change");					
											}
										});
									}
									if (faceFlag == "N") {
										$.messager.alert("提示", faceMessage+"请再次比对！");	
										$("#faceRecognition").hide();
										$("#faceRecognitionAgain").show();
								  }
									return false;		
								}
							}
						});
		$("#faceRecognitionAgain").hide();
	});
	/* 免填单打印校验 edit by gaojiaming start */		
	function checkPrintFlag(){
		var printFlag=$("#printFlag").val();
		if(printFlag=="Y"){
			return true;	
		}else{
			return false;				
		}		
	}
	/* edit by gaojiaming end */
	
	/* 免填单按钮控制 edit by gaojiaming start */		
	function printControl(){
		var posNo='${acceptance__process_result_list[0].POS_NO}';
		$.ajax({type: "GET",
			url:"${ctx}acceptance/branch/checkIsShowButton.do",
			data:{"posNo" :posNo},
			cache: false,
			async: false,
			dataType:"json",
			success:function(data){
				if(data.isApplicationFree=="Y"&&data.isPremClass=="Y"){
					$("#print").show();
					$("#print1").show();	
					$("#printFlag").val("N");					
				}else{
					$("#print").hide();
					$("#print1").hide();
					$("#printFlag").val("Y");					
				}
				if(data.isApplicationFree=="Y"){		
					$("#cancelAndReaccept").hide();				
				}else{
					$("#cancelAndReaccept").show();						
				}				
			}
		});							
	}
	//校验免填单业务和条码是否一致，不一致给出提示
	function checkFillForm(){
		var flag=true;
		var posNo='${acceptance__process_result_list[0].POS_NO}';
		$.ajax({type: "GET",
			url:"${ctx}acceptance/branch/checkIsShowButton.do",
			data:{"posNo" :posNo},
			cache: false,
			async: false,
			dataType:"json",
			success:function(data){
				if(data.isApplicationFree=="N"&&data.barCodeNo.substr(4, 2) == "09"){
					flag=false;				
				}				
			}
		});	
		return flag;
	}	
	/* edit by gaojiaming end */
	/* 免填单打印校验并提交 edit by gaojiaming start */	
	function checkFillFormAndSubmit(){
		if(!checkFillForm()){	
			$.messager.confirm("温馨提示", "条形码为免填单业务条码，但未定义为免填单业务，请核实！是否继续？", function(yes){
				if(yes) {
				<c:choose>
					<c:when test="${empty TRIAL_FLAG}">
						$("form").attr("action", "${ctx}acceptance/branch/processResultSubmit.do");
						$("form").submit();
					</c:when>
					<c:otherwise>
						window.location.href = "${ctx}acceptance/trial/entry.do";
					</c:otherwise>
				</c:choose>
				return false;					
				}else{
					return false;					
				}
			});	
			return false;
		}
				<c:choose>
					<c:when test="${empty TRIAL_FLAG}">
						$("form").attr("action", "${ctx}acceptance/branch/processResultSubmit.do");
						$("form").submit();
					</c:when>
					<c:otherwise>
						window.location.href = "${ctx}acceptance/trial/entry.do";
					</c:otherwise>
				</c:choose>			
	}
	/* 代办代审退费金额超限制校验edit by gaojiaming end */
</script>
</sf:override>
<sf:override name="content">
	<form>
	<input type="hidden" name="posNo" value="${acceptance__process_result_list[0].POS_NO}"/>
	<!-- 免填单打印标志 edit by gaojiaming start --> 
	<input type="hidden" id="printFlag" name="printFlag" value=""/>
	<input type="hidden" id="faceRecognitionFlag" name="faceRecognitionFlag" value=""/>
	<!--  edit by gaojiaming end --> 
	<div class="layout_div_top_spance">
		<div class="font_heading2">经办结果预览（共${acceptance__process_result_list[0].ACCEPT_COUNT}个受理）：</div>
		<table class="infoDisTab">
			<c:forEach items="${acceptance__process_result_list}" var="item" varStatus="status">
				<tr class="even_column">
					<th width="10%">受理信息：</th>
					<td>第${item.ACCEPT_SEQ}个受理，${item.POLICY_NO}保单，${item.SERVICE_ITEMS_DESC}项目	</td>
				</tr>
				<c:choose>
					<c:when test="${not empty item.RULE_CHECK_MSG}">
						<tr class="even_column">
							<th class="table_head_th" valign="top">规则检查不通过原因：</th>
							<td class="table_column" valign="top">
								<pre class="ruleCheckMsg <c:if test="${item.DELAY_FLAG eq 'Y'}">delayedServiceItems</c:if>">${item.RULE_CHECK_MSG}</pre>
							</td>
						</tr>
					</c:when>
					<c:otherwise>
						<tr class="odd_column">
							<th valign="top">批文预览：</th>
							<td valign="top"><pre>${item.APPROVE_TEXT}</pre>
								<c:if test="${not empty TRIAL_FLAG}">
									<br/>
									该试算结果仅供参考，具体补退费批文请以实际操作为准。
								</c:if>
							</td>
						</tr>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</table>
	</div>
	<table width="100%">
		<tr>
			<td style="text-align:right;">
				
				<c:if test="${empty TRIAL_FLAG}">
					<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="toEntry">开始新受理</a>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-cancel" id="cancel">受理撤销</a>
					撤销原因：
					 <select name="cancelCause">
						<c:forEach items="${CodeTable.posTableMap.POS_ROLLBACK_CAUSE_CODE}"	var="item">
							<option value="${item.code}">${item.description}</option>
						</c:forEach>
					</select>
					撤销备注：
					<input type="text" name="cancelRemark" class="{byteRangeLength:[0,2000]}"/>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-back" id="cancelAndReaccept">重新受理</a>
					<c:if test="${acceptance__item_input.isBranchFlag eq 'Y'}">
						<a href="${ctx}acceptance/branch/downloadTFace.do" id="downloadTFace" iconCls="icon-print" class="easyui-linkbutton">下载插件(人脸识别控件)</a>
						<a class="easyui-linkbutton" href="javascript:void(0)"  id="faceRecognition">进行人脸识别</a>
						<a class="easyui-linkbutton" href="javascript:void(0)"  id="faceRecognitionAgain">再次进行人脸识别</a>
					</c:if>
					<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="ok">确定</a>	
					<!-- 免填单按钮  edit by gaojiaming start --> 								
					<a class="easyui-linkbutton" href="${ctx}print/print_application_for_fillform.do?printFlag=N&posNo=${acceptance__process_result_list[0].POS_NO}&printType=0&entOrApplication=application" 
					iconCls="icon-print" id="print">激光打印申请书</a>
					<a class="easyui-linkbutton" href="${ctx}print/print_application_for_fillform.do?printFlag=N&posNo=${acceptance__process_result_list[0].POS_NO}&printType=1&entOrApplication=application" 
					iconCls="icon-print" id="print1">针式打印申请书</a>					
					<!-- edit by gaojiaming end -->
					
				</c:if>
			</td>
			<c:if test="${not empty TRIAL_SOURCE}">
				<td style="text-align:center;">
			        <a class="easyui-linkbutton"href="javascript:void(0)" 
			        onclick="javascript:window.external.FromExit()">关闭</a>
			    </td>
		    </c:if>
		</tr>
	</table>
	</form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>