<%--
popup.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun  5 11:03:53     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" z.type="zul.widget.Pop"${self.outerAttrs}${self.innerAttrs}>
<div class="z-pp-tl"><div class="z-pp-tr"><div class="z-pp-tm"></div></div></div>
<div id="${self.uuid}!bwrap" class="z-pp-bwrap"><div class="z-pp-cl"><div class="z-pp-cr"><div class="z-pp-cm">
<div id="${self.uuid}!cave" class="z-pp-body"><c:forEach var="child" items="${self.children}">${z:redraw(child, null)}</c:forEach></div></div></div></div><div class="z-pp-bl"><div class="z-pp-br"><div class="z-pp-bm"></div></div></div></div></div>