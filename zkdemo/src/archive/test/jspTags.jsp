<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%--
test.jsp

{{IS_NOTE
	Purpose:
		Test of zuljsp
	Description:
		
	History:
		Mon Jul 23 10:51:25     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/jsp/zul" prefix="z" %>

<html>
	<head>
		<title>Test of ZUL on JSP</title>
		<z:zkhead/>
	</head>
	<body>

	<h1>1. Header outside z:page</h1>
<z:page>
	<h2>2. Header in z:page</h2>

	<z:window id="win" title="Test" border="normal" width="300px">
		<p>3. Content in z:window</p>
		<z:button label="Overlap" onClick="win.doOverlapped()"/>
		<z:button label="Embed" onClick="win.doEmbedded()"/>
	</z:window>
	<jsp:include page="jspTags-inc.jsp"/>
		<p>4. Content in z:page after z:window</p>
</z:page>

	<p>5. Content after z:page</p>
	</body>
</html>
