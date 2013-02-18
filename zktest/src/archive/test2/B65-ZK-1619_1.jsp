<%--
B65-ZK-1619_1.jsp

	Purpose:
		
	Description:
		
	History:
		Mon, Feb 18, 2013  6:48:08 PM, Created by jumperchen

Copyright (C) 2013 Potix Corporation. All Rights Reserved.
--%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://potix.com/tld/pat/core" prefix="p" %>
<%
org.zkoss.web.servlet.http.Https.write( request, response, ( org.zkoss.util.media.Media ) session.getAttribute( "media" ), true, false );
%>
