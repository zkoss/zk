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
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>JSP Inclusion Test</title>
<%= org.zkoss.zk.fn.JspFns.outZkHtmlTags(application, request, response, null)%>
</head>
<body>
	<p>Case 1: Include include2.zul</p>
	<jsp:include page="include2.zul"/>
</body>
</html>
