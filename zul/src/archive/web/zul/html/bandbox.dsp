<%--
bandbox.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Mar 20 12:43:40     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zk/core.dsp.tld" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/><%-- zk_combo means an input with addition buttons --%>
<span id="${self.uuid}"${self.outerAttrs} zk_type="zul.cb.Bdbox" zk_combo="true"><input id="${self.uuid}!real"${self.innerAttrs}/><img id="${self.uuid}!btn" src="${c:encodeURL(self.image)}"/><div id="${self.uuid}!pp" class="bandboxpp" style="display:none" tabindex="-1">${z:redraw(self.dropdown, null)}</div></span>