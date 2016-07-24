<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<%-- 缴费年期变更 --%>
<sf:override name="headBlock">
<script type="text/javascript">
  $(function(){
	  $(".inputProductChecked").change(function(){
		  if(this.checked==true){
			  $(this).parent().parent().find(":input:not('.inputProductChecked')").removeAttr("disabled");
		  }else{
			  $(this).parent().parent().find(":input:not('.inputProductChecked')").val("");
			  $(this).parent().parent().find(":input:not('.inputProductChecked')").attr("disabled",true);
		  }
	  });

  });

</script>
</sf:override>

<sf:override name="serviceItemsInput">
<sfform:formEnv commandName="acceptance__item_input">

    <br />
    保单险种信息&nbsp;&nbsp;币种：人民币
    <input type="hidden" name="checkOneAtLeast" class="{required:function(){return $('.inputProductChecked:checked').length<1 }, messages:{required:'请至少选择一个险种'}}" />
	<table class="infoDisTab">
		<thead>
			<tr>
              <th width="5%">选择</th>
              <th>险种序号</th>
              <th>险种</th>
              <th>被保人</th>
              <th>险种状态</th>
              <th>原缴费年期</th>
              <th>原年缴标准保费</th>
              <th>生效日</th>
              <th>缴至日</th>
              <th width="20%">新缴费年期</th>
			</tr>
		</thead>
		<tbody>
              <c:forEach items="${acceptance__item_input.productList}" var="item" varStatus="status">
                <c:if test="${status.index%2==0}">
                    <tr class="odd_column">
                </c:if>
                <c:if test="${status.index%2!=0}">
                    <tr class="even_column">
                </c:if>
			    <td>
			    <c:choose>
			      <c:when test="${item.premStatus eq '1' and item.premSource eq '4'}">
			        <div title="不能操作豁免险" style="color:#F00">X</div>
			      </c:when>
			      <c:when test="${item.premStatus eq '0'}">
			        <div title="不能操作已交清的险种" style="color:#F00">X</div>
			      </c:when>
			      <c:otherwise>
			        <sfform:checkbox path="productList[${status.index}].checked" 
			          cssClass="inputProductChecked"/>
			      </c:otherwise>
			    </c:choose>
			    </td>
				<td>${item.prodSeq}</td>
                <td>${item.productName}（${item.productCode}）</td>
                <td>${item.insuredName}</td>
                <td>${item.dutyStatusDesc}</td>
                <td>${item.premTerm}${item.premPeriodTypeDesc}</td>
                <td>${item.annStandardPrem}</td>
                <td><fmt:formatDate value="${item.effectDate}" pattern="yyyy-MM-dd"/></td>
                <td><fmt:formatDate value="${item.payToDate}" pattern="yyyy-MM-dd"/></td>
                <td>
                  <input type="text" name="productList[${status.index}].premTerm" disabled="true" class="easyui-numberbox {required:true}" size="10" />
                  <sfform:select path="productList[${status.index}].premPeriodType" disabled="true" >
                    <sfform:option value="2">年</sfform:option>
                    <sfform:option value="3">岁</sfform:option>
                  </sfform:select>
                </td>
			</tr>
			</c:forEach>
		</tbody>
	</table>

</sfform:formEnv>
</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
