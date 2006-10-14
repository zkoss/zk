<%--
doublebox.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Oct  14 13:25:12     2006, Created by henrichen@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<input id="${self.uuid}" z:type="zul.widget.Dbbox"${self.outerAttrs}${self.innerAttrs}/>