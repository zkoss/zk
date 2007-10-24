<%--
auxheader.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 24 11:10:06     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<th id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}><div id="${self.uuid}!cave" class="head-cell-inner">${self.imgTag}<c:out value="${self.label}"/></div></th>
