<%--
audio.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Nov 16 17:04:52     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<embed id="${self.uuid}"${self.outerAttrs}${self.innerAttrs} mastersound z.type="zul.audio.Audio" enablejavascript="true"/>