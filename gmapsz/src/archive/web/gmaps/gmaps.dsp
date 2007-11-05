<%--
gmaps.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 12 15:02:32     2006, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zk/core.dsp.tld" prefix="z" %>
<c:set var="self" value="${requestScope.arg.self}"/>
<div id="${self.uuid}"${self.outerAttrs} z.type="gmapsz.gmaps.Gmaps">
<div id="${self.uuid}!real" style="width:100%;height:100%">
</div>
<div id="${self.uuid}!cave" style="display:none">
<c:forEach var="child" items="${self.children}">
${z:redraw(child, null)}
</c:forEach>
</div>
</div>
<%-- Due to the Gmaps limitation that we cannot create script element dynamically
	So, it is user's job to declare <script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=xxx"
	in a page that is not dynamically loaded.

<c:if test="${empty zk_gmapsKey}">
	<c:set var="zk_gmapsKey" value="true" scope="request"/>
	<span id="zk_gmapsKey" style="display:none" z.key="${self.key}"/>
</c:if>
--%>
