<?xml version="1.0" encoding="UTF-8"?><%--
desktop.dsp

{{IS_NOTE
	Purpose:
		Used to render a MIL page as a complete page (aka., desktop)
		if it is not included
	Description:
		
	History:
		Mon May 28 14:31:51     2007, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="arg" value="${requestScope.arg}"/>
<c:set var="page" value="${arg.page}"/>
<c:include page="~./mil/rmil/page.dsp"/><%-- OC4J cannot handle relative page correctly --%>
