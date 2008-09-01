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
<a id="${self.uuid}!a" tabindex="-1" onclick="return false;" href="javascript:;" style="position:absolute;left:0px;top:-5px;width:0px;height:0px;line-height:1px;"></a>
<ul class="${self.moldSclass}-cnt" id="${self.uuid}!cave">
	<c:forEach var="child" items="${self.children}">
${z:redraw(child, null)}
	</c:forEach>
</ul>
</div>