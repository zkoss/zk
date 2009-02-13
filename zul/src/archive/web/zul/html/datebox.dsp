<%--
datebox.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul  5 09:15:23     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/><%-- z.combo means an input with addition buttons --%>
<span id="${self.uuid}"${self.outerAttrs} z.type="zul.db.Dtbox" z.combo="true"><input id="${self.uuid}!real" class="${self.sclass}inp" autocomplete="off"${self.innerAttrs}/><span id="${self.uuid}!btn" class="rbtnbk"${self.buttonVisible?'':' style="display:none"'}><img src="${c:encodeURL(self.image)}"/></span><div id="${self.uuid}!pp" class="${self.sclass}pp" style="display:none" tabindex="-1"></div></span>