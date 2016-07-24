<%@ page language="java" contentType="text/html; charset=utf-8"
	import="com.sinolife.sf.comm.SecurityException" pageEncoding="utf-8"%>
<html>
<head>
<title>没有权限</title>
</head>

<body>
您没有访问资源
<h1><%=((com.sinolife.sf.comm.SecurityException) request.getAttribute("SecurityException")).getResourceName()%>
</h1>
的权限
</body>
</html>