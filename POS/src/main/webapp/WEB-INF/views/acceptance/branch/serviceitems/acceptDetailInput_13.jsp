<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<%-- 缴费频次变更 --%>

<sf:override name="serviceItemsInput">
<sfform:formEnv commandName="acceptance__item_input">
    
    <br />
    保单险种信息&nbsp;&nbsp;币种：人民币
	<table class="infoDisTab">
		<thead>
			<tr>
              <th>序号</th>
              <th width="30%">险种</th>
              <th>被保人</th>
              <th>险种状态</th>
              <th>原缴费频次</th>
              <th>原期缴保费</th>
              <th>原缴至日</th>
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
				<td>${item.prodSeq}</td>
                <td>${item.productName}（${item.productCode}）</td>
                <td>${item.insuredName}</td>
                <td>${item.dutyStatusDesc}</td>
                <td>${item.frequencyDesc}</td>
                <td>${item.periodPremSum}</td>
                <td><fmt:formatDate value="${item.payToDate}" pattern="yyyy-MM-dd"/></td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
    请选择新缴费频次：
    <select name="frequency" class="{required:true}">
    	<c:forEach items="${CodeTable.posTableMap.FREQUENCY}" var="item">
        	<c:forEach items="${acceptance__item_input.frequencyArr}" var="i">
        	<c:if test="${i eq item.code}">
            	<option value="${item.code}">${item.description}</option>
            </c:if>
            </c:forEach>
        </c:forEach>
    </select>

</sfform:formEnv>
</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
