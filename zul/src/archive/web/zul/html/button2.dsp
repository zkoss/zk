<%--
button2.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		In Safari, the shape of <button> is not pleasure, so we use
		<input> if no image is required
	History:
		Wed Aug  4 10:19:50     2008, Created by robbiecheng
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
<span z.type="zul.widget.Button" id="${self.uuid}" class="${mcls}"${z:noCSSAttrs(self.outerAttrs)}><table id="${self.uuid}!box" cellspacing="0" cellpadding="0" border="0"${z:outCSSAttrs(self.outerAttrs)}${self.innerAttrs}>
<tr>
	<td class="${mcls}-tl"></td>
	<td class="${mcls}-tm"></td>
	<td class="${mcls}-tr"></td>
</tr>
<tr>
	<td class="${mcls}-cl"><button id="${self.uuid}!real" class="z ${mcls}" <c:if test="${self.tabindex >= 0}">tabindex="${self.tabindex}"</c:if></button></td>
	<td class="${mcls}-cm"><c:choose trim="true">
	<c:when test="${self.dir == 'reverse'}">
		<c:out value="${self.label}"/><c:if test="${self.imageAssigned and self.orient == 'vertical'}"><br/></c:if>${self.imgTag}
	</c:when>
	<c:otherwise>
		${self.imgTag}<c:if test="${self.imageAssigned and self.orient == 'vertical'}"><br/></c:if><c:out value="${self.label}"/>
	</c:otherwise>
	</c:choose></td>
	<td class="${mcls}-cr"><i class="z ${mcls}"></i></td>
</tr>
<tr>
	<td class="${mcls}-bl"></td>
	<td class="${mcls}-bm"></td>
	<td class="${mcls}-br"></td>
</tr>
</table></span>