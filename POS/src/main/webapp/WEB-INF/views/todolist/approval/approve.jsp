<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<%@page import="com.sinolife.pos.common.util.PosUtils ;"%>
<sf:override name="pathString">审批&gt;&gt;审批操作</sf:override>
<sf:override name="head">
<script type="text/javascript">
  $(function(){ 
		/* 新增不良记录JS start*/
		$("#insertBadBehaviorBt").click(function(){
			$("#insertBadBehaviorWindowDiv").window("open");
		});	
		$("#save").click(function(){
	    	var objectType = $("#objectType").val();
	    	var objectNo = $("#objectNo").val();
	    	var objectName = $("#objectName").val();
	    	var policyNo = $("#policyNo").val();
	    	var behaviorType = $("#behaviorType").val();
	    	var descrpiton = $("#descrpiton").val();	
			if(objectNo==''||objectName==''||policyNo==''||descrpiton==''){
				$.messager.alert("提示", "请录入*号必录项！");
				return;			
			}
			$.messager.confirm('确认保存', '您确认要保存此不良记录吗？', function(r){
				if (r){
					$.ajax({type: "GET",
						url:"${ctx}approve/insertBadBehavior.do?objectNo="+objectNo+"&objectName="+encodeURI(objectName)+
								"&policyNo="+policyNo+"&behaviorType="+behaviorType+"&descrpiton="+encodeURI(descrpiton),
						data:{"objectType":objectType},
						cache: false,
						async: false,
						dataType:"text",
						success:function(data){
							if(data=="Y"){
						        $.messager.alert("提示", "保存成功！", "info", function () {
						        	$("#insertBadBehaviorWindowDiv").window("close");
						        	/*$("#objectType").val("1");
						        	$("#objectNo").val("");
						        	$("#objectName").val("");
						        	$("#policyNo").val("");*/
						        	$("#behaviorType").val("1");
						        	$("#descrpiton").val("");
									return false;						
						        });	
								return false;						
							}else if(data=="E"){
								$.messager.alert("提示", "是否需要送审判断出错！");													
							}else{
								$.messager.alert("提示", "保存失败！");													
							}
						}
					});
				}
			});
			return false;						
		});	
		$("#objectNo").blur(function(){
			var objectNo=$("#objectNo").val();
			var objectType=$("#objectType").val();
			$.ajax({type: "GET",
				url:"${ctx}approve/getObjectInfoByObjectNo.do?needCheckPrivs=N&objectType="+objectType,
				data:{"objectNo" :objectNo},
				cache: false,
				async: false,
				dataType:"json",
				success:function(data){
					if(data.objectName==""||data.objectName==null){
						$.messager.alert("提示", "获取对象信息失败,请核查对象号码！");	
						$("#objectName").val("");	
						return false;
					}else{
						$("#objectName").val(data.objectName);													
					}
				}
			});		
		});	
		$("#objectType").change(function(){
			$("#objectNo").val("");
			$("#objectName").val("");
		});		
		/* 新增不良记录JS end*/	  
	  $("input[name='approveDecision']").click(function(){
		  if($(this).val()=="3"){
			  $("#anotherApproverSp").show();
		  }else{
			  $("#anotherApproverSp").hide();
			  $("#newApprover").val("");
		  }
	  });
	  
	  $(".usualApproveRemarkCb").change(function(){
		  if(this.checked==true){
			  $("[name='approveRemark']").text($("[name='approveRemark']").text()+$(this).val());
		  }else{
			  $("[name='approveRemark']").text($("[name='approveRemark']").text().replace($(this).val(),""));
		  }
	  });
	  
	  $("#approveSubmitBt").click(function(){
		  if($(this).attr("name")=="X"){
			  //<a>标签disable属性不好使，用个name属性
			  $.messager.alert("提示", "存在未完成的问题件，不能操作审批或再下发问题件");
			  return false;
		  }
		  
		  var opType = "A";
		  if($("#newProblemCb").attr("checked")==true){
			  opType = "P";
		  }
		$.post("${ctx}todolist/approve/approvalCheck", 
			{posNo:"${posNo}",status:"A08",opType:opType},
			function(data) {
				if(data=="1"){
					$.messager.alert("提示", "该保全记录状态不为待审批，不能操作");
				}else if(data=="2"){
					$.messager.alert("提示", "存在未完成的审批问题件，不能继续下发");
				}else{

					  if(opType=="P"){
						  $("#mainForm").attr("action","${ctx}todolist/approve/newproblem");
						  $("#mainForm").submit();
					  }else{
						  $("#mainForm").attr("action","${ctx}todolist/approve/submit");
							if($("input[name='approveDecision']:checked").val()=="3"){
								$.post("${ctx}todolist/approve/newApproverCheck", 
									{newApprover:$("#newApprover").val()},
									function(data) {
										if(data=="N"){
											$.messager.alert("提示", $("#newApprover").val()+"用户名不存在，请核实");
										}else{
											$("#mainForm").submit();
										}
									}, "text");
							}else{
								$("#mainForm").submit();
							}
					  }

				}
			}, "text");
			
		  return false;
	  });
	  
	  $("#newProblemCb").change(function(){
		  if(this.checked == true){
			  $("#problemContentSp").show();
		  }else{
			  $("#problemContentSp").hide();
		  }
	  });
	  
	  $("#mainForm").validate();
	  
	  $("#signImageDiv").attr("scrollLeft",2000);
	  $("#signImageDiv").attr("scrollTop",2000);
	  
	  $("#gqs").click(function(){
		  
		 // window.location.href = "\${GQ_URL}SL_GQS/GQQuery.do?type=policyInfo&key=${posInfo.policyNo}&isShowBackFlag=N";
		// window.open("${GQ_URL}SL_GQS/GQQuery.do?type=policyInfo&key=${posInfo.policyNo}&isShowBackFlag=N, target=_blank");
    	  //window.location.href = "${GQ_URL}SL_GQS/GQQuery.do?type=posChangeInfo&pos_no="+$("#posId").text()+"&isShowBackFlag=N";
    	  return false;  
	  });
  });

