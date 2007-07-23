<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%--
test.jsp

{{IS_NOTE
	Purpose:
		Test of zul2jsp
	Description:
		
	History:
		Mon Jul 23 10:51:25     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/zul/jsp/zul2jsp.tld" prefix="z" %>

<html>
	<head>
		<title>Test of ZUL on JSP</title>
	</head>
	<body>

<z:page>
	<h1>Content in z:page</h1>

	<z:window>
		<h2>h2 in JSP</h2>
	</z:window>
</z:page>

	<p>Content after z:page</p>
	</body>
</html>
