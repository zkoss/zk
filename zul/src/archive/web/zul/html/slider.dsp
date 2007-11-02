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
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<table id="${self.uuid}"${self.outerAttrs}${self.innerAttrs} z.type="zul.sld.Sld" cellpadding="0" cellspacing="0">
<tr height="17">
 <td class="slider-bkl"></td>
 <td class="slider-bk"><span id="${self.uuid}!btn" class="slider-btn" title="${c:string(self.curpos)}"/></td>
 <td class="slider-bkr"></td>
</tr>
</table>
