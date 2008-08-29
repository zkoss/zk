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
<c:set var="mcls" value="${self.moldSclass}"/>
<div id="${self.uuid}" z.type="zul.widget.Pop"${self.outerAttrs}${self.innerAttrs}>
<div class="${mcls}-tl"><div class="${mcls}-tr"><div class="${mcls}-tm"></div></div></div>
<div id="${self.uuid}!bwrap" class="${mcls}-body"><div class="${mcls}-cl"><div class="${mcls}-cr"><div class="${mcls}-cm">
<div id="${self.uuid}!cave" class="${mcls}-content"><c:forEach var="child" items="${self.children}">${z:redraw(child, null)}</c:forEach></div></div></div></div><div class="${mcls}-bl"><div class="${mcls}-br"><div class="${mcls}-bm"></div></div></div></div></div>