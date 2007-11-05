<%--
gpolyline.dsp

{{IS_NOTE
	Purpose:
		Display Google Maps Info Window.
	Description:
		
	History:
		Thu Aug 09 18:09:13     2007, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<span id="${self.uuid}"${self.outerAttrs}${self.innerAttrs} z.type="gmapsz.gmaps.Gpoly">
</span>