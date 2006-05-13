<%--
treecol.dsp

{{IS_NOTE
	$Id: treecol.dsp,v 1.7 2006/05/05 01:44:44 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Thu Jul  7 15:33:36     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<td id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}>${self.imgTag}<c:out value="${self.label}"/></td>
