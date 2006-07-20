<%--
groupbox-3d.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 11 14:32:34     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/web/html.dsp.tld" prefix="h" %>
<%@ taglib uri="/WEB-INF/tld/zk/core.dsp.tld" prefix="u" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<table id="${self.uuid}" zk_type="zul.html.widget.Grbox"${self.outerAttrs}${self.innerAttrs}>
<tr>
	<td>
<c:if test="${! empty self.caption}">
<table width="100%" border="0" cellpadding="0" cellspacing="0">
<tr>
	<td width="5" height="5" style="background-image:url(${c:encodeURL('~./zul/img/tab/3d-tl-uns.gif')})"></td>
	<td height="5" colspan="3" style="background-image:url(${c:encodeURL('~./zul/img/tab/3d-tm-uns.gif')})"></td>
	<td width="5" height="5" style="background-image:url(${c:encodeURL('~./zul/img/tab/3d-tr-uns.gif')})"></td>
</tr>
<tr height="22">
	<td width="5" style="background-image:url(${c:encodeURL('~./zul/img/tab/3d-ml-uns.gif')})"></td>
	<td width="3" style="background-image:url(${c:encodeURL('~./zul/img/tab/3d-mm-uns.gif')})"></td>
	<td style="background-image:url(${c:encodeURL('~./zul/img/tab/3d-mm-uns.gif')})">${u:redraw(self.caption, null)}</td>
	<td width="3" style="background-image:url(${c:encodeURL('~./zul/img/tab/3d-mm-uns.gif')})"></td>
	<td width="5" style="background-image:url(${c:encodeURL('~./zul/img/tab/3d-mr-uns.gif')})"></td>
</tr>
<tr height="1">
	<td colspan="5" style="background-image:url(${c:encodeURL('~./zul/img/tab/3d-b.gif')})"></td>
</tr>
</table>
</c:if>
	<div id="${self.uuid}!slide"${self.open?'':' style="display:none"'} class="groupbox-3d"><div<c:if test="${empty self.caption}"> style="border: 1px solid #5C6C7C"</c:if>>
	<c:forEach var="child" items="${self.children}">
	<c:if test="${self.caption != child}">${u:redraw(child, null)}</c:if>
	</c:forEach>
	<span style="display:none" id="${self.uuid}!child"></span><%-- bookmark for adding child (when insertHTMLBeforeEnd not appliable) --%>
	</div></div>
<%-- shadow --%>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
 <td width="6"><h:img width="6" height="6" src="~./img/shdlf.gif"/></td>
 <td style="background-image:url(${c:encodeURL('~./img/shdmd.gif')})"><h:img width="1" height="1" src="~./img/spacer.gif"/></td>
 <td width="6"><h:img width="6" height="6" src="~./img/shdrg.gif"/></td>
</tr>
</table>
	</td>
</tr>
</table>
