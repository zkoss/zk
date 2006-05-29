<%--
combobox.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Dec 16 09:14:06     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/web/html.dsp.tld" prefix="h" %>
<%@ taglib uri="/WEB-INF/tld/zul/core.dsp.tld" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/><%-- zk_combo means an input with addition buttons --%>
<span id="${self.uuid}"${self.outerAttrs} zk_type="zul.html.cb.Cmbox" zk_combo="true"><input class="combobox" id="${self.uuid}!real"${self.innerAttrs}/><h:img id="${self.uuid}!btn" align="top" src="~./zul/img/combobtn.gif"/><div id="${self.uuid}!pp" class="comboboxpp" style="display:none" tabindex="-1">
 <table id="${self.uuid}!pp2" cellpadding="0" cellspacing="0">
	<c:forEach var="child" items="${self.children}">
  ${u:redraw(child, null)}
	</c:forEach>
 <tr id="${self.uuid}!child" style="display:none"><td></td><td></td></tr><%-- bookmark for adding children --%>
 </table>
</div></span>