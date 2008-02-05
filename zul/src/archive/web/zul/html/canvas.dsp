<%--
area.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Feb 04 14:54:59     2008, Created by willychiang
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<canvas id="${self.uuid}"${self.outerAttrs} z.type="zul.canvas.Canvas" width="${self.width}" height="${self.height}"></canvas>

 