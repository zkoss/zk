<%--
calendar.dsp

{{IS_NOTE
	Purpose:

	Description:

	History:
		Mon Apr 24 17:13:31     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<table id="${self.uuid}" z.type="zul.db.Cal"${self.outerAttrs}${self.innerAttrs}></table>