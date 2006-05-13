<%--
radiogroup.dsp

{{IS_NOTE
	$Id: radiogroup.dsp,v 1.8 2006/03/17 10:06:33 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Fri Jun 17 09:26:15     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<span id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}>
	<c:forEach var="child" items="${self.children}">
<span id="${child.uuid}" zk_type="zul.html.widget.Radio"${child.outerAttrs}><input type="radio" id="${child.uuid}!real"${child.innerAttrs}/><label for="${child.uuid}!real"${child.labelAttrs}>${child.imgTag} <c:out value="${child.label}"/></label><c:if test="${self.orient=='vertical'}"><br/></c:if></span>
	</c:forEach>
</span>
