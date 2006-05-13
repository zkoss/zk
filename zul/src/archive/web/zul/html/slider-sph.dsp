<%--
slider-sph.dsp

{{IS_NOTE
	$Id: slider-sph.dsp,v 1.7 2006/03/17 10:06:34 tomyeh Exp $
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
<table id="${self.uuid}"${self.outerAttrs}${self.innerAttrs} zk_type="zul.html.sld.Sld" cellpadding="0" cellspacing="0">
<tr height="20">
 <td width="4" background="${c:encodeURL('~./zul/img/slider/bklsph.gif')}"></td>
 <td background="${c:encodeURL('~./zul/img/slider/bksph.gif')}"><h:img id="${self.uuid}!btn" src="~./zul/img/slider/btnsph.gif" title="${c:string(self.curpos)}"/></td>
 <td width="4" background="${c:encodeURL('~./zul/img/slider/bkrsph.gif')}"></td>
</table>
