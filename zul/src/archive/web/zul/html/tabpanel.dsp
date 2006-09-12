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
		Tue Jul 12 10:58:38     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zk/core.dsp.tld" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<tr id="${self.uuid}"${self.outerAttrs}>
<td id="${self.uuid}!real" class="tabpanel-hr"${self.innerAttrs}>
<c:forEach var="child" items="${self.children}">
	${z:redraw(child, null)}
</c:forEach>
</td>
</tr>
