<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>

<sf:override name="serviceItemsInput">

<table class="layouttable">
  <tr>
    <td class="layouttable_td_label" width="15%">当前保单冻结状态：
    </td>
    <td class="layouttable_td_widget">
    <c:if test="${acceptance__item_input.policyStatus eq 'Y'}">已冻结</c:if>
    <c:if test="${acceptance__item_input.policyStatus eq 'N'}">未冻结</c:if>
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label">操作后的保单冻结状态：
    </td>
    <td class="layouttable_td_widget">
    <input type="radio" name="policyStatus" class="{required:true}" value="Y" <c:if test="${acceptance__item_input.policyStatus eq 'Y'}">disabled</c:if> />冻结
    <input type="radio" name="policyStatus" class="{required:true}" value="N" <c:if test="${acceptance__item_input.policyStatus eq 'N'}">disabled</c:if> />解冻
    </td>
  </tr>
  <tr>
    <td class="layouttable_td_label">备注录入：
    </td>
    <td class="layouttable_td_widget">
    	<textarea name="freezeOrThawRemark" class="{byteRangeLength:[0,200]}"></textarea>
    </td>
  </tr>
</table>

</sf:override>
<jsp:include page="/WEB-INF/views/acceptance/branch/acceptDetailInput.jsp"></jsp:include>