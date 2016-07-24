<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/commons/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/WEB-INF/views/commons/head.jsp"%>
<style type="text/css">
	.infoDisTab th {
		white-space:normal;
	}
</style>
<sf:block name="head"></sf:block>
</head>
<body style="margin:0;padding:0;">
<div class="layout_div" style="padding:0;margin:0;">
<div class="navigation_div"><span class="font_heading1"><sf:block name="pathString" /></span></div>
<sf:block name="content" />
</div>
<c:if test="${not empty RETURN_MSG}">
	<script type="text/javascript">
		$.messager.alert("提示", "${RETURN_MSG}");
	</script>
</c:if>
</body>
</html>