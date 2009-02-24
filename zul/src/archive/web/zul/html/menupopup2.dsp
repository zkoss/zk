<%--
menupopup2.dsp

{{IS_NOTE
	Purpose:

	Description:
		New trendy mold for Menupopup component
	History:
		Fri May 23 14:35:44 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}>
<${c:browser('ie') || c:browser('gecko') ? 'a' : 'button'} id="${self.uuid}!a" tabindex="-1" onclick="return false;" href="javascript:;" style="padding:0 !important; margin:0 !important; border:0 !important;	background: transparent !important;	font-size: 1px !important; width: 1px !important; height: 1px !important;-moz-outline: 0 none; outline: 0 none;	-moz-user-select: text; -khtml-user-select: text;"></${c:browser('ie') || c:browser('gecko') ? 'a' : 'button'}>
<ul class="${self.zclass}-cnt" id="${self.uuid}!cave">
	<c:forEach var="child" items="${self.children}">
${z:redraw(child, null)}
	</c:forEach>
</ul>
</div>