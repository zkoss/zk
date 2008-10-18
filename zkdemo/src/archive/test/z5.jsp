<%--
z5.jsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 17 08:53:28     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>ZK 5 JSP Test</title>
<%= org.zkoss.zk.fn.JspFns.outZkHtmlTags(application, request, response, null)%>
</head>
<body>
	<p>Case 1: Include z5.zul (which includes z5-1.zul).
	<jsp:include page="z5.zul"/>
	<p>Case 2: Include z5-1.zul.
	<jsp:include page="z5-1.zul"/>
</body>
</html>
