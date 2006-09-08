<%--
datebox.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul  5 09:15:23     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/web/html.dsp.tld" prefix="h" %>
<c:set var="self" value="${requestScope.arg.self}"/><%-- zk_combo means an input with addition buttons --%>
<span id="${self.uuid}"${self.outerAttrs} zk_type="zul.html.db.Dtbox" zk_combo="true"><input id="${self.uuid}!real" autocomplete="off"${self.innerAttrs}/><h:img id="${self.uuid}!btn" src="~./zul/img/caldrbtn.gif"/><div id="${self.uuid}!pp" class="dateboxpp" style="display:none" tabindex="-1"></div></span>