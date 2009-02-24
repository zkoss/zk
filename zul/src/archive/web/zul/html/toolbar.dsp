<%--
toolbar.dsp

{{IS_NOTE
	Purpose:

	Description:

	History:
		Thu Jun 23 16:34:06     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="zcls" value="${self.zclass}"/>
<c:set var="break" value=""/>
<c:set var="verticalBreak" value="${self.orient == 'vertical' ? '<br/>': ''}"/>
<div id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}><div id="${self.uuid}!cave" class="${zcls}-body ${zcls}-${self.align}"><c:forEach var="child" items="${self.children}">${break}${z:redraw(child, null)}<c:set var="break" value="${verticalBreak}"/></c:forEach></div><div class="z-clear"></div></div>
