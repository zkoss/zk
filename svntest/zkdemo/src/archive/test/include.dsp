<%--
include.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Dec  7 16:54:20     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<html xmlns:zk="http://www.zkoss.org/2005/zk">
<head>
<title>Include two zul pages</title>
</head>
<body>
<c:set var="some" value="Some" scope="session"/>
<c:include page="slider.zul"/>
<c:include page="ctxmnu.zul"/>
<c:include page="menu.zul"/>
<c:include page="ctxmnu.zul"/>
</body>
</html>