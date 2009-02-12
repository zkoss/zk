<%--
fieldset.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 29 17:03:07     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<fieldset id="${self.uuid}" z.type="zul.widget.Grfs"${self.outerAttrs}${self.innerAttrs}>
${z:redraw(self.caption, null)}
<div id="${self.uuid}!cave" class="${self.contentSclass} ${self.zclass}-cnt"${c:attr('style', self.contentStyle)}>
<c:forEach var="child" items="${self.children}"><c:if test="${self.caption != child}">${z:redraw(child, null)}</c:if></c:forEach>
</div>
</fieldset>
