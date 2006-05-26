<%--
listheader.dsp

{{IS_NOTE
	$Id: listheader.dsp,v 1.8 2006/05/26 10:08:19 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Aug  9 09:36:00     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<td id="${self.uuid}" zk_type="Lhr"${self.outerAttrs}${self.innerAttrs}>${self.imgTag}<c:out value="${self.label}"/><img id="${self.uuid}!hint" src="${c:encodeURL('~./zul/img/sort/hint.gif')}" style="display:none"/></td>
