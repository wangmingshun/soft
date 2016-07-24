<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<sf:override name="serviceItemsInput">

<table class="layouttable">
  <tr>
    <td width="34%" class="layouttable_td_label">保单${acceptance__item_input.policyNo}当前红利给付方式信息： 
    </td>
    <td class="layouttable_td_widget">
        <c:forEach items="${CodeTable.posTableMap.DIVIDEND_SELECTION}" var="item">
          <c:if test="${acceptance__item_input.oldDividendSelection eq item.code}">${item.description}</c:if>
        </c:forEach>
        <input type="hidden" value="${acceptance__item_input.originServiceItemsInputDTO.oldDividendSelection}" name="oldDividendSelection" id="oldDividendSelection"/>
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label">请选择新的红利给付方式：
    </td>
    <td class="layouttable_td_widget">
        <select name="newDividendSelection" class="{notEqualTo:'#oldDividendSelection'}">
          <c:forEach items="${CodeTable.posTableMap.DIVIDEND_SELECTION}" var="item">
             <option value="${item.code}">${item.description}</option>
          </c:forEach>
       </select>
    </td>
  </tr>
</table>

</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>