var theWin;
function fileDownWindow(url){
	theWin = window.open(url);
	setTimeout("theWin.close();",8000);
}


		window.onload=function(){
			controlApproveDecisionShow();
			controlInsertBadBehavior();
		}
/* 控制重新录入受理明细 edit by gaojiaming start */	
function controlApproveDecisionShow(){
	var posNo='${posNo}';
	$.ajax({type: "GET",
		url:"${ctx}acceptance/branch/checkIsShowButton.do",
		data:{"posNo" :posNo},
		cache: false,
		async: false,
		dataType:"json",
		success:function(data){
			if(data.isApplicationFree=="Y"){	
				$("#approveDecisionShow").hide();
				$("#approveDecisionShowText").hide();
			}else{
				$("#approveDecisionShow").show();
				$("#approveDecisionShowText").show();	
			}				
		}
	});							
}
/* edit by gaojiaming end */
/* 控制新增不良记录 edit by gaojiaming start */	
function controlInsertBadBehavior(){
	var posNo='${posNo}';
	$.ajax({type: "GET",
		url:"${ctx}approve/getBadBehaviorInfoByPosNo.do",
		data:{"posNo" :posNo},
		cache: false,
		async: false,
		dataType:"json",
		success:function(data){
			if(data==null){
				$("#insertBadBehaviorBt").hide();
			}else{
				$("#insertBadBehaviorBt").show();
	        	$("#objectType").val(data.objectType);
	        	$("#objectNo").val(data.objectNo);
	        	$("#objectName").val(data.objectName);
	        	$("#policyNo").val(data.policyNo);
			}				
		}
	});							
}
/* edit by gaojiaming end */
</script>

</sf:override>

<sf:override name="content">

<form id="mainForm">

<input type="hidden" name="posNo" value="${posNo}" />
<input type="hidden" name="submitNo" value="${submitNo}" />
<input type="hidden" name="approveNo" value="${approveNo}" />
<input type="hidden" name="barcodeNo" value="${posInfo.barcodeNo}" />

