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
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<c:set var="self" value="${requestScope.arg.self}"/><%-- z.combo means an input with addition buttons --%>
<span id="${self.uuid}"${self.outerAttrs} z.type="zul.tb.Tmbox" z.combo="true"><input id="${self.uuid}!real" class="${self.sclass}inp" autocomplete="off"${self.innerAttrs}/><span id="${self.uuid}!btn" class="rbtnbk"${self.buttonVisible?'':' style="display:none"'}><img src="${c:encodeURL(self.image)}"/></span></span>