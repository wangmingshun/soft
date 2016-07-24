 <%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<sf:override name="pathString">保单回执查询清单列表</sf:override>
<sf:override name="head">

<script type="text/javascript">
$(function(){
});

</script>
<style type="text/css">
#menu_ul li {
	line-height: 30px;
	font-size:15px;
}

</style>
</sf:override>
<sf:override name="content">
<form>
<ul id="menu_ul">
  <li><a href="${ctx}receipt/list/listQuery?listFlag=1">待回销清单</a></li>
  <c:if test="${branchAcceptorFlag ne 'Y'}">
      <li><a href="${ctx}receipt/list/listQuery?listFlag=2">回执日期修改清单</a></li>
  </c:if>
  <li><a href="${ctx}receipt/list/listQuery?listFlag=4">问题件清单</a></li>
  <li><a href="${ctx}receipt/list/listQuery?listFlag=5">报备保单清单</a></li>
  <li><a href="${ctx}receipt/list/otherReport">其他追踪报表</a></li>
  <c:if test="${branchAcceptorFlag ne 'Y'}">
      <li><a href="${ctx}receipt/list/listQuery?listFlag=7">统计报表</a></li>
  </c:if>
</ul>
</form>
</sf:override>
<jsp:include page="/WEB-INF/views/layouts/PosDefaultLayout.jsp"></jsp:include>