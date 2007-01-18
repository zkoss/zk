<%--
combobox.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Dec 16 09:14:06     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zk/core.dsp.tld" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/><%-- z.combo means an input with addition buttons --%>
<span id="${self.uuid}"${self.outerAttrs} z.type="zul.cb.Cmbox" z.combo="true"><input id="${self.uuid}!real" autocomplete="off"${self.innerAttrs}/><img id="${self.uuid}!btn" align="absmiddle" src="${c:encodeURL('~./zul/img/combobtn.gif')}"${self.buttonVisible?'':' style="display:none"'}/><div id="${self.uuid}!pp" class="comboboxpp" style="display:none" tabindex="-1">
 <table id="${self.uuid}!cave" cellpadding="0" cellspacing="0">
	<c:forEach var="child" items="${self.children}">
  ${z:redraw(child, null)}
	</c:forEach>
 </table>
</div></span>