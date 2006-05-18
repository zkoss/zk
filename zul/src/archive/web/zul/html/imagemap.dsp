<%--
imagemap.dsp

{{IS_NOTE
	$Id: imagemap.dsp,v 1.1 2006/04/12 10:43:09 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Mar 28 14:54:56     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zul/core.dsp.tld" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<span id="${self.uuid}" zk_type="zul.html.widget.Map"${self.outerAttrs}>
<a href="${c:encodeURL('~./zul/html/imagemap-done.dsp')}?${self.uuid}" target="zk_hfr_"><img id="${self.uuid}!real" ismap="ismap"${self.innerAttrs}/></a>
	<c:if test="${!empty self.children}"/>
<map name="${self.uuid}_map">
	<c:forEach var="child" items="${self.children}">
	${u:redraw(child, null)}
	</c:forEach>
</map>
	</c:if>
</span>
