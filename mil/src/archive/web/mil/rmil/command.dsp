<%--
label.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri June 01 12:02:51     2007, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<cm id="${self.uuid}" ${self.outerAttrs}${self.innerAttrs}/>
