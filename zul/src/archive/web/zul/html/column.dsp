<%--
column.dsp

{{IS_NOTE
	$Id: column.dsp,v 1.7 2006/05/05 01:44:43 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Mon Jan  9 14:50:13     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<td id="${self.uuid}" zk_type="Col"${self.outerAttrs}${self.innerAttrs}>${self.imgTag}<c:out value="${self.label}"/><img id="${self.uuid}!hint" src="${c:encodeURL('~./zul/img/sort/hint.gif')}" style="display:none"/></td>
