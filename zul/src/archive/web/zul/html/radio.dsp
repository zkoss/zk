<%--
radio.dsp

{{IS_NOTE
	$Id: radio.dsp,v 1.8 2006/03/17 10:06:33 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Fri Jun 17 09:26:19     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<span id="${self.uuid}" zk_type="zul.html.widget.Radio"${self.outerAttrs}><input type="radio" id="${self.uuid}!real"${self.innerAttrs}/><label for="${self.uuid}!real"${self.labelAttrs}>${self.imgTag}<c:out value="${self.label}"/></label><c:if test="${self.parent.orient=='vertical'}"><br/></c:if></span>
<%-- we have to group input and label so we can replace them together --%>