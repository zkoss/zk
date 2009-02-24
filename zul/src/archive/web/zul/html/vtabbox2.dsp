<%--
vtabbox2.dsp

{{IS_NOTE
	Purpose:

	Description:
		Vertical orientation of tabbox
	History:
		Aug 18 2008, Created by Ryanwu
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}"${self.outerAttrs}${self.innerAttrs} z.type="zul.tab2.Tabbox2">
${z:redraw(self.tabs, null)}
${z:redraw(self.tabpanels, null)}
<div class="z-clear" ></div>
</div>