<fieldset class="fieldsetdefault">
<legend>待审批件${posNo}保全批单信息：</legend>
  <table class="layouttable">
    <tr>
      <td class="layouttable_td_label">条形码：</td>
      <td class="layouttable_td_widget">${posInfo.barcodeNo}</td>
      <td class="layouttable_td_label">保单号：</td>
      <td class="layouttable_td_widget">${posInfo.policyNo}
        <input type="hidden" value="${posInfo.policyNo}" name="policyNo" />
      </td>
    </tr>
    <tr>
      <td class="layouttable_td_label">投保人姓名：</td>
      <td class="layouttable_td_widget">${posInfo.clientName}</td>
      <td class="layouttable_td_label">投保人证件号：</td>
      <td class="layouttable_td_widget">${posInfo.idNo}</td>
    </tr>
     <tr>
      <td class="layouttable_td_label">生效日：</td>
      <td class="layouttable_td_widget">${posInfo.effectDate}</td>
      <td class="layouttable_td_label">回执日：</td>
      <td class="layouttable_td_widget">${posInfo.signDate}</td>
    </tr>
     <tr>
      <td class="layouttable_td_label">保单第一次打印日：</td>
      <td class="layouttable_td_widget">${posInfo.provideDate}</td>
      <td class="layouttable_td_label">保全项目：</td>
      <td class="layouttable_td_widget">
        <c:forEach items="${CodeTable.posTableMap.PRODUCT_SERVICE_ITEMS}" var="item">
          <c:if test="${posInfo.serviceItems eq item.code}">${item.description}</c:if>
        </c:forEach>
        <input type="hidden" value="${posInfo.serviceItems}" name="serviceItems" />
      </td>
    </tr>
    
    
    <tr>
      <td class="layouttable_td_label">受理日期：</td>
      <td class="layouttable_td_widget">${posInfo.acceptDate}</td>
      <td class="layouttable_td_label">经办人：</td>
      <td class="layouttable_td_widget">
      	${posInfo.acceptor}
        <input type="hidden" value="${posInfo.acceptor}" name="acceptor" />
      </td>
    </tr>
    <tr>
      <td class="layouttable_td_label">补发次数：</td>
      <td class="layouttable_td_widget">${posInfo.reprovide}</td>
      <td class="layouttable_td_label">是否亲办：</td>
      <td class="layouttable_td_widget">
        ${posInfo.applyType eq '1'? '是':'否'}
      </td>
    </tr>
    <tr>
      <td class="layouttable_td_label">电话回访标识：</td>
      <td class="layouttable_td_widget">${posInfo.phoneFlag}
      </td>
      <c:choose>      
        <c:when test="${posInfo.branchPercent eq null}">
          <td class="layouttable_td_label">备注：</td>
          <td class="layouttable_td_widget" >${posInfo.remark}</td>
        </c:when>
      <c:otherwise>
         <td class="layouttable_td_label">分公司承担比例(%)：</td>
         <td class="layouttable_td_widget">${posInfo.branchPercent}</td>  
       </c:otherwise>
     </c:choose>    
      
    </tr>
    <c:if test="${posInfo.branchPercent ne null}">
     <tr>     
      <td class="layouttable_td_label">备注：</td>
      <td class="layouttable_td_widget" colspan="3">${posInfo.remark}</td>
    </tr>
    </c:if>
   	<c:if test="${posInfo.attachFlag eq 'Y'}">
    <tr>
    	<td class="layouttable_td_label">附件：</td>
        <td class="layouttable_td_widget" colspan="3">
        	<c:forEach items="${posInfo.attachments}" var="item">
                <a href="javascript:fileDownWindow('${item.phoneFileUrl}')" >${item.phoneFileName}</a>
        	</c:forEach>
        </td>
    </tr>
    </c:if>
    <tr>
      <td class="layouttable_td_label_multiple">批文：</td>
      <td align="left" colspan="3">
      ${posInfo.approveText}
      </td>
    </tr>
  </table>
</fieldset>
<br />

<br />
<fieldset class="fieldsetdefault">
<legend>金额及转账信息：</legend>
  <table class="layouttable">
    <tr>
      <td class="layouttable_td_label" width="20%">收付方式：</td>
      <td align="left" width="25%">
        <c:forEach items="${CodeTable.posTableMap.CHARGING_METHOD}" var="item">
          <c:if test="${posInfo.paymentType eq item.code}">${item.description}</c:if>
        </c:forEach>
      </td>
      <td class="layouttable_td_label" width="20%">退费（试算）金额：</td>
      <td class="layouttable_td_widget">${posInfo.premSum}</td>
    </tr>
    <c:if test="${posInfo.paymentType eq '2'}">
    <tr>
      <td class="layouttable_td_label">户名：</td>
      <td class="layouttable_td_widget">${posInfo.premName}</td>
      <td class="layouttable_td_label">原缴费账户值：</td>
      <td class="layouttable_td_widget">${acctNo}</td>
    </tr>
     <tr>
      
      <td class="layouttable_td_label">转账账号：</td>
      <td class="layouttable_td_widget">${posInfo.accountNo}</td>
      <td colspan="2"></td>
      
    </tr>
    </c:if>
  </table>
</fieldset>
<br />

