<%--
applet.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Sep 19 17:31:03 TST 2008, Created by davidchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

<c:set var="self" value="${requestScope.arg.self}" />
<applet id="${self.uuid}" name="${self.name}" code="${self.code}" ${self.outerAttrs}${self.innerAttrs} z.type="zul.applet.Applet">
<c:forEach var="param" items="${self.params}">
	<param name="${param.x}" value="${param.y}"/>
</c:forEach>
</applet>