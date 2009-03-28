<%--
flash.dsp

{{IS_NOTE
	Purpose:

	Description:

	History:
		Tue Jul 24 12:01:12     2007, Created by jeffliu
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="encsrc" value="${self.encodedSrc}"/>
<div id="${self.uuid}" ${self.outerAttrs} z.type="zul.flash.Flash">
<object id="${self.uuid}!obj" width="${self.width}" height="${self.height}">
<param name="movie" value="${encsrc}"></param>
<param name="wmode" value="${self.wmode}"></param>
<embed id="${self.uuid}!emb" src="${encsrc}" type="application/x-shockwave-flash" wmode="${self.wmode}" width="${self.width}" height="${self.height}">
</embed>
</object>
</div>