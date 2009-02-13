<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8" %><%--
fileupload.html.dsp

{{IS_NOTE
	Purpose:
		The content of the inline frame of the fileupload modal dialog
		(fileupload.html.zul)
	Description:
		
	History:
		Thu Jul 21 11:37:28     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
--%><%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=UTF-8" />
<title>Upload</title>
${z:outDeviceStyleSheets('ajax')}
<%-- We cannot use ${z:outLangStyleSheets()} since Executions.getCurrent()
	is not available for this page.
 --%>
<script type="text/javascript">
<!--
var _zuuid = '${c:eatQuot(param.uuid)}';
function submitUpload() {
	var img = parent.$e(_zuuid + '!img');
	if (img) img.parentNode.removeChild(img);
		<%-- Bug 1578549: we have to remove the closable button first, since
			it might mis-behave if user clicks it after submitting
		--%>

	parent.zkau.beginUpload(_zuuid);
}
function cancelUpload() {
	parent.setTimeout(function(){parent.zkau.endUpload();parent.zkau.sendOnClose(_zuuid);}, 100);
}
function init() {
	if (!parent.zk || !parent.zk.booted) {
		setTimeout(init, 100);
		return;
	}

	parent.zk.listen(document, "keydown", onDocKeydown);

	var el = document.getElementById("form");
	el.action = parent.zk.getUpdateURI(
		"/upload?dtid=${c:eatQuot(param.dtid)}&uuid=" + _zuuid);
	if (parent.zk.ie) {
		var cave = parent.$e(_zuuid + "!cave");
		if (cave)
			document.body.style.backgroundColor = parent.Element.getStyle(cave, "background-color") || "";
	}
	parent.zk.focus(document.getElementById("file"));
}
function onDocKeydown(evt) {
	if (!evt) evt = window.event;
	if (parent.Event.keyCode(evt) == 27)
		cancelUpload();
}
function addUpload(img) {
	img.src = parent.zk.rename(img.src, "delete");
	img.onclick = function () {deleteUpload(img)};
	
	// due to the runtime error of IE, we cannot use the tr.innerHTML method.  
	var table = parent.$parentByTag(img, "TABLE"), 
		tr = table.insertRow(table.rows.length),
		td = tr.insertCell(0);
	td.innerHTML = table.rows.length;
	td.align = "right";
	tr.insertCell(1).innerHTML = '<input class="file" type="file" id="file" name="file"/>' +
		'<img src="${c:encodeURL('~./zul/img/add.gif')}" onclick="addUpload(this);" />';
	adjustHgh(table);
}
function deleteUpload(img) {
	var table = parent.$parentByTag(img, "TABLE");
	table.deleteRow(img.parentNode.parentNode.rowIndex);
	for (var i = 0, j = table.rows.length; i < j; ++i)
		table.rows[i].cells[0].innerHTML = i+1;
	adjustHgh(table);
}
function adjustHgh(table) {
	table.parentNode.style.height = table.rows.length > 3 ? "100px" : "";
	if (parent.zk.opera) table.parentNode.style.overflow = "auto";
	table.parentNode.scrollTop = table.parentNode.scrollHeight;
}
// -->
</script>
</head>
<body onload="init()">
	<form id="form" enctype="multipart/form-data" method="POST" onsubmit="submitUpload()">
	<%-- We have to encode dtid and uuid in action rather than hidden fields,
		because 1) dtid must be ready before parsing multi-part requests.
		2) parsing multi-part might fail
	--%>
	<%-- change the following if you want the return URI to be different from the default
	<input type="hidden" name="nextURI" value="~./zul/html/fileupload-done.html.dsp"/>
	--%>
	<input type="hidden" name="native" value="${c:eatQuot(param.native)}"/>

	<div style="overflow-y:auto;overflow-x:hidden;width:100%;height:${param.max > 3 ? '100px' : ''};">
	<table id="upload-list" border="0" width="100%">
<c:set var="unlimited" value="${param.max < 0 ? true: false}"/>
<c:set var="maxcnt" value="${empty param.max ? 1 : unlimited ? (param.max/-1) : param.max}"/>
<c:forEach var="cnt" begin="1" end="${maxcnt}" varStatus="s">
	<tr>
		<td align="right"><c:if test="${unlimited || maxcnt gt 2}">${cnt}</c:if></td>
		<td><input class="file" type="file" id="file" name="file"/><c:if test="${unlimited && s.index == maxcnt}"><img src="${c:encodeURL('~./zul/img/add.gif')}" onclick='addUpload(this);'"/></c:if><c:if test="${unlimited && s.index < maxcnt}"><img src="${c:encodeURL('~./zul/img/delete.gif')}" onclick='deleteUpload(this);'"/></c:if></td>
	</tr>
</c:forEach>
	</table>
	</div>
	<table border="0" width="100%">
	<tr align="left">
		<td colspan="2" style="border: outset 1px">
		<input class="button" type="submit" value="${c:l('mesg:org.zkoss.zul.mesg.MZul:UPLOAD_SUBMIT')}" onclick="parent.zk.progress()"/>
		<input class="button" type="button" value="${c:l('mesg:org.zkoss.zul.mesg.MZul:UPLOAD_CANCEL')}" onclick="cancelUpload()"/>
		</td>
	<tr>
	</table>
	</form>
</body>
</html>
