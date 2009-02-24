<%--
hmenubar2.dsp

{{IS_NOTE
	Purpose:

	Description:
		New trendy mold for Menubar component
	History:
		Thu May 22 17:14:08 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" z.type="zul.menu2.Menubar2"${self.outerAttrs}${self.innerAttrs}>
<table cellpadding="0" cellspacing="0" border="0">
<tr valign="bottom" id="${self.uuid}!cave">
	<c:forEach var="child" items="${self.children}">
	${z:redraw(child, null)}
	</c:forEach>
</tr>
</table>
</div>