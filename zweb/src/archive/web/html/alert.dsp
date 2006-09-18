<%@ page contentType="text/html;charset=UTF-8" %><%--
alert.dsp

{{IS_NOTE
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
		Tue Apr 12 11:41:02     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/web/html.dsp.tld" prefix="h" %>
<c:set var="arg" value="${requestScope.arg}"/>
<c:set var="alert_type"
	value="${arg != null ? arg.px_alert_type: param.px_alert_type}"/>
<c:set var="alert"
	value="${arg != null ? arg.px_alert: param.px_alert}"/>
<c:choose>
<c:when test="${alert_type=='error'}">
 <h:box color="red" caption="${c:l('error')}">
<pre><c:out value="${alert}"/></pre>
 </h:box>
</c:when>
<c:when test="${alert_type=='warning'}">
 <h:box color="#EEE040" caption="${c:l('warning')}">
<pre><c:out value="${alert}"/></pre>
 </h:box>
</c:when>
<c:otherwise>
 <h:box color="#606035">
<pre><c:out value="${alert}"/></pre>
 </h:box>
</c:otherwise>
</c:choose>
