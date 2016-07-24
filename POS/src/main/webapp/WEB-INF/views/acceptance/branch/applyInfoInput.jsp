<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">机构受理&gt;&gt;逐单受理&gt;&gt;申请信息录入</sf:override>
<sf:override name="head">
	<script type="text/javascript" language="javascript">
		var validator = null;
		var validPolilcyNoArr = [<c:forEach items="${acceptance__apply_info.clientPolicyNoList}" var="item" varStatus="status"><c:if test="${not status.first}">,</c:if>'${item}'</c:forEach>];
		var staffInfoQueryExecuting = false;
		var clientIdnoValidityDateIsExist = null; //初始化页面时记录客户数据库证件有效期是否存在 edit by wangmingshun
		
		/**
		 * 绑定事件
		 */
		function bindEventHandlers() {
			//代审人
			$("#approveEmpNo").blur(function(){
				var approveEmpNo=$("#approveEmpNo").val();
				if(approveEmpNo==''){
					return false;					
				}
				approveCheckAndQuery('2',approveEmpNo);	
			});				

			//代审网点
			$("#approveDeptNo").blur(function(){
				var approveDeptNo=$("#approveDeptNo").val();
				if(approveDeptNo==''){
					return false;					
				}
				approveCheckAndQuery('1',approveDeptNo);		
			});				
						
			/* 申请类型按钮change事件处理，当申请类型为2/3/4时显示代办人信息*/
			$("#applyType").change(function(event){
				//代办时显示代办人信息录入部分
				if(["2", "3", "4","11"].indexOf($(this).val()) > -1) {
					$(".representOption").show();
				} else {
					$(".representOption").hide();
				}
				//业务员代办时显示业务员号码录入部分
				if($(this).val() == "3") {
					$(".empNo").show();
				} else {
					$(".empNo").hide();
				}
				if($(this).val() == '5'){
					$("#idnoValidityDate").attr("disabled","true");
					$('#isLongidnoValidityDate').attr("disabled","true");
					if(!clientIdnoValidityDateIsExist){
						$("#idnoValidityDate").val("");
						$('#isLongidnoValidityDate').attr("checked",false);
					}
				}else{
					$("#idnoValidityDate").removeAttr("disabled");
					$('#isLongidnoValidityDate').removeAttr("disabled");
				}
				
				/* 代审页面控制 edit by gaojiaming start */
				/*检查是否是代办代审的试点机构 edit by wangmingshun start*/
				/*
				if($(this).val() == "11"){
					var isExamBranch = checkExamBranch();
					if(!isExamBranch){
						$(".representOption").hide();
					}
				}
				*/
				$(".representOption3").hide();
				if($(this).val() == "12") {
					/*
					$(".representOption1").hide();
					$(".representOption2").show();
					$(".representOption3").show();
					*/
					var isExamBranch = checkExamBranch();
					if(isExamBranch){
						$(".representOption1").hide();
						$(".representOption2").show();
					}else{
						$(".representOption1").hide();
						$(".representOption2").hide();
					}
					/*检查是否是代办代审的试点机构 edit by wangmingshun end*/
				}else{
					$(".representOption1").hide();
					$(".representOption2").hide();
				}
				controlAcceptChannel();		
				/* 代审页面控制 edit by gaojiaming end */					
				/* 控制亲办件及分支公司柜面 edit by gaojiaming start */		
				controlFillForm();		
				/* 控制亲办件及分支公司柜面 edit by gaojiaming end */							
			});

			/* 待审页面控制取消  edit by wangmingshun start 
			$("#examType").change(function(event){	
				if($(this).val() == "02") {
					$(".representOption1").show();
					$(".representOption2").hide();
				} else if($(this).val() == "01") {
					$(".representOption1").hide();
					$(".representOption2").show();
				}else{
					$(".representOption1").hide();
					$(".representOption2").hide();					
				}
				controlAcceptChannel();
			});
			 待审页面控制取消  edit by wangmingshun end */
			
			/* 控制亲办件及分支公司柜面 edit by gaojiaming start */		
			$("#acceptChannel").change(function(event){	
				/* 受理渠道控制  edit by wangmingshun start */
				var applyType = $("#applyType").val();
				// 只有代审需要进行试点机构校验
				if(applyType == '12') {
					if(checkExamBranch()){
						if($(this).val() == '3'){
							$(".representOption1").hide();
							$(".representOption2").show();
						}else if($(this).val() == '20'){
							$(".representOption1").show();
							$(".representOption2").hide();
						}else{
							$(".representOption1").hide();
							$(".representOption2").hide();
						}
					}
				} 
				/* 受理渠道控制  edit by wangmingshun end */
				controlFillForm();
			});	
			/* edit by gaojiaming end */	
			/* 业务员代办时，输入业务员代码，按回车键查询业务员信息处理 */
			$("#empNo").keydown(function(event){
				if(event.keyCode == 13) {
					$(this).trigger("change");
					return false;
				}
				return true;
			}).change(function(event){
				var empNo = $.trim($("#empNo").val());
				if(empNo == '') {
					$.messager.alert("校验错误", "请输入业务员代码");
					$("#empNo").focus();
				} else if(!staffInfoQueryExecuting){//防止同一时间触发多个请求
					staffInfoQueryExecuting = true;
					$.get("${ctx}include/queryStaffByEmpNo.do",
							{"empNo" : empNo },
							function(data){
								var closeCallback = function(){
									staffInfoQueryExecuting = false;
								};
								if(data) {
									if(data.flag == "Y") {
										$("#representor").val(data.staffInfo.empName);
										$("#idType").val(data.staffInfo.idType);
										$("#representIdno").val(data.staffInfo.empIdno);
									} else {
										$.messager.alert("查询失败", data.message, "warning", closeCallback);
										$("#representor").val("");
										$("#idType").val("")
										$("#representIdno").val("");
									}
								} else {
									$.messager.alert("查询失败", "查询业务员信息失败!", "error", closeCallback);
								}
							}, "json");
				}
			});
			
			/* 收付方式change事件处理，切换显示银行信息录入项 */
			$(".paymentType").change(function(event){
				var $paymentTypeOption = $(this).closest("table").find(".paymentTypeOption");
				if($(this).val() == '2') {
					$paymentTypeOption.show();
				} else {
					$paymentTypeOption.hide();
				}
			});
			
			/* 条码录入域变更处理 */
			$(".barcode").change(function(){
				var barcode = $(this).val();
				if(barcode && (barcode.substr(0, 4) == "1211" || barcode.substr(0, 4) == "1234")) {
					//非补退费类条码
					$(this).closest("table").find(".premRelatedServiceItemsOption :input").addClass(".ignore").attr("disabled", true).end().find(".premRelatedServiceItemsOption").hide();
				} else {
					$(this).closest("table").find(".premRelatedServiceItemsOption :input").removeClass(".ignore").attr("disabled", false).end().find(".premRelatedServiceItemsOption").show();
					$(this).closest("table").find(".paymentType").trigger("change");
				}
				$(".serviceItems:text").trigger("change");
			});
			
			/* 保全项目选择按钮click事件处理，弹出保全项目选择窗口 */
			$(".serviceItemSelect").click(function(event){
	
				var $bt = $(this);
				var barcode = $bt.closest("table").find(".barcode").val();
				if(barcode && barcode.length && barcode.length >= 4) {
					barcode = barcode.substring(0, 4);
				} else {
					barcode = null;
				}
				
				/*
				openProductServiceItemSelectWindow(function(labelData, valueData){
					//给文本框赋值 
					$bt.siblings(":text").val(valueData).trigger("change").trigger("blur");
					//$bt.siblings(":text").val(valueData).trigger("change");
				}, $bt.siblings(":text").val(), barcode);
				*/
				
				/* edit by wangmingshun start */
				//判断用户是否需要进行保全项目录入控制,采用的方法是将没有在查询出来的数组中的保全项目隐藏
				var	isFilterServiceItems = "${isFilterServiceItems}";
				if(isFilterServiceItems == "true") {
					var	serviceItems = "${serviceItems}";
					//新方案
					var itemsArray = new Array();
					<c:forEach items="${serviceItems }" var="item">
						itemsArray.push("${item}");
					</c:forEach>
					var flag = false;//记录弹出框是否全选了
					openProductServiceItemSelectWindow(function(labelData, valueData){
						//给文本框赋值
						valueData = (flag == true ? itemsArray.join(",") : valueData);
						$bt.siblings(":text").val(valueData).trigger("change").trigger("blur");
					}, $bt.siblings(":text").val(), barcode);
					//此处加入全选框改变事件，记录下最终的状态，方便给文本框赋值。
					//默认全选框是所有保全项目，但是此处需要进行保全项目的录入控制，全选的话，只能是查询到的可录入保全项目
					$(".selectAllCheckBox").change(function() {
						if($(".selectAllCheckBox").attr("checked") == true) 
							flag = true;
						else 
							flag = false; 
					});
					var $checkboxes = $("#productServiceItemsSelectWindowDiv :checkbox:not(.selectAllCheckBox)");
					$checkboxes.each(function() {
						if(itemsArray.indexOf($(this).val()) == -1)
							$(this).closest("div").hide();
					});
				} else {
					//如果不进行控制，则采用以前的方法
					openProductServiceItemSelectWindow(function(labelData, valueData){
						//给文本框赋值 
						$bt.siblings(":text").val(valueData).trigger("change").trigger("blur");
						//$bt.siblings(":text").val(valueData).trigger("change");
					}, $bt.siblings(":text").val(), barcode);
				}
				/* edit by wangmingshun start */
				
				return false;
			});
			
			/* 保全项目录入框change事件处理，更新保全项目名称显示 */
			$(".serviceItems:text").change(function(event){
				var $input = $(this);
				
				/*edit by wangmingshun start*/
				//判断用户是否需要进行保全项目录入控制
				var	isFilterServiceItems = "${isFilterServiceItems}";
				if(isFilterServiceItems == "true") {
					var	serviceItems = "${serviceItems}";
					//获取当前录入框的内容， 此处检查当前用户是否有该保全的录入权限
					var itemsArray = new Array();//权限数组
					var itemsInput = $input.val().split(",");//当前录入数组
					<c:forEach items="${serviceItems }" var="item">
						itemsArray.push("${item}");
					</c:forEach>
					
					$.each(itemsInput, function(index, content) {
						if(itemsArray.indexOf(content) == -1) {
							//不符合录入要求，直接清空
							$input.val("");
							$input.siblings(".itemList").empty();
							return false;
						}
					});
				}
				/*edit by wangmingshun end*/
				
				var barcode = $input.closest("table").find(".barcode").val();
				if(barcode && barcode.length && barcode.length >= 4) {
					barcode = barcode.substring(0, 4);
				} else {
					barcode = null;
				}
				
				var val = formatServiceItemsInput($input.val(), barcode);
		
				$input.val(val);
				var label = getPosTypeLabels(val);
				var $ul = $("<ul class='itemList'/>");
				var hasContent = false;
				$.each(label.split(","), function(idx, content) {
					if(content != "") {
						$("<li/>").text(content).appendTo($ul);
						hasContent = true;
					}
				});
				$input.siblings("ul").remove().end();
				if(hasContent) {
					$input.closest("td").append($ul);
				}
				if(barcode == "1212") {
					if($input.val() == "22") {
						//核保类通知书只选择了22，按照非补退费类通知书处理
						$(this).closest("table").find(".premRelatedServiceItemsOption :input").addClass(".ignore").attr("disabled", true).end().find(".premRelatedServiceItemsOption").hide();
					} else {
						$(this).closest("table").find(".premRelatedServiceItemsOption :input").removeClass(".ignore").attr("disabled", false).end().find(".premRelatedServiceItemsOption").show();
						$(this).closest("table").find(".paymentType").trigger("change");
					}
				}
				
				if(barcode == "1233") {
					if($input.val() == "19" ||$input.val() == "45"||$input.val() == "19,45"||$input.val() == "45,19") {
						//银代渠道申请书只选择了19，按照非补退费类通知书处理
						$(this).closest("table").find(".premRelatedServiceItemsOption :input").addClass(".ignore").attr("disabled", true).end().find(".premRelatedServiceItemsOption").hide();
					} else {
						$(this).closest("table").find(".premRelatedServiceItemsOption :input").removeClass(".ignore").attr("disabled", false).end().find(".premRelatedServiceItemsOption").show();
						$(this).closest("table").find(".paymentType").trigger("change");
					}
				}
				if(barcode == "1235") {
					if(($input.val() == "19" ||$input.val() == "45"||$input.val() == "19,45"||$input.val() == "45,19")) {
						//银代渠道申请书只选择了19，按照非补退费类通知书处理
						$(this).closest("table").find(".premRelatedServiceItemsOption :input").addClass(".ignore").attr("disabled", true).end().find(".premRelatedServiceItemsOption").hide();
					} else {
						$(this).closest("table").find(".premRelatedServiceItemsOption :input").removeClass(".ignore").attr("disabled", false).end().find(".premRelatedServiceItemsOption").show();
						$(this).closest("table").find(".paymentType").trigger("change");
					}
				}
			});
			
			/* 申请材料选择按钮点击事件处理，弹出申请材料选择窗口 */
			$(".applyFileMaterialSelect").click(function(event){
				var $bt = $(this);
				openApplyMaterialSelectWindow(function(labelData, valueData){
					$bt.siblings(":text").val(valueData).trigger("change");
				}, $bt.siblings(":text").val());
				return false;
			});
			
			/* 申请材料录入框change事件处理，更新申请材料名称显示 */
			$(".applyFileMaterials:text").change(function(event){
				var $input = $(this);
				var val = formatMaterialInput($input.val());
				$input.val(val);
				var label = getApplyMaterialLabels(val);
				var $ul = $("<ul class='itemList'/>");
				var hasContent = false;
				$.each(label.split(","), function(idx, content) {
					if(content != "") {
						$("<li/>").text(content).appendTo($ul);
						hasContent = true;
					}
				});
				$input.siblings("ul").remove().end();
				if(hasContent) {
					$input.closest("td").append($ul);
				}
			});
			
			/* 批单送达方式change事件处理，只有选择邮寄时才展示邮编与地址录入框  
			$(".approvalServiceType").change(function(){
				var $this = $(this);
				
				if($this.val() == "2") {//邮寄
					$this.closest("tbody").find("tr.approvalServiceTypeOption").show();
				} else if($this.val() == "1"){//自领
					$this.closest("tbody").find("tr.approvalServiceTypeOption").hide();
				}
			});*/
			
			/* 银行选择链接处理 */
			$(".bankSelectLink").click(function(){
				var $bankCode = $(this).siblings(".bankCode");
				showBankSelectWindow($bankCode, function(code, label){
					$bankCode.siblings("div").remove();
					$bankCode.closest("td").append($("<div class='left'/>").html(label));
				});
				return false;
			});
			
			/* 签名影像查询链接处理 */
			$(".signView").click(function(){
				var policyNo = $(this).closest("table").find(".policyNo").val();
				var clientNo = "${acceptance__apply_info.clientInfo.clientNo}";
				if(!policyNo) {
					$.messager.alert("校验错误", "请先录入查询影像签名的保单号!");
					return false;
				}
				if(validPolilcyNoArr.indexOf(policyNo) < 0) {
					$.messager.alert("校验错误", "无效的保单号:" + policyNo);
					return false;
				}
				$("#autographImageDiv").empty().html("签名影像查询中....请稍候").window("open");
				$.post("${ctx}acceptance/branch/getAutographImageURL.do", 
						{"policyNo" : policyNo, "clientNo" : clientNo, "_timestamp" : new Date().getTime()},
						function(data){
							if(data.flag == "Y") {
								$("#autographImageDiv").empty();
								$("<img/>").attr("alt", "签名影像信息").attr("src", data.url).appendTo("#autographImageDiv");
							} else {
								$("#autographImageDiv").empty().html(data.msg);
							}
						});
				return false;
			});
			
			/* 返回按钮click处理 */
			$("#back").click(function(event){
				window.location.href = "clientSelect";
				return false;
			});
			
			/* 删除按钮click处理，进行删除确认*/
			$("#remove").click(function(event){
				$("#submitFlag").attr("name", "delete");
				var tab = $('#tabContainer').tabs('getSelected');
				var title = tab.panel('options').title;
				$.messager.confirm('确认删除', '请确认是否删除申请书: '+ title, function(r){
					if (r){
						var idx = title.substring(title.indexOf("[") + 1, title.indexOf("]"));
						$(":hidden[name='index']").val(idx - 1);
						$("#fm")[0].submit();
					}
				});
				return false;
			});
			
			/* 新增按钮处理 */
			$("#add").click(function(event){
				$("#submitFlag").attr("name", "new");
				$("#fm")[0].submit();
				return false;
			});
			
			
			$(".policyNo").focusout(function() {				
				   var $policyNo=null;
				   var $thisPolicy=null;
				   $(".policyNo").each(function(i,cont) {					      
						$policyNo=$(".policyNo")[i].value;
					     /*检查是否是代办代审的试点机构 edit by wangmingshun start*/
					    if(i<1){
					    	/*
					    	if($("#applyType").val() == "11"){
								var isExamBranch = checkExamBranch();
								if(!isExamBranch){
									$(".representOption").hide();
								}else{
									$(".representOption").show();
									$(".empNo").hide();
								}
							}*/
							if($("#applyType").val() == "12") {
								var isExamBranch = checkExamBranch();
								if(isExamBranch){
									if($("#acceptChannel").val() == '20'){
										$(".representOption1").show();
									}
									if($("#acceptChannel").val() == '3'){
										$(".representOption2").show();
									}
								}else{
									$(".representOption1").hide();
									$(".representOption2").hide();
								}
							}
					    }
					    /*检查是否是代办代审的试点机构 edit by wangmingshun end*/
					    
					     var serviceItems = $(this).closest("tr").next().find(".serviceItems");
			    	     $thisPolicy=$(this);
					     if($policyNo) {	
							    if(serviceItems.val() == "17" || serviceItems.val().indexOf("17") >= 0) {
							    	getFinPosPayInfo($thisPolicy);
							     } else {
							    	 $thisPolicy.closest("tbody").find(".finAccountNoType").empty();
							    	 $.ajax({type: "GET",
											url:"${ctx}acceptance/branch/queryPolicyChannelType.do",
											data:{"policyNo" :$policyNo},
											cache: false,
											async: false,
											dataType:"text",
											success:function(data){
												 if(data=="06") {							    	   
													 $thisPolicy.closest("tbody").find(".finAccountNoType").append("<option value='1'>卡</option><option value='3'>信用卡</option>");
										    	 } else {
										    	  	 $thisPolicy.closest("tbody").find(".finAccountNoType").append("<option value='1'>卡</option>");
										    	 }
									  }});
							    }
							    
						   }
					     //同时为服务项目文本框绑定 blur事件
					     //edit by wangmingshun start
					     /*
						    serviceItems.blur(function() {
						    	if(serviceItems.val() == "17" || serviceItems.val().indexOf("17") >= 0) {
						    		getFinPosPayInfo($thisPolicy);
						    	} else {
						    		 $thisPolicy.closest("tbody").find(".finAccountNoType").empty();
							    	 $.ajax({type: "GET",
											url:"${ctx}acceptance/branch/queryPolicyChannelType.do",
											data:{"policyNo" :$policyNo},
											cache: false,
											async: false,
											dataType:"text",
											success:function(data){
												 if(data=="06") {							    	   
													 $thisPolicy.closest("tbody").find(".finAccountNoType").append("<option value='1'>卡</option><option value='3'>信用卡</option>");
										    	 } else {
										    	  	 $thisPolicy.closest("tbody").find(".finAccountNoType").append("<option value='1'>卡</option>");
										    	 }
									  }});
						    	}
						    });	
					     */			    
					});
			});
			
			function checkSurNotPaied() {
				var $policnNos = $(".policyNo");
				var $serviceItems = null;
				var $policnNo = null;
				var warningMsg = null;
				var serviceItems = null;
				var policyNo = null;
				$policnNos.each(function(idx,cont) {
					$policyNo = $($policnNos[idx]);
					$serviceItems = $policyNo.closest("table").find(".serviceItems");
					serviceItems = $serviceItems.val();
					policyNo = $policyNo.val();
					if(serviceItems && serviceItems.split(",").indexOf("2") > -1 && policyNo) {
						$.ajax({type: "GET",
										url:"${ctx}acceptance/branch/checkHasSurvivalNotPayByPolicyNo.do",
										data:{"policyNo" : policyNo},
										cache: false,
										async: false,
										dataType:"json",
										success:function(data){
											if(data){
												if(warningMsg) {
													warningMsg += "，" + policyNo;
												} else {
													warningMsg = "保单号：" + policyNo;
												}
											}
										}});
					}
				});
				if(warningMsg) {
					$.messager.alert("提示", warningMsg + " 有未领取生存金，请及时受理生存金领取!!","warning", function(){
						$("#fm").submit();
						//如果校验失败，需要将TAB切换到第一个有失效元素PANEL
						if(!validator.valid()) {
							var $errDiv = $(".tabDiv:has(:input.error)");
							if($errDiv.length > 0) {
								$("#tabContainer").tabs("select", $errDiv[0].id);
							}
						}
					});
					return false;
				}
				return true;
			}
			
			function check06ChannelAccount() {
				   var warningMsg = null;
				   var waringMessage=null;
				   var $policyNo=null;
				   var $thisPolicy=null;
				   var $bankCode=null;
				   var $channel=null;
				   var $paymentType=null;
				   $(".policyNo").each(function(i,cont)
						{					      
					    $policyNo=$(".policyNo")[i].value;	
					    if($policyNo)
					     {	 
					     $bankCode=$(this).closest("tbody").find(".bankCode").val();
					     $finAccountNoType=$(this).closest("tbody").find(".finAccountNoType").val();
					     $serviceItems=$(this).closest("tbody").find(".serviceItems").val();
					     $paymentType=$(this).closest("tbody").find(".paymentType").val();
					     $.ajax({type: "GET",
								url:"${ctx}acceptance/branch/queryPolicyChannelType.do",
								data:{"policyNo" :$policyNo},
								cache: false,
								async: false,
								dataType:"text",
								success:function(data){
									$channel=data;									 
								}});	
					    
					     if($paymentType=="2"&&( $(this).closest("table").find(".bankCode").attr("disabled")==false))
					    	 {
					    	 
					    	 if($finAccountNoType==null)
				    		 {					    		
				    				if(waringMessage) {
				    					waringMessage += "，" +  $policyNo;
										} else {
											waringMessage = "保单号：" +  $policyNo;
									}
				    		 }
					    	 }
					     
					     if($channel=="06"&&$paymentType=="2"&&( $(this).closest("table").find(".bankCode").attr("disabled")==false)&&(($serviceItems!="33"&&$serviceItems!="8"&&$serviceItems!="9"&&$serviceItems!="27"&&$serviceItems!="12"&&$serviceItems!="15"&&$serviceItems!="17"&&$serviceItems!="1"&&$serviceItems!="2")||(checkTessProjectCode()&&$serviceItems=="1")||(checkTessProjectCode()&&$serviceItems=="2")))
					      {
					    	 if($finAccountNoType)					    		
					    	  {
					    	 var accountNo=null;
					    	 var accountNoType=null;
					    	 var clientAccount=null;
					    	 var bankCategory=null;					    	 
					    	 var $inputAccountNo=null;
					    	 var $inputAccountNoType=null;
					    	 var $inputClientAccount=null;
					    	 var $inputBankCategory=null;				    	 
					    	 $.ajax({type: "GET",
									url:"${ctx}acceptance/branch/queryAccountByPolicyNo.do",
									data:{"policy_no" :$policyNo},
									cache: false,
									async: false,
									dataType:"json",
									success:function(data){
										if(data)
											{
										    accountNo=data.ACCOUNT_NO;
										    accountNoType=data.ACCOUNT_NO_TYPE;
										    clientAccount=data.CLIENT_ACCOUNT;
										    bankCategory=data.BANK_CATEGORY;
											}										
									}});
					    	 $.ajax({type: "GET",
									url:"${ctx}acceptance/branch/getBankCategoryByBankCode.do",
									data:{"bankCode" :$bankCode},
									cache: false,
									async: false,
									dataType:"text",
									success:function(data){
										if(data)
											{
										     $inputBankCategory=data;	
											}
									}});
					    	  $inputAccountNoType=$(this).closest("tbody").find(".finAccountNoType").val();
					    	  $inputAccountNo=$(this).closest("tbody").find(".transferAccountno").val(); 
					    	  $inputClientAccount=$(this).closest("tbody").find(".transferAccountOwner").val();
						
					      if(!($inputBankCategory==bankCategory && $inputAccountNoType==accountNoType && $inputAccountNo==accountNo && $inputClientAccount==clientAccount))
					      {
					      	
					      	if(warningMsg) {
								warningMsg += "，" +  $policyNo;
								} else {
								warningMsg = "保单号：" +  $policyNo;
							}
					      					      
					      }					      					      
					     } 
					    }
					   }
					 });
				  if(waringMessage)
					{
					
					 $.messager.alert("校验错误", waringMessage + " 录入的银行账号类型为空，请单击保单号后再进行银行卡类型的选择!","warning", function(){
							//如果校验失败，需要将TAB切换到第一个有失效元素PANEL
							if(!validator.valid()) {
								var $errDiv = $(".tabDiv:has(:input.error)");
								if($errDiv.length > 0) {
									$("#tabContainer").tabs("select", $errDiv[0].id);
								}
							}
						});			
					 return false;	
					}
				   if(warningMsg) 						
					{							
						 $.messager.alert("校验错误", warningMsg + " 此次保全的补退费账户与续期交费账户不一致，请先操作续期交费账户变更或检查输入内容是否有误!","warning", function(){
						//如果校验失败，需要将TAB切换到第一个有失效元素PANEL
						if(!validator.valid()) {
							var $errDiv = $(".tabDiv:has(:input.error)");
							if($errDiv.length > 0) {
								$("#tabContainer").tabs("select", $errDiv[0].id);
							}
						}
					});						
				   return false;						
				}						
		     	return true;
			
		}
			
			function checkPolicyAddressExists()
			{
				 
				  var address=null;
				  var $policyNo=null;
				  var warningMsg = null;
				  var waringMessage=null;
				  var $approvalServiceType =null;				  
				  $(".policyNo").each(function(i,cont)
						{					      
						 $policyNo=$(".policyNo")[i].value;	
						 $approvalServiceType = $(this).closest("tbody").find(".approvalServiceType").val();
						    
						if($approvalServiceType=='2')
							{
						    if($policyNo)
						     {
						     $.ajax({type: "GET",
									url:"${ctx}acceptance/branch/getPolicyAddressByPolicyNo.do",
									data:{"policy_no" :$policyNo},
									cache: false,
									async: false,
									dataType:"text",
									success:function(data){
										address=data;									 
									}});	
						    
						     }
						    if( address==null||address=="")
							 {
							  
								if(warningMsg) {
									warningMsg += "，" +  $policyNo;
									} else {
									warningMsg = "保单号：" +  $policyNo;
								}
							 }
							}
						});
				  
				 
					  
				  if(warningMsg)
					{					
					 $.messager.alert("校验错误", warningMsg + " 的联系地址为空，批单寄送方式请选择自领或先操作联系方式变更!","warning", function(){
							//如果校验失败，需要将TAB切换到第一个有失效元素PANEL
							if(!validator.valid()) {
								var $errDiv = $(".tabDiv:has(:input.error)");
								if($errDiv.length > 0) {
									$("#tabContainer").tabs("select", $errDiv[0].id);
								}
							}
						});			
					 return false;	
					}
				  
				 return true;
			}
			
			
			/* 保存按钮处理 */
			$("#ok").click(function() {
				var valid = true;
				/* 客户信息中证件有效期小于受理日，对应受理渠道和保全项目限制(只针对有效期为空的) edit by wangmingshun start */
				if(!clientIdnoValidityDateIsExist){
					var idnoValidityDate=$("#idnoValidityDate").val();
					var start=new Date(idnoValidityDate.replace("-", "/").replace("-", "/")); 
					var $applyDate = $(".applyDate");
					var str = '申请书[';
					$applyDate.each(function(index){
						var end=new Date($applyDate[index].value.replace("-", "/").replace("-", "/"));
						if(start < end){
							str += index+"、";
							valid = false;
						}
					})
					if(!valid){
						$.messager.alert("校验错误", str+"]的证件有效期小于客户保全申请日期，无法完成操作！");
						return false;
					}
				}
				/* edit by wangmingshun end */
				
				/*检查证件有效期是否勾选长期  edit by wangmingshun start*/
				if(!clientIdnoValidityDateIsExist){
					if ($('#isLongidnoValidityDate').attr('checked')==true)
					{
					 if($('#idnoValidityDate').val()!='')
						 {
							$.messager.alert("提示", "身份证的有效期勾选为长期时，请不要再录入日期。");
							return false;
						 }
					}
				}
				/*检查证件有效期是否勾选长期  edit by wangmingshun start*/
				
				/* 代审人结合保单检查权限edit by gaojiaming start */
				
				var applyType=$("#applyType").val();
				if(applyType=='12'){
					var objectNo='';
					var objectType='';
					/*由于待审类型取消了 判断时取受理渠道  edit by wangmingshun start */
					/*
					var examType=$("#examType").val();
					if(examType=='02'){
						objectType='2';
						objectNo=$("#approveEmpNo").val();					
					}
					if(examType=='01'){
						objectType='1';
						objectNo=$("#approveDeptNo").val();				
					}*/
					var acceptChannel=$("#acceptChannel").val();
					if(acceptChannel=='3'){
						objectType='1';
						objectNo=$("#approveDeptNo").val();	
					}
					if(acceptChannel=='20'){
						objectType='2';
						objectNo=$("#approveEmpNo").val();	
					}
					/*由于待审类型取消了 判断时取受理渠道  edit by wangmingshun end */
					if(objectNo!=''&&applyType!=''){
						var $policyNo = $(".policyNo");
						$policyNo.each(function(index){				
							if($policyNo[index].value!=''){
						    	 $.ajax({type: "GET",
										url:"${ctx}approve/checkPrivsByPolicyNo.do?objectNo="+objectNo+"&objectType="+objectType,
										data:{"policyNo" :$policyNo[index].value},
										cache: false,
										async: false,
										dataType:"text",
										success:function(data){
											if(data!='Y'){
												$.messager.alert("校验错误", objectNo+"对保单号"+$policyNo[index].value+"没有代审权限！");	
												valid=false;
												return false;
											}
										}
									});		
							}
							if(!valid){
								return false;
							}
						});						
					}
				} 
				/* 代审人结合保单检查权限edit by gaojiaming end */				
				/* edit by gaojiaming start */				
				var $serviceItems = $(".serviceItems");
				$serviceItems.each(function(index){
					if($('#fillForm'+index).attr("checked")==true){	
						$('#fillForm'+index).val("Y");
						if(($serviceItems[index].value).indexOf(",")>-1){
							$.messager.alert("校验错误", "申请书[" + (index+1)+"]是免填单业务，只能选取一个服务项目");	
							valid=false;
						}else if($serviceItems[index].value.length>0){
					    	 $.ajax({type: "GET",
									url:"${ctx}acceptance/branch/getBarCodeNo.do",
									data:{"serviceItem" :$serviceItems[index].value},
									cache: false,
									async: false,
									dataType:"text",
									success:function(data){
										$('#barcodeNo'+index).val(data);
										$('#barcodeNo'+index).change();
									}
								});							
						}
					}else{
						var barcode=$('#barcodeNo'+index).val();
						if(barcode.substr(4, 2) == "09" ){
							$.messager.alert("校验错误", "申请书[" + (index+1)+"]条形码为免填单业务条码，但是未勾选免填单，请核实！");	
							valid=false;
						}						
					}
				});					
				/* edit by gaojiaming end */					
				$("#submitFlag").attr("name", "save");
				//校验关联条码是否存在
				var found = false;
				var $relateBarcode = $(".relateBarcode");
				var $barcode = $(".barcode");
				var $policyNo = $(".policyNo");
				$relateBarcode.each(function(idx){
					if(valid && $relateBarcode[idx].value) {
						if($relateBarcode[idx].value == $($relateBarcode[idx]).closest("table").find(".barcode").val()) {
							$.messager.alert("校验错误", "关联条码" + $relateBarcode[idx].value + "不能与主条码相同!");
							valid = false;
						} else {
							$barcode.each(function(idx1){
								if($barcode[idx1].value == $relateBarcode[idx].value)
									found = true;
							});
							if(!found) {
								$.messager.alert("校验错误", "无效的关联条码：" + $relateBarcode[idx].value);
								valid = false;
							}
						}
						if(!valid) {
							$relateBarcode[idx].focus();
						}
					}
				});
				//校验条形码是否重复
				if(valid) {
					var barcodeArr = [];
					var barcode;
					$barcode.each(function(idx, content){
						barcode = $barcode[idx].value;
						if(valid && barcode && barcodeArr.indexOf(barcode) > -1) {
							$.messager.alert("校验错误", "条形码重复：" + barcode);
							valid = false;
						}
						barcodeArr.push(barcode);
					});
				}
				//校验保单号
				if(valid) {
					var policyNo;
					$policyNo.each(function(idx, content){
						policyNo = $policyNo[idx].value;
						if(valid && policyNo && validPolilcyNoArr.indexOf(policyNo) < 0) {
							$.messager.alert("校验错误", "无效的保单号：" + policyNo);
							valid = false;
						}
					});
				}
				
				//触发默认校验
				if(valid && checkSurNotPaied()&&check06ChannelAccount()&&checkPolicyAddressExists()) {
					$("#fm").submit();
					//如果校验失败，需要将TAB切换到第一个有失效元素PANEL
					if(!validator.valid()) {
						var $errDiv = $(".tabDiv:has(:input.error)");
						if($errDiv.length > 0) {
							$("#tabContainer").tabs("select", $errDiv[0].id);
							valid = false;
						}
					}
				}
				
				
				
				return false;
			});
			
			$(".applyDate").click(function(){
				WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}',maxDate:'${CodeTable.posTableMap.SYSDATE}',isShowToday:false});
			});
			
			//获取保单原账户
			$(".policyAcctCb").live("change",function(){
				var $the = $(this);
				if(this.checked==true){
					var policyNo = $the.closest("table").find(".policyNo").val();
					if(policyNo=="" || validPolilcyNoArr.indexOf(policyNo) < 0){
						$.messager.alert("提示", "请先输入正确的保单号");
						this.checked=false;
						return;
					}
					$.post("${ctx}acceptance/branch/policybankacctno", 
					{policyNo:policyNo},
					function(map) {
						if(map.flag==0){
							var $bank = $the.closest("table").find(".bankCode");
							$bank.val(map.acctBankCode).attr("readonly",true);
							$bank.siblings("div").remove();
							$bank.closest("td").append($("<div class='left'/>").html(map.acctBankName));
							$the.closest("table").find(".transferAccountno, .bankAccountConfirm").val(map.acctNo).attr("readonly",true);
							$the.closest("table").find(".transferAccountOwner").val(map.acctName).attr("readonly",true);
							$the.closest("table").find(".finAccountNoType").val(map.acctNoType);
						}else if(map.flag==1){
							$.messager.alert("提示","该保单无原账户信息");
							$the.attr("checked",false);
						}else{
							$.messager.alert("对不起","获取保单账户失败，请联系技术人员支持");
							$the.attr("checked",false);
						}
					}, "json");
					
				}else{
					$the.closest("table").find(".bankCode").removeAttr("readonly");
					$the.closest("table").find(".transferAccountno, .bankAccountConfirm").removeAttr("readonly");
					$the.closest("table").find(".transferAccountOwner").removeAttr("readonly");					
					$(".bankSelectLink").show();
				}
			});
			$(".policyNo").live("change",function(){
				$(this).closest("table").find(".policyAcctCb").attr("checked",false).trigger("change");
			});
			
			//edit by wangmingshun start
		    $(".serviceItems:text").blur(function() {
		    	$(".serviceItems:text").each(function() {
			    	var serviceItems = $(this);
			    	var $thisPolicy = $(this).closest("tbody").find(".policyNo");
			    	var policyNo = $thisPolicy.val();
			    	if(serviceItems.val() == "17" || serviceItems.val().indexOf("17") >= 0) {
			    		getFinPosPayInfo($thisPolicy);
			    	} else {
			    		 $thisPolicy.closest("tbody").find(".finAccountNoType").empty();
				    	 $.ajax({type: "GET",
								url:"${ctx}acceptance/branch/queryPolicyChannelType.do",
								data:{"policyNo" : policyNo},
								cache: false,
								async: false,
								dataType:"text",
								success:function(data){
									 if(data=="06") {							    	   
										 $thisPolicy.closest("tbody").find(".finAccountNoType").append("<option value='1'>卡</option><option value='3'>信用卡</option>");
							    	 } else {
							    	  	 $thisPolicy.closest("tbody").find(".finAccountNoType").append("<option value='1'>卡</option>");
							    	 }
						  }});
			    	}
		    	})
		    });	
		}
		
		/**
		 * 设置页面初始状态
		 */
		function initPage() {
			$("#applyType").trigger("change");
			$(".paymentType").trigger("change");
			$(".serviceItems:text").trigger("change");
			$(".applyFileMaterials:text").trigger("change");
			//$(".approvalServiceType").trigger("change");
			$(".barcode").trigger("change");
			
			$("#tabContainer").css("display", "").tabs();
			
			$(".policyNo").trigger("focusout");
			
			setTimeout(function(){
				<c:choose>
					<c:when test="${not empty selectedIndex}">
					$("#tabContainer").tabs("select", "申请书[${selectedIndex + 1}]");
					</c:when>
					<c:otherwise>
					$("#tabContainer").tabs("select", "申请书[1]");
					</c:otherwise>
				</c:choose>
			}, 100);
			
			/* 控制证件有效期，如果为空则可输入，不为空则设置为只读  edit by wangmingshun start */
			clientIdnoValidityDateIsExist = "${clientIdnoValidityDateIsExist}"=='true'?true:false;
			if(clientIdnoValidityDateIsExist){
				$("#idnoValidityDate").attr("disabled","false");
				$('#isLongidnoValidityDate').attr("disabled","true");
			}
			//初始化可疑交易备注
			$("#isDoubtTransaction").trigger("change");
			/* edit by wangmingshun end */
		}
		
		/**
		 * 初始化校验器
		 */
		function initValidator() {
			validator = $("#fm").validate({ignore: ".ignore :input,.ignore"});
		}
		
		/* 初始化代理 */
		var initProxyComplete = false;
		$(document).ready(function() {
			if(!initProxyComplete) {
				initProxyComplete = true;
				bindEventHandlers();
				initPage();
				initValidator();
			}
		});
		
		function asc(x,y) {
			if (x > y)
				return 1;
			else
				return -1;
		}
		
		/* 格式化保全项目录入值，并去除掉无效录入值  */
		function formatServiceItemsInput(val, barcode) {
			var v = val.replace(/\D/g, " ");
			v = $.trim(v);
			v = v.replace(/\s+/g, ",");
			var arr = v.split(",");
			var arr1 = [];
			var num;
			$.each(arr, function(idx, content){
				num = parseInt(content);
				//edit by wangmingshun 项目范围添加44,扩大到50,方便以后不用修改
				if(num >= 1 && num <= 50 && arr1.indexOf(num) == -1 && filterServiceItemsByBarcode("" + num, barcode)) {
					arr1.push(num);
				}
			});
			arr1.sort(asc);
			return arr1.join(",");
		}
		
		/* 格式化申请材料录入值，并去除掉无效录入值  */
		function formatMaterialInput(val) {
			var v = val.replace(/\D/g, " ");
			v = $.trim(v);
			v = v.replace(/\s+/g, ",");
			var arr = v.split(",");
			var arr1 = [];
			var num;
			$.each(arr, function(idx, content){
				num = parseInt(content);
				if(getApplyMaterialLabels(num + "")) {
					arr1.push(num);
				}
			});
			arr1.sort(asc);
			return arr1.join(",");
		}

		posValidateHandler=function(){
			return stop20and23();
		};
		
		function stop20and23(){
			var str = "";
			$(".serviceItems").each(function(){
				str += $(this).val()+",";
			});
			if(str.indexOf("20")>=0 && str.indexOf("23")>=0){
				$.messager.alert("提示","投保人变更和续期缴费方式变更不能在同一个批次受理，请先做前者");
				return false;
			}
			return true;
		}
			
		/* 免填单控制条形码只读 edit by gaojiaming start */		
		function fillForm(index,flag){			
			if($('#fillForm'+index).attr("checked")==true){	
				$('#barcodeNo'+index).attr("readonly",true); 
				$('#barcodeNo'+index).attr("style","background-color:#d2d2d2");
			}else{
				$('#barcodeNo'+index).removeAttr("readonly");
				$('#barcodeNo'+index).removeAttr("style");
			}
			if(flag!="load"){
				$('#barcodeNo'+index).val("");				
			}
		}
		/* edit by gaojiaming end */
		
		/* 控制亲办件及分支公司柜面 edit by gaojiaming start */		
		function controlFillForm(){
			var applyType=$("#applyType").val();
			var acceptChannel=$("#acceptChannel").val();
			var $serviceItems = $(".serviceItems");
			$serviceItems.each(function(index){
				if(applyType=="1"&&(acceptChannel=="1"||acceptChannel=="2")){	
					$('#fillForm'+index).show(); 
					$('#fillFormText'+index).show(); 
				}else{
					$('#fillForm'+index).hide();
					$('#fillFormText'+index).hide(); 
					$('#fillForm'+index).removeAttr("checked"); 
					$('#barcodeNo'+index).removeAttr("readonly");
					$('#barcodeNo'+index).removeAttr("style");					
				}
			});				
		}
		/* edit by gaojiaming end */
		/* 控制受理渠道 edit by gaojiaming start */		
		function controlAcceptChannel(){
			var applyType=$("#applyType").val();
			//var examType=$("#examType").val(); //待审已取消 edit by wangmingshun
			$("#acceptChannel").val("");
			/* 取消 代审类型 控制  edit by wangmingshun start
			if(applyType=='12'&& examType=='01'){				
				$("#acceptChannel option").remove();
				$("#acceptChannel").append( "<option value=\"3\">银行柜面</option>" );		
			}else if(applyType=='12'&& examType=='06'){
				$("#acceptChannel option").remove();
				 $("#acceptChannel").append( "<option value=\"6\">经代公司柜面</option>" );					
				
			}
			else if(applyType=='11'){				
				$("#acceptChannel option").remove();
				$("#acceptChannel").append( "<option value=\"1\">分/支公司柜面</option>" );
				$("#acceptChannel").append( "<option value=\"2\">四级机构/服务部柜面</option>" );
			}
			else if(applyType=='12'&& (examType=='02'||examType=='04'||examType=='05'||examType=='03')){
				$("#acceptChannel option").remove();
				$("#acceptChannel").append( "<option value=\"1\">分/支公司柜面</option>" );
							
			}else{
				$("#acceptChannel option").remove();
				 $("#acceptChannel").append( "<option value=\"1\">分/支公司柜面</option>" );				
				 $("#acceptChannel").append( "<option value=\"2\">四级机构/服务部柜面</option>" );
			}
			*/
			if(applyType=='8'){				
				$("#acceptChannel option").remove();
				$("#acceptChannel").append( "<option value=\"1\">分/支公司柜面</option>" );
			}else if(applyType=='12'){
				$("#acceptChannel option").remove();
				$("#acceptChannel").append( "<option value=\"3\">银行柜面</option>" );
				$("#acceptChannel").append( "<option value=\"6\">经代柜面</option>" );
				$("#acceptChannel").append( "<option value=\"20\">银代客户经理</option>" );
				$("#acceptChannel").append( "<option value=\"21\">团险业务员</option>" );
				$("#acceptChannel").append( "<option value=\"22\">个险营销员</option>" );
				$("#acceptChannel").append( "<option value=\"23\">续期服务人员</option>" );
				$("#acceptChannel").append( "<option value=\"2\">四级机构/服务部柜面</option>" );
			}else{
				$("#acceptChannel option").remove();
				$("#acceptChannel").append( "<option value=\"1\">分/支公司柜面</option>" );				
				$("#acceptChannel").append( "<option value=\"2\">四级机构/服务部柜面</option>" );
			}
			/* edit by wangmingshun end */
		}
		/* edit by gaojiaming end */		
		/* 页面加载时控制条形码样式 edit by gaojiaming start */		
		window.onload=function(){
			$("#approveEmpNo").blur();
			$("#approveDeptNo").blur();
			var $serviceItems = $(".serviceItems");
			$serviceItems.each(function(index){
				fillForm(index,"load");
			});
		}
		/* edit by gaojiaming end */
		/* 代审查询和校验 edit by gaojiaming start */		
		function approveCheckAndQuery(objectType,objectNo){
			$.ajax({type: "GET",
				url:"${ctx}approve/getObjectInfoByObjectNo.do?needCheckPrivs=Y&objectType="+objectType,
				data:{"objectNo" :objectNo},
				cache: false,
				async: false,
				dataType:"json",
				success:function(data){
					if(data.objectName==""||data.objectName==null){
						$.messager.alert("提示", "查无"+objectNo+"工号或网点,请重新输入！");	
						cleanApproveValue();	
						return false;
					}
					if(data.privs!="Y"){
						$.messager.alert("提示", objectNo+"木有权限！");	
						cleanApproveValue();
						return false;
					}
					if(data.blacklist=="Y"){
						$.messager.alert("提示", objectNo+"已上黑名单！");
						cleanApproveValue();
						return false;
					}
					if(data.approving=="Y"){
				        $.messager.alert("提示", "该代审人员不良记录已送审，请从严审核保全资料！", "info", function () {
							if(objectType=='2'){
								$("#approveEmpName").html(data.objectName);
								$("#approveEmpNoBadCount").html(data.badBehaviorCount+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"view('"+objectType+"','"+objectNo+"')\" >详情</a>");	
							}else if (objectType=='1'){
								$("#approveDeptName").html(data.objectName);
								$("#approveDeptNoBadCount").html(data.badBehaviorCount+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"view('"+objectType+"','"+objectNo+"')\" >详情</a>");								
							}												
							return false;							
				        });	
						return false;
					}else{
						if(objectType=='2'){
							$("#approveEmpName").html(data.objectName);
							$("#approveEmpNoBadCount").html(data.badBehaviorCount+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"view('"+objectType+"','"+objectNo+"')\" >详情</a>");	
						}else if (objectType=='1'){
							$("#approveDeptName").html(data.objectName);
							$("#approveDeptNoBadCount").html(data.badBehaviorCount+"&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"view('"+objectType+"','"+objectNo+"')\" >详情</a>");								
						}
						return false;
					}					
				}
			});				
		}
		
		function cleanApproveValue(){
			$("#approveEmpNo").val("");
			$("#approveEmpName").html("");
			$("#approveEmpNoBadCount").html("");
			$("#approveDeptNo").val("");
			$("#approveDeptName").html("");
			$("#approveDeptNoBadCount").html("");			
		}
		function view(objectType,objectNo){
				var columns;
				var wd = 680;
				if (objectType=='2'){
					columns=[[{field:"OBJECT_NO", title:"号码", align : 'center', width:wd*7*2/100},
					         {field:"OBJECT_NAME", title:"名称", align : 'center', width:wd*5*2/100},
					         {field:"EMP_IDNO", title:"证件号", align : 'center', width:wd*11*2/100},
					         {field:"DEPT_NAME", title:"所属网点", align : 'center', width:wd*12*2/100},
					         {field:"RECORD_DATE", title:"时间", align : 'center', width:wd*6*2/100},
					         {field:"RECORD_USER", title:"记录人", align : 'center', width:wd*13*2/100},
					         {field:"POLICY_NO", title:"保单号", align : 'center', width:wd*10*2/100},
					         {field:"BEHAVIOR_TYPE", title:"行为描述", align : 'center', width:wd*8*2/100},
					         {field:"DESCRPITON", title:"原因", align : 'center', width:wd*17*2/100}
					         ]];			
				}else{
					columns=[[{field:"OBJECT_NO", title:"号码", align : 'center', width:wd*10*2/100},
						         {field:"OBJECT_NAME", title:"名称", align : 'center', width:wd*12*2/100},
						         {field:"BRANCH_NAME", title:"对应机构", align : 'center', width:wd*10*2/100},
						         {field:"RECORD_DATE", title:"时间", align : 'center', width:wd*6*2/100},
						         {field:"RECORD_USER", title:"记录人", align : 'center', width:wd*13*2/100},
						         {field:"POLICY_NO", title:"保单号", align : 'center', width:wd*10*2/100},
						         {field:"BEHAVIOR_TYPE", title:"行为描述", align : 'center', width:wd*8*2/100},
						         {field:"DESCRPITON", title:"原因", align : 'center', width:wd*20*2/100}
						         ]];		
				}
				$("#badBehavior").datagrid({
					url:"${ctx}approve/queryBadBehaviorPage.do?objectNo="+objectNo+"&objectType="+objectType,
					pagination:true,
					rownumbers:true,
					singleSelect:true,
					striped:true,
					width : wd,
					nowrap : true,
					striped : true,		
					columns:columns
				});	
				$("#badBehaviorWindowDiv").window("open");
		}		
		/* edit by gaojiaming end */
		/* 校验原账户电销项目代码控制 edit by yanghanguang start */
		function checkTessProjectCode(){
			var flag=false;
			var policyNo = $("#policyNo0").val();
			$.ajax({type: "GET",
				url:"${ctx}acceptance/branch/getProjectCodeByPolicyNo.do",
				data:{"policyNo" :policyNo},
				cache: false,
				async: false,
				dataType:"text",
				success:function(data){
					if(data=="001"){
						flag=true;				
					}    
				}
			 }	
			);
			return 	flag;		
		}
		
		/*检查是否是代办代审的试点机构 edit by wangmingshun start*/
		function checkExamBranch(){
			var policyNo = $("#policyNo0").val();
			var flag = true;
			var objectNo='';
			var objectType='';
			$.ajax({type: "GET",
				url:"${ctx}approve/checkExamBranch.do?objectNo="+objectNo+"&objectType="+objectType,
				data:{"policyNo" : policyNo},
				cache: false,
				async: false,
				dataType:"text",
				success:function(data){
					if(data!='Y' && data!=''){
						flag = false;
					}
				}
			});
			return flag;
		}
		/*检查是否是代办代审的试点机构 edit by wangmingshun end*/
		
		$("#isDoubtTransaction").live("change", function(){
			if($("#isDoubtTransaction").attr("checked") == true) {
				//$(".doubtTransactionRemark").show();
				$("th.doubtTransactionRemark").show();
				$("th .doubtTransactionRemark").focus();
				var remark = $("th .doubtTransactionRemark").val();
				$("th .doubtTransactionRemark").val("");
				$("th .doubtTransactionRemark").val(remark);
			} else {
				$("th.doubtTransactionRemark").hide();
			}
		});
		
		//根据保单号查询最近一期续期保费退费信息 传入参数为当前操作的保单号的DOM对象
		function getFinPosPayInfo(policy) {
			var policyNo = $.trim(policy.val());
			if(policyNo != '') {
				policy.closest("tbody").find(".finAccountNoType").empty();
				$.ajax({type: "GET",
					url:"${ctx}acceptance/branch/getFinPosPayInfo.do",
					data:{"policyNo" : policyNo},
					cache: false,
					async: false,
					dataType:"json",
					success:function(data){
						var flag = data.p_flag;
						var creditFlag = data.creditFlag;
						var accountNo = data.accountNo;
						var premDue = data.premDue;
						if(flag == 'Y' && creditFlag == 'Y') {
							policy.closest("tbody").find(".finAccountNoType").empty().append("<option value='1'>卡</option><option value='3'>信用卡</option>");
						} else {
							policy.closest("tbody").find(".finAccountNoType").empty().append("<option value='1'>卡</option>");
						}
					}});
			}
		} 
		
	</script>
