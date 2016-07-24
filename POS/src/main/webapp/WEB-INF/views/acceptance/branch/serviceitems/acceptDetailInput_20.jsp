<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<%-- 投保人变更--%>
<sf:override name="headBlock">
<script type="text/javascript">
$(function(){
	$("#newApplicantBt").click(function(){
		window.location.href = "${ctx}acceptance/branch/queryAndAddClient.do?policyNo=${acceptance__item_input.policyNo}&clientNo=" + $("[name='applicantNo']").val() + 
			"&rewriteClientNoPropName=applicantNo&rewriteQAACPropName=queryAndAddClientDTO";
		return false;
	});

	$("#relation").change(function(){
	  if($(this).val()=='99'){
		  $("#relationDesc").removeAttr("disabled");
	  }else{
		  $("#relationDesc").attr("disabled",true);
		  $("#relationDesc").val("");
	  }
	});
	  
});

posValidateHandler=function(){
	if("N"=="${acceptance__item_input.contactChangedFlag}"){
		alert("变更投保人后请您务必变更保单的联系方式");
	}
	return true;
};
</script>
</sf:override>

<sf:override name="serviceItemsInput">

<sfform:formEnv commandName="acceptance__item_input">

<br />
查询及新增客户资料：
<table class="layouttable">
  <tr>
    <td class="layouttable_td_label" width="15%">新投保人客户号：</td>
    <td class="layouttable_td_widget">
      <input type="hidden" id="originApplicantNo" value="${acceptance__item_input.originServiceItemsInputDTO.originApplicantNo}"/>
      <sfform:input path="applicantNo" cssClass="{required:true,notEqualTo:'#originApplicantNo',messages:{notEqualTo:'新投保人不能与原投保人相同'}}" readonly="true"/>
      <a href="javascript:void(0)" id="newApplicantBt">查询新投保人资料</a>
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label">新投保人与被保人关系：</td>
    <td class="layouttable_td_widget">
      <sfform:select path="relation" items="${CodeTable.posTableMap.RELATIONSHIP}" itemLabel="description" itemValue="code"></sfform:select>
      其他关系描述：
      <sfform:input path="relationDesc" cssClass="{required:true}" disabled="true" />
      <input type="hidden" name="relationCheck" class="{required:function(){return $('#applicantNo').val()!='${acceptance__item_input.insuredNo}' && $('#relation').val()=='01'},messages:{required:'新投保人并不是被保人本人'}}" />
    </td>
  </tr>
</table>

</sfform:formEnv>

</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
