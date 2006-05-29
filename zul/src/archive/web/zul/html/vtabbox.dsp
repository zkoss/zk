<%--
vtabbox.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		Vertical orientation of tabbox
	History:
		May 3 2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zul/core.dsp.tld" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<table id="${self.uuid}"${self.outerAttrs}${self.innerAttrs} border="0" cellpadding="0" cellspacing="0">
<tr valign="top">
${u:redraw(self.tabs, null)}
${u:redraw(self.tabpanels, null)}
</tr>
</table>
