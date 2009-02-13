<%--
slider.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 29 21:06:03     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="scls" value="${self.sclass}"/>
<c:set var="scls" value="${'sphere' == self.mold ? 'slidersph': 'slider'}" if="${empty scls}"/>
<table id="${self.uuid}"${self.outerAttrs}${self.innerAttrs} z.type="zul.sld.Sld" cellpadding="0" cellspacing="0">
<tr>
 <td class="${scls}-bkl"></td>
 <td class="${scls}-bk"><span id="${self.uuid}!btn" class="${scls}-btn" title="${c:string(self.curpos)}"/></td>
 <td class="${scls}-bkr"></td>
</tr>
</table>
