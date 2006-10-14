<%@ page contentType="text/html;charset=UTF-8" %><%--
iframe.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 21 11:15:37     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<iframe id="${self.uuid}" frameborder="0"${self.outerAttrs}${self.innerAttrs}>
Your browser doesn't support inline frames.
</iframe>
