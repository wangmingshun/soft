<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<sf:override name="serviceItemsInput">

<table class="layouttable">
  <tr>
    <td class="layouttable_td_label" width="15%">当前险种：
    </td>
    <td class="layouttable_td_widget">
    <c:if test="${acceptance__item_input.changeableProduct eq 'CIME_PN0'}">D款</c:if>
    <c:if test="${acceptance__item_input.changeableProduct eq 'CIME_QN0'}">E款</c:if>
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label">本次操作后险种转换为：
    </td>
    <td class="layouttable_td_widget">
    <input type="radio" name="changeableProduct" class="{required:true}" value="CIME_PN0" <c:if test="${acceptance__item_input.changeableProduct eq 'CIME_PN0'}">disabled</c:if> />D款
    <input type="radio" name="changeableProduct" class="{required:true}" value="CIME_QN0" <c:if test="${acceptance__item_input.changeableProduct eq 'CIME_QN0'}">disabled</c:if> />E款
    </td>
  </tr>
</table>

</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>