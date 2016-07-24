<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<sf:override name="serviceItemsInput">

<table class="layouttable">
  <tr>
    <td class="layouttable_td_label" width="15%">当前自垫选择方式：
    </td>
    <td class="layouttable_td_widget">
    <c:if test="${acceptance__item_input.aplOption eq '1'}">同意垫交</c:if>
    <c:if test="${acceptance__item_input.aplOption eq '2'}">不同意垫交</c:if>
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label">本次操作后自垫选择方式：
    </td>
    <td class="layouttable_td_widget">
    <input type="radio" name="aplOption" class="{required:true}" value="1" <c:if test="${acceptance__item_input.aplOption eq '1'}">disabled</c:if> />同意垫交
    <input type="radio" name="aplOption" class="{required:true}" value="2" <c:if test="${acceptance__item_input.aplOption eq '2'}">disabled</c:if> />不同意垫交
    </td>
  </tr>
</table>

</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>