<%--
jspTags-inc.jsp

	Purpose:
		
	Description:
		
	History:
		Fri Nov 20 15:47:16     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.
--%><%@ taglib uri="http://www.zkoss.org/jsp/zul" prefix="z" %>
<h3>Content in a page but before page</h3>
<z:page>
	Conentent in a page but before window
	<z:window title="2nd window" border="normal">
		This is an inline window.<br/>
		Remember to check if &lt;html&gt; is generated correctly
		(i.e., only once)
	</z:window>
	Conentent in a page but after window
</z:page>
Content in a page but after page