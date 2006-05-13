<%--
tabpanel-accd.dsp

{{IS_NOTE
	$Id: tabpanel-accd.dsp,v 1.8 2006/05/04 11:21:05 tomyeh Exp $
	Purpose:
		A accordion-type tabpanel.
	Description:
		
	History:
		Tue Sep 27 15:02:30     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zul/core.dsp.tld" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<c:set var="tab" value="${self.linkedTab}"/>
<c:set var="suffix" value="-sel.gif" if="${tab.selected}"/>
<c:set var="suffix" value="-uns.gif" unless="${tab.selected}"/>
<tr id="${self.uuid}"><%-- no exteriorAttribute here because tab.js controls it diff --%>
<td>
<table id="${tab.uuid}" zk_sel="${tab.selected}" zk_type="zul.html.tab.Tab" zk_box="${tab.tabbox.uuid}" zk_panel="${tab.linkedPanel.uuid}" width="100%" border="0" cellpadding="0" cellspacing="0">
<c:if test="${!empty self.tabbox.panelSpacing and self.index!=0}"><tr height="${self.tabbox.panelSpacing}"><td></td></tr></c:if>
<tr>
	<td width="5" height="5" background="${c:encodeURL(c:cat('~./zul/img/tab/3d-tl',suffix))}"></td>
	<td height="5" colspan="3" background="${c:encodeURL(c:cat('~./zul/img/tab/3d-tm',suffix))}"></td>
	<td width="5" height="5" background="${c:encodeURL(c:cat('~./zul/img/tab/3d-tr',suffix))}"></td>
</tr>
<tr height="22">
	<td width="5" background="${c:encodeURL(c:cat('~./zul/img/tab/3d-ml',suffix))}"></td>
	<td width="3" background="${c:encodeURL(c:cat('~./zul/img/tab/3d-mm',suffix))}"></td>
	<td align="left" background="${c:encodeURL(c:cat('~./zul/img/tab/3d-mm',suffix))}" id="${tab.uuid}!real"${tab.outerAttrs}${tab.innerAttrs}><a href="javascript:;" id="${tab.uuid}!a">${tab.imgTag} <c:out value="${tab.label}"/></a></td>
	<td width="3" background="${c:encodeURL(c:cat('~./zul/img/tab/3d-mm',suffix))}"></td>
	<td width="5" background="${c:encodeURL(c:cat('~./zul/img/tab/3d-mr',suffix))}"></td>
</tr>
<tr height="1">
	<td colspan="5" background="${c:encodeURL('~./zul/img/tab/3d-b.gif')}"></td>
</tr>
</table>
	<div id="${self.uuid}!real"${self.outerAttrs}${self.innerAttrs}><div class="tabpanel-ac">
<c:forEach var="child" items="${self.children}">
	${u:redraw(child, null)}
</c:forEach>
	<span style="display:none" id="${self.uuid}!child"></span><%-- bookmark for adding child (when insertHTMLBeforeEnd not appliable) --%>
	</div></div>
</td>
</tr>
