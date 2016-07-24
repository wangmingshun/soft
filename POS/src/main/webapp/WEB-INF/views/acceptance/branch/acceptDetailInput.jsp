<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">
	<c:choose>
		<c:when test="${empty TRIAL_FLAG}">机构受理&gt;&gt;逐单受理&gt;&gt;项目经办信息录入</c:when>
		<c:otherwise>
			<c:if test="${empty TRIAL_SOURCE}">		  
		      机构受理&gt;&gt;试算&gt;&gt;项目经办信息录入	
		 </c:if>
		</c:otherwise>
	</c:choose>
</sf:override>
<sf:override name="head">
	<script type="text/javascript" language="javascript">
	var posValidator;
	var initPage;//覆盖这个初始化是为了确保让代码在specialFuncSelected的trigger事件之前执行
	var ignorePersonalNotice = false;//是否忽略未录入个人资料告知
	var serviceItems = "${acceptance__item_input.serviceItems}";
	var UW_SERVICE_ITEMS_ARRAY = [ "3", "5", "6", "12", "14", "15", "16", "20",
			"35" ];
	var UW_PERSONAL_NOTICE_REQUIRED_ITEM_ARRAY = [ "3", "5", "6" ];
	$(function() {
		$("[name='callConfirm']").click(
				function() {
					if (this.checked == true) {
						$(this).val("Y");
						$("[name='remark']").val(
								$("[name='remark']").val() + "#电话回访确认件#");
					} else {
						if ($("#attechmentFileId").val()) {
							$.messager.alert("提示",
									"取消电话回访确认件选项，附件将不会被保存，请先删除附件。");
							return false;
						}
						$(this).val("N");
						$("[name='remark']").val(
								$("[name='remark']").val().replace("#电话回访确认件#",
										""));
					}
					return true;
				});

		$("#itemsInputFormSubmitBt")
				.click(
						function() {
							if ("${TRIAL_FLAG}" == "") {
								if (UW_PERSONAL_NOTICE_REQUIRED_ITEM_ARRAY
										.indexOf(serviceItems) > -1) {		
									
									var mainProductCode="${acceptance__item_input.mainProductCode}";
									if ("CEAN_AN1"!=mainProductCode)
										{
											var appInsTheSame = "${acceptance__item_input.appInsTheSame}";
											if((appInsTheSame == "false") || (serviceItems == '20')){
												if ($("#personalNoticeClearApp:visible").length == 0) {
													$.messager
															.confirm(
																	"提示",
																	"该申请项目必须录入《最新个人资料告知书》，是否现在录入？",
																	function(yes) {
																		if (yes) {
																			$(
																					"#personalNoticeApp:visible")
																					.trigger(
																							"click");
																		}
																	});
													return false;
												}
											}
									var clientInsListlength = '${fn:length(acceptance__item_input.clientNoInsList)}';
									for(var i=1;i<=clientInsListlength;i++){
											var	id ="personalNoticeIns"+i;
											var sign = $("#"+id).val();
											if(sign == "N"){
												$.messager
														.confirm(
																"提示",
																"该申请项目必须每个被保险人都录入《最新个人资料告知书》，是否现在录入？",
																function(yes) {
																	if (yes) {
																	  savepersonalNoticeIns(i);
																	}
																});
												return false;
											}
									}
										
								}
								} else if (UW_SERVICE_ITEMS_ARRAY
										.indexOf(serviceItems) > -1) {
									if (!ignorePersonalNotice) {
										
										  var appInsTheSame = "${acceptance__item_input.appInsTheSame}";
											if((appInsTheSame == "false") || (serviceItems == '20')){
												if ($("#personalNoticeClearApp:visible").length == 0) {
													$.messager
															.confirm(
																	"提示",
																	"该申请项目必须录入《最新个人资料告知书》，是否现在录入？",
																	function(yes) {
																		if (yes) {
																			$(
																					"#personalNoticeApp:visible")
																					.trigger(
																							"click");
																		}else {
																			ignorePersonalNotice = true;
																			$(
																					"#itemsInputFormSubmitBt")
																					.trigger(
																							"click");
																		}
																	});
													return false;
												}
											}
										var clientInsListlength = '${fn:length(acceptance__item_input.clientNoInsList)}';
										for(var i=1;i<=clientInsListlength;i++){
												var	id ="personalNoticeIns"+i;
												var sign = $("#"+id).val();
												if(sign == "N"){
													$.messager
															.confirm(
																	"提示",
																	"该申请项目必须每个被保险人都录入《最新个人资料告知书》，是否现在录入？",
																	function(yes) {
																		if (yes) {
																			savepersonalNoticeIns(i);
																		} else {
																			ignorePersonalNotice = true;
																			$(
																					"#itemsInputFormSubmitBt")
																					.trigger(
																							"click");
																		}
																	});
													return false;
												}
										}
									}
								}
							}
							if ($("#specialRuleType").val() == "99"
									&& $("#otherSpecialRuleType").val() == "") {
								$.messager.alert("失败", "请录入规则特殊件其它类型描述");
								return false;
							}
							if ($("#specialRuleReason").val() == "99"
									&& $("#otherSpecialRuleReason").val() == "") {
								$.messager.alert("失败", "请录入规则特殊件其它原因描述");
								return false;
							}
							/*协议退费的校验 edit by gaojiaming start*/
							if(serviceItems=='2'&&$("#specialFuncSelected").attr("checked")==true&&$("#specialFunc").val()=='7'){
								var sumBackBenefit=0;
								var premPaid=$("#premPaid").val();
								var $productCb = $(".productCb");
								$productCb.each(function(index){	
									if($(this).is(":checked")==true){
										sumBackBenefit=FloatAdd01(sumBackBenefit,$('#agreePremSum'+index).val());					    	 
								     }									
								});	
								if(parseFloat(sumBackBenefit)!=parseFloat(premPaid)){
									$.messager.confirm("提示","协议退费金额"+sumBackBenefit+"元，不等于保单累积已交的保费"+premPaid+"元，是否继续？",function(yes) {
										if (yes) {
											$("form[name='itemsInputForm']").submit();
											return false;
										} else {
											return false;											
										}
									});
									return false;
								}
							}
							/*协议退费的校验 edit by gaojiaming end*/
							$("form[name='itemsInputForm']").submit();
							return false;
						});
		//浮点数加法运算 
		function FloatAdd(arg1,arg2){ 
			/*var r1,r2,m; 
			try{r1=arg1.toString().split(".")[1].length;}catch(e){r1=0;} 
			try{r2=arg2.toString().split(".")[1].length;}catch(e){r2=0;} 
			m=Math.pow(10,Math.max(r1,r2)); 
			return (arg1*m+arg2*m)/m; */

		       var r1, r2, m, c;
		       try {
		          r1 = arg1.toString().split(".")[1].length;
		       }
		       catch (e) {
		          r1 = 0;
		       }
		       try {
		          r2 = arg2.toString().split(".")[1].length;
		       }
		       catch (e) {
		          r2 = 0;
		       }
		       c = Math.abs(r1 - r2);
		       m = Math.pow(10, Math.max(r1, r2));
		       if (c > 0) {
		          var cm = Math.pow(10, c);
		          if (r1 > r2) {
		             arg1 = Number(arg1.toString().replace(".", ""));
		             arg2 = Number(arg2.toString().replace(".", "")) * cm;
		          } else {
		             arg1 = Number(arg1.toString().replace(".", "")) * cm;
		             arg2 = Number(arg2.toString().replace(".", ""));
		          }
		       } else {
		          arg1 = Number(arg1.toString().replace(".", ""));
		          arg2 = Number(arg2.toString().replace(".", ""));  
		       }
		       return (arg1 + arg2) / m;
		} 
		
		//使用此方法计算不会出现精度丢失问题 ,到时候测试下，之前的代码我保留。我自己测试没问题。
		//edit by  wangmingshun
		function FloatAdd01(arg1, arg2) {
			var r1, r2, m, c;
			try {
				r1 = arg1.toString().split(".")[1].length;
			}
			catch (e) {
				r1 = 0;
			}
			try {
				r2 = arg2.toString().split(".")[1].length;
			}
			catch (e) {
				r2 = 0;
			}
			c = Math.abs(r1 - r2);
			m = Math.pow(10, Math.max(r1, r2));
			if (c > 0) {
				var cm = Math.pow(10, c);
				if (r1 > r2) {
					arg1 = Number(arg1.toString().replace(".", ""));
					arg2 = Number(arg2.toString().replace(".", "")) * cm;
				} else {
					arg1 = Number(arg1.toString().replace(".", "")) * cm;
					arg2 = Number(arg2.toString().replace(".", ""));
				}
			} else {
				arg1 = Number(arg1.toString().replace(".", ""));
				arg2 = Number(arg2.toString().replace(".", ""));	
			}
			return (arg1 + arg2) / m;
		}
		
		$('#bindingOrderQueryBtn')
				.click(
						function() {
							$("#bindingOrderQueryDiv").window("open");
							$("#bindingOrderQueryGrid")
									.datagrid(
											{
												url : "${ctx}acceptance/branch/bindingOrderQuery?posBatchNo=${acceptance__item_input.batchNo}",
												columns : [ [ {
													field : 'acceptSeq',
													title : '捆绑顺序',
													width : 60
												}, {
													field : 'barcodeNo',
													title : '条码号',
													width : 100
												}, {
													field : 'policyNo',
													title : '保单号',
													width : 120
												}, {
													field : 'serviceItemsDesc',
													title : '保全项目',
													width : 120
												}, {
													field : 'acceptStatusDesc',
													title : '受理状态',
													width : 100
												} ] ]
											});
							return false;
						});

		$("#cancelServieBt")
				.click(
						function() {
							$.messager
									.confirm(
											"确认撤消",
											"是否确认撤销该受理？",
											function(yes) {
												if (yes) {
													$
															.post(
																	"${ctx}acceptance/branch/cancelService.do",
																	{
																		posNo : "${acceptance__item_input.posNo}",
																		cancelCause : $(
																				"#cancelCause")
																				.val(),
																		cancelRemark : $(
																				"#cancelRemark")
																				.val()
																	},
																	function(
																			data) {
																		if (data.flag == "Y") {
																			$.messager
																					.alert(
																							"提示",
																							"受理撤销成功",
																							"info",
																							function() {
																								if (data.hasNext) {
																									window.location.href = "${ctx}acceptance/branch/acceptDetailInput/"
																											+ data.posBatchNo;
																								} else {
																									window.location.href = "${ctx}acceptance/branch/entry"
																								}
																							});
																		} else {
																			$.messager
																					.alert(
																							"失败",
																							data.message);
																		}
																	}, "json");
												}
											});
							return false;
						});

		$("#specialFuncSelected").change(function(event) {
			$("#specialRetreatReason").find("option").remove();
			if (this.checked) {
				$("#specialFunc").attr("disabled", false).trigger("change");
			} else {
				$("#specialFunc").attr("disabled", true);
				$("#specialRetreatReasonLabel").attr("style", "display:none");
				$("#specialRetreatReason").find("option").remove();

			}
		});

		$("#specialRuleSelected").change(
				function(event) {

					if (this.checked) {
						$("#specialRuleType").attr("disabled", false).trigger(
								"change");
						$("#specialRuleReasonDiv").attr("style",
								"display:inline");
						$("#specialRuleReason").attr("disabled", false)
								.trigger("change");

					} else {
						$("#specialRuleType").attr("disabled", true);
						$("#specialRuleReasonDiv")
								.attr("style", "display:none");
						$("#otherSpecialRuleTypeDiv").attr("style",
								"display:none");
						$("#otherSpecialRuleReasonDiv").attr("style",
								"display:none");
					}
				});

		$("#specialRuleType").change(function(event) {
			if ($("#specialRuleType").val() == "99") {
				$("#otherSpecialRuleTypeDiv").attr("style", "display:inline");
			} else {
				$("#otherSpecialRuleTypeDiv").attr("style", "display:none");
			}
		});

		$("#specialRuleReason").change(
				function(event) {
					if ($("#specialRuleReason").val() == "99") {
						$("#otherSpecialRuleReasonDiv").attr("style",
								"display:inline");
					} else {
						$("#otherSpecialRuleReasonDiv").attr("style",
								"display:none");
					}
				});

		$("#specialFunc")
				.change(
						function() {
							var userSpecPrivArr = "${acceptance__item_input.funcSpecPriv}"
									.split(",");
							var selectedSpecFunc = $(this).val();
							if (userSpecPrivArr && userSpecPrivArr.length > 0) {
								//如果用户有权限操作特殊件，还要判断当前选中的特殊件类型是否在用户的权限列表中
								var valToSet = null;//可选的第一个特殊件类型，找到就说明有权限
								var specFuncOptions = $("#specialFunc option");
								specFuncOptions.each(function(idx, value) {
									if (userSpecPrivArr.indexOf($(
											specFuncOptions[idx]).val()) >= 0
											&& !valToSet) {
										valToSet = $(specFuncOptions[idx])
												.val();
									}
								});
								if (valToSet == null) {
									$.messager.alert("提示", "您没有操作功能特殊件的权限",
											"info");
									$("#specialFuncSelected").attr("checked",
											false);
									$("#specialFunc").attr("disabled", true);
								} else if (selectedSpecFunc) {
									if (userSpecPrivArr
											.indexOf(selectedSpecFunc) < 0) {
										$.messager.alert("提示",
												"您没有操作该功能特殊件的权限", "info");
										$("#specialFunc").val(valToSet);
									}
								} else {
									$("#specialFunc").val(valToSet);
								}
							} else {
								//如果用户没有任何特殊件的权限
								$.messager.alert("提示", "您没有操作功能特殊件的权限", "info");
								$("#specialFuncSelected")
										.attr("checked", false);
								$("#specialFunc").attr("disabled", true);
							}
							if (selectedSpecFunc == "9") {
								$(".branchPercent").show();
							} else {
								$(".branchPercent").hide();
							}

							var $specialRetreatReason = $("#specialRetreatReason");

							$("#specialRetreatReason").find("option").remove();

							if ($("#specialFuncSelected").is(":checked") == true
									&& (selectedSpecFunc == '2'
											|| selectedSpecFunc == '8' || selectedSpecFunc == '9')) {
								$("#specialRetreatReasonLabel").attr("style",
										"");
								$("#specialRetreatReason").find("option")
										.remove();
								$
										.post(
												"${ctx}acceptance/branch/querySpecialRetreatReason.do",
												{
													"specialFunc" : selectedSpecFunc
												},
												function(data) {
													$("#specialRetreatReason")
															.find("option")
															.remove();
													for ( var i = 0; data
															&& data.length
															&& i < data.length; i++) {

														$("<option/>")
																.attr(
																		"value",
																		data[i].code)
																.html(
																		data[i].description)
																.appendTo(
																		$specialRetreatReason);
													}
												}, "json");

							} else {
								$("#specialRetreatReason").find("option")
										.remove();
								$("#specialRetreatReasonLabel").attr("style",
										"display:none");

							}

						});

		$("#personalNoticeApp")
				.click(
						function(event) {
							var ret = window
									.showModalDialog(
											"${ctx}acceptance/branch/personalNotice?target=app&insuredSeq=998",
											null,
											"dialogHeight=550px;dialogWidth=830px;center=yes;help=no;resizable=yes;status=yes;");
							if (ret == "SAVE_SUCCESS") {
								$("#personalNoticeClearApp").show();
								$(this).html("修改");
							}
							return false;
						});

		/*$("#personalNoticeIns")
				.click(
						function(event) {
							var ret = window
									.showModalDialog(
											"${ctx}acceptance/branch/personalNotice?target=ins",
											null,
											"dialogHeight=550px;dialogWidth=780px;center=yes;help=no;resizable=yes;status=yes;");
							if (ret == "SAVE_SUCCESS") {
								$("#personalNoticeClearIns").show();
								$(this).html("修改");
							}
							return false;
						});*/

		$("#personalNoticeClearApp")
				.click(
						function() {
							$
									.get(
											"${ctx}acceptance/branch/personalNotice?clear=true&target=app&insuredSeq=998",
											function(data) {
												if (data.flag == "Y") {
													$("#personalNoticeApp")
															.html("录入");
													$("#personalNoticeClearApp")
															.hide();
												}
											});
							return false;
						});

		$("#personalNoticeClearIns")
				.click(
						function() {
							$
									.get(
											"${ctx}acceptance/branch/personalNotice?clear=true&target=ins&insuredSeq=999",
											function(data) {
												if (data.flag == "Y") {
													var clientInsListlength = '${fn:length(acceptance__item_input.clientNoInsList)}';
													for(var i=1;i<=clientInsListlength;i++){
															var	id ="personalNoticeIns"+i;
															$("#"+id).val("N");
													}
													$("#personalNoticeClearIns")
															.hide();
												}
											});
							return false;
						});

		$("#showSignImageBt")
				.click(
						function() {
							var autographImageURL = "${acceptance__item_input.autographImageURL}";
							if (!autographImageURL) {
								$.messager.alert("提示", "没有签名影像信息！");
							} else {
								var imgURL;
								if (autographImageURL.indexOf("?") > -1) {
									imgURL = autographImageURL + "&";
								} else {
									imgURL = autographImageURL + "?";
								}
								imgURL += "_timestamp=" + new Date().getTime();
								$("#autographImageDiv").find("img").attr("src",
										imgURL).end().window("open");
							}
							return false;
						});

		$("#attachmentLink").click(
				function() {
					if ($("#callConfirm").is(":not(:checked)")) {
						$.messager.alert("提示", "必须是电话回访确认件才可以上传附件!");
						return false;
					}
					var $this = $(this);
					if ($this.html() == "删除") {
						$("#loading").show();
						$this.hide();
						$.post("${ctx}acceptance/branch/clearAttechment.do",
								null, function(data) {
									if (data && data.flag == "Y") {
										$this.html("上传");
										$("#fileName").html("");
										$("#attechmentFileId").val("");
										$("#attechmentFileName").val("");
										$("#fileToUpload").val("").show();
										$.messager.alert("提示", "删除附件成功!");
									} else {
										$.messager.alert("提示", "删除附件失败!");
									}
									$("#loading").hide();
									$this.show();
								});
					} else {
						if (!$("#fileToUpload").val()) {
							$.messager.alert("提示", "请选择要上传的文件!");
							return false;
						}
						$("#loading").show();
						$this.hide();
						$
								.ajaxFileUpload({
									url : '${ctx}include/uploadFile.do',
									secureuri : false,
									fileElementId : "fileToUpload",
									dataType : "json",
									data : {
										filePath : "acceptance/recording"
									},
									success : function(data, status) {
										$("#loading").hide();
										$this.show();
										if (data.errorMessage) {
											$.messager
													.alert("失败", errorMessage);
										} else {
											$("#attechmentFileId").val(
													data.fileId);
											$("#attechmentFileName").val(
													data.fileName);
											$("#fileName").html(data.fileName);
											$this.html("删除");
											$("#fileToUpload").hide();
										}
									},
									error : function(data, status, e) {
										$.messager.alert("失败", e);
										$("#loading").hide();
										$this.show();
									}
								});
					}
					return false;
				});

		$("[name='clientSelection']").change(
				function() {
					$("<input/>").attr("name", "action_updateClientNo").attr(
							"type", "hidden").val("X").appendTo(
							$("form[name=itemsInputForm]"));
					$("form[name=itemsInputForm]")[0].submit();
				});

		posValidator = $("form[name=itemsInputForm]").validate({
			ignore : ".ignore :input,.ignore"
		});

		if (initPage) {
			initPage();
		}

		$("#specialFuncSelected").trigger("change");
		if ($("#attechmentFileId").val()) {
			$("#attachmentLink").html("删除");
			$("#fileToUpload").hide();
		} else {
			$("#attachmentLink").html("上传");
			$("#fileToUpload").show();
		}

		var remindMessage = "${empty acceptance__item_input.remindMessage ? '' : acceptance__item_input.remindMessage}";
		if (remindMessage) {
			$.messager.alert("提示", remindMessage, "info");
		}
	});
	function savepersonalNoticeIns(insuredSeq){
		var url = "${ctx}acceptance/branch/personalNotice?target=ins&insuredSeq="+insuredSeq;
		var ret = window.showModalDialog(
				url,
				null,
				"dialogHeight=550px;dialogWidth=830px;center=yes;help=no;resizable=yes;status=yes;");
		if (ret == "SAVE_SUCCESS") {
			var id = "personalNoticeIns"+insuredSeq;
			$("#"+id).val("Y");
			$("#personalNoticeClearIns").show();
		}
		return false;
	}
	

