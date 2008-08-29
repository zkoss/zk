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
<c:set var="mcls" value="${self.moldSclass}"/>
<c:choose>
<c:when test="${self.topmost}">
<td id="${self.uuid}" align="left" z.type="Menuit2"${self.outerAttrs}${self.innerAttrs}>
<a href="${empty self.href?'javascript:;':c:encodeURL(self.href)}"${c:attr('target',self.target)} class="${mcls}-content">
<table id="${self.uuid}!a" cellspacing="0" cellpadding="0" border="0" class="${mcls}-btn <c:if test="${self.imageAssigned}">${mcls}-btn<c:if test="${!empty self.label}">-text</c:if>-img</c:if>" style="width: auto;">
<tbody><tr><td class="${mcls}-btn-l"><i>&nbsp;</i></td>
<c:if test="${!empty self.imageContent}">
	<c:set var="imagesrc" value="background-image:url(${self.contentSrc})"/>
</c:if>
<c:if test="${!empty self.src}">
	<c:set var="imagesrc" value="background-image:url(${c:encodeURL(self.src)})"/>
</c:if>
<td class="${mcls}-btn-m"><em unselectable="on"><button id="${self.uuid}!b" type="button" class="${mcls}-btn-text" style="${imagesrc}"><c:out value="${self.label}"/></button>
</em>
</td>
<td class="${mcls}-btn-r"><i>&nbsp;</i></td>
</tr>
</tbody>
</table></a></td>
</c:when>
<c:otherwise>
<li id="${self.uuid}" z.type="Menuit2"${self.outerAttrs}${self.innerAttrs}>
<c:choose>
	<c:when test="${!self.imageAssigned && self.parent.checkmark}">
		<c:choose>
			<c:when test="${self.checked}">
				<c:set var="class1" value="${mcls}-content ${mcls}-content-ck"/>
			</c:when>
			<c:otherwise>
				<c:set var="class1" value="${mcls}-content ${mcls}-content-unck"/>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<c:set var="class1" value="${mcls}-content"/>
	</c:otherwise>
</c:choose>
<a href="${empty self.href?'javascript:;':c:encodeURL(self.href)}"${c:attr('target',self.target)} id="${self.uuid}!a" class="${class1}">${self.imgTag}<c:out value="${self.label}"/></a>
</li>
</c:otherwise>
</c:choose>