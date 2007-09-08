<%--
groupbox-3d.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 11 14:32:34     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="tscls" value="${self.captionSclass}-"/>
<table id="${self.uuid}" z.type="zul.widget.Grbox"${self.outerAttrs}${self.innerAttrs}>
<tr valign="top">
	<td>
<c:if test="${!empty self.caption}">
<table width="100%" border="0" cellpadding="0" cellspacing="0">
<tr>
	<td class="${c:cat(tscls,'tl')}"></td>
	<td colspan="3" class="${c:cat(tscls,'tm')}"></td>
	<td class="${c:cat(tscls,'tr')}"></td>
</tr>
<tr height="22">
	<td class="${c:cat(tscls,'ml')}"></td>
	<td width="3" class="${c:cat(tscls,'mm')}"></td>
	<td class="${c:cat(tscls,'mm')}">${z:redraw(self.caption, null)}</td>
	<td width="3" class="${c:cat(tscls,'mm')}"></td>
	<td class="${c:cat(tscls,'mr')}"></td>
</tr>
<tr>
	<td colspan="5" class="${c:cat(tscls,'b')}"></td>
</tr>
</table>
</c:if>
<c:set var="gcExtStyle" value="${c:cat(empty self.caption ? '': 'border-top:0;', self.contentStyle)}"/>
	<div id="${self.uuid}!slide"${self.open?'':' style="display:none"'}><div id="${self.uuid}!cave" class="${self.contentSclass}"${c:attr('style',gcExtStyle)}>
	<c:forEach var="child" items="${self.children}">
	<c:if test="${self.caption != child}">${z:redraw(child, null)}</c:if>
	</c:forEach>
	</div></div>
<%-- shadow --%>
<table id="${self.uuid}!sdw" border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
 <td class="${c:cat(tscls,'shdl')}"></td>
 <td class="${c:cat(tscls,'shdm')}"><img width="1" height="1" src="${c:encodeURL('~./img/spacer.gif')}"/></td><%-- it must have something --%>
 <td class="${c:cat(tscls,'shdr')}"></td>
</tr>
</table>
	</td>
</tr>
</table>