</script>
	<sf:block name="headBlock" />
	<style type="text/css">
#buttonBlock {
	display: inline;
}
</style>
</sf:override>

<sf:override name="content">
	<sfform:form name="itemsInputForm" commandName="acceptance__item_input">
		<c:if test="${empty TRIAL_FLAG}">
			<div class="easyui-panel">&nbsp;共『<strong>${acceptance__item_input.acceptCount}</strong>』个受理，当前受理第
			『<strong>${acceptance__item_input.acceptSeq}</strong>』个 ：保单<strong>${acceptance__item_input.policyNo}</strong>
			<strong>${acceptance__item_input.serviceItemsDesc}</strong> 项目。 <a
				id="bindingOrderQueryBtn" href="javascript:void(0)">捆绑查询</a></div>
			<div class="layout_div_top_spance">
			<div class="easyui-panel" title="公共信息录入">
			<table class="layouttable">
				<tbody>
					<tr>
						<td class="layouttable_td_label">申请类型:</td>
						<td class="layouttable_td_widget"><c:forEach
							items="${CodeTable.posTableMap.POS_APPLY_TYPE_CODE}" var="item">
							<c:if test="${acceptance__item_input.applyType eq item.code}">${item.description}</c:if>
						</c:forEach></td>
						<td class="layouttable_td_label">受理渠道:</td>
						<td class="layouttable_td_widget"><c:forEach
							items="${CodeTable.posTableMap.POS_ACCEPT_CHANNEL_CODE}"
							var="item">
							<c:if test="${acceptance__item_input.acceptChannel eq item.code}">${item.description}</c:if>
						</c:forEach></td>
						<td class="layouttable_td_label">申请日期:</td>
						<td class="layouttable_td_widget">${acceptance__item_input.applyDate}</td>
					</tr>
					<c:if
						test="${acceptance__item_input.applyType eq '2' or acceptance__item_input.applyType eq '3' or acceptance__item_input.applyType eq '4'}">
						<tr>
							<td class="layouttable_td_label">代办人姓名:</td>
							<td class="layouttable_td_widget">${acceptance__item_input.representor}</td>
							<td class="layouttable_td_label">代办人证件类型:</td>
							<td class="layouttable_td_widget"><c:forEach
								items="${CodeTable.posTableMap.ID_TYPE}" var="item">
								<c:if test="${acceptance__item_input.idType eq item.code}">${item.description}</c:if>
							</c:forEach></td>
							<td class="layouttable_td_label">代办人证件号码:</td>
							<td class="layouttable_td_widget">${acceptance__item_input.representIdNo}</td>
						</tr>
					</c:if>
					<tr>
						<td class="layouttable_td_label">条形码:</td>
						<td class="layouttable_td_widget">${acceptance__item_input.barcodeNo}</td>
						<td class="layouttable_td_label">关联条形码:</td>
						<td class="layouttable_td_widget"><c:choose>
							<c:when test="${empty acceptance__item_input.relateBarcode}">无</c:when>
							<c:otherwise>${acceptance__item_input.relateBarcode}</c:otherwise>
						</c:choose></td>
						<td class="layouttable_td_label">资料页数:</td>
						<td class="layouttable_td_widget">${acceptance__item_input.filePages}</td>
					</tr>
					<tr>
						<td class="layouttable_td_label">签名不符件:</td>
						<td class="layouttable_td_widget"><sfform:checkbox
							path="signAccordFlag" value="Y" /> <a id="showSignImageBt"
							href="javascript:void(0);">查看签名影像</a></td>
						<td class="layouttable_td_label">规则特殊件:</td>
						<td class="layouttable_td_widget"><sfform:checkbox
							path="specialRule" id="specialRuleSelected" value="Y" /> <sfform:select
							id="specialRuleType" path="specialRuleType" disabled="true"
							items="${acceptance__item_input.specialRuleList}"
							itemLabel="description" itemValue="code" cssClass="input_select" />

						<div id="otherSpecialRuleTypeDiv" style="display: none;"><br></br>
						其它描述 <sfform:textarea path="otherSpecialRuleType"
							cssClass="multi_text {byteRangeLength:[0,200]}" cols="20"
							rows="1"></sfform:textarea></div>

						<div id="specialRuleReasonDiv" style="display: none;"><br></br>
						规则特殊件原因: <sfform:select id="specialRuleReason"
							path="specialRuleReason" disabled="true"
							items="${acceptance__item_input.specialRuleReasonList}"
							itemLabel="description" itemValue="code" cssClass="input_select" />

						<div id="otherSpecialRuleReasonDiv" style="display: none;">
						<br></br>
						其它描述 <sfform:textarea path="otherSpecialRuleReason"
							cssClass="multi_text {byteRangeLength:[0,200]}" cols="20"
							rows="1"></sfform:textarea></div>

						</div>
						</td>
						<td class="layouttable_td_label">功能特殊件:</td>
						<td class="layouttable_td_widget"><c:choose>
							<c:when test="${acceptance__item_input.specialFuncEnabled}">
								<sfform:checkbox id="specialFuncSelected"
									path="specialFuncSelected" />
								<sfform:select id="specialFunc" path="specialFunc"
									disabled="true"
									items="${acceptance__item_input.specialFuncList}"
									itemLabel="description" itemValue="code"
									cssClass="input_select" />
							</c:when>
							<c:otherwise>无功能特殊件</c:otherwise>
						</c:choose></td>
					</tr>
					<tr>
						<td class="layouttable_td_label">保单已补发次数:</td>
						<td class="layouttable_td_widget"><c:choose>
							<c:when
								test="${acceptance__item_input.policyProvideTimeEditable}">
								<input type="hidden"
									value="${acceptance__item_input.policyProvideTime}"
									id="policyProvideTime" />
								<input type="text"
									class="input_text {required:true,number:true,equalTo:'#policyProvideTime',messages:{equalTo:'保单补发次数不正确'}}" />
							</c:when>
							<c:otherwise>${acceptance__item_input.policyProvideTime}</c:otherwise>
						</c:choose></td>
						<td class="layouttable_td_label">电话回访确认件:</td>
						<td class="layouttable_td_widget"><input type="checkbox"
							name="callConfirm" id="callConfirm" value="N" /></td>
						<td class="layouttable_td_label">最新个人资料告知:</td>
						<td class="layouttable_td_widget">
						<c:if test="${not acceptance__item_input.appInsTheSame or acceptance__item_input.appInsTheSame and acceptance__item_input.serviceItems eq '20'}">
									投保人
									<a id="personalNoticeApp" href="javascript:void(0);"><c:choose>
								<c:when test="${empty acceptance__item_input.appPersonalNotice}">录入</c:when>
								<c:otherwise>修改</c:otherwise>
							</c:choose></a>
							<a id="personalNoticeClearApp" href="javascript:void(0);"
								style="<c:if test="${empty acceptance__item_input.appPersonalNotice}">display:none</c:if>">删除</a>
							<br />

						</c:if> 
						<c:if test="${not acceptance__item_input.appInsTheSame or acceptance__item_input.appInsTheSame and acceptance__item_input.serviceItems ne '20'}">
									被保人:
								<c:forEach var="clientNoInsList" items="${acceptance__item_input.clientNoInsList}" varStatus="status">	
								<a  id="personalNoticeInsP${clientNoInsList.insuredSeq}" href="javascript:void(0)" onclick="savepersonalNoticeIns('${clientNoInsList.insuredSeq}');">
									${clientNoInsList.insuredName}
								</a>
								<input type="hidden" value="N" id="personalNoticeIns${clientNoInsList.insuredSeq}" />
								&nbsp;
								</c:forEach>
								<a id="personalNoticeClearIns" href="javascript:void(0);" style="<c:if test="${empty acceptance__item_input.insPersonalNoticeList}">display:none</c:if>">删除</a>
						</c:if></td>


					</tr>
					<tr>
						<td class="layouttable_td_label">附件:</td>
						<td class="layouttable_td_widget"><sfform:hidden
							path="attechmentFileId" id="attechmentFileId" /> <sfform:hidden
							path="attechmentFileName" id="attechmentFileName" /> <input
							id="fileToUpload" type="file" size="20" name="fileToUpload" /> <span
							id="fileName">${acceptance__item_input.attechmentFileName}</span>
						<img id="loading"
							src="${ctx}CacheableResource/javascript_v1_01/ajax_file_uploader_v2_1/loading.gif"
							style="display: none; width: 16px; height: 16px;" /> <a
							href="javascript:void(0)" id="attachmentLink"></a></td>
						<td class="layouttable_td_label">业务员自保件:</td>
						<td class="layouttable_td_widget">
						${acceptance__item_input.selfInsured ? '是' : '否'}</td>
						<td class="layouttable_td_label">送审备注:</td>
						<td class="layouttable_td_widget"><sfform:textarea
							path="remark" cssClass="multi_text {byteRangeLength:[0,2000]}"
							cols="20" rows="1" /></td>
					</tr>
				</tbody>
			</table>
			</div>
			</div>
		</c:if>
		<div class="layout_div_top_spance">
		<div class="easyui-panel" title="项目信息录入">
				<c:if
					test="${acceptance__item_input.serviceItems eq '21' and acceptance__item_input.clientNoIns ne acceptance__item_input.clientNoApp  and acceptance__item_input.clientNoIns eq acceptance__item_input.acceptClientNo}">			 
					请选择变更对象：&nbsp;&nbsp;
					被保人:
					<c:forEach var="clientNoInsList" items="${acceptance__item_input.clientNoInsList}" varStatus="status">	
								<input type="radio" value="I,${clientNoInsList.insuredNo}" name="clientSelection"
									<c:if test="${clientNoInsList.insuredNo eq acceptance__item_input.clientNo}">checked="checked"</c:if> />${clientNoInsList.insuredName}
					</c:forEach>
					
				</c:if>
				<c:if
					test="${acceptance__item_input.serviceItems eq '21' and acceptance__item_input.clientNoIns ne acceptance__item_input.clientNoApp  and acceptance__item_input.clientNoIns ne acceptance__item_input.acceptClientNo}">			 
					请选择变更对象：&nbsp;&nbsp;
					<input type="radio" value="A" name="clientSelection"
						<c:if test="${acceptance__item_input.clientNoApp eq acceptance__item_input.clientNo}">checked="checked"</c:if> />投保人
					被保人:
					<c:forEach var="clientNoInsList" items="${acceptance__item_input.clientNoInsList}" varStatus="status">	
								<input type="radio" value="I,${clientNoInsList.insuredNo}" name="clientSelection"
									<c:if test="${clientNoInsList.insuredNo eq acceptance__item_input.clientNo}">checked="checked"</c:if> />${clientNoInsList.insuredName}
					</c:forEach>
					
				</c:if>
				<c:if
					test="${acceptance__item_input.serviceItems ne '21' and acceptance__item_input.clientSelectEnabled and not acceptance__item_input.appInsTheSame}">			 
					请选择变更对象：&nbsp;&nbsp;
					<input type="radio" value="A" name="clientSelection"
						<c:if test="${acceptance__item_input.clientNoApp eq acceptance__item_input.clientNo}">checked="checked"</c:if> />投保人
					被保人:
					<c:forEach var="clientNoInsList" items="${acceptance__item_input.clientNoInsList}" varStatus="status">	
								<input type="radio" value="I,${clientNoInsList.insuredNo}" name="clientSelection"
									<c:if test="${clientNoInsList.insuredNo eq acceptance__item_input.clientNo}">checked="checked"</c:if> />${clientNoInsList.insuredName}
					</c:forEach>
					
				</c:if>
				<c:if
					test="${acceptance__item_input.clientSelectEnabled and acceptance__item_input.appInsTheSame }">			 
					请选择变更对象：&nbsp;&nbsp;
					被保人:
					<c:forEach var="clientNoInsList" items="${acceptance__item_input.clientNoInsList}" varStatus="status">	
								<input type="radio" value="I,${clientNoInsList.insuredNo}" name="clientSelection"
									<c:if test="${clientNoInsList.insuredNo eq acceptance__item_input.clientNo}">checked="checked"</c:if> />${clientNoInsList.insuredName}
					</c:forEach>
					
				</c:if>
		 <sf:block name="serviceItemsInput" /></div>
		</div>


		<div style="font-size: 16px; color: red; text-align: left;"><c:if
			test="${not empty VALIDATE_MSG }">
          ${VALIDATE_MSG}
          </c:if> <sfform:errors path="*"></sfform:errors></div>

		<br />

		<div align="right"><c:if test="${empty TRIAL_SOURCE}">
			<a class="easyui-linkbutton" href="javascript:void(0)"
				iconCls="icon-search"
				onclick="window.open('${ctx}pub/posGQ','apGQ','resizable=yes,height=700px,width=1000px');">综合查询</a>
		</c:if> <c:if test="${not empty TRIAL_SOURCE}">
			<a class="easyui-linkbutton" href="javascript:void(0)"
				onclick="javascript:window.external.FromExit()">关闭</a>
		</c:if> <sf:block name="buttonBlock" /> <c:if test="${empty TRIAL_FLAG}">
			<a class="easyui-linkbutton" href="javascript:void(0)"
				iconCls="icon-cancel" id="cancelServieBt">受理撤销</a>
				撤消原因：
				<select name="cancelCause" id="cancelCause">
				<c:forEach items="${CodeTable.posTableMap.POS_ROLLBACK_CAUSE_CODE}"
					var="item">
					<option value="${item.code}">${item.description}</option>
				</c:forEach>
			</select>
				撤消备注：
				<input type="text" name="cancelRemark" id="cancelRemark"
				class="{byteRangeLength:[0,2000]}" />
		</c:if> &nbsp;&nbsp;&nbsp;&nbsp; <a class="easyui-linkbutton"
			href="javascript:void(0)" iconCls="icon-ok" 
			id="itemsInputFormSubmitBt">确定</a></div>
	</sfform:form>

	<div style="display: none;">
	<div id="bindingOrderQueryDiv" class="easyui-window" closed="true"
		modal="true" title="捆绑顺序查询" style="width: 525px; height: 300px;"
		collapsible="false" minimizable="false" maximizable="true">
	<div id="bindingOrderQueryGrid" />
	</div>
	<div id="autographImageDiv" class="easyui-window" closed="true"
		model="true" title="签名影像查询" style="width: 400px; height: 300px;"
		collapsible="false" minimizable="false" maximizable="true"><img
		alt="签名影像信息" /></div>
	</div>

</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>
