<%--
desktop.dsp

{{IS_NOTE
	Purpose:
		The desktop template for the xml language
	Description:
		
	History:
		August 23, 2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %><%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %><c:set var="page" value="${requestScope.arg.page}"/><%@ page contentType="${z:outContentType(page)}" %>${z:outFirstLine(page)}${z:outDocType(page)}<c:include page="~./zml/page.dsp"/><%-- OC4J cannot handle relative page correctly --%>
