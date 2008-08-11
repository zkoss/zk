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
<c:set var="self" value="${requestScope.arg.self}"/>
<c:choose trim="true">
<table id="${self.uuid}" z.type="zul.widget.Button" cellspacing="0" cellpadding="0" border="0" class="z-btn" 
${self.outerAttrs}${self.innerAttrs}>
<tbody>
<tr>
	<td class="z-btn-tl"></td>
	<td colspan="3" class="z-btn-tm"></td>
	<td class="z-btn-tr"></td>
</tr>
<tr>
	<td class="z-btn-ml"><i>&#160;</i></td>
	<td colspan="3" class="z-btn-mm">
	<em unselectable="on">
	<c:choose trim="true">	
	<button id="${self.uuid}!b" type="button">
	<c:choose trim="true">
	<c:when test="${self.dir == 'reverse'}">
		<c:out value="${self.label}"/><c:if test="${self.imageAssigned and self.orient == 'vertical'}"><br/></c:if>${self.imgTag}
	</c:when>
	<c:otherwise>
		${self.imgTag}<c:if test="${self.imageAssigned and self.orient == 'vertical'}"><br/></c:if><c:out value="${self.label}"/>
	</c:otherwise>
	</c:choose>
	</button>
	</em>	
	</c:choose>		
	</td>
	<td class="z-btn-mr"><i>&#160;</i></td>
</tr>
<tr>
	<td class="z-btn-bl"></td>
	<td colspan="3" class="z-btn-bm"></td>
	<td class="z-btn-br"></td>
</tr>
</tbody>
</table>
</c:choose>


<!-- 

 -->
