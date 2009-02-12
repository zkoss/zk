<%--
report.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jan 16 14:37:13     2008, Created by gracelin
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<iframe id="${self.uuid}" z.type="zul.widget.Ifr"${self.outerAttrs}${self.innerAttrs}>
</iframe><%-- z.type same as iframe due to xml, pdf... --%>