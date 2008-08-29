<%--
spinner.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Mar  5 09:26:51 TST 2008, Created by gracelin
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="mcls" value="${self.moldSclass}"/>
<span id="${self.uuid}"${self.outerAttrs} z.type="zul.spinner.Spinner" z.combo="true"><input id="${self.uuid}!real" class="${mcls}-inp" autocomplete="off"${self.innerAttrs}/><span id="${self.uuid}!btn" class="${mcls}-btn"${self.buttonVisible?'':' style="display:none"'}><img class="${mcls}-img" <c:if test="${!empty self.image}">style="background-image:url(${c:encodeURL(self.image)})"</c:if> src="${c:encodeURL('~./img/spacer.gif')}"/></span></span>