<%--
tabpanel-accd.dsp

{{IS_NOTE
	Purpose:
		A accordion-type tabpanel.
	Description:
		
	History:
		Tue Sep 27 15:02:30     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="tab" value="${self.linkedTab}"/>
<c:set var="suffix" value="-sel" if="${tab.selected}"/>
<c:set var="suffix" value="-uns" unless="${tab.selected}"/>
<c:set var="look" value="${self.tabbox.tabLook}-"/>
<c:set var="hghStyle" value="height:${tab.height}" unless="${empty tab.height}"/>
<div id="${self.uuid}"><%-- self.outerAttrs/innerAttrs gen below --%>
<table id="${tab.uuid}"${tab.outerAttrs}${tab.innerAttrs} z.sel="${tab.selected}" z.type="zul.tab.Tab" z.box="${tab.tabbox.uuid}" z.panel="${self.uuid}" width="100%" border="0" cellpadding="0" cellspacing="0" z.disabled="${tab.disabled}">
<c:if test="${!empty self.tabbox.panelSpacing and self.index!=0}">
 <tr height="${self.tabbox.panelSpacing}"><td></td></tr>
</c:if>
<tr>
	<td class="${c:cat3(look,'tl',suffix)}"></td>
	<td colspan="${tab.closable?4:3}" class="${c:cat3(look,'tm',suffix)}"></td>
	<td class="${c:cat3(look,'tr',suffix)}"></td>
</tr>
<tr class="${c:cat(look,'m')}"${c:attr('style',hghStyle)}>
	<td class="${c:cat3(look,'ml',suffix)}"></td>
	<td width="3" class="${c:cat3(look,'mm',suffix)}"></td>
	<td align="left" class="${c:cat3(look,'mm',suffix)}"><a href="javascript:;" id="${tab.uuid}!a">${tab.imgTag}<c:out value="${tab.label}"/></a></td>
<c:if test="${tab.closable}">
	<td width="11" align="right" class="${c:cat3(look,'mm',suffix)}"><img id="${tab.uuid}!close" src="${c:encodeURL('~./zul/img/close-off.gif')}"/></td>
</c:if>
	<td width="3" class="${c:cat3(look,'mm',suffix)}"></td>
	<td class="${c:cat3(look,'mr',suffix)}"></td>
</tr>
<tr>
	<td colspan="${tab.closable?6:5}" class="${c:cat(look,'b')}"></td>
</tr>
</table>
	<div id="${self.uuid}!real"${self.outerAttrs}${self.innerAttrs}><div id="${self.uuid}!cave">
<c:forEach var="child" items="${self.children}">
	${z:redraw(child, null)}
</c:forEach>
	</div></div>
</div>
