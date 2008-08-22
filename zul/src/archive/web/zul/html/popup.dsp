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
<div class="${self.moldSclass}-tl"><div class="${self.moldSclass}-tr"><div class="${self.moldSclass}-tm"></div></div></div>
<div id="${self.uuid}!bwrap" class="${self.moldSclass}-body"><div class="${self.moldSclass}-cl"><div class="${self.moldSclass}-cr"><div class="${self.moldSclass}-cm">
<div id="${self.uuid}!cave" class="${self.moldSclass}-content"><c:forEach var="child" items="${self.children}">${z:redraw(child, null)}</c:forEach></div></div></div></div><div class="${self.moldSclass}-bl"><div class="${self.moldSclass}-br"><div class="${self.moldSclass}-bm"></div></div></div></div></div>