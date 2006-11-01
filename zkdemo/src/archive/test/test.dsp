<%--
test.psp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Sep  5 17:23:07     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/web/html.dsp.tld" prefix="h" %>

<html>
<head>
	<title>Test of DSP</title>
</head>
<body>
<c:set var="v" value="${123}"/>
	<h1>Potix Dynamic Script Page</h1>
	<ul>
	<li>$\{param.some}: ${param.some}</li>
	<li>$\{v}: ${v}</li>
	<li>visible: <c:if test="${!empty v}"> yes <c:remove var="v"/></c:if></li>
	<li>invisible: <c:if test="${!empty v}">wrong</c:if></li>
	</ul>
	<h:box caption="Information" shadow="true">
	This is inside a box.
	</h:box>
</body>
