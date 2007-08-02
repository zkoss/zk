<%--
timebox.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jul 9, 2007 10:03:38 AM , Created by Dennis Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/><%-- z.combo means an input with addition buttons --%>
<span id="${self.uuid}"${self.outerAttrs} z.type="zul.tb.Tmbox" z.combo="true"><input id="${self.uuid}!real" autocomplete="off"${self.innerAttrs} /><span id="${self.uuid}!btn" class="rbtnbk" ${self.buttonVisible?'':' style="display:none"'}><table cellpadding="0" cellspacing="0" border="0" style="display:inline">
<tr>
	<td id="${self.uuid}!up"><img src="${c:encodeURL('~./zul/img/upbtn.gif')}"/></td>
</tr>
<tr>
	<td id="${self.uuid}!dn"><img src="${c:encodeURL('~./zul/img/dnbtn.gif')}"/></td>
</tr>
</table></span></span>