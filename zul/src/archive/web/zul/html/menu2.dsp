<%--
menu2.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		New trendy mold for Menu component
	History:
		Tue May 20 12:56:37 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:choose>
<c:when test="${self.topmost}">
<td id="${self.uuid}" align="left" z.type="zul.menu2.Menu2"${self.outerAttrs}${self.innerAttrs}>
<table id="${self.uuid}!a" cellspacing="0" cellpadding="0" border="0" class="z-btn <c:if test="${self.imageAssigned}">z-btn<c:if test="${!empty self.label}">-text</c:if>-icon</c:if>" style="width: auto;">
<tbody><tr class="z-menu-btn"><td class="z-btn-l"><i>&nbsp;</i></td>
<c:if test="${!empty self.imageContent}">
	<c:set var="imagesrc" value="background-image:url(${self.contentSrc})"/>
</c:if>
<c:if test="${!empty self.src}">
	<c:set var="imagesrc" value="background-image:url(${c:encodeURL(self.src)})"/>
</c:if>
<td class="z-btn-m"><em unselectable="on"><button id="${self.uuid}!b" type="button" class="z-btn-text" style="${imagesrc}"><c:out value="${self.label}"/></button>
${z:redraw(self.menupopup, null)}
</em>
</td>
<td class="z-btn-r"><i>&nbsp;</i></td>
</tr>
</tbody>
</table>
</td>
</c:when>
<c:otherwise>
<li id="${self.uuid}" z.type="zul.menu2.Menu2"${self.outerAttrs}${self.innerAttrs}>
<a href="javascript:;" id="${self.uuid}!a" class="${self.sclass}-item ${self.sclass}-item-arrow">${self.imgTag}<c:out value="${self.label}"/></a>${z:redraw(self.menupopup, null)}
</li>
</c:otherwise>
</c:choose>