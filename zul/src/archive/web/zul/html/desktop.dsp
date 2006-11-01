<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" %><%--
desktop.dsp

{{IS_NOTE
	Purpose:
		Used to render a ZUL page as a complete page (aka., desktop)
		if it is not included
	Description:
		
	History:
		Fri Jun 10 09:16:14     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zk/core.dsp.tld" prefix="z" %>
<c:set var="arg" value="${requestScope.arg}"/>
<c:set var="page" value="${arg.page}"/>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>${page.title}</title>
${z:outLangStyleSheets()}
<meta http-equiv="Cache-Control" content="no-cache,no-store,must-revalidate,max-age=0"/>
<c:set var="zk_htmlHeadRequired" value="true" scope="request"/><%-- ask page.dsp to generate </head><body> --%>
<c:include page="~./zul/html/page.dsp"/><%-- OC4J cannot handle relative page correctly --%>
</body>
</html>