<br />
<fieldset class="fieldsetdefault">
<legend>审批信息：</legend>
  <table class="infoDisTab">
    <tr>
      <th width="5%">序号</th>
      <th>审批人</th>
      <th width="40%">送审原因</th>
      <th width="10%">审批状态</th>
      <th>审批意见</th>
      <th>审批时间</th>
    </tr>
    <c:forEach items="${approvedList}" var="item" varStatus="status">
    <c:if test="${status.index%2==0}">
        <tr class="odd_column">
    </c:if>
    <c:if test="${status.index%2!=0}">
        <tr class="even_column">
    </c:if>
      <td>${item.APPROVE_NO}</td>
      <td>${item.APPROVER}</td>
      <td>${item.SUBMIT_DESCRIPTION}</td>
      <td>${item.APPROVE_DECISION}</td>
      <td>${item.APPROVE_OPNION}</td>
      <td>${item.APPROVE_TIME}</td>
    </tr>
    </c:forEach>
  </table>
</fieldset>
<br />

<c:if test="${problemInfo.problemFlag eq 'Y'}">
<br />
<fieldset class="fieldsetdefault">
<legend>问题件信息：</legend>
  <table class="infoDisTab">
    <tr>
      <th>问题件号</th>
      <th>下发人</th>
      <th>状态</th>
      <th>处理结果</th>
      <th>内容</th>
      <th>处理意见</th>
    </tr>
    <c:forEach items="${problemInfo.problemList}" var="item" varStatus="status">
    <c:if test="${status.index%2==0}">
        <tr class="odd_column">
    </c:if>
    <c:if test="${status.index%2!=0}">
        <tr class="even_column">
    </c:if>
      <td>${item.PROBLEM_ITEM_NO}</td>
      <td>${item.SUBMITTER}</td>
      <td>${item.PROBLEM_STATUS_DESC}</td>
      <td>${item.DEAL_RESULT}</td>
      <td>${item.PROBLEM_CONTENT}</td>
      <td>${item.DEAL_OPINION}</td>
    </tr>
    </c:forEach>
  </table>
</fieldset>
<br />
</c:if>

<br />
<fieldset class="fieldsetdefault">
<legend>签名样本：</legend>
    <div style="width:550px; height:100px; overflow:scroll" id="signImageDiv">
        <img alt="查询签名影像图片失败" src="${signImageInfo}" />
    </div>
     <c:if test="${posInfo.channelType ne '06'}">  
        <a href="javascript:void(0)" onclick="window.open('${ctx}include/getImageByBarcodeNo.do?barcodeNo=${policyApplyBarcode}','apUwBarCode','resizable=yes,height=700px,width=600px');">查看投保单影像</a>
     </c:if>
     <c:if test="${posInfo.channelType eq '06'}">     
      <a href="javascript:void(0)" onclick="window.open('${ctx}include/getImagesByBarcodeNo.do?barcodeNo=${policyApplyBarcode}','apUwBarCode','resizable=yes,height=700px,width=600px');">查看投保影像</a>
     </c:if>
     
</fieldset>
<br />

<br />
<fieldset class="fieldsetdefault">
<legend>审批意见：</legend>
  <table align="left" width="100%" >
    <tr>
      <td colspan="3" align="left">
        <input type="radio" name="approveDecision" value="1" class="{required:function(){return $('#newProblemCb').attr('checked')==false}}" />通过
        <input type="radio" name="approveDecision" value="2" class="{required:function(){return $('#newProblemCb').attr('checked')==false}}" />不通过
        <input type="radio" name="approveDecision" value="3" class="{required:function(){return $('#newProblemCb').attr('checked')==false}}" />转他人审批
        <c:if test="${selfApproveFlag eq 'Y' and posInfo.serviceItems ne '40'}">
        	<input id="approveDecisionShow" type="radio" name="approveDecision" value="4" class="{required:function(){return $('#newProblemCb').attr('checked')==false}}" /><span id="approveDecisionShowText">重新录入受理明细</span>
        </c:if>
      <span id="anotherApproverSp" style="display:none">
      请输入新审批人：
      <input type="text" name="newApprover" id="newApprover" class="{required:function(){return $('[name=approveDecision]:checked').val()=='3'}}" /></span>
      </td>
    </tr>
    <tr>
      <td valign="top" align="right">
      审批备注：
      </td>
      <td align="left" valign="top">
      <textarea cols="35" rows="2" name="approveRemark" wrap="off" class="{byteRangeLength:[0,500]}"></textarea>
      </td>
      <td align="left" width="30%">
      <input type="checkbox" class="usualApproveRemarkCb" value="申请资料齐全;" />申请资料齐全<br />
      <input type="checkbox" class="usualApproveRemarkCb" value="申请书填写正确;" />申请书填写正确<br />
      <input type="checkbox" class="usualApproveRemarkCb" value="授权委托书填写正确;" />授权委托书填写正确<br />
      </td>
    </tr>
    <tr>
      <td colspan="2" align="left">
      <input type="checkbox" id="newProblemCb" />下发问题件
      <span id="problemContentSp" style="display:none">
      <br />
      问题件内容:<br />
      <textarea cols="45" rows="2" name="problemContent" wrap="off" class="{required:function(){return $('#newProblemCb').attr('checked')==true}}" ></textarea>
      </span>
      </td>
      <td align="right" valign="bottom">
		<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="insertBadBehaviorBt">新增不良记录</a>      
      <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-ok" id="approveSubmitBt" <c:if test="${problemInfo.appProblemFlag eq 'Y'}">name="X" disabled title="存在未完成的问题件，不能操作审批或再下发问题件"</c:if>>确定</a>
      </td>
    </tr>
  </table>
