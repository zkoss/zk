<%--
slider.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 29 21:06:03     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/web/html.dsp.tld" prefix="h" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<table id="${self.uuid}"${self.outerAttrs}${self.innerAttrs} z:type="zul.sld.Sld" cellpadding="0" cellspacing="0">
<tr height="17">
 <td width="4" style="background-image:url(${c:encodeURL('~./zul/img/slider/bkl.gif')})"></td>
 <td style="background-image:url(${c:encodeURL('~./zul/img/slider/bk.gif')})"><h:img id="${self.uuid}!btn" src="~./zul/img/slider/btn.gif" title="${c:string(self.curpos)}"/></td>
 <td width="4" style="background-image:url(${c:encodeURL('~./zul/img/slider/bkr.gif')})"></td>
</table>