<style type="text/css">
.representOption {
	display: none;
}

.itemList {
	margin: 0px;
	padding: 0px;
	display: block;
	list-style-type: none;
}
</style>
</sf:override>

<sf:override name="content">
	<sfform:form commandName="acceptance__apply_info"
		action="applyInfoInput" id="fm">
		<input type="hidden" name="index" value="" />
		<input type="hidden" id="submitFlag" />
		<div class="easyui-panel" title="客户基本信息" collapsible="true">
		<table class="infoDisTab">
			<thead>
				<tr>
					<th>客户号</th>
					<th>姓名</th>
					<th>出生日期</th>
					<th width="50">性别</th>
					<th>证件类型</th>
					<th>证件号码</th>
					<th>证件有效期</th><!-- 增加证件有效期显示 edit by wangmingshun start -->
					<th>是否为VIP客户</th>
				</tr>
			</thead>
			<tbody>
				<tr class="odd_column">
					<td>${acceptance__apply_info.clientInfo.clientNo}</td>
					<td>${acceptance__apply_info.clientInfo.clientName}</td>
					<td><fmt:formatDate pattern="yyyy-MM-dd"
						value="${acceptance__apply_info.clientInfo.birthday}" /></td>
					<td>${acceptance__apply_info.clientInfo.sexDesc}</td>
					<td>${acceptance__apply_info.clientInfo.idTypeDesc}</td>
					<td>${acceptance__apply_info.clientInfo.idNo}</td>
					<!-- 增加证件有效期显示  edit by wangmingshun start -->
					<td>
						<sfform:input path="clientInfo.idnoValidityDate" size="18px" id="idnoValidityDate"
							onfocus="WdatePicker({skin:'whyGreen',startDate:'',minDate:'',isShowToday:false});"
							cssClass="applyDate Wdate {required:function(element){if($('#applyType').val() == '5'||$('#isLongidnoValidityDate').attr('checked')==true){return false;}else{return true;}}}" />
							长期	<sfform:checkbox path="clientInfo.isLongidnoValidityDate"  id="isLongidnoValidityDate" cssClass="isLongidnoValidityDate"/>
					</td>
					<!-- 增加证件有效期显示  edit by wangmingshun end -->
					<td>${acceptance__apply_info.clientInfo.vipGrade ne '0' ? '是' : '否'}</td>
				</tr>
			</tbody>
		</table>
		</div>
		
		<div class="layout_div_top_spance" >
		<div class="easyui-panel" title="" collapsible="false">
		<table class="layouttable">
			<tbody>
				<tr>
					<th class="layouttable_td_label" style="width:200px;text-align:center;left:15px;">
						<sfform:checkbox path="isDoubtTransaction"  id="isDoubtTransaction"/> 是否可疑交易
					</th>
					<th class="layouttable_td_label doubtTransactionRemark" >备注：</th>
					<th class="layouttable_td_widget doubtTransactionRemark" >
						<sfform:input path="doubtTransactionRemark" style="width:300px;"
							cssClass="doubtTransactionRemark {required:function(element){if($('#isDoubtTransaction').attr('checked')==true){return true;} else {return false;}}}"/>
					</th>
					<th></th>
				</tr>
			</tbody>
		</table>
		</div>
		</div>
		
		<div class="layout_div_top_spance">
		<div class="easyui-panel" title="基本信息录入" collapsible="true">
		<table class="layouttable">
			<tbody>
				<tr>
					<th class="layouttable_td_label">申请类型: <span
						class="requred_font">*</span></th>
					<td class="layouttable_td_widget"><sfform:select
						path="applyTypeCode"
						items="${CodeTable.posTableMap.POS_APPLY_TYPE_CODE}"
						itemLabel="description" itemValue="code" cssClass="input_select"
						id="applyType" /></td>
					<th class="layouttable_td_label representOption3"
						style="display: none;">代审类型: <span class="requred_font">*</span></th>
					<td class="layouttable_td_widget representOption3"
						style="display: none;"><sfform:select path="examType"
						items="${CodeTable.posTableMap.POS_EXAM_TYPE}"
						itemLabel="description" itemValue="code" cssClass="input_select"
						id="examType" /></td>
					<th class="layouttable_td_label">受理渠道: <span
						class="requred_font">*</span></th>
					<td class="layouttable_td_widget"><sfform:select
						path="acceptChannelCode"
						items="${CodeTable.posTableMap.POS_ACCEPT_CHANNEL_CODE}"
						itemLabel="description" itemValue="code" cssClass="input_select"
						id="acceptChannel" /></td>
					<th class="layouttable_td_label representOption empNo"><span
						class="representOption empNo">业务员代码:</span></th>
					<td class="layouttable_td_widget representOption"><sfform:input
						path="empNo" cssClass="input_text representOption empNo"
						title="请按回车键查询业务员" /></td>
				</tr>
				</tr>
				<tr class="representOption" style="display: none;">
					<th class="layouttable_td_label">代办人姓名: <span
						class="requred_font">*</span></th>
					<td class="layouttable_td_widget"><sfform:input
						path="representor"
						cssClass="input_text {required:function(element){return $('#applyType').val()=='11';}}" />
					</td>
					<th class="layouttable_td_label">代办人证件类型: <span
						class="requred_font">*</span></th>
					<td class="layouttable_td_widget"><sfform:select path="idType"
						id="representIdTypeCode" items="${CodeTable.posTableMap.ID_TYPE}"
						itemLabel="description" itemValue="code"
						cssClass="input_select {required:function(element){return $('#applyType').val()=='11';}}" />
					</td>
					<th class="layouttable_td_label">代办人证件号码: <span
						class="requred_font">*</span></th>
					<td class="layouttable_td_widget"><sfform:input
						path="representIdno"
						cssClass="input_text {required:function(element){return $('#applyType').val()=='11';}}" />
					</td>
				</tr>
				<tr class="representOption1" style="display: none;">
					<th class="layouttable_td_label">代审人工号: <span
						class="requred_font">*</span></th>
					<td class="layouttable_td_widget"><sfform:input
						path="approveEmpNo" id="approveEmpNo"
						cssClass="input_text {required:function(element){return $('#applyType').val()=='12' && $('#acceptChannel').val()=='20' && checkExamBranch();}}" />
					</td>
					<th class="layouttable_td_label" align="center" valign="middle">代审人姓名:</th>
					<td class="layouttable_td_widget" width="15%" id="approveEmpName">
					</td>
					<th class="layouttable_td_label" align="center" valign="middle">不良记录次数:
					</th>
					<td class="layouttable_td_widget" width="15%"
						id="approveEmpNoBadCount"></td>
				</tr>
				<tr class="representOption2" style="display: none;">
					<th class="layouttable_td_label">网点编码: <span
						class="requred_font">*</span></th>
					<td class="layouttable_td_widget"><sfform:input
						path="approveDeptNo" id="approveDeptNo"
						cssClass="input_text {required:function(element){return $('#applyType').val()=='12' && $('#acceptChannel').val()=='3' && checkExamBranch();}}" />
					</td>
					<th class="layouttable_td_label" align="center" valign="middle">网点名称:
					</th>
					<td class="layouttable_td_widget" width="15%" id="approveDeptName">
					</td>
					<th class="layouttable_td_label" align="center" valign="middle">不良记录次数:
					</th>
					<td class="layouttable_td_widget" width="15%"
						id="approveDeptNoBadCount"></td>
				</tr>
			</tbody>
		</table>
		</div>
		</div>

		<div class="layout_div_top_spance">
		<div class="easyui-panel" title="受理项目录入" collapsible="true">
		<div id="tabContainer" style="display: none;">
		<c:forEach varStatus="status" items="${acceptance__apply_info.applyFiles}" var="item">
			<div title="申请书[${status.index+1}]" style="padding: 0;"
				class="tabDiv" id="申请书[${status.index+1}]">
			<table class="layouttable">
				<tbody>
					<tr>
						<th class="layouttable_td_label">条&nbsp;形&nbsp;码: <span
							class="requred_font">*</span></th>
						<td class="layouttable_td_widget"><sfform:input
							path="applyFiles[${status.index}].barcodeNo"
							id="barcodeNo${status.index}"
							cssClass="input_text barcode {required:true, barcodeNo:true, remote:{url:'${ctx}include/isBarcodeNoNotUsed.do',type:'post',data:{barcodeNo:function(){return $('#barcodeNo${status.index}').val();}}},messages:{remote:'条码校验不正确或已经被使用'}} " />
						<!-- edit by gaojiaming start --> <sfform:checkbox
							path="fillFormList[${status.index}].fillForm" value="Y"
							id="fillForm${status.index}"
							onclick="javascript:fillForm('${status.index}');" /> <span
							id="fillFormText${status.index}">免填单</span> <!-- edit by gaojiaming end -->
						</td>
						<th class="layouttable_td_label">申请保单号: <span
							class="requred_font">*</span></th>
						<td class="layouttable_td_widget"><sfform:input
							path="applyFiles[${status.index}].policyNo"
							id="policyNo${status.index}"
							cssClass="policyNo input_text nopaste required" /></td>
						<th class="layouttable_td_label">关联条形码:</th>
						<td class="layouttable_td_widget"><sfform:input
							path="applyFiles[${status.index}].relateBarcode"
							cssClass="input_text relateBarcode {barcodeNo:true}" /></td>
					</tr>
					<tr>
						<th class="layouttable_td_label">服务项目: <span
							class="requred_font">*</span></th>
						<td class="layouttable_td_widget"><sfform:input
							path="applyFiles[${status.index}].serviceItems"
							cssClass="serviceItems input_text required" /> <a
							class="serviceItemSelect" href="javascript:void(0);">选择项目</a><br />
						</td>
						<th class="layouttable_td_label">资料页数:</th>
						<td class="layouttable_td_widget_1"><sfform:input
							path="applyFiles[${status.index}].applyFileMaterials"
							cssClass="applyFileMaterials input_text" /> <a
							class="applyFileMaterialSelect" href="javascript:void(0);">选择资料</a><br />
						<sfform:label
							path="applyFiles[${status.index}].applyFileMaterials"
							cssClass="applyFileMaterials"></sfform:label></td>
						<th class="layouttable_td_label">申请日期: <span
							class="requred_font">*</span></th>	
						<td class="layouttable_td_widget_1">
							<sfform:date path="applyFiles[${status.index}].applyDate"
								cssClass="applyDate Wdate required" /></td>
					</tr>
					<tr>
						<th class="layouttable_td_label">批单送达方式:</th>
						<td class="layouttable_td_widget"><sfform:select
							path="applyFiles[${status.index}].approvalServiceType"
							items="${CodeTable.posTableMap.APPROVAL_SERVICE_TYPE}"
							itemLabel="description" itemValue="code"
							cssClass="input_select approvalServiceType" /></td>
						<th class="layouttable_td_label">签名不符件:</th>
						<td class="layouttable_td_widget"><sfform:checkbox
							path="applyFiles[${status.index}].signAccordFlag" value="Y" /> <a
							class="signView" href="javascript:void(0);">查询签名影像</a></td>
						<th class="layouttable_td_label">备&nbsp;&nbsp;&nbsp;注:</th>
						<td class="layouttable_td_widget"><sfform:input
							path="applyFiles[${status.index}].remark" cssClass="input_text" />
						</td>
					</tr>
					<!--
					<tr class="odd approvalServiceTypeOption">
						<th class="layouttable_td_label">批单邮寄邮编: <span
							class="requred_font">*</span></th>
						<td class="layouttable_td_widget"><sfform:input
							path="applyFiles[${status.index}].zip"
							cssClass="input_text {required:function(ele){return $('#applyFiles${status.index}\\\\.approvalServiceType').val()=='2'}}" />
						</td>
						<th class="layouttable_td_label">批单邮寄地址: <span
							class="requred_font">*</span></th>
						<td class="layouttable_td_widget" colspan="3"><sfform:input
							path="applyFiles[${status.index}].address"
							cssClass="multi_text {required:function(ele){return $('#applyFiles${status.index}\\\\.approvalServiceType').val()=='2'}}" />
						</td>
					</tr>
					-->
					<tr class="premRelatedServiceItemsOption">
						<th class="layouttable_td_label">收付方式: <span
							class="requred_font">*</span></th>
						<td class="layouttable_td_widget"><sfform:select
							path="applyFiles[${status.index}].paymentType"
							items="${CodeTable.posTableMap.CHARGING_METHOD}"
							itemLabel="description" itemValue="code"
							cssClass="input_select paymentType required"/>
							</td>
						<!--	
						<th class="layouttable_td_label"><span
							class="paymentTypeOption">账户类型:</span></th>
						<td class="layouttable_td_widget"><span
							class="paymentTypeOption"> <sfform:select
							path="applyFiles[${status.index}].foreignExchangeType"
							items="${CodeTable.posTableMap.FIN_FOREIGN_EXCHANGE_TYPE}"
							itemLabel="description" itemValue="code" cssClass="input_select"
							disabled="true" /> </span></td>
						-->
						<th class="layouttable_td_label"><span
							class="paymentTypeOption">对公账号:</span></th>
						<td><sfform:checkbox
							path="applyFiles[${status.index}].privateFlag" value="1"
							class="paymentTypeOption" /></td>
						<th class="layouttable_td_label"><span
							class="paymentTypeOption">保单原账户:</span></th>
						<td><input type="checkbox"
							class="paymentTypeOption policyAcctCb" /></td>
					</tr>
					<tr class="paymentTypeOption premRelatedServiceItemsOption">
						<th class="layouttable_td_label">开户银行: <span
							class="requred_font">*</span></th>
						<td class="layouttable_td_widget_1"><sfform:input
							path="applyFiles[${status.index}].bankCode"
							cssClass="bankCode input_text {required:function(ele){return $('#applyFiles${status.index}\\\\.paymentType').val()=='2'}, messages:'请选择银行'}"
							readonly="true" /> <a href="javascript:void(0);"
							class="bankSelectLink">选择银行</a></td>
						<th class="layouttable_td_label">银行卡类型:</th>
						<td class="layouttable_td_widget_1"><select
							class="finAccountNoType"
							name="applyFiles[${status.index}].accountNoType">

						</select> <!--<sfform:select
							path="applyFiles[${status.index}].accountNoType"
							items="${CodeTable.posTableMap.FIN_ACCOUNT_NO_TYPE}"
							itemLabel="description" itemValue="code"></sfform:select>
							<input type="text">
							--></td>
						<td class="layouttable_td_widget">
						<table>
							<tr>
								<th class="layouttable_td_label">银行账号: <span
									class="requred_font">*</span></th>
							</tr>
							<tr>
								<th class="layouttable_td_label">再次输入银行账号: <span
									class="requred_font">*</span></th>
							</tr>
						</table>

						</td>

						<td class="layouttable_td_widget">
						<table>
							<tr>

								<td class="layouttable_td_widget"><sfform:input
									path="applyFiles[${status.index}].transferAccountno"
									cssClass="transferAccountno input_text nopaste {required:function(ele){return $('#applyFiles${status.index}\\\\.paymentType').val()=='2'}}"
									onchange="$('#bankConfirm${status.index}').val('');"
									style="width:155px;" /></td>
							</tr>
							<tr>

								<td class="layouttable_td_widget"><input type="text"
									value="${acceptance__apply_info.applyFiles[status.index].transferAccountno}"
									id="bankConfirm${status.index}" style="width: 155px;"
									class="input_text nopaste bankAccountConfirm {equalTo:'#applyFiles${status.index}\\.transferAccountno', required:function(ele){return $('#applyFiles${status.index}\\.paymentType').val()=='2'}}" />
								</td>
							</tr>
						</table>
						</td>
					</tr>
					<tr class="premRelatedServiceItemsOption">
						<th class="layouttable_td_label">收/付款人姓名: <span
							class="requred_font">*</span></th>
						<td class="layouttable_td_widget"><sfform:input
							path="applyFiles[${status.index}].transferAccountOwner"
							cssClass="transferAccountOwner input_text required" /></td>
						<th class="layouttable_td_label">收/付款人证件类型: <span
							class="requred_font">*</span></th>
						<td class="layouttable_td_widget"><sfform:select
							path="applyFiles[${status.index}].transferAccountOwnerIdType"
							id="transferAccountOwnerIdType${status.index}"
							items="${CodeTable.posTableMap.ID_TYPE}" itemLabel="description"
							itemValue="code" cssClass="input_select required" /></td>
						<th class="layouttable_td_label">收/付款人证件号码: <span
							class="requred_font">*</span></th>
						<td class="layouttable_td_widget"><sfform:input
							path="applyFiles[${status.index}].transferAccountOwnerIdNo"
							cssClass="input_text {required:true,idNo:{target:'#transferAccountOwnerIdType${status.index}',value:'01'}}" />
						</td>
					</tr>
				</tbody>
			</table>
			</div>
		</c:forEach></div>
		</div>
		</div>
		<div id="errorMsg" style="color: red; text-align: left;"><c:if
			test="${not empty errorMsg}">
			${errorMsg}<br />
		</c:if> <sfform:errors path="*"></sfform:errors></div>
		<table width="100%">
			<tr>
				<td style="text-align: right;"><a class="easyui-linkbutton"
					href="javascript:void(0)" iconCls="icon-search"
					onclick="window.open('${ctx}pub/posGQ','apGQ','resizable=yes,height=700px,width=1000px');">综合查询</a>
				&nbsp; <a class="easyui-linkbutton" href="javascript:void(0)"
					iconCls="icon-back" id="back">返回</a> &nbsp; <a
					class="easyui-linkbutton cancel" href="javascript:void(0)"
					iconCls="icon-add" id="add">新增</a> <a
					class="easyui-linkbutton cancel" href="javascript:void(0)"
					iconCls="icon-remove" id="remove">删除</a> &nbsp; <a
					class="easyui-linkbutton" href="javascript:void(0)"
					iconCls="icon-ok" id="ok">确定</a></td>
			</tr>
		</table>
		<!-- 不良记录明细DIV -->
		<div id="badBehaviorWindowDiv" class="easyui-window" closed="true"
			modal="true" title="不良记录明细" style="width: 700px; height: 300px;"
			collapsible="false" minimizable="false" maximizable="false">
		<table width="100%" id="badBehavior" class="easyui-datagrid"></table>
		</div>
		<!-- 不良记录明细DIV -->
	</sfform:form>
	<div style="display: none;">
	<div id="autographImageDiv" class="easyui-window" closed="true"
		model="true" title="签名影像查询" style="width: 400px; height: 300px;"
		collapsible="false" minimizable="false" maximizable="true"></div>
	</div>
	<jsp:include
		page="/WEB-INF/views/include/productServiceItemsSelectWindow.jsp" />
	<jsp:include page="/WEB-INF/views/include/bankSelectWindow.jsp" />
	<jsp:include
		page="/WEB-INF/views/include/applyMaterialSelectWindow.jsp" />
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
