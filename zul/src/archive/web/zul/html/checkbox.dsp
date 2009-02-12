<%--
checkbox.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun 16 23:48:26     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<span id="${self.uuid}" z.type="zul.btn.Ckbox"${self.outerAttrs}><input type="checkbox" id="${self.uuid}!real"${self.innerAttrs}/><label for="${self.uuid}!real"${self.labelAttrs} class="${self.zclass}-cnt">${self.imgTag}<c:out value="${self.label}"/></label></span>
<%-- we have to group input and label so we can replace them together --%>