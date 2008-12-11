<%@ page contentType="application/x-javascript;charset=UTF-8" %><%--
zk.js.dsp

	Purpose:
		
	Description:
		Integrate all common js into one file
	History:
		Oct 24 2007 	Dennis Chen

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
if (!window.zk) {
<c:include page="zk.js"/>
<c:include page="lang/mesg*.js"/>
<c:include page="utl.js"/>
<c:include page="dom.js"/>
<c:include page="evt.js"/>
<c:include page="drag.js"/>
<c:include page="effect.js"/>
<c:include page="anima.js"/>
<c:include page="widget.js"/>
<c:include page="pkg.js"/>
<c:include page="mount.js"/>
<c:include page="history.js"/>
<c:include page="au.js"/>

${z:outLocaleJavaScript()}
}