<%@ page contentType="text/html;charset=UTF-8" %><%--
alert.dsp

	Purpose:
		Shows the alert from the request's parameters
		or from the arg attribute
	Description:
		Parameters:
		px_alert_type
			The alert type
		px_alert
			The alert message
	History:
		Tue Apr 12 11:41:02     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/html" prefix="h" %>
<c:set var="arg" value="${requestScope.arg}"/>
<c:set var="arg" value="${param}" if="${empty arg}"/>
<c:choose>
<c:when test="${arg.px_alert_type=='error'}">
 <h:box color="red" caption="${c:l('error')}">
<pre><c:out value="${arg.px_alert}"/></pre>
 </h:box>
</c:when>
<c:when test="${arg.px_alert_type=='warning'}">
 <h:box color="#EEE040" caption="${c:l('warning')}">
<pre><c:out value="${arg.px_alert}"/></pre>
 </h:box>
</c:when>
<c:otherwise>
 <h:box color="#606035">
<pre><c:out value="${arg.px_alert}"/></pre>
 </h:box>
</c:otherwise>
</c:choose>
