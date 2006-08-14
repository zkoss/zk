<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" %><%--
fileupload.dsp

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 21 11:37:28     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="/WEB-INF/tld/web/core.dsp.tld" prefix="c" %>
<%@ taglib uri="/WEB-INF/tld/zk/core.dsp.tld" prefix="u" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Upload</title>
<script type="text/javascript">
<!--
function cancelUpload() {
	parent.zkau.remove('${param.uuid}');
	parent.zkau.endUpload();
}
// -->
</script>
${u:outLangStyleSheets()}
</head>
<body>
	<form action="${param.action}?dtid=${param.dtid}&uuid=${param.uuid}" enctype="multipart/form-data" method="POST" onsubmit="parent.zkau.beginUpload()">
	<%-- We have to encode dtid and uuid in action rather than hidden fields,
		because 1) dtid must be ready before parsing multi-part requests.
		2) parsing multi-part might fail
	--%>
	<%-- change the following if you want the return URI to be different from the default
	<input type="hidden" name="nextURI" value="~./zul/html/fileupload-done.dsp"/>
	--%>

	<table border="0">
	<tr>
		<td><input type="file" name="file"/></td>
	</tr>
	<tr align="left">
		<td style="border: outset 1px">
		<input type="submit" value="${c:l('mesg:com.potix.zul.mesg.MZul:UPLOAD_SUBMIT')}" onclick="parent.zk.progress()"/>
		<input type="button" value="${c:l('mesg:com.potix.zul.mesg.MZul:UPLOAD_CANCEL')}" onclick="cancelUpload()"/>
		</td>
	<tr>
	</table>
	</form>
</body>
