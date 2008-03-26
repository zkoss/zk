<%--
bandbox.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Mar 20 12:43:40     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/><%-- z.combo means an input with addition buttons --%>
<span id="${self.uuid}"${self.outerAttrs} z.type="zul.cb.Bdbox" z.combo="true"><input id="${self.uuid}!real" class="${self.sclass}inp" autocomplete="off"${self.innerAttrs}/><span id="${self.uuid}!btn" class="rbtnbk"${self.buttonVisible?'':' style="display:none"'}><img src="${c:encodeURL(self.image)}"/></span><div id="${self.uuid}!pp" class="${self.sclass}pp" style="display:none" tabindex="-1">${z:redraw(self.dropdown, null)}</div></span>