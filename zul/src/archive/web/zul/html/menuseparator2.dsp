<%--
menuseparator2.dsp

{{IS_NOTE
	Purpose:

	Description:
		New trendy mold for Menuseparator component
	History:
		Fri May 23 14:39:14 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="tagnm" value="${self.popup ? 'li' : 'td' }"/>
<${tagnm} id="${self.uuid}" z.type="Menusp2"${self.outerAttrs}${self.innerAttrs}><span class="${self.zclass}-inner">&nbsp;</span></${tagnm}>