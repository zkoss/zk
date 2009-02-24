<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" %><%--
fileupload-done.html.dsp

{{IS_NOTE
	Purpose:
		The page after the user submits the fileupload dialog.
	Description:

	History:
		Thu Jul 21 18:10:25     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<c:set var="arg" value="${requestScope.arg}"/>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=UTF-8" />
<title>Upload Result</title>
${z:outDeviceStyleSheets('ajax')}
<%-- We cannot use ${z:outLangStyleSheets()} since Executions.getCurrent()
	is not available for this page.
 --%>
</head>
<body>
 <c:if test="${!empty arg.alert}">
	<div style="border: 1px solid red;background: white"><c:out value="${arg.alert}"/></div>
	<input type="button" value="${c:l('mesg:org.zkoss.zul.mesg.MZul:UPLOAD_CANCEL')}" onclick="exec(cmdClose)"/>
 </c:if>
</body>
<script type="text/javascript">
<!--
	parent.zkau.endUpload();

<%-- NOTE: we cannot execute zkau.sendUpdateResult in this frame with Firefox,
	because this frame will be removed and it will cause the following error
	if we try to insert some elements (some kind of NullPointerException
	"Component returned failure code: 0x80004005 (NS_ERROR_FAILURE) [nsIXMLHttpRequest.open]"
--%>
	var cmdUpdate = "zkau.sendUpdateResult('${arg.uuid}', '${arg.contentId}');";
	var cmdClose = "zkau.sendOnClose('${arg.uuid}');";

	<%-- exec at the parent's scope --%>
	function exec(cmd) {
		parent.setTimeout(cmd, 0);
	}
	<c:if test="${!empty arg.contentId}">
		exec(cmdUpdate + cmdClose);
		<%-- Bug 1920877: do not use two timer to do cmdUpdate and
			cmdClose separately, since Safari might do cmdClose first!!
		--%>
	</c:if>
	<c:if test="${empty arg.contentId and empty arg.alert}">
		exec(cmdClose);
	</c:if>
// -->
</script>
</html>
