<%--
menuitem2.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		New trendy mold for Menuitem component
	History:
		Fri May 23 14:35:44 TST 2008, Created by jumperchen
		modify by dennis 20090217
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="zcls" value="${self.zclass}"/>
<c:choose>
<c:when test="${self.topmost}">
<td id="${self.uuid}" align="left" z.type="Menuit2"${self.outerAttrs}${self.innerAttrs}>
<a href="${empty self.href?'javascript:;':c:encodeURL(self.href)}"${c:attr('target',self.target)} class="${zcls}-cnt">
<table id="${self.uuid}!a" cellspacing="0" cellpadding="0" border="0" class="${zcls}-body <c:if test="${self.imageAssigned}">${zcls}-body<c:if test="${!empty self.label}">-text</c:if>-img</c:if>" style="width: auto;">
<tbody><tr><td class="${zcls}-inner-l"></td>
<c:set var="imagesrc" value="${self.encodedImageURL}"/>
<c:set var="imagesrc" value='style="background-image:url(${imagesrc})"' unless="${empty imagesrc}"/>
<td class="${zcls}-inner-m"><div><button id="${self.uuid}!b" type="button" class="${zcls}-btn"${imagesrc}><c:out value="${self.label}"/>&nbsp</button>
</div>
</td>
<td class="${zcls}-inner-r"></td>
</tr>
</tbody>
</table></a></td>
</c:when>
<c:otherwise>
<li id="${self.uuid}" z.type="Menuit2"${self.outerAttrs}${self.innerAttrs}>
<c:choose>
	<c:when test="${!self.imageAssigned && self.checkmark}">
		<c:choose>
			<c:when test="${self.checked}">
				<c:set var="class1" value="${zcls}-cnt ${zcls}-cnt-ck"/>
			</c:when>
			<c:otherwise>
				<c:set var="class1" value="${zcls}-cnt ${zcls}-cnt-unck"/>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<c:set var="class1" value="${zcls}-cnt"/>
	</c:otherwise>
</c:choose>
<a href="${empty self.href?'javascript:;':c:encodeURL(self.href)}"${c:attr('target',self.target)} id="${self.uuid}!a" class="${class1}">${self.imgTag}<c:out value="${self.label}"/></a>
</li>
</c:otherwise>
</c:choose>