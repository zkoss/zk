<%--
bandbox.dsp

{{IS_NOTE
	$Id: bandbox.dsp,v 1.2 2006/03/29 10:00:14 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Mon Mar 20 12:43:40     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zul/core.dsp.tld" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/><%-- zk_combo means an input with addition buttons --%>
<span id="${self.uuid}"${self.outerAttrs} zk_type="zul.html.cb.Bdbox" zk_combo="true"><input class="bandbox" id="${self.uuid}!real"${self.innerAttrs}/><img id="${self.uuid}!btn" align="top" src="${c:encodeURL(self.image)}"/><div id="${self.uuid}!pp" class="bandboxpp" style="display:none" tabindex="-1">${u:redraw(self.popup, null)}</div></span>