<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<sf:override name="serviceItemsInput">

<table class="layouttable">  
  <tr>
    <td width="15%" class="layouttable_td_label">操作前的保单挂失状态：
    </td>
    <td class="layouttable_td_widget">
    ${acceptance__item_input.isLoss eq 'Y'? '已挂失':'未挂失' }
    <input type="hidden" name="isLoss" value="${acceptance__item_input.isLoss}" />
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label">操作后的保单挂失状态：
    </td>
    <td class="layouttable_td_widget">
    <input type="checkbox" name="cb1" class="{required:true}" <c:if test="${acceptance__item_input.isLoss eq 'N'}">disabled</c:if> />挂失解除
    <input type="checkbox" name="cb2" class="{required:true}" <c:if test="${acceptance__item_input.isLoss eq 'Y'}">disabled</c:if> />已挂失
    </td>
  </tr>
</table>

</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>
