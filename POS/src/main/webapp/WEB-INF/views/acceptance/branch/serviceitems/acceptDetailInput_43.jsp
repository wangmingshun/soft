<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<sf:override name="serviceItemsInput">

<table class="layouttable">
  <tr>
    <td class="layouttable_td_label" width="15%">当前E服务平台权限：
    </td>
    <td class="layouttable_td_widget">
    <c:if test="${acceptance__item_input.privsFlag eq '1'}">初级权限</c:if>
    <c:if test="${acceptance__item_input.privsFlag eq '2'}">高级权限</c:if>
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label">本次操作后E服务平台权限：
    </td>
    <td class="layouttable_td_widget">
    <input type="radio" name="privsFlag" class="{required:true}" value="1" <c:if test="${acceptance__item_input.privsFlag eq '1'}">disabled</c:if> />初级权限
    <input type="radio" name="privsFlag" class="{required:true}" value="2" <c:if test="${acceptance__item_input.privsFlag eq '2'}">disabled</c:if> />高级权限
    </td>
  </tr>
</table>

</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>