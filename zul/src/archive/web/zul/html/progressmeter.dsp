<%--
progressmeter.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Aug 14 17:12:24     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" z.type="zul.widget.PMeter"${self.outerAttrs}${self.innerAttrs}><span id="${self.uuid}!img" class="${self.iconSclass}"></span></div>