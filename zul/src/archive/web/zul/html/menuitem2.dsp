<%--
menuitem2.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		New trendy mold for Menuitem component
	History:
		Fri May 23 14:35:44 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:choose>
<c:when test="${self.topmost}">
<td id="${self.uuid}" align="left" z.type="Menuit2"${self.outerAttrs}${self.innerAttrs}>
<a href="${empty self.href?'javascript:;':c:encodeURL(self.href)}"${c:attr('target',self.target)} class="z-menu-item">
<table id="${self.uuid}!a" cellspacing="0" cellpadding="0" border="0" class="z-btn <c:if test="${self.imageAssigned}">z-btn<c:if test="${!empty self.label}">-text</c:if>-icon</c:if>" style="width: auto;">
<tbody><tr class="z-menu-btn"><td class="z-btn-l"><i></i></td>
<c:if test="${!empty self.imageContent}">
	<c:set var="imagesrc" value="background-image:url(${self.contentSrc})"/>
</c:if>
<c:if test="${!empty self.src}">
	<c:set var="imagesrc" value="background-image:url(${c:encodeURL(self.src)})"/>
</c:if>
<td class="z-btn-m"><em unselectable="on"><button id="${self.uuid}!b" type="button" class="z-btn-text" style="${imagesrc}"><c:out value="${self.label}"/></button>
</em>
</td>
<td class="z-btn-r"><i></i></td>
</tr>
</tbody>
</table></a></td>
</c:when>
<c:otherwise>
<li id="${self.uuid}" z.type="Menuit2"${self.outerAttrs}${self.innerAttrs}>
<a href="${empty self.href?'javascript:;':c:encodeURL(self.href)}"${c:attr('target',self.target)} id="${self.uuid}!a" class="${self.parent.checkmark ? self.checked ? 'z-menu-item z-menu-item-ck' : 'z-menu-item z-menu-item-unck' : 'z-menu-item'} ${self.disabled ? 'z-item-disd' : ''}">${self.imgTag}<c:out value="${self.label}"/></a>
</li>
</c:otherwise>
</c:choose>