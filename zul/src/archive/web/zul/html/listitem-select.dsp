<%--
listitem-select.dsp

{{IS_NOTE
	$Id: listitem-select.dsp,v 1.3 2006/03/17 10:06:33 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Wed Sep 28 14:01:29     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<option id="${self.uuid}"${self.outerAttrs}${self.innerAttrs}><c:out value="${self.label}" maxlength="${self.maxlength}"/></option>
