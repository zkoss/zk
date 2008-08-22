<%--
timebox2.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  5 16:08:30 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<span id="${self.uuid}"${self.outerAttrs} z.type="zul.tb.Tmbox" z.combo="true"><input id="${self.uuid}!real" class="${self.moldSclass}-inp" autocomplete="off"${self.innerAttrs}/><span id="${self.uuid}!btn" class="${self.moldSclass}-btn"${self.buttonVisible?'':' style="display:none"'}><img class="${self.moldSclass}-img" <c:if test="${!empty self.image}">style="background-image:url(${c:encodeURL(self.image)})"</c:if> src="${c:encodeURL('~./img/spacer.gif')}"/></span></span>