<%--
F30-1977343.jsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu May 29 14:12:41     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%>
<html>
	<head>
		<title>Test Progress Bar</title>
	</head>
	<body>
		<h1>Test of Progress Bar with an Included ZK Page</h1>
		<p>You shall see the progress bar only appear in the following box.</p>
		<jsp:include page="F30-1977343_inc.zul"/>
		<p>This is another part of the JSP page (container).</p>
		<jsp:include page="F30-1977343_inc.zul"/>
	</body>
</html>
