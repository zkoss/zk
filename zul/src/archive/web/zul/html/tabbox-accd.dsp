<%--
accordions.dsp

{{IS_NOTE
	$Id: tabbox-accd.dsp,v 1.3 2006/03/17 10:06:34 tomyeh Exp $
	Purpose:
		A accordion-type tabbox.
	Description:
		
	History:
		Tue Sep 27 14:45:27     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zul/core.dsp.tld" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<table id="${self.uuid}"${self.outerAttrs}${self.innerAttrs} zk_accd="true" border="0" cellpadding="0" cellspacing="0">
${u:redraw(self.tabpanels, null)}
</table>
