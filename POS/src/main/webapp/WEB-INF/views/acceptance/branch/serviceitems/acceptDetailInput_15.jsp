<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<%-- 职业等级变更 --%>
<sf:override name="headBlock">
<script type="text/javascript">
  $(function(){
	  $("#occupationCodeInput").keydown(function(e){
		  if (e.keyCode == 13){
			  $("#occupationGradeTd").text("");
			  $("#occupationDescTd").text("");
			  $.post("${ctx}acceptance/branch/queryOccupationInfo.do", 
		         {occupationCode:$(this).val()},
				 function(data){
					 if(data){
						 $("#occupationGradeTd").text(data.GRADE_DESC);
					     $("#occupationDescTd").text(data.OCCUPATION_DESC);
						 $("#occupationCodeInputCheck").val("Y");
					 }
				 },
				 "json"
			  );
		  }
	  });
	
	$("#occupationCodeInput").change(function(){
		$("#occupationCodeInputCheck").val("");
	});
	
    $("#workStartDate").click(function(){
		WdatePicker({skin:'whyGreen',startDate:'${CodeTable.posTableMap.SYSDATE}'});
	});

	$(".productPremCb").change(function(){
		$(this).next().val(this.checked);
	});
	//初始化选中
	$(".productPremCb").each(function(){
		if($(this).prev().val()=="${acceptance__item_input.policyNo}"){
			$(this).attr("checked",true);
			$(this).next().val(true);
			$(this).attr("disabled",true);
		}else{
			$(this).next().val(false);
		}
	});
		
  });

</script>
</sf:override>

<sf:override name="serviceItemsInput">
<sfform:formEnv commandName="acceptance__item_input">

    <br />
    客户层信息：&nbsp;
    客户号：${acceptance__item_input.client.clientNo}&nbsp;
    客户姓名：${acceptance__item_input.client.clientName}
	<table class="layouttable">
      <tr>
        <td class="layouttable_td_label">原客户职业代码：</td>
        <td class="layouttable_td_widget">${acceptance__item_input.originServiceItemsInputDTO.client.occupationCode}</td>
        <td class="layouttable_td_label">原客户职业等级：</td>
        <td class="layouttable_td_widget">${acceptance__item_input.client.occupationGradeDesc}</td>
        <td class="layouttable_td_label">原客户职业描述：</td>
        <td class="layouttable_td_widget">${acceptance__item_input.client.occupationDesc}</td>
      </tr>
      <tr class="list_td">
        <td class="layouttable_td_label">现客户职业代码：</td>
        <td class="layouttable_td_widget">
          <input name="client.occupationCode" type="text" id="occupationCodeInput" title="输入后回车" />
          <input type="hidden" name="occuInputCheck" id="occupationCodeInputCheck" class="{required:true,messages:{required:'请先输入职业代码后回车查询到职业描述'}}" />
        </td>
        <td class="layouttable_td_label">现客户职业等级：</td>
        <td class="layouttable_td_widget" id="occupationGradeTd"></td>
        <td class="layouttable_td_label">现客户职业描述：</td>
        <td class="layouttable_td_widget" id="occupationDescTd"></td>
      </tr>
      <tr class="list_td">
        <td class="layouttable_td_label">现服务单位名称：</td>
        <td class="layouttable_td_widget">
          <input type="text" name="client.workUnit" class="{required:true}" />
        </td>
        <td class="layouttable_td_label">就职日期：</td>
        <td class="layouttable_td_widget">
          <input type="text" name="workStartDate" class="Wdate {required:true}" id="workStartDate" />
        </td>
        <td class="layouttable_td_label">现职务内容：</td>
        <td class="layouttable_td_widget">
          <input type="text" name="client.position" class="{required:true}" />
        </td>
      </tr>
	</table>
    <hr class="hr_default" />
    
    保单层信息：
    <table class="infoDisTab">
      <tr>
        <th>选择/序号</th>
        <th>保单号</th>
        <th>被保人</th>
        <th>投保人</th>
        <th>生效日期</th>
        <th>交至日</th>
        <th>保单缴费状态</th>
        <th>变更前客户职业代码</th>
      </tr>
      <c:forEach items="${acceptance__item_input.productPremList}" var="item" varStatus="s">
        <c:if test="${s.index%2==0}">
            <tr class="odd_column">
        </c:if>
        <c:if test="${s.index%2!=0}">
            <tr class="even_column">
        </c:if>
        <td>
        	<c:choose>
        		<c:when test="${not empty item.verifyResult.ruleInfos}">
        			<a href="javascript:void(0)" onclick="$('#unpassRuleDetailWindow${s.index}').window('open');return false;">规则检查不通过</a>
	    			<div id="unpassRuleDetailWindow${s.index}" class="easyui-window" title="规则检查不通过详细信息"
					style="width:500px;height:300px;padding:5px;background: #fafafa;" closed="true" modal="true" collapsible="false" minimizable="false" maximizable="false">
						<div class="easyui-layout" fit="true">
							<div region="center" border="false" style="padding:10px;background:#fff;border:1px solid #ccc;">
								<ol style="padding:0;margin:0 0 0 28px;">
									<c:forEach items="${item.verifyResult.ruleInfos}" var="verifyResultItem">
										<li>${verifyResultItem.description}</li>
									</c:forEach>
								</ol>
							</div>
							<div region="south" border="false" style="text-align:right;height:30px;line-height:30px;">
								<a class="easyui-linkbutton" iconCls="icon-ok" href="javascript:void(0)" onclick="$('#unpassRuleDetailWindow${s.index}').window('close');return false;">关闭</a>
							</div>
						</div>
					</div>
        		</c:when>
        		<c:otherwise>
        			<sfform:hidden path="productPremList[${s.index}].policyNo" />
			        <input type="checkbox" class="productPremCb" />${s.index+1}
			        <sfform:hidden path="productPremList[${s.index}].checked" />
        		</c:otherwise>
        	</c:choose>
        </td>
        <td>${item.policyNo}</td>
        <td>${item.insuredName}</td>
        <td>${item.applicantName}</td>
        <td><fmt:formatDate pattern='yyyy-MM-dd' value='${item.effectDate}'/></td>
        <td><fmt:formatDate pattern='yyyy-MM-dd' value='${item.payToDate}'/></td>
        <td>${item.premStatus}</td>
        <td>${item.occupationCode}</td>
      </tr>
      </c:forEach>
    </table>

</sfform:formEnv>
</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
