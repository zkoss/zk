<%--
tabpanel.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		Border cannot be specified in <tr>, so we cannot specify style
		in <style-class> in zk-xul-html.xml.
		To customize it, user could specify a class for this tabpanel
		and then, in CSS, specify tr.yourClass td.tabpanel {...}

	History:
		Tue Jul 12 10:58:38     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}"${self.outerAttrs} z.type="zul.tab.Tabpanel">
<div id="${self.uuid}!real"${self.innerAttrs}>
<c:forEach var="child" items="${self.children}">
	${z:redraw(child, null)}
</c:forEach>
</div>
</div>
