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
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zk/core.dsp.tld" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="tab" value="${self.linkedTab}"/>
<c:set var="suffix" value="-sel.gif" if="${tab.selected}"/>
<c:set var="suffix" value="-uns.gif" unless="${tab.selected}"/>
<tr id="${self.uuid}"><%-- no exteriorAttribute here because tab.js controls it diff --%>
<td>
<table id="${tab.uuid}"${tab.outerAttrs}${tab.innerAttrs} z.sel="${tab.selected}" z.type="zul.tab.Tab" z.box="${tab.tabbox.uuid}" z.panel="${self.uuid}" width="100%" border="0" cellpadding="0" cellspacing="0">
<c:if test="${!empty self.tabbox.panelSpacing and self.index!=0}"><tr height="${self.tabbox.panelSpacing}"><td></td></tr></c:if>
<tr>
	<td width="5" height="5" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/3d-tl',suffix))})"></td>
	<td height="5" colspan="${tab.closable?4:3}" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/3d-tm',suffix))})"></td>
	<td width="5" height="5" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/3d-tr',suffix))})"></td>
</tr>
<tr height="22">
	<td width="5" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/3d-ml',suffix))})"></td>
	<td width="3" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/3d-mm',suffix))})"></td>
	<td align="left" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/3d-mm',suffix))})"><a href="javascript:;" id="${tab.uuid}!a">${tab.imgTag}<c:out value="${tab.label}"/></a></td>
<c:if test="${tab.closable}">
	<td width="11" align="right" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/3d-mm',suffix))})"><img id="${tab.uuid}!close" src="${c:encodeURL('~./zul/img/close-off.gif')}"/></td>
</c:if>
	<td width="3" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/3d-mm',suffix))})"></td>
	<td width="5" style="background-image:url(${c:encodeURL(c:cat('~./zul/img/tab/3d-mr',suffix))})"></td>
</tr>
<tr height="1">
	<td colspan="${tab.closable?6:5}" style="background-image:url(${c:encodeURL('~./zul/img/tab/3d-b.gif')})"></td>
</tr>
</table>
	<div id="${self.uuid}!real"${self.outerAttrs}${self.innerAttrs}><div id="${self.uuid}!cave">
<c:forEach var="child" items="${self.children}">
	${z:redraw(child, null)}
</c:forEach>
	</div></div>
</td>
</tr>
