<%--
combobox2.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  5 09:37:38 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/><%-- z.combo means an input with addition buttons --%>
<span id="${self.uuid}"${self.outerAttrs} z.type="zul.cb.Cmbox" z.combo="true"><input id="${self.uuid}!real" class="${self.sclass}inp" autocomplete="off"${self.innerAttrs}/><span id="${self.uuid}!btn" class="z-rbtn"${self.buttonVisible?'':' style="display:none"'}><img class="${self.sclass}" <c:if test="${!empty self.image}">style="background-image:url(${c:encodeURL(self.image)})"</c:if> src="${c:encodeURL('~./img/spacer.gif')}"/></span><div id="${self.uuid}!pp" class="${self.sclass}pp" style="display:none" tabindex="-1">
 <table id="${self.uuid}!cave" cellpadding="0" cellspacing="0">
	<c:forEach var="child" items="${self.children}">
  ${z:redraw(child, null)}
	</c:forEach>
 </table>
</div></span>