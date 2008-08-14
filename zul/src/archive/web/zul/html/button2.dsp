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
<span id="${self.uuid}" class="${self.moldSclass}" z.type="zul.widget.Button">
<table id="${self.uuid}!real" cellspacing="0" cellpadding="0" border="0" ${self.outerAttrs}${self.innerAttrs}>
<tbody>
<tr>
	<td class="${self.moldSclass}-tl"></td>
	<td colspan="3" class="${self.moldSclass}-tm"></td>
	<td class="${self.moldSclass}-tr"></td>
</tr>
<tr>
	<td class="${self.moldSclass}-ml"><i>&#160;</i></td>
	<td colspan="3" class="${self.moldSclass}-mm"><em unselectable="on"><a id="${self.uuid}!btn" class="${self.moldSclass}" href="javascript:;">
	<c:choose trim="true">
	<c:when test="${self.dir == 'reverse'}">
		<c:out value="${self.label}"/><c:if test="${self.imageAssigned and self.orient == 'vertical'}"><br/></c:if>${self.imgTag}
	</c:when>
	<c:otherwise>
		${self.imgTag}<c:if test="${self.imageAssigned and self.orient == 'vertical'}"><br/></c:if><c:out value="${self.label}"/>
	</c:otherwise>
	</c:choose>			
	</a></em>
	</td>
	<td class="${self.moldSclass}-mr"><i>&#160;</i></td>
</tr>
<tr>
	<td class="${self.moldSclass}-bl"></td>
	<td colspan="3" class="${self.moldSclass}-bm"></td>
	<td class="${self.moldSclass}-br"></td>
</tr>
</tbody>
</table>
</span>