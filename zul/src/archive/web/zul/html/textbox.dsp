<%--
textbox.dsp

{{IS_NOTE
	$Id: textbox.dsp,v 1.6 2006/03/17 10:06:34 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Jun 14 17:17:17     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:choose>
<c:when test="${self.multiline}"><%-- textarea doesn't support maxlength --%>
<textarea id="${self.uuid}" zk_type="zul.html.widget.Txbox"${self.outerAttrs}${self.innerAttrs}><c:out value="${self.value}" multilineReplace=""/></textarea>
</c:when>
<c:otherwise>
<input id="${self.uuid}" zk_type="zul.html.widget.Txbox"${self.outerAttrs}${self.innerAttrs}/>
</c:otherwise>
</c:choose>