</fieldset>
<p align="right">
	<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="imageViewLink">查看原始影像</a>
	<a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-search" id="relateImageViewLink">查看关联影像</a>
<a href="javascript:void(0)" onclick="window.open('<%=PosUtils.getPosProperty("gqUrl")%>SL_GQS/GQQuery.do?type=policyInfo&policy_no=${posInfo.policyNo}','apGQ','resizable=yes,scrollbars=yes,height=700px,width=1000px');">综合查询</a>
	<!--<a href="javascript:void(0)" onclick="window.open('${ctx}pub/posGQ.do?type=policyInfo&key=${posInfo.policyNo}','apGQ','resizable=yes,scrollbars=yes,height=700px,width=1000px');">综合查询</a>-->
<!--<a   onclick="window.open('${GQ_URL}SL_GQS/GQQuery.do?type=policyInfo&key=${posInfo.policyNo}&isShowBackFlag=N','apGQ','resizable=yes,scrollbars=yes,height=700px,width=1000px');">综合查询</a>-->

  

</p>
<!-- 新增不良记录DIV -->
<div id="insertBadBehaviorWindowDiv" class="easyui-window" closed="true" modal="true"
	title="新增不良记录" style="width: 570px; height: 350px;" collapsible="false" minimizable="false" maximizable="false">
		<div  class="easyui-panel">
		<table class="layouttable">
		  <tr>
		    <td width="15%" class="layouttable_td_label">类别：</td>
		    <td class="layouttable_td_widget">
			    <select id="objectType" name="objectType">					
				    <option value="1">网点</option>
				    <option value="2">银代经理</option>
				</select>
		    </td>
		  </tr>
		  <tr>    
		    <td width="15%" class="layouttable_td_label">对象号码：<span class="requred_font">*</span></td>
		    <td class="layouttable_td_widget">
				<input type="text"  id="objectNo" name="objectNo" value="" class="{required:true}"/>
		    </td>    
		  </tr>
		  <tr>
		    <td width="15%" class="layouttable_td_label">对象名称：<span class="requred_font">*</span></td>
		    <td class="layouttable_td_widget">
				<input type="text"  readonly id="objectName" name="objectName" value="" class="{required:true}"/>
		    </td>
		  </tr>
		  <tr>    
		    <td width="15%" class="layouttable_td_label">保单号：<span class="requred_font">*</span></td>
		    <td class="layouttable_td_widget">
				<input type="text"  name="policyNo" id="policyNo" value=""/>
		    </td>   
		  </tr>
		  <tr>
		    <td width="15%" class="layouttable_td_label">行为类别：</td>
		    <td class="layouttable_td_widget">
			    <select id="behaviorType" name="behaviorType">					
				    <option value="1">有效投诉件数</option>
				    <option value="2">疑似销售误导</option>
				    <option value="3">疑似代签名</option>
				    <option value="4">伪造保全资料</option>
				    <option value="5">其它</option>		    
				</select>		
		    </td>
		  </tr>
		  <tr>    
		    <td width="15%" class="layouttable_td_label">备注：<span class="requred_font">*</span></td>
		    <td class="layouttable_td_widget">
				<textarea id="descrpiton" name="descrpiton" wrap="off" cols="40" value="" ></textarea>		
		    </td>    
		  </tr>    
		</table>
		</div>
		<br />
		<div align="right">
		    <a class="easyui-linkbutton" href="javascript:void(0)" iconCls="icon-add" id="save" >保存</a>
		</div>
</div>	
<!-- 新增不良记录DIV -->
</form>
  
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosSimpleImageViewLayout.jsp"></jsp:include>

<c:if test="${not empty RETURN_MESSAGE}">
  <script type="text/javascript">
	alert("${RETURN_MESSAGE}");
	$("#mainForm").attr("action","${ctx}todolist/index");
	$("#mainForm").submit();
  </script>
</c:if>