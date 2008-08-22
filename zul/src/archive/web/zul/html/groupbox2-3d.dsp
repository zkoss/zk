<%--
groupbox2-3d.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 17 12:38:05 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}" z.type="zul.widget.Grbox"${self.outerAttrs}${self.innerAttrs}>
<c:if test="${!empty self.caption}">
<div class="${self.moldSclass}-tl"><div class="${self.moldSclass}-tr"><div class="${self.moldSclass}-tm">
<div class="${self.moldSclass}-header">${z:redraw(self.caption, null)}</div>
</div></div></div>
</c:if>
<c:set var="gcExtStyle" value="${c:cat(empty self.caption ? '': 'border-top:0;', self.contentStyle)}"/>
	<div id="${self.uuid}!slide" class="${self.moldSclass}-body" ${self.open?'':' style="display:none"'}><div id="${self.uuid}!cave" class="${self.contentSclass} ${self.moldSclass}-content"${c:attr('style',gcExtStyle)}>
	<c:forEach var="child" items="${self.children}"><c:if test="${self.caption != child}">${z:redraw(child, null)}</c:if></c:forEach>
	</div></div>
<%-- shadow --%>
<div id="${self.uuid}!sdw" class="${self.moldSclass}-bl"><div class="${self.moldSclass}-br"><div class="${self.moldSclass}-bm"></div></div></div>
</div>