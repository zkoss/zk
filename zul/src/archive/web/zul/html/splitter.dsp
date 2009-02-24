<%--
splitter.dsp

{{IS_NOTE
	Purpose:

	Description:

	History:
		Thu Jun  8 15:11:44     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" z.type="zul.box.Splt"${self.outerAttrs}${self.innerAttrs}><span id="${self.uuid}!btn" style="display:none"></span></